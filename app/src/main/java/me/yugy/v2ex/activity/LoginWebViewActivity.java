package me.yugy.v2ex.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.github.myutils.MessageUtils;
import me.yugy.v2ex.R;

/**
 * Created by yugy on 14/11/27.
 */
public class LoginWebViewActivity extends BaseActivity{

    public static void launch(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginWebViewActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @InjectView(R.id.progress) ProgressBar mProgressBar;
    @InjectView(R.id.webview) WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_webview);
        ButterKnife.inject(this);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new GetUserInfoJavaScriptInterface(this), "htmlViewer");
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (mProgressBar.isShown()) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (!mProgressBar.isShown()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("http://www.v2ex.com/")) {
                    //login success
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookies = cookieManager.getCookie("www.v2ex.com");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginWebViewActivity.this);
                    preferences.edit().putString("cookies", cookies).apply();
                    view.loadUrl("javascript:window.htmlViewer.getUserInfo('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }
        });
        mWebView.loadUrl("http://www.v2ex.com/signin");
    }

    class GetUserInfoJavaScriptInterface {
        private Context mContext;

        GetUserInfoJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void getUserInfo(String html) {
            Pattern pattern = Pattern.compile("<a href=\"/member/([^\"]+)\" class=\"top\">");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                try {
                    String username = matcher.group(1);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginWebViewActivity.this);
                    preferences.edit().putString("username", username).apply();
                    setResult(RESULT_OK);
                    MessageUtils.toast(mContext, getString(R.string.login_success));
                    finish();
                    return;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            MessageUtils.toast(mContext, getString(R.string.login_fail));
            finish();
        }
    }
}
