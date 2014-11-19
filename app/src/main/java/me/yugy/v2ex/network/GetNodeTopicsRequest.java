package me.yugy.v2ex.network;

import com.android.volley.Response;

import me.yugy.v2ex.dao.datahelper.NodeTopicsDataHelper;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/19.
 */
public class GetNodeTopicsRequest extends BaseTopicsRequest {

    public GetNodeTopicsRequest(int nodeId, Response.Listener<Topic[]> listener, Response.ErrorListener errorListener) {
        super(RequestManager.API_TOPICS + "?node_id=" + nodeId, listener, errorListener);
    }

    @Override
    protected void deliverResponse(Topic[] response) {
        NodeTopicsDataHelper dataHelper = new NodeTopicsDataHelper();
        dataHelper.bulkInsert(response);
        super.deliverResponse(response);
    }
}
