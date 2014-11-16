package me.yugy.v2ex.dao.dbinfo;

import me.yugy.github.myutils.database.Column;
import me.yugy.github.myutils.database.SQLiteTable;

/**
 * Created by yugy on 14/11/15.
 */
public class MemberDBInfo {

    public static final int ID = 1;
    public static final String TABLE_NAME = "member";

    public static final String MID = "mid";
    public static final String USERNAME = "username";
    public static final String TAGLINE = "tagline";
    public static final String AVATAR = "avatar";

    public static final String URL = "url";
    public static final String WEBSITE = "website";
    public static final String TWITTER = "twitter";
    public static final String PSN = "psn";
    public static final String GITHUB = "github";
    public static final String BTC = "btc";
    public static final String LOCATION = "location";
    public static final String BIO = "bio";
    public static final String CREATED = "created";

    public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
            .addColumn(MID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(USERNAME, Column.DataType.TEXT)
            .addColumn(TAGLINE, Column.DataType.TEXT)
            .addColumn(AVATAR, Column.DataType.TEXT)
            .addColumn(URL, Column.DataType.TEXT)
            .addColumn(WEBSITE, Column.DataType.TEXT)
            .addColumn(TWITTER, Column.DataType.TEXT)
            .addColumn(PSN, Column.DataType.TEXT)
            .addColumn(GITHUB, Column.DataType.TEXT)
            .addColumn(BTC, Column.DataType.TEXT)
            .addColumn(LOCATION, Column.DataType.TEXT)
            .addColumn(BIO, Column.DataType.TEXT)
            .addColumn(CREATED, Column.DataType.INTEGER);

}
