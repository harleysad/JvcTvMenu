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
import com.android.tv.settings.connectivity.util.AdvancedOptionsFlowUtil;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class Dns2State implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public Dns2State(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new Dns2Fragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new Dns2Fragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class Dns2Fragment extends WifiConnectivityGuidedStepFragment {
        private GuidedAction mAction;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_dns2), getString(R.string.wifi_dns2_description), (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAdvancedOptionsFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(getActivity()).get(AdvancedOptionsFlowInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                public int onProvideItemLayoutId() {
                    return R.layout.setup_text_input_item;
                }
            };
        }

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            String title = getString(R.string.wifi_dns2_hint);
            if (this.mAdvancedOptionsFlowInfo.containsPage(11)) {
                title = this.mAdvancedOptionsFlowInfo.get(11);
            } else if (this.mAdvancedOptionsFlowInfo.getInitialDns(1) != null) {
                title = this.mAdvancedOptionsFlowInfo.getInitialDns(1).getHostAddress();
            }
            this.mAction = ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((CharSequence) title)).editable(true)).id(-7)).build();
            actions.add(this.mAction);
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            openInEditMode(this.mAction);
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            if (action.getId() == -7) {
                this.mAdvancedOptionsFlowInfo.put(11, action.getTitle());
                if (AdvancedOptionsFlowUtil.processIpSettings(getActivity()) == 0) {
                    this.mStateMachine.getListener().onComplete(23);
                } else {
                    this.mStateMachine.getListener().onComplete(20);
                }
            }
            return action.getId();
        }
    }
}
