package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeRecord;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.R;

public class MissingStorageFragment extends LeanbackPreferenceFragment {
    private static final String KEY_FORGET = "forget";
    private static final String TAG = "MissingStorageFragment";
    /* access modifiers changed from: private */
    public String mFsUuid;
    /* access modifiers changed from: private */
    public StorageManager mStorageManager;

    public static void prepareArgs(Bundle b, String fsUuid) {
        b.putString("android.os.storage.extra.FS_UUID", fsUuid);
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mFsUuid = getArguments().getString("android.os.storage.extra.FS_UUID");
        this.mStorageManager = (StorageManager) getContext().getSystemService(StorageManager.class);
        this.mStorageManager.registerListener(new StorageEventListener());
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        if (this.mStorageManager.findRecordByUuid(this.mFsUuid) == null) {
            getFragmentManager().popBackStack();
            Log.i(TAG, "FsUuid " + this.mFsUuid + " vanished upon resuming");
            return;
        }
        refresh();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        String str = null;
        setPreferencesFromResource(R.xml.missing_storage, (String) null);
        VolumeRecord record = this.mStorageManager.findRecordByUuid(this.mFsUuid);
        PreferenceScreen screen = getPreferenceScreen();
        if (record != null) {
            str = record.getNickname();
        }
        screen.setTitle((CharSequence) str);
    }

    /* access modifiers changed from: private */
    public void refresh() {
        Preference forget = findPreference(KEY_FORGET);
        forget.setFragment(ForgetPrivateConfirmFragment.class.getName());
        ForgetPrivateConfirmFragment.prepareArgs(forget.getExtras(), this.mFsUuid);
    }

    private class StorageEventListener extends android.os.storage.StorageEventListener {
        private StorageEventListener() {
        }

        public void onVolumeForgotten(String fsUuid) {
            if (TextUtils.equals(fsUuid, MissingStorageFragment.this.mFsUuid) && MissingStorageFragment.this.isResumed()) {
                if (MissingStorageFragment.this.mStorageManager.findRecordByUuid(fsUuid) == null) {
                    MissingStorageFragment.this.getFragmentManager().popBackStack();
                    Log.i(MissingStorageFragment.TAG, "FsUuid " + MissingStorageFragment.this.mFsUuid + " vanished while resumed");
                    return;
                }
                MissingStorageFragment.this.refresh();
            }
        }

        public void onVolumeRecordChanged(VolumeRecord rec) {
            if (TextUtils.equals(rec.getFsUuid(), MissingStorageFragment.this.mFsUuid) && MissingStorageFragment.this.isResumed()) {
                MissingStorageFragment.this.refresh();
            }
        }
    }
}
