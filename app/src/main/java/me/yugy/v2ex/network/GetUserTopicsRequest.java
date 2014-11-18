package me.yugy.v2ex.network;

import com.android.volley.Response;

import me.yugy.v2ex.dao.datahelper.UserTopicsDataHelper;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/18.
 */
public class GetUserTopicsRequest extends BaseTopicsRequest {

    public GetUserTopicsRequest(String username, Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
        super(RequestManager.API_TOPICS + "?username=" + username, listener, errorListener);
    }

    @Override
    protected void deliverResponse(Topic[] response) {
        UserTopicsDataHelper dataHelper = new UserTopicsDataHelper();
        dataHelper.deleteAll();
        dataHelper.bulkInsert(response);
        super.deliverResponse(response);
    }
}