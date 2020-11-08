package com.android.tv.settings.connectivity;

import android.net.IpConfiguration;
import android.net.wifi.WifiManager;

public interface NetworkConfiguration {
    IpConfiguration getIpConfiguration();

    String getPrintableName();

    void save(WifiManager.ActionListener actionListener);

    void setIpConfiguration(IpConfiguration ipConfiguration);
}
