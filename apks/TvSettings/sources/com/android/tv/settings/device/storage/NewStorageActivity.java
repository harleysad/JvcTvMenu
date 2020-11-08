package com.android.tv.settings.device.storage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.device.StorageResetActivity;
import java.util.Iterator;
import java.util.List;

public class NewStorageActivity extends Activity {
    private static final String ACTION_MISSING_STORAGE = "com.android.tv.settings.device.storage.NewStorageActivity.MISSING_STORAGE";
    private static final String ACTION_NEW_STORAGE = "com.android.tv.settings.device.storage.NewStorageActivity.NEW_STORAGE";
    private static final String TAG = "NewStorageActivity";

    public static Intent getNewStorageLaunchIntent(Context context, String volumeId, String diskId) {
        Intent i = new Intent(context, NewStorageActivity.class);
        i.setAction(ACTION_NEW_STORAGE);
        i.putExtra("android.os.storage.extra.VOLUME_ID", volumeId);
        i.putExtra("android.os.storage.extra.DISK_ID", diskId);
        return i;
    }

    public static Intent getMissingStorageLaunchIntent(Context context, String fsUuid) {
        Intent i = new Intent(context, NewStorageActivity.class);
        i.setAction(ACTION_MISSING_STORAGE);
        i.putExtra("android.os.storage.extra.FS_UUID", fsUuid);
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        if (TextUtils.equals(getIntent().getAction(), ACTION_MISSING_STORAGE)) {
            String fsUuid = getIntent().getStringExtra("android.os.storage.extra.FS_UUID");
            if (!TextUtils.isEmpty(fsUuid)) {
                getFragmentManager().beginTransaction().add(16908290, MissingStorageFragment.newInstance(fsUuid)).commit();
                return;
            }
            throw new IllegalStateException("NewStorageActivity launched without specifying missing storage");
        }
        String volumeId = getIntent().getStringExtra("android.os.storage.extra.VOLUME_ID");
        String diskId = getIntent().getStringExtra("android.os.storage.extra.DISK_ID");
        if (!TextUtils.isEmpty(volumeId) || !TextUtils.isEmpty(diskId)) {
            getFragmentManager().beginTransaction().add(16908290, NewStorageFragment.newInstance(volumeId, diskId)).commit();
            return;
        }
        throw new IllegalStateException("NewStorageActivity launched without specifying new storage");
    }

    public static class NewStorageFragment extends GuidedStepFragment {
        private static final int ACTION_BROWSE = 1;
        private static final int ACTION_FORMAT_AS_PRIVATE = 2;
        private static final int ACTION_FORMAT_AS_PUBLIC = 4;
        private static final int ACTION_UNMOUNT = 3;
        private String mDescription;
        private String mDiskId;
        private final StorageEventListener mStorageEventListener = new StorageEventListener() {
            public void onDiskDestroyed(DiskInfo disk) {
                NewStorageFragment.this.checkForUnmount();
            }

            public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
                NewStorageFragment.this.checkForUnmount();
            }
        };
        private String mVolumeId;

        public static NewStorageFragment newInstance(String volumeId, String diskId) {
            Bundle b = new Bundle(1);
            b.putString("android.os.storage.extra.VOLUME_ID", volumeId);
            b.putString("android.os.storage.extra.DISK_ID", diskId);
            NewStorageFragment fragment = new NewStorageFragment();
            fragment.setArguments(b);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState) {
            StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
            this.mVolumeId = getArguments().getString("android.os.storage.extra.VOLUME_ID");
            this.mDiskId = getArguments().getString("android.os.storage.extra.DISK_ID");
            if (!TextUtils.isEmpty(this.mVolumeId) || !TextUtils.isEmpty(this.mDiskId)) {
                if (!TextUtils.isEmpty(this.mVolumeId)) {
                    VolumeInfo info = storageManager.findVolumeById(this.mVolumeId);
                    this.mDescription = storageManager.getBestVolumeDescription(info);
                    this.mDiskId = info.getDiskId();
                } else {
                    this.mDescription = storageManager.findDiskById(this.mDiskId).getDescription();
                }
                super.onCreate(savedInstanceState);
                return;
            }
            throw new IllegalStateException("NewStorageFragment launched without specifying new storage");
        }

