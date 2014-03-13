package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.widget.TopicView;

import java.util.ArrayList;

/**
 * Created by yugy on 14-2-25.
 */
public class TopicAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TopicModel> mModels;
    private OnScrollToBottomListener mListener;

    /**
     *
     * @param context
     * @param models
     * @param listener pass null if there is no need.
     */
    public TopicAdapter(Context context, ArrayList<TopicModel> models, OnScrollToBottomListener listener) {
        mContext = context;
        mModels = models;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public TopicModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopicView item = (TopicView) convertView;
        if(item == null){
            item = new TopicView(mContext);
        }
        item.parse(getItem(position));
        if(position == getCount() - 1){
            if(mListener != null) {
                mListener.onScrollToBottom();
            }
        }
        return item;
    }

    public void setModels(ArrayList<TopicModel> models){
        mModels = models;
    }

    public static interface OnScrollToBottomListener{
        public void onScrollToBottom();
    }
}
