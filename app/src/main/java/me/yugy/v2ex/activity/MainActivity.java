package me.yugy.v2ex.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.fragment.HotTopicsFragment;
import me.yugy.v2ex.fragment.MenuFragment;


public class MainActivity extends BaseActivity implements MenuFragment.OnMenuItemSelectListener{

    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if(mToolbar != null){
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.text_color_primary));
        }

        if(savedInstanceState == null){
            //bug: mToolbar.setTitle() not working here.
            getSupportActionBar().setTitle(getResources().getStringArray(R.array.menu_entries)[0]);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HotTopicsFragment())
                    .commit();
        }else{
            getSupportActionBar().setTitle(savedInstanceState.getString("title"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", mToolbar.getTitle().toString());
    }

    @Override
    public void onSelect(int index, String title) {
        mToolbar.setTitle(title);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerLayout.setDrawerListener(null);

            }
        });
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
