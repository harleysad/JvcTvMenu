package com.android.tv.settings.connectivity;

import android.arch.lifecycle.ViewModelProviders;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo;
import com.android.tv.settings.connectivity.setup.MessageFragment;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;

public class SaveState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public SaveState(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void processForward() {
        NetworkConfiguration networkConfig = ((EditSettingsInfo) ViewModelProviders.of(this.mActivity).get(EditSettingsInfo.class)).getNetworkConfiguration();
        networkConfig.setIpConfiguration(((AdvancedOptionsFlowInfo) ViewModelProviders.of(this.mActivity).get(AdvancedOptionsFlowInfo.class)).getIpConfiguration());
        this.mFragment = SaveWifiConfigurationFragment.newInstance(this.mActivity.getString(R.string.wifi_saving, new Object[]{networkConfig.getPrintableName()}));
        ((State.FragmentChangeListener) this.mActivity).onFragmentChange(this.mFragment, true);
    }

    public void processBackward() {
        ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class)).back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class SaveWifiConfigurationFragment extends MessageFragment implements WifiManager.ActionListener {
        public static SaveWifiConfigurationFragment newInstance(String title) {
            SaveWifiConfigurationFragment fragment = new SaveWifiConfigurationFragment();
            Bundle args = new Bundle();
            addArguments(args, title, true);
            fragment.setArguments(args);
            return fragment;
        }

        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            ((EditSettingsInfo) ViewModelProviders.of(getActivity()).get(EditSettingsInfo.class)).getNetworkConfiguration().save(this);
        }

        public void onSuccess() {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                ((StateMachine) ViewModelProviders.of(activity).get(StateMachine.class)).getListener().onComplete(14);
            }
        }

        public void onFailure(int reason) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                ((StateMachine) ViewModelProviders.of(activity).get(StateMachine.class)).getListener().onComplete(15);
            }
        }
    }
}
