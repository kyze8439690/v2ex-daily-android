package me.yugy.v2ex.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import me.yugy.github.myutils.UIUtils;
import me.yugy.v2ex.R;

/**
 * Created by yugy on 14/11/25.
 */
public class AsyncImageGetter implements Html.ImageGetter {

    private TextView mContainer;
    private Drawable mDefaultDrawable;

    private int mMaxWidth;

    public AsyncImageGetter(TextView container){
        mContainer = container;
        mMaxWidth = UIUtils.getDisplayWidth(container.getContext()) - UIUtils.dp(container.getContext(), 32);
        mDefaultDrawable = container.getContext().getResources().getDrawable(R.drawable.ic_launcher);
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        ImageLoader.getInstance().loadImage(source, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    int width;
                    int height;
                    if (bitmap.getWidth() > mMaxWidth) {
                        width = mMaxWidth;
                        height = mMaxWidth * bitmap.getHeight() / bitmap.getWidth();
                    } else {
                        width = bitmap.getWidth();
                        height = bitmap.getHeight();
                    }
                    Drawable drawable = new BitmapDrawable(mContainer.getContext().getResources(), bitmap);
                    drawable.setBounds(0, 0, width, height);
                    urlDrawable.setBounds(0, 0, width, height);
                    urlDrawable.mDrawable = drawable;
                    //reset text to invalidate.
                    mContainer.setText(mContainer.getText());
                }
            }
        });
        return urlDrawable;
    }

    public class URLDrawable extends BitmapDrawable{

        protected Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if(mDrawable != null){
                mDrawable.draw(canvas);
            }else{
                mDefaultDrawable.draw(canvas);
            }
        }
    }
}
