package com.yugy.v2ex.daily.dao.dbinfo;

import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.utils.database.SQLiteTable;

import static com.yugy.v2ex.daily.utils.database.Column.*;

/**
 * Created by yugy on 14-3-7.
 */
public class AllNodesDBInfo extends BaseNodesDBInfo {

    public static final SQLiteTable TABLE = new SQLiteTable(AllNodesDataHelper.TABLE_NAME)
            .addColumn(NODE_ID, DataType.INTEGER)
            .addColumn(NAME, DataType.TEXT)
            .addColumn(TITLE, DataType.TEXT)
            .addColumn(TITLE_ALTERNATIVE, DataType.TEXT)
            .addColumn(URL, DataType.TEXT)
            .addColumn(TOPICS, DataType.INTEGER)
            .addColumn(HEADER, DataType.TEXT)
            .addColumn(FOOTER, DataType.TEXT)
            .addColumn(IS_COLLECTED, DataType.INTEGER);

}
