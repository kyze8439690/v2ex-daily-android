package me.yugy.v2ex.model;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import me.yugy.v2ex.dao.dbinfo.HotTopicsDBInfo;

/**
 * Created by yugy on 14/11/14.
 */
public class Topic {

    public int id;
    public String title;
    public String url;
    public String content;
    public String content_rendered;
    public int replies;
    public Member member;
    public Node node;
    public long created;
    public long last_modified;
    public long last_touched;

    public static Topic fromJson(JSONObject json) throws JSONException {
        Topic topic = new Topic();
        topic.id = json.getInt("id");
        topic.title = json.getString("title");
        topic.url = json.getString("url");
        topic.content = json.getString("content");
        topic.content_rendered = json.getString("content_rendered");
        topic.replies = json.getInt("replies");
        topic.member = Member.fromJson(json.getJSONObject("member"));
        topic.node = Node.fromJson(json.getJSONObject("node"));
        topic.created = json.getLong("created");
        topic.last_modified = json.getLong("last_modified");
        topic.last_touched = json.getLong("last_touched");
        return topic;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(HotTopicsDBInfo.TID, id);
        values.put(HotTopicsDBInfo.TITLE, title);
        values.put(HotTopicsDBInfo.URL, url);
        values.put(HotTopicsDBInfo.CONTENT, content);
        values.put(HotTopicsDBInfo.CONTENT_RENDERED, content_rendered);
        values.put(HotTopicsDBInfo.REPLIES, replies);
        values.put(HotTopicsDBInfo.MID, member.id);
        values.put(HotTopicsDBInfo.NID, node.id);
        values.put(HotTopicsDBInfo.CREATED, created);
        values.put(HotTopicsDBInfo.LAST_MODIFIED, last_modified);
        values.put(HotTopicsDBInfo.LAST_TOUCHED, last_touched);
        return values;
    }
}
