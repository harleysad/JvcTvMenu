package com.mediatek.wwtv.setting.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import android.os.Handler;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvChannelListBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvDVBRating;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvISDBRating;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvMultiMediaBase;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvScanPalSecamBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvCQAMScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsSatelliteSettingBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPPara;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfo;
import com.mediatek.wwtv.setting.base.scan.model.DVBCCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBCScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBTCNScanner;
import com.mediatek.wwtv.setting.base.scan.model.DVBTScanner;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.model.ScanParams;
import com.mediatek.wwtv.setting.base.scan.model.ScannerListener;
import com.mediatek.wwtv.setting.base.scan.model.ScannerManager;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.sa.db.DBMgrProgramList;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class TVContent {
    public static final int VSH_SRC_TAG3D_2D = 0;
    public static final int VSH_SRC_TAG3D_FP = 2;
    public static final int VSH_SRC_TAG3D_FS = 3;
    public static final int VSH_SRC_TAG3D_LA = 8;
    public static final int VSH_SRC_TAG3D_MVC = 1;
    public static final int VSH_SRC_TAG3D_NOT_SUPPORT = 10;
    public static final int VSH_SRC_TAG3D_REALD = 6;
    public static final int VSH_SRC_TAG3D_SBS = 5;
    public static final int VSH_SRC_TAG3D_SENSIO = 7;
    public static final int VSH_SRC_TAG3D_TB = 4;
    public static final int VSH_SRC_TAG3D_TTDO = 9;
    public static int dvbsLastOP = -1;
    private static TVContent instance;
    public static int mDvbsSatSnapShotId = -1;
    public static int snapshotID = -1;
    private final String TAG = "TVContent";
    List<TvContentRating> allThRatings;
    List<TvContentRating> allsgpRatings;
    List<TvContentRating> allzafRatings;
    private String currInputSourceName = "";
    private final boolean dumy = false;
    private final HashMap<String, Integer> dumyData = new HashMap<>();
    private String lastInputSourceName = "";
    private final MtkTvATSCScanPara mATSCScanPara = new MtkTvATSCScanPara();
    private MtkTvCI mCIBase;
    private TvCallbackHandler mCallbackHandler;
    /* access modifiers changed from: private */
    public final Context mContext;
    public Handler mHandler;
    private final MtkTvISDBScanPara mISDBScanPara = new MtkTvISDBScanPara();
    private MtkTvMultiMediaBase mMtkTvMultiMediaBase;
    private final MtkTvNTSCScanPara mNTSCScanPara = new MtkTvNTSCScanPara();
    private MtkTvOpenVCHIPSettingInfoBase mOpenVCHIPSettingInfoBase;
    private MtkTvScan mScan;
    private ScannerManager mScanManager;
    public ScanSimpleParam mScanSimpleParam;
    private InputSourceManager mSourceManager;
    private MtkTvConfig mTvConfig;
    private MtkTvATSCRating mTvRatingSettingInfo;
    private MtkTvOpenVCHIPPara para;
    private int region = 0;
    private final SaveValue saveV;

    protected TVContent(Context context) {
        this.mContext = context;
        this.saveV = SaveValue.getInstance(context);
        init();
    }

    private void init() {
        this.dumyData.clear();
        this.mScan = MtkTvScan.getInstance();
        this.mTvConfig = MtkTvConfig.getInstance();
        this.mCIBase = MtkTvCI.getInstance(0);
        this.mTvRatingSettingInfo = MtkTvATSCRating.getInstance();
        this.mCallbackHandler = TvCallbackHandler.getInstance();
        this.region = MarketRegionInfo.getCurrentMarketRegion();
        this.mScanManager = new ScannerManager(this.mContext, this);
        this.mMtkTvMultiMediaBase = new MtkTvMultiMediaBase();
    }

    public static synchronized TVContent getInstance(Context context) {
        TVContent tVContent;
        synchronized (TVContent.class) {
            if (instance == null) {
                instance = new TVContent(context);
            }
            tVContent = instance;
        }
        return tVContent;
    }

    public InputSourceManager getSourceManager() {
        if (this.mSourceManager == null) {
            this.mSourceManager = InputSourceManager.getInstance();
        }
        return this.mSourceManager;
    }

    public String getSysVersion(int eType, String sVersion) {
        String version = MtkTvUtil.getInstance().getSysVersion(eType, sVersion);
        MtkLog.d("TVContent", "getSysVersion" + version);
        return version;
    }

    public MtkTvATSCRating getATSCRating() {
        if (this.mTvRatingSettingInfo == null) {
            this.mTvRatingSettingInfo = MtkTvATSCRating.getInstance();
        }
        return this.mTvRatingSettingInfo;
    }

    public MtkTvISDBRating getIsdbRating() {
        return MtkTvISDBRating.getInstance();
    }

    public MtkTvDVBRating getDVBRating() {
        return MtkTvDVBRating.getInstance();
    }

    public MtkTvUSTvRatingSettingInfo getUsRating() {
        return new MtkTvUSTvRatingSettingInfo();
    }

    public int getDVBTIFRatingPlus() {
        List<TvContentRating> dvbRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        if (dvbRatings.size() > 0) {
            List<Integer> ageList = new ArrayList<>();
            for (TvContentRating rating : dvbRatings) {
                String ratingName = rating.getMainRating();
                MtkLog.d("TVContent", "getDVBTIFRating:ratingName ==" + ratingName);
                int pos = ratingName.indexOf("_");
                if (pos == -1) {
                    return 2;
                }
                ageList.add(Integer.valueOf(Integer.parseInt(ratingName.substring(pos + 1))));
            }
            int[] ageArray = new int[ageList.size()];
            for (int i = 0; i < ageList.size(); i++) {
                ageArray[i] = ageList.get(i).intValue();
            }
            Arrays.sort(ageArray);
            return ageArray[0];
        }
        MtkLog.d("TVContent", "getDVBTIFRating:no ratings getted");
        return 2;
    }

    public int getZafTIFRatingPlus() {
        int value = 0;
        List<TvContentRating> zafRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        this.allzafRatings = getZAFRatings();
        if (zafRatings.size() == 0) {
            return 6;
        }
        for (int i = this.allzafRatings.size() - 1; i >= 0; i--) {
            if (zafRatings.contains(this.allzafRatings.get(i))) {
                value = i;
            }
        }
        MtkLog.d("TVContent", "get zafRatings == " + value);
        return value;
    }

    public List<TvContentRating> getZAFRatings() {
        if (this.allzafRatings == null) {
            this.allzafRatings = new ArrayList();
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_PG10, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_10, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_PG13, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_13, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_16, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_18, new String[0]));
            this.allzafRatings.add(TvContentRating.createRating("com.android.tv", "ZA_TV", RatingConst.RATING_ZA_TV_R18, new String[0]));
        }
        return this.allzafRatings;
    }

    public void genereateZafTIFRating(int age) {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> zafRatings = mTvInputManager.getBlockedRatings();
        this.allzafRatings = getZAFRatings();
        if (zafRatings.size() > 0) {
            for (int i = age; i >= 0; i--) {
                TvContentRating oldRating = this.allzafRatings.get(i);
                if (zafRatings.contains(oldRating)) {
                    MtkLog.d("TVContent", "remove oldRating.flattenToString==" + oldRating.flattenToString());
                    mTvInputManager.removeBlockedRating(oldRating);
                }
            }
        }
        List<TvContentRating> list = zafRatings;
        for (int i2 = age; i2 <= 6; i2++) {
            List<TvContentRating> zafRatings2 = mTvInputManager.getBlockedRatings();
            TvContentRating newRating = this.allzafRatings.get(i2);
            if (!zafRatings2.contains(newRating)) {
                MtkLog.d("TVContent", "add new Ratings" + newRating.flattenToString());
                mTvInputManager.addBlockedRating(newRating);
            }
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            MtkLog.d("TVContent", "xRating.String==" + xRating.flattenToString());
        }
    }

    public List<TvContentRating> getSGPRatings() {
        if (this.allsgpRatings == null) {
            this.allsgpRatings = new ArrayList();
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_G, new String[0]));
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_PG, new String[0]));
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_PG13, new String[0]));
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_NC16, new String[0]));
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_M18, new String[0]));
            this.allsgpRatings.add(TvContentRating.createRating("com.android.tv", "SG_TV", RatingConst.RATING_SG_TV_R21, new String[0]));
        }
        return this.allsgpRatings;
    }

    public int getSgpTIFRatingPlus() {
        int value = 0;
        List<TvContentRating> sgpRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        this.allsgpRatings = getSGPRatings();
        if (sgpRatings.size() == 0) {
            MtkLog.d("TVContent", "getSgpTIFRatingPlus() = 0");
            return 0;
        }
        for (int i = this.allsgpRatings.size() - 1; i >= 0; i--) {
            if (sgpRatings.contains(this.allsgpRatings.get(i))) {
                value = i;
            }
        }
        MtkLog.d("TVContent", "get sgpRatings == " + value + 1);
        return value + 1;
    }

    public void genereateSingaporeTIFRating(int age) {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> sgpRatings = mTvInputManager.getBlockedRatings();
        this.allsgpRatings = getSGPRatings();
        if (age == 0) {
            for (int i = 0; i < sgpRatings.size(); i++) {
                mTvInputManager.removeBlockedRating(sgpRatings.get(i));
                MtkLog.d("TVContent", "0 removeBlockedRating" + sgpRatings.get(i));
            }
            return;
        }
        int age2 = age - 1;
        if (sgpRatings.size() > 0) {
            for (int i2 = age2; i2 >= 0; i2--) {
                TvContentRating oldRating = this.allsgpRatings.get(i2);
                if (sgpRatings.contains(oldRating)) {
                    MtkLog.d("TVContent", "remove oldRating.flattenToString==" + oldRating.flattenToString());
                    mTvInputManager.removeBlockedRating(oldRating);
                }
            }
        }
        List<TvContentRating> list = sgpRatings;
        for (int i3 = age2; i3 <= 5; i3++) {
            List<TvContentRating> sgpRatings2 = mTvInputManager.getBlockedRatings();
            TvContentRating newRating = this.allsgpRatings.get(i3);
            if (!sgpRatings2.contains(newRating)) {
                MtkLog.d("TVContent", "add new Ratings" + newRating.flattenToString());
                mTvInputManager.addBlockedRating(newRating);
            }
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            MtkLog.d("TVContent", "xRating.String==" + xRating.flattenToString());
        }
    }

    public int getThlTIFRatingPlus() {
        int value = 0;
        List<TvContentRating> thlRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        this.allThRatings = getThRatings();
        if (thlRatings.size() == 0) {
            return 0;
        }
        for (int i = this.allThRatings.size() - 1; i >= 0; i--) {
            if (thlRatings.contains(this.allThRatings.get(i))) {
                value = i + 1;
            }
        }
        MtkLog.d("TVContent", "get thlRatings == " + value);
        return value;
    }

    public List<TvContentRating> getThRatings() {
        if (this.allThRatings == null) {
            this.allThRatings = new ArrayList();
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_P, new String[0]));
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_C, new String[0]));
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_G, new String[0]));
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_PG13, new String[0]));
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_PG18, new String[0]));
            this.allThRatings.add(TvContentRating.createRating("com.android.tv", "TH_TV", RatingConst.RATING_TH_TV_X, new String[0]));
        }
        return this.allThRatings;
    }

    public void genereateThailandTIFRating(int age) {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> thlRatings = mTvInputManager.getBlockedRatings();
        this.allThRatings = getThRatings();
        if (age != 0 || thlRatings.size() <= 0) {
            int age2 = age - 1;
            if (thlRatings.size() > 0) {
                for (int i = age2; i >= 0; i--) {
                    TvContentRating oldRating = this.allThRatings.get(i);
                    if (thlRatings.contains(oldRating)) {
                        MtkLog.d("TVContent", "remove oldRating.flattenToString==" + oldRating.flattenToString());
                        mTvInputManager.removeBlockedRating(oldRating);
                    }
                }
            }
            List<TvContentRating> list = thlRatings;
            for (int i2 = age2; i2 < 6; i2++) {
                List<TvContentRating> thlRatings2 = mTvInputManager.getBlockedRatings();
                TvContentRating newRating = this.allThRatings.get(i2);
                if (!thlRatings2.contains(newRating)) {
                    MtkLog.d("TVContent", "add new Ratings" + newRating.flattenToString());
                    mTvInputManager.addBlockedRating(newRating);
                }
            }
            for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
                MtkLog.d("TVContent", "xRating.String==" + xRating.flattenToString());
            }
            return;
        }
        for (int i3 = 0; i3 < thlRatings.size(); i3++) {
            mTvInputManager.removeBlockedRating(thlRatings.get(i3));
        }
    }

    public void genereateDVBTIFRatingPlus(int age) {
        String rate = null;
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        if (age == 0) {
            List<TvContentRating> dvbRatings = mTvInputManager.getBlockedRatings();
            if (dvbRatings.size() > 0) {
                for (TvContentRating rating : dvbRatings) {
                    mTvInputManager.removeBlockedRating(rating);
                }
            }
            MtkLog.d("TVContent", "remove ratings to none");
            return;
        }
        List<TvContentRating> dvbRatings2 = mTvInputManager.getBlockedRatings();
        if (dvbRatings2.size() > 0) {
            for (int i = age - 1; i >= 3; i--) {
                rate = String.valueOf(i);
                TvContentRating oldRating = TvContentRating.createRating("com.android.tv", "DVB", "DVB_" + rate, new String[0]);
                if (dvbRatings2.contains(oldRating)) {
                    MtkLog.d("TVContent", "remove oldRating.flattenToString==" + oldRating.flattenToString());
                    mTvInputManager.removeBlockedRating(oldRating);
                }
            }
        }
        String str = rate;
        for (int i2 = age; i2 <= 18; i2++) {
            TvContentRating newRating = TvContentRating.createRating("com.android.tv", "DVB", "DVB_" + String.valueOf(i2), new String[0]);
            MtkLog.d("TVContent", "newRating.flattenToString==" + newRating.flattenToString());
            if (!dvbRatings2.contains(newRating)) {
                mTvInputManager.addBlockedRating(newRating);
            }
        }
        for (TvContentRating xRating : mTvInputManager.getBlockedRatings()) {
            MtkLog.d("TVContent", "xRating.String==" + xRating.flattenToString());
        }
    }

    public int getSATIFAgeRating() {
        List<TvContentRating> saRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        if (saRatings.size() <= 0) {
            return 0;
        }
        List<Integer> ageList = new ArrayList<>();
        for (TvContentRating rating : saRatings) {
            String ratingName = rating.getMainRating();
            List<String> subs = rating.getSubRatings();
            MtkLog.d("TVContent", "getSATIFAgeRating:ratingName ==" + ratingName);
            if (subs == null || subs.size() == 0) {
                String age = ratingName.substring(ratingName.lastIndexOf("_") + 1);
                if (age.equals("L")) {
                    ageList.add(0);
                } else {
                    ageList.add(Integer.valueOf(Integer.parseInt(age)));
                }
            }
        }
        int[] ageArray = new int[ageList.size()];
        for (int i = 0; i < ageList.size(); i++) {
            ageArray[i] = ageList.get(i).intValue();
        }
        Arrays.sort(ageArray);
        if (ageArray[0] == 0) {
            return 0;
        }
        if (ageArray[0] == 10) {
            return 1;
        }
        if (ageArray[0] == 12) {
            return 2;
        }
        if (ageArray[0] == 14) {
            return 3;
        }
        if (ageArray[0] == 16) {
            return 4;
        }
        if (ageArray[0] == 18) {
            return 5;
        }
        return -1;
    }

    public void setSATIFAgeRating(int value) {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> saRatings = mTvInputManager.getBlockedRatings();
        if (saRatings.size() > 0) {
            for (TvContentRating rating : saRatings) {
                List<String> subRatings = rating.getSubRatings();
                if (subRatings == null || subRatings.size() == 0) {
                    mTvInputManager.removeBlockedRating(rating);
                }
            }
        }
        if (value == 0) {
            TvContentRating newRating = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_L", new String[0]);
            mTvInputManager.addBlockedRating(newRating);
            MtkLog.d("TVContent", "SA age Rating:only L --newRating.flattenToString==" + newRating.flattenToString());
            return;
        }
        for (int i = (value * 2) + 8; i <= 18; i += 2) {
            TvContentRating newRating2 = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_" + i, new String[0]);
            mTvInputManager.addBlockedRating(newRating2);
            MtkLog.d("TVContent", "SA age Rating:" + i + " --newRating.flattenToString==" + newRating2.flattenToString());
        }
    }

    public int getSATIFContentRating() {
        List<TvContentRating> saRatings = ((TvInputManager) this.mContext.getSystemService("tv_input")).getBlockedRatings();
        if (saRatings.size() <= 0) {
            return 0;
        }
        List<String> subRatings = null;
        Iterator<TvContentRating> it = saRatings.iterator();
        while (it.hasNext() && ((subRatings = it.next().getSubRatings()) == null || subRatings.size() <= 0)) {
        }
        MtkLog.d("TVContent", "getSATIFContentRating:subratings:" + subRatings);
        if (subRatings == null) {
            return 0;
        }
        if (subRatings.size() == 1) {
            String sub = subRatings.get(0);
            if (sub.equals("BR_TV_D")) {
                return 1;
            }
            if (sub.equals("BR_TV_V")) {
                return 2;
            }
            if (sub.equals("BR_TV_S")) {
                return 3;
            }
            return 0;
        } else if (subRatings.size() == 3) {
            return 7;
        } else {
            if (subRatings.size() != 2) {
                return 0;
            }
            if (!subRatings.contains("BR_TV_D")) {
                return 6;
            }
            if (subRatings.contains("BR_TV_V")) {
                return 4;
            }
            return 5;
        }
    }

    public void setSATIFContentRating(int value) {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> saRatings = mTvInputManager.getBlockedRatings();
        MtkLog.e("TVContent", "setSATIFContentRating:saRatings.size=" + saRatings.size() + ",value==" + value);
        for (TvContentRating rating : saRatings) {
            List<String> subRatings = rating.getSubRatings();
            if (subRatings != null && subRatings.size() > 0) {
                mTvInputManager.removeBlockedRating(rating);
            }
        }
        if (value != 0) {
            String[] subRatings2 = null;
            if (value == 1) {
                subRatings2 = new String[]{"BR_TV_D"};
            } else if (value == 2) {
                subRatings2 = new String[]{"BR_TV_V"};
            } else if (value == 3) {
                subRatings2 = new String[]{"BR_TV_S"};
            } else if (value == 4) {
                subRatings2 = new String[]{"BR_TV_D", "BR_TV_V"};
            } else if (value == 5) {
                subRatings2 = new String[]{"BR_TV_D", "BR_TV_S"};
            } else if (value == 6) {
                subRatings2 = new String[]{"BR_TV_S", "BR_TV_V"};
            } else if (value == 7) {
                subRatings2 = new String[]{"BR_TV_D", "BR_TV_S", "BR_TV_V"};
            }
            TvContentRating createRating = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_L", subRatings2);
            for (int i = 10; i <= 18; i += 2) {
                TvContentRating newRating = TvContentRating.createRating("com.android.tv", "BR_TV", "BR_TV_" + i, subRatings2);
                MtkLog.d("TVContent", "setSATIFContentRating:newRating.flattenToString==" + newRating.flattenToString());
                mTvInputManager.addBlockedRating(newRating);
            }
        }
    }

    public int getRatingEnable() {
        boolean ret = ((TvInputManager) this.mContext.getSystemService("tv_input")).isParentalControlsEnabled();
        Log.d("TVContent", "TIF.isParentalControlsEnabled():" + ret);
        return ret;
    }

    public void setRatingEnable(boolean isRatingEnable) {
        ((TvInputManager) this.mContext.getSystemService("tv_input")).setParentalControlsEnabled(isRatingEnable);
        Log.d("TVContent", "TIF.setParentalControlsEnabled():" + isRatingEnable);
    }

    public int getBlockUnrated() {
        return this.mTvRatingSettingInfo.getBlockUnrated() ? 1 : 0;
    }

    public void setBlockUnrated(boolean isBlockUnrated) {
        this.mTvRatingSettingInfo.setBlockUnrated(isBlockUnrated);
    }

    public MtkTvOpenVCHIPInfoBase getOpenVchip() {
        this.para = getOpenVCHIPPara();
        return this.mTvRatingSettingInfo.getOpenVCHIPInfo(this.para);
    }

    public MtkTvOpenVCHIPPara getOpenVCHIPPara() {
        if (this.para == null) {
            this.para = new MtkTvOpenVCHIPPara();
        }
        return this.para;
    }

    public MtkTvOpenVCHIPSettingInfoBase getOpenVchipSetting() {
        if (this.mOpenVCHIPSettingInfoBase == null) {
            this.mOpenVCHIPSettingInfoBase = this.mTvRatingSettingInfo.getOpenVCHIPSettingInfo();
        }
        return this.mOpenVCHIPSettingInfoBase;
    }

    public MtkTvOpenVCHIPSettingInfoBase getNewOpenVchipSetting(int regionIndex, int dimIndex) {
        return this.mTvRatingSettingInfo.getOpenVCHIPSettingInfo(regionIndex, dimIndex);
    }

    public void resetRRT5() {
        getOpenVCHIPPara().setOpenVCHIPParaType(0);
        int regionNum = getOpenVchip().getRegionNum();
        MtkLog.d("TVContent", "regionNum=" + regionNum);
        for (int i = 0; i < regionNum; i++) {
            getOpenVCHIPPara().setOpenVCHIPParaType(1);
            getOpenVCHIPPara().setRegionIndex(i);
            getOpenVCHIPPara().setOpenVCHIPParaType(4);
            int dimNum = getOpenVchip().getDimNum();
            MtkLog.d("TVContent", "dimNum=" + dimNum);
            for (int j = 0; j < dimNum; j++) {
                getOpenVCHIPPara().setOpenVCHIPParaType(5);
                getOpenVCHIPPara().setDimIndex(j);
                getOpenVCHIPPara().setOpenVCHIPParaType(7);
                int levelNum = getOpenVchip().getLevelNum();
                MtkLog.d("TVContent", "levelNum=" + levelNum);
                for (int k = 0; k < levelNum; k++) {
                    getOpenVCHIPPara().setLevelIndex(k + 1);
                    MtkTvOpenVCHIPSettingInfoBase info = getNewOpenVchipSetting(i, j);
                    byte[] block = info.getLvlBlockData();
                    byte iniValue = block[k];
                    for (int l = 0; l < block.length; l++) {
                        block[l] = 0;
                        if (iniValue == 0) {
                            if (l >= k) {
                                block[l] = 0;
                            }
                        } else if (iniValue == 1 && i <= k) {
                            block[l] = 0;
                        }
                    }
                    info.setRegionIndex(i);
                    info.setDimIndex(j);
                    info.setLvlBlockData(block);
                    this.mTvRatingSettingInfo.setOpenVCHIPSettingInfo(info);
                    this.mTvRatingSettingInfo.setAtscStorage(false);
                }
            }
        }
    }

    public void setOpenVChipSetting(int regionIndex, int dimIndex, int levIndex) {
        MtkTvOpenVCHIPSettingInfoBase info = getNewOpenVchipSetting(regionIndex, dimIndex);
        byte[] block = info.getLvlBlockData();
        byte iniValue = block[levIndex];
        getOpenVCHIPPara().setOpenVCHIPParaType(6);
        getOpenVCHIPPara().setRegionIndex(regionIndex);
        getOpenVCHIPPara().setDimIndex(dimIndex);
        getOpenVCHIPPara().setLevelIndex(levIndex);
        int i = 0;
        if (!getOpenVchip().isDimGrad()) {
            if (iniValue == 0) {
                block[levIndex] = 1;
            } else if (iniValue == 1) {
                block[levIndex] = 0;
            }
            while (true) {
                int i2 = i;
                if (i2 >= block.length) {
                    break;
                }
                MtkLog.d("TVContent", "block[i]:" + block[i2] + "---i:" + i2);
                i = i2 + 1;
            }
        } else {
            for (int i3 = 0; i3 < block.length; i3++) {
                if (iniValue == 0) {
                    if (i3 >= levIndex) {
                        MtkLog.d("TVContent", "i >= levIndex set block 1");
                        block[i3] = 1;
                    }
                } else if (iniValue == 1 && i3 <= levIndex) {
                    block[i3] = 0;
                    MtkLog.d("TVContent", "i <= levIndex set block 0");
                }
                MtkLog.d("TVContent", "block[i]:" + block[i3] + "---i:" + i3);
            }
        }
        info.setRegionIndex(regionIndex);
        info.setDimIndex(dimIndex);
        info.setLvlBlockData(block);
        this.mTvRatingSettingInfo.setOpenVCHIPSettingInfo(info);
        this.mTvRatingSettingInfo.setAtscStorage(true);
    }

    public boolean isM7ScanMode() {
        if (!CommonIntegration.getInstance().isPreferSatMode()) {
            return false;
        }
        int op = getConfigValue("g_bs__bs_sat_brdcster");
        if (op == 26 || op == 30) {
            return true;
        }
        switch (op) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            default:
                switch (op) {
                    case 11:
                    case 12:
                        return true;
                    default:
                        return false;
                }
        }
    }

    public boolean addScanCallBackListener(Handler listerner) {
        return this.mCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_SCAN_NOTIFY, listerner);
    }

    public void removeCallBackListener(Handler listerner) {
        this.mCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_SCAN_NOTIFY, listerner);
    }

    public boolean addSingleLevelCallBackListener(Handler listerner) {
        return this.mCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, listerner);
    }

    public void removeSingleLevelCallBackListener(Handler listerner) {
        this.mCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_SVCTX_NOTIFY, listerner);
    }

    public boolean addConfigCallBackListener(Handler listerner) {
        return this.mCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_CONFIG, listerner);
    }

    public void removeConfigCallBackListener(Handler listerner) {
        this.mCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_CONFIG, listerner);
    }

    public boolean addCallBackListener(int msg, Handler listerner) {
        return this.mCallbackHandler.addCallBackListener(msg, listerner);
    }

    public void removeCallBackListener(int msg, Handler listerner) {
        this.mCallbackHandler.removeCallBackListener(msg, listerner);
    }

    public void stopTimeShift() {
        if (TifTimeShiftManager.getInstance() != null) {
            if (isTshitRunning()) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
            }
            TifTimeShiftManager.getInstance().stopAll();
            MtkLog.d("TVContent", "stopTimeShift!!!!!!");
        }
    }

    public void startEURangleScan(int fromchanel, int tochannel) {
        stopTimeShift();
        MtkLog.d("startEURangleScan(),Range scan", "firstScanIndex:" + fromchanel + ",lastScanIndex:" + tochannel);
        MtkTvScanDvbtBase.RfInfo[] rfChannels = MtkTvScan.getInstance().getScanDvbtInstance().getAllRf();
        MtkTvScan.getInstance().getScanDvbtInstance().startRangeScan(rfChannels[Math.min(fromchanel, tochannel)], rfChannels[Math.max(fromchanel, tochannel)]);
    }

    public void startSaRangleScan(int fromchanel, int tochannel, String itemID) {
        stopTimeShift();
        MtkLog.d("Range scan", "firstScanIndex:" + fromchanel + ",lastScanIndex:" + tochannel + "itemID:" + itemID);
        MtkTvScanBase.ScanType mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_ISDB;
        if (itemID.equals(MenuConfigManager.FACTORY_TV_RANGE_SCAN_DIG)) {
            this.mISDBScanPara.setStartIndex(fromchanel);
            this.mISDBScanPara.setEndIndex(tochannel);
            this.mATSCScanPara.setFreqPlan(0);
            this.mATSCScanPara.setModMask(0);
            this.mScan.setISDBScanParas(this.mISDBScanPara);
        } else {
            mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_NTSC;
            this.mNTSCScanPara.setStartIndex(fromchanel);
            this.mNTSCScanPara.setEndIndex(tochannel);
            this.mATSCScanPara.setFreqPlan(0);
            this.mATSCScanPara.setModMask(0);
            this.mScan.setNTSCScanParas(this.mNTSCScanPara);
        }
        this.mScan.startScan(mScanType, MtkTvScanBase.ScanMode.SCAN_MODE_RANGE, true);
    }

    public void startUsRangeScan(int fromchanel, int tochannel) {
        MtkTvScanBase.ScanType mScanType;
        stopTimeShift();
        int mScanMode = getUSRangeScanMode();
        MtkLog.d("Range scan", "fromchanel:" + fromchanel + ",tochannel:" + tochannel + "mScanMode:" + mScanMode);
        MtkTvScanBase.ScanType scanType = MtkTvScanBase.ScanType.SCAN_TYPE_US;
        if (mScanMode == 0) {
            this.mATSCScanPara.setStartIndex(fromchanel);
            this.mATSCScanPara.setEndIndex(tochannel);
            this.mATSCScanPara.setFreqPlan(0);
            this.mATSCScanPara.setModMask(0);
            this.mScan.setATSCScanParas(this.mATSCScanPara);
            this.mNTSCScanPara.setStartIndex(fromchanel);
            this.mNTSCScanPara.setEndIndex(tochannel);
            this.mScan.setNTSCScanParas(this.mNTSCScanPara);
            mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_US;
        } else if (mScanMode == 1) {
            this.mNTSCScanPara.setStartIndex(fromchanel);
            this.mNTSCScanPara.setEndIndex(tochannel);
            this.mATSCScanPara.setFreqPlan(0);
            this.mATSCScanPara.setModMask(0);
            this.mScan.setNTSCScanParas(this.mNTSCScanPara);
            mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_NTSC;
        } else {
            this.mATSCScanPara.setStartIndex(fromchanel);
            this.mATSCScanPara.setEndIndex(tochannel);
            this.mATSCScanPara.setFreqPlan(0);
            this.mATSCScanPara.setModMask(0);
            this.mScan.setATSCScanParas(this.mATSCScanPara);
            if (getCurrentTunerMode() == 0) {
                mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_ATSC;
            } else {
                mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_CQAM;
            }
        }
        MtkLog.d("Range scan", "mScanType:" + mScanType);
        this.mScan.startScan(mScanType, MtkTvScanBase.ScanMode.SCAN_MODE_RANGE, true);
    }

    public void startUsSaSingleScan(int rfchannel) {
        MtkTvScanBase.ScanType mScanType;
        stopTimeShift();
        MtkLog.d("Range scan startUsSingleScan", "rfchannel:" + rfchannel);
        if (CommonIntegration.isUSRegion()) {
            this.mATSCScanPara.setStartIndex(rfchannel);
            this.mATSCScanPara.setEndIndex(rfchannel);
            MtkTvScanBase.ScanType scanType = MtkTvScanBase.ScanType.SCAN_TYPE_US;
            if (getCurrentTunerMode() == 0) {
                mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_ATSC;
                this.mATSCScanPara.setFreqPlan(0);
                this.mATSCScanPara.setModMask(0);
            } else {
                int plan = this.saveV.readValue(MenuConfigManager.FREQUENEY_PLAN) + 1;
                int modMask = this.saveV.readValue(MenuConfigManager.TV_SINGLE_SCAN_MODULATION) + 2;
                MtkLog.d("Range scan startUsSingleScan", "setModMask:" + plan + ">>>" + modMask);
                mScanType = MtkTvScanBase.ScanType.SCAN_TYPE_CQAM;
                this.mATSCScanPara.setFreqPlan(plan);
                this.mATSCScanPara.setModMask(modMask);
            }
            this.mScan.setATSCScanParas(this.mATSCScanPara);
            MtkLog.d("Range scan mScanType", "mScanType:" + mScanType);
            this.mScan.startScan(mScanType, MtkTvScanBase.ScanMode.SCAN_MODE_SINGLE_RF_CHANNEL, true);
        } else if (CommonIntegration.isSARegion()) {
            this.mISDBScanPara.setStartIndex(rfchannel);
            this.mISDBScanPara.setEndIndex(rfchannel);
            MtkTvScanBase.ScanType mScanType2 = MtkTvScanBase.ScanType.SCAN_TYPE_ISDB;
            this.mScan.setISDBScanParas(this.mISDBScanPara);
            MtkLog.d("Range scan mScanType", "mScanType:" + mScanType2);
            this.mScan.startScan(mScanType2, MtkTvScanBase.ScanMode.SCAN_MODE_SINGLE_RF_CHANNEL, true);
        }
    }

    public int getUSRangeScanMode() {
        return this.saveV.readValue(MenuConfigManager.US_SCAN_MODE);
    }

    public int getChannelIDByRFIndex(int rfIndex) {
        if (CommonIntegration.isUSRegion()) {
            return this.mScan.getScanATSCInstance().getChannelIDByRFIndex(rfIndex);
        }
        if (CommonIntegration.isSARegion()) {
            return this.mScan.getScanISDBInstance().getChannelIDByRFIndex(rfIndex);
        }
        return 0;
    }

    public int getRFScanIndex() {
        if (CommonIntegration.isUSRegion()) {
            return this.mScan.getScanATSCInstance().getCurrentRFIndex();
        }
        if (CommonIntegration.isSARegion()) {
            return this.mScan.getScanISDBInstance().getCurrentRFIndex();
        }
        return 0;
    }

    public int getFirstScanIndex() {
        if (CommonIntegration.isUSRegion()) {
            return this.mScan.getScanATSCInstance().getFirstScanIndex();
        }
        if (CommonIntegration.isSARegion()) {
            return this.mScan.getScanISDBInstance().getFirstScanIndex();
        }
        return CommonIntegration.isEURegion() ? 0 : 0;
    }

    public int getLastScanIndex() {
        if (CommonIntegration.isUSRegion()) {
            return this.mScan.getScanATSCInstance().getLastScanIndex();
        }
        if (CommonIntegration.isSARegion()) {
            return this.mScan.getScanISDBInstance().getLastScanIndex();
        }
        if (!CommonIntegration.isEURegion() || MtkTvScan.getInstance().getScanDvbtInstance().getAllRf() == null) {
            return 0;
        }
        return MtkTvScan.getInstance().getScanDvbtInstance().getAllRf().length - 1;
    }

    public int startScan(MtkTvScanBase.ScanType scanType, MtkTvScanBase.ScanMode mode, Object param) {
        stopTimeShift();
        MtkLog.d("TVContent", "startScan:");
        MtkLog.d("TVContent", "mScanType:" + scanType);
        Throwable tr = new Throwable();
        Log.getStackTraceString(tr);
        tr.printStackTrace();
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
                MtkTvScanBase.ScanType scanType2 = MtkTvScanBase.ScanType.SCAN_TYPE_ISDB;
                MtkTvScanBase.ScanMode mode2 = MtkTvScanBase.ScanMode.SCAN_MODE_FULL;
                return 0;
            case 1:
                MtkTvScanBase.ScanType scanType3 = MtkTvScanBase.ScanType.SCAN_TYPE_US;
                MtkTvScanBase.ScanMode mode3 = MtkTvScanBase.ScanMode.SCAN_MODE_FULL;
                return 0;
            case 2:
                MtkTvScanBase.ScanType scanType4 = MtkTvScanBase.ScanType.SCAN_TYPE_ISDB;
                MtkTvScanBase.ScanMode mode4 = MtkTvScanBase.ScanMode.SCAN_MODE_FULL;
                return 0;
            case 3:
                if (param == null) {
                    this.mScan.startScan(scanType, mode, true);
                    setSimpleScanParam(new ScanSimpleParam(scanType, mode));
                    return 0;
                }
                switch (scanType) {
                    case SCAN_TYPE_DVBC:
                        if (param instanceof MtkTvDvbcScanParaBase) {
                            this.mScan.setDvbcScanParas((MtkTvDvbcScanParaBase) param);
                            break;
                        }
                        break;
                    case SCAN_TYPE_NTSC:
                        if (param instanceof MtkTvNTSCScanParaBase) {
                            this.mScan.setNTSCScanParas((MtkTvNTSCScanParaBase) param);
                            break;
                        }
                        break;
                    case SCAN_TYPE_ATSC:
                        if (param instanceof MtkTvATSCScanParaBase) {
                            this.mScan.setATSCScanParas((MtkTvATSCScanParaBase) param);
                            break;
                        }
                        break;
                    case SCAN_TYPE_ISDB:
                        if (param instanceof MtkTvISDBScanParaBase) {
                            this.mScan.setISDBScanParas((MtkTvISDBScanParaBase) param);
                            break;
                        }
                        break;
                    case SCAN_TYPE_CQAM:
                        if (param instanceof MtkTvCQAMScanParaBase) {
                            this.mScan.setCQAMScanParas((MtkTvCQAMScanParaBase) param);
                            break;
                        }
                        break;
                    case SCAN_TYPE_DVBS:
                        if (param instanceof MtkTvDvbsSatelliteSettingBase) {
                            this.mScan.setSatelliteSetting((MtkTvDvbsSatelliteSettingBase) param);
                            break;
                        }
                        break;
                }
                this.mScan.startScan(scanType, mode, false);
                setSimpleScanParam(new ScanSimpleParam(scanType, mode));
                return 0;
            default:
                this.mScan.startScan(scanType, mode, true);
                setSimpleScanParam(new ScanSimpleParam(scanType, MtkTvScanBase.ScanMode.SCAN_MODE_FULL));
                return 0;
        }
    }

    public void forOnlyEUdvbtCancescan() {
        MtkTvScan.getInstance().getScanDvbtInstance().cancelScan();
    }

    public int cancelScan() {
        MtkLog.d("TVContent", "cancelScan()");
        setSimpleScanParam((ScanSimpleParam) null);
        this.mScanManager.cancelScan();
        return 0;
    }

    public void setScanListener(ScannerListener listener) {
        this.mScanManager.setListener(listener);
    }

    public int getMinValue(String cfgId) {
        return MtkTvConfig.getMinValue(this.mTvConfig.getMinMaxConfigValue(cfgId));
    }

    public int getMaxValue(String cfgId) {
        int value = this.mTvConfig.getMinMaxConfigValue(cfgId);
        MtkLog.d("TVContent", "value:" + value);
        return MtkTvConfig.getMaxValue(value);
    }

    public int getConfigValue(String cfgId) {
        int value = this.mTvConfig.getConfigValue(cfgId);
        MtkLog.d("TVContent", "getConfigValue(cfgId):" + value + "cfgId:" + cfgId);
        return value;
    }

    public String getConfigString(String cfgId) {
        return this.mTvConfig.getConfigString(cfgId);
    }

    public void setConfigValue(String cfgId, int value) {
        MtkLog.d("TVContent", "setConfigValue cfgId:" + cfgId + "----value:" + value);
        if (cfgId.equalsIgnoreCase("g_video__vid_mjc_demo")) {
            this.mTvConfig.setConfigValue(cfgId, value, 1);
        } else {
            this.mTvConfig.setConfigValue(cfgId, value);
        }
    }

    public void setConfigValue(String cfgId, int value, boolean isUpate) {
        MtkLog.d("TVContent", "setConfigValue cfgId:" + cfgId + "----value:" + value);
        int update = 0;
        if (isUpate) {
            update = 1;
        }
        this.mTvConfig.setConfigValue(cfgId, value, update);
    }

    public boolean isConfigEnabled(String cfgId) {
        if (this.mTvConfig.isConfigEnabled(cfgId) == 0) {
            return true;
        }
        return false;
    }

    public String getCurrentInputSourceName() {
        String sourcename = InputSourceManager.getInstance().getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus());
        this.lastInputSourceName = this.currInputSourceName;
        this.currInputSourceName = sourcename;
        return sourcename;
    }

    public boolean isCurrentSourceTv() {
        return CommonIntegration.getInstance().isCurrentSourceTv();
    }

    public boolean isCurrentSourceVGA() {
        return CommonIntegration.getInstance().isCurrentSourceVGA();
    }

    public boolean isCurrentSourceHDMI() {
        return CommonIntegration.getInstance().isCurrentSourceHDMI();
    }

    public boolean iCurrentInputSourceHasSignal() {
        return !isSignalLoss();
    }

    public boolean isCurrentSourceComponent() {
        return InputSourceManager.getInstance().getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus()).equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_COMPONENT);
    }

    public boolean isCurrentSourceComposite() {
        return InputSourceManager.getInstance().getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus()).equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_COMPOSITE);
    }

    public boolean isCurrentSourceDTV() {
        return CommonIntegration.getInstance().isCurrentSourceDTV();
    }

    public boolean isCurrentSourceATV() {
        return CommonIntegration.getInstance().isCurrentSourceATV();
    }

    public boolean isCurrentSourceScart() {
        return InputSourceManager.getInstance().getCurrentInputSourceName(CommonIntegration.getInstance().getCurrentFocus()).equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_SCART);
    }

    public boolean isCurrentSourceBlocking() {
        return InputSourceManager.getInstance().isCurrentSourceBlocked(CommonIntegration.getInstance().getCurrentFocus());
    }

    public boolean isTvInputBlock() {
        return CommonIntegration.getInstance().isMenuInputTvBlock();
    }

    public int getCurrentTunerMode() {
        return getConfigValue("g_bs__bs_src");
    }

    public boolean isAnalog(MtkTvChannelInfoBase channel) {
        MtkLog.d("TVContent", "isAnalog\n");
        if (channel instanceof MtkTvAnalogChannelInfo) {
            MtkLog.d("TVContent", "isAnalog yes\n");
            return true;
        }
        MtkLog.d("TVContent", "isAnalog no\n");
        return false;
    }

    public boolean isHaveScreenMode() {
        boolean flag = isConfigVisible("g_video__screen_mode");
        MtkLog.d("TVContent", "isHaveScreenMode flag:" + flag);
        return flag;
    }

    public boolean isConfigVisible(String cfgid) {
        if (this.mTvConfig.isConfigVisible(cfgid) == 0) {
            return true;
        }
        return false;
    }

    public boolean isFilmModeEnabled() {
        return false;
    }

    public boolean isSignalLoss() {
        boolean hasSignal = MtkTvBroadcast.getInstance().isSignalLoss();
        MtkLog.d("TVContent", "isSignalLoss()?," + hasSignal);
        return hasSignal;
    }

    public boolean isTsLocked() {
        int ret = MtkTvBroadcast.getInstance().getConnectAttr(CommonIntegration.getInstance().getCurrentFocus(), 6);
        MtkLog.d("TVContent", "lock:" + ret);
        return ret == 1;
    }

    public int getSignalLevel() {
        return MtkTvBroadcast.getInstance().getSignalLevel();
    }

    public int getSignalQuality() {
        return MtkTvBroadcast.getInstance().getSignalQuality();
    }

    public void updatePowerOn(String cfgID, int enable, String date) {
        int timerValue = (((enable & 1) << 31) & Integer.MIN_VALUE) | (131071 & onTimeModified(date));
        MtkLog.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
        this.mTvConfig.setConfigValue(cfgID, timerValue);
    }

    public void updatePowerOff(String cfgID, int enable, String date) {
        int timerValue = (((enable & 1) << 31) & Integer.MIN_VALUE) | (131071 & onTimeModified(date));
        MtkLog.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
        this.mTvConfig.setConfigValue(cfgID, timerValue);
    }

    public int onTimeModified(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        if ("0".equals(SystemProperties.get("ro.vendor.mtk.system.timesync.existed", "0"))) {
            TimeZone tz = TimeZone.getTimeZone("Etc/UTC");
            Calendar cal = Calendar.getInstance();
            cal.set(11, hour);
            cal.set(12, minute);
            cal.add(11, 0 - ((tz.getRawOffset() / MtkTvTimeFormatBase.SECONDS_PER_HOUR) / 1000));
            long mills = cal.getTimeInMillis();
            MtkTvTimeFormatBase tb = new MtkTvTimeFormatBase();
            tb.setByUtcAndConvertToLocalTime(mills / 1000);
            MtkLog.d("{DT}{onTimeModified}", "Hour:" + tb.hour + "  Min:" + tb.minute);
            hour = tb.hour;
            minute = tb.minute;
        }
        return (hour * MtkTvTimeFormatBase.SECONDS_PER_HOUR) + (minute * 60);
    }

    public void setTimeInterval(int value) {
        this.mTvConfig.setConfigValue("g_rating__bl_type", value);
    }

    public void setTimeIntervalTime(String cfgID, String date) {
        this.mTvConfig.setConfigValue(cfgID, onTimeModified(date) * 1000);
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setSleepTimer(boolean r11) {
        /*
            r10 = this;
            java.lang.String r0 = "TVContent"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "direction:"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 0
            int r1 = r10.getSleepTimerRemaining()
            com.mediatek.twoworlds.tv.MtkTvTime r2 = com.mediatek.twoworlds.tv.MtkTvTime.getInstance()
            int r2 = r2.getSleepTimer(r11)
            java.lang.String r3 = "TVContent"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "leftmill:"
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = ",mill=="
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r4)
            if (r1 <= 0) goto L_0x0145
            int r3 = r1 / 60
            r4 = 9
            r5 = 1
            if (r3 < r5) goto L_0x004f
            if (r3 >= r4) goto L_0x004f
            r0 = r11
            goto L_0x0126
        L_0x004f:
            r6 = 0
            r7 = 11
            r8 = 2
            if (r3 < r4) goto L_0x005e
            if (r3 >= r7) goto L_0x005e
            if (r11 == 0) goto L_0x005b
            r6 = r8
        L_0x005b:
            r0 = r6
            goto L_0x0126
        L_0x005e:
            r4 = 19
            if (r3 < r7) goto L_0x006b
            if (r3 >= r4) goto L_0x006b
            if (r11 == 0) goto L_0x0068
            r5 = r8
        L_0x0068:
            r0 = r5
            goto L_0x0126
        L_0x006b:
            r7 = 21
            r9 = 3
            if (r3 < r4) goto L_0x0079
            if (r3 >= r7) goto L_0x0079
            if (r11 == 0) goto L_0x0076
            r5 = r9
        L_0x0076:
            r0 = r5
            goto L_0x0126
        L_0x0079:
            r4 = 29
            if (r3 < r7) goto L_0x0086
            if (r3 >= r4) goto L_0x0086
            if (r11 == 0) goto L_0x0083
            r8 = r9
        L_0x0083:
            r0 = r8
            goto L_0x0126
        L_0x0086:
            r5 = 31
            r7 = 4
            if (r3 < r4) goto L_0x0094
            if (r3 >= r5) goto L_0x0094
            if (r11 == 0) goto L_0x0090
            goto L_0x0091
        L_0x0090:
            r7 = r8
        L_0x0091:
            r0 = r7
            goto L_0x0126
        L_0x0094:
            if (r3 < r5) goto L_0x00a1
            r4 = 39
            if (r3 >= r4) goto L_0x00a1
            if (r11 == 0) goto L_0x009d
            goto L_0x009e
        L_0x009d:
            r7 = r9
        L_0x009e:
            r0 = r7
            goto L_0x0126
        L_0x00a1:
            r4 = 39
            r5 = 5
            if (r3 < r4) goto L_0x00b1
            r4 = 41
            if (r3 >= r4) goto L_0x00b1
            if (r11 == 0) goto L_0x00ad
            goto L_0x00ae
        L_0x00ad:
            r5 = r9
        L_0x00ae:
            r0 = r5
            goto L_0x0126
        L_0x00b1:
            r4 = 41
            if (r3 < r4) goto L_0x00c0
            r4 = 49
            if (r3 >= r4) goto L_0x00c0
            if (r11 == 0) goto L_0x00bc
            goto L_0x00bd
        L_0x00bc:
            r5 = r7
        L_0x00bd:
            r0 = r5
            goto L_0x0126
        L_0x00c0:
            r4 = 49
            r8 = 6
            if (r3 < r4) goto L_0x00d0
            r4 = 51
            if (r3 >= r4) goto L_0x00d0
            if (r11 == 0) goto L_0x00cd
            r7 = r8
        L_0x00cd:
            r0 = r7
            goto L_0x0126
        L_0x00d0:
            r4 = 51
            if (r3 < r4) goto L_0x00de
            r4 = 59
            if (r3 >= r4) goto L_0x00de
            if (r11 == 0) goto L_0x00dc
            r5 = r8
        L_0x00dc:
            r0 = r5
            goto L_0x0126
        L_0x00de:
            r4 = 59
            r7 = 7
            if (r3 < r4) goto L_0x00ed
            r4 = 61
            if (r3 >= r4) goto L_0x00ed
            if (r11 == 0) goto L_0x00eb
            r5 = r7
        L_0x00eb:
            r0 = r5
            goto L_0x0126
        L_0x00ed:
            r4 = 61
            if (r3 < r4) goto L_0x00fb
            r4 = 89
            if (r3 >= r4) goto L_0x00fb
            if (r11 == 0) goto L_0x00f8
            goto L_0x00f9
        L_0x00f8:
            r7 = r8
        L_0x00f9:
            r0 = r7
            goto L_0x0126
        L_0x00fb:
            r4 = 89
            if (r3 < r4) goto L_0x010a
            r4 = 91
            if (r3 >= r4) goto L_0x010a
            if (r11 == 0) goto L_0x0108
            r8 = 8
        L_0x0108:
            r0 = r8
            goto L_0x0126
        L_0x010a:
            r4 = 91
            if (r3 < r4) goto L_0x0119
            r4 = 119(0x77, float:1.67E-43)
            if (r3 >= r4) goto L_0x0119
            if (r11 == 0) goto L_0x0117
            r7 = 8
        L_0x0117:
            r0 = r7
            goto L_0x0126
        L_0x0119:
            r4 = 119(0x77, float:1.67E-43)
            if (r3 < r4) goto L_0x0126
            r4 = 120(0x78, float:1.68E-43)
            if (r3 > r4) goto L_0x0126
            if (r11 == 0) goto L_0x0124
            goto L_0x0125
        L_0x0124:
            r6 = r7
        L_0x0125:
            r0 = r6
        L_0x0126:
            java.lang.String r4 = "TVContent"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "minute:"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r6 = "valueIndex:"
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            goto L_0x017a
        L_0x0145:
            int r3 = r2 / 60
            r4 = 10
            if (r3 == r4) goto L_0x0178
            r4 = 20
            if (r3 == r4) goto L_0x0176
            r4 = 30
            if (r3 == r4) goto L_0x0174
            r4 = 40
            if (r3 == r4) goto L_0x0172
            r4 = 50
            if (r3 == r4) goto L_0x0170
            r4 = 60
            if (r3 == r4) goto L_0x016e
            r4 = 90
            if (r3 == r4) goto L_0x016c
            r4 = 120(0x78, float:1.68E-43)
            if (r3 == r4) goto L_0x0169
            r0 = 0
            goto L_0x017a
        L_0x0169:
            r0 = 8
            goto L_0x017a
        L_0x016c:
            r0 = 7
            goto L_0x017a
        L_0x016e:
            r0 = 6
            goto L_0x017a
        L_0x0170:
            r0 = 5
            goto L_0x017a
        L_0x0172:
            r0 = 4
            goto L_0x017a
        L_0x0174:
            r0 = 3
            goto L_0x017a
        L_0x0176:
            r0 = 2
            goto L_0x017a
        L_0x0178:
            r0 = 1
        L_0x017a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.setting.util.TVContent.setSleepTimer(boolean):int");
    }

    public int getSleepTimerRemaining() {
        return MtkTvTime.getInstance().getSleepTimerRemainingTime();
    }

    public void resetConfigValues() {
        this.mTvConfig.resetConfigValues(6);
    }

    public void resetPub(final Handler handler) {
        new Thread(new Runnable() {
            public void run() {
                MtkLog.e("gitmaster", "resetPub........." + System.currentTimeMillis());
                MtkTvUtil.getInstance().resetPub();
                MtkLog.e("gitmaster", "resetPub ended........." + System.currentTimeMillis());
                handler.sendEmptyMessage(35);
            }
        }).start();
    }

    public void resetPri(final Handler handler) {
        new Thread(new Runnable() {
            public void run() {
                MtkLog.e("TVContent", "resetPri........." + System.currentTimeMillis());
                MtkTvUtil.getInstance().resetPri();
                handler.sendEmptyMessage(35);
            }
        }).start();
    }

    public ScanSimpleParam getSimpleScanParam() {
        if (this.mScanSimpleParam == null) {
            return new ScanSimpleParam((MtkTvScanBase.ScanType) null, (MtkTvScanBase.ScanMode) null);
        }
        return this.mScanSimpleParam;
    }

    public void setSimpleScanParam(ScanSimpleParam mScanSimpleParam2) {
        this.mScanSimpleParam = mScanSimpleParam2;
    }

    public String getRFChannel(int type) {
        return DVBTScanner.getRFChannel(type);
    }

    public String getCNRFChannel(int type) {
        return DVBTCNScanner.getScanInstance().getCNRFChannel(type);
    }

    public String getRFChannelByIndex(int index) {
        return MtkTvScan.getInstance().getScanDvbtInstance().getAllRf()[index].rfChannelName;
    }

    public String[] getDvbtAllRFChannels() {
        MtkTvScanDvbtBase.RfInfo[] rfChannels = MtkTvScan.getInstance().getScanDvbtInstance().getAllRf();
        String[] rfStrChannels = new String[rfChannels.length];
        for (int i = 0; i < rfStrChannels.length; i++) {
            rfStrChannels[i] = rfChannels[i].rfChannelName;
        }
        MtkLog.d("TVContent", "getAllRFChannels()," + rfStrChannels.length);
        return rfStrChannels;
    }

    public static class ScanSimpleParam {
        public static MtkTvScanBase.ScanMode mode = null;
        public MtkTvScanBase.ScanType type = null;

        public ScanSimpleParam(MtkTvScanBase.ScanType type2, MtkTvScanBase.ScanMode mode2) {
            this.type = type2;
            mode = mode2;
        }
    }

    public int getActionSize() {
        return this.mScanManager.getActionList().size();
    }

    public void clearActionList() {
        this.mScanManager.clearActionList();
    }

    public boolean hasOPToDo() {
        return this.mScanManager.hasOPToDo();
    }

    public void uiOpEnd() {
        this.mScanManager.uiOpEnd();
    }

    public int getOriginalActionSize() {
        return this.mScanManager.getActionList().totalScanActionSize;
    }

    public void startDVBSScanTask(ScannerListener listener) {
        stopTimeShift();
        this.mScanManager.startScan(11, listener, this.mScanManager.getDVBSDataParams());
    }

    public void startOtherScanTask(ScannerListener listener) {
        stopTimeShift();
        if (this.mScanManager.getActionList().size() <= 0) {
            listener.onCompleted(2);
            return;
        }
        ScannerManager.Action action = (ScannerManager.Action) this.mScanManager.getActionList().get(0);
        switch (action) {
            case DTV:
                this.mScanManager.startScan(1, listener, this.mScanManager.getstoredParams());
                break;
            case ATV:
                this.mScanManager.startScan(2, listener, (ScanParams) null);
                break;
            case DTV_UPDATE:
                this.mScanManager.startScan(9, listener, (ScanParams) null);
                break;
            case ATV_UPDATE:
                this.mScanManager.startScan(5, listener, (ScanParams) null);
                break;
            case SA_ATV_UPDATE:
                startScanByScanManager(15, listener, (ScanParams) null);
                break;
            case SA_DTV_UPDATE:
                startScanByScanManager(16, listener, (ScanParams) null);
                break;
        }
        if (action.equals(ScannerManager.Action.DTV) || action.equals(ScannerManager.Action.ATV)) {
            this.mScanManager.setRollback(true);
        }
    }

    public void startSingleRFScan(int rfChannel, ScannerListener listener) {
        stopTimeShift();
        ScanParams params = new ScanParams();
        params.singleRFChannel = rfChannel;
        this.mScanManager.startScan(3, listener, params);
    }

    public void startAnalogScanUpOrDown(boolean isScanUp, int frequence, ScannerListener listener) {
        stopTimeShift();
        ScanParams params = new ScanParams();
        params.freq = frequence;
        if (isScanUp) {
            this.mScanManager.startScan(6, listener, params);
        } else {
            this.mScanManager.startScan(7, listener, params);
        }
    }

    public void startATVAutoOrUpdateScan(int type, ScannerListener listener, ScanParams params) {
        stopTimeShift();
        this.mScanManager.startScan(type, listener, params);
    }

    public static void clearChannelDB() {
        int svl = CommonIntegration.getInstance().getSvl();
        if (svl == 3 || svl == 4 || svl == 7) {
            MtkTvChannelListBase.cleanChannelList(svl, false);
        } else {
            MtkTvChannelListBase.cleanChannelList(svl);
        }
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    public void clearCurrentSvlChannelDB() {
        int svl = CommonIntegration.getInstance().getSvl();
        if (CommonIntegration.isCNRegion()) {
            int brd_type = 7;
            if (svl == 1) {
                brd_type = 7;
            } else if (InputSourceManager.getInstance(this.mContext).isCurrentATvSource(CommonIntegration.getInstance().getCurrentFocus())) {
                brd_type = 1;
            } else if (InputSourceManager.getInstance(this.mContext).isCurrentDTvSource(CommonIntegration.getInstance().getCurrentFocus())) {
                brd_type = 2;
            }
            MtkLog.d("TVContent", "brd_type:" + brd_type);
            new MtkTvChannelListBase().deleteChannelByBrdcstType(svl, brd_type);
        } else if (CommonIntegration.isEUPARegion()) {
            if (svl == 1) {
                MtkLog.d("TVContent", "Svl == 1,Dvbt cleanChannelList");
                MtkTvChannelListBase.cleanChannelList(svl);
            } else {
                int brd_type2 = 1;
                if (InputSourceManager.getInstance(this.mContext).isCurrentATvSource(CommonIntegration.getInstance().getCurrentFocus())) {
                    brd_type2 = 1;
                } else if (InputSourceManager.getInstance(this.mContext).isCurrentDTvSource(CommonIntegration.getInstance().getCurrentFocus())) {
                    brd_type2 = 2;
                }
                MtkLog.d("TVContent", "brd_type:" + brd_type2);
                new MtkTvChannelListBase().deleteChannelByBrdcstType(svl, brd_type2);
            }
        } else if (svl == 3 || svl == 4 || svl == 7) {
            MtkTvChannelListBase.cleanChannelList(svl, false);
        } else {
            MtkTvChannelListBase.cleanChannelList(svl);
        }
        MtkTvConfig.getInstance().setConfigValue(MtkTvConfigTypeBase.CFG_MISC_CHANNEL_STORE, 0);
    }

    public void startScanByScanManager(int scanType, ScannerListener mListener, ScanParams param) {
        stopTimeShift();
        if (param == null) {
            param = new ScanParams();
        }
        this.mScanManager.startScan(scanType, mListener, param);
    }

    public boolean isScanTaskFinish() {
        return this.mScanManager.isScanTaskFinish();
    }

    public boolean isScanning() {
        return MtkTvScan.getInstance().isScanning();
    }

    public boolean isTshitRunning() {
        MtkLog.d("TVContent", "isTshitRunning:" + this.saveV.readBooleanValue(MenuConfigManager.TIMESHIFT_START));
        return this.saveV.readBooleanValue(MenuConfigManager.TIMESHIFT_START);
    }

    public boolean isPVrRunning() {
        MtkLog.d("TVContent", "isPVRRunning:" + this.saveV.readBooleanValue(MenuConfigManager.PVR_START));
        return this.saveV.readBooleanValue(MenuConfigManager.PVR_START);
    }

    public boolean isCanScan() {
        return SaveValue.readWorldBooleanValue(this.mContext, MenuConfigManager.PVR_START) || SaveValue.readWorldBooleanValue(this.mContext, MenuConfigManager.TIMESHIFT_START) || SaveValue.readWorldBooleanValue(this.mContext, MenuConfigManager.PVR_PLAYBACK_START);
    }

    public int getDefaultNetWorkID() {
        return 104000;
    }

    public ScannerManager getScanManager() {
        return this.mScanManager;
    }

    public int updateCIKey() {
        return this.mCIBase.updateCIKey();
    }

    public int updateCIECPKey() {
        return this.mCIBase.updateCIKeyEx(1);
    }

    public int eraseCIKey() {
        return this.mCIBase.eraseCIKey();
    }

    public String getCIKeyinfo() {
        return this.mCIBase.getCIKeyinfo();
    }

    public boolean isShowCountryRegion() {
        String country = this.mTvConfig.getCountry();
        MtkLog.v("TVContent", "isShowCountryRegion country*****************" + country);
        boolean isShow = false;
        if (country.equalsIgnoreCase("AUS") || country.equalsIgnoreCase("ESP") || country.equalsIgnoreCase(MtkTvConfigTypeBase.S639_CFG_LANG_POR) || country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_PRT)) {
            if (!country.equalsIgnoreCase("ESP")) {
                isShow = true;
            } else if (getConfigValue(MtkTvConfigTypeBase.CFG_TIME_TZ_SYNC_WITH_TS) == 1) {
                isShow = true;
            }
        }
        MtkLog.v("TVContent", "isShowCountryRegion*****************" + isShow);
        return isShow;
    }

    public boolean isAusCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase("AUS")) {
            return false;
        }
        return true;
    }

    public boolean isItaCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_ITA)) {
            return false;
        }
        return true;
    }

    public boolean isEcuadorCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (country.equalsIgnoreCase("ecu")) {
            return true;
        }
        return false;
    }

    public boolean isPhiCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (country.equalsIgnoreCase("phl")) {
            return true;
        }
        return false;
    }

    public boolean isNorCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_NOR)) {
            return false;
        }
        return true;
    }

    public int getTKGSOperatorMode() {
        int ret = this.mTvConfig.getConfigValue("g_misc__tkgs_operating_mode");
        MtkLog.v("TVContent", "TKGSOperatormode==" + ret);
        return ret;
    }

    public boolean isTKGSOperator() {
        int op = this.mTvConfig.getConfigValue("g_bs__bs_sat_brdcster");
        MtkLog.v("TVContent", "isTKGSOperator,now op==" + op);
        if (op != 27) {
            return false;
        }
        MtkLog.v("TVContent", "isTKGSOperator,true");
        return true;
    }

    public boolean isTurkeyCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_TUR)) {
            return false;
        }
        return true;
    }

    public boolean isRomCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_ROU)) {
            return false;
        }
        return true;
    }

    public boolean isSgpCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase("SGP")) {
            return false;
        }
        return true;
    }

    public boolean isSouthAfricaCountry() {
        if (this.mTvConfig.getCountry().equalsIgnoreCase("ZAF")) {
            return true;
        }
        return false;
    }

    public boolean isFraCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase("FRA")) {
            return false;
        }
        return true;
    }

    public boolean isIDNCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_IDN)) {
            return true;
        }
        return false;
    }

    public boolean isMYSCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_MYS)) {
            return true;
        }
        return false;
    }

    public boolean isNZLCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase("NZL")) {
            return false;
        }
        return true;
    }

    public boolean isAUSCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase("AUS")) {
            return false;
        }
        return true;
    }

    public boolean isFinCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_FIN)) {
            return true;
        }
        return false;
    }

    public boolean isSQPCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase("SGP")) {
            return false;
        }
        return true;
    }

    public boolean isTHACountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase("THA")) {
            return false;
        }
        return true;
    }

    public boolean isVNMCountry() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_VNM)) {
            return false;
        }
        return true;
    }

    public boolean isUKCountry() {
        String country = this.mTvConfig.getCountry();
        if (!CommonIntegration.isEURegion() || !country.equalsIgnoreCase("GBR")) {
            return false;
        }
        return true;
    }

    public boolean isHKRegion() {
        return this.mTvConfig.getCountry().equals(MtkTvConfigTypeBase.S3166_CFG_COUNT_HKG);
    }

    public boolean isChineseTWN() {
        String country = this.mTvConfig.getCountry();
        MtkLog.d("TVContent", "country:" + country);
        if (!MarketRegionInfo.isFunctionSupport(16) || !country.equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_TWN)) {
            return false;
        }
        return true;
    }

    public static void createChanneListSnapshot() {
        releaseChanneListSnapshot();
        if (CommonIntegration.getInstance().getChannelAllNumByAPI() > 0) {
            snapshotID = MtkTvChannelListBase.createSnapshot(CommonIntegration.getInstance().getSvl());
        }
    }

    public static void restoreChanneListSnapshot() {
        MtkLog.printStackTrace();
        if (snapshotID != -1) {
            MtkTvChannelListBase.restoreSnapshot(snapshotID);
        }
    }

    public static void releaseChanneListSnapshot() {
        if (snapshotID != -1) {
            int result = MtkTvChannelListBase.freeSnapshot(snapshotID);
            snapshotID = -1;
        }
    }

    public static void backUpDVBSOP() {
        int currentOP;
        dvbsLastOP = -1;
        if (MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src") >= 2 && (currentOP = ScanContent.getDVBSCurrentOP()) != 0) {
            dvbsLastOP = currentOP;
        }
    }

    public static void restoreDVBSOP() {
        if (dvbsLastOP != -1) {
            MtkTvConfig.getInstance().setConfigValue("g_bs__bs_sat_brdcster", dvbsLastOP);
        }
    }

    public static void freeBackupDVBSOP() {
        dvbsLastOP = -1;
    }

    public static void backUpDVBSsatellites() {
        freeBachUpDVBSsatellites();
        mDvbsSatSnapShotId = MtkTvDvbsConfigBase.createSatlSnapshot(CommonIntegration.getInstance().getSvl());
    }

    public static void restoreDVBSsatellites() {
        if (mDvbsSatSnapShotId != -1) {
            MtkTvDvbsConfigBase.restoreSatlSnapshot(mDvbsSatSnapShotId);
        }
    }

    public static void freeBachUpDVBSsatellites() {
        if (mDvbsSatSnapShotId != -1) {
            MtkTvDvbsConfigBase.freeSatlSnapshot(mDvbsSatSnapShotId);
            mDvbsSatSnapShotId = -1;
        }
    }

    public boolean selectScanedRFChannel() {
        int frequency = 0;
        if (CommonIntegration.isEURegion()) {
            if (getScanManager().isDVBTSingleRFScan()) {
                frequency = DVBTScanner.selectedRFChannelFreq;
            } else {
                frequency = DVBCScanner.selectedRFChannelFreq;
            }
        } else if (CommonIntegration.isCNRegion()) {
            if (getScanManager().isDVBTSingleRFScan()) {
                frequency = DVBTCNScanner.selectedRFChannelFreq;
            } else {
                frequency = DVBCCNScanner.selectedRFChannelFreq;
            }
        }
        TurnkeyUiMainActivity.getInstance().resetLayout();
        return changeChannelByFreq(frequency);
    }

    public boolean changeChannelByFreq(int frequency) {
        int length = CommonIntegration.getInstance().getChannelAllNumByAPI();
        if (CommonIntegration.isCNRegion()) {
            length = CommonIntegration.getInstance().getCurrentSvlChannelNum(TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        }
        List<MtkTvChannelInfoBase> channelBaseList = CommonIntegration.getInstance().getChannelListByMaskFilter(0, 0, length, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        int i = 0;
        while (i < channelBaseList.size()) {
            if (channelBaseList.get(i).getFrequency() > 2500000 + frequency || channelBaseList.get(i).getFrequency() < frequency - 250000) {
                i++;
            } else {
                MtkLog.d("TVContent", "changeChannelByFreq channel id = " + channelBaseList.get(i).getChannelId());
                CommonIntegration.getInstance().selectChannelById(channelBaseList.get(i).getChannelId());
                return true;
            }
        }
        return false;
    }

    public boolean changeChannelByQueryFreq(int frequency) {
        MtkTvChannelQuery mMtkTvChannelQuery = new MtkTvChannelQuery();
        mMtkTvChannelQuery.setQueryType(2);
        mMtkTvChannelQuery.setFrequency(frequency);
        List<MtkTvChannelInfoBase> tempList = CommonIntegration.getInstance().getChnnelListByQueryInfo(mMtkTvChannelQuery, 1);
        StringBuilder sb = new StringBuilder();
        sb.append("channel:tempList>>");
        sb.append(tempList == null ? tempList : Integer.valueOf(tempList.size()));
        MtkLog.d("TVContent", sb.toString());
        if (tempList == null || tempList.size() <= 0) {
            return false;
        }
        MtkLog.d("TVContent", "tempList.get(0)>>" + tempList.get(0).getChannelId() + ">>>" + tempList.get(0).getServiceName());
        int curId = CommonIntegration.getInstance().getCurrentChannelId();
        boolean hasfind = false;
        int i = 0;
        while (true) {
            if (i >= tempList.size()) {
                break;
            } else if (curId == tempList.get(i).getChannelId()) {
                hasfind = true;
                break;
            } else {
                i++;
            }
        }
        MtkLog.d("TVContent", "mTV.isScanning()2>>" + isScanning());
        if (!isScanning()) {
            if (!hasfind) {
                CommonIntegration.getInstance().selectChannelById(tempList.get(0).getChannelId());
            } else if (isSignalLoss()) {
                CommonIntegration.getInstance().selectChannelById(curId);
            }
        }
        return true;
    }

    public boolean isSourceType3D() {
        int type = new MtkTvAppTVBase().GetVideoSrcTag3DType(CommonIntegration.getInstance().getCurrentFocus());
        MtkLog.d("TVContent", "isSourceType3D type:" + type);
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            case 9:
            case 10:
                return false;
            default:
                return false;
        }
    }

    public int[] dvbsGetNfySvcUpd() {
        MtkTvScanDvbsBase base = new MtkTvScanDvbsBase();
        base.dvbsGetNfySvcUpd();
        return new int[]{0 + base.nfySvcUpd_tvAdd + base.nfySvcUpd_rdAdd + base.nfySvcUpd_apAdd, 0 + base.nfySvcUpd_tvDel + base.nfySvcUpd_rdDel + base.nfySvcUpd_apDel};
    }

    public boolean isLastInputSourceVGA() {
        if (!this.lastInputSourceName.equalsIgnoreCase(MtkTvInputSourceBase.INPUT_TYPE_VGA)) {
            return false;
        }
        this.lastInputSourceName = "";
        return true;
    }

    public int getInitATVFreq() {
        MtkTvChannelInfoBase tvCh = CommonIntegration.getInstance().getCurChInfo();
        if (tvCh != null && CommonIntegration.getInstance().isCurrentSourceATV()) {
            return tvCh.getFrequency() / 1000000;
        }
        MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase.ScanPalSecamFreqRange();
        MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
        return Math.max(range.lower_freq / 1000000, 44);
    }

    public int getInitDVBCRFFreq() {
        MtkTvChannelInfoBase currentCH = CommonIntegration.getInstance().getCurChInfo();
        if (currentCH == null || !CommonIntegration.getInstance().isCurrentSourceDTV()) {
            return 306000;
        }
        return currentCH.getFrequency() / 1000;
    }

    public String getCurrInputSourceNameByApi() {
        return InputSourceManager.getInstance().getInputSourceByAPI(CommonIntegration.getInstance().getCurrentFocus());
    }

    public String getDrmRegistrationCode() {
        return this.mMtkTvMultiMediaBase.GetDrmRegistrationCode();
    }

    public String setDrmDeactivation() {
        return this.mMtkTvMultiMediaBase.SetDrmDeactivation();
    }

    public long getDrmUiHelpInfo() {
        return this.mMtkTvMultiMediaBase.GetDrmUiHelpInfo();
    }

    public void cleanLocalData() {
        if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            new Thread(new Runnable() {
                public void run() {
                    DBMgrProgramList.getInstance(TVContent.this.mContext).getWriteableDB();
                    DBMgrProgramList.getInstance(TVContent.this.mContext).deleteAllPrograms();
                    DBMgrProgramList.getInstance(TVContent.this.mContext).closeDB();
                    DataReader.getInstance(TVContent.this.mContext).cleanMStypeDB();
                }
            }).start();
        } else if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
            new Thread(new Runnable() {
                public void run() {
                    DataReader.getInstance(TVContent.this.mContext).cleanMStypeDB();
                }
            }).start();
        }
    }

    public void cleanRatings() {
        TvInputManager mTvInputManager = (TvInputManager) this.mContext.getSystemService("tv_input");
        List<TvContentRating> dvbRatings = mTvInputManager.getBlockedRatings();
        if (dvbRatings != null) {
            for (TvContentRating rating : dvbRatings) {
                mTvInputManager.removeBlockedRating(rating);
            }
        }
    }

    public void restartSelfActivity(Context context, Class cls) {
        ((Activity) context).finish();
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public int calcATSCFreq(int rfCh) {
        int resultFreq = 0;
        if (rfCh < getFirstScanIndex() || rfCh > getLastScanIndex()) {
            resultFreq = 0;
        } else if (rfCh < 5) {
            resultFreq = (rfCh * 6) + 45;
        } else if (rfCh == 5) {
            resultFreq = 79;
        } else if (rfCh == 6) {
            resultFreq = 85;
        } else if (rfCh < 14) {
            resultFreq = (rfCh * 6) + 135;
        } else if (rfCh <= getLastScanIndex()) {
            resultFreq = (rfCh * 6) + 389;
        }
        return 1000000 * resultFreq;
    }

    public int calcCQAMFreq(int rfCh) {
        switch (this.saveV.readValue(MenuConfigManager.FREQUENEY_PLAN)) {
            case 0:
                if (rfCh == 1) {
                    return 0;
                }
                if (rfCh == 5) {
                    return 79000000;
                }
                if (rfCh == 6) {
                    return 85000000;
                }
                return 0;
            case 1:
                if (rfCh == 1) {
                    return 75000000;
                }
                if (rfCh == 5) {
                    return 81000000;
                }
                if (rfCh == 6) {
                    return 87000000;
                }
                if (rfCh < 5) {
                    return (6000000 * rfCh) + 45000000;
                }
                if (rfCh < 14) {
                    return (6000000 * rfCh) + 135000000;
                }
                if (rfCh < 23) {
                    return (6000000 * rfCh) + 39000000;
                }
                if (rfCh < 95) {
                    return (6000000 * rfCh) + 81000000;
                }
                if (rfCh < 100) {
                    return ((rfCh - 95) * 6000000) + 93000000;
                }
                if (rfCh <= getLastScanIndex()) {
                    return (6000000 * rfCh) + 51000000;
                }
                return 0;
            case 2:
                if (rfCh == 1) {
                    return 73753750;
                }
                if (rfCh == 5) {
                    return 79754050;
                }
                if (rfCh == 6) {
                    return 85754350;
                }
                if (rfCh < 5) {
                    return 42002100 + rfCh + 1750150;
                }
                if (rfCh < 14) {
                    return 132006600 + rfCh + 1750150;
                }
                if (rfCh < 23) {
                    return 36001800 + rfCh + 1750150;
                }
                if (rfCh < 95) {
                    return 78003900 + rfCh + 1750150;
                }
                if (rfCh < 100) {
                    return (rfCh - 480024000) + 1750150;
                }
                if (rfCh <= getLastScanIndex()) {
                    return 48002400 + rfCh + 1750150;
                }
                return 0;
            default:
                return 0;
        }
    }

    public int calcSAFreq(int rfCh) {
        if (rfCh >= 7 && rfCh <= 13) {
            return 177143 + ((rfCh - 7) * 6000000);
        }
        if (rfCh <= 69) {
            return 473143 + ((rfCh - 14) * 6000000);
        }
        return 0;
    }

    public int getModulation() {
        return this.saveV.readValue(MenuConfigManager.TV_SINGLE_SCAN_MODULATION);
    }
}
