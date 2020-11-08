package com.android.tv.settings.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;

public class AccessibilityHelper {
    public static boolean forceFocusableViews(Context context) {
        return ((AccessibilityManager) context.getSystemService(PartnerSettingsConfig.ATTR_DEVICE_ACCESSIBILITY)).isEnabled();
    }
}
