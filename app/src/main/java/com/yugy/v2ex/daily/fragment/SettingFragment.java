package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.LoginActivity;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.network.RequestManager;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPreferenceManager().findPreference(PREF_LOGIN).setOnPreferenceClickListener(this);
        getPreferenceManager().findPreference(PREF_CONTACT).setOnPreferenceClickListener(this);
        getPreferenceManager().findPreference(PREF_UPDATE).setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }

    @Override
    public void onDestroy() {
        RequestManager.getInstance().cancelRequests(getActivity());
        super.onDestroy();
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
            if(getPreferenceManager().getSharedPreferences().contains("logined")){
                if(getPreferenceManager().getSharedPreferences().getBoolean("logined", false)){

                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
            return true;
        }
        return false;
    }
}
