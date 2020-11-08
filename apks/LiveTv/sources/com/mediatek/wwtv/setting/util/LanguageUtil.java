package com.mediatek.wwtv.setting.util;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LanguageUtil {
    static final String TAG = "LanguageUtil";
    private IActivityManager am;
    private Configuration config;
    private List<String> mBannerLanguageSubtitleArray = new ArrayList();
    private List<String> mBannerLanguageSubtitleArrayStr = new ArrayList();
    private final Context mContext;
    private List<String> mLanguageAudioArray = new ArrayList();
    private List<String> mLanguageAudioArrayPA = new ArrayList();
    private List<String> mLanguageOSDArray = new ArrayList();
    private List<String> mLanguageSubtitleArray = new ArrayList();
    private List<String> mLanguageSubtitleArrayStr = new ArrayList();
    private List<String> mMtsLanguageArrayList = new ArrayList();
    private List<String> mMtsLanguageArrayListstr = new ArrayList();
    private final TVContent mTV;

    public LanguageUtil(Context context) {
        int arrResStr;
        int arrRes;
        this.mContext = context;
        this.mTV = TVContent.getInstance(this.mContext);
        this.mLanguageOSDArray.clear();
        this.mLanguageAudioArray.clear();
        this.mLanguageAudioArrayPA.clear();
        this.mLanguageAudioArray = new LinkedList(Arrays.asList(context.getResources().getStringArray(R.array.menu_tv_audio_language_eu_array_value)));
        this.mLanguageAudioArrayPA = new LinkedList(Arrays.asList(context.getResources().getStringArray(R.array.menu_tv_audio_language_array_PA_value)));
        this.mLanguageOSDArray = new LinkedList(Arrays.asList(new String[]{MtkTvConfigTypeBase.S639_CFG_LANG_ENG, MtkTvConfigTypeBase.S639_CFG_LANG_BAQ, MtkTvConfigTypeBase.S639_CFG_LANG_CAT, MtkTvConfigTypeBase.S639_CFG_LANG_SCR, MtkTvConfigTypeBase.S639_CFG_LANG_CZE, MtkTvConfigTypeBase.S638_CFG_LANG_DAN, MtkTvConfigTypeBase.S639_CFG_LANG_DUT, MtkTvConfigTypeBase.S639_CFG_LANG_FIN, MtkTvConfigTypeBase.S639_CFG_LANG_FRA, "gla", "glg", MtkTvConfigTypeBase.S639_CFG_LANG_GER, MtkTvConfigTypeBase.S639_CFG_LANG_HUN, MtkTvConfigTypeBase.S639_CFG_LANG_ITA, MtkTvConfigTypeBase.S639_CFG_LANG_NOR, MtkTvConfigTypeBase.S639_CFG_LANG_POL, MtkTvConfigTypeBase.S639_CFG_LANG_POR, MtkTvConfigTypeBase.S639_CFG_LANG_RUM, MtkTvConfigTypeBase.S639_CFG_LANG_SCC, MtkTvConfigTypeBase.S639_CFG_LANG_SLO, MtkTvConfigTypeBase.S639_CFG_LANG_SLV, MtkTvConfigTypeBase.S639_CFG_LANG_SPA, MtkTvConfigTypeBase.S639_CFG_LANG_SWE, MtkTvConfigTypeBase.S639_CFG_LANG_TUR, "wel", MtkTvConfigTypeBase.S639_CFG_LANG_EST, MtkTvConfigTypeBase.S639_CFG_LANG_RUS}));
        if (this.mTV.isIDNCountry() || this.mTV.isMYSCountry() || this.mTV.isAUSCountry() || this.mTV.isVNMCountry()) {
            arrRes = R.array.menu_tv_subtitle_language_in_mys_aus_tha_vnm_array_value;
            arrResStr = R.array.menu_tv_subtitle_language_in_mys_aus_tha_vnm_array;
        } else if (this.mTV.isSQPCountry()) {
            arrRes = R.array.menu_tv_subtitle_language_sgp_array_value;
            arrResStr = R.array.menu_tv_subtitle_language_sgp_array;
        } else if (this.mTV.isNZLCountry()) {
            arrRes = R.array.menu_tv_subtitle_language_nzl_array_value;
            arrResStr = R.array.menu_tv_subtitle_language_nzl_array;
        } else if (CommonIntegration.isEUPARegion()) {
            arrRes = R.array.menu_tv_subtitle_language_pa_array_value;
            arrResStr = R.array.menu_tv_subtitle_language_pa_array;
        } else {
            arrRes = R.array.menu_tv_subtitle_language_eu_array_value;
            arrResStr = R.array.menu_tv_subtitle_language_eu_array;
        }
        this.mBannerLanguageSubtitleArray = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(R.array.banner_tv_subtitle_language_array_value)));
        this.mBannerLanguageSubtitleArrayStr = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(R.array.banner_tv_subtitle_language_array)));
        this.mLanguageSubtitleArray = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(arrRes)));
        this.mLanguageSubtitleArrayStr = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(arrResStr)));
        this.mMtsLanguageArrayList = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_mts_array_value)));
        this.mMtsLanguageArrayListstr = new LinkedList(Arrays.asList(this.mContext.getResources().getStringArray(R.array.menu_tv_audio_language_mts_array)));
    }

    public String getSubitleNameByValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String value2 = value.trim();
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= this.mBannerLanguageSubtitleArray.size()) {
                break;
            } else if (value2.contains(this.mBannerLanguageSubtitleArray.get(i))) {
                index = i;
                break;
            } else {
                i++;
            }
        }
        String subtitleName = value2;
        MtkLog.d(TAG, "getSubitleNameByValue replace before------>subtitleName: " + subtitleName + "index: " + index);
        if (index < 0) {
            return subtitleName;
        }
        String subtitleName2 = value2.replace(this.mBannerLanguageSubtitleArray.get(index), this.mBannerLanguageSubtitleArrayStr.get(index));
        MtkLog.d(TAG, "getSubitleNameByValue replace after------>subtitleName: " + subtitleName2);
        return subtitleName2;
    }

    public String getMtsNameByValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String value2 = value.trim();
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= this.mMtsLanguageArrayList.size()) {
                break;
            } else if (value2.contains(this.mMtsLanguageArrayList.get(i))) {
                index = i;
                break;
            } else {
                i++;
            }
        }
        MtkLog.d(TAG, "getSubitleNameByValue replace before------>subtitleName: " + value2 + "index: " + index);
        if (index >= 0) {
            String subtitleName = value2.replace(this.mMtsLanguageArrayList.get(index), this.mMtsLanguageArrayListstr.get(index));
            MtkLog.d(TAG, "getSubitleNameByValue replace after------>subtitleName: " + subtitleName);
            return subtitleName;
        }
        String mtsname = new Locale(value2).getDisplayLanguage();
        MtkLog.d(TAG, "locale language==" + mtsname);
        if (!mtsname.equals(value2)) {
            return mtsname;
        }
        return value2;
    }

    public void setTimeZone(int position) {
        int[] timeZoneArray = this.mContext.getResources().getIntArray(R.array.menu_setup_us_timezone_values);
        if (position >= 0 && position < timeZoneArray.length) {
            AlarmManager alarm = (AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
            int offset = TimeZone.getTimeZone(this.mContext.getResources().getStringArray(R.array.menu_setup_us_timezone_str)[position]).getOffset(Calendar.getInstance().getTimeInMillis());
            int p = Math.abs(offset);
            StringBuilder name = new StringBuilder();
            name.append("GMT");
            if (offset < 0) {
                name.append('-');
            } else {
                name.append('+');
            }
            name.append(p / 3600000);
            name.append(':');
            int min = (p / 60000) % 60;
            if (min < 10) {
                name.append('0');
            }
            name.append(min);
            alarm.setTimeZone(name.toString());
        }
    }

    public void setOSDLanguage(int choose) throws RemoteException {
        try {
            this.am = ActivityManagerNative.getDefault();
            this.config = this.am.getConfiguration();
            switch (choose) {
                case 0:
                    this.config.locale = Locale.US;
                    break;
                case 1:
                    this.config.locale = Locale.SIMPLIFIED_CHINESE;
                    break;
                case 2:
                    this.config.locale = Locale.TRADITIONAL_CHINESE;
                    break;
            }
            this.config.userSetLocale = true;
            this.am.updateConfiguration(this.config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
            throw e;
        }
    }

    public boolean getOSDLanguageIsChinese() {
        this.am = ActivityManagerNative.getDefault();
        try {
            this.config = this.am.getConfiguration();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (this.config == null || !Locale.SIMPLIFIED_CHINESE.equals(this.config.locale)) {
            return false;
        }
        return true;
    }

    public class MyLanguageData {
        public Locale local;
        public String tvAPILanguageStr;

        public MyLanguageData() {
        }
    }

    private void setOSDLanguage(MyLanguageData languageData) {
        try {
            this.am = ActivityManagerNative.getDefault();
            this.config = this.am.getConfiguration();
            this.config.locale = languageData.local;
            this.config.userSetLocale = true;
            this.am.updateConfiguration(this.config);
            MtkTvConfig.getInstance().setLanguage(languageData.tvAPILanguageStr);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLocaleLanguage() {
        Locale mLocale = Locale.getDefault();
        return String.format("%s-%s", new Object[]{mLocale.getLanguage(), mLocale.getCountry()});
    }

    private static String getLocaleLanguage(Locale mLocale) {
        return String.format("%s-%s", new Object[]{mLocale.getLanguage(), mLocale.getCountry()});
    }

    public void setLanguage(int value) {
        MtkLog.d(TAG, "osdLanguage.setLanguage value: " + value);
        MyLanguageData languageData = getLauguageData(value);
        String systemLanguage = getLocaleLanguage();
        MtkLog.d(TAG, "osdLanguage.systemLanguage: " + systemLanguage);
        String selectedLanguage = getLocaleLanguage(languageData.local);
        MtkLog.d(TAG, "osdLanguage.selectedLanguage: " + selectedLanguage);
        if (!systemLanguage.equalsIgnoreCase(selectedLanguage)) {
            try {
                setOSDLanguage(languageData);
                MtkLog.d(TAG, "osdLanguage.setOSDLanguage: " + selectedLanguage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SaveValue.getInstance(this.mContext).saveValue("g_gui_lang__gui_language", value);
    }

    public int getLanguage(String cfgid) {
        String lang = MtkTvConfig.getInstance().getLanguage(cfgid);
        MtkLog.d(TAG, "getLanguage:" + lang);
        if (CommonIntegration.isEURegion()) {
            return getLanguageOSDIndex(this.mContext.getResources().getConfiguration().locale.getLanguage());
        }
        return getLanguageOSDIndex(lang);
    }

    public void setAudioLanguage(String cfgid, int value) {
        MtkTvConfig.getInstance().setLanguage(cfgid, getLauguageStr(value));
        SaveValue.getInstance(this.mContext).saveValue(cfgid, value);
    }

    public int getAudioLanguage(String cfgid) {
        String lang = MtkTvConfig.getInstance().getLanguage(cfgid);
        MtkLog.d(TAG, "getAudioLanguage: " + cfgid + " " + lang);
        return getLanguageAudioIndex(lang);
    }

    public void setSubtitleLanguage(String cfgid, int value) {
        String lang = this.mLanguageSubtitleArray.get(value);
        MtkLog.d(TAG, "setSubtitleLanguage cfgid:" + cfgid + "lang:" + lang);
        MtkTvConfig.getInstance().setLanguage(cfgid, lang);
    }

    public int getSubtitleLanguage(String cfgid) {
        int i = 0;
        if ("g_subtitle__subtitle_lang".equals(cfgid)) {
            int subtitleEnable = MtkTvConfig.getInstance().getConfigValue("g_subtitle__subtitle_enable_ex");
            MtkLog.d(TAG, "getSubtitleLanguage cfgid:" + cfgid + "subtitleEnable:" + subtitleEnable);
            if (subtitleEnable == 0) {
                return 0;
            }
        } else if ("g_subtitle__subtitle_lang_2nd".equals(cfgid)) {
            int subtitleEnable2nd = MtkTvConfig.getInstance().getConfigValue("g_subtitle__subtitle_enable_ex_2nd");
            MtkLog.d(TAG, "getSubtitleLanguage cfgid:" + cfgid + "subtitleEnable2nd:" + subtitleEnable2nd);
            if (subtitleEnable2nd == 0) {
                return 0;
            }
        }
        String lang = MtkTvConfig.getInstance().getLanguage(cfgid);
        int index = 8;
        if (this.mTV.isIDNCountry() || this.mTV.isMYSCountry() || this.mTV.isAUSCountry() || this.mTV.isVNMCountry() || this.mTV.isSQPCountry()) {
            index = 6;
        }
        while (true) {
            if (i >= this.mLanguageSubtitleArray.size()) {
                break;
            } else if (lang.equalsIgnoreCase(this.mLanguageSubtitleArray.get(i))) {
                index = i;
                break;
            } else {
                i++;
            }
        }
        MtkLog.d(TAG, "getSubtitleLanguage cfgid:" + cfgid + "lang:" + lang + ",index:" + index);
        return index;
    }

    private int getLanguageOSDIndex(String lang) {
        int index = 0;
        MtkLog.d(TAG, "lang:" + lang);
        if (CommonIntegration.isUSRegion()) {
            if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_ENG)) {
                index = 0;
            } else if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_SPA)) {
                index = 1;
            } else if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_FRA) || lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_FRE)) {
                index = 2;
            } else {
                index = 0;
            }
        } else if (CommonIntegration.isSARegion()) {
            if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_ENG)) {
                index = 0;
            } else if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_POR)) {
                index = 1;
            } else if (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_SPA)) {
                index = 2;
            } else {
                index = 0;
            }
        } else if (CommonIntegration.isEURegion()) {
            index = Math.max(0, ScanContent.getRegionEULanguageCodeList().indexOf(lang));
        }
        MtkLog.d(TAG, "index:" + index);
        return index;
    }

    private int getLanguageAudioIndex(String lang) {
        int index = 0;
        if (CommonIntegration.isUSRegion()) {
            index = lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_ENG) ? 0 : lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_SPA) ? 1 : (lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_FRA) || lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_FRE)) ? 2 : lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_KOR) ? 3 : 0;
        } else if (CommonIntegration.isSARegion()) {
            index = lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_ENG) ? 0 : lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_POR) ? 1 : lang.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_SPA) ? 2 : 0;
        } else if (CommonIntegration.isEURegion()) {
            int i = 0;
            if (MarketRegionInfo.isFunctionSupport(16)) {
                List<String> langListTmp = this.mLanguageAudioArrayPA;
                while (true) {
                    if (i >= langListTmp.size()) {
                        break;
                    } else if (lang.equalsIgnoreCase(langListTmp.get(i))) {
                        index = i;
                        break;
                    } else {
                        i++;
                    }
                }
            } else {
                while (true) {
                    int i2 = i;
                    if (i2 >= this.mLanguageAudioArray.size()) {
                        break;
                    } else if (lang.equalsIgnoreCase(this.mLanguageAudioArray.get(i2))) {
                        index = i2;
                        break;
                    } else {
                        i = i2 + 1;
                    }
                }
            }
        }
        MtkLog.d(TAG, "getLanguageAudioIndex index: " + index);
        return index;
    }

    private MyLanguageData getLauguageData(int position) {
        MyLanguageData data = new MyLanguageData();
        MtkLog.d(TAG, "osdLanguage.mTV.isEURegion(): " + CommonIntegration.isEURegion());
        if (CommonIntegration.isCNRegion()) {
            return getRegionCNLanguageData(position);
        }
        if (CommonIntegration.isUSRegion()) {
            return getRegionUSLanguageData(position);
        }
        if (CommonIntegration.isEURegion()) {
            MyLanguageData data2 = getRegionEULanguageData(position);
            MtkLog.d(TAG, "osdLanguage.data: " + data2.tvAPILanguageStr);
            return data2;
        } else if (CommonIntegration.isSARegion()) {
            return getRegionSALanguageData(position);
        } else {
            return data;
        }
    }

    private String getLauguageStr(int position) {
        MtkLog.d(TAG, "mTV.getLauguageStr() position: " + position);
        if (CommonIntegration.isCNRegion()) {
            return getRegionCNLanguageStr(position);
        }
        if (CommonIntegration.isUSRegion()) {
            return getRegionUSLanguageStr(position);
        }
        if (CommonIntegration.isEURegion()) {
            return getRegionEULanguageStr(position);
        }
        if (CommonIntegration.isSARegion()) {
            return getRegionSALanguageStr(position);
        }
        return "";
    }

    private MyLanguageData getRegionCNLanguageData(int position) {
        MyLanguageData data = new MyLanguageData();
        switch (position) {
            case 0:
                data.local = Locale.ENGLISH;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
            case 1:
                data.local = Locale.CHINESE;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_CHI;
                break;
            default:
                data.local = Locale.ENGLISH;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
        }
        return data;
    }

    private String getRegionCNLanguageStr(int position) {
        switch (position) {
            case 0:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
            case 1:
                return MtkTvConfigTypeBase.S639_CFG_LANG_CHI;
            default:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
        }
    }

    private MyLanguageData getRegionUSLanguageData(int position) {
        MyLanguageData data = new MyLanguageData();
        switch (position) {
            case 0:
                data.local = Locale.US;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
            case 1:
                data.local = new Locale("es", "ES");
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_SPA;
                break;
            case 2:
                data.local = Locale.FRANCE;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_FRA;
                break;
            default:
                data.local = Locale.US;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
        }
        return data;
    }

    private String getRegionUSLanguageStr(int position) {
        switch (position) {
            case 0:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
            case 1:
                return MtkTvConfigTypeBase.S639_CFG_LANG_SPA;
            case 2:
                return MtkTvConfigTypeBase.S639_CFG_LANG_FRA;
            case 3:
                return MtkTvConfigTypeBase.S639_CFG_LANG_KOR;
            default:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
        }
    }

    private MyLanguageData getRegionSALanguageData(int position) {
        MyLanguageData data = new MyLanguageData();
        switch (position) {
            case 0:
                data.local = Locale.ENGLISH;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
            case 1:
                data.local = new Locale("pt", "PT");
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_POR;
                break;
            case 2:
                data.local = new Locale("es", "ES");
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_SPA;
                break;
            default:
                data.local = Locale.ENGLISH;
                data.tvAPILanguageStr = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
                break;
        }
        return data;
    }

    private String getRegionSALanguageStr(int position) {
        switch (position) {
            case 0:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
            case 1:
                return MtkTvConfigTypeBase.S639_CFG_LANG_POR;
            case 2:
                return MtkTvConfigTypeBase.S639_CFG_LANG_SPA;
            default:
                return MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
        }
    }

    private MyLanguageData getRegionEULanguageData(int position) {
        MyLanguageData data = new MyLanguageData();
        MtkLog.d(TAG, "getRegionEULanguageData.position: " + position);
        MyLanguageData wizardData = ScanContent.getRegionEULanguageStrForMenu(this.mContext, position);
        data.local = wizardData.local;
        data.tvAPILanguageStr = wizardData.tvAPILanguageStr;
        return data;
    }

    private String getRegionEULanguageStr(int position) {
        String lang = MtkTvConfigTypeBase.S639_CFG_LANG_ENG;
        List<String> langListTmp = this.mLanguageAudioArray;
        if (MarketRegionInfo.isFunctionSupport(16)) {
            langListTmp = this.mLanguageAudioArrayPA;
            MtkLog.d(TAG, "getRegionEULanguageStr.getCountry: " + MtkTvConfig.getInstance().getCountry());
        }
        if (position < langListTmp.size()) {
            lang = langListTmp.get(position);
        }
        MtkLog.d(TAG, "getRegionEULanguageStr.lang: " + lang);
        return lang;
    }
}
