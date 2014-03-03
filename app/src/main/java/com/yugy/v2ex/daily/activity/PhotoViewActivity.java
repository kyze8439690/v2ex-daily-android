package com.yugy.v2ex.daily.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.fragment.PhotoViewFragment;
import com.yugy.v2ex.daily.adapter.PhotoViewerPagerAdapter;

import java.util.ArrayList;

/**
 * Created by wuyexiong on 14-2-28.
 */
public class PhotoViewActivity extends BaseActivity{

    private ViewPager mViewPager;
    private PhotoViewerPagerAdapter mAdapter;
    private ArrayList<String> mPhohoUrls;
    private int mCurrentPosition;

    public static void launch(Context context, int position, ArrayList<String> photoUrls){
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(PhotoViewFragment.ARG_POSITION, position);
        intent.putExtra(PhotoViewFragment.ARG_PHOTO_DOWNLOAD_URL, photoUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mPhohoUrls = (ArrayList<String>) getIntent().getSerializableExtra(PhotoViewFragment.ARG_PHOTO_DOWNLOAD_URL);
        mCurrentPosition = getIntent().getIntExtra(PhotoViewFragment.ARG_POSITION, 0);

        mAdapter = new PhotoViewerPagerAdapter(getFragmentManager());
        mAdapter.setData(mPhohoUrls);

        mViewPager = (ViewPager) findViewById(R.id.photo_view_pager);
        mViewPager.setAdapter(mAdapter);
//        mViewPager.setOnPageChangeListener(this);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.photo_page_margin));
        mViewPager.setCurrentItem(mCurrentPosition);
    }

}
