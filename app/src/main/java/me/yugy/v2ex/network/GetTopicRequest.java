package me.yugy.v2ex.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/27.
 */
public class GetTopicRequest extends Request<Topic>{
    public GetTopicRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<Topic> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Topic response) {

    }
}
