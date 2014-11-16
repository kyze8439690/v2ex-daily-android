package me.yugy.v2ex.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import me.yugy.v2ex.BuildConfig;
import me.yugy.v2ex.dao.dbinfo.HotTopicsDBInfo;
import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;
import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;

/**
 * Created by yugy on 14/11/14.
 */
public class DataProvider extends ContentProvider{

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        return true;
    }

    public DBHelper getDBHelper() {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper();
        }
        return mDBHelper;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (DataProvider.class){
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(getTableName(uri));
            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH){{
        addURI(AUTHORITY, HotTopicsDBInfo.TABLE_NAME, HotTopicsDBInfo.ID);
        addURI(AUTHORITY, MemberDBInfo.TABLE_NAME, MemberDBInfo.ID);
        addURI(AUTHORITY, NodeDBInfo.TABLE_NAME, NodeDBInfo.ID);
    }};

    private String getTableName(Uri uri){
        switch (sUriMatcher.match(uri)) {
            case HotTopicsDBInfo.ID:
                return HotTopicsDBInfo.TABLE_NAME;
            case MemberDBInfo.ID:
                return MemberDBInfo.TABLE_NAME;
            case NodeDBInfo.ID:
                return NodeDBInfo.TABLE_NAME;
            default:
                return "";
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (DataProvider.class){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId =db.insert(getTableName(uri), null, values);
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
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        synchronized (DataProvider.class){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for(ContentValues contentValues : values){
                    db.insertWithOnConflict(getTableName(uri), BaseColumns._ID, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                db.endTransaction();
                return values.length;
            }catch (Exception e) {
                e.printStackTrace();
            }
            db.endTransaction();
            throw new SQLException("Failed to insert row into "+ uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DataProvider.class){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.delete(getTableName(uri), selection, selectionArgs);
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
        synchronized (DataProvider.class){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count;
            db.beginTransaction();
            try {
                count = db.update(getTableName(uri), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }
}
