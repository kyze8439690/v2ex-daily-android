package com.yugy.v2ex.daily.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.MessageUtils;
import com.yugy.v2ex.daily.widget.NodeView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-3-7.
 */
public class SearchAllNodeAdapter extends BaseAdapter implements NodeView.OnAddButtonClickListener{

    private AllNodesDataHelper mAllNodesDataHelper;
    private NodeModel[] mResult;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private boolean mIsLogined;
    private String mRegTime;

    public SearchAllNodeAdapter(Context context, String keyword){
        mContext = context;
        mAllNodesDataHelper = new AllNodesDataHelper(context);
        mResult = mAllNodesDataHelper.search(keyword);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mIsLogined = mSharedPreferences.contains("username");
        mRegTime = mSharedPreferences.getString("reg_time", null);
    }

    public void setKeyword(String keyword){
        mResult = mAllNodesDataHelper.search(keyword);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mResult.length;
    }

    @Override
    public NodeModel getItem(int position) {
        return mResult[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NodeView item = (NodeView) convertView;
        if(item == null){
            item = new NodeView(mContext);
        }
        item.parse(mResult[position]);
        item.setOnAddButtonClickListener(this);
        return item;
    }

    @Override
    public void onClick(final int nodeId, final boolean added) {
        if(mIsLogined){
            if(added){
                mProgressDialog = ProgressDialog.show(mContext, null, "Add to collections...", true, false);
            }else{
                mProgressDialog = ProgressDialog.show(mContext, null, "Remove from collections...", true, false);
            }
            if(mRegTime == null){
                V2EX.getRegTime(mContext, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getString("result").equals("ok")) {
                                mRegTime = response.getString("reg_time");
                                mSharedPreferences.edit().putString("reg_time", mRegTime).commit();
                                syncCollection(nodeId, added, mRegTime);
                            } else if (response.getString("result").equals("fail")) {
                                MessageUtils.toast(mContext, "Get reg time fail");
                                mProgressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                syncCollection(nodeId, added, mRegTime);
            }
        }else{
            mAllNodesDataHelper.setCollected(added, nodeId);
        }
    }

    private void syncCollection(final int nodeId, final boolean added, String regTime){
        V2EX.syncCollection(mContext, nodeId, regTime, added, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getString("result").equals("ok")){
                        mProgressDialog.setMessage("OK");
                        mAllNodesDataHelper.setCollected(added, nodeId);
                    }else if(response.getString("result").equals("fail")){
                        mProgressDialog.setMessage("Fail");
                        MessageUtils.toast(mContext, "Sync collections failed.");
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
