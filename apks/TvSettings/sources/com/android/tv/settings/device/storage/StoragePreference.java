package com.android.tv.settings.device.storage;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.text.format.Formatter;
import android.util.AttributeSet;
import com.android.tv.settings.R;

public class StoragePreference extends Preference {
    private static final long SIZE_CALCULATING = -1;

    public StoragePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSize(-1);
    }

    public StoragePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSize(-1);
    }

    public StoragePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSize(-1);
    }

    public StoragePreference(Context context) {
        super(context);
        setSize(-1);
    }

    public void setSize(long size) {
        setSummary((CharSequence) formatSize(getContext(), size));
    }

    public static String formatSize(Context context, long size) {
        if (size == -1) {
            return context.getString(R.string.storage_calculating_size);
        }
        return Formatter.formatShortFileSize(context, size);
    }
}
