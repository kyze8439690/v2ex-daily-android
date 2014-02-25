package com.yugy.v2ex.daily;

import android.content.Context;

/**
 * Created by yugy on 14-1-6.
 */
public class Application extends android.app.Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext(){
        return mContext;
    }
}
