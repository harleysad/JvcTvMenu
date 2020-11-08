package com.mediatek.wwtv.setting.preferences;

import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.mediatek.dm.DMNativeDaemonConnector;
import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.MenuDataHelper;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.setting.view.FacVideo;
import com.mediatek.wwtv.setting.widget.detailui.Action;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreferenceData {
    private static final String TAG = "PreferenceData";
    private static PreferenceData mInstance = null;
    public boolean isNeedRefreshTime = false;
    public MenuConfigManager mConfigManager = null;
    /* access modifiers changed from: private */
    public final Context mContext;
    private volatile PreferenceScreen mScreen;
    public TVContent mTV;
    Action satelliteManualTuning;

    public interface OnDataChangeListener {
        boolean OnDataChangeListener(PreferenceScreen preferenceScreen, Object obj);
    }

    private PreferenceData(Context context) {
        this.mContext = context;
        this.mTV = TVContent.getInstance(context);
        this.mConfigManager = MenuConfigManager.getInstance(context);
    }

    public static PreferenceData getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreferenceData(context);
        }
        return mInstance;
    }

    public static synchronized void setInstance() {
        synchronized (PreferenceData.class) {
            mInstance = null;
        }
    }

    public synchronized PreferenceScreen getData() {
        return this.mScreen;
    }

    public synchronized void setData(PreferenceScreen screen) {
        this.mScreen = screen;
    }

    public synchronized void invalidate(String itemID, Object newValue) {
        MtkLog.d(TAG, itemID + "," + newValue);
        int i = 0;
        if (itemID.startsWith(MenuConfigManager.CFG_MENU_AUDIOINFO_GET_STRING)) {
            for (int i2 = 0; i2 < this.mScreen.getPreferenceCount(); i2++) {
                ListPreference listPreference = (ListPreference) this.mScreen.getPreference(i2);
                if (listPreference.getKey() != itemID) {
                    listPreference.setValue("1");
                    listPreference.setSummary("1");
                }
            }
        }
        if (itemID.startsWith(MenuConfigManager.SOUNDTRACKS_GET_STRING)) {
            for (int i3 = 0; i3 < this.mScreen.getPreferenceCount(); i3++) {
                ListPreference listPreference2 = (ListPreference) this.mScreen.getPreference(i3);
                if (listPreference2.getKey() != itemID) {
                    listPreference2.setValue("1");
                    listPreference2.setSummary("1");
                }
            }
        }
        int i4 = -1;
        switch (itemID.hashCode()) {
            case -2032280123:
                if (itemID.equals("g_video__contrast")) {
                    i4 = 11;
                    break;
                }
                break;
            case -1990872699:
                if (itemID.equals("g_misc__ch_list_type")) {
                    i4 = 20;
                    break;
                }
                break;
            case -1756838208:
                if (itemID.equals("g_video__vid_3d_mode")) {
                    i4 = 15;
                    break;
                }
                break;
            case -1706416389:
                if (itemID.equals("g_video__vid_mjc_effect")) {
                    i4 = 3;
                    break;
                }
                break;
            case -1615458895:
                if (itemID.equals("g_video__vid_3d_img_sfty")) {
                    i4 = 17;
                    break;
                }
                break;
            case -1534564801:
                if (itemID.equals("g_video__vid_game_mode")) {
                    i4 = 9;
                    break;
                }
                break;
            case -1220557303:
                if (itemID.equals("g_fusion_common__lcn")) {
                    i4 = 21;
                    break;
                }
                break;
            case -1075305093:
                if (itemID.equals("g_video__clr_gain_b")) {
                    i4 = 6;
                    break;
                }
                break;
            case -1075305088:
                if (itemID.equals("g_video__clr_gain_g")) {
                    i4 = 5;
                    break;
                }
                break;
            case -1075305077:
                if (itemID.equals("g_video__clr_gain_r")) {
                    i4 = 4;
                    break;
                }
                break;
            case 284317334:
                if (itemID.equals(MenuConfigManager.PARENTAL_RATINGS_ENABLE)) {
                    i4 = 0;
                    break;
                }
                break;
            case 588589667:
                if (itemID.equals("g_video__vid_vga_mode")) {
                    i4 = 7;
                    break;
                }
                break;
            case 756863104:
                if (itemID.equals(MenuConfigManager.AUTO_ADJUST)) {
                    i4 = 2;
                    break;
                }
                break;
            case 1172431336:
                if (itemID.equals("g_video__vid_3d_nav_auto")) {
                    i4 = 16;
                    break;
                }
                break;
            case 1212371540:
                if (itemID.equals("g_video__brightness")) {
                    i4 = 10;
                    break;
                }
                break;
            case 1408177919:
                if (itemID.equals("g_bs__bs_src")) {
                    i4 = 19;
                    break;
                }
                break;
            case 1467509692:
                if (itemID.equals("g_cc__dcs")) {
                    i4 = 1;
                    break;
                }
                break;
            case 1652255911:
                if (itemID.equals("g_video__vid_hue")) {
                    i4 = 13;
                    break;
                }
                break;
            case 1652265877:
                if (itemID.equals("g_video__vid_sat")) {
                    i4 = 12;
                    break;
                }
                break;
            case 1652266090:
                if (itemID.equals("g_video__vid_shp")) {
                    i4 = 14;
                    break;
                }
                break;
            case 1764657165:
                if (itemID.equals(MenuConfigManager.TV_CHANNEL_CLEAR)) {
                    i4 = 18;
                    break;
                }
                break;
            case 2000928345:
                if (itemID.equals("g_video__vid_hdmi_mode")) {
                    i4 = 8;
                    break;
                }
                break;
        }
        switch (i4) {
            case 0:
                int value = ((Integer) newValue).intValue();
                for (int i5 = 0; i5 < this.mScreen.getPreferenceCount(); i5++) {
                    Preference tempPre = this.mScreen.getPreference(i5);
                    if (tempPre.getKey() != itemID) {
                        if (value == 1 && this.mTV.getRatingEnable() == 1) {
                            if (tempPre.getKey() != MenuConfigManager.PARENTAL_OPEN_VCHIP) {
                                tempPre.setEnabled(true);
                            } else if (this.mTV.getATSCRating().isOpenVCHIPInfoAvailable()) {
                                tempPre.setEnabled(true);
                            } else {
                                tempPre.setEnabled(false);
                            }
                        } else if (value == 0) {
                            tempPre.setEnabled(false);
                        }
                    }
                }
                break;
            case 1:
                int CSvalue = Integer.parseInt((String) newValue);
                if (1 == CSvalue) {
                    setCaptionStyle(CSvalue);
                }
                for (int i6 = 0; i6 < this.mScreen.getPreferenceCount(); i6++) {
                    Preference CStempPre = this.mScreen.getPreference(i6);
                    if (CStempPre.getKey() != itemID) {
                        if (CSvalue == 1) {
                            CStempPre.setEnabled(true);
                        } else if (CSvalue == 0) {
                            CStempPre.setEnabled(false);
                        }
                    }
                }
                break;
            case 2:
                MtkLog.d(TAG, "AUTO_ADJUST == " + itemID);
                int intValue = ((Integer) newValue).intValue();
                for (int i7 = 0; i7 < this.mScreen.getPreferenceCount(); i7++) {
                    Preference autoadjusttempPre = this.mScreen.getPreference(i7);
                    if (autoadjusttempPre.getKey() != itemID) {
                        if (autoadjusttempPre instanceof ProgressPreference) {
                            ProgressPreference autoadjust = (ProgressPreference) autoadjusttempPre;
                            if (autoadjust.getKey().equals("g_vga__vga_pos_h")) {
                                autoadjust.setCurrentValue(this.mConfigManager.getDefault("g_vga__vga_pos_h"));
                            } else if (autoadjust.getKey().equals("g_vga__vga_pos_v")) {
                                autoadjust.setCurrentValue(this.mConfigManager.getDefault("g_vga__vga_pos_v"));
                            } else if (autoadjust.getKey().equals("g_vga__vga_phase")) {
                                autoadjust.setCurrentValue(this.mConfigManager.getDefault("g_vga__vga_phase"));
                            } else if (autoadjust.getKey().equals("g_vga__vga_clock")) {
                                autoadjust.setCurrentValue(this.mConfigManager.getDefault("g_vga__vga_clock"));
                            }
                        }
                    }
                }
                break;
            case 3:
                int effectValue = Integer.parseInt((String) newValue);
                for (int i8 = 0; i8 < this.mScreen.getPreferenceCount(); i8++) {
                    Preference effectTempPre = this.mScreen.getPreference(i8);
                    if (effectTempPre.getKey() != itemID) {
                        if (effectValue == 0) {
                            effectTempPre.setEnabled(false);
                        } else {
                            effectTempPre.setEnabled(true);
                        }
                    }
                }
                break;
            case 4:
            case 5:
            case 6:
                for (int i9 = 0; i9 < this.mScreen.getPreferenceCount(); i9++) {
                    Preference colorTempPre = this.mScreen.getPreference(i9);
                    if (colorTempPre.getKey() == "g_video__clr_temp") {
                        ListPreference listPreference3 = (ListPreference) colorTempPre;
                        listPreference3.setValue(String.valueOf(this.mConfigManager.getDefault("g_video__clr_temp") - this.mConfigManager.getMin("g_video__clr_temp")));
                        listPreference3.setSummary(listPreference3.getEntries()[this.mConfigManager.getDefault("g_video__clr_temp") - this.mConfigManager.getMin("g_video__clr_temp")]);
                    }
                }
                break;
            case 7:
                int vgaModeValue = Integer.parseInt((String) newValue);
                for (int i10 = 0; i10 < this.mScreen.getPreferenceCount(); i10++) {
                    Preference vgaModePre = this.mScreen.getPreference(i10);
                    if (vgaModePre.getKey() == "g_video__vid_nr" || vgaModePre.getKey() == "g_video__vid_mpeg_nr" || vgaModePre.getKey() == "g_video__vid_luma" || vgaModePre.getKey() == "g_video__vid_flash_tone" || vgaModePre.getKey() == "g_video__vid_blue_stretch") {
                        if (vgaModeValue == 0) {
                            vgaModePre.setEnabled(false);
                        } else {
                            vgaModePre.setEnabled(true);
                        }
                    }
                }
                break;
            case 8:
                int hdmiModeValue = Integer.parseInt((String) newValue);
                for (int i11 = 0; i11 < this.mScreen.getPreferenceCount(); i11++) {
                    Preference hdmiModePre = this.mScreen.getPreference(i11);
                    if (hdmiModePre.getKey() == "g_video__vid_nr" || hdmiModePre.getKey() == "g_video__vid_mpeg_nr" || hdmiModePre.getKey() == "g_video__vid_luma" || hdmiModePre.getKey() == "g_video__vid_flash_tone" || hdmiModePre.getKey() == "g_video__vid_blue_stretch") {
                        if (hdmiModeValue != 1) {
                            hdmiModePre.setEnabled(true);
                        } else {
                            hdmiModePre.setEnabled(false);
                        }
                    }
                }
                break;
            case 9:
                int gameModeValue = Integer.parseInt((String) newValue);
                for (int i12 = 0; i12 < this.mScreen.getPreferenceCount(); i12++) {
                    Preference gameModePre = this.mScreen.getPreference(i12);
                    if (gameModePre.getKey() == MenuConfigManager.MJC) {
                        if (gameModeValue != 0) {
                            gameModePre.setEnabled(false);
                        } else if (CommonIntegration.getInstance().isPipOrPopState()) {
                            gameModePre.setEnabled(false);
                        } else {
                            gameModePre.setEnabled(true);
                        }
                    }
                    if (gameModePre.getKey() == "g_video__vid_di_film_mode") {
                        if (gameModeValue == 1) {
                            gameModePre.setEnabled(false);
                        } else {
                            gameModePre.setEnabled(true);
                        }
                    }
                }
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                MtkLog.d(TAG, "BRIGHTNESS  == " + itemID);
                for (int i13 = 0; i13 < this.mScreen.getPreferenceCount(); i13++) {
                    Preference pictureModePre = this.mScreen.getPreference(i13);
                    if (pictureModePre.getKey() == "g_video__picture_mode") {
                        MtkLog.d(TAG, "CONTRAST == " + pictureModePre.getKey());
                        ListPreference listPreference4 = (ListPreference) pictureModePre;
                        MtkLog.d(TAG, "CONTRAST value== " + (this.mConfigManager.getDefault("g_video__picture_mode") - this.mConfigManager.getMin("g_video__picture_mode")));
                        int cur = MtkTvConfig.getInstance().getConfigValue("g_video__picture_mode");
                        if (cur != 5) {
                            if (cur != 6) {
                                listPreference4.setValue(String.valueOf(this.mConfigManager.getDefault("g_video__picture_mode") - this.mConfigManager.getMin("g_video__picture_mode")));
                                listPreference4.setSummary(listPreference4.getEntries()[this.mConfigManager.getDefault("g_video__picture_mode") - this.mConfigManager.getMin("g_video__picture_mode")]);
                            }
                        }
                        Log.d(TAG, "the picture mode index" + cur);
                        int cur2 = cur + -5;
                        listPreference4.setValue(String.valueOf(cur2));
                        listPreference4.setSummary(listPreference4.getEntries()[cur2]);
                    }
                }
                break;
            case 15:
            case 16:
            case 17:
                MtkLog.d(TAG, "3D  == " + itemID);
                for (int i14 = 0; i14 < this.mScreen.getPreferenceCount(); i14++) {
                    Preference video3DModePre = this.mScreen.getPreference(i14);
                    if (video3DModePre.getKey() == "g_video__vid_3d_mode") {
                        if (this.mConfigManager.getDefault(video3DModePre.getKey()) == 0) {
                            this.mConfigManager.setValue(video3DModePre.getKey(), 0);
                        }
                        String[] m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array);
                        if (this.mConfigManager.getDefault("g_video__vid_3d_nav_auto") == 1) {
                            m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array_for_1);
                        } else if (this.mConfigManager.getDefault("g_video__vid_3d_nav_auto") == 0) {
                            m3DModeArr = this.mContext.getResources().getStringArray(R.array.menu_video_3d_mode_array_for_0);
                        }
                        ListPreference listPreference5 = (ListPreference) video3DModePre;
                        listPreference5.setEntries((CharSequence[]) m3DModeArr);
                        listPreference5.setEntryValues((CharSequence[]) PreferenceUtil.getCharSequence(m3DModeArr.length));
                        listPreference5.setValue("" + this.mConfigManager.getDefault("g_video__vid_3d_mode"));
                        video3DModePre = listPreference5;
                    }
                    video3DModePre.setEnabled(this.mConfigManager.isConfigEnabled(video3DModePre.getKey()));
                }
                break;
            case 18:
                MtkLog.d(TAG, "start changeEnable TV_CHANNEL_CLEAR");
                int i15 = 0;
                while (true) {
                    if (i15 < this.mScreen.getPreferenceCount()) {
                        Preference cleanList = this.mScreen.getPreference(i15);
                        if (cleanList != null) {
                            MtkLog.d(TAG, "changeEnable cleanList.getKey() is " + cleanList.getKey());
                            if (!(cleanList.getKey() == MenuConfigManager.TV_CHANNEL_SKIP || cleanList.getKey() == MenuConfigManager.TV_CHANNEL_SORT || cleanList.getKey() == MenuConfigManager.TV_CHANNEL_EDIT || cleanList.getKey() == MenuConfigManager.TV_SA_CHANNEL_EDIT || cleanList.getKey() == MenuConfigManager.TV_CHANNEL_MOVE || cleanList.getKey() == MenuConfigManager.TV_CHANNEL_CLEAR)) {
                                if (cleanList.getKey() != MenuConfigManager.TV_FAVORITE_NETWORK) {
                                    if (cleanList.getKey() == MenuConfigManager.TV_CHANNELFINE_TUNE) {
                                        if (CommonIntegration.getInstanceWithContext(this.mContext).hasActiveChannel()) {
                                            MtkLog.d(TAG, "changeEnable TV_CHANNELFINE_TUNE");
                                            if (CommonIntegration.isSARegion()) {
                                                MtkLog.d(TAG, "changeEnable TV_CHANNELFINE_TUNE isSARegion()");
                                                if (TIFChannelManager.getInstance(this.mContext).hasATVChannels()) {
                                                    cleanList.setEnabled(true);
                                                } else {
                                                    cleanList.setEnabled(false);
                                                }
                                            }
                                            if (CommonIntegration.isEURegion()) {
                                                MtkLog.d(TAG, "changeEnable TV_CHANNELFINE_TUNE isEURegion()");
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("changeEnable TV_CHANNELFINE_TUNE !isCurrentSourceDTV ");
                                                sb.append(!this.mTV.isCurrentSourceDTV());
                                                MtkLog.d(TAG, sb.toString());
                                                if (!this.mTV.isCurrentSourceDTV()) {
                                                    cleanList.setEnabled(true);
                                                } else {
                                                    cleanList.setEnabled(false);
                                                }
                                                MtkLog.d(TAG, "mTV.isTurkeyCountry()> " + this.mTV.isTurkeyCountry() + "dvbs_operator_name_tivibu > " + ScanContent.getDVBSCurrentOPStr(this.mContext) + "  >>   " + this.mContext.getString(R.string.dvbs_operator_name_tivibu));
                                                if (!this.mTV.isTurkeyCountry() || !ScanContent.getDVBSCurrentOPStr(this.mContext).equalsIgnoreCase(this.mContext.getString(R.string.dvbs_operator_name_tivibu))) {
                                                    cleanList.setEnabled(true);
                                                } else {
                                                    cleanList.setEnabled(false);
                                                }
                                            }
                                        } else {
                                            MtkLog.d(TAG, "changeEnable false TV_CHANNELFINE_TUNE");
                                            cleanList.setEnabled(false);
                                        }
                                        cleanList.setEnabled(false);
                                    }
                                    i15++;
                                }
                            }
                            if (CommonIntegration.isCNRegion()) {
                                if (CommonIntegration.getInstanceWithContext(this.mContext).hasActiveChannel()) {
                                    MtkLog.d(TAG, "changeEnable true");
                                    if (cleanList != null) {
                                        cleanList.setEnabled(true);
                                    }
                                } else {
                                    MtkLog.d(TAG, "changeEnable false");
                                    if (cleanList != null) {
                                        cleanList.setEnabled(false);
                                    }
                                }
                            } else if (CommonIntegration.getInstanceWithContext(this.mContext).hasActiveChannel()) {
                                MtkLog.d(TAG, "changeEnable true cleanList.getKey() is " + cleanList.getKey());
                                cleanList.setEnabled(true);
                                if (cleanList.getKey() != MenuConfigManager.TV_SA_CHANNEL_EDIT) {
                                    MenuDataHelper.getInstance(this.mContext).setSkipSortEditItemHid(cleanList);
                                }
                            } else {
                                MtkLog.d(TAG, "changeEnable false cleanList.getKey() is " + cleanList.getKey());
                                cleanList.setEnabled(false);
                            }
                            cleanList.setEnabled(false);
                            MtkLog.d(TAG, "end changeEnable set success");
                            i15++;
                        }
                    }
                }
                break;
            case 19:
                if (CommonIntegration.isUSRegion() != 0) {
                    for (int i16 = 0; i16 < this.mScreen.getPreferenceCount(); i16++) {
                        Preference tunermode = this.mScreen.getPreference(i16);
                        if (tunermode.getKey() == MenuConfigManager.CHANNEL_CUSTOMIZE_CHANNEL_LIST) {
                            if (CommonIntegration.getInstanceWithContext(this.mContext).hasActiveChannel(true)) {
                                tunermode.setEnabled(true);
                            } else {
                                tunermode.setEnabled(false);
                            }
                        }
                    }
                }
                TVAsyncExecutor.getInstance().execute(new Runnable() {
                    public void run() {
                        TIFChannelManager.getInstance(PreferenceData.this.mContext).getChannelListForFindOrNomal();
                    }
                });
                CommonIntegration.getInstance().getChannelAllandActionNum();
                break;
            case 20:
                int ChannelListTypeValue = Integer.parseInt((String) newValue);
                int i17 = 0;
                while (true) {
                    if (i17 < this.mScreen.getPreferenceCount()) {
                        Preference tvChannelListType = this.mScreen.getPreference(i17);
                        if (tvChannelListType.getKey() == itemID) {
                            String[] channelListType = this.mContext.getResources().getStringArray(R.array.menu_tv_channel_listtype);
                            if (ChannelListTypeValue == 0) {
                                MtkLog.d(TAG, "MenuConfigManager.CHANNEL_LIST_TYPE, profileName : " + channelListType[0]);
                                tvChannelListType.setSummary((CharSequence) channelListType[0]);
                            } else {
                                String profileName = MtkTvConfig.getInstance().getConfigString("g_misc__ch_list_slot");
                                MtkLog.d(TAG, "MenuConfigManager.CHANNEL_LIST_TYPE, profileName : " + profileName);
                                tvChannelListType.setSummary((CharSequence) profileName);
                            }
                        } else {
                            i17++;
                        }
                    }
                }
                break;
            case 21:
                MtkLog.d(TAG, "MenuConfigManager.CHANNEL_LCN:");
                if (this.mTV.getConfigValue("g_fusion_common__lcn") != 0 && !CommonIntegration.getInstance().isCurrentSourceATVforEuPA()) {
                    for (int i18 = 0; i18 < this.mScreen.getPreferenceCount(); i18++) {
                        Preference lcn = this.mScreen.getPreference(i18);
                        if (lcn.getKey() == MenuConfigManager.TV_CHANNEL_SORT || lcn.getKey() == MenuConfigManager.TV_CHANNEL_MOVE) {
                            lcn.setEnabled(false);
                        }
                    }
                    break;
                }
                break;
        }
        if (itemID.startsWith(MenuConfigManager.PARENTAL_TIF_CONTENT_RATGINS_SYSTEM)) {
            String[] strInfo = itemID.split("\\|");
            String prefix = "parental_tif_ratings_system_cnt|" + strInfo[strInfo.length - 2];
            for (int i19 = 0; i19 < this.mScreen.getPreferenceCount(); i19++) {
                Preference tempPre2 = this.mScreen.getPreference(i19);
                if (tempPre2.getKey().startsWith(prefix)) {
                    tempPre2.setEnabled(((Integer) newValue).intValue() == 1);
                    if (((Integer) newValue).intValue() == 0) {
                        ((SwitchPreference) tempPre2).setChecked(false);
                    }
                }
            }
        }
        if (itemID.equals(MenuConfigManager.POWER_SETTING_CONFIG_VALUE)) {
            while (true) {
                int i20 = i;
                if (i20 < this.mScreen.getPreferenceCount()) {
                    Preference tempPre3 = this.mScreen.getPreference(i20);
                    if (tempPre3.getKey().equals(MenuConfigManager.POWER_SETTING_CONFIG_VALUE)) {
                        ListPreference listPreference6 = (ListPreference) tempPre3;
                        listPreference6.setSummary(listPreference6.getEntries()[Integer.valueOf((String) newValue).intValue()]);
                        MtkLog.d(TAG, "power setting: " + listPreference6.getSummary());
                    }
                    i = i20 + 1;
                }
            }
        }
    }

    public void setTVItemsVisibility(PreferenceScreen preferenceScreen) {
        String tabTV = this.mContext.getResources().getString(R.string.menu_tab_tv);
        String tabSetUp = this.mContext.getResources().getString(R.string.menu_tab_setup);
        boolean startTag = false;
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            Preference tempPre = preferenceScreen.getPreference(i);
            if (tempPre.getKey().equals(tabTV) && (tempPre instanceof PreferenceCategory)) {
                startTag = true;
            } else if (tempPre.getKey().equals(tabSetUp) && (tempPre instanceof PreferenceCategory)) {
                return;
            }
            if (startTag) {
                if (!this.mTV.isCurrentSourceTv() || this.mTV.isCurrentSourceBlocking()) {
                    tempPre.setVisible(false);
                } else {
                    tempPre.setVisible(true);
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v6 */
    /* JADX WARNING: type inference failed for: r2v7, types: [boolean] */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x1237, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0810  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0833  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x0f94  */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0f98  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0f9a  */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x0fda  */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x0ffa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void resume() {
        /*
            r26 = this;
            r1 = r26
            monitor-enter(r26)
            r0 = 0
            android.support.v7.preference.PreferenceScreen r2 = r1.mScreen     // Catch:{ all -> 0x1238 }
            if (r2 != 0) goto L_0x000a
            monitor-exit(r26)
            return
        L_0x000a:
            android.support.v7.preference.PreferenceScreen r2 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r1.setTVItemsVisibility(r2)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r2 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            r2.notifyPreferenceForVideo()     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = "PreferenceData"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r3.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r4 = "mScreen.getKey "
            r3.append(r4)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r4 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r4 = r4.getKey()     // Catch:{ all -> 0x1238 }
            r3.append(r4)     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = "SETUP_digital_style"
            android.support.v7.preference.PreferenceScreen r3 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r3.getKey()     // Catch:{ all -> 0x1238 }
            boolean r2 = r2.equals(r3)     // Catch:{ all -> 0x1238 }
            if (r2 == 0) goto L_0x004a
            com.mediatek.wwtv.setting.util.TVContent r2 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "g_cc__dcs"
            int r2 = r2.getConfigValue(r3)     // Catch:{ all -> 0x1238 }
            goto L_0x0055
        L_0x004a:
            java.lang.String r2 = "SETUP_caption_setup"
            android.support.v7.preference.PreferenceScreen r3 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r3.getKey()     // Catch:{ all -> 0x1238 }
            r2.equals(r3)     // Catch:{ all -> 0x1238 }
        L_0x0055:
            r2 = 0
            r3 = r0
            r0 = r2
        L_0x0058:
            android.support.v7.preference.PreferenceScreen r4 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r4 = r4.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r0 >= r4) goto L_0x11fd
            android.support.v7.preference.PreferenceScreen r4 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r4 = r4.getPreference(r0)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "PreferenceData"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r6.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "mScreen.getPreferenceCount() "
            r6.append(r7)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r7 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r7 = r7.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            r6.append(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "PreferenceData"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r6.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "mScreen.getPreference(i) "
            r6.append(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r4.getKey()     // Catch:{ all -> 0x1238 }
            r6.append(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)     // Catch:{ all -> 0x1238 }
            android.content.Context r5 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ all -> 0x1238 }
            r6 = 2131691508(0x7f0f07f4, float:1.901209E38)
            java.lang.String r5 = r5.getString(r6)     // Catch:{ all -> 0x1238 }
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r6 = r6.equals(r5)     // Catch:{ all -> 0x1238 }
            r7 = 1
            if (r6 == 0) goto L_0x00db
            boolean r6 = r4 instanceof android.support.v7.preference.PreferenceCategory     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x00db
            android.support.v7.preference.PreferenceScreen r6 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "g_video__vid_vga_mode"
            android.support.v7.preference.Preference r6 = r6.findPreference(r8)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SUB_VGA"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            boolean r9 = r6.isVisible()     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x00d8
            boolean r9 = r8.isVisible()     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x00d8
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x00db
        L_0x00d8:
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
        L_0x00db:
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "SETUP_OADSetting"
            boolean r6 = r6.startsWith(r8)     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x010b
            android.content.Context r6 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r6 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r6)     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "pvr_start"
            boolean r6 = r6.readBooleanValue(r8)     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x0108
            android.content.Context r6 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r6 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r6)     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "timeshift_start"
            boolean r6 = r6.readBooleanValue(r8)     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x0104
            goto L_0x0108
        L_0x0104:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x010b
        L_0x0108:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x010b:
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "tveuChannel"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x0123
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "tv_channel"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x018b
        L_0x0123:
            java.lang.String r6 = "PreferenceData"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r8.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "isPipOrPopState() ="
            r8.append(r9)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.isPipOrPopState()     // Catch:{ all -> 0x1238 }
            r8.append(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r8)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r6 = r6.isPipOrPopState()     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x0158
            com.mediatek.wwtv.setting.util.TVContent r6 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r6 = r6.isCanScan()     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x0154
            goto L_0x0158
        L_0x0154:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x015b
        L_0x0158:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x015b:
            android.content.Context r6 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.scan.EditChannel r6 = com.mediatek.wwtv.setting.scan.EditChannel.getInstance(r6)     // Catch:{ all -> 0x1238 }
            int r6 = r6.getBlockChannelNumForSource()     // Catch:{ all -> 0x1238 }
            if (r6 <= 0) goto L_0x017e
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r6 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen$ScanPreferenceClickListener r6 = r6.mScanPreferenceClickListener     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            r6.setActionId(r8)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r6 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen$ScanPreferenceClickListener r6 = r6.mScanPreferenceClickListener     // Catch:{ all -> 0x1238 }
            r4.setOnPreferenceClickListener(r6)     // Catch:{ all -> 0x1238 }
            goto L_0x018b
        L_0x017e:
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r6 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen$ScanPreferenceClickListener r6 = r6.mScanPreferenceClickListener     // Catch:{ all -> 0x1238 }
            r8 = 0
            r6.setActionId(r8)     // Catch:{ all -> 0x1238 }
            r4.setOnPreferenceClickListener(r8)     // Catch:{ all -> 0x1238 }
        L_0x018b:
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "g_fusion_common__encrypt_dvbt"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x01bb
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "g_fusion_common__storage_dvbt"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x01bb
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "g_fusion_common__encrypt_dvbc"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 != 0) goto L_0x01bb
            java.lang.String r6 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "g_fusion_common__storage_dvbc"
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x01cc
        L_0x01bb:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r6 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r6 = r6.isCurrentSourceATV()     // Catch:{ all -> 0x1238 }
            if (r6 == 0) goto L_0x01c9
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x01cc
        L_0x01c9:
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
        L_0x01cc:
            android.content.Context r6 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r6 = r6.getResources()     // Catch:{ all -> 0x1238 }
            r8 = 2131691503(0x7f0f07ef, float:1.901208E38)
            java.lang.String r6 = r6.getString(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.equals(r6)     // Catch:{ all -> 0x1238 }
            r9 = 2
            if (r8 == 0) goto L_0x02a5
            boolean r8 = r4 instanceof android.support.v7.preference.PreferenceCategory     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x02a5
            r8 = 0
            r10 = 0
            com.mediatek.wwtv.setting.util.TVContent r11 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.isCurrentSourceBlocking()     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x01f4
            r8 = 0
            goto L_0x01f5
        L_0x01f4:
            r8 = 1
        L_0x01f5:
            android.support.v7.preference.PreferenceScreen r11 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "g_menu__soundtracks"
            android.support.v7.preference.Preference r11 = r11.findPreference(r12)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0223
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_menu__soundtracksenable"
            boolean r12 = r12.isConfigEnabled(r13)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0220
            r13 = 41
            boolean r13 = com.mediatek.wwtv.tvcenter.util.MarketRegionInfo.isFunctionSupport(r13)     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x0220
            com.mediatek.wwtv.setting.util.TVContent r13 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r13 = r13.isCurrentSourceDTV()     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x0220
            if (r12 == 0) goto L_0x0220
            r11.setVisible(r7)     // Catch:{ all -> 0x1238 }
            r10 = 1
            goto L_0x0223
        L_0x0220:
            r11.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x0223:
            android.support.v7.preference.PreferenceScreen r12 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_audio__aud_type"
            android.support.v7.preference.Preference r12 = r12.findPreference(r13)     // Catch:{ all -> 0x1238 }
            r11 = r12
            if (r11 == 0) goto L_0x0250
            boolean r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x024d
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.isCurrentSourceTv()     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x024d
            if (r8 == 0) goto L_0x024d
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_menu__audio_ad_type"
            boolean r12 = r12.isConfigVisible(r13)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x024d
            r11.setVisible(r7)     // Catch:{ all -> 0x1238 }
            r10 = 1
            goto L_0x0250
        L_0x024d:
            r11.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x0250:
            android.support.v7.preference.PreferenceScreen r12 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "SUB_VISUALLYIMPAIRED"
            android.support.v7.preference.Preference r12 = r12.findPreference(r13)     // Catch:{ all -> 0x1238 }
            r11 = r12
            if (r11 == 0) goto L_0x02a2
            boolean r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x029d
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.isCurrentSourceTv()     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x029d
            if (r8 == 0) goto L_0x029d
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_menu__audio_ad_type"
            boolean r12 = r12.isConfigVisible(r13)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x029d
            r11.setVisible(r7)     // Catch:{ all -> 0x1238 }
            r10 = 1
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.Context r12 = r12.getApplicationContext()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.MenuConfigManager r12 = com.mediatek.wwtv.setting.util.MenuConfigManager.getInstance(r12)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_audio__aud_type"
            int r12 = r12.getDefault(r13)     // Catch:{ all -> 0x1238 }
            if (r12 != r9) goto L_0x0299
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "g_menu__audio_ad_type"
            boolean r12 = r12.isConfigEnabled(r13)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x0299
            r11.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x02a2
        L_0x0299:
            r11.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x02a2
        L_0x029d:
            if (r11 == 0) goto L_0x02a2
            r11.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x02a2:
            r4.setVisible(r10)     // Catch:{ all -> 0x1238 }
        L_0x02a5:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "soundtracksgetstring"
            boolean r8 = r8.startsWith(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x02d1
            boolean r8 = r4 instanceof android.support.v7.preference.ListPreference     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x02d1
            r8 = r4
            android.support.v7.preference.ListPreference r8 = (android.support.v7.preference.ListPreference) r8     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = "_"
            java.lang.String[] r10 = r10.split(r11)     // Catch:{ all -> 0x1238 }
            java.util.Timer r11 = new java.util.Timer     // Catch:{ all -> 0x1238 }
            r11.<init>()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.PreferenceData$2 r12 = new com.mediatek.wwtv.setting.preferences.PreferenceData$2     // Catch:{ all -> 0x1238 }
            r12.<init>(r10, r8)     // Catch:{ all -> 0x1238 }
            r13 = 500(0x1f4, double:2.47E-321)
            r11.schedule(r12, r13)     // Catch:{ all -> 0x1238 }
        L_0x02d1:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "favorite_network_select"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x02f5
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x02e7
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x02f5
        L_0x02e7:
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isFavoriteNetworkEnable()     // Catch:{ all -> 0x1238 }
            r4.setEnabled(r8)     // Catch:{ all -> 0x1238 }
        L_0x02f5:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "SETUP_date"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x11d5
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "SETUP_time"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0313
            r22 = r3
            r23 = r5
            goto L_0x11d9
        L_0x0313:
            java.lang.String r8 = "g_menu__ch_update_msg"
            java.lang.String r10 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0335
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            int r8 = r8.getCurrentTunerMode()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x032e
            if (r8 != r7) goto L_0x032a
            goto L_0x032e
        L_0x032a:
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x032e:
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x0331:
            r22 = r3
            goto L_0x11d1
        L_0x0335:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "parental_channel_schedule_block_channellist"
            boolean r8 = r8.startsWith(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x037b
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "parental_channel_schedule_block_channellist"
            int r9 = r9.length()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r4.getKey()     // Catch:{ all -> 0x1238 }
            int r10 = r10.length()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = r8.substring(r9, r10)     // Catch:{ all -> 0x1238 }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = ""
            java.lang.String r10 = "Off"
            java.lang.String r11 = "Block"
            java.lang.String[] r10 = new java.lang.String[]{r10, r11}     // Catch:{ all -> 0x1238 }
            android.content.Context r11 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.scan.EditChannel r11 = com.mediatek.wwtv.setting.scan.EditChannel.getInstance(r11)     // Catch:{ all -> 0x1238 }
            int r11 = r11.getSchBlockType((int) r8)     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x0374
            r7 = r10[r2]     // Catch:{ all -> 0x1238 }
        L_0x0373:
            goto L_0x0377
        L_0x0374:
            r7 = r10[r7]     // Catch:{ all -> 0x1238 }
            goto L_0x0373
        L_0x0377:
            r4.setSummary((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x037b:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "parental_channel_block"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x038a
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x038a:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "tkgs_setting"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x03bd
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isPreferSatMode()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "PreferenceData"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r11.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "tkgs_ispre"
            r11.append(r12)     // Catch:{ all -> 0x1238 }
            r11.append(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r11)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x03ba
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x03bd
        L_0x03ba:
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
        L_0x03bd:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "DVBS_SAT_MANUAL_TURNING"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0494
            com.mediatek.wwtv.setting.util.MenuConfigManager r8 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "g_misc__tkgs_operating_mode"
            int r8 = r8.getDefault(r10)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r10 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r10 = r10.isPreferSatMode()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r11 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.isTKGSOperator()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "satOperOnly="
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r13.append(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "mTV.isTurkeyCountry() :"
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r14 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r14 = r14.isTurkeyCountry()     // Catch:{ all -> 0x1238 }
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "mTV.isTKGSOperator(): "
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r14 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r14 = r14.isTKGSOperator()     // Catch:{ all -> 0x1238 }
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "tkgsmode="
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r13.append(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.widget.detailui.Action r12 = r1.satelliteManualTuning     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x043c
            com.mediatek.wwtv.setting.widget.detailui.Action r12 = new com.mediatek.wwtv.setting.widget.detailui.Action     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "DVBS_SAT_MANUAL_TURNING"
            android.content.Context r13 = r1.mContext     // Catch:{ all -> 0x1238 }
            r15 = 2131691313(0x7f0f0731, float:1.9011694E38)
            java.lang.String r15 = r13.getString(r15)     // Catch:{ all -> 0x1238 }
            r16 = 10004(0x2714, float:1.4019E-41)
            r17 = 10004(0x2714, float:1.4019E-41)
            r18 = 10004(0x2714, float:1.4019E-41)
            r19 = 0
            r20 = 1
            com.mediatek.wwtv.setting.widget.detailui.Action$DataType r21 = com.mediatek.wwtv.setting.widget.detailui.Action.DataType.SATELITEINFO     // Catch:{ all -> 0x1238 }
            r13 = r12
            r13.<init>(r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ all -> 0x1238 }
            r1.satelliteManualTuning = r12     // Catch:{ all -> 0x1238 }
        L_0x043c:
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.MenuDataHelper r12 = com.mediatek.wwtv.setting.util.MenuDataHelper.getInstance(r12)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.widget.detailui.Action r13 = r1.satelliteManualTuning     // Catch:{ all -> 0x1238 }
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.util.List r14 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSsatellites(r14)     // Catch:{ all -> 0x1238 }
            java.util.List r12 = r12.buildDVBSSATDetailInfo(r13, r14, r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "PreferenceData"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r14.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = "manualSatList="
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            int r15 = r12.size()     // Catch:{ all -> 0x1238 }
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)     // Catch:{ all -> 0x1238 }
            int r13 = r12.size()     // Catch:{ all -> 0x1238 }
            if (r13 <= 0) goto L_0x048a
            if (r10 == 0) goto L_0x047f
            if (r11 == 0) goto L_0x047f
            if (r8 != 0) goto L_0x047f
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "PreferenceData"
            java.lang.String r14 = "satelliteManualTuning---->false"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)     // Catch:{ all -> 0x1238 }
            goto L_0x0494
        L_0x047f:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "PreferenceData"
            java.lang.String r14 = "satelliteManualTuningtrue"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)     // Catch:{ all -> 0x1238 }
            goto L_0x0494
        L_0x048a:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "PreferenceData"
            java.lang.String r14 = "satelliteManualTuning---->false"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)     // Catch:{ all -> 0x1238 }
        L_0x0494:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "g_fusion_common__lcn"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x04b8
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x04b8
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceATV()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x04b5
            r4.setVisible(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x04b5:
            r4.setVisible(r7)     // Catch:{ all -> 0x1238 }
        L_0x04b8:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "Satellite Update"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0503
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase r8 = new com.mediatek.twoworlds.tv.MtkTvScanDvbsBase     // Catch:{ all -> 0x1238 }
            r8.<init>()     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.MtkTvScanDvbsBase$MtkTvSbDvbsBGMData r10 = new com.mediatek.twoworlds.tv.MtkTvScanDvbsBase$MtkTvSbDvbsBGMData     // Catch:{ all -> 0x1238 }
            java.util.Objects.requireNonNull(r8)     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            r8.dvbsGetSaveBgmData(r10)     // Catch:{ all -> 0x1238 }
            int r11 = r10.i4ScanTimes     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "scanTimes"
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r13.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            if (r11 <= 0) goto L_0x04f9
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.String r13 = "DVBS_SAT_UPDATE_SCAN--->true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            goto L_0x0503
        L_0x04f9:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.String r13 = "DVBS_SAT_UPDATE_SCAN--->false"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
        L_0x0503:
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEURegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0888
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "channel_scan_dvbt_full"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "channel_scan_dvbt_UPDATE"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "analog_scan"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "tv_dvbt_single_rf_scan"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "channel_scan_dvbc_fulls_operator"
            boolean r8 = r8.startsWith(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "channel_scan_dvbc_fulls"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "tv_dvbc_single_rf_scan"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "Satellite Re-scan"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "DVBS_SAT_OP"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0581
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "Satellite Update"
            boolean r8 = r8.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x07b6
        L_0x0581:
            r8 = r2
        L_0x0582:
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r10 = r10.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r8 >= r10) goto L_0x07b6
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r10 = r10.getPreference(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "channel_skip"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x062f
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "channel_sort"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x062f
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "channel_edit"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x062f
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "channel_move"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x062f
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "channel_clean"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x05cd
            goto L_0x062f
        L_0x05cd:
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "Analog ChannelFine Tune"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0602
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r11.getCurChInfoByTIF()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x05fd
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r13 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r13 = r13.getCurChInfoByTIF()     // Catch:{ all -> 0x1238 }
            boolean r13 = r13.isAnalogService()     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x05fd
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0600
        L_0x05fd:
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0600:
            goto L_0x07b1
        L_0x0602:
            java.lang.String r11 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "analog_scan"
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x07b1
            boolean r11 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.isCountryUK()     // Catch:{ all -> 0x1238 }
            if (r11 != 0) goto L_0x062a
            boolean r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0625
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.isCurrentSourceDTV()     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0625
            goto L_0x062a
        L_0x0625:
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x07b1
        L_0x062a:
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x07b1
        L_0x062f:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0797
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.is3rdTVSource()     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x0797
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.isTurkeyCountry()     // Catch:{ all -> 0x1238 }
            r13 = 2131689815(0x7f0f0157, float:1.9008656E38)
            if (r12 == 0) goto L_0x0698
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSCurrentOPStr(r12)     // Catch:{ all -> 0x1238 }
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = r14.getString(r13)     // Catch:{ all -> 0x1238 }
            boolean r12 = r12.equalsIgnoreCase(r14)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x0698
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r14.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = "mTV.isTurkeyCountry()> "
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r15 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r15 = r15.isTurkeyCountry()     // Catch:{ all -> 0x1238 }
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = "dvbs_operator_name_tivibu > "
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            android.content.Context r15 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSCurrentOPStr(r15)     // Catch:{ all -> 0x1238 }
            android.content.Context r9 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r9.getString(r13)     // Catch:{ all -> 0x1238 }
            boolean r9 = r15.equalsIgnoreCase(r9)     // Catch:{ all -> 0x1238 }
            r14.append(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r14.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r9)     // Catch:{ all -> 0x1238 }
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x06cf
        L_0x0698:
            java.lang.String r9 = "PreferenceData"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r12.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "mTV.isTurkeyCountry()> "
            r12.append(r14)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r14 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r14 = r14.isTurkeyCountry()     // Catch:{ all -> 0x1238 }
            r12.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "dvbs_operator_name_tivibu > "
            r12.append(r14)     // Catch:{ all -> 0x1238 }
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.getDVBSCurrentOPStr(r14)     // Catch:{ all -> 0x1238 }
            android.content.Context r15 = r1.mContext     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r15.getString(r13)     // Catch:{ all -> 0x1238 }
            boolean r13 = r14.equalsIgnoreCase(r13)     // Catch:{ all -> 0x1238 }
            r12.append(r13)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r12)     // Catch:{ all -> 0x1238 }
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
        L_0x06cf:
            boolean r9 = com.mediatek.wwtv.setting.base.scan.model.ScanContent.isPreferedSat()     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x06e7
            com.mediatek.wwtv.setting.util.TVContent r9 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.isTurkeyCountry()     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x06e7
            com.mediatek.wwtv.setting.util.TVContent r9 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.isTKGSOperator()     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x06e7
            r9 = r7
            goto L_0x06e8
        L_0x06e7:
            r9 = r2
        L_0x06e8:
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "isEnabled,isTKGS "
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r13.append(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x0763
            java.lang.String r12 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "channel_edit"
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x0724
            java.lang.String r12 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "channel_move"
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x0724
            java.lang.String r12 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "channel_sort"
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x0763
        L_0x0724:
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "isEnabled, mTV.getTKGSOperatorMode()"
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r14 = r1.mTV     // Catch:{ all -> 0x1238 }
            int r14 = r14.getTKGSOperatorMode()     // Catch:{ all -> 0x1238 }
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            int r12 = r12.getTKGSOperatorMode()     // Catch:{ all -> 0x1238 }
            if (r12 != 0) goto L_0x074c
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0763
        L_0x074c:
            java.lang.String r12 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "channel_edit"
            boolean r12 = r12.equals(r13)     // Catch:{ all -> 0x1238 }
            if (r12 == 0) goto L_0x0763
            com.mediatek.wwtv.setting.util.TVContent r12 = r1.mTV     // Catch:{ all -> 0x1238 }
            int r12 = r12.getTKGSOperatorMode()     // Catch:{ all -> 0x1238 }
            if (r12 != r7) goto L_0x0763
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0763:
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.MenuDataHelper r12 = com.mediatek.wwtv.setting.util.MenuDataHelper.getInstance(r12)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r13 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r13 = r13.isM7ScanMode()     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x0796
            java.lang.String r13 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "channel_move"
            boolean r13 = r13.equals(r14)     // Catch:{ all -> 0x1238 }
            if (r13 != 0) goto L_0x0789
            java.lang.String r13 = r10.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "channel_sort"
            boolean r13 = r13.equals(r14)     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x0796
        L_0x0789:
            boolean r13 = r12.isM7HasNumExceed4K()     // Catch:{ all -> 0x1238 }
            if (r13 == 0) goto L_0x0793
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0796
        L_0x0793:
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0796:
            goto L_0x079a
        L_0x0797:
            r10.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x079a:
            java.lang.String r9 = "PreferenceData"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r12.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = "isEnabled,hasActivtChannel "
            r12.append(r13)     // Catch:{ all -> 0x1238 }
            r12.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r12)     // Catch:{ all -> 0x1238 }
        L_0x07b1:
            int r8 = r8 + 1
            r9 = 2
            goto L_0x0582
        L_0x07b6:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "tveuChannel"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0852
            boolean r8 = com.mediatek.wwtv.setting.LiveTvSetting.isForChannelBootFromTVSettingPlus()     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0852
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceATV()     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x07f5
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.iCurrentInputSourceHasSignal()     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x07e5
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x07e5
            goto L_0x07f5
        L_0x07e5:
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_audio__aud_mts"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0806
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r9.removePreference(r8)     // Catch:{ all -> 0x1238 }
            goto L_0x0806
        L_0x07f5:
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_audio__aud_mts"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0806
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.tvFirstVoice     // Catch:{ all -> 0x1238 }
            r8.addPreference(r9)     // Catch:{ all -> 0x1238 }
        L_0x0806:
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_menu__audio_lang_attr"
            boolean r8 = r8.isConfigVisible(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0833
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_aud_lang__aud_language"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0821
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.tvFirstLanguageEU     // Catch:{ all -> 0x1238 }
            r8.addPreference(r9)     // Catch:{ all -> 0x1238 }
        L_0x0821:
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_aud_lang__aud_2nd_language"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0852
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.tvSecondLanguageEU     // Catch:{ all -> 0x1238 }
            r8.addPreference(r9)     // Catch:{ all -> 0x1238 }
            goto L_0x0852
        L_0x0833:
            android.support.v7.preference.PreferenceScreen r8 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_aud_lang__aud_language"
            android.support.v7.preference.Preference r8 = r8.findPreference(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0842
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r9.removePreference(r8)     // Catch:{ all -> 0x1238 }
        L_0x0842:
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "g_aud_lang__aud_2nd_language"
            android.support.v7.preference.Preference r9 = r9.findPreference(r10)     // Catch:{ all -> 0x1238 }
            r8 = r9
            if (r8 == 0) goto L_0x0852
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r9.removePreference(r8)     // Catch:{ all -> 0x1238 }
        L_0x0852:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "channel_sort"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x086a
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "channel_move"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0888
        L_0x086a:
            java.lang.String r8 = "PreferenceData"
            java.lang.String r9 = "lcn sort & move  "
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_fusion_common__lcn"
            int r8 = r8.getConfigValue(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0888
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceATVforEuPA()     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0888
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0888:
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x08cc
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_bs__bs_src"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x08cc
            java.lang.String r8 = "PreferenceData"
            java.lang.String r9 = "tunemode,hasActivtChannel "
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)     // Catch:{ all -> 0x1238 }
            r8 = r2
        L_0x08a2:
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r9 = r9.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r8 >= r9) goto L_0x08cc
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = r9.getPreference(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r9.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = "channel_customize_channel_list"
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x1238 }
            if (r10 == 0) goto L_0x08c9
            java.lang.Thread r10 = new java.lang.Thread     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.PreferenceData$3 r11 = new com.mediatek.wwtv.setting.preferences.PreferenceData$3     // Catch:{ all -> 0x1238 }
            r11.<init>(r9)     // Catch:{ all -> 0x1238 }
            r10.<init>(r11)     // Catch:{ all -> 0x1238 }
            r10.run()     // Catch:{ all -> 0x1238 }
        L_0x08c9:
            int r8 = r8 + 1
            goto L_0x08a2
        L_0x08cc:
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0954
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "channel_skip"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x091a
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "channel_sa_edit"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x08eb
            goto L_0x091a
        L_0x08eb:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "Analog ChannelFine Tune"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0997
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r8 = r8.getCurChInfoByTIF()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0915
            boolean r9 = r8.isAnalogService()     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x0915
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK     // Catch:{ all -> 0x1238 }
            int r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL     // Catch:{ all -> 0x1238 }
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r8, (int) r9, (int) r10)     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x0915
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0918
        L_0x0915:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0918:
            goto L_0x0997
        L_0x091a:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x093a
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.is3rdTVSource()     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x093a
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x093d
        L_0x093a:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x093d:
            java.lang.String r9 = "PreferenceData"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = "isSARegion,isEnabled,hasActivtChannel "
            r10.append(r11)     // Catch:{ all -> 0x1238 }
            r10.append(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)     // Catch:{ all -> 0x1238 }
            goto L_0x0997
        L_0x0954:
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEURegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0997
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "channel_skip"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0997
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.hasActiveChannel(r7)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x097e
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r9 = r9.is3rdTVSource()     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x097e
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0981
        L_0x097e:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
        L_0x0981:
            java.lang.String r9 = "PreferenceData"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = "iseuRegion,isEnabled,hasActivtChannel "
            r10.append(r11)     // Catch:{ all -> 0x1238 }
            r10.append(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)     // Catch:{ all -> 0x1238 }
        L_0x0997:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_PowerOnCh"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x09c7
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.hasActiveChannel()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x09c3
            android.content.Context r8 = r1.mContext     // Catch:{ all -> 0x1238 }
            int r8 = com.mediatek.wwtv.tvcenter.util.SaveValue.readWorldInputType(r8)     // Catch:{ all -> 0x1238 }
            r9 = 11
            if (r8 == r9) goto L_0x09bf
            r9 = 13
            if (r8 == r9) goto L_0x09bf
            r9 = 12
            if (r8 != r9) goto L_0x09c2
        L_0x09bf:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
        L_0x09c2:
            goto L_0x09e5
        L_0x09c3:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x09e5
        L_0x09c7:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_system_information"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x09e5
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceTv()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x09e0
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x09e0:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x09e5:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_timer2"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a0a
            com.mediatek.wwtv.setting.util.MenuConfigManager r8 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_timer__timer_off"
            int r8 = r8.getDefault(r9)     // Catch:{ all -> 0x1238 }
            r3 = r8
            if (r3 == r7) goto L_0x0a04
            r8 = 2
            if (r3 != r8) goto L_0x0a00
            goto L_0x0a04
        L_0x0a00:
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0a07
        L_0x0a04:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
        L_0x0a07:
            r14 = r2
            goto L_0x11f8
        L_0x0a0a:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SUBTITLE_GROUP"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a2e
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a29
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceHDMI()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a29
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0a29:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0a2e:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_teletext"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a52
            boolean r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a4d
            com.mediatek.wwtv.setting.util.TVContent r8 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.isCurrentSourceHDMI()     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a4d
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0a4d:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0a52:
            java.lang.String r8 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_power_onchannel"
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a75
            android.content.Context r8 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r8 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r8)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_select_mode"
            int r8 = r8.readValue(r9)     // Catch:{ all -> 0x1238 }
            r3 = r8
            if (r3 != 0) goto L_0x0a71
            r4.setEnabled(r2)     // Catch:{ all -> 0x1238 }
            goto L_0x0a07
        L_0x0a71:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0a07
        L_0x0a75:
            boolean r8 = r4 instanceof com.mediatek.wwtv.setting.preferences.ProgressPreference     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0aa5
            r7 = r4
            com.mediatek.wwtv.setting.preferences.ProgressPreference r7 = (com.mediatek.wwtv.setting.preferences.ProgressPreference) r7     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "vSignalQualityProgress"
            java.lang.String r9 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 != 0) goto L_0x0331
            java.lang.String r8 = "vSignalProgress"
            java.lang.String r9 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r8 = r8.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x0a96
            goto L_0x0331
        L_0x0a96:
            com.mediatek.wwtv.setting.util.MenuConfigManager r8 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r7.getKey()     // Catch:{ all -> 0x1238 }
            int r8 = r8.getDefault(r9)     // Catch:{ all -> 0x1238 }
            r7.setCurrentValue(r8)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0aa5:
            boolean r8 = r4 instanceof android.support.v7.preference.ListPreference     // Catch:{ all -> 0x1238 }
            if (r8 == 0) goto L_0x1182
            java.lang.String r8 = "PreferenceData"
            java.lang.String r9 = "ListPreference"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)     // Catch:{ all -> 0x1238 }
            r8 = r4
            android.support.v7.preference.ListPreference r8 = (android.support.v7.preference.ListPreference) r8     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "PreferenceData"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = "tmp.getKey(): "
            r10.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = r8.getKey()     // Catch:{ all -> 0x1238 }
            r10.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "SETUP_time_zone"
            boolean r9 = r9.equals(r10)     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x0b1b
            android.content.Context r9 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r9 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "Zone_time"
            boolean r9 = r9.readBooleanValue(r10)     // Catch:{ all -> 0x1238 }
            if (r9 != r7) goto L_0x0aee
            java.lang.String r7 = "As Broadcast"
            r8.setSummary(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0aee:
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.GetTimeZone r7 = com.mediatek.wwtv.setting.util.GetTimeZone.getInstance(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String[] r10 = r7.generateTimeZonesArray()     // Catch:{ all -> 0x1238 }
            int r11 = r7.getCurrentTimeZoneIndex()     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "timezones[defzone]: "
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r14 = r10[r11]     // Catch:{ all -> 0x1238 }
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            r12 = r10[r11]     // Catch:{ all -> 0x1238 }
            r8.setSummary(r12)     // Catch:{ all -> 0x1238 }
            goto L_0x0331
        L_0x0b1b:
            java.lang.String r9 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "SETUP_regionSetting_philippines"
            boolean r9 = r9.contains(r10)     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x0c01
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r7 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_regionSetting_philippines"
            int r7 = r7.readValue(r9)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r9 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r9 = r9.regionSettingScreen     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r11 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.isEcuadorCountry()     // Catch:{ all -> 0x1238 }
            r12 = r2
        L_0x0b40:
            int r13 = r9.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r12 >= r13) goto L_0x0bfb
            android.support.v7.preference.Preference r13 = r9.getPreference(r12)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.ListPreference r13 = (android.support.v7.preference.ListPreference) r13     // Catch:{ all -> 0x1238 }
            if (r12 != r7) goto L_0x0ba9
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r14 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = "SETUP_regionSetting_select"
            int r14 = r14.readValue(r15)     // Catch:{ all -> 0x1238 }
            int r15 = com.mediatek.wwtv.setting.util.RegionConst.getEcuadorCityArray(r7)     // Catch:{ all -> 0x1238 }
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r2 = r2.getResources()     // Catch:{ all -> 0x1238 }
            java.lang.String[] r2 = r2.getStringArray(r15)     // Catch:{ all -> 0x1238 }
            r10 = r2[r14]     // Catch:{ all -> 0x1238 }
            r13.setSummary(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = java.lang.String.valueOf(r14)     // Catch:{ all -> 0x1238 }
            r13.setValue(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "PreferenceData"
            r22 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r3.<init>()     // Catch:{ all -> 0x1238 }
            r23 = r5
            java.lang.String r5 = "onItemPosition:"
            r3.append(r5)     // Catch:{ all -> 0x1238 }
            r3.append(r12)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = ",itemPosition:"
            r3.append(r5)     // Catch:{ all -> 0x1238 }
            r3.append(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = ",summary:"
            r3.append(r5)     // Catch:{ all -> 0x1238 }
            r5 = r2[r14]     // Catch:{ all -> 0x1238 }
            r3.append(r5)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = ",selectPosition:"
            r3.append(r5)     // Catch:{ all -> 0x1238 }
            r3.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r3)     // Catch:{ all -> 0x1238 }
            goto L_0x0bf2
        L_0x0ba9:
            r22 = r3
            r23 = r5
            int r2 = com.mediatek.wwtv.setting.util.RegionConst.getEcuadorCityArray(r12)     // Catch:{ all -> 0x1238 }
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r3 = r3.getResources()     // Catch:{ all -> 0x1238 }
            java.lang.String[] r3 = r3.getStringArray(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = ""
            r13.setSummary(r5)     // Catch:{ all -> 0x1238 }
            r5 = -1
            java.lang.String r10 = java.lang.String.valueOf(r5)     // Catch:{ all -> 0x1238 }
            r13.setValue(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "PreferenceData"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "onItemPosition:"
            r10.append(r14)     // Catch:{ all -> 0x1238 }
            r10.append(r12)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = ",itemPosition:"
            r10.append(r14)     // Catch:{ all -> 0x1238 }
            r10.append(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = ",summary:"
            r10.append(r14)     // Catch:{ all -> 0x1238 }
            r14 = 0
            r15 = r3[r14]     // Catch:{ all -> 0x1238 }
            r10.append(r15)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r10)     // Catch:{ all -> 0x1238 }
            r2 = r3
        L_0x0bf2:
            int r12 = r12 + 1
            r3 = r22
            r5 = r23
            r2 = 0
            goto L_0x0b40
        L_0x0bfb:
            r22 = r3
            r23 = r5
            monitor-exit(r26)
            return
        L_0x0c01:
            r22 = r3
            r23 = r5
            java.lang.String r2 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SETUP_regionSetting_LUZON"
            boolean r2 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r2 != 0) goto L_0x107f
            java.lang.String r2 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SETUP_regionSetting_VISAYAS"
            boolean r2 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r2 != 0) goto L_0x107f
            java.lang.String r2 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SETUP_regionSetting_MINDANAO"
            boolean r2 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r2 == 0) goto L_0x0c2b
            goto L_0x107f
        L_0x0c2b:
            com.mediatek.wwtv.setting.util.MenuConfigManager r2 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r2 = r2.getDefault(r3)     // Catch:{ all -> 0x1238 }
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.PreferenceUtil r3 = com.mediatek.wwtv.setting.preferences.PreferenceUtil.getInstance(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_aud_lang__aud_language"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0c54
            com.mediatek.wwtv.setting.util.LanguageUtil r5 = r3.mOsdLanguage     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getAudioLanguage(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0c54:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "UNDEFINE_parental_input_block_source"
            boolean r5 = r5.startsWith(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0c62
            goto L_0x0f88
        L_0x0c62:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_subtitle__subtitle_lang"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0c99
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0c89
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.isCurrentSourceATV()     // Catch:{ all -> 0x1238 }
            if (r5 != 0) goto L_0x0c84
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.isCurrentSourceComposite()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0c89
        L_0x0c84:
            r5 = 0
            r8.setEnabled(r5)     // Catch:{ all -> 0x1238 }
            goto L_0x0c8c
        L_0x0c89:
            r8.setEnabled(r7)     // Catch:{ all -> 0x1238 }
        L_0x0c8c:
            com.mediatek.wwtv.setting.util.LanguageUtil r5 = r3.mOsdLanguage     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getSubtitleLanguage(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0c99:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_subtitle__subtitle_lang_2nd"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 != 0) goto L_0x0f47
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_subtitle__subtitle_attr"
            boolean r5 = r5.startsWith(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0cb3
            goto L_0x0f47
        L_0x0cb3:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_subtitle__subtitle_enable"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0cd8
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0cd3
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.isCurrentSourceDTV()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0cd3
            r5 = 0
            r8.setEnabled(r5)     // Catch:{ all -> 0x1238 }
            goto L_0x0f88
        L_0x0cd3:
            r8.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0f88
        L_0x0cd8:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_auto_syn"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0cf3
            android.content.Context r5 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r5 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r5)     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "SETUP_auto_syn"
            int r5 = r5.readValue(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0cf3:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_video__screen_mode"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0d10
            com.mediatek.wwtv.setting.util.MenuConfigManager r5 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.MenuConfigManager r7 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String[] r7 = r7.getScreenModeList()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_video__screen_mode"
            int r5 = r5.getScreenMode(r7, r9)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0d10:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_select_mode"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0d2b
            android.content.Context r5 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r5 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r5)     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "SETUP_select_mode"
            int r5 = r5.readValue(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0d2b:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_sleep_timer"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0d3c
            r1.showSleepTimerInfo(r8)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x0d3c:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "parental_channel_schedule_block_MOde"
            boolean r5 = r5.contains(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0dfb
            int r5 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.block     // Catch:{ all -> 0x1238 }
            r2 = r5
            r5 = 4
            r9 = 3
            switch(r2) {
                case 0: goto L_0x0dc3;
                case 1: goto L_0x0d8a;
                case 2: goto L_0x0d52;
                default: goto L_0x0d50;
            }     // Catch:{ all -> 0x1238 }
        L_0x0d50:
            goto L_0x0f88
        L_0x0d52:
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 0
            android.support.v7.preference.Preference r10 = r10.getPreference(r11)     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r11 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            r12 = 2
            r11 = r11[r12]     // Catch:{ all -> 0x1238 }
            r10.setSummary((java.lang.CharSequence) r11)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r10 = r10.getPreference(r7)     // Catch:{ all -> 0x1238 }
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 2
            android.support.v7.preference.Preference r10 = r10.getPreference(r11)     // Catch:{ all -> 0x1238 }
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = r10.getPreference(r9)     // Catch:{ all -> 0x1238 }
            r9.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r5 = r9.getPreference(r5)     // Catch:{ all -> 0x1238 }
            r5.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0f88
        L_0x0d8a:
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 0
            android.support.v7.preference.Preference r10 = r10.getPreference(r11)     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r11 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            r11 = r11[r7]     // Catch:{ all -> 0x1238 }
            r10.setSummary((java.lang.CharSequence) r11)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r10 = r10.getPreference(r7)     // Catch:{ all -> 0x1238 }
            r11 = 0
            r10.setEnabled(r11)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 2
            android.support.v7.preference.Preference r10 = r10.getPreference(r11)     // Catch:{ all -> 0x1238 }
            r10.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r9 = r10.getPreference(r9)     // Catch:{ all -> 0x1238 }
            r10 = 0
            r9.setEnabled(r10)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r5 = r9.getPreference(r5)     // Catch:{ all -> 0x1238 }
            r5.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0f88
        L_0x0dc3:
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 0
            android.support.v7.preference.Preference r10 = r10.getPreference(r11)     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r12 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            r12 = r12[r11]     // Catch:{ all -> 0x1238 }
            r10.setSummary((java.lang.CharSequence) r12)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r10 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r7 = r10.getPreference(r7)     // Catch:{ all -> 0x1238 }
            r10 = 0
            r7.setEnabled(r10)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r7 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r11 = 2
            android.support.v7.preference.Preference r7 = r7.getPreference(r11)     // Catch:{ all -> 0x1238 }
            r7.setEnabled(r10)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r7 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r7 = r7.getPreference(r9)     // Catch:{ all -> 0x1238 }
            r7.setEnabled(r10)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r7 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r5 = r7.getPreference(r5)     // Catch:{ all -> 0x1238 }
            r5.setEnabled(r10)     // Catch:{ all -> 0x1238 }
            goto L_0x0f88
        L_0x0dfb:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "g_aud_lang__aud_2nd_language"
            boolean r5 = r5.startsWith(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0e14
            com.mediatek.wwtv.setting.util.LanguageUtil r5 = r3.mOsdLanguage     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getAudioLanguage(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0e14:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "parental_open_vchip_level"
            boolean r5 = r5.startsWith(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0eab
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "parental_open_vchip_level"
            int r7 = r7.length()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r9 = r9.length()     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = r5.substring(r7, r9)     // Catch:{ all -> 0x1238 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r7 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r9 = r1.mTV     // Catch:{ all -> 0x1238 }
            int r10 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.reginIndex     // Catch:{ all -> 0x1238 }
            int r11 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.dimIndex     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase r9 = r9.getNewOpenVchipSetting(r10, r11)     // Catch:{ all -> 0x1238 }
            byte[] r9 = r9.getLvlBlockData()     // Catch:{ all -> 0x1238 }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ all -> 0x1238 }
            r10.<init>()     // Catch:{ all -> 0x1238 }
            r11 = 0
        L_0x0e52:
            android.support.v7.preference.PreferenceScreen r12 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r12 = r12.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r11 >= r12) goto L_0x0e66
            android.support.v7.preference.PreferenceScreen r12 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r12 = r12.getPreference(r11)     // Catch:{ all -> 0x1238 }
            r10.add(r12)     // Catch:{ all -> 0x1238 }
            int r11 = r11 + 1
            goto L_0x0e52
        L_0x0e66:
            r11 = 0
        L_0x0e67:
            int r12 = r9.length     // Catch:{ all -> 0x1238 }
            if (r11 >= r12) goto L_0x0e8d
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r13.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "print levelBlicks[ "
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            r13.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "]="
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            byte r14 = r9[r11]     // Catch:{ all -> 0x1238 }
            r13.append(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)     // Catch:{ all -> 0x1238 }
            int r11 = r11 + 1
            goto L_0x0e67
        L_0x0e8d:
            android.content.Context r11 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.TVContent r11 = com.mediatek.wwtv.setting.util.TVContent.getInstance(r11)     // Catch:{ all -> 0x1238 }
            int r12 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.reginIndex     // Catch:{ all -> 0x1238 }
            int r13 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.dimIndex     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase r11 = r11.getNewOpenVchipSetting(r12, r13)     // Catch:{ all -> 0x1238 }
            byte[] r12 = r11.getLvlBlockData()     // Catch:{ all -> 0x1238 }
            byte r13 = r12[r5]     // Catch:{ all -> 0x1238 }
            r8.setValueIndex(r13)     // Catch:{ all -> 0x1238 }
            r14 = r7[r13]     // Catch:{ all -> 0x1238 }
            r8.setSummary(r14)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x0eab:
            java.lang.String r5 = "g_bs__bs_src"
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f05
            com.mediatek.wwtv.setting.util.MenuConfigManager r5 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getDefault(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "PreferenceData"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r9.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "TUNER_MODE value="
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            r9.append(r5)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r7 = r8.getEntryValues()     // Catch:{ all -> 0x1238 }
            java.lang.CharSequence[] r9 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            r10 = 0
        L_0x0ee0:
            int r11 = r7.length     // Catch:{ all -> 0x1238 }
            if (r10 >= r11) goto L_0x0f04
            r11 = r7[r10]     // Catch:{ all -> 0x1238 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r12.<init>()     // Catch:{ all -> 0x1238 }
            r12.append(r5)     // Catch:{ all -> 0x1238 }
            java.lang.String r13 = ""
            r12.append(r13)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.equals(r12)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x0f01
            r11 = r9[r10]     // Catch:{ all -> 0x1238 }
            r8.setSummary(r11)     // Catch:{ all -> 0x1238 }
        L_0x0f01:
            int r10 = r10 + 1
            goto L_0x0ee0
        L_0x0f04:
            goto L_0x0f37
        L_0x0f05:
            java.lang.String r5 = "g_misc__ch_list_type"
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f3b
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "g_misc__ch_list_type"
            boolean r5 = r5.isConfigVisible(r7)     // Catch:{ all -> 0x1238 }
            com.mediatek.twoworlds.tv.MtkTvConfig r7 = com.mediatek.twoworlds.tv.MtkTvConfig.getInstance()     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "g_misc__ch_list_slot"
            java.lang.String r7 = r7.getConfigString(r9)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f2b
            boolean r9 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x1238 }
            if (r9 == 0) goto L_0x0f37
        L_0x0f2b:
            java.lang.String r9 = "PreferenceData"
            java.lang.String r10 = "MenuConfigManager.CHANNEL_LIST_TYPE remove"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r9 = r1.mScreen     // Catch:{ all -> 0x1238 }
            r9.removePreference(r8)     // Catch:{ all -> 0x1238 }
        L_0x0f37:
            r3 = r2
            r14 = 0
            goto L_0x11f8
        L_0x0f3b:
            com.mediatek.wwtv.setting.util.MenuConfigManager r5 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getDefault(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0f47:
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f62
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.isCurrentSourceATV()     // Catch:{ all -> 0x1238 }
            if (r5 != 0) goto L_0x0f5d
            com.mediatek.wwtv.setting.util.TVContent r5 = r1.mTV     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.isCurrentSourceComposite()     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f62
        L_0x0f5d:
            r5 = 0
            r8.setEnabled(r5)     // Catch:{ all -> 0x1238 }
            goto L_0x0f65
        L_0x0f62:
            r8.setEnabled(r7)     // Catch:{ all -> 0x1238 }
        L_0x0f65:
            java.lang.String r5 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "g_subtitle__subtitle_attr"
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f7d
            com.mediatek.wwtv.setting.util.MenuConfigManager r5 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getDefault(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
            goto L_0x0f88
        L_0x0f7d:
            com.mediatek.wwtv.setting.util.LanguageUtil r5 = r3.mOsdLanguage     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            int r5 = r5.getSubtitleLanguage(r7)     // Catch:{ all -> 0x1238 }
            r2 = r5
        L_0x0f88:
            java.lang.String r5 = "g_video__vid_vga_mode"
            java.lang.String r7 = r8.getKey()     // Catch:{ all -> 0x1238 }
            boolean r5 = r5.equals(r7)     // Catch:{ all -> 0x1238 }
            if (r5 == 0) goto L_0x0f96
            int r2 = r2 + -1
        L_0x0f96:
            if (r2 >= 0) goto L_0x0f9a
            r5 = 0
            goto L_0x0f9b
        L_0x0f9a:
            r5 = r2
        L_0x0f9b:
            r2 = r5
            java.lang.CharSequence[] r5 = r8.getEntries()     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = "PreferenceData"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r9.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "=== index"
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            r9.append(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "  tmp.getKey():"
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = r8.getKey()     // Catch:{ all -> 0x1238 }
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "  entries:"
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            int r10 = r5.length     // Catch:{ all -> 0x1238 }
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)     // Catch:{ all -> 0x1238 }
            int r7 = r5.length     // Catch:{ all -> 0x1238 }
            if (r7 > r2) goto L_0x0ffa
            java.lang.String r7 = "g_audio__aud_mts"
            java.lang.String r9 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r7 != 0) goto L_0x0ffa
            java.lang.String r7 = "PreferenceData"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r9.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = "error."
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            r9.append(r4)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = ","
            r9.append(r10)     // Catch:{ all -> 0x1238 }
            r9.append(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r9)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x0ffa:
            java.lang.String r7 = "g_record_setting_power_setting"
            java.lang.String r9 = r8.getKey()     // Catch:{ all -> 0x1238 }
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r7 == 0) goto L_0x1015
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.PreferenceUtil r7 = com.mediatek.wwtv.setting.preferences.PreferenceUtil.getInstance(r7)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.util.MenuConfigManager r7 = r7.mConfigManager     // Catch:{ all -> 0x1238 }
            android.content.Context r9 = r1.mContext     // Catch:{ all -> 0x1238 }
            int r7 = r7.getDefaultPowerSetting(r9)     // Catch:{ all -> 0x1238 }
            r2 = r7
        L_0x1015:
            java.lang.String r7 = "g_audio__aud_mts"
            java.lang.String r9 = r4.getKey()     // Catch:{ all -> 0x1238 }
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r7 == 0) goto L_0x1078
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.nav.util.SundryImplement r7 = com.mediatek.wwtv.tvcenter.nav.util.SundryImplement.getInstanceNavSundryImplement(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = r7.getMtsSummaryByAcfgValue(r2)     // Catch:{ all -> 0x1238 }
            if (r9 != 0) goto L_0x1045
            r10 = r4
            android.support.v7.preference.ListPreference r10 = (android.support.v7.preference.ListPreference) r10     // Catch:{ all -> 0x1238 }
            r11 = 0
            java.lang.String[] r12 = new java.lang.String[r11]     // Catch:{ all -> 0x1238 }
            r10.setEntries((java.lang.CharSequence[]) r12)     // Catch:{ all -> 0x1238 }
            r10 = r4
            android.support.v7.preference.ListPreference r10 = (android.support.v7.preference.ListPreference) r10     // Catch:{ all -> 0x1238 }
            java.lang.String[] r12 = new java.lang.String[r11]     // Catch:{ all -> 0x1238 }
            r10.setEntryValues((java.lang.CharSequence[]) r12)     // Catch:{ all -> 0x1238 }
            java.lang.String r10 = ""
            r8.setSummary(r10)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x1045:
            java.lang.String r10 = ""
            boolean r10 = r10.equals(r9)     // Catch:{ all -> 0x1238 }
            if (r10 == 0) goto L_0x105e
            com.mediatek.wwtv.setting.util.MenuConfigManager r10 = r1.mConfigManager     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = r4.getKey()     // Catch:{ all -> 0x1238 }
            r12 = 0
            r10.setValue((java.lang.String) r11, (int) r12)     // Catch:{ all -> 0x1238 }
            r10 = r5[r12]     // Catch:{ all -> 0x1238 }
            r8.setSummary(r10)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x105e:
            java.lang.String[] r10 = r7.getAllMtsModes()     // Catch:{ all -> 0x1238 }
            r11 = r4
            android.support.v7.preference.ListPreference r11 = (android.support.v7.preference.ListPreference) r11     // Catch:{ all -> 0x1238 }
            r11.setEntries((java.lang.CharSequence[]) r10)     // Catch:{ all -> 0x1238 }
            r11 = r4
            android.support.v7.preference.ListPreference r11 = (android.support.v7.preference.ListPreference) r11     // Catch:{ all -> 0x1238 }
            int r12 = r10.length     // Catch:{ all -> 0x1238 }
            java.lang.String[] r12 = com.mediatek.wwtv.setting.preferences.PreferenceUtil.getCharSequence(r12)     // Catch:{ all -> 0x1238 }
            r11.setEntryValues((java.lang.CharSequence[]) r12)     // Catch:{ all -> 0x1238 }
            r8.setSummary(r9)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x1078:
            r7 = r5[r2]     // Catch:{ all -> 0x1238 }
            r8.setSummary(r7)     // Catch:{ all -> 0x1238 }
            goto L_0x0f37
        L_0x107f:
            r2 = 0
            java.lang.String r3 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "SETUP_regionSetting_LUZON"
            boolean r3 = r3.contains(r5)     // Catch:{ all -> 0x1238 }
            if (r3 == 0) goto L_0x1090
            int[] r3 = com.mediatek.wwtv.setting.util.RegionConst.phiProsCityLuzong     // Catch:{ all -> 0x1238 }
            r2 = r3
            goto L_0x10a3
        L_0x1090:
            java.lang.String r3 = r8.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "SETUP_regionSetting_VISAYAS"
            boolean r3 = r3.contains(r5)     // Catch:{ all -> 0x1238 }
            if (r3 == 0) goto L_0x10a0
            int[] r3 = com.mediatek.wwtv.setting.util.RegionConst.phiProsCityVisayas     // Catch:{ all -> 0x1238 }
            r2 = r3
            goto L_0x10a3
        L_0x10a0:
            int[] r3 = com.mediatek.wwtv.setting.util.RegionConst.phiProsCityMindanao     // Catch:{ all -> 0x1238 }
            r2 = r3
        L_0x10a3:
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r3 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "SETUP_regionSetting_philippines"
            int r3 = r3.readValue(r5)     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen r5 = com.mediatek.wwtv.setting.preferences.SettingsPreferenceScreen.getInstance()     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.PreferenceScreen r5 = r5.regionSettingScreen     // Catch:{ all -> 0x1238 }
            android.content.Context r7 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r7 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r7)     // Catch:{ all -> 0x1238 }
            java.lang.String r9 = "SETUP_regionSetting"
            java.lang.String r7 = r7.readStrValue(r9)     // Catch:{ all -> 0x1238 }
            r9 = 0
        L_0x10c2:
            int r10 = r5.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r9 >= r10) goto L_0x117e
            android.support.v7.preference.Preference r10 = r5.getPreference(r9)     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.ListPreference r10 = (android.support.v7.preference.ListPreference) r10     // Catch:{ all -> 0x1238 }
            if (r9 != r3) goto L_0x1131
            java.lang.String r11 = r8.getKey()     // Catch:{ all -> 0x1238 }
            boolean r11 = r11.contains(r7)     // Catch:{ all -> 0x1238 }
            if (r11 == 0) goto L_0x1131
            android.content.Context r11 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r11 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "SETUP_regionSetting_select"
            int r11 = r11.readValue(r12)     // Catch:{ all -> 0x1238 }
            r12 = r2[r3]     // Catch:{ all -> 0x1238 }
            android.content.Context r13 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r13 = r13.getResources()     // Catch:{ all -> 0x1238 }
            java.lang.String[] r13 = r13.getStringArray(r12)     // Catch:{ all -> 0x1238 }
            r14 = r13[r11]     // Catch:{ all -> 0x1238 }
            r10.setSummary(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = java.lang.String.valueOf(r11)     // Catch:{ all -> 0x1238 }
            r10.setValue(r14)     // Catch:{ all -> 0x1238 }
            java.lang.String r14 = "PreferenceData"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r15.<init>()     // Catch:{ all -> 0x1238 }
            r24 = r2
            java.lang.String r2 = "onItemPosition:"
            r15.append(r2)     // Catch:{ all -> 0x1238 }
            r15.append(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = ",itemPosition:"
            r15.append(r2)     // Catch:{ all -> 0x1238 }
            r15.append(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = ",summary:"
            r15.append(r2)     // Catch:{ all -> 0x1238 }
            r2 = r13[r11]     // Catch:{ all -> 0x1238 }
            r15.append(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = ",selectPosition:"
            r15.append(r2)     // Catch:{ all -> 0x1238 }
            r15.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = r15.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r14, r2)     // Catch:{ all -> 0x1238 }
            goto L_0x1178
        L_0x1131:
            r24 = r2
            int r2 = com.mediatek.wwtv.setting.util.RegionConst.getEcuadorCityArray(r9)     // Catch:{ all -> 0x1238 }
            android.content.Context r11 = r1.mContext     // Catch:{ all -> 0x1238 }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x1238 }
            java.lang.String[] r11 = r11.getStringArray(r2)     // Catch:{ all -> 0x1238 }
            r13 = r11
            java.lang.String r11 = ""
            r10.setSummary(r11)     // Catch:{ all -> 0x1238 }
            r11 = -1
            java.lang.String r12 = java.lang.String.valueOf(r11)     // Catch:{ all -> 0x1238 }
            r10.setValue(r12)     // Catch:{ all -> 0x1238 }
            java.lang.String r12 = "PreferenceData"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r14.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = "onItemPosition:"
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            r14.append(r9)     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = ",itemPosition:"
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            r14.append(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r15 = ",summary:"
            r14.append(r15)     // Catch:{ all -> 0x1238 }
            r15 = 0
            r11 = r13[r15]     // Catch:{ all -> 0x1238 }
            r14.append(r11)     // Catch:{ all -> 0x1238 }
            java.lang.String r11 = r14.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r11)     // Catch:{ all -> 0x1238 }
        L_0x1178:
            int r9 = r9 + 1
            r2 = r24
            goto L_0x10c2
        L_0x117e:
            r24 = r2
            monitor-exit(r26)
            return
        L_0x1182:
            r22 = r3
            r23 = r5
            boolean r2 = r4 instanceof android.support.v7.preference.Preference     // Catch:{ all -> 0x1238 }
            if (r2 == 0) goto L_0x11d1
            java.lang.String r2 = r4.getKey()     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SETUP_start_date"
            boolean r3 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r3 != 0) goto L_0x11ae
            java.lang.String r3 = "SETUP_start_time"
            boolean r3 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r3 != 0) goto L_0x11ae
            java.lang.String r3 = "SETUP_end_time"
            boolean r3 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r3 != 0) goto L_0x11ae
            java.lang.String r3 = "SETUP_end_date"
            boolean r3 = r2.contains(r3)     // Catch:{ all -> 0x1238 }
            if (r3 == 0) goto L_0x11d1
        L_0x11ae:
            android.content.Context r3 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r3 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = r3.readStrValue(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r5 = "PreferenceData"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1238 }
            r7.<init>()     // Catch:{ all -> 0x1238 }
            java.lang.String r8 = "dateTime: "
            r7.append(r8)     // Catch:{ all -> 0x1238 }
            r7.append(r3)     // Catch:{ all -> 0x1238 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r7)     // Catch:{ all -> 0x1238 }
            r4.setSummary((java.lang.CharSequence) r3)     // Catch:{ all -> 0x1238 }
        L_0x11d1:
            r3 = r22
            r14 = 0
            goto L_0x11f8
        L_0x11d5:
            r22 = r3
            r23 = r5
        L_0x11d9:
            r1.isNeedRefreshTime = r7     // Catch:{ all -> 0x1238 }
            android.content.Context r2 = r1.mContext     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.tvcenter.util.SaveValue r2 = com.mediatek.wwtv.tvcenter.util.SaveValue.getInstance(r2)     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SETUP_auto_syn"
            int r2 = r2.readValue(r3)     // Catch:{ all -> 0x1238 }
            if (r2 == r7) goto L_0x11f3
            r3 = 2
            if (r2 != r3) goto L_0x11ed
            goto L_0x11f3
        L_0x11ed:
            r4.setEnabled(r7)     // Catch:{ all -> 0x1238 }
            r14 = 0
            goto L_0x11f7
        L_0x11f3:
            r14 = 0
            r4.setEnabled(r14)     // Catch:{ all -> 0x1238 }
        L_0x11f7:
            r3 = r2
        L_0x11f8:
            int r0 = r0 + 1
            r2 = r14
            goto L_0x0058
        L_0x11fd:
            r14 = r2
            r22 = r3
            java.lang.String r0 = "SUB_factory_video"
            android.support.v7.preference.PreferenceScreen r2 = r1.mScreen     // Catch:{ all -> 0x1238 }
            java.lang.String r2 = r2.getKey()     // Catch:{ all -> 0x1238 }
            boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x1238 }
            if (r0 == 0) goto L_0x1236
        L_0x120f:
            r0 = r14
            android.support.v7.preference.PreferenceScreen r2 = r1.mScreen     // Catch:{ all -> 0x1238 }
            int r2 = r2.getPreferenceCount()     // Catch:{ all -> 0x1238 }
            if (r0 >= r2) goto L_0x1236
            android.support.v7.preference.PreferenceScreen r2 = r1.mScreen     // Catch:{ all -> 0x1238 }
            android.support.v7.preference.Preference r2 = r2.getPreference(r0)     // Catch:{ all -> 0x1238 }
            java.lang.String r3 = "SUB_FV_AUTOCOLOR"
            java.lang.String r4 = r2.getKey()     // Catch:{ all -> 0x1238 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x1238 }
            if (r3 == 0) goto L_0x1233
            android.support.v7.preference.Preference$OnPreferenceClickListener r3 = r2.getOnPreferenceClickListener()     // Catch:{ all -> 0x1238 }
            com.mediatek.wwtv.setting.view.FacVideo r3 = (com.mediatek.wwtv.setting.view.FacVideo) r3     // Catch:{ all -> 0x1238 }
            r3.addListener()     // Catch:{ all -> 0x1238 }
        L_0x1233:
            int r14 = r0 + 1
            goto L_0x120f
        L_0x1236:
            monitor-exit(r26)
            return
        L_0x1238:
            r0 = move-exception
            monitor-exit(r26)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.preferences.PreferenceData.resume():void");
    }

    public int getIndexByLeftTime(Long timeLeft) {
        if (timeLeft.longValue() < 10) {
            return 0;
        }
        if (timeLeft.longValue() < 20) {
            return 1;
        }
        if (timeLeft.longValue() < 30) {
            return 2;
        }
        if (timeLeft.longValue() < 40) {
            return 3;
        }
        if (timeLeft.longValue() < 50) {
            return 4;
        }
        if (timeLeft.longValue() < 60) {
            return 5;
        }
        if (timeLeft.longValue() < 90) {
            return 6;
        }
        if (timeLeft.longValue() < 120) {
            return 7;
        }
        return 8;
    }

    public void showSleepTimerInfo(Preference pref) {
        if ("SETUP_sleep_timer".equals(pref.getKey())) {
            int remaintime = this.mTV.getSleepTimerRemaining();
            MtkLog.d(TAG, "remaintime==" + remaintime);
            Long timeLeft = 0L;
            if (remaintime != 0) {
                if (remaintime % DMNativeDaemonConnector.ResponseCode.UnsolicitedInformational == 0) {
                    timeLeft = Long.valueOf(((long) this.mTV.getSleepTimerRemaining()) / 60);
                } else {
                    timeLeft = Long.valueOf((((long) this.mTV.getSleepTimerRemaining()) / 60) + 1);
                }
            }
            String[] optionTiemSleep = addSuffix(this.mContext.getResources().getStringArray(R.array.menu_setup_sleep_timer_array), R.string.menu_arrays_Minutes_xx);
            if (timeLeft.longValue() > 0) {
                Matcher matcher = Pattern.compile("^\\d*\\d").matcher(optionTiemSleep[optionTiemSleep.length - 1]);
                pref.setSummary((CharSequence) matcher.replaceFirst(String.valueOf(timeLeft)));
                MtkLog.d(TAG, "leftTimer------------------------>" + matcher.replaceFirst(String.valueOf(timeLeft)));
                return;
            }
            pref.setSummary((CharSequence) optionTiemSleep[0]);
            MtkLog.d(TAG, "not  timeLeft>0");
            return;
        }
        MtkLog.d(TAG, "not sleeptimer");
    }

    private String[] addSuffix(String[] str, int strId) {
        if (this.mContext == null) {
            return null;
        }
        for (int i = 0; i < str.length; i++) {
            String numStr = str[i];
            int num = (numStr == null || numStr.isEmpty() || !numStr.matches("^[0-9]*$")) ? Integer.MAX_VALUE : Integer.parseInt(numStr);
            if (num != Integer.MAX_VALUE) {
                str[i] = this.mContext.getResources().getString(strId, new Object[]{Integer.valueOf(num)});
            }
        }
        return str;
    }

    public void handleSleepTimerChange(ListPreference tmp, int value) {
        int lastValue;
        int remaintime = this.mTV.getSleepTimerRemaining();
        Long timeLeft = 0L;
        if (remaintime != 0) {
            timeLeft = Long.valueOf((((long) this.mTV.getSleepTimerRemaining()) / 60) + 1);
        }
        if (timeLeft.longValue() > 0) {
            lastValue = getIndexByLeftTime(timeLeft);
        } else {
            lastValue = 0;
        }
        if (remaintime != 0) {
            Long timeLeft2 = Long.valueOf((((long) this.mTV.getSleepTimerRemaining()) / 60) + 1);
        }
        MtkLog.d(TAG, "handleSleepTimerChange value:" + value + "  lastValue:" + lastValue);
        if (value > lastValue) {
            if (lastValue == 0 && value == tmp.getEntries().length - 1) {
                this.mTV.setSleepTimer(false);
            } else {
                int times = value - lastValue;
                while (times > 0) {
                    times--;
                    this.mTV.setSleepTimer(true);
                }
            }
        } else if (value == 0 && lastValue == tmp.getEntries().length - 1) {
            this.mTV.setSleepTimer(true);
        } else {
            int times2 = lastValue - value;
            while (times2 > 0) {
                times2--;
                this.mTV.setSleepTimer(false);
            }
        }
        MenuDataHelper.getInstance(this.mContext).startSleepTimerTask(tmp.getEntries()[value].toString());
    }

    public synchronized void pause() {
        MtkLog.d(TAG, "pause...");
        if (MenuConfigManager.FACTORY_VIDEO.equals(this.mScreen.getKey())) {
            for (int i = 0; i < this.mScreen.getPreferenceCount(); i++) {
                Preference tempPre = this.mScreen.getPreference(i);
                if (MenuConfigManager.FV_AUTOCOLOR.equals(tempPre.getKey())) {
                    ((FacVideo) tempPre.getOnPreferenceClickListener()).removeListener();
                }
            }
        }
        int i2 = 0;
        while (true) {
            if (i2 >= this.mScreen.getPreferenceCount()) {
                break;
            }
            Preference tempPre2 = this.mScreen.getPreference(i2);
            if (tempPre2.getKey().equals("SETUP_date") || tempPre2.getKey().equals("SETUP_time")) {
                this.isNeedRefreshTime = false;
            }
            if (tempPre2.getKey().equals("SETUP_sleep_timer")) {
                ListPreference sleepTimerPref = (ListPreference) tempPre2;
                Long timeLeft = 0L;
                if (this.mTV.getSleepTimerRemaining() != 0) {
                    timeLeft = Long.valueOf((((long) this.mTV.getSleepTimerRemaining()) / 60) + 1);
                }
                if (timeLeft.longValue() > 0) {
                    int value = getInstance(this.mContext.getApplicationContext()).getIndexByLeftTime(timeLeft);
                    if (value == 0) {
                        value = 1;
                    }
                    sleepTimerPref.setValueIndex(value);
                } else {
                    sleepTimerPref.setValueIndex(0);
                }
            } else {
                i2++;
            }
        }
    }

    public void setCaptionStyle(int value) {
        MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(0, value);
    }
}
