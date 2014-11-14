package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MessageUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;

/**
 * Created by yugy on 14/11/14.
 */
public class HotTopicsFragment extends Fragment {

    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestManager.getInstance().getHotTopics(this, new Response.Listener<Topic[]>() {
            @Override
            public void onResponse(Topic[] response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getCause() instanceof TimeoutError) {
                    MessageUtils.toast(getActivity(), "网络超时");
                } else {
                    MessageUtils.toast(getActivity(), error.toString());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }
}
