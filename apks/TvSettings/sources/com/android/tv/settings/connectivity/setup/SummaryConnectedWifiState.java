package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class SummaryConnectedWifiState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public SummaryConnectedWifiState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new SummaryConnectWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new SummaryConnectWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class SummaryConnectWifiFragment extends WifiConnectivityGuidedStepFragment {
        private static final int DO_NOT_CHANGE_NETWORK = 100003;
        private static final int WIFI_ACTION_CHANGE_NETWORK = 100004;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_setup_action_dont_change_network)).id(100003)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_setup_action_change_network)).id(100004)).build());
        }

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.wifi_setup_summary_title_connected, this.mUserChoiceInfo.getConnectedNetwork()), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onGuidedActionClicked(GuidedAction action) {
            long id = action.getId();
            if (id == 100003) {
                this.mStateMachine.finish(-1);
            } else if (id == 100004) {
                this.mStateMachine.getListener().onComplete(6);
            }
        }
    }
}
