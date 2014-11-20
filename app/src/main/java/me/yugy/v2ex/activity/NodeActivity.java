package me.yugy.v2ex.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.github.myutils.MathUtils;
import me.yugy.github.myutils.UIUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.TopicsAdapter;
import me.yugy.v2ex.dao.datahelper.NodeTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NodesDataHelper;
import me.yugy.v2ex.dao.dbinfo.NodeTopicsDBInfo;
import me.yugy.v2ex.model.Node;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;
import me.yugy.v2ex.utils.UIUtils2;
import me.yugy.v2ex.widget.AlphaForegroundColorSpan;
import me.yugy.v2ex.widget.CircularProgressBar;
import me.yugy.v2ex.widget.CircularProgressDrawable;
import me.yugy.v2ex.widget.PauseOnScrollListener2;
import me.yugy.v2ex.widget.RevealColorView;

/**
 * Created by yugy on 14/11/19.
 */
public class NodeActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static void launch(Context context, int nodeId) {
        Intent intent = new Intent(context, NodeActivity.class);
        intent.putExtra("node_id", nodeId);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.header) RelativeLayout mHeader;
    @InjectView(R.id.reveal) RevealColorView mRevealColorView;
    @InjectView(R.id.head_icon) CircleImageView mHeadIcon;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.tagline) TextView mTagline;
    private int mNodeId;
    private int mActionBarSize;
    private ColorDrawable mActionBarBackground;
    private SpannableString mActionBarTitle;
    private AlphaForegroundColorSpan mActionBarTitleColorSpan;
    private TopicsAdapter mAdapter;
    private NodeTopicsDataHelper mDataHelper;
    private boolean mIsLoading = false;
    private CircularProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
        ButterKnife.inject(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); mRecyclerView.setOnScrollListener(new PauseOnScrollListener2(
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

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        mNodeId = getIntent().getIntExtra("node_id", 0);
        mActionBarSize = UIUtils2.getActionBarHeight(this);

        mAdapter = new TopicsAdapter(this, TopicActivity.TYPE_NODE);
        mDataHelper = new NodeTopicsDataHelper();
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(2, null, this);

        parseHeaderData();

        if (mDataHelper.getCount(mNodeId) == 0) {
            getNodeTopicsData();
        }
    }

    private void parseHeaderData() {
        Node node = new NodesDataHelper().select(mNodeId);
        if (node != null) {
            ImageLoader.getInstance().displayImage(node.avatar, mHeadIcon, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    Palette.generateAsync(loadedImage, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int color = palette.getVibrantColor(0xFF161616);
                            if (color == Color.TRANSPARENT) { color = 0xFF161616; }
                            mActionBarBackground = new ColorDrawable(color);
                            mActionBarBackground.setAlpha(0);
                            mToolbar.setBackgroundDrawable(mActionBarBackground);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(palette.getDarkMutedColor(Color.BLACK));
                                getWindow().setNavigationBarColor(palette.getDarkMutedColor(Color.BLACK));
                            }
                            mRevealColorView.reveal(mRevealColorView.getWidth() / 2, UIUtils.dp(NodeActivity.this, 64),
                                    palette.getMutedColor(0xFF161616), null);
                        }
                    });
                }
            });
            mName.setText(node.title);
            mTagline.setText(node.title_alternative);
            mActionBarTitle = new SpannableString(node.title);
            mActionBarTitleColorSpan = new AlphaForegroundColorSpan(Color.WHITE);
            setActionBarTitleAlpha(0);
        }
    }

    private void getNodeTopicsData() {
        mIsLoading = true;
        invalidateOptionsMenu();
        RequestManager.getInstance().getNodeTopics(this, mNodeId, new Response.Listener<Topic[]>() {
            @Override
            public void onResponse(Topic[] response) {
                stopLoadingAnimation();
            }
        }, new SimpleErrorListener(this){
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                stopLoadingAnimation();
            }
        });
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

    private int getHeaderMaxTranslationY() {
        return mHeader.getHeight() - mActionBarSize;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getNodeTopicsData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setActionBarTitleAlpha(float alpha){
        mActionBarTitleColorSpan.setAlpha(alpha);
        mActionBarTitle.setSpan(mActionBarTitleColorSpan, 0, mActionBarTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(mActionBarTitle);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader(NodeTopicsDBInfo.NID + "=?", new String[]{String.valueOf(mNodeId)});
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancel(this);
        super.onDestroy();
    }
}
