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

public class ConnectFailedState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectFailedState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ConnectFailedFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class)).back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ConnectFailedFragment extends WifiConnectivityGuidedStepFragment {
        private static final int ACTION_ID_TRY_AGAIN = 100001;
        private static final int ACTION_ID_VIEW_AVAILABLE_NETWORK = 100002;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_could_not_connect, this.mUserChoiceInfo.getWifiConfiguration().getPrintableSsid()), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            super.onCreate(savedInstanceState);
        }

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_action_try_again)).id(100001)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_action_view_available_networks)).id(100002)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == 100001) {
                this.mStateMachine.getListener().onComplete(16);
            } else if (action.getId() == 100002) {
                this.mStateMachine.getListener().onComplete(6);
            }
        }
    }
}
