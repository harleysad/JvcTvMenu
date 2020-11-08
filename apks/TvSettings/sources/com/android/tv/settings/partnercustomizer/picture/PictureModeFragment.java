package com.android.tv.settings.partnercustomizer.picture;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.DataSeparaterUtil;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class PictureModeFragment extends SettingsPreferenceFragment {
    private static final String PICTURE_MODE_RADIO_GROUP = "picture_mode";
    private static final String TAG = "PictureModeFragment";
    private static PictureModeFragment pmf;
    private ContentResolver contentResolver;
    private PreferenceConfigUtils mConfigUtils;
    private String[] mEntries;
    private String[] mEntriesValues;
    private int[] mPMArrayIds;

    public static PictureModeFragment newInstance() {
        if (pmf == null) {
            pmf = new PictureModeFragment();
        }
        return pmf;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate ");
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MtkLog.d(TAG, "onCreatePreferences ");
        Context themedContext = getPreferenceManager().getContext();
        this.contentResolver = getContext().getContentResolver();
        this.mConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(themedContext);
        screen.setTitle((int) R.string.device_picture_mode);
        this.mPMArrayIds = this.mConfigUtils.getArrayIdsByCustomer("picture_mode");
        this.mEntries = getContext().getResources().getStringArray(this.mPMArrayIds[0]);
        this.mEntriesValues = getContext().getResources().getStringArray(this.mPMArrayIds[1]);
        PreferenceConfigUtils preferenceConfigUtils = this.mConfigUtils;
        int val = PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), "picture_mode");
        MtkLog.d(TAG, "onCreatePreferences ,val==" + val);
        boolean isShowEnergyStar = false;
        if (DataSeparaterUtil.getInstance() != null) {
            isShowEnergyStar = DataSeparaterUtil.getInstance().isShowEnergyStarIcon();
        }
        MtkLog.d(TAG, "onCreatePreferences ,isShowEnergyStar==" + isShowEnergyStar);
        Preference activePref = null;
        for (int i = 0; i < this.mEntries.length; i++) {
            RadioPreference radioPreference = new RadioPreference(themedContext);
            radioPreference.setKey(this.mEntries[i]);
            radioPreference.setPersistent(false);
            radioPreference.setTitle((CharSequence) this.mEntries[i]);
            radioPreference.setRadioGroup("picture_mode");
            radioPreference.setLayoutResource(R.layout.preference_reversed_widget);
            if (Integer.parseInt(this.mEntriesValues[i]) == val) {
                radioPreference.setChecked(true);
                activePref = radioPreference;
            }
            MtkLog.d(TAG, "onCreatePreferences,mEntries[i]== " + this.mEntries[i]);
            if ("Energy Saving".equals(this.mEntries[i]) && MarketRegionInfo.getCurrentMarketRegion() == 1 && isShowEnergyStar) {
                radioPreference.setLayoutResource(R.layout.partner_picture_mode_item_preference);
            }
            screen.addPreference(radioPreference);
        }
        if (activePref != null && savedInstanceState == null) {
            scrollToPreference(activePref);
        }
        setPreferenceScreen(screen);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String prefKey = preference.getKey();
        MtkLog.d(TAG, "onPreferenceTreeClick,prefKey==" + prefKey);
        if (preference instanceof RadioPreference) {
            RadioPreference radioPreference = (RadioPreference) preference;
            radioPreference.clearOtherRadioPreferences(getPreferenceScreen());
            if (!radioPreference.isChecked()) {
                radioPreference.setChecked(true);
            }
            int i = 0;
            while (true) {
                if (i >= this.mEntries.length) {
                    break;
                } else if (this.mEntries[i].equals(prefKey)) {
                    PreferenceConfigUtils preferenceConfigUtils = this.mConfigUtils;
                    PreferenceConfigUtils.putSettingValueInt(this.contentResolver, "picture_mode", Integer.parseInt(this.mEntriesValues[i]));
                    MtkLog.d(TAG, "onPreferenceTreeClick,mEntries[i]==" + this.mEntries[i]);
                    MtkLog.d(TAG, "onPreferenceTreeClick,mEntriesValues[i]==" + this.mEntriesValues[i]);
                    break;
                } else {
                    i++;
                }
            }
        }
        MtkLog.d(TAG, "onPreferenceTreeClick,send KEYCODE_BACK");
        InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
        return super.onPreferenceTreeClick(preference);
    }

    public int getMetricsCategory() {
        return 336;
    }
}
