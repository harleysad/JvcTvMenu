package com.android.tv.settings.device.storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.ProgressDialogFragment;
import java.util.List;
import java.util.Objects;

public class MigrateStorageActivity extends Activity {
    private static final String EXTRA_MIGRATE_HERE = "com.android.tv.settings.device.storage.MigrateStorageActivity.MIGRATE_HERE";
    private static final String SAVE_STATE_MOVE_ID = "MigrateStorageActivity.MOVE_ID";
    private static final String TAG = "MigrateStorageActivity";
    private final Handler mHandler = new Handler();
    private final PackageManager.MoveCallback mMoveCallback = new PackageManager.MoveCallback() {
        public void onStatusChanged(int moveId, int status, long estMillis) {
            if (moveId == MigrateStorageActivity.this.mMoveId && PackageManager.isMoveStatusFinished(status)) {
                if (status == -100) {
                    MigrateStorageActivity.this.showMigrationSuccessToast();
                } else {
                    MigrateStorageActivity.this.showMigrationFailureToast();
                }
                MigrateStorageActivity.this.finish();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mMoveId = -1;
    private PackageManager mPackageManager;
    private String mTargetVolumeDesc;
    private VolumeInfo mTargetVolumeInfo;
    private String mVolumeDesc;
    private VolumeInfo mVolumeInfo;

    public static Intent getLaunchIntent(Context context, String volumeId, boolean migrateHere) {
        Intent i = new Intent(context, MigrateStorageActivity.class);
        i.putExtra("android.os.storage.extra.VOLUME_ID", volumeId);
        i.putExtra(EXTRA_MIGRATE_HERE, migrateHere);
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String volumeId = intent.getStringExtra("android.os.storage.extra.VOLUME_ID");
        StorageManager storageManager = (StorageManager) getSystemService(StorageManager.class);
        if (intent.getBooleanExtra(EXTRA_MIGRATE_HERE, true)) {
            this.mTargetVolumeInfo = storageManager.findVolumeById(volumeId);
            if (this.mTargetVolumeInfo == null) {
                finish();
                return;
            } else {
                this.mTargetVolumeDesc = storageManager.getBestVolumeDescription(this.mTargetVolumeInfo);
                getFragmentManager().beginTransaction().add(16908290, MigrateConfirmationStepFragment.newInstance(this.mTargetVolumeDesc)).commit();
            }
        } else {
            this.mVolumeInfo = storageManager.findVolumeById(volumeId);
            if (this.mVolumeInfo == null) {
                finish();
                return;
            } else {
                this.mVolumeDesc = storageManager.getBestVolumeDescription(this.mVolumeInfo);
                getFragmentManager().beginTransaction().add(16908290, ChooseStorageStepFragment.newInstance(this.mVolumeInfo)).commit();
            }
        }
        this.mPackageManager = getPackageManager();
        this.mPackageManager.registerMoveCallback(this.mMoveCallback, this.mHandler);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_MOVE_ID, this.mMoveId);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        getPackageManager().unregisterMoveCallback(this.mMoveCallback);
    }

    /* access modifiers changed from: private */
    public void onConfirmCancel() {
        finish();
    }

    /* access modifiers changed from: private */
    public void onConfirmProceed() {
        startMigrationInternal();
    }

    /* access modifiers changed from: private */
    public void onChoose(VolumeInfo volumeInfo) {
        this.mTargetVolumeInfo = volumeInfo;
        this.mTargetVolumeDesc = ((StorageManager) getSystemService(StorageManager.class)).getBestVolumeDescription(this.mTargetVolumeInfo);
        startMigrationInternal();
    }

    private void startMigrationInternal() {
        try {
            this.mMoveId = this.mPackageManager.movePrimaryStorage(this.mTargetVolumeInfo);
            getFragmentManager().beginTransaction().replace(16908290, MigrateProgressFragment.newInstance(this.mTargetVolumeDesc)).commitNow();
        } catch (IllegalArgumentException e) {
            if (Objects.equals(this.mTargetVolumeInfo.getFsUuid(), ((StorageManager) getSystemService("storage")).getPrimaryStorageVolume().getUuid())) {
                showMigrationSuccessToast();
            } else {
                Log.e(TAG, "Storage migration failure", e);
                showMigrationFailureToast();
            }
            finish();
        } catch (IllegalStateException e2) {
            showMigrationFailureToast();
            finish();
        }
    }

    /* access modifiers changed from: private */
    public void showMigrationSuccessToast() {
        Toast.makeText(this, getString(R.string.storage_wizard_migrate_toast_success, new Object[]{this.mTargetVolumeDesc}), 0).show();
    }

    /* access modifiers changed from: private */
    public void showMigrationFailureToast() {
        Toast.makeText(this, getString(R.string.storage_wizard_migrate_toast_failure, new Object[]{this.mTargetVolumeDesc}), 0).show();
    }

    public static class MigrateConfirmationStepFragment extends GuidedStepFragment {
        private static final int ACTION_CONFIRM = 1;
        private static final int ACTION_LATER = 2;
        private static final String ARG_VOLUME_DESC = "volumeDesc";

        public static MigrateConfirmationStepFragment newInstance(String volumeDescription) {
            MigrateConfirmationStepFragment fragment = new MigrateConfirmationStepFragment();
            Bundle b = new Bundle(1);
            b.putString(ARG_VOLUME_DESC, volumeDescription);
            fragment.setArguments(b);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String driveDesc = getArguments().getString(ARG_VOLUME_DESC);
            return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_migrate_confirm_title, new Object[]{driveDesc}), getString(R.string.storage_wizard_migrate_confirm_description, new Object[]{driveDesc}), (String) null, getActivity().getDrawable(R.drawable.ic_storage_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((int) R.string.storage_wizard_migrate_confirm_action_move_now)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(2)).title((int) R.string.storage_wizard_migrate_confirm_action_move_later)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            switch ((int) action.getId()) {
                case 1:
                    ((MigrateStorageActivity) getActivity()).onConfirmProceed();
                    return;
                case 2:
                    ((MigrateStorageActivity) getActivity()).onConfirmCancel();
                    return;
                default:
                    return;
            }
        }
    }

    public static class ChooseStorageStepFragment extends GuidedStepFragment {
        private List<VolumeInfo> mCandidateVolumes;

        public static ChooseStorageStepFragment newInstance(VolumeInfo currentVolumeInfo) {
            Bundle args = new Bundle(1);
            args.putString("android.os.storage.extra.VOLUME_ID", currentVolumeInfo.getId());
            ChooseStorageStepFragment fragment = new ChooseStorageStepFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_migrate_choose_title), (String) null, (String) null, getActivity().getDrawable(R.drawable.ic_storage_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            StorageManager storageManager = (StorageManager) getContext().getSystemService(StorageManager.class);
            this.mCandidateVolumes = getContext().getPackageManager().getPrimaryStorageCandidateVolumes();
            String volumeId = getArguments().getString("android.os.storage.extra.VOLUME_ID");
            for (VolumeInfo candidate : this.mCandidateVolumes) {
                if (!TextUtils.equals(candidate.getId(), volumeId)) {
                    actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((CharSequence) storageManager.getBestVolumeDescription(candidate))).description((CharSequence) getString(R.string.storage_wizard_back_up_apps_space_available, new Object[]{Formatter.formatFileSize(getActivity(), candidate.getPath().getFreeSpace())}))).id((long) this.mCandidateVolumes.indexOf(candidate))).build());
                }
            }
        }

        public void onGuidedActionClicked(GuidedAction action) {
            ((MigrateStorageActivity) getActivity()).onChoose(this.mCandidateVolumes.get((int) action.getId()));
        }
    }

    public static class MigrateProgressFragment extends ProgressDialogFragment {
        private static final String ARG_VOLUME_DESC = "volumeDesc";

        public static MigrateProgressFragment newInstance(String volumeDescription) {
            MigrateProgressFragment fragment = new MigrateProgressFragment();
            Bundle b = new Bundle(1);
            b.putString(ARG_VOLUME_DESC, volumeDescription);
            fragment.setArguments(b);
            return fragment;
        }

        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setTitle((CharSequence) getActivity().getString(R.string.storage_wizard_migrate_progress_title, new Object[]{getArguments().getString(ARG_VOLUME_DESC)}));
            setSummary((CharSequence) getActivity().getString(R.string.storage_wizard_migrate_progress_description));
        }
    }
}
