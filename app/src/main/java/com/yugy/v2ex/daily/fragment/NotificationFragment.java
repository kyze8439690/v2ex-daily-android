package com.yugy.v2ex.daily.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yugy.v2ex.daily.R;
import com.yugy.v2ex.daily.activity.MainActivity;
import com.yugy.v2ex.daily.activity.TopicActivity;
import com.yugy.v2ex.daily.adapter.NotificationAdapter;
import com.yugy.v2ex.daily.model.NotificationModel;
import com.yugy.v2ex.daily.sdk.V2EX;
import com.yugy.v2ex.daily.utils.DebugUtils;
import com.yugy.v2ex.daily.utils.MessageUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-3-13.
 */
public class NotificationFragment extends Fragment implements OnRefreshListener, AdapterView.OnItemClickListener{

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;

    private ArrayList<NotificationModel> mModels;
    private String mToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_notification, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_fragment_notification);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.txt_fragment_notification_empty));
        mListView.setOnItemClickListener(this);
        return mPullToRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBarPullToRefresh.from(getActivity())
                .listener(this)
                .allChildrenArePullable()
                .setup(mPullToRefreshLayout);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences.contains("username")){
            if(sharedPreferences.contains("token")){
                mToken = sharedPreferences.getString("token", null);
                getNotificationData();
            }else{
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Getting notification token...", true, false);
                V2EX.getNotificationToken(getActivity(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    DebugUtils.log(response);
                    if (getActivity() != null) {
                        try {
                            if (response.getString("result").equals("ok")) {
                                progressDialog.setMessage("Save notification token...");
                                mToken = response.getString("token");
                                sharedPreferences.edit()
                                        .putString("token", mToken)
                                        .apply();
                                progressDialog.dismiss();
                                getNotificationData();
                            } else if (response.getString("result").equals("fail")) {
                                progressDialog.setMessage("Get token fail");
                                MessageUtils.toast(getActivity(), "Get token fail");
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    }
                });
            }
        }
    }

    private void getNotificationData(){
        mPullToRefreshLayout.setRefreshing(true);
        V2EX.getNotification(getActivity(), mToken, new TextHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Pattern entryPattern = Pattern.compile("<entry>[\\d\\D]+?</entry>");
                Matcher entryMatcher = entryPattern.matcher(responseBody);
                ArrayList<String> entries = new ArrayList<String>();
                while(entryMatcher.find()){
                    entries.add(entryMatcher.group());
                }
                mModels = new ArrayList<>();
                for(String entry : entries){
                    NotificationModel model = new NotificationModel();
                    try {
                        model.parse(entry);
                        mModels.add(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mListView.setAdapter(new NotificationAdapter(getActivity(), mModels));
                mPullToRefreshLayout.setRefreshComplete();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }

    @Override
    public void onRefreshStarted(View view) {
        if(mToken != null){
            getNotificationData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TopicActivity.class);
        Bundle argument = new Bundle();
        argument.putInt("topic_id", mModels.get(position).topicId);
        intent.putExtra("argument", argument);
        startActivity(intent);
    }
}
