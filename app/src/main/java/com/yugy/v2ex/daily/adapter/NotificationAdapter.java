package com.yugy.v2ex.daily.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.v2ex.daily.model.NotificationModel;
import com.yugy.v2ex.daily.widget.NotificationView;

import java.util.ArrayList;

/**
 * Created by yugy on 14-3-14.
 */
public class NotificationAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<NotificationModel> mModels;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> models){
        mContext = context;
        mModels = models;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public NotificationModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int mLastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationView item = (NotificationView) convertView;
        if(item == null){
            item = new NotificationView(mContext);
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
