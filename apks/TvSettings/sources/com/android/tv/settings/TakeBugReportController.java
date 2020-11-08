package com.android.tv.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.preference.Preference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.tv.settings.system.development.DevelopmentFragment;

public class TakeBugReportController extends AbstractPreferenceController {
    static final String KEY_TAKE_BUG_REPORT = "take_bug_report";

    public TakeBugReportController(Context context) {
        super(context);
    }

    public boolean isAvailable() {
        return Build.TYPE.equals("userdebug") && this.mContext != null && this.mContext.getResources() != null && this.mContext.getResources().getBoolean(R.bool.config_quick_settings_show_take_bugreport);
    }

    public String getPreferenceKey() {
        return KEY_TAKE_BUG_REPORT;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_TAKE_BUG_REPORT.equals(preference.getKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        DevelopmentFragment.captureBugReport((Activity) this.mContext);
        return true;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (KEY_TAKE_BUG_REPORT.equals(preference.getKey())) {
            preference.setTitle(17039591);
            preference.setIcon((int) R.drawable.ic_bug_report);
        }
    }
}
