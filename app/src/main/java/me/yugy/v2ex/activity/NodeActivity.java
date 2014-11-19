package me.yugy.v2ex.activity;

import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.github.myutils.MathUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.TopicsAdapter;
import me.yugy.v2ex.dao.datahelper.NodeTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NodesDataHelper;
import me.yugy.v2ex.dao.dbinfo.NodeTopicsDBInfo;
import me.yugy.v2ex.model.Node;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;
import me.yugy.v2ex.utils.UIUtils;
import me.yugy.v2ex.widget.AlphaForegroundColorSpan;
import me.yugy.v2ex.widget.PauseOnScrollListener2;

/**
 * Created by yugy on 14/11/19.
 */
public class NodeActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static void launch(Context context, int nodeId) {
        Intent intent = new Intent(context, NodeActivity.class);
        intent.putExtra("node_id", nodeId);
        context.startActivity(intent);
    }

    @InjectView(R.id.recycler_view) RecyclerView mRecyclerView;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.header) RelativeLayout mHeader;
    @InjectView(R.id.head_icon) CircleImageView mHeadIcon;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.tagline) TextView mTagline;
    private int mNodeId;
    private int mActionBarSize;
    private ObjectAnimator mHeaderBackgroundAnimator;
    private ColorDrawable mActionBarBackground;
    private SpannableString mActionBarTitle;
    private AlphaForegroundColorSpan mActionBarTitleColorSpan;
    private TopicsAdapter mAdapter;
    private NodeTopicsDataHelper mDataHelper;

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
        mActionBarSize = UIUtils.getActionBarHeight(this);

        mAdapter = new TopicsAdapter(this);
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
                            mActionBarBackground = new ColorDrawable(palette.getMutedColor(0xFF161616));
                            mActionBarBackground.setAlpha(0);
                            mToolbar.setBackgroundDrawable(mActionBarBackground);

                            ColorDrawable headerBackground = new ColorDrawable(palette.getMutedColor(0xFF161616));
                            headerBackground.setAlpha(0);
                            mHeader.setBackgroundDrawable(headerBackground);

                            mHeaderBackgroundAnimator = ObjectAnimator.ofInt(headerBackground, "alpha",
                                    255);
                            mHeaderBackgroundAnimator.setDuration(600);
                            mHeaderBackgroundAnimator.start();
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
        RequestManager.getInstance().getNodeTopics(this, mNodeId, new Response.Listener<Topic[]>() {
            @Override
            public void onResponse(Topic[] response) {

            }
        }, new SimpleErrorListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usercenter, menu);
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

}
