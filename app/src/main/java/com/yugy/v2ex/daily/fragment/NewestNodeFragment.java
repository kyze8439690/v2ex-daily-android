package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.activity.TopicActivity;
import com.yugy.v2ex.daily.adapter.NewestNodeAdapter;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.dao.datahelper.NewestNodeDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.tasker.AllNodesParseTask;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;
import com.yugy.v2ex.daily.widget.TopicView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static android.widget.AdapterView.OnItemClickListener;
import static com.yugy.v2ex.daily.adapter.NewestNodeAdapter.OnScrollToBottomListener;

/**
 * Created by yugy on 14-2-23.
 */
public class NewestNodeFragment extends Fragment implements OnRefreshListener, OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, OnScrollToBottomListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;
    private AllNodesDataHelper mAllNodesDataHelper;
    private NewestNodeDataHelper mNewestNodeDataHelper;
    private NewestNodeAdapter mNewestNodeAdapter;

    private int mPage;
    private boolean mLoadFromCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_node, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_fragment_node);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_fragment_node));
        mListView.setOnItemClickListener(this);
        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
        mNewestNodeDataHelper = new NewestNodeDataHelper(getActivity());
        mNewestNodeAdapter = new NewestNodeAdapter(getActivity(), this);
        mListView.setAdapter(mNewestNodeAdapter);
        getLoaderManager().initLoader(0, null, this);
        return mPullToRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
        if(mAllNodesDataHelper.query().length == 0){
            mNewestNodeDataHelper.clear();
            getAllNodesData();
        }else{
            getNewestNodeData();
        }
    }

    private void getAllNodesData(){
        V2EX.getAllNode(getActivity(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DebugUtils.log(response);
                new AllNodesParseTask(getActivity()){
                    @Override
                    protected void onPostExecute(ArrayList<NodeModel> nodeModels) {
                        mAllNodesDataHelper.bulkInsert(nodeModels);
                        getNewestNodeData();
                    }
                }.execute(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                if(getActivity() != null) {
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                }
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    private void getNewestNodeData(){
        if(mNewestNodeDataHelper.query().length == 0){
            mPage = 1;
            getData();
            mLoadFromCache = false;
        }else{
            mLoadFromCache = true;
        }
    }

    private void getData(){
        if(mPage == 1){
            mLoadFromCache = false;
        }
        mPullToRefreshLayout.setRefreshing(true);
        V2EX.getLatestTopics(getActivity(), mPage, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DebugUtils.log(response);
                try {
                    if(mPage == 1){
                        mNewestNodeDataHelper.clear();
                    }
                    mNewestNodeDataHelper.bulkInsert(getModels(response));
                    mPage++;
                } catch (JSONException e) {
                    AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                    e.printStackTrace();
                }
                mPullToRefreshLayout.setRefreshComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                e.printStackTrace();
                if(getActivity() != null) {
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                    mPullToRefreshLayout.setRefreshComplete();
                }
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        mPage = 1;
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TopicView topicView = (TopicView) view;
        int topicId = topicView.getTopicId();
        TopicModel topicModel = mNewestNodeDataHelper.select(topicId);
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        Bundle argument = new Bundle();
        argument.putParcelable("model", topicModel);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mNewestNodeDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewestNodeAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewestNodeAdapter.changeCursor(null);
    }

    @Override
    public void onScrollToBottom() {
        if(!mLoadFromCache) {
            getData();
        }
    }
}
