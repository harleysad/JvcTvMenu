package com.android.tv.settings.about;

import android.content.Context;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractUptimePreferenceController;

public class UptimePreferenceController extends AbstractUptimePreferenceController {
    public UptimePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
