package com.yugy.v2ex.daily.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.yugy.v2ex.daily.fragment.NodeFragment;
import com.yugy.v2ex.daily.model.NodeModel;

import java.util.ArrayList;

/**
 * Created by yugy on 14-3-7.
 */
public class CollectionPagerAdapter extends FragmentStatePagerAdapter{

    private NodeModel[] mModels;

    public CollectionPagerAdapter(FragmentManager fm, NodeModel[] models) {
        super(fm);
        mModels = models;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mModels[position].title;
    }

    @Override
    public Fragment getItem(int position) {
        NodeFragment nodeFragment = new NodeFragment();
        Bundle argument = new Bundle();
        argument.putInt("node_id", mModels[position].id);
        nodeFragment.setArguments(argument);
        return nodeFragment;
    }

    @Override
    public int getCount() {
        return mModels.length;
    }
}
