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

public class ProxyPortState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ProxyPortState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ProxyPortFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, true);
    }

    public void processBackward() {
        this.mFragment = new ProxyPortFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, false);
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ProxyPortFragment extends WifiConnectivityGuidedStepFragment {
        private GuidedAction mAction;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_proxy_port), getString(R.string.proxy_port_description), (String) null, (Drawable) null);
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
            String title = getString(R.string.proxy_port_hint);
            if (this.mAdvancedOptionsFlowInfo.containsPage(4)) {
                title = this.mAdvancedOptionsFlowInfo.get(4);
            } else if (this.mAdvancedOptionsFlowInfo.getInitialProxyInfo() != null) {
                title = Integer.toString(this.mAdvancedOptionsFlowInfo.getInitialProxyInfo().getPort());
            }
            this.mAction = ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((CharSequence) title)).editable(true)).id(-7)).build();
            actions.add(this.mAction);
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            openInEditMode(this.mAction);
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            this.mAdvancedOptionsFlowInfo.put(4, action.getTitle());
            if (action.getId() == -7) {
                this.mStateMachine.getListener().onComplete(2);
            }
            return action.getId();
        }
    }
}
