package me.yugy.v2ex.dao.datahelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import me.yugy.v2ex.dao.DBHelper;
import me.yugy.v2ex.dao.DataProvider;
import me.yugy.v2ex.dao.dbinfo.NewestTopicsDBInfo;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class NewestTopicsDataHelper extends BaseTopicsDataHelper<Topic> {
    @Override
    protected String getTableName() {
        return NewestTopicsDBInfo.TABLE_NAME;
    }

    @Override
    public Topic select(int tid) {
        Cursor cursor = query(null, NewestTopicsDBInfo.TID + "=?", new String[]{String.valueOf(tid)}, null);
        Topic topic = null;
        if (cursor.moveToFirst()) {
            topic = Topic.fromCursor(cursor);
        }
        cursor.close();
        return topic;
    }

    public int getCount() {
        synchronized (DataProvider.class) {
            SQLiteDatabase db = new DBHelper().getReadableDatabase();
            Cursor cursor = db.query(getTableName(), new String[]{"count(*)"},
                    null, null, null, null, null);
            int count;
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            } else {
                count = 0;
            }
            cursor.close();
            return count;
        }
    }

}
