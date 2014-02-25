package com.yugy.v2ex.daily.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yugy.v2ex.daily.R;


/**
 * Created by yugy on 13-11-7.
 */
public class SelectorImageView extends ImageView{
    public SelectorImageView(Context context) {
        super(context);
        init();
    }

    public SelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private Drawable mForegroundSelector;

    private void init(){
        mForegroundSelector = getResources().getDrawable(R.drawable.holo_selector);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mForegroundSelector.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mForegroundSelector.setBounds(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mForegroundSelector.draw(canvas);
    }
}
