package com.android.tv.settings.partnercustomizer.picture;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.android.tv.settings.R;

public class DialogPreference extends Preference {
    static final String TAG = "DialogPreference";
    private Dialog mDialog;

    public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }

    public DialogPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setDialog(Dialog dialog) {
        this.mDialog = dialog;
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        if (this.mDialog != null) {
            this.mDialog.show();
        }
    }
}
