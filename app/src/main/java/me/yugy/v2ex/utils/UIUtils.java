package me.yugy.v2ex.utils;

import android.content.Context;
import android.content.res.TypedArray;

import me.yugy.v2ex.R;

/**
 * Created by yugy on 14/11/18.
 */
public class UIUtils {

    public static int getActionBarHeight(Context context){
        int[] attrs = new int[] { R.attr.actionBarSize };
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs);
        int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarHeight;
    }
}
