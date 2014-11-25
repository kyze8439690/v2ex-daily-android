package me.yugy.v2ex.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.yugy.v2ex.R;
import me.yugy.v2ex.model.Reply;
import me.yugy.v2ex.widget.container.RepliesAdapterHolder;

/**
 * Created by yugy on 14/11/25.
 */
public class RepliesAdapter extends CursorAdapter {

    public RepliesAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, viewGroup, false);
        RepliesAdapterHolder holder = new RepliesAdapterHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Reply reply = Reply.fromCursor(cursor);
        RepliesAdapterHolder holder = (RepliesAdapterHolder) view.getTag();
        holder.parse(reply);
    }

}
