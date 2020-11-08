package com.mediatek.wwtv.tvcenter.util;

import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;

public class Util {
    public static final String TAG = "Util";

    public static String mapKeyCodeToStr(int keyCode) {
        switch (keyCode) {
            case 7:
                return "0";
            case 8:
                return "1";
            case 9:
                return "2";
            case 10:
                return MtkTvRatingConvert2Goo.RATING_STR_3;
            case 11:
                return MtkTvRatingConvert2Goo.RATING_STR_4;
            case 12:
                return MtkTvRatingConvert2Goo.RATING_STR_5;
            case 13:
                return MtkTvRatingConvert2Goo.RATING_STR_6;
            case 14:
                return MtkTvRatingConvert2Goo.RATING_STR_7;
            case 15:
                return "8";
            case 16:
                return MtkTvRatingConvert2Goo.RATING_STR_9;
            default:
                return "";
        }
    }

    public static byte[] stringToByte(String s) {
        byte[] b = new byte[3];
        if (s != null && s.length() == 5) {
            byte[] bytes = s.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                System.out.println(i + "  = " + Integer.toBinaryString(bytes[i] - 48));
            }
            b[0] = (byte) (((bytes[0] - 48) * 16) | (bytes[1] - 48));
            b[1] = (byte) ((bytes[3] - 48) | ((bytes[2] - 48) * 16));
            b[2] = (byte) (((bytes[4] - 48) * 16) | 15);
        }
        return b;
    }

    public static String convertConurty(String conurty) {
        MtkLog.d(TAG, "conurty=" + conurty);
        if ("AUS".equalsIgnoreCase(conurty)) {
            return "AU";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_BEL.equalsIgnoreCase(conurty)) {
            return "BE";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_CHE.equalsIgnoreCase(conurty)) {
            return "CH";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_CZE.equalsIgnoreCase(conurty)) {
            return "CS";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_DEU.equalsIgnoreCase(conurty)) {
            return "DE";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_DNK.equalsIgnoreCase(conurty)) {
            return "DN";
        }
        if ("ESP".equalsIgnoreCase(conurty)) {
            return "ES";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_FIN.equalsIgnoreCase(conurty)) {
            return "FI";
        }
        if ("FRA".equalsIgnoreCase(conurty)) {
            return "FR";
        }
        if ("GBR".equalsIgnoreCase(conurty)) {
            return "GB";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_ITA.equalsIgnoreCase(conurty)) {
            return "IT";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_LUX.equalsIgnoreCase(conurty)) {
            return "LU";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_NLD.equalsIgnoreCase(conurty)) {
            return "NL";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_NOR.equalsIgnoreCase(conurty)) {
            return "NO";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_SWE.equalsIgnoreCase(conurty)) {
            return "SE";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_HRV.equalsIgnoreCase(conurty)) {
            return "HR";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_GRC.equalsIgnoreCase(conurty)) {
            return "GR";
        }
        if (MtkTvConfigTypeBase.S639_CFG_LANG_HUN.equalsIgnoreCase(conurty)) {
            return "HU";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_IRL.equalsIgnoreCase(conurty)) {
            return "IE";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_POL.equalsIgnoreCase(conurty)) {
            return "PL";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_PRT.equalsIgnoreCase(conurty)) {
            return "PT";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_ROU.equalsIgnoreCase(conurty)) {
            return "RO";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_RUS.equalsIgnoreCase(conurty)) {
            return "RU";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_SRB.equalsIgnoreCase(conurty)) {
            return "SR";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_SVK.equalsIgnoreCase(conurty)) {
            return "SK";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_SVN.equalsIgnoreCase(conurty)) {
            return "SI";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_TUR.equalsIgnoreCase(conurty)) {
            return "TR";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_EST.equalsIgnoreCase(conurty)) {
            return "EE";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_UKR.equalsIgnoreCase(conurty)) {
            return "UA";
        }
        if ("THA".equalsIgnoreCase(conurty)) {
            return "TH";
        }
        if ("ZAF".equalsIgnoreCase(conurty)) {
            return "ZA";
        }
        if ("SGP".equalsIgnoreCase(conurty)) {
            return "SG";
        }
        if ("ARG".equalsIgnoreCase(conurty)) {
            return "AR";
        }
        if ("BRA".equalsIgnoreCase(conurty)) {
            return "BR";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_CAN.equalsIgnoreCase(conurty)) {
            return "CA";
        }
        if (MtkTvConfigTypeBase.S3166_CFG_COUNT_JPN.equalsIgnoreCase(conurty)) {
            return "JP";
        }
        return conurty;
    }
}
