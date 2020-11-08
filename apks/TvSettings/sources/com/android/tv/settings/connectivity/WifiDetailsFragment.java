package com.android.tv.settings.connectivity;

import android.content.Context;
import android.net.IpConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.connectivity.ConnectivityListener;
import java.util.Iterator;
import java.util.List;

public class WifiDetailsFragment extends SettingsPreferenceFragment implements ConnectivityListener.Listener, ConnectivityListener.WifiNetworkListener {
    private static final String ARG_ACCESS_POINT_STATE = "apBundle";
    private static final String KEY_CONNECTION_STATUS = "connection_status";
    private static final String KEY_FORGET_NETWORK = "forget_network";
    private static final String KEY_IP_ADDRESS = "ip_address";
    private static final String KEY_IP_SETTINGS = "ip_settings";
    private static final String KEY_MAC_ADDRESS = "mac_address";
    private static final String KEY_PROXY_SETTINGS = "proxy_settings";
    private static final String KEY_SIGNAL_STRENGTH = "signal_strength";
    private AccessPoint mAccessPoint;
    private Preference mConnectionStatusPref;
    private ConnectivityListener mConnectivityListener;
    private Preference mForgetNetworkPref;
    private Preference mIpAddressPref;
    private Preference mIpSettingsPref;
    private Preference mMacAddressPref;
    private Preference mProxySettingsPref;
    private Preference mSignalStrengthPref;

    public static void prepareArgs(@NonNull Bundle args, AccessPoint accessPoint) {
        Bundle apBundle = new Bundle();
        accessPoint.saveWifiState(apBundle);
        args.putParcelable(ARG_ACCESS_POINT_STATE, apBundle);
    }

    public int getMetricsCategory() {
        return 849;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mConnectivityListener = new ConnectivityListener(getContext(), this, getLifecycle());
        this.mAccessPoint = new AccessPoint(getContext(), getArguments().getBundle(ARG_ACCESS_POINT_STATE));
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        this.mConnectivityListener.setWifiListener(this);
    }

    public void onResume() {
        super.onResume();
        update();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.wifi_details, (String) null);
        getPreferenceScreen().setTitle(this.mAccessPoint.getSsid());
        this.mConnectionStatusPref = findPreference(KEY_CONNECTION_STATUS);
        this.mIpAddressPref = findPreference(KEY_IP_ADDRESS);
        this.mMacAddressPref = findPreference(KEY_MAC_ADDRESS);
        this.mSignalStrengthPref = findPreference(KEY_SIGNAL_STRENGTH);
        this.mProxySettingsPref = findPreference(KEY_PROXY_SETTINGS);
        this.mIpSettingsPref = findPreference(KEY_IP_SETTINGS);
        this.mForgetNetworkPref = findPreference(KEY_FORGET_NETWORK);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    public void onConnectivityChange() {
        update();
    }

    public void onWifiListChanged() {
        Iterator<AccessPoint> it = this.mConnectivityListener.getAvailableNetworks().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AccessPoint accessPoint = it.next();
            if (TextUtils.equals(this.mAccessPoint.getSsidStr(), accessPoint.getSsidStr()) && this.mAccessPoint.getSecurity() == accessPoint.getSecurity()) {
                this.mAccessPoint = accessPoint;
                break;
            }
        }
        update();
    }

    private void update() {
        if (isAdded()) {
            boolean active = this.mAccessPoint.isActive();
            this.mConnectionStatusPref.setSummary(active ? R.string.connected : R.string.not_connected);
            this.mIpAddressPref.setVisible(active);
            this.mMacAddressPref.setVisible(active);
            this.mSignalStrengthPref.setVisible(active);
            if (active) {
                this.mIpAddressPref.setSummary((CharSequence) this.mConnectivityListener.getWifiIpAddress());
                this.mMacAddressPref.setSummary((CharSequence) this.mConnectivityListener.getWifiMacAddress());
                this.mSignalStrengthPref.setSummary((CharSequence) getSignalStrength());
            }
            WifiConfiguration wifiConfiguration = this.mAccessPoint.getConfig();
            if (wifiConfiguration != null) {
                int networkId = wifiConfiguration.networkId;
                this.mProxySettingsPref.setSummary(wifiConfiguration.getProxySettings() == IpConfiguration.ProxySettings.NONE ? R.string.wifi_action_proxy_none : R.string.wifi_action_proxy_manual);
                this.mProxySettingsPref.setIntent(EditProxySettingsActivity.createIntent(getContext(), networkId));
                this.mIpSettingsPref.setSummary(wifiConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC ? R.string.wifi_action_static : R.string.wifi_action_dhcp);
                this.mIpSettingsPref.setIntent(EditIpSettingsActivity.createIntent(getContext(), networkId));
                this.mForgetNetworkPref.setFragment(ForgetNetworkConfirmFragment.class.getName());
                ForgetNetworkConfirmFragment.prepareArgs(this.mForgetNetworkPref.getExtras(), this.mAccessPoint);
            }
            boolean z = false;
            this.mProxySettingsPref.setVisible(wifiConfiguration != null);
            this.mIpSettingsPref.setVisible(wifiConfiguration != null);
            Preference preference = this.mForgetNetworkPref;
            if (wifiConfiguration != null) {
                z = true;
            }
            preference.setVisible(z);
        }
    }

    private String getSignalStrength() {
        String[] signalLevels = getResources().getStringArray(R.array.wifi_signal_strength);
        return signalLevels[this.mConnectivityListener.getWifiSignalStrength(signalLevels.length)];
    }

    public static class ForgetNetworkConfirmFragment extends GuidedStepFragment {
        private AccessPoint mAccessPoint;

        public static void prepareArgs(@NonNull Bundle args, AccessPoint accessPoint) {
            Bundle apBundle = new Bundle();
            accessPoint.saveWifiState(apBundle);
            args.putParcelable(WifiDetailsFragment.ARG_ACCESS_POINT_STATE, apBundle);
        }

        public void onCreate(Bundle savedInstanceState) {
            this.mAccessPoint = new AccessPoint(getContext(), getArguments().getBundle(WifiDetailsFragment.ARG_ACCESS_POINT_STATE));
            super.onCreate(savedInstanceState);
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.wifi_forget_network), getString(R.string.wifi_forget_network_description), this.mAccessPoint.getSsidStr(), getContext().getDrawable(R.drawable.ic_wifi_signal_4_white_132dp));
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            Context context = getContext();
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-4)).build());
            actions.add(((GuidedAction.Builder) new GuidedAction.Builder(context).clickAction(-5)).build());
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -4) {
                ((WifiManager) getContext().getSystemService("wifi")).forget(this.mAccessPoint.getConfig().networkId, (WifiManager.ActionListener) null);
            }
            getFragmentManager().popBackStack();
        }
    }
}
