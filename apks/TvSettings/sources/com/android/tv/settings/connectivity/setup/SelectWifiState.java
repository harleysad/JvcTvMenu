package com.android.tv.settings.connectivity.setup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedActionsStylist;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import com.android.tv.settings.R;
import com.android.tv.settings.connectivity.util.State;
import com.android.tv.settings.connectivity.util.StateMachine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelectWifiState implements State {
    private FragmentActivity mActivity;
    private Fragment mFragment;

    public SelectWifiState(FragmentActivity wifiSetupActivity) {
        this.mActivity = wifiSetupActivity;
    }

    public void processForward() {
        this.mFragment = new SelectWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, true);
        }
    }

    public void processBackward() {
        this.mFragment = new SelectWifiFragment();
        State.FragmentChangeListener listener = (State.FragmentChangeListener) this.mActivity;
        if (listener != null) {
            listener.onFragmentChange(this.mFragment, false);
        }
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public static class SelectWifiFragment extends WifiConnectivityGuidedStepFragment {
        private static final int RESULT_NETWORK_SKIPPED = 3;
        private NetworkListInfo mNetworkListInfo;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;
        private WifiGuidedActionComparator mWifiComparator = new WifiGuidedActionComparator();

        public void onCreate(Bundle savedInstanceState) {
            this.mNetworkListInfo = (NetworkListInfo) ViewModelProviders.of(getActivity()).get(NetworkListInfo.class);
            this.mUserChoiceInfo = (UserChoiceInfo) ViewModelProviders.of(getActivity()).get(UserChoiceInfo.class);
            this.mStateMachine = (StateMachine) ViewModelProviders.of(getActivity()).get(StateMachine.class);
            super.onCreate(savedInstanceState);
        }

        /* access modifiers changed from: package-private */
        public void updateNetworkList() {
            int lastSelectedActionPosition = getSelectedActionPosition();
            CharSequence lastWifiTitle = null;
            if (lastSelectedActionPosition != -1) {
                lastWifiTitle = getActions().get(lastSelectedActionPosition).getTitle();
            }
            setActions(new ArrayList<>(getNetworks()));
            moveToPosition(lastWifiTitle);
        }

        private void moveToPosition(CharSequence title) {
            if (title != null) {
                for (int i = 0; i < getActions().size(); i++) {
                    if (TextUtils.equals(getActions().get(i).getTitle(), title)) {
                        setSelectedActionPosition(i);
                        return;
                    }
                }
            }
        }

        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            return new GuidanceStylist.Guidance(getString(R.string.title_select_wifi_network), (String) null, (String) null, (Drawable) null);
        }

        public GuidedActionsStylist onCreateActionsStylist() {
            return new GuidedActionsStylist() {
                public void onBindViewHolder(GuidedActionsStylist.ViewHolder vh, GuidedAction action) {
                    super.onBindViewHolder(vh, action);
                    WifiGuidedAction wifiAction = (WifiGuidedAction) action;
                    if (wifiAction.hasIconLevel()) {
                        vh.getIconView().setImageLevel(wifiAction.getIconLevel());
                    }
                }
            };
        }

        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            actions.addAll(getNetworks());
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x003d, code lost:
            r6 = new android.util.Pair<>(r5.SSID, java.lang.Integer.valueOf(com.android.tv.settings.connectivity.util.WifiSecurityUtil.getSecurity(r5)));
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.ArrayList<com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction> getNetworks() {
            /*
                r10 = this;
                android.support.v4.app.FragmentActivity r0 = r10.getActivity()
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                com.android.tv.settings.connectivity.setup.NetworkListInfo r2 = r10.mNetworkListInfo
                com.android.settingslib.wifi.WifiTracker r2 = r2.getWifiTracker()
                android.net.wifi.WifiManager r2 = r2.getManager()
                java.util.List r2 = r2.getScanResults()
                java.util.HashMap r3 = new java.util.HashMap
                r3.<init>()
                if (r2 != 0) goto L_0x0024
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                return r4
            L_0x0024:
                java.util.Iterator r4 = r2.iterator()
            L_0x0028:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L_0x005e
                java.lang.Object r5 = r4.next()
                android.net.wifi.ScanResult r5 = (android.net.wifi.ScanResult) r5
                java.lang.String r6 = r5.SSID
                boolean r6 = android.text.TextUtils.isEmpty(r6)
                if (r6 == 0) goto L_0x003d
                goto L_0x0028
            L_0x003d:
                android.util.Pair r6 = new android.util.Pair
                java.lang.String r7 = r5.SSID
                int r8 = com.android.tv.settings.connectivity.util.WifiSecurityUtil.getSecurity((android.net.wifi.ScanResult) r5)
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                r6.<init>(r7, r8)
                java.lang.Object r7 = r3.get(r6)
                android.net.wifi.ScanResult r7 = (android.net.wifi.ScanResult) r7
                if (r7 == 0) goto L_0x005a
                int r8 = r7.level
                int r9 = r5.level
                if (r8 >= r9) goto L_0x005d
            L_0x005a:
                r3.put(r6, r5)
            L_0x005d:
                goto L_0x0028
            L_0x005e:
                java.util.Collection r4 = r3.values()
                java.util.Iterator r4 = r4.iterator()
            L_0x0066:
                boolean r5 = r4.hasNext()
                r6 = 0
                if (r5 == 0) goto L_0x00af
                java.lang.Object r5 = r4.next()
                android.net.wifi.ScanResult r5 = (android.net.wifi.ScanResult) r5
                int r7 = com.android.tv.settings.connectivity.util.WifiSecurityUtil.getSecurity((android.net.wifi.ScanResult) r5)
                if (r7 != 0) goto L_0x007d
                r7 = 2131231015(0x7f080127, float:1.80781E38)
                goto L_0x0080
            L_0x007d:
                r7 = 2131231014(0x7f080126, float:1.8078097E38)
            L_0x0080:
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r8 = new com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder
                r8.<init>(r0)
                java.lang.String r6 = r5.SSID
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r8.title((java.lang.CharSequence) r6)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r6 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r6
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r6 = r6.icon((int) r7)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r6 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r6
                r8 = 1
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r6 = r6.setHasIconLevel(r8)
                int r8 = r5.level
                r9 = 4
                int r8 = android.net.wifi.WifiManager.calculateSignalLevel(r8, r9)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r6 = r6.setIconLevel(r8)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r6 = r6.setScanResult(r5)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction r6 = r6.build()
                r1.add(r6)
                goto L_0x0066
            L_0x00af:
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedActionComparator r4 = r10.mWifiComparator
                r1.sort(r4)
                com.android.tv.settings.connectivity.setup.NetworkListInfo r4 = r10.mNetworkListInfo
                boolean r4 = r4.isShowSkipNetwork()
                r5 = 0
                if (r4 == 0) goto L_0x00e7
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = new com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder
                r4.<init>(r0)
                r7 = 2131690524(0x7f0f041c, float:1.9010094E38)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r4 = r4.title((int) r7)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r4
                r7 = -5
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r4 = r4.id(r7)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r4
                r7 = 2131230826(0x7f08006a, float:1.8077716E38)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r4 = r4.icon((int) r7)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r4
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = r4.setHasIconLevel(r5)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction r4 = r4.build()
                r1.add(r4)
            L_0x00e7:
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = new com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder
                r4.<init>(r0)
                r6 = 2131690333(0x7f0f035d, float:1.9009707E38)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r4 = r4.title((int) r6)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r4
                r6 = 2131230909(0x7f0800bd, float:1.8077884E38)
                android.support.v17.leanback.widget.GuidedAction$BuilderBase r4 = r4.icon((int) r6)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = (com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.WifiGuidedAction.Builder) r4
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction$Builder r4 = r4.setHasIconLevel(r5)
                com.android.tv.settings.connectivity.setup.SelectWifiState$SelectWifiFragment$WifiGuidedAction r4 = r4.build()
                r1.add(r4)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.connectivity.setup.SelectWifiState.SelectWifiFragment.getNetworks():java.util.ArrayList");
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            CharSequence title = this.mUserChoiceInfo.getPageSummary(1);
            if (title != null) {
                moveToPosition(title);
            }
        }

        public void onGuidedActionFocused(GuidedAction action) {
            this.mNetworkListInfo.updateNextNetworkRefreshTime();
        }

        public void onGuidedActionClicked(GuidedAction action) {
            if (action.getId() == -5) {
                this.mStateMachine.finish(3);
                return;
            }
            this.mUserChoiceInfo.put(1, action.getTitle().toString());
            this.mUserChoiceInfo.setChosenNetwork(((WifiGuidedAction) action).getScanResult());
            this.mStateMachine.getListener().onComplete(17);
        }

        private static class WifiGuidedActionComparator implements Comparator<WifiGuidedAction> {
            private WifiGuidedActionComparator() {
            }

            public int compare(WifiGuidedAction o1, WifiGuidedAction o2) {
                int levelDiff = o2.getIconLevel() - o1.getIconLevel();
                if (levelDiff != 0) {
                    return levelDiff;
                }
                return o1.getTitle().toString().compareTo(o2.getTitle().toString());
            }
        }

        private static class WifiGuidedAction extends GuidedAction {
            boolean mHasIconLevel;
            int mIconLevel;
            ScanResult mScanResult;

            private WifiGuidedAction() {
            }

            /* access modifiers changed from: package-private */
            public ScanResult getScanResult() {
                return this.mScanResult;
            }

            /* access modifiers changed from: package-private */
            public void setScanResult(ScanResult scanResult) {
                this.mScanResult = scanResult;
            }

            /* access modifiers changed from: package-private */
            public int getIconLevel() {
                return this.mIconLevel;
            }

            /* access modifiers changed from: package-private */
            public void setIconLevel(int iconLevel) {
                this.mIconLevel = iconLevel;
            }

            /* access modifiers changed from: package-private */
            public void setHasIconLevel(boolean hasIconLevel) {
                this.mHasIconLevel = hasIconLevel;
            }

            /* access modifiers changed from: package-private */
            public boolean hasIconLevel() {
                return this.mHasIconLevel;
            }

            static class Builder extends GuidedAction.BuilderBase<Builder> {
                boolean mHasIconLevel;
                int mIconLevel;
                ScanResult mScanResult;

                private Builder(Context context) {
                    super(context);
                }

                /* access modifiers changed from: package-private */
                public Builder setScanResult(ScanResult scanResult) {
                    this.mScanResult = scanResult;
                    return this;
                }

                /* access modifiers changed from: package-private */
                public Builder setIconLevel(int iconLevel) {
                    this.mIconLevel = iconLevel;
                    return this;
                }

                /* access modifiers changed from: package-private */
                public Builder setHasIconLevel(boolean hasIconLevel) {
                    this.mHasIconLevel = hasIconLevel;
                    return this;
                }

                /* access modifiers changed from: package-private */
                public WifiGuidedAction build() {
                    WifiGuidedAction action = new WifiGuidedAction();
                    action.setScanResult(this.mScanResult);
                    action.setHasIconLevel(this.mHasIconLevel);
                    action.setIconLevel(this.mIconLevel);
                    applyValues(action);
                    return action;
                }
            }
        }
    }
}
