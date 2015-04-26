package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.LoginActivity;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.dao.datahelper.AllNodesDataHelper;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.TextUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.preference.Preference.OnPreferenceClickListener;

/**
 * Created by yugy on 14-2-26.
 */
public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static final String PREF_LOGIN = "pref_login";
    public static final String PREF_CONTACT = "pref_contact";
    public static final String PREF_UPDATE = "pref_check_update";
    public static final String PREF_SYNC = "pref_sync";

    private static final int REQUEST_CODE_LOGIN = 10086;

    private boolean logined = false;

    private AllNodesDataHelper mAllNodesDataHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAllNodesDataHelper = new AllNodesDataHelper(getActivity());

        if(logined = getPreferenceManager().getSharedPreferences().contains("username")){
            String username = getPreferenceManager().getSharedPreferences().getString("username", null);
            if(username != null){
                findPreference(PREF_LOGIN).setTitle(username);
                findPreference(PREF_SYNC).setEnabled(true);
                long syncTime = getPreferenceManager().getSharedPreferences().getLong("sync_time", 0);
                if(syncTime != 0){
                    findPreference(PREF_SYNC).setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), syncTime));
                }
            }
        }

        findPreference(PREF_LOGIN).setOnPreferenceClickListener(this);
        findPreference(PREF_CONTACT).setOnPreferenceClickListener(this);
        findPreference(PREF_UPDATE).setOnPreferenceClickListener(this);
        findPreference(PREF_SYNC).setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(5);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if(preference.getKey().equals(PREF_CONTACT)){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:me@yanghui.name"));
            if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
            else{
                AppMsg.makeText(getActivity(), "没有找到邮件程序", AppMsg.STYLE_CONFIRM).show();
            }
            return true;
        }else if(preference.getKey().equals(PREF_UPDATE)){
            UmengUpdateAgent.forceUpdate(getActivity());
            UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                @Override
                public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                    if(getActivity() != null){
                        switch (i){
                            case UpdateStatus.No:
                                AppMsg.makeText(getActivity(), "您现在使用的就是最新版本", AppMsg.STYLE_INFO).show();
                                break;
                            case UpdateStatus.Timeout:
                                AppMsg.makeText(getActivity(), "网络超时", AppMsg.STYLE_CONFIRM).show();
                                break;
                        }
                    }
                    UmengUpdateAgent.setUpdateListener(null);
                }
            });
            return true;
        }else if(preference.getKey().equals(PREF_LOGIN)){
            if(logined){
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setMessage("你确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }else{
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_CODE_LOGIN);
            }
            return true;
        }else if(preference.getKey().equals(PREF_SYNC)){
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Syncing...", true, true);
            V2EX.getUserInfo(getActivity(), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    DebugUtils.log(response);
                    try{
                        progressDialog.setMessage("Import Node Collections...");
                        JSONArray collectionsJson = response.getJSONObject("content").getJSONArray("collections");
                        String[] collections = new String[collectionsJson.length()];
                        for(int i = 0; i < collections.length; i++){
                            collections[i] = collectionsJson.getString(i);
                        }
                        mAllNodesDataHelper.removeCollections();
                        mAllNodesDataHelper.importCollections(collections);
                        long currentTimeMillis = System.currentTimeMillis();
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                                .putLong("sync_time", currentTimeMillis)
                                .commit();
                        progressDialog.setMessage("Finished");
                        progressDialog.dismiss();
                        preference.setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), currentTimeMillis));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }else {
            return false;
        }
    }

    private void logout(){
        getPreferenceManager().getSharedPreferences().edit()
                .remove("username")
                .remove("reg_time")
                .remove("sync_time")
                .remove("token")
                .commit();
        V2EX.logout(getActivity());
        logined = false;
        findPreference(PREF_LOGIN).setTitle(getString(R.string.title_login));
        findPreference(PREF_SYNC).setEnabled(false);
        findPreference(PREF_SYNC).setSummary(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK){
            String username = data.getStringExtra("username");
            long syncTime = data.getLongExtra("sync_time", 0);
            logined = username != null;
            if(logined){
                findPreference(PREF_LOGIN).setTitle(username);
                findPreference(PREF_SYNC).setEnabled(true);
                if(syncTime != 0){
                    findPreference(PREF_SYNC).setSummary(TextUtils.getRelativeTimeDisplayString(getActivity(), syncTime));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
