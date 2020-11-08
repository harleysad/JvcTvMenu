package com.android.tv.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.service.settings.suggestions.Suggestion;
import android.support.annotation.Keep;
import android.support.annotation.VisibleForTesting;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.suggestions.SuggestionControllerMixin;
import com.android.settingslib.utils.IconCache;
import com.android.tv.settings.HotwordSwitchController;
import com.android.tv.settings.accounts.AccountsFragment;
import com.android.tv.settings.connectivity.ConnectivityListener;
import com.android.tv.settings.partnercustomizer.retailmode.RetailModeService;
import com.android.tv.settings.suggestions.SuggestionPreference;
import com.android.tv.settings.system.SecurityFragment;
import com.android.tv.twopanelsettings.slices.SlicePreference;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Keep
public class MainFragment extends PreferenceControllerFragment implements SuggestionControllerMixin.SuggestionControllerHost, SuggestionPreference.Callback, HotwordSwitchController.HotwordStateListener {
    private static final String ACTION_ACCOUNTS = "com.android.tv.settings.ACCOUNTS";
    @VisibleForTesting
    static final String ACTION_CONNECTED_DEVICES = "com.android.tv.settings.CONNECTED_DEVICES";
    public static final String ACTION_SOUND = "com.android.tv.settings.SOUND";
    @VisibleForTesting
    static final String KEY_ACCESSORIES = "remotes_and_accessories";
    @VisibleForTesting
    static final String KEY_ACCOUNTS_AND_SIGN_IN = "accounts_and_sign_in";
    @VisibleForTesting
    static final String KEY_ACCOUNTS_AND_SIGN_IN_SLICE = "accounts_and_sign_in_slice";
    private static final String KEY_APPLICATIONS = "applications";
    private static final String KEY_CHANNEL = "channel";
    @VisibleForTesting
    static final String KEY_CONNECTED_DEVICES = "connected_devices";
    @VisibleForTesting
    static final String KEY_NETWORK = "network";
    @VisibleForTesting
    static final String KEY_QUICK_SETTINGS = "quick_settings";
    @VisibleForTesting
    static final String KEY_SOUND = "sound";
    private static final String KEY_SUGGESTIONS_LIST = "suggestions";
    private static final String TAG = "MainFragment";
    private final BroadcastReceiver mBCMReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MainFragment.this.updateAccessoryPref();
        }
    };
    @VisibleForTesting
    BluetoothAdapter mBtAdapter;
    @VisibleForTesting
    ConnectivityListener mConnectivityListener;
    @VisibleForTesting
    boolean mHasAccounts;
    @VisibleForTesting
    boolean mHasBtAccessories;
    private SwitchPreference mHotwordSwitch;
    private HotwordSwitchController mHotwordSwitchController;
    @VisibleForTesting
    IconCache mIconCache;
    private List<AbstractPreferenceController> mPreferenceControllers;
    private PreferenceCategory mQuickSettingsList;
    private SuggestionControllerMixin mSuggestionControllerMixin;
    @VisibleForTesting
    PreferenceCategory mSuggestionsList;
    private TakeBugReportController mTakeBugReportController;
    private Preference mTakeBugReportPreference;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public int getMetricsCategory() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return R.xml.main_prefs;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mIconCache = new IconCache(getContext());
        this.mConnectivityListener = new ConnectivityListener(getContext(), new ConnectivityListener.Listener() {
            public final void onConnectivityChange() {
                MainFragment.this.updateConnectivity();
            }
        }, getLifecycle());
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
    }

    public void onDestroy() {
        if (this.mHotwordSwitchController != null) {
            this.mHotwordSwitchController.unregister();
        }
        super.onDestroy();
    }

    private boolean shouldShowQuickSettings() {
        for (AbstractPreferenceController controller : this.mPreferenceControllers) {
            if (controller.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void showOrHideQuickSettings() {
        if (shouldShowQuickSettings()) {
            showQuickSettings();
        } else {
            hideQuickSettings();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showOrHideQuickSettings();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void showQuickSettings() {
        if (this.mQuickSettingsList == null) {
            this.mQuickSettingsList = new PreferenceCategory(getPreferenceManager().getContext());
            this.mQuickSettingsList.setKey(KEY_QUICK_SETTINGS);
            this.mQuickSettingsList.setTitle((int) R.string.header_category_quick_settings);
            this.mQuickSettingsList.setOrder(1);
            getPreferenceScreen().addPreference(this.mQuickSettingsList);
            if (this.mHotwordSwitchController.isAvailable()) {
                this.mHotwordSwitch = new SwitchPreference(getPreferenceManager().getContext());
                this.mHotwordSwitch.setKey("hotword_switch");
                this.mHotwordSwitchController.updateState(this.mHotwordSwitch);
                this.mQuickSettingsList.addPreference(this.mHotwordSwitch);
            }
            if (this.mTakeBugReportController.isAvailable()) {
                this.mTakeBugReportPreference = new Preference(getPreferenceManager().getContext());
                this.mTakeBugReportPreference.setKey("take_bug_report");
                this.mTakeBugReportController.updateState(this.mTakeBugReportPreference);
                this.mQuickSettingsList.addPreference(this.mTakeBugReportPreference);
            }
        }
    }

    private void hideQuickSettings() {
        if (findPreference(KEY_QUICK_SETTINGS) != null) {
            this.mQuickSettingsList.removeAll();
            getPreferenceScreen().removePreference(this.mQuickSettingsList);
            this.mQuickSettingsList = null;
        }
    }

    public void onHotwordStateChanged() {
        if (this.mHotwordSwitch != null) {
            this.mHotwordSwitchController.updateState(this.mHotwordSwitch);
        }
        showOrHideQuickSettings();
    }

    public void onHotwordEnable() {
        try {
            Intent intent = new Intent("com.google.android.assistant.HOTWORD_ENABLE");
            intent.setPackage("com.google.android.katniss");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Unable to find hotwording activity.", e);
        }
    }

    public void onHotwordDisable() {
        try {
            Intent intent = new Intent("com.google.android.assistant.HOTWORD_DISABLE");
            intent.setPackage("com.google.android.katniss");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Unable to find hotwording activity.", e);
        }
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Preference accessoryPreference;
        setPreferencesFromResource(R.xml.main_prefs, (String) null);
        if (isRestricted()) {
            Preference appPref = findPreference(KEY_APPLICATIONS);
            if (appPref != null) {
                appPref.setVisible(false);
            }
            Preference accountsPref = findPreference(KEY_ACCOUNTS_AND_SIGN_IN);
            if (accountsPref != null) {
                accountsPref.setVisible(false);
            }
        }
        if (!supportBlueTooth() && (accessoryPreference = findPreference(KEY_ACCESSORIES)) != null) {
            accessoryPreference.setVisible(false);
        }
        this.mHotwordSwitchController.init(this);
        updateSoundSettings();
        findPreference(KEY_CHANNEL).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setClassName(RetailModeService.PACKAGE_NAME_LIVE_TV, "com.mediatek.wwtv.setting.LiveTvSetting");
                if (MainFragment.this.getArguments().getBoolean("isFromLiveTV")) {
                    intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SRC, EventHelper.MTK_EVENT_EXTRA_SRC_LIVE_TV);
                } else {
                    intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SRC, EventHelper.MTK_EVENT_EXTRA_SRC_TV_SETTING_PLUS);
                }
                intent.putExtra(EventHelper.MTK_EVENT_EXTRA_SUB_TYPE, EventHelper.MTK_EVENT_EXTRA_SUB_TYPE_CHANNEL_SRC);
                MainFragment.this.getContext().startActivity(intent);
                MainFragment.this.getActivity().finish();
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> onCreatePreferenceControllers(Context context) {
        this.mPreferenceControllers = new ArrayList(2);
        this.mHotwordSwitchController = new HotwordSwitchController(context);
        this.mTakeBugReportController = new TakeBugReportController(context);
        this.mPreferenceControllers.add(this.mHotwordSwitchController);
        this.mPreferenceControllers.add(this.mTakeBugReportController);
        return this.mPreferenceControllers;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateConnectivity() {
        Preference networkPref = findPreference(KEY_NETWORK);
        if (networkPref != null) {
            if (this.mConnectivityListener.isCellConnected()) {
                switch (this.mConnectivityListener.getCellSignalStrength()) {
                    case 1:
                        networkPref.setIcon((int) R.drawable.ic_cell_signal_1_white);
                        return;
                    case 2:
                        networkPref.setIcon((int) R.drawable.ic_cell_signal_2_white);
                        return;
                    case 3:
                        networkPref.setIcon((int) R.drawable.ic_cell_signal_3_white);
                        return;
                    case 4:
                        networkPref.setIcon((int) R.drawable.ic_cell_signal_4_white);
                        return;
                    default:
                        networkPref.setIcon((int) R.drawable.ic_cell_signal_0_white);
                        return;
                }
            } else if (this.mConnectivityListener.isEthernetConnected()) {
                networkPref.setIcon((int) R.drawable.ic_ethernet_white);
                networkPref.setSummary((int) R.string.connectivity_summary_ethernet_connected);
            } else if (!this.mConnectivityListener.isWifiEnabledOrEnabling()) {
                networkPref.setIcon((int) R.drawable.ic_wifi_signal_off_white);
                networkPref.setSummary((int) R.string.connectivity_summary_wifi_disabled);
            } else if (this.mConnectivityListener.isWifiConnected()) {
                switch (this.mConnectivityListener.getWifiSignalStrength(5)) {
                    case 1:
                        networkPref.setIcon((int) R.drawable.ic_wifi_signal_1_white);
                        break;
                    case 2:
                        networkPref.setIcon((int) R.drawable.ic_wifi_signal_2_white);
                        break;
                    case 3:
                        networkPref.setIcon((int) R.drawable.ic_wifi_signal_3_white);
                        break;
                    case 4:
                        networkPref.setIcon((int) R.drawable.ic_wifi_signal_4_white);
                        break;
                    default:
                        networkPref.setIcon((int) R.drawable.ic_wifi_signal_0_white);
                        break;
                }
                networkPref.setSummary((CharSequence) this.mConnectivityListener.getSsid());
            } else {
                networkPref.setIcon((int) R.drawable.ic_wifi_not_connected);
                networkPref.setSummary((int) R.string.connectivity_summary_no_network_connected);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateSoundSettings() {
        Preference soundPref = findPreference(KEY_SOUND);
        if (soundPref != null) {
            ResolveInfo info = systemIntentIsHandled(getContext(), new Intent(ACTION_SOUND));
            soundPref.setVisible(info != null);
            if (info != null && info.activityInfo != null) {
                String pkgName = info.activityInfo.packageName;
                Drawable icon = getDrawableResource(pkgName, "sound_icon");
                if (icon != null) {
                    soundPref.setIcon(icon);
                }
                String title = getStringResource(pkgName, "sound_pref_title");
                if (!TextUtils.isEmpty(title)) {
                    soundPref.setTitle((CharSequence) title);
                }
                String summary = getStringResource(pkgName, "sound_pref_summary");
                if (!TextUtils.isEmpty(summary)) {
                    soundPref.setSummary((CharSequence) summary);
                }
            }
        }
    }

    private String getStringResource(String pkgName, String resourceName) {
        try {
            Context targetContext = getContext().createPackageContext(pkgName, 0);
            Resources resources = targetContext.getResources();
            int resId = resources.getIdentifier(pkgName + ":string/" + resourceName, (String) null, (String) null);
            if (resId != 0) {
                return targetContext.getResources().getString(resId);
            }
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException | SecurityException e) {
            Log.w(TAG, "Unable to get string resource " + resourceName, e);
        }
        return null;
    }

    private Drawable getDrawableResource(String pkgName, String resourceName) {
        try {
            Context targetContext = getContext().createPackageContext(pkgName, 0);
            Resources resources = targetContext.getResources();
            int resId = resources.getIdentifier(pkgName + ":drawable/" + resourceName, (String) null, (String) null);
            if (resId != 0) {
                return targetContext.getResources().getDrawable(resId);
            }
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException | SecurityException e) {
            Log.w(TAG, "Unable to get drawable resource " + resourceName, e);
        }
        return null;
    }

    public static ResolveInfo systemIntentIsHandled(Context context, Intent intent) {
        if (intent == null) {
            return null;
        }
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities(intent, 0)) {
            if (info.activityInfo != null && (info.activityInfo.applicationInfo.flags & 1) == 1) {
                return info;
            }
        }
        return null;
    }

    public void onSuggestionReady(List<Suggestion> data) {
        if (data != null && data.size() != 0) {
            if (this.mSuggestionsList == null) {
                this.mSuggestionsList = new PreferenceCategory(getPreferenceManager().getContext());
                this.mSuggestionsList.setKey(KEY_SUGGESTIONS_LIST);
                this.mSuggestionsList.setTitle((int) R.string.header_category_suggestions);
                this.mSuggestionsList.setOrder(0);
                getPreferenceScreen().addPreference(this.mSuggestionsList);
            }
            updateSuggestionList(data);
        } else if (this.mSuggestionsList != null) {
            getPreferenceScreen().removePreference(this.mSuggestionsList);
            this.mSuggestionsList = null;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateSuggestionList(List<Suggestion> suggestions) {
        for (int i = 0; i < this.mSuggestionsList.getPreferenceCount(); i++) {
            SuggestionPreference pref = (SuggestionPreference) this.mSuggestionsList.getPreference(i);
            boolean isInNewSuggestionList = false;
            Iterator<Suggestion> it = suggestions.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (pref.getId().equals(it.next().getId())) {
                        isInNewSuggestionList = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!isInNewSuggestionList) {
                this.mSuggestionsList.removePreference(pref);
            }
        }
        for (Suggestion suggestion : suggestions) {
            Preference curPref = findPreference(SuggestionPreference.SUGGESTION_PREFERENCE_KEY + suggestion.getId());
            if (curPref == null) {
                SuggestionPreference newSuggPref = new SuggestionPreference(suggestion, getPreferenceManager().getContext(), this.mSuggestionControllerMixin, this);
                newSuggPref.setIcon(this.mIconCache.getIcon(suggestion.getIcon()));
                newSuggPref.setTitle(suggestion.getTitle());
                newSuggPref.setSummary(suggestion.getSummary());
                this.mSuggestionsList.addPreference(newSuggPref);
            } else {
                curPref.setIcon(this.mIconCache.getIcon(suggestion.getIcon()));
                curPref.setTitle(suggestion.getTitle());
                curPref.setSummary(suggestion.getSummary());
            }
        }
    }

    public void onResume() {
        super.onResume();
        showOrHideQuickSettings();
        updateAccountPref();
        updateAccessoryPref();
        updateConnectivity();
    }

    private boolean isRestricted() {
        return SecurityFragment.isRestrictedProfileInEffect(getContext());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateAccessoryPref() {
        Preference accessoryPreference = findPreference(KEY_ACCESSORIES);
        Preference connectedDevicesPreference = findPreference(KEY_CONNECTED_DEVICES);
        if (connectedDevicesPreference != null) {
            ResolveInfo info = systemIntentIsHandled(getContext(), new Intent(ACTION_CONNECTED_DEVICES));
            connectedDevicesPreference.setVisible(info != null);
            accessoryPreference.setVisible(info == null);
            if (info != null) {
                String pkgName = info.activityInfo.packageName;
                Drawable icon = getDrawableResource(pkgName, "connected_devices_pref_icon");
                if (icon != null) {
                    connectedDevicesPreference.setIcon(icon);
                }
                String title = getStringResource(pkgName, "connected_devices_pref_title");
                if (!TextUtils.isEmpty(title)) {
                    connectedDevicesPreference.setTitle((CharSequence) title);
                }
                String summary = getStringResource(pkgName, "connected_devices_pref_summary");
                if (!TextUtils.isEmpty(summary)) {
                    connectedDevicesPreference.setSummary((CharSequence) summary);
                    return;
                }
                return;
            }
        }
        if (this.mBtAdapter != null && accessoryPreference != null) {
            if (this.mBtAdapter.getBondedDevices().size() == 0) {
                this.mHasBtAccessories = false;
            } else {
                this.mHasBtAccessories = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateAccountPref() {
        Preference accountsPref = findPreference(KEY_ACCOUNTS_AND_SIGN_IN);
        SlicePreference acccountsSlicePref = (SlicePreference) findPreference(KEY_ACCOUNTS_AND_SIGN_IN_SLICE);
        Intent intent = new Intent(ACTION_ACCOUNTS);
        if (systemIntentIsHandled(getContext(), intent) != null) {
            accountsPref.setVisible(true);
            accountsPref.setFragment((String) null);
            accountsPref.setIntent(intent);
            acccountsSlicePref.setVisible(false);
        } else if (isSliceProviderValid(acccountsSlicePref.getUri())) {
            accountsPref.setVisible(false);
            acccountsSlicePref.setVisible(true);
        } else {
            accountsPref.setVisible(true);
            acccountsSlicePref.setVisible(false);
            updateAccountPrefInfo();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateAccountPrefInfo() {
        Preference accountsPref = findPreference(KEY_ACCOUNTS_AND_SIGN_IN);
        if (accountsPref != null && accountsPref.isVisible()) {
            Account[] accounts = AccountManager.get(getContext()).getAccounts();
            if (accounts.length == 0) {
                this.mHasAccounts = false;
                accountsPref.setIcon((int) R.drawable.ic_add_an_account);
                accountsPref.setSummary((int) R.string.accounts_category_summary_no_account);
                AccountsFragment.setUpAddAccountPrefIntent(accountsPref, getContext());
                return;
            }
            this.mHasAccounts = true;
            accountsPref.setIcon((int) R.drawable.ic_accounts_and_sign_in);
            if (accounts.length == 1) {
                accountsPref.setSummary((CharSequence) accounts[0].name);
                return;
            }
            accountsPref.setSummary((CharSequence) getResources().getQuantityString(R.plurals.accounts_category_summary, accounts.length, new Object[]{Integer.valueOf(accounts.length)}));
        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter btChangeFilter = new IntentFilter();
        btChangeFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        btChangeFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        btChangeFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        getContext().registerReceiver(this.mBCMReceiver, btChangeFilter);
    }

    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(this.mBCMReceiver);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ComponentName componentName = new ComponentName("com.android.settings.intelligence", "com.android.settings.intelligence.suggestions.SuggestionService");
        if (!isRestricted()) {
            this.mSuggestionControllerMixin = new SuggestionControllerMixin(context, this, getLifecycle(), componentName);
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if ((!preference.getKey().equals(KEY_ACCOUNTS_AND_SIGN_IN) || this.mHasAccounts) && (!preference.getKey().equals(KEY_ACCESSORIES) || this.mHasBtAccessories)) {
            return super.onPreferenceTreeClick(preference);
        }
        getContext().startActivity(preference.getIntent());
        return true;
    }

    public void onSuggestionClosed(Preference preference) {
        if (this.mSuggestionsList != null && this.mSuggestionsList.getPreferenceCount() != 0) {
            if (this.mSuggestionsList.getPreferenceCount() == 1) {
                getPreferenceScreen().removePreference(this.mSuggestionsList);
            } else {
                this.mSuggestionsList.removePreference(preference);
            }
        }
    }

    private boolean supportBlueTooth() {
        if (getActivity().getPackageManager().hasSystemFeature("android.hardware.bluetooth")) {
            return true;
        }
        return false;
    }

    private boolean isSliceProviderValid(String uri) {
        ContentProviderClient client;
        if (uri == null || (client = getContext().getContentResolver().acquireContentProviderClient(Uri.parse(uri))) == null) {
            return false;
        }
        client.close();
        return true;
    }

    public LoaderManager getLoaderManager() {
        try {
            return super.getLoaderManager();
        } catch (Exception e) {
            return null;
        }
    }
}
