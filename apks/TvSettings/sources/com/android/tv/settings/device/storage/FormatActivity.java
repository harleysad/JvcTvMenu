package com.android.tv.settings.device.storage;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.tv.settings.R;
import com.android.tv.settings.device.storage.FormatAsPrivateStepFragment;
import com.android.tv.settings.device.storage.FormatAsPublicStepFragment;
import com.android.tv.settings.device.storage.SlowDriveStepFragment;
import java.util.Iterator;

public class FormatActivity extends Activity implements FormatAsPrivateStepFragment.Callback, FormatAsPublicStepFragment.Callback, SlowDriveStepFragment.Callback {
    public static final String INTENT_ACTION_FORMAT_AS_PRIVATE = "com.android.tv.settings.device.storage.FormatActivity.formatAsPrivate";
    public static final String INTENT_ACTION_FORMAT_AS_PUBLIC = "com.android.tv.settings.device.storage.FormatActivity.formatAsPublic";
    private static final String SAVE_STATE_FORMAT_DISK_DESC = "StorageResetActivity.formatDiskDesc";
    private static final String SAVE_STATE_FORMAT_PRIVATE_DISK_ID = "StorageResetActivity.formatPrivateDiskId";
    private static final String SAVE_STATE_FORMAT_PUBLIC_DISK_ID = "StorageResetActivity.formatPrivateDiskId";
    private static final String TAG = "FormatActivity";
    @VisibleForTesting
    String mFormatAsPrivateDiskId;
    @VisibleForTesting
    String mFormatAsPublicDiskId;
    /* access modifiers changed from: private */
    public String mFormatDiskDesc;
    private final BroadcastReceiver mFormatReceiver = new FormatReceiver(this);
    private PackageManager mPackageManager;
    private StorageManager mStorageManager;

    public static Intent getFormatAsPublicIntent(Context context, String diskId) {
        Intent i = new Intent(context, FormatActivity.class);
        i.setAction(INTENT_ACTION_FORMAT_AS_PUBLIC);
        i.putExtra("android.os.storage.extra.DISK_ID", diskId);
        return i;
    }

