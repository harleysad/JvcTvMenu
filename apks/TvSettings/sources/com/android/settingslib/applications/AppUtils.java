package com.android.settingslib.applications;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import com.android.settingslib.R;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.instantapps.InstantAppDataProvider;
import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    private static final String TAG = "AppUtils";
    private static InstantAppDataProvider sInstantAppDataProvider = null;

    public static CharSequence getLaunchByDefaultSummary(ApplicationsState.AppEntry appEntry, IUsbManager usbManager, PackageManager pm, Context context) {
        int i;
        String packageName = appEntry.info.packageName;
        boolean hasDomainURLsPreference = true;
        boolean hasPreferred = hasPreferredActivities(pm, packageName) || hasUsbDefaults(usbManager, packageName);
        if (pm.getIntentVerificationStatusAsUser(packageName, UserHandle.myUserId()) == 0) {
            hasDomainURLsPreference = false;
        }
        if (hasPreferred || hasDomainURLsPreference) {
            i = R.string.launch_defaults_some;
        } else {
            i = R.string.launch_defaults_none;
        }
        return context.getString(i);
    }

    public static boolean hasUsbDefaults(IUsbManager usbManager, String packageName) {
        if (usbManager == null) {
            return false;
        }
        try {
            return usbManager.hasDefaults(packageName, UserHandle.myUserId());
        } catch (RemoteException e) {
            Log.e(TAG, "mUsbManager.hasDefaults", e);
            return false;
        }
    }

    public static boolean hasPreferredActivities(PackageManager pm, String packageName) {
        List<ComponentName> prefActList = new ArrayList<>();
        pm.getPreferredActivities(new ArrayList<>(), prefActList, packageName);
        Log.d(TAG, "Have " + prefActList.size() + " number of activities in preferred list");
        return prefActList.size() > 0;
    }

    public static boolean isInstant(ApplicationInfo info) {
        String[] searchTerms;
        if (sInstantAppDataProvider != null) {
            if (sInstantAppDataProvider.isInstantApp(info)) {
                return true;
            }
        } else if (info.isInstantApp()) {
            return true;
        }
        String propVal = SystemProperties.get("settingsdebug.instant.packages");
        if (!(propVal == null || propVal.isEmpty() || info.packageName == null || (searchTerms = propVal.split(",")) == null)) {
            for (String term : searchTerms) {
                if (info.packageName.contains(term)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static CharSequence getApplicationLabel(PackageManager packageManager, String packageName) {
        try {
            return packageManager.getApplicationInfo(packageName, 4194816).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to find info for package: " + packageName);
            return null;
        }
    }
}
