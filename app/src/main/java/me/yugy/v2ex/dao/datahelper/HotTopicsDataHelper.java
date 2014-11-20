package me.yugy.v2ex.dao.datahelper;

import android.database.Cursor;

import me.yugy.v2ex.dao.dbinfo.HotTopicsDBInfo;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class HotTopicsDataHelper extends BaseTopicsDataHelper<Topic> {
    @Override
    protected String getTableName() {
        return HotTopicsDBInfo.TABLE_NAME;
    }

    @Override
    public Topic select(int tid) {
        Cursor cursor = query(null, HotTopicsDBInfo.TID + "=?", new String[]{String.valueOf(tid)}, null);
        Topic topic = null;
        if (cursor.moveToFirst()) {
            topic = Topic.fromCursor(cursor);
        }
        cursor.close();
        return topic;
    }

}
