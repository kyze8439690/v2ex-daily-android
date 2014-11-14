package me.yugy.v2ex.network;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import me.yugy.v2ex.Application;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class RequestManager {

    private static final String API_HOST = "http://www.v2ex.com/api";
    public static final String API_HOT_TOPICS = API_HOST + "/topics/hot.json";

    private static RequestManager sInstance;

    public static RequestManager getInstance(){
        if(sInstance == null) {
            synchronized (RequestManager.class) {
                if (sInstance == null) {
                    sInstance = new RequestManager();
                }
            }
        }
        return sInstance;
    }

    private RequestQueue mRequestQueue;

    private RequestManager(){
        mRequestQueue = Volley.newRequestQueue(Application.getInstance());
    }

    public void getHotTopics(Object tag, Response.Listener<Topic[]> listener, Response.ErrorListener errorListener){
        GetHotTopicsRequest request = new GetHotTopicsRequest(listener, errorListener);
        request.setTag(tag);
        mRequestQueue.add(request);
    }

    public void cancel(Object tag){
        mRequestQueue.cancelAll(tag);
    }

}
