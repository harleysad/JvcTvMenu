package com.android.tv.settings.device.display.daydream;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.v7.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

@Keep
public class DreamTimePreference extends ListPreference {
    public DreamTimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DreamTimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DreamTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DreamTimePreference(Context context) {
        super(context);
    }

    public CharSequence getSummary() {
        if (TextUtils.equals(getValue(), "-1")) {
            return getEntry();
        }
        return super.getSummary();
    }
}
