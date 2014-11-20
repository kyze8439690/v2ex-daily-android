package me.yugy.v2ex.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MathUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.TopicsAdapter;
import me.yugy.v2ex.adapter.UserCenterHeaderPagerAdapter;
import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.dao.datahelper.UserTopicsDataHelper;
import me.yugy.v2ex.dao.dbinfo.UserTopicsDBInfo;
import me.yugy.v2ex.listener.OnPaletteColorGenerateListener;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;
import me.yugy.v2ex.utils.UIUtils;
import me.yugy.v2ex.widget.AlphaForegroundColorSpan;
import me.yugy.v2ex.widget.CirclePageIndicator;
import me.yugy.v2ex.widget.CircularProgressBar;
import me.yugy.v2ex.widget.CircularProgressDrawable;
import me.yugy.v2ex.widget.PauseOnScrollListener2;

/**
 * Created by yugy on 14/11/16.
 */
public class UserCenterActivity extends ActionBarActivity implements OnPaletteColorGenerateListener, LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.header) FrameLayout mHeader;
    @InjectView(R.id.viewpager) ViewPager mViewPager;
    @InjectView(R.id.pager_indicator) CirclePageIndicator mPageIndicator;
    private UserCenterHeaderPagerAdapter mHeaderAdapter;
    private String mUsername;
    private int mUid = -1;
    private ObjectAnimator mHeaderBackgroundAnimator;
    private UserTopicsDataHelper mDataHelper;
    private TopicsAdapter mAdapter;
    private int mActionBarSize;
    private ColorDrawable mActionBarBackground;
    private SpannableString mActionBarTitle;
    private AlphaForegroundColorSpan mActionBarTitleColorSpan;
    private boolean mIsLoading = false;
    private CircularProgressBar mProgressBar;

    public static void launch(Context context, String username, HeadIconInfo headIconInfo) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        intent.putExtra("username", username);
        if (headIconInfo != null) {
            intent.putExtra("headIconInfo", headIconInfo);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnScrollListener(new PauseOnScrollListener2(
                ImageLoader.getInstance(), true, true, new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float translationY = mHeader.getTranslationY() - dy;
                float alpha = MathUtils.clamp(-translationY / getHeaderMaxTranslationY(), 0f, 1f);
                if (mActionBarBackground != null) {
                    mActionBarBackground.setAlpha((int) (alpha * 255));
                }
                setActionBarTitleAlpha(alpha);
                mHeader.setTranslationY(translationY);
            }
        }));
        mRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRecyclerView.setAlpha(0f);
        mRecyclerView.animate().alpha(1f).setDuration(600).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }).start();


        mUsername = getIntent().getStringExtra("username");
        mActionBarTitle = new SpannableString(mUsername);
        mActionBarTitleColorSpan = new AlphaForegroundColorSpan(Color.WHITE);
        setActionBarTitleAlpha(0);
        HeadIconInfo headIconInfo = null;
        if (getIntent().hasExtra("headIconInfo")) {
            headIconInfo = getIntent().getParcelableExtra("headIconInfo");
        }
        mHeaderAdapter = new UserCenterHeaderPagerAdapter(getFragmentManager(), mUsername, headIconInfo);
        mViewPager.setAdapter(mHeaderAdapter);
        mPageIndicator.setViewPager(mViewPager);

        mDataHelper = new UserTopicsDataHelper();
        mAdapter = new TopicsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        getUserInfoData();

        if (mUid != -1) {
            getLoaderManager().initLoader(1, null, this);
        }

        if (mUid != -1 && mDataHelper.getCount(mUid) == 0) {
            getUserTopicsData();
        }

        mActionBarSize = UIUtils.getActionBarHeight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usercenter, menu);
        if (mIsLoading) {
            MenuItem item = menu.findItem(R.id.refresh);
            View actionView = getLayoutInflater().inflate(R.layout.view_menu_loading, null);
            mProgressBar = (CircularProgressBar) actionView.findViewById(R.id.progress);
            int size = me.yugy.github.myutils.UIUtils.dp(this, 48);
            actionView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            MenuItemCompat.setActionView(item, actionView);
            mProgressBar.setIndeterminate(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getUserInfoData();
            getUserTopicsData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setActionBarTitleAlpha(float alpha) {
        mActionBarTitleColorSpan.setAlpha(alpha);
        mActionBarTitle.setSpan(mActionBarTitleColorSpan, 0, mActionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(mActionBarTitle);
    }

    private int getHeaderMaxTranslationY() {
        return mHeader.getHeight() - mActionBarSize;
    }

    private void getUserInfoData() {
        Member member = new MembersDataHelper().select(mUsername);
        if (member != null) {
            mUid = member.id;
        }
        RequestManager.getInstance().getUserInfo(this, mUsername, new Response.Listener<Member>() {
            @Override
            public void onResponse(Member response) {
                mHeaderAdapter.refreshData();
                if (mUid == -1) {
                    Member member = new MembersDataHelper().select(mUsername);
                    if (member != null) {
                        mUid = member.id;
                        getLoaderManager().initLoader(1, null, UserCenterActivity.this);
                    }
                    if (mUid != -1 && mDataHelper.getCount(mUid) == 0) {
                        getUserTopicsData();
                    }
                }
            }
        }, new SimpleErrorListener(this));
    }

    private void getUserTopicsData() {
        mIsLoading = true;
        invalidateOptionsMenu();
        RequestManager.getInstance().getUserTopics(this, mUsername, new Response.Listener<Topic[]>() {
            @Override
            public void onResponse(Topic[] response) {
                stopLoadingAnimation();
            }
        }, new SimpleErrorListener(this) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                stopLoadingAnimation();
            }
        });
    }

    private void stopLoadingAnimation() {
        mIsLoading = false;
        if (mProgressBar != null) {
            mProgressBar.progressiveStop(new CircularProgressDrawable.OnEndListener() {
                @Override
                public void onEnd(CircularProgressDrawable drawable) {
                    invalidateOptionsMenu();
                }
            });
        } else {
            invalidateOptionsMenu();
        }
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
        mRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mRecyclerView.animate().alpha(0f).setDuration(600).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancel(this);
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onGenerated(Palette palette) {
        int color = palette.getMutedColor(0xFF161616);
        if (color == Color.TRANSPARENT) { color = 0xFF161616; }
        mActionBarBackground = new ColorDrawable(color);
        mActionBarBackground.setAlpha(0);
        mToolbar.setBackgroundDrawable(mActionBarBackground);

        ColorDrawable headerBackground = new ColorDrawable(color);
        headerBackground.setAlpha(0);
        mHeader.setBackgroundDrawable(headerBackground);

        mHeaderBackgroundAnimator = ObjectAnimator.ofInt(headerBackground, "alpha",
                255);
        mHeaderBackgroundAnimator.setDuration(600);
        mHeaderBackgroundAnimator.start();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader(UserTopicsDBInfo.MID + "=?", new String[]{String.valueOf(mUid)});
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
