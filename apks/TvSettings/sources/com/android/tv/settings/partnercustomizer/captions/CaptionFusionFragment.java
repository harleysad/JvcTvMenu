package com.android.tv.settings.partnercustomizer.captions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.HashMap;
import java.util.Map;

public class CaptionFusionFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String KEY_CAPTIONS_ADVANCED = "captions_advanced_selection";
    private static final String KEY_CAPTIONS_BACKGROUND_COLOR = "captions_background_color";
    private static final String KEY_CAPTIONS_BACKGROUND_OPACITY = "captions_background_opacity";
    private static final String KEY_CAPTIONS_DISPLAY = "captions_display";
    private static final String KEY_CAPTIONS_EDGE_COLOR = "captions_edge_color";
    private static final String KEY_CAPTIONS_EDGE_TYPE = "captions_edge_type";
    private static final String KEY_CAPTIONS_FONT_FAMILY = "captions_font_family";
    private static final String KEY_CAPTIONS_SERVICES = "captions_services";
    private static final String KEY_CAPTIONS_TEXTSIZE = "captions_text_size";
    private static final String KEY_CAPTIONS_TEXT_COLOR = "captions_text_color";
    private static final String KEY_CAPTIONS_TEXT_OPACITY = "captions_font_opacity";
    private static final String TAG = "CaptionFusionFragment";
    private static final Map<String, String> keyConfigMap = new HashMap();
    /* access modifiers changed from: private */
    public final MtkTvAppTVBase appTV = new MtkTvAppTVBase();
    private TVSettingConfig mSettingConfig;

    public static CaptionFusionFragment newInstance() {
        return new CaptionFusionFragment();
    }

    public void onResume() {
        super.onResume();
        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(0, 1);
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        new Thread(new Runnable() {
            public void run() {
                MtkLog.d(CaptionFusionFragment.TAG, "now onResume updatedSysStatus to RESUME");
                CaptionFusionFragment.this.appTV.updatedSysStatus(MtkTvAppTVBase.SYS_MENU_RESUME);
                Intent intent = new Intent("com.mediatek.tv.callcc");
                intent.putExtra("ccvisible", false);
                CaptionFusionFragment.this.getContext().sendBroadcast(intent);
            }
        }).start();
        setPreferencesFromResource(R.xml.partner_caption, (String) null);
        this.mSettingConfig = TVSettingConfig.getInstance(getContext());
        initKeyConfigMap();
        initPrefs();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    private void initKeyConfigMap() {
        keyConfigMap.put(KEY_CAPTIONS_DISPLAY, "g_cc__cc_enable");
        keyConfigMap.put(KEY_CAPTIONS_SERVICES, "g_cc__cc_analog_cc");
        keyConfigMap.put(KEY_CAPTIONS_ADVANCED, "g_cc__cc_digital_cc");
        keyConfigMap.put(KEY_CAPTIONS_FONT_FAMILY, "g_cc__dis_op_ft_style");
        keyConfigMap.put(KEY_CAPTIONS_TEXTSIZE, "g_cc__dis_op_ft_size");
        keyConfigMap.put(KEY_CAPTIONS_TEXT_COLOR, "g_cc__dis_op_ft_color");
        keyConfigMap.put(KEY_CAPTIONS_TEXT_OPACITY, "g_cc__dis_op_ft_opacity");
        keyConfigMap.put(KEY_CAPTIONS_EDGE_TYPE, "g_cc__dis_op_eg_type");
        keyConfigMap.put(KEY_CAPTIONS_EDGE_COLOR, "g_cc__dis_op_eg_color");
        keyConfigMap.put(KEY_CAPTIONS_BACKGROUND_COLOR, "g_cc__dis_op_bk_color");
        keyConfigMap.put(KEY_CAPTIONS_BACKGROUND_OPACITY, "g_cc__dis_op_bk_opacity");
    }

    private void initPrefs() {
        for (String key : keyConfigMap.keySet()) {
            ListPreference preference = (ListPreference) findPreference(key);
            preference.setEntryValues((CharSequence[]) getCharSequence(preference.getEntries().length));
            preference.setOnPreferenceChangeListener(this);
            int defString = this.mSettingConfig.getConfigValueInt(keyConfigMap.get(key));
            if (!key.equals(KEY_CAPTIONS_DISPLAY) && !key.equals(KEY_CAPTIONS_SERVICES) && !key.equals(KEY_CAPTIONS_ADVANCED)) {
                defString = defString == 255 ? 0 : defString + 1;
            } else if ((key.equals(KEY_CAPTIONS_SERVICES) || key.equals(KEY_CAPTIONS_ADVANCED)) && defString != 0) {
                defString--;
            }
            MtkLog.d(TAG, "initPrefs " + key + "=" + defString);
            preference.setValueIndex(defString);
        }
    }

    private String[] getCharSequence(int size) {
        String[] seq = new String[size];
        for (int i = 0; i < size; i++) {
            seq[i] = String.valueOf(i);
        }
        return seq;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!TextUtils.isEmpty(preference.getKey())) {
            setValueToTvConfig(preference.getKey(), Integer.parseInt(newValue.toString()));
            return true;
        }
        throw new IllegalStateException("Unknown preference change");
    }

    private void setValueToTvConfig(String key, int value) {
        MtkLog.d(TAG, "setValueToTvConfig key=" + key + ",value=" + value);
        if (key.equals(KEY_CAPTIONS_SERVICES)) {
            this.mSettingConfig.setConifg("g_cc__cc_analog_cc", value + 1);
        } else if (key.equals(KEY_CAPTIONS_ADVANCED)) {
            this.mSettingConfig.setConifg("g_cc__cc_digital_cc", value + 1);
        } else if (key.equals(KEY_CAPTIONS_DISPLAY)) {
            this.mSettingConfig.setConifg("g_cc__cc_enable", value);
        } else if (key.equals(KEY_CAPTIONS_TEXTSIZE)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(1, value);
        } else if (key.equals(KEY_CAPTIONS_FONT_FAMILY)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(2, value);
        } else if (key.equals(KEY_CAPTIONS_TEXT_COLOR)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(3, value);
        } else if (key.equals(KEY_CAPTIONS_TEXT_OPACITY)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(4, value);
        } else if (key.equals(KEY_CAPTIONS_BACKGROUND_COLOR)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(5, value);
        } else if (key.equals(KEY_CAPTIONS_BACKGROUND_OPACITY)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(6, value);
        } else if (key.equals(KEY_CAPTIONS_EDGE_TYPE)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(9, value);
        } else if (key.equals(KEY_CAPTIONS_EDGE_COLOR)) {
            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(10, value);
        }
    }

    public int getMetricsCategory() {
        return 3;
    }

    public void onPause() {
        super.onPause();
        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(0, 0);
    }

    public void onDestroy() {
        this.appTV.updatedSysStatus(MtkTvAppTVBase.SYS_MENU_PAUSE);
        Intent intent = new Intent("com.mediatek.tv.callcc");
        intent.putExtra("ccvisible", true);
        getContext().sendBroadcast(intent);
        super.onDestroy();
    }
}
