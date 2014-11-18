package me.yugy.v2ex;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import me.yugy.github.myutils.DebugUtils;

/**
 * Created by yugy on 14/11/13.
 */
public class Application extends android.app.Application{

    private static android.app.Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        DebugUtils.setLogEnable(BuildConfig.DEBUG);

        //init imageLoader
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static android.app.Application getInstance() {
        return sInstance;
    }
}
