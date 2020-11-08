package com.android.tv.settings.name;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.tv.settings.R;
import com.android.tv.settings.name.setup.DeviceNameFlowStartActivity;
import com.android.tv.settings.util.GuidedActionsAlignUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceNameSetFragment extends GuidedStepFragment {
    private ArrayList<String> mDeviceNames = new ArrayList<>();

    public static DeviceNameSetFragment newInstance() {
        return new DeviceNameSetFragment();
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
        return new GuidanceStylist.Guidance(getString(R.string.select_device_name_title, new Object[]{Build.MODEL}), getString(R.string.select_device_name_description), (String) null, (Drawable) null);
    }

    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        this.mDeviceNames.add(Build.MODEL);
        this.mDeviceNames.addAll(Arrays.asList(getResources().getStringArray(R.array.rooms)));
        String currentDeviceName = DeviceManager.getDeviceName(getActivity());
        if (currentDeviceName == null) {
            currentDeviceName = Build.MODEL;
        }
        int i = 0;
        if (this.mDeviceNames.indexOf(currentDeviceName) == -1) {
            this.mDeviceNames.add(0, currentDeviceName);
        }
        int length = this.mDeviceNames.size();
        while (true) {
            int i2 = i;
            if (i2 < length) {
                actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().title((CharSequence) this.mDeviceNames.get(i2))).id((long) i2)).build());
                i = i2 + 1;
            } else {
                actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder().title((CharSequence) getString(R.string.custom_room))).id((long) this.mDeviceNames.size())).build());
                super.onCreateActions(actions, savedInstanceState);
                return;
            }
        }
    }

    public void onResume() {
        super.onResume();
        int currentNamePosition = this.mDeviceNames.indexOf(DeviceManager.getDeviceName(getActivity()));
        if (currentNamePosition != -1) {
            setSelectedActionPosition(currentNamePosition);
        }
    }

    public void onGuidedActionClicked(GuidedAction action) {
        long id = action.getId();
        if (id < 0 || id > ((long) this.mDeviceNames.size())) {
            throw new IllegalStateException("Unknown action ID");
        } else if (id < ((long) this.mDeviceNames.size())) {
            DeviceManager.setDeviceName(getActivity(), this.mDeviceNames.get((int) id));
            if (getActivity() instanceof DeviceNameFlowStartActivity) {
                ((DeviceNameFlowStartActivity) getActivity()).setResultOk(true);
            }
            DeviceNameSuggestionStatus.getInstance(getActivity().getApplicationContext()).setFinished();
            getActivity().setResult(-1);
            getActivity().finish();
        } else if (id == ((long) this.mDeviceNames.size())) {
            GuidedStepFragment.add(getFragmentManager(), DeviceNameSetCustomFragment.newInstance());
        }
    }

    /* access modifiers changed from: protected */
    public void onProvideFragmentTransitions() {
        super.onProvideFragmentTransitions();
        setExitTransition((Transition) null);
    }
}
