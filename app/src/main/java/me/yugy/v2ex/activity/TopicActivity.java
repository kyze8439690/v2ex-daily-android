package me.yugy.v2ex.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.yugy.v2ex.model.Topic;

/**
 * Created by yugy on 14/11/20.
 */
public class TopicActivity extends BaseActivity{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_HOT, TYPE_NEWEST, TYPE_NODE, TYPE_USER})
    public @interface Type {}

    public static final int TYPE_HOT = 0;
    public static final int TYPE_NEWEST = 1;
    public static final int TYPE_NODE = 2;
    public static final int TYPE_USER = 3;

    public static void launch(Context context, @Type int type, int topicId) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("topicId", topicId);
        context.startActivity(intent);
    }

    private Topic mTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
}
