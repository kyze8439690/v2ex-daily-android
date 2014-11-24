package me.yugy.v2ex.dao.dbinfo;

import me.yugy.github.myutils.database.Column;
import me.yugy.github.myutils.database.SQLiteTable;

/**
 * Created by yugy on 14/11/20.
 */
public class ReplyDBInfo {

    public static final int ID = 6;
    public static final String TABLE_NAME = "replies";

    public static final String RID = "rid";
    public static final String THANKS = "thanks";
    public static final String CONTENT = "content";
    public static final String CONTENT_RENDERED = "content_rendered";
    public static final String MID = "mid";
    public static final String CREATED = "created";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String TID = "tid";

    public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
            .addColumn(RID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(THANKS, Column.DataType.INTEGER)
            .addColumn(CONTENT, Column.DataType.TEXT)
            .addColumn(CONTENT_RENDERED, Column.DataType.TEXT)
            .addColumn(MID, Column.DataType.INTEGER)
            .addColumn(CREATED, Column.DataType.INTEGER)
            .addColumn(LAST_MODIFIED, Column.DataType.INTEGER)
            .addColumn(TID, Column.DataType.INTEGER);
}
