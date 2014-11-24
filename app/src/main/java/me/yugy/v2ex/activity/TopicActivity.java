package me.yugy.v2ex.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.github.myutils.UIUtils;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.EmptyAdapter;
import me.yugy.v2ex.dao.datahelper.HotTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NewestTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.NodeTopicsDataHelper;
import me.yugy.v2ex.dao.datahelper.UserTopicsDataHelper;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.widget.CircularProgressBar;
import me.yugy.v2ex.widget.RelativeTimeTextView;

/**
 * Created by yugy on 14/11/20.
 */
public class TopicActivity extends BaseActivity{

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
    private HeaderContainer mHeaderContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.inject(this);

        getTopicData();
        initTopicData();

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
        mHeaderContainer = new HeaderContainer(headerView);
        if (mTopic != null) {
            mHeaderContainer.parse(mTopic);
        }
        mListView.addHeaderView(headerView, null, false);
        mListView.setAdapter(new EmptyAdapter());

    }

    public class HeaderContainer {

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.time) RelativeTimeTextView time;
        @InjectView(R.id.content) TextView content;
        @InjectView(R.id.head_icon) CircleImageView headIcon;
        @InjectView(R.id.name) TextView name;
        @InjectView(R.id.comment_count) TextView commentCount;
        @InjectView(R.id.node) TextView node;
        private String mUsername;
        private int mNodeId;

        public HeaderContainer(View itemView) {
            ButterKnife.inject(this, itemView);
        }

        public void parse (final Topic topic) {
            mUsername = topic.member.username;
            mNodeId = topic.node.id;
            title.setText(topic.title);
            time.setReferenceTime(topic.created * 1000);
            content.setText(topic.content_rendered);
            ImageLoader.getInstance().displayImage(topic.member.avatar, headIcon);
            name.setText(topic.member.username);
            commentCount.setText(String.format("%d 个回复", topic.replies));
            node.setText(topic.node.title);
        }

        @OnClick(R.id.head_icon)
        void onHeadIconClick(View view) {
            view.setVisibility(View.INVISIBLE);
            HeadIconInfo headIconInfo = new HeadIconInfo();
            int[] screenLocation = new int[2];
            headIcon.getLocationOnScreen(screenLocation);
            headIconInfo.left = screenLocation[0];
            headIconInfo.top = screenLocation[1];
            headIconInfo.width = view.getWidth();
            headIconInfo.height = view.getHeight();
            UserCenterActivity.launch(headIcon.getContext(), mUsername, headIconInfo);
        }

        @OnClick(R.id.node)
        void onNodeClick(View view) {
            NodeActivity.launch(view.getContext(), mNodeId);
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
    protected void onResume() {
        super.onResume();
        if (!mHeaderContainer.headIcon.isShown()) {
            mHeaderContainer.headIcon.setVisibility(View.VISIBLE);
        }
    }
}


