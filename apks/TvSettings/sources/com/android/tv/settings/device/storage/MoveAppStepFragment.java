package com.android.tv.settings.device.storage;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import com.android.tv.settings.R;
import java.util.List;

public class MoveAppStepFragment extends GuidedStepFragment {
    private static final String ARG_PACKAGE_DESC = "packageDesc";
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String TAG = "MoveAppStepFragment";
    private List<VolumeInfo> mCandidateVolumes;
    private VolumeInfo mCurrentVolume;
    private String mPackageDesc;
    private PackageManager mPackageManager;
    private String mPackageName;
    private StorageManager mStorageManager;

    public interface Callback {
        void onRequestMovePackageToVolume(String str, VolumeInfo volumeInfo);
    }

    public static MoveAppStepFragment newInstance(String packageName, String packageDesc) {
        MoveAppStepFragment fragment = new MoveAppStepFragment();
        Bundle b = new Bundle(2);
        b.putString(ARG_PACKAGE_NAME, packageName);
        b.putString(ARG_PACKAGE_DESC, packageDesc);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mPackageManager = getActivity().getPackageManager();
        this.mStorageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        this.mPackageDesc = getArguments().getString(ARG_PACKAGE_DESC, "");
        this.mPackageName = getArguments().getString(ARG_PACKAGE_NAME);
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        Drawable icon;
        try {
            icon = this.mPackageManager.getApplicationIcon(this.mPackageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Missing package while resolving icon", e);
            icon = null;
        }
        return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_move_app_title), (String) null, this.mPackageDesc, icon);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        try {
            ApplicationInfo info = this.mPackageManager.getApplicationInfo(getArguments().getString(ARG_PACKAGE_NAME), 0);
            this.mCurrentVolume = this.mPackageManager.getPackageCurrentVolume(info);
            this.mCandidateVolumes = this.mPackageManager.getPackageCandidateVolumes(info);
            for (VolumeInfo candidate : this.mCandidateVolumes) {
                if (candidate.isMountedWritable()) {
                    actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((CharSequence) this.mStorageManager.getBestVolumeDescription(candidate))).description((CharSequence) getString(R.string.storage_wizard_back_up_apps_space_available, new Object[]{Formatter.formatFileSize(getActivity(), candidate.getPath().getFreeSpace())}))).checked(TextUtils.equals(this.mCurrentVolume.getId(), candidate.getId()))).checkSetId(1)).id((long) this.mCandidateVolumes.indexOf(candidate))).build());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package missing while resolving storage", e);
        }
    }

    public void onGuidedActionClicked(GuidedAction action) {
        Callback callback = (Callback) getActivity();
        VolumeInfo destination = this.mCandidateVolumes.get((int) action.getId());
        if (!destination.equals(this.mCurrentVolume)) {
            callback.onRequestMovePackageToVolume(this.mPackageName, destination);
        } else if (!getFragmentManager().popBackStackImmediate()) {
            getActivity().finish();
        }
    }
}
