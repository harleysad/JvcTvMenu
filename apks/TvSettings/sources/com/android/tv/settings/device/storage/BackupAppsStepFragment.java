package com.android.tv.settings.device.storage;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import com.android.tv.settings.device.apps.MoveAppActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BackupAppsStepFragment extends GuidedStepFragment implements ApplicationsState.Callbacks {
    private static final int ACTION_BACKUP_APP_BASE = 100;
    private static final int ACTION_MIGRATE_DATA = 1;
    private static final int ACTION_NO_APPS = 0;
    private ApplicationsState.AppFilter mAppFilter;
    private ApplicationsState mApplicationsState;
    private final List<ApplicationsState.AppEntry> mEntries = new ArrayList();
    /* access modifiers changed from: private */
    public IconLoaderTask mIconLoaderTask;
    /* access modifiers changed from: private */
    public final Map<String, Drawable> mIconMap = new ArrayMap();
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    private ApplicationsState.Session mSession;
    private StorageManager mStorageManager;
    private String mVolumeId;

    public static BackupAppsStepFragment newInstance(String volumeId) {
        BackupAppsStepFragment fragment = new BackupAppsStepFragment();
        Bundle b = new Bundle(1);
        b.putString("android.os.storage.extra.VOLUME_ID", volumeId);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mPackageManager = getActivity().getPackageManager();
        this.mStorageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        this.mVolumeId = getArguments().getString("android.os.storage.extra.VOLUME_ID");
        VolumeInfo info = this.mStorageManager.findVolumeById(this.mVolumeId);
        if (info != null) {
            this.mAppFilter = new ApplicationsState.VolumeFilter(info.getFsUuid());
        } else {
            if (!getFragmentManager().popBackStackImmediate()) {
                getActivity().finish();
            }
            this.mAppFilter = new ApplicationsState.AppFilter() {
                public void init() {
                }

                public boolean filterApp(ApplicationsState.AppEntry info) {
                    return false;
                }
            };
        }
        this.mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        this.mSession = this.mApplicationsState.newSession(this);
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        this.mSession.onResume();
        updateActions();
    }

    public void onPause() {
        super.onPause();
        this.mSession.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mSession.onDestroy();
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title;
        VolumeInfo volumeInfo = this.mStorageManager.findVolumeById(this.mVolumeId);
        String volumeDesc = this.mStorageManager.getBestVolumeDescription(volumeInfo);
        if (TextUtils.equals(this.mPackageManager.getPrimaryStorageCurrentVolume().getId(), volumeInfo.getId())) {
            title = getString(R.string.storage_wizard_back_up_apps_and_data_title, new Object[]{volumeDesc});
        } else {
            title = getString(R.string.storage_wizard_back_up_apps_title, new Object[]{volumeDesc});
        }
        return new GuidanceStylist.Guidance(title, "", "", getActivity().getDrawable(R.drawable.ic_storage_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        List<ApplicationsState.AppEntry> entries = this.mSession.rebuild(this.mAppFilter, ApplicationsState.ALPHA_COMPARATOR);
        if (entries != null) {
            actions.addAll(getAppActions(true, entries));
        }
    }

    /* access modifiers changed from: private */
    public List<GuidedAction> getAppActions(boolean refreshIcons, List<ApplicationsState.AppEntry> entries) {
        List<GuidedAction> actions = new ArrayList<>(entries.size() + 1);
        boolean showMigrate = false;
        VolumeInfo currentExternal = this.mPackageManager.getPrimaryStorageCurrentVolume();
        if (currentExternal != null && TextUtils.equals(currentExternal.getId(), this.mVolumeId)) {
            Iterator<VolumeInfo> it = this.mPackageManager.getPrimaryStorageCandidateVolumes().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (!TextUtils.equals(it.next().getId(), this.mVolumeId)) {
                        showMigrate = true;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (showMigrate) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((int) R.string.storage_migrate_away)).build());
        }
        int index = 100;
        for (ApplicationsState.AppEntry entry : entries) {
            ApplicationInfo info = entry.info;
            entry.ensureLabel(getActivity());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((CharSequence) entry.label)).description((CharSequence) entry.sizeStr)).icon(this.mIconMap.get(info.packageName))).id((long) index)).build());
            index++;
        }
        this.mEntries.clear();
        this.mEntries.addAll(entries);
        if (refreshIcons) {
            if (this.mIconLoaderTask != null) {
                this.mIconLoaderTask.cancel(true);
            }
            this.mIconLoaderTask = new IconLoaderTask(entries);
            this.mIconLoaderTask.execute(new Void[0]);
        }
        if (actions.size() == 0) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(0)).title((int) R.string.storage_no_apps)).build());
        }
        return actions;
    }

    private void updateActions() {
        List<ApplicationsState.AppEntry> entries = this.mSession.rebuild(this.mAppFilter, ApplicationsState.ALPHA_COMPARATOR);
        if (entries != null) {
            setActions(getAppActions(true, entries));
        } else {
            setActions(getAppActions(true, this.mEntries));
        }
    }

    public void onGuidedActionClicked(GuidedAction action) {
        int actionId = (int) action.getId();
        if (actionId == 1) {
            startActivity(MigrateStorageActivity.getLaunchIntent(getActivity(), this.mVolumeId, false));
        } else if (actionId == 0) {
            if (!getFragmentManager().popBackStackImmediate()) {
                getActivity().finish();
            }
        } else if (actionId < 100 || actionId >= this.mEntries.size() + 100) {
            throw new IllegalArgumentException("Unknown action " + action);
        } else {
            ApplicationsState.AppEntry entry = this.mEntries.get(actionId - 100);
            entry.ensureLabel(getActivity());
            startActivity(MoveAppActivity.getLaunchIntent(getActivity(), entry.info.packageName, entry.label));
        }
    }

    public void onRunningStateChanged(boolean running) {
        updateActions();
    }

    public void onPackageListChanged() {
        updateActions();
    }

    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
        setActions(getAppActions(true, apps));
    }

    public void onLauncherInfoChanged() {
        updateActions();
    }

    public void onLoadEntriesCompleted() {
        updateActions();
    }

    public void onPackageIconChanged() {
        updateActions();
    }

    public void onPackageSizeChanged(String packageName) {
        updateActions();
    }

    public void onAllSizesComputed() {
        updateActions();
    }

    private class IconLoaderTask extends AsyncTask<Void, Void, Map<String, Drawable>> {
        private final List<ApplicationsState.AppEntry> mEntries;

        public IconLoaderTask(List<ApplicationsState.AppEntry> entries) {
            this.mEntries = entries;
        }

        /* access modifiers changed from: protected */
        public Map<String, Drawable> doInBackground(Void... params) {
            Map<String, Drawable> result = new ArrayMap<>(this.mEntries.size());
            for (ApplicationsState.AppEntry entry : this.mEntries) {
                result.put(entry.info.packageName, BackupAppsStepFragment.this.mPackageManager.getApplicationIcon(entry.info));
            }
            return result;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Map<String, Drawable> stringDrawableMap) {
            IconLoaderTask unused = BackupAppsStepFragment.this.mIconLoaderTask = null;
            if (BackupAppsStepFragment.this.isAdded()) {
                BackupAppsStepFragment.this.mIconMap.putAll(stringDrawableMap);
                BackupAppsStepFragment.this.setActions(BackupAppsStepFragment.this.getAppActions(false, this.mEntries));
            }
        }
    }
}
