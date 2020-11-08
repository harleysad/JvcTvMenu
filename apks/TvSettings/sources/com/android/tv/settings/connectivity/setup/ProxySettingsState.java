package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class ProxySettingsState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ProxySettingsState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ProxySettingsFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new ProxySettingsFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ProxySettingsFragment extends WifiConnectivityGuidedStepFragment {
        private static final long WIFI_ACTION_PROXY_MANUAL = 100002;
        private static final long WIFI_ACTION_PROXY_NONE = 100001;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_proxy_settings), getString(R.string.proxy_warning_limited_support), (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAdvancedOptionsFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(getActivity()).get(AdvancedOptionsFlowInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_action_proxy_none)).id(WIFI_ACTION_PROXY_NONE)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_action_proxy_manual)).id(WIFI_ACTION_PROXY_MANUAL)).build());
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            CharSequence title = null;
            if (this.mAdvancedOptionsFlowInfo.containsPage(2)) {
                title = this.mAdvancedOptionsFlowInfo.get(2);
            } else if (this.mAdvancedOptionsFlowInfo.getInitialProxyInfo() != null) {
                title = getString(R.string.wifi_action_proxy_manual);
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

        public void onGuidedActionClicked(GuidedAction action) {
            this.mAdvancedOptionsFlowInfo.put(2, action.getTitle());
            if (action.getId() == WIFI_ACTION_PROXY_NONE) {
                AdvancedOptionsFlowUtil.processProxySettings(getActivity());
                if (this.mAdvancedOptionsFlowInfo.isSettingsFlow()) {
                    this.mStateMachine.getListener().onComplete(23);
                } else {
                    this.mStateMachine.getListener().onComplete(19);
                }
            } else if (action.getId() == WIFI_ACTION_PROXY_MANUAL) {
                this.mStateMachine.getListener().onComplete(21);
            }
        }
    }
}
