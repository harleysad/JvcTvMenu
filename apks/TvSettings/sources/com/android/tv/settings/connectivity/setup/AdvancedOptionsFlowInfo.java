package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModel;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.util.HashMap;

public class AdvancedOptionsFlowInfo extends ViewModel {
    public static final int ADVANCED_OPTIONS = 1;
    public static final int DNS1 = 10;
    public static final int DNS2 = 11;
    public static final int GATEWAY = 9;
    public static final int IP_ADDRESS = 7;
    public static final int IP_SETTINGS = 6;
    public static final int NETWORK_PREFIX_LENGTH = 8;
    public static final int PROXY_BYPASS = 5;
    public static final int PROXY_HOSTNAME = 3;
    public static final int PROXY_PORT = 4;
    public static final int PROXY_SETTINGS = 2;
    private boolean mCanStart = false;
    private IpConfiguration mIpConfiguration;
    private HashMap<Integer, CharSequence> mPageSummary = new HashMap<>();
    private String mPrintableSsid;
    private boolean mSettingsFlow;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PAGE {
    }

    public void put(int page, CharSequence info) {
        this.mPageSummary.put(Integer.valueOf(page), info);
    }

    public boolean choiceChosen(CharSequence choice, int page) {
        if (!this.mPageSummary.containsKey(Integer.valueOf(page))) {
            return false;
        }
        return TextUtils.equals(choice, this.mPageSummary.get(Integer.valueOf(page)));
    }

    public boolean containsPage(int page) {
        return this.mPageSummary.containsKey(Integer.valueOf(page));
    }

    public String get(int page) {
        if (!this.mPageSummary.containsKey(Integer.valueOf(page))) {
            return "";
        }
        return this.mPageSummary.get(Integer.valueOf(page)).toString();
    }

    public void remove(int page) {
        this.mPageSummary.remove(Integer.valueOf(page));
    }

    public IpConfiguration getIpConfiguration() {
        return this.mIpConfiguration;
    }

    public void setIpConfiguration(IpConfiguration ipConfiguration) {
        this.mIpConfiguration = ipConfiguration;
    }

    public boolean isSettingsFlow() {
        return this.mSettingsFlow;
    }

    public void setSettingsFlow(boolean settingsFlow) {
        this.mSettingsFlow = settingsFlow;
    }

    public void setCanStart(boolean canStart) {
        this.mCanStart = canStart;
    }

    public boolean canStart() {
        return this.mCanStart;
    }

    public String getPrintableSsid() {
        return this.mPrintableSsid;
    }

    public void setPrintableSsid(String ssid) {
        this.mPrintableSsid = ssid;
    }

    public InetAddress getInitialDns(int index) {
        if (this.mIpConfiguration == null || this.mIpConfiguration.getStaticIpConfiguration() == null || this.mIpConfiguration.getStaticIpConfiguration().dnsServers != null) {
            return null;
        }
        try {
            return (InetAddress) this.mIpConfiguration.getStaticIpConfiguration().dnsServers.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public InetAddress getInitialGateway() {
        if (this.mIpConfiguration == null || this.mIpConfiguration.getStaticIpConfiguration() == null) {
            return null;
        }
        return this.mIpConfiguration.getStaticIpConfiguration().gateway;
    }

    public LinkAddress getInitialLinkAddress() {
        if (this.mIpConfiguration == null || this.mIpConfiguration.getStaticIpConfiguration() == null) {
            return null;
        }
        return this.mIpConfiguration.getStaticIpConfiguration().ipAddress;
    }

    public ProxyInfo getInitialProxyInfo() {
        if (this.mIpConfiguration != null) {
            return this.mIpConfiguration.getHttpProxy();
        }
        return null;
    }
}
