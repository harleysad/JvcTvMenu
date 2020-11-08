package com.android.tv.settings.connectivity.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import com.android.tv.settings.R;

public class WifiSecurityUtil {
    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return 1;
        }
        if (result.capabilities.contains("PSK")) {
            return 2;
        }
        if (result.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    public static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (config.wepKeys[0] != null) {
            return 1;
        }
        return 0;
    }

    public static String getName(Context context, int wifiSecurity) {
        switch (wifiSecurity) {
            case 0:
                return context.getString(R.string.wifi_security_type_none);
            case 1:
                return context.getString(R.string.wifi_security_type_wep);
            case 2:
                return context.getString(R.string.wifi_security_type_wpa);
            case 3:
                return context.getString(R.string.wifi_security_type_eap);
            default:
                return null;
        }
    }

    public static boolean isOpen(int wifiSecurity) {
        return wifiSecurity == 0;
    }
}
