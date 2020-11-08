package com.android.settingslib.wifi;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.wifi.WifiTracker;

public class WifiTrackerFactory {
    private static WifiTracker sTestingWifiTracker;

    @Keep
    public static void setTestingWifiTracker(WifiTracker tracker) {
        sTestingWifiTracker = tracker;
    }

    public static WifiTracker create(Context context, WifiTracker.WifiListener wifiListener, @NonNull Lifecycle lifecycle, boolean includeSaved, boolean includeScans) {
        if (sTestingWifiTracker != null) {
            return sTestingWifiTracker;
        }
        return new WifiTracker(context, wifiListener, lifecycle, includeSaved, includeScans);
    }
}
