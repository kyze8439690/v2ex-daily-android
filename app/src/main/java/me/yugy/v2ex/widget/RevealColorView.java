package me.yugy.v2ex.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

/**
 * Created by yugy on 14/11/20.
 */
public class RevealColorView extends ViewGroup{

    private static final int DURATION = 600;

    private static final float SCALE = 8f;

    private View mInkView;
    private int mInkColor;
    private ShapeDrawable mCircle;
    private ViewPropertyAnimator mAnimator;

    public RevealColorView(Context context) {
        this(context, null);
    }

    public RevealColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

        mInkView = new View(context);
        addView(mInkView);

        mCircle = new ShapeDrawable(new OvalShape());

        mInkView.setBackgroundDrawable(mCircle);
        mInkView.setVisibility(View.INVISIBLE);
        mInkView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInkView.layout(left, top, left + mInkView.getMeasuredWidth(), top + mInkView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        final float circleSize = (float) Math.sqrt(width * width + height * height) * 2f;
        final int size = (int) (circleSize / SCALE);
        final int sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        mInkView.measure(sizeSpec, sizeSpec);
    }

    public void reveal(final int x, final int y, final int color, Animator.AnimatorListener listener) {
        reveal(x, y, color, 0, DURATION, listener);
    }

    public void reveal(final int x, final int y, final int color, final int startRadius, long duration, final Animator.AnimatorListener listener) {
        if (color == mInkColor) {
            return;
        }
        mInkColor = color;

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mCircle.getPaint().setColor(color);
        mInkView.setVisibility(View.VISIBLE);

        final float startScale = startRadius * 2f / mInkView.getHeight();
        final float finalScale = calculateScale(x, y) * SCALE;

        prepareView(mInkView, x, y, startScale);
        mAnimator = mInkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundColor(color);
                mInkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(mAnimator);
        mAnimator.start();
    }

    public void hide(final int x, final int y, final int color, final Animator.AnimatorListener listener) {
        hide(x, y, color, 0, DURATION, listener);
    }

    public void hide(final int x, final int y, final int color, final int endRadius, final long duration, final Animator.AnimatorListener listener) {
        mInkColor = Color.TRANSPARENT;

        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mInkView.setVisibility(View.VISIBLE);
        setBackgroundColor(color);

        final float startScale = calculateScale(x, y) * SCALE;
        final float finalScale = endRadius * SCALE / mInkView.getWidth();

        prepareView(mInkView, x, y, startScale);

        mAnimator = mInkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mInkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(mAnimator);
        mAnimator.start();
    }

    public ViewPropertyAnimator prepareAnimator(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animator.withLayer();
        }
        animator.setInterpolator(BakedBezierInterpolator.getInstance());
        return animator;
    }

    private void prepareView(View view, int x, int y, float scale) {
        final int centerX = (view.getWidth() / 2);
        final int centerY = (view.getHeight() / 2);
        view.setTranslationX(x - centerX);
        view.setTranslationY(y - centerY);
        view.setPivotX(centerX);
        view.setPivotY(centerY);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private float calculateScale(int x, int y) {
        final float centerX = getWidth() / 2f;
        final float centerY = getHeight() / 2f;
        final float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        final float deltaX = centerX - x;
        final float deltaY = centerY - y;
        final float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return 0.5f + (distance / maxDistance) * 0.5f;
    }
}