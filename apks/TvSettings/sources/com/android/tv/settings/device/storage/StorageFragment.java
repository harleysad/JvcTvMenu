package com.android.tv.settings.device.storage;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.deviceinfo.StorageMeasurement;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.apps.AppsFragment;
import java.util.HashMap;
import java.util.Iterator;

public class StorageFragment extends SettingsPreferenceFragment {
    private static final String KEY_APPS_USAGE = "apps_usage";
    private static final String KEY_AVAILABLE = "available";
    private static final String KEY_CACHE_USAGE = "cache_usage";
    private static final String KEY_DCIM_USAGE = "dcim_usage";
    private static final String KEY_DOWNLOADS_USAGE = "downloads_usage";
    private static final String KEY_EJECT = "eject";
    private static final String KEY_ERASE = "erase";
    private static final String KEY_MIGRATE = "migrate";
    private static final String KEY_MISC_USAGE = "misc_usage";
    private static final String KEY_MUSIC_USAGE = "music_usage";
    private static final String TAG = "StorageFragment";
    private StoragePreference mAppsUsagePref;
    private StoragePreference mAvailablePref;
    private StoragePreference mCacheUsagePref;
    private StoragePreference mDcimUsagePref;
    private StoragePreference mDownloadsUsagePref;
    private Preference mEjectPref;
    private Preference mErasePref;
    private StorageMeasurement mMeasure;
    private final StorageMeasurement.MeasurementReceiver mMeasurementReceiver = new MeasurementReceiver();
    private Preference mMigratePref;
    private StoragePreference mMiscUsagePref;
    private StoragePreference mMusicUsagePref;
    private PackageManager mPackageManager;
    private final StorageEventListener mStorageEventListener = new StorageEventListener();
    private StorageManager mStorageManager;
    /* access modifiers changed from: private */
    public VolumeInfo mVolumeInfo;

    public static void prepareArgs(Bundle bundle, VolumeInfo volumeInfo) {
        bundle.putString("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mStorageManager = (StorageManager) getContext().getSystemService(StorageManager.class);
        this.mPackageManager = getContext().getPackageManager();
        this.mVolumeInfo = this.mStorageManager.findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        this.mStorageManager.registerListener(this.mStorageEventListener);
        startMeasurement();
    }

    public void onResume() {
        super.onResume();
        this.mVolumeInfo = this.mStorageManager.findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
        if (this.mVolumeInfo == null || !this.mVolumeInfo.isMountedReadable()) {
            getFragmentManager().popBackStack();
        } else {
            refresh();
        }
    }

    public void onStop() {
        super.onStop();
        this.mStorageManager.unregisterListener(this.mStorageEventListener);
        stopMeasurement();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.storage, (String) null);
        getPreferenceScreen().setTitle((CharSequence) this.mStorageManager.getBestVolumeDescription(this.mVolumeInfo));
        this.mMigratePref = findPreference(KEY_MIGRATE);
        this.mEjectPref = findPreference(KEY_EJECT);
        this.mErasePref = findPreference(KEY_ERASE);
        this.mAppsUsagePref = (StoragePreference) findPreference(KEY_APPS_USAGE);
        this.mDcimUsagePref = (StoragePreference) findPreference(KEY_DCIM_USAGE);
        this.mMusicUsagePref = (StoragePreference) findPreference(KEY_MUSIC_USAGE);
        this.mDownloadsUsagePref = (StoragePreference) findPreference(KEY_DOWNLOADS_USAGE);
        this.mCacheUsagePref = (StoragePreference) findPreference(KEY_CACHE_USAGE);
        this.mMiscUsagePref = (StoragePreference) findPreference(KEY_MISC_USAGE);
        this.mAvailablePref = (StoragePreference) findPreference(KEY_AVAILABLE);
    }

