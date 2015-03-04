package com.yugy.v2ex.daily.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.swipeback.SwipeBackActivity;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-2-26.
 */
public class LoginActivity extends SwipeBackActivity implements View.OnClickListener{

    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private ProgressDialog mProgressDialog;

    private AllNodesDataHelper mAllNodesDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsername = (EditText) findViewById(R.id.edit_activity_login_username);
        mPassword = (EditText) findViewById(R.id.edit_activity_login_password);
        mLogin = (Button) findViewById(R.id.btn_activity_login_login);
        mLogin.setOnClickListener(this);

        mAllNodesDataHelper = new AllNodesDataHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_activity_login_login:
                if(mUsername.getText().length() == 0){
                    mUsername.setError("Can not be empty");
                    mUsername.requestFocus();
                }else if(mPassword.getText().length() == 0){
                    mPassword.setError("Can not be empty");
                    mPassword.requestFocus();
                }else{
                    getOnceCode();
                }
                break;
            case R.id.txt_activity_login_sign_up:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.v2ex.com/signup")));
                break;
        }
    }

    private void getOnceCode(){
        mProgressDialog = ProgressDialog.show(LoginActivity.this, null, "Get Once Code...", true, true);
        V2EX.getOnceCode(this, "https://www.v2ex.com/signin", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(LoginActivity.this != null){
                    try {
                        if(response.getString("result").equals("ok")){
                            int onceCode = response.getJSONObject("content").getInt("once");
                            mProgressDialog.setMessage("Login...");
                            login(onceCode);
                        }else{
                            MessageUtils.toast(LoginActivity.this, "get once code fail");
                        }
                    } catch (JSONException e) {
                        MessageUtils.toast(LoginActivity.this, "json error");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void login(int onceCode){
        V2EX.login(LoginActivity.this, mUsername.getText().toString(), mPassword.getText().toString(), onceCode, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(LoginActivity.this != null){
                    try {
                        if(response.getString("result").equals("ok")){
                            mProgressDialog.setMessage("Get userinfo...");
                            getUserInfo();
                        }else if(response.getString("result").equals("fail")){
                            String errorContent = response.getJSONObject("content").getString("error_msg");
                            MessageUtils.toast(LoginActivity.this, errorContent);
                            mProgressDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getUserInfo(){
        V2EX.getUserInfo(LoginActivity.this, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                DebugUtils.log(response);
                try{
                    String username = response.getJSONObject("content").getString("username");
                    mProgressDialog.setMessage("Import Node Collections...");
                    JSONArray collectionsJson = response.getJSONObject("content").getJSONArray("collections");
                    String[] collections = new String[collectionsJson.length()];
                    for(int i = 0; i < collections.length; i++){
                        collections[i] = collectionsJson.getString(i);
                    }
                    mAllNodesDataHelper.removeCollections();
                    mAllNodesDataHelper.importCollections(collections);
                    MessageUtils.toast(LoginActivity.this, "Hello, " + username);
                    long currentTimeMillis = System.currentTimeMillis();
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                            .putString("username", username)
                            .putLong("sync_time", currentTimeMillis)
                            .commit();
                    mProgressDialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("username", username);
                    intent.putExtra("sync_time", currentTimeMillis);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
}
