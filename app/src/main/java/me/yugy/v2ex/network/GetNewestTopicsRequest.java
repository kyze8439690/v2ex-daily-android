package me.yugy.v2ex.network;

import com.android.volley.Response;

import me.yugy.v2ex.dao.datahelper.NewestTopicsDataHelper;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/20.
 */
public class GetNewestTopicsRequest extends BaseTopicsRequest{

    public GetNewestTopicsRequest(Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
        super(RequestManager.API_LATEST_TOPICS, listener, errorListener);
    }

    @Override
    protected void deliverResponse(Topic[] response) {
        new NewestTopicsDataHelper().bulkInsert(response);
        super.deliverResponse(response);
    }
}
