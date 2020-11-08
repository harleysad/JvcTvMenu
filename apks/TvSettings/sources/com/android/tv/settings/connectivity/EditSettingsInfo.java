package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModel;

public class EditSettingsInfo extends ViewModel {
    private NetworkConfiguration mNetworkConfiguration;

    public NetworkConfiguration getNetworkConfiguration() {
        return this.mNetworkConfiguration;
    }

    public void setNetworkConfiguration(NetworkConfiguration config) {
        this.mNetworkConfiguration = config;
    }
}
