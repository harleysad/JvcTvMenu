package com.android.tv.settings.connectivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.TwoStatePreference;
import android.util.Pair;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.connectivity.ConnectivityListener;
import com.mediatek.net.MtkNetworkManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.HashSet;
import java.util.Set;

@Keep
public class NetworkFragment extends SettingsPreferenceFragment implements ConnectivityListener.Listener, ConnectivityListener.WifiNetworkListener, AccessPoint.AccessPointListener {
    private static final int INITIAL_UPDATE_DELAY = 500;
    private static final String KEY_ETHERNET = "ethernet";
    private static final String KEY_ETHERNET_DHCP = "ethernet_dhcp";
    private static final String KEY_ETHERNET_PROXY = "ethernet_proxy";
    private static final String KEY_ETHERNET_STATUS = "ethernet_status";
    private static final String KEY_NETWORK_ETHERNET = "network_ethernet";
    private static final String KEY_NETWORK_WOL = "network_wol";
    private static final String KEY_NETWORK_WOW = "network_wow";
    private static final String KEY_WIFI_ADD = "wifi_add";
    private static final String KEY_WIFI_ALWAYS_SCAN = "wifi_always_scan";
    private static final String KEY_WIFI_COLLAPSE = "wifi_collapse";
    private static final String KEY_WIFI_ENABLE = "wifi_enable";
    private static final String KEY_WIFI_LIST = "wifi_list";
    private static final String KEY_WIFI_OTHER = "wifi_other";
    private Preference mAddPref;
    private TwoStatePreference mAlwaysScan;
    private Preference mCollapsePref;
    private ConnectivityListener mConnectivityListener;
    private TwoStatePreference mEnableWifiPref;
    private PreferenceCategory mEthernetCategory;
    private Preference mEthernetDhcpPref;
    private Preference mEthernetProxyPref;
    private Preference mEthernetStatusPref;
    private final Handler mHandler = new Handler();
    private Runnable mInitialUpdateWifiListRunnable = new Runnable() {
        public void run() {
            long unused = NetworkFragment.this.mNoWifiUpdateBeforeMillis = 0;
            NetworkFragment.this.updateWifiList();
        }
    };
    private boolean mIsWifiHardwarePresent;
    private TwoStatePreference mNetworkWol;
    private TwoStatePreference mNetworkWow;
    /* access modifiers changed from: private */
    public long mNoWifiUpdateBeforeMillis;
    private AccessPointPreference.UserBadgeCache mUserBadgeCache;
    private CollapsibleCategory mWifiNetworksCategory;
    private PreferenceCategory mWifiOther;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mIsWifiHardwarePresent = getContext().getPackageManager().hasSystemFeature("android.hardware.wifi");
        this.mConnectivityListener = new ConnectivityListener(getContext(), this, getLifecycle());
        this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getContext().getPackageManager());
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        this.mConnectivityListener.setWifiListener(this);
        this.mNoWifiUpdateBeforeMillis = SystemClock.elapsedRealtime() + 500;
        updateWifiList();
    }

    public void onResume() {
        super.onResume();
        updateConnectivity();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceComparisonCallback(new PreferenceManager.SimplePreferenceComparisonCallback());
        setPreferencesFromResource(R.xml.network, (String) null);
        this.mEnableWifiPref = (TwoStatePreference) findPreference(KEY_WIFI_ENABLE);
        this.mWifiNetworksCategory = (CollapsibleCategory) findPreference(KEY_WIFI_LIST);
        this.mCollapsePref = findPreference(KEY_WIFI_COLLAPSE);
        this.mAddPref = findPreference(KEY_WIFI_ADD);
        this.mAlwaysScan = (TwoStatePreference) findPreference(KEY_WIFI_ALWAYS_SCAN);
        this.mWifiOther = (PreferenceCategory) findPreference(KEY_WIFI_OTHER);
        this.mNetworkWow = (TwoStatePreference) findPreference(KEY_NETWORK_WOW);
        this.mNetworkWol = (TwoStatePreference) findPreference(KEY_NETWORK_WOL);
        this.mEthernetCategory = (PreferenceCategory) findPreference(KEY_ETHERNET);
        this.mEthernetStatusPref = findPreference(KEY_ETHERNET_STATUS);
        this.mEthernetProxyPref = findPreference(KEY_ETHERNET_PROXY);
        this.mEthernetProxyPref.setIntent(EditProxySettingsActivity.createIntent(getContext(), -1));
        this.mEthernetDhcpPref = findPreference(KEY_ETHERNET_DHCP);
        this.mEthernetDhcpPref.setIntent(EditIpSettingsActivity.createIntent(getContext(), -1));
        if (!this.mIsWifiHardwarePresent) {
            this.mEnableWifiPref.setVisible(false);
            this.mWifiOther.setVisible(false);
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey() == null) {
            return super.onPreferenceTreeClick(preference);
        }
        MtkLog.d("onPreferenceTreeClick ", "preference =  " + preference.getKey());
        String key = preference.getKey();
        char c = 65535;
        switch (key.hashCode()) {
            case -1940287741:
                if (key.equals(KEY_NETWORK_WOL)) {
                    c = 4;
                    break;
                }
                break;
            case -1940287730:
                if (key.equals(KEY_NETWORK_WOW)) {
                    c = 3;
                    break;
                }
                break;
            case -1340291977:
                if (key.equals(KEY_WIFI_ADD)) {
                    c = 6;
                    break;
                }
                break;
            case -283511677:
                if (key.equals(KEY_WIFI_ALWAYS_SCAN)) {
                    c = 2;
                    break;
                }
                break;
            case -49459878:
                if (key.equals(KEY_ETHERNET_STATUS)) {
                    c = 5;
                    break;
                }
                break;
            case 1796424173:
                if (key.equals(KEY_WIFI_ENABLE)) {
                    c = 0;
                    break;
                }
                break;
            case 1807469975:
                if (key.equals(KEY_WIFI_COLLAPSE)) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mConnectivityListener.setWifiEnabled(this.mEnableWifiPref.isChecked());
                if (this.mMetricsFeatureProvider != null) {
                    if (this.mEnableWifiPref.isChecked()) {
                        this.mMetricsFeatureProvider.action(getContext(), (int) Cea708CCParser.Const.CODE_C1_TGW, (Pair<Integer, Object>[]) new Pair[0]);
                    } else {
                        this.mMetricsFeatureProvider.action(getContext(), (int) Cea708CCParser.Const.CODE_C1_HDW, this.mConnectivityListener.isWifiConnected());
                    }
                }
                return true;
            case 1:
                boolean collapse = !this.mWifiNetworksCategory.isCollapsed();
                this.mCollapsePref.setTitle(collapse ? R.string.wifi_setting_see_all : R.string.wifi_setting_see_fewer);
                this.mWifiNetworksCategory.setCollapsed(collapse);
                return true;
            case 2:
                Settings.Global.putInt(getActivity().getContentResolver(), "wifi_scan_always_enabled", this.mAlwaysScan.isChecked() ? 1 : 0);
                return true;
            case 3:
                Settings.Global.putInt(getActivity().getContentResolver(), KEY_NETWORK_WOW, this.mNetworkWow.isChecked() ? 1 : 0);
                MtkLog.d("onPreferenceTreeClick", this.mNetworkWow.isChecked() + "");
                return true;
            case 4:
                Settings.Global.putInt(getActivity().getContentResolver(), KEY_NETWORK_WOL, this.mNetworkWol.isChecked() ? 1 : 0);
                return true;
            case 5:
                return true;
            case 6:
                this.mMetricsFeatureProvider.action((Context) getActivity(), (int) Cea708CCParser.Const.CODE_C1_CW6, (Pair<Integer, Object>[]) new Pair[0]);
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void updateConnectivity() {
        if (isAdded()) {
            boolean ethernetAvailable = true;
            boolean wifiEnabled = this.mIsWifiHardwarePresent && this.mConnectivityListener.isWifiEnabledOrEnabling();
            this.mEnableWifiPref.setChecked(wifiEnabled);
            this.mNetworkWow.setVisible(PartnerSettingsConfig.isMiscItemDisplay(KEY_NETWORK_WOW));
            this.mNetworkWol.setVisible(PartnerSettingsConfig.isMiscItemDisplay(KEY_NETWORK_WOL) && !PartnerSettingsConfig.isMiscItemDisplay(KEY_NETWORK_ETHERNET));
            if (this.mNetworkWow != null) {
                this.mNetworkWow.setEnabled(wifiEnabled);
            }
            this.mWifiNetworksCategory.setVisible(wifiEnabled);
            this.mCollapsePref.setVisible(wifiEnabled && this.mWifiNetworksCategory.shouldShowCollapsePref());
            this.mAddPref.setVisible(wifiEnabled);
            if (!wifiEnabled) {
                updateWifiList();
            }
            int scanAlwaysAvailable = 0;
            try {
                scanAlwaysAvailable = Settings.Global.getInt(getContext().getContentResolver(), "wifi_scan_always_enabled");
            } catch (Settings.SettingNotFoundException e) {
            }
            this.mAlwaysScan.setChecked(scanAlwaysAvailable == 1);
            SaveValue.getInstance(getContext());
            if (!SaveValue.readWorldBooleanValue(getContext(), KEY_NETWORK_WOW)) {
                SaveValue.getInstance(getContext());
                SaveValue.saveWorldBooleanValue(getContext(), KEY_NETWORK_WOW, true, true);
                if (this.mNetworkWow != null) {
                    this.mNetworkWow.setChecked(false);
                }
                MtkNetworkManager.getInstance().setEnableWoWL(false);
                Settings.Global.putInt(getActivity().getContentResolver(), KEY_NETWORK_WOW, 0);
            } else if (this.mNetworkWow != null) {
                this.mNetworkWow.setChecked(MtkNetworkManager.getInstance().isEnanbleWoWL());
            }
            SaveValue.getInstance(getContext());
            if (!SaveValue.readWorldBooleanValue(getContext(), KEY_NETWORK_WOL)) {
                SaveValue.getInstance(getContext());
                SaveValue.saveWorldBooleanValue(getContext(), KEY_NETWORK_WOL, true, true);
                if (this.mNetworkWol != null) {
                    this.mNetworkWol.setChecked(false);
                }
                MtkNetworkManager.getInstance().setEnableWol(false);
                Settings.Global.putInt(getActivity().getContentResolver(), KEY_NETWORK_WOL, 0);
            } else if (this.mNetworkWol != null) {
                this.mNetworkWol.setChecked(MtkNetworkManager.getInstance().isEnableWol());
            }
            if (!this.mConnectivityListener.isEthernetAvailable() || PartnerSettingsConfig.isMiscItemDisplay(KEY_NETWORK_ETHERNET)) {
                ethernetAvailable = false;
            }
            this.mEthernetCategory.setVisible(ethernetAvailable);
            this.mEthernetStatusPref.setVisible(ethernetAvailable);
            this.mEthernetProxyPref.setVisible(ethernetAvailable);
            this.mEthernetDhcpPref.setVisible(ethernetAvailable);
            if (ethernetAvailable) {
                this.mEthernetStatusPref.setTitle(this.mConnectivityListener.isEthernetConnected() ? R.string.connected : R.string.not_connected);
                this.mEthernetStatusPref.setSummary((CharSequence) this.mConnectivityListener.getEthernetIpAddress());
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateWifiList() {
        if (isAdded()) {
            if (!this.mIsWifiHardwarePresent || !this.mConnectivityListener.isWifiEnabledOrEnabling()) {
                this.mWifiNetworksCategory.removeAll();
                this.mNoWifiUpdateBeforeMillis = 0;
                return;
            }
            long now = SystemClock.elapsedRealtime();
            if (this.mNoWifiUpdateBeforeMillis > now) {
                this.mHandler.removeCallbacks(this.mInitialUpdateWifiListRunnable);
                this.mHandler.postDelayed(this.mInitialUpdateWifiListRunnable, this.mNoWifiUpdateBeforeMillis - now);
                return;
            }
            int existingCount = this.mWifiNetworksCategory.getRealPreferenceCount();
            Set<Preference> toRemove = new HashSet<>(existingCount);
            for (int i = 0; i < existingCount; i++) {
                toRemove.add(this.mWifiNetworksCategory.getPreference(i));
            }
            Context themedContext = getPreferenceManager().getContext();
            int index = 0;
            for (AccessPoint accessPoint : this.mConnectivityListener.getAvailableNetworks()) {
                accessPoint.setListener(this);
                AccessPointPreference pref = (AccessPointPreference) accessPoint.getTag();
                if (pref == null) {
                    pref = new AccessPointPreference(accessPoint, themedContext, this.mUserBadgeCache, false);
                    accessPoint.setTag(pref);
                } else {
                    toRemove.remove(pref);
                }
                if (accessPoint.isActive()) {
                    pref.setFragment(WifiDetailsFragment.class.getName());
                    WifiDetailsFragment.prepareArgs(pref.getExtras(), accessPoint);
                    pref.setIntent((Intent) null);
                } else {
                    pref.setFragment((String) null);
                    pref.setIntent(WifiConnectionActivity.createIntent(getContext(), accessPoint));
                }
                pref.setOrder(index);
                this.mWifiNetworksCategory.addPreference(pref);
                index++;
            }
            for (Preference preference : toRemove) {
                this.mWifiNetworksCategory.removePreference(preference);
            }
            this.mCollapsePref.setVisible(this.mWifiNetworksCategory.shouldShowCollapsePref());
        }
    }

    public void onConnectivityChange() {
        updateConnectivity();
    }

    public void onWifiListChanged() {
        updateWifiList();
    }

    public void onAccessPointChanged(AccessPoint accessPoint) {
        ((AccessPointPreference) accessPoint.getTag()).refresh();
    }

    public void onLevelChanged(AccessPoint accessPoint) {
        ((AccessPointPreference) accessPoint.getTag()).onLevelChanged();
    }

    public int getMetricsCategory() {
        return 746;
    }
}
