package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.widget.NodeView;

/**
 * Created by yugy on 14-3-7.
 */
public class SearchAllNodeAdapter extends BaseAdapter implements NodeView.OnAddButtonClickListener{

    private AllNodesDataHelper mAllNodesDataHelper;
    private NodeModel[] mResult;
    private Context mContext;

    public SearchAllNodeAdapter(Context context, String keyword){
        mContext = context;
        mAllNodesDataHelper = new AllNodesDataHelper(context);
        mResult = mAllNodesDataHelper.search(keyword);
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
    public void onClick(int nodeId, boolean added) {
        mAllNodesDataHelper.setCollected(added, nodeId);
    }
}