    public static Intent getFormatAsPrivateIntent(Context context, String diskId) {
        Intent i = new Intent(context, FormatActivity.class);
        i.setAction(INTENT_ACTION_FORMAT_AS_PRIVATE);
        i.putExtra("android.os.storage.extra.DISK_ID", diskId);
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Fragment f;
        super.onCreate(savedInstanceState);
        this.mPackageManager = getPackageManager();
        this.mStorageManager = (StorageManager) getSystemService(StorageManager.class);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsStorageService.ACTION_FORMAT_AS_PRIVATE);
        filter.addAction(SettingsStorageService.ACTION_FORMAT_AS_PUBLIC);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mFormatReceiver, filter);
        if (savedInstanceState != null) {
            this.mFormatAsPrivateDiskId = savedInstanceState.getString("StorageResetActivity.formatPrivateDiskId");
            this.mFormatAsPublicDiskId = savedInstanceState.getString("StorageResetActivity.formatPrivateDiskId");
            this.mFormatDiskDesc = savedInstanceState.getString(SAVE_STATE_FORMAT_DISK_DESC);
            return;
        }
        String diskId = getIntent().getStringExtra("android.os.storage.extra.DISK_ID");
        String action = getIntent().getAction();
        if (TextUtils.equals(action, INTENT_ACTION_FORMAT_AS_PRIVATE)) {
            f = FormatAsPrivateStepFragment.newInstance(diskId);
        } else if (TextUtils.equals(action, INTENT_ACTION_FORMAT_AS_PUBLIC)) {
            f = FormatAsPublicStepFragment.newInstance(diskId);
        } else {
            throw new IllegalStateException("No known action specified");
        }
        getFragmentManager().beginTransaction().add(16908290, f).commit();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        VolumeInfo volumeInfo;
        super.onResume();
        if (!TextUtils.isEmpty(this.mFormatAsPrivateDiskId) && (volumeInfo = findVolume(this.mFormatAsPrivateDiskId)) != null && volumeInfo.getType() == 1) {
            handleFormatAsPrivateComplete(-1.0f, -1.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mFormatReceiver);
    }

    private VolumeInfo findVolume(String diskId) {
        for (VolumeInfo vol : this.mStorageManager.getVolumes()) {
            if (TextUtils.equals(diskId, vol.getDiskId()) && vol.getType() == 1) {
                return vol;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("StorageResetActivity.formatPrivateDiskId", this.mFormatAsPrivateDiskId);
        outState.putString("StorageResetActivity.formatPrivateDiskId", this.mFormatAsPublicDiskId);
        outState.putString(SAVE_STATE_FORMAT_DISK_DESC, this.mFormatDiskDesc);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleFormatAsPrivateComplete(float privateBench, float internalBench) {
        if (((double) Math.abs(-1.0f - privateBench)) < 0.1d) {
            Log.d(TAG, "New volume is " + (privateBench / internalBench) + "x the speed of internal");
            if (privateBench > 2.0E9f) {
                getFragmentManager().beginTransaction().replace(16908290, SlowDriveStepFragment.newInstance()).commit();
                return;
            }
        }
        launchMigrateStorageAndFinish(this.mFormatAsPrivateDiskId);
    }

    @VisibleForTesting
    static class FormatReceiver extends BroadcastReceiver {
        private final FormatActivity mActivity;

        FormatReceiver(FormatActivity activity) {
            this.mActivity = activity;
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), SettingsStorageService.ACTION_FORMAT_AS_PRIVATE) && !TextUtils.isEmpty(this.mActivity.mFormatAsPrivateDiskId)) {
                if (!TextUtils.equals(this.mActivity.mFormatAsPrivateDiskId, intent.getStringExtra("android.os.storage.extra.DISK_ID"))) {
                    return;
                }
                if (intent.getBooleanExtra(SettingsStorageService.EXTRA_SUCCESS, false)) {
                    if (this.mActivity.isResumed()) {
                        this.mActivity.handleFormatAsPrivateComplete((float) intent.getLongExtra(SettingsStorageService.EXTRA_PRIVATE_BENCH, -1), (float) intent.getLongExtra(SettingsStorageService.EXTRA_INTERNAL_BENCH, -1));
                    }
                    Toast.makeText(context, this.mActivity.getString(R.string.storage_format_success, new Object[]{this.mActivity.mFormatDiskDesc}), 0).show();
                    return;
                }
                Toast.makeText(context, this.mActivity.getString(R.string.storage_format_failure, new Object[]{this.mActivity.mFormatDiskDesc}), 0).show();
                this.mActivity.finish();
            } else if (TextUtils.equals(intent.getAction(), SettingsStorageService.ACTION_FORMAT_AS_PUBLIC) && !TextUtils.isEmpty(this.mActivity.mFormatAsPublicDiskId)) {
                if (TextUtils.equals(this.mActivity.mFormatAsPublicDiskId, intent.getStringExtra("android.os.storage.extra.DISK_ID"))) {
                    if (intent.getBooleanExtra(SettingsStorageService.EXTRA_SUCCESS, false)) {
                        Toast.makeText(context, this.mActivity.getString(R.string.storage_format_success, new Object[]{this.mActivity.mFormatDiskDesc}), 0).show();
                    } else {
                        Toast.makeText(context, this.mActivity.getString(R.string.storage_format_failure, new Object[]{this.mActivity.mFormatDiskDesc}), 0).show();
                    }
                    this.mActivity.finish();
                }
            }
        }
    }

    public void onRequestFormatAsPrivate(String diskId) {
        DiskInfo info;
        getFragmentManager().beginTransaction().replace(16908290, FormattingProgressFragment.newInstance()).commit();
        this.mFormatAsPrivateDiskId = diskId;
        for (VolumeInfo volume : this.mStorageManager.getVolumes()) {
            if ((volume.getType() == 1 || volume.getType() == 0) && TextUtils.equals(volume.getDiskId(), diskId)) {
                this.mFormatDiskDesc = this.mStorageManager.getBestVolumeDescription(volume);
            }
        }
        if (TextUtils.isEmpty(this.mFormatDiskDesc) && (info = this.mStorageManager.findDiskById(diskId)) != null) {
            this.mFormatDiskDesc = info.getDescription();
        }
        SettingsStorageService.formatAsPrivate(this, diskId);
    }

    private void launchMigrateStorageAndFinish(String diskId) {
        VolumeInfo moveTarget = null;
        Iterator<VolumeInfo> it = this.mPackageManager.getPrimaryStorageCandidateVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            VolumeInfo candidate = it.next();
            if (TextUtils.equals(candidate.getDiskId(), diskId)) {
                moveTarget = candidate;
                break;
            }
        }
        if (moveTarget != null) {
            startActivity(MigrateStorageActivity.getLaunchIntent(this, moveTarget.getId(), true));
        }
        finish();
    }

    public void onRequestFormatAsPublic(String diskId, String volumeId) {
        DiskInfo info;
        VolumeInfo info2;
        getFragmentManager().beginTransaction().replace(16908290, FormattingProgressFragment.newInstance()).commit();
        this.mFormatAsPublicDiskId = diskId;
        if (!TextUtils.isEmpty(volumeId) && (info2 = this.mStorageManager.findVolumeById(volumeId)) != null) {
            this.mFormatDiskDesc = this.mStorageManager.getBestVolumeDescription(info2);
        }
        if (TextUtils.isEmpty(this.mFormatDiskDesc) && (info = this.mStorageManager.findDiskById(diskId)) != null) {
            this.mFormatDiskDesc = info.getDescription();
        }
        SettingsStorageService.formatAsPublic(this, diskId);
    }

    public void onCancelFormatDialog() {
        finish();
    }

    public void onSlowDriveWarningComplete() {
        launchMigrateStorageAndFinish(this.mFormatAsPrivateDiskId);
    }
}
