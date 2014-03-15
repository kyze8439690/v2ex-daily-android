package com.yugy.v2ex.daily.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yugy.v2ex.daily.R;

/**
 * Created by yugy on 14-3-15.
 */
public class DrawerAdapter extends BaseAdapter{

    private Context mContext;
    private String[] mTitles;

    public DrawerAdapter(Context context){
        mContext = context;
        mTitles = new String[]{
                context.getResources().getString(R.string.title_section1),
                context.getResources().getString(R.string.title_section2),
                context.getResources().getString(R.string.title_section3),
                context.getResources().getString(R.string.title_section4),
                context.getResources().getString(R.string.title_section5),
        };
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public String getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getIconId(int position) {
        switch (position){
            case 0:
                return R.drawable.ic_menu_today;
            case 1:
                return R.drawable.ic_menu_view;
            case 2:
                return R.drawable.ic_menu_star;
            case 3:
                return R.drawable.ic_menu_notifications;
            case 4:
                return R.drawable.ic_menu_preferences;
            default:
                return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView item = (TextView) convertView;
        if(item == null){
            item = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_drawer_item, null);
        }
        item.setText(getItem(position));
        item.setCompoundDrawablesWithIntrinsicBounds(getIconId(position), 0, 0, 0);
        return item;
    }
}
