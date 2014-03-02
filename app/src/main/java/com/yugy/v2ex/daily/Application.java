package com.yugy.v2ex.daily;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * Created by yugy on 14-1-6.
 */
public class Application extends android.app.Application{

    private static Application mContext;
    private static int sMemoryClass;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        initiImageLoader();

        initAppConfig();
    }

    private void initAppConfig() {
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        sMemoryClass = mgr.getMemoryClass();
    }

    private void initiImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.ic_launcher)
                .build();

        File cacheDir;
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            cacheDir = getCacheDir();
        }
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options);
        if(BuildConfig.DEBUG){
            configBuilder.writeDebugLogs();
        }
        ImageLoader.getInstance().init(configBuilder.build());
    }

    public static Application getInstance(){
        return mContext;
    }

    public int getMemorySize(){
        return sMemoryClass;
    }

    public static Context getContext(){
        return mContext;
    }
}
