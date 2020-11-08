package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;

public class OptionsOrConnectState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public OptionsOrConnectState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = null;
        StateMachine stateMachine = (StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class);
        if (((AdvancedOptionsFlowInfo) ViewModelProviders.of(this.mActivity).get(AdvancedOptionsFlowInfo.class)).canStart()) {
            stateMachine.getListener().onComplete(24);
        } else {
            stateMachine.getListener().onComplete(5);
        }
    }

    public void processBackward() {
        this.mFragment = null;
        ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class)).back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }
}
