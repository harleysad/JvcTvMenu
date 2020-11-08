package com.android.tv.settings.about;

import android.content.Context;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractWifiMacAddressPreferenceController;

public class WifiMacAddressPreferenceController extends AbstractWifiMacAddressPreferenceController {
    public WifiMacAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
