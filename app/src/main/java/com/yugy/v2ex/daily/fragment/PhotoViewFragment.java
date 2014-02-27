/*
 * Copyright (C) 2011 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yugy.v2ex.daily.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.widget.photo.ImageUtils;
import com.yugy.v2ex.daily.widget.photo.PhotoView;


/**
 * Displays a photo.
 */
public class PhotoViewFragment extends Fragment implements
        OnClickListener {

    /**
     * Interface for components that are internally scrollable left-to-right.
     */
    public static interface HorizontallyScrollable {
        /**
         * Return {@code true} if the component needs to receive right-to-left
         * touch movements.
         *
         * @param origX the raw x coordinate of the initial touch
         * @param origY the raw y coordinate of the initial touch
         */

        public boolean interceptMoveLeft(float origX, float origY);

        /**
         * Return {@code true} if the component needs to receive left-to-right
         * touch movements.
         *
         * @param origX the raw x coordinate of the initial touch
         * @param origY the raw y coordinate of the initial touch
         */
        public boolean interceptMoveRight(float origX, float origY);
    }

    protected final static String STATE_INTENT_KEY =
            "com.android.mail.photo.fragments.PhotoViewFragment.INTENT";

    public final static String ARG_INTENT = "arg-intent";
    public final static String ARG_POSITION = "arg-position";
    public final static String ARG_SHOW_SPINNER = "arg-show-spinner";
    public static final String ARG_PHOTO_DOWNLOAD_URL = "arg-photo-download-url";

    /**
     * The size of the photo
     */
    public static Integer sPhotoSize;

    /**
     * The intent we were launched with
     */
    protected Intent mIntent;
//    protected PhotoViewCallbacks mCallback;

    protected PhotoView mPhotoView;
    protected String mDownloadUrl;

    protected int mPosition;

    /**
     * Public no-arg constructor for allowing the framework to handle orientation changes
     */
    public PhotoViewFragment() {
        // Do nothing.
    }


    public static Fragment newInstance(String imageUrl) {
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_PHOTO_DOWNLOAD_URL, imageUrl);
        final PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageLoader.getInstance().loadImage(mDownloadUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                bindPhoto(bitmap);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sPhotoSize == null) {
            final DisplayMetrics metrics = new DisplayMetrics();
            final WindowManager wm =
                    (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            final ImageUtils.ImageSize imageSize = ImageUtils.sUseImageSize;
            wm.getDefaultDisplay().getMetrics(metrics);
            switch (imageSize) {
                case EXTRA_SMALL:
                    // Use a photo that's 80% of the "small" size
                    sPhotoSize = (Math.min(metrics.heightPixels, metrics.widthPixels) * 800) / 1000;
                    break;
                case SMALL:
                    // Fall through.
                case NORMAL:
                    // Fall through.
                default:
                    sPhotoSize = Math.min(metrics.heightPixels, metrics.widthPixels);
                    break;
            }
        }

        final Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        mDownloadUrl = bundle.getString(ARG_PHOTO_DOWNLOAD_URL);

        if (savedInstanceState != null) {
            final Bundle state = savedInstanceState.getBundle(STATE_INTENT_KEY);
            if (state != null) {
                mIntent = new Intent().putExtras(state);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        initializeView(view);
        return view;
    }

    protected void initializeView(View view) {
        mPhotoView = (PhotoView) view.findViewById(R.id.photo_view);
        mPhotoView.setMaxInitialScale(1);
        mPhotoView.setOnClickListener(this);
        mPhotoView.setFullScreen(false, false);
        mPhotoView.enableImageTransforms(false);

    }

    @Override
    public void onResume() {
        super.onResume();
//        mCallback.addScreenListener(mPosition, this);
    }

    @Override
    public void onPause() {
//        mCallback.removeCursorListener(this);
//        mCallback.removeScreenListener(mPosition);
        resetPhotoView();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // Clean up views and other components
        if (mPhotoView != null) {
            mPhotoView.clear();
            mPhotoView = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mIntent != null) {
            outState.putParcelable(STATE_INTENT_KEY, mIntent.getExtras());
        }
    }


    /**
     * Binds an image to the photo view.
     */
    private void bindPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            if (mPhotoView != null) {
                mPhotoView.bindPhoto(bitmap);
            }
            enableImageTransforms(true);
        }
    }

    /**
     * Enable or disable image transformations. When transformations are enabled, this view
     * consumes all touch events.
     */
    public void enableImageTransforms(boolean enable) {
        mPhotoView.enableImageTransforms(enable);
    }

    /**
     * Resets the photo view to it's default state w/ no bound photo.
     */
    private void resetPhotoView() {
        if (mPhotoView != null) {
            mPhotoView.bindPhoto(null);
        }
    }


    @Override
    public void onClick(View v) {
    }

    /**
     * Reset the views to their default states
     */
    public void resetViews() {
        if (mPhotoView != null) {
            mPhotoView.resetTransformations();
        }
    }

    /**
     * Returns {@code true} if a photo has been bound. Otherwise, returns {@code false}.
     */
    public boolean isPhotoBound() {
        return (mPhotoView != null && mPhotoView.isPhotoBound());
    }


}
