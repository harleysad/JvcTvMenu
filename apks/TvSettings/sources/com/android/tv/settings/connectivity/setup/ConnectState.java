package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.android.settingslib.wifi.AccessPoint;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.ConnectivityListener;
import com.android.tv.settings.connectivity.WifiConfigHelper;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.lang.ref.WeakReference;
import java.util.List;

public class ConnectState implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectState(FragmentActivity wifiSetupActivity) {
        this.mActivity = wifiSetupActivity;
    }

    public void processForward() {
        WifiConfiguration wifiConfig = ((UserChoiceInfo) ViewModelProviders.of(this.mActivity).get(UserChoiceInfo.class)).getWifiConfiguration();
        this.mFragment = ConnectToWifiFragment.newInstance(this.mActivity.getString(R.string.wifi_connecting, new Object[]{wifiConfig.getPrintableSsid()}), true);
        if (!WifiConfigHelper.isNetworkSaved(wifiConfig)) {
            AdvancedOptionsFlowInfo advFlowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(this.mActivity).get(AdvancedOptionsFlowInfo.class);
            if (advFlowInfo.getIpConfiguration() != null) {
                wifiConfig.setIpConfiguration(advFlowInfo.getIpConfiguration());
            }
        }
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        ((StateMachine) ViewModelProviders.of(this.mActivity).get(StateMachine.class)).back();
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class ConnectToWifiFragment extends MessageFragment implements ConnectivityListener.WifiNetworkListener {
        @VisibleForTesting
        static final int CONNECTION_TIMEOUT = 60000;
        private static final boolean DEBUG = false;
        @VisibleForTesting
        static final int MSG_TIMEOUT = 1;
        private static final String TAG = "ConnectToWifiFragment";
        private ConnectivityListener mConnectivityListener;
        @VisibleForTesting
        Handler mHandler;
        @VisibleForTesting
        StateMachine mStateMachine;
        @VisibleForTesting
        WifiConfiguration mWifiConfiguration;
        @VisibleForTesting
        WifiManager mWifiManager;

        public static ConnectToWifiFragment newInstance(String title, boolean showProgressIndicator) {
            ConnectToWifiFragment fragment = new ConnectToWifiFragment();
            Bundle args = new Bundle();
            addArguments(args, title, showProgressIndicator);
            fragment.setArguments(args);
            return fragment;
        }

        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            this.mConnectivityListener = new ConnectivityListener(getActivity(), (ConnectivityListener.Listener) null);
            this.mConnectivityListener.start();
            this.mWifiConfiguration = ((UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class)).getWifiConfiguration();
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            this.mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService("wifi");
            this.mHandler = new MessageHandler(this);
            this.mConnectivityListener.setWifiListener(this);
        }

        public void onResume() {
            super.onResume();
            postTimeout();
            proceedDependOnNetworkState();
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public void proceedDependOnNetworkState() {
            if (isNetworkConnected()) {
                this.mWifiManager.disconnect();
            }
            this.mWifiManager.addNetwork(this.mWifiConfiguration);
            this.mWifiManager.connect(this.mWifiConfiguration, (WifiManager.ActionListener) null);
        }

        public void onDestroy() {
            if (!isNetworkConnected()) {
                this.mWifiManager.disconnect();
            }
            this.mConnectivityListener.stop();
            this.mConnectivityListener.destroy();
            this.mHandler.removeMessages(1);
            super.onDestroy();
        }

        public void onWifiListChanged() {
            List<AccessPoint> accessPointList = this.mConnectivityListener.getAvailableNetworks();
            if (accessPointList != null) {
                for (AccessPoint accessPoint : accessPointList) {
                    if (accessPoint != null && AccessPoint.convertToQuotedString(accessPoint.getSsidStr()).equals(this.mWifiConfiguration.SSID)) {
                        inferConnectionStatus(accessPoint);
                    }
                }
            }
        }

        private void inferConnectionStatus(AccessPoint accessPoint) {
            WifiConfiguration configuration = accessPoint.getConfig();
            if (configuration != null) {
                if (!configuration.getNetworkSelectionStatus().isNetworkEnabled()) {
                    int networkSelectionDisableReason = configuration.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                    if (networkSelectionDisableReason != 13) {
                        switch (networkSelectionDisableReason) {
                            case 2:
                                notifyListener(10);
                                break;
                            case 3:
                                break;
                            case 4:
                            case 5:
                                notifyListener(11);
                                break;
                        }
                    }
                    notifyListener(13);
                    accessPoint.clearConfig();
                } else if (isNetworkConnected()) {
                    notifyListener(14);
                }
            }
        }

        /* access modifiers changed from: private */
        public void notifyListener(int result) {
            if (this.mStateMachine.getCurrentState() instanceof ConnectState) {
                this.mStateMachine.getListener().onComplete(result);
            }
        }

        private NetworkInfo getActiveWifiNetworkInfo() {
            ConnectivityManager connMan = (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class);
            for (Network network : connMan.getAllNetworks()) {
                NetworkInfo networkInfo = connMan.getNetworkInfo(network);
                if (networkInfo.isConnected() && networkInfo.getType() == 1) {
                    return networkInfo;
                }
            }
            return null;
        }

        /* access modifiers changed from: private */
        public boolean isNetworkConnected() {
            WifiInfo currentConnection;
            NetworkInfo netInfo = getActiveWifiNetworkInfo();
            if (netInfo != null && netInfo.isConnected() && netInfo.getType() == 1 && (currentConnection = this.mWifiManager.getConnectionInfo()) != null && currentConnection.getSSID().equals(this.mWifiConfiguration.SSID)) {
                return true;
            }
            return false;
        }

        private void postTimeout() {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 60000);
        }

        private static class MessageHandler extends Handler {
            private final WeakReference<ConnectToWifiFragment> mFragmentRef;

            MessageHandler(ConnectToWifiFragment fragment) {
                this.mFragmentRef = new WeakReference<>(fragment);
            }

            public void handleMessage(Message msg) {
                ConnectToWifiFragment fragment = (ConnectToWifiFragment) this.mFragmentRef.get();
                if (fragment != null) {
                    if (fragment.isNetworkConnected()) {
                        fragment.notifyListener(14);
                    } else {
                        fragment.notifyListener(12);
                    }
                }
            }
        }
    }
}
