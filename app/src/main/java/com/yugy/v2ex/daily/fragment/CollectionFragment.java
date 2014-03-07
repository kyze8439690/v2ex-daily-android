package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.adapter.CollectionPagerAdapter;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-2-25.
 */
public class CollectionFragment extends Fragment{

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;

    private AllNodesDataHelper mAllNodesDataHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tab_fragment_collection);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_fragment_collection);
        mEmptyText = (TextView) rootView.findViewById(R.id.txt_fragment_collection_empty);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NodeModel[] collections = mAllNodesDataHelper.getCollections();
        if(collections.length != 0){
            mViewPager.setAdapter(new CollectionPagerAdapter(getFragmentManager(), collections));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        }else{
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }
}

