package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.lang.reflect.Field;

public class CountrysIndex {
    public static final int ANLG_ONLY_XAP_CTY_1_India_IND = 37;
    public static final int ANLG_ONLY_XAP_CTY_2_Philippines_PHL = 33;
    public static final int ANLG_ONLY_XAP_CTY_3_CIS_CIS = 34;
    public static final int ANLG_ONLY_XAP_CTY_4_Others = 35;
    public static final int CTY_10_United_Kingdom_GBR = 10;
    public static final int CTY_11_Italy_ITA = 11;
    public static final int CTY_12_Luxembourg_LUX = 12;
    public static final int CTY_13_Netherlands_NLD = 13;
    public static final int CTY_14_Norway_NOR = 14;
    public static final int CTY_15_Sweden_SWE = 15;
    public static final int CTY_16_Bulgaria_BGR = 16;
    public static final int CTY_17_Croatia_HRV = 17;
    public static final int CTY_18_Greece_GRC = 18;
    public static final int CTY_19_Hungary_HUN = 19;
    public static final int CTY_1_Austria_AUT = 1;
    public static final int CTY_20_Ireland_IRL = 20;
    public static final int CTY_21_Poland_POL = 21;
    public static final int CTY_22_Portugal_PRT = 22;
    public static final int CTY_23_Romania_ROU = 23;
    public static final int CTY_24_Russia_RUS = 24;
    public static final int CTY_25_Serbia_SRB = 25;
    public static final int CTY_26_Slovakia_SVK = 26;
    public static final int CTY_27_Slovenia_SVN = 27;
    public static final int CTY_28_Turkey_TUR = 28;
    public static final int CTY_29_Estonia_EST = 29;
    public static final int CTY_2_Belgium_BEL = 2;
    public static final int CTY_30_Australia_AUS = 30;
    public static final int CTY_31_New_Zealand_NZL = 31;
    public static final int CTY_32_Indonesia_IDN = 32;
    public static final int CTY_36_Ukraine_UKR = 36;
    public static final int CTY_3_Switzerland_CHE = 3;
    public static final int CTY_4_Czech_Republic_CZE = 4;
    public static final int CTY_5_Germany_DEU = 5;
    public static final int CTY_6_Denmark_DNK = 6;
    public static final int CTY_7_Spain_ESP = 7;
    public static final int CTY_8_Finland_FIN = 8;
    public static final int CTY_9_France_FRA = 9;
    public static final String TAG = "CountrysIndex";

    public static int reflectCountryStrToInt(String counrtyStr) {
        int countryIndex = -1;
        MtkLog.d("reflectCountryStrToInt(),counrtyStr:" + counrtyStr);
        try {
            CountrysIndex countryObject = new CountrysIndex();
            Object o = countryObject.getClass().newInstance();
            Field[] fileds = countryObject.getClass().getDeclaredFields();
            int length = fileds.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Field f = fileds[i];
                String paramName = f.getName();
                if (paramName.substring(paramName.lastIndexOf("_") + 1, paramName.length()).equalsIgnoreCase(counrtyStr)) {
                    countryIndex = f.getInt(o);
                    break;
                }
                i++;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        MtkLog.d("reflectCountryStrToInt(),countryIndex: " + countryIndex);
        return countryIndex;
    }
}
