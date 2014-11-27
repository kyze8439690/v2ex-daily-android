package me.yugy.v2ex.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/27.
 */
public class GetTopicRequest extends Request<Topic>{

    private Response.Listener<Topic> mListener;

    public GetTopicRequest(int topicId, Response.Listener<Topic> listener, Response.ErrorListener errorListener) {
        super(Method.GET, RequestManager.API_TOPICS + "?id=" + topicId, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<Topic> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONArray(jsonString).getJSONObject(0);
            Topic topic = Topic.fromJson(json);;
            return Response.success(topic, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Topic response) {
        mListener.onResponse(response);
    }
}
