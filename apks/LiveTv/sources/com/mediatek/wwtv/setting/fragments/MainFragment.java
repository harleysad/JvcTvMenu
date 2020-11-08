package com.mediatek.wwtv.setting.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.mediatek.wwtv.setting.LiveTvSetting;
import com.mediatek.wwtv.setting.preferences.DialogPreference;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.view.PinDialog;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;

public class MainFragment extends LeanbackPreferenceFragment {
    private static final String TAG = "MainFragment";
    private PinDialog.ResultListener listener;
    private boolean mActive;
    private Handler mCAMHandler;
    private boolean mFactoryMode;
    /* access modifiers changed from: private */
    public boolean mParentalControl;
    private PreferenceScreen mScreen;
    private Handler mSignalHandler;
    private TVContent mTv;

    public static MainFragment newInstance(boolean factoryMode) {
        return new MainFragment(factoryMode);
    }

    public MainFragment() {
        this.mActive = false;
        this.mParentalControl = false;
        this.mFactoryMode = false;
        this.listener = new PinDialog.ResultListener() {
            public void done(boolean success) {
                boolean unused = MainFragment.this.mParentalControl = success;
                MainFragment.this.updateParentalControlList();
            }
        };
        this.mFactoryMode = false;
    }

    public MainFragment(boolean factoryMode) {
        this.mActive = false;
        this.mParentalControl = false;
        this.mFactoryMode = false;
        this.listener = new PinDialog.ResultListener() {
            public void done(boolean success) {
                boolean unused = MainFragment.this.mParentalControl = success;
                MainFragment.this.updateParentalControlList();
            }
        };
        this.mFactoryMode = factoryMode;
    }

    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(TAG, "onCreate." + this.mFactoryMode);
        this.mSignalHandler = SettingsPreferenceScreen.getInstance(getContext(), getPreferenceManager()).mSignalHandler;
        this.mCAMHandler = SettingsPreferenceScreen.getInstance(getContext(), getPreferenceManager()).mCAMHandler;
        this.mTv = TVContent.getInstance(getContext());
        this.mTv.addSingleLevelCallBackListener(this.mSignalHandler);
        this.mTv.addCallBackListener(TvCallbackConst.MSG_CB_CI_MSG, this.mCAMHandler);
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if (this.mFactoryMode) {
            this.mScreen = SettingsPreferenceScreen.getInstance(getContext(), getPreferenceManager()).getFactoryScreen();
        } else {
            SettingsPreferenceScreen screen = SettingsPreferenceScreen.getInstance(getContext(), getPreferenceManager());
            if (LiveTvSetting.isForChannelBootFromTVSettingPlus()) {
                this.mScreen = screen.getChannelMainScreen();
            } else if (LiveTvSetting.isFor3rdCaptionsTVMenuOptionOrSaRegion()) {
                this.mScreen = screen.getCaptionSetupScreen();
            } else {
                this.mScreen = screen.getMainScreen();
            }
        }
        setPreferenceScreen(this.mScreen);
        for (int i = 0; i < this.mScreen.getPreferenceCount(); i++) {
            Preference tempPre = this.mScreen.getPreference(i);
            if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_ENTER_PASSWORD)) {
                ((PinDialog) ((DialogPreference) tempPre).getDialog()).setResultListener(this.listener);
            }
        }
    }

    public void onStart() {
        MtkLog.d(TAG, "onStart.");
        super.onStart();
    }

    public void onResume() {
        PreferenceData data = PreferenceData.getInstance(getContext());
        data.setData(this.mScreen);
        data.resume();
        updateParentalControlList();
        this.mActive = true;
        MtkLog.d(TAG, "onResume.");
        super.onResume();
        if (LiveTvSetting.isBootFromLiveTV()) {
            TurnkeyUiMainActivity.getInstance().resetLayout();
        }
    }

    public void onPause() {
        MtkLog.d(TAG, "onPause.");
        super.onPause();
        this.mActive = false;
        this.mParentalControl = false;
    }

    public void onStop() {
        MtkLog.d(TAG, "onStop.");
        this.mTv.removeCallBackListener(TvCallbackConst.MSG_CB_CI_MSG, this.mCAMHandler);
        super.onStop();
    }

    public void onDestroy() {
        MtkLog.d(TAG, " onDestroy()");
        this.mTv.removeSingleLevelCallBackListener(this.mSignalHandler);
        super.onDestroy();
    }

    public boolean getActive() {
        return this.mActive;
    }

    /* access modifiers changed from: private */
    public void updateParentalControlList() {
        if (this.mScreen != null) {
            for (int i = 0; i < this.mScreen.getPreferenceCount(); i++) {
                Preference tempPre = this.mScreen.getPreference(i);
                if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_ENTER_PASSWORD)) {
                    tempPre.setVisible(!this.mParentalControl);
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_CHANNEL_BLOCK)) {
                    if (!this.mTv.isCurrentSourceTv() || !CommonIntegration.getInstance().hasActiveChannel()) {
                        tempPre.setVisible(false);
                    } else {
                        tempPre.setVisible(this.mParentalControl);
                    }
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_PROGRAM_BLOCK)) {
                    tempPre.setVisible(this.mParentalControl);
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_CHANNEL_SCHEDULE_BLOCK)) {
                    tempPre.setVisible(this.mParentalControl);
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_INPUT_BLOCK)) {
                    tempPre.setVisible(this.mParentalControl);
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_CHANGE_PASSWORD)) {
                    tempPre.setVisible(this.mParentalControl);
                } else if (TextUtils.equals(tempPre.getKey(), MenuConfigManager.PARENTAL_CLEAN_ALL)) {
                    tempPre.setVisible(this.mParentalControl);
                }
            }
        }
    }
}
