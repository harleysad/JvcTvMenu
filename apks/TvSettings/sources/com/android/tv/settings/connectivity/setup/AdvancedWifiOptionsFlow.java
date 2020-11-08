package com.android.tv.settings.connectivity.setup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AdvancedWifiOptionsFlow {
    public static final int START_DEFAULT_PAGE = 0;
    public static final int START_IP_SETTINGS_PAGE = 1;
    public static final int START_PROXY_SETTINGS_PAGE = 2;
    private static final String TAG = "AdvancedWifiOptionsFlow";

    @Retention(RetentionPolicy.SOURCE)
    public @interface START_PAGE {
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [com.android.tv.settings.connectivity.setup.ProxySettingsState, com.android.tv.settings.connectivity.util.State] */
    /* JADX WARNING: type inference failed for: r9v0, types: [com.android.tv.settings.connectivity.util.State, com.android.tv.settings.connectivity.setup.IpSettingsState] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createFlow(android.support.v4.app.FragmentActivity r23, boolean r24, boolean r25, com.android.tv.settings.connectivity.NetworkConfiguration r26, com.android.tv.settings.connectivity.util.State r27, com.android.tv.settings.connectivity.util.State r28, int r29) {
        /*
            r0 = r23
            r1 = r27
            android.arch.lifecycle.ViewModelProvider r2 = android.arch.lifecycle.ViewModelProviders.of((android.support.v4.app.FragmentActivity) r23)
            java.lang.Class<com.android.tv.settings.connectivity.util.StateMachine> r3 = com.android.tv.settings.connectivity.util.StateMachine.class
            android.arch.lifecycle.ViewModel r2 = r2.get(r3)
            com.android.tv.settings.connectivity.util.StateMachine r2 = (com.android.tv.settings.connectivity.util.StateMachine) r2
            android.arch.lifecycle.ViewModelProvider r3 = android.arch.lifecycle.ViewModelProviders.of((android.support.v4.app.FragmentActivity) r23)
            java.lang.Class<com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo> r4 = com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo.class
            android.arch.lifecycle.ViewModel r3 = r3.get(r4)
            com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo r3 = (com.android.tv.settings.connectivity.setup.AdvancedOptionsFlowInfo) r3
            r4 = r25
            r3.setSettingsFlow(r4)
            if (r26 == 0) goto L_0x0028
            android.net.IpConfiguration r6 = r26.getIpConfiguration()
            goto L_0x002d
        L_0x0028:
            android.net.IpConfiguration r6 = new android.net.IpConfiguration
            r6.<init>()
        L_0x002d:
            r3.setIpConfiguration(r6)
            com.android.tv.settings.connectivity.setup.AdvancedOptionsState r7 = new com.android.tv.settings.connectivity.setup.AdvancedOptionsState
            r7.<init>(r0)
            com.android.tv.settings.connectivity.setup.ProxySettingsState r8 = new com.android.tv.settings.connectivity.setup.ProxySettingsState
            r8.<init>(r0)
            com.android.tv.settings.connectivity.setup.IpSettingsState r9 = new com.android.tv.settings.connectivity.setup.IpSettingsState
            r9.<init>(r0)
            com.android.tv.settings.connectivity.setup.ProxyHostNameState r10 = new com.android.tv.settings.connectivity.setup.ProxyHostNameState
            r10.<init>(r0)
            com.android.tv.settings.connectivity.setup.ProxyPortState r11 = new com.android.tv.settings.connectivity.setup.ProxyPortState
            r11.<init>(r0)
            com.android.tv.settings.connectivity.setup.ProxyBypassState r12 = new com.android.tv.settings.connectivity.setup.ProxyBypassState
            r12.<init>(r0)
            com.android.tv.settings.connectivity.setup.ProxySettingsInvalidState r13 = new com.android.tv.settings.connectivity.setup.ProxySettingsInvalidState
            r13.<init>(r0)
            com.android.tv.settings.connectivity.setup.IpAddressState r14 = new com.android.tv.settings.connectivity.setup.IpAddressState
            r14.<init>(r0)
            com.android.tv.settings.connectivity.setup.GatewayState r15 = new com.android.tv.settings.connectivity.setup.GatewayState
            r15.<init>(r0)
            r16 = r3
            com.android.tv.settings.connectivity.setup.NetworkPrefixLengthState r3 = new com.android.tv.settings.connectivity.setup.NetworkPrefixLengthState
            r3.<init>(r0)
            com.android.tv.settings.connectivity.setup.Dns1State r4 = new com.android.tv.settings.connectivity.setup.Dns1State
            r4.<init>(r0)
            com.android.tv.settings.connectivity.setup.Dns2State r5 = new com.android.tv.settings.connectivity.setup.Dns2State
            r5.<init>(r0)
            r17 = r6
            com.android.tv.settings.connectivity.setup.IpSettingsInvalidState r6 = new com.android.tv.settings.connectivity.setup.IpSettingsInvalidState
            r6.<init>(r0)
            r18 = r6
            com.android.tv.settings.connectivity.setup.AdvancedFlowCompleteState r6 = new com.android.tv.settings.connectivity.setup.AdvancedFlowCompleteState
            r6.<init>(r0)
            r19 = 0
            switch(r29) {
                case 0: goto L_0x0096;
                case 1: goto L_0x0090;
                case 2: goto L_0x008d;
                default: goto L_0x0081;
            }
        L_0x0081:
            java.lang.String r0 = "AdvancedWifiOptionsFlow"
            r20 = r5
            java.lang.String r5 = "Got a wrong start state"
            android.util.Log.wtf(r0, r5)
        L_0x008a:
            r0 = r19
            goto L_0x009e
        L_0x008d:
            r19 = r8
            goto L_0x0093
        L_0x0090:
            r19 = r9
        L_0x0093:
            r20 = r5
            goto L_0x008a
        L_0x0096:
            if (r24 == 0) goto L_0x009b
            r19 = r7
            goto L_0x0093
        L_0x009b:
            r19 = r8
            goto L_0x0093
        L_0x009e:
            if (r1 == 0) goto L_0x00a6
            r5 = 24
            r2.addState(r1, r5, r0)
            goto L_0x00a9
        L_0x00a6:
            r2.setStartState(r0)
        L_0x00a9:
            r5 = 25
            r21 = r0
            r0 = r28
            r2.addState(r6, r5, r0)
            r5 = 23
            r2.addState(r7, r5, r6)
            r5 = 2
            r2.addState(r7, r5, r8)
            r5 = 19
            r2.addState(r8, r5, r9)
            r5 = 23
            r2.addState(r8, r5, r6)
            r5 = 21
            r2.addState(r8, r5, r10)
            r5 = 2
            r2.addState(r10, r5, r11)
            r2.addState(r11, r5, r12)
            r5 = 23
            r2.addState(r12, r5, r6)
            r5 = 19
            r2.addState(r12, r5, r9)
            r5 = 22
            r2.addState(r12, r5, r13)
            r5 = 2
            r2.addState(r13, r5, r8)
            r5 = 23
            r2.addState(r9, r5, r6)
            r5 = 2
            r2.addState(r9, r5, r14)
            r2.addState(r14, r5, r15)
            r2.addState(r15, r5, r3)
            r2.addState(r3, r5, r4)
            r0 = r20
            r2.addState(r4, r5, r0)
            r5 = 23
            r2.addState(r0, r5, r6)
            r5 = 20
            r1 = r18
            r2.addState(r0, r5, r1)
            r5 = 2
            r2.addState(r1, r5, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.connectivity.setup.AdvancedWifiOptionsFlow.createFlow(android.support.v4.app.FragmentActivity, boolean, boolean, com.android.tv.settings.connectivity.NetworkConfiguration, com.android.tv.settings.connectivity.util.State, com.android.tv.settings.connectivity.util.State, int):void");
    }
}
