package me.yugy.v2ex.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import me.yugy.v2ex.R;
import me.yugy.v2ex.adapter.MenuAdapter;

/**
 * Created by yugy on 14/11/13.
 */
public class MenuFragment extends ListFragment {

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

    public interface OnMenuItemSelectListener{
        public void onSelect(int index, String title);
    }

    public class HeaderContainer{
        @InjectView(R.id.head_icon) CircleImageView headIcon;
        @InjectView(R.id.name) TextView name;
    }

}
