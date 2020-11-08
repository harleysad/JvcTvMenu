package com.mediatek.wwtv.tvcenter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.android.tv.parental.ContentRatingSystem;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvCDTChLogoBase;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvHighLevel;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsConfigInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvFavoritelistInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.setting.base.scan.model.ScanContent;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.view.ChannelListDialog;
import com.mediatek.wwtv.tvcenter.nav.view.NavCommonInfoBar;
import com.mediatek.wwtv.tvcenter.nav.view.PwdDialog;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CommonIntegration {
    public static final int ANALOG_CHANNEL_NUMBER_START = 2001;
    public static final int BRDCST_TYPE_ATV = 1;
    public static final int BRDCST_TYPE_DTV = 0;
    public static final int CATEGORIES_CHANNELNUM_BASE = 2000;
    public static final int CH_FAKE_MASK = MtkTvChCommonBase.SB_VNET_FAKE;
    public static final int CH_FAKE_VAL = MtkTvChCommonBase.SB_VNET_FAKE;
    public static final int CH_LIST_3RDCAHNNEL_MASK = -1;
    public static final int CH_LIST_3RDCAHNNEL_VAL = -1;
    public static final int CH_LIST_ANALOG_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_ANALOG_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_DIGITAL_MASK = (((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE) | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_DIGITAL_RADIO_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_DIGITAL_RADIO_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_DIGITAL_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_FREE_MASK = (MtkTvChCommonBase.SB_VNET_SCRAMBLED | MtkTvChCommonBase.SB_VNET_ACTIVE);
    public static final int CH_LIST_FREE_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_MASK = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_LIST_MASK_SKIP = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_VISIBLE);
    public static final int CH_LIST_RADIO_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE);
    public static final int CH_LIST_RADIO_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE);
    public static final int CH_LIST_SCRAMBLED_MASK = (MtkTvChCommonBase.SB_VNET_SCRAMBLED | MtkTvChCommonBase.SB_VNET_ACTIVE);
    public static final int CH_LIST_SCRAMBLED_VAL = (MtkTvChCommonBase.SB_VNET_SCRAMBLED | MtkTvChCommonBase.SB_VNET_ACTIVE);
    public static final int CH_LIST_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_VAL_NO_SKIP = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE);
    public static final int CH_LIST_VAL_SKIP = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final String CH_TYPE_BASE = "type_";
    public static final int CH_UP_DOWN_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE) | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_UP_DOWN_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE);
    public static int COUNTCHLISTACTIVE = 0;
    public static int COUNTCHLISTALL = 0;
    private static final String CUR_2ND_CHANNEL_ID = "g_misc__2nd_crnt_ch_id";
    private static final String CUR_CHANNEL_ID = "g_nav__air_crnt_ch";
    public static final int DB_AIR_OPTID = 0;
    public static final int DB_AIR_SVLID = 1;
    public static final int DB_CAB_OPTID = 1;
    public static final int DB_CAB_SVLID = 2;
    public static final int DB_CI_PLUS_SVLID_AIR = 5;
    public static final int DB_CI_PLUS_SVLID_CAB = 6;
    public static final int DB_CI_PLUS_SVLID_SAT = 7;
    public static final int DB_GENERAL_SAT_OPTID = 3;
    public static final int DB_SAT_OPTID = 2;
    public static final int DB_SAT_PRF_SVLID = 4;
    public static final int DB_SAT_SVLID = 3;
    public static final int DECREASE_NUM = 2000;
    private static final String LAST_2ND_CHANNEL_ID = "g_misc__2nd_last_ch_id";
    private static final String LAST_CHANNEL_ID = "g_nav__air_last_ch";
    public static final int M7BaseNumber = 4001;
    public static final int NOT_SUPPORT_THIRD_PIP_MODE = 0;
    public static final String OCEANIA_POSTAL = "g_eas__lct_ct";
    private static final int PAGE_COUNT = 7;
    public static final String SAT_BRDCSTER = "g_bs__bs_sat_brdcster";
    private static final String SCREEN_MODE = "g_video__screen_mode";
    public static final String SCREEN_MODE_CHANGE_AFTER = "After_ASP_Ratio_chg";
    public static final String SCREEN_MODE_CHANGE_BEFORE = "Before_ASP_Ratio_chg";
    public static final int SCREEN_MODE_DOT_BY_DOT = 6;
    public static final int SCREEN_MODE_NORMAL = 1;
    public static final String SOURCE_ATV = "ATV";
    public static final String SOURCE_DTV = "DTV";
    public static final String SOURCE_TV = "TV";
    public static final int SUPPORT_THIRD_PIP_MODE = 1;
    public static final int SVCTX_NTFY_CODE_BLOCK = 9;
    public static final int SVCTX_NTFY_CODE_CHANNEL_CHANGE = 7;
    public static final int SVCTX_NTFY_CODE_RATING = 18;
    public static final int SVCTX_NTFY_CODE_UNBLOCK = 12;
    public static final int SVCTX_NTFY_CODE_VIDEO_FMT_UPDATE = 37;
    public static final int SVCTX_NTFY_CODE_VIDEO_ONLY_SVC = 20;
    public static final int SVCTX_NTFY_CODE_VIDEO_SIGNAL_LOCKED = 4;
    private static final String TAG = "CommonIntegration";
    private static final String THIRD_PIP_MODE = "g_pip_pop__android_pop_mode";
    public static final int TTS_TIME = 90;
    private static final String TV_BRDCST_TYPE = "g_bs__bs_brdcst_type";
    private static final String TV_DUAL_TUNER_ENABLE = "g_misc__2nd_channel_enable";
    private static final String TV_FOCUS_WIN = "g_pip_pop__pip_pop_tv_focus_win";
    public static final String TV_FOCUS_WIN_MAIN = "main";
    public static final String TV_FOCUS_WIN_SUB = "sub";
    private static final String TV_MODE = "g_pip_pop__pip_pop_tv_mode";
    public static final int TV_NORMAL_MODE = 0;
    public static final int TV_PIP_MODE = 1;
    public static final int TV_POP_MODE = 2;
    public static final String WW_SKIP_MINOR = "ww.sa.skip";
    public static final String ZOOM_CHANGE_AFTER = "SAfter_Zoom_Mode_Chg";
    public static final String ZOOM_CHANGE_BEFORE = "Before_Zoom_Mode_Chg";
    private static MtkTvBroadcast chBroadCast = null;
    public static final String channelListfortypeMask = "channelListfortype";
    public static final String channelListfortypeMaskvalue = "channelListfortypeMaskvalue";
    public static final int[] favMask = {MtkTvChCommonBase.SB_VNET_FAVORITE1, MtkTvChCommonBase.SB_VNET_FAVORITE2, MtkTvChCommonBase.SB_VNET_FAVORITE3, MtkTvChCommonBase.SB_VNET_FAVORITE4};
    private static MtkTvHighLevel instanceMtkTvHighLevel;
    private static MtkTvUtil instanceMtkTvUtil = null;
    private static CommonIntegration instanceNavIntegration = null;
    /* access modifiers changed from: private */
    public static Context mContext;
    private static int mCurrentTvMode = 0;
    private static MtkTvChannelInfoBase mPreChInfo;
    /* access modifiers changed from: private */
    public static MtkTvChannelList mtkTvChList;
    private boolean doPIPPOPAction = false;
    private int hasVGASource = -1;
    private boolean iCurrentInputSourceHasSignal;
    private boolean iCurrentTVHasSignal;
    private boolean isVideoScrambled;
    private ChannelChangedListener mChListener;
    private ContentRatingSystem mContentRatingSystem;
    private boolean mIsCHChanging = false;
    private boolean mIsTurnCHAfterExitEPG = false;
    private final MtkTvAppTV mMtkTvAppTV;
    private final MtkTvCDTChLogoBase mMtkTvCDTChLogoBase;
    MtkTvConfig mMtkTvConfig;
    private final MtkTvDvbsConfigBase mMtkTvDvbsConfigBase;
    private NavCommonInfoBar mPopup;
    private String mPrivSimBFirstLine = "";
    private final MtkTvScanDvbsBase mtkTvScanDvbsBase;
    private boolean showFAVListFullToastDealy = false;
    private boolean stopTIFSetupWizardFunction = false;

    public interface ChannelChangedListener {
        void onChannelChanged();
    }

    public String getPrivSimBFirstLine() {
        return this.mPrivSimBFirstLine;
    }

    public void setPrivSimBFirstLine(String privSimBFirstLine) {
        this.mPrivSimBFirstLine = privSimBFirstLine;
    }

    public boolean isCHChanging() {
        return this.mIsCHChanging;
    }

    public void setCHChanging(boolean isCHChanging) {
        this.mIsCHChanging = isCHChanging;
    }

    public boolean isTurnCHAfterExitEPG() {
        return this.mIsTurnCHAfterExitEPG;
    }

    public void setTurnCHAfterExitEPG(boolean isTurnCHAfterExitEPG) {
        this.mIsTurnCHAfterExitEPG = isTurnCHAfterExitEPG;
    }

    public boolean isBarkChannel(MtkTvChannelInfoBase channelInfo) {
        boolean isBarkerCH = false;
        if (channelInfo != null && (channelInfo instanceof MtkTvDvbChannelInfo)) {
            MtkTvDvbChannelInfo dvbChannelInfo = (MtkTvDvbChannelInfo) channelInfo;
            MtkLog.d(TAG, "isBarkChannel----->barkerMask=" + dvbChannelInfo.getBarkerMask());
            if (dvbChannelInfo.getBarkerMask() == 1) {
                isBarkerCH = true;
            }
        }
        MtkLog.d(TAG, "isBarkChannel----->isBarkerCH=" + isBarkerCH);
        return isBarkerCH;
    }

    public boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    public void setChannelChangedListener(ChannelChangedListener listener) {
        this.mChListener = listener;
    }

    public static boolean supportTIFFunction() {
        return MarketRegionInfo.isFunctionSupport(13);
    }

    public boolean isShowFAVListFullToastDealy() {
        return this.showFAVListFullToastDealy;
    }

    public void setShowFAVListFullToastDealy(boolean showFAVListFullToastDealy2) {
        this.showFAVListFullToastDealy = showFAVListFullToastDealy2;
    }

    public boolean isDoPIPPOPAction() {
        return this.doPIPPOPAction;
    }

    public void setDoPIPPOPAction(boolean doPIPPOPAction2) {
        this.doPIPPOPAction = doPIPPOPAction2;
    }

    public boolean isStopTIFSetupWizardFunction() {
        return this.stopTIFSetupWizardFunction;
    }

    public void setStopTIFSetupWizardFunction(boolean stopTIFSetupWizardFunction2) {
        this.stopTIFSetupWizardFunction = stopTIFSetupWizardFunction2;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public boolean isContextInit() {
        return mContext != null;
    }

    private CommonIntegration() {
        chBroadCast = MtkTvBroadcast.getInstance();
        instanceMtkTvUtil = MtkTvUtil.getInstance();
        instanceMtkTvHighLevel = new MtkTvHighLevel();
        mtkTvChList = MtkTvChannelList.getInstance();
        this.mMtkTvCDTChLogoBase = new MtkTvCDTChLogoBase();
        this.mMtkTvAppTV = MtkTvAppTV.getInstance();
        this.mMtkTvDvbsConfigBase = new MtkTvDvbsConfigBase();
        this.mMtkTvConfig = MtkTvConfig.getInstance();
        this.mtkTvScanDvbsBase = new MtkTvScanDvbsBase();
        getChannelAllandActionNum();
    }

    private void initRatingSystem() {
        String country = MtkTvConfig.getInstance().getCountry();
        String convertCountry = Util.convertConurty(country);
        MtkLog.d(TAG, "country=" + country + ",convertCountry=" + convertCountry);
        this.mContentRatingSystem = TvSingletons.getSingletons().getContentRatingsManager().getContentRatingSystemByCountry(convertCountry);
    }

    public String mapRating2CustomerStr(int ratingValue, String strRating) {
        if (ratingValue > 0 || !TextUtils.isEmpty(strRating)) {
            initRatingSystem();
            if (this.mContentRatingSystem == null) {
                MtkLog.w(TAG, "mContentRatingSystem==null!");
                return "";
            }
            List<ContentRatingSystem.Rating> ratings = this.mContentRatingSystem.getRatings();
            if (ratings == null || ratings.isEmpty()) {
                MtkLog.w(TAG, "ratings==null or ratings is empty!");
                return "";
            }
            ContentRatingSystem.Rating rating = getRatingByRatingValue(ratings, ratingValue, strRating);
            return rating == null ? strRating : rating.getTitle();
        }
        MtkLog.e(TAG, "ratingValue<0  && strRating is empty!");
        return "";
    }

    private ContentRatingSystem.Rating getRatingByRatingValue(List<ContentRatingSystem.Rating> ratings, int ratingValue, String strRating) {
        MtkLog.d(TAG, "getRatingByRatingValue---->ratingValue=" + ratingValue + ",strRating=" + strRating);
        ContentRatingSystem.Rating firstRating = ratings.get(0);
        ContentRatingSystem.Rating lastRating = ratings.get(ratings.size() + -1);
        if (ratingValue == 0 && !TextUtils.isEmpty(strRating)) {
            return lastRating;
        }
        if (ratingValue < firstRating.getAgeHint() || ratingValue > lastRating.getAgeHint()) {
            MtkLog.w(TAG, "Out of bound rating age.");
            return null;
        }
        for (ContentRatingSystem.Rating rating : ratings) {
            MtkLog.d(TAG, "getRatingByRatingValue--->rating.title=" + rating.getTitle() + ",getAgeHint=" + rating.getAgeHint());
            if (ratingValue <= rating.getAgeHint()) {
                return rating;
            }
        }
        return null;
    }

    public static CommonIntegration getInstanceWithContext(Context context) {
        if (instanceNavIntegration == null) {
            instanceNavIntegration = new CommonIntegration();
        }
        if (mContext == null) {
            mContext = context;
        }
        return instanceNavIntegration;
    }

    public static CommonIntegration getInstance() {
        if (instanceNavIntegration == null) {
            instanceNavIntegration = new CommonIntegration();
        }
        return instanceNavIntegration;
    }

    protected static void remove() {
        instanceNavIntegration = null;
        chBroadCast = null;
        instanceMtkTvUtil = null;
        instanceMtkTvHighLevel = null;
        mtkTvChList = null;
    }

    public static MtkTvBroadcast getInstanceMtkTvBroadcast() {
        if (chBroadCast == null) {
            chBroadCast = MtkTvBroadcast.getInstance();
        }
        return chBroadCast;
    }

    public static MtkTvUtil getInstanceMtkTvUtil() {
        if (instanceMtkTvUtil == null) {
            instanceMtkTvUtil = MtkTvUtil.getInstance();
        }
        return instanceMtkTvUtil;
    }

    public static MtkTvHighLevel getInstanceMtkTvHighLevel() {
        if (instanceMtkTvHighLevel == null) {
            instanceMtkTvHighLevel = new MtkTvHighLevel();
        }
        return instanceMtkTvHighLevel;
    }

    public boolean isCurCHAnalog() {
        boolean isCurCHAnalog = false;
        MtkTvChannelInfoBase mtkchannelinfo = getCurChInfo();
        if (mtkchannelinfo != null) {
            isCurCHAnalog = mtkchannelinfo.isAnalogService();
        }
        MtkLog.d(TAG, "isCurCHAnalog = " + isCurCHAnalog);
        return isCurCHAnalog;
    }

    public boolean isCurrentSourceTv() {
        boolean isTv = InputSourceManager.getInstance().isCurrentTvSource(getCurrentFocus());
        MtkLog.d(TAG, "isTv = " + isTv);
        return isTv;
    }

    public boolean is3rdTVSource() {
        boolean is3rdTVSource = false;
        TIFChannelInfo tIFChannelInfo = TIFChannelManager.getInstance(mContext).getChannelInfoByUri();
        if (tIFChannelInfo != null && (tIFChannelInfo.mDataValue == null || tIFChannelInfo.mDataValue.length != 9)) {
            is3rdTVSource = true;
        }
        MtkLog.d(TAG, "is3rdTVSource = " + is3rdTVSource);
        return is3rdTVSource;
    }

    public static boolean is3rdTVSource(TvInputInfo input) {
        if (input == null) {
            return false;
        }
        MtkLog.d(TAG, "is3rdTVSource, input.getId() = " + input.getId() + "  :" + input.getHdmiDeviceInfo());
        if (input.getId().contains("/HW") || input.getHdmiDeviceInfo() != null) {
            return false;
        }
        return true;
    }

    public TvInputInfo getTvInputInfo() {
        return InputSourceManager.getInstance().getTvInputInfo();
    }

    public boolean isCurrentSourceHDMI() {
        return InputSourceManager.getInstance().isCurrentHDMISource(getCurrentFocus());
    }

    public String getCurrentSource() {
        String current = InputSourceManager.getInstance().getCurrentInputSourceName(getCurrentFocus());
        if (TextUtils.isEmpty(current)) {
            return "";
        }
        return current;
    }

    public boolean isCurrentSourceTv(String output) {
        return InputSourceManager.getInstance().isCurrentTvSource(output);
    }

    public int getAnalogChannelDisplayNumInt(int orignalNum) {
        if (orignalNum < 2001) {
            MtkLog.d(TAG, "getAnalogChannelDisplayNumInt invalid original channel number:" + orignalNum);
            return orignalNum;
        }
        int displayNum = (orignalNum - 2001) + 1;
        MtkLog.d(TAG, "getAnalogChannelDisplayNumInt channel number:" + displayNum);
        return displayNum;
    }

    public int getAnalogChannelDisplayNumInt(String orignalNum) {
        try {
            int displayNum = Integer.parseInt(orignalNum);
            if (displayNum < 2001) {
                MtkLog.d(TAG, "getAnalogChannelDisplayNumInt invalid original channel number:" + displayNum);
                return displayNum;
            }
            int displayNum2 = (displayNum - 2001) + 1;
            MtkLog.d(TAG, "getAnalogChannelDisplayNumInt channel number:" + displayNum2);
            return displayNum2;
        } catch (Exception e) {
            MtkLog.d(TAG, "getAnalogChannelDisplayNumInt invalid channel number:" + orignalNum);
            return -1;
        }
    }

    public int getAnalogChannelOrignalNumInt(int displayNum) {
        if (displayNum < 1) {
            MtkLog.d(TAG, "getAnalogChannelOrignalNumInt invalid displayNum:" + displayNum);
            return displayNum;
        }
        int originalNum = (displayNum + ANALOG_CHANNEL_NUMBER_START) - 1;
        MtkLog.d(TAG, "getAnalogChannelOrignalNumInt channel number:" + originalNum);
        return originalNum;
    }

    public int getAnalogChannelOrignalNumInt(String displayNum) {
        try {
            int originalNum = Integer.parseInt(displayNum);
            if (originalNum < 1) {
                MtkLog.d(TAG, "getAnalogChannelOrignalNumInt invalid originalNum:" + originalNum);
                return originalNum;
            }
            int originalNum2 = (originalNum + ANALOG_CHANNEL_NUMBER_START) - 1;
            MtkLog.d(TAG, "getAnalogChannelOrignalNumInt channel number:" + originalNum2);
            return originalNum2;
        } catch (Exception e) {
            MtkLog.d(TAG, "getAnalogChannelOrignalNumInt invalid channel number:" + displayNum);
            return -1;
        }
    }

    public boolean isCurrentSourceBlocked() {
        return InputSourceManager.getInstance().isCurrentSourceBlocked(getCurrentFocus());
    }

    public boolean isCurrentSourceBlockEx() {
        return InputSourceManager.getInstance().isCurrentInputBlockEx(getCurrentFocus());
    }

    public boolean isMenuInputTvBlock() {
        boolean ret = false;
        PwdDialog pwdDialog = (PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG);
        if (InputSourceManager.getInstance().isTvInputBlocked() || (pwdDialog != null && pwdDialog.isContentBlock())) {
            ret = true;
        }
        MtkLog.d("isMenuInputTvBlock", "biaoqing ret ==" + ret);
        return ret;
    }

    public void iSetSourcetoTv() {
        if (supportTIFFunction()) {
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    if (MarketRegionInfo.getCurrentMarketRegion() != 0 && !CommonIntegration.isEUPARegion()) {
                        InputSourceManager.getInstance().changeCurrentInputSourceByName("TV");
                    } else if (CommonIntegration.getInstance().getCurChInfo() instanceof MtkTvAnalogChannelInfo) {
                        InputSourceManager.getInstance().changeCurrentInputSourceByName("ATV");
                    } else {
                        InputSourceManager.getInstance().changeCurrentInputSourceByName("DTV");
                    }
                }
            });
        } else {
            InputSourceManager.getInstance().changeCurrentInputSourceByName(MtkTvInputSourceBase.INPUT_TYPE_TV);
        }
    }

    public void iSetSourcetoTv(final int channelId) {
        if (supportTIFFunction()) {
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    InputSourceManager.getInstance().changeToTVAndSelectChannel("TV", channelId);
                }
            });
        } else {
            InputSourceManager.getInstance().changeCurrentInputSourceByName(MtkTvInputSourceBase.INPUT_TYPE_TV);
        }
    }

    public boolean isPipOrPopState() {
        if (mCurrentTvMode == 0) {
            return false;
        }
        if (!MarketRegionInfo.isFunctionSupport(26)) {
            mCurrentTvMode = getInstanceMtkTvHighLevel().getCurrentTvMode();
        }
        if (1 == mCurrentTvMode || 2 == mCurrentTvMode) {
            return true;
        }
        return false;
    }

    public boolean isPIPState() {
        if (mCurrentTvMode == 0) {
            return false;
        }
        if (!MarketRegionInfo.isFunctionSupport(26)) {
            mCurrentTvMode = instanceMtkTvHighLevel.getCurrentTvMode();
        }
        if (1 == mCurrentTvMode) {
            return true;
        }
        return false;
    }

    public boolean isPOPState() {
        if (mCurrentTvMode == 0) {
            return false;
        }
        if (!MarketRegionInfo.isFunctionSupport(26)) {
            mCurrentTvMode = instanceMtkTvHighLevel.getCurrentTvMode();
        }
        if (2 == mCurrentTvMode) {
            return true;
        }
        return false;
    }

    public boolean isTVNormalState() {
        if (mCurrentTvMode != 0 && !MarketRegionInfo.isFunctionSupport(26)) {
            mCurrentTvMode = instanceMtkTvHighLevel.getCurrentTvMode();
        }
        if (mCurrentTvMode == 0) {
            return true;
        }
        return false;
    }

    public static boolean isUSRegion() {
        return MarketRegionInfo.getCurrentMarketRegion() == 1;
    }

    public static boolean isSARegion() {
        return MarketRegionInfo.getCurrentMarketRegion() == 2;
    }

    public static boolean isEURegion() {
        return MarketRegionInfo.getCurrentMarketRegion() == 3;
    }

    public static boolean isEUPARegion() {
        if (isEURegion()) {
            return MarketRegionInfo.isFunctionSupport(16);
        }
        return false;
    }

    public static boolean isCNRegion() {
        return MarketRegionInfo.getCurrentMarketRegion() == 0;
    }

    public boolean isCurrentSourceDTV() {
        boolean isDTV = false;
        AbstractInput input = getCurrentAbstractInput();
        if (input != null) {
            if (input.isTV()) {
                MtkTvChannelInfoBase chInfo = getCurChInfo();
                if (chInfo != null && chInfo.getBrdcstType() > 1) {
                    isDTV = true;
                }
            } else {
                isDTV = input.isDTV();
            }
        }
        MtkLog.d(TAG, "isCurrentSourceDTV isDTV =" + isDTV);
        return isDTV;
    }

    public boolean isCurrentSourceATV() {
        boolean isATV = false;
        AbstractInput input = getCurrentAbstractInput();
        if (input != null) {
            if (input.isTV()) {
                isATV = isCurCHAnalog();
            } else {
                isATV = input.isATV();
            }
        }
        MtkLog.d(TAG, "isCurrentSourceATV isATV =" + isATV);
        return isATV;
    }

    public boolean isCurrentSourceDTVforEuPA() {
        AbstractInput input = getCurrentAbstractInput();
        return input != null && input.isDTV();
    }

    public boolean isCurrentSourceATVforEuPA() {
        AbstractInput input = getCurrentAbstractInput();
        return input != null && input.isATV();
    }

    public boolean isCurrentSourceVGA() {
        AbstractInput input = getCurrentAbstractInput();
        return input != null && input.isVGA();
    }

    private AbstractInput getCurrentAbstractInput() {
        return InputUtil.getInput(TvSingletons.getSingletons().getInputSourceManager().getCurrentInputSourceHardwareId());
    }

    public void stopMainOrSubTv() {
        if (getCurrentFocus().equalsIgnoreCase("main")) {
            MtkTvBroadcast.getInstance().syncStop("main", false);
        } else if (getCurrentFocus().equalsIgnoreCase("sub")) {
            MtkTvBroadcast.getInstance().syncStop("sub", false);
        }
    }

    public void startMainOrSubTv() {
        if (getCurrentFocus().equalsIgnoreCase("main")) {
            instanceMtkTvHighLevel.startTV();
        } else if (getCurrentFocus().equalsIgnoreCase("sub")) {
            selectChannelById(getCurrentChannelId());
        }
    }

    public void iSetCurrentChannelFavorite() {
        int chId = getCurrentChannelId();
        List<MtkTvFavoritelistInfoBase> favList = MtkTvChannelList.getInstance().getFavoritelistByFilter();
        for (int i = 0; i < favList.size(); i++) {
            if (favList.get(i).getChannelId() == chId) {
                MtkTvChannelList.getInstance().removeFavoritelistChannel(i);
                MtkTvChannelList.getInstance().storeFavoritelistChannel();
                return;
            }
        }
        if (!getInstance().isFavListFull()) {
            MtkTvChannelList.getInstance().addFavoritelistChannel();
            MtkTvChannelList.getInstance().storeFavoritelistChannel();
            return;
        }
        ChannelListDialog mChannelListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChannelListDialog == null || !mChannelListDialog.isShowing()) {
            showFavFullMsg();
        } else {
            setShowFAVListFullToastDealy(true);
        }
    }

    public void iSetChannelFavorite(MtkTvChannelInfoBase chInfo) {
    }

    public boolean selectChannelByNum(int majorNo, int minorNo) {
        int result;
        MtkTvChannelInfoBase curCh = getCurChInfo();
        if (minorNo == -1) {
            result = chBroadCast.channelSelectByChannelNumber(majorNo);
        } else {
            result = chBroadCast.channelSelectByChannelNumber(majorNo, minorNo);
        }
        MtkLog.d(TAG, "selectChannelByNum majorNo = " + majorNo + " minorNo = " + minorNo + "result =" + result);
        if (result != 0) {
            return false;
        }
        if (this.mChListener != null && (checkChMask(curCh, CH_LIST_MASK, 0) || checkCurChMask(CH_LIST_MASK, 0))) {
            this.mChListener.onChannelChanged();
        }
        mPreChInfo = curCh;
        return true;
    }

    public boolean selectChannelByInfo(MtkTvChannelInfoBase chInfo) {
        MtkLog.d(TAG, "selectChannelByInfo chInfo = " + chInfo);
        if (supportTIFFunction()) {
            TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(mContext).getTIFChannelInfoById(chInfo.getChannelId());
            if (tifChannelInfo != null) {
                return TIFChannelManager.getInstance(mContext).selectChannelByTIFInfo(tifChannelInfo);
            }
            return false;
        }
        if (!(chInfo == null || chInfo.getChannelId() == getCurrentChannelId())) {
            ComponentStatusListener.getInstance().updateStatus(10, 0);
        }
        MtkTvChannelInfoBase curCh = getCurChInfo();
        int result = chBroadCast.channelSelect(chInfo, false);
        MtkLog.d(TAG, "selectChannelByInfo selected curInfo  = " + getCurChInfo());
        if (result != 0) {
            return false;
        }
        if (this.mChListener != null && (checkChMask(curCh, CH_LIST_MASK, 0) || checkCurChMask(CH_LIST_MASK, 0))) {
            this.mChListener.onChannelChanged();
        }
        mPreChInfo = curCh;
        return true;
    }

    public int getLastChannelId() {
        int chId = MtkTvConfig.getInstance().getConfigValue("g_nav__air_last_ch");
        MtkLog.d(TAG, "getLastChannelId chId = " + chId);
        return chId;
    }

    public int getCurrentChannelId() {
        TIFChannelInfo mTIFChannelInfo;
        int chId = MtkTvConfig.getInstance().getConfigValue("g_nav__air_crnt_ch");
        MtkLog.d(TAG, "CUR_CHANNEL_ID:" + chId);
        if (!isCurrentSourceATVforEuPA() && mContext != null && (mTIFChannelInfo = TIFChannelManager.getInstance(mContext).getChannelInfoByUri()) != null && (mTIFChannelInfo.mDataValue == null || mTIFChannelInfo.mDataValue.length != 9)) {
            MtkLog.d(TAG, "getCurrentChannelId 3rd chId");
            chId = (int) mTIFChannelInfo.mId;
        }
        MtkLog.d(TAG, "getCurrentChannelId chId = " + chId);
        return chId;
    }

    public int get2NDLastChannelId() {
        int chId = MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_last_ch_id");
        MtkLog.d(TAG, "getLastChannelId chId = " + chId);
        return chId;
    }

    public int setCurrentChannelId(int num_id) {
        int result = MtkTvConfig.getInstance().setConfigValue("g_nav__air_crnt_ch", num_id);
        MtkLog.d(TAG, "setCurrentChannelId result = " + result);
        return result;
    }

    public int get2NDCurrentChannelId() {
        int chId = MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_crnt_ch_id");
        MtkLog.d(TAG, "getCurrentChannelId chId = " + chId);
        return chId;
    }

    public MtkTvChannelInfoBase getCurChInfo() {
        if (isdualtunermode()) {
            int chId = get2NDCurrentChannelId();
            MtkTvChannelInfoBase curCh = TIFChannelManager.getInstance(mContext).getAPIChannelInfoById(chId);
            MtkLog.d(TAG, "getCurChInfo chId = " + chId + "curCh=" + curCh);
            return curCh;
        }
        MtkTvChannelInfoBase curCh2 = MtkTvChannelList.getCurrentChannel();
        MtkLog.d(TAG, "getCurChInfo for mtkapi chId = " + 0 + "curCh=" + curCh2);
        return curCh2;
    }

    public void selectTIFChannelInfoByChannelIdForTuneMode() {
        MtkLog.d(TAG, "selectTIFChannelInfoByChannelIdForTuneMode  = ");
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                TIFChannelManager.getInstance(CommonIntegration.mContext).selectTIFChannelInfoByChannelIdForTuneMode();
            }
        });
    }

    public int getChannelIdByConfigId() {
        int chId;
        if (isdualtunermode()) {
            chId = get2NDCurrentChannelId();
        } else {
            chId = MtkTvConfig.getInstance().getConfigValue("g_nav__air_crnt_ch");
        }
        MtkLog.d(TAG, "getChannelIdByConfigId chId = " + chId);
        return chId;
    }

    public MtkTvChannelInfoBase getCurChInfoByTIF() {
        int chId = getCurrentChannelId();
        MtkTvChannelInfoBase curCh = TIFChannelManager.getInstance(mContext).getAPIChannelInfoById(chId);
        MtkLog.d(TAG, "getCurChInfoByTIF chId = " + chId + "curCh=" + curCh);
        return curCh;
    }

    public boolean checkCurChMask(int mask, int val) {
        int chId = getCurrentChannelId();
        MtkTvChannelInfoBase curCh = TIFChannelManager.getInstance(mContext).getAPIChannelInfoByChannelId(chId);
        MtkLog.d(TAG, "checkCurChMask chId = " + chId + "curCh=" + curCh);
        if (curCh != null) {
            MtkLog.d(TAG, "checkCurChMask curCh.getNwMask() = " + curCh.getNwMask() + "mask=" + mask + "val=" + val);
            if ((curCh.getNwMask() & mask) != val) {
                return false;
            }
            MtkLog.d(TAG, "checkCurChMask true");
            return true;
        } else if (chId <= 0) {
            return false;
        } else {
            MtkLog.d(TAG, "checkCurChMask 3rd chId ");
            TIFChannelInfo mTIFChannelInfo = TIFChannelManager.getInstance(mContext).getTifChannelInfoByUri(TIFChannelManager.getInstance(mContext).buildChannelUri((long) chId));
            if (mTIFChannelInfo == null || mTIFChannelInfo.mMtkTvChannelInfo != null) {
                return false;
            }
            if (mask != -1 && !isDisableColorKey()) {
                return false;
            }
            MtkLog.d(TAG, "checkCurChMask 3rd chId true");
            return true;
        }
    }

    public boolean checkChMask(MtkTvChannelInfoBase chinfo, int mask, int val) {
        MtkTvChannelInfoBase curCh = chinfo;
        MtkLog.d(TAG, "checkChMask chId = curCh=" + curCh);
        if (curCh == null) {
            return false;
        }
        MtkLog.d(TAG, "checkChMask curCh.getNwMask() = " + curCh.getNwMask() + "mask=" + mask + "val=" + val);
        if ((curCh.getNwMask() & mask) != val) {
            return false;
        }
        MtkLog.d(TAG, "checkChMask true");
        return true;
    }

    public int getSvlFromACFG() {
        int svlId = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_svl_id");
        MtkLog.d(TAG, "getSvlFromACFG>>>>" + svlId);
        return svlId;
    }

    public int getTunerModeFromSvlId(int svlId) {
        switch (svlId) {
            case 1:
            case 5:
                return 0;
            case 2:
            case 6:
                return 1;
            case 3:
            case 7:
                if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                    return 2;
                }
                return 3;
            case 4:
                return 2;
            default:
                return 0;
        }
    }

    public void setChanenlListType(boolean isNetwork) {
        int type = 0;
        int mask = CH_LIST_MASK;
        int maskvalue = CH_LIST_VAL;
        if (isNetwork) {
            type = ((ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST)).getChannelNetworkIndex();
            mask = -1;
            maskvalue = -1;
        }
        SaveValue instance = SaveValue.getInstance(mContext);
        instance.saveValue(CH_TYPE_BASE + getSvl(), type);
        SaveValue.getInstance(mContext).saveValue(channelListfortypeMask, mask);
        SaveValue.getInstance(mContext).saveValue(channelListfortypeMaskvalue, maskvalue);
    }

    public int getSvl() {
        return getSvlFromACFG();
    }

    public int getTunerMode() {
        return MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
    }

    public int get2ndTunerMode() {
        return MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_tuner_type");
    }

    public boolean isdualtunermode() {
        if (!getCurrentFocus().equalsIgnoreCase("sub") || !MarketRegionInfo.isFunctionSupport(32) || !isCurrentSourceTv()) {
            return false;
        }
        return true;
    }

    public boolean isPreferSatMode() {
        if (isdualtunermode()) {
            if (MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_prefer_ch_lst") == 0 || get2ndTunerMode() < 2) {
                return false;
            }
            return true;
        } else if (MtkTvConfig.getInstance().getConfigValue("g_two_sat_chlist__preferred_sat") == 0 || getTunerMode() < 2) {
            return false;
        } else {
            return true;
        }
    }

    public static void resetBlueMuteFor3rd(Context mContext2) {
        MtkLog.d("LiveTvSetting", "resetBlueMuteFor3rd BlueMute defvalue !=0");
        Intent intent = new Intent("mtk.intent.blue.mute");
        intent.putExtra(NotificationCompat.CATEGORY_STATUS, "disable");
        mContext2.sendBroadcast(intent);
    }

    public static void resetBlueMuteForLiveTv(Context mContext2) {
        MtkLog.d("LiveTvSetting", "resetBlueMuteForLiveTv defValue==0 && oldValue!=0");
        Intent intent = new Intent("mtk.intent.blue.mute");
        intent.putExtra(NotificationCompat.CATEGORY_STATUS, "update");
        mContext2.sendBroadcast(intent);
    }

    public boolean isGeneralSatMode() {
        return MtkTvConfig.getInstance().getConfigValue("g_two_sat_chlist__preferred_sat") == 0 && getTunerMode() >= 2;
    }

    public int getChUpDownFilter() {
        int filter = 0;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 1:
            case 2:
            case 3:
                filter = MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE;
                break;
        }
        MtkLog.d(TAG, "getChUpDownFilter filter =" + filter);
        return filter;
    }

    public int getChListFilter() {
        int filter = 0;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 1:
            case 2:
            case 3:
                filter = MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE;
                break;
        }
        MtkLog.d(TAG, "getChListFilter filter =" + filter);
        return filter;
    }

    public int getChUpDownFilterEPG() {
        int filter = 0;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 2:
            case 3:
                filter = MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE;
                break;
            case 1:
                filter = MtkTvChCommonBase.SB_VNET_EPG | MtkTvChCommonBase.SB_VNET_VISIBLE;
                break;
        }
        MtkLog.d(TAG, "getChUpDownFilter filter =" + filter);
        return filter;
    }

    public int getChListFilterEPG() {
        int filter = 0;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 2:
            case 3:
                filter = MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE;
                break;
            case 1:
                filter = MtkTvChCommonBase.SB_VNET_EPG | MtkTvChCommonBase.SB_VNET_FAKE;
                break;
        }
        MtkLog.d(TAG, "getChListFilter filter =" + filter);
        return filter;
    }

    public TIFChannelInfo getFirstATVChannelList() {
        return getFirstChannelList(CH_LIST_ANALOG_MASK, CH_LIST_ANALOG_VAL);
    }

    public TIFChannelInfo getFirstDTVChannelList() {
        return getFirstChannelList(CH_LIST_DIGITAL_MASK, CH_LIST_DIGITAL_VAL);
    }

    public TIFChannelInfo getFirstChannelList(int mask, int val) {
        MtkLog.d(TAG, "getFirstChannelList " + mask + " " + val);
        List<MtkTvChannelInfoBase> channelList = mtkTvChList.getChannelListByMask(getSvlFromACFG(), mask, val, 0, -1, 1);
        StringBuilder sb = new StringBuilder();
        sb.append("getFirstChannelList size:");
        sb.append(channelList.size());
        MtkLog.d(TAG, sb.toString());
        if (channelList.size() != 1) {
            return null;
        }
        List<TIFChannelInfo> tIFChannelList = TIFFunctionUtil.getTIFChannelList(channelList);
        MtkTvChannelInfoBase channel = channelList.get(0);
        if (channel == null) {
            return null;
        }
        MtkLog.d(TAG, channel.toString());
        return TIFChannelManager.getInstance(mContext).queryChannelById(channel.getChannelId());
    }

    public int getChannelActiveNumByAPI() {
        MtkLog.d(TAG, "getchannellength getChannelNumByAPI COUNTCHLISTACTIVE = " + COUNTCHLISTACTIVE);
        return COUNTCHLISTACTIVE;
    }

    public int getChannelActiveNumByAPIForScan() {
        int num = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_ACTIVE);
        MtkLog.d(TAG, "getChannelActiveNumByAPIForScan num = " + num);
        return num;
    }

    public int getChannelAllNumByAPI() {
        MtkLog.d(TAG, "getchannellength getChannelAllNumByAPI COUNTCHLISTALL = " + COUNTCHLISTALL);
        return COUNTCHLISTALL;
    }

    public int getBlockChannelNum() {
        int len = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_BLOCKED);
        MtkLog.d(TAG, "getchannellength getBlockChannelNum len = " + len);
        return len;
    }

    public int getBlockChannelNumForSource() {
        int mask = 0;
        int len = 0;
        if (isCNRegion() || isEUPARegion()) {
            if (isCurrentSourceATVforEuPA()) {
                mask = MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE | MtkTvChCommonBase.SB_VNET_BLOCKED;
            } else if (isCurrentSourceDTVforEuPA()) {
                mask = MtkTvChCommonBase.SB_VNET_BLOCKED;
            }
            if (mask != 0) {
                len = mtkTvChList.getChannelCountByMask(getSvl(), MtkTvChCommonBase.SB_VNET_BLOCKED | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE, mask);
            }
        }
        if (mask == 0) {
            len = mtkTvChList.getChannelCountByMask(getSvl(), MtkTvChCommonBase.SB_VNET_BLOCKED, MtkTvChCommonBase.SB_VNET_BLOCKED);
        }
        MtkLog.d(TAG, "getchannellength getBlockChannelNumForSource len = " + len);
        return len;
    }

    public int getChannelCountFromSVL() {
        int channelCount = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_ALL);
        MtkLog.d(TAG, "getChannelCountFromSVL channelCount = " + channelCount);
        return channelCount;
    }

    public void getChannelAllandActionNum() {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                CommonIntegration.COUNTCHLISTALL = CommonIntegration.mtkTvChList.getChannelCountByFilter(CommonIntegration.this.getSvl(), MtkTvChCommonBase.SB_VNET_ALL);
                CommonIntegration.COUNTCHLISTACTIVE = CommonIntegration.mtkTvChList.getChannelCountByFilter(CommonIntegration.this.getSvl(), MtkTvChCommonBase.SB_VNET_ACTIVE);
            }
        });
    }

    public int getAllChannelListByTIFFor3rdSource() {
        new ArrayList();
        List<TIFChannelInfo> mTIFChannelInfoList = TIFChannelManager.getInstance(mContext).get3RDChannelList();
        MtkLog.d(TAG, "getchannellength mTIFChannelInfoList= " + mTIFChannelInfoList);
        if (mTIFChannelInfoList != null) {
            return mTIFChannelInfoList.size();
        }
        return 0;
    }

    public int getCurrentSvlChannelNum(int mask, int val) {
        return mtkTvChList.getChannelCountByMask(getSvl(), mask, val);
    }

    public int getAllEPGChannelLength() {
        return getAllEPGChannelLength(false);
    }

    public int getAllEPGChannelLength(boolean showSkip) {
        int len = 0;
        if (!supportTIFFunction()) {
            len = mtkTvChList.getChannelCountByFilter(getSvl(), getChListFilterEPG());
        } else if (TIFChannelManager.getInstance(mContext).hasActiveChannel(showSkip)) {
            len = 1;
        }
        MtkLog.d(TAG, "getchannellength getAllEPGChannelLength len = " + len);
        return len;
    }

    public boolean hasDTVChannels() {
        if (supportTIFFunction()) {
            return TIFChannelManager.getInstance(mContext).hasDTVChannels();
        }
        return false;
    }

    public boolean isDualChannellist() {
        if (MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_channel_enable") != 1 || !getCurrentFocus().equalsIgnoreCase("sub") || !isCurrentSourceTv()) {
            return false;
        }
        return true;
    }

    public boolean hasActiveChannel() {
        return hasActiveChannel(false);
    }

    public boolean hasActiveChannel(boolean showSkip) {
        if (isDualChannellist()) {
            List<MtkTvChannelInfoBase> chList = getChannelListByMaskFilter(0, 0, 1, CH_LIST_MASK, CH_LIST_VAL);
            return chList != null && chList.size() > 0;
        } else if (supportTIFFunction()) {
            MtkLog.d(TAG, "hasActiveChannel tif ");
            if (showSkip) {
                if (mtkTvChList.getChannelCountByMask(getSvl(), CH_LIST_MASK, CH_LIST_VAL) > 0) {
                    return true;
                }
                return false;
            } else if (mtkTvChList.getChannelCountByMask(getSvl(), CH_LIST_MASK_SKIP, CH_LIST_VAL_NO_SKIP) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            List<MtkTvChannelInfoBase> chList2 = getChannelListByMaskFilter(0, 0, 1, CH_LIST_MASK, CH_LIST_VAL);
            if (chList2 != null && chList2.size() > 0) {
                MtkLog.d(TAG, "hasActiveChannel true~ ");
                return true;
            } else if (MarketRegionInfo.getCurrentMarketRegion() == 1 && checkCurChMask(CH_LIST_MASK, 0)) {
                return true;
            } else {
                MtkLog.d(TAG, "hasActiveChannel false ");
                return false;
            }
        }
    }

    public boolean hasChannels() {
        if (supportTIFFunction()) {
            return TIFChannelManager.getInstance(mContext).isChannelsExist();
        }
        List<MtkTvChannelInfoBase> chList = getChannelListByMaskFilter(0, 0, 1, TIFFunctionUtil.CH_MASK_ALL, TIFFunctionUtil.CH_MASK_ALL);
        if (chList == null || chList.size() <= 0) {
            MtkLog.d(TAG, "hasChannels false ");
            return false;
        }
        MtkLog.d(TAG, "hasChannels true~ ");
        return true;
    }

    public int hasNextPageChannel(int count, int mask, int val) {
        int value = 0;
        List<MtkTvChannelInfoBase> chList = getChannelListByMaskFilter(0, 0, count, mask, val);
        if (chList != null) {
            MtkLog.d(TAG, "hasNextPageChannel true~ ");
            value = chList.size();
            if (value == count) {
                return value;
            }
        }
        if (MarketRegionInfo.getCurrentMarketRegion() == 1 && checkCurChMask(CH_LIST_MASK, 0)) {
            value++;
        }
        MtkLog.d(TAG, "hasNextPageChannel false~ value = " + value);
        return value;
    }

    public boolean channelUpDownByMaskAndSat(int mask, int val, int satRecId, boolean isUp) {
        boolean result = doChannelUpDownByMaskAndSat(mask, val, satRecId, isUp);
        if (result) {
            this.mIsCHChanging = true;
        }
        return result;
    }

    private boolean doChannelUpDownByMaskAndSat(int mask, int val, int satRecId, boolean isUp) {
        ArrayList<String> sourceList;
        MtkLog.d(TAG, "channelUpDownByMaskAndSat");
        if (!isPipOrPopState() || (sourceList = InputSourceManager.getInstance().getConflictSourceList()) == null || !sourceList.contains("TV")) {
            if (!isCurrentSourceTv()) {
                iSetSourcetoTv();
            } else if (isMenuInputTvBlock()) {
                return false;
            } else {
                MtkTvChannelInfoBase chInfo = null;
                List<MtkTvChannelInfoBase> chInfoList = getChannelListByMaskAndSat(getCurrentChannelId(), isUp ? 2 : 3, 1, mask, val, satRecId);
                if (chInfoList != null && chInfoList.size() > 0) {
                    chInfo = chInfoList.get(0);
                }
                MtkLog.d(TAG, "channelUpDownByMaskAndSat chInfo = " + chInfo);
                if (chInfo != null) {
                    return selectChannelByInfo(chInfo);
                }
            }
            return false;
        }
        MtkLog.d(TAG, "input source in conflict");
        return false;
    }

    public boolean channelUpDownByMask(boolean isUp, int mask, int val) {
        boolean result = doChannelUpDownByMask(isUp, mask, val);
        if (result) {
            this.mIsCHChanging = true;
        }
        return result;
    }

    private boolean doChannelUpDownByMask(boolean isUp, int mask, int val) {
        ArrayList<String> sourceList;
        MtkLog.d(TAG, "channelUpDownByMask");
        if (!isPipOrPopState() || (sourceList = InputSourceManager.getInstance().getConflictSourceList()) == null || !sourceList.contains("TV")) {
            if (!isCurrentSourceTv()) {
                iSetSourcetoTv();
            } else if (isMenuInputTvBlock()) {
                return false;
            } else {
                MtkTvChannelInfoBase chInfo = getUpDownChInfoByMask(isUp, mask, val);
                MtkLog.d(TAG, "channelUp chInfo = " + chInfo);
                if (chInfo != null) {
                    return selectChannelByInfo(chInfo);
                }
            }
            return false;
        }
        MtkLog.d(TAG, "input source in conflict");
        return false;
    }

    public boolean channelUp() {
        boolean result = false;
        ChannelListDialog mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChListDialog != null) {
            result = mChListDialog.channelUpDown(true);
        }
        if (result) {
            this.mIsCHChanging = true;
        }
        MtkLog.d(TAG, "channelUp result=" + result);
        return result;
    }

    public boolean channelUpDownNoVisible(boolean isUp) {
        ChannelListDialog mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChListDialog != null) {
            return mChListDialog.channelUpDownNoVisible(isUp);
        }
        return false;
    }

    public boolean channelPre() {
        boolean result = doChannelPre();
        if (result) {
            this.mIsCHChanging = true;
        }
        return result;
    }

    private boolean doChannelPre() {
        ArrayList<String> sourceList;
        MtkLog.d(TAG, "channelPre mPreChInfo =" + mPreChInfo);
        if (supportTIFFunction()) {
            MtkLog.d(TAG, "channelPre supportTIFFunction");
            if (!is3rdTVSource()) {
                MtkLog.d(TAG, "channelPre supportTIFFunction !is3rdTVSource()");
                return TIFChannelManager.getInstance(mContext).channelPre();
            }
            MtkLog.d(TAG, "channelPre supportTIFFunction is3rdTVSource()");
            return false;
        } else if (isCurrentSourceTv() && isMenuInputTvBlock()) {
            return false;
        } else {
            if (isPipOrPopState() && (sourceList = InputSourceManager.getInstance().getConflictSourceList()) != null && sourceList.contains("TV")) {
                MtkLog.d(TAG, "input source in conflict");
                return false;
            } else if (mPreChInfo != null) {
                return selectChannelByInfo(mPreChInfo);
            } else {
                return selectChannelByInfo(getCurChInfo());
            }
        }
    }

    public boolean channelDown() {
        MtkLog.d(TAG, "channelDown");
        boolean result = false;
        ChannelListDialog mChListDialog = (ChannelListDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (mChListDialog != null) {
            result = mChListDialog.channelUpDown(false);
        }
        if (result) {
            this.mIsCHChanging = true;
        }
        MtkLog.d(TAG, "channelDown result=" + result);
        return result;
    }

    public boolean selectChannelById(int channelId) {
        if (supportTIFFunction()) {
            MtkLog.d(TAG, "getCurrentFocus = " + getCurrentFocus().equalsIgnoreCase("sub") + "  isCurrentSourceTv = " + isCurrentSourceTv());
            if (isdualtunermode()) {
                TurnkeyUiMainActivity.getInstance().getPipView().tune(InputSourceManager.getInstance().getTvInputInfo("sub").getId(), MtkTvTISMsgBase.createSvlChannelUri((long) channelId));
                return true;
            }
            TIFChannelInfo tifChannelInfo = TIFChannelManager.getInstance(mContext).getTIFChannelInfoById(channelId);
            if (tifChannelInfo != null) {
                if (tifChannelInfo.mMtkTvChannelInfo != null) {
                    MtkLog.d(TAG, "selectChannelById tifChannelInfo channelId = " + tifChannelInfo.mMtkTvChannelInfo.getChannelId());
                }
                return TIFChannelManager.getInstance(mContext).selectChannelByTIFInfo(tifChannelInfo);
            }
            ScanViewActivity.isSelectedChannel = true;
            return false;
        }
        if (channelId != getCurrentChannelId()) {
            ComponentStatusListener.getInstance().updateStatus(10, 0);
        }
        int result = -1;
        MtkLog.d(TAG, "selectChannelById channelId =" + channelId);
        MtkTvChannelInfoBase curCh = getCurChInfo();
        MtkTvChannelInfoBase chInfo = getChannelById(channelId);
        if (chInfo != null) {
            result = chBroadCast.channelSelect(chInfo, false);
        }
        MtkLog.d(TAG, "selectChannelById chInfo =" + chInfo + "result =" + result);
        if (result != 0) {
            return false;
        }
        MtkLog.d(TAG, "selectChannelById mPreChInfo =" + curCh);
        if (this.mChListener != null && (checkChMask(curCh, CH_LIST_MASK, 0) || checkCurChMask(CH_LIST_MASK, 0))) {
            this.mChListener.onChannelChanged();
        }
        mPreChInfo = curCh;
        return true;
    }

    public void singleRFScan(float rf) {
    }

    public boolean selectChannelByIdSvl(int channelId) {
        int currentRegion = MarketRegionInfo.getCurrentMarketRegion();
        if (currentRegion != 2 && currentRegion != 1) {
            return selectChannelById(channelId);
        }
        if (channelId != getCurrentChannelId()) {
            ComponentStatusListener.getInstance().updateStatus(10, 0);
        }
        int result = -1;
        MtkLog.d(TAG, "selectChannelById channelId =" + channelId);
        MtkTvChannelInfoBase curCh = getCurChInfo();
        MtkTvChannelInfoBase chInfo = getChannelById(channelId);
        if (chInfo != null) {
            result = chBroadCast.channelSelect(chInfo, false);
        }
        MtkLog.d(TAG, "selectChannelById chInfo =" + chInfo + "result =" + result);
        if (result != 0) {
            return false;
        }
        MtkLog.d(TAG, "selectChannelById mPreChInfo =" + curCh);
        if (this.mChListener != null && (checkChMask(curCh, CH_LIST_MASK, 0) || checkCurChMask(CH_LIST_MASK, 0))) {
            this.mChListener.onChannelChanged();
        }
        mPreChInfo = curCh;
        return true;
    }

    public void setChannelList(int channelOperator, List<MtkTvChannelInfoBase> chList) {
        mtkTvChList.setChannelList(channelOperator, chList);
        TIFChannelManager.getInstance(mContext).updateMapsChannelInfoByList(chList);
    }

    public void setChannelListForfusion(int chNumbegin, int chNumEnd) {
        mtkTvChList.slideForOnePartChannelId(getSvl(), chNumbegin, chNumEnd);
    }

    public List<MtkTvChannelInfoBase> getChannelList(int chId, int prevCount, int nextCount, int filter) {
        MtkLog.d(TAG, "getChannelList chId =" + chId + "pre =" + prevCount + "nextCOunt =" + nextCount + "filter =" + filter);
        List<MtkTvChannelInfoBase> chList = mtkTvChList.getChannelListByFilter(getSvl(), filter, chId, prevCount, nextCount);
        if (chList == null || chList.size() <= 0) {
            MtkLog.d(TAG, "getChannelList chId = " + chId + "chList == null,or size == 0");
        } else {
            MtkLog.d(TAG, "getChannelList chId = " + chId + "chList.get(0).getchId" + chList.get(0).getChannelId());
        }
        return chList;
    }

    private List<MtkTvChannelInfoBase> getChannelListByFakefilter(int chId, int prevCount, int nextCount) {
        List<MtkTvChannelInfoBase> chList;
        List<MtkTvChannelInfoBase> chList2;
        int loopTime;
        boolean z;
        boolean loopEnd;
        boolean loopEnd2;
        int loopPreId = chId;
        int i = prevCount;
        int i2 = nextCount;
        MtkLog.d(TAG, "getChannelListByFakefilter chId" + loopPreId + "prevCount =" + i + "nextCount =" + i2);
        List<MtkTvChannelInfoBase> chList3 = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListPre = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListNext = new ArrayList<>();
        int chLen = mtkTvChList.getChannelCountByFilter(getSvl(), getChListFilter());
        int curChId = MtkTvConfig.getInstance().getConfigValue("g_nav__air_crnt_ch");
        if (chLen <= 0) {
            return null;
        }
        int loopTime2 = (chLen / 7) + (chLen % 7 == 0 ? 0 : 1);
        MtkLog.d(TAG, "chLen = " + chLen + "loopTime =" + loopTime2);
        if (loopPreId >= 0) {
            if (i > 0) {
                boolean loopEnd3 = false;
                int loopPreId2 = loopPreId;
                int loops = loopTime2;
                while (true) {
                    int loops2 = loops - 1;
                    if (loops <= 0 && loopEnd3) {
                        chList = chList3;
                        z = loopEnd3;
                        loopTime = loopTime2;
                        break;
                    }
                    int loops3 = loops2;
                    chList = chList3;
                    loopEnd = loopEnd3;
                    loopTime = loopTime2;
                    List<MtkTvChannelInfoBase> chListTmp = mtkTvChList.getChannelListByFilter(getSvl(), getChListFilter(), loopPreId2, chLen, 0);
                    StringBuilder sb = new StringBuilder();
                    sb.append("getChannelListByFakefilter Pre  loops = ");
                    sb.append(loops3);
                    sb.append("loopEnd =");
                    sb.append(loopEnd);
                    sb.append("loopPreId =");
                    sb.append(loopPreId2);
                    sb.append(" pre chListTmp size =");
                    sb.append(chListTmp == null ? 0 : chListTmp.size());
                    MtkLog.d(TAG, sb.toString());
                    if (chListTmp == null || chListTmp.size() <= 0) {
                        z = loopEnd;
                    } else {
                        for (int index = chListTmp.size() - 1; index >= 0; index--) {
                            MtkLog.d(TAG, "CH_LIST_ANALOG_MASK pre" + chListTmp.get(index).getNwMask() + " " + (chListTmp.get(index).getNwMask() & CH_LIST_ANALOG_MASK));
                            MtkLog.d(TAG, "getChannelListByFakefilter Pre loop info id =" + chListTmp.get(index).getChannelId() + "number = " + chListTmp.get(index).getChannelNumber() + "nwMask =" + Integer.toHexString(chListTmp.get(index).getNwMask()));
                            if (isdualtunermode() && (chListTmp.get(index).getNwMask() & CH_LIST_ANALOG_MASK) == CH_LIST_ANALOG_VAL) {
                                chListTmp.remove(index);
                            }
                        }
                        loopPreId2 = chListTmp.get(0).getChannelId();
                        int index2 = chListTmp.size() - 1;
                        while (true) {
                            if (index2 < 0) {
                                loopEnd3 = loopEnd;
                                break;
                            }
                            MtkTvChannelInfoBase chTmp = chListTmp.get(index2);
                            StringBuilder sb2 = new StringBuilder();
                            boolean loopEnd4 = loopEnd;
                            sb2.append("getChannelListByFakefilter Prev chTmp.getChannelId()=");
                            sb2.append(chTmp.getChannelId());
                            MtkLog.d(TAG, sb2.toString());
                            if (chListPre.size() > 0 && chTmp.getChannelId() == chListPre.get(chListPre.size() - 1).getChannelId()) {
                                MtkLog.d(TAG, "getChannelListByFakefilter Prev LoopEnd");
                                loopEnd2 = true;
                                break;
                            } else if (chTmp.getChannelId() == loopPreId) {
                                MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                loopEnd2 = true;
                                break;
                            } else {
                                if ((chTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE || curChId == chTmp.getChannelId()) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter Prev loop chListPre size = " + chListPre.size());
                                    if (chListPre.size() >= i) {
                                        loops3 = 0;
                                        loopEnd3 = loopEnd4;
                                        break;
                                    }
                                    chListPre.add(0, chTmp);
                                }
                                index2--;
                                loopEnd = loopEnd4;
                            }
                        }
                        loopEnd3 = loopEnd2;
                        if (chListTmp.size() < 7) {
                            break;
                        }
                        loops = loops3;
                        loopTime2 = loopTime;
                        chList3 = chList;
                    }
                }
                z = loopEnd;
                boolean loopEnd5 = z;
            } else {
                chList = chList3;
                loopTime = loopTime2;
            }
            if (i2 > 0) {
                int loops4 = loopTime;
                int loopNextId = loopPreId + 1;
                boolean loopEnd6 = false;
                while (true) {
                    int loopNextId2 = loopNextId;
                    int loops5 = loops4 - 1;
                    if (loops4 <= 0 || loopEnd6) {
                        boolean z2 = loopEnd6;
                    } else {
                        int loops6 = loops5;
                        List<MtkTvChannelInfoBase> chListTmp2 = mtkTvChList.getChannelListByFilter(getSvl(), getChListFilter(), loopNextId2, 0, chLen);
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("getChannelListByFakefilter Next  loops = ");
                        sb3.append(loops6);
                        sb3.append("loopEnd =");
                        sb3.append(loopEnd6);
                        sb3.append("loopNextId =");
                        sb3.append(loopNextId2);
                        sb3.append(" pre chListTmp size =");
                        sb3.append(chListTmp2 == null ? 0 : chListTmp2.size());
                        MtkLog.d(TAG, sb3.toString());
                        if (chListTmp2 != null && chListTmp2.size() > 0) {
                            for (int index3 = chListTmp2.size() - 1; index3 >= 0; index3--) {
                                MtkLog.d(TAG, "CH_LIST_ANALOG_MASK next" + chListTmp2.get(index3).getNwMask() + " " + (chListTmp2.get(index3).getNwMask() & CH_LIST_ANALOG_MASK));
                                MtkLog.d(TAG, "getChannelListByFakefilter Pre loop info id =" + chListTmp2.get(index3).getChannelId() + "number = " + chListTmp2.get(index3).getChannelNumber() + "nwMask =" + Integer.toHexString(chListTmp2.get(index3).getNwMask()));
                                if (isdualtunermode() && (chListTmp2.get(index3).getNwMask() & CH_LIST_ANALOG_MASK) == CH_LIST_ANALOG_VAL) {
                                    chListTmp2.remove(index3);
                                }
                            }
                            loopNextId = chListTmp2.get(chListTmp2.size() - 1).getChannelId() + 1;
                            int index4 = 0;
                            while (true) {
                                if (index4 >= chListTmp2.size()) {
                                    break;
                                }
                                MtkTvChannelInfoBase chTmp2 = chListTmp2.get(index4);
                                MtkLog.d(TAG, "getChannelListByFakefilter Next chTmp.getChannelId()=" + chTmp2.getChannelId());
                                if (chListNext.size() > 0) {
                                    if (chTmp2.getChannelId() == chListNext.get(0).getChannelId()) {
                                        MtkLog.d(TAG, "getChannelListByFakefilter Next LoopEnd");
                                        loopEnd6 = true;
                                        break;
                                    }
                                }
                                if (chTmp2.getChannelId() == loopPreId) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                    loopEnd6 = true;
                                    break;
                                }
                                if ((chTmp2.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE || curChId == chTmp2.getChannelId()) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter Next loop chListNext size = " + chListNext.size());
                                    if (chListNext.size() >= i2) {
                                        loops6 = 0;
                                        break;
                                    }
                                    chListNext.add(chTmp2);
                                }
                                index4++;
                            }
                            loops4 = loops6;
                            if (chListTmp2.size() < 7) {
                                break;
                            }
                            int i3 = prevCount;
                        }
                    }
                }
                boolean z22 = loopEnd6;
            }
        } else {
            chList = chList3;
            int i4 = loopTime2;
        }
        MtkTvChannelInfoBase baseCh = getChannelById(chId);
        if (baseCh != null) {
            chList2 = chList;
            chList2.addAll(chListPre);
            chList2.add(baseCh);
            chList2.addAll(chListNext);
        } else {
            chList2 = chList;
            chList2.addAll(chListPre);
            chList2.addAll(chListNext);
        }
        if (chListPre.size() > 0 && chListNext.size() > 0) {
            int pre = 0;
            for (MtkTvChannelInfoBase tmpPreInfo : chListPre) {
                for (MtkTvChannelInfoBase tmpNextInfo : chListNext) {
                    if (tmpPreInfo.getChannelId() == tmpNextInfo.getChannelId()) {
                        chList2.remove(pre);
                    }
                    int i5 = chId;
                }
                pre++;
                int i6 = chId;
            }
        }
        if (chList2.size() > prevCount + i2) {
            chList2.remove(chList2.size() - 1);
        }
        MtkLog.d(TAG, "getChannelListByFakefilter chListPre = " + chListPre);
        MtkLog.d(TAG, "getChannelListByFakefilter baseCh = " + baseCh);
        MtkLog.d(TAG, "getChannelListByFakefilter chListNext = " + chListNext);
        MtkLog.d(TAG, "getChannelListByFakefilter chList = " + chList2);
        return chList2;
    }

    public List<MtkTvChannelInfoBase> getChannelListByMaskFilter(int chId, int dir, int count, int mask, int val) {
        MtkLog.d(TAG, "getChannelListByMaskFilter chId = " + chId + "  dir = " + dir + " count = " + count + "mask = " + mask + " val = " + val);
        int chLen = mtkTvChList.getChannelCountByMask(getSvl(), mask, val);
        if (chLen <= 0) {
            return new ArrayList();
        }
        return mtkTvChList.getChannelListByMask(getSvl(), mask, val, dir, chId, count > chLen ? chLen : count);
    }

    public List<MtkTvChannelInfoBase> getChnnelListByQueryInfo(MtkTvChannelQuery mtkTvChannelQuery, int count) {
        return mtkTvChList.getChannelListByQueryInfo(getSvl(), mtkTvChannelQuery, count);
    }

    private List<MtkTvChannelInfoBase> getChannelListByFakefilter(int chId, int prevCount, int nextCount, int filter) {
        int loopTime;
        boolean loopEnd;
        int loopPreId = chId;
        int i = prevCount;
        int i2 = nextCount;
        MtkLog.d(TAG, "getChannelListByFakefilter chId" + loopPreId + "prevCount =" + i + "nextCount =" + i2);
        List<MtkTvChannelInfoBase> chList = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListPre = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListNext = new ArrayList<>();
        int loopPreId2 = filter;
        int chLen = mtkTvChList.getChannelCountByFilter(getSvl(), loopPreId2);
        int curChId = getCurrentChannelId();
        if (chLen <= 0) {
            return null;
        }
        boolean z = false;
        int loopTime2 = (chLen / 7) + (chLen % 7 == 0 ? 0 : 1);
        MtkLog.d(TAG, "chLen = " + chLen + "loopTime =" + loopTime2);
        if (loopPreId >= 0) {
            if (i > 0) {
                boolean loopEnd2 = false;
                int loopPreId3 = loopPreId;
                int loops = loopTime2;
                while (true) {
                    int loops2 = loops - 1;
                    if (loops <= 0 && loopEnd2) {
                        break;
                    }
                    int loopPreId4 = loopPreId3;
                    boolean loopEnd3 = loopEnd2;
                    int loopTime3 = loopTime2;
                    int chLen2 = chLen;
                    int chLen3 = z;
                    List<MtkTvChannelInfoBase> chListTmp = mtkTvChList.getChannelListByFilter(getSvl(), loopPreId2, loopPreId4, 7, 0);
                    StringBuilder sb = new StringBuilder();
                    sb.append("getChannelListByFakefilter Pre  loops = ");
                    int loops3 = loops2;
                    sb.append(loops3);
                    sb.append("loopEnd =");
                    boolean loopEnd4 = loopEnd3;
                    sb.append(loopEnd4);
                    sb.append("loopPreId =");
                    int loopPreId5 = loopPreId4;
                    sb.append(loopPreId5);
                    sb.append(" pre chListTmp size =");
                    sb.append(chListTmp == null ? 0 : chListTmp.size());
                    MtkLog.d(TAG, sb.toString());
                    if (chListTmp == null || chListTmp.size() <= 0) {
                        int loops4 = loops3;
                        loopEnd2 = true;
                        loopPreId3 = loopPreId5;
                        loops = loops4;
                    } else {
                        Iterator<MtkTvChannelInfoBase> it = chListTmp.iterator();
                        while (it.hasNext()) {
                            MtkTvChannelInfoBase info = it.next();
                            MtkLog.d(TAG, "getChannelListByFakefilter Pre loop info id =" + info.getChannelId() + "number = " + info.getChannelNumber() + "nwMask =" + Integer.toHexString(info.getNwMask()));
                            it = it;
                            loops3 = loops3;
                        }
                        int loops5 = loops3;
                        int loopPreId6 = chListTmp.get(0).getChannelId();
                        int index = chListTmp.size() - 1;
                        while (index >= 0) {
                            MtkTvChannelInfoBase chTmp = chListTmp.get(index);
                            StringBuilder sb2 = new StringBuilder();
                            int loopPreId7 = loopPreId6;
                            sb2.append("getChannelListByFakefilter Prev chTmp.getChannelId()=");
                            sb2.append(chTmp.getChannelId());
                            MtkLog.d(TAG, sb2.toString());
                            if (chListPre.size() > 0 && chTmp.getChannelId() == chListPre.get(chListPre.size() - 1).getChannelId()) {
                                MtkLog.d(TAG, "getChannelListByFakefilter Prev LoopEnd");
                                loopEnd = true;
                            } else if (chTmp.getChannelId() == loopPreId) {
                                MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                loopEnd = true;
                            } else if ((chTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE || curChId == chTmp.getChannelId()) {
                                MtkLog.d(TAG, "getChannelListByFakefilter Prev loop chListPre size = " + chListPre.size());
                                if (chListPre.size() < i) {
                                    chListPre.add(0, chTmp);
                                } else {
                                    loops5 = 0;
                                }
                                index--;
                                loopPreId6 = loopPreId7;
                            } else {
                                index--;
                                loopPreId6 = loopPreId7;
                            }
                            loopEnd4 = loopEnd;
                            index--;
                            loopPreId6 = loopPreId7;
                        }
                        int loopPreId8 = loopPreId6;
                        loopEnd2 = chListTmp.size() < 7 ? true : loopEnd4;
                        loops = loops5;
                        loopPreId3 = loopPreId8;
                    }
                    loopTime2 = loopTime3;
                    chLen = chLen2;
                    z = false;
                }
                int i3 = chLen;
                loopTime = loopTime2;
            } else {
                loopTime = loopTime2;
            }
            if (i2 > 0) {
                int loops6 = loopTime;
                int loopNextId = loopPreId + 1;
                boolean loopEnd5 = false;
                while (true) {
                    int loopNextId2 = loopNextId;
                    int loops7 = loops6 - 1;
                    if (loops6 <= 0 || loopEnd5) {
                        boolean z2 = loopEnd5;
                    } else {
                        int i4 = loopPreId2;
                        int loops8 = loops7;
                        int loopNextId3 = loopNextId2;
                        boolean loopEnd6 = loopEnd5;
                        List<MtkTvChannelInfoBase> chListTmp2 = mtkTvChList.getChannelListByFilter(getSvl(), i4, loopNextId2, 0, 7);
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("getChannelListByFakefilter Next  loops = ");
                        sb3.append(loops8);
                        sb3.append("loopEnd =");
                        sb3.append(loopEnd6);
                        sb3.append("loopNextId =");
                        sb3.append(loopNextId3);
                        sb3.append(" pre chListTmp size =");
                        sb3.append(chListTmp2 == null ? 0 : chListTmp2.size());
                        MtkLog.d(TAG, sb3.toString());
                        if (chListTmp2 == null || chListTmp2.size() <= 0) {
                            loopEnd6 = true;
                            loopNextId = loopNextId3;
                        } else {
                            for (MtkTvChannelInfoBase info2 : chListTmp2) {
                                MtkLog.d(TAG, "getChannelListByFakefilter Next loop info id =" + info2.getChannelId() + "number = " + info2.getChannelNumber() + "nwMask =" + Integer.toHexString(info2.getNwMask()));
                            }
                            loopNextId = chListTmp2.get(chListTmp2.size() - 1).getChannelId() + 1;
                            for (int index2 = 0; index2 < chListTmp2.size(); index2++) {
                                MtkTvChannelInfoBase chTmp2 = chListTmp2.get(index2);
                                MtkLog.d(TAG, "getChannelListByFakefilter Next chTmp.getChannelId()=" + chTmp2.getChannelId());
                                if (chListNext.size() > 0) {
                                    if (chTmp2.getChannelId() == chListNext.get(0).getChannelId()) {
                                        MtkLog.d(TAG, "getChannelListByFakefilter Next LoopEnd");
                                        loopEnd6 = true;
                                    }
                                }
                                if (chTmp2.getChannelId() == loopPreId) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                    loopEnd6 = true;
                                } else if ((chTmp2.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE || curChId == chTmp2.getChannelId()) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter Next loop chListNext size = " + chListNext.size());
                                    if (chListNext.size() < i2) {
                                        chListNext.add(chTmp2);
                                    } else {
                                        loops8 = 0;
                                    }
                                }
                            }
                            if (chListTmp2.size() < 7) {
                                loopEnd5 = true;
                                loops6 = loops8;
                                int i5 = prevCount;
                                loopPreId2 = filter;
                            }
                        }
                        loopEnd5 = loopEnd6;
                        loops6 = loops8;
                        int i52 = prevCount;
                        loopPreId2 = filter;
                    }
                }
                boolean z22 = loopEnd5;
            }
        } else {
            int i6 = loopTime2;
        }
        MtkTvChannelInfoBase baseCh = getChannelById(chId);
        if (baseCh != null) {
            chList.addAll(chListPre);
            chList.add(baseCh);
            chList.addAll(chListNext);
        } else {
            chList.addAll(chListPre);
            chList.addAll(chListNext);
        }
        if (chListPre.size() > 0 && chListNext.size() > 0) {
            int pre = 0;
            for (MtkTvChannelInfoBase tmpPreInfo : chListPre) {
                for (MtkTvChannelInfoBase tmpNextInfo : chListNext) {
                    if (tmpPreInfo.getChannelId() == tmpNextInfo.getChannelId()) {
                        chList.remove(pre);
                    }
                }
                pre++;
            }
        }
        if (chList.size() > prevCount + i2) {
            chList.remove(chList.size() - 1);
        }
        MtkLog.d(TAG, "getChannelListByFakefilter chListPre = " + chListPre);
        MtkLog.d(TAG, "getChannelListByFakefilter baseCh = " + baseCh);
        MtkLog.d(TAG, "getChannelListByFakefilter chListNext = " + chListNext);
        return chList;
    }

    public List<MtkTvChannelInfoBase> getChannelListByMaskValuefilter(int chId, int prevCount, int nextCount, int filter, int value, boolean contaisBaseCh) {
        List<MtkTvChannelInfoBase> chListPre;
        List<MtkTvChannelInfoBase> chList;
        List<MtkTvChannelInfoBase> chListPre2;
        List<MtkTvChannelInfoBase> chList2;
        int loopTime;
        boolean loopEnd;
        int tmpLen;
        boolean loopEnd2;
        int i = chId;
        int i2 = prevCount;
        int i3 = nextCount;
        int i4 = filter;
        int i5 = value;
        MtkLog.d(TAG, "getChannelListByMaskValuefilter chId" + i + "prevCount =" + i2 + "nextCount =" + i3);
        List<MtkTvChannelInfoBase> chList3 = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListPre3 = new ArrayList<>();
        List<MtkTvChannelInfoBase> chListNext = new ArrayList<>();
        int chLen = mtkTvChList.getChannelCountByFilter(getSvl(), i4);
        int curChId = getCurrentChannelId();
        if (chLen <= 0) {
            return null;
        }
        int tmpLen2 = 7;
        boolean z = false;
        int loopTime2 = (chLen / 7) + (chLen % 7 == 0 ? 0 : 1);
        MtkLog.d(TAG, "chLen = " + chLen + "loopTime =" + loopTime2);
        if (i >= 0) {
            if (i2 > 0) {
                int loopPreId = i;
                boolean loopEnd3 = false;
                int loops = loopTime2;
                while (true) {
                    tmpLen = tmpLen2;
                    int tmpLen3 = loops - 1;
                    if (loops <= 0 && loopEnd3) {
                        break;
                    }
                    int loopPreId2 = loopPreId;
                    boolean loopEnd4 = loopEnd3;
                    int loopTime3 = loopTime2;
                    int curChId2 = curChId;
                    int curChId3 = z;
                    int loops2 = tmpLen3;
                    int tmpLen4 = tmpLen;
                    List<MtkTvChannelInfoBase> chListTmp = mtkTvChList.getChannelListByFilter(getSvl(), i4, loopPreId2, chLen, 0);
                    StringBuilder sb = new StringBuilder();
                    sb.append("getChannelListByFakefilter Pre  loops = ");
                    sb.append(loops2);
                    sb.append("loopEnd =");
                    boolean loopEnd5 = loopEnd4;
                    sb.append(loopEnd5);
                    sb.append("loopPreId =");
                    int loopPreId3 = loopPreId2;
                    sb.append(loopPreId3);
                    sb.append(" pre chListTmp size =");
                    sb.append(chListTmp == null ? 0 : chListTmp.size());
                    MtkLog.d(TAG, sb.toString());
                    if (chListTmp == null || chListTmp.size() <= 0) {
                        boolean z2 = loopEnd5;
                        tmpLen2 = tmpLen4;
                        loopEnd3 = true;
                        loopPreId = loopPreId3;
                        loops = loops2;
                    } else {
                        int index = chListTmp.size() - 1;
                        while (index >= 0) {
                            StringBuilder sb2 = new StringBuilder();
                            boolean loopEnd6 = loopEnd5;
                            sb2.append("eu CH_LIST_ANALOG_MASK pre");
                            sb2.append(chListTmp.get(index).getNwMask());
                            sb2.append(" ");
                            sb2.append(chListTmp.get(index).getNwMask() & CH_LIST_ANALOG_MASK);
                            MtkLog.d(TAG, sb2.toString());
                            MtkLog.d(TAG, "getChannelListByFakefilter Pre loop info id =" + chListTmp.get(index).getChannelId() + "number = " + chListTmp.get(index).getChannelNumber() + "nwMask =" + Integer.toHexString(chListTmp.get(index).getNwMask()));
                            if (isdualtunermode() && (chListTmp.get(index).getNwMask() & CH_LIST_ANALOG_MASK) == CH_LIST_ANALOG_VAL) {
                                chListTmp.remove(index);
                            }
                            index--;
                            loopEnd5 = loopEnd6;
                        }
                        boolean loopEnd7 = loopEnd5;
                        int loopPreId4 = chListTmp.get(0).getChannelId();
                        int index2 = chListTmp.size() - 1;
                        while (index2 >= 0) {
                            MtkTvChannelInfoBase chTmp = chListTmp.get(index2);
                            StringBuilder sb3 = new StringBuilder();
                            int loopPreId5 = loopPreId4;
                            sb3.append("getChannelListByFakefilter Prev chTmp.getChannelId()=");
                            sb3.append(chTmp.getChannelId());
                            MtkLog.d(TAG, sb3.toString());
                            if (chListPre3.size() > 0 && chTmp.getChannelId() == chListPre3.get(chListPre3.size() - 1).getChannelId()) {
                                MtkLog.d(TAG, "getChannelListByFakefilter Prev LoopEnd");
                                loopEnd2 = true;
                            } else if (chTmp.getChannelId() == i) {
                                MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                loopEnd2 = true;
                            } else {
                                if ((chTmp.getNwMask() & i4) == i5) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter Prev loop chListPre size = " + chListPre3.size());
                                    if (chListPre3.size() < i2) {
                                        chListPre3.add(0, chTmp);
                                    } else {
                                        loops2 = 0;
                                    }
                                }
                                index2--;
                                loopPreId4 = loopPreId5;
                            }
                            loopEnd7 = loopEnd2;
                            index2--;
                            loopPreId4 = loopPreId5;
                        }
                        int loopPreId6 = loopPreId4;
                        tmpLen2 = tmpLen4;
                        if (chListTmp.size() < tmpLen2) {
                            loopEnd3 = true;
                            loops = loops2;
                        } else {
                            loops = loops2;
                            loopEnd3 = loopEnd7;
                        }
                        loopPreId = loopPreId6;
                    }
                    loopTime2 = loopTime3;
                    curChId = curChId2;
                    z = false;
                }
                boolean z3 = loopEnd3;
                loopTime = loopTime2;
                int i6 = curChId;
                tmpLen2 = tmpLen;
            } else {
                loopTime = loopTime2;
                int i7 = curChId;
            }
            if (i3 > 0) {
                int loops3 = loopTime;
                int loopNextId = i + 1;
                boolean loopEnd8 = false;
                while (true) {
                    int loopNextId2 = loopNextId;
                    int loops4 = loops3 - 1;
                    if (loops3 <= 0 || loopEnd8) {
                        chList = chList3;
                        chListPre = chListPre3;
                        int i8 = tmpLen2;
                        boolean z4 = loopEnd8;
                    } else {
                        int loops5 = loops4;
                        List<MtkTvChannelInfoBase> chList4 = chList3;
                        int loopNextId3 = loopNextId2;
                        List<MtkTvChannelInfoBase> chListPre4 = chListPre3;
                        int tmpLen5 = tmpLen2;
                        List<MtkTvChannelInfoBase> chListTmp2 = mtkTvChList.getChannelListByFilter(getSvl(), i4, loopNextId2, 0, chLen);
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("getChannelListByFakefilter Next  loops = ");
                        sb4.append(loops5);
                        sb4.append("loopEnd =");
                        sb4.append(loopEnd8);
                        sb4.append("loopNextId =");
                        sb4.append(loopNextId3);
                        sb4.append(" pre chListTmp size =");
                        sb4.append(chListTmp2 == null ? 0 : chListTmp2.size());
                        MtkLog.d(TAG, sb4.toString());
                        if (chListTmp2 == null || chListTmp2.size() <= 0) {
                            loops3 = loops5;
                            loopEnd8 = true;
                            loopNextId = loopNextId3;
                        } else {
                            for (int index3 = chListTmp2.size() - 1; index3 >= 0; index3--) {
                                MtkLog.d(TAG, "eu CH_LIST_ANALOG_MASK next" + chListTmp2.get(index3).getNwMask() + " " + (chListTmp2.get(index3).getNwMask() & CH_LIST_ANALOG_MASK));
                                MtkLog.d(TAG, "getChannelListByFakefilter Pre loop info id =" + chListTmp2.get(index3).getChannelId() + "number = " + chListTmp2.get(index3).getChannelNumber() + "nwMask =" + Integer.toHexString(chListTmp2.get(index3).getNwMask()));
                                if (isdualtunermode() && (chListTmp2.get(index3).getNwMask() & CH_LIST_ANALOG_MASK) == CH_LIST_ANALOG_VAL) {
                                    chListTmp2.remove(index3);
                                }
                            }
                            int loops6 = chListTmp2.get(chListTmp2.size() - 1).getChannelId() + 1;
                            int loops7 = loops5;
                            int index4 = 0;
                            while (index4 < chListTmp2.size()) {
                                MtkTvChannelInfoBase chTmp2 = chListTmp2.get(index4);
                                MtkLog.d(TAG, "getChannelListByFakefilter Next chTmp.getChannelId()=" + chTmp2.getChannelId());
                                if (chListNext.size() > 0) {
                                    if (chTmp2.getChannelId() == chListNext.get(0).getChannelId()) {
                                        MtkLog.d(TAG, "getChannelListByFakefilter Next LoopEnd");
                                        loopEnd = true;
                                        loopEnd8 = loopEnd;
                                        index4++;
                                    }
                                }
                                if (chTmp2.getChannelId() == i) {
                                    MtkLog.d(TAG, "getChannelListByFakefilter loop to start id LoopEnd");
                                    loopEnd = true;
                                    loopEnd8 = loopEnd;
                                    index4++;
                                } else {
                                    if ((chTmp2.getNwMask() & i4) == i5) {
                                        MtkLog.d(TAG, "getChannelListByFakefilter Next loop chListNext size = " + chListNext.size());
                                        if (chListNext.size() < i3) {
                                            chListNext.add(chTmp2);
                                        } else {
                                            loops7 = 0;
                                        }
                                    }
                                    index4++;
                                }
                            }
                            if (chListTmp2.size() < tmpLen5) {
                                loopEnd8 = true;
                            }
                            int i9 = loops7;
                            loopNextId = loops6;
                            loops3 = i9;
                        }
                        tmpLen2 = tmpLen5;
                        chList3 = chList4;
                        chListPre3 = chListPre4;
                        int i10 = prevCount;
                    }
                }
                chList = chList3;
                chListPre = chListPre3;
                int i82 = tmpLen2;
                boolean z42 = loopEnd8;
            } else {
                chList = chList3;
                chListPre = chListPre3;
                int i11 = tmpLen2;
            }
        } else {
            chList = chList3;
            chListPre = chListPre3;
            int i12 = curChId;
        }
        MtkTvChannelInfoBase baseCh = getChannelById(chId);
        if (baseCh == null || !contaisBaseCh) {
            chList2 = chList;
            chListPre2 = chListPre;
            chList2.addAll(chListPre2);
            chList2.addAll(chListNext);
        } else {
            chList2 = chList;
            chListPre2 = chListPre;
            chList2.addAll(chListPre2);
            chList2.add(baseCh);
            chList2.addAll(chListNext);
        }
        if (chList2.size() > prevCount + i3) {
            chList2.remove(chList2.size() - 1);
        }
        MtkLog.d(TAG, "getChannelListByFakefilter chListPre = " + chListPre2);
        MtkLog.d(TAG, "getChannelListByFakefilter baseCh = " + baseCh);
        MtkLog.d(TAG, "getChannelListByFakefilter chListNext = " + chListNext);
        return chList2;
    }

    public MtkTvChannelInfoBase getChannelById(int chId) {
        MtkLog.d(TAG, "getChannelById chID = " + chId);
        List<MtkTvChannelInfoBase> chList = getChannelList(chId, 0, 1, MtkTvChCommonBase.SB_VNET_ALL);
        MtkTvChannelInfoBase chInfo = null;
        if (chList != null && chList.size() > 0 && chId == chList.get(0).getChannelId()) {
            chInfo = chList.get(0);
        }
        if (chInfo != null) {
            MtkLog.d(TAG, "getChannelById chId = " + chId + "chInfo.getchId" + chInfo.getChannelId());
        } else {
            MtkLog.d(TAG, "getChannelList chId = " + chId + "chInfo == null");
        }
        return chInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0055, code lost:
        com.mediatek.wwtv.tvcenter.util.MtkLog.w(TAG, r7 + "line =" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x006e, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0074, code lost:
        if (r0.length() <= 0) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return java.lang.Integer.valueOf(r0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0046, code lost:
        if (r1 != null) goto L_0x0048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0048, code lost:
        r1.destroy();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0052, code lost:
        if (r1 == null) goto L_0x0055;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getProperty(java.lang.String r7) {
        /*
            r0 = 0
            r1 = 0
            r2 = 0
            java.lang.Runtime r3 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x004e }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r4.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = "getprop "
            r4.append(r5)     // Catch:{ Exception -> 0x004e }
            r4.append(r7)     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x004e }
            java.lang.Process r3 = r3.exec(r4)     // Catch:{ Exception -> 0x004e }
            r1 = r3
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ Exception -> 0x004e }
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x004e }
            java.io.InputStream r5 = r1.getInputStream()     // Catch:{ Exception -> 0x004e }
            r4.<init>(r5)     // Catch:{ Exception -> 0x004e }
            r3.<init>(r4)     // Catch:{ Exception -> 0x004e }
            java.lang.String r4 = r3.readLine()     // Catch:{ Exception -> 0x004e }
            r0 = r4
            java.lang.String r4 = "CommonIntegration"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004e }
            r5.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r6 = "line ="
            r5.append(r6)     // Catch:{ Exception -> 0x004e }
            r5.append(r0)     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x004e }
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r4, r5)     // Catch:{ Exception -> 0x004e }
            if (r1 == 0) goto L_0x0055
        L_0x0048:
            r1.destroy()
            goto L_0x0055
        L_0x004c:
            r3 = move-exception
            goto L_0x0082
        L_0x004e:
            r3 = move-exception
            r3.printStackTrace()     // Catch:{ all -> 0x004c }
            if (r1 == 0) goto L_0x0055
            goto L_0x0048
        L_0x0055:
            java.lang.String r3 = "CommonIntegration"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r7)
            java.lang.String r5 = "line ="
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.w(r3, r4)
            if (r0 == 0) goto L_0x0081
            int r3 = r0.length()
            if (r3 <= 0) goto L_0x0081
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0080 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x0080 }
            r2 = r3
            goto L_0x0081
        L_0x0080:
            r3 = move-exception
        L_0x0081:
            return r2
        L_0x0082:
            if (r1 == 0) goto L_0x0087
            r1.destroy()
        L_0x0087:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.CommonIntegration.getProperty(java.lang.String):int");
    }

    public MtkTvChannelInfoBase getUpDownChInfo(boolean prev) {
        int chLen;
        boolean z;
        int loopChId;
        int chLen2;
        CommonIntegration commonIntegration = this;
        boolean z2 = prev;
        MtkLog.d(TAG, "getUpDownChInfo prev =" + z2);
        int chLen3 = mtkTvChList.getChannelCountByFilter(getSvl(), getChUpDownFilter());
        MtkLog.d(TAG, "getUpDownChInfo chLen =" + chLen3);
        if (chLen3 <= 1) {
            return null;
        }
        boolean isOption = false;
        if (getProperty(WW_SKIP_MINOR) == 1) {
            isOption = true;
        }
        int curChId = getCurrentChannelId();
        int i = 0;
        int loopTime = (chLen3 / 7) + (chLen3 % 7 == 0 ? 0 : 1);
        MtkTvChannelInfoBase curChInfo = commonIntegration.getChannelById(curChId);
        int loopChId2 = z2 ? curChId : curChId + 1;
        int prevCount = z2 ? 7 : 0;
        int nextCount = z2 ? 0 : 7;
        MtkLog.d(TAG, "chLen = " + chLen3 + "loopTime =" + loopTime);
        MtkLog.d(TAG, "getUpDownChInfo prev =" + z2 + " curChId =" + curChId + " prevCount =" + prevCount + "nextCount =" + nextCount);
        while (true) {
            int loopTime2 = loopTime - 1;
            if (loopTime > 0) {
                List<MtkTvChannelInfoBase> chList = commonIntegration.getChannelList(loopChId2, prevCount, nextCount, getChUpDownFilter());
                StringBuilder sb = new StringBuilder();
                sb.append("getUpDownChInfo loopTime =");
                sb.append(loopTime2);
                sb.append("chList size =");
                sb.append(chList != null ? chList.size() : i);
                MtkLog.d(TAG, sb.toString());
                if (chList == null || chList.size() <= 0) {
                    chLen = chLen3;
                    z = true;
                    loopTime = 0;
                } else {
                    if (z2) {
                        loopChId = chList.get(i).getChannelId();
                    } else {
                        loopChId = chList.get(chList.size() - 1).getChannelId() + 1;
                    }
                    loopChId2 = loopChId;
                    MtkLog.d(TAG, "getUpDownChInfo loop loopChId = " + loopChId2 + " chList.size() =" + chList.size());
                    if (MarketRegionInfo.getCurrentMarketRegion() != 2 || !isOption || !(curChInfo instanceof MtkTvISDBChannelInfo)) {
                        chLen = chLen3;
                    } else {
                        MtkTvISDBChannelInfo isdbCurChInfo = (MtkTvISDBChannelInfo) curChInfo;
                        List<MtkTvChannelInfoBase> tmpList = new ArrayList<>();
                        for (MtkTvChannelInfoBase info : chList) {
                            if (info instanceof MtkTvISDBChannelInfo) {
                                MtkTvISDBChannelInfo isdbChInfo = (MtkTvISDBChannelInfo) info;
                                chLen2 = chLen3;
                                MtkTvISDBChannelInfo mtkTvISDBChannelInfo = isdbChInfo;
                                if (isdbChInfo.getMajorNum() != isdbCurChInfo.getMajorNum()) {
                                    tmpList.add(info);
                                }
                            } else {
                                chLen2 = chLen3;
                                tmpList.add(info);
                            }
                            chLen3 = chLen2;
                        }
                        chLen = chLen3;
                        if (tmpList.size() > 0) {
                            chList = tmpList;
                        } else {
                            loopTime = loopTime2;
                            chLen3 = chLen;
                            commonIntegration = this;
                            i = 0;
                        }
                    }
                    if (z2) {
                        z = true;
                        for (int index = chList.size() - 1; index >= 0; index--) {
                            MtkTvChannelInfoBase upTmp = chList.get(index);
                            if (upTmp.getChannelId() == curChId) {
                                return null;
                            }
                            if (!upTmp.isSkip() && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_VISIBLE) == MtkTvChCommonBase.SB_VNET_VISIBLE && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_ACTIVE) == MtkTvChCommonBase.SB_VNET_ACTIVE) {
                                return upTmp;
                            }
                        }
                    } else {
                        z = true;
                        for (int index2 = 0; index2 < chList.size(); index2++) {
                            MtkTvChannelInfoBase downTmp = chList.get(index2);
                            if (downTmp.getChannelId() == curChId) {
                                return null;
                            }
                            if (!downTmp.isSkip() && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_VISIBLE) == MtkTvChCommonBase.SB_VNET_VISIBLE && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_ACTIVE) == MtkTvChCommonBase.SB_VNET_ACTIVE) {
                                return downTmp;
                            }
                        }
                    }
                    loopTime = loopTime2;
                }
                boolean z3 = z;
                chLen3 = chLen;
                commonIntegration = this;
                i = 0;
            } else {
                int i2 = chLen3;
                return null;
            }
        }
    }

    public MtkTvChannelInfoBase getUpDownChInfoByMask(boolean prev, int mask, int val) {
        MtkTvChannelInfoBase tmpChInfo;
        boolean z = prev;
        MtkLog.d(TAG, "getUpDownChInfoByMask prev =" + z);
        int i = mask;
        int i2 = val;
        int chLen = mtkTvChList.getChannelCountByMask(getSvl(), i, i2);
        MtkLog.d(TAG, "getUpDownChInfoByMask chLen =" + chLen);
        if (chLen < 1) {
            return null;
        }
        boolean isOption = false;
        if (getProperty(WW_SKIP_MINOR) == 1) {
            isOption = true;
        }
        boolean isOption2 = isOption;
        int curChId = getCurrentChannelId();
        MtkTvChannelInfoBase curChInfo = getChannelById(curChId);
        MtkLog.d(TAG, "getUpDownChInfoByMask isOption =" + isOption2 + " curChId = " + curChId);
        while (true) {
            int chLen2 = chLen - 1;
            if (chLen <= 0) {
                return null;
            }
            List<MtkTvChannelInfoBase> chInfoList = mtkTvChList.getChannelListByMask(getSvl(), i, i2, z ? 2 : 3, curChId, 1);
            tmpChInfo = null;
            if (chInfoList != null && chInfoList.size() > 0) {
                tmpChInfo = chInfoList.get(0);
            }
            if (tmpChInfo == null || MarketRegionInfo.getCurrentMarketRegion() != 2 || !isOption2 || !(curChInfo instanceof MtkTvISDBChannelInfo) || !(tmpChInfo instanceof MtkTvISDBChannelInfo) || ((MtkTvISDBChannelInfo) curChInfo).getMajorNum() != ((MtkTvISDBChannelInfo) tmpChInfo).getMajorNum()) {
                return tmpChInfo;
            }
            chLen = chLen2;
        }
        return tmpChInfo;
    }

    public MtkTvChannelInfoBase getUpDownChInfoByFilter(boolean prev, int filter) {
        int chLen;
        int loopChId;
        int chLen2;
        CommonIntegration commonIntegration = this;
        boolean z = prev;
        int i = filter;
        MtkLog.d(TAG, "getUpDownChInfo prev =" + z);
        int chLen3 = mtkTvChList.getChannelCountByFilter(getSvl(), i);
        MtkLog.d(TAG, "getUpDownChInfo chLen =" + chLen3);
        if (chLen3 <= 1) {
            return null;
        }
        boolean isOption = false;
        if (getProperty(WW_SKIP_MINOR) == 1) {
            isOption = true;
        }
        int curChId = getCurrentChannelId();
        int loopTime = (chLen3 / 7) + (chLen3 % 7 == 0 ? 0 : 1);
        MtkTvChannelInfoBase curChInfo = commonIntegration.getChannelById(curChId);
        int loopChId2 = z ? curChId : curChId + 1;
        int prevCount = z ? 7 : 0;
        int nextCount = z ? 0 : 7;
        MtkLog.d(TAG, "chLen = " + chLen3 + "loopTime =" + loopTime);
        MtkLog.d(TAG, "getUpDownChInfo prev =" + z + " curChId =" + curChId + " prevCount =" + prevCount + "nextCount =" + nextCount);
        while (true) {
            int loopTime2 = loopTime - 1;
            if (loopTime > 0) {
                List<MtkTvChannelInfoBase> chList = commonIntegration.getChannelList(loopChId2, prevCount, nextCount, i);
                StringBuilder sb = new StringBuilder();
                sb.append("getUpDownChInfo loopTime =");
                sb.append(loopTime2);
                sb.append("chList size =");
                sb.append(chList != null ? chList.size() : 0);
                MtkLog.d(TAG, sb.toString());
                if (chList == null || chList.size() <= 0) {
                    chLen = chLen3;
                    loopTime = 0;
                } else {
                    if (z) {
                        loopChId = chList.get(0).getChannelId();
                    } else {
                        loopChId = chList.get(chList.size() - 1).getChannelId() + 1;
                    }
                    loopChId2 = loopChId;
                    MtkLog.d(TAG, "getUpDownChInfo loop loopChId = " + loopChId2 + " chList.size() =" + chList.size());
                    if (MarketRegionInfo.getCurrentMarketRegion() != 2 || !isOption || !(curChInfo instanceof MtkTvISDBChannelInfo)) {
                        chLen = chLen3;
                    } else {
                        MtkTvISDBChannelInfo isdbCurChInfo = (MtkTvISDBChannelInfo) curChInfo;
                        List<MtkTvChannelInfoBase> tmpList = new ArrayList<>();
                        for (MtkTvChannelInfoBase info : chList) {
                            if (info instanceof MtkTvISDBChannelInfo) {
                                MtkTvISDBChannelInfo isdbChInfo = (MtkTvISDBChannelInfo) info;
                                chLen2 = chLen3;
                                MtkTvISDBChannelInfo mtkTvISDBChannelInfo = isdbChInfo;
                                if (isdbChInfo.getMajorNum() != isdbCurChInfo.getMajorNum()) {
                                    tmpList.add(info);
                                }
                            } else {
                                chLen2 = chLen3;
                                tmpList.add(info);
                            }
                            chLen3 = chLen2;
                            int i2 = filter;
                        }
                        chLen = chLen3;
                        if (tmpList.size() > 0) {
                            chList = tmpList;
                        } else {
                            loopTime = loopTime2;
                        }
                    }
                    for (MtkTvChannelInfoBase info2 : chList) {
                        MtkLog.d(TAG, "getUpDownChInfo loop info id =" + info2.getChannelId() + "number = " + info2.getChannelNumber() + "nwMask =" + Integer.toHexString(info2.getNwMask()));
                    }
                    if (z) {
                        for (int index = chList.size() - 1; index >= 0; index--) {
                            MtkTvChannelInfoBase upTmp = chList.get(index);
                            if (upTmp.getChannelId() == curChId) {
                                return null;
                            }
                            if (!upTmp.isSkip() && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_VISIBLE) == MtkTvChCommonBase.SB_VNET_VISIBLE && (upTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_EPG) == MtkTvChCommonBase.SB_VNET_EPG) {
                                return upTmp;
                            }
                        }
                    } else {
                        for (int index2 = 0; index2 < chList.size(); index2++) {
                            MtkTvChannelInfoBase downTmp = chList.get(index2);
                            if (downTmp.getChannelId() == curChId) {
                                return null;
                            }
                            if (!downTmp.isSkip() && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_FAKE) != MtkTvChCommonBase.SB_VNET_FAKE && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_VISIBLE) == MtkTvChCommonBase.SB_VNET_VISIBLE && (downTmp.getNwMask() & MtkTvChCommonBase.SB_VNET_EPG) == MtkTvChCommonBase.SB_VNET_EPG) {
                                return downTmp;
                            }
                        }
                    }
                    loopTime = loopTime2;
                }
                chLen3 = chLen;
                commonIntegration = this;
                i = filter;
            } else {
                int i3 = chLen3;
                return null;
            }
        }
    }

    public List<MtkTvChannelInfoBase> getChListByMask(int chId, int dir, int count, int mask, int val) {
        MtkLog.d(TAG, "getChListByMask chId =" + chId + "dir =" + dir + " count = " + count + " mask = " + mask + " val = " + val);
        return getChannelListByMaskFilter(chId, dir, count, mask, val);
    }

    public List<MtkTvChannelInfoBase> getChannelListByMaskAndSat(int chId, int dir, int count, int mask, int val, int satRecId) {
        int i = mask;
        int i2 = val;
        int i3 = satRecId;
        int chLen = mtkTvChList.getChannelCountByMaskAndSat(getSvl(), i, i2, i3);
        if (chLen <= 0) {
            return null;
        }
        int i4 = count;
        List<MtkTvChannelInfoBase> chList = mtkTvChList.getChannelListByMaskAndSat(getSvl(), i, i2, i3, dir, chId, i4 > chLen ? chLen : i4);
        for (MtkTvChannelInfoBase chInfo : chList) {
            MtkLog.d(TAG, "getChannelListByMaskAndSat chInfo = " + chInfo);
        }
        return chList;
    }

    public List<MtkTvChannelInfoBase> getFavoriteListByFilter(int filter, int channelId, int prevCount, int nextCount) {
        MtkLog.d(TAG, "getFavoriteListByFilter channelId =" + channelId);
        if (mtkTvChList.getChannelCountByFilter(getSvl(), filter) <= 0) {
            return null;
        }
        List<MtkTvChannelInfoBase> chList = mtkTvChList.getChannelListByFilter(getSvl(), filter, channelId, prevCount, nextCount);
        if (isCurrentSourceATVforEuPA()) {
            for (int z = 0; z < chList.size(); z++) {
                chList.get(z).setChannelNumber(getAnalogChannelDisplayNumInt(chList.get(z).getChannelNumber()));
            }
        }
        MtkLog.d(TAG, "getFavoriteListByFilter chList.size() =" + chList.size());
        return chList;
    }

    public List<MtkTvChannelInfoBase> getChList(int chId, int prevCount, int nextCount) {
        MtkLog.d(TAG, "getChList chId =" + chId + "prevCount =" + prevCount + "nextCount = " + nextCount);
        return getChannelListByFakefilter(chId, prevCount, nextCount);
    }

    public List<MtkTvChannelInfoBase> getChannelList() {
        if (isSARegion()) {
            int length = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_ALL);
            MtkLog.d(TAG, "getChannelList length " + length);
            return getChList(0, 0, length);
        }
        int length2 = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_ALL);
        MtkLog.d(TAG, "getChannelList length " + length2);
        return getChannelList(0, 0, length2, MtkTvChCommonBase.SB_VNET_ALL);
    }

    public List<MtkTvChannelInfoBase> getChannelListForMap() {
        int length = mtkTvChList.getChannelCountByFilter(getSvl(), MtkTvChCommonBase.SB_VNET_ALL);
        MtkLog.d(TAG, "getChannelList length " + length);
        return getChannelList(0, 0, length, MtkTvChCommonBase.SB_VNET_ALL);
    }

    public List<MtkTvChannelInfoBase> getEPGChList(int chId, int prevCount, int nextCount) {
        MtkLog.d(TAG, "getChList chId =" + chId + "prevCount =" + prevCount + "nextCount = " + nextCount);
        return getChannelListByFakefilter(chId, prevCount, nextCount, getChListFilterEPG());
    }

    public String getCurrentFocus() {
        String focusWin = "";
        MtkLog.d(TAG, "getCurrentFocus(), mCurrentTvMode =" + mCurrentTvMode);
        if (mCurrentTvMode == 0) {
            focusWin = "main";
        } else {
            int result = MtkTvConfig.getInstance().getConfigValue("g_pip_pop__pip_pop_tv_focus_win");
            MtkLog.d(TAG, "getCurrentFocus(), TV_FOCUS_WIN =" + result);
            if (result == 0) {
                focusWin = "main";
            } else if (1 == result) {
                focusWin = "sub";
            }
        }
        MtkLog.d(TAG, "come in getCurrentFocus,focusWin ==" + focusWin);
        return focusWin;
    }

    public void setCurrentTVMode(int tvMode) {
        MtkTvConfig.getInstance().setConfigValue("g_pip_pop__pip_pop_tv_mode", tvMode);
    }

    public int getCurrentScreenMode() {
        return MtkTvConfig.getInstance().getConfigValue("g_video__screen_mode");
    }

    public void setCurrentScreenMode(int screenMode) {
        MtkTvConfig.getInstance().setConfigValue("g_video__screen_mode", screenMode);
    }

    public void setSupportThirdPIPMode(int supportMode) {
        MtkTvConfig.getInstance().setConfigValue("g_pip_pop__android_pop_mode", supportMode);
    }

    public String getISDBChannelLogo(MtkTvISDBChannelInfo chInfo) {
        return getChannelLogoPicPath(chInfo.getOnID(), chInfo.getProgId(), chInfo.getFrequency());
    }

    public String getChannelLogoPicPath(int onid, int svcid, int freq) {
        return this.mMtkTvCDTChLogoBase.getChLogoPNGFilePath(onid, svcid, freq);
    }

    public boolean isCurrentInputSourceHasSignal() {
        return this.iCurrentInputSourceHasSignal;
    }

    public void setCurrentInputSourceHasSignal(boolean iCurrentInputSourceHasSignal2) {
        this.iCurrentInputSourceHasSignal = iCurrentInputSourceHasSignal2;
    }

    public boolean isCurrentTVHasSignal() {
        return this.iCurrentTVHasSignal;
    }

    public void setCurrentTVHasSignal(boolean iCurrentTVHasSignal2) {
        this.iCurrentTVHasSignal = iCurrentTVHasSignal2;
    }

    public boolean isVideoScrambled() {
        return this.isVideoScrambled;
    }

    public void setVideoScrambled(boolean isVideoScrambled2) {
        this.isVideoScrambled = isVideoScrambled2;
    }

    public boolean isFavListFull() {
        for (MtkTvFavoritelistInfoBase channel : MtkTvChannelList.getInstance().getFavoritelistByFilter()) {
            if (channel.getChannelId() == -1) {
                return false;
            }
        }
        return true;
    }

    public void showFavFullMsg() {
        showCommonInfo(TurnkeyUiMainActivity.getInstance(), TurnkeyUiMainActivity.getInstance().getString(R.string.fav_list_is_full));
    }

    public void showCommonInfo(Activity activity, String info) {
        Toast mToast = Toast.makeText(activity.getApplicationContext(), info, 2000);
        ((TextView) mToast.getView().findViewById(16908299)).setCompoundDrawablesWithIntrinsicBounds(activity.getApplicationContext().getResources().getDrawable(R.drawable.nav_ib_warning_icon), (Drawable) null, (Drawable) null, (Drawable) null);
        mToast.show();
    }

    public void closeFavFullMsg() {
        if (this.mPopup != null && this.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }

    public boolean isCurrentSourceHasSignal() {
        if (chBroadCast.isSignalLoss()) {
            return false;
        }
        return true;
    }

    public int getChannelIDByBannerNum(String channelNumString) {
        String[] num;
        int result = -1;
        int euPAATVCHNum = 0;
        MtkLog.d(TAG, "come in getChannelIDByBannerNum, the channelNumString = " + channelNumString);
        if (TextUtils.isEmpty(channelNumString)) {
            return -1;
        }
        if (isCurrentSourceATVforEuPA()) {
            euPAATVCHNum = 2000;
        }
        MtkLog.d(TAG, "come in getChannelIDByBannerNum,euPAATVCHNum = " + euPAATVCHNum);
        if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            num = channelNumString.split("\\.");
        } else {
            num = channelNumString.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
        }
        MtkLog.d(TAG, "come in getChannelIDByBannerNum,num = " + Arrays.toString(num) + ", length:" + num.length);
        StringBuilder sb = new StringBuilder();
        sb.append("come in getChannelIDByBannerNum,getSvl() =");
        sb.append(getSvl());
        MtkLog.d(TAG, sb.toString());
        if (num.length > 1) {
            MtkLog.d(TAG, "come in getChannelIDByBannerNum,num1 =" + Integer.valueOf(num[0]));
            MtkLog.d(TAG, "come in getChannelIDByBannerNum,num2 =" + Integer.valueOf(num[1]));
            result = this.mMtkTvAppTV.getMatchedChannel(getSvl(), true, Integer.valueOf(num[0]).intValue() + euPAATVCHNum, Integer.valueOf(num[1]).intValue());
            MtkLog.d(TAG, "come in getChannelIDByBannerNum,result =" + result);
        } else if (num.length == 1) {
            MtkLog.d(TAG, "come in getChannelIDByBannerNum,num1 =" + Integer.valueOf(num[0]) + ",num2 = null");
            result = this.mMtkTvAppTV.getMatchedChannel(getSvl(), false, Integer.valueOf(num[0]).intValue() + euPAATVCHNum, 0);
        }
        MtkLog.d(TAG, "come in getChannelIDByBannerNum,result =" + result);
        return result;
    }

    public int getCurrentTVState() {
        if (!MarketRegionInfo.isFunctionSupport(26) && mCurrentTvMode != 0) {
            mCurrentTvMode = instanceMtkTvHighLevel.getCurrentTvMode();
        }
        return mCurrentTvMode;
    }

    public void recordCurrentTvState(int value) {
        if (value >= 0 && value <= 2) {
            mCurrentTvMode = value;
        }
    }

    public void updateOutputChangeState(String changeState) {
        this.mMtkTvAppTV.updatedSysStatus(getCurrentFocus(), changeState);
    }

    public int getSatelliteCount() {
        return this.mMtkTvDvbsConfigBase.getSatlNumRecs(getSvl());
    }

    public List<MtkTvDvbsConfigInfoBase> getSatelliteListInfo(int count) {
        List<MtkTvDvbsConfigInfoBase> tempResultList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < count; i++) {
            List<MtkTvDvbsConfigInfoBase> tempSatelliteList = this.mMtkTvDvbsConfigBase.getSatlRecordByRecIdx(getSvl(), i);
            if (tempSatelliteList != null) {
                int satelliteChannelCount = j;
                for (int j2 = 0; j2 < tempSatelliteList.size(); j2++) {
                    MtkTvDvbsConfigInfoBase tempSatelliteChannel = tempSatelliteList.get(j2);
                    satelliteChannelCount = getSatelliteChannelCount(tempSatelliteChannel.getSatlRecId(), MtkTvChCommonBase.SB_VNET_ALL, MtkTvChCommonBase.SB_VNET_ALL);
                    if (satelliteChannelCount > 0) {
                        tempResultList.add(tempSatelliteChannel);
                    }
                }
                j = satelliteChannelCount;
            }
        }
        return tempResultList;
    }

    public String[] getSatelliteNames(List<MtkTvDvbsConfigInfoBase> tempResultList) {
        if (tempResultList == null) {
            return new String[0];
        }
        int size = tempResultList.size();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = tempResultList.get(i).getSatName();
        }
        return names;
    }

    public MtkTvDvbsConfigInfoBase getDefaultSatellite(String name) {
        MtkTvDvbsConfigInfoBase tempSatellite = new MtkTvDvbsConfigInfoBase();
        tempSatellite.setSatlRecId(0);
        tempSatellite.setSatName(name);
        return tempSatellite;
    }

    public int getSatelliteChannelCount(int sateRecordId, int mask, int val) {
        return mtkTvChList.getChannelCountByMaskAndSat(getSvl(), mask, val, sateRecordId);
    }

    public int getFavouriteChannelCount(int filter) {
        return mtkTvChList.getChannelCountByFilter(getSvl(), filter);
    }

    public String getAvailableString(String illegalString) {
        if (illegalString == null || "".equals(illegalString)) {
            return "";
        }
        byte[] illegalByte = illegalString.getBytes();
        byte[] availableByte = new byte[illegalByte.length];
        int j = 0;
        for (byte mByte : illegalByte) {
            if (((mByte & 255) >= 32 && (mByte & 255) != Byte.MAX_VALUE) || (mByte & 255) == 10 || (mByte & 255) == 13) {
                availableByte[j] = mByte;
                j++;
            }
        }
        if ((availableByte[availableByte.length - 1] & 255) == 10 || (availableByte[availableByte.length - 1] & 255) == 13) {
            j--;
        }
        return new String(availableByte, 0, j);
    }

    public void setCurrentFocus(String currentFocus) {
        MtkLog.d(TAG, "come in setCurrentFocus,currentFocus ==" + currentFocus);
        if (currentFocus.equals("main")) {
            MtkTvConfig.getInstance().setConfigValue("g_pip_pop__pip_pop_tv_focus_win", 0);
        } else if (currentFocus.equals("sub")) {
            MtkTvConfig.getInstance().setConfigValue("g_pip_pop__pip_pop_tv_focus_win", 1);
        }
    }

    public void setBrdcstType(int brdcstType) {
        MtkLog.d(TAG, "come in setBrdcstType, brdcstType = " + brdcstType);
        MtkTvConfig.getInstance().setConfigValue("g_bs__bs_brdcst_type", brdcstType);
    }

    public int getBrdcstType() {
        int brdcstType = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_brdcst_type");
        MtkLog.d(TAG, "come in getConfigValue, brdcstType = " + brdcstType);
        return brdcstType;
    }

    public void restoreFineTune(float mRestoreHz) {
        MtkTvChannelInfoBase channel = getCurChInfo();
        channel.setFrequency((int) (1000000.0f * mRestoreHz));
        List<MtkTvChannelInfoBase> list = new ArrayList<>();
        list.add(channel);
        setChannelList(1, list);
        MtkLog.d(TAG, "restoreFineTune,mRestoreHz =" + mRestoreHz);
        if (getCurrentFocus().equalsIgnoreCase("sub")) {
            this.mMtkTvAppTV.setFinetuneFreq("sub", ((int) mRestoreHz) * 1000000, true);
        } else {
            this.mMtkTvAppTV.setFinetuneFreq("main", ((int) mRestoreHz) * 1000000, true);
        }
    }

    public boolean isDualTunerEnable() {
        if (!MarketRegionInfo.isFunctionSupport(32)) {
            return false;
        }
        int dualTunerValue = MtkTvConfig.getInstance().getConfigValue("g_misc__2nd_channel_enable");
        MtkLog.d(TAG, "isDualTunerEnable(), dualTunerValue =" + dualTunerValue);
        if (1 == dualTunerValue) {
            return true;
        }
        return false;
    }

    public boolean selectPowerOnChannel() {
        int powerOnChannelID;
        int result = -1;
        int def = SaveValue.getInstance(mContext).readValue(MenuConfigManager.SELECT_MODE);
        MtkLog.d(TAG, "selectPowerOnChannel SELECT_MODE ==" + def);
        if (def == 1) {
            if (this.mMtkTvConfig.getConfigValue("g_bs__bs_src") == 0) {
                powerOnChannelID = this.mMtkTvConfig.getConfigValue("g_nav__air_on_time_ch");
            } else {
                powerOnChannelID = this.mMtkTvConfig.getConfigValue("g_nav__cable_on_time_ch");
            }
            MtkLog.d(TAG, "selectPowerOnChannel powerOnChannelID==" + powerOnChannelID);
            if (powerOnChannelID > 0) {
                TIFChannelInfo channelInfo = TIFChannelManager.getInstance(mContext).getTIFChannelInfoById(powerOnChannelID);
                if (channelInfo == null || channelInfo.mMtkTvChannelInfo == null) {
                    MtkLog.d(TAG, "selectPowerOnChannel,currentPowerOnChannelInfo is null");
                } else if (MarketRegionInfo.isFunctionSupport(39)) {
                    if (channelInfo.mMtkTvChannelInfo.getBrdcstType() == 1) {
                        if (isCurrentSourceATVforEuPA()) {
                            result = InputSourceManager.getInstance().tuneChannelByTIFChannelInfoForAssistant(channelInfo);
                        }
                    } else if (isCurrentSourceDTVforEuPA()) {
                        result = InputSourceManager.getInstance().tuneChannelByTIFChannelInfoForAssistant(channelInfo);
                    }
                } else if (isCurrentSourceTv()) {
                    result = InputSourceManager.getInstance().tuneChannelByTIFChannelInfoForAssistant(channelInfo);
                }
            }
        }
        if (result == 0) {
            return true;
        }
        return false;
    }

    public boolean hasVGASource() {
        if (this.hasVGASource == -1) {
            this.hasVGASource = 0;
            Iterator<String> it = InputSourceManager.getInstance().getInputSourceList().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().toUpperCase().equals("VGA")) {
                        this.hasVGASource = 1;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (this.hasVGASource == 1) {
            return true;
        }
        return false;
    }

    public boolean isActiveChannel() {
        if (isUSRegion() || isSARegion()) {
            TIFChannelInfo channelInfo = TIFChannelManager.getInstance(mContext).getChannelInfoByUri();
            if (channelInfo != null) {
                MtkTvChannelInfoBase curCh = getCurChInfo();
                if (channelInfo.mDataValue == null || channelInfo.mDataValue.length != 9 || curCh == null || channelInfo.mInternalProviderFlag3 == curCh.getChannelId()) {
                    if (TIFFunctionUtil.checkChMaskformDataValue(channelInfo, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                        return TIFFunctionUtil.checkChMaskformDataValue(channelInfo, TIFFunctionUtil.CH_LIST_EPG_US_MASK, TIFFunctionUtil.CH_LIST_EPG_US_VAL);
                    }
                    return false;
                } else if (TIFFunctionUtil.checkChMask(curCh, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                    return TIFFunctionUtil.checkChMask(curCh, TIFFunctionUtil.CH_LIST_EPG_US_MASK, TIFFunctionUtil.CH_LIST_EPG_US_VAL);
                } else {
                    return false;
                }
            } else {
                MtkTvChannelInfoBase curCh2 = getCurChInfo();
                if (TIFFunctionUtil.checkChMask(curCh2, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                    return TIFFunctionUtil.checkChMask(curCh2, TIFFunctionUtil.CH_LIST_EPG_US_MASK, TIFFunctionUtil.CH_LIST_EPG_US_VAL);
                }
                return false;
            }
        } else {
            TIFChannelInfo channelInfo2 = TIFChannelManager.getInstance(mContext).getChannelInfoByUri();
            if (channelInfo2 != null) {
                return TIFFunctionUtil.checkChMaskformDataValue(channelInfo2, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
            }
            return TIFFunctionUtil.checkChMask(getCurChInfo(), TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        }
    }

    public boolean isFakerChannel(MtkTvChannelInfoBase curCh) {
        if (!isUSRegion() && !isSARegion()) {
            return false;
        }
        boolean isFakerChannel = checkChMask(curCh, CH_FAKE_MASK, CH_FAKE_VAL);
        MtkLog.d(TAG, "isFakerChannel=" + isFakerChannel);
        return isFakerChannel;
    }

    public int dvbsGetCategoryNum() {
        MtkLog.d(TAG, "dvbsGetCategoryNum");
        this.mtkTvScanDvbsBase.dvbsGetCategoryNum();
        return this.mtkTvScanDvbsBase.category_num;
    }

    public String dvbsGetCategoryInfoByIdx(int categoryIdx) {
        MtkLog.d(TAG, "dvbsGetCategoryInfoByIdx");
        return this.mtkTvScanDvbsBase.dvbsGetCategoryInfoByIdx(categoryIdx);
    }

    public boolean isOperatorNTVPLUS() {
        int OperatorName = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
        MtkLog.d(TAG, "isOperatorNTVPLUS OperatorName " + OperatorName + " ,MtkTvScanDvbsBase.DVBS_OPERATOR_NAME_NTV_PLUS =" + 21);
        return OperatorName == 21;
    }

    public boolean isOperatorTELEKARTA() {
        int OperatorName = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_sat_brdcster");
        MtkLog.d(TAG, "isOperatorTELEKARTA OperatorName " + OperatorName + " ,MtkTvScanDvbsBase.DVBS_OPERATOR_NAME_TELEKARTA =" + 35);
        return OperatorName == 35;
    }

    public boolean isOperatorTKGS() {
        boolean satOperOnly = isPreferSatMode();
        boolean isTkgs = TVContent.getInstance(mContext).isTKGSOperator();
        MtkLog.d(TAG, "isOperatorTKGSsatOperOnly " + satOperOnly);
        if (!satOperOnly || !isTkgs) {
            MtkLog.d(TAG, "isOperatorTKGS  false");
            return false;
        }
        MtkLog.d(TAG, "isOperatorTKGS  true");
        return true;
    }

    public boolean isFavoriteNetworkEnable() {
        MtkTvScanDvbtBase.UiOpSituation opSituation = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetSituation();
        MtkTvScanDvbtBase.FavNwk[] nwkList = MtkTvScan.getInstance().getScanDvbtInstance().uiOpGetFavNwk();
        if (!opSituation.favouriteNeteorkPopUp || nwkList == null || nwkList.length <= 0) {
            MtkLog.d(TAG, "favNetworkel false :" + false);
            return false;
        }
        MtkLog.d(TAG, "favNetworkel true :" + true);
        return true;
    }

    public int getDoblyVersion() {
        int doblyVersion = this.mMtkTvConfig.getConfigValue(MtkTvConfigTypeBase.CFG_AUD_DOLBY_INFO);
        MtkLog.w(TAG, "doblyVersion=" + doblyVersion);
        return doblyVersion;
    }

    public String[] getTunerModes() {
        String[] tunerModeStr;
        if (DataSeparaterUtil.getInstance().isTunerModeIniExist()) {
            List<String> tunerModes = new ArrayList<>();
            if (DataSeparaterUtil.getInstance().isDVBTSupport()) {
                tunerModes.add(mContext.getString(R.string.menu_arrays_Antenna));
            }
            if (DataSeparaterUtil.getInstance().isDVBCSupport()) {
                tunerModes.add(mContext.getString(R.string.menu_arrays_Cable));
            }
            if (isEUPARegion()) {
                if (MarketRegionInfo.isFunctionSupport(50) && DataSeparaterUtil.getInstance().isDVBSSupport()) {
                    if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                        tunerModes.add(mContext.getString(R.string.menu_arrays_Satellite));
                    } else {
                        tunerModes.add(mContext.getString(R.string.dvbs_preferred_satellite));
                        tunerModes.add(mContext.getString(R.string.dvbs_general_satellite));
                    }
                }
            } else if (MarketRegionInfo.isFunctionSupport(4) && DataSeparaterUtil.getInstance().isDVBSSupport()) {
                if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                    tunerModes.add(mContext.getString(R.string.menu_arrays_Satellite));
                } else {
                    tunerModes.add(mContext.getString(R.string.dvbs_preferred_satellite));
                    tunerModes.add(mContext.getString(R.string.dvbs_general_satellite));
                }
            }
            return (String[]) tunerModes.toArray(new String[0]);
        } else if (!isEUPARegion()) {
            if (!MarketRegionInfo.isFunctionSupport(4) || MarketRegionInfo.isFunctionSupport(16)) {
                tunerModeStr = mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
            } else {
                tunerModeStr = ScanContent.getDVBSOperatorList(mContext).size() == 0 ? mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu_sat_only) : mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu);
            }
            MtkLog.d(TAG, "TunerModeIni not Exist tunerModeStr.size=" + tunerModeStr.length);
            return tunerModeStr;
        } else if (!MarketRegionInfo.isFunctionSupport(50)) {
            return mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array);
        } else {
            if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                return mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu_sat_only);
            }
            return mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu);
        }
    }

    public String[] getTunerModeValues() {
        String[] tunerModeValue;
        if (DataSeparaterUtil.getInstance().isTunerModeIniExist()) {
            List<String> values = new ArrayList<>();
            if (DataSeparaterUtil.getInstance().isDVBTSupport()) {
                values.add("0");
            }
            if (DataSeparaterUtil.getInstance().isDVBCSupport()) {
                values.add("1");
            }
            if (isEUPARegion()) {
                if (MarketRegionInfo.isFunctionSupport(50) && DataSeparaterUtil.getInstance().isDVBSSupport()) {
                    if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                        values.add("2");
                    } else {
                        values.add("2");
                        values.add(MtkTvRatingConvert2Goo.RATING_STR_3);
                    }
                }
            } else if (MarketRegionInfo.isFunctionSupport(4) && DataSeparaterUtil.getInstance().isDVBSSupport()) {
                if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                    values.add("2");
                } else {
                    values.add("2");
                    values.add(MtkTvRatingConvert2Goo.RATING_STR_3);
                }
            }
            return (String[]) values.toArray(new String[0]);
        } else if (!isEUPARegion()) {
            if (!MarketRegionInfo.isFunctionSupport(4) || MarketRegionInfo.isFunctionSupport(16)) {
                tunerModeValue = new String[]{"0", "1"};
            } else if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                tunerModeValue = new String[]{"0", "1", "2"};
            } else {
                tunerModeValue = new String[]{"0", "1", "2", MtkTvRatingConvert2Goo.RATING_STR_3};
            }
            MtkLog.d(TAG, "TunerModeIni not Exist tunerModeValue.size=" + tunerModeValue.length);
            return tunerModeValue;
        } else if (!MarketRegionInfo.isFunctionSupport(50)) {
            return new String[]{"0", "1"};
        } else {
            if (ScanContent.getDVBSOperatorList(mContext).size() == 0) {
                return new String[]{"0", "1", "2"};
            }
            return new String[]{"0", "1", "2", MtkTvRatingConvert2Goo.RATING_STR_3};
        }
    }

    public boolean isDisableColorKey() {
        return DataSeparaterUtil.getInstance().isDisableColorKey();
    }
}
