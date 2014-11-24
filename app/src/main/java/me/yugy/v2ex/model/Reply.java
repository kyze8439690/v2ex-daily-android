package me.yugy.v2ex.model;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.dao.dbinfo.ReplyDBInfo;

/**
 * Created by yugy on 14/11/20.
 */
public class Reply {

    public int id;
    public int thanks;
    public String content;
    public String content_rendered;
    public Member member;
    public long created;
    public long last_modified;
    public int topicId;

    public static Reply fromJson(JSONObject json, int topicId) throws JSONException {
        Reply reply = new Reply();
        reply.id = json.getInt("id");
        reply.thanks = json.getInt("thanks");
        reply.content = json.getString("content");
        reply.content_rendered = json.getString("content_rendered");
        reply.member = Member.fromJson(json.getJSONObject("member"));
        reply.created = json.getLong("created");
        reply.last_modified = json.getLong("last_modified");
        reply.topicId = topicId;
        return reply;
    }

    public static Reply fromCursor(Cursor cursor) {
        Reply reply = new Reply();
        reply.id = cursor.getInt(cursor.getColumnIndex(ReplyDBInfo.RID));
        reply.thanks = cursor.getInt(cursor.getColumnIndex(ReplyDBInfo.THANKS));
        reply.content = cursor.getString(cursor.getColumnIndex(ReplyDBInfo.CONTENT));
        reply.content_rendered = cursor.getString(cursor.getColumnIndex(ReplyDBInfo.CONTENT_RENDERED));
        int mid = cursor.getInt(cursor.getColumnIndex(ReplyDBInfo.MID));
        reply.member = new MembersDataHelper().select(mid);
        reply.created = cursor.getLong(cursor.getColumnIndex(ReplyDBInfo.CREATED));
        reply.last_modified = cursor.getLong(cursor.getColumnIndex(ReplyDBInfo.LAST_MODIFIED));
        reply.topicId = cursor.getInt(cursor.getColumnIndex(ReplyDBInfo.TID));
        return reply;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(ReplyDBInfo.RID, id);
        values.put(ReplyDBInfo.THANKS, thanks);
        values.put(ReplyDBInfo.CONTENT, content);
        values.put(ReplyDBInfo.CONTENT_RENDERED, content_rendered);
        values.put(ReplyDBInfo.MID, member.id);
        values.put(ReplyDBInfo.CREATED, created);
        values.put(ReplyDBInfo.LAST_MODIFIED, last_modified);
        values.put(ReplyDBInfo.TID, topicId);
        return values;
    }

}
