package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.activity.TopicActivity;
import com.yugy.v2ex.daily.adapter.TopicAdapter;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.network.RequestManager;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.EOFException;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-23.
 */
public class NewestNodeFragment extends Fragment implements OnRefreshListener, AdapterView.OnItemClickListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;

    private ArrayList<TopicModel> mModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_node, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_fragment_node);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_fragment_node));
        mListView.setOnItemClickListener(this);
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

    private void getData(boolean forceRefresh){
        mPullToRefreshLayout.setRefreshing(true);
        V2EX.getLatestTopics(getActivity(), forceRefresh, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray jsonArray) {
                DebugUtils.log(jsonArray);
                try {
                    mModels = getModels(jsonArray);
                    mListView.setAdapter(new TopicAdapter(getActivity(), mModels));
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
                mPullToRefreshLayout.setRefreshComplete();
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

    @Override
    public void onRefreshStarted(View view) {
        getData(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        Bundle argument = new Bundle();
        argument.putParcelable("model", mModels.get(position));
        intent.putExtra("argument", argument);
        startActivity(intent);
    }

    private ArrayList<TopicModel> getModels(JSONArray response) throws JSONException {
        ArrayList<TopicModel> models = new ArrayList<TopicModel>();
        for(int i = 0; i < response.length(); i++){
            TopicModel model = new TopicModel();
            model.parse(response.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onDestroy() {
        RequestManager.getInstance().cancelRequests(getActivity());
        super.onDestroy();
    }
}
