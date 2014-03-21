package com.yugy.v2ex.daily.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.model.NodeModel;

/**
 * Created by yugy on 14-2-23.
 */
public class NodeView extends RelativeLayout implements View.OnClickListener{
    public NodeView(Context context) {
        super(context);
        init();
    }

    public NodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private static final int TYPE_NOT_ADDED = 0;
    private static final int TYPE_ADDED = 1;

    private int mType = TYPE_NOT_ADDED;

    private ImageButton mAdd;
    private TextView mTitle;
    private TextView mHeader;
    private TextView mTopics;

    private int mNodeId;

    private OnAddButtonClickListener mOnAddButtonClickListener;

    private void init(){
        inflate(getContext(), R.layout.view_node, this);
        mAdd = (ImageButton) findViewById(R.id.btn_view_node_add);
        mTitle = (TextView) findViewById(R.id.txt_view_node_title);
        mHeader = (TextView) findViewById(R.id.txt_view_node_header);
        mTopics = (TextView) findViewById(R.id.txt_view_node_topics);

        mAdd.setOnClickListener(this);
    }

    public void parse(NodeModel model){
        mNodeId = model.id;
        mTitle.setText(model.title);
        if(model.header != null){
            mHeader.setVisibility(VISIBLE);
            mHeader.setText(Html.fromHtml(model.header));
        }else{
            mHeader.setVisibility(GONE);
        }
        setTypeAdded(model.isCollected);
        mTopics.setText(model.topics + " topics");
    }

    public int getNodeId(){
        return mNodeId;
    }

    private void setTypeAdded(boolean added){
        if(added){
            mType = TYPE_ADDED;
            mAdd.setImageResource(R.drawable.btn_add_on_card_after);
        }else{
            mType = TYPE_NOT_ADDED;
            mAdd.setImageResource(R.drawable.btn_add_on_card_before);
        }
    }

    @Override
    public void onClick(View v) {
        if(mType == TYPE_NOT_ADDED){
            setTypeAdded(true);
            if(mOnAddButtonClickListener != null){
                mOnAddButtonClickListener.onClick(mNodeId, true);
            }
        }else if(mType == TYPE_ADDED){
            setTypeAdded(false);
            if(mOnAddButtonClickListener != null){
                mOnAddButtonClickListener.onClick(mNodeId, false);
            }
        }
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener onAddButtonClickListener) {
        mOnAddButtonClickListener = onAddButtonClickListener;
    }

    public static interface OnAddButtonClickListener{
        public void onClick(int nodeId, boolean added);
    }
}
