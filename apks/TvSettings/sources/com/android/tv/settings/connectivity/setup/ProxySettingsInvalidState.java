package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class ProxySettingsInvalidState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ProxySettingsInvalidState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ProxySettingsInvalidFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new ProxySettingsInvalidFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ProxySettingsInvalidFragment extends WifiConnectivityGuidedStepFragment {
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_proxy_settings_invalid), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAdvancedOptionsFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(getActivity()).get(AdvancedOptionsFlowInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((CharSequence) getString(R.string.wifi_action_try_again))).id(-7)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -7) {
                this.mAdvancedOptionsFlowInfo.remove(2);
                this.mAdvancedOptionsFlowInfo.remove(3);
                this.mAdvancedOptionsFlowInfo.remove(4);
                this.mAdvancedOptionsFlowInfo.remove(5);
                this.mStateMachine.getListener().onComplete(2);
            }
        }
    }
}
