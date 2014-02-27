package com.yugy.v2ex.daily.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.yugy.v2ex.daily.utils.ScreenUtils;

/**
 * Created by yugy on 14-2-26.
 */
public class AsyncImageGetter implements Html.ImageGetter {

    private Context mContext;
    private TextView mContainer;

    private int mMaxWidth;

    public AsyncImageGetter(Context context, TextView container){
        mContext = context;
        mContainer = container;
        mMaxWidth = ScreenUtils.getDisplayWidth(mContext) - ScreenUtils.dp(mContext, 100);
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        RequestManager.getImageLoader().get(source, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                Bitmap bitmap = imageContainer.getBitmap();
                if(bitmap != null){
                    int width;
                    int height;
                    if(bitmap.getWidth() > mMaxWidth){
                        width = mMaxWidth;
                        height = mMaxWidth * bitmap.getHeight() / bitmap.getWidth();
                    }else{
                        width = bitmap.getWidth();
                        height = bitmap.getHeight();
                    }
                    Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                    drawable.setBounds(0, 0, width, height);
                    urlDrawable.setBounds(0, 0, width, height);
                    urlDrawable.mDrawable = drawable;
//                    mContainer.invalidate();
                    mContainer.setText(mContainer.getText());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        return urlDrawable;
    }

    public static class URLDrawable extends BitmapDrawable{

        protected Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if(mDrawable != null){
                mDrawable.draw(canvas);
            }
        }
    }
}
