package me.yugy.v2ex.network;

import com.android.volley.Response;

import me.yugy.v2ex.dao.datahelper.HotTopicsDataHelper;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class GetHotTopicsRequest extends BaseTopicsRequest {

    public GetHotTopicsRequest(Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
        super(RequestManager.API_HOT_TOPICS, listener, errorListener);
    }

    @Override
    protected void deliverResponse(Topic[] response) {
        HotTopicsDataHelper dataHelper = new HotTopicsDataHelper();
        dataHelper.deleteAll();
        dataHelper.bulkInsert(response);
        super.deliverResponse(response);
    }
}
