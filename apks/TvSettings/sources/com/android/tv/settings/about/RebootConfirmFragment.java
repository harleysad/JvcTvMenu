package com.android.tv.settings.about;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.view.View;
import com.android.tv.settings.R;
import java.util.List;

@Keep
public class RebootConfirmFragment extends GuidedStepFragment {
    private static final String ARG_SAFE_MODE = "RebootConfirmFragment.safe_mode";

    public static RebootConfirmFragment newInstance(boolean safeMode) {
        Bundle args = new Bundle(1);
        args.putBoolean(ARG_SAFE_MODE, safeMode);
        RebootConfirmFragment fragment = new RebootConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSelectedActionPosition(1);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        if (getArguments().getBoolean(ARG_SAFE_MODE, false)) {
            return new GuidanceStylist.Guidance(getString(R.string.reboot_safemode_confirm), getString(R.string.reboot_safemode_desc), getString(R.string.about_preference), getActivity().getDrawable(R.drawable.ic_warning_132dp));
        }
        return new GuidanceStylist.Guidance(getString(R.string.system_reboot_confirm), (String) null, getString(R.string.about_preference), getActivity().getDrawable(R.drawable.ic_warning_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getActivity();
        if (getArguments().getBoolean(ARG_SAFE_MODE, false)) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-4)).title((int) R.string.reboot_safemode_action)).build());
        } else {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-4)).title((int) R.string.restart_button_label)).build());
        }
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            final boolean toSafeMode = getArguments().getBoolean(ARG_SAFE_MODE, false);
            final PowerManager pm = (PowerManager) getActivity().getSystemService("power");
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... params) {
                    if (toSafeMode) {
                        pm.rebootSafeMode();
                    } else {
                        pm.reboot((String) null);
                    }
                    return null;
                }
            }.execute(new Void[0]);
            return;
        }
        getFragmentManager().popBackStack();
    }
}
