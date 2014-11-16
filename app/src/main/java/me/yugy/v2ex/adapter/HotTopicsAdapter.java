package me.yugy.v2ex.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Debug;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.model.Topic;
import me.yugy.v2ex.widget.RelativeTimeTextView;

/**
 * Created by yugy on 14/11/16.
 */
public class HotTopicsAdapter extends CursorAdapter2<HotTopicsAdapter.TopicHolder>{

    public HotTopicsAdapter(Context context) {
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
        viewHolder.title.setText(topic.title);
        viewHolder.time.setReferenceTime(topic.created * 1000);
        viewHolder.content.setText(topic.content_rendered);
        ImageLoader.getInstance().displayImage(topic.member.avatar, viewHolder.headIcon);
        viewHolder.name.setText(topic.member.username);
        viewHolder.commentCount.setText(String.format("%d 个回复", topic.replies));

//        Debug.stopMethodTracing();
    }

    public class TopicHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.time) RelativeTimeTextView time;
        @InjectView(R.id.content) TextView content;
        @InjectView(R.id.head_icon) CircleImageView headIcon;
        @InjectView(R.id.name) TextView name;
        @InjectView(R.id.comment_count) TextView commentCount;

        public TopicHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
