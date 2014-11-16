package me.yugy.v2ex.dao.dbinfo;

import me.yugy.github.myutils.database.Column;
import me.yugy.github.myutils.database.SQLiteTable;

/**
 * Created by yugy on 14/11/15.
 */
public class NodeDBInfo {

    public static final int ID = 2;
    public static final String TABLE_NAME = "node";

    public static final String NID = "nid";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String TITLE_ALTERNATIVE = "title_alternative";
    public static final String URL = "url";
    public static final String TOPICS = "topics";
    public static final String AVATAR = "avatar";

    public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
            .addColumn(NID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(NAME, Column.DataType.TEXT)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(TITLE_ALTERNATIVE, Column.DataType.TEXT)
            .addColumn(URL, Column.DataType.TEXT)
            .addColumn(TOPICS, Column.DataType.INTEGER)
            .addColumn(AVATAR, Column.DataType.TEXT);


}
