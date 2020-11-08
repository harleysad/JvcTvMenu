package com.android.tv.settings.device.apps.specialaccess;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ManageApplicationsController implements LifecycleObserver {
    public static final String HEADER_KEY = "header";
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks = new ApplicationsState.Callbacks() {
        public void onRunningStateChanged(boolean running) {
            ManageApplicationsController.this.updateAppList();
        }

        public void onPackageListChanged() {
            ManageApplicationsController.this.updateAppList();
        }

        public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
            ManageApplicationsController.this.updateAppList(apps);
        }

        public void onPackageIconChanged() {
            ManageApplicationsController.this.updateAppList();
        }

        public void onPackageSizeChanged(String packageName) {
            ManageApplicationsController.this.updateAppList();
        }

        public void onAllSizesComputed() {
            ManageApplicationsController.this.updateAppList();
        }

        public void onLauncherInfoChanged() {
            ManageApplicationsController.this.updateAppList();
        }

        public void onLoadEntriesCompleted() {
            ManageApplicationsController.this.updateAppList();
        }
    };
    private ApplicationsState mApplicationsState;
    private final Callback mCallback;
    private final Comparator<ApplicationsState.AppEntry> mComparator;
    private final ApplicationsState.AppFilter mFilter;
    private final Lifecycle mLifecycle;

    public interface Callback {
        @NonNull
        Preference bindPreference(@NonNull Preference preference, ApplicationsState.AppEntry appEntry);

        @NonNull
        Preference createAppPreference();

        @NonNull
        PreferenceGroup getAppPreferenceGroup();

        @NonNull
        Preference getEmptyPreference();
    }

    public ManageApplicationsController(@NonNull Context context, @NonNull Callback callback, @NonNull Lifecycle lifecycle, ApplicationsState.AppFilter filter, Comparator<ApplicationsState.AppEntry> comparator) {
        this.mCallback = callback;
        lifecycle.addObserver(this);
        this.mLifecycle = lifecycle;
        this.mFilter = filter;
        this.mComparator = comparator;
        this.mApplicationsState = ApplicationsState.getInstance((Application) context.getApplicationContext());
        this.mAppSession = this.mApplicationsState.newSession(this.mAppSessionCallbacks, this.mLifecycle);
        updateAppList();
    }

    public void updateAppList() {
        ArrayList<ApplicationsState.AppEntry> apps = this.mAppSession.rebuild(new ApplicationsState.CompoundFilter(this.mFilter, ApplicationsState.FILTER_NOT_HIDE), this.mComparator);
        if (apps != null) {
            updateAppList(apps);
        }
    }

    /* access modifiers changed from: private */
    public void updateAppList(ArrayList<ApplicationsState.AppEntry> apps) {
        PreferenceGroup group = this.mCallback.getAppPreferenceGroup();
        List<Preference> newList = new ArrayList<>(apps.size() + 1);
        Iterator<ApplicationsState.AppEntry> it = apps.iterator();
        while (it.hasNext()) {
            ApplicationsState.AppEntry entry = it.next();
            String packageName = entry.info.packageName;
            this.mApplicationsState.ensureIcon(entry);
            Preference recycle = group.findPreference(packageName);
            if (recycle == null) {
                recycle = this.mCallback.createAppPreference();
            }
            newList.add(this.mCallback.bindPreference(recycle, entry));
        }
        Preference header = group.findPreference(HEADER_KEY);
        group.removeAll();
        if (header != null) {
            group.addPreference(header);
        }
        if (newList.size() > 0) {
            for (Preference prefToAdd : newList) {
                group.addPreference(prefToAdd);
            }
            return;
        }
        group.addPreference(this.mCallback.getEmptyPreference());
    }
}
