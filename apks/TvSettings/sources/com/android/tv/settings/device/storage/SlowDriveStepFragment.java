package com.android.tv.settings.device.storage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

public class SlowDriveStepFragment extends GuidedStepFragment {

    public interface Callback {
        void onSlowDriveWarningComplete();
    }

    public static SlowDriveStepFragment newInstance() {
        return new SlowDriveStepFragment();
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.storage_wizard_format_slow_title), getString(R.string.storage_wizard_format_slow_summary), (String) null, getActivity().getDrawable(R.drawable.ic_error_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getContext()).clickAction(-4)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        ((Callback) getActivity()).onSlowDriveWarningComplete();
    }
}
