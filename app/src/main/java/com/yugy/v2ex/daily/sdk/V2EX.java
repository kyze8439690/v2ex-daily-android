package com.yugy.v2ex.daily.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yugy.v2ex.daily.utils.DebugUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yugy on 14-2-22.
 */
public class V2EX {

    private static final String API_URL = "https://www.v2ex.com/api";
    private static final String API_LATEST = "/topics/latest.json";
    private static final String API_ALL_NODE = "/nodes/all.json";
    private static final String API_REPLIES = "/replies/show.json";
    private static final String API_TOPIC = "/topics/show.json";
    private static final String API_USER = "/members/show.json";

    public static void getLatestTopics(Context context, int page, final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("getLatestTopics");
        RequestParams params = new RequestParams();
        params.put("p", String.valueOf(page));
        new AsyncHttpClient().get(context, API_URL + API_LATEST, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseHandler.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                responseHandler.onFailure(statusCode, headers, responseBody, e);
                super.onFailure(statusCode, headers, responseBody, e);
            }
        });
    }

    public static void getAllNode(Context context, final JsonHttpResponseHandler responseHandler){
        DebugUtils.log("getAllNode");
        new AsyncHttpClient().get(context, API_URL + API_ALL_NODE, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseHandler.onSuccess(statusCode, headers, response);
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
                    responseHandler.onSuccess(200, new Header[0], jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_TOPIC + "?node_id=" + nodeId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                sharedPreferences.edit().putString("topics_" + nodeId + "_cache", response.toString()).commit();
                responseHandler.onSuccess(statusCode, headers, response);
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
                    responseHandler.onSuccess(200, new Header[0], jsonArray);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        new AsyncHttpClient().get(context, API_URL + API_TOPIC + "?username=" + username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                sharedPreferences.edit().putString("topics_" + username + "_cache", response.toString()).commit();
                responseHandler.onSuccess(statusCode, headers, response);
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

    private static AsyncHttpClient sClient = null;

    private static AsyncHttpClient getClient(Context context){
        if(sClient == null){
            sClient = new AsyncHttpClient();
            sClient.setEnableRedirects(false);
            sClient.setCookieStore(new PersistentCookieStore(context));
            sClient.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.2.1; en-us; M040 Build/JOP40D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            sClient.addHeader("Cache-Control", "max-age=0");
            sClient.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            sClient.addHeader("Accept-Charset", "utf-8, iso-8859-1, utf-16, *;q=0.7");
            sClient.addHeader("Accept-Language", "zh-CN, en-US");
            sClient.addHeader("X-Requested-With", "com.android.browser");
        }
        return sClient;
    }

    /**
     * return example:
     * {
     *      result:ok/fail,
     *      content:{
     *          once: 12345,
     *          cookie_referer: asdce
     *      }
     * }
     */
    public static void getOnceCode(final Context context, int topicId, final JsonHttpResponseHandler responseHandler){
        AsyncHttpClient client = getClient(context);
        client.addHeader("Referer", "https://www.v2ex.com");
        client.get("https://www.v2ex.com/t/" + topicId, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                JSONObject result = new JSONObject();
                Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
                final Matcher matcher = pattern.matcher(content);
                try {
                    if(matcher.find()){
                        result.put("result", "ok");

                        JSONObject jsonContent = new JSONObject();
                        jsonContent.put("once", Integer.parseInt(matcher.group(1)));

                        for(Header header : headers){
                            if(header.getName().equals("Set-Cookie")){
                                DebugUtils.log(header.getName() + ": " + header.getValue());
                                Pattern cookiePattern = Pattern.compile("V2EX_REFERRER=\"[^\"]+\"");
                                Matcher cookieMatcher = cookiePattern.matcher(header.getValue());
                                if(cookieMatcher.find()){
                                    jsonContent.put("cookie_referer", cookieMatcher.group());
                                }
                            }
                        }

                        result.put("content", jsonContent);
                    }else{
                        result.put("result", "fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseHandler.onSuccess(statusCode, headers, result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                responseHandler.onFailure(statusCode, headers, responseBody, error);
            }
        });
    }

    public static void getOnceCode(Context context, String url, final JsonHttpResponseHandler responseHandler){
        AsyncHttpClient client = getClient(context);
        client.get(url, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                responseHandler.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
//                DebugUtils.log(responseBody);
                JSONObject result = new JSONObject();
                Pattern pattern = Pattern.compile("<input type=\"hidden\" value=\"([0-9]+)\" name=\"once\" />");
                final Matcher matcher = pattern.matcher(responseBody);
                try {
                    if(matcher.find()){
                        result.put("result", "ok");

                        JSONObject jsonContent = new JSONObject();
                        jsonContent.put("once", Integer.parseInt(matcher.group(1)));

                        result.put("content", jsonContent);
                    }else{
                        result.put("result", "fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DebugUtils.log(result);
                responseHandler.onSuccess(statusCode, headers, result);
            }
        });
    }


    /**
     * return example:
     * {
     *      result:ok/fail,
     *      content:{
     *          error_msg: 123
     *      }
     * }
     */
    public static void login(final Context context, String username, String password, int onceCode, final JsonHttpResponseHandler responseHandler){
        AsyncHttpClient client = getClient(context);
        client.addHeader("Origin", "https://www.v2ex.com");
        client.addHeader("Referer", "https://www.v2ex.com/signin");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.put("next", "/");
        params.put("u", username);
        params.put("once", String.valueOf(onceCode));
        params.put("p", password);
        client.post("https://www.v2ex.com/signin", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                DebugUtils.log(statusCode);
                JSONObject result = new JSONObject();
                try {
                    if(statusCode == 302){
                        result.put("result", "ok");
                    }else{
                        result.put("result", "fail");
                    }
                    DebugUtils.log(result);
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
//                DebugUtils.log(responseBody);
                JSONObject result = new JSONObject();
                try {
                    result.put("result", "fail");
                    Pattern errorPattern = Pattern.compile("<div class=\"problem\">(.*)</div>");
                    Matcher errorMatcher = errorPattern.matcher(responseBody);
                    final String errorContent;
                    if(errorMatcher.find()){
                        errorContent = errorMatcher.group(1).replaceAll("<[^>]+>", "");
                    }else{
                        errorContent = "Unknown error";
                    }
                    result.put("content", new JSONObject() {{
                        put("error_msg", errorContent);
                    }});
                    DebugUtils.log(result);
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * result example:
     * {
     *     result:ok/fail,
     *     content:{
     *         username:kyze8439690,
     *         collections:[
     *              all4all,
     *              android,
     *              ...
     *         ]
     *     }
     * }
     */
    public static void getUserInfo(final Context context, final JsonHttpResponseHandler responseHandler){
        getClient(context).get("https://www.v2ex.com/my/nodes", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                responseHandler.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                JSONObject result = new JSONObject();
                try {
                    Pattern userPattern = Pattern.compile("<a href=\"/member/([^\"]+)\" class=\"top\">");
                    Matcher userMatcher = userPattern.matcher(responseBody);
                    if (userMatcher.find()) {
                        JSONObject content = new JSONObject();
                        content.put("username", userMatcher.group(1));
                        Pattern collectionPattern = Pattern.compile("<a class=\"grid_item\" href=\"/go/([^\"]+)\"");
                        Matcher collectionMatcher = collectionPattern.matcher(responseBody);
                        JSONArray collections = new JSONArray();
                        if (collectionMatcher.find()) {
                            collections.put(collectionMatcher.group(1));
                            while (collectionMatcher.find()) {
                                collections.put(collectionMatcher.group(1));
                            }
                        }
                        content.put("collections", collections);
                        result.put("content", content);
                        result.put("result", "ok");
                        responseHandler.onSuccess(statusCode, headers, result);
                    } else {
                        result.put("result", "fail");
                        responseHandler.onSuccess(statusCode, headers, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void postComment(final Context context, int onceCode, int topicId, final String commentContent, final JsonHttpResponseHandler responseHandler){
        AsyncHttpClient client = getClient(context);
        client.addHeader("Origin", "https://www.v2ex.com");
        client.addHeader("Referer", "https://www.v2ex.com/t/" + topicId);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        RequestParams params = new RequestParams();
        params.put("content", commentContent);
        params.put("once", String.valueOf(onceCode));
        client.post("https://www.v2ex.com/t/" + topicId, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject result = new JSONObject();
                    result.put("result", "fail");
                    Pattern errorPattern = Pattern.compile("<div class=\"problem\">(.*)</div>");
                    Matcher errorMatcher = errorPattern.matcher(new String(responseBody));
                    final String errorContent;
                    if(errorMatcher.find()){
                        errorContent = errorMatcher.group(1).replaceAll("<[^>]+>", "");
                    }else{
                        errorContent = "Unknown error";
                    }
                    result.put("content", new JSONObject(){{
                        put("error_msg", errorContent);
                    }});
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                DebugUtils.log(statusCode);
                JSONObject result = new JSONObject();
                try {
                    if(statusCode == 302){
                        result.put("result", "ok");
                        result.put("content", commentContent);
                    }else{
                        result.put("result", "fail");
                    }
                    DebugUtils.log(result);
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * result example:
     * {
     *     result:ok/fail,
     *     reg_time:13123213213
     * }
     */
    public static void getRegTime(final Context context, final JsonHttpResponseHandler responseHandler){
        getClient(context).get("https://www.v2ex.com/go/nds", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                responseHandler.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
                    JSONObject result = new JSONObject();
                    Pattern regTimePattern = Pattern.compile("favorite/node/32\\?t=([^\"]+)\">");
                    Matcher regTimeMatcher = regTimePattern.matcher(responseBody);
                    if (regTimeMatcher.find()) {
                        result.put("result", "ok");
                        result.put("reg_time", regTimeMatcher.group(1));
                    } else {
                        result.put("result", "fail");
                    }
                    DebugUtils.log(result);
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void syncCollection(final Context context, int nodeId, String regTime, boolean add, final JsonHttpResponseHandler responseHandler){
        String url;
        if(add){
            url = "https://www.v2ex.com/favorite/node/" + nodeId + "?t=" + regTime;
        }else{
            url = "https://www.v2ex.com/unfavorite/node/" + nodeId + "?t=" + regTime;
        }
        getClient(context).get(url, new TextHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                JSONObject result = new JSONObject();
                try {
                    if(statusCode == 302){
                        result.put("result", "ok");
                    }else{
                        result.put("result", "fail");
                    }
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                try {
                    JSONObject result = new JSONObject();
                    result.put("result", "fail");
                    responseHandler.onSuccess(statusCode, headers, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * result example:
     * {
     *     result:ok/fail,
     *     token:123
     * }
     */
    public static void getNotificationToken(final Context context, final JsonHttpResponseHandler responseHandler){
        AsyncHttpClient client = getClient(context);
        client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        client.get("https://www.v2ex.com/notifications", new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                responseHandler.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Pattern tokenPattern = Pattern.compile("<input type=\"text\" value=\"https://www\\.v2ex\\.com/n/([^\\.]+)\\.xml\" class=\"sll\" onclick=\"this\\.select\\(\\);\" readonly=\"readonly\" />");
                Matcher tokenMatcher = tokenPattern.matcher(responseBody);
                try {
                    JSONObject result = new JSONObject();
                    if(tokenMatcher.find()){
                        result.put("result", "ok");
                        result.put("token", tokenMatcher.group(1));
                    }else{
                        result.put("result", "fail");
                    }
                    responseHandler.onSuccess(statusCode, headers, result);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getNotification(final Context context, String token, TextHttpResponseHandler responseHandler){
        getClient(context).get("https://www.v2ex.com/n/" + token + ".xml", responseHandler);
    }

    public static void logout(Context context){
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        cookieStore.clear();
    }
}
