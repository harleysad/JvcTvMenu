package com.mediatek.wwtv.setting.util;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import com.android.tv.util.TvSettings;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;

public class RecoveryRatings {
    private static final String DEFAULT_PARENTAL_CONTROLS = "vendor.mtk.pc_defvalue";
    public static final String RATING_AU_TV_ALL = "ALL";
    public static final String RATING_AU_TV_AV = "AU_TV_AV";
    public static final String RATING_AU_TV_C = "AU_TV_C";
    public static final String RATING_AU_TV_G = "AU_TV_G";
    public static final String RATING_AU_TV_M = "AU_TV_M";
    public static final String RATING_AU_TV_MA = "AU_TV_MA";
    public static final String RATING_AU_TV_P = "AU_TV_P";
    public static final String RATING_AU_TV_PG = "AU_TV_PG";
    public static final String RATING_AU_TV_R = "AU_TV_R";
    public static final String RATING_DOMAIN = "com.android.tv";
    public static final String RATING_SA_TV_AGE_10 = "BR_TV_10";
    public static final String RATING_SA_TV_AGE_12 = "BR_TV_12";
    public static final String RATING_SA_TV_AGE_14 = "BR_TV_14";
    public static final String RATING_SA_TV_AGE_16 = "BR_TV_16";
    public static final String RATING_SA_TV_AGE_18 = "BR_TV_18";
    public static final String RATING_SA_TV_AGE_L = "BR_TV_L";
    public static final String RATING_SA_TV_DRAG = "BR_TV_D";
    public static final String RATING_SA_TV_SEX = "BR_TV_S";
    public static final String RATING_SA_TV_VIOLENCE = "BR_TV_V";
    public static final String RATING_SYS_AU_TV = "AU_TV";
    public static final String RATING_SYS_DVB_TV = "DVB";
    public static final String RATING_SYS_SA_TV = "BR_TV";
    public static final String RATING_SYS_US_CA_EN_TV = "CA_EN_TV";
    public static final String RATING_SYS_US_CA_FR_TV = "CA_TV";
    public static final String RATING_SYS_US_MV = "US_MV";
    public static final String RATING_SYS_US_TV = "US_TV";
    public static final String RATING_US_CA_EN_TV_14 = "CA_EN_TV_14";
    public static final String RATING_US_CA_EN_TV_18 = "CA_EN_TV_18";
    public static final String RATING_US_CA_EN_TV_C = "CA_EN_TV_C";
    public static final String RATING_US_CA_EN_TV_C8 = "CA_EN_TV_C8";
    public static final String RATING_US_CA_EN_TV_EXEMPT = "CA_EN_TV_EXEMPT";
    public static final String RATING_US_CA_EN_TV_G = "CA_EN_TV_G";
    public static final String RATING_US_CA_EN_TV_PG = "CA_EN_TV_PG";
    public static final String RATING_US_CA_FR_TV_13 = "CA_FR_TV_13";
    public static final String RATING_US_CA_FR_TV_16 = "CA_FR_TV_16";
    public static final String RATING_US_CA_FR_TV_18 = "CA_FR_TV_18";
    public static final String RATING_US_CA_FR_TV_8 = "CA_FR_TV_8";
    public static final String RATING_US_CA_FR_TV_E = "CA_FR_TV_E";
    public static final String RATING_US_CA_FR_TV_G = "CA_FR_TV_G";
    public static final String RATING_US_MV_G = "US_MV_G";
    public static final String RATING_US_MV_NC17 = "US_MV_NC17";
    public static final String RATING_US_MV_PG = "US_MV_PG";
    public static final String RATING_US_MV_PG13 = "US_MV_PG13";
    public static final String RATING_US_MV_R = "US_MV_R";
    public static final String RATING_US_MV_X = "US_MV_X";
    public static final String RATING_US_TV_14 = "US_TV_14";
    public static final String RATING_US_TV_G = "US_TV_G";
    public static final String RATING_US_TV_MA = "US_TV_MA";
    public static final String RATING_US_TV_PG = "US_TV_PG";
    public static final String RATING_US_TV_Y = "US_TV_Y";
    public static final String RATING_US_TV_Y7 = "US_TV_Y7";
    public static final String SUB_RATING_US_TV_A = "US_TV_A";
    public static final String SUB_RATING_US_TV_D = "US_TV_D";
    public static final String SUB_RATING_US_TV_FV = "US_TV_FV";
    public static final String SUB_RATING_US_TV_L = "US_TV_L";
    public static final String SUB_RATING_US_TV_S = "US_TV_S";
    public static final String SUB_RATING_US_TV_V = "US_TV_V";
    private static final String TAG = "RecoveryRatings";
    public static final String[] US_TV_14_SUB_RATINGS = {"US_TV_A", "US_TV_D", "US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_14_SUB_RATINGS_N_A = {"US_TV_D", "US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_G_SUB_RATINGS = {"US_TV_A"};
    public static final String[] US_TV_G_SUB_RATINGS_N_A = new String[0];
    public static final String[] US_TV_MA_SUB_RATINGS = {"US_TV_A", "US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_MA_SUB_RATINGS_N_A = {"US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_PG_SUB_RATINGS = {"US_TV_A", "US_TV_D", "US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_PG_SUB_RATINGS_N_A = {"US_TV_D", "US_TV_L", "US_TV_S", "US_TV_V"};
    public static final String[] US_TV_Y7_SUB_RATINGS = {"US_TV_A", "US_TV_FV"};
    public static final String[] US_TV_Y7_SUB_RATINGS_N_A = {"US_TV_FV"};
    public static final String[] US_TV_Y_SUB_RATINGS = {"US_TV_A"};
    public static final String[] US_TV_Y_SUB_RATINGS_N_A = new String[0];
    private static Context mContext;
    static TVContent mTV;
    static TvInputManager mTvInputManager;

    public static void recoveryFromTVAPI(Context context) {
        boolean isRating = MarketRegionInfo.isFunctionSupport(21);
        boolean isRatingSA = MarketRegionInfo.isFunctionSupport(28);
        if (isRating || isRatingSA) {
            mContext = context;
            mTvInputManager = (TvInputManager) mContext.getSystemService("tv_input");
            if (mTvInputManager.getBlockedRatings().size() > 0) {
                MtkLog.e(TAG, "needn't to recovery rating!");
                for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
                    MtkLog.d(TAG, "exist Rating.String==" + xRating.flattenToString());
                }
                return;
            }
            mTV = TVContent.getInstance(context);
            boolean defEnabled = true;
            if (CommonIntegration.isEURegion()) {
                if (TVContent.getInstance(context).getRatingEnable() != 1) {
                    defEnabled = false;
                }
                recoverEURatingEnabled(defEnabled);
            } else if (CommonIntegration.isSARegion()) {
                int ageValue = mTV.getIsdbRating().getISDBAgeRatingSetting();
                int cntValue = mTV.getIsdbRating().getISDBContentRatingSetting();
                if (ageValue >= 0) {
                    String[] subRatings = null;
                    if (cntValue == 1) {
                        subRatings = new String[]{"BR_TV_D"};
                    } else if (cntValue == 2) {
                        subRatings = new String[]{"BR_TV_V"};
                    } else if (cntValue == 3) {
                        subRatings = new String[]{"BR_TV_S"};
                    } else if (cntValue == 4) {
                        subRatings = new String[]{"BR_TV_D", "BR_TV_V"};
                    } else if (cntValue == 5) {
                        subRatings = new String[]{"BR_TV_D", "BR_TV_S"};
                    } else if (cntValue == 6) {
                        subRatings = new String[]{"BR_TV_S", "BR_TV_V"};
                    } else if (cntValue == 7) {
                        subRatings = new String[]{"BR_TV_D", "BR_TV_S", "BR_TV_V"};
                    }
                    if (ageValue > 0) {
                        for (int i = (ageValue * 2) + 8; i <= 18; i += 2) {
                            TvContentRating mRating = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_" + i, subRatings);
                            mTvInputManager.addBlockedRating(mRating);
                            MtkLog.d(TAG, "SA mRating for age " + i + " toString==" + mRating.flattenToString());
                        }
                    } else if (cntValue > 0) {
                        TvContentRating mRatingL = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_L", subRatings);
                        mTvInputManager.addBlockedRating(mRatingL);
                        MtkLog.d(TAG, "SA mRating for add L toString==" + mRatingL.flattenToString());
                        int i2 = ((ageValue + 1) * 2) + 8;
                        while (true) {
                            int i3 = i2;
                            if (i3 <= 18) {
                                TvContentRating mRating2 = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_" + i3, subRatings);
                                MtkLog.d(TAG, "SA mRating for age " + i3 + " toString==" + mRating2.flattenToString());
                                mTvInputManager.addBlockedRating(mRating2);
                                i2 = i3 + 2;
                            } else {
                                return;
                            }
                        }
                    }
                }
            } else if (CommonIntegration.isUSRegion()) {
                recoverUSRatingEnabled();
                recoverUSTVRating();
                recoveryUSMVRating();
                recoveryUSCAENTVRating();
                recoveryUSCAFRTVRating();
            }
        } else {
            MtkLog.e(TAG, "not support TIF so needn't to recovery rating!");
        }
    }

    private static void recoverContentRatingLevel(int level) {
        MtkLog.d(TAG, "recoverContentRatingLevel:level=" + level);
        if (TvSettings.getContentRatingLevel(mContext) != -1) {
            return;
        }
        if (level > 0) {
            TvSettings.setContentRatingLevel(mContext, 4);
        } else {
            TvSettings.setContentRatingLevel(mContext, 0);
        }
    }

    private static void recoverEURatingEnabled(boolean enable) {
        MtkLog.d(TAG, "recoverEURatingEnabled:enable=" + enable);
        mTvInputManager.setParentalControlsEnabled(enable);
    }

    private static void recoverUSRatingEnabled() {
        boolean enable = mTV.getATSCRating().getRatingEnable();
        if (enable) {
            mTvInputManager.setParentalControlsEnabled(enable);
        }
    }

    private static void recoverUSTVRating() {
        List<String> subRatingsList = new ArrayList<>();
        MtkTvUSTvRatingSettingInfoBase info = mTV.getATSCRating().getUSTvRatingSettingInfo();
        String[] subRatings = US_TV_Y_SUB_RATINGS;
        if (info.isUsAgeTvYBlock()) {
            mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y", subRatings));
        }
        subRatingsList.clear();
        String[] subRatings2 = US_TV_Y7_SUB_RATINGS;
        int[] indexs = {-1, -1};
        if (info.isUsAgeTvY7Block()) {
            indexs[0] = 0;
        }
        if (info.isUsCntTvY7FVBlock()) {
            indexs[1] = 1;
        }
        for (int subidx : indexs) {
            if (subidx >= 0) {
                subRatingsList.add(subRatings2[subidx]);
            }
        }
        String[] subRatings3 = (String[]) subRatingsList.toArray(new String[0]);
        if (subRatings3 != null) {
            int length = subRatings3.length;
            for (int i = 0; i < length; i++) {
                mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_Y7", new String[]{subRatings3[i]}));
            }
        }
        String[] subRatings4 = US_TV_G_SUB_RATINGS;
        if (info.isUsAgeTvGBlock()) {
            mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_G", subRatings4));
        }
        subRatingsList.clear();
        String[] subRatings5 = US_TV_PG_SUB_RATINGS;
        int[] indexs2 = {-1, -1, -1, -1, -1};
        if (info.isUsAgeTvPGBlock()) {
            indexs2[0] = 0;
        }
        if (info.isUsCntTvPGDBlock()) {
            indexs2[1] = 1;
        }
        if (info.isUsCntTvPGLBlock()) {
            indexs2[2] = 2;
        }
        if (info.isUsCntTvPGSBlock()) {
            indexs2[3] = 3;
        }
        if (info.isUsCntTvPGVBlock()) {
            indexs2[4] = 4;
        }
        for (int subidx2 : indexs2) {
            if (subidx2 >= 0) {
                subRatingsList.add(subRatings5[subidx2]);
            }
        }
        String[] subRatings6 = (String[]) subRatingsList.toArray(new String[0]);
        if (subRatings6 != null) {
            int length2 = subRatings6.length;
            for (int i2 = 0; i2 < length2; i2++) {
                mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_PG", new String[]{subRatings6[i2]}));
            }
        }
        subRatingsList.clear();
        String[] subRatings7 = US_TV_14_SUB_RATINGS;
        int[] indexs3 = {-1, -1, -1, -1, -1};
        if (info.isUsAgeTv14Block()) {
            indexs3[0] = 0;
        }
        if (info.isUsCntTv14DBlock()) {
            indexs3[1] = 1;
        }
        if (info.isUsCntTv14LBlock()) {
            indexs3[2] = 2;
        }
        if (info.isUsCntTv14SBlock()) {
            indexs3[3] = 3;
        }
        if (info.isUsCntTv14VBlock()) {
            indexs3[4] = 4;
        }
        for (int subidx3 : indexs3) {
            if (subidx3 >= 0) {
                subRatingsList.add(subRatings7[subidx3]);
            }
        }
        String[] subRatings8 = (String[]) subRatingsList.toArray(new String[0]);
        if (subRatings8 != null) {
            int length3 = subRatings8.length;
            for (int i3 = 0; i3 < length3; i3++) {
                mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_14", new String[]{subRatings8[i3]}));
            }
        }
        subRatingsList.clear();
        String[] subRatings9 = US_TV_MA_SUB_RATINGS;
        int[] indexs4 = {-1, -1, -1, -1};
        if (info.isUsAgeTvMABlock()) {
            indexs4[0] = 0;
        }
        if (info.isUsCntTvMALBlock()) {
            indexs4[1] = 1;
        }
        if (info.isUsCntTvMASBlock()) {
            indexs4[2] = 2;
        }
        if (info.isUsCntTvMAVBlock()) {
            indexs4[3] = 3;
        }
        for (int subidx4 : indexs4) {
            if (subidx4 >= 0) {
                subRatingsList.add(subRatings9[subidx4]);
            }
        }
        String[] subRatings10 = (String[]) subRatingsList.toArray(new String[0]);
        if (subRatings10 != null) {
            int length4 = subRatings10.length;
            for (int i4 = 0; i4 < length4; i4++) {
                mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_TV", "US_TV_MA", new String[]{subRatings10[i4]}));
            }
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            MtkLog.d(TAG, "USTV-xRating.String==" + xRating.flattenToString());
        }
    }

    private static void recoveryUSMVRating() {
        String[] ratingNames = {"US_MV_G", "US_MV_PG", "US_MV_PG13", "US_MV_R", "US_MV_NC17", "US_MV_X"};
        for (int value = mTV.getATSCRating().getUSMovieRatingSettingInfo(); value < 6; value++) {
            mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "US_MV", ratingNames[value], new String[0]));
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            if (xRating.getRatingSystem().equals("US_MV")) {
                MtkLog.d(TAG, "USMV-xRating.String==" + xRating.flattenToString());
            }
        }
    }

