package me.yugy.v2ex.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;

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
        node.title_alternative = json.optString("title_alternative");
        node.url = json.getString("url");
        node.topics = json.getInt("topics");
        String avatar = json.optString("avatar_large");
        if (TextUtils.isEmpty(avatar)) {
            node.avatar = "";
        } else {
            node.avatar = "http:" + avatar;
        }
        return node;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(NodeDBInfo.NID, id);
        values.put(NodeDBInfo.NAME, name);
        values.put(NodeDBInfo.TITLE, title);
        values.put(NodeDBInfo.TITLE_ALTERNATIVE, title_alternative);
        values.put(NodeDBInfo.URL, url);
        values.put(NodeDBInfo.TOPICS, topics);
        values.put(NodeDBInfo.AVATAR, avatar);
        return values;
    }

    public static Node fromCursor(Cursor cursor) {
        Node node = new Node();
        node.id = cursor.getInt(cursor.getColumnIndex(NodeDBInfo.NID));
        node.name = cursor.getString(cursor.getColumnIndex(NodeDBInfo.NAME));
        node.title = cursor.getString(cursor.getColumnIndex(NodeDBInfo.TITLE));
        node.title_alternative = cursor.getString(cursor.getColumnIndex(NodeDBInfo.TITLE_ALTERNATIVE));
        node.url = cursor.getString(cursor.getColumnIndex(NodeDBInfo.URL));
        node.topics = cursor.getInt(cursor.getColumnIndex(NodeDBInfo.TOPICS));
        node.avatar = cursor.getString(cursor.getColumnIndex(NodeDBInfo.AVATAR));
        return node;
    }
}
