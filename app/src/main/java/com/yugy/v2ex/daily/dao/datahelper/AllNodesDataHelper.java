package com.yugy.v2ex.daily.dao.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.yugy.v2ex.daily.dao.DataProvider;
import com.yugy.v2ex.daily.dao.dbinfo.AllNodesDBInfo;
import com.yugy.v2ex.daily.dao.dbinfo.BaseNodesDBInfo;
import com.yugy.v2ex.daily.model.NodeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yugy on 14-3-7.
 */
public class AllNodesDataHelper extends BaseDataHelper{

    public static final String TABLE_NAME = "allNodes";

    public AllNodesDataHelper(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.ALL_NODES_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return "allNodes";
    }

    protected ContentValues getContentValues(NodeModel node){
        ContentValues values = new ContentValues();
        values.put(AllNodesDBInfo.NODE_ID, node.id);
        values.put(AllNodesDBInfo.NAME, node.name);
        values.put(AllNodesDBInfo.TITLE, node.title);
        if(node.titleAlternative == null){
            values.put(AllNodesDBInfo.TITLE_ALTERNATIVE, "");
        }else{
            values.put(AllNodesDBInfo.TITLE_ALTERNATIVE, node.titleAlternative);
        }
        values.put(AllNodesDBInfo.URL, node.url);
        values.put(AllNodesDBInfo.TOPICS, node.topics);
        if(node.header == null){
            values.put(AllNodesDBInfo.HEADER, "");
        }else{
            values.put(AllNodesDBInfo.HEADER, node.header);
        }
        if(node.footer == null){
            values.put(AllNodesDBInfo.FOOTER, "");
        }else{
            values.put(AllNodesDBInfo.FOOTER, node.footer);
        }
        if(node.isCollected){
            values.put(AllNodesDBInfo.IS_COLLECTED, 1);
        }else{
            values.put(AllNodesDBInfo.IS_COLLECTED, 0);
        }
        return values;
    }

    /**
     * return all nodes
     * @return
     */
    public NodeModel[] query(){
        NodeModel[] nodes;
        Cursor cursor = query(null, null, null, null);
        if(cursor.moveToFirst()){
            ArrayList<NodeModel> nodeModelArrayList = new ArrayList<NodeModel>();
            nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            while (cursor.moveToNext()){
                nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            }
            nodes = nodeModelArrayList.toArray(new NodeModel[nodeModelArrayList.size()]);
        }else{
            nodes = new NodeModel[0];
        }
        cursor.close();
        return nodes;
    }

    public NodeModel[] getCollections(){
        NodeModel[] nodes;
        Cursor cursor = query(null, BaseNodesDBInfo.IS_COLLECTED + "=" + 1, null, null);
        if(cursor.moveToFirst()){
            ArrayList<NodeModel> nodeModelArrayList = new ArrayList<NodeModel>();
            nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            while (cursor.moveToNext()){
                nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            }
            nodes = nodeModelArrayList.toArray(new NodeModel[nodeModelArrayList.size()]);
        }else{
            nodes = new NodeModel[0];
        }
        cursor.close();
        return nodes;
    }

    public void removeCollections(){
        NodeModel[] nodes = getCollections();
        for(NodeModel node : nodes){
            node.isCollected = false;
            update(node);
        }
    }

    public void importCollections(String[] collections){
        for(String collectionName : collections){
            Cursor cursor = query(null, BaseNodesDBInfo.URL + "=\"http://www.v2ex.com/go/" + collectionName + "\"", null, null);
            if(cursor.moveToFirst()){
                NodeModel node = NodeModel.fromCursor(cursor);
                node.isCollected = true;
                update(node);
            }
        }
    }

    public NodeModel select(int nodeId){
        Cursor cursor = query(null, BaseNodesDBInfo.NODE_ID + "=" + nodeId, null, null);
        if(cursor.moveToFirst()){
            return NodeModel.fromCursor(cursor);
        }else{
            return null;
        }
    }

    public NodeModel[] search(String keyword){
        NodeModel[] nodes;
        Cursor cursor = query(null, BaseNodesDBInfo.TITLE + " like '%" + keyword + "%'", null, null);
        if(cursor.moveToFirst()){
            ArrayList<NodeModel> nodeModelArrayList = new ArrayList<NodeModel>();
            nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            while (cursor.moveToNext()){
                nodeModelArrayList.add(NodeModel.fromCursor(cursor));
            }
            nodes = nodeModelArrayList.toArray(new NodeModel[nodeModelArrayList.size()]);
        }else{
            nodes = new NodeModel[0];
        }
        cursor.close();
        return nodes;
    }

    public int bulkInsert(List<NodeModel> nodes){
        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        for(NodeModel node: nodes){
            ContentValues values = getContentValues(node);
            contentValues.add(values);
        }
        ContentValues[] valueArray = new ContentValues[contentValues.size()];
        return bulkInsert(contentValues.toArray(valueArray));
    }

    /**
     * return false if fail
     * @param isCollected
     * @param NodeId
     * @return
     */
    public boolean setCollected(boolean isCollected, int NodeId){
        Cursor cursor = query(null, BaseNodesDBInfo.NODE_ID + "=" + NodeId, null, null);
        if(cursor.moveToFirst()){
            NodeModel node = NodeModel.fromCursor(cursor);
            node.isCollected = isCollected;
            int result = update(node);
            if(result == 0){
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public int update(NodeModel node){
        ContentValues values = getContentValues(node);
        return update(values, BaseNodesDBInfo.NODE_ID + "=" + node.id, null);
    }

    public int bulkUpdate(List<NodeModel> nodes){
        synchronized (DataProvider.obj){
            SQLiteDatabase db = DataProvider.getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for(NodeModel node : nodes){
                    ContentValues values = getContentValues(node);
                    db.update(getTableName(), values, BaseNodesDBInfo.NODE_ID + "=" + node.id, null);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(getContentUri(), null);
                return nodes.size();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to update row into "+ getContentUri());
        }
    }
}
