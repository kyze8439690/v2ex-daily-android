package me.yugy.v2ex.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14/11/14.
 */
public class Node {

    public int id;
    public String name;
    public String title;
    public String title_alternative;
    public String url;
    public int topics;
    public String avatar;

    public static Node fromJson(JSONObject json) throws JSONException {
        Node node = new Node();
        node.id = json.getInt("id");
        node.name = json.getString("name");
        node.title = json.getString("title");
        node.title_alternative = json.getString("title_alternative");
        node.url = json.getString("url");
        node.topics = json.getInt("topics");
        node.avatar = "http:" + json.getString("avatar_large");
        return node;
    }
}
