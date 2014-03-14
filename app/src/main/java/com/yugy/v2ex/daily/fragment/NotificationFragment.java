package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * Created by yugy on 14-3-13.
 */
public class NotificationFragment extends Fragment{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_notification, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_fragment_notification);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.txt_fragment_notification_empty));
        return mPullToRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }

}
