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
public class OemUnlockDialog extends GuidedStepFragment {

    public interface Callback {
        void onOemUnlockConfirm();
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.confirm_enable_oem_unlock_title), getString(R.string.confirm_enable_oem_unlock_text), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getContext();
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-4)).title((CharSequence) getString(R.string.device_apps_app_management_enable))).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            ((Callback) getTargetFragment()).onOemUnlockConfirm();
            getFragmentManager().popBackStack();
            return;
        }
        getFragmentManager().popBackStack();
    }
}
