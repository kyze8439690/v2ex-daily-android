package me.yugy.v2ex.dao.datahelper;

import android.content.ContentValues;
import android.widget.Toast;

import me.yugy.v2ex.dao.dbinfo.HotTopicsDBInfo;
import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/14.
 */
public class HotTopicsDataHelper extends BaseDataHelper<Topic>{
    @Override
    protected String getTableName() {
        return HotTopicsDBInfo.TABLE_NAME;
    }

    @Override
    protected ContentValues getContentValues(Topic topic) {
        return topic.toContentValues();
    }
}
