package com.android.tv.settings.device;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.support.annotation.Keep;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.util.ArraySet;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.device.storage.MissingStorageFragment;
import com.android.tv.settings.device.storage.NewStorageActivity;
import com.android.tv.settings.device.storage.StorageFragment;
import com.android.tv.settings.device.storage.StoragePreference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Keep
public class StorageSummaryFragment extends SettingsPreferenceFragment {
    private static final String KEY_DEVICE_CATEGORY = "device_storage";
    private static final String KEY_REMOVABLE_CATEGORY = "removable_storage";
    private static final int REFRESH_DELAY_MILLIS = 500;
    private static final String TAG = "StorageSummaryFragment";
    private final Handler mHandler = new Handler();
    private final Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            StorageSummaryFragment.this.refresh();
        }
    };
    private final StorageEventListener mStorageEventListener = new StorageEventListener();
    private StorageManager mStorageManager;

    public static StorageSummaryFragment newInstance() {
        return new StorageSummaryFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mStorageManager = (StorageManager) getContext().getSystemService(StorageManager.class);
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.storage_summary, (String) null);
        findPreference(KEY_REMOVABLE_CATEGORY).setVisible(false);
    }

    public void onStart() {
        super.onStart();
        this.mStorageManager.registerListener(this.mStorageEventListener);
    }

    public void onResume() {
        super.onResume();
        this.mHandler.removeCallbacks(this.mRefreshRunnable);
        this.mHandler.postDelayed(this.mRefreshRunnable, 500);
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this.mRefreshRunnable);
    }

    public void onStop() {
        super.onStop();
        this.mStorageManager.unregisterListener(this.mStorageEventListener);
    }

    /* access modifiers changed from: private */
    public void refresh() {
        VolPreference volPreference;
        StorageSummaryFragment storageSummaryFragment = this;
        if (isResumed()) {
            Context themedContext = getPreferenceManager().getContext();
            List<VolumeInfo> volumes = storageSummaryFragment.mStorageManager.getVolumes();
            volumes.sort(VolumeInfo.getDescriptionComparator());
            List<VolumeInfo> privateVolumes = new ArrayList<>(volumes.size());
            List<VolumeInfo> publicVolumes = new ArrayList<>(volumes.size());
            for (VolumeInfo vol : volumes) {
                if (vol.getType() == 1) {
                    privateVolumes.add(vol);
                } else if (vol.getType() == 0) {
                    publicVolumes.add(vol);
                } else {
                    Log.d(TAG, "Skipping volume " + vol.toString());
                }
            }
            List<VolumeRecord> volumeRecords = storageSummaryFragment.mStorageManager.getVolumeRecords();
            List<VolumeRecord> privateMissingVolumes = new ArrayList<>(volumeRecords.size());
            for (VolumeRecord record : volumeRecords) {
                if (record.getType() == 1 && storageSummaryFragment.mStorageManager.findVolumeByUuid(record.getFsUuid()) == null) {
                    privateMissingVolumes.add(record);
                }
            }
            List<DiskInfo> disks = storageSummaryFragment.mStorageManager.getDisks();
            List<DiskInfo> unsupportedDisks = new ArrayList<>(disks.size());
            for (DiskInfo disk : disks) {
                if (disk.volumeCount == 0 && disk.size > 0) {
                    unsupportedDisks.add(disk);
                }
            }
            PreferenceCategory deviceCategory = (PreferenceCategory) storageSummaryFragment.findPreference(KEY_DEVICE_CATEGORY);
            Set<String> touchedDeviceKeys = new ArraySet<>(privateVolumes.size() + privateMissingVolumes.size());
            for (VolumeInfo volumeInfo : privateVolumes) {
                String key = VolPreference.makeKey(volumeInfo);
                touchedDeviceKeys.add(key);
                VolPreference volPreference2 = (VolPreference) deviceCategory.findPreference(key);
                if (volPreference2 == null) {
                    volPreference2 = new VolPreference(themedContext, volumeInfo);
                }
                volPreference2.refresh(themedContext, storageSummaryFragment.mStorageManager, volumeInfo);
                deviceCategory.addPreference(volPreference2);
            }
            for (VolumeRecord volumeRecord : privateMissingVolumes) {
                String key2 = MissingPreference.makeKey(volumeRecord);
                touchedDeviceKeys.add(key2);
                MissingPreference missingPreference = (MissingPreference) deviceCategory.findPreference(key2);
                if (missingPreference == null) {
                    missingPreference = new MissingPreference(themedContext, volumeRecord);
                }
                deviceCategory.addPreference(missingPreference);
            }
            int i = 0;
            while (i < deviceCategory.getPreferenceCount()) {
                Preference pref = deviceCategory.getPreference(i);
                if (touchedDeviceKeys.contains(pref.getKey())) {
                    i++;
                } else {
                    deviceCategory.removePreference(pref);
                }
            }
            PreferenceCategory removableCategory = (PreferenceCategory) storageSummaryFragment.findPreference(KEY_REMOVABLE_CATEGORY);
            int publicCount = publicVolumes.size() + unsupportedDisks.size();
            Set<String> touchedRemovableKeys = new ArraySet<>(publicCount);
            removableCategory.setVisible(publicCount > 0);
            for (VolumeInfo volumeInfo2 : publicVolumes) {
                List<VolumeInfo> volumes2 = volumes;
                String key3 = VolPreference.makeKey(volumeInfo2);
                touchedRemovableKeys.add(key3);
                VolPreference volPreference3 = (VolPreference) removableCategory.findPreference(key3);
                if (volPreference3 == null) {
                    String str = key3;
                    volPreference = new VolPreference(themedContext, volumeInfo2);
                } else {
                    volPreference = volPreference3;
                }
                volPreference.refresh(themedContext, storageSummaryFragment.mStorageManager, volumeInfo2);
                removableCategory.addPreference(volPreference);
                volumes = volumes2;
                privateVolumes = privateVolumes;
            }
            List<VolumeInfo> list = privateVolumes;
            for (DiskInfo diskInfo : unsupportedDisks) {
                String key4 = UnsupportedDiskPreference.makeKey(diskInfo);
                touchedRemovableKeys.add(key4);
                UnsupportedDiskPreference unsupportedDiskPreference = (UnsupportedDiskPreference) storageSummaryFragment.findPreference(key4);
                if (unsupportedDiskPreference == null) {
                    unsupportedDiskPreference = new UnsupportedDiskPreference(themedContext, diskInfo);
                }
                removableCategory.addPreference(unsupportedDiskPreference);
                storageSummaryFragment = this;
            }
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 < removableCategory.getPreferenceCount()) {
                    Preference pref2 = removableCategory.getPreference(i3);
                    if (touchedRemovableKeys.contains(pref2.getKey())) {
                        i3++;
                    } else {
                        removableCategory.removePreference(pref2);
                    }
                    i2 = i3;
                } else {
                    return;
                }
            }
        }
    }

    private static class VolPreference extends Preference {
        VolPreference(Context context, VolumeInfo volumeInfo) {
            super(context);
            setKey(makeKey(volumeInfo));
        }

        /* access modifiers changed from: private */
        public void refresh(Context context, StorageManager storageManager, VolumeInfo volumeInfo) {
            String description = storageManager.getBestVolumeDescription(volumeInfo);
            setTitle((CharSequence) description);
            if (volumeInfo.isMountedReadable()) {
                setSummary((CharSequence) getSizeString(volumeInfo));
                setFragment(StorageFragment.class.getName());
                StorageFragment.prepareArgs(getExtras(), volumeInfo);
                return;
            }
            setSummary((CharSequence) context.getString(R.string.storage_unmount_success, new Object[]{description}));
        }

        private String getSizeString(VolumeInfo vol) {
            File path = vol.getPath();
            if (!vol.isMountedReadable() || path == null) {
                return null;
            }
            return String.format(getContext().getString(R.string.storage_size), new Object[]{StoragePreference.formatSize(getContext(), path.getTotalSpace())});
        }

        public static String makeKey(VolumeInfo volumeInfo) {
            return "VolPref:" + volumeInfo.getId();
        }
    }

    private static class MissingPreference extends Preference {
        MissingPreference(Context context, VolumeRecord volumeRecord) {
            super(context);
            setKey(makeKey(volumeRecord));
            setTitle((CharSequence) volumeRecord.getNickname());
            setSummary((int) R.string.storage_not_connected);
            setFragment(MissingStorageFragment.class.getName());
            MissingStorageFragment.prepareArgs(getExtras(), volumeRecord.getFsUuid());
        }

        public static String makeKey(VolumeRecord volumeRecord) {
            return "MissingPref:" + volumeRecord.getFsUuid();
        }
    }

    private static class UnsupportedDiskPreference extends Preference {
        UnsupportedDiskPreference(Context context, DiskInfo info) {
            super(context);
            setKey(makeKey(info));
            setTitle((CharSequence) info.getDescription());
            setIntent(NewStorageActivity.getNewStorageLaunchIntent(context, (String) null, info.getId()));
        }

        public static String makeKey(DiskInfo info) {
            return "UnsupportedPref:" + info.getId();
        }
    }

    private class StorageEventListener extends android.os.storage.StorageEventListener {
        private StorageEventListener() {
        }

        public void onStorageStateChanged(String path, String oldState, String newState) {
            StorageSummaryFragment.this.refresh();
        }

        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            StorageSummaryFragment.this.refresh();
        }

        public void onVolumeRecordChanged(VolumeRecord rec) {
            StorageSummaryFragment.this.refresh();
        }

        public void onVolumeForgotten(String fsUuid) {
            StorageSummaryFragment.this.refresh();
        }

        public void onDiskScanned(DiskInfo disk, int volumeCount) {
            StorageSummaryFragment.this.refresh();
        }

        public void onDiskDestroyed(DiskInfo disk) {
            StorageSummaryFragment.this.refresh();
        }
    }

    public int getMetricsCategory() {
        return 745;
    }
}
