package com.yugy.v2ex.daily.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.swipeback.SwipeBackActivity;
import com.yugy.v2ex.daily.fragment.PostCommentDialogFragment;
import com.yugy.v2ex.daily.fragment.TopicFragment;

import org.json.JSONObject;

import static com.yugy.v2ex.daily.fragment.PostCommentDialogFragment.OnCommentFinishListener;

/**
 * Created by yugy on 14-2-24.
 */
public class TopicActivity extends SwipeBackActivity implements OnCommentFinishListener {

    private TopicFragment mTopicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        if(savedInstanceState == null){
            mTopicFragment = new TopicFragment();
            if(getIntent().hasExtra("argument")){
                mTopicFragment.setArguments(getIntent().getBundleExtra("argument"));
            }
            getFragmentManager().beginTransaction().add(R.id.container_activity_topic, mTopicFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }else{
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCommentFinished(JSONObject result) {
        if(mTopicFragment != null){
            mTopicFragment.onCommentFinish(result);
        }
    }
}
