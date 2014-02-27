package com.yugy.v2ex.daily.network;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Environment;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.yugy.v2ex.daily.Application;
import com.yugy.v2ex.daily.R;

import java.io.File;

/**
 * Created by yugy on 14-1-6.
 */
public class RequestManager {

    private final int MEM_CACHE_SIZE;

    private static RequestManager sInstance;
    private RequestQueue sRequestQueue;
    private ImageLoader sImageLoader;
    private DiskBasedCache sDiskBasedCache;

    private RequestManager(){
        File dir;
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            dir = Application.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            dir = Application.getContext().getCacheDir();
        }
        sDiskBasedCache = new DiskBasedCache(dir);
        sRequestQueue = new RequestQueue(sDiskBasedCache, new BasicNetwork(new HurlStack()));
        sRequestQueue.start();
        MEM_CACHE_SIZE = 1024 * 1024 *
                ((ActivityManager) Application.getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 3;
        sImageLoader = new ImageLoader(sRequestQueue, new BitmapLruCache(MEM_CACHE_SIZE));
    }

    public static ImageLoader getImageLoader() {
        return getInstance().sImageLoader;
    }

    public static RequestManager getInstance(){
        if(sInstance == null){
            sInstance = new RequestManager();
        }
        return sInstance;
    }

    public void displayImage(String url, final ImageView imageView){
        imageView.setImageResource(R.drawable.ic_launcher);
        sImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                imageView.setImageBitmap(imageContainer.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
    }

    public void addRequest(Context context, Request request){
        request.setTag(context);
        sRequestQueue.add(request);
    }

    public void cancelRequests(Context tag){
        sRequestQueue.cancelAll(tag);
    }
}
