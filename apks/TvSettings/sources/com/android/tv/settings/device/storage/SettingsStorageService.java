package com.android.tv.settings.device.storage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

public class SettingsStorageService {
    public static final String ACTION_FORMAT_AS_PRIVATE = "com.android.tv.settings.device.storage.FORMAT_AS_PRIVATE";
    public static final String ACTION_FORMAT_AS_PUBLIC = "com.android.tv.settings.device.storage.FORMAT_AS_PUBLIC";
    public static final String ACTION_UNMOUNT = "com.android.tv.settings.device.storage.UNMOUNT";
    public static final String EXTRA_INTERNAL_BENCH = "com.android.tv.settings.device.storage.INTERNAL_BENCH";
    public static final String EXTRA_PRIVATE_BENCH = "com.android.tv.settings.device.storage.PRIVATE_BENCH";
    public static final String EXTRA_SUCCESS = "com.android.tv.settings.device.storage.SUCCESS";
    private static final String TAG = "SettingsStorageService";

    public static void formatAsPublic(Context context, String diskId) {
        Intent intent = new Intent(context, Impl.class);
        intent.setAction(ACTION_FORMAT_AS_PUBLIC);
        intent.putExtra("android.os.storage.extra.DISK_ID", diskId);
        context.startService(intent);
    }

    public static void formatAsPrivate(Context context, String diskId) {
        Intent intent = new Intent(context, Impl.class);
        intent.setAction(ACTION_FORMAT_AS_PRIVATE);
        intent.putExtra("android.os.storage.extra.DISK_ID", diskId);
        context.startService(intent);
    }

    public static void unmount(Context context, String volumeId) {
        Intent intent = new Intent(context, Impl.class);
        intent.setAction(ACTION_UNMOUNT);
        intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeId);
        context.startService(intent);
    }

    public static class Impl extends IntentService {
        public Impl() {
            super(Impl.class.getName());
        }

        /* access modifiers changed from: protected */
        public void onHandleIntent(@Nullable Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != -984474434) {
                    if (hashCode != 470208032) {
                        if (hashCode == 1079174062 && action.equals(SettingsStorageService.ACTION_FORMAT_AS_PUBLIC)) {
                            c = 0;
                        }
                    } else if (action.equals(SettingsStorageService.ACTION_UNMOUNT)) {
                        c = 2;
                    }
                } else if (action.equals(SettingsStorageService.ACTION_FORMAT_AS_PRIVATE)) {
                    c = 1;
                }
                switch (c) {
                    case 0:
                        String diskId = intent.getStringExtra("android.os.storage.extra.DISK_ID");
                        if (!TextUtils.isEmpty(diskId)) {
                            formatAsPublic(diskId);
                            return;
                        }
                        throw new IllegalArgumentException("No disk ID specified for format as public: " + intent);
                    case 1:
                        String diskId2 = intent.getStringExtra("android.os.storage.extra.DISK_ID");
                        if (!TextUtils.isEmpty(diskId2)) {
                            formatAsPrivate(diskId2);
                            return;
                        }
                        throw new IllegalArgumentException("No disk ID specified for format as public: " + intent);
                    case 2:
                        String volumeId = intent.getStringExtra("android.os.storage.extra.VOLUME_ID");
                        if (!TextUtils.isEmpty(volumeId)) {
                            unmount(volumeId);
                            return;
                        }
                        throw new IllegalArgumentException("No volume ID specified for unmount: " + intent);
                    default:
                        return;
                }
            } else {
                throw new IllegalArgumentException("Empty action in intent: " + intent);
            }
        }

        private void formatAsPublic(String diskId) {
            try {
                StorageManager storageManager = (StorageManager) getSystemService(StorageManager.class);
                for (VolumeInfo volume : storageManager.getVolumes()) {
                    if (TextUtils.equals(diskId, volume.getDiskId()) && volume.getType() == 1) {
                        storageManager.forgetVolume(volume.getFsUuid());
                    }
                }
                storageManager.partitionPublic(diskId);
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_FORMAT_AS_PUBLIC).putExtra("android.os.storage.extra.DISK_ID", diskId).putExtra(SettingsStorageService.EXTRA_SUCCESS, true));
            } catch (Exception e) {
                Log.e(SettingsStorageService.TAG, "Failed to format " + diskId, e);
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_FORMAT_AS_PUBLIC).putExtra("android.os.storage.extra.DISK_ID", diskId).putExtra(SettingsStorageService.EXTRA_SUCCESS, false));
            }
        }

        private void formatAsPrivate(String diskId) {
            long privateBench;
            try {
                StorageManager storageManager = (StorageManager) getSystemService(StorageManager.class);
                storageManager.partitionPrivate(diskId);
                long internalBench = storageManager.benchmark("private");
                VolumeInfo privateVol = findPrivateVolume(storageManager, diskId);
                if (privateVol != null) {
                    privateBench = storageManager.benchmark(privateVol.getId());
                } else {
                    privateBench = -1;
                }
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_FORMAT_AS_PRIVATE).putExtra("android.os.storage.extra.DISK_ID", diskId).putExtra(SettingsStorageService.EXTRA_INTERNAL_BENCH, internalBench).putExtra(SettingsStorageService.EXTRA_PRIVATE_BENCH, privateBench).putExtra(SettingsStorageService.EXTRA_SUCCESS, true));
            } catch (Exception e) {
                Log.e(SettingsStorageService.TAG, "Failed to format " + diskId, e);
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_FORMAT_AS_PRIVATE).putExtra("android.os.storage.extra.DISK_ID", diskId).putExtra(SettingsStorageService.EXTRA_SUCCESS, false));
            }
        }

        @Nullable
        private VolumeInfo findPrivateVolume(@NonNull StorageManager storageManager, String diskId) {
            for (VolumeInfo vol : storageManager.getVolumes()) {
                if (TextUtils.equals(diskId, vol.getDiskId()) && vol.getType() == 1) {
                    return vol;
                }
            }
            return null;
        }

        private void unmount(String volumeId) {
            try {
                long minTime = System.currentTimeMillis() + 3000;
                StorageManager storageManager = (StorageManager) getSystemService(StorageManager.class);
                VolumeInfo volumeInfo = storageManager.findVolumeById(volumeId);
                if (volumeInfo == null || !volumeInfo.isMountedReadable()) {
                    Log.d(SettingsStorageService.TAG, "Volume not found, skipping unmount");
                } else {
                    Log.d(SettingsStorageService.TAG, "Trying to unmount " + volumeId);
                    storageManager.unmount(volumeId);
                }
                for (long waitTime = minTime - System.currentTimeMillis(); waitTime > 0; waitTime = minTime - System.currentTimeMillis()) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                    }
                }
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_UNMOUNT).putExtra("android.os.storage.extra.VOLUME_ID", volumeId).putExtra(SettingsStorageService.EXTRA_SUCCESS, true));
            } catch (Exception e2) {
                Log.d(SettingsStorageService.TAG, "Could not unmount", e2);
                sendLocalBroadcast(new Intent(SettingsStorageService.ACTION_UNMOUNT).putExtra("android.os.storage.extra.VOLUME_ID", volumeId).putExtra(SettingsStorageService.EXTRA_SUCCESS, false));
            }
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public void sendLocalBroadcast(Intent intent) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
