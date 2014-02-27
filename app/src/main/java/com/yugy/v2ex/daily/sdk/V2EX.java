package com.yugy.v2ex.daily.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.Application;
import com.yugy.v2ex.daily.utils.DebugUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by yugy on 14-2-22.
 */
public class V2EX {

    private static final String API_URL = "http://www.v2ex.com/api";
    private static final String API_LATEST = "/topics/latest.json";
    private static final String API_ALL_NODE = "/nodes/all.json";
    private static final String API_REPLIES = "/replies/show.json";
    private static final String API_TOPIC = "/topics/show.json";
    private static final String API_USER = "/members/show.json";

    public static void getLatestTopics(Context context, boolean forceRefresh, final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("getLatestTopics");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("latest_topics_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("latest_topics_cache", null));
                    responseHandler.onSuccess(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_LATEST, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                sharedPreferences.edit().putString("latest_topics_cache", response.toString()).commit();
                responseHandler.onSuccess(response);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                responseHandler.onFailure(statusCode, headers, responseBody, e);
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public static void getAllNode(Context context, boolean forceRefresh, final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("getAllNode");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        if(!forceRefresh){
            if(sharedPreferences.contains("all_node")){
                try {
                    JSONArray response = new JSONArray(sharedPreferences.getString("all_node", "/"));
                    responseHandler.onSuccess(response);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_ALL_NODE, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONArray response) {
                sharedPreferences.edit().putString("all_node", response.toString()).commit();
                responseHandler.onSuccess(response);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                responseHandler.onFailure(statusCode, headers, responseBody, e);
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public static void showTopicByTopicId(Context context, int topicId, JsonHttpResponseHandler responseHandler){
        DebugUtils.log("showTopicByTopicId");
        new AsyncHttpClient().get(context, API_URL + API_TOPIC + "?id=" + topicId, responseHandler);
    }

    public static void getReplies(Context context, int topicId, JsonHttpResponseHandler responseHandler){
        DebugUtils.log("getReplies");
        new AsyncHttpClient().get(context, API_URL + API_REPLIES + "?topic_id=" + topicId, responseHandler);
    }

    public static void showTopicByNodeId(Context context, boolean forceRefresh, final int nodeId,
                                         final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("showTopicByNodeId");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("topics_" + nodeId + "_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("topics_" + nodeId + "_cache", null));
                    responseHandler.onSuccess(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_TOPIC + "?node_id=" + nodeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                sharedPreferences.edit().putString("topics_" + nodeId + "_cache", response.toString()).commit();
                responseHandler.onSuccess(response);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                responseHandler.onFailure(statusCode, headers, responseBody, e);
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public static void showTopicByUsername(Context context, boolean forceRefresh, final String username,
                                         final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("showTopicByUsername");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("topics_" + username + "_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("topics_" + username + "_cache", null));
                    responseHandler.onSuccess(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_TOPIC + "?username=" + username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                sharedPreferences.edit().putString("topics_" + username + "_cache", response.toString()).commit();
                responseHandler.onSuccess(response);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                responseHandler.onFailure(statusCode, headers, responseBody, e);
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public static void showUser(Context context, String username, JsonHttpResponseHandler responseHandler){
        DebugUtils.log("showUser");
        new AsyncHttpClient().get(context, API_URL + API_USER + "?username=" + username, responseHandler);
    }

}
