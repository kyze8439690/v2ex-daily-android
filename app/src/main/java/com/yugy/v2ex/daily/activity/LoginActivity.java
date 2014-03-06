package com.yugy.v2ex.daily.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.swipeback.SwipeBackActivity;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by yugy on 14-2-26.
 */
public class LoginActivity extends SwipeBackActivity implements View.OnClickListener{

    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsername = (EditText) findViewById(R.id.edit_activity_login_username);
        mPassword = (EditText) findViewById(R.id.edit_activity_login_password);
        mLogin = (Button) findViewById(R.id.btn_activity_login_login);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mUsername.getText().length() == 0){
            mUsername.setError("Can not be empty");
            mUsername.requestFocus();
        }else if(mPassword.getText().length() == 0){
            mPassword.setError("Can not be empty");
            mPassword.requestFocus();
        }else{
            final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, null, "Login...", true, true);
            V2EX.getOnceCode(this, "http://www.v2ex.com/signin", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONObject response) {
                    if(LoginActivity.this != null){
                        try {
                            if(response.getString("result").equals("ok")){
                                int onceCode = response.getJSONObject("content").getInt("once");
                                V2EX.login(LoginActivity.this, mUsername.getText().toString(), mPassword.getText().toString(), onceCode, new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        if(LoginActivity.this != null){
                                            try {
                                                if(response.getString("result").equals("ok")){
                                                    String username = response.getJSONObject("content").getString("username");
                                                    MessageUtils.toast(LoginActivity.this, "Hello, " + username);
                                                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("username", username).commit();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent();
                                                    intent.putExtra("username", username);
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }else if(response.getString("result").equals("fail")){
                                                    String errorContent = response.getJSONObject("content").getString("error_msg");
                                                    MessageUtils.toast(LoginActivity.this, errorContent);
                                                    progressDialog.dismiss();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            super.onSuccess(response);
                                        }
                                    }
                                });
                            }else{
                                MessageUtils.toast(LoginActivity.this, "get oncecode fail");
                            }
                            super.onSuccess(response);
                        } catch (JSONException e) {
                            MessageUtils.toast(LoginActivity.this, "json error");
                            e.printStackTrace();
                        }
                    }
                }
            });
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
}
