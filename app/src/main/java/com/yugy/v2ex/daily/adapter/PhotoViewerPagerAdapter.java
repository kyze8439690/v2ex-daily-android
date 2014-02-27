package com.yugy.v2ex.daily.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.yugy.v2ex.daily.fragment.PhotoViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyexiong on 13-7-27.
 */
public class PhotoViewerPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mList = new ArrayList<String>();

    public PhotoViewerPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoViewFragment.newInstance(mList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    public ArrayList<String> getData() {
        return mList;
    }

    public void setData(List<String> datas) {
        mList.clear();
        mList.addAll(datas);
        notifyDataSetChanged();
    }
}
