package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class NetworkPrefixLengthState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public NetworkPrefixLengthState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new NetworkPrefixLengthFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new NetworkPrefixLengthFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class NetworkPrefixLengthFragment extends WifiConnectivityGuidedStepFragment {
        private GuidedAction mAction;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_network_prefix_length), getString(R.string.wifi_network_prefix_length_description), (String) null, (Drawable) null);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                public int onProvideItemLayoutId() {
                    return R.layout.setup_text_input_item;
                }
            };
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAdvancedOptionsFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(getActivity()).get(AdvancedOptionsFlowInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            String title = getString(R.string.wifi_network_prefix_length_hint);
            if (this.mAdvancedOptionsFlowInfo.containsPage(8)) {
                title = this.mAdvancedOptionsFlowInfo.get(8);
            } else if (this.mAdvancedOptionsFlowInfo.getInitialLinkAddress() != null) {
                title = Integer.toString(this.mAdvancedOptionsFlowInfo.getInitialLinkAddress().getNetworkPrefixLength());
            }
            this.mAction = ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((CharSequence) title)).editable(true)).id(-7)).build();
            actions.add(this.mAction);
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            openInEditMode(this.mAction);
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            if (action.getId() == -7) {
                this.mAdvancedOptionsFlowInfo.put(8, action.getTitle());
                this.mStateMachine.getListener().onComplete(2);
            }
            return action.getId();
        }
    }
}
