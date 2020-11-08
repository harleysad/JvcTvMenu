package com.android.tv.settings.device.apps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.subtitle.Cea708CCParser;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;

public class UninstallPreference extends AppActionPreference {
    public UninstallPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        refresh();
    }

    public void refresh() {
        if (canUninstall()) {
            setVisible(true);
            setTitle((int) R.string.device_apps_app_management_uninstall);
        } else if (canUninstallUpdates()) {
            setVisible(true);
            setTitle((int) R.string.device_apps_app_management_uninstall_updates);
        } else {
            setVisible(false);
        }
    }

    public boolean canUninstall() {
        return canUninstall(this.mEntry);
    }

    public static boolean canUninstall(ApplicationsState.AppEntry entry) {
        return (entry.info.flags & Cea708CCParser.Const.CODE_C1_CW1) == 0;
    }

    public boolean canUninstallUpdates() {
        return (this.mEntry.info.flags & 128) != 0;
    }

    public Intent getIntent() {
        Intent uninstallIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + this.mEntry.info.packageName));
        uninstallIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", true);
        uninstallIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
        return uninstallIntent;
    }
}
