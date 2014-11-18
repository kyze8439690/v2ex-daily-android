package me.yugy.v2ex.dao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.yugy.v2ex.Application;
import me.yugy.v2ex.dao.dbinfo.HotTopicsDBInfo;
import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;
import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;
import me.yugy.v2ex.dao.dbinfo.UserTopicsDBInfo;

/**
 * Created by yugy on 14/11/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "v2ex.db";
    private static final int DB_VERSION = 1;


    public DBHelper() {
        super(Application.getInstance(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HotTopicsDBInfo.TABLE.create(db);
        MemberDBInfo.TABLE.create(db);
        NodeDBInfo.TABLE.create(db);
        UserTopicsDBInfo.TABLE.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HotTopicsDBInfo.TABLE.delete(db);
        MemberDBInfo.TABLE.delete(db);
        NodeDBInfo.TABLE.delete(db);
        UserTopicsDBInfo.TABLE.delete(db);

        onCreate(db);
    }
}
