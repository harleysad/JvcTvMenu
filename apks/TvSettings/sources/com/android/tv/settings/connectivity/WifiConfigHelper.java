package com.android.tv.settings.connectivity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.WifiSecurityUtil;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WifiConfigHelper {
    private static final boolean DEBUG = false;
    private static final Pattern EXCLUSION_PATTERN = Pattern.compile(EXCLUSION_REGEXP);
    private static final String EXCLUSION_REGEXP = "$|^(\\*)?\\.?[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$";
    private static final String HC = "a-zA-Z0-9\\_";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$";
    private static final String TAG = "WifiConfigHelper";

    private WifiConfigHelper() {
    }

    public static void setConfigSsid(WifiConfiguration config, String ssid) {
        config.SSID = AccessPoint.convertToQuotedString(ssid);
    }

    public static void setConfigKeyManagementBySecurity(WifiConfiguration config, int security) {
        config.allowedKeyManagement.clear();
        config.allowedAuthAlgorithms.clear();
        switch (security) {
            case 0:
                config.allowedKeyManagement.set(0);
                return;
            case 1:
                config.allowedKeyManagement.set(0);
                config.allowedAuthAlgorithms.set(0);
                config.allowedAuthAlgorithms.set(1);
                return;
            case 2:
                config.allowedKeyManagement.set(1);
                return;
            case 3:
                config.allowedKeyManagement.set(2);
                config.allowedKeyManagement.set(3);
                return;
            default:
                return;
        }
    }

    public static int validate(String hostname, String port, String exclList) {
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        String[] exclListArray = exclList.split(",");
        if (!match.matches()) {
            return R.string.proxy_error_invalid_host;
        }
        for (String excl : exclListArray) {
            if (!EXCLUSION_PATTERN.matcher(excl).matches()) {
                return R.string.proxy_error_invalid_exclusion_list;
            }
        }
        if (hostname.length() > 0 && port.length() == 0) {
            return R.string.proxy_error_empty_port;
        }
        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return R.string.proxy_error_empty_host_set_port;
            }
            try {
                int portVal = Integer.parseInt(port);
                if (portVal <= 0 || portVal > 65535) {
                    return R.string.proxy_error_invalid_port;
                }
            } catch (NumberFormatException e) {
                return R.string.proxy_error_invalid_port;
            }
        }
        return 0;
    }

    public static WifiConfiguration getWifiConfiguration(WifiManager wifiManager, int networkId) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks == null) {
            return null;
        }
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            if (configuredNetwork.networkId == networkId) {
                return configuredNetwork;
            }
        }
        return null;
    }

    public static boolean isNetworkSaved(WifiConfiguration config) {
        return config != null && config.networkId > -1;
    }

    public static WifiConfiguration getConfiguration(Context context, String ssid, int security) {
        WifiConfiguration config = getFromConfiguredNetworks(context, ssid, security);
        if (config != null) {
            return config;
        }
        WifiConfiguration config2 = new WifiConfiguration();
        setConfigSsid(config2, ssid);
        setConfigKeyManagementBySecurity(config2, security);
        return config2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0004, code lost:
        r1 = (android.net.wifi.WifiManager) r4.getSystemService("wifi");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean saveConfiguration(android.content.Context r4, android.net.wifi.WifiConfiguration r5) {
        /*
            r0 = 0
            if (r5 != 0) goto L_0x0004
            return r0
        L_0x0004:
            java.lang.String r1 = "wifi"
            java.lang.Object r1 = r4.getSystemService(r1)
            android.net.wifi.WifiManager r1 = (android.net.wifi.WifiManager) r1
            int r2 = r1.addNetwork(r5)
            r3 = -1
            if (r2 != r3) goto L_0x0014
            return r0
        L_0x0014:
            boolean r3 = r1.enableNetwork(r2, r0)
            if (r3 != 0) goto L_0x001b
            return r0
        L_0x001b:
            boolean r3 = r1.saveConfiguration()
            if (r3 != 0) goto L_0x0022
            return r0
        L_0x0022:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.connectivity.WifiConfigHelper.saveConfiguration(android.content.Context, android.net.wifi.WifiConfiguration):boolean");
    }

    private static WifiConfiguration getFromConfiguredNetworks(Context context, String ssid, int security) {
        List<WifiConfiguration> configuredNetworks = ((WifiManager) context.getSystemService("wifi")).getConfiguredNetworks();
        if (configuredNetworks == null) {
            return null;
        }
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            if (configuredNetwork != null && configuredNetwork.SSID != null && TextUtils.equals(WifiInfo.removeDoubleQuotes(configuredNetwork.SSID), ssid) && WifiSecurityUtil.getSecurity(configuredNetwork) == security) {
                return configuredNetwork;
            }
        }
        return null;
    }
}
