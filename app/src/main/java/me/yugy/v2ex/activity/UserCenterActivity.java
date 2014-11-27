package me.yugy.v2ex.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MathUtils;
import me.yugy.github.myutils.UIUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.TopicsAdapter;
import me.yugy.v2ex.adapter.UserCenterHeaderPagerAdapter;
import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.dao.datahelper.UserTopicsDataHelper;
import me.yugy.v2ex.dao.dbinfo.UserTopicsDBInfo;
import me.yugy.v2ex.listener.OnPaletteColorGenerateListener;
import me.yugy.v2ex.listener.SimpleAnimationListener;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;
import me.yugy.v2ex.utils.UIUtils2;
import me.yugy.v2ex.widget.AlphaForegroundColorSpan;
import me.yugy.v2ex.widget.CirclePageIndicator;
import me.yugy.v2ex.widget.CircularProgressBar;
import me.yugy.v2ex.widget.CircularProgressDrawable;
import me.yugy.v2ex.widget.PauseOnScrollListener2;
import me.yugy.v2ex.widget.RevealColorView;

/**
 * Created by yugy on 14/11/16.
 */
public class UserCenterActivity extends BaseActivity implements OnPaletteColorGenerateListener, LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.header) FrameLayout mHeader;
    @InjectView(R.id.reveal) RevealColorView mRevealColorView;
    @InjectView(R.id.viewpager) ViewPager mViewPager;
    @InjectView(R.id.pager_indicator) CirclePageIndicator mPageIndicator;
    private UserCenterHeaderPagerAdapter mHeaderAdapter;
    private String mUsername;
    private int mUid = -1;
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
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(600);
        mRecyclerView.startAnimation(alphaAnimation);

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
        mAdapter = new TopicsAdapter(this, TopicActivity.TYPE_USER);
        mRecyclerView.setAdapter(mAdapter);

        getUserInfoData();

        if (mUid != -1) {
            getLoaderManager().initLoader(1, null, this);
        }

        if (mUid != -1 && mDataHelper.getCount(mUid) == 0) {
            getUserTopicsData();
        }

        mActionBarSize = UIUtils2.getActionBarHeight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usercenter, menu);
        if (mIsLoading) {
            MenuItem item = menu.findItem(R.id.refresh);
            View actionView = getLayoutInflater().inflate(R.layout.view_menu_loading, null);
            mProgressBar = (CircularProgressBar) actionView.findViewById(R.id.progress);
            int size = UIUtils.dp(this, 48);
            actionView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            MenuItemCompat.setActionView(item, actionView);
            mProgressBar.setIndeterminate(true);
        }
        return true;
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
        mHeaderAdapter.playExitAnimation(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                UserCenterActivity.super.onBackPressed();
            }
        });
        mPageIndicator.setAlpha(0);
        mRevealColorView.hide(mRevealColorView.getWidth() / 2, UIUtils.dp(this, 64), Color.TRANSPARENT, null);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(600);
        mRecyclerView.startAnimation(alphaAnimation);
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancel(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onGenerated(Palette palette) {
        int color = palette.getMutedColor(0xFF161616);
        if (color == Color.TRANSPARENT) { color = 0xFF161616; }
        mActionBarBackground = new ColorDrawable(color);
        mActionBarBackground.setAlpha(0);
        mToolbar.setBackgroundDrawable(mActionBarBackground);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(palette.getDarkMutedColor(Color.BLACK));
            getWindow().setNavigationBarColor(palette.getDarkMutedColor(Color.BLACK));
        }
        mRevealColorView.reveal(mRevealColorView.getWidth() / 2, UIUtils.dp(this, 64),
                color, null);

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
