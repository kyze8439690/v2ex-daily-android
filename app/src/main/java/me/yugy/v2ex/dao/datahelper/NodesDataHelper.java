package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;

import me.yugy.v2ex.dao.dbinfo.NodeDBInfo;
import me.yugy.v2ex.model.Node;

/**
 * Created by yugy on 14/11/15.
 */
public class NodesDataHelper extends BaseDataHelper<Node>{
    @Override
    protected String getTableName() {
        return NodeDBInfo.TABLE_NAME;
    }

    @Override
    protected ContentValues getContentValues(Node T) {
        return T.toContentValues();
    }

    public Node select(int nid) {
        Cursor cursor = query(null, NodeDBInfo.NID + "=?", new String[]{String.valueOf(nid)}, null);
        Node node = null;
        if (cursor.moveToFirst()) {
            node = Node.fromCursor(cursor);
        }
        cursor.close();
        return node;
    }

    public void insert(Node node) {
        ContentValues values = getContentValues(node);
        if (select(node.id) != null) {
            update(values, NodeDBInfo.NID + "=?", new String[]{String.valueOf(node.id)});
        }else{
            insert(values);
        }
    }
}
