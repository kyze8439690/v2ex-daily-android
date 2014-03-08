package com.yugy.v2ex.daily.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.yugy.v2ex.daily.R;

/**
 * Created by yugy on 14-3-9.
 */
public class TextUtils {

    public static CharSequence getRelativeTimeDisplayString(Context context, long referenceTime) {
        long now = System.currentTimeMillis();
        long difference = now - referenceTime;
        return (difference >= 0 &&  difference<= DateUtils.MINUTE_IN_MILLIS) ?
                context.getResources().getString(R.string.just_now):
                DateUtils.getRelativeTimeSpanString(
                        referenceTime,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
    }

}
