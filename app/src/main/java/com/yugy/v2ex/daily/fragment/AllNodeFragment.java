package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.activity.NodeActivity;
import com.yugy.v2ex.daily.adapter.AllNodesAdapter;
import com.yugy.v2ex.daily.adapter.SearchAllNodeAdapter;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.tasker.AllNodesParseTask;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;
import com.yugy.v2ex.daily.widget.NodeView;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by yugy on 14-2-23.
 */
public class AllNodeFragment extends Fragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private StaggeredGridView mGridView;
    private AllNodesDataHelper mAllNodesDataHelper;
    private AllNodesAdapter mAllNodesAdapter;
    private SearchAllNodeAdapter mSearchAllNodeAdapter = null;
    private View mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_node, container, false);
        mGridView = (StaggeredGridView) rootView.findViewById(R.id.grid_fragment_all_node);
        mEmptyView = rootView.findViewById(R.id.progress_fragment_all_node);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(this);
        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
        mAllNodesAdapter = new AllNodesAdapter(getActivity());
        mGridView.setAdapter(mAllNodesAdapter);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mAllNodesDataHelper.query().length == 0){
            getData();
        }
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
                DebugUtils.log("onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DebugUtils.log("onQueryTextChange: " + newText);
                if(newText.equals("")){     //删除关键字到空或者初始状态
                    if(mGridView.getAdapter() instanceof AllNodesAdapter){      //初始状态, do nothing

                    }else if(mGridView.getAdapter() instanceof SearchAllNodeAdapter){       //删除关键字到空，用回AllNodesAdapter
                        mGridView.setAdapter(mAllNodesAdapter);
                        mSearchAllNodeAdapter = null;
                    }
                }else{      //有关键字啦
                    if(mSearchAllNodeAdapter == null){      //SearchAllNodesAdapter 初始化
                        mSearchAllNodeAdapter = new SearchAllNodeAdapter(getActivity(), newText);
                        mGridView.setAdapter(mSearchAllNodeAdapter);
                    }else{
                        mSearchAllNodeAdapter.setKeyword(newText);
                    }
                }
                return true;
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                DebugUtils.log("onMenuItemActionExpand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                DebugUtils.log("onMenuItemActionCollapse");
                mGridView.setAdapter(mAllNodesAdapter);
                mSearchAllNodeAdapter = null;
                return true;
            }
        });

    }

    private void getData(){
        V2EX.getAllNode(getActivity(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DebugUtils.log(response);
                new AllNodesParseTask(getActivity()) {
                    @Override
                    protected void onPostExecute(ArrayList<NodeModel> nodeModels) {
                        mAllNodesDataHelper.bulkInsert(nodeModels);
                        mEmptyView.setVisibility(View.GONE);
                        mGridView.setEmptyView(null);
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mAllNodesDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAllNodesAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAllNodesAdapter.changeCursor(null);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }
}
