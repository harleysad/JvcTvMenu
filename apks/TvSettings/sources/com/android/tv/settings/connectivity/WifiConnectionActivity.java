package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.settingslib.wifi.AccessPoint;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.setup.AddStartState;
import com.android.tv.settings.connectivity.setup.AdvancedWifiOptionsFlow;
import com.android.tv.settings.connectivity.setup.ConnectAuthFailureState;
import com.android.tv.settings.connectivity.setup.ConnectFailedState;
import com.android.tv.settings.connectivity.setup.ConnectRejectedByApState;
import com.android.tv.settings.connectivity.setup.ConnectState;
import com.android.tv.settings.connectivity.setup.ConnectTimeOutState;
import com.android.tv.settings.connectivity.setup.EnterPasswordState;
import com.android.tv.settings.connectivity.setup.KnownNetworkState;
import com.android.tv.settings.connectivity.setup.OptionsOrConnectState;
import com.android.tv.settings.connectivity.setup.SuccessState;
import com.android.tv.settings.connectivity.setup.UserChoiceInfo;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.connectivity.util.WifiSecurityUtil;
import com.android.tv.settings.core.instrumentation.InstrumentedActivity;

public class WifiConnectionActivity extends InstrumentedActivity implements State.FragmentChangeListener {
    private static final String EXTRA_WIFI_SECURITY_NAME = "wifi_security_name";
    private static final String EXTRA_WIFI_SSID = "wifi_ssid";
    private static final String TAG = "WifiConnectionActivity";
    private State mAddStartState;
    private WifiConfiguration mConfiguration;
    private State mConnectAuthFailureState;
    private State mConnectFailedState;
    private State mConnectRejectedByApState;
    private State mConnectState;
    private State mConnectTimeOutState;
    private State mEnterPasswordState;
    private State mFinishState;
    private State mKnownNetworkState;
    private State mOptionsOrConnectState;
    private StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new StateMachine.Callback() {
        public void onFinish(int result) {
            WifiConnectionActivity.this.setResult(result);
            WifiConnectionActivity.this.finish();
        }
    };
    private State mSuccessState;
    private int mWifiSecurity;

    public static Intent createIntent(Context context, AccessPoint result, int security) {
        return new Intent(context, WifiConnectionActivity.class).putExtra(EXTRA_WIFI_SSID, result.getSsidStr()).putExtra(EXTRA_WIFI_SECURITY_NAME, security);
    }

    public static Intent createIntent(Context context, AccessPoint result) {
        return createIntent(context, result, result.getSecurity());
    }

    public static Intent createIntent(Context context, WifiConfiguration configuration) {
        return new Intent(context, WifiConnectionActivity.class).putExtra(EXTRA_WIFI_SSID, configuration.getPrintableSsid()).putExtra(EXTRA_WIFI_SECURITY_NAME, WifiSecurityUtil.getSecurity(configuration));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_container);
        this.mStateMachine = (StateMachine) ViewModelProviders.of((FragmentActivity) this).get(StateMachine.class);
        this.mStateMachine.setCallback(this.mStateMachineCallback);
        this.mKnownNetworkState = new KnownNetworkState(this);
        this.mEnterPasswordState = new EnterPasswordState(this);
        this.mConnectState = new ConnectState(this);
        this.mConnectTimeOutState = new ConnectTimeOutState(this);
        this.mConnectRejectedByApState = new ConnectRejectedByApState(this);
        this.mConnectFailedState = new ConnectFailedState(this);
        this.mConnectAuthFailureState = new ConnectAuthFailureState(this);
        this.mSuccessState = new SuccessState(this);
        this.mOptionsOrConnectState = new OptionsOrConnectState(this);
        this.mAddStartState = new AddStartState(this);
        this.mFinishState = new FinishState(this);
        this.mStateMachine.addState(this.mKnownNetworkState, 0, this.mAddStartState);
        this.mStateMachine.addState(this.mKnownNetworkState, 6, this.mFinishState);
        this.mStateMachine.addState(this.mAddStartState, 7, this.mEnterPasswordState);
        this.mStateMachine.addState(this.mAddStartState, 5, this.mConnectState);
        this.mStateMachine.addState(this.mEnterPasswordState, 18, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mOptionsOrConnectState, 5, this.mConnectState);
        this.mStateMachine.addState(this.mConnectState, 10, this.mConnectRejectedByApState);
        this.mStateMachine.addState(this.mConnectState, 11, this.mConnectFailedState);
        this.mStateMachine.addState(this.mConnectState, 12, this.mConnectTimeOutState);
        this.mStateMachine.addState(this.mConnectState, 13, this.mConnectAuthFailureState);
        this.mStateMachine.addState(this.mConnectState, 14, this.mSuccessState);
        this.mStateMachine.addState(this.mConnectFailedState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectFailedState, 6, this.mFinishState);
        this.mStateMachine.addState(this.mConnectTimeOutState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectTimeOutState, 6, this.mFinishState);
        this.mStateMachine.addState(this.mConnectRejectedByApState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectRejectedByApState, 6, this.mFinishState);
        this.mStateMachine.addState(this.mConnectAuthFailureState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectAuthFailureState, 6, this.mFinishState);
        this.mWifiSecurity = getIntent().getIntExtra(EXTRA_WIFI_SECURITY_NAME, 0);
        this.mConfiguration = WifiConfigHelper.getConfiguration(this, getIntent().getStringExtra(EXTRA_WIFI_SSID), this.mWifiSecurity);
        AdvancedWifiOptionsFlow.createFlow(this, false, true, (NetworkConfiguration) null, this.mOptionsOrConnectState, this.mConnectState, 0);
        UserChoiceInfo userChoiceInfo = (UserChoiceInfo) ViewModelProviders.of((FragmentActivity) this).get(UserChoiceInfo.class);
        userChoiceInfo.setWifiConfiguration(this.mConfiguration);
        userChoiceInfo.setWifiSecurity(this.mWifiSecurity);
        if (this.mConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason() == 13) {
            this.mStateMachine.setStartState(this.mEnterPasswordState);
        } else if (WifiConfigHelper.isNetworkSaved(this.mConfiguration)) {
            this.mStateMachine.setStartState(this.mKnownNetworkState);
        } else {
            this.mStateMachine.setStartState(this.mAddStartState);
        }
        this.mStateMachine.start(true);
    }

    public void onBackPressed() {
        this.mStateMachine.back();
    }

    private void updateView(Fragment fragment, boolean movingForward) {
        if (fragment != null) {
            FragmentTransaction updateTransaction = getSupportFragmentManager().beginTransaction();
            if (movingForward) {
                updateTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            } else {
                updateTransaction.setTransition(8194);
            }
            updateTransaction.replace(R.id.wifi_container, fragment, TAG);
            updateTransaction.commit();
        }
    }

    public void onFragmentChange(Fragment newFragment, boolean movingForward) {
        updateView(newFragment, movingForward);
    }

    public int getMetricsCategory() {
        return 1331;
    }
}
