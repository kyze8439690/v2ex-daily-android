package com.yugy.v2ex.daily.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.adapter.PhotoViewerPagerAdapter;
import com.yugy.v2ex.daily.widget.photo.Intents;
import com.yugy.v2ex.daily.widget.photo.PhotoViewCallbacks;
import com.yugy.v2ex.daily.widget.photo.PhotoViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuyexiong on 14-2-28.
 */
public class PhotoViewActivity extends BaseActivity implements ViewPager.OnPageChangeListener, PhotoViewPager.OnInterceptTouchListener, PhotoViewCallbacks {

    private PhotoViewPager mViewPager;
    private PhotoViewerPagerAdapter mAdapter;

    private ArrayList<String> mPhohoUrls;

    /**
     * The index of the currently viewed photo
     */
    private int mCurrentPhotoIndex;

    /**
     * The listeners wanting full screen state for each screen position
     */
    private final Map<Integer, OnScreenListener>
            mScreenListeners = new HashMap<Integer, OnScreenListener>();

    public static void launch(Context context, int position, ArrayList<String> photoUrls) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(Intents.EXTRA_PHOTO_INDEX, position);
        intent.putExtra(Intents.EXTRA_PHOTO_DATAS, photoUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        final Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_PHOTO_DATAS)) {
            mPhohoUrls = (ArrayList<String>) getIntent().getSerializableExtra(Intents.EXTRA_PHOTO_DATAS);
        }

        mCurrentPhotoIndex = getIntent().getIntExtra(Intents.EXTRA_PHOTO_INDEX, 0);

        mAdapter = new PhotoViewerPagerAdapter(getFragmentManager());
        mAdapter.setData(mPhohoUrls);

        mViewPager = (PhotoViewPager) findViewById(R.id.photo_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOnInterceptTouchListener(this);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.photo_page_margin));
        mViewPager.setCurrentItem(mCurrentPhotoIndex);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPhotoIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public PhotoViewPager.InterceptType onTouchIntercept(float origX, float origY) {
        boolean interceptLeft = false;
        boolean interceptRight = false;

        for (OnScreenListener listener : mScreenListeners.values()) {
            if (!interceptLeft) {
                interceptLeft = listener.onInterceptMoveLeft(origX, origY);
            }
            if (!interceptRight) {
                interceptRight = listener.onInterceptMoveRight(origX, origY);
            }
        }

        if (interceptLeft) {
            if (interceptRight) {
                return PhotoViewPager.InterceptType.BOTH;
            }
            return PhotoViewPager.InterceptType.LEFT;
        } else if (interceptRight) {
            return PhotoViewPager.InterceptType.RIGHT;
        }
        return PhotoViewPager.InterceptType.NONE;
    }

    @Override
    public void addScreenListener(int position, OnScreenListener listener) {
        mScreenListeners.put(position, listener);
    }

    @Override
    public void removeScreenListener(int position) {
        mScreenListeners.remove(position);
    }

    @Override
    public boolean isFragmentActive(Fragment fragment) {
        if (mViewPager == null || mAdapter == null) {
            return false;
        }
        return mViewPager.getCurrentItem() == mAdapter.getItemPosition(fragment);
    }
}
