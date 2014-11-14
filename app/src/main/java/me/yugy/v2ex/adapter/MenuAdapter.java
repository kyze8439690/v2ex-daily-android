package me.yugy.v2ex.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.yugy.v2ex.R;

/**
 * Created by yugy on 14/11/13.
 */
public class MenuAdapter extends BaseAdapter {

    private String[] mEntries;
    private int mActiveColor;
    private int mNormalColor;
    private int mCurrentIndex = 0;

    public MenuAdapter(String[] entries, int activeColor, int normalColor) {
        mEntries = entries;
        mActiveColor = activeColor;
        mNormalColor = normalColor;
    }

    @Override
    public int getCount() {
        return mEntries.length;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
        notifyDataSetChanged();
    }

    @Override
    public String getItem(int position) {
        return mEntries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;
        if(view == null){
            view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        }
        view.setText(getItem(position));
        if(position == mCurrentIndex){
            view.setTextColor(mActiveColor);
        }else{
            view.setTextColor(mNormalColor);
        }
        return view;
    }
}
