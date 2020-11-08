package com.android.tv.settings.system.development;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.app.backup.IBackupManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.service.persistentdata.PersistentDataBlockManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.internal.app.LocalePicker;
import com.android.settingslib.core.ConfirmationDialogController;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.system.development.AdbDialog;
import com.android.tv.settings.system.development.EnableDevelopmentDialog;
import com.android.tv.settings.system.development.OemUnlockDialog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DevelopmentFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, EnableDevelopmentDialog.Callback, OemUnlockDialog.Callback, AdbDialog.Callback {
    private static final String ANIMATOR_DURATION_SCALE_KEY = "animator_duration_scale";
    private static final String APP_PROCESS_LIMIT_KEY = "app_process_limit";
    private static final String BTSNOOP_ENABLE_PROPERTY = "persist.bluetooth.btsnoopenable";
    private static final String BT_HCI_SNOOP_LOG = "bt_hci_snoop_log";
    private static final String BUGREPORT = "bugreport";
    private static final String BUGREPORT_IN_POWER_KEY = "bugreport_in_power";
    private static final String CLEAR_ADB_KEYS = "clear_adb_keys";
    private static final String DEBUG_APP_KEY = "debug_app";
    private static final String DEBUG_DEBUGGING_CATEGORY_KEY = "debug_debugging_category";
    private static final String DEBUG_HW_OVERDRAW_KEY = "debug_hw_overdraw";
    private static final String DEBUG_LAYOUT_KEY = "debug_layout";
    private static final String DEBUG_VIEW_ATTRIBUTES = "debug_view_attributes";
    private static String DEFAULT_LOG_RING_BUFFER_SIZE_IN_BYTES = "262144";
    private static final String DISABLE_OVERLAYS_KEY = "disable_overlays";
    private static final String ENABLE_ADB = "enable_adb";
    private static final String ENABLE_DEVELOPER = "development_settings_enable";
    private static final String ENABLE_OEM_UNLOCK = "oem_unlock_enable";
    private static final String ENABLE_TERMINAL = "enable_terminal";
    private static final String FORCE_ALLOW_ON_EXTERNAL_KEY = "force_allow_on_external";
    private static final String FORCE_HARDWARE_UI_KEY = "force_hw_ui";
    private static final String FORCE_MSAA_KEY = "force_msaa";
    private static final String FORCE_RESIZABLE_KEY = "force_resizable_activities";
    private static final String FORCE_RTL_LAYOUT_KEY = "force_rtl_layout_all_locales";
    private static final String HARDWARE_UI_PROPERTY = "persist.sys.ui.hw";
    private static final String HDCP_CHECKING_KEY = "hdcp_checking";
    private static final String HDCP_CHECKING_PROPERTY = "persist.sys.hdcp_checking";
    private static final String IMMEDIATELY_DESTROY_ACTIVITIES_KEY = "immediately_destroy_activities";
    private static final String INACTIVE_APPS_KEY = "inactive_apps";
    private static final String KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_COLOR_MODE = "color_mode";
    private static final String KEY_CONVERT_FBE = "convert_to_file_encryption";
    private static final String LOCAL_BACKUP_PASSWORD = "local_backup_password";
    private static final String MOBILE_DATA_ALWAYS_ON = "mobile_data_always_on";
    private static final String MOCK_LOCATION_APP_KEY = "mock_location_app";
    private static final int[] MOCK_LOCATION_APP_OPS = {58};
    private static final String MSAA_PROPERTY = "debug.egl.force_msaa";
    private static final String OPENGL_TRACES_KEY = "enable_opengl_traces";
    private static final String OPENGL_TRACES_PROPERTY = "debug.egl.trace";
    private static final String OVERLAY_DISPLAY_DEVICES_KEY = "overlay_display_devices";
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
    private static final String POINTER_LOCATION_KEY = "pointer_location";
    private static final int RESULT_DEBUG_APP = 1000;
    private static final int RESULT_MOCK_LOCATION_APP = 1001;
    private static final String RUNNING_APPS = "running_apps";
    private static final String SHOW_ALL_ANRS_KEY = "show_all_anrs";
    private static final String SHOW_HW_LAYERS_UPDATES_KEY = "show_hw_layers_udpates";
    private static final String SHOW_HW_SCREEN_UPDATES_KEY = "show_hw_screen_udpates";
    private static final String SHOW_NON_RECTANGULAR_CLIP_KEY = "show_non_rect_clip";
    private static final String SHOW_SCREEN_UPDATES_KEY = "show_screen_updates";
    private static final String SHOW_TOUCHES_KEY = "show_touches";
    private static final String SIMULATE_COLOR_SPACE = "simulate_color_space";
    private static final String STATE_SHOWING_DIALOG_KEY = "showing_dialog_key";
    private static final String STRICT_MODE_KEY = "strict_mode";
    private static final String TAG = "DevelopmentSettings";
    private static final String TERMINAL_APP_PACKAGE = "com.android.terminal";
    private static final String TRACK_FRAME_TIME_KEY = "track_frame_time";
    private static final String TRANSITION_ANIMATION_SCALE_KEY = "transition_animation_scale";
    private static final String USB_AUDIO_KEY = "usb_audio";
    private static final String USB_CONFIGURATION_KEY = "select_usb_configuration";
    private static final String VERIFY_APPS_OVER_USB_KEY = "verify_apps_over_usb";
    private static final String WAIT_FOR_DEBUGGER_KEY = "wait_for_debugger";
    private static final String WIFI_DISPLAY_CERTIFICATION_KEY = "wifi_display_certification";
    private static final String WIFI_VERBOSE_LOGGING_KEY = "wifi_verbose_logging";
    private static final String WINDOW_ANIMATION_SCALE_KEY = "window_animation_scale";
    private final ArrayList<Preference> mAllPrefs = new ArrayList<>();
    private ListPreference mAnimatorDurationScale;
    private ListPreference mAppProcessLimit;
    private IBackupManager mBackupManager;
    private SwitchPreference mBtHciSnoopLog;
    private Preference mBugreport;
    private Preference mClearAdbKeys;
    private ColorModePreference mColorModePreference;
    private ContentResolver mContentResolver;
    private String mDebugApp;
    private Preference mDebugAppPref;
    private ListPreference mDebugHwOverdraw;
    private SwitchPreference mDebugLayout;
    private SwitchPreference mDebugViewAttributes;
    private SwitchPreference mDisableOverlays;
    private final HashSet<Preference> mDisabledPrefs = new HashSet<>();
    private DevicePolicyManager mDpm;
    private SwitchPreference mEnableAdb;
    private SwitchPreference mEnableDeveloper;
    private SwitchPreference mEnableOemUnlock;
    private SwitchPreference mEnableTerminal;
    private SwitchPreference mForceAllowOnExternal;
    private SwitchPreference mForceHardwareUi;
    private SwitchPreference mForceMsaa;
    private SwitchPreference mForceResizable;
    private SwitchPreference mForceRtlLayout;
    private boolean mHaveDebugSettings;
    private SwitchPreference mImmediatelyDestroyActivities;
    private SwitchPreference mKeepScreenOn;
    private boolean mLastEnabledState;
    private LogdSizePreferenceController mLogdSizeController;
    private LogpersistPreferenceController mLogpersistController;
    private SwitchPreference mMobileDataAlwaysOn;
    private String mMockLocationApp;
    private Preference mMockLocationAppPref;
    private ListPreference mOpenGLTraces;
    private ListPreference mOverlayDisplayDevices;
    private PreferenceScreen mPassword;
    private String mPendingDialogKey;
    private SwitchPreference mPointerLocation;
    private final ArrayList<SwitchPreference> mResetSwitchPrefs = new ArrayList<>();
    private SwitchPreference mShowAllANRs;
    private SwitchPreference mShowHwLayersUpdates;
    private SwitchPreference mShowHwScreenUpdates;
    private ListPreference mShowNonRectClip;
    private SwitchPreference mShowScreenUpdates;
    private SwitchPreference mShowTouches;
    private ListPreference mSimulateColorSpace;
    private SwitchPreference mStrictMode;
    private ListPreference mTrackFrameTime;
    private ListPreference mTransitionAnimationScale;
    private SwitchPreference mUSBAudio;
    private UserManager mUm;
    private boolean mUnavailable;
    private ListPreference mUsbConfiguration;
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            DevelopmentFragment.this.updateUsbConfigurationValues();
        }
    };
    private SwitchPreference mVerifyAppsOverUsb;
    private SwitchPreference mWaitForDebugger;
    private SwitchPreference mWifiDisplayCertification;
    private WifiManager mWifiManager;
    private SwitchPreference mWifiVerboseLogging;
    private ListPreference mWindowAnimationScale;
    private IWindowManager mWindowManager;

    public static DevelopmentFragment newInstance() {
        return new DevelopmentFragment();
    }

    public int getMetricsCategory() {
        return 39;
    }

    public void onCreate(Bundle icicle) {
        if (icicle != null) {
            this.mPendingDialogKey = icicle.getString(STATE_SHOWING_DIALOG_KEY);
        }
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        this.mUm = (UserManager) getActivity().getSystemService("user");
        this.mWifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mContentResolver = getActivity().getContentResolver();
        super.onCreate(icicle);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mLogdSizeController = new LogdSizePreferenceController(getActivity());
        this.mLogpersistController = new LogpersistPreferenceController(getActivity(), getLifecycle());
        if (!this.mUm.isAdminUser() || this.mUm.hasUserRestriction("no_debugging_features") || Settings.Global.getInt(this.mContentResolver, "device_provisioned", 0) == 0) {
            this.mUnavailable = true;
            addPreferencesFromResource(R.xml.development_prefs_not_available);
            return;
        }
        addPreferencesFromResource(R.xml.development_prefs);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mEnableDeveloper = (SwitchPreference) findPreference(ENABLE_DEVELOPER);
        PreferenceGroup debugDebuggingCategory = (PreferenceGroup) findPreference(DEBUG_DEBUGGING_CATEGORY_KEY);
        this.mEnableAdb = findAndInitSwitchPref(ENABLE_ADB);
        this.mClearAdbKeys = findPreference(CLEAR_ADB_KEYS);
        if (!SystemProperties.getBoolean("ro.adb.secure", false) && debugDebuggingCategory != null) {
            debugDebuggingCategory.removePreference(this.mClearAdbKeys);
        }
        this.mAllPrefs.add(this.mClearAdbKeys);
        this.mEnableTerminal = findAndInitSwitchPref(ENABLE_TERMINAL);
        if (!isPackageInstalled(getActivity(), TERMINAL_APP_PACKAGE)) {
            if (debugDebuggingCategory != null) {
                debugDebuggingCategory.removePreference(this.mEnableTerminal);
            }
            this.mEnableTerminal = null;
        }
        this.mBugreport = findPreference(BUGREPORT);
        this.mLogdSizeController.displayPreference(preferenceScreen);
        this.mLogpersistController.displayPreference(preferenceScreen);
        this.mKeepScreenOn = findAndInitSwitchPref(KEEP_SCREEN_ON);
        this.mBtHciSnoopLog = findAndInitSwitchPref(BT_HCI_SNOOP_LOG);
        this.mEnableOemUnlock = findAndInitSwitchPref(ENABLE_OEM_UNLOCK);
        if (!showEnableOemUnlockPreference()) {
            removePreference((Preference) this.mEnableOemUnlock);
            this.mEnableOemUnlock = null;
        }
        removePreference(RUNNING_APPS);
        this.mDebugViewAttributes = findAndInitSwitchPref(DEBUG_VIEW_ATTRIBUTES);
        this.mForceAllowOnExternal = findAndInitSwitchPref(FORCE_ALLOW_ON_EXTERNAL_KEY);
        this.mPassword = (PreferenceScreen) findPreference(LOCAL_BACKUP_PASSWORD);
        this.mPassword.setVisible(false);
        this.mAllPrefs.add(this.mPassword);
        if (!this.mUm.isAdminUser()) {
            disableForUser(this.mEnableAdb);
            disableForUser(this.mClearAdbKeys);
            disableForUser(this.mEnableTerminal);
            disableForUser(this.mPassword);
        }
        this.mDebugAppPref = findPreference(DEBUG_APP_KEY);
        this.mAllPrefs.add(this.mDebugAppPref);
        this.mWaitForDebugger = findAndInitSwitchPref(WAIT_FOR_DEBUGGER_KEY);
        this.mMockLocationAppPref = findPreference(MOCK_LOCATION_APP_KEY);
        this.mAllPrefs.add(this.mMockLocationAppPref);
        this.mVerifyAppsOverUsb = findAndInitSwitchPref(VERIFY_APPS_OVER_USB_KEY);
        if (!showVerifierSetting()) {
            if (debugDebuggingCategory != null) {
                debugDebuggingCategory.removePreference(this.mVerifyAppsOverUsb);
            } else {
                this.mVerifyAppsOverUsb.setEnabled(false);
            }
        }
        this.mStrictMode = findAndInitSwitchPref(STRICT_MODE_KEY);
        this.mPointerLocation = findAndInitSwitchPref(POINTER_LOCATION_KEY);
        this.mShowTouches = findAndInitSwitchPref(SHOW_TOUCHES_KEY);
        this.mShowScreenUpdates = findAndInitSwitchPref(SHOW_SCREEN_UPDATES_KEY);
        this.mDisableOverlays = findAndInitSwitchPref(DISABLE_OVERLAYS_KEY);
        this.mForceHardwareUi = findAndInitSwitchPref(FORCE_HARDWARE_UI_KEY);
        this.mForceMsaa = findAndInitSwitchPref(FORCE_MSAA_KEY);
        this.mTrackFrameTime = addListPreference(TRACK_FRAME_TIME_KEY);
        this.mShowNonRectClip = addListPreference(SHOW_NON_RECTANGULAR_CLIP_KEY);
        this.mShowHwScreenUpdates = findAndInitSwitchPref(SHOW_HW_SCREEN_UPDATES_KEY);
        this.mShowHwLayersUpdates = findAndInitSwitchPref(SHOW_HW_LAYERS_UPDATES_KEY);
        this.mDebugLayout = findAndInitSwitchPref(DEBUG_LAYOUT_KEY);
        this.mForceRtlLayout = findAndInitSwitchPref(FORCE_RTL_LAYOUT_KEY);
        this.mDebugHwOverdraw = addListPreference(DEBUG_HW_OVERDRAW_KEY);
        this.mWifiDisplayCertification = findAndInitSwitchPref(WIFI_DISPLAY_CERTIFICATION_KEY);
        this.mWifiVerboseLogging = findAndInitSwitchPref(WIFI_VERBOSE_LOGGING_KEY);
        this.mMobileDataAlwaysOn = findAndInitSwitchPref(MOBILE_DATA_ALWAYS_ON);
        this.mUsbConfiguration = addListPreference(USB_CONFIGURATION_KEY);
        this.mWindowAnimationScale = addListPreference(WINDOW_ANIMATION_SCALE_KEY);
        this.mTransitionAnimationScale = addListPreference(TRANSITION_ANIMATION_SCALE_KEY);
        this.mAnimatorDurationScale = addListPreference(ANIMATOR_DURATION_SCALE_KEY);
        this.mOverlayDisplayDevices = addListPreference(OVERLAY_DISPLAY_DEVICES_KEY);
        this.mOpenGLTraces = addListPreference(OPENGL_TRACES_KEY);
        this.mSimulateColorSpace = addListPreference(SIMULATE_COLOR_SPACE);
        this.mUSBAudio = findAndInitSwitchPref(USB_AUDIO_KEY);
        this.mForceResizable = findAndInitSwitchPref(FORCE_RESIZABLE_KEY);
        this.mImmediatelyDestroyActivities = (SwitchPreference) findPreference(IMMEDIATELY_DESTROY_ACTIVITIES_KEY);
        this.mAllPrefs.add(this.mImmediatelyDestroyActivities);
        this.mResetSwitchPrefs.add(this.mImmediatelyDestroyActivities);
        this.mAppProcessLimit = addListPreference(APP_PROCESS_LIMIT_KEY);
        this.mShowAllANRs = (SwitchPreference) findPreference(SHOW_ALL_ANRS_KEY);
        this.mAllPrefs.add(this.mShowAllANRs);
        this.mResetSwitchPrefs.add(this.mShowAllANRs);
        Preference hdcpChecking = findPreference(HDCP_CHECKING_KEY);
        if (hdcpChecking != null) {
            this.mAllPrefs.add(hdcpChecking);
            removePreferenceForProduction(hdcpChecking);
        }
        removePreference(KEY_CONVERT_FBE);
        this.mColorModePreference = (ColorModePreference) findPreference(KEY_COLOR_MODE);
        this.mColorModePreference.updateCurrentAndSupported();
        if (this.mColorModePreference.getColorModeCount() < 2) {
            removePreference(KEY_COLOR_MODE);
            this.mColorModePreference = null;
        }
    }

    private void removePreference(String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    private ListPreference addListPreference(String prefKey) {
        ListPreference pref = (ListPreference) findPreference(prefKey);
        this.mAllPrefs.add(pref);
        pref.setOnPreferenceChangeListener(this);
        return pref;
    }

    private void disableForUser(Preference pref) {
        if (pref != null) {
            pref.setEnabled(false);
            this.mDisabledPrefs.add(pref);
        }
    }

    private SwitchPreference findAndInitSwitchPref(String key) {
        SwitchPreference pref = (SwitchPreference) findPreference(key);
        if (pref != null) {
            this.mAllPrefs.add(pref);
            this.mResetSwitchPrefs.add(pref);
            return pref;
        }
        throw new IllegalArgumentException("Cannot find preference with key = " + key);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mUnavailable && this.mEnableDeveloper != null) {
            this.mEnableDeveloper.setEnabled(false);
        }
    }

    private boolean removePreferenceForProduction(Preference preference) {
        if (!"user".equals(Build.TYPE)) {
            return false;
        }
        removePreference(preference);
        return true;
    }

    private void removePreference(Preference preference) {
        getPreferenceScreen().removePreference(preference);
        this.mAllPrefs.remove(preference);
        this.mResetSwitchPrefs.remove(preference);
    }

    private void setPrefsEnabledState(boolean enabled) {
        Iterator<Preference> it = this.mAllPrefs.iterator();
        while (it.hasNext()) {
            Preference pref = it.next();
            pref.setEnabled(enabled && !this.mDisabledPrefs.contains(pref));
        }
        this.mLogdSizeController.enablePreference(enabled);
        this.mLogpersistController.enablePreference(enabled);
        updateAllOptions();
    }

    public void onResume() {
        super.onResume();
        if (!this.mUnavailable) {
            if (this.mDpm.getMaximumTimeToLock((ComponentName) null) > 0) {
                this.mDisabledPrefs.add(this.mKeepScreenOn);
            } else {
                this.mDisabledPrefs.remove(this.mKeepScreenOn);
            }
            this.mLastEnabledState = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext());
            this.mEnableDeveloper.setChecked(this.mLastEnabledState);
            setPrefsEnabledState(this.mLastEnabledState);
            if (this.mHaveDebugSettings && !this.mLastEnabledState) {
                DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), true);
                this.mLastEnabledState = true;
                this.mEnableDeveloper.setChecked(this.mLastEnabledState);
                setPrefsEnabledState(this.mLastEnabledState);
            }
            if (this.mColorModePreference != null) {
                this.mColorModePreference.startListening();
                this.mColorModePreference.updateCurrentAndSupported();
            }
            if (this.mPendingDialogKey != null) {
                recreateDialogForKey(this.mPendingDialogKey);
                this.mPendingDialogKey = null;
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mColorModePreference != null) {
            this.mColorModePreference.stopListening();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SHOWING_DIALOG_KEY, getKeyForShowingDialog());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_STATE");
        if (getActivity().registerReceiver(this.mUsbReceiver, filter) == null) {
            updateUsbConfigurationValues();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(this.mUsbReceiver);
    }

    public void onDestroy() {
        super.onDestroy();
        dismissDialogs();
    }

    /* access modifiers changed from: package-private */
    public void updateSwitchPreference(SwitchPreference switchPreference, boolean value) {
        switchPreference.setChecked(value);
        this.mHaveDebugSettings |= value;
    }

    private void updateAllOptions() {
        Context context = getActivity();
        ContentResolver cr = context.getContentResolver();
        boolean z = false;
        this.mHaveDebugSettings = false;
        updateSwitchPreference(this.mEnableAdb, Settings.Global.getInt(cr, "adb_enabled", 0) != 0);
        if (this.mEnableTerminal != null) {
            updateSwitchPreference(this.mEnableTerminal, context.getPackageManager().getApplicationEnabledSetting(TERMINAL_APP_PACKAGE) == 1);
        }
        updateSwitchPreference(this.mKeepScreenOn, Settings.Global.getInt(cr, "stay_on_while_plugged_in", 0) != 0);
        updateSwitchPreference(this.mBtHciSnoopLog, SystemProperties.getBoolean(BTSNOOP_ENABLE_PROPERTY, false));
        if (this.mEnableOemUnlock != null) {
            updateSwitchPreference(this.mEnableOemUnlock, isOemUnlockEnabled(getActivity()));
            this.mEnableOemUnlock.setEnabled(isOemUnlockAllowed());
        }
        updateSwitchPreference(this.mDebugViewAttributes, Settings.Global.getInt(cr, DEBUG_VIEW_ATTRIBUTES, 0) != 0);
        SwitchPreference switchPreference = this.mForceAllowOnExternal;
        if (Settings.Global.getInt(cr, FORCE_ALLOW_ON_EXTERNAL_KEY, 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
        updateHdcpValues();
        updatePasswordSummary();
        updateDebuggerOptions();
        updateMockLocation();
        updateStrictModeVisualOptions();
        updatePointerLocationOptions();
        updateShowTouchesOptions();
        updateFlingerOptions();
        updateHardwareUiOptions();
        updateMsaaOptions();
        updateTrackFrameTimeOptions();
        updateShowNonRectClipOptions();
        updateShowHwScreenUpdatesOptions();
        updateShowHwLayersUpdatesOptions();
        updateDebugHwOverdrawOptions();
        updateDebugLayoutOptions();
        updateAnimationScaleOptions();
        updateOverlayDisplayDevicesOptions();
        updateOpenGLTracesOptions();
        updateImmediatelyDestroyActivitiesOptions();
        updateAppProcessLimitOptions();
        updateShowAllANRsOptions();
        updateVerifyAppsOverUsbOptions();
        updateBugreportOptions();
        updateForceRtlOptions();
        this.mLogdSizeController.updateLogdSizeValues();
        this.mLogpersistController.updateLogpersistValues();
        updateWifiDisplayCertificationOptions();
        updateWifiVerboseLoggingOptions();
        updateMobileDataAlwaysOnOptions();
        updateSimulateColorSpace();
        updateUSBAudioOptions();
        updateForceResizableOptions();
    }

    private void resetDangerousOptions() {
        SystemPropPoker.getInstance().blockPokes();
        Iterator<SwitchPreference> it = this.mResetSwitchPrefs.iterator();
        while (it.hasNext()) {
            SwitchPreference cb = it.next();
            if (cb.isChecked()) {
                cb.setChecked(false);
                onPreferenceTreeClick(cb);
            }
        }
        resetDebuggerOptions();
        this.mLogpersistController.writeLogpersistOption((Object) null, true);
        this.mLogdSizeController.writeLogdSizeOption((Object) null);
        writeAnimationScaleOption(0, this.mWindowAnimationScale, (Object) null);
        writeAnimationScaleOption(1, this.mTransitionAnimationScale, (Object) null);
        writeAnimationScaleOption(2, this.mAnimatorDurationScale, (Object) null);
        if (usingDevelopmentColorSpace()) {
            writeSimulateColorSpace(-1);
        }
        writeOverlayDisplayDevicesOptions((Object) null);
        writeAppProcessLimitOptions((Object) null);
        this.mHaveDebugSettings = false;
        updateAllOptions();
        SystemPropPoker.getInstance().unblockPokes();
        SystemPropPoker.getInstance().poke();
    }

    private void updateHdcpValues() {
        ListPreference hdcpChecking = (ListPreference) findPreference(HDCP_CHECKING_KEY);
        if (hdcpChecking != null) {
            String currentValue = SystemProperties.get(HDCP_CHECKING_PROPERTY);
            String[] values = getResources().getStringArray(R.array.hdcp_checking_values);
            String[] summaries = getResources().getStringArray(R.array.hdcp_checking_summaries);
            int index = 1;
            int i = 0;
            while (true) {
                if (i >= values.length) {
                    break;
                } else if (currentValue.equals(values[i])) {
                    index = i;
                    break;
                } else {
                    i++;
                }
            }
            hdcpChecking.setValue(values[index]);
            hdcpChecking.setSummary(summaries[index]);
            hdcpChecking.setOnPreferenceChangeListener(this);
        }
    }

    private void updatePasswordSummary() {
        try {
            if (this.mBackupManager.hasBackupPassword()) {
                this.mPassword.setSummary((int) R.string.local_backup_password_summary_change);
            } else {
                this.mPassword.setSummary((int) R.string.local_backup_password_summary_none);
            }
        } catch (RemoteException e) {
        }
    }

    private void writeBtHciSnoopLogOptions() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        SystemProperties.set(BTSNOOP_ENABLE_PROPERTY, Boolean.toString(this.mBtHciSnoopLog.isChecked()));
    }

    private void writeDebuggerOptions() {
        try {
            ActivityManager.getService().setDebugApp(this.mDebugApp, this.mWaitForDebugger.isChecked(), true);
        } catch (RemoteException e) {
        }
    }

    private void writeMockLocation() {
        AppOpsManager appOpsManager = (AppOpsManager) getActivity().getSystemService("appops");
        List<AppOpsManager.PackageOps> packageOps = appOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS);
        if (packageOps != null) {
            for (AppOpsManager.PackageOps packageOp : packageOps) {
                if (((AppOpsManager.OpEntry) packageOp.getOps().get(0)).getMode() != 2) {
                    String oldMockLocationApp = packageOp.getPackageName();
                    try {
                        appOpsManager.setMode(58, getActivity().getPackageManager().getApplicationInfo(oldMockLocationApp, 512).uid, oldMockLocationApp, 2);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(this.mMockLocationApp)) {
            try {
                appOpsManager.setMode(58, getActivity().getPackageManager().getApplicationInfo(this.mMockLocationApp, 512).uid, this.mMockLocationApp, 0);
            } catch (PackageManager.NameNotFoundException e2) {
            }
        }
    }

    private static void resetDebuggerOptions() {
        try {
            ActivityManager.getService().setDebugApp((String) null, false, true);
        } catch (RemoteException e) {
        }
    }

    private void updateDebuggerOptions() {
        String label;
        this.mDebugApp = Settings.Global.getString(this.mContentResolver, DEBUG_APP_KEY);
        updateSwitchPreference(this.mWaitForDebugger, Settings.Global.getInt(this.mContentResolver, WAIT_FOR_DEBUGGER_KEY, 0) != 0);
        if (this.mDebugApp == null || this.mDebugApp.length() <= 0) {
            this.mDebugAppPref.setSummary((CharSequence) getResources().getString(R.string.debug_app_not_set));
            this.mWaitForDebugger.setEnabled(false);
            return;
        }
        try {
            CharSequence lab = getActivity().getPackageManager().getApplicationLabel(getActivity().getPackageManager().getApplicationInfo(this.mDebugApp, 512));
            label = lab != null ? lab.toString() : this.mDebugApp;
        } catch (PackageManager.NameNotFoundException e) {
            label = this.mDebugApp;
        }
        this.mDebugAppPref.setSummary((CharSequence) getResources().getString(R.string.debug_app_set, new Object[]{label}));
        this.mWaitForDebugger.setEnabled(true);
        this.mHaveDebugSettings = true;
    }

    private void updateMockLocation() {
        List<AppOpsManager.PackageOps> packageOps = ((AppOpsManager) getActivity().getSystemService("appops")).getPackagesForOps(MOCK_LOCATION_APP_OPS);
        if (packageOps != null) {
            Iterator<AppOpsManager.PackageOps> it = packageOps.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((AppOpsManager.OpEntry) it.next().getOps().get(0)).getMode() == 0) {
                        this.mMockLocationApp = packageOps.get(0).getPackageName();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(this.mMockLocationApp)) {
            String label = this.mMockLocationApp;
            try {
                CharSequence appLabel = getActivity().getPackageManager().getApplicationLabel(getActivity().getPackageManager().getApplicationInfo(this.mMockLocationApp, 512));
                if (appLabel != null) {
                    label = appLabel.toString();
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
            this.mMockLocationAppPref.setSummary((CharSequence) getString(R.string.mock_location_app_set, new Object[]{label}));
            this.mHaveDebugSettings = true;
            return;
        }
        this.mMockLocationAppPref.setSummary((CharSequence) getString(R.string.mock_location_app_not_set));
    }

    private void updateVerifyAppsOverUsbOptions() {
        SwitchPreference switchPreference = this.mVerifyAppsOverUsb;
        boolean z = true;
        if (Settings.Global.getInt(this.mContentResolver, "verifier_verify_adb_installs", 1) == 0) {
            z = false;
        }
        updateSwitchPreference(switchPreference, z);
        this.mVerifyAppsOverUsb.setEnabled(enableVerifierSetting());
    }

    private void writeVerifyAppsOverUsbOptions() {
        Settings.Global.putInt(this.mContentResolver, "verifier_verify_adb_installs", this.mVerifyAppsOverUsb.isChecked() ? 1 : 0);
    }

    private boolean enableVerifierSetting() {
        if (Settings.Global.getInt(this.mContentResolver, "adb_enabled", 0) == 0 || Settings.Global.getInt(this.mContentResolver, "package_verifier_enable", 1) == 0) {
            return false;
        }
        PackageManager pm = getActivity().getPackageManager();
        Intent verification = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
        verification.setType(PACKAGE_MIME_TYPE);
        verification.addFlags(1);
        if (pm.queryBroadcastReceivers(verification, 0).size() == 0) {
            return false;
        }
        return true;
    }

    private boolean showVerifierSetting() {
        return Settings.Global.getInt(this.mContentResolver, "verifier_setting_visible", 1) > 0;
    }

    private static boolean showEnableOemUnlockPreference() {
        return !SystemProperties.get(PERSISTENT_DATA_BLOCK_PROP).equals("");
    }

    private boolean isOemUnlockAllowed() {
        return !this.mUm.hasUserRestriction("no_oem_unlock");
    }

    private void updateBugreportOptions() {
        int i = 1;
        boolean enabled = "1".equals(SystemProperties.get("ro.debuggable")) || this.mEnableDeveloper.isChecked();
        this.mBugreport.setEnabled(enabled);
        ComponentName componentName = new ComponentName("com.android.shell", "com.android.shell.BugreportStorageProvider");
        PackageManager packageManager = getActivity().getPackageManager();
        if (!enabled) {
            i = 0;
        }
        packageManager.setComponentEnabledSetting(componentName, i, 0);
    }

    public static void captureBugReport(Activity activity) {
        Toast.makeText(activity, R.string.capturing_bugreport, 0).show();
        try {
            ActivityManager.getService().requestBugReport(0);
        } catch (RemoteException e) {
            Log.e(TAG, "Error taking bugreport", e);
        }
    }

    private static int currentStrictModeActiveIndex() {
        if (TextUtils.isEmpty(SystemProperties.get("persist.sys.strictmode.visual"))) {
            return 0;
        }
        return SystemProperties.getBoolean("persist.sys.strictmode.visual", false) ? 1 : 2;
    }

    private void writeStrictModeVisualOptions() {
        try {
            this.mWindowManager.setStrictModeVisualIndicatorPreference(this.mStrictMode.isChecked() ? "1" : "");
        } catch (RemoteException e) {
        }
    }

    private void updateStrictModeVisualOptions() {
        SwitchPreference switchPreference = this.mStrictMode;
        boolean z = true;
        if (currentStrictModeActiveIndex() != 1) {
            z = false;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writePointerLocationOptions() {
        Settings.System.putInt(this.mContentResolver, POINTER_LOCATION_KEY, this.mPointerLocation.isChecked() ? 1 : 0);
    }

    private void updatePointerLocationOptions() {
        SwitchPreference switchPreference = this.mPointerLocation;
        boolean z = false;
        if (Settings.System.getInt(this.mContentResolver, POINTER_LOCATION_KEY, 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeShowTouchesOptions() {
        Settings.System.putInt(this.mContentResolver, SHOW_TOUCHES_KEY, this.mShowTouches.isChecked() ? 1 : 0);
    }

    private void updateShowTouchesOptions() {
        SwitchPreference switchPreference = this.mShowTouches;
        boolean z = false;
        if (Settings.System.getInt(this.mContentResolver, SHOW_TOUCHES_KEY, 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void updateFlingerOptions() {
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                boolean z = false;
                flinger.transact(1010, data, reply, 0);
                int readInt = reply.readInt();
                int readInt2 = reply.readInt();
                updateSwitchPreference(this.mShowScreenUpdates, reply.readInt() != 0);
                int readInt3 = reply.readInt();
                int disableOverlays = reply.readInt();
                SwitchPreference switchPreference = this.mDisableOverlays;
                if (disableOverlays != 0) {
                    z = true;
                }
                updateSwitchPreference(switchPreference, z);
                reply.recycle();
                data.recycle();
            }
        } catch (RemoteException e) {
        }
    }

    private void writeShowUpdatesOption() {
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                data.writeInt((int) this.mShowScreenUpdates.isChecked());
                flinger.transact(1002, data, (Parcel) null, 0);
                data.recycle();
                updateFlingerOptions();
            }
        } catch (RemoteException e) {
        }
    }

    private void writeDisableOverlaysOption() {
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                data.writeInt((int) this.mDisableOverlays.isChecked());
                flinger.transact(1008, data, (Parcel) null, 0);
                data.recycle();
                updateFlingerOptions();
            }
        } catch (RemoteException e) {
        }
    }

    private void updateHardwareUiOptions() {
        updateSwitchPreference(this.mForceHardwareUi, SystemProperties.getBoolean(HARDWARE_UI_PROPERTY, false));
    }

    private void writeHardwareUiOptions() {
        SystemProperties.set(HARDWARE_UI_PROPERTY, this.mForceHardwareUi.isChecked() ? "true" : "false");
        SystemPropPoker.getInstance().poke();
    }

    private void updateMsaaOptions() {
        updateSwitchPreference(this.mForceMsaa, SystemProperties.getBoolean(MSAA_PROPERTY, false));
    }

    private void writeMsaaOptions() {
        SystemProperties.set(MSAA_PROPERTY, this.mForceMsaa.isChecked() ? "true" : "false");
        SystemPropPoker.getInstance().poke();
    }

    private void updateTrackFrameTimeOptions() {
        String value = SystemProperties.get("debug.hwui.profile");
        if (value == null) {
            value = "";
        }
        CharSequence[] values = this.mTrackFrameTime.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (value.contentEquals(values[i])) {
                this.mTrackFrameTime.setValueIndex(i);
                this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[i]);
                return;
            }
        }
        this.mTrackFrameTime.setValueIndex(0);
        this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[0]);
    }

    private void writeTrackFrameTimeOptions(Object newValue) {
        SystemProperties.set("debug.hwui.profile", newValue == null ? "" : newValue.toString());
        SystemPropPoker.getInstance().poke();
        updateTrackFrameTimeOptions();
    }

    private void updateShowNonRectClipOptions() {
        String value = SystemProperties.get("debug.hwui.show_non_rect_clip");
        if (value == null) {
            value = "hide";
        }
        CharSequence[] values = this.mShowNonRectClip.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (value.contentEquals(values[i])) {
                this.mShowNonRectClip.setValueIndex(i);
                this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[i]);
                return;
            }
        }
        this.mShowNonRectClip.setValueIndex(0);
        this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[0]);
    }

    private void writeShowNonRectClipOptions(Object newValue) {
        SystemProperties.set("debug.hwui.show_non_rect_clip", newValue == null ? "" : newValue.toString());
        SystemPropPoker.getInstance().poke();
        updateShowNonRectClipOptions();
    }

    private void updateShowHwScreenUpdatesOptions() {
        updateSwitchPreference(this.mShowHwScreenUpdates, SystemProperties.getBoolean("debug.hwui.show_dirty_regions", false));
    }

    private void writeShowHwScreenUpdatesOptions() {
        SystemProperties.set("debug.hwui.show_dirty_regions", this.mShowHwScreenUpdates.isChecked() ? "true" : null);
        SystemPropPoker.getInstance().poke();
    }

    private void updateShowHwLayersUpdatesOptions() {
        updateSwitchPreference(this.mShowHwLayersUpdates, SystemProperties.getBoolean("debug.hwui.show_layers_updates", false));
    }

    private void writeShowHwLayersUpdatesOptions() {
        SystemProperties.set("debug.hwui.show_layers_updates", this.mShowHwLayersUpdates.isChecked() ? "true" : null);
        SystemPropPoker.getInstance().poke();
    }

    private void updateDebugHwOverdrawOptions() {
        String value = SystemProperties.get("debug.hwui.overdraw");
        if (value == null) {
            value = "";
        }
        CharSequence[] values = this.mDebugHwOverdraw.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (value.contentEquals(values[i])) {
                this.mDebugHwOverdraw.setValueIndex(i);
                this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[i]);
                return;
            }
        }
        this.mDebugHwOverdraw.setValueIndex(0);
        this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[0]);
    }

    private void writeDebugHwOverdrawOptions(Object newValue) {
        SystemProperties.set("debug.hwui.overdraw", newValue == null ? "" : newValue.toString());
        SystemPropPoker.getInstance().poke();
        updateDebugHwOverdrawOptions();
    }

    private void updateDebugLayoutOptions() {
        updateSwitchPreference(this.mDebugLayout, SystemProperties.getBoolean("debug.layout", false));
    }

    private void writeDebugLayoutOptions() {
        SystemProperties.set("debug.layout", this.mDebugLayout.isChecked() ? "true" : "false");
        SystemPropPoker.getInstance().poke();
    }

    private void updateSimulateColorSpace() {
        if (Settings.Secure.getInt(this.mContentResolver, "accessibility_display_daltonizer_enabled", 0) != 0) {
            String mode = Integer.toString(Settings.Secure.getInt(this.mContentResolver, "accessibility_display_daltonizer", -1));
            this.mSimulateColorSpace.setValue(mode);
            if (this.mSimulateColorSpace.findIndexOfValue(mode) < 0) {
                this.mSimulateColorSpace.setSummary(getString(R.string.daltonizer_type_overridden, new Object[]{getString(R.string.accessibility_display_daltonizer_preference_title)}));
            } else {
                this.mSimulateColorSpace.setSummary("%s");
            }
        } else {
            this.mSimulateColorSpace.setValue(Integer.toString(-1));
        }
    }

    private boolean usingDevelopmentColorSpace() {
        if (Settings.Secure.getInt(this.mContentResolver, "accessibility_display_daltonizer_enabled", 0) != 0) {
            if (this.mSimulateColorSpace.findIndexOfValue(Integer.toString(Settings.Secure.getInt(this.mContentResolver, "accessibility_display_daltonizer", -1))) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void writeSimulateColorSpace(Object value) {
        int newMode = Integer.parseInt(value.toString());
        if (newMode < 0) {
            Settings.Secure.putInt(this.mContentResolver, "accessibility_display_daltonizer_enabled", 0);
            return;
        }
        Settings.Secure.putInt(this.mContentResolver, "accessibility_display_daltonizer_enabled", 1);
        Settings.Secure.putInt(this.mContentResolver, "accessibility_display_daltonizer", newMode);
    }

    private void updateUSBAudioOptions() {
        SwitchPreference switchPreference = this.mUSBAudio;
        boolean z = false;
        if (Settings.Secure.getInt(this.mContentResolver, "usb_audio_automatic_routing_disabled", 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeUSBAudioOptions() {
        Settings.Secure.putInt(this.mContentResolver, "usb_audio_automatic_routing_disabled", this.mUSBAudio.isChecked() ? 1 : 0);
    }

    private void updateForceResizableOptions() {
        SwitchPreference switchPreference = this.mForceResizable;
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, FORCE_RESIZABLE_KEY, 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeForceResizableOptions() {
        Settings.Global.putInt(this.mContentResolver, FORCE_RESIZABLE_KEY, this.mForceResizable.isChecked() ? 1 : 0);
    }

    private void updateForceRtlOptions() {
        SwitchPreference switchPreference = this.mForceRtlLayout;
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, "debug.force_rtl", 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeForceRtlOptions() {
        boolean value = this.mForceRtlLayout.isChecked();
        Settings.Global.putInt(this.mContentResolver, "debug.force_rtl", value);
        SystemProperties.set("debug.force_rtl", value ? "1" : "0");
        LocalePicker.updateLocale(getActivity().getResources().getConfiguration().getLocales().get(0));
    }

    private void updateWifiDisplayCertificationOptions() {
        SwitchPreference switchPreference = this.mWifiDisplayCertification;
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, "wifi_display_certification_on", 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeWifiDisplayCertificationOptions() {
        Settings.Global.putInt(this.mContentResolver, "wifi_display_certification_on", this.mWifiDisplayCertification.isChecked() ? 1 : 0);
    }

    private void updateWifiVerboseLoggingOptions() {
        updateSwitchPreference(this.mWifiVerboseLogging, this.mWifiManager != null && this.mWifiManager.getVerboseLoggingLevel() > 0);
    }

    private void writeWifiVerboseLoggingOptions() {
        if (this.mWifiManager != null) {
            this.mWifiManager.enableVerboseLogging(this.mWifiVerboseLogging.isChecked() ? 1 : 0);
        }
    }

    private void updateMobileDataAlwaysOnOptions() {
        SwitchPreference switchPreference = this.mMobileDataAlwaysOn;
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, MOBILE_DATA_ALWAYS_ON, 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void writeMobileDataAlwaysOnOptions() {
        Settings.Global.putInt(this.mContentResolver, MOBILE_DATA_ALWAYS_ON, this.mMobileDataAlwaysOn.isChecked() ? 1 : 0);
    }

    /* access modifiers changed from: private */
    public void updateUsbConfigurationValues() {
        if (this.mUsbConfiguration != null) {
            String[] values = getResources().getStringArray(R.array.usb_configuration_values);
            String[] titles = getResources().getStringArray(R.array.usb_configuration_titles);
            int index = 0;
            long functions = ((UsbManager) getActivity().getSystemService("usb")).getCurrentFunctions();
            int i = 0;
            while (true) {
                if (i >= titles.length) {
                    break;
                } else if ((UsbManager.usbFunctionsFromString(values[i]) | functions) != 0) {
                    index = i;
                    break;
                } else {
                    i++;
                }
            }
            this.mUsbConfiguration.setValue(values[index]);
            this.mUsbConfiguration.setSummary(titles[index]);
            this.mUsbConfiguration.setOnPreferenceChangeListener(this);
        }
    }

    private void writeUsbConfigurationOption(Object newValue) {
        ((UsbManager) getActivity().getSystemService("usb")).setCurrentFunctions(UsbManager.usbFunctionsFromString(newValue.toString()));
    }

    private void writeImmediatelyDestroyActivitiesOptions() {
        try {
            ActivityManager.getService().setAlwaysFinish(this.mImmediatelyDestroyActivities.isChecked());
        } catch (RemoteException e) {
        }
    }

    private void updateImmediatelyDestroyActivitiesOptions() {
        SwitchPreference switchPreference = this.mImmediatelyDestroyActivities;
        boolean z = false;
        if (Settings.Global.getInt(this.mContentResolver, "always_finish_activities", 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    private void updateAnimationScaleValue(int which, ListPreference pref) {
        try {
            float scale = this.mWindowManager.getAnimationScale(which);
            if (scale != 1.0f) {
                this.mHaveDebugSettings = true;
            }
            CharSequence[] values = pref.getEntryValues();
            for (int i = 0; i < values.length; i++) {
                if (scale <= Float.parseFloat(values[i].toString())) {
                    pref.setValueIndex(i);
                    pref.setSummary(pref.getEntries()[i]);
                    return;
                }
            }
            pref.setValueIndex(values.length - 1);
            pref.setSummary(pref.getEntries()[0]);
        } catch (RemoteException e) {
        }
    }

    private void updateAnimationScaleOptions() {
        updateAnimationScaleValue(0, this.mWindowAnimationScale);
        updateAnimationScaleValue(1, this.mTransitionAnimationScale);
        updateAnimationScaleValue(2, this.mAnimatorDurationScale);
    }

    private void writeAnimationScaleOption(int which, ListPreference pref, Object newValue) {
        float scale;
        if (newValue != null) {
            try {
                scale = Float.parseFloat(newValue.toString());
            } catch (RemoteException e) {
                return;
            }
        } else {
            scale = 1.0f;
        }
        this.mWindowManager.setAnimationScale(which, scale);
        updateAnimationScaleValue(which, pref);
    }

    private void updateOverlayDisplayDevicesOptions() {
        String value = Settings.Global.getString(this.mContentResolver, OVERLAY_DISPLAY_DEVICES_KEY);
        if (value == null) {
            value = "";
        }
        CharSequence[] values = this.mOverlayDisplayDevices.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (value.contentEquals(values[i])) {
                this.mOverlayDisplayDevices.setValueIndex(i);
                this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[i]);
                return;
            }
        }
        this.mOverlayDisplayDevices.setValueIndex(0);
        this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[0]);
    }

    private void writeOverlayDisplayDevicesOptions(Object newValue) {
        Settings.Global.putString(this.mContentResolver, OVERLAY_DISPLAY_DEVICES_KEY, (String) newValue);
        updateOverlayDisplayDevicesOptions();
    }

    private void updateOpenGLTracesOptions() {
        String value = SystemProperties.get(OPENGL_TRACES_PROPERTY);
        if (value == null) {
            value = "";
        }
        CharSequence[] values = this.mOpenGLTraces.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (value.contentEquals(values[i])) {
                this.mOpenGLTraces.setValueIndex(i);
                this.mOpenGLTraces.setSummary(this.mOpenGLTraces.getEntries()[i]);
                return;
            }
        }
        this.mOpenGLTraces.setValueIndex(0);
        this.mOpenGLTraces.setSummary(this.mOpenGLTraces.getEntries()[0]);
    }

    private void writeOpenGLTracesOptions(Object newValue) {
        SystemProperties.set(OPENGL_TRACES_PROPERTY, newValue == null ? "" : newValue.toString());
        SystemPropPoker.getInstance().poke();
        updateOpenGLTracesOptions();
    }

    private void updateAppProcessLimitOptions() {
        try {
            int limit = ActivityManager.getService().getProcessLimit();
            CharSequence[] values = this.mAppProcessLimit.getEntryValues();
            for (int i = 0; i < values.length; i++) {
                if (Integer.parseInt(values[i].toString()) >= limit) {
                    if (i != 0) {
                        this.mHaveDebugSettings = true;
                    }
                    this.mAppProcessLimit.setValueIndex(i);
                    this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[i]);
                    return;
                }
            }
            this.mAppProcessLimit.setValueIndex(0);
            this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[0]);
        } catch (RemoteException e) {
        }
    }

    private void writeAppProcessLimitOptions(Object newValue) {
        int limit;
        if (newValue != null) {
            try {
                limit = Integer.parseInt(newValue.toString());
            } catch (RemoteException e) {
                return;
            }
        } else {
            limit = -1;
        }
        ActivityManager.getService().setProcessLimit(limit);
        updateAppProcessLimitOptions();
    }

    private void writeShowAllANRsOptions() {
        Settings.Secure.putInt(this.mContentResolver, "anr_show_background", this.mShowAllANRs.isChecked() ? 1 : 0);
    }

    private void updateShowAllANRsOptions() {
        SwitchPreference switchPreference = this.mShowAllANRs;
        boolean z = false;
        if (Settings.Secure.getInt(this.mContentResolver, "anr_show_background", 0) != 0) {
            z = true;
        }
        updateSwitchPreference(switchPreference, z);
    }

    public void onOemUnlockConfirm() {
        this.mEnableOemUnlock.setChecked(true);
        setOemUnlockEnabled(getActivity(), true);
        updateAllOptions();
    }

    public void onEnableDevelopmentConfirm() {
        this.mEnableDeveloper.setChecked(true);
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), true);
        this.mLastEnabledState = true;
        setPrefsEnabledState(true);
    }

    public void onEnableAdbConfirm() {
        Settings.Global.putInt(this.mContentResolver, "adb_enabled", 1);
        this.mEnableAdb.setChecked(true);
        updateVerifyAppsOverUsbOptions();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == -1) {
                this.mDebugApp = data.getAction();
                writeDebuggerOptions();
                updateDebuggerOptions();
            }
        } else if (requestCode != 1001) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == -1) {
            this.mMockLocationApp = data.getAction();
            writeMockLocation();
            updateMockLocation();
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        int i;
        if (ActivityManager.isUserAMonkey()) {
            return false;
        }
        if (preference == this.mEnableDeveloper) {
            if (this.mEnableDeveloper.isChecked()) {
                super.onPreferenceTreeClick(preference);
                this.mEnableDeveloper.setChecked(false);
            } else {
                resetDangerousOptions();
                DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), false);
                this.mLastEnabledState = false;
                setPrefsEnabledState(false);
            }
        } else if (preference == this.mBugreport) {
            captureBugReport(getActivity());
        } else if (preference != this.mEnableAdb) {
            int i2 = 1;
            if (preference == this.mEnableTerminal) {
                PackageManager pm = getActivity().getPackageManager();
                if (!this.mEnableTerminal.isChecked()) {
                    i2 = 0;
                }
                pm.setApplicationEnabledSetting(TERMINAL_APP_PACKAGE, i2, 0);
            } else if (preference == this.mKeepScreenOn) {
                ContentResolver contentResolver = this.mContentResolver;
                if (this.mKeepScreenOn.isChecked()) {
                    i = 3;
                } else {
                    i = 0;
                }
                Settings.Global.putInt(contentResolver, "stay_on_while_plugged_in", i);
            } else if (preference == this.mBtHciSnoopLog) {
                writeBtHciSnoopLogOptions();
            } else if (preference == this.mEnableOemUnlock) {
                if (this.mEnableOemUnlock.isChecked()) {
                    super.onPreferenceTreeClick(preference);
                    this.mEnableOemUnlock.setChecked(false);
                } else {
                    setOemUnlockEnabled(getActivity(), false);
                }
            } else if (preference == this.mMockLocationAppPref) {
                Intent intent = new Intent(getActivity(), AppPicker.class);
                intent.putExtra(AppPicker.EXTRA_REQUESTIING_PERMISSION, "android.permission.ACCESS_MOCK_LOCATION");
                startActivityForResult(intent, 1001);
            } else if (preference == this.mDebugViewAttributes) {
                Settings.Global.putInt(this.mContentResolver, DEBUG_VIEW_ATTRIBUTES, this.mDebugViewAttributes.isChecked() ? 1 : 0);
            } else if (preference == this.mForceAllowOnExternal) {
                Settings.Global.putInt(this.mContentResolver, FORCE_ALLOW_ON_EXTERNAL_KEY, this.mForceAllowOnExternal.isChecked() ? 1 : 0);
            } else if (preference == this.mDebugAppPref) {
                Intent intent2 = new Intent(getActivity(), AppPicker.class);
                intent2.putExtra(AppPicker.EXTRA_DEBUGGABLE, true);
                startActivityForResult(intent2, 1000);
            } else if (preference == this.mWaitForDebugger) {
                writeDebuggerOptions();
            } else if (preference == this.mVerifyAppsOverUsb) {
                writeVerifyAppsOverUsbOptions();
            } else if (preference == this.mStrictMode) {
                writeStrictModeVisualOptions();
            } else if (preference == this.mPointerLocation) {
                writePointerLocationOptions();
            } else if (preference == this.mShowTouches) {
                writeShowTouchesOptions();
            } else if (preference == this.mShowScreenUpdates) {
                writeShowUpdatesOption();
            } else if (preference == this.mDisableOverlays) {
                writeDisableOverlaysOption();
            } else if (preference == this.mImmediatelyDestroyActivities) {
                writeImmediatelyDestroyActivitiesOptions();
            } else if (preference == this.mShowAllANRs) {
                writeShowAllANRsOptions();
            } else if (preference == this.mForceHardwareUi) {
                writeHardwareUiOptions();
            } else if (preference == this.mForceMsaa) {
                writeMsaaOptions();
            } else if (preference == this.mShowHwScreenUpdates) {
                writeShowHwScreenUpdatesOptions();
            } else if (preference == this.mShowHwLayersUpdates) {
                writeShowHwLayersUpdatesOptions();
            } else if (preference == this.mDebugLayout) {
                writeDebugLayoutOptions();
            } else if (preference == this.mForceRtlLayout) {
                writeForceRtlOptions();
            } else if (preference == this.mWifiDisplayCertification) {
                writeWifiDisplayCertificationOptions();
            } else if (preference == this.mWifiVerboseLogging) {
                writeWifiVerboseLoggingOptions();
            } else if (preference == this.mMobileDataAlwaysOn) {
                writeMobileDataAlwaysOnOptions();
            } else if (preference == this.mUSBAudio) {
                writeUSBAudioOptions();
            } else if (preference != this.mForceResizable) {
                return super.onPreferenceTreeClick(preference);
            } else {
                writeForceResizableOptions();
            }
        } else if (this.mEnableAdb.isChecked()) {
            super.onPreferenceTreeClick(preference);
            this.mEnableAdb.setChecked(false);
        } else {
            Settings.Global.putInt(this.mContentResolver, "adb_enabled", 0);
            this.mVerifyAppsOverUsb.setEnabled(false);
            this.mVerifyAppsOverUsb.setChecked(false);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (HDCP_CHECKING_KEY.equals(preference.getKey())) {
            SystemProperties.set(HDCP_CHECKING_PROPERTY, newValue.toString());
            updateHdcpValues();
            SystemPropPoker.getInstance().poke();
            return true;
        } else if (preference == this.mUsbConfiguration) {
            writeUsbConfigurationOption(newValue);
            return true;
        } else if (preference == this.mWindowAnimationScale) {
            writeAnimationScaleOption(0, this.mWindowAnimationScale, newValue);
            return true;
        } else if (preference == this.mTransitionAnimationScale) {
            writeAnimationScaleOption(1, this.mTransitionAnimationScale, newValue);
            return true;
        } else if (preference == this.mAnimatorDurationScale) {
            writeAnimationScaleOption(2, this.mAnimatorDurationScale, newValue);
            return true;
        } else if (preference == this.mOverlayDisplayDevices) {
            writeOverlayDisplayDevicesOptions(newValue);
            return true;
        } else if (preference == this.mOpenGLTraces) {
            writeOpenGLTracesOptions(newValue);
            return true;
        } else if (preference == this.mTrackFrameTime) {
            writeTrackFrameTimeOptions(newValue);
            return true;
        } else if (preference == this.mDebugHwOverdraw) {
            writeDebugHwOverdrawOptions(newValue);
            return true;
        } else if (preference == this.mShowNonRectClip) {
            writeShowNonRectClipOptions(newValue);
            return true;
        } else if (preference == this.mAppProcessLimit) {
            writeAppProcessLimitOptions(newValue);
            return true;
        } else if (preference != this.mSimulateColorSpace) {
            return false;
        } else {
            writeSimulateColorSpace(newValue);
            return true;
        }
    }

    private String getKeyForShowingDialog() {
        List<ConfirmationDialogController> dialogControllers = new ArrayList<>(2);
        dialogControllers.add(this.mLogpersistController);
        for (ConfirmationDialogController dialogController : dialogControllers) {
            if (dialogController.isConfirmationDialogShowing()) {
                return dialogController.getPreferenceKey();
            }
        }
        return null;
    }

    private void recreateDialogForKey(String preferenceKey) {
        List<ConfirmationDialogController> dialogControllers = new ArrayList<>(2);
        dialogControllers.add(this.mLogpersistController);
        for (ConfirmationDialogController dialogController : dialogControllers) {
            if (TextUtils.equals(preferenceKey, dialogController.getPreferenceKey())) {
                dialogController.showConfirmationDialog(findPreference(preferenceKey));
            }
        }
    }

    private void dismissDialogs() {
        this.mLogpersistController.dismissConfirmationDialog();
    }

    private static boolean isPackageInstalled(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static boolean isOemUnlockEnabled(Context context) {
        return ((PersistentDataBlockManager) context.getSystemService("persistent_data_block")).getOemUnlockEnabled();
    }

    static void setOemUnlockEnabled(Context context, boolean enabled) {
        try {
            ((PersistentDataBlockManager) context.getSystemService("persistent_data_block")).setOemUnlockEnabled(enabled);
        } catch (SecurityException e) {
            Log.e(TAG, "Fail to set oem unlock.", e);
        }
    }
}
