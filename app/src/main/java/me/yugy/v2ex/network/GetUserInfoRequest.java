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

import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.model.Member;

/**
 * Created by yugy on 14/11/17.
 */
public class GetUserInfoRequest extends Request<Member>{

    private Response.Listener<Member> mListener;

    public GetUserInfoRequest(String username, Response.Listener<Member> listener, Response.ErrorListener errorListener) {
        super(Method.GET, RequestManager.API_USER_INFO + "?username=" + username, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<Member> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            Member member = Member.fromJson(json);
            return Response.success(member, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Member response) {
        MembersDataHelper dataHelper = new MembersDataHelper();
        dataHelper.insert(response);
        mListener.onResponse(response);
    }
}
