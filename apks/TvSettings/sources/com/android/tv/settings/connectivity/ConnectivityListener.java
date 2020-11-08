package com.android.tv.settings.connectivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.UiThread;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConnectivityListener implements WifiTracker.WifiListener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "ConnectivityListener";
    /* access modifiers changed from: private */
    public SignalStrength mCellSignalStrength;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final EthernetManager.Listener mEthernetListener;
    private final EthernetManager mEthernetManager;
    /* access modifiers changed from: private */
    public final Listener mListener;
    private final BroadcastReceiver mNetworkReceiver;
    private int mNetworkType;
    private final PhoneStateListener mPhoneStateListener;
    private boolean mStarted;
    private WifiNetworkListener mWifiListener;
    private final WifiManager mWifiManager;
    private int mWifiSignalStrength;
    private String mWifiSsid;
    private WifiTracker mWifiTracker;

    public interface Listener {
        void onConnectivityChange();
    }

    public interface WifiNetworkListener {
        void onWifiListChanged();
    }

    @Deprecated
    public ConnectivityListener(Context context, Listener listener) {
        this(context, listener, (Lifecycle) null);
    }

    public ConnectivityListener(Context context, Listener listener, Lifecycle lifecycle) {
        this.mNetworkReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (ConnectivityListener.this.mListener != null) {
                    ConnectivityListener.this.mListener.onConnectivityChange();
                }
            }
        };
        this.mEthernetListener = new EthernetManager.Listener() {
            public void onAvailabilityChanged(String iface, boolean isAvailable) {
                ConnectivityListener.this.mListener.onConnectivityChange();
            }
        };
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                SignalStrength unused = ConnectivityListener.this.mCellSignalStrength = signalStrength;
                ConnectivityListener.this.mListener.onConnectivityChange();
            }
        };
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
        this.mEthernetManager = (EthernetManager) this.mContext.getSystemService(EthernetManager.class);
        this.mListener = listener;
        if (this.mWifiManager != null) {
            if (lifecycle != null) {
                this.mWifiTracker = new WifiTracker(context, this, lifecycle, true, true);
            } else {
                this.mWifiTracker = new WifiTracker(context, this, true, true);
            }
        }
        updateConnectivityStatus();
    }

    @UiThread
    @Deprecated
    public void start() {
        if (!this.mStarted && this.mWifiTracker != null) {
            this.mWifiTracker.onStart();
        }
        onStart();
    }

    public void onStart() {
        if (!this.mStarted) {
            this.mStarted = true;
            updateConnectivityStatus();
            IntentFilter networkIntentFilter = new IntentFilter();
            networkIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
            networkIntentFilter.addAction("android.net.wifi.RSSI_CHANGED");
            networkIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            this.mContext.registerReceiver(this.mNetworkReceiver, networkIntentFilter);
            this.mEthernetManager.addListener(this.mEthernetListener);
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                telephonyManager.listen(this.mPhoneStateListener, 256);
            }
        }
    }

    @UiThread
    @Deprecated
    public void stop() {
        if (this.mStarted && this.mWifiTracker != null) {
            this.mWifiTracker.onStop();
        }
        onStop();
    }

    public void onStop() {
        if (this.mStarted) {
            this.mStarted = false;
            this.mContext.unregisterReceiver(this.mNetworkReceiver);
            this.mWifiListener = null;
            this.mEthernetManager.removeListener(this.mEthernetListener);
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                telephonyManager.listen(this.mPhoneStateListener, 0);
            }
        }
    }

    @Deprecated
    public void destroy() {
        if (this.mWifiTracker != null) {
            this.mWifiTracker.onDestroy();
        }
    }

    public void setWifiListener(WifiNetworkListener wifiListener) {
        this.mWifiListener = wifiListener;
    }

    public String getWifiIpAddress() {
        if (!isWifiConnected()) {
            return "";
        }
        int ip = this.mWifiManager.getConnectionInfo().getIpAddress();
        return String.format(Locale.US, "%d.%d.%d.%d", new Object[]{Integer.valueOf(ip & 255), Integer.valueOf((ip >> 8) & 255), Integer.valueOf((ip >> 16) & 255), Integer.valueOf((ip >> 24) & 255)});
    }

    @SuppressLint({"HardwareIds"})
    public String getWifiMacAddress() {
        if (isWifiConnected()) {
            return this.mWifiManager.getConnectionInfo().getMacAddress();
        }
        return "";
    }

    public boolean isEthernetConnected() {
        return this.mNetworkType == 9;
    }

    public boolean isWifiConnected() {
        if (this.mNetworkType == 1) {
            return true;
        }
        if (this.mWifiManager == null) {
            return false;
        }
        if (this.mWifiManager.getConnectionInfo().getNetworkId() != -1) {
            return true;
        }
        return false;
    }

    public boolean isCellConnected() {
        return this.mNetworkType == 0;
    }

    public boolean isEthernetAvailable() {
        return this.mConnectivityManager.isNetworkSupported(9) && this.mEthernetManager.getAvailableInterfaces().length > 0;
    }

    private Network getFirstEthernet() {
        for (Network network : this.mConnectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == 9) {
                return network;
            }
        }
        return null;
    }

    public String getEthernetIpAddress() {
        Network network = getFirstEthernet();
        if (network == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean gotAddress = false;
        for (LinkAddress linkAddress : this.mConnectivityManager.getLinkProperties(network).getLinkAddresses()) {
            if (gotAddress) {
                sb.append("\n");
            }
            sb.append(linkAddress.getAddress().getHostAddress());
            gotAddress = true;
        }
        if (gotAddress) {
            return sb.toString();
        }
        return null;
    }

    public int getWifiSignalStrength(int maxLevel) {
        if (this.mWifiManager != null) {
            return WifiManager.calculateSignalLevel(this.mWifiManager.getConnectionInfo().getRssi(), maxLevel);
        }
        return 0;
    }

    public int getCellSignalStrength() {
        if (!isCellConnected() || this.mCellSignalStrength == null) {
            return 0;
        }
        return this.mCellSignalStrength.getLevel();
    }

    public List<AccessPoint> getAvailableNetworks() {
        return this.mWifiTracker == null ? new ArrayList() : this.mWifiTracker.getAccessPoints();
    }

    public boolean isWifiEnabledOrEnabling() {
        return this.mWifiManager != null && (this.mWifiManager.getWifiState() == 3 || this.mWifiManager.getWifiState() == 2);
    }

    public void setWifiEnabled(boolean enable) {
        if (this.mWifiManager != null) {
            this.mWifiManager.setWifiEnabled(enable);
        }
    }

    private void updateConnectivityStatus() {
        NetworkInfo networkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            this.mNetworkType = -1;
            return;
        }
        int type = networkInfo.getType();
        if (type != 9) {
            int signalStrength = 0;
            switch (type) {
                case 0:
                    this.mNetworkType = 0;
                    return;
                case 1:
                    if (this.mWifiManager != null) {
                        this.mNetworkType = 1;
                        String ssid = getSsid();
                        if (!TextUtils.equals(this.mWifiSsid, ssid)) {
                            this.mWifiSsid = ssid;
                        }
                        WifiInfo wifiInfo = this.mWifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            signalStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
                        }
                        if (this.mWifiSignalStrength != signalStrength) {
                            this.mWifiSignalStrength = signalStrength;
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    this.mNetworkType = -1;
                    return;
            }
        } else {
            this.mNetworkType = 9;
        }
    }

    public void onWifiStateChanged(int state) {
        updateConnectivityStatus();
        if (this.mListener != null) {
            this.mListener.onConnectivityChange();
        }
    }

    public void onConnectedChanged() {
        updateConnectivityStatus();
        if (this.mListener != null) {
            this.mListener.onConnectivityChange();
        }
    }

    public void onAccessPointsChanged() {
        if (this.mWifiListener != null) {
            this.mWifiListener.onWifiListChanged();
        }
    }

    public String getSsid() {
        WifiInfo wifiInfo;
        if (this.mWifiManager == null || (wifiInfo = this.mWifiManager.getConnectionInfo()) == null) {
            return null;
        }
        String ssid = wifiInfo.getSSID();
        if (ssid != null) {
            return WifiInfo.removeDoubleQuotes(ssid);
        }
        return ssid;
    }
}
