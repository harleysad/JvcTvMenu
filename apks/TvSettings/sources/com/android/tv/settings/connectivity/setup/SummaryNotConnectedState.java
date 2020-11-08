package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
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

public class SummaryNotConnectedState implements State {
    private FragmentActivity mActivity;
    private Fragment mFragment;

    public SummaryNotConnectedState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new SummaryNotConnectedWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new SummaryNotConnectedWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class SummaryNotConnectedWifiFragment extends WifiConnectivityGuidedStepFragment {
        private static final int ACTION_OK = 100001;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((int) R.string.wifi_action_ok)).build());
        }

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.wifi_summary_title_not_connected), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == 100001) {
                this.mUserChoiceInfo.removePageSummary(1);
                this.mStateMachine.getListener().onComplete(6);
            }
        }
    }
}
