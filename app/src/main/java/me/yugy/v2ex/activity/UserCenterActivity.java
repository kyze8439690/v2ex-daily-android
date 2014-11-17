package me.yugy.v2ex.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MessageUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.UserCenterHeaderPagerAdapter;
import me.yugy.v2ex.fragment.UserInfoFirstFragment;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.widget.CirclePageIndicator;

/**
 * Created by yugy on 14/11/16.
 */
public class UserCenterActivity extends ActionBarActivity implements UserInfoFirstFragment.OnPaletteColorGenerateListener{

    public static void launch(Context context, String username, UserInfoFirstFragment.HeadIconInfo headIconInfo) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra("username", username);
        if (headIconInfo != null) {
            intent.putExtra("headIconInfo", headIconInfo);
        }
        context.startActivity(intent);
    }

    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.header) View mHeader;
    @InjectView(R.id.viewpager) ViewPager mViewPager;
    @InjectView(R.id.pager_indicator) CirclePageIndicator mPageIndicator;

    private UserCenterHeaderPagerAdapter mHeaderAdapter;
    private String mUsername;
    private int mDefaultHeaderBackgroundColor = Color.parseColor("#FF161616");
    private ObjectAnimator mHeaderBackgroundAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUsername = getIntent().getStringExtra("username");
        UserInfoFirstFragment.HeadIconInfo headIconInfo = null;
        if (getIntent().hasExtra("headIconInfo")) {
            headIconInfo = getIntent().getParcelableExtra("headIconInfo");
        }
        mHeaderAdapter = new UserCenterHeaderPagerAdapter(getFragmentManager(), mUsername, headIconInfo);
        mViewPager.setAdapter(mHeaderAdapter);
        mPageIndicator.setViewPager(mViewPager);

        getUserInfoData();
    }

    private void getUserInfoData() {
        RequestManager.getInstance().getUserInfo(this, mUsername, new Response.Listener<Member>() {
            @Override
            public void onResponse(Member response) {
                mHeaderAdapter.refreshData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshLayout.setRefreshing(false);
                if (error.getCause() instanceof TimeoutError) {
                    MessageUtils.toast(UserCenterActivity.this, "网络超时");
                } else {
                    MessageUtils.toast(UserCenterActivity.this, error.toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        mHeaderAdapter.playExitAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                UserCenterActivity.super.onBackPressed();
            }
        });
        if (mHeaderBackgroundAnimator != null) {
            mHeaderBackgroundAnimator.reverse();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onGenerated(Palette palette) {
        mHeaderBackgroundAnimator = ObjectAnimator.ofInt(mHeader, "backgroundColor",
                mDefaultHeaderBackgroundColor, palette.getMutedColor(mDefaultHeaderBackgroundColor));
        mHeaderBackgroundAnimator.setDuration(600);
        mHeaderBackgroundAnimator.setEvaluator(new ArgbEvaluator());
        mHeaderBackgroundAnimator.start();
    }
}
