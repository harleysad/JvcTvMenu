package com.android.tv.settings.system.development;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

@Keep
public class InactiveApps extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private UsageStatsManager mUsageStats;

    public void onCreate(Bundle icicle) {
        this.mUsageStats = (UsageStatsManager) getActivity().getSystemService(UsageStatsManager.class);
        super.onCreate(icicle);
    }

    public void onResume() {
        super.onResume();
        init();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.inactive_apps_title);
        setPreferenceScreen(screen);
    }

    private void init() {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceScreen();
        screen.removeAll();
        screen.setOrderingAsAdded(false);
        PackageManager pm = getActivity().getPackageManager();
        Intent launcherIntent = new Intent("android.intent.action.MAIN");
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo app : pm.queryIntentActivities(launcherIntent, 0)) {
            String packageName = app.activityInfo.applicationInfo.packageName;
            Preference p = new Preference(themedContext);
            p.setTitle(app.loadLabel(pm));
            p.setIcon(app.loadIcon(pm));
            p.setKey(packageName);
            updateSummary(p);
            p.setOnPreferenceClickListener(this);
            screen.addPreference(p);
        }
    }

    private void updateSummary(Preference p) {
        int i;
        if (this.mUsageStats.isAppInactive(p.getKey())) {
            i = R.string.inactive_app_inactive_summary;
        } else {
            i = R.string.inactive_app_active_summary;
        }
        p.setSummary(i);
    }

    public boolean onPreferenceClick(Preference preference) {
        String packageName = preference.getKey();
        this.mUsageStats.setAppInactive(packageName, !this.mUsageStats.isAppInactive(packageName));
        updateSummary(preference);
        return false;
    }

    public int getMetricsCategory() {
        return 238;
    }
}
