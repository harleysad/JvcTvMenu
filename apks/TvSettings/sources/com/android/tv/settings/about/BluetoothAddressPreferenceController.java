package com.android.tv.settings.about;

import android.content.Context;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractBluetoothAddressPreferenceController;

public class BluetoothAddressPreferenceController extends AbstractBluetoothAddressPreferenceController {
    public BluetoothAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
