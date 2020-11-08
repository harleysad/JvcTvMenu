package com.android.tv.settings.system.development;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

@Keep
public class EnableDevelopmentDialog extends GuidedStepFragment {

    public interface Callback {
        void onEnableDevelopmentConfirm();
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.dev_settings_warning_title), getString(R.string.dev_settings_warning_message), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getActivity();
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-8)).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-9)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -8) {
            ((Callback) getTargetFragment()).onEnableDevelopmentConfirm();
            getFragmentManager().popBackStack();
            return;
        }
        getFragmentManager().popBackStack();
    }
}
