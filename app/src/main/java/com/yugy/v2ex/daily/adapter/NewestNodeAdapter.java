package com.yugy.v2ex.daily.adapter;

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

    public NewestNodeAdapter(Context context) {
        super(context, null, false);
        mContext = context;
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
}
