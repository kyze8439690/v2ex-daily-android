package me.yugy.v2ex.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14/11/14.
 */
public class Member {

    //necessary
    public int id;
    public String username;
    public String tagline;
    public String avatar;

    //unnecessary
    public String url;
    public String website;
    public String twitter;
    public String psn;
    public String github;
    public String btc;
    public String location;
    public String bio;
    public long created;

    public static Member fromJson(JSONObject json) throws JSONException {
        Member member = new Member();
        member.id = json.getInt("id");
        member.username = json.getString("username");
        member.tagline = json.getString("tagline");
        member.avatar = "http:" + json.getString("avatar_large");

        member.url = json.optString("url");
        member.website = json.optString("website");
        member.twitter = json.optString("twitter");
        member.psn = json.optString("psn");
        member.github = json.optString("github");
        member.btc = json.optString("btc");
        member.location = json.optString("location");
        member.bio = json.optString("bio");
        member.created = json.optLong("created");
        return member;
    }
}
