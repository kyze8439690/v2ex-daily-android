package com.yugy.v2ex.daily.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.adapter.LoadingAdapter;
import com.yugy.v2ex.daily.model.ReplyModel;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;
import com.yugy.v2ex.daily.utils.ScreenUtils;
import com.yugy.v2ex.daily.widget.AppMsg;
import com.yugy.v2ex.daily.widget.ReplyView;
import com.yugy.v2ex.daily.widget.TopicView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-24.
 */
public class TopicFragment extends Fragment implements AdapterView.OnItemClickListener, OnRefreshListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private TopicView mHeaderView;
    private ListView mListView;

    private TopicModel mTopicModel;

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;

    private int mType = TYPE_EMPTY;
    private boolean mLogined = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogined = PreferenceManager.getDefaultSharedPreferences(getActivity()).contains("username");
        if(mLogined){
            setHasOptionsMenu(true);
        }
    }

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
        mHeaderView = new TopicView(getActivity());
        mHeaderView.setViewDetail();
        mHeaderView.parse(mTopicModel);
        mListView.addHeaderView(mHeaderView, mTopicModel, false);
        mListView.setAdapter(new LoadingAdapter(getActivity()));
        getReplyData();
    }

    private void getTopicData(){
        V2EX.showTopicByTopicId(getActivity(), mTopicModel.id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                DebugUtils.log(response);
                try {
                    mTopicModel.parse(response.getJSONObject(0));
                    mHeaderView.parse(mTopicModel);
                    getReplyData();
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                e.printStackTrace();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public void onCommentFinish(JSONObject result){
        try {
            if(result.getString("result").equals("ok")){
                MessageUtils.toast(getActivity(), "Comment success");
                getReplyData();
            }else if(result.getString("result").equals("fail")){
                MessageUtils.toast(getActivity(), "Comment failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReplyData(){
        mPullToRefreshLayout.setRefreshing(true);
        V2EX.getReplies(getActivity(), mTopicModel.id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                DebugUtils.log(response);
                if(response.length() == 0){
                    mListView.setAdapter(new NoReplyAdapter());
                    mPullToRefreshLayout.setRefreshComplete();
                    return;
                }
                try {
                    ArrayList<ReplyModel> models = getModels(response);
                    mListView.setAdapter(new TopicReplyAdapter(models));
                    mType = TYPE_NORMAL;
                    mPullToRefreshLayout.setRefreshComplete();
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                e.printStackTrace();
                super.onFailure(statusCode, headers, responseBody, e);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_topic_comment:
                CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
                Bundle argument = new Bundle();
                argument.putInt("topic_id", mTopicModel.id);
                commentDialogFragment.setArguments(argument);
                commentDialogFragment.show(getFragmentManager(), "comment");
                break;
        }
        return super.onOptionsItemSelected(item);
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
            item.setFloorNum(position + 1);
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
}
