package com.mediatek.wwtv.setting.util;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.base.scan.model.CableOperator;
import com.mediatek.wwtv.setting.base.scan.model.CountrysIndex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangUtil {
    public static CableOperator getCurrentOperator() {
        int operatorIndex = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_BS_BS_CABLE_BRDCSTER);
        CableOperator cableOperator = CableOperator.OTHER;
        switch (operatorIndex) {
            case 0:
                return CableOperator.OTHER;
            case 1:
                return CableOperator.UPC;
            case 2:
                return CableOperator.Comhem;
            case 3:
                return CableOperator.Canal_Digital;
            case 4:
                return CableOperator.TELE2;
            case 5:
                return CableOperator.Stofa;
            case 6:
                return CableOperator.Yousee;
            case 7:
                return CableOperator.Ziggo;
            case 8:
                return CableOperator.Unitymedia;
            case 9:
                return CableOperator.Numericable;
            case 10:
                return CableOperator.VOLIA;
            case 11:
                return CableOperator.TELEMACH;
            case 12:
                return CableOperator.ONLIME;
            case 13:
                return CableOperator.AKADO;
            case 14:
                return CableOperator.TKT;
            case 15:
                return CableOperator.DIVAN_TV;
            case 16:
                return CableOperator.NET1;
            case 17:
                return CableOperator.KDG;
            case 18:
                return CableOperator.KBW;
            case 19:
                return CableOperator.BLIZOO;
            case 20:
                return CableOperator.TELNET;
            case 21:
                return CableOperator.GLENTEN;
            case 22:
                return CableOperator.TELECOLUMBUS;
            case 23:
                return CableOperator.RCS_RDS;
            default:
                return CableOperator.OTHER;
        }
    }

    public static int getCountryIndex() {
        return CountrysIndex.reflectCountryStrToInt(MtkTvConfig.getInstance().getCountry());
    }

    public static List<String> getRegionEUMTKLanguageList() {
        return new ArrayList<>(Arrays.asList(new String[]{MtkTvConfigTypeBase.S639_CFG_LANG_ENG, MtkTvConfigTypeBase.S639_CFG_LANG_BAQ, MtkTvConfigTypeBase.S639_CFG_LANG_CAT, MtkTvConfigTypeBase.S639_CFG_LANG_SCR, MtkTvConfigTypeBase.S639_CFG_LANG_CZE, MtkTvConfigTypeBase.S638_CFG_LANG_DAN, MtkTvConfigTypeBase.S639_CFG_LANG_DUT, MtkTvConfigTypeBase.S639_CFG_LANG_FIN, MtkTvConfigTypeBase.S639_CFG_LANG_FRA, "gla", "glg", MtkTvConfigTypeBase.S639_CFG_LANG_DEU, MtkTvConfigTypeBase.S639_CFG_LANG_HUN, MtkTvConfigTypeBase.S639_CFG_LANG_ITA, MtkTvConfigTypeBase.S639_CFG_LANG_NOR, MtkTvConfigTypeBase.S639_CFG_LANG_POL, MtkTvConfigTypeBase.S639_CFG_LANG_POR, MtkTvConfigTypeBase.S639_CFG_LANG_RUM, MtkTvConfigTypeBase.S639_CFG_LANG_SCC, MtkTvConfigTypeBase.S639_CFG_LANG_SLO, MtkTvConfigTypeBase.S639_CFG_LANG_SLV, MtkTvConfigTypeBase.S639_CFG_LANG_SPA, MtkTvConfigTypeBase.S639_CFG_LANG_SWE, MtkTvConfigTypeBase.S639_CFG_LANG_TUR, "wel", MtkTvConfigTypeBase.S639_CFG_LANG_EST, MtkTvConfigTypeBase.S639_CFG_LANG_RUS}));
    }

    public static List<String> getRegionEULanguageCodeList() {
        return new ArrayList<>(Arrays.asList(new String[]{"en", "eu", "ca", "hr", "cs", "da", "nl", "fi", "fr", "gd", "gl", "de", "hu", "it", "no", "pl", "pt", "ro", "sr", "sk", "sl", "es", "sv", "tr", "cy", "et", "ru"}));
    }
}
