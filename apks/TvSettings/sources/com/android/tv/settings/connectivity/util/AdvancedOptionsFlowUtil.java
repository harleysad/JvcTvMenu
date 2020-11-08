package com.android.tv.settings.connectivity.util;

import android.arch.lifecycle.ViewModelProviders;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.WifiConfigHelper;
import com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo;
import java.net.Inet4Address;

public class AdvancedOptionsFlowUtil {
    public static int processProxySettings(FragmentActivity activity) {
        int result;
        AdvancedOptionsFlowInfo flowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(activity).get(AdvancedOptionsFlowInfo.class);
        IpConfiguration mIpConfiguration = flowInfo.getIpConfiguration();
        boolean hasProxySettings = true;
        if ((flowInfo.containsPage(1) && flowInfo.choiceChosen(activity.getString(R.string.wifi_action_advanced_no), 1)) || flowInfo.choiceChosen(activity.getString(R.string.wifi_action_proxy_none), 2)) {
            hasProxySettings = false;
        }
        mIpConfiguration.setProxySettings(hasProxySettings ? IpConfiguration.ProxySettings.STATIC : IpConfiguration.ProxySettings.NONE);
        if (hasProxySettings) {
            String host = flowInfo.get(3);
            String portStr = flowInfo.get(4);
            String exclusionList = flowInfo.get(5);
            int port = 0;
            try {
                port = Integer.parseInt(portStr);
                result = WifiConfigHelper.validate(host, portStr, exclusionList);
            } catch (NumberFormatException e) {
                result = R.string.proxy_error_invalid_port;
            }
            if (result != 0) {
                return result;
            }
            mIpConfiguration.setHttpProxy(new ProxyInfo(host, port, exclusionList));
        } else {
            mIpConfiguration.setHttpProxy((ProxyInfo) null);
        }
        return 0;
    }

    public static int processIpSettings(FragmentActivity activity) {
        IpConfiguration.IpAssignment ipAssignment;
        AdvancedOptionsFlowInfo flowInfo = (AdvancedOptionsFlowInfo) ViewModelProviders.of(activity).get(AdvancedOptionsFlowInfo.class);
        IpConfiguration mIpConfiguration = flowInfo.getIpConfiguration();
        boolean hasIpSettings = true;
        if ((flowInfo.containsPage(1) && flowInfo.choiceChosen(activity.getString(R.string.wifi_action_advanced_no), 1)) || flowInfo.choiceChosen(activity.getString(R.string.wifi_action_dhcp), 6)) {
            hasIpSettings = false;
        }
        if (hasIpSettings) {
            ipAssignment = IpConfiguration.IpAssignment.STATIC;
        } else {
            ipAssignment = IpConfiguration.IpAssignment.DHCP;
        }
        mIpConfiguration.setIpAssignment(ipAssignment);
        if (hasIpSettings) {
            StaticIpConfiguration staticConfig = new StaticIpConfiguration();
            mIpConfiguration.setStaticIpConfiguration(staticConfig);
            String ipAddr = flowInfo.get(7);
            if (TextUtils.isEmpty(ipAddr)) {
                return R.string.wifi_ip_settings_invalid_ip_address;
            }
            try {
                Inet4Address inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ipAddr);
                try {
                    int networkPrefixLength = Integer.parseInt(flowInfo.get(8));
                    if (networkPrefixLength < 0) {
                        return R.string.wifi_ip_settings_invalid_network_prefix_length;
                    }
                    if (networkPrefixLength > 32) {
                        return R.string.wifi_ip_settings_invalid_network_prefix_length;
                    }
                    staticConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
                    String gateway = flowInfo.get(9);
                    if (!TextUtils.isEmpty(gateway)) {
                        try {
                            staticConfig.gateway = NetworkUtils.numericToInetAddress(gateway);
                        } catch (ClassCastException | IllegalArgumentException e) {
                            return R.string.wifi_ip_settings_invalid_gateway;
                        }
                    }
                    String dns1 = flowInfo.get(10);
                    if (!TextUtils.isEmpty(dns1)) {
                        try {
                            staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(dns1));
                        } catch (ClassCastException | IllegalArgumentException e2) {
                            return R.string.wifi_ip_settings_invalid_dns;
                        }
                    }
                    String dns2 = flowInfo.get(11);
                    if (!TextUtils.isEmpty(dns2)) {
                        try {
                            staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(dns2));
                        } catch (ClassCastException | IllegalArgumentException e3) {
                            return R.string.wifi_ip_settings_invalid_dns;
                        }
                    }
                } catch (NumberFormatException e4) {
                    return R.string.wifi_ip_settings_invalid_ip_address;
                }
            } catch (ClassCastException | IllegalArgumentException e5) {
                return R.string.wifi_ip_settings_invalid_ip_address;
            }
        } else {
            mIpConfiguration.setStaticIpConfiguration((StaticIpConfiguration) null);
        }
        return 0;
    }
}
