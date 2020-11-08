package com.mediatek.twoworlds.tv.model;

import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.MtkTvDVBRating;
import com.mediatek.twoworlds.tv.MtkTvISDBRating;
import com.mediatek.twoworlds.tv.TVNativeWrapper;
import com.mediatek.wwtv.setting.util.RatingConst;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MtkTvRatingConvert2Goo {
    public static final int BRTV_AGE_10 = 2;
    public static final int BRTV_AGE_12 = 3;
    public static final int BRTV_AGE_14 = 4;
    public static final int BRTV_AGE_16 = 5;
    public static final int BRTV_AGE_18 = 6;
    public static final int BRTV_AGE_L = 1;
    public static final int BRTV_CNT_D = 1;
    public static final int BRTV_CNT_DS = 5;
    public static final int BRTV_CNT_DV = 3;
    public static final int BRTV_CNT_DVS = 7;
    public static final int BRTV_CNT_S = 4;
    public static final int BRTV_CNT_V = 2;
    public static final int BRTV_CNT_VS = 6;
    public static final int CATV_EN_14 = 5;
    public static final int CATV_EN_18 = 6;
    public static final int CATV_EN_C = 1;
    public static final int CATV_EN_C8 = 2;
    public static final int CATV_EN_E = 0;
    public static final int CATV_EN_G = 3;
    public static final int CATV_EN_OFF = 7;
    public static final int CATV_EN_PG = 4;
    public static final int CATV_FR_13 = 3;
    public static final int CATV_FR_16 = 4;
    public static final int CATV_FR_18 = 5;
    public static final int CATV_FR_8 = 2;
    public static final int CATV_FR_E = 0;
    public static final int CATV_FR_G = 1;
    public static final int CATV_FR_OFF = 6;
    public static final int CA_TV_ENG = 0;
    public static final int CA_TV_FRA = 1;
    private static final boolean DEBUG = true;
    private static final String DELIMITER = ",";
    public static final int LOCKED_STATUS_CHANNEL_LOCKED = 2;
    public static final int LOCKED_STATUS_INP_SRC_LOCKED = 3;
    private static final int LOCKED_STATUS_M7_18AGE = 65536;
    public static final int LOCKED_STATUS_NONE = 0;
    public static final int LOCKED_STATUS_PROGRAM_RATING = 1;
    public static final int RATING_MAX_AGE = 255;
    public static final String RATING_STR_10 = "10";
    public static final String RATING_STR_11 = "11";
    public static final String RATING_STR_12 = "12";
    public static final String RATING_STR_13 = "13";
    public static final String RATING_STR_14 = "14";
    public static final String RATING_STR_15 = "15";
    public static final String RATING_STR_16 = "16";
    public static final String RATING_STR_17 = "17";
    public static final String RATING_STR_18 = "18";
    public static final String RATING_STR_19 = "19";
    public static final String RATING_STR_20 = "20";
    public static final String RATING_STR_3 = "3";
    public static final String RATING_STR_4 = "4";
    public static final String RATING_STR_5 = "5";
    public static final String RATING_STR_6 = "6";
    public static final String RATING_STR_7 = "7";
    public static final String RATING_STR_8 = "8";
    public static final String RATING_STR_9 = "9";
    public static final String RATING_STR_ALL = "ALL";
    public static final String RATING_STR_AO = "AO";
    public static final String RATING_STR_ATP = "ATP";
    public static final String RATING_STR_AV = "AV";
    public static final String RATING_STR_C = "C";
    public static final String RATING_STR_CH_LOCK = "channel_lock";
    public static final String RATING_STR_F = "F";
    public static final String RATING_STR_G = "G";
    public static final String RATING_STR_INPUT_LOCK = "input_lock";
    public static final String RATING_STR_L = "L";
    public static final String RATING_STR_M = "M";
    public static final String RATING_STR_M18 = "M18";
    public static final String RATING_STR_MA = "MA";
    public static final String RATING_STR_NC16 = "NC16";
    public static final String RATING_STR_NC18 = "NC_18";
    public static final String RATING_STR_NONE = "NONE";
    public static final String RATING_STR_P = "P";
    public static final String RATING_STR_PG = "PG";
    public static final String RATING_STR_PG10 = "PG10";
    public static final String RATING_STR_PG13 = "PG13";
    public static final String RATING_STR_PG18 = "PG18";
    public static final String RATING_STR_PGR = "PGR";
    public static final String RATING_STR_R = "R";
    public static final String RATING_STR_R18 = "R18";
    public static final String RATING_STR_R21 = "R21";
    public static final String RATING_STR_SAM_13 = "SAM_13";
    public static final String RATING_STR_SAM_16 = "SAM_16";
    public static final String RATING_STR_SAM_18 = "SAM_18";
    public static final String RATING_STR_TV_14 = "US_TV_14";
    public static final String RATING_STR_TV_G = "US_TV_G";
    public static final String RATING_STR_TV_MA = "US_TV_MA";
    public static final String RATING_STR_TV_NONE = "US_TV_NONE";
    public static final String RATING_STR_TV_PG = "US_TV_PG";
    public static final String RATING_STR_TV_Y = "US_TV_Y";
    public static final String RATING_STR_TV_Y7 = "US_TV_Y7";
    public static final String RATING_STR_U = "U";
    public static final String RATING_STR_X = "X";
    public static final String RATING_SYS_STR_AR_TV = "AR_TV";
    public static final String RATING_SYS_STR_AU_TV = "AU_TV";
    public static final String RATING_SYS_STR_BR_TV = "BR_TV";
    public static final String RATING_SYS_STR_CA_TV_EN = "CA_TV_EN";
    public static final String RATING_SYS_STR_CA_TV_FR = "CA_TV_FR";
    public static final String RATING_SYS_STR_DVB = "DVB";
    public static final String RATING_SYS_STR_ES_DVB = "ES_DVB";
    public static final String RATING_SYS_STR_FR_DVB = "FR_DVB";
    public static final String RATING_SYS_STR_ISDB = "ISDB";
    public static final String RATING_SYS_STR_KR_TV = "KR_TV";
    public static final String RATING_SYS_STR_NZ_TV = "NZ_TV";
    public static final String RATING_SYS_STR_SG_TV = "SG_TV";
    public static final String RATING_SYS_STR_TH_TV = "TH_TV";
    public static final String RATING_SYS_STR_US_MV = "US_MV";
    public static final String RATING_SYS_STR_US_TV = "US_TV";
    public static final String RATING_SYS_STR_ZA_TV = "ZA_TV";
    public static final int RATING_TYPE_CATV = 4;
    public static final int RATING_TYPE_DVB = 1;
    public static final int RATING_TYPE_ISDB = 0;
    public static final int RATING_TYPE_USMV = 3;
    public static final int RATING_TYPE_USTV = 2;
    public static final String STR_CA_TV_13 = "13";
    public static final String STR_CA_TV_14 = "14";
    public static final String STR_CA_TV_16 = "16";
    public static final String STR_CA_TV_18 = "18";
    public static final String STR_CA_TV_8 = "8";
    public static final String STR_CA_TV_C = "C";
    public static final String STR_CA_TV_C8 = "C8";
    public static final String STR_CA_TV_E = "E";
    public static final String STR_CA_TV_EXEMPT = "EXEMPT";
    public static final String STR_CA_TV_G = "G";
    public static final String STR_CA_TV_PG = "PG";
    public static final String STR_MPAA_G = "G";
    public static final String STR_MPAA_NA = "NA";
    public static final String STR_MPAA_NC_17 = "NC17";
    public static final String STR_MPAA_NR = "NR";
    public static final String STR_MPAA_PG = "PG";
    public static final String STR_MPAA_PG13 = "PG13";
    public static final String STR_MPAA_R = "R";
    public static final String STR_MPAA_X = "X";
    public static final String SUB_RATING_STR_A = "A";
    public static final String SUB_RATING_STR_D = "D";
    public static final String SUB_RATING_STR_FV = "FV";
    public static final String SUB_RATING_STR_L = "L";
    public static final String SUB_RATING_STR_S = "S";
    public static final String SUB_RATING_STR_V = "V";
    private static final String TAG = "MtkTvRatingConvert2Goo";
    public static final int USTV_AGE_14 = 5;
    public static final int USTV_AGE_G = 3;
    public static final int USTV_AGE_MA = 6;
    public static final int USTV_AGE_PG = 4;
    public static final int USTV_AGE_Y = 1;
    public static final int USTV_AGE_Y7 = 2;
    public static final int USTV_CONTENT_A = 1;
    public static final int USTV_CONTENT_D = 2;
    public static final int USTV_CONTENT_FV = 6;
    public static final int USTV_CONTENT_L = 3;
    public static final int USTV_CONTENT_S = 4;
    public static final int USTV_CONTENT_V = 5;
    public static final int USTV_MPAA = 7;
    public static final int USTV_MPAA_G = 0;
    public static final int USTV_MPAA_NC_17 = 4;
    public static final int USTV_MPAA_PG = 1;
    public static final int USTV_MPAA_PG13 = 2;
    public static final int USTV_MPAA_R = 3;
    public static final int USTV_MPAA_X = 5;
    public static final String dvb_country_code_esp = "ESP";
    private static final String mDomain = "com.android.tv";
    public static final String t_dvb_country_code_arg = "ARG";
    public static final String t_dvb_country_code_aus = "AUS";
    public static final String t_dvb_country_code_bra = "BRA";
    public static final String t_dvb_country_code_chl = "CHL";
    public static final String t_dvb_country_code_fra = "FRA";
    public static final String t_dvb_country_code_nz = "NZL";
    public static final String t_dvb_country_code_sgp = "SGP";
    public static final String t_dvb_country_code_tha = "THA";
    public static final String t_dvb_country_code_zaf = "ZAF";
    private Map<String, Integer> mAgeMap;
    private String mRating;
    private String mRatingSystem;
    private String[] mSubRating;

    private void initAgeMap() {
        this.mAgeMap.put("AR_TV_ATP", 1);
        this.mAgeMap.put("AR_TV_SAM_13", 2);
        this.mAgeMap.put("AR_TV_SAM_16", 3);
        this.mAgeMap.put("AR_TV_SAM_18", 4);
        this.mAgeMap.put("AR_TV_NC_18", 5);
        this.mAgeMap.put("BR_TV_L", 1);
        this.mAgeMap.put("BR_TV_10", 2);
        this.mAgeMap.put("BR_TV_12", 3);
        this.mAgeMap.put("BR_TV_14", 4);
        this.mAgeMap.put("BR_TV_16", 5);
        this.mAgeMap.put("BR_TV_18", 6);
        this.mAgeMap.put("BR_TV_D", 1);
        this.mAgeMap.put("BR_TV_V", 2);
        this.mAgeMap.put("BR_TV_S", 4);
        this.mAgeMap.put("BR_TV_D,BR_TV_V", 3);
        this.mAgeMap.put("BR_TV_D,BR_TV_S", 5);
        this.mAgeMap.put("BR_TV_V,BR_TV_S", 6);
        this.mAgeMap.put("BR_TV_D,BR_TV_S,BR_TV_V", 7);
        this.mAgeMap.put("ISDB_4", 4);
        this.mAgeMap.put("ISDB_5", 5);
        this.mAgeMap.put("ISDB_6", 6);
        this.mAgeMap.put("ISDB_7", 7);
        this.mAgeMap.put("ISDB_8", 8);
        this.mAgeMap.put("ISDB_9", 9);
        this.mAgeMap.put("ISDB_10", 10);
        this.mAgeMap.put("ISDB_11", 11);
        this.mAgeMap.put("ISDB_12", 12);
        this.mAgeMap.put("ISDB_13", 13);
        this.mAgeMap.put("ISDB_14", 14);
        this.mAgeMap.put("ISDB_15", 15);
        this.mAgeMap.put("ISDB_16", 16);
        this.mAgeMap.put("ISDB_17", 17);
        this.mAgeMap.put("ISDB_18", 18);
        this.mAgeMap.put("ISDB_19", 19);
        this.mAgeMap.put("ISDB_20", 20);
        this.mAgeMap.put("FR_DVB_U", 1);
        this.mAgeMap.put("FR_DVB_4", 4);
        this.mAgeMap.put("FR_DVB_5", 5);
        this.mAgeMap.put("FR_DVB_6", 6);
        this.mAgeMap.put("FR_DVB_7", 7);
        this.mAgeMap.put("FR_DVB_8", 8);
        this.mAgeMap.put("FR_DVB_9", 9);
        this.mAgeMap.put("FR_DVB_10", 10);
        this.mAgeMap.put("FR_DVB_11", 11);
        this.mAgeMap.put("FR_DVB_12", 12);
        this.mAgeMap.put("FR_DVB_13", 13);
        this.mAgeMap.put("FR_DVB_14", 14);
        this.mAgeMap.put("FR_DVB_15", 15);
        this.mAgeMap.put("FR_DVB_16", 16);
        this.mAgeMap.put("FR_DVB_17", 17);
        this.mAgeMap.put("FR_DVB_18", 18);
        this.mAgeMap.put("ES_DVB_ALL", 29);
        this.mAgeMap.put("ES_DVB_C", 30);
        this.mAgeMap.put("ES_DVB_X", 31);
        this.mAgeMap.put("ES_DVB_4", 4);
        this.mAgeMap.put("ES_DVB_5", 5);
        this.mAgeMap.put("ES_DVB_6", 6);
        this.mAgeMap.put("ES_DVB_7", 7);
        this.mAgeMap.put("ES_DVB_8", 8);
        this.mAgeMap.put("ES_DVB_9", 9);
        this.mAgeMap.put("ES_DVB_10", 10);
        this.mAgeMap.put("ES_DVB_11", 11);
        this.mAgeMap.put("ES_DVB_12", 12);
        this.mAgeMap.put("ES_DVB_13", 13);
        this.mAgeMap.put("ES_DVB_14", 14);
        this.mAgeMap.put("ES_DVB_15", 15);
        this.mAgeMap.put("ES_DVB_16", 16);
        this.mAgeMap.put("ES_DVB_17", 17);
        this.mAgeMap.put("ES_DVB_18", 18);
        this.mAgeMap.put("AU_TV_P", 5);
        this.mAgeMap.put("AU_TV_C", 7);
        this.mAgeMap.put("AU_TV_G", 9);
        this.mAgeMap.put("AU_TV_PG", 11);
        this.mAgeMap.put("AU_TV_M", 13);
        this.mAgeMap.put("AU_TV_MA", 15);
        this.mAgeMap.put("AU_TV_AV", 17);
        this.mAgeMap.put("AU_TV_R", 18);
        this.mAgeMap.put("AU_TV_ALL", 1);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_G, 4);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_PG, 5);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_PG13, 8);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_NC16, 14);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_M18, 17);
        this.mAgeMap.put(RatingConst.RATING_SG_TV_R21, 19);
        this.mAgeMap.put("NZ_TV_G", 4);
        this.mAgeMap.put("NZ_TV_PGR", 10);
        this.mAgeMap.put("NZ_TV_AO", 12);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_PG10, 4);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_10, 6);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_PG13, 7);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_13, 10);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_16, 11);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_18, 14);
        this.mAgeMap.put(RatingConst.RATING_ZA_TV_R18, 17);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_P, 4);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_C, 6);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_G, 10);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_PG13, 13);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_PG18, 18);
        this.mAgeMap.put(RatingConst.RATING_TH_TV_X, 19);
        this.mAgeMap.put("DVB_3", 3);
        this.mAgeMap.put("DVB_4", 4);
        this.mAgeMap.put("DVB_5", 5);
        this.mAgeMap.put("DVB_6", 6);
        this.mAgeMap.put("DVB_7", 7);
        this.mAgeMap.put("DVB_8", 8);
        this.mAgeMap.put("DVB_9", 9);
        this.mAgeMap.put("DVB_10", 10);
        this.mAgeMap.put("DVB_11", 11);
        this.mAgeMap.put("DVB_12", 12);
        this.mAgeMap.put("DVB_13", 13);
        this.mAgeMap.put("DVB_14", 14);
        this.mAgeMap.put("DVB_15", 15);
        this.mAgeMap.put("DVB_16", 16);
        this.mAgeMap.put("DVB_17", 17);
        this.mAgeMap.put("DVB_18", 18);
        this.mAgeMap.put("DVB_19", 19);
        this.mAgeMap.put("DVB_NONE", 0);
        this.mAgeMap.put("US_TV_Y", 1);
        this.mAgeMap.put("US_TV_Y7", 2);
        this.mAgeMap.put("US_TV_G", 3);
        this.mAgeMap.put("US_TV_PG", 4);
        this.mAgeMap.put("US_TV_14", 5);
        this.mAgeMap.put("US_TV_MA", 6);
        this.mAgeMap.put("US_MV", 7);
        this.mAgeMap.put("US_TV_A", 1);
        this.mAgeMap.put("US_TV_D", 2);
        this.mAgeMap.put("US_TV_L", 3);
        this.mAgeMap.put("US_TV_S", 4);
        this.mAgeMap.put("US_TV_V", 5);
        this.mAgeMap.put("US_TV_FV", 6);
        this.mAgeMap.put("US_MV_G", 0);
        this.mAgeMap.put("US_MV_PG", 1);
        this.mAgeMap.put("US_MV_PG13", 2);
        this.mAgeMap.put("US_MV_R", 3);
        this.mAgeMap.put("US_MV_NC17", 4);
        this.mAgeMap.put("US_MV_X", 5);
        this.mAgeMap.put(RATING_SYS_STR_CA_TV_EN, 0);
        this.mAgeMap.put(RATING_SYS_STR_CA_TV_FR, 1);
        this.mAgeMap.put("CA_TV_EN_EXEMPT", 0);
        this.mAgeMap.put("CA_TV_EN_C", 1);
        this.mAgeMap.put("CA_TV_EN_C8", 2);
        this.mAgeMap.put("CA_TV_EN_G", 3);
        this.mAgeMap.put("CA_TV_EN_PG", 4);
        this.mAgeMap.put("CA_TV_EN_14", 5);
        this.mAgeMap.put("CA_TV_EN_18", 6);
        this.mAgeMap.put("CA_TV_FR_E", 0);
        this.mAgeMap.put("CA_TV_FR_G", 1);
        this.mAgeMap.put("CA_TV_FR_8", 2);
        this.mAgeMap.put("CA_TV_FR_13", 3);
        this.mAgeMap.put("CA_TV_FR_16", 4);
        this.mAgeMap.put("CA_TV_FR_18", 5);
    }

    public MtkTvRatingConvert2Goo() {
        this.mAgeMap = new HashMap();
        this.mRatingSystem = "";
        this.mRating = "";
        this.mSubRating = null;
        initAgeMap();
    }

    public MtkTvRatingConvert2Goo(String domain, String ratingSystem, String rating, List<String> subRatings) {
        this.mAgeMap = new HashMap();
        this.mRatingSystem = ratingSystem;
        this.mRating = rating;
        if (subRatings == null || subRatings.size() <= 0) {
            this.mSubRating = null;
        } else {
            this.mSubRating = new String[subRatings.size()];
            for (int i = 0; i < subRatings.size(); i++) {
                this.mSubRating[i] = subRatings.get(i);
            }
        }
        initAgeMap();
    }

    public void setRatingSystem(String ratingSystem) {
        this.mRatingSystem = ratingSystem;
    }

    public String getRatingSystem() {
        return this.mRatingSystem;
    }

    public void setRating(String rating) {
        this.mRating = rating;
    }

    public String getRating() {
        return this.mRating;
    }

    public void setSubRating(String subRating) {
        this.mSubRating = subRating.split(DELIMITER);
    }

    public void setSubRating(List<String> subRatings) {
        if (subRatings == null || subRatings.size() <= 0) {
            this.mSubRating = null;
            return;
        }
        this.mSubRating = new String[subRatings.size()];
        for (int i = 0; i < subRatings.size(); i++) {
            this.mSubRating[i] = subRatings.get(i);
        }
    }

    public void mergeSubRating(List<String> subRatings) {
        if (subRatings != null && subRatings.size() != 0) {
            List<String> result = new ArrayList<>(subRatings);
            if (this.mSubRating != null && this.mSubRating.length > 0) {
                for (int i = 0; i < this.mSubRating.length; i++) {
                    boolean flag = false;
                    int j = 0;
                    while (true) {
                        if (j >= result.size()) {
                            break;
                        } else if (this.mSubRating[i].equals(result.get(j))) {
                            flag = true;
                            break;
                        } else {
                            j++;
                        }
                    }
                    if (!flag) {
                        result.add(this.mSubRating[i]);
                    }
                }
            }
            setSubRating(result);
        }
    }

    public String[] getSubRating() {
        return this.mSubRating;
    }

    public String getDomain() {
        return "com.android.tv";
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("com.android.tv");
        builder.append("]");
        builder.append("[");
        builder.append(this.mRatingSystem);
        builder.append("]");
        builder.append("[");
        builder.append(this.mRating);
        builder.append("]");
        if (this.mSubRating != null && this.mSubRating.length > 0) {
            for (String subRating : this.mSubRating) {
                builder.append("[");
                builder.append(subRating);
                builder.append("]");
            }
        }
        return builder.toString();
    }

    public static int getCurrentRating(MtkTvRatingConvert2Goo ratingMapped) {
        TVNativeWrapper.getCrntRatingInfo_native(ratingMapped);
        return ratingMapped.convert2TCRFormat();
    }

    public int createSpecialRating4ChLock() {
        setRatingSystem("DVB");
        setRating(RATING_STR_CH_LOCK);
        return 0;
    }

    public int createSpecialRating4InputLock() {
        setRatingSystem("DVB");
        setRating(RATING_STR_INPUT_LOCK);
        return 0;
    }

    public int createSpecialRatingByBlockedStatus(int blockedStatus) {
        Log.d(TAG, "createSpecialRatingByBlockedStatus:" + blockedStatus);
        if ((1 & blockedStatus) != 0) {
            return 0;
        }
        if ((2 & blockedStatus) != 0) {
            createSpecialRating4ChLock();
            if ((65536 & blockedStatus) == 0) {
                return 0;
            }
            setSubRating("M7_18AGE");
            return 0;
        } else if ((3 & blockedStatus) != 0) {
            createSpecialRating4InputLock();
            return 0;
        } else {
            Log.d(TAG, "blocked status invalid, default set as channel lock!");
            createSpecialRating4ChLock();
            return 0;
        }
    }

    public int getRatingString2Age() {
        Log.d(TAG, "getRatingString2Age: mRating=" + this.mRating + "\n");
        if (this.mRating == null || this.mRating.length() == 0) {
            return 0;
        }
        Integer intObj = this.mAgeMap.get(this.mRating);
        if (intObj != null) {
            return intObj.intValue();
        }
        return 255;
    }

    public int getRatingString2Age(String strAge) {
        Integer intObj;
        Log.d(TAG, "getRatingString2Age: strAge=[" + strAge + "]\n");
        if (strAge == null || strAge.length() == 0 || (intObj = this.mAgeMap.get(strAge)) == null) {
            return 255;
        }
        return intObj.intValue();
    }

    private int convertARTV2TCRFormat() {
        return 0;
    }

    private int convertAUTV2TCRFormat() {
        return 0;
    }

    private int convertBRTV2TCRFormat() {
        if (this.mRatingSystem.equals("BR_TV")) {
            int ageRating = getRatingString2Age(this.mRating);
            if (ageRating < 1 || ageRating > 6) {
                Log.d(TAG, "convertUSTV2TCRFormat: ERROR Invalid rating age.\n");
            } else {
                if (this.mSubRating == null || this.mSubRating.length == 0) {
                    Log.d(TAG, "convertUSTV2TCRFormat: ageRating=" + ageRating + "\n");
                    this.mSubRating = new String[4];
                    this.mSubRating[0] = "BR_TV_A";
                    this.mSubRating[1] = "BR_TV_D";
                    this.mSubRating[2] = "BR_TV_S";
                    this.mSubRating[3] = "BR_TV_V";
                }
                Log.d(TAG, "convertUSTV2TCRFormat: Convert done.\n");
            }
        } else {
            Log.d(TAG, "convertUSTV2TCRFormat: ERROR Invalid BR-TV Rating system.\n");
        }
        return 0;
    }

    private int convertDVB2TCRFormat() {
        return 0;
    }

    private int convertESDVB2TCRFormat() {
        return 0;
    }

    private int convertFRDVB2TCRFormat() {
        return 0;
    }

    private int convertISDB2TCRFormat() {
        return 0;
    }

    private int convertKRTV2TCRFormat() {
        return 0;
    }

    private int convertSGTV2TCRFormat() {
        return 0;
    }

    private int convertUSMV2TCRFormat() {
        return 0;
    }

    private int convertUSTV2TCRFormat() {
        if (this.mRatingSystem.equals("US_TV")) {
            int ageRating = getRatingString2Age(this.mRating);
            if (ageRating == 1) {
                Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_Y.\n");
                this.mSubRating = new String[1];
                this.mSubRating[0] = "US_TV_A";
            } else if (ageRating == 2) {
                if (this.mSubRating == null || this.mSubRating.length == 0) {
                    Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_Y7 A.\n");
                    this.mSubRating = new String[2];
                    this.mSubRating[0] = "US_TV_A";
                    this.mSubRating[1] = "US_TV_FV";
                }
            } else if (ageRating == 3) {
                Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_G.\n");
                this.mSubRating = new String[1];
                this.mSubRating[0] = "US_TV_A";
            } else if (ageRating == 4) {
                if (this.mSubRating == null || this.mSubRating.length == 0) {
                    Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_PG A.\n");
                    this.mSubRating = new String[5];
                    this.mSubRating[0] = "US_TV_A";
                    this.mSubRating[1] = "US_TV_D";
                    this.mSubRating[2] = "US_TV_L";
                    this.mSubRating[3] = "US_TV_S";
                    this.mSubRating[4] = "US_TV_V";
                }
            } else if (ageRating == 5) {
                if (this.mSubRating == null || this.mSubRating.length == 0) {
                    Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_14 A.\n");
                    this.mSubRating = new String[5];
                    this.mSubRating[0] = "US_TV_A";
                    this.mSubRating[1] = "US_TV_D";
                    this.mSubRating[2] = "US_TV_L";
                    this.mSubRating[3] = "US_TV_S";
                    this.mSubRating[4] = "US_TV_V";
                }
            } else if (ageRating != 6) {
                Log.d(TAG, "convertUSTV2TCRFormat: ERROR Invalid rating age.\n");
            } else if (this.mSubRating == null || this.mSubRating.length == 0) {
                Log.d(TAG, "convertUSTV2TCRFormat: USTV_AGE_MA A.\n");
                this.mSubRating = new String[4];
                this.mSubRating[0] = "US_TV_A";
                this.mSubRating[1] = "US_TV_L";
                this.mSubRating[2] = "US_TV_S";
                this.mSubRating[3] = "US_TV_V";
            }
        } else {
            Log.d(TAG, "convertUSTV2TCRFormat: ERROR Invalid US-TV Rating system.\n");
        }
        return 0;
    }

    private int convertCATV2TCRFormat() {
        if (getRatingType(this.mRatingSystem) != 4) {
            return 0;
        }
        int ageRating = getRatingString2Age(this.mRating);
        if (this.mRatingSystem.equals(RATING_SYS_STR_CA_TV_EN)) {
            Log.d(TAG, "convertUSTV2TCRFormat: CA_TV_ENG.\n");
            return 0;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_CA_TV_FR)) {
            Log.d(TAG, "convertUSTV2TCRFormat: CA_TV_FRA.\n");
            return 0;
        } else {
            Log.d(TAG, "convertUSTV2TCRFormat: ERROR Invalid rating age.\n");
            return 0;
        }
    }

    private int convert2TCRFormat() {
        if (this.mRatingSystem == null || this.mRatingSystem.length() == 0) {
            Log.d(TAG, "convert2TCRFormat: Warning!! Invalid rating system\n");
            return 0;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_AR_TV)) {
            int ret = convertARTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret;
        } else if (this.mRatingSystem.equals("AU_TV")) {
            int ret2 = convertAUTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret2;
        } else if (this.mRatingSystem.equals("BR_TV")) {
            int ret3 = convertBRTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret3;
        } else if (this.mRatingSystem.equals("DVB")) {
            int ret4 = convertDVB2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret4;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_ES_DVB)) {
            int ret5 = convertESDVB2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret5;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_FR_DVB)) {
            int ret6 = convertFRDVB2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret6;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_ISDB)) {
            int ret7 = convertISDB2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret7;
        } else if (this.mRatingSystem.equals(RATING_SYS_STR_KR_TV)) {
            int ret8 = convertKRTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret8;
        } else if (this.mRatingSystem.equals("SG_TV")) {
            int ret9 = convertSGTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret9;
        } else if (this.mRatingSystem.equals("US_TV")) {
            int ret10 = convertUSTV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret10;
        } else if (this.mRatingSystem.equals("US_MV")) {
            int ret11 = convertUSMV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret11;
        } else if (getRatingType(this.mRatingSystem) == 4) {
            int ret12 = convertCATV2TCRFormat();
            Log.d(TAG, "convert2TCRFormat: " + toString());
            return ret12;
        } else {
            Log.d(TAG, "convert2TCRFormat: \n");
            return 0;
        }
    }

    public int convert2USTVRatingInfo(MtkTvUSTvRatingSettingInfoBase tvRatingInfo, boolean flag) {
        boolean ageFlag = false;
        if (this.mRatingSystem.equals("US_TV")) {
            int ageRating = getRatingString2Age(this.mRating);
            int i = 0;
            if (flag) {
                if (ageRating == 1) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y.\n");
                    tvRatingInfo.setUsAgeTvYBlock(flag);
                    tvRatingInfo.setUsAgeTvY7Block(flag);
                    tvRatingInfo.setUsCntTvY7FVBlock(flag);
                    tvRatingInfo.setUsAgeTvGBlock(flag);
                    tvRatingInfo.setUsAgeTvPGBlock(flag);
                    tvRatingInfo.setUsCntTvPGDBlock(flag);
                    tvRatingInfo.setUsCntTvPGLBlock(flag);
                    tvRatingInfo.setUsCntTvPGSBlock(flag);
                    tvRatingInfo.setUsCntTvPGVBlock(flag);
                    tvRatingInfo.setUsAgeTv14Block(flag);
                    tvRatingInfo.setUsCntTv14DBlock(flag);
                    tvRatingInfo.setUsCntTv14LBlock(flag);
                    tvRatingInfo.setUsCntTv14SBlock(flag);
                    tvRatingInfo.setUsCntTv14VBlock(flag);
                    tvRatingInfo.setUsAgeTvMABlock(flag);
                    tvRatingInfo.setUsCntTvMALBlock(flag);
                    tvRatingInfo.setUsCntTvMASBlock(flag);
                    tvRatingInfo.setUsCntTvMAVBlock(flag);
                } else if (ageRating == 2) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7.\n");
                    if (this.mSubRating != null && this.mSubRating.length > 0) {
                        while (true) {
                            int i2 = i;
                            if (i2 >= this.mSubRating.length) {
                                break;
                            }
                            String tmp = this.mSubRating[i2];
                            if (tmp.equals("US_TV_A")) {
                                ageFlag = true;
                                break;
                            }
                            if (tmp.equals("US_TV_FV")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7 FV.\n");
                                tvRatingInfo.setUsCntTvY7FVBlock(flag);
                            } else {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7 Unknow Sub-Rating.\n");
                            }
                            i = i2 + 1;
                        }
                    }
                    if (this.mSubRating == null || ageFlag) {
                        Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7 A.\n");
                        tvRatingInfo.setUsAgeTvY7Block(flag);
                        tvRatingInfo.setUsCntTvY7FVBlock(flag);
                        tvRatingInfo.setUsAgeTvGBlock(flag);
                        tvRatingInfo.setUsAgeTvPGBlock(flag);
                        tvRatingInfo.setUsCntTvPGDBlock(flag);
                        tvRatingInfo.setUsCntTvPGLBlock(flag);
                        tvRatingInfo.setUsCntTvPGSBlock(flag);
                        tvRatingInfo.setUsCntTvPGVBlock(flag);
                        tvRatingInfo.setUsAgeTv14Block(flag);
                        tvRatingInfo.setUsCntTv14DBlock(flag);
                        tvRatingInfo.setUsCntTv14LBlock(flag);
                        tvRatingInfo.setUsCntTv14SBlock(flag);
                        tvRatingInfo.setUsCntTv14VBlock(flag);
                        tvRatingInfo.setUsAgeTvMABlock(flag);
                        tvRatingInfo.setUsCntTvMALBlock(flag);
                        tvRatingInfo.setUsCntTvMASBlock(flag);
                        tvRatingInfo.setUsCntTvMAVBlock(flag);
                    }
                } else if (ageRating == 3) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_G.\n");
                    tvRatingInfo.setUsAgeTvGBlock(flag);
                    tvRatingInfo.setUsAgeTvPGBlock(flag);
                    tvRatingInfo.setUsCntTvPGDBlock(flag);
                    tvRatingInfo.setUsCntTvPGLBlock(flag);
                    tvRatingInfo.setUsCntTvPGSBlock(flag);
                    tvRatingInfo.setUsCntTvPGVBlock(flag);
                    tvRatingInfo.setUsAgeTv14Block(flag);
                    tvRatingInfo.setUsCntTv14DBlock(flag);
                    tvRatingInfo.setUsCntTv14LBlock(flag);
                    tvRatingInfo.setUsCntTv14SBlock(flag);
                    tvRatingInfo.setUsCntTv14VBlock(flag);
                    tvRatingInfo.setUsAgeTvMABlock(flag);
                    tvRatingInfo.setUsCntTvMALBlock(flag);
                    tvRatingInfo.setUsCntTvMASBlock(flag);
                    tvRatingInfo.setUsCntTvMAVBlock(flag);
                } else if (ageRating == 4) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG.\n");
                    if (this.mSubRating != null && this.mSubRating.length > 0) {
                        while (true) {
                            int i3 = i;
                            if (i3 >= this.mSubRating.length) {
                                break;
                            }
                            String tmp2 = this.mSubRating[i3];
                            if (tmp2.equals("US_TV_A")) {
                                ageFlag = true;
                                break;
                            }
                            if (tmp2.equals("US_TV_D")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG D.\n");
                                tvRatingInfo.setUsCntTvPGDBlock(flag);
                                tvRatingInfo.setUsCntTv14DBlock(flag);
                            } else if (tmp2.equals("US_TV_L")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG L.\n");
                                tvRatingInfo.setUsCntTvPGLBlock(flag);
                                tvRatingInfo.setUsCntTv14LBlock(flag);
                                tvRatingInfo.setUsCntTvMALBlock(flag);
                            } else if (tmp2.equals("US_TV_S")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG S.\n");
                                tvRatingInfo.setUsCntTvPGSBlock(flag);
                                tvRatingInfo.setUsCntTv14SBlock(flag);
                                tvRatingInfo.setUsCntTvMASBlock(flag);
                            } else if (tmp2.equals("US_TV_V")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG V.\n");
                                tvRatingInfo.setUsCntTvPGVBlock(flag);
                                tvRatingInfo.setUsCntTv14VBlock(flag);
                                tvRatingInfo.setUsCntTvMAVBlock(flag);
                            } else {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG Unknow Sub-Rating.\n");
                            }
                            i = i3 + 1;
                        }
                    }
                    if (this.mSubRating == null || ageFlag) {
                        Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG A.\n");
                        tvRatingInfo.setUsAgeTvPGBlock(flag);
                        tvRatingInfo.setUsCntTvPGDBlock(flag);
                        tvRatingInfo.setUsCntTvPGLBlock(flag);
                        tvRatingInfo.setUsCntTvPGSBlock(flag);
                        tvRatingInfo.setUsCntTvPGVBlock(flag);
                        tvRatingInfo.setUsAgeTv14Block(flag);
                        tvRatingInfo.setUsCntTv14DBlock(flag);
                        tvRatingInfo.setUsCntTv14LBlock(flag);
                        tvRatingInfo.setUsCntTv14SBlock(flag);
                        tvRatingInfo.setUsCntTv14VBlock(flag);
                        tvRatingInfo.setUsAgeTvMABlock(flag);
                        tvRatingInfo.setUsCntTvMALBlock(flag);
                        tvRatingInfo.setUsCntTvMASBlock(flag);
                        tvRatingInfo.setUsCntTvMAVBlock(flag);
                    }
                } else if (ageRating == 5) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14.\n");
                    if (this.mSubRating != null && this.mSubRating.length > 0) {
                        while (true) {
                            int i4 = i;
                            if (i4 >= this.mSubRating.length) {
                                break;
                            }
                            String tmp3 = this.mSubRating[i4];
                            if (tmp3.equals("US_TV_A")) {
                                ageFlag = true;
                                break;
                            }
                            if (tmp3.equals("US_TV_D")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 D.\n");
                                tvRatingInfo.setUsCntTv14DBlock(flag);
                            } else if (tmp3.equals("US_TV_L")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 L.\n");
                                tvRatingInfo.setUsCntTv14LBlock(flag);
                                tvRatingInfo.setUsCntTvMALBlock(flag);
                            } else if (tmp3.equals("US_TV_S")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 S.\n");
                                tvRatingInfo.setUsCntTv14SBlock(flag);
                                tvRatingInfo.setUsCntTvMASBlock(flag);
                            } else if (tmp3.equals("US_TV_V")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 V.\n");
                                tvRatingInfo.setUsCntTv14VBlock(flag);
                                tvRatingInfo.setUsCntTvMAVBlock(flag);
                            } else {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 Unknow Sub-Rating.\n");
                            }
                            i = i4 + 1;
                        }
                    }
                    if (this.mSubRating == null || ageFlag) {
                        Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 A.\n");
                        tvRatingInfo.setUsAgeTv14Block(flag);
                        tvRatingInfo.setUsCntTv14DBlock(flag);
                        tvRatingInfo.setUsCntTv14LBlock(flag);
                        tvRatingInfo.setUsCntTv14SBlock(flag);
                        tvRatingInfo.setUsCntTv14VBlock(flag);
                        tvRatingInfo.setUsAgeTvMABlock(flag);
                        tvRatingInfo.setUsCntTvMALBlock(flag);
                        tvRatingInfo.setUsCntTvMASBlock(flag);
                        tvRatingInfo.setUsCntTvMAVBlock(flag);
                    }
                } else if (ageRating == 6) {
                    Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA\n");
                    if (this.mSubRating != null && this.mSubRating.length > 0) {
                        while (true) {
                            int i5 = i;
                            if (i5 >= this.mSubRating.length) {
                                break;
                            }
                            String tmp4 = this.mSubRating[i5];
                            if (tmp4.equals("US_TV_A")) {
                                ageFlag = true;
                                break;
                            }
                            if (tmp4.equals("US_TV_L")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA L.\n");
                                tvRatingInfo.setUsCntTvMALBlock(flag);
                            } else if (tmp4.equals("US_TV_S")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA S.\n");
                                tvRatingInfo.setUsCntTvMASBlock(flag);
                            } else if (tmp4.equals("US_TV_V")) {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA V.\n");
                                tvRatingInfo.setUsCntTvMAVBlock(flag);
                            } else {
                                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA Unknow Sub-Rating.\n");
                            }
                            i = i5 + 1;
                        }
                    }
                    if (this.mSubRating == null || ageFlag) {
                        Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA A.\n");
                        tvRatingInfo.setUsAgeTvMABlock(flag);
                        tvRatingInfo.setUsCntTvMALBlock(flag);
                        tvRatingInfo.setUsCntTvMASBlock(flag);
                        tvRatingInfo.setUsCntTvMAVBlock(flag);
                    }
                } else {
                    Log.d(TAG, "convert2USTVRatingInfo: ERROR Invalid rating age.\n");
                }
            } else if (ageRating == 1) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y.\n");
                tvRatingInfo.setUsAgeTvYBlock(flag);
            } else if (ageRating == 2) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7.\n");
                if (this.mSubRating != null && this.mSubRating.length > 0) {
                    while (true) {
                        int i6 = i;
                        if (i6 >= this.mSubRating.length) {
                            break;
                        }
                        if (this.mSubRating[i6].equals("US_TV_FV")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7 FV.\n");
                            tvRatingInfo.setUsCntTvY7FVBlock(flag);
                        } else {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_Y7 Unknow Sub-Rating.\n");
                        }
                        i = i6 + 1;
                    }
                } else {
                    tvRatingInfo.setUsAgeTvY7Block(flag);
                    tvRatingInfo.setUsCntTvY7FVBlock(flag);
                    tvRatingInfo.setUsAgeTvYBlock(flag);
                }
            } else if (ageRating == 3) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_G.\n");
                tvRatingInfo.setUsAgeTvYBlock(flag);
                tvRatingInfo.setUsAgeTvY7Block(flag);
                tvRatingInfo.setUsCntTvY7FVBlock(flag);
                tvRatingInfo.setUsAgeTvGBlock(flag);
            } else if (ageRating == 4) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG.\n");
                if (this.mSubRating != null && this.mSubRating.length > 0) {
                    while (true) {
                        int i7 = i;
                        if (i7 >= this.mSubRating.length) {
                            break;
                        }
                        String tmp5 = this.mSubRating[i7];
                        if (tmp5.equals("US_TV_D")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG D.\n");
                            tvRatingInfo.setUsCntTvPGDBlock(flag);
                        } else if (tmp5.equals("US_TV_L")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG L.\n");
                            tvRatingInfo.setUsCntTvPGLBlock(flag);
                        } else if (tmp5.equals("US_TV_S")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG S.\n");
                            tvRatingInfo.setUsCntTvPGSBlock(flag);
                        } else if (tmp5.equals("US_TV_V")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG V.\n");
                            tvRatingInfo.setUsCntTvPGVBlock(flag);
                        } else {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_PG Unknow Sub-Rating.\n");
                        }
                        i = i7 + 1;
                    }
                } else {
                    tvRatingInfo.setUsAgeTvYBlock(flag);
                    tvRatingInfo.setUsAgeTvY7Block(flag);
                    tvRatingInfo.setUsCntTvY7FVBlock(flag);
                    tvRatingInfo.setUsAgeTvGBlock(flag);
                    tvRatingInfo.setUsAgeTvPGBlock(flag);
                    tvRatingInfo.setUsCntTvPGDBlock(flag);
                    tvRatingInfo.setUsCntTvPGLBlock(flag);
                    tvRatingInfo.setUsCntTvPGSBlock(flag);
                    tvRatingInfo.setUsCntTvPGVBlock(flag);
                }
            } else if (ageRating == 5) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14.\n");
                if (this.mSubRating != null && this.mSubRating.length > 0) {
                    while (true) {
                        int i8 = i;
                        if (i8 >= this.mSubRating.length) {
                            break;
                        }
                        String tmp6 = this.mSubRating[i8];
                        if (tmp6.equals("US_TV_D")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 D.\n");
                            tvRatingInfo.setUsCntTvPGDBlock(flag);
                            tvRatingInfo.setUsCntTv14DBlock(flag);
                        } else if (tmp6.equals("US_TV_L")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 L.\n");
                            tvRatingInfo.setUsCntTvPGLBlock(flag);
                            tvRatingInfo.setUsCntTv14LBlock(flag);
                        } else if (tmp6.equals("US_TV_S")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 S.\n");
                            tvRatingInfo.setUsCntTvPGSBlock(flag);
                            tvRatingInfo.setUsCntTv14SBlock(flag);
                        } else if (tmp6.equals("US_TV_V")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 V.\n");
                            tvRatingInfo.setUsCntTvPGVBlock(flag);
                            tvRatingInfo.setUsCntTv14VBlock(flag);
                        } else {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_14 Unknow Sub-Rating.\n");
                        }
                        i = i8 + 1;
                    }
                } else {
                    tvRatingInfo.setUsAgeTvYBlock(flag);
                    tvRatingInfo.setUsAgeTvY7Block(flag);
                    tvRatingInfo.setUsCntTvY7FVBlock(flag);
                    tvRatingInfo.setUsAgeTvGBlock(flag);
                    tvRatingInfo.setUsAgeTvPGBlock(flag);
                    tvRatingInfo.setUsCntTvPGDBlock(flag);
                    tvRatingInfo.setUsCntTvPGLBlock(flag);
                    tvRatingInfo.setUsCntTvPGSBlock(flag);
                    tvRatingInfo.setUsCntTvPGVBlock(flag);
                    tvRatingInfo.setUsAgeTv14Block(flag);
                    tvRatingInfo.setUsCntTv14DBlock(flag);
                    tvRatingInfo.setUsCntTv14LBlock(flag);
                    tvRatingInfo.setUsCntTv14SBlock(flag);
                    tvRatingInfo.setUsCntTv14VBlock(flag);
                }
            } else if (ageRating == 6) {
                Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA.\n");
                if (this.mSubRating != null && this.mSubRating.length > 0) {
                    while (true) {
                        int i9 = i;
                        if (i9 >= this.mSubRating.length) {
                            break;
                        }
                        String tmp7 = this.mSubRating[i9];
                        if (tmp7.equals("US_TV_L")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA L.\n");
                            tvRatingInfo.setUsCntTvPGLBlock(flag);
                            tvRatingInfo.setUsCntTv14LBlock(flag);
                            tvRatingInfo.setUsCntTvMALBlock(flag);
                        } else if (tmp7.equals("US_TV_S")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA S.\n");
                            tvRatingInfo.setUsCntTvPGSBlock(flag);
                            tvRatingInfo.setUsCntTv14SBlock(flag);
                            tvRatingInfo.setUsCntTvMASBlock(flag);
                        } else if (tmp7.equals("US_TV_V")) {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA V.\n");
                            tvRatingInfo.setUsCntTvPGVBlock(flag);
                            tvRatingInfo.setUsCntTv14VBlock(flag);
                            tvRatingInfo.setUsCntTvMAVBlock(flag);
                        } else {
                            Log.d(TAG, "convert2USTVRatingInfo: USTV_AGE_MA Unknow Sub-Rating.\n");
                        }
                        i = i9 + 1;
                    }
                } else {
                    tvRatingInfo.setUsAgeTvYBlock(flag);
                    tvRatingInfo.setUsAgeTvY7Block(flag);
                    tvRatingInfo.setUsCntTvY7FVBlock(flag);
                    tvRatingInfo.setUsAgeTvGBlock(flag);
                    tvRatingInfo.setUsAgeTvPGBlock(flag);
                    tvRatingInfo.setUsCntTvPGDBlock(flag);
                    tvRatingInfo.setUsCntTvPGLBlock(flag);
                    tvRatingInfo.setUsCntTvPGSBlock(flag);
                    tvRatingInfo.setUsCntTvPGVBlock(flag);
                    tvRatingInfo.setUsAgeTv14Block(flag);
                    tvRatingInfo.setUsCntTv14DBlock(flag);
                    tvRatingInfo.setUsCntTv14LBlock(flag);
                    tvRatingInfo.setUsCntTv14SBlock(flag);
                    tvRatingInfo.setUsCntTv14VBlock(flag);
                    tvRatingInfo.setUsAgeTvMABlock(flag);
                    tvRatingInfo.setUsCntTvMALBlock(flag);
                    tvRatingInfo.setUsCntTvMASBlock(flag);
                    tvRatingInfo.setUsCntTvMAVBlock(flag);
                }
            } else {
                Log.d(TAG, "convert2USTVRatingInfo: ERROR Invalid rating age.\n");
            }
            return 0;
        }
        Log.d(TAG, "convert2USTVRatingInfo: Invalid rating for US-TV.\n");
        return -1;
    }

    public int blockContent() {
        int ratingType = getRatingType(this.mRatingSystem);
        if (ratingType == 0) {
            int ageRating = getRatingString2Age(this.mRating);
            Log.d(TAG, "blockContent: ISDB Rating ageRating=" + ageRating);
            MtkTvISDBRating.getInstance().setISDBAgeRatingSetting(ageRating);
            if (this.mRatingSystem.equals("BR_TV")) {
                if (this.mSubRating == null || this.mSubRating.length <= 0) {
                    MtkTvISDBRating.getInstance().setISDBContentRatingSetting(0);
                } else {
                    int contentRating = 0;
                    int ret = 0;
                    for (String ratingString2Age : this.mSubRating) {
                        ret = getRatingString2Age(ratingString2Age);
                        if (ret != 255) {
                            contentRating |= ret;
                        }
                    }
                    Log.d(TAG, "blockContent: ISDB Rating contentRating=" + contentRating);
                    MtkTvISDBRating.getInstance().setISDBContentRatingSetting(contentRating);
                    int i = ret;
                    int ret2 = contentRating;
                }
            }
        } else if (ratingType == 1) {
            int ageRating2 = getRatingString2Age(this.mRating);
            MtkTvDVBRating.getInstance().setDVBAgeRatingSetting(ageRating2);
            Log.d(TAG, "blockContent: DVB Rating Age=" + ageRating2 + "\n");
        } else if (ratingType == 2) {
            MtkTvUSTvRatingSettingInfoBase tvRatingInfo = MtkTvATSCRating.getInstance().getUSTvRatingSettingInfo();
            if (convert2USTVRatingInfo(tvRatingInfo, true) == 0) {
                Log.d(TAG, "blockContent: US-TV Rating.\n");
                MtkTvATSCRating.getInstance().setUSTvRatingSettingInfo(tvRatingInfo);
            } else {
                Log.d(TAG, "blockContent: US-TV Rating Fail!\n");
            }
        } else if (ratingType == 3) {
            int age = getRatingString2Age();
            if (255 == age || age > 5) {
                age = 6;
            }
            Log.d(TAG, "blockContent: US-TV MPAA Rating " + age + ".\n\n");
            MtkTvATSCRating.getInstance().setUSMovieRatingSettingInfo(age);
        } else if (ratingType == 4) {
            int age2 = getRatingString2Age(this.mRating);
            if (getRatingString2Age(this.mRatingSystem) == 0) {
                if (255 == age2) {
                    age2 = 7;
                }
                Log.d(TAG, "blockContent: CA-TV ENG Rating(" + age2 + ")\n");
                MtkTvATSCRating.getInstance().setCANEngRatingSettingInfo(age2);
            } else if (getRatingString2Age(this.mRatingSystem) == 1) {
                if (255 == age2) {
                    age2 = 6;
                }
                Log.d(TAG, "blockContent: CA-TV FRA Rating(" + age2 + ")\n");
                MtkTvATSCRating.getInstance().setCANFreRatingSettingInfo(age2);
            } else {
                Log.d(TAG, "blockContent: CA-TV Invalid(" + toString() + ")\n");
            }
        } else {
            Log.d(TAG, "blockContent: Unknow Rating.\n");
        }
        return 0;
    }

    public int unBlockContent() {
        int age;
        int age2;
        int age3;
        int ratingType = getRatingType(this.mRatingSystem);
        if (ratingType == 0) {
            int ageRating = getRatingString2Age(this.mRating);
            Log.d(TAG, "blockContent: ISDB Rating ageRating=" + ageRating);
            MtkTvISDBRating.getInstance().setISDBAgeRatingSetting(ageRating);
            if (!this.mRatingSystem.equals("BR_TV")) {
                MtkTvISDBRating.getInstance().setISDBContentRatingSetting(0);
            } else if (this.mSubRating != null && this.mSubRating.length > 0) {
                int contentRating = 0;
                int ret = 0;
                for (String ratingString2Age : this.mSubRating) {
                    ret = getRatingString2Age(ratingString2Age);
                    if (ret != 255) {
                        contentRating |= ret;
                    }
                }
                Log.d(TAG, "blockContent: ISDB Rating contentRating=" + contentRating);
                MtkTvISDBRating.getInstance().setISDBContentRatingSetting(contentRating);
                int i = ret;
                int ret2 = contentRating;
            }
        } else if (ratingType == 1) {
            int ageRating2 = getRatingString2Age(this.mRating);
            Log.d(TAG, "unBlockContent: DVB Rating=" + ageRating2);
            MtkTvDVBRating.getInstance().setDVBAgeRatingSetting(ageRating2);
        } else if (ratingType == 2) {
            MtkTvUSTvRatingSettingInfoBase tvRatingInfo = MtkTvATSCRating.getInstance().getUSTvRatingSettingInfo();
            if (convert2USTVRatingInfo(tvRatingInfo, false) == 0) {
                Log.d(TAG, "unBlockContent: US-TV Rating.\n");
                MtkTvATSCRating.getInstance().setUSTvRatingSettingInfo(tvRatingInfo);
            } else {
                Log.d(TAG, "unBlockContent: US-TV Rating Fail!\n");
            }
        } else if (ratingType == 3) {
            int age4 = getRatingString2Age(this.mRating);
            if (255 == age4 || age4 + 1 > 6) {
                age3 = 7;
            } else {
                age3 = age4 + 1;
            }
            Log.d(TAG, "unBlockContent: US-TV MPAA Rating=" + age3 + ".");
            MtkTvATSCRating.getInstance().setUSMovieRatingSettingInfo(age3);
        } else if (ratingType == 4) {
            int age5 = getRatingString2Age(this.mRating);
            if (getRatingString2Age(this.mRatingSystem) == 0) {
                if (255 == age5 || age5 + 1 > 6) {
                    age2 = 7;
                } else {
                    age2 = age5 + 1;
                }
                Log.d(TAG, "unBlockContent: CA-TV ENG Rating=" + age2 + ".");
                MtkTvATSCRating.getInstance().setCANEngRatingSettingInfo(age2);
            } else if (getRatingString2Age(this.mRatingSystem) == 1) {
                if (255 == age5 || age5 + 1 > 5) {
                    age = 6;
                } else {
                    age = age5 + 1;
                }
                Log.d(TAG, "unBlockContent: CA-TV FRA Rating=" + age + ".");
                MtkTvATSCRating.getInstance().setCANFreRatingSettingInfo(age);
            }
        } else {
            Log.d(TAG, "unBlockContent: Unknow Rating.\n");
        }
        return 0;
    }

    public int getRatingType(String ratingSystem) {
        if (ratingSystem.equals(RATING_SYS_STR_ISDB) || ratingSystem.equals(RATING_SYS_STR_AR_TV) || ratingSystem.equals("BR_TV")) {
            return 0;
        }
        if (ratingSystem.equals("US_TV")) {
            return 2;
        }
        if (ratingSystem.equals("US_MV")) {
            return 3;
        }
        if (ratingSystem.equals(RATING_SYS_STR_CA_TV_FR) || ratingSystem.equals(RATING_SYS_STR_CA_TV_EN)) {
            return 4;
        }
        return 1;
    }
}
