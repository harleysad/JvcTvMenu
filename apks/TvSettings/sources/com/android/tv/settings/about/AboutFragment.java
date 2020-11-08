package com.android.tv.settings.about;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SELinux;
import android.os.SystemProperties;
import android.os.UserManager;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.tv.settings.LongClickPreference;
import com.android.tv.settings.MainFragment;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.name.DeviceManager;
import com.android.tv.twopanelsettings.TwoPanelSettingsFragment;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;

@Keep
public class AboutFragment extends SettingsPreferenceFragment implements LongClickPreference.OnLongClickListener {
    private static final String KEY_BASEBAND_VERSION = "baseband_version";
    private static final String KEY_BUILD_NUMBER = "build_number";
    private static final String KEY_DEVICE_FEEDBACK = "device_feedback";
    private static final String KEY_DEVICE_MODEL = "device_model";
    private static final String KEY_DEVICE_NAME = "device_name";
    private static final String KEY_EQUIPMENT_ID = "fcc_equipment_id";
    private static final String KEY_ESN_KEY = "netflix_esn_key";
    private static final String KEY_FIRMWARE_VERSION = "firmware_version";
    private static final String KEY_KERNEL_VERSION = "kernel_version";
    private static final String KEY_MANUAL = "manual";
    private static final String KEY_MODEL = "ro.vendor.modelname";
    private static final String KEY_OEM = "ro.vendor.oemname";
    private static final String KEY_PRODUCTION_NAME = "production_name";
    private static final String KEY_REGULATORY_INFO = "regulatory_info";
    private static final String KEY_RESTART = "restart";
    private static final String KEY_SAFETY_LEGAL = "safetylegal";
    private static final String KEY_SECURITY_PATCH = "security_patch";
    private static final String KEY_SELINUX_STATUS = "selinux_status";
    private static final String KEY_SOFTWARE_VERSION = "software_version";
    private static final String KEY_SYSTEM_UPDATE_SETTINGS = "system_update_settings";
    private static final String KEY_TUTORIALS = "tutorials";
    private static final String KEY_UPDATE_SETTING = "additional_system_update_settings";
    private static final String PROPERTY_EQUIPMENT_ID = "ro.ril.fccid";
    private static final String PROPERTY_SELINUX_STATUS = "ro.build.selinux";
    private static final String PROPERTY_URL_SAFETYLEGAL = "ro.url.safetylegal";
    private static final String TAG = "AboutFragment";
    static final int TAPS_TO_BE_A_DEVELOPER = 7;
    IntentFilter esnIntentFilter = new IntentFilter("com.netflix.ninja.intent.action.ESN_RESPONSE");
    private BroadcastReceiver esnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AboutFragment.this.mESNKey.setSummary((CharSequence) intent.getStringExtra("ESNValue"));
        }
    };
    int mDevHitCountdown;
    Toast mDevHitToast;
    private final BroadcastReceiver mDeviceNameReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AboutFragment.this.refreshDeviceName();
        }
    };
    /* access modifiers changed from: private */
    public Preference mESNKey;
    long[] mHits = new long[3];
    private Preference mProductionName;
    private UserManager mUm;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mUm = UserManager.get(getActivity());
        getContext().registerReceiver(this.esnReceiver, this.esnIntentFilter);
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.device_info_settings, (String) null);
        PreferenceScreen screen = getPreferenceScreen();
        refreshDeviceName();
        PreferenceUtils.resolveSystemActivityOrRemove(getActivity(), screen, findPreference(KEY_DEVICE_NAME), 0);
        Preference firmwareVersionPref = findPreference(KEY_FIRMWARE_VERSION);
        firmwareVersionPref.setSummary((CharSequence) Build.VERSION.RELEASE);
        firmwareVersionPref.setEnabled(true);
        Preference securityPatchPref = findPreference(KEY_SECURITY_PATCH);
        String patch = DeviceInfoUtils.getSecurityPatch();
        if (!TextUtils.isEmpty(patch)) {
            securityPatchPref.setSummary((CharSequence) patch);
        } else {
            removePreference(securityPatchPref);
        }
        ((LongClickPreference) findPreference(KEY_RESTART)).setLongClickListener(this);
        findPreference(KEY_BASEBAND_VERSION).setSummary((CharSequence) getSystemPropertySummary("gsm.version.baseband"));
        Preference findPreference = findPreference(KEY_DEVICE_MODEL);
        findPreference.setSummary((CharSequence) Build.MODEL + DeviceInfoUtils.getMsvSuffix());
        findPreference(KEY_EQUIPMENT_ID).setSummary((CharSequence) getSystemPropertySummary(PROPERTY_EQUIPMENT_ID));
        Preference buildNumberPref = findPreference(KEY_BUILD_NUMBER);
        buildNumberPref.setSummary((CharSequence) Build.DISPLAY);
        buildNumberPref.setEnabled(true);
        findPreference(KEY_KERNEL_VERSION).setSummary((CharSequence) DeviceInfoUtils.getFormattedKernelVersion(getContext()));
        Preference selinuxPref = findPreference(KEY_SELINUX_STATUS);
        if (!SELinux.isSELinuxEnabled()) {
            selinuxPref.setSummary((int) R.string.selinux_status_disabled);
        } else if (!SELinux.isSELinuxEnforced()) {
            selinuxPref.setSummary((int) R.string.selinux_status_permissive);
        }
        if (TextUtils.isEmpty(SystemProperties.get(PROPERTY_SELINUX_STATUS))) {
            removePreference(selinuxPref);
        }
        if (TextUtils.isEmpty(SystemProperties.get(PROPERTY_URL_SAFETYLEGAL))) {
            removePreference(findPreference(KEY_SAFETY_LEGAL));
        }
        if (TextUtils.isEmpty(SystemProperties.get(PROPERTY_EQUIPMENT_ID))) {
            removePreference(findPreference(KEY_EQUIPMENT_ID));
        }
        if (Utils.isWifiOnly(getActivity())) {
            removePreference(findPreference(KEY_BASEBAND_VERSION));
        }
        if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(getActivity()))) {
            removePreference(findPreference(KEY_DEVICE_FEEDBACK));
        }
        Preference updateSettingsPref = findPreference(KEY_SYSTEM_UPDATE_SETTINGS);
        if (this.mUm.isAdminUser()) {
            PreferenceUtils.resolveSystemActivityOrRemove(getActivity(), screen, updateSettingsPref, 1);
        } else if (updateSettingsPref != null) {
            removePreference(updateSettingsPref);
        }
        if (!getResources().getBoolean(R.bool.config_additional_system_update_setting_enable)) {
            removePreference(findPreference(KEY_UPDATE_SETTING));
        }
        if (!getResources().getBoolean(R.bool.config_show_manual)) {
            removePreference(findPreference(KEY_MANUAL));
        }
        PreferenceUtils.resolveSystemActivityOrRemove(getActivity(), screen, findPreference(KEY_REGULATORY_INFO), 0);
        this.mProductionName = findPreference(KEY_PRODUCTION_NAME);
        String oemString = SystemProperties.get(KEY_OEM);
        String modelString = SystemProperties.get(KEY_MODEL);
        if (!TextUtils.isEmpty(oemString) && !TextUtils.isEmpty(modelString)) {
            Preference preference = this.mProductionName;
            preference.setSummary((CharSequence) oemString + " " + modelString);
        } else if (!TextUtils.isEmpty(oemString)) {
            this.mProductionName.setSummary((CharSequence) oemString);
        } else if (!TextUtils.isEmpty(modelString)) {
            this.mProductionName.setSummary((CharSequence) modelString);
        } else {
            this.mProductionName.setVisible(false);
        }
        Log.d(TAG, "oemString:" + oemString + ",modelString:" + modelString);
        this.mESNKey = findPreference(KEY_ESN_KEY);
        if (!PartnerSettingsConfig.isMiscItemDisplay(KEY_ESN_KEY)) {
            this.mESNKey.setVisible(false);
        } else {
            Intent esnQueryIntent = new Intent("com.netflix.ninja.intent.action.ESN");
            esnQueryIntent.setPackage("com.netflix.ninja");
            esnQueryIntent.addFlags(32);
            getContext().sendBroadcast(esnQueryIntent);
            Log.d(TAG, "sendBroadcast esnQueryIntent");
        }
        Preference ref = findPreference(KEY_SOFTWARE_VERSION);
        if (!PartnerSettingsConfig.isMiscItemDisplay(KEY_SOFTWARE_VERSION)) {
            ref.setVisible(false);
            PreferenceScreen preferenceScreen = screen;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        PreferenceScreen preferenceScreen2 = screen;
        sb.append(SystemProperties.get(getResources().getString(R.string.s_cus_product_version)));
        sb.append("\r\n");
        sb.append(SystemProperties.get(getResources().getString(R.string.s_cus_product_revision)));
        sb.append("\r\n");
        sb.append(SystemProperties.get("ro.vendor.build.date"));
        sb.append(" ");
        ref.setSummary((CharSequence) sb.toString());
    }

    private void removePreference(@Nullable Preference preference) {
        if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    public void onStart() {
        super.onStart();
        refreshDeviceName();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mDeviceNameReceiver, new IntentFilter(DeviceManager.ACTION_DEVICE_NAME_UPDATE));
    }

    public void onResume() {
        super.onResume();
        this.mDevHitCountdown = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext()) ? -1 : 7;
        this.mDevHitToast = null;
        updateTutorials();
    }

    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mDeviceNameReceiver);
    }

    /* access modifiers changed from: private */
    public void refreshDeviceName() {
        Preference deviceNamePref = findPreference(KEY_DEVICE_NAME);
        if (deviceNamePref != null) {
            deviceNamePref.setSummary((CharSequence) DeviceManager.getDeviceName(getActivity()));
        }
    }

    public boolean onPreferenceLongClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), KEY_RESTART)) {
            return false;
        }
        Fragment fragment = getCallbackFragment();
        if (fragment instanceof LeanbackSettingsFragment) {
            ((LeanbackSettingsFragment) fragment).startImmersiveFragment(RebootConfirmFragment.newInstance(true));
            return true;
        } else if (!(fragment instanceof TwoPanelSettingsFragment)) {
            return false;
        } else {
            ((TwoPanelSettingsFragment) fragment).startImmersiveFragment(RebootConfirmFragment.newInstance(true));
            return true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0177  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceTreeClick(android.support.v7.preference.Preference r11) {
        /*
            r10 = this;
            java.lang.String r0 = r11.getKey()
            int r1 = r0.hashCode()
            r2 = -1253860806(0xffffffffb5439a3a, float:-7.286759E-7)
            r3 = 0
            r4 = 1
            if (r1 == r2) goto L_0x003d
            r2 = -987819436(0xffffffffc51f1254, float:-2545.1455)
            if (r1 == r2) goto L_0x0033
            r2 = 1037942606(0x3dddbf4e, float:0.10827504)
            if (r1 == r2) goto L_0x0029
            r2 = 1738150089(0x679a10c9, float:1.4551081E24)
            if (r1 == r2) goto L_0x001f
            goto L_0x0047
        L_0x001f:
            java.lang.String r1 = "system_update_settings"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0047
            r0 = 3
            goto L_0x0048
        L_0x0029:
            java.lang.String r1 = "device_feedback"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0047
            r0 = 2
            goto L_0x0048
        L_0x0033:
            java.lang.String r1 = "firmware_version"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0047
            r0 = r3
            goto L_0x0048
        L_0x003d:
            java.lang.String r1 = "build_number"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0047
            r0 = r4
            goto L_0x0048
        L_0x0047:
            r0 = -1
        L_0x0048:
            switch(r0) {
                case 0: goto L_0x0177;
                case 1: goto L_0x0072;
                case 2: goto L_0x006d;
                case 3: goto L_0x004d;
                default: goto L_0x004b;
            }
        L_0x004b:
            goto L_0x01e1
        L_0x004d:
            android.app.Activity r0 = r10.getActivity()
            java.lang.String r1 = "carrier_config"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.telephony.CarrierConfigManager r0 = (android.telephony.CarrierConfigManager) r0
            android.os.PersistableBundle r1 = r0.getConfig()
            if (r1 == 0) goto L_0x01e1
            java.lang.String r2 = "ci_action_on_sys_update_bool"
            boolean r2 = r1.getBoolean(r2)
            if (r2 == 0) goto L_0x01e1
            r10.ciActionOnSysUpdate(r1)
            goto L_0x01e1
        L_0x006d:
            r10.sendFeedback()
            goto L_0x01e1
        L_0x0072:
            android.os.UserManager r0 = r10.mUm
            boolean r0 = r0.isAdminUser()
            r1 = 847(0x34f, float:1.187E-42)
            if (r0 != 0) goto L_0x0088
            com.android.settingslib.core.instrumentation.MetricsFeatureProvider r0 = r10.mMetricsFeatureProvider
            android.content.Context r2 = r10.getContext()
            android.util.Pair[] r3 = new android.util.Pair[r3]
            r0.action((android.content.Context) r2, (int) r1, (android.util.Pair<java.lang.Integer, java.lang.Object>[]) r3)
            return r4
        L_0x0088:
            android.os.UserManager r0 = r10.mUm
            java.lang.String r2 = "no_debugging_features"
            boolean r0 = r0.hasUserRestriction(r2)
            if (r0 == 0) goto L_0x009e
            com.android.settingslib.core.instrumentation.MetricsFeatureProvider r0 = r10.mMetricsFeatureProvider
            android.content.Context r2 = r10.getContext()
            android.util.Pair[] r3 = new android.util.Pair[r3]
            r0.action((android.content.Context) r2, (int) r1, (android.util.Pair<java.lang.Integer, java.lang.Object>[]) r3)
            return r4
        L_0x009e:
            int r0 = r10.mDevHitCountdown
            r2 = 848(0x350, float:1.188E-42)
            if (r0 <= 0) goto L_0x013e
            int r0 = r10.mDevHitCountdown
            int r0 = r0 - r4
            r10.mDevHitCountdown = r0
            int r0 = r10.mDevHitCountdown
            if (r0 != 0) goto L_0x00ea
            android.content.Context r0 = r10.getContext()
            com.android.settingslib.development.DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(r0, r4)
            android.widget.Toast r0 = r10.mDevHitToast
            if (r0 == 0) goto L_0x00be
            android.widget.Toast r0 = r10.mDevHitToast
            r0.cancel()
        L_0x00be:
            android.app.Activity r0 = r10.getActivity()
            r5 = 2131690510(0x7f0f040e, float:1.9010066E38)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r5, r4)
            r10.mDevHitToast = r0
            android.widget.Toast r0 = r10.mDevHitToast
            r0.show()
            com.android.settingslib.core.instrumentation.MetricsFeatureProvider r0 = r10.mMetricsFeatureProvider
            android.content.Context r5 = r10.getContext()
            android.util.Pair[] r6 = new android.util.Pair[r4]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            android.util.Pair r7 = android.util.Pair.create(r7, r8)
            r6[r3] = r7
            r0.action((android.content.Context) r5, (int) r1, (android.util.Pair<java.lang.Integer, java.lang.Object>[]) r6)
            goto L_0x0123
        L_0x00ea:
            int r0 = r10.mDevHitCountdown
            if (r0 <= 0) goto L_0x0123
            int r0 = r10.mDevHitCountdown
            r5 = 5
            if (r0 >= r5) goto L_0x0123
            android.widget.Toast r0 = r10.mDevHitToast
            if (r0 == 0) goto L_0x00fc
            android.widget.Toast r0 = r10.mDevHitToast
            r0.cancel()
        L_0x00fc:
            android.app.Activity r0 = r10.getActivity()
            android.content.res.Resources r5 = r10.getResources()
            r6 = 2131558409(0x7f0d0009, float:1.8742133E38)
            int r7 = r10.mDevHitCountdown
            java.lang.Object[] r8 = new java.lang.Object[r4]
            int r9 = r10.mDevHitCountdown
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r3] = r9
            java.lang.String r5 = r5.getQuantityString(r6, r7, r8)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r5, r3)
            r10.mDevHitToast = r0
            android.widget.Toast r0 = r10.mDevHitToast
            r0.show()
        L_0x0123:
            com.android.settingslib.core.instrumentation.MetricsFeatureProvider r0 = r10.mMetricsFeatureProvider
            android.content.Context r5 = r10.getContext()
            android.util.Pair[] r4 = new android.util.Pair[r4]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            android.util.Pair r2 = android.util.Pair.create(r2, r6)
            r4[r3] = r2
            r0.action((android.content.Context) r5, (int) r1, (android.util.Pair<java.lang.Integer, java.lang.Object>[]) r4)
            goto L_0x01e1
        L_0x013e:
            int r0 = r10.mDevHitCountdown
            if (r0 >= 0) goto L_0x01e1
            android.widget.Toast r0 = r10.mDevHitToast
            if (r0 == 0) goto L_0x014b
            android.widget.Toast r0 = r10.mDevHitToast
            r0.cancel()
        L_0x014b:
            android.app.Activity r0 = r10.getActivity()
            r5 = 2131690509(0x7f0f040d, float:1.9010064E38)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r5, r4)
            r10.mDevHitToast = r0
            android.widget.Toast r0 = r10.mDevHitToast
            r0.show()
            com.android.settingslib.core.instrumentation.MetricsFeatureProvider r0 = r10.mMetricsFeatureProvider
            android.content.Context r5 = r10.getContext()
            android.util.Pair[] r6 = new android.util.Pair[r4]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            android.util.Pair r2 = android.util.Pair.create(r2, r4)
            r6[r3] = r2
            r0.action((android.content.Context) r5, (int) r1, (android.util.Pair<java.lang.Integer, java.lang.Object>[]) r6)
            goto L_0x01e1
        L_0x0177:
            long[] r0 = r10.mHits
            long[] r1 = r10.mHits
            long[] r2 = r10.mHits
            int r2 = r2.length
            int r2 = r2 - r4
            java.lang.System.arraycopy(r0, r4, r1, r3, r2)
            long[] r0 = r10.mHits
            long[] r1 = r10.mHits
            int r1 = r1.length
            int r1 = r1 - r4
            long r4 = android.os.SystemClock.uptimeMillis()
            r0[r1] = r4
            long[] r0 = r10.mHits
            r0 = r0[r3]
            long r4 = android.os.SystemClock.uptimeMillis()
            r6 = 500(0x1f4, double:2.47E-321)
            long r4 = r4 - r6
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 < 0) goto L_0x01e1
            android.os.UserManager r0 = r10.mUm
            java.lang.String r1 = "no_fun"
            boolean r0 = r0.hasUserRestriction(r1)
            if (r0 == 0) goto L_0x01af
            java.lang.String r0 = "AboutFragment"
            java.lang.String r1 = "Sorry, no fun for you!"
            android.util.Log.d(r0, r1)
            return r3
        L_0x01af:
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.MAIN"
            r0.<init>(r1)
            java.lang.String r1 = "android"
            java.lang.Class<com.android.internal.app.PlatLogoActivity> r2 = com.android.internal.app.PlatLogoActivity.class
            java.lang.String r2 = r2.getName()
            r0.setClassName(r1, r2)
            r10.startActivity(r0)     // Catch:{ Exception -> 0x01c5 }
            goto L_0x01e0
        L_0x01c5:
            r1 = move-exception
            java.lang.String r2 = "AboutFragment"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unable to start activity "
            r3.append(r4)
            java.lang.String r4 = r0.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.e(r2, r3)
        L_0x01e0:
        L_0x01e1:
            boolean r0 = super.onPreferenceTreeClick(r11)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.about.AboutFragment.onPreferenceTreeClick(android.support.v7.preference.Preference):boolean");
    }

    private void ciActionOnSysUpdate(PersistableBundle b) {
        String intentStr = b.getString("ci_action_on_sys_update_intent_string");
        if (!TextUtils.isEmpty(intentStr)) {
            String extra = b.getString("ci_action_on_sys_update_extra_string");
            String extraVal = b.getString("ci_action_on_sys_update_extra_val_string");
            Intent intent = new Intent(intentStr);
            if (!TextUtils.isEmpty(extra)) {
                intent.putExtra(extra, extraVal);
            }
            Log.d(TAG, "ciActionOnSysUpdate: broadcasting intent " + intentStr + " with extra " + extra + ", " + extraVal);
            getActivity().getApplicationContext().sendBroadcast(intent);
        }
    }

    private String getSystemPropertySummary(String property) {
        return SystemProperties.get(property, getResources().getString(R.string.device_info_default));
    }

    private void sendFeedback() {
        String reporterPackage = DeviceInfoUtils.getFeedbackReporterPackage(getActivity());
        if (!TextUtils.isEmpty(reporterPackage)) {
            Intent intent = new Intent("android.intent.action.BUG_REPORT");
            intent.setPackage(reporterPackage);
            startActivityForResult(intent, 0);
        }
    }

    private void updateTutorials() {
        Preference deviceTutorialsPref = findPreference(KEY_TUTORIALS);
        if (deviceTutorialsPref != null) {
            ResolveInfo info = MainFragment.systemIntentIsHandled(getContext(), deviceTutorialsPref.getIntent());
            deviceTutorialsPref.setVisible(info != null);
            if (info != null) {
                deviceTutorialsPref.setTitle(info.loadLabel(getContext().getPackageManager()));
            }
        }
    }

    public int getMetricsCategory() {
        return 40;
    }
}
