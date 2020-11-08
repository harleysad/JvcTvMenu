package com.android.tv.settings;

import android.content.Context;
import com.android.settingslib.core.AbstractPreferenceController;

public class NopePreferenceController extends AbstractPreferenceController {
    private final String mKey;

    public NopePreferenceController(Context context, String key) {
        super(context);
        this.mKey = key;
    }

    public boolean isAvailable() {
        return false;
    }

    public String getPreferenceKey() {
        return this.mKey;
    }
}
