package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.TextUtils;
import com.android.tv.settings.R;
import java.util.Iterator;
import java.util.List;

public class FormatAsPublicStepFragment extends GuidedStepFragment {
    private static final int ACTION_ID_BACKUP = 1;
    private static final int ACTION_ID_FORMAT = 2;
    private String mDiskId;
    private String mVolumeId;

    public interface Callback {
        void onCancelFormatDialog();

        void onRequestFormatAsPublic(String str, String str2);
    }

    public static FormatAsPublicStepFragment newInstance(String diskId) {
        FormatAsPublicStepFragment fragment = new FormatAsPublicStepFragment();
        Bundle b = new Bundle(1);
        b.putString("android.os.storage.extra.DISK_ID", diskId);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mDiskId = getArguments().getString("android.os.storage.extra.DISK_ID");
        Iterator<VolumeInfo> it = ((StorageManager) getActivity().getSystemService(StorageManager.class)).getVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            VolumeInfo volume = it.next();
            if ((volume.getType() == 1 || volume.getType() == 0) && TextUtils.equals(volume.getDiskId(), this.mDiskId)) {
                this.mVolumeId = volume.getId();
                break;
            }
        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_format_as_public_title), getString(R.string.storage_wizard_format_as_public_description), "", getActivity().getDrawable(R.drawable.ic_warning_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
        if (!TextUtils.isEmpty(this.mVolumeId)) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((CharSequence) getString(R.string.storage_wizard_backup_apps_action))).build());
        }
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(2)).title((CharSequence) getString(R.string.storage_wizard_format_action))).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        long id = action.getId();
        if (id == -5) {
            ((Callback) getActivity()).onCancelFormatDialog();
        } else if (id == 1) {
            getFragmentManager().beginTransaction().replace(16908290, BackupAppsStepFragment.newInstance(this.mVolumeId)).addToBackStack((String) null).commit();
        } else if (id == 2) {
            ((Callback) getActivity()).onRequestFormatAsPublic(this.mDiskId, this.mVolumeId);
        }
    }
}
