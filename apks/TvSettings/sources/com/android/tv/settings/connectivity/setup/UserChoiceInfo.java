package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModel;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

public class UserChoiceInfo extends ViewModel {
    public static final int PASSWORD = 2;
    public static final int SECURITY = 3;
    public static final int SELECT_WIFI = 1;
    public static final int SSID = 4;
    private ScanResult mChosenNetwork;
    private String mConnectedNetwork;
    private HashMap<Integer, CharSequence> mDataSummary = new HashMap<>();
    private boolean mIsPasswordHidden = false;
    private WifiConfiguration mWifiConfiguration = new WifiConfiguration();
    private int mWifiSecurity;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PAGE {
    }

    public void put(int page, String info) {
        this.mDataSummary.put(Integer.valueOf(page), info);
    }

    public boolean choiceChosen(CharSequence choice, int page) {
        if (!this.mDataSummary.containsKey(Integer.valueOf(page))) {
            return false;
        }
        return TextUtils.equals(choice, this.mDataSummary.get(Integer.valueOf(page)));
    }

    public CharSequence getPageSummary(int page) {
        if (!this.mDataSummary.containsKey(Integer.valueOf(page))) {
            return null;
        }
        return this.mDataSummary.get(Integer.valueOf(page));
    }

    public void removePageSummary(int page) {
        this.mDataSummary.remove(Integer.valueOf(page));
    }

    public ScanResult getChosenNetwork() {
        return this.mChosenNetwork;
    }

    public void setChosenNetwork(ScanResult result) {
        this.mChosenNetwork = result;
    }

    public WifiConfiguration getWifiConfiguration() {
        return this.mWifiConfiguration;
    }

    public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.mWifiConfiguration = wifiConfiguration;
    }

    public int getWifiSecurity() {
        return this.mWifiSecurity;
    }

    public void setWifiSecurity(int wifiSecurity) {
        this.mWifiSecurity = wifiSecurity;
    }

    public String getConnectedNetwork() {
        return this.mConnectedNetwork;
    }

    public void setConnectedNetwork(String connectedNetwork) {
        this.mConnectedNetwork = connectedNetwork;
    }

    public boolean isPasswordHidden() {
        return this.mIsPasswordHidden;
    }

    public void setPasswordHidden(boolean hidden) {
        this.mIsPasswordHidden = hidden;
    }

    public void init() {
        this.mDataSummary = new HashMap<>();
        this.mWifiConfiguration = new WifiConfiguration();
        this.mWifiSecurity = 0;
        this.mChosenNetwork = null;
        this.mChosenNetwork = null;
        this.mIsPasswordHidden = false;
    }
}