    private static void recoveryUSCAENTVRating() {
        String[] ratingNames = {"CA_EN_TV_C", "CA_EN_TV_C8", "CA_EN_TV_G", "CA_EN_TV_PG", "CA_EN_TV_14", "CA_EN_TV_18"};
        int value = mTV.getATSCRating().getCANEngRatingSettingInfo();
        while (value > 0 && value < 7) {
            mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_EN_TV", ratingNames[value - 1], new String[0]));
            value++;
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            if (xRating.getRatingSystem().equals("CA_EN_TV")) {
                MtkLog.d(TAG, "USCAEN-xRating.String==" + xRating.flattenToString());
            }
        }
    }

    private static void recoveryUSCAFRTVRating() {
        String[] ratingNames = {"CA_FR_TV_G", "CA_FR_TV_8", "CA_FR_TV_13", "CA_FR_TV_16", "CA_FR_TV_18"};
        int value = mTV.getATSCRating().getCANFreRatingSettingInfo();
        while (value > 0 && value < 6) {
            mTvInputManager.addBlockedRating(TvContentRating.createRating("com.android.tv", "CA_TV", ratingNames[value - 1], new String[0]));
            value++;
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            if (xRating.getRatingSystem().equals("CA_TV")) {
                MtkLog.d(TAG, "USCAFR-xRating.String==" + xRating.flattenToString());
            }
        }
    }
}
