package com.android.tv.settings.connectivity;

import android.content.Context;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.wifi.WifiManager;
import com.android.tv.settings.R;

class EthernetConfig implements NetworkConfiguration {
    private final EthernetManager mEthernetManager;
    private String mInterfaceName;
    private IpConfiguration mIpConfiguration = new IpConfiguration();
    private final String mName;

    EthernetConfig(Context context) {
        this.mEthernetManager = (EthernetManager) context.getSystemService("ethernet");
        this.mName = context.getResources().getString(R.string.connectivity_ethernet);
    }

    public void setIpConfiguration(IpConfiguration configuration) {
        this.mIpConfiguration = configuration;
    }

    public IpConfiguration getIpConfiguration() {
        return this.mIpConfiguration;
    }

    public void save(WifiManager.ActionListener listener) {
        if (this.mInterfaceName != null) {
            this.mEthernetManager.setConfiguration(this.mInterfaceName, this.mIpConfiguration);
        }
        if (listener != null) {
            listener.onSuccess();
        }
    }

    public void load() {
        String[] ifaces = this.mEthernetManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            this.mInterfaceName = ifaces[0];
            this.mIpConfiguration = this.mEthernetManager.getConfiguration(this.mInterfaceName);
        }
    }

    public String getPrintableName() {
        return this.mName;
    }
}
