package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModel;
import com.android.settingslib.wifi.WifiTracker;

public class NetworkListInfo extends ViewModel {
    private static final int NETWORK_REFRESH_BUFFER_DURATION = 5000;
    private long mNextNetworkRefreshTime;
    private boolean mShowSkipNetwork;
    private WifiTracker mWifiTracker;

    public boolean isShowSkipNetwork() {
        return this.mShowSkipNetwork;
    }

    public void setShowSkipNetwork(boolean showSkipNetwork) {
        this.mShowSkipNetwork = showSkipNetwork;
    }

    public long getNextNetworkRefreshTime() {
        return this.mNextNetworkRefreshTime;
    }

    public void updateNextNetworkRefreshTime() {
        this.mNextNetworkRefreshTime = System.currentTimeMillis() + 5000;
    }

    public void initNetworkRefreshTime() {
        this.mNextNetworkRefreshTime = System.currentTimeMillis();
    }

    public WifiTracker getWifiTracker() {
        return this.mWifiTracker;
    }

    public void setWifiTracker(WifiTracker wifiTracker) {
        this.mWifiTracker = wifiTracker;
    }
}
