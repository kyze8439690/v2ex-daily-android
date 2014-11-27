package me.yugy.v2ex.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.activity.LoginWebViewActivity;
import me.yugy.v2ex.activity.UserCenterActivity;
import me.yugy.v2ex.adapter.MenuAdapter;
import me.yugy.v2ex.dao.datahelper.MembersDataHelper;
import me.yugy.v2ex.model.HeadIconInfo;
import me.yugy.v2ex.model.Member;
import me.yugy.v2ex.network.RequestManager;
import me.yugy.v2ex.network.SimpleErrorListener;

/**
 * Created by yugy on 14/11/13.
 */
public class MenuFragment extends ListFragment {

    private static final int REQUEST_LOGIN = 523483845;

    private HeaderContainer mHeaderContainer = new HeaderContainer();
    private MenuAdapter mAdapter;
    private OnMenuItemSelectListener mOnMenuItemSelectListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] entries = getResources().getStringArray(R.array.menu_entries);
        int colorActive = getResources().getColor(R.color.menu_item_color_active);
        int colorNormal = getResources().getColor(R.color.text_color_primary);
        mAdapter = new MenuAdapter(entries, colorActive, colorNormal);

        if(savedInstanceState != null){
            mAdapter.setCurrentIndex(savedInstanceState.getInt("current_index", 0));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_menu, getListView(), false);
        ButterKnife.inject(mHeaderContainer, headerView);

        getListView().setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        getListView().setBackgroundColor(Color.WHITE);
        getListView().addHeaderView(headerView, null, false);
        getListView().setDividerHeight(0);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        setListAdapter(mAdapter);

        initUserInfo();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnMenuItemSelectListener = (OnMenuItemSelectListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.getClass().getName() + " should implement OnMenuSelectListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMenuItemSelectListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_index", mAdapter.getCurrentIndex());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int index = (int) id;
        if(id != mAdapter.getCurrentIndex()){
            mAdapter.setCurrentIndex(index);
            if(mOnMenuItemSelectListener != null){
                mOnMenuItemSelectListener.onSelect(index, mAdapter.getItem(index));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHeaderContainer != null) {
            if (!mHeaderContainer.headIcon.isShown()) {
                mHeaderContainer.headIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            initUserInfo();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUserInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences.contains("username")) {
            String username = preferences.getString("username", "点击登陆");
            mHeaderContainer.name.setText(username);
            Member member = new MembersDataHelper().select(username);
            if (member == null) {
                RequestManager.getInstance().getUserInfo(this, username, new Response.Listener<Member>() {
                    @Override
                    public void onResponse(Member response) {
                        loadUserInfo(response);
                    }
                }, new SimpleErrorListener(getActivity()));
            } else {
                loadUserInfo(member);
            }
        }
    }

    private void loadUserInfo(final Member member) {
        ImageLoader.getInstance().displayImage(member.avatar, mHeaderContainer.headIcon, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Palette.generateAsync(loadedImage, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                            mHeaderContainer.rootLayout.setBackgroundColor(palette.getDarkMutedColor(android.R.color.darker_gray));
                    }
                });
                mHeaderContainer.headIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.INVISIBLE);
                        HeadIconInfo headIconInfo = new HeadIconInfo();
                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        headIconInfo.left = screenLocation[0];
                        headIconInfo.top = screenLocation[1];
                        headIconInfo.width = v.getWidth();
                        headIconInfo.height = v.getHeight();
                        UserCenterActivity.launch(v.getContext(), member.username, headIconInfo);
                        ((Activity)v.getContext()).overridePendingTransition(0, 0);
                    }
                });
            }
        });
    }

    public interface OnMenuItemSelectListener{
        public void onSelect(int index, String title);
    }

    public class HeaderContainer{
        @InjectView(R.id.root_layout) View rootLayout;
        @InjectView(R.id.head_icon) CircleImageView headIcon;
        @InjectView(R.id.name) TextView name;

        @OnClick(R.id.root_layout)
        void onHeaderClick(View view){
            LoginWebViewActivity.launch(MenuFragment.this, REQUEST_LOGIN);
        }
    }

    @Override
    public void onDestroy() {
        RequestManager.getInstance().cancel(this);
        super.onDestroy();
    }
}
