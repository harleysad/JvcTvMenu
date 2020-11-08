package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class ProxyHostNameState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ProxyHostNameState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ProxyHostNameFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, true);
    }

    public void processBackward() {
        this.mFragment = new ProxyHostNameFragment();
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, false);
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ProxyHostNameFragment extends WifiConnectivityGuidedStepFragment {
        private GuidedAction mAction;
        private AdvancedOptionsFlowInfo mAdvancedOptionsFlowInfo;
        private StateMachine mStateMachine;

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_wifi_proxy_hostname), getString(R.string.proxy_hostname_description), (String) null, (Drawable) null);
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

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            String title = getString(R.string.proxy_hostname_hint);
            if (this.mAdvancedOptionsFlowInfo.containsPage(3)) {
                title = this.mAdvancedOptionsFlowInfo.get(3);
            } else if (this.mAdvancedOptionsFlowInfo.getInitialProxyInfo() != null) {
                title = this.mAdvancedOptionsFlowInfo.getInitialProxyInfo().getHost();
            }
            this.mAction = ((GuidedAction.Builder) ((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(getActivity()).title((CharSequence) title)).editable(true)).id(-7)).build();
            actions.add(this.mAction);
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            openInEditMode(this.mAction);
        }

        public long onGuidedActionEditedAndProceed(GuidedAction action) {
            if (action.getId() == -7) {
                this.mAdvancedOptionsFlowInfo.put(3, action.getTitle());
                this.mStateMachine.getListener().onComplete(2);
            }
            return action.getId();
        }
    }
}