        public void onStart() {
            super.onStart();
            checkForUnmount();
            ((StorageManager) getActivity().getSystemService(StorageManager.class)).registerListener(this.mStorageEventListener);
        }

        public void onStop() {
            super.onStop();
            ((StorageManager) getActivity().getSystemService(StorageManager.class)).unregisterListener(this.mStorageEventListener);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.storage_new_title), this.mDescription, (String) null, getActivity().getDrawable(R.drawable.ic_storage_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            if (TextUtils.isEmpty(this.mVolumeId)) {
                actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((int) R.string.storage_new_action_format_public)).id(4)).build());
            } else {
                actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((int) R.string.storage_new_action_browse)).id(1)).build());
            }
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((int) R.string.storage_new_action_adopt)).id(2)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).title((int) R.string.storage_new_action_eject)).id(3)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            switch ((int) action.getId()) {
                case 1:
                    startActivity(new Intent(getActivity(), StorageResetActivity.class));
                    break;
                case 2:
                    startActivity(FormatActivity.getFormatAsPrivateIntent(getActivity(), this.mDiskId));
                    break;
                case 3:
                    if (!TextUtils.isEmpty(this.mVolumeId)) {
                        startActivity(UnmountActivity.getIntent(getActivity(), this.mVolumeId, this.mDescription));
                        break;
                    }
                    break;
                case 4:
                    startActivity(FormatActivity.getFormatAsPublicIntent(getActivity(), this.mDiskId));
                    break;
            }
            getActivity().finish();
        }

        /* access modifiers changed from: private */
        public void checkForUnmount() {
            if (isAdded()) {
                StorageManager storageManager = (StorageManager) getContext().getSystemService(StorageManager.class);
                if (!TextUtils.isEmpty(this.mDiskId)) {
                    boolean found = false;
                    Iterator<DiskInfo> it = storageManager.getDisks().iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (TextUtils.equals(it.next().getId(), this.mDiskId)) {
                                found = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (!found) {
                        getActivity().finish();
                    }
                } else if (!TextUtils.isEmpty(this.mVolumeId)) {
                    boolean found2 = false;
                    Iterator<VolumeInfo> it2 = storageManager.getVolumes().iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (TextUtils.equals(it2.next().getId(), this.mVolumeId)) {
                                found2 = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (!found2) {
                        getActivity().finish();
                    }
                }
            }
        }
    }

    public static class MissingStorageFragment extends GuidedStepFragment {
        private String mDescription;
        private final BroadcastReceiver mDiskReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (TextUtils.equals(intent.getAction(), "android.os.storage.action.VOLUME_STATE_CHANGED")) {
                    MissingStorageFragment.this.checkForRemount();
                }
            }
        };
        private String mFsUuid;

        public static MissingStorageFragment newInstance(String fsUuid) {
            MissingStorageFragment fragment = new MissingStorageFragment();
            Bundle b = new Bundle(1);
            b.putString("android.os.storage.extra.FS_UUID", fsUuid);
            fragment.setArguments(b);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState) {
            StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
            this.mFsUuid = getArguments().getString("android.os.storage.extra.FS_UUID");
            if (!TextUtils.isEmpty(this.mFsUuid)) {
                this.mDescription = storageManager.findRecordByUuid(this.mFsUuid).getNickname();
                super.onCreate(savedInstanceState);
                return;
            }
            throw new IllegalStateException("MissingStorageFragment launched without specifying missing storage");
        }

        public void onStart() {
            super.onStart();
            getContext().registerReceiver(this.mDiskReceiver, new IntentFilter("android.os.storage.action.VOLUME_STATE_CHANGED"));
            checkForRemount();
        }

        public void onStop() {
            super.onStop();
            getContext().unregisterReceiver(this.mDiskReceiver);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.storage_missing_title, new Object[]{this.mDescription}), getString(R.string.storage_missing_description), (String) null, getActivity().getDrawable(R.drawable.ic_error_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            getActivity().finish();
        }

        /* access modifiers changed from: private */
        public void checkForRemount() {
            if (isAdded()) {
                for (VolumeInfo info : ((StorageManager) getContext().getSystemService(StorageManager.class)).getVolumes()) {
                    if (TextUtils.equals(info.getFsUuid(), this.mFsUuid) && info.isMountedReadable()) {
                        getActivity().finish();
                    }
                }
            }
        }
    }
}
