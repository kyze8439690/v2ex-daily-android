package com.yugy.v2ex.daily.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-2-24.
 */
public class ReplyModel {

    public int id;
    public int thanks;
    public String content;
    public String contentRendered;
    public MemberModel member;
    public long created;
    public long lastModified;

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        thanks = jsonObject.getInt("thanks");
        content = jsonObject.getString("content");
        contentRendered = jsonObject.getString("content_rendered")
                .replace("href=\"/member/", "href=\"v2ex://member/")
                .replace("href=\"/i/", "href=\"https://i.v2ex.co/");
        member = new MemberModel();
        member.parse(jsonObject.getJSONObject("member"));
        created = jsonObject.getLong("created");
        lastModified = jsonObject.getLong("last_modified");
    }

}
