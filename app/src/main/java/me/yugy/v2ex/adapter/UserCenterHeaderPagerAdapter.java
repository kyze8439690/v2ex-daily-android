package me.yugy.v2ex.adapter;

import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import me.yugy.v2ex.fragment.UserInfoFirstFragment;
import me.yugy.v2ex.fragment.UserInfoSecondFragment;
import me.yugy.v2ex.model.HeadIconInfo;

/**
 * Created by yugy on 14/11/17.
 */
public class UserCenterHeaderPagerAdapter extends FragmentPagerAdapter {

    private UserInfoFirstFragment mUserInfoFirstFragment;
    private UserInfoSecondFragment mUserInfoSecondFragment;

    public UserCenterHeaderPagerAdapter(FragmentManager fm, String username, HeadIconInfo headIconInfo) {
        super(fm);
        mUserInfoFirstFragment = UserInfoFirstFragment.newInstance(username, headIconInfo);
        mUserInfoSecondFragment = UserInfoSecondFragment.newInstance(username);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: return mUserInfoFirstFragment;
            case 1: return mUserInfoSecondFragment;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void refreshData() {
//        mUserInfoFirstFragment.refreshData();
        mUserInfoSecondFragment.refreshData();
    }

    public void playExitAnimation(Animator.AnimatorListener listener) {
        mUserInfoFirstFragment.playExitAnimation(listener);
    }
}
