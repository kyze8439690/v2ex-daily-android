package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.SearchView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.etsy.android.grid.StaggeredGridView;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.activity.NodeActivity;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.network.RequestManager;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;
import com.yugy.v2ex.daily.widget.NodeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-23.
 */
public class AllNodeFragment extends Fragment implements OnRefreshListener, NodeView.OnAddButtonClickListener, AdapterView.OnItemClickListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private StaggeredGridView mGridView;

    private SharedPreferences.Editor mEditor;
    private ArrayList<NodeModel> mModels;
    private Set<String> mNodeIdCollection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = sharedPreferences.edit();
        mNodeIdCollection = sharedPreferences.getStringSet("node_collections", new HashSet<String>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_all_node, container, false);
        mGridView = (StaggeredGridView) mPullToRefreshLayout.findViewById(R.id.grid_fragment_all_node);
        mGridView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_fragment_all_node));
        mGridView.setOnItemClickListener(this);
        return mPullToRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
        getData(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.all_node, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_all_node_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search for nodes");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    ArrayList<NodeModel> result = new ArrayList<NodeModel>();
                    for (int i = 0; i < mModels.size(); i++) {
                        String title = mModels.get(i).title.toLowerCase();
                        if (title.contains(newText.toLowerCase())) {
                            result.add(mModels.get(i));
                        }
                    }
                    mGridView.setAdapter(new AllNodeAdapter(result));
                } else {
                    if(mModels != null){
                        mGridView.setAdapter(new AllNodeAdapter(mModels));
                    }
                }
                return false;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.setQuery("", false);
                mGridView.setAdapter(new AllNodeAdapter(mModels));
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getData(boolean forceRefresh){
        mPullToRefreshLayout.setRefreshing(true);
        V2EX.getAllNode(getActivity(), forceRefresh, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                DebugUtils.log(jsonArray);
                new ParseTask().execute(jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(volleyError.getCause() instanceof EOFException){
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                }
                mPullToRefreshLayout.setRefreshComplete();
                volleyError.printStackTrace();
            }
        });
    }

    private class ParseTask extends AsyncTask<JSONArray, Void, ArrayList<NodeModel>>{

        @Override
        protected ArrayList<NodeModel> doInBackground(JSONArray... params) {
            try {
                mModels = getModels(params[0]);
                return mModels;
            } catch (JSONException e) {
                AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NodeModel> nodeModels) {
            mGridView.setAdapter(new AllNodeAdapter(mModels));
            mPullToRefreshLayout.setRefreshComplete();
            mPullToRefreshLayout.findViewById(R.id.progress_fragment_all_node).setVisibility(View.GONE);
            mGridView.setEmptyView(null);
            super.onPostExecute(nodeModels);
        }
    }

    private ArrayList<NodeModel> getModels(JSONArray jsonArray) throws JSONException {
        ArrayList<NodeModel> models = new ArrayList<NodeModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            NodeModel model = new NodeModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

    @Override
    public void onClick(int nodeId, boolean added) {
        String nodeIdString = String.valueOf(nodeId);
        if(added){
            mNodeIdCollection.add(nodeIdString);
        }else{
            mNodeIdCollection.remove(nodeIdString);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NodeView item = (NodeView) view;
        Intent intent = new Intent(getActivity(), NodeActivity.class);
        Bundle argument = new Bundle();
        argument.putInt("node_id", item.getNodeId());
        intent.putExtra("argument", argument);
        startActivity(intent);
    }

    private class AllNodeAdapter extends BaseAdapter{

        private ArrayList<NodeModel> mModels;

        private AllNodeAdapter(ArrayList<NodeModel> models) {
            mModels = models;
        }

        @Override
        public int getCount() {
            return mModels.size();
        }

        @Override
        public NodeModel getItem(int position) {
            return mModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NodeView item = (NodeView) convertView;
            if(item == null){
                item = new NodeView(getActivity());
                item.setOnAddButtonClickListener(AllNodeFragment.this);
            }
            item.parse(getItem(position));
            if(mNodeIdCollection.contains(String.valueOf(getItem(position).id))){
                item.setTypeAdded(true);
            }else{
                item.setTypeAdded(false);
            }
            return item;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }

    @Override
    public void onDestroy() {
        RequestManager.getInstance().cancelRequests(getActivity());
        mEditor.remove("node_collections").commit();
        mEditor.putStringSet("node_collections", mNodeIdCollection).commit();
        super.onDestroy();
    }

    @Override
    public void onRefreshStarted(View view) {
        getData(true);
    }
}
