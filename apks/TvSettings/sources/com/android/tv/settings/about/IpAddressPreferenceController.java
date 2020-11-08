package com.android.tv.settings.about;

import android.content.Context;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractIpAddressPreferenceController;

public class IpAddressPreferenceController extends AbstractIpAddressPreferenceController {
    public IpAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
