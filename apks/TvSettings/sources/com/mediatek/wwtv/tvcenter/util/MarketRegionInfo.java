package com.mediatek.wwtv.tvcenter.util;

import android.util.SparseBooleanArray;
import com.mediatek.twoworlds.tv.SystemProperties;

public final class MarketRegionInfo {
    public static final int F_ACR_SUPPORT = 35;
    public static final int F_ALLOW_TIMESYNC = 25;
    public static final int F_AUTO_SLEEP_SUPPORT = 40;
    public static final int F_AV_RECORD_LIB = 11;
    public static final int F_BISS_KEY = 49;
    public static final int F_BLUE_MUTE_SUPPORT = 47;
    public static final int F_CI = 8;
    public static final int F_DISEQC12_IMPROVE = 24;
    public static final int F_DIVX_SUPPORT = 23;
    public static final int F_DUAL_TUNER_SUPPORT = 32;
    public static final int F_DVBS = 4;
    public static final int F_EAS = 0;
    public static final int F_EPG_1D_SUPPORT = 46;
    public static final int F_EPG_SUPPORT = 37;
    public static final int F_EU_COL = 18;
    public static final int F_EU_DISEQC = 33;
    public static final int F_EU_HK = 19;
    public static final int F_EU_PA = 16;
    public static final int F_EU_TAIWAN = 17;
    public static final int F_EWS = 1;
    public static final int F_FVP = 44;
    public static final int F_GINGA = 2;
    public static final int F_HBBTV = 5;
    public static final int F_MHEG5 = 9;
    public static final int F_MMP_MODE = 45;
    public static final int F_MODULES_WITH_TIF = 20;
    public static final int F_MULTI_VIEW_SUPPORT = 26;
    public static final int F_NEW_APP = 27;
    public static final int F_OAD = 10;
    public static final int F_OCEANIA = 3;
    public static final int F_PA_DVBS_SUPPORT = 50;
    public static final int F_PA_EWS = 51;
    public static final int F_POWER_ON_CHANNEL_SUPPORT = 48;
    public static final int F_SA_EWS_SUPPORT = 34;
    public static final int F_SEP_TV_SRC_SUPPORT = 39;
    public static final int F_SET_AUDIO_TRACK_SUPP = 41;
    public static final int F_SET_INTERACTION_CH_SUPP = 42;
    public static final int F_SET_RECORD_SETTING_SUPP = 43;
    public static final int F_SOURCE_KEY_SUPPORT = 38;
    public static final int F_SUBTITLE = 7;
    public static final int F_TIF_DVR = 30;
    public static final int F_TIF_PWD = 29;
    public static final int F_TIF_RATING = 21;
    public static final int F_TIF_RATING_SA = 28;
    public static final int F_TIF_SA_RATING = 31;
    public static final int F_TIF_SUBTITLE = 22;
    public static final int F_TIF_SUPPORT = 13;
    public static final int F_TIF_TIMESHIFT = 36;
    public static final int F_TTX = 6;
    public static final int F_TV_CAPTION = 15;
    public static final int F_VERSION_INFO_SUPPORT = 52;
    public static final int F_VOLUME_SYNC = 12;
    public static final int F_VSS_SUPPORT = 14;
    public static final int REGION_CN = 0;
    public static final int REGION_EU = 3;
    public static final int REGION_SA = 2;
    public static final int REGION_US = 1;
    private static final String TAG = "MarketRegion";
    private static String platfrom = "";
    private static int region = 0;
    private static SparseBooleanArray sba = null;

    static {
        init();
    }

    private MarketRegionInfo() {
    }

