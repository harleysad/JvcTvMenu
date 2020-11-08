package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.WifiConfigHelper;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.List;

public class ChooseSecurityState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ChooseSecurityState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = new ChooseSecurityFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new ChooseSecurityFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ChooseSecurityFragment extends WifiConnectivityGuidedStepFragment {
        @VisibleForTesting
        static final long WIFI_SECURITY_TYPE_NONE = 100001;
        @VisibleForTesting
        static final long WIFI_SECURITY_TYPE_WEP = 100002;
        @VisibleForTesting
        static final long WIFI_SECURITY_TYPE_WPA = 100003;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.security_type), (String) null, (String) null, (Drawable) null);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        public void onCreateActions(List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getActivity();
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_security_type_none)).id(WIFI_SECURITY_TYPE_NONE)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_security_type_wep)).id(WIFI_SECURITY_TYPE_WEP)).build());
            actions.add(((GuidedAction.Builder) ((GuidedAction.Builder) new GuidedAction.Builder(context).title((int) R.string.wifi_security_type_wpa)).id(WIFI_SECURITY_TYPE_WPA)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == WIFI_SECURITY_TYPE_NONE) {
                this.mUserChoiceInfo.setWifiSecurity(0);
            } else if (action.getId() == WIFI_SECURITY_TYPE_WEP) {
                this.mUserChoiceInfo.setWifiSecurity(1);
            } else if (action.getId() == WIFI_SECURITY_TYPE_WPA) {
                this.mUserChoiceInfo.setWifiSecurity(2);
            }
            WifiConfigHelper.setConfigKeyManagementBySecurity(this.mUserChoiceInfo.getWifiConfiguration(), this.mUserChoiceInfo.getWifiSecurity());
            if (action.getId() == WIFI_SECURITY_TYPE_NONE) {
                this.mStateMachine.getListener().onComplete(18);
            } else {
                this.mStateMachine.getListener().onComplete(7);
            }
        }
    }
}
