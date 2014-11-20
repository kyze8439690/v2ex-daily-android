package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import me.yugy.v2ex.Application;
import me.yugy.v2ex.dao.DBHelper;
import me.yugy.v2ex.dao.DataProvider;

/**
 * Created by yugy on 2014/7/3.
 */
public abstract class BaseDataHelper<T> {

    protected abstract String getTableName();

    private Uri mUri;

    public BaseDataHelper() {
        mUri = Uri.parse("content://" + DataProvider.AUTHORITY + "/" + getTableName());
    }

    public Uri getContentUri() {
        return mUri;
    }

    public void notifyChange() {
        Application.getInstance().getContentResolver().notifyChange(mUri, null);
    }

    protected final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return Application.getInstance().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    protected final Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return Application.getInstance().getContentResolver().query(mUri, projection, selection, selectionArgs, sortOrder);
    }

    protected final Uri insert(ContentValues values) {
        return Application.getInstance().getContentResolver().insert(mUri, values);
    }

    protected final int bulkInsert(ContentValues[] values) {
        return Application.getInstance().getContentResolver().bulkInsert(mUri, values);
    }

    protected final int update(ContentValues values, String where, String[] whereArgs) {
        return Application.getInstance().getContentResolver().update(mUri, values, where, whereArgs);
    }

    protected final int delete(String where, String[] selectionArgs) {
        return Application.getInstance().getContentResolver().delete(mUri, where, selectionArgs);
    }

    public CursorLoader getCursorLoader(Context context) {
        return getCursorLoader(context, null, null, null, null);
    }

    public CursorLoader getCursorLoader(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return new CursorLoader(context, mUri, projection, selection, selectionArgs, sortOrder);
    }

    public int deleteAll() {
        synchronized (DataProvider.class) {
            SQLiteDatabase db = new DBHelper().getWritableDatabase();
            return db.delete(getTableName(), null, null);
        }
    }

    public CursorLoader getCursorLoader() {
        return new CursorLoader(Application.getInstance(), mUri, null, null, null, null);
    }

    public CursorLoader getCursorLoader(String sortOrder) {
        CursorLoader loader = new CursorLoader(Application.getInstance(), mUri, null, null, null, sortOrder);
        return new CursorLoader(Application.getInstance(), mUri, null, null, null, sortOrder);
    }

    public CursorLoader getCursorLoader(String selection, String[] selectionArgs) {
        return new CursorLoader(Application.getInstance(), mUri, null, selection, selectionArgs, null);
    }

    protected abstract ContentValues getContentValues(T T);

    public int getCount() {
        synchronized (DataProvider.class) {
            SQLiteDatabase db = new DBHelper().getReadableDatabase();
            Cursor cursor = db.query(getTableName(), new String[]{"count(*)"}, null, null, null, null, null);
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