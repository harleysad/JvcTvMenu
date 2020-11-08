package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.net.wifi.WifiConfiguration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.connectivity.util.WifiSecurityUtil;

public class AddStartState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;
    private final StateMachine mStateMachine = ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class));
    private final UserChoiceInfo mUserChoiceInfo = ((UserChoiceInfo) ViewModelProviders.of(this.mActivity).get(UserChoiceInfo.class));

    public AddStartState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = null;
        int wifiSecurity = this.mUserChoiceInfo.getWifiSecurity();
        WifiConfiguration configuration = this.mUserChoiceInfo.getWifiConfiguration();
        if ((wifiSecurity != 1 || !TextUtils.isEmpty(configuration.wepKeys[0])) && (WifiSecurityUtil.isOpen(wifiSecurity) || !TextUtils.isEmpty(configuration.preSharedKey))) {
            this.mStateMachine.getListener().onComplete(5);
        } else {
            this.mStateMachine.getListener().onComplete(7);
        }
    }

    public void processBackward() {
        this.mStateMachine.back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }
}
