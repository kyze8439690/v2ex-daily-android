package com.yugy.v2ex.daily.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.adapter.LoadingAdapter;
import com.yugy.v2ex.daily.model.ReplyModel;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.network.RequestManager;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.ScreenUtils;
import com.yugy.v2ex.daily.widget.AppMsg;
import com.yugy.v2ex.daily.widget.ReplyView;
import com.yugy.v2ex.daily.widget.TopicView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.EOFException;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-24.
 */
public class TopicFragment extends Fragment implements AdapterView.OnItemClickListener, OnRefreshListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;

    private TopicModel mTopicModel;

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;

    private int mType = TYPE_EMPTY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_topic, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_fragment_topic);
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

        mTopicModel = getArguments().getParcelable("model");
        TopicView topicView = new TopicView(getActivity());
        topicView.setViewDetail();
        topicView.parse(mTopicModel);
        mListView.addHeaderView(topicView, mTopicModel, false);
        mListView.setAdapter(new LoadingAdapter(getActivity()));
        getReplyData();
    }

    private void getTopicData(){
        V2EX.showTopicByTopicId(getActivity(), mTopicModel.id, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        DebugUtils.log(jsonArray);
                        try {
                            mTopicModel.parse(jsonArray.getJSONObject(0));
                            getReplyData();
                        } catch (JSONException e) {
                            AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (volleyError.getCause() instanceof EOFException) {
                            AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                        } else if (volleyError.getCause() instanceof TimeoutError) {
                            AppMsg.makeText(getActivity(), "Timeout error", AppMsg.STYLE_ALERT).show();
                        }
                        volleyError.printStackTrace();
                    }
                }
        );
    }

    private void getReplyData(){
        V2EX.getReplies(getActivity(), mTopicModel.id, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        DebugUtils.log(jsonArray);
                        if(jsonArray.length() == 0){
                            mListView.setAdapter(new NoReplyAdapter());
                            mPullToRefreshLayout.setRefreshComplete();
                            return;
                        }
                        try {
                            ArrayList<ReplyModel> models = getModels(jsonArray);
                            mListView.setAdapter(new TopicReplyAdapter(models));
                            mType = TYPE_NORMAL;
                            mPullToRefreshLayout.setRefreshComplete();
                        } catch (JSONException e) {
                            AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(volleyError.getCause() instanceof EOFException){
                            AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                        }else if(volleyError.getCause() instanceof TimeoutError){
                            AppMsg.makeText(getActivity(), "Timeout error", AppMsg.STYLE_ALERT).show();
                        }
                        volleyError.printStackTrace();
                    }
                });
    }

    private ArrayList<ReplyModel> getModels(JSONArray jsonArray) throws JSONException {
        ArrayList<ReplyModel> models = new ArrayList<ReplyModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            ReplyModel model = new ReplyModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mType == TYPE_NORMAL){
//            MessageUtils.toast(getActivity(), position + "");
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        getTopicData();
    }

    private class TopicReplyAdapter extends BaseAdapter{

        private ArrayList<ReplyModel> mModels;

        private TopicReplyAdapter(ArrayList<ReplyModel> models) {
            mModels = models;
        }

        @Override
        public int getCount() {
            return mModels.size();
        }

        @Override
        public ReplyModel getItem(int position) {
            return mModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ReplyView item = (ReplyView) convertView;
            if(item == null){
                item = new ReplyView(getActivity());
            }
            item.parse(getItem(position));
            return item;
        }
    }

    private class NoReplyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getActivity());
            int padding = ScreenUtils.dp(getActivity(), 8);
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);
            textView.setText("No replies");
            return textView;
        }
    }

    @Override
    public void onDestroy() {
        RequestManager.getInstance().cancelRequests(getActivity());
        super.onDestroy();
    }
}
