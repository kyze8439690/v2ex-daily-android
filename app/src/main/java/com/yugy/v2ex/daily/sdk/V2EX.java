package com.yugy.v2ex.daily.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.yugy.v2ex.daily.Application;
import com.yugy.v2ex.daily.network.RequestManager;
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

    public static void getLatestTopics(Context context, boolean forceRefresh,
                                       final Response.Listener<JSONArray> listener,
                                       Response.ErrorListener errorListener){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("latest_topics_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("latest_topics_cache", null));
                    listener.onResponse(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_LATEST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                sharedPreferences.edit().putString("latest_topics_cache", jsonArray.toString()).commit();
                listener.onResponse(jsonArray);
            }
        }, errorListener));
    }

    public static void getAllNode(Context context,
                                  boolean forceRefresh,
                                  final Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
        if(!forceRefresh){
            if(sharedPreferences.contains("all_node")){
                try {
                    JSONArray response = new JSONArray(sharedPreferences.getString("all_node", "/"));
                    listener.onResponse(response);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_ALL_NODE, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                sharedPreferences.edit().putString("all_node", jsonArray.toString()).commit();
                listener.onResponse(jsonArray);
            }
        }, errorListener));
    }

    public static void showTopicByTopicId(Context context, int topicId,
                                          Response.Listener<JSONArray> listener,
                                          Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_TOPIC + "?id=" + topicId, listener, errorListener));
    }

    public static void getReplies(Context context, int topicId,
                                  Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener){
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_REPLIES + "?topic_id=" + topicId, listener, errorListener));
    }

    public static void showTopicByNodeId(Context context, boolean forceRefresh, final int nodeId,
                                         final Response.Listener<JSONArray> listener,
                                         Response.ErrorListener errorListener){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("topics_" + nodeId + "_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("topics_" + nodeId + "_cache", null));
                    listener.onResponse(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_TOPIC + "?node_id=" + nodeId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                sharedPreferences.edit().putString("topics_" + nodeId + "_cache", jsonArray.toString()).commit();
                listener.onResponse(jsonArray);
            }
        }, errorListener));
    }

    public static void showTopicByUsername(Context context, boolean forceRefresh, final String username,
                                         final Response.Listener<JSONArray> listener,
                                         Response.ErrorListener errorListener){
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!forceRefresh){
            if(sharedPreferences.contains("topics_" + username + "_cache")){
                try {
                    JSONArray jsonArray = new JSONArray(sharedPreferences.getString("topics_" + username + "_cache", null));
                    listener.onResponse(jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        RequestManager.getInstance().addRequest(context, new JsonArrayRequest(
                API_URL + API_TOPIC + "?username=" + username, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                sharedPreferences.edit().putString("topics_" + username + "_cache", jsonArray.toString()).commit();
                listener.onResponse(jsonArray);
            }
        }, errorListener));
    }

}
