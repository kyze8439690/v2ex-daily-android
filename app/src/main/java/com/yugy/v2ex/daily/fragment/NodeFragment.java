package com.yugy.v2ex.daily.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.NodeActivity;
import com.yugy.v2ex.daily.activity.TopicActivity;
import com.yugy.v2ex.daily.adapter.TopicAdapter;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-25.
 */
public class NodeFragment extends Fragment implements OnRefreshListener, AdapterView.OnItemClickListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;
    private int mNodeId;
    private ArrayList<TopicModel> mModels;
    private AllNodesDataHelper mAllNodesDataHelper;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private boolean mIsLogined;
    private String mRegTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mIsLogined = mSharedPreferences.contains("username");
        mRegTime = mSharedPreferences.getString("reg_time", null);
    }

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

        if((mNodeId = getArguments().getInt("node_id", 0)) != 0){
            getData(false);
        }else{
            getActivity().finish();
        }

        NodeModel nodeModel = mAllNodesDataHelper.select(mNodeId);

        if(getActivity() instanceof NodeActivity && !nodeModel.isCollected){
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.node, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_node_add:
                if(mIsLogined){
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "Add to collections...", true, false);
                    if(mRegTime == null){
                        V2EX.getRegTime(getActivity(), new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if(getActivity() != null){
                                    try {
                                        if(response.getString("result").equals("ok")){
                                            mRegTime = response.getString("reg_time");
                                            mSharedPreferences.edit().putString("reg_time", mRegTime).commit();
                                            syncCollection(mNodeId, true, mRegTime);
                                        }else if(response.getString("result").equals("fail")){
                                            MessageUtils.toast(getActivity(), "Get reg time fail");
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }else{
                        syncCollection(mNodeId, true, mRegTime);
                    }
                }else{
                    mAllNodesDataHelper.setCollected(true, mNodeId);
                }
                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData(boolean forceRefresh){
        V2EX.showTopicByNodeId(getActivity(), forceRefresh, mNodeId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                DebugUtils.log(response);
                if(getActivity() != null){
                    try {
                        mModels = getModels(response);
                        if(getActivity() instanceof NodeActivity){
                            if(mModels.size() != 0){
                                getActivity().getActionBar().setTitle(mModels.get(0).node.title);
                            }
                        }
                        mListView.setAdapter(new TopicAdapter(getActivity(), mModels));
                    } catch (JSONException e) {
                        AppMsg.makeText(getActivity(), "Json decode error", AppMsg.STYLE_ALERT).show();
                        e.printStackTrace();
                    }
                    mPullToRefreshLayout.setRefreshComplete();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                if(getActivity() != null) {
                    AppMsg.makeText(getActivity(), "Network error", AppMsg.STYLE_ALERT).show();
                }
                e.printStackTrace();
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
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

    private void syncCollection(final int nodeId, final boolean added, String regTime){
        V2EX.syncCollection(getActivity(), nodeId, regTime, added, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(getActivity() != null){
                    try {
                        if(response.getString("result").equals("ok")){
                            mProgressDialog.setMessage("OK");
                            mAllNodesDataHelper.setCollected(added, nodeId);
                        }else if(response.getString("result").equals("fail")){
                            mProgressDialog.setMessage("Fail");
                            MessageUtils.toast(getActivity(), "Sync collections failed.");
                        }
                        mProgressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
