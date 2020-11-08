package com.android.tv.settings.device.apps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.tv.settings.R;
import java.util.List;

public class AppStoragePreference extends AppActionPreference {
    private final PackageManager mPackageManager;
    private final StorageManager mStorageManager;

    public AppStoragePreference(Context context, ApplicationsState.AppEntry entry) {
        super(context, entry);
        this.mPackageManager = context.getPackageManager();
        this.mStorageManager = (StorageManager) context.getSystemService("storage");
        refresh();
    }

    public void refresh() {
        ApplicationInfo applicationInfo = this.mEntry.info;
        VolumeInfo volumeInfo = this.mPackageManager.getPackageCurrentVolume(applicationInfo);
        List<VolumeInfo> candidates = this.mPackageManager.getPackageCandidateVolumes(applicationInfo);
        if (candidates.size() > 1 || (candidates.size() == 1 && !candidates.contains(volumeInfo))) {
            setIntent(MoveAppActivity.getLaunchIntent(getContext(), this.mEntry.info.packageName, getAppName()));
        }
        setTitle((int) R.string.device_apps_app_management_storage_used);
        String volumeDesc = this.mStorageManager.getBestVolumeDescription(volumeInfo);
        if (TextUtils.isEmpty(this.mEntry.sizeStr)) {
            setSummary((int) R.string.storage_calculating_size);
            return;
        }
        setSummary((CharSequence) getContext().getString(R.string.device_apps_app_management_storage_used_desc, new Object[]{this.mEntry.sizeStr, volumeDesc}));
    }

    private String getAppName() {
        this.mEntry.ensureLabel(getContext());
        return this.mEntry.label;
    }
}
