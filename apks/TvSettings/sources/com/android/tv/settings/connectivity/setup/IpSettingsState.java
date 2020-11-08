package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.IpConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.AdvancedOptionsFlowUtil;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class IpSettingsState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public IpSettingsState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new IpSettingsFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new IpSettingsFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class IpSettingsFragment extends WifiConnectivityGuidedStepFragment {
        private static final int WIFI_ACTION_DHCP = 100001;
        private static final int WIFI_ACTION_STATIC = 100002;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_ip_settings), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAdvancedOptionsFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(getActivity()).get(AdvancedOptionsFlowInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            CharSequence title;
            super.onViewCreated(view, savedInstanceState);
            if (this.mAdvancedOptionsFlowInfo.containsPage(6)) {
                title = this.mAdvancedOptionsFlowInfo.get(6);
            } else if (this.mAdvancedOptionsFlowInfo.getIpConfiguration().getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
                title = getString(R.string.wifi_action_static);
            } else {
                title = getString(R.string.wifi_action_dhcp);
            }
            moveToPosition(title);
        }

        private void moveToPosition(CharSequence title) {
            if (title != null) {
                for (int i = 0; i < getActions().size(); i++) {
                    if (TextUtils.equals(getActions().get(i).getTitle(), title)) {
                        setSelectedActionPosition(i);
                        return;
                    }
                }
            }
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((CharSequence) getString(R.string.wifi_action_dhcp))).id(100001)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((CharSequence) getString(R.string.wifi_action_static))).id(100002)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            this.mAdvancedOptionsFlowInfo.put(6, action.getTitle());
            if (action.getId() == 100001) {
                AdvancedOptionsFlowUtil.processIpSettings(getActivity());
                this.mStateMachine.getListener().onComplete(23);
            } else if (action.getId() == 100002) {
                this.mStateMachine.getListener().onComplete(2);
            }
        }
    }
}
