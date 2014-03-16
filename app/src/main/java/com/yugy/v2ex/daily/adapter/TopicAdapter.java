package com.yugy.v2ex.daily.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

    /**
     *
     * @param context
     * @param models
     */
    public TopicAdapter(Context context, ArrayList<TopicModel> models) {
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

    private int mLastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopicView item = (TopicView) convertView;
        if(item == null){
            item = new TopicView(mContext);
        }
        item.parse(getItem(position));
        if((position > mLastPosition)){
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(400).playTogether(
                    ObjectAnimator.ofFloat(item, View.TRANSLATION_Y, 150, 0),
                    ObjectAnimator.ofFloat(item, View.ROTATION_X,    8,   0)
            );
            animatorSet.start();
        }
        mLastPosition = position;
        return item;
    }
}
