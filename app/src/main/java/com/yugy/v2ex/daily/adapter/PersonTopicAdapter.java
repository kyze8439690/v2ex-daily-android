package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.widget.PersonTopicView;

import java.util.ArrayList;

/**
 * Created by yugy on 14-2-25.
 */
public class PersonTopicAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TopicModel> mModels;

    /**
     *
     * @param context
     * @param models
     */
    public PersonTopicAdapter(Context context, ArrayList<TopicModel> models) {
        mContext = context;
        mModels = models;
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
        PersonTopicView item = (PersonTopicView) convertView;
        if(item == null){
            item = new PersonTopicView(mContext);
        }
        item.parse(getItem(position));
        return item;
    }
}
