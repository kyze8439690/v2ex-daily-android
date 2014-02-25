package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.yugy.v2ex.daily.utils.ScreenUtils;

/**
 * Created by yugy on 14-2-25.
 */
public class LoadingAdapter extends BaseAdapter {

    private Context mContext;

    public LoadingAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProgressBar progressBar = new ProgressBar(mContext);
        int padding = ScreenUtils.dp(mContext, 8);
        progressBar.setPadding(padding, padding, padding, padding);
        return progressBar;
    }
}