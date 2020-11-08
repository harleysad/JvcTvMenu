package com.android.tv.settings.partnercustomizer.picture;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.tv.ini.IniDocument;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvanceVideoFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static int MJC_EXT_TYPE = -1;
    public static final String TAG = "AdvanceVideoFragment";
    private ListPreference AdvanceVideoDiFilmModePref;
    private SwitchPreference AdvanceVideoGameModePref;
    private ListPreference AdvanceVideoHdmiRGBRangePref;
    private Preference AdvanceVideoMJCPref;
    private SwitchPreference AdvanceVideoSuperResolution;
    private SwitchPreference PCModePref;
    private Context mContext;
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private TVSettingConfig mTVSettingConfig;
    private String marketregion;

    public static AdvanceVideoFragment newInstance() {
        return new AdvanceVideoFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_advanced_video, (String) null);
        this.marketregion = SystemProperties.get("ro.vendor.mtk.system.marketregion");
        this.mContext = getContext();
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        this.mTVSettingConfig = TVSettingConfig.getInstance(this.mContext);
        createPreferences();
        findAllPreferences();
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList(PartnerSettingsConfig.ATTR_ADVANCED_VIDEO);
        if (prefKeys == null) {
            MtkLog.e(TAG, "List prefKeys is null");
            return;
        }
        for (int i = 0; i < prefKeys.size(); i++) {
            String prefKey = prefKeys.get(i);
            if (prefKey == null) {
                MtkLog.e(TAG, "prefKey is null");
                return;
            }
            if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DNR)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_DNR, (int) R.array.picture_advanced_video_DNR_entries, (int) R.array.picture_advanced_video_DNR_entries_values, "g_video__vid_nr"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MPEG_NR)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_MPEG_NR, (int) R.array.picture_advanced_video_MPEG_NR_entries, (int) R.array.picture_advanced_video_MPEG_NR_entries_values, "g_video__vid_mpeg_nr"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_adaptive_luma_control, (int) R.array.picture_advanced_video_MPEG_NR_entries, (int) R.array.picture_advanced_video_adaptive_luma_control_entries_values, "g_video__vid_luma"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_LOCAL_CONTRAST_CONTROL)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_local_contrast_control, (int) R.array.picture_advanced_video_local_contrast_entries, (int) R.array.picture_advanced_video_local_contrast_entries_values, "g_video__vid_local_contrast"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_FLESH_TONE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_flesh_tone, (int) R.array.picture_advanced_video_local_contrast_entries, (int) R.array.picture_advanced_video_flesh_tone_entries_values, "g_video__vid_flash_tone"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_di_film_mode, (int) R.array.picture_advanced_vedio_di_film_mode_entries, (int) R.array.picture_advanced_vedio_di_film_mode_entries_values, "g_video__vid_di_film_mode"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_BLUE_STRETCH)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_advanced_video_blue_stretch, "g_video__vid_blue_stretch"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_advanced_video_game_mode, "g_video__vid_game_mode"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createSwitchPreference(this, prefKey, R.string.device_picture_advanced_video_pc_mode, "g_fusion_picture__pc_mode"));
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC)) {
                if (isSupportMJC()) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_MJC, "com.android.tv.settings.partnercustomizer.picture.MJCFragment"));
                }
            } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE)) {
                mScreen.addPreference(this.mPreferenceConfigUtils.createListPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_advanced_video_hdmi_rgb_range, (int) R.array.picture_advanced_video_hdmi_rgb_range_entries, (int) R.array.picture_advanced_video_hdmi_rgb_range_entries_values, "g_fusion_picture__hdmi_rgb_range"));
            }
        }
    }

    private void findAllPreferences() {
        this.AdvanceVideoDiFilmModePref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_DI_FILM_MODE);
        this.AdvanceVideoGameModePref = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_GAME_MODE);
        this.AdvanceVideoHdmiRGBRangePref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_HDMI_RGB_RANGE);
        this.AdvanceVideoMJCPref = findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC);
        this.PCModePref = (SwitchPreference) findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_PC_MODE);
    }

    private void updatePrefState() {
        if (this.marketregion.equalsIgnoreCase("cn")) {
            String[] entries = getContext().getResources().getStringArray(R.array.picture_advanced_vedio_di_film_mode_entries_cn);
            String[] entriesValues = getContext().getResources().getStringArray(R.array.picture_advanced_vedio_di_film_mode_entries_values_cn);
            this.AdvanceVideoDiFilmModePref.setEntries((CharSequence[]) entries);
            this.AdvanceVideoDiFilmModePref.setEntryValues((CharSequence[]) entriesValues);
        }
        if (!this.mTVSettingConfig.isInLiveTV() || !this.mPreferenceConfigUtils.isHDMISource(this.mContext)) {
            if (this.AdvanceVideoHdmiRGBRangePref != null) {
                this.AdvanceVideoHdmiRGBRangePref.setEnabled(false);
            }
            if (this.PCModePref != null) {
                this.PCModePref.setEnabled(false);
            }
        } else {
            if (this.AdvanceVideoHdmiRGBRangePref != null) {
                this.AdvanceVideoHdmiRGBRangePref.setEnabled(true);
            }
            if (this.PCModePref != null) {
                this.PCModePref.setEnabled(true);
            }
        }
        if ((this.AdvanceVideoGameModePref == null || !this.AdvanceVideoGameModePref.isChecked()) && (this.PCModePref == null || !this.PCModePref.isChecked())) {
            if (this.AdvanceVideoDiFilmModePref != null) {
                this.AdvanceVideoDiFilmModePref.setEnabled(true);
            }
            if (this.AdvanceVideoMJCPref != null) {
                this.AdvanceVideoMJCPref.setEnabled(true);
            }
        } else {
            if (this.AdvanceVideoDiFilmModePref != null) {
                this.AdvanceVideoDiFilmModePref.setEnabled(false);
            }
            if (this.AdvanceVideoMJCPref != null) {
                this.AdvanceVideoMJCPref.setEnabled(false);
            }
        }
        int val = PreferenceConfigUtils.getSettingValueInt(getContext().getContentResolver(), PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_MJC_EFFECT, 0);
        if (this.AdvanceVideoMJCPref == null || val == 0) {
            if (this.AdvanceVideoGameModePref != null) {
                this.AdvanceVideoGameModePref.setEnabled(true);
            }
            if (this.PCModePref != null) {
                this.PCModePref.setEnabled(true);
            }
        } else {
            if (this.AdvanceVideoGameModePref != null) {
                this.AdvanceVideoGameModePref.setEnabled(false);
            }
            if (this.PCModePref != null) {
                this.PCModePref.setEnabled(false);
            }
        }
        if (findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL) != null) {
            findPreference(PreferenceConfigUtils.KEY_PICTURE_ADVANCE_VIDEO_ADAPTIVE_LUMA_CONTROL).setEnabled(true ^ this.mPreferenceConfigUtils.isHDMIVGASignal(getContext()));
        }
    }

    private boolean isSupportMJC() {
        MJC_EXT_TYPE = SaveValue.getInstance(getContext()).readValue("MJC_EXT_TYPE", -1);
        if (MJC_EXT_TYPE > 0) {
            return true;
        }
        if (MJC_EXT_TYPE == 0) {
            return false;
        }
        try {
            String documentInfo = new IniDocument(getFile()).get("m_u32SupportExtFrcType").toString().trim();
            MtkLog.d(TAG, " step 3" + documentInfo);
            int index = Integer.parseInt(keyTool(documentInfo));
            MtkLog.d(TAG, "m_u32SupportExtFrcType == " + index);
            SaveValue.getInstance(getContext()).saveValue("MJC_EXT_TYPE", index);
            if (index != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            MtkLog.d(TAG, " isSupportMJC ==error " + e);
            return false;
        }
    }

    private String getFile() {
        MtkLog.d(TAG, "getFile ,path == " + "/vendor/tvconfig/config/board.ini");
        if (new File("/vendor/tvconfig/config/board.ini").exists()) {
            return "/vendor/tvconfig/config/board.ini";
        }
        MtkLog.d(TAG, "getFile ,path == " + null);
        return null;
    }

    private String keyTool(String key) {
        if (key != null && !key.isEmpty()) {
            if (key.startsWith("[")) {
                key = key.replace("[", "");
            }
            if (key.endsWith("]")) {
                key = key.replace("]", "");
            }
        }
        MtkLog.d(TAG, " = " + key);
        return key;
    }

    public void onResume() {
        super.onResume();
        updatePrefState();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver contentResolver = getContext().getContentResolver();
        String preferenceKey = preference.getKey();
        Log.e(TAG, "onPreferenceChange preference " + preferenceKey);
        Log.d(TAG, "onPreferenceChange newValue == " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        updatePrefState();
        return true;
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String prefKey = preference.getKey();
        MtkLog.d(TAG, "onPreferenceTreeClick,prefKey==" + prefKey);
        if (preference instanceof SwitchPreference) {
            this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
        }
        updatePrefState();
        return super.onPreferenceTreeClick(preference);
    }
}
