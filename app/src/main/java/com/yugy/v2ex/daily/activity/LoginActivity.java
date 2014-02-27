package com.yugy.v2ex.daily.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.swipeback.SwipeBackActivity;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by yugy on 14-2-26.
 */
public class LoginActivity extends SwipeBackActivity{

    private WebView mWebView;
    private SmoothProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebView = (WebView) findViewById(R.id.webview_activity_login);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progress_activity_login);
        mWebView.setWebChromeClient(new LoginWebChromeClient());
        mWebView.setWebViewClient(new LoginWebViewClient());

        mWebView.loadUrl("http://www.v2ex.com/signin");
    }
    
    private class LoginWebChromeClient extends WebChromeClient{

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                if (!mProgressBar.isShown()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class LoginWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            DebugUtils.log(url);
            if(url.equals("http://www.v2ex.com/")){
                CookieManager cookieManager = CookieManager.getInstance();
                DebugUtils.log(cookieManager.getCookie("http://www.v2ex.com/"));
                new GetUserInfoTask().execute();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class GetUserInfoTask extends AsyncTask<Void, Void, Void> implements DialogInterface.OnCancelListener{

        @Override
        protected void onPreExecute() {
            ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, null, "Getting userinfo", true, true, this);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            cancel(true);
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