    private static boolean init() {
        if (sba == null) {
            sba = new SparseBooleanArray();
        } else {
            sba.clear();
        }
        platfrom = SystemProperties.get("ro.board.platform");
        String marketregion = SystemProperties.get("ro.vendor.mtk.system.marketregion");
        MtkLog.d(TAG, "marketregion>>>" + marketregion);
        if (marketregion == null || marketregion.length() <= 0) {
            MtkLog.d(TAG, "can't get MarketRegion~");
        } else {
            if (marketregion.equals("us")) {
                region = 1;
                sba.append(0, true);
            } else if (marketregion.equals("eu")) {
                region = 3;
                sba.append(4, 1 == SystemProperties.getInt("ro.vendor.mtk.system.dvbs.existed", 0));
                sba.append(5, 1 == SystemProperties.getInt("ro.vendor.mtk.system.hbbtv.existed", 0));
                sba.append(6, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ttx.existed", 0));
                sba.append(7, 1 == SystemProperties.getInt("ro.vendor.mtk.system.subtitle.existed", 0));
                sba.append(8, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ci.existed", 0));
                sba.append(9, 1 == SystemProperties.getInt("ro.vendor.mtk.system.mheg5.existed", 0));
                sba.append(10, 1 == SystemProperties.getInt("ro.vendor.mtk.system.oad.existed", 0));
                sba.append(3, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.oceania", 0));
                sba.append(24, 1 == SystemProperties.getInt("ro.vendor.mtk.system.diseqcip.existed", 0));
                sba.append(25, 1 == SystemProperties.getInt("ro.vendor.mtk.system.timesync.existed", 0));
                sba.append(32, 1 == SystemProperties.getInt("ro.vendor.mtk.system.dualtuner", 0));
                sba.append(16, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.pa", 0));
                sba.append(17, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.taiwan", 0));
                sba.append(18, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.col", 0));
                sba.append(19, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.hk", 0));
                sba.append(33, 1 == SystemProperties.getInt("ro.vendor.mtk.system.eu.diseqc", 0));
                sba.append(42, 1 == SystemProperties.getInt("ro.vendor.mtk.system.interaction.existed", 0));
                sba.append(50, 1 == SystemProperties.getInt("ro.vendor.mtk.system.pa.dvbs", 0));
                sba.append(51, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ews.support", 0));
                sba.append(52, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ver.info.supported", 1));
                if (sba.get(16, false) || sba.get(17, false) || sba.get(18, false)) {
                    sba.append(39, true);
                } else {
                    sba.append(39, 1 == SystemProperties.getInt("ro.vendor.mtk.system.sep.dtv.atv", 0));
                }
            } else if (marketregion.equals("sa")) {
                region = 2;
                sba.append(2, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ginga.existed", 0));
                sba.append(1, true);
                sba.append(34, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ews.isdb", 0));
            } else if (marketregion.equals("cn")) {
                region = 0;
                sba.append(39, true);
                sba.append(8, 1 == SystemProperties.getInt("ro.vendor.mtk.system.ci.existed", 0));
            }
            sba.append(11, 1 == SystemProperties.getInt("ro.vendor.mtk.system.av_record_lib", 0));
            MtkLog.d(TAG, "MarketRegion info:" + marketregion + "," + region);
        }
        sba.append(12, 1 == SystemProperties.getInt("ro.vendor.mtk.system.audiosync", 0));
        sba.append(14, 1 == SystemProperties.getInt("ro.vendor.mtk.system.vss.existed", 0));
        sba.append(23, 1 == SystemProperties.getInt("ro.vendor.mtk.system.divx.existed", 0));
        SparseBooleanArray sparseBooleanArray = sba;
        int i = SystemProperties.getInt("vendor.mtk.tif.dvr", 0);
        sparseBooleanArray.append(30, true);
        sba.append(36, 1 == SystemProperties.getInt("vendor.mtk.tif.timeshift", 0));
        sba.append(35, 1 == SystemProperties.getInt("ro.vendor.mtk.samba.acr", 0));
        sba.append(37, 1 == SystemProperties.getInt("ro.vendor.mtk.epg.existed", 0));
        sba.append(46, 1 == SystemProperties.getInt("ro.vendor.mtk.epg.1d", 0));
        sba.append(38, 1 == SystemProperties.getInt("ro.vendor.mtk.funcs.key.existed", 0));
        sba.append(41, 1 == SystemProperties.getInt("ro.vendor.mtk.setup.audtrack.existed", 1));
        sba.append(43, 1 == SystemProperties.getInt("ro.vendor.mtk.system.record.setting.existed", 1));
        sba.append(47, 1 == SystemProperties.getInt("ro.vendor.mtk.system.blue.mute.existed", 1));
        sba.append(48, 1 == SystemProperties.getInt("ro.vendor.mtk.system.power.on.channel.existed", 1));
        sba.append(49, 1 == SystemProperties.getInt("ro.vendor.mtk.system.biss.key.existed", 1));
        sba.append(13, true);
        sba.append(27, true);
        sba.append(26, false);
        sba.append(40, 1 == SystemProperties.getInt("ro.vendor.mtk.system.autosleep.existed", 1));
        sba.append(44, true);
        sba.append(45, SystemProperties.getInt("vendor.mtk.factory.disable.input", 0) == 0);
        if (sba.get(13, false)) {
            sba.append(29, true);
        } else {
            sba.append(29, false);
        }
        if (sba.get(13, false)) {
            sba.append(22, true);
        } else {
            sba.append(22, false);
        }
        if (!sba.get(13, false)) {
            sba.append(15, false);
        } else if (region == 1 || region == 2) {
            sba.append(15, true);
        } else {
            sba.append(15, false);
        }
        if (sba.get(13, false)) {
            sba.append(20, true);
        } else {
            sba.append(20, false);
        }
        if (sba.get(13, false)) {
            if (region == 1 || region == 3) {
                sba.append(21, true);
            } else {
                sba.append(21, false);
            }
            if (region == 2) {
                sba.append(28, true);
            }
        } else {
            sba.append(21, false);
        }
        MtkLog.d(TAG, "functions array size:" + sba.size());
        return true;
    }

    public static int getCurrentMarketRegion() {
        return region;
    }

    public static boolean isFunctionSupport(int function) {
        return sba.get(function, false);
    }
}
