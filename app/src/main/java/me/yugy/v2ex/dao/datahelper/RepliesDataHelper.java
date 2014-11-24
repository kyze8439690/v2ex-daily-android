package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.yugy.v2ex.Application;
import me.yugy.v2ex.dao.DBHelper;
import me.yugy.v2ex.dao.DataProvider;
import me.yugy.v2ex.dao.dbinfo.BaseTopicsDBInfo;
import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;
import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;
import me.yugy.v2ex.dao.dbinfo.ReplyDBInfo;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.model.Node;
import me.yugy.v2ex.model.Reply;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/19.
 */
public class RepliesDataHelper extends BaseDataHelper<Reply> {

    public Reply[] getReplies(int topicId) {
        Cursor cursor = query(null, ReplyDBInfo.TID + "=?", new String[]{String.valueOf(topicId)}, null);
        if (cursor != null) {
            Reply[] replies = new Reply[cursor.getCount()];
            while (cursor.moveToNext()) {
                replies[cursor.getPosition()] = Reply.fromCursor(cursor);
            }
            cursor.close();
            return replies;
        }
        return new Reply[0];
    }

    public void bulkInsert(Topic[] topics) {
        synchronized (DataProvider.class) {
            SQLiteDatabase db = new DBHelper().getWritableDatabase();
            int changeCount = 0;
            db.beginTransaction();
            try {
                ArrayList<Member> members = new ArrayList<Member>();
                ArrayList<Node> nodes = new ArrayList<Node>();
                for (Topic topic : topics) {
                    members.add(topic.member);
                    nodes.add(topic.node);
                    if (db.query(getTableName(), new String[]{BaseTopicsDBInfo.TID},
                            BaseTopicsDBInfo.TID + "=?", new String[]{String.valueOf(topic.id)},
                            null, null, null).moveToFirst()) {
                        db.update(getTableName(), topic.toContentValues(),
                                BaseTopicsDBInfo.TID + "=?", new String[]{String.valueOf(topic.id)});
                        changeCount++;
                    } else {
                        db.insert(getTableName(), null, topic.toContentValues());
                        changeCount++;
                    }
                }
                for (Member member : members) {
                    if (db.query(MemberDBInfo.TABLE_NAME, new String[]{MemberDBInfo.MID},
                            MemberDBInfo.MID + "=?", new String[]{String.valueOf(member.id)},
                            null, null, null).moveToFirst()) {
                        db.update(MemberDBInfo.TABLE_NAME, member.toContentValues(),
                                MemberDBInfo.MID + "=?", new String[]{String.valueOf(member.id)});
                    } else {
                        db.insert(MemberDBInfo.TABLE_NAME, null, member.toContentValues());
                    }
                }
                for (Node node : nodes) {
                    if (db.query(NodeDBInfo.TABLE_NAME, new String[]{NodeDBInfo.NID},
                            NodeDBInfo.NID + "=?", new String[]{String.valueOf(node.id)},
                            null, null, null).moveToFirst()) {
                        db.update(NodeDBInfo.TABLE_NAME, node.toContentValues(),
                                NodeDBInfo.NID + "=?", new String[]{String.valueOf(node.id)});
                    } else {
                        db.insert(NodeDBInfo.TABLE_NAME, null, node.toContentValues());
                    }
                }
                db.setTransactionSuccessful();
                if (changeCount > 0) {
                    Application.getInstance().getContentResolver().notifyChange(getContentUri(), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    @Override
    protected String getTableName() {
        return ReplyDBInfo.TABLE_NAME;
    }

    @Override
    protected ContentValues getContentValues(Reply T) {
        return T.toContentValues();
    }
}
