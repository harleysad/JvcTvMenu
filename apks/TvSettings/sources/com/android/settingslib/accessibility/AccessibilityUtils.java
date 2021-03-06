package com.android.settingslib.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.accessibility.AccessibilityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AccessibilityUtils {
    public static final char ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR = ':';
    static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);

    public static Set<ComponentName> getEnabledServicesFromSettings(Context context) {
        return getEnabledServicesFromSettings(context, UserHandle.myUserId());
    }

    public static boolean hasServiceCrashed(String packageName, String serviceName, List<AccessibilityServiceInfo> enabledServiceInfos) {
        for (int i = 0; i < enabledServiceInfos.size(); i++) {
            AccessibilityServiceInfo accessibilityServiceInfo = enabledServiceInfos.get(i);
            ServiceInfo serviceInfo = enabledServiceInfos.get(i).getResolveInfo().serviceInfo;
            if (TextUtils.equals(serviceInfo.packageName, packageName) && TextUtils.equals(serviceInfo.name, serviceName)) {
                return accessibilityServiceInfo.crashed;
            }
        }
        return false;
    }

    public static Set<ComponentName> getEnabledServicesFromSettings(Context context, int userId) {
        String enabledServicesSetting = Settings.Secure.getStringForUser(context.getContentResolver(), "enabled_accessibility_services", userId);
        if (enabledServicesSetting == null) {
            return Collections.emptySet();
        }
        Set<ComponentName> enabledServices = new HashSet<>();
        TextUtils.SimpleStringSplitter colonSplitter = sStringColonSplitter;
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            ComponentName enabledService = ComponentName.unflattenFromString(colonSplitter.next());
            if (enabledService != null) {
                enabledServices.add(enabledService);
            }
        }
        return enabledServices;
    }

    public static CharSequence getTextForLocale(Context context, Locale locale, int resId) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config).getText(resId);
    }

    public static void setAccessibilityServiceState(Context context, ComponentName toggledService, boolean enabled) {
        setAccessibilityServiceState(context, toggledService, enabled, UserHandle.myUserId());
    }

    public static void setAccessibilityServiceState(Context context, ComponentName toggledService, boolean enabled, int userId) {
        Set<ComponentName> enabledServices = getEnabledServicesFromSettings(context, userId);
        if (enabledServices.isEmpty()) {
            enabledServices = new ArraySet<>(1);
        }
        if (!enabled) {
            enabledServices.remove(toggledService);
            Set<ComponentName> installedServices = getInstalledServices(context);
            Iterator<ComponentName> it = enabledServices.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (installedServices.contains(it.next())) {
                        break;
                    }
                } else {
                    break;
                }
            }
        } else {
            enabledServices.add(toggledService);
        }
        StringBuilder enabledServicesBuilder = new StringBuilder();
        for (ComponentName enabledService : enabledServices) {
            enabledServicesBuilder.append(enabledService.flattenToString());
            enabledServicesBuilder.append(ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR);
        }
        int enabledServicesBuilderLength = enabledServicesBuilder.length();
        if (enabledServicesBuilderLength > 0) {
            enabledServicesBuilder.deleteCharAt(enabledServicesBuilderLength - 1);
        }
        Settings.Secure.putStringForUser(context.getContentResolver(), "enabled_accessibility_services", enabledServicesBuilder.toString(), userId);
    }

    public static String getShortcutTargetServiceComponentNameString(Context context, int userId) {
        String currentShortcutServiceId = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_shortcut_target_service", userId);
        if (currentShortcutServiceId != null) {
            return currentShortcutServiceId;
        }
        return context.getString(17039650);
    }

    public static boolean isShortcutEnabled(Context context, int userId) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "accessibility_shortcut_enabled", 1, userId) == 1;
    }

    private static Set<ComponentName> getInstalledServices(Context context) {
        Set<ComponentName> installedServices = new HashSet<>();
        installedServices.clear();
        List<AccessibilityServiceInfo> installedServiceInfos = AccessibilityManager.getInstance(context).getInstalledAccessibilityServiceList();
        if (installedServiceInfos == null) {
            return installedServices;
        }
        for (AccessibilityServiceInfo info : installedServiceInfos) {
            ResolveInfo resolveInfo = info.getResolveInfo();
            installedServices.add(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
        }
        return installedServices;
    }
}
