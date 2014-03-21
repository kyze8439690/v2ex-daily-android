package com.yugy.v2ex.daily.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.widget.TopicView;

/**
 * Created by yugy on 14-3-14.
 */
public class NewestNodeAdapter extends CursorAdapter{

    private Context mContext;
    private OnScrollToBottomListener mListener;

    public NewestNodeAdapter(Context context, OnScrollToBottomListener listener) {
        super(context, null, false);
        mContext = context;
        mListener = listener;
    }

    @Override
    public TopicModel getItem(int position) {
        getCursor().moveToPosition(position);
        return TopicModel.fromCursor(getCursor(), mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new TopicView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TopicView topicView = (TopicView) view;
        TopicModel topicModel = TopicModel.fromCursor(cursor, context);
        topicView.parse(topicModel);
    }

    private int mLastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == getCount() - 1){
            if(mListener != null){
                mListener.onScrollToBottom();
            }
        }
        View view = super.getView(position, convertView, parent);
        if((position > mLastPosition)){
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(400).playTogether(
                    ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 150, 0),
                    ObjectAnimator.ofFloat(view, View.ROTATION_X,    8,   0),
                    ObjectAnimator.ofFloat(view, View.ALPHA,         0, 1.0f)
                    );
            animatorSet.start();
        }
        mLastPosition = position;
        return view;
    }

    public static interface OnScrollToBottomListener{
        public void onScrollToBottom();
    }
}
