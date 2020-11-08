package com.android.tv.settings.partnercustomizer.picture;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.LoadingUI;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.android.tv.settings.partnercustomizer.utils.ProgressPreference;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class WhiteBalanceCorrection11Fragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private final int MSG_UPDATE_RGB = 0;
    private String TAG = "WhiteBalanceCorrection11Fragment";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                WhiteBalanceCorrection11Fragment.this.updatePrefValue();
                WhiteBalanceCorrection11Fragment.this.hideLoading();
            }
        }
    };
    private LoadingUI mLoadingUI;
    private PreferenceConfigUtils mPreferenceConfigUtils;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        createPreferences();
        updatePrefEnable(((SwitchPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_ENABLE)).isChecked());
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof SwitchPreference) {
            SwitchPreference pref = (SwitchPreference) preference;
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(pref.isChecked()));
            if (TextUtils.equals(preference.getKey(), PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_ENABLE)) {
                updatePrefEnable(pref.isChecked());
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        if (!(preference instanceof ListPreference)) {
            return true;
        }
        this.mHandler.sendEmptyMessageDelayed(0, 1000);
        showLoading();
        return true;
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture_white_balance11);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("picture_white_balance11");
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(this.TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_ENABLE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_color_tune_enable, (String) null));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GAIN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_color_tune_gain, (int) R.array.picture_white_balance11_entries, (int) R.array.picture_white_balance11_entry_values, (String) null));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_red, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_green, (String) null, 100, 0, 1));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_color_tune_hue_blue, (String) null, 100, 0, 1));
            }
        }
    }

    private void updatePrefEnable(boolean enable) {
        findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GAIN).setEnabled(enable);
        findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED).setEnabled(enable);
        findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN).setEnabled(enable);
        findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE).setEnabled(enable);
    }

    /* access modifiers changed from: private */
    public void updatePrefValue() {
        ProgressPreference pref = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED);
        if (pref != null) {
            pref.setValue(PreferenceUtils.getSettingIntValue(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_RED, pref.getCurrentValue()));
        }
        ProgressPreference pref2 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN);
        if (pref2 != null) {
            pref2.setValue(PreferenceUtils.getSettingIntValue(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_GREEN, pref2.getCurrentValue()));
        }
        ProgressPreference pref3 = (ProgressPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE);
        if (pref3 != null) {
            pref3.setValue(PreferenceUtils.getSettingIntValue(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_WHITE_BALANCE11_BLUE, pref3.getCurrentValue()));
        }
    }

    private void showLoading() {
        if (this.mLoadingUI == null) {
            this.mLoadingUI = new LoadingUI(getContext());
            this.mLoadingUI.show();
        } else if (!this.mLoadingUI.isShowing()) {
            this.mLoadingUI.show();
        }
    }

    /* access modifiers changed from: private */
    public void hideLoading() {
        try {
            if (this.mLoadingUI != null && this.mLoadingUI.isShowing()) {
                this.mLoadingUI.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
