package me.yugy.v2ex.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.listener.OnPaletteColorGenerateListener;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Member;

/**
 * Created by yugy on 14/11/17.
 */
public class UserInfoFirstFragment extends Fragment{

    public static UserInfoFirstFragment newInstance(String username, HeadIconInfo headIconInfo) {
        UserInfoFirstFragment fragment = new UserInfoFirstFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        if (headIconInfo != null) {
            args.putParcelable("headIconInfo", headIconInfo);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.head_icon) CircleImageView mHeadIcon;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.tagline) TextView mTagline;
    private String mUsername;
    private HeadIconInfo mHeadIconInfo;
    private AnimatorSet mAnimatorSet;
    private AnimationSet mAnimationSet;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private OnPaletteColorGenerateListener mOnPaletteColorGenerateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_userinfo_first, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUsername= getArguments().getString("username");
        if (getArguments().containsKey("headIconInfo")) {
            mHeadIconInfo = getArguments().getParcelable("headIconInfo");
        }

        mName.setText(mUsername);

        initData();
    }

    public void initData() {
        Member member = new MembersDataHelper().select(mUsername);
        if (member != null) {
            ImageLoader.getInstance().displayImage(member.avatar, mHeadIcon, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (mHeadIconInfo != null) {
                        mHeadIcon.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mHeadIcon.getViewTreeObserver().removeOnPreDrawListener(this);
                                int[] screenLocation = new int[2];
                                mHeadIcon.getLocationOnScreen(screenLocation);
                                mLeftDelta = mHeadIconInfo.left - screenLocation[0];
                                mTopDelta = mHeadIconInfo.top - screenLocation[1];
                                mWidthScale = (float) mHeadIconInfo.width / mHeadIcon.getWidth();
                                mHeightScale = (float) mHeadIconInfo.height / mHeadIcon.getHeight();

                                mAnimationSet = new AnimationSet(false);
                                ScaleAnimation scaleAnimation = new ScaleAnimation(mWidthScale, 1f, mHeightScale, 1f, 0f, 0f);
                                mAnimationSet.addAnimation(scaleAnimation);
                                TranslateAnimation yTranslateAnimation = new TranslateAnimation(0f, 0f, mTopDelta, 0f);
                                yTranslateAnimation.setInterpolator(new PathInterpolator());
                                mAnimationSet.addAnimation(yTranslateAnimation);
                                TranslateAnimation xTranslateAnimation = new TranslateAnimation(mLeftDelta, 0f, 0f, 0f);
                                mAnimationSet.addAnimation(xTranslateAnimation);
                                mAnimationSet.setDuration(600);
                                mHeadIcon.startAnimation(mAnimationSet);

                                return true;
                            }
                        });
                    }
                    Palette.generateAsync(loadedImage, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (mOnPaletteColorGenerateListener != null) {
                                mOnPaletteColorGenerateListener.onGenerated(palette);
                            }
                        }
                    });
                }
            });
            mTagline.setText(member.tagline);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnPaletteColorGenerateListener = (OnPaletteColorGenerateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getName() + " should implement the OnPaletteColorGenerateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPaletteColorGenerateListener = null;
    }

    public void playExitAnimation(Animation.AnimationListener listener) {
        mName.setAlpha(0);
        mTagline.setAlpha(0);

        int[] screenLocation = new int[2];
        mHeadIcon.getLocationOnScreen(screenLocation);
        mLeftDelta = mHeadIconInfo.left - screenLocation[0];
        mTopDelta = mHeadIconInfo.top - screenLocation[1];

        mAnimationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, mWidthScale, 1f, mHeightScale, 0f, 0f);
        mAnimationSet.addAnimation(scaleAnimation);
        TranslateAnimation yTranslateAnimation = new TranslateAnimation(0f, 0f, 0f, mTopDelta);
        yTranslateAnimation.setInterpolator(new ReversePathInterpolator());
        mAnimationSet.addAnimation(yTranslateAnimation);
        TranslateAnimation xTranslateAnimation = new TranslateAnimation(0f, mLeftDelta, 0f, 0f);
        mAnimationSet.addAnimation(xTranslateAnimation);
        mAnimationSet.setDuration(600);
        mAnimationSet.setFillAfter(true);
        mAnimationSet.setAnimationListener(listener);
        mHeadIcon.startAnimation(mAnimationSet);
    }

    private static class PathInterpolator implements Interpolator{
        @Override
        public float getInterpolation(float input) {
            return (float) Math.pow(1 - Math.pow(input - 1, 2), 0.5f);
        }
    }

    private static class ReversePathInterpolator implements Interpolator{
        @Override
        public float getInterpolation(float input) {
            return (float) (1 - Math.pow(1 - Math.pow(input, 2), 0.5f));
        }
    }
}
