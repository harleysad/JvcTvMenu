package com.android.tv.settings.device.apps;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.IntentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.settingslib.SliceBroadcastRelay;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecentAppsPreferenceController extends AbstractPreferenceController implements Comparator<UsageStats> {
    private static final String KEY_PREF_CATEGORY = "recently_used_apps_category";
    @VisibleForTesting
    static final String KEY_SEE_ALL = "see_all_apps";
    private static final int SHOW_RECENT_APP_COUNT = 5;
    private static final Set<String> SKIP_SYSTEM_PACKAGES = new ArraySet();
    private static final String TAG = "RecentAppsPreferenceController";
    private final ApplicationsState mApplicationsState;
    private Calendar mCal;
    private PreferenceCategory mCategory;
    private final IconDrawableFactory mIconDrawableFactory;
    private final PackageManager mPm;
    private List<UsageStats> mStats;
    private final UsageStatsManager mUsageStatsManager;
    private final int mUserId;

    static {
        SKIP_SYSTEM_PACKAGES.addAll(Arrays.asList(new String[]{"android", "com.android.tv.settings", SliceBroadcastRelay.SYSTEMUI_PACKAGE, "com.android.providers.calendar", "com.android.providers.media"}));
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public RecentAppsPreferenceController(Context context, Application app) {
        this(context, app == null ? null : ApplicationsState.getInstance(app));
    }

    @VisibleForTesting(otherwise = 5)
    RecentAppsPreferenceController(Context context, ApplicationsState appState) {
        super(context);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(context);
        this.mUserId = UserHandle.myUserId();
        this.mPm = context.getPackageManager();
        this.mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        this.mApplicationsState = appState;
    }

    public boolean isAvailable() {
        return true;
    }

    public String getPreferenceKey() {
        return KEY_PREF_CATEGORY;
    }

    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        this.mCategory = (PreferenceCategory) screen.findPreference(getPreferenceKey());
        refreshUi(this.mCategory.getContext());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void refreshUi(Context prefContext) {
        reloadData();
        List<UsageStats> recentApps = getDisplayableRecentAppList();
        if (recentApps == null || recentApps.isEmpty()) {
            displayOnlyAllApps();
        } else {
            displayRecentApps(prefContext, recentApps);
        }
    }

    private void displayOnlyAllApps() {
        this.mCategory.setVisible(false);
        for (int i = this.mCategory.getPreferenceCount() - 1; i >= 0; i--) {
            Preference pref = this.mCategory.getPreference(i);
            if (!TextUtils.equals(pref.getKey(), KEY_SEE_ALL)) {
                this.mCategory.removePreference(pref);
            }
        }
    }

    private void displayRecentApps(Context prefContext, List<UsageStats> recentApps) {
        this.mCategory.setVisible(true);
        Map<String, Preference> appPreferences = new ArrayMap<>();
        int prefCount = this.mCategory.getPreferenceCount();
        for (int i = 0; i < prefCount; i++) {
            Preference pref = this.mCategory.getPreference(i);
            String key = pref.getKey();
            if (!TextUtils.equals(key, KEY_SEE_ALL)) {
                appPreferences.put(key, pref);
            }
        }
        int recentAppsCount = recentApps.size();
        for (int i2 = 0; i2 < recentAppsCount; i2++) {
            UsageStats stat = recentApps.get(i2);
            String pkgName = stat.getPackageName();
            ApplicationsState.AppEntry appEntry = this.mApplicationsState.getEntry(pkgName, this.mUserId);
            if (appEntry == null) {
                Context context = prefContext;
            } else {
                boolean rebindPref = true;
                Preference pref2 = appPreferences.remove(pkgName);
                if (pref2 == null) {
                    pref2 = new Preference(prefContext);
                    rebindPref = false;
                } else {
                    Context context2 = prefContext;
                }
                pref2.setKey(pkgName);
                pref2.setTitle((CharSequence) appEntry.label);
                pref2.setIcon(this.mIconDrawableFactory.getBadgedIcon(appEntry.info));
                pref2.setSummary(StringUtil.formatRelativeTime(this.mContext, (double) (System.currentTimeMillis() - stat.getLastTimeUsed()), false));
                pref2.setOrder(i2);
                AppManagementFragment.prepareArgs(pref2.getExtras(), pkgName);
                pref2.setFragment(AppManagementFragment.class.getName());
                if (!rebindPref) {
                    this.mCategory.addPreference(pref2);
                }
            }
        }
        Context context3 = prefContext;
        List<UsageStats> list = recentApps;
        for (Preference unusedPrefs : appPreferences.values()) {
            this.mCategory.removePreference(unusedPrefs);
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshUi(this.mCategory.getContext());
    }

    public final int compare(UsageStats a, UsageStats b) {
        return Long.compare(b.getLastTimeUsed(), a.getLastTimeUsed());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void reloadData() {
        this.mCal = Calendar.getInstance();
        this.mCal.add(6, -1);
        this.mStats = this.mUsageStatsManager.queryUsageStats(4, this.mCal.getTimeInMillis(), System.currentTimeMillis());
    }

    private List<UsageStats> getDisplayableRecentAppList() {
        List<UsageStats> recentApps = new ArrayList<>();
        Map<String, UsageStats> map = new ArrayMap<>();
        int statCount = this.mStats.size();
        for (int i = 0; i < statCount; i++) {
            UsageStats pkgStats = this.mStats.get(i);
            if (shouldIncludePkgInRecents(pkgStats)) {
                String pkgName = pkgStats.getPackageName();
                UsageStats existingStats = map.get(pkgName);
                if (existingStats == null) {
                    map.put(pkgName, pkgStats);
                } else {
                    existingStats.add(pkgStats);
                }
            }
        }
        List<UsageStats> packageStats = new ArrayList<>();
        packageStats.addAll(map.values());
        Collections.sort(packageStats, this);
        int count = 0;
        for (UsageStats stat : packageStats) {
            if (this.mApplicationsState.getEntry(stat.getPackageName(), this.mUserId) != null) {
                recentApps.add(stat);
                count++;
                if (count >= 5) {
                    break;
                }
            }
        }
        return recentApps;
    }

    private boolean shouldIncludePkgInRecents(UsageStats stat) {
        String pkgName = stat.getPackageName();
        if (stat.getLastTimeUsed() < this.mCal.getTimeInMillis()) {
            Log.d(TAG, "Invalid timestamp, skipping " + pkgName);
            return false;
        } else if (SKIP_SYSTEM_PACKAGES.contains(pkgName)) {
            Log.d(TAG, "System package, skipping " + pkgName);
            return false;
        } else {
            if (this.mPm.resolveActivity(new Intent().addCategory(IntentCompat.CATEGORY_LEANBACK_LAUNCHER).setPackage(pkgName), 0) != null) {
                return true;
            }
            ApplicationsState.AppEntry appEntry = this.mApplicationsState.getEntry(pkgName, this.mUserId);
            if (appEntry != null && appEntry.info != null && AppUtils.isInstant(appEntry.info)) {
                return true;
            }
            Log.d(TAG, "Not a user visible or instant app, skipping " + pkgName);
            return false;
        }
    }
}
