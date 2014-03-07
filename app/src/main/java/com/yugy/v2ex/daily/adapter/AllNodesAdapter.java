package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.widget.NodeView;
import com.yugy.v2ex.daily.widget.ReplyView;

/**
 * Created by yugy on 14-3-7.
 */
public class AllNodesAdapter extends CursorAdapter implements NodeView.OnAddButtonClickListener{

    private AllNodesDataHelper mAllNodesDataHelper;

    public AllNodesAdapter(Context context){
        super(context, null, false);
        mAllNodesDataHelper = new AllNodesDataHelper(context);
    }

    @Override
    public NodeModel getItem(int position) {
        getCursor().moveToPosition(position);
        return NodeModel.fromCursor(getCursor());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new NodeView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NodeView nodeView = (NodeView) view;
        NodeModel nodeModel = NodeModel.fromCursor(cursor);
        nodeView.parse(nodeModel);
        nodeView.setOnAddButtonClickListener(this);
    }

    @Override
    public void onClick(int nodeId, boolean added) {
        mAllNodesDataHelper.setCollected(added, nodeId);
    }
}
