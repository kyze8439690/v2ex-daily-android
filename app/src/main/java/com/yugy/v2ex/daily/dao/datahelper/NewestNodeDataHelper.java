package com.yugy.v2ex.daily.dao.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.yugy.v2ex.daily.dao.DataProvider;
import com.yugy.v2ex.daily.dao.dbinfo.BaseTopicsDBInfo;
import com.yugy.v2ex.daily.dao.dbinfo.NewestNodeDBInfo;
import com.yugy.v2ex.daily.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yugy on 14-3-13.
 */
public class NewestNodeDataHelper extends BaseDataHelper{

    public static final String TABLE_NAME = "newestNode";

    public NewestNodeDataHelper(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.NEWEST_NODE_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return "newestNode";
    }

    protected ContentValues getContentValues(TopicModel topic){
        ContentValues values = new ContentValues();
        values.put(NewestNodeDBInfo.TOPIC_ID, topic.id);
        values.put(NewestNodeDBInfo.TITLE, topic.title);
        values.put(NewestNodeDBInfo.URL, topic.url);
        values.put(NewestNodeDBInfo.CONTENT, topic.content);
        values.put(NewestNodeDBInfo.CONTENT_RENDERED, topic.contentRendered);
        values.put(NewestNodeDBInfo.REPLIES, topic.replies);
        values.put(NewestNodeDBInfo.MEMBER_ID, topic.member.id);
        values.put(NewestNodeDBInfo.MEMBER_USERNAME, topic.member.username);
        values.put(NewestNodeDBInfo.MEMBER_TAGLINE, topic.member.tagline);
        values.put(NewestNodeDBInfo.MEMBER_AVATAR, topic.member.avatar);
        values.put(NewestNodeDBInfo.NODE_ID, topic.node.id);
        values.put(NewestNodeDBInfo.CREATED, topic.created);
        values.put(NewestNodeDBInfo.LAST_MODIFIED, topic.lastModified);
        values.put(NewestNodeDBInfo.LAST_TOUCHED, topic.lastTouched);
        return values;
    }

    public TopicModel[] query(){
        TopicModel[] topics;
        Cursor cursor = query(null, null, null, BaseTopicsDBInfo.CREATED);
        if(cursor.moveToFirst()){
            ArrayList<TopicModel> topicModelArrayList = new ArrayList<TopicModel>();
            topicModelArrayList.add(TopicModel.fromCursor(cursor, getContext()));
            while(cursor.moveToNext()){
                topicModelArrayList.add(TopicModel.fromCursor(cursor, getContext()));
            }
            topics = topicModelArrayList.toArray(new TopicModel[topicModelArrayList.size()]);
        }else{
            topics = new TopicModel[0];
        }
        cursor.close();
        return topics;
    }

    public TopicModel select(int topicId){
        Cursor cursor = query(null, BaseTopicsDBInfo.TOPIC_ID + "=" + topicId, null, null);
        if(cursor.moveToFirst()){
            return TopicModel.fromCursor(cursor, getContext());
        }else{
            return null;
        }
    }

    public int bulkInsert(List<TopicModel> topics){
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for(TopicModel topic : topics){
            ContentValues values = getContentValues(topic);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        return bulkInsert(contentValues.toArray(valueArray));
    }

    public int clear(){
        return delete(null, null);
    }
}
