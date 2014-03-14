package com.yugy.v2ex.daily.tasker;

import android.content.Context;
import android.os.AsyncTask;

import com.yugy.v2ex.daily.model.NodeModel;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by yugy on 14-3-14.
 */
public abstract class AllNodesParseTask extends AsyncTask<JSONArray, Void, ArrayList<NodeModel>>{

    private Context mContext;

    public AllNodesParseTask(Context context){
        mContext = context;
    }

    @Override
    protected ArrayList<NodeModel> doInBackground(JSONArray... params) {
        try {
            return getModels(params[0]);
        } catch (JSONException e) {
            AppMsg.makeText(mContext, "Json decode error", AppMsg.STYLE_ALERT).show();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected abstract void onPostExecute(ArrayList<NodeModel> nodeModels);

    private ArrayList<NodeModel> getModels(JSONArray jsonArray) throws JSONException {
        ArrayList<NodeModel> models = new ArrayList<NodeModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            NodeModel model = new NodeModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
        }
        return models;
    }

}
