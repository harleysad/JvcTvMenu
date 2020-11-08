package com.android.tv.ui.sidepanel;

import android.content.Context;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.PreferenceScreen;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.R;

public class RecordTShiftFragment extends LeanbackPreferenceFragment {
    private static final String TAG = "RecordTShiftFragment";
    private MenuConfigManager mConfigManager;

    public static RecordTShiftFragment newInstance() {
        return new RecordTShiftFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mConfigManager = MenuConfigManager.getInstance(getActivity());
        super.onCreate(savedInstanceState);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferenceScreen(getRecordTShiftScreen());
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    private PreferenceScreen getRecordTShiftScreen() {
        Context themedContext = getPreferenceManager().getContext();
        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(themedContext);
        preferenceScreen.setTitle((int) R.string.menu_setup_record_setting);
        PreferenceUtil util = PreferenceUtil.getInstance(themedContext);
        boolean z = true;
        if (this.mConfigManager.getDefault("g_record__rec_tshift_mode") != 1) {
            z = false;
        }
        preferenceScreen.addPreference(util.createSwitchPreference("g_record__rec_tshift_mode", (int) R.string.menu_setup_time_shifting_mode, z));
        return preferenceScreen;
    }
}
