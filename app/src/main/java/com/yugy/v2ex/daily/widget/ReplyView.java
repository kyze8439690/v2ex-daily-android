package com.yugy.v2ex.daily.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.PhotoViewActivity;
import com.yugy.v2ex.daily.activity.UserActivity;
import com.yugy.v2ex.daily.fragment.CommentDialogFragment;
import com.yugy.v2ex.daily.model.MemberModel;
import com.yugy.v2ex.daily.model.ReplyModel;
import com.yugy.v2ex.daily.network.AsyncImageGetter;

import java.util.ArrayList;

/**
 * Created by yugy on 14-2-25.
 */
public class ReplyView extends RelativeLayout implements View.OnClickListener{
    public ReplyView(Context context) {
        super(context);
        init();
    }

    public ReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private SelectorImageView mHead;
    private TextView mName;
    private ImageButton mReply;
    private RelativeTimeTextView mTime;
    private TextView mContent;

    private MemberModel mMember;

    private int mTopicId;

    private void init(){
        inflate(getContext(), R.layout.view_reply, this);
        mHead = (SelectorImageView) findViewById(R.id.img_view_reply_head);
        mReply = (ImageButton) findViewById(R.id.btn_view_reply_reply);
        mName = (TextView) findViewById(R.id.txt_view_reply_name);
        mTime = (RelativeTimeTextView) findViewById(R.id.txt_view_reply_time);
        mContent = (TextView) findViewById(R.id.txt_view_reply_content);

        mHead.setOnClickListener(this);
        mReply.setOnClickListener(this);
    }

    public void parse(boolean logined, int topicId, ReplyModel replyModel){
        if(logined){
            mReply.setVisibility(VISIBLE);
        }else{
            mReply.setVisibility(INVISIBLE);
        }
        mTopicId = topicId;
        mName.setText(replyModel.member.username);
        mTime.setReferenceTime(replyModel.created * 1000);

        Spanned spanned = Html.fromHtml(replyModel.contentRendered, new AsyncImageGetter(getContext(), mContent), null);
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
//            final String imageUrl = currentSpan.getSource();
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
        mContent.setMovementMethod(LinkMovementMethod.getInstance());

        mMember = replyModel.member;

        ImageLoader.getInstance().displayImage(replyModel.member.avatar, mHead);
    }

    @Override
    public void onClick(View v) {
        Bundle argument;
        switch (v.getId()){
            case R.id.img_view_reply_head:
                Intent intent = new Intent(getContext(), UserActivity.class);
                argument = new Bundle();
                argument.putParcelable("model", mMember);
                intent.putExtra("argument", argument);
                getContext().startActivity(intent);
                break;
            case R.id.btn_view_reply_reply:
                CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
                argument = new Bundle();
                argument.putInt("topic_id", mTopicId);
                argument.putString("comment_content", "@" + mMember.username + " ");
                commentDialogFragment.setArguments(argument);
                commentDialogFragment.show(((Activity)getContext()).getFragmentManager(), "comment");
                break;
        }

    }
}
