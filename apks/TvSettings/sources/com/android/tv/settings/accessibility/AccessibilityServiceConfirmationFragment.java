package com.android.tv.settings.accessibility;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import com.android.tv.settings.R;
import java.util.List;

public class AccessibilityServiceConfirmationFragment extends GuidedStepFragment {
    private static final String ARG_COMPONENT = "component";
    private static final String ARG_ENABLING = "enabling";
    private static final String ARG_LABEL = "label";

    public interface OnAccessibilityServiceConfirmedListener {
        void onAccessibilityServiceConfirmed(ComponentName componentName, boolean z);
    }

    public static AccessibilityServiceConfirmationFragment newInstance(ComponentName cn, CharSequence label, boolean enabling) {
        Bundle args = new Bundle(3);
        prepareArgs(args, cn, label, enabling);
        AccessibilityServiceConfirmationFragment fragment = new AccessibilityServiceConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void prepareArgs(@NonNull Bundle args, ComponentName cn, CharSequence label, boolean enabling) {
        args.putParcelable("component", cn);
        args.putCharSequence(ARG_LABEL, label);
        args.putBoolean(ARG_ENABLING, enabling);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        CharSequence label = getArguments().getCharSequence(ARG_LABEL);
        if (getArguments().getBoolean(ARG_ENABLING)) {
            return new GuidanceStylist.Guidance(getString(R.string.system_accessibility_service_on_confirm_title, new Object[]{label}), getString(R.string.system_accessibility_service_on_confirm_desc, new Object[]{label}), (String) null, getActivity().getDrawable(R.drawable.ic_accessibility_new_132dp));
        }
        return new GuidanceStylist.Guidance(getString(R.string.system_accessibility_service_off_confirm_title, new Object[]{label}), getString(R.string.system_accessibility_service_off_confirm_desc, new Object[]{label}), (String) null, getActivity().getDrawable(R.drawable.ic_accessibility_new_132dp));
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getActivity();
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-4)).build());
        actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -4) {
            ComponentName component = (ComponentName) getArguments().getParcelable("component");
            Fragment fragment = getTargetFragment();
            boolean enabling = getArguments().getBoolean(ARG_ENABLING);
            if (fragment instanceof OnAccessibilityServiceConfirmedListener) {
                ((OnAccessibilityServiceConfirmedListener) fragment).onAccessibilityServiceConfirmed(component, enabling);
                getFragmentManager().popBackStack();
                return;
            }
            throw new IllegalStateException("Target fragment is not an OnAccessibilityServiceConfirmedListener");
        } else if (action.getId() == -5) {
            getFragmentManager().popBackStack();
        } else {
            super.onGuidedActionClicked(action);
        }
    }
}
