package me.yugy.v2ex.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class GetHotTopicsRequest extends Request<Topic[]> {

    private Response.Listener<Topic[]> mListener;

    public GetHotTopicsRequest(Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, RequestManager.API_HOT_TOPICS, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<Topic[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONArray json = new JSONArray(jsonString);
            int length = json.length();
            Topic[] topics = new Topic[length];
            for (int i = 0; i < length; i++){
                topics[i] = Topic.fromJson(json.getJSONObject(i));
            }
            return Response.success(topics, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Topic[] response) {
        mListener.onResponse(response);
    }
}
