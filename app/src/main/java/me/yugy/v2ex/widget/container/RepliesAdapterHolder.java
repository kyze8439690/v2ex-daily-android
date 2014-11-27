package me.yugy.v2ex.widget.container;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.UserCenterActivity;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Reply;
import me.yugy.v2ex.widget.AsyncImageGetter;
import me.yugy.v2ex.widget.RelativeTimeTextView;

/**
* Created by yugy on 14/11/25.
*/
public class RepliesAdapterHolder {

    @InjectView(R.id.head_icon) CircleImageView headIcon;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.content) TextView content;
    @InjectView(R.id.time) RelativeTimeTextView time;

    private String mUsername;

    public RepliesAdapterHolder(View view) {
        ButterKnife.inject(this, view);
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void parse(Reply reply) {
        mUsername = reply.member.username;
        ImageLoader.getInstance().displayImage(reply.member.avatar, headIcon);
        name.setText(reply.member.username);
        time.setReferenceTime(reply.created * 1000);

        Spanned spanned = Html.fromHtml(reply.content_rendered, new AsyncImageGetter(content), null);
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
        final ArrayList<String> imageUrls = new ArrayList<String>();
        for (ImageSpan currentSpan : spans) {
            final String imageUrl = currentSpan.getSource();
            final int start = spannableStringBuilder.getSpanStart(currentSpan);
            final int end = spannableStringBuilder.getSpanEnd(currentSpan);
            imageUrls.add(imageUrl);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
//                        PhotoViewActivity.launch(widget.getContext(), imagePositions.indexOf(start + "," + end), imageUrls);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                    widget.getContext().startActivity(intent);
                }
            };

            ClickableSpan[] clickableSpans = spannableStringBuilder.getSpans(start, end, ClickableSpan.class);
            if (clickableSpans != null && clickableSpans.length != 0) {
                for (ClickableSpan span : clickableSpans) {
                    spannableStringBuilder.removeSpan(span);
                }
            }

            spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        content.setText(spanned);
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

}
