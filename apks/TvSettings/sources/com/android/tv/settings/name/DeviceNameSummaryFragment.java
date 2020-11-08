package com.android.tv.settings.name;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;
import com.android.tv.settings.util.GuidedActionsAlignUtil;
import java.util.List;

public class DeviceNameSummaryFragment extends GuidedStepFragment {
    public static DeviceNameSummaryFragment newInstance() {
        return new DeviceNameSummaryFragment();
    }

    public GuidanceStylist onCreateGuidanceStylist() {
        return GuidedActionsAlignUtil.createGuidanceStylist();
    }

    public GuidedActionsStylist onCreateActionsStylist() {
        return GuidedActionsAlignUtil.createGuidedActionsStylist();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return GuidedActionsAlignUtil.createView(super.onCreateView(inflater, container, savedInstanceState), this);
    }

    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.device_rename_title, new Object[]{Build.MODEL}), getString(R.string.device_rename_description, new Object[]{Build.MODEL, DeviceManager.getDeviceName(getActivity())}), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getActivity();
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-7)).title((int) R.string.change_setting)).build());
        actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).id(-5)).title((int) R.string.keep_settings)).build());
    }

    public void onGuidedActionClicked(GuidedAction action) {
        long actionId = action.getId();
        if (actionId == -7) {
            GuidedStepFragment.add(getFragmentManager(), DeviceNameSetFragment.newInstance());
        } else if (actionId == -5) {
            getActivity().finish();
        } else {
            throw new IllegalStateException("Unknown action");
        }
    }
}
