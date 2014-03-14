package com.yugy.v2ex.daily.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.yugy.v2ex.daily.Application;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.dao.datahelper.NewestNodeDataHelper;
import com.yugy.v2ex.daily.dao.dbinfo.AllNodesDBInfo;
import com.yugy.v2ex.daily.dao.dbinfo.NewestNodeDBInfo;

/**
 * Created by yugy on 14-3-6.
 */
public class DataProvider extends ContentProvider{

    public static Object obj = new Object();
    public static final String AUTHORITY = "com.yugy.v2ex.daily";
    public static final String SCHEME = "content://";

    public static final String PATH_ALL_NODES = "/allNodes";
    public static final String PATH_NEWEST_NODE = "/newestNode";

    public static final Uri ALL_NODES_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_ALL_NODES);
    public static final Uri NEWEST_NODE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_NEWEST_NODE);

    private static final int ALL_NODES = 0;
    private static final int NEWEST_NODE = 1;

    public static final String ALL_NODES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.all.nodes";
    public static final String NEWEST_NODE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.newest.node";

    private static final UriMatcher sUriMATCHER = new UriMatcher(UriMatcher.NO_MATCH){{
        addURI(AUTHORITY, "allNodes", ALL_NODES);
        addURI(AUTHORITY, "newestNode", NEWEST_NODE);
    }};

    private static DBHelper mDBHelper;

    public static DBHelper getDBHelper() {
        if(mDBHelper == null){
            mDBHelper = new DBHelper(Application.getContext());
        }
        return mDBHelper;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (obj){
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(matchTable(uri));

            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    private String matchTable(Uri uri) {
        String table;
        switch (sUriMATCHER.match(uri)){
            case ALL_NODES:
                table = AllNodesDataHelper.TABLE_NAME;
                break;
            case NEWEST_NODE:
                table = NewestNodeDataHelper.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
        return table;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMATCHER.match(uri)){
            case ALL_NODES:
                return ALL_NODES_CONTENT_TYPE;
            case NEWEST_NODE:
                return NEWEST_NODE_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId =db.insert(matchTable(uri), null, values);
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
            if(rowId > 0){
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for(ContentValues contentValues : values){
                    db.insertWithOnConflict(matchTable(uri), BaseColumns._ID, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into "+ uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.delete(matchTable(uri), selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count;
            db.beginTransaction();
            try {
                count = db.update(matchTable(uri), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    public static class DBHelper extends SQLiteOpenHelper{

        private static final String DB_NAME = "v2ex_daily.db";

        private static final int DB_VERSION = 1;

        private DBHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            AllNodesDBInfo.TABLE.create(db);
            NewestNodeDBInfo.TABLE.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
