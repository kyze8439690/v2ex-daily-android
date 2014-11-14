package me.yugy.v2ex;

/**
 * Created by yugy on 14/11/13.
 */
public class Application extends android.app.Application{

    private static android.app.Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static android.app.Application getInstance() {
        return sInstance;
    }
}
