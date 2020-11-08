package com.android.tv.settings.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.location.RecentLocationApps;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.AppManagementFragment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Keep
public class LocationFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String CURRENT_MODE_KEY = "CURRENT_MODE";
    private static final String KEY_LOCATION_MODE = "locationMode";
    private static final String LOCATION_MODE_OFF = "off";
    private static final String LOCATION_MODE_WIFI = "wifi";
    private static final String MODE_CHANGING_ACTION = "com.android.settings.location.MODE_CHANGING";
    private static final String NEW_MODE_KEY = "NEW_MODE";
    private static final String TAG = "LocationFragment";
    private ListPreference mLocationMode;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Log.isLoggable(LocationFragment.TAG, 3)) {
                Log.d(LocationFragment.TAG, "Received location mode change intent: " + intent);
            }
            LocationFragment.this.refreshLocationMode();
        }
    };

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);
        screen.setTitle((int) R.string.system_location);
        this.mLocationMode = new ListPreference(themedContext);
        screen.addPreference(this.mLocationMode);
        this.mLocationMode.setKey(KEY_LOCATION_MODE);
        this.mLocationMode.setPersistent(false);
        this.mLocationMode.setTitle((int) R.string.location_status);
        this.mLocationMode.setDialogTitle((int) R.string.location_status);
        this.mLocationMode.setSummary("%s");
        this.mLocationMode.setEntries(new CharSequence[]{getString(R.string.location_mode_wifi_description), getString(R.string.off)});
        this.mLocationMode.setEntryValues(new CharSequence[]{LOCATION_MODE_WIFI, "off"});
        this.mLocationMode.setOnPreferenceChangeListener(this);
        this.mLocationMode.setEnabled(!UserManager.get(getContext()).hasUserRestriction("no_share_location"));
        PreferenceCategory recentRequests = new PreferenceCategory(themedContext);
        screen.addPreference(recentRequests);
        recentRequests.setTitle((int) R.string.location_category_recent_location_requests);
        List<RecentLocationApps.Request> recentLocationRequests = new RecentLocationApps(themedContext).getAppList();
        List<Preference> recentLocationPrefs = new ArrayList<>(recentLocationRequests.size());
        for (RecentLocationApps.Request request : recentLocationRequests) {
            Preference pref = new Preference(themedContext);
            pref.setIcon(request.icon);
            pref.setTitle(request.label);
            BatteryManager batteryManager = (BatteryManager) getContext().getSystemService("batterymanager");
            if (batteryManager != null && !batteryManager.isCharging()) {
                if (request.isHighBattery) {
                    pref.setSummary((int) R.string.location_high_battery_use);
                } else {
                    pref.setSummary((int) R.string.location_low_battery_use);
                }
            }
            pref.setFragment(AppManagementFragment.class.getName());
            AppManagementFragment.prepareArgs(pref.getExtras(), request.packageName);
            recentLocationPrefs.add(pref);
        }
        if (recentLocationRequests.size() > 0) {
            addPreferencesSorted(recentLocationPrefs, recentRequests);
        } else {
            Preference banner = new Preference(themedContext);
            banner.setTitle((int) R.string.location_no_recent_apps);
            banner.setSelectable(false);
            recentRequests.addPreference(banner);
        }
        setPreferenceScreen(screen);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(this.mReceiver, new IntentFilter("android.location.MODE_CHANGED"));
        refreshLocationMode();
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.mReceiver);
    }

    private void addPreferencesSorted(List<Preference> prefs, PreferenceGroup container) {
        prefs.sort(Comparator.comparing($$Lambda$LocationFragment$bd6aiHN5izQdXVNPYUBJ02EbSuI.INSTANCE));
        for (Preference entry : prefs) {
            container.addPreference(entry);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!TextUtils.equals(preference.getKey(), KEY_LOCATION_MODE)) {
            return true;
        }
        int mode = 0;
        if (TextUtils.equals((CharSequence) newValue, LOCATION_MODE_WIFI)) {
            mode = 3;
        } else if (TextUtils.equals((CharSequence) newValue, "off")) {
            mode = 0;
        } else {
            Log.wtf(TAG, "Tried to set unknown location mode!");
        }
        writeLocationMode(mode);
        refreshLocationMode();
        return true;
    }

    private void writeLocationMode(int mode) {
        int currentMode = Settings.Secure.getInt(getActivity().getContentResolver(), "location_mode", 0);
        Intent intent = new Intent(MODE_CHANGING_ACTION);
        intent.putExtra(CURRENT_MODE_KEY, currentMode);
        intent.putExtra(NEW_MODE_KEY, mode);
        getActivity().sendBroadcast(intent, "android.permission.WRITE_SECURE_SETTINGS");
        Settings.Secure.putInt(getActivity().getContentResolver(), "location_mode", mode);
    }

    /* access modifiers changed from: private */
    public void refreshLocationMode() {
        if (this.mLocationMode != null) {
            int mode = Settings.Secure.getInt(getActivity().getContentResolver(), "location_mode", 0);
            if (mode == 3 || mode == 2) {
                this.mLocationMode.setValue(LOCATION_MODE_WIFI);
            } else if (mode == 0) {
                this.mLocationMode.setValue("off");
            } else if (mode == 1) {
                writeLocationMode(0);
                this.mLocationMode.setValue("off");
            } else {
                Log.d(TAG, "Unknown location mode: " + mode);
            }
        }
    }

    public int getMetricsCategory() {
        return 63;
    }
}
