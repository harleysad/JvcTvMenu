package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.setup.MessageFragment;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;

public class SaveSuccessState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public SaveSuccessState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        this.mFragment = SaveSuccessFragment.newInstance(this.mActivity.getString(R.string.wifi_setup_save_success));
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, true);
    }

    public void processBackward() {
        ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class)).back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class SaveSuccessFragment extends MessageFragment {
        private static final String KEY_TIME_OUT_DURATION = "time_out_duration";
        private static final int MSG_TIME_OUT = 1;
        private static final int TIME_OUT_MS = 3000;
        private Handler mTimeoutHandler;

        public static SaveSuccessFragment newInstance(String title) {
            SaveSuccessFragment fragment = new SaveSuccessFragment();
            Bundle args = new Bundle();
            addArguments(args, title, false);
            fragment.setArguments(args);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState) {
            final StateMachine stateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            this.mTimeoutHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        stateMachine.finish(-1);
                    }
                }
            };
            super.onCreate(savedInstanceState);
        }

        public void onResume() {
            super.onResume();
            this.mTimeoutHandler.sendEmptyMessageDelayed(1, (long) getArguments().getInt(KEY_TIME_OUT_DURATION, 3000));
        }

        public void onPause() {
            super.onPause();
            this.mTimeoutHandler.removeMessages(1);
        }
    }
}
