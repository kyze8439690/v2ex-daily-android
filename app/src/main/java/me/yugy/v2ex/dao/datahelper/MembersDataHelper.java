package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;

import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;
import me.yugy.v2ex.model.Member;

/**
 * Created by yugy on 14/11/15.
 */
public class MembersDataHelper extends BaseDataHelper<Member>{
    @Override
    protected String getTableName() {
        return MemberDBInfo.TABLE_NAME;
    }

    @Override
    protected ContentValues getContentValues(Member member) {
        return member.toContentValues();
    }

    public Member select(int mid){
        Cursor cursor = query(null, MemberDBInfo.MID + "=?", new String[]{String.valueOf(mid)}, null);
        Member member = null;
        if (cursor.moveToFirst()) {
            member = Member.fromCursor(cursor);
        }
        cursor.close();
        return member;
    }

    public Member select(String username){
        Cursor cursor = query(null, MemberDBInfo.USERNAME + "=?", new String[]{username}, null);
        Member member = null;
        if (cursor.moveToFirst()) {
            member = Member.fromCursor(cursor);
        }
        cursor.close();
        return member;
    }

    public void insert(Member member) {
        ContentValues values = getContentValues(member);
        if (select(member.id) != null) {
            update(values, MemberDBInfo.MID + "=?", new String[]{String.valueOf(member.id)});
        } else {
            insert(values);
        }
    }

}
