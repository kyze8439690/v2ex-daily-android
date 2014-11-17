package me.yugy.v2ex.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.model.Member;

/**
 * Created by yugy on 14/11/17.
 */
public class UserInfoSecondFragment extends Fragment{

    public static UserInfoSecondFragment newInstance(String username) {
        UserInfoSecondFragment fragment = new UserInfoSecondFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @InjectView(R.id.table_layout) TableLayout mTableLayout;
    @InjectView(R.id.empty_view) View mEmptyView;
    private String mUsername;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_userinfo_second, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshData();
    }

    public void refreshData() {
        if (mUsername == null) {
            mUsername = getArguments().getString("username");
        }
        Member member = new MembersDataHelper().select(mUsername);
        if (member != null) {
            mTableLayout.removeAllViews();
            if (!TextUtils.isEmpty(member.website)) {
                View view = newRowView("Website", member.website, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.twitter)) {
                View view = newRowView("Twitter", member.twitter, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.psn)) {
                View view = newRowView("PSN", member.psn, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.github)) {
                View view = newRowView("Github", member.github, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.btc)) {
                View view = newRowView("BTC", member.btc, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.location)) {
                View view = newRowView("Location", member.location, null);
                mTableLayout.addView(view);
            }
            if (!TextUtils.isEmpty(member.bio)) {
                View view = newRowView("BIO", member.bio, null);
                mTableLayout.addView(view);
            }
            if (mTableLayout.getChildCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    public View newRowView(String key, String value, View.OnClickListener onClickListener) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_userinfo, mTableLayout, false);
        RowContainer container = new RowContainer(view);
        container.key.setText(key);
        container.value.setText(value);
        view.setOnClickListener(onClickListener);
        return view;
    }

    class RowContainer{
        @InjectView(R.id.key) TextView key;
        @InjectView(R.id.value) TextView value;

        public RowContainer(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
