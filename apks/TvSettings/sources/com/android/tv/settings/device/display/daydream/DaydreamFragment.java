package com.android.tv.settings.device.display.daydream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settingslib.dream.DreamBackend;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Keep
public class DaydreamFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int DEFAULT_DREAM_TIME_MS = 1800000;
    private static final int DEFAULT_SLEEP_TIME_MS = 10800000;
    private static final String DREAM_COMPONENT_NONE = "NONE";
    private static final String KEY_ACTIVE_DREAM = "activeDream";
    private static final String KEY_DREAM_NOW = "dreamNow";
    private static final String KEY_DREAM_TIME = "dreamTime";
    private static final String KEY_SLEEP_TIME = "sleepTime";
    private static final String PACKAGE_SCHEME = "package";
    private static final String TAG = "DaydreamFragment";
    private DreamBackend mBackend;
    private final Map<String, DreamBackend.DreamInfo> mDreamInfos = new ArrayMap();
    private final PackageReceiver mPackageReceiver = new PackageReceiver();

    public static DaydreamFragment newInstance() {
        return new DaydreamFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mBackend = new DreamBackend(getActivity());
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        refreshFromBackend();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme("package");
        getActivity().registerReceiver(this.mPackageReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mPackageReceiver);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.daydream, (String) null);
        ListPreference activeDreamPref = (ListPreference) findPreference(KEY_ACTIVE_DREAM);
        refreshActiveDreamPref(activeDreamPref);
        activeDreamPref.setOnPreferenceChangeListener(this);
        ListPreference dreamTimePref = (ListPreference) findPreference(KEY_DREAM_TIME);
        dreamTimePref.setValue(Integer.toString(getDreamTime()));
        dreamTimePref.setOnPreferenceChangeListener(this);
        ListPreference sleepTimePref = (ListPreference) findPreference(KEY_SLEEP_TIME);
        sleepTimePref.setValue(Integer.toString(getSleepTime()));
        sleepTimePref.setOnPreferenceChangeListener(this);
        findPreference(KEY_DREAM_NOW).setEnabled(this.mBackend.isEnabled());
    }

    private void refreshActiveDreamPref(ListPreference activeDreamPref) {
        List<DreamBackend.DreamInfo> infos = this.mBackend.getDreamInfos();
        CharSequence[] dreamEntries = new CharSequence[(infos.size() + 1)];
        CharSequence[] dreamEntryValues = new CharSequence[(infos.size() + 1)];
        refreshDreamInfoMap(infos, dreamEntries, dreamEntryValues);
        activeDreamPref.setEntries(dreamEntries);
        activeDreamPref.setEntryValues(dreamEntryValues);
        ComponentName currentDreamComponent = this.mBackend.getActiveDream();
        activeDreamPref.setValue((!this.mBackend.isEnabled() || currentDreamComponent == null) ? "NONE" : currentDreamComponent.toShortString());
    }

    private void refreshDreamInfoMap(List<DreamBackend.DreamInfo> infos, CharSequence[] listEntries, CharSequence[] listEntryValues) {
        this.mDreamInfos.clear();
        listEntries[0] = getString(R.string.device_daydreams_none);
        listEntryValues[0] = "NONE";
        int index = 1;
        for (DreamBackend.DreamInfo info : infos) {
            String componentNameString = info.componentName.toShortString();
            this.mDreamInfos.put(componentNameString, info);
            listEntries[index] = info.caption;
            listEntryValues[index] = componentNameString;
            index++;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0052  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(android.support.v7.preference.Preference r5, java.lang.Object r6) {
        /*
            r4 = this;
            java.lang.String r0 = r5.getKey()
            int r1 = r0.hashCode()
            r2 = -1216047760(0xffffffffb7849570, float:-1.5805213E-5)
            r3 = 1
            if (r1 == r2) goto L_0x002d
            r2 = -1076327267(0xffffffffbfd88c9d, float:-1.6917912)
            if (r1 == r2) goto L_0x0023
            r2 = -12733884(0xffffffffff3db244, float:-2.521497E38)
            if (r1 == r2) goto L_0x0019
            goto L_0x0037
        L_0x0019:
            java.lang.String r1 = "sleepTime"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0037
            r0 = 2
            goto L_0x0038
        L_0x0023:
            java.lang.String r1 = "activeDream"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0037
            r0 = 0
            goto L_0x0038
        L_0x002d:
            java.lang.String r1 = "dreamTime"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0037
            r0 = r3
            goto L_0x0038
        L_0x0037:
            r0 = -1
        L_0x0038:
            switch(r0) {
                case 0: goto L_0x0052;
                case 1: goto L_0x0047;
                case 2: goto L_0x003c;
                default: goto L_0x003b;
            }
        L_0x003b:
            goto L_0x0059
        L_0x003c:
            r0 = r6
            java.lang.String r0 = (java.lang.String) r0
            int r0 = java.lang.Integer.parseInt(r0)
            r4.setSleepTime(r0)
            goto L_0x0059
        L_0x0047:
            r0 = r6
            java.lang.String r0 = (java.lang.String) r0
            int r0 = java.lang.Integer.parseInt(r0)
            r4.setDreamTime(r0)
            goto L_0x0059
        L_0x0052:
            r0 = r6
            java.lang.String r0 = (java.lang.String) r0
            r4.setActiveDream(r0)
        L_0x0059:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.device.display.daydream.DaydreamFragment.onPreferenceChange(android.support.v7.preference.Preference, java.lang.Object):boolean");
    }

    private void setActiveDream(String componentNameString) {
        DreamBackend.DreamInfo dreamInfo = this.mDreamInfos.get(componentNameString);
        if (dreamInfo != null) {
            if (dreamInfo.settingsComponentName != null) {
                startActivity(new Intent().setComponent(dreamInfo.settingsComponentName));
            }
            if (!this.mBackend.isEnabled()) {
                this.mBackend.setEnabled(true);
            }
            if (!Objects.equals(this.mBackend.getActiveDream(), dreamInfo.componentName)) {
                this.mBackend.setActiveDream(dreamInfo.componentName);
            }
        } else if (this.mBackend.isEnabled()) {
            this.mBackend.setActiveDream((ComponentName) null);
            this.mBackend.setEnabled(false);
        }
    }

    private int getDreamTime() {
        return Settings.System.getInt(getActivity().getContentResolver(), "screen_off_timeout", DEFAULT_DREAM_TIME_MS);
    }

    private void setDreamTime(int ms) {
        Settings.System.putInt(getActivity().getContentResolver(), "screen_off_timeout", ms);
    }

    private int getSleepTime() {
        return Settings.Secure.getInt(getActivity().getContentResolver(), "sleep_timeout", DEFAULT_SLEEP_TIME_MS);
    }

    private void setSleepTime(int ms) {
        Settings.Secure.putInt(getActivity().getContentResolver(), "sleep_timeout", ms);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (((key.hashCode() == -731969581 && key.equals(KEY_DREAM_NOW)) ? (char) 0 : 65535) != 0) {
            return super.onPreferenceTreeClick(preference);
        }
        this.mBackend.startDreaming();
        return true;
    }

    /* access modifiers changed from: private */
    public void refreshFromBackend() {
        if (getActivity() == null) {
            Log.d(TAG, "No activity, not refreshing");
            return;
        }
        ListPreference activeDreamPref = (ListPreference) findPreference(KEY_ACTIVE_DREAM);
        if (activeDreamPref != null) {
            refreshActiveDreamPref(activeDreamPref);
        }
        ListPreference dreamTimePref = (ListPreference) findPreference(KEY_DREAM_TIME);
        if (dreamTimePref != null) {
            dreamTimePref.setValue(Integer.toString(getDreamTime()));
        }
        ListPreference sleepTimePref = (ListPreference) findPreference(KEY_SLEEP_TIME);
        if (sleepTimePref != null) {
            sleepTimePref.setValue(Integer.toString(getSleepTime()));
        }
        Preference dreamNowPref = findPreference(KEY_DREAM_NOW);
        if (dreamNowPref != null) {
            dreamNowPref.setEnabled(this.mBackend.isEnabled());
        }
    }

    public int getMetricsCategory() {
        return 47;
    }

    private class PackageReceiver extends BroadcastReceiver {
        private PackageReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            DaydreamFragment.this.refreshFromBackend();
        }
    }
}
