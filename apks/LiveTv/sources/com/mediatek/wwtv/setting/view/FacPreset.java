package com.mediatek.wwtv.setting.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.preferences.PreferenceData;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class FacPreset implements Preference.OnPreferenceClickListener {
    protected static final String TAG = "FacPreset";
    private static FacPreset mInstance = null;
    private Context mContext;
    private PreferenceData mPrefData = PreferenceData.getInstance(this.mContext);

    private FacPreset(Context context) {
        this.mContext = context;
    }

    public static FacPreset getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FacPreset(context);
        }
        return mInstance;
    }

    public boolean onPreferenceClick(Preference preference) {
        MtkLog.d(TAG, "onPreferenceClick, " + preference);
        String key = preference.getKey();
        MenuConfigManager menuConfigManager = this.mPrefData.mConfigManager;
        if (key.equals(MenuConfigManager.FACTORY_PRESET_CH_DUMP)) {
            this.mPrefData.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_DUMP_CH_INFO_2USB, 0);
            return true;
        }
        String key2 = preference.getKey();
        MenuConfigManager menuConfigManager2 = this.mPrefData.mConfigManager;
        if (key2.equals(MenuConfigManager.FACTORY_PRESET_CH_PRINT)) {
            this.mPrefData.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_DUMP_CH_INFO_2TERM, 0);
            return true;
        }
        String key3 = preference.getKey();
        MenuConfigManager menuConfigManager3 = this.mPrefData.mConfigManager;
        if (!key3.equals(MenuConfigManager.FACTORY_PRESET_CH_RESTORE)) {
            return true;
        }
        this.mPrefData.mTV.setConfigValue(MtkTvConfigTypeBase.CFG_MISC_PRE_CH_LOAD_PRESET_CH, 0);
        this.mContext.sendBroadcast(new Intent("com.mediatek.timeshift.mode.off"));
        return true;
    }
}
