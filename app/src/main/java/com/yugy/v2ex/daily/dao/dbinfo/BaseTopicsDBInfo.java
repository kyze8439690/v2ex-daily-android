package com.yugy.v2ex.daily.dao.dbinfo;

import android.provider.BaseColumns;

/**
 * Created by yugy on 14-3-14.
 */
public class BaseTopicsDBInfo implements BaseColumns{

    public static final String TOPIC_ID = "topic_id";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String CONTENT = "content";
    public static final String CONTENT_RENDERED = "content_rendered";
    public static final String REPLIES = "replies";

    public static final String MEMBER_ID = "member_id";
    public static final String MEMBER_USERNAME = "member_username";
    public static final String MEMBER_TAGLINE = "member_tagline";
    public static final String MEMBER_AVATAR = "member_avatar";

    public static final String NODE_ID = "node_id";

    public static final String CREATED = "created";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String LAST_TOUCHED = "last_touched";

}
