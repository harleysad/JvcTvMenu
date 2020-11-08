package com.android.tv.settings.device.storage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.ProgressDialogFragment;
import java.util.List;

public class UnmountActivity extends Activity {
    public static final String EXTRA_VOLUME_DESC = "UnmountActivity.volumeDesc";
    private static final String TAG = "UnmountActivity";
    private final Handler mHandler = new Handler();
    private final BroadcastReceiver mUnmountReceiver = new UnmountReceiver();
    /* access modifiers changed from: private */
    public String mUnmountVolumeDesc;
    /* access modifiers changed from: private */
    public String mUnmountVolumeId;

    public static Intent getIntent(Context context, String volumeId, String volumeDesc) {
        Intent i = new Intent(context, UnmountActivity.class);
        i.putExtra("android.os.storage.extra.VOLUME_ID", volumeId);
        i.putExtra(EXTRA_VOLUME_DESC, volumeDesc);
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUnmountVolumeId = getIntent().getStringExtra("android.os.storage.extra.VOLUME_ID");
        this.mUnmountVolumeDesc = getIntent().getStringExtra(EXTRA_VOLUME_DESC);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mUnmountReceiver, new IntentFilter(SettingsStorageService.ACTION_UNMOUNT));
        if (savedInstanceState == null) {
            VolumeInfo volumeInfo = ((StorageManager) getSystemService(StorageManager.class)).findVolumeById(this.mUnmountVolumeId);
            if (volumeInfo == null) {
                finish();
            } else if (volumeInfo.getType() == 1) {
                getFragmentManager().beginTransaction().replace(16908290, UnmountPrivateStepFragment.newInstance(this.mUnmountVolumeId)).commit();
            } else {
                onRequestUnmount();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mUnmountReceiver);
    }

    public void onRequestUnmount() {
        getFragmentManager().beginTransaction().replace(16908290, UnmountProgressFragment.newInstance(this.mUnmountVolumeDesc)).commit();
        this.mHandler.post(new Runnable() {
            public void run() {
                SettingsStorageService.unmount(UnmountActivity.this, UnmountActivity.this.mUnmountVolumeId);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (((StorageManager) getSystemService(StorageManager.class)).findVolumeById(this.mUnmountVolumeId) == null) {
            finish();
        }
    }

    private class UnmountReceiver extends BroadcastReceiver {
        private UnmountReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), SettingsStorageService.ACTION_UNMOUNT) && TextUtils.equals(intent.getStringExtra("android.os.storage.extra.VOLUME_ID"), UnmountActivity.this.mUnmountVolumeId)) {
                if (Boolean.valueOf(intent.getBooleanExtra(SettingsStorageService.EXTRA_SUCCESS, false)).booleanValue()) {
                    Toast.makeText(context, UnmountActivity.this.getString(R.string.storage_unmount_success, new Object[]{UnmountActivity.this.mUnmountVolumeDesc}), 0).show();
                } else {
                    Toast.makeText(context, UnmountActivity.this.getString(R.string.storage_unmount_failure, new Object[]{UnmountActivity.this.mUnmountVolumeDesc}), 0).show();
                }
                UnmountActivity.this.finish();
            }
        }
    }

    public static class UnmountPrivateStepFragment extends GuidedStepFragment {
        private static final int ACTION_ID_UNMOUNT = 1;

        public static UnmountPrivateStepFragment newInstance(String volumeId) {
            UnmountPrivateStepFragment fragment = new UnmountPrivateStepFragment();
            Bundle b = new Bundle(1);
            b.putString("android.os.storage.extra.VOLUME_ID", volumeId);
            fragment.setArguments(b);
            return fragment;
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_eject_private_title), getString(R.string.storage_wizard_eject_private_description), "", getActivity().getDrawable(R.drawable.ic_storage_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((CharSequence) getString(R.string.storage_eject))).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            long id = action.getId();
            if (id == -5) {
                if (!getFragmentManager().popBackStackImmediate()) {
                    getActivity().finish();
                }
            } else if (id == 1) {
                ((UnmountActivity) getActivity()).onRequestUnmount();
            }
        }
    }

    public static class UnmountProgressFragment extends ProgressDialogFragment {
        private static final String ARG_DESCRIPTION = "description";

        public static UnmountProgressFragment newInstance(CharSequence description) {
            Bundle b = new Bundle(1);
            b.putCharSequence(ARG_DESCRIPTION, description);
            UnmountProgressFragment fragment = new UnmountProgressFragment();
            fragment.setArguments(b);
            return fragment;
        }

        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            CharSequence description = getArguments().getCharSequence(ARG_DESCRIPTION);
            setTitle((CharSequence) getActivity().getString(R.string.storage_wizard_eject_progress_title, new Object[]{description}));
        }
    }
}
