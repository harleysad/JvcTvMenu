package com.mediatek.wwtv.setting.fragments;

import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.PreferenceScreen;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class BaseContentFragment extends LeanbackPreferenceFragment {
    private static String TAG = null;
    private static final String TAG_BASE = "BaseContentFragment";
    private PreferenceScreen mScreen;
    private SettingsPreferenceScreen mSettingSPreferenceScreen;

    public static BaseContentFragment newInstance() {
        return new BaseContentFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        MtkLog.d(TAG_BASE, "onCreate.");
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        String parent = getArguments().getString(PreferenceUtil.PARENT_PREFERENCE_ID);
        String str = TAG;
        MtkLog.d(str, "onCreatePreferences, " + parent);
        TAG = TAG_BASE + parent;
        this.mSettingSPreferenceScreen = SettingsPreferenceScreen.getInstance(getContext(), getPreferenceManager());
        this.mScreen = this.mSettingSPreferenceScreen.getSubScreen(parent);
        setPreferenceScreen(this.mScreen);
    }

    public void onStart() {
        MtkLog.d(TAG, "onStart.");
        int cur = MtkTvConfig.getInstance().getConfigValue("g_video__picture_mode");
        if (cur == 5 || cur == 6) {
            MenuConfigManager.PICTURE_MODE_dOVI = true;
        } else {
            MenuConfigManager.PICTURE_MODE_dOVI = false;
        }
        super.onStart();
    }

    public void onResume() {
        MtkLog.d(TAG, "onResume.");
        PreferenceData data = PreferenceData.getInstance(getContext());
        data.setData(this.mScreen);
        data.resume();
        super.onResume();
    }

    public void onPause() {
        MtkLog.d(TAG, "onPause.");
        PreferenceData.getInstance(getContext()).pause();
        super.onPause();
    }

    public void onStop() {
        MtkLog.d(TAG, "onStop.");
        MenuConfigManager.PICTURE_MODE_dOVI = false;
        super.onStop();
    }
}
