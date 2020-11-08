package com.android.tv.settings.device.storage;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.R;

public class DiskReceiver extends BroadcastReceiver {
    private static final String TAG = "DiskReceiver";
    private StorageManager mStorageManager;

    public void onReceive(Context context, Intent intent) {
        if (((UserManager) context.getSystemService("user")).getUserInfo(UserHandle.myUserId()).isRestricted() || ActivityManager.getCurrentUser() != UserHandle.myUserId()) {
            Log.d(TAG, "Ignoring storage notification: wrong user");
        } else if (Settings.Secure.getInt(context.getContentResolver(), "user_setup_complete", 0) == 0) {
            Log.d(TAG, "Ignoring storage notification: setup not complete");
        } else {
            this.mStorageManager = (StorageManager) context.getSystemService(StorageManager.class);
            if (TextUtils.equals(intent.getAction(), "android.os.storage.action.VOLUME_STATE_CHANGED")) {
                int state = intent.getIntExtra("android.os.storage.extra.VOLUME_STATE", -1);
                if (state == 2 || state == 3) {
                    handleMount(context, intent);
                } else if (state == 0 || state == 8) {
                    handleUnmount(context, intent);
                }
            } else if (TextUtils.equals(intent.getAction(), "com.google.android.tungsten.setupwraith.TV_SETTINGS_POST_SETUP")) {
                handleSetupComplete(context);
            }
        }
    }

    private void handleMount(Context context, Intent intent) {
        String volumeId = intent.getStringExtra("android.os.storage.extra.VOLUME_ID");
        for (VolumeInfo info : this.mStorageManager.getVolumes()) {
            if (TextUtils.equals(info.getId(), volumeId)) {
                Log.d(TAG, "Scanning volume: " + info);
                if (info.getType() == 1 && !TextUtils.equals(volumeId, "private")) {
                    Toast.makeText(context, R.string.storage_mount_adopted, 0).show();
                    return;
                }
            }
        }
    }

    private void handleUnmount(Context context, Intent intent) {
        String fsUuid = intent.getStringExtra("android.os.storage.extra.FS_UUID");
        if (TextUtils.isEmpty(fsUuid)) {
            Log.e(TAG, "Missing fsUuid, not launching activity.");
            return;
        }
        VolumeRecord volumeRecord = null;
        try {
            volumeRecord = this.mStorageManager.findRecordByUuid(fsUuid);
        } catch (Exception e) {
            Log.e(TAG, "Error finding volume record", e);
        }
        if (volumeRecord != null) {
            Log.d(TAG, "Found ejected volume: " + volumeRecord + " for FSUUID: " + fsUuid);
            if (volumeRecord.getType() == 1) {
                Intent i = NewStorageActivity.getMissingStorageLaunchIntent(context, fsUuid);
                setPopupLaunchFlags(i);
                context.startActivity(i);
            }
        }
    }

    private void handleSetupComplete(Context context) {
        Log.d(TAG, "Scanning for storage post-setup");
        for (DiskInfo diskInfo : this.mStorageManager.getDisks()) {
            Log.d(TAG, "Scanning disk: " + diskInfo);
            if (diskInfo.size <= 0) {
                Log.d(TAG, "Disk ID " + diskInfo.id + " has no media");
            } else if (diskInfo.volumeCount != 0) {
                Log.d(TAG, "Disk ID " + diskInfo.id + " has usable volumes, deferring");
            } else {
                Intent i = NewStorageActivity.getNewStorageLaunchIntent(context, (String) null, diskInfo.id);
                setPopupLaunchFlags(i);
                context.startActivity(i);
                return;
            }
        }
        for (VolumeInfo info : this.mStorageManager.getVolumes()) {
            String uuid = info.getFsUuid();
            Log.d(TAG, "Scanning volume: " + info);
            if (info.getType() == 0 && !TextUtils.isEmpty(uuid)) {
                VolumeRecord record = this.mStorageManager.findRecordByUuid(uuid);
                if (!record.isInited() && !record.isSnoozed()) {
                    DiskInfo disk = info.getDisk();
                    if (disk.isAdoptable()) {
                        Intent i2 = NewStorageActivity.getNewStorageLaunchIntent(context, info.getId(), disk.getId());
                        setPopupLaunchFlags(i2);
                        context.startActivity(i2);
                        return;
                    }
                }
            }
        }
    }

    private void setPopupLaunchFlags(Intent intent) {
        intent.setFlags(268468224);
    }
}
