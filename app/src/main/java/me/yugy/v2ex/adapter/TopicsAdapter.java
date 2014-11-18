package me.yugy.v2ex.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.UserCenterActivity;
import me.yugy.v2ex.fragment.UserInfoFirstFragment;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.widget.RelativeTimeTextView;

/**
 * Created by yugy on 14/11/16.
 */
public class TopicsAdapter extends CursorAdapter2<TopicsAdapter.TopicHolder>{

    public TopicsAdapter(Context context) {
        super(context, null);
    }

    @Override
    public TopicHolder newViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicHolder(view);
    }

    @Override
    public void bindViewHolder(TopicHolder viewHolder, Cursor cursor) {
//        Debug.startMethodTracing();

        Topic topic = Topic.fromCursor(cursor);
        viewHolder.parse(topic);

//        Debug.stopMethodTracing();
    }

    public class TopicHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.time) RelativeTimeTextView time;
        @InjectView(R.id.content) TextView content;
        @InjectView(R.id.head_icon) CircleImageView headIcon;
        @InjectView(R.id.name) TextView name;
        @InjectView(R.id.comment_count) TextView commentCount;
        private String mUsername;

        public TopicHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void parse (Topic topic) {
            mUsername = topic.member.username;
            title.setText(topic.title);
            time.setReferenceTime(topic.created * 1000);
            content.setText(topic.content_rendered);
            ImageLoader.getInstance().displayImage(topic.member.avatar, headIcon);
            name.setText(topic.member.username);
            commentCount.setText(String.format("%d 个回复", topic.replies));
        }

        @OnClick(R.id.head_icon)
        void onHeadIconClick(View view) {
            UserInfoFirstFragment.HeadIconInfo headIconInfo = new UserInfoFirstFragment.HeadIconInfo();
            int[] screenLocation = new int[2];
            headIcon.getLocationOnScreen(screenLocation);
            headIconInfo.left = screenLocation[0];
            headIconInfo.top = screenLocation[1];
            headIconInfo.width = view.getWidth();
            headIconInfo.height = view.getHeight();
            UserCenterActivity.launch(headIcon.getContext(), mUsername, headIconInfo);
            ((Activity)headIcon.getContext()).overridePendingTransition(0, 0);
        }
    }
}
