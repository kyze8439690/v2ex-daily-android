package com.yugy.v2ex.daily.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.dao.dbinfo.BaseTopicsDBInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-2-23.
 */
public class TopicModel implements Parcelable{

    public int id;
    public String title;
    public String url;
    public String content;
    public String contentRendered;
    public int replies;
    public MemberModel member;
    public NodeModel node;
    public long created;
    public long lastModified;
    public long lastTouched;

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        title = jsonObject.getString("title");
        url = jsonObject.getString("url");
        content = jsonObject.getString("content");
        contentRendered = jsonObject.getString("content_rendered")
                .replace("href=\"/member/", "href=\"v2ex://member/")
                .replace("href=\"/i/", "href=\"https://i.v2ex.co/");
        replies = jsonObject.getInt("replies");
        member = new MemberModel();
        member.parse(jsonObject.getJSONObject("member"));
        node = new NodeModel();
        node.parse(jsonObject.getJSONObject("node"));
        created = jsonObject.getLong("created");
        lastModified = jsonObject.getLong("last_modified");
        lastTouched = jsonObject.getLong("last_touched");
    }

    public TopicModel(){}

    private TopicModel(Parcel in){
        int[] ints= new int[2];
        in.readIntArray(ints);
        id = ints[0];
        replies = ints[1];
        String[] strings = new String[4];
        in.readStringArray(strings);
        title = strings[0];
        url = strings[1];
        content = strings[2];
        contentRendered = strings[3];
        long[] longs = new long[3];
        in.readLongArray(longs);
        created = longs[0];
        lastModified = longs[1];
        lastTouched = longs[2];
        member = (MemberModel) in.readValue(MemberModel.class.getClassLoader());
        node = (NodeModel) in.readValue(NodeModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{
                id,
                replies
        });
        dest.writeStringArray(new String[]{
                title,
                url,
                content,
                contentRendered
        });
        dest.writeLongArray(new long[]{
                created,
                lastModified,
                lastTouched
        });
        dest.writeValue(member);
        dest.writeValue(node);
    }

    public static final Creator<TopicModel> CREATOR = new Creator<TopicModel>() {
        @Override
        public TopicModel createFromParcel(Parcel source) {
            return new TopicModel(source);
        }

        @Override
        public TopicModel[] newArray(int size) {
            return new TopicModel[size];
        }
    };

    public static TopicModel fromCursor(Cursor cursor, Context context) {
        TopicModel topicModel = new TopicModel();
        topicModel.id = cursor.getInt(cursor.getColumnIndex(BaseTopicsDBInfo.TOPIC_ID));
        topicModel.title = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.TITLE));
        topicModel.url = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.URL));
        topicModel.content = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.CONTENT));
        topicModel.contentRendered = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.CONTENT_RENDERED));
        topicModel.replies = cursor.getInt(cursor.getColumnIndex(BaseTopicsDBInfo.REPLIES));

        MemberModel memberModel = new MemberModel();
        memberModel.id = cursor.getInt(cursor.getColumnIndex(BaseTopicsDBInfo.MEMBER_ID));
        memberModel.username = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.MEMBER_USERNAME));
        memberModel.tagline = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.MEMBER_TAGLINE));
        memberModel.avatar = cursor.getString(cursor.getColumnIndex(BaseTopicsDBInfo.MEMBER_AVATAR));
        topicModel.member = memberModel;

        AllNodesDataHelper allNodesDataHelper = new AllNodesDataHelper(context);
        int nodeId = cursor.getInt(cursor.getColumnIndex(BaseTopicsDBInfo.NODE_ID));
        topicModel.node = allNodesDataHelper.select(nodeId);

        topicModel.created = cursor.getLong(cursor.getColumnIndex(BaseTopicsDBInfo.CREATED));
        topicModel.lastModified = cursor.getLong(cursor.getColumnIndex(BaseTopicsDBInfo.LAST_MODIFIED));
        topicModel.lastTouched = cursor.getLong(cursor.getColumnIndex(BaseTopicsDBInfo.LAST_TOUCHED));
        return topicModel;
    }
}
