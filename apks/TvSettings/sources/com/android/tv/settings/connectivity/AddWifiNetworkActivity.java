package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.subtitle.Cea708CCParser;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.setup.AdvancedWifiOptionsFlow;
import com.android.tv.settings.connectivity.setup.ChooseSecurityState;
import com.android.tv.settings.connectivity.setup.ConnectAuthFailureState;
import com.android.tv.settings.connectivity.setup.ConnectFailedState;
import com.android.tv.settings.connectivity.setup.ConnectRejectedByApState;
import com.android.tv.settings.connectivity.setup.ConnectState;
import com.android.tv.settings.connectivity.setup.ConnectTimeOutState;
import com.android.tv.settings.connectivity.setup.EnterPasswordState;
import com.android.tv.settings.connectivity.setup.EnterSsidState;
import com.android.tv.settings.connectivity.setup.OptionsOrConnectState;
import com.android.tv.settings.connectivity.setup.SuccessState;
import com.android.tv.settings.connectivity.setup.UserChoiceInfo;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.core.instrumentation.InstrumentedActivity;

public class AddWifiNetworkActivity extends InstrumentedActivity implements State.FragmentChangeListener {
    private static final String TAG = "AddWifiNetworkActivity";
    private State mChooseSecurityState;
    private State mConnectAuthFailureState;
    private State mConnectFailedState;
    private State mConnectRejectedByApState;
    private State mConnectState;
    private State mConnectTimeOutState;
    private State mEnterAdvancedFlowOrRetryState;
    private State mEnterPasswordState;
    private State mEnterSsidState;
    private State mFinishState;
    private State mOptionsOrConnectState;
    private StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new StateMachine.Callback() {
        public void onFinish(int result) {
            AddWifiNetworkActivity.this.setResult(result);
            AddWifiNetworkActivity.this.finish();
        }
    };
    private State mSuccessState;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_container);
        this.mStateMachine = (StateMachine) ViewModelProviders.of((FragmentActivity) this).get(StateMachine.class);
        this.mStateMachine.setCallback(this.mStateMachineCallback);
        ((UserChoiceInfo) ViewModelProviders.of((FragmentActivity) this).get(UserChoiceInfo.class)).getWifiConfiguration().hiddenSSID = true;
        this.mEnterSsidState = new EnterSsidState(this);
        this.mChooseSecurityState = new ChooseSecurityState(this);
        this.mEnterPasswordState = new EnterPasswordState(this);
        this.mConnectState = new ConnectState(this);
        this.mSuccessState = new SuccessState(this);
        this.mOptionsOrConnectState = new OptionsOrConnectState(this);
        this.mConnectTimeOutState = new ConnectTimeOutState(this);
        this.mConnectRejectedByApState = new ConnectRejectedByApState(this);
        this.mConnectFailedState = new ConnectFailedState(this);
        this.mConnectAuthFailureState = new ConnectAuthFailureState(this);
        this.mFinishState = new FinishState(this);
        this.mEnterAdvancedFlowOrRetryState = new EnterAdvancedFlowOrRetryState(this);
        AdvancedWifiOptionsFlow.createFlow(this, true, true, (NetworkConfiguration) null, this.mOptionsOrConnectState, this.mConnectState, 0);
        AdvancedWifiOptionsFlow.createFlow(this, true, true, (NetworkConfiguration) null, this.mEnterAdvancedFlowOrRetryState, this.mConnectState, 0);
        this.mStateMachine.addState(this.mEnterSsidState, 2, this.mChooseSecurityState);
        this.mStateMachine.addState(this.mChooseSecurityState, 18, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mChooseSecurityState, 7, this.mEnterPasswordState);
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
        this.mStateMachine.addState(this.mConnectAuthFailureState, 16, this.mEnterAdvancedFlowOrRetryState);
        this.mStateMachine.addState(this.mConnectRejectedByApState, 6, this.mFinishState);
        this.mStateMachine.addState(this.mEnterAdvancedFlowOrRetryState, 2, this.mEnterSsidState);
        this.mStateMachine.setStartState(this.mEnterSsidState);
        this.mStateMachine.start(true);
    }

    public int getMetricsCategory() {
        return Cea708CCParser.Const.CODE_C1_CW6;
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
}
