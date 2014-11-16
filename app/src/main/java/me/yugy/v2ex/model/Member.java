package me.yugy.v2ex.model;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;

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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MemberDBInfo.MID, id);
        values.put(MemberDBInfo.USERNAME, username);
        values.put(MemberDBInfo.TAGLINE, tagline);
        values.put(MemberDBInfo.AVATAR, avatar);
        values.put(MemberDBInfo.URL, url);
        values.put(MemberDBInfo.WEBSITE, website);
        values.put(MemberDBInfo.TWITTER, twitter);
        values.put(MemberDBInfo.PSN, psn);
        values.put(MemberDBInfo.GITHUB, github);
        values.put(MemberDBInfo.BTC, btc);
        values.put(MemberDBInfo.LOCATION, location);
        values.put(MemberDBInfo.BIO, bio);
        values.put(MemberDBInfo.CREATED, created);
        return values;
    }

    public static Member fromCursor(Cursor cursor) {
        Member member = new Member();
        member.id = cursor.getInt(cursor.getColumnIndex(MemberDBInfo.MID));
        member.username = cursor.getString(cursor.getColumnIndex(MemberDBInfo.USERNAME));
        member.tagline = cursor.getString(cursor.getColumnIndex(MemberDBInfo.TAGLINE));
        member.avatar = cursor.getString(cursor.getColumnIndex(MemberDBInfo.AVATAR));
        member.url = cursor.getString(cursor.getColumnIndex(MemberDBInfo.URL));
        member.website = cursor.getString(cursor.getColumnIndex(MemberDBInfo.WEBSITE));
        member.twitter = cursor.getString(cursor.getColumnIndex(MemberDBInfo.TWITTER));
        member.psn = cursor.getString(cursor.getColumnIndex(MemberDBInfo.PSN));
        member.github = cursor.getString(cursor.getColumnIndex(MemberDBInfo.GITHUB));
        member.btc = cursor.getString(cursor.getColumnIndex(MemberDBInfo.BTC));
        member.location = cursor.getString(cursor.getColumnIndex(MemberDBInfo.LOCATION));
        member.bio = cursor.getString(cursor.getColumnIndex(MemberDBInfo.BIO));
        member.created = cursor.getLong(cursor.getColumnIndex(MemberDBInfo.CREATED));
        return member;
    }
}
