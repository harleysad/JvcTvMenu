package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.setup.AdvancedWifiOptionsFlow;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.core.instrumentation.InstrumentedActivity;

public class EditProxySettingsActivity extends InstrumentedActivity implements State.FragmentChangeListener {
    private static final String EXTRA_NETWORK_ID = "network_id";
    private static final int NETWORK_ID_ETHERNET = -1;
    private static final String TAG = "EditProxySettings";
    private State mSaveFailedState;
    private State mSaveState;
    private State mSaveSuccessState;
    private StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new StateMachine.Callback() {
        public final void onFinish(int i) {
            EditProxySettingsActivity.lambda$new$0(EditProxySettingsActivity.this, i);
        }
    };

    public static Intent createIntent(Context context, int networkId) {
        return new Intent(context, EditProxySettingsActivity.class).putExtra(EXTRA_NETWORK_ID, networkId);
    }

    public static /* synthetic */ void lambda$new$0(EditProxySettingsActivity editProxySettingsActivity, int result) {
        editProxySettingsActivity.setResult(result);
        editProxySettingsActivity.finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        NetworkConfiguration netConfig;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_container);
        this.mStateMachine = (StateMachine) ViewModelProviders.of((FragmentActivity) this).get(StateMachine.class);
        this.mStateMachine.setCallback(this.mStateMachineCallback);
        this.mSaveState = new SaveState(this);
        this.mSaveSuccessState = new SaveSuccessState(this);
        this.mSaveFailedState = new SaveFailedState(this);
        int networkId = getIntent().getIntExtra(EXTRA_NETWORK_ID, -1);
        if (networkId == -1) {
            netConfig = new EthernetConfig(this);
            ((EthernetConfig) netConfig).load();
        } else {
            netConfig = new WifiConfig(this);
            ((WifiConfig) netConfig).load(networkId);
        }
        ((EditSettingsInfo) ViewModelProviders.of((FragmentActivity) this).get(EditSettingsInfo.class)).setNetworkConfiguration(netConfig);
        AdvancedWifiOptionsFlow.createFlow(this, false, true, netConfig, (State) null, this.mSaveState, 2);
        this.mStateMachine.addState(this.mSaveState, 14, this.mSaveSuccessState);
        this.mStateMachine.addState(this.mSaveState, 15, this.mSaveFailedState);
        this.mStateMachine.start(true);
    }

    public int getMetricsCategory() {
        return 603;
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
