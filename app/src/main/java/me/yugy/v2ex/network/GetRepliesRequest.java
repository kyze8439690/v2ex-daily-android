package me.yugy.v2ex.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import me.yugy.v2ex.dao.datahelper.RepliesDataHelper;
import me.yugy.v2ex.model.Reply;

/**
 * Created by yugy on 14/11/25.
 */
public class GetRepliesRequest extends Request<Reply[]> {

    private Response.Listener<Reply[]> mListener;
    private int mTopicId;

    public GetRepliesRequest(int topicId, Response.Listener<Reply[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, RequestManager.API_REPLIES + "?topic_id=" + topicId, errorListener);
        mListener = listener;
        mTopicId = topicId;
    }

    @Override
    protected Response<Reply[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONArray json = new JSONArray(jsonString);
            int length = json.length();
            Reply[] replies = new Reply[length];
            for (int i = 0; i < length; i++) {
                replies[i] = Reply.fromJson(json.getJSONObject(i), mTopicId);
            }
            return Response.success(replies, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Reply[] response) {
        new RepliesDataHelper().bulkInsert(response);
        mListener.onResponse(response);
    }
}
