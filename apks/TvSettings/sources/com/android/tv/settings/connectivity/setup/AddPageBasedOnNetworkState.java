package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.WifiConfigHelper;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.connectivity.util.WifiSecurityUtil;

public class AddPageBasedOnNetworkState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;
    private final StateMachine mStateMachine = ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class));
    private final UserChoiceInfo mUserChoiceInfo = ((UserChoiceInfo) ViewModelProviders.of(this.mActivity).get(UserChoiceInfo.class));

    public AddPageBasedOnNetworkState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = null;
        if (this.mUserChoiceInfo.choiceChosen(getString(R.string.other_network), 1)) {
            this.mUserChoiceInfo.getWifiConfiguration().hiddenSSID = true;
            this.mStateMachine.getListener().onComplete(8);
            return;
        }
        ScanResult scanResult = this.mUserChoiceInfo.getChosenNetwork();
        String chosenNetwork = this.mUserChoiceInfo.getChosenNetwork().SSID;
        WifiConfiguration prevWifiConfig = this.mUserChoiceInfo.getWifiConfiguration();
        if (this.mUserChoiceInfo.getPageSummary(2) != null && (prevWifiConfig == null || !chosenNetwork.equals(prevWifiConfig.getPrintableSsid()))) {
            this.mUserChoiceInfo.removePageSummary(2);
        }
        int wifiSecurity = WifiSecurityUtil.getSecurity(scanResult);
        WifiConfiguration wifiConfiguration = WifiConfigHelper.getConfiguration(this.mActivity, scanResult.SSID, wifiSecurity);
        this.mUserChoiceInfo.setWifiSecurity(wifiSecurity);
        this.mUserChoiceInfo.setWifiConfiguration(wifiConfiguration);
        if (WifiConfigHelper.isNetworkSaved(wifiConfiguration)) {
            this.mStateMachine.getListener().onComplete(9);
        } else {
            this.mStateMachine.getListener().onComplete(0);
        }
    }

    private String getString(int id) {
        return this.mActivity.getString(id);
    }

    public void processBackward() {
        this.mStateMachine.back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }
}
