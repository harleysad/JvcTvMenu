package com.android.tv.settings.device.apps;

import android.app.INotificationManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.util.Log;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;

public class NotificationsPreference extends SwitchPreference {
    private static final String TAG = "NotificationsPreference";
    private ApplicationsState.AppEntry mEntry;
    private final INotificationManager mNotificationManager = NotificationManager.getService();

    public NotificationsPreference(Context context, ApplicationsState.AppEntry entry) {
        super(context);
        this.mEntry = entry;
        refresh();
    }

    public void setEntry(@NonNull ApplicationsState.AppEntry entry) {
        this.mEntry = entry;
        refresh();
    }

    public void refresh() {
        setTitle((int) R.string.device_apps_app_management_notifications);
        try {
            super.setChecked(this.mNotificationManager.areNotificationsEnabledForPackage(this.mEntry.info.packageName, this.mEntry.info.uid));
        } catch (RemoteException e) {
            Log.d(TAG, "Remote exception while checking notifications for package " + this.mEntry.info.packageName, e);
        }
    }

    public void setChecked(boolean checked) {
        if (setNotificationsEnabled(checked)) {
            super.setChecked(checked);
        }
    }

    private boolean setNotificationsEnabled(boolean enabled) {
        if (isChecked() == enabled) {
            return true;
        }
        try {
            this.mNotificationManager.setNotificationsEnabledForPackage(this.mEntry.info.packageName, this.mEntry.info.uid, enabled);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
