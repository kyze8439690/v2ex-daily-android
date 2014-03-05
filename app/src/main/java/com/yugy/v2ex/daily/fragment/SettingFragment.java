package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.LoginActivity;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;
import com.yugy.v2ex.daily.widget.AppMsg;

/**
 * Created by yugy on 14-2-26.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    private static final String PREF_LOGIN = "pref_login";
    private static final String PREF_CONTACT = "pref_contact";
    private static final String PREF_UPDATE = "pref_check_update";

    private static final int REQUEST_CODE_LOGIN = 10086;

    private boolean logined = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(logined = getPreferenceManager().getSharedPreferences().contains("username")){
            String username = getPreferenceManager().getSharedPreferences().getString("username", null);
            if(username != null){
                findPreference(PREF_LOGIN).setTitle(username);
            }
        }

        findPreference(PREF_LOGIN).setOnPreferenceClickListener(this);
        findPreference(PREF_CONTACT).setOnPreferenceClickListener(this);
        findPreference(PREF_UPDATE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals(PREF_CONTACT)){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:me@yanghui.name"));
            if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
            else{
                AppMsg.makeText(getActivity(), "没有找到邮件程序", AppMsg.STYLE_CONFIRM).show();
            }
            return true;
        }
        if(preference.getKey().equals(PREF_UPDATE)){
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
        }
        if(preference.getKey().equals(PREF_LOGIN)){
            if(logined){
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setMessage("你确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getPreferenceManager().getSharedPreferences().edit()
                                        .remove("username")
                                        .commit();
                                logined = false;
                                getPreferenceManager().findPreference(PREF_LOGIN).setTitle("登陆");
                            }
                        }).setNegativeButton("取消", null)
                        .show();
            }else{
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_CODE_LOGIN);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK){
            String username = data.getStringExtra("username");
            logined = username != null;
            if(logined){
                findPreference(PREF_LOGIN).setTitle(username);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