    /* access modifiers changed from: private */
    public void refresh() {
        boolean showMigrate = false;
        VolumeInfo currentExternal = this.mPackageManager.getPrimaryStorageCurrentVolume();
        if (currentExternal != null && !TextUtils.equals(currentExternal.getId(), this.mVolumeInfo.getId())) {
            Iterator<VolumeInfo> it = this.mPackageManager.getPrimaryStorageCandidateVolumes().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (TextUtils.equals(it.next().getId(), this.mVolumeInfo.getId())) {
                        showMigrate = true;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        this.mMigratePref.setVisible(showMigrate);
        boolean z = true;
        this.mMigratePref.setIntent(MigrateStorageActivity.getLaunchIntent(getContext(), this.mVolumeInfo.getId(), true));
        String description = this.mStorageManager.getBestVolumeDescription(this.mVolumeInfo);
        boolean privateInternal = "private".equals(this.mVolumeInfo.getId());
        if (this.mVolumeInfo.getType() != 1) {
            z = false;
        }
        boolean isPrivate = z;
        this.mEjectPref.setVisible(!privateInternal);
        this.mEjectPref.setIntent(UnmountActivity.getIntent(getContext(), this.mVolumeInfo.getId(), description));
        this.mErasePref.setVisible(!privateInternal);
        if (isPrivate) {
            this.mErasePref.setIntent(FormatActivity.getFormatAsPublicIntent(getContext(), this.mVolumeInfo.getDiskId()));
            this.mErasePref.setTitle((int) R.string.storage_format_as_public);
        } else {
            this.mErasePref.setIntent(FormatActivity.getFormatAsPrivateIntent(getContext(), this.mVolumeInfo.getDiskId()));
            this.mErasePref.setTitle((int) R.string.storage_format_as_private);
        }
        this.mAppsUsagePref.setVisible(isPrivate);
        this.mAppsUsagePref.setFragment(AppsFragment.class.getName());
        AppsFragment.prepareArgs(this.mAppsUsagePref.getExtras(), this.mVolumeInfo.fsUuid, description);
        this.mDcimUsagePref.setVisible(isPrivate);
        this.mMusicUsagePref.setVisible(isPrivate);
        this.mDownloadsUsagePref.setVisible(isPrivate);
        this.mCacheUsagePref.setVisible(isPrivate);
        this.mCacheUsagePref.setFragment(ConfirmClearCacheFragment.class.getName());
    }

    private void startMeasurement() {
        if (this.mVolumeInfo != null && this.mVolumeInfo.isMountedReadable()) {
            this.mMeasure = new StorageMeasurement(getContext(), this.mVolumeInfo, this.mStorageManager.findEmulatedForPrivate(this.mVolumeInfo));
            this.mMeasure.setReceiver(this.mMeasurementReceiver);
            this.mMeasure.forceMeasure();
        }
    }

    private void stopMeasurement() {
        if (this.mMeasure != null) {
            this.mMeasure.onDestroy();
        }
    }

    /* access modifiers changed from: private */
    public void updateDetails(StorageMeasurement.MeasurementDetails details) {
        int currentUser = ActivityManager.getCurrentUser();
        long dcimSize = totalValues(details.mediaSize.get(currentUser), Environment.DIRECTORY_DCIM, Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_PICTURES);
        long musicSize = totalValues(details.mediaSize.get(currentUser), Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_PODCASTS);
        long downloadsSize = totalValues(details.mediaSize.get(currentUser), Environment.DIRECTORY_DOWNLOADS);
        this.mAvailablePref.setSize(details.availSize);
        this.mAppsUsagePref.setSize(details.appsSize.get(currentUser));
        this.mDcimUsagePref.setSize(dcimSize);
        this.mMusicUsagePref.setSize(musicSize);
        this.mDownloadsUsagePref.setSize(downloadsSize);
        this.mCacheUsagePref.setSize(details.cacheSize);
        this.mMiscUsagePref.setSize(details.miscSize.get(currentUser));
    }

    private static long totalValues(HashMap<String, Long> map, String... keys) {
        long total = 0;
        if (map != null) {
            for (String key : keys) {
                if (map.containsKey(key)) {
                    total += map.get(key).longValue();
                }
            }
        } else {
            Log.w(TAG, "MeasurementDetails mediaSize array does not have key for current user " + ActivityManager.getCurrentUser());
        }
        return total;
    }

    private class MeasurementReceiver implements StorageMeasurement.MeasurementReceiver {
        private MeasurementReceiver() {
        }

        public void onDetailsChanged(StorageMeasurement.MeasurementDetails details) {
            StorageFragment.this.updateDetails(details);
        }
    }

    private class StorageEventListener extends android.os.storage.StorageEventListener {
        private StorageEventListener() {
        }

        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            VolumeInfo unused = StorageFragment.this.mVolumeInfo = vol;
            if (!StorageFragment.this.isResumed()) {
                return;
            }
            if (StorageFragment.this.mVolumeInfo.isMountedReadable()) {
                StorageFragment.this.refresh();
            } else {
                StorageFragment.this.getFragmentManager().popBackStack();
            }
        }
    }

    public int getMetricsCategory() {
        return 745;
    }
}
