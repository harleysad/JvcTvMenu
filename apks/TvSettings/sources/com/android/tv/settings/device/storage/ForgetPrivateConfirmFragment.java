package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

public class ForgetPrivateConfirmFragment extends GuidedStepFragment {
    private static final int ACTION_ID_FORGET = 1;

    public static void prepareArgs(Bundle b, String fsUuid) {
        b.putString("android.os.storage.extra.FS_UUID", fsUuid);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_forget_confirm_title), getString(R.string.storage_wizard_forget_confirm_description), "", getActivity().getDrawable(R.drawable.ic_warning_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((CharSequence) getString(R.string.storage_wizard_forget_action))).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        long id = action.getId();
        if (id == -5) {
            getFragmentManager().popBackStack();
        } else if (id == 1) {
            ((StorageManager) getContext().getSystemService(StorageManager.class)).forgetVolume(getArguments().getString("android.os.storage.extra.FS_UUID"));
            getFragmentManager().popBackStack();
        }
    }
}
