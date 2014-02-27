package com.yugy.v2ex.daily;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * Created by yugy on 14-1-6.
 */
public class Application extends android.app.Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
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
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options);
        if(BuildConfig.DEBUG){
            configBuilder.writeDebugLogs();
        }
        ImageLoader.getInstance().init(configBuilder.build());

    }

    public static Context getContext(){
        return mContext;
    }
}
