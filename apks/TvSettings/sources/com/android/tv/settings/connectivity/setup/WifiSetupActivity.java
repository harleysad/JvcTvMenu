package com.android.tv.settings.connectivity.setup;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.android.settingslib.wifi.WifiTracker;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.NetworkConfiguration;
import com.android.tv.settings.connectivity.setup.SelectWifiState;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import com.android.tv.settings.util.ThemeHelper;
import com.android.tv.settings.util.TransitionUtils;

public class WifiSetupActivity extends FragmentActivity implements State.FragmentChangeListener {
    private static final String EXTRA_MOVING_FORWARD = "movingForward";
    private static final String EXTRA_SHOW_SKIP_NETWORK = "extra_show_skip_network";
    private static final String EXTRA_SHOW_SUMMARY = "extra_show_summary";
    private static final String TAG = "WifiSetupActivity";
    private State mAddPageBasedOnNetworkChoiceState;
    private State mAddStartState;
    private State mChooseSecurityState;
    private State mConnectAuthFailureState;
    private State mConnectFailedState;
    private State mConnectRejectedByApState;
    private State mConnectState;
    private State mConnectTimeOutState;
    private State mEnterPasswordState;
    private State mEnterSsidState;
    private State mKnownNetworkState;
    /* access modifiers changed from: private */
    public NetworkListInfo mNetworkListInfo;
    private State mOptionsOrConnectState;
    /* access modifiers changed from: private */
    public boolean mResultOk = false;
    /* access modifiers changed from: private */
    public State mSelectWifiState;
    private boolean mShowFirstFragmentForwards;
    /* access modifiers changed from: private */
    public StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new StateMachine.Callback() {
        public void onFinish(int result) {
            boolean z = true;
            WifiSetupActivity.this.setResult(result, new Intent().putExtra("user_initiated", true));
            WifiSetupActivity wifiSetupActivity = WifiSetupActivity.this;
            if (result != -1) {
                z = false;
            }
            boolean unused = wifiSetupActivity.mResultOk = z;
            WifiSetupActivity.this.finish();
        }
    };
    private State mSuccessState;
    private State mSummaryConnectedNonWifiState;
    private State mSummaryConnectedWifiState;
    private State mSummaryNotConnectedState;
    private UserChoiceInfo mUserChoiceInfo;
    private WifiTracker mWifiTracker;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_container);
        ObjectAnimator animator = TransitionUtils.createActivityFadeInAnimator(getResources(), true);
        animator.setTarget(findViewById(R.id.wifi_container));
        animator.start();
        this.mStateMachine = (StateMachine) ViewModelProviders.of((FragmentActivity) this).get(StateMachine.class);
        this.mStateMachine.setCallback(this.mStateMachineCallback);
        this.mNetworkListInfo = (NetworkListInfo) ViewModelProviders.of((FragmentActivity) this).get(NetworkListInfo.class);
        this.mNetworkListInfo.initNetworkRefreshTime();
        this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of((FragmentActivity) this).get(UserChoiceInfo.class);
        this.mWifiTracker = new WifiTracker(this, new WifiTracker.WifiListener() {
            public void onWifiStateChanged(int state) {
            }

            public void onConnectedChanged() {
            }

            public void onAccessPointsChanged() {
                long currentTime = System.currentTimeMillis();
                if (WifiSetupActivity.this.mStateMachine.getCurrentState() == WifiSetupActivity.this.mSelectWifiState && currentTime >= WifiSetupActivity.this.mNetworkListInfo.getNextNetworkRefreshTime()) {
                    ((SelectWifiState.SelectWifiFragment) WifiSetupActivity.this.mSelectWifiState.getFragment()).updateNetworkList();
                    WifiSetupActivity.this.mNetworkListInfo.updateNextNetworkRefreshTime();
                }
            }
        }, true, true);
        this.mNetworkListInfo.setWifiTracker(this.mWifiTracker);
        boolean showSummary = getIntent().getBooleanExtra(EXTRA_SHOW_SUMMARY, false);
        this.mNetworkListInfo.setShowSkipNetwork(getIntent().getBooleanExtra(EXTRA_SHOW_SKIP_NETWORK, false));
        this.mShowFirstFragmentForwards = getIntent().getBooleanExtra(EXTRA_MOVING_FORWARD, true);
        this.mKnownNetworkState = new KnownNetworkState(this);
        this.mSelectWifiState = new SelectWifiState(this);
        this.mEnterSsidState = new EnterSsidState(this);
        this.mChooseSecurityState = new ChooseSecurityState(this);
        this.mEnterPasswordState = new EnterPasswordState(this);
        this.mConnectState = new ConnectState(this);
        this.mConnectTimeOutState = new ConnectTimeOutState(this);
        this.mConnectRejectedByApState = new ConnectRejectedByApState(this);
        this.mConnectFailedState = new ConnectFailedState(this);
        this.mConnectAuthFailureState = new ConnectAuthFailureState(this);
        this.mSuccessState = new SuccessState(this);
        this.mOptionsOrConnectState = new OptionsOrConnectState(this);
        this.mAddPageBasedOnNetworkChoiceState = new AddPageBasedOnNetworkState(this);
        this.mAddStartState = new AddStartState(this);
        this.mSelectWifiState = new SelectWifiState(this);
        if (showSummary) {
            addSummaryState();
        } else {
            this.mStateMachine.setStartState(this.mSelectWifiState);
        }
        AdvancedWifiOptionsFlow.createFlow(this, true, false, (NetworkConfiguration) null, this.mOptionsOrConnectState, this.mConnectState, 0);
        this.mStateMachine.addState(this.mKnownNetworkState, 0, this.mAddStartState);
        this.mStateMachine.addState(this.mKnownNetworkState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mAddStartState, 7, this.mEnterPasswordState);
        this.mStateMachine.addState(this.mAddStartState, 5, this.mConnectState);
        this.mStateMachine.addState(this.mSelectWifiState, 17, this.mAddPageBasedOnNetworkChoiceState);
        this.mStateMachine.addState(this.mAddPageBasedOnNetworkChoiceState, 8, this.mEnterSsidState);
        this.mStateMachine.addState(this.mAddPageBasedOnNetworkChoiceState, 9, this.mKnownNetworkState);
        this.mStateMachine.addState(this.mAddPageBasedOnNetworkChoiceState, 0, this.mAddStartState);
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
        this.mStateMachine.addState(this.mConnectFailedState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mConnectTimeOutState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectTimeOutState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mConnectRejectedByApState, 16, this.mOptionsOrConnectState);
        this.mStateMachine.addState(this.mConnectRejectedByApState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mConnectAuthFailureState, 16, this.mAddPageBasedOnNetworkChoiceState);
        this.mStateMachine.addState(this.mConnectAuthFailureState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mSummaryNotConnectedState, 6, this.mSelectWifiState);
        this.mStateMachine.addState(this.mSummaryConnectedWifiState, 6, this.mSelectWifiState);
        this.mStateMachine.start(this.mShowFirstFragmentForwards);
    }

    public void onBackPressed() {
        this.mStateMachine.back();
    }

    public void onResume() {
        super.onResume();
        this.mWifiTracker.onStart();
    }

    public void onPause() {
        super.onPause();
        this.mWifiTracker.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mWifiTracker.onDestroy();
    }

    public void finish() {
        Animator animator;
        if (!ThemeHelper.fromSetupWizard(getIntent())) {
            animator = TransitionUtils.createActivityFadeOutAnimator(getResources(), true);
        } else if (this.mResultOk) {
            animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_open_out);
        } else {
            animator = AnimatorInflater.loadAnimator(this, R.anim.setup_fragment_close_out);
        }
        animator.setTarget(findViewById(R.id.wifi_container));
        animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                WifiSetupActivity.this.doFinish();
            }

            public void onAnimationCancel(Animator animation) {
            }
        });
        animator.start();
    }

    /* access modifiers changed from: private */
    public void doFinish() {
        super.finish();
    }

    private void addSummaryState() {
        NetworkInfo currentConnection = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        boolean isConnected = currentConnection != null && currentConnection.isConnected();
        this.mSummaryConnectedWifiState = new SummaryConnectedWifiState(this);
        this.mSummaryConnectedNonWifiState = new SummaryConnectedNonWifiState(this);
        this.mSummaryNotConnectedState = new SummaryNotConnectedState(this);
        if (!isConnected) {
            this.mStateMachine.setStartState(this.mSummaryNotConnectedState);
        } else if (currentConnection.getType() == 1) {
            String connectedNetwork = WifiInfo.removeDoubleQuotes(this.mWifiTracker.getManager().getConnectionInfo().getSSID());
            if (connectedNetwork == null) {
                connectedNetwork = getString(R.string.wifi_summary_unknown_network);
            }
            this.mUserChoiceInfo.setConnectedNetwork(connectedNetwork);
            this.mStateMachine.setStartState(this.mSummaryConnectedWifiState);
        } else {
            this.mStateMachine.setStartState(this.mSummaryConnectedNonWifiState);
        }
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
