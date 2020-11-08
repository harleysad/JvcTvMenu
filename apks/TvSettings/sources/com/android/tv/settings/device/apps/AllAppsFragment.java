package com.android.tv.settings.device.apps;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.utils.HiddenApplicationsConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Keep
public class AllAppsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    /* access modifiers changed from: private */
    public static final ApplicationsState.AppFilter FILTER_DISABLED = new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            return info.info != null && (info.info.enabledSetting == 2 || info.info.enabledSetting == 3 || (info.info.enabledSetting == 0 && !info.info.enabled));
        }
    };
    /* access modifiers changed from: private */
    public static final ApplicationsState.AppFilter FILTER_INSTALLED = new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            return !AllAppsFragment.FILTER_DISABLED.filterApp(info) && info.info != null && info.info.enabled && info.hasLauncherEntry && info.launcherEntryEnabled;
        }
    };
    private static final ApplicationsState.AppFilter FILTER_OTHER = new ApplicationsState.AppFilter() {
        public void init() {
        }

        public boolean filterApp(ApplicationsState.AppEntry info) {
            return !AllAppsFragment.FILTER_INSTALLED.filterApp(info) && !AllAppsFragment.FILTER_DISABLED.filterApp(info);
        }
    };
    private static final String KEY_SHOW_OTHER_APPS = "ShowOtherApps";
    private static final int SESSION_FLAGS = 23;
    private static final String TAG = "AllAppsFragment";
    private ApplicationsState mApplicationsState;
    /* access modifiers changed from: private */
    public PreferenceGroup mDisabledPreferenceGroup;
    private ApplicationsState.AppFilter mFilterDisabled;
    private ApplicationsState.AppFilter mFilterInstalled;
    private ApplicationsState.AppFilter mFilterOther;
    private final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public PreferenceGroup mInstalledPreferenceGroup;
    /* access modifiers changed from: private */
    public PreferenceGroup mOtherPreferenceGroup;
    /* access modifiers changed from: private */
    public long mRunAt = Long.MIN_VALUE;
    private ApplicationsState.Session mSessionDisabled;
    private ApplicationsState.Session mSessionInstalled;
    private ApplicationsState.Session mSessionOther;
    /* access modifiers changed from: private */
    public Preference mShowOtherApps;
    /* access modifiers changed from: private */
    public final Map<PreferenceGroup, ArrayList<ApplicationsState.AppEntry>> mUpdateMap = new ArrayMap(3);
    private final Runnable mUpdateRunnable = new Runnable() {
        public void run() {
            for (PreferenceGroup group : AllAppsFragment.this.mUpdateMap.keySet()) {
                AllAppsFragment.this.updateAppListInternal(group, (ArrayList) AllAppsFragment.this.mUpdateMap.get(group));
            }
            AllAppsFragment.this.mUpdateMap.clear();
            long unused = AllAppsFragment.this.mRunAt = 0;
        }
    };

    public static void prepareArgs(Bundle b, String volumeUuid, String volumeName) {
        b.putString("volumeUuid", volumeUuid);
        b.putString("volumeName", volumeName);
    }

    public static AllAppsFragment newInstance(String volumeUuid, String volumeName) {
        Bundle b = new Bundle(2);
        prepareArgs(b, volumeUuid, volumeName);
        AllAppsFragment f = new AllAppsFragment();
        f.setArguments(b);
        return f;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        String volumeUuid = getArguments().getString("volumeUuid");
        String volumeName = getArguments().getString("volumeName");
        if (!TextUtils.isEmpty(volumeUuid) || !TextUtils.isEmpty(volumeName)) {
            ApplicationsState.AppFilter volumeFilter = new ApplicationsState.VolumeFilter(volumeUuid);
            this.mFilterInstalled = new ApplicationsState.CompoundFilter(FILTER_INSTALLED, volumeFilter);
            this.mFilterDisabled = new ApplicationsState.CompoundFilter(FILTER_DISABLED, volumeFilter);
            this.mFilterOther = new ApplicationsState.CompoundFilter(FILTER_OTHER, volumeFilter);
        } else {
            this.mFilterInstalled = FILTER_INSTALLED;
            this.mFilterDisabled = FILTER_DISABLED;
            this.mFilterOther = FILTER_OTHER;
        }
        this.mSessionInstalled = this.mApplicationsState.newSession(new RowUpdateCallbacks() {
            /* access modifiers changed from: protected */
            public void doRebuild() {
                AllAppsFragment.this.rebuildInstalled();
            }

            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
                AllAppsFragment.this.updateAppList(AllAppsFragment.this.mInstalledPreferenceGroup, apps);
            }
        }, getLifecycle());
        this.mSessionInstalled.setSessionFlags(23);
        this.mSessionDisabled = this.mApplicationsState.newSession(new RowUpdateCallbacks() {
            /* access modifiers changed from: protected */
            public void doRebuild() {
                AllAppsFragment.this.rebuildDisabled();
            }

            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
                AllAppsFragment.this.updateAppList(AllAppsFragment.this.mDisabledPreferenceGroup, apps);
            }
        }, getLifecycle());
        this.mSessionDisabled.setSessionFlags(23);
        this.mSessionOther = this.mApplicationsState.newSession(new RowUpdateCallbacks() {
            /* access modifiers changed from: protected */
            public void doRebuild() {
                if (!AllAppsFragment.this.mShowOtherApps.isVisible()) {
                    AllAppsFragment.this.rebuildOther();
                }
            }

            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
                AllAppsFragment.this.updateAppList(AllAppsFragment.this.mOtherPreferenceGroup, apps);
            }
        }, getLifecycle());
        this.mSessionOther.setSessionFlags(23);
        rebuildInstalled();
        rebuildDisabled();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.all_apps, (String) null);
        this.mInstalledPreferenceGroup = (PreferenceGroup) findPreference("InstalledPreferenceGroup");
        this.mDisabledPreferenceGroup = (PreferenceGroup) findPreference("DisabledPreferenceGroup");
        this.mOtherPreferenceGroup = (PreferenceGroup) findPreference("OtherPreferenceGroup");
        this.mOtherPreferenceGroup.setVisible(false);
        this.mShowOtherApps = findPreference(KEY_SHOW_OTHER_APPS);
        this.mShowOtherApps.setOnPreferenceClickListener(this);
        this.mShowOtherApps.setVisible(TextUtils.isEmpty(getArguments().getString("volumeUuid")));
    }

    /* access modifiers changed from: private */
    public void rebuildInstalled() {
        ArrayList<ApplicationsState.AppEntry> apps = this.mSessionInstalled.rebuild(this.mFilterInstalled, ApplicationsState.ALPHA_COMPARATOR);
        if (apps != null) {
            updateAppList(this.mInstalledPreferenceGroup, apps);
        }
    }

    /* access modifiers changed from: private */
    public void rebuildDisabled() {
        ArrayList<ApplicationsState.AppEntry> apps = this.mSessionDisabled.rebuild(this.mFilterDisabled, ApplicationsState.ALPHA_COMPARATOR);
        if (apps != null) {
            updateAppList(this.mDisabledPreferenceGroup, apps);
        }
    }

    /* access modifiers changed from: private */
    public void rebuildOther() {
        ArrayList<ApplicationsState.AppEntry> apps = this.mSessionOther.rebuild(this.mFilterOther, ApplicationsState.ALPHA_COMPARATOR);
        if (apps != null) {
            updateAppList(this.mOtherPreferenceGroup, apps);
        }
    }

    private void filterPartnerHiddenApps(ArrayList<ApplicationsState.AppEntry> entries) {
        ArrayList<ApplicationsState.AppEntry> removed = new ArrayList<>();
        if (entries == null) {
            Log.d(TAG, "entries null");
            return;
        }
        ArrayList<String> mHiddenApps = HiddenApplicationsConfig.getInstance(getContext()).getPackageList();
        Iterator<ApplicationsState.AppEntry> it = entries.iterator();
        while (it.hasNext()) {
            ApplicationsState.AppEntry entry = it.next();
            if (mHiddenApps.contains(entry.info.packageName)) {
                removed.add(entry);
            }
        }
        Iterator<ApplicationsState.AppEntry> it2 = removed.iterator();
        while (it2.hasNext()) {
            entries.remove(it2.next());
        }
    }

    /* access modifiers changed from: private */
    public void updateAppList(PreferenceGroup group, ArrayList<ApplicationsState.AppEntry> entries) {
        if (group == null) {
            Log.d(TAG, "Not updating list for null group");
            return;
        }
        filterPartnerHiddenApps(entries);
        this.mUpdateMap.put(group, entries);
        if (this.mRunAt == Long.MIN_VALUE) {
            this.mHandler.removeCallbacks(this.mUpdateRunnable);
            this.mHandler.post(this.mUpdateRunnable);
            return;
        }
        if (this.mRunAt == 0) {
            this.mRunAt = SystemClock.uptimeMillis() + 1000;
        }
        int delay = (int) (this.mRunAt - SystemClock.uptimeMillis());
        int delay2 = delay < 0 ? 0 : delay;
        this.mHandler.removeCallbacks(this.mUpdateRunnable);
        this.mHandler.postDelayed(this.mUpdateRunnable, (long) delay2);
    }

    /* access modifiers changed from: private */
    public void updateAppListInternal(PreferenceGroup group, ArrayList<ApplicationsState.AppEntry> entries) {
        boolean z = false;
        if (entries != null) {
            Set<String> touched = new ArraySet<>(entries.size());
            Iterator<ApplicationsState.AppEntry> it = entries.iterator();
            while (it.hasNext()) {
                ApplicationsState.AppEntry entry = it.next();
                String packageName = entry.info.packageName;
                Preference recycle = group.findPreference(packageName);
                if (recycle == null) {
                    recycle = new Preference(getPreferenceManager().getContext());
                }
                group.addPreference(bindPreference(recycle, entry));
                touched.add(packageName);
            }
            int i = 0;
            while (i < group.getPreferenceCount()) {
                Preference pref = group.getPreference(i);
                if (touched.contains(pref.getKey())) {
                    i++;
                } else {
                    group.removePreference(pref);
                }
            }
        }
        PreferenceGroup preferenceGroup = this.mDisabledPreferenceGroup;
        if (this.mDisabledPreferenceGroup.getPreferenceCount() > 0) {
            z = true;
        }
        preferenceGroup.setVisible(z);
    }

    private Preference bindPreference(@NonNull Preference preference, ApplicationsState.AppEntry entry) {
        preference.setKey(entry.info.packageName);
        entry.ensureLabel(getContext());
        preference.setTitle((CharSequence) entry.label);
        preference.setSummary((CharSequence) entry.sizeStr);
        preference.setFragment(AppManagementFragment.class.getName());
        AppManagementFragment.prepareArgs(preference.getExtras(), entry.info.packageName);
        preference.setIcon(entry.icon);
        return preference;
    }

    public boolean onPreferenceClick(Preference preference) {
        if (!KEY_SHOW_OTHER_APPS.equals(preference.getKey())) {
            return false;
        }
        showOtherApps();
        return true;
    }

    private void showOtherApps() {
        this.mShowOtherApps.setVisible(false);
        this.mOtherPreferenceGroup.setVisible(true);
        rebuildOther();
    }

    private abstract class RowUpdateCallbacks implements ApplicationsState.Callbacks {
        /* access modifiers changed from: protected */
        public abstract void doRebuild();

        private RowUpdateCallbacks() {
        }

        public void onRunningStateChanged(boolean running) {
            doRebuild();
        }

        public void onPackageListChanged() {
            doRebuild();
        }

        public void onPackageIconChanged() {
            doRebuild();
        }

        public void onPackageSizeChanged(String packageName) {
            doRebuild();
        }

        public void onAllSizesComputed() {
            doRebuild();
        }

        public void onLauncherInfoChanged() {
            doRebuild();
        }

        public void onLoadEntriesCompleted() {
            doRebuild();
        }
    }

    public int getMetricsCategory() {
        return 65;
    }
}
