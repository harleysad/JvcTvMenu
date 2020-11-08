package com.android.tv.settings.system;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import com.android.tv.settings.R;

public class LeanbackPickerDialogPreference extends DialogPreference {
    private final String mPreferenceType;

    public LeanbackPickerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LeanbackPickerDialogPreference, 0, 0);
        this.mPreferenceType = a.getString(0);
        a.recycle();
    }

    public LeanbackPickerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LeanbackPickerDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.dialogPreferenceStyle, 16842897));
    }

    public LeanbackPickerDialogPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public String getType() {
        return this.mPreferenceType;
    }
}
