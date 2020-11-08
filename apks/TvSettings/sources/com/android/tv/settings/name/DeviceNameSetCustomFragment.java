package com.android.tv.settings.name;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;
import com.android.tv.settings.name.setup.DeviceNameFlowStartActivity;
import com.android.tv.settings.util.GuidedActionsAlignUtil;
import java.util.List;

public class DeviceNameSetCustomFragment extends GuidedStepFragment {
    private GuidedAction mEditAction;

    public static DeviceNameSetCustomFragment newInstance() {
        return new DeviceNameSetCustomFragment();
    }

    public GuidanceStylist onCreateGuidanceStylist() {
        return GuidedActionsAlignUtil.createGuidanceStylist();
    }

    public GuidedActionsStylist onCreateActionsStylist() {
        return GuidedActionsAlignUtil.createNoBackgroundGuidedActionsStylist();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return GuidedActionsAlignUtil.createView(super.onCreateView(inflater, container, savedInstanceState), this);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.select_device_name_title, new Object[]{Build.MODEL}), getString(R.string.select_device_name_description), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        this.mEditAction = ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().editable(true)).editTitle((CharSequence) "")).build();
        actions.add(this.mEditAction);
    }

    public void onResume() {
        super.onResume();
        openInEditMode(this.mEditAction);
    }

    /* access modifiers changed from: protected */
    public void onProvideFragmentTransitions() {
        setEnterTransition((Transition) null);
    }

    public long onGuidedActionEditedAndProceed(GuidedAction action) {
        CharSequence name = action.getEditTitle();
        if (TextUtils.isGraphic(name)) {
            DeviceManager.setDeviceName(getActivity(), name.toString());
            getActivity().setResult(-1);
            if (getActivity() instanceof DeviceNameFlowStartActivity) {
                ((DeviceNameFlowStartActivity) getActivity()).setResultOk(true);
            }
            DeviceNameSuggestionStatus.getInstance(getActivity().getApplicationContext()).setFinished();
            getActivity().finish();
            return super.onGuidedActionEditedAndProceed(action);
        }
        popBackStackToGuidedStepFragment(DeviceNameSetCustomFragment.class, 1);
        return -5;
    }

    public void onGuidedActionEditCanceled(GuidedAction action) {
        popBackStackToGuidedStepFragment(DeviceNameSetCustomFragment.class, 1);
    }
}
