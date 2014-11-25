package me.yugy.v2ex.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.UIUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.RepliesAdapter;
import me.yugy.v2ex.dao.datahelper.HotTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NewestTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NodeTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.RepliesDataHelper;
import me.yugy.v2ex.dao.datahelper.UserTopicsDataHelper;
import me.yugy.v2ex.dao.dbinfo.ReplyDBInfo;
import me.yugy.v2ex.model.Reply;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;
import me.yugy.v2ex.widget.CircularProgressBar;
import me.yugy.v2ex.widget.CircularProgressDrawable;
import me.yugy.v2ex.widget.RevealLayout;
import me.yugy.v2ex.widget.container.TopicHeaderContainer;

/**
 * Created by yugy on 14/11/20.
 */
public class TopicActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_HOT, TYPE_NEWEST, TYPE_NODE, TYPE_USER})
    public @interface Type {}

    public static final int TYPE_HOT = 0;
    public static final int TYPE_NEWEST = 1;
    public static final int TYPE_NODE = 2;
    public static final int TYPE_USER = 3;

    public static void launch(Context context, @Type int type, int topicId) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("topicId", topicId);
        context.startActivity(intent);
    }

    @InjectView(R.id.list) ListView mListView;
    private Topic mTopic;
    private boolean mIsLoading = false;
    private CircularProgressBar mProgressBar;
    private TopicHeaderContainer mHeaderContainer;
    private RepliesDataHelper mDataHelper;
    private RepliesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.inject(this);

        getSupportActionBar().setElevation(UIUtils.dp(this, 2));
        mDataHelper = new RepliesDataHelper();
        mAdapter = new RepliesAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

        getTopicData();
        initTopicData();
        getLoaderManager().initLoader(4, null, this);

        getRepliesData();
    }

    private void getTopicData() {
        int type = getIntent().getIntExtra("type", -1);
        int topicId = getIntent().getIntExtra("topicId", 0);

        switch (type) {
            case TYPE_HOT:
                mTopic = new HotTopicsDataHelper().select(topicId);
                break;
            case TYPE_NEWEST:
                mTopic = new NewestTopicsDataHelper().select(topicId);
                break;
            case TYPE_NODE:
                mTopic = new NodeTopicsDataHelper().select(topicId);
                break;
            case TYPE_USER:
                mTopic = new UserTopicsDataHelper().select(topicId);
                break;
        }
    }

    private void initTopicData() {
        View headerView = getLayoutInflater().inflate(R.layout.header_topic, mListView, false);
        mHeaderContainer = new TopicHeaderContainer(headerView);
        if (mTopic != null) {
            mHeaderContainer.parse(mTopic);
            mListView.addHeaderView(headerView, null, false);
        }
    }

    private void getRepliesData() {
        mIsLoading = true;
        invalidateOptionsMenu();
        RequestManager.getInstance().getReplies(this, mTopic.id, new Response.Listener<Reply[]>() {
            @Override
            public void onResponse(Reply[] response) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getRepliesData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mHeaderContainer.headIcon.isShown()) {
            mHeaderContainer.headIcon.setVisibility(View.VISIBLE);
        }
        int childCount = mListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mListView.getChildAt(i);
            View headIcon = view.findViewById(R.id.head_icon);
            if (headIcon != null && !headIcon.isShown()) {
                headIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancel(this);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return mDataHelper.getCursorLoader(ReplyDBInfo.TID + "=?", new String[]{String.valueOf(mTopic.id)});
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
}


