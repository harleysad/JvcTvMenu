package com.android.tv.settings.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.provider.Settings;
import android.service.autofill.AutofillServiceInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.wrapper.PackageManagerWrapper;
import java.util.ArrayList;
import java.util.List;

public class AutofillHelper {
    static final Intent AUTOFILL_PROBE = new Intent("android.service.autofill.AutofillService");
    private static final String TAG = "AutofillHelper";

    @NonNull
    public static List<DefaultAppInfo> getAutofillCandidates(@NonNull Context context, @NonNull PackageManagerWrapper pm, int myUserId) {
        List<DefaultAppInfo> candidates = new ArrayList<>();
        for (ResolveInfo info : pm.queryIntentServices(AUTOFILL_PROBE, 128)) {
            String permission = info.serviceInfo.permission;
            if ("android.permission.BIND_AUTOFILL_SERVICE".equals(permission) || "android.permission.BIND_AUTOFILL".equals(permission)) {
                candidates.add(new DefaultAppInfo(context, pm, myUserId, new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name)));
            }
        }
        return candidates;
    }

    @Nullable
    public static String getCurrentAutofill(@NonNull Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "autofill_service");
    }

    @Nullable
    public static ComponentName getCurrentAutofillAsComponentName(@NonNull Context context) {
        String flattenedName = getCurrentAutofill(context);
        if (TextUtils.isEmpty(flattenedName)) {
            return null;
        }
        return ComponentName.unflattenFromString(flattenedName);
    }

    @Nullable
    public static DefaultAppInfo getCurrentAutofill(@NonNull Context context, @NonNull List<DefaultAppInfo> candidates) {
        ComponentName name = getCurrentAutofillAsComponentName(context);
        for (int i = 0; i < candidates.size(); i++) {
            DefaultAppInfo appInfo = candidates.get(i);
            if ((name == null && appInfo.componentName == null) || (name != null && name.equals(appInfo.componentName))) {
                return appInfo;
            }
        }
        return null;
    }

    @Nullable
    public static Intent getAutofillSettingsIntent(@NonNull Context context, @NonNull PackageManagerWrapper pm, @Nullable DefaultAppInfo appInfo) {
        if (appInfo == null || appInfo.componentName == null) {
            return null;
        }
        String plattenString = appInfo.componentName.flattenToString();
        for (ResolveInfo resolveInfo : pm.queryIntentServices(AUTOFILL_PROBE, 128)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (TextUtils.equals(plattenString, new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToString())) {
                try {
                    String settingsActivity = new AutofillServiceInfo(context, serviceInfo).getSettingsActivity();
                    if (TextUtils.isEmpty(settingsActivity)) {
                        return null;
                    }
                    return new Intent("android.intent.action.MAIN").setComponent(new ComponentName(serviceInfo.packageName, settingsActivity));
                } catch (SecurityException e) {
                    Log.w(TAG, "Error getting info for " + serviceInfo + ": " + e);
                    return null;
                }
            }
        }
        return null;
    }

    public static void setCurrentAutofill(@NonNull Context context, @Nullable String id) {
        if (id != null) {
            Settings.Secure.putString(context.getContentResolver(), "autofill_service", id);
            return;
        }
        throw new IllegalArgumentException("Null ID");
    }
}
