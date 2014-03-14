package com.yugy.v2ex.daily.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.model.NotificationModel;
import com.yugy.v2ex.daily.network.AsyncImageGetter;

/**
 * Created by yugy on 14-3-14.
 */
public class NotificationView extends RelativeLayout{
    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private RelativeTimeTextView mTime;
    private TextView mContent;

    private void init(){
        inflate(getContext(), R.layout.view_notification, this);
        mTitle = (TextView) findViewById(R.id.txt_view_notification_title);
        mTime = (RelativeTimeTextView) findViewById(R.id.txt_view_notification_time);
        mContent = (TextView) findViewById(R.id.txt_view_notification_content);
    }

    public void parse(NotificationModel model){
        mTitle.setText(model.title);
        mTime.setReferenceTime(model.time);
        mContent.setText(Html.fromHtml(model.content, new AsyncImageGetter(getContext(), mContent), null));
    }
}
