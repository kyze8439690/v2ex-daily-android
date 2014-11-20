package me.yugy.v2ex.dao.dbinfo;

import me.yugy.github.myutils.database.Column;
import me.yugy.github.myutils.database.SQLiteTable;

/**
 * Created by yugy on 14/11/14.
 */
public class HotTopicsDBInfo extends BaseTopicsDBInfo{

    public static final int ID = 0;
    public static final String TABLE_NAME = "hot_topics";

    public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
            .addColumn(TID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(URL, Column.DataType.TEXT)
            .addColumn(CONTENT, Column.DataType.TEXT)
            .addColumn(CONTENT_RENDERED, Column.DataType.TEXT)
            .addColumn(REPLIES, Column.DataType.INTEGER)
            .addColumn(MID, Column.DataType.INTEGER)
            .addColumn(NID, Column.DataType.INTEGER)
            .addColumn(CREATED, Column.DataType.INTEGER)
            .addColumn(LAST_MODIFIED, Column.DataType.INTEGER)
            .addColumn(LAST_TOUCHED, Column.DataType.INTEGER);
}
