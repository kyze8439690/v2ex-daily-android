package me.yugy.v2ex.network;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import me.yugy.github.myutils.MessageUtils;

/**
 * Created by yugy on 14/11/18.
 */
public class SimpleErrorListener implements Response.ErrorListener {

    private Context mContext;

    public SimpleErrorListener(Context context) {
        mContext = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.getCause() instanceof TimeoutError) {
            MessageUtils.toast(mContext, "网络超时");
        } else {
            MessageUtils.toast(mContext, error.toString());
        }
    }
}
