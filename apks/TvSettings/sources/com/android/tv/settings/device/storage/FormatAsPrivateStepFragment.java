package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

public class FormatAsPrivateStepFragment extends GuidedStepFragment {
    private static final int ACTION_ID_FORMAT = 1;

    public interface Callback {
        void onCancelFormatDialog();

        void onRequestFormatAsPrivate(String str);
    }

    public static FormatAsPrivateStepFragment newInstance(String diskId) {
        FormatAsPrivateStepFragment fragment = new FormatAsPrivateStepFragment();
        Bundle b = new Bundle(1);
        b.putString("android.os.storage.extra.DISK_ID", diskId);
        fragment.setArguments(b);
        return fragment;
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_format_as_private_title), getString(R.string.storage_wizard_format_as_private_description), "", getActivity().getDrawable(R.drawable.ic_warning_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-5)).build());
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getContext()).id(1)).title((CharSequence) getString(R.string.storage_wizard_format_action))).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        long id = action.getId();
        if (id == -5) {
            ((Callback) getActivity()).onCancelFormatDialog();
        } else if (id == 1) {
            ((Callback) getActivity()).onRequestFormatAsPrivate(getArguments().getString("android.os.storage.extra.DISK_ID"));
        }
    }
}
