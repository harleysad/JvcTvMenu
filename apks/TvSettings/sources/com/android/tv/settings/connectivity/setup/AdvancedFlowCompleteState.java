package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;

public class AdvancedFlowCompleteState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;
    private final StateMachine mStateMachine = ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class));

    public AdvancedFlowCompleteState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = null;
        this.mStateMachine.getListener().onComplete(25);
    }

    public void processBackward() {
        this.mStateMachine.back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }
}
