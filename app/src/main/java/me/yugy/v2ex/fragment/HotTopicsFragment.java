package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MessageUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.TopicActivity;
import me.yugy.v2ex.adapter.TopicsAdapter;
import me.yugy.v2ex.dao.datahelper.HotTopicsDataHelper;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.widget.PauseOnScrollListener2;

/**
 * Created by yugy on 14/11/14.
 */
public class HotTopicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    private HotTopicsDataHelper mDataHelper;
    private TopicsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataHelper = new HotTopicsDataHelper();
        mAdapter = new TopicsAdapter(getActivity(), TopicActivity.TYPE_HOT);
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new PauseOnScrollListener2(ImageLoader.getInstance(), true, true));

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        if(mDataHelper.getCount() == 0) {
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mRecyclerView.getChildAt(i).findViewById(R.id.head_icon);
            if (view != null && !view.isShown()) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.hot_topics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            if (!mRefreshLayout.isRefreshing()) {
                getData();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getData() {
        if (!mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(true);
        }
        RequestManager.getInstance().getHotTopics(this, new Response.Listener<Topic[]>() {
            @Override
            public void onResponse(Topic[] response) {
                mRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshLayout.setRefreshing(false);
                if (error.getCause() instanceof TimeoutError) {
                    MessageUtils.toast(getActivity(), "网络超时");
                } else {
                    MessageUtils.toast(getActivity(), error.toString());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestManager.getInstance().cancel(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
}
