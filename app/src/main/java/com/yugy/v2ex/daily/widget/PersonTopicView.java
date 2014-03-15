package com.yugy.v2ex.daily.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.NodeActivity;
import com.yugy.v2ex.daily.activity.PhotoViewActivity;
import com.yugy.v2ex.daily.activity.UserActivity;
import com.yugy.v2ex.daily.model.MemberModel;
import com.yugy.v2ex.daily.model.TopicModel;
import com.yugy.v2ex.daily.network.AsyncImageGetter;

import java.util.ArrayList;

/**
 * Created by yugy on 14-3-15.
 */
public class PersonTopicView extends RelativeLayout implements View.OnClickListener{
    public PersonTopicView(Context context) {
        super(context);
        init();
    }

    public PersonTopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersonTopicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private TextView mContent;
    private RelativeTimeTextView mTime;
    private TextView mReplies;
    private TextView mNode;

    private int mNodeId;
    private int mTopicId;
    private MemberModel mMember;

    private void init(){
        inflate(getContext(), R.layout.view_person_topic, this);
        mTitle = (TextView) findViewById(R.id.txt_view_person_topic_title);
        mContent = (TextView) findViewById(R.id.txt_view_person_topic_content);
        mTime = (RelativeTimeTextView) findViewById(R.id.txt_view_person_topic_time);
        mReplies = (TextView) findViewById(R.id.txt_view_person_topic_replies);
        mNode = (TextView) findViewById(R.id.txt_view_person_topic_node);

        mNode.setOnClickListener(this);
    }

    public void parse(TopicModel model){
        mTopicId = model.id;
        mTitle.setText(model.title);
        Spanned spanned = Html.fromHtml(model.contentRendered, new AsyncImageGetter(getContext(), mContent), null);
        SpannableStringBuilder htmlSpannable;
        if(spanned instanceof SpannableStringBuilder){
            htmlSpannable = (SpannableStringBuilder) spanned;
        } else {
            htmlSpannable = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<String> imagePositions = new ArrayList<String>();
        for(ImageSpan currentSpan : spans){
            final String imageUrl = currentSpan.getSource();
            final int start = htmlSpannable.getSpanStart(currentSpan);
            final int end   = htmlSpannable.getSpanEnd(currentSpan);
            imagePositions.add(start + "," + end);
            imageUrls.add(imageUrl);
        }

        for(ImageSpan currentSpan : spans){
            final int start = htmlSpannable.getSpanStart(currentSpan);
            final int end   = htmlSpannable.getSpanEnd(currentSpan);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PhotoViewActivity.launch(getContext(), imagePositions.indexOf(start + "," + end), imageUrls);
                }
            };

            ClickableSpan[] clickSpans = htmlSpannable.getSpans(start, end, ClickableSpan.class);
            if(clickSpans != null && clickSpans.length != 0) {

                for(ClickableSpan c_span : clickSpans) {
                    htmlSpannable.removeSpan(c_span);
                }
            }

            htmlSpannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mContent.setText(spanned);

        mTime.setReferenceTime(model.created * 1000);
        mReplies.setText(model.replies + " 个回复");
        mNode.setText(model.node.title);

        mMember = model.member;
        mNodeId = model.node.id;

    }

    public int getTopicId() {
        return mTopicId;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle argument;
        switch (v.getId()){
            case R.id.img_view_topic_head:
                intent = new Intent(getContext(), UserActivity.class);
                argument = new Bundle();
                argument.putParcelable("model", mMember);
                intent.putExtra("argument", argument);
                getContext().startActivity(intent);
                break;
            case R.id.txt_view_topic_node:
                intent = new Intent(getContext(), NodeActivity.class);
                argument = new Bundle();
                argument.putInt("node_id", mNodeId);
                intent.putExtra("argument", argument);
                getContext().startActivity(intent);
                break;
        }
    }
}
