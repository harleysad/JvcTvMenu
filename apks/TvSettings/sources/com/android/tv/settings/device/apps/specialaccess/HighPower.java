package com.android.tv.settings.device.apps.specialaccess;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.TwoStatePreference;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.specialaccess.ManageApplicationsController;
import com.mediatek.wwtv.tvcenter.util.KeyMap;

@Keep
public class HighPower extends SettingsPreferenceFragment implements ManageApplicationsController.Callback {
    private final ApplicationsState.AppFilter mFilter = new ApplicationsState.CompoundFilter(new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, ApplicationsState.FILTER_ALL_ENABLED), new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            info.extraInfo = Boolean.valueOf(HighPower.this.mPowerWhitelistBackend.isWhitelisted(info.info.packageName));
            return !ManageAppOp.shouldIgnorePackage(HighPower.this.getContext(), info.info.packageName);
        }
    });
    private ManageApplicationsController mManageApplicationsController;
    /* access modifiers changed from: private */
    public PowerWhitelistBackend mPowerWhitelistBackend;

    public int getMetricsCategory() {
        return KeyMap.KEYCODE_MTKIR_GREEN;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPowerWhitelistBackend = PowerWhitelistBackend.getInstance(context);
        this.mManageApplicationsController = new ManageApplicationsController(context, this, getLifecycle(), this.mFilter, ApplicationsState.ALPHA_COMPARATOR);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.manage_high_power, (String) null);
    }

    public void onResume() {
        super.onResume();
        this.mManageApplicationsController.updateAppList();
    }

    @NonNull
    public Preference bindPreference(@NonNull Preference preference, ApplicationsState.AppEntry entry) {
        TwoStatePreference switchPref = (SwitchPreference) preference;
        switchPref.setTitle((CharSequence) entry.label);
        switchPref.setKey(entry.info.packageName);
        switchPref.setIcon(entry.icon);
        if (this.mPowerWhitelistBackend.isSysWhitelisted(entry.info.packageName)) {
            switchPref.setChecked(false);
            switchPref.setEnabled(false);
        } else {
            switchPref.setEnabled(true);
            switchPref.setChecked(true ^ ((Boolean) entry.extraInfo).booleanValue());
            switchPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return HighPower.lambda$bindPreference$0(HighPower.this, preference, obj);
                }
            });
        }
        updateSummary(switchPref);
        return switchPref;
    }

    public static /* synthetic */ boolean lambda$bindPreference$0(HighPower highPower, Preference pref, Object newValue) {
        String pkg = pref.getKey();
        if (((Boolean) newValue).booleanValue()) {
            highPower.mPowerWhitelistBackend.removeApp(pkg);
        } else {
            highPower.mPowerWhitelistBackend.addApp(pkg);
        }
        highPower.updateSummary(pref);
        return true;
    }

    private void updateSummary(Preference preference) {
        String pkg = preference.getKey();
        if (this.mPowerWhitelistBackend.isSysWhitelisted(pkg)) {
            preference.setSummary((int) R.string.high_power_system);
        } else if (this.mPowerWhitelistBackend.isWhitelisted(pkg)) {
            preference.setSummary((int) R.string.high_power_on);
        } else {
            preference.setSummary((int) R.string.high_power_off);
        }
    }

    @NonNull
    public Preference createAppPreference() {
        return new SwitchPreference(getPreferenceManager().getContext());
    }

    @NonNull
    public Preference getEmptyPreference() {
        Preference empty = new Preference(getPreferenceManager().getContext());
        empty.setKey("empty");
        empty.setTitle((int) R.string.high_power_apps_empty);
        empty.setEnabled(false);
        return empty;
    }

    @NonNull
    public PreferenceGroup getAppPreferenceGroup() {
        return getPreferenceScreen();
    }
}
