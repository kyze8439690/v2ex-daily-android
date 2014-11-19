package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.yugy.v2ex.Application;
import me.yugy.v2ex.dao.DBHelper;
import me.yugy.v2ex.dao.DataProvider;
import me.yugy.v2ex.dao.dbinfo.MemberDBInfo;
import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;
import me.yugy.v2ex.dao.dbinfo.NodeTopicsDBInfo;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.model.Node;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class NodeTopicsDataHelper extends BaseDataHelper<Topic>{
    @Override
    protected String getTableName() {
        return NodeTopicsDBInfo.TABLE_NAME;
    }

    @Override
    protected ContentValues getContentValues(Topic topic) {
        return topic.toContentValues();
    }

    public Topic select(int tid) {
        Cursor cursor = query(null, NodeTopicsDBInfo.TID + "=?", new String[]{String.valueOf(tid)}, null);
        Topic topic = null;
        if (cursor.moveToFirst()){
            topic = Topic.fromCursor(cursor);
        }
        cursor.close();
        return topic;
    }

    public int getCount(int nodeId){
        synchronized (DataProvider.class){
            SQLiteDatabase db = new DBHelper().getReadableDatabase();
            Cursor cursor = db.query(getTableName(), new String[] {"count(*)"},
                    NodeTopicsDBInfo.NID + "=?", new String[]{String.valueOf(nodeId)}, null, null, null);
            int count;
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }else{
                count = 0;
            }
            cursor.close();
            return count;
        }
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
                    if (db.query(NodeTopicsDBInfo.TABLE_NAME, new String[]{NodeTopicsDBInfo.TID},
                            NodeTopicsDBInfo.TID + "=?", new String[]{String.valueOf(topic.id)},
                            null, null, null).moveToFirst()) {
                        db.update(NodeTopicsDBInfo.TABLE_NAME, topic.toContentValues(),
                                NodeTopicsDBInfo.TID + "=?", new String[]{String.valueOf(topic.id)});
                        changeCount++;
                    } else {
                        db.insert(NodeTopicsDBInfo.TABLE_NAME, null, topic.toContentValues());
                        changeCount++;
                    }
                }
                for (Member member : members) {
                    if (db.query(MemberDBInfo.TABLE_NAME, new String[]{MemberDBInfo.MID},
                            MemberDBInfo.MID + "=?", new String[]{String.valueOf(member.id)},
                            null, null, null).moveToFirst()){
                        db.update(MemberDBInfo.TABLE_NAME, member.toContentValues(),
                                MemberDBInfo.MID + "=?", new String[]{String.valueOf(member.id)});
                    } else {
                        db.insert(MemberDBInfo.TABLE_NAME, null, member.toContentValues());
                    }
                }
                for (Node node : nodes) {
                    if (db.query(NodeDBInfo.TABLE_NAME, new String[]{NodeDBInfo.NID},
                            NodeDBInfo.NID + "=?", new String[]{String.valueOf(node.id)},
                            null, null, null).moveToFirst()){
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
}
