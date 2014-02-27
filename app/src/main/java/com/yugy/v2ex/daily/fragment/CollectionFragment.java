package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-25.
 */
public class CollectionFragment extends Fragment implements OnRefreshListener{

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;

    private ArrayList<NodeModel> mCollectionNode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tab_fragment_collection);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_fragment_collection);
        mEmptyText = (TextView) rootView.findViewById(R.id.txt_fragment_collection_empty);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> collectionNodeId = sharedPreferences.getStringSet("node_collections", new HashSet<String>());
        if(collectionNodeId.size() == 0){
            mEmptyText.setVisibility(View.VISIBLE);
        }

        mCollectionNode = new ArrayList<NodeModel>();
        V2EX.getAllNode(getActivity(), false, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                DebugUtils.log(response);
                new ParseTask().execute(response);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    private class ParseTask extends AsyncTask<JSONArray, Void, CollectionAdapter>{

        @Override
        protected CollectionAdapter doInBackground(JSONArray... params) {
            try {
                ArrayList<NodeModel> models = getModels(params[0]);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Set<String> collectionNodeId = sharedPreferences.getStringSet("node_collections", new HashSet<String>());
                if(collectionNodeId.size() != 0){
                    for(NodeModel model : models){
                        for(String id : collectionNodeId){
                            if(String.valueOf(model.id).equals(id)){
                                mCollectionNode.add(model);
                            }
                        }
                    }
                    return new CollectionAdapter(getFragmentManager(), mCollectionNode);
                }
            } catch (JSONException e) {
                AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CollectionAdapter collectionAdapter) {
            if(collectionAdapter != null){
                mViewPager.setAdapter(collectionAdapter);
                mPagerSlidingTabStrip.setViewPager(mViewPager);
            }
            super.onPostExecute(collectionAdapter);
        }
    }

    private class CollectionAdapter extends FragmentStatePagerAdapter {

        private ArrayList<NodeModel> mModels;
        private ArrayList<String> mTitles;

        public CollectionAdapter(FragmentManager fm, ArrayList<NodeModel> models) {
            super(fm);
            mModels = models;
            mTitles = new ArrayList<String>();
            for(NodeModel model : mModels){
                mTitles.add(model.title);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            NodeFragment nodeFragment = new NodeFragment();
            Bundle argument = new Bundle();
            argument.putInt("node_id", mModels.get(position).id);
            nodeFragment.setArguments(argument);
            return nodeFragment;
        }

        @Override
        public int getCount() {
            return mModels.size();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onRefreshStarted(View view) {

    }
}

