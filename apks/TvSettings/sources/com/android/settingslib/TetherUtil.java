package com.android.settingslib;

import android.content.Context;
import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.telephony.CarrierConfigManager;

public class TetherUtil {
    @VisibleForTesting
    static boolean isEntitlementCheckRequired(Context context) {
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (configManager == null || configManager.getConfig() == null) {
            return true;
        }
        return configManager.getConfig().getBoolean("require_entitlement_checks_bool");
    }

    public static boolean isProvisioningNeeded(Context context) {
        String[] provisionApp = context.getResources().getStringArray(17236021);
        if (SystemProperties.getBoolean("net.tethering.noprovisioning", false) || provisionApp == null || !isEntitlementCheckRequired(context) || provisionApp.length != 2) {
            return false;
        }
        return true;
    }
}
