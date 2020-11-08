package com.android.tv.settings.connectivity;

import android.content.Context;
import android.net.IpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

class WifiConfig implements NetworkConfiguration {
    private WifiConfiguration mWifiConfiguration = new WifiConfiguration();
    private final WifiManager mWifiManager;

    WifiConfig(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public void setIpConfiguration(IpConfiguration configuration) {
        this.mWifiConfiguration.setIpConfiguration(configuration);
    }

    public IpConfiguration getIpConfiguration() {
        return this.mWifiConfiguration.getIpConfiguration();
    }

    public void save(WifiManager.ActionListener listener) {
        this.mWifiManager.save(this.mWifiConfiguration, listener);
    }

    public void load(int networkId) {
        this.mWifiConfiguration = WifiConfigHelper.getWifiConfiguration(this.mWifiManager, networkId);
    }

    public String getPrintableName() {
        return this.mWifiConfiguration.getPrintableSsid();
    }
}
