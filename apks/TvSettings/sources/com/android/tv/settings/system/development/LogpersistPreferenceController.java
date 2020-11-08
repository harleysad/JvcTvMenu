package com.android.tv.settings.system.development;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.AbstractLogpersistPreferenceController;
import com.android.tv.settings.R;

public class LogpersistPreferenceController extends AbstractLogpersistPreferenceController {
    /* access modifiers changed from: private */
    public Dialog mLogpersistClearDialog;

    LogpersistPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    public void showConfirmationDialog(@Nullable Preference preference) {
        if (preference != null) {
            if (this.mLogpersistClearDialog != null) {
                dismissConfirmationDialog();
            }
            this.mLogpersistClearDialog = new AlertDialog.Builder(this.mContext).setMessage(R.string.dev_logpersist_clear_warning_message).setTitle(R.string.dev_logpersist_clear_warning_title).setPositiveButton(17039379, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LogpersistPreferenceController.this.setLogpersistOff(true);
                }
            }).setNegativeButton(17039369, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LogpersistPreferenceController.this.updateLogpersistValues();
                }
            }).show();
            this.mLogpersistClearDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    LogpersistPreferenceController.this.mLogpersistClearDialog = null;
                }
            });
        }
    }

    public void dismissConfirmationDialog() {
        if (this.mLogpersistClearDialog != null) {
            this.mLogpersistClearDialog.dismiss();
            this.mLogpersistClearDialog = null;
        }
    }

    public boolean isConfirmationDialogShowing() {
        return this.mLogpersistClearDialog != null;
    }
}
