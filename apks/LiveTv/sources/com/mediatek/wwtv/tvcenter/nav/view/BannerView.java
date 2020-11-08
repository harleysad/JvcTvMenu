package com.mediatek.wwtv.tvcenter.nav.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.tv.menu.MenuOptionMain;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvGinga;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvGingaAppInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvr;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateNormal;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.dvr.ui.DvrDialog;
import com.mediatek.wwtv.tvcenter.epg.eu.EPGEuActivity;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.fav.FavChannelManager;
import com.mediatek.wwtv.tvcenter.nav.fav.FavoriteListDialog;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.BannerImplement;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.DetailTextReader;
import com.mediatek.wwtv.tvcenter.nav.util.FocusLabelControl;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.nav.util.MultiViewControl;
import com.mediatek.wwtv.tvcenter.nav.util.SundryImplement;
import com.mediatek.wwtv.tvcenter.nav.util.TVStateControl;
import com.mediatek.wwtv.tvcenter.nav.view.ciview.CIStateChangedCallBack;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasicView;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.KeyDispatch;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.ScreenConstant;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import com.mediatek.wwtv.tvcenter.util.TvCallbackConst;
import com.mediatek.wwtv.tvcenter.util.TvCallbackData;
import com.mediatek.wwtv.tvcenter.util.TvCallbackHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BannerView extends NavBasicView implements ComponentStatusListener.ICStatusListener {
    public static final int BANNER_MSG_ITEM = 1;
    public static final int BANNER_MSG_ITM_AUDIO_INFO_CHANGE = 19;
    public static final int BANNER_MSG_ITM_CAPTION = 16;
    public static final int BANNER_MSG_ITM_CAPTION_ICON_CHANGE = 27;
    public static final int BANNER_MSG_NAV = 0;
    public static final int BANNNER_MSG_ITEM_ATMOS_CHNAGE = 36;
    public static final int BANNNER_MSG_ITEM_IPTS_NAME = 21;
    public static final int BANNNER_MSG_ITEM_IPTS_RSLT_CHANGE = 22;
    public static final int CHILD_TYPE_INFO_BASIC = 2;
    public static final int CHILD_TYPE_INFO_DETAIL = 3;
    public static final int CHILD_TYPE_INFO_NONE = 0;
    public static final int CHILD_TYPE_INFO_SIMPLE = 1;
    public static final int HBBTV_MSG_DISABLE_BANNER = 257;
    public static final int HBBTV_MSG_ENABLE_BANNER = 258;
    public static final int HBBTV_STATUS_3RD_APP_NOT_RUNNING = 263;
    public static final int HBBTV_STATUS_3RD_APP_RUNNING = 262;
    public static final int MHEG5_MSG_DISABLE_BANNER = 0;
    public static final int MHEG5_MSG_ENABLE_BANNER = 1;
    public static final int MHEG5_MSG_IF_DISABLE = 258;
    private static final int MSG_NUMBER_KEY = 1111;
    private static final int MSG_SHOW_SOURCE_BANNER_TIMING = 1112;
    private static final int MSG_TTS_SPEAK_DELAY = 1113;
    public static final int SPECIAL_CHANNEL_LOCK = 2;
    public static final int SPECIAL_EMPTY_SPECIAFIED_CH_LIST = 17;
    public static final int SPECIAL_HIDDEN_CHANNEL = 5;
    public static final int SPECIAL_INPUT_LOCK = 4;
    public static final int SPECIAL_NO_AUDIO_VIDEO = 6;
    public static final int SPECIAL_NO_CHANNEL = 16;
    public static final int SPECIAL_NO_SIGNAL = 1;
    public static final int SPECIAL_NO_SUPPORT = 20;
    public static final int SPECIAL_PROGRAM_LOCK = 3;
    public static final int SPECIAL_RETRIVING = 19;
    public static final int SPECIAL_SCRAMBLED_AUDIO_NO_VIDEO = 10;
    public static final int SPECIAL_SCRAMBLED_AUDIO_VIDEO = 8;
    public static final int SPECIAL_SCRAMBLED_VIDEO_CLEAR_AUDIO = 9;
    public static final int SPECIAL_SCRAMBLED_VIDEO_NO_AUDIO = 7;
    public static final int SPECIAL_STATUS_AUDIO_ONLY = 11;
    public static final int SPECIAL_STATUS_AUDIO_VIDEO = 14;
    public static final int SPECIAL_STATUS_UNKNOWN = 12;
    public static final int SPECIAL_STATUS_VIDEO_ONLY = 13;
    private static final String TAG = "BannerView";
    public static final int TV_BEFORE_SVC_CHANGE = 21;
    public static final int TV_STATE_NORMAL = 15;
    /* access modifiers changed from: private */
    public static BannerImplement mNavBannerImplement = null;
    /* access modifiers changed from: private */
    public static CommonIntegration mNavIntegration = null;
    public int PWD_SHOW_FLAG;
    /* access modifiers changed from: private */
    public boolean audioScramebled = false;
    /* access modifiers changed from: private */
    public boolean bSourceOnTune = false;
    /* access modifiers changed from: private */
    public View bannerLayout;
    BannerView bannerView;
    /* access modifiers changed from: private */
    public boolean cckeysend = false;
    private String channelChangeTtsText = "";
    private String channelChangeTtsTextForKey = "";
    /* access modifiers changed from: private */
    public boolean interruptShowBannerWithDelay = false;
    private boolean isBlockStateFromSVCTX;
    /* access modifiers changed from: private */
    public boolean isHostQuietTuneStatus = false;
    private boolean isOnkeyInfo;
    /* access modifiers changed from: private */
    public boolean isShowEmptyVideoInfoFirst = false;
    /* access modifiers changed from: private */
    public boolean isSpecialState = false;
    private long lastSec = 0;
    private TvCallbackHandler mBannerTvCallbackHandler;
    /* access modifiers changed from: private */
    public BasicBanner mBasicBanner;
    private CIStateChangedCallBack mCIState;
    private ChannelListDialog mChannelListDialog;
    /* access modifiers changed from: private */
    public MtkTvCI mCi = null;
    private ComponentsManager mComponentsManager;
    /* access modifiers changed from: private */
    public DetailBanner mDetailBanner;
    private boolean mDisableBanner = false;
    private FavoriteListDialog mFavoriteChannelListView;
    private boolean mFirstTimeToSystem = true;
    private boolean mIs3rdRunning = false;
    /* access modifiers changed from: private */
    public boolean mIs3rdTVSource = false;
    private boolean mIsNotShowBanner = false;
    protected MediaSession mMediaSession;
    protected MediaMetadata.Builder mMetadataBuilder;
    private SundryImplement mNavSundryImplement = null;
    private boolean mNeedChangeSource;
    /* access modifiers changed from: private */
    public boolean mNextEvent;
    /* access modifiers changed from: private */
    public boolean mNumputChangeChannel = false;
    private String mSelectedChannelNumString = "";
    /* access modifiers changed from: private */
    public SimpleBanner mSimpleBanner;
    private StateManage mStateManage;
    private SundryShowTextView mSundryTextView;
    private int mTuneChannelId = -1;
    /* access modifiers changed from: private */
    public final Handler navBannerHandler = new Handler(this.mContext.getMainLooper()) {
        public void handleMessage(Message msg) {
            int delayTime;
            if (!DestroyApp.isCurActivityTkuiMainActivity() || BannerView.this.disableShowBanner(false)) {
                if (msg.what == 1879048226) {
                    TvCallbackData specialMsgData = (TvCallbackData) msg.obj;
                    if (specialMsgData.param1 == 0) {
                        MtkLog.d(BannerView.TAG, "come in handleMessage BANNER_MSG_NAV value=== " + specialMsgData.param2);
                        switch (specialMsgData.param2) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 16:
                            case 17:
                            case 19:
                            case 20:
                                boolean unused = BannerView.this.isSpecialState = true;
                                int unused2 = BannerView.this.specialType = specialMsgData.param2;
                                break;
                            case 6:
                                int unused3 = BannerView.this.specialType = -1;
                                boolean unused4 = BannerView.this.noAudio = true;
                                boolean unused5 = BannerView.this.noVideo = true;
                                boolean unused6 = BannerView.this.audioScramebled = false;
                                boolean unused7 = BannerView.this.videoScramebled = false;
                                boolean unused8 = BannerView.this.isSpecialState = false;
                                break;
                            case 7:
                                int unused9 = BannerView.this.specialType = -1;
                                boolean unused10 = BannerView.this.videoScramebled = true;
                                boolean unused11 = BannerView.this.noVideo = false;
                                boolean unused12 = BannerView.this.noAudio = true;
                                boolean unused13 = BannerView.this.audioScramebled = false;
                                boolean unused14 = BannerView.this.isSpecialState = false;
                                break;
                            case 8:
                                int unused15 = BannerView.this.specialType = -1;
                                boolean unused16 = BannerView.this.audioScramebled = true;
                                boolean unused17 = BannerView.this.videoScramebled = true;
                                boolean unused18 = BannerView.this.noAudio = false;
                                boolean unused19 = BannerView.this.noVideo = false;
                                boolean unused20 = BannerView.this.isSpecialState = false;
                                break;
                            case 9:
                                int unused21 = BannerView.this.specialType = -1;
                                boolean unused22 = BannerView.this.videoScramebled = true;
                                boolean unused23 = BannerView.this.noVideo = false;
                                boolean unused24 = BannerView.this.audioScramebled = false;
                                boolean unused25 = BannerView.this.isSpecialState = false;
                                break;
                            case 10:
                                int unused26 = BannerView.this.specialType = -1;
                                boolean unused27 = BannerView.this.audioScramebled = true;
                                boolean unused28 = BannerView.this.noAudio = false;
                                boolean unused29 = BannerView.this.noVideo = true;
                                boolean unused30 = BannerView.this.videoScramebled = false;
                                boolean unused31 = BannerView.this.isSpecialState = false;
                                break;
                            case 11:
                                int unused32 = BannerView.this.specialType = -1;
                                boolean unused33 = BannerView.this.noVideo = true;
                                boolean unused34 = BannerView.this.noAudio = false;
                                boolean unused35 = BannerView.this.audioScramebled = false;
                                TIFChannelInfo info = TIFChannelManager.getInstance(BannerView.this.mContext).getCurrChannelInfo();
                                if (info != null) {
                                    boolean isNotHiddenCH = TIFFunctionUtil.checkChMaskformDataValue(info, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
                                    MtkLog.d(BannerView.TAG, "not hidden channel:" + isNotHiddenCH);
                                    if (!isNotHiddenCH) {
                                        int unused36 = BannerView.this.specialType = 5;
                                        break;
                                    } else {
                                        boolean unused37 = BannerView.this.isSpecialState = false;
                                        break;
                                    }
                                } else {
                                    MtkLog.d(BannerView.TAG, "the current has no channel!!!!!!!!!!!!!!!");
                                    break;
                                }
                            case 13:
                                int unused38 = BannerView.this.specialType = -1;
                                boolean unused39 = BannerView.this.noAudio = true;
                                boolean unused40 = BannerView.this.noVideo = false;
                                boolean unused41 = BannerView.this.videoScramebled = false;
                                boolean unused42 = BannerView.this.isSpecialState = false;
                                break;
                            case 14:
                                int unused43 = BannerView.this.specialType = -1;
                                boolean unused44 = BannerView.this.audioScramebled = false;
                                boolean unused45 = BannerView.this.videoScramebled = false;
                                boolean unused46 = BannerView.this.noAudio = false;
                                boolean unused47 = BannerView.this.noVideo = false;
                                boolean unused48 = BannerView.this.isSpecialState = false;
                                break;
                            case 15:
                                if (BannerView.this.specialType == 1) {
                                    int unused49 = BannerView.this.specialType = -1;
                                    break;
                                }
                                break;
                        }
                    }
                } else if (msg.what == BannerView.MSG_NUMBER_KEY) {
                    BannerView.this.turnCHByNumKey();
                }
                MtkLog.d(BannerView.TAG, "come in navBannerHandler, current apk not TKUI");
                return;
            }
            switch (msg.what) {
                case BannerView.MSG_NUMBER_KEY /*1111*/:
                    BannerView.this.turnCHByNumKey();
                    return;
                case BannerView.MSG_SHOW_SOURCE_BANNER_TIMING /*1112*/:
                    BannerView.this.mSimpleBanner.changeSourceBannerTiming();
                    return;
                case BannerView.MSG_TTS_SPEAK_DELAY /*1113*/:
                    String ttsStr = (String) msg.obj;
                    MtkLog.d(BannerView.TAG, "come in MSG_TTS_SPEAK_DELAY, ttsStr==" + ttsStr);
                    BannerView.this.ttsUtil.speak(ttsStr, 1);
                    return;
                case TvCallbackConst.MSG_CB_CONFIG /*1879048193*/:
                    TvCallbackData data = (TvCallbackData) msg.obj;
                    if (data.param1 == 2) {
                        MtkLog.d(BannerView.TAG, "pre_chg_input");
                        boolean unused50 = BannerView.this.bSourceOnTune = false;
                        return;
                    } else if (data.param1 == 0) {
                        MtkLog.d(BannerView.TAG, "chg_input");
                        if (!BannerView.mNavIntegration.isPipOrPopState()) {
                            int unused51 = BannerView.this.specialType = -1;
                            MtkLog.d(BannerView.TAG, "TvCallbackConst.MSG_CB_CONFIG");
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                case TvCallbackConst.MSG_CB_SCAN_NOTIFY /*1879048197*/:
                    if (100 == ((TvCallbackData) msg.obj).param2) {
                        BannerView.this.show(false, -1, false);
                        return;
                    }
                    return;
                case TvCallbackConst.MSG_CB_CI_MSG /*1879048215*/:
                    TvCallbackData data2 = (TvCallbackData) msg.obj;
                    MtkLog.d(BannerView.TAG, "MSG_CB_CI_MSG MTKTV_CI_BEFORE_SVC_CHANGE_SILENTLY :" + data2.param2);
                    if (data2.param2 == 23) {
                        MtkLog.d(BannerView.TAG, "MSG_CB_CI_MSG mCi.getHostQuietTuneStatus() :" + BannerView.this.mCi.getHostQuietTuneStatus());
                        if (BannerView.this.mCi.getHostQuietTuneStatus() == 1) {
                            boolean unused52 = BannerView.this.isHostQuietTuneStatus = true;
                            return;
                        }
                        return;
                    }
                    return;
                case TvCallbackConst.MSG_CB_BANNER_MSG /*1879048226*/:
                    MtkLog.d(BannerView.TAG, "come in handleMessage get msg.what === TvCallbackConst.MSG_CB_BANNER_MSG");
                    TvCallbackData msgData = (TvCallbackData) msg.obj;
                    int nativeActiveCompId = ComponentsManager.getNativeActiveCompId();
                    if (CIStateChangedCallBack.getInstance(BannerView.this.mContext).camUpgradeStatus()) {
                        MtkLog.d(BannerView.TAG, "cam card on upgrade, no permit banner show");
                        return;
                    } else if (msgData.param1 == 0) {
                        MtkLog.d(BannerView.TAG, "come in handleMessage BANNER_MSG_NAV value=== " + msgData.param2);
                        MtkLog.d(BannerView.TAG, "bSourceOnTune:" + BannerView.this.bSourceOnTune);
                        switch (msgData.param2) {
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                                if ((4 == msgData.param2 || 3 == msgData.param2 || 2 == msgData.param2) && BannerView.this.bSourceOnTune) {
                                    int unused53 = BannerView.this.specialType = msgData.param2;
                                    boolean unused54 = BannerView.this.isSpecialState = true;
                                    break;
                                }
                            case 5:
                            case 16:
                            case 19:
                            case 20:
                                break;
                            case 6:
                                int unused55 = BannerView.this.specialType = -1;
                                boolean unused56 = BannerView.this.noAudio = true;
                                boolean unused57 = BannerView.this.noVideo = true;
                                boolean unused58 = BannerView.this.audioScramebled = false;
                                boolean unused59 = BannerView.this.videoScramebled = false;
                                boolean unused60 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 7:
                                int unused61 = BannerView.this.specialType = -1;
                                boolean unused62 = BannerView.this.videoScramebled = true;
                                boolean unused63 = BannerView.this.noVideo = false;
                                boolean unused64 = BannerView.this.noAudio = true;
                                boolean unused65 = BannerView.this.audioScramebled = false;
                                boolean unused66 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 8:
                                boolean unused67 = BannerView.this.audioScramebled = true;
                                boolean unused68 = BannerView.this.videoScramebled = true;
                                boolean unused69 = BannerView.this.noAudio = false;
                                boolean unused70 = BannerView.this.noVideo = false;
                                boolean unused71 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 9:
                                int unused72 = BannerView.this.specialType = -1;
                                boolean unused73 = BannerView.this.videoScramebled = true;
                                boolean unused74 = BannerView.this.noVideo = false;
                                boolean unused75 = BannerView.this.audioScramebled = false;
                                boolean unused76 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 10:
                                int unused77 = BannerView.this.specialType = -1;
                                boolean unused78 = BannerView.this.audioScramebled = true;
                                boolean unused79 = BannerView.this.noAudio = false;
                                boolean unused80 = BannerView.this.noVideo = true;
                                boolean unused81 = BannerView.this.videoScramebled = false;
                                boolean unused82 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 11:
                                int unused83 = BannerView.this.specialType = -1;
                                boolean unused84 = BannerView.this.noVideo = true;
                                boolean unused85 = BannerView.this.noAudio = false;
                                boolean unused86 = BannerView.this.audioScramebled = false;
                                TIFChannelInfo info2 = TIFChannelManager.getInstance(BannerView.this.mContext).getCurrChannelInfo();
                                if (info2 == null) {
                                    MtkLog.d(BannerView.TAG, "the current has no channel!!!!!!!!!!!!!!!");
                                    return;
                                }
                                boolean isNotHiddenCH2 = TIFFunctionUtil.checkChMaskformDataValue(info2, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
                                MtkLog.d(BannerView.TAG, "not hidden channel:" + isNotHiddenCH2);
                                if (isNotHiddenCH2) {
                                    boolean unused87 = BannerView.this.isSpecialState = false;
                                } else {
                                    int unused88 = BannerView.this.specialType = 5;
                                }
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 13:
                                int unused89 = BannerView.this.specialType = -1;
                                boolean unused90 = BannerView.this.noAudio = true;
                                boolean unused91 = BannerView.this.noVideo = false;
                                boolean unused92 = BannerView.this.videoScramebled = false;
                                boolean unused93 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 14:
                                int unused94 = BannerView.this.specialType = -1;
                                boolean unused95 = BannerView.this.audioScramebled = false;
                                boolean unused96 = BannerView.this.videoScramebled = false;
                                boolean unused97 = BannerView.this.noAudio = false;
                                boolean unused98 = BannerView.this.noVideo = false;
                                boolean unused99 = BannerView.this.isSpecialState = false;
                                BannerView.this.channelInputChangeShowBanner();
                                return;
                            case 15:
                                if (BannerView.this.specialType == 1) {
                                    int unused100 = BannerView.this.specialType = -1;
                                    return;
                                }
                                return;
                            case 17:
                                break;
                            default:
                                return;
                        }
                        int unused101 = BannerView.this.specialType = msgData.param2;
                        if (5 == msgData.param2) {
                            TIFChannelInfo info3 = TIFChannelManager.getInstance(BannerView.this.mContext).getCurrChannelInfo();
                            if (info3 == null) {
                                MtkLog.d(BannerView.TAG, "the current has no channel!!!!!!!!!!!!!!!");
                                return;
                            }
                            boolean isNotHiddenCH3 = TIFFunctionUtil.checkChMaskformDataValue(info3, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
                            MtkLog.d(BannerView.TAG, "not hidden channel:" + isNotHiddenCH3);
                            if (isNotHiddenCH3) {
                                String country = MtkTvConfig.getInstance().getCountry();
                                MtkLog.d(BannerView.TAG, "country:" + country);
                                if ("NZL".equals(country)) {
                                    BannerView.this.show(false, -1, false);
                                    return;
                                } else if (MtkTvConfigTypeBase.S3166_CFG_COUNT_NLD.equals(country) && msgData.param2 == 5) {
                                    int unused102 = BannerView.this.specialType = msgData.param2;
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                        MtkLog.d(BannerView.TAG, "come in handleMessage BANNER_MSG_NAV showSpecialBar");
                        boolean condition1 = BannerView.mNavIntegration.isCurrentSourceBlocked();
                        if (msgData.param2 != 4 || condition1) {
                            boolean unused103 = BannerView.this.isSpecialState = true;
                            if (BannerView.this.specialType == 1) {
                                delayTime = 1000;
                            } else {
                                delayTime = 40;
                            }
                            postDelayed(new Runnable() {
                                public void run() {
                                    MtkLog.d(BannerView.TAG, "delay to show specialBar!");
                                    if (ComponentsManager.getNativeActiveCompId() != 33554434 && !BannerView.this.isMenuOptionShow() && BannerView.this.specialType != -1) {
                                        BannerView.this.showSpecialBar(BannerView.this.specialType);
                                    }
                                }
                            }, (long) delayTime);
                            return;
                        }
                        boolean unused104 = BannerView.this.isSpecialState = false;
                        MtkLog.d(BannerView.TAG, "tvapi shouldn't send this message!!");
                        return;
                    } else if (1 == msgData.param1) {
                        MtkLog.d(BannerView.TAG, "come in handleMessage BANNER_MSG_ITEM value=== " + msgData.param2);
                        int i = msgData.param2;
                        if (i != 16) {
                            if (i != 19) {
                                if (i != 27) {
                                    if (i != 36) {
                                        switch (i) {
                                            case 21:
                                                if (TurnkeyUiMainActivity.getInstance().getChangeSource()) {
                                                    MtkLog.d(BannerView.TAG, "BANNNER_MSG_ITEM_IPTS_NAME");
                                                    TurnkeyUiMainActivity.getInstance().setChangeSource(false);
                                                    BannerView.this.showSimpleBar();
                                                    return;
                                                }
                                                return;
                                            case 22:
                                                MtkLog.d(BannerView.TAG, "BANNNER_MSG_ITEM_IPTS_RSLT_CHANGE");
                                                if (BannerView.mNavIntegration.isCurrentSourceHDMI()) {
                                                    BannerView.this.mSimpleBanner.changeSourceBannerTiming();
                                                    BannerView.this.showSimpleBar();
                                                    return;
                                                }
                                                return;
                                            default:
                                                return;
                                        }
                                    }
                                }
                            }
                            MtkLog.d(BannerView.TAG, "BANNNER_MSG_ITEM_ATMOS_CHNAGE or BANNER_MSG_ITM_AUDIO_INFO_CHANGE------>msgData.param2=" + msgData.param2);
                            if (CommonIntegration.getInstance().isCHChanging()) {
                                MtkLog.d(BannerView.TAG, "No need to update audio info in changing channel.");
                                return;
                            } else if (BannerView.mNavIntegration.isCurrentSourceHDMI()) {
                                if (BannerView.this.mSimpleBanner.getVisibility() == 0) {
                                    BannerView.this.mSimpleBanner.showDoblyIcon();
                                    BannerView.this.startTimeout(5000);
                                    return;
                                }
                                return;
                            } else if (BannerView.mNavIntegration.isCurrentSourceTv() && BannerView.this.mBasicBanner.getVisibility() == 0) {
                                BannerView.this.mBasicBanner.updateAudioLanguage();
                                BannerView.this.startTimeout(5000);
                                return;
                            } else {
                                return;
                            }
                        }
                        if (MarketRegionInfo.getCurrentMarketRegion() != 3 || !BannerView.this.isSpecialState || !BannerView.this.mContext.getString(R.string.nav_hidden_channel).equals(BannerView.this.mBasicBanner.mSpecialInfo.getText().toString())) {
                            BannerView.this.updateCaptionInfo();
                            BannerView.this.updateSimpleCaptionInfo();
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                case TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO /*1879048234*/:
                    TvCallbackData msgLogoData = (TvCallbackData) msg.obj;
                    String picPath = BannerView.mNavIntegration.getChannelLogoPicPath(msgLogoData.param2, msgLogoData.param3, msgLogoData.param4);
                    MtkLog.d(BannerView.TAG, "come in MSG_CB_BANNER_CHANNEL_LOGO, picture path ==" + picPath);
                    BannerView.this.mBasicBanner.showChannelLogo(picPath);
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean noAudio = false;
    /* access modifiers changed from: private */
    public boolean noVideo = false;
    /* access modifiers changed from: private */
    public int specialType = -1;
    private int tempType = -1;
    /* access modifiers changed from: private */
    public final ITimeChange timeTimeChange = new ITimeChange() {
        public void refresh(final TextView tv) {
            MtkLog.v(BannerView.TAG, "timeTimeChange refresh");
            final String value = BannerView.mNavBannerImplement.getCurrentTime();
            tv.post(new Runnable() {
                public void run() {
                    MtkLog.v(BannerView.TAG, "timeTimeChange refresh time == " + value);
                    tv.setText(value);
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public View topBanner;
    private int totalState = -1;
    /* access modifiers changed from: private */
    public TextToSpeechUtil ttsUtil;
    /* access modifiers changed from: private */
    public final ITimeChange videoFormatSourceTimeChange = new ITimeChange() {
        public void refresh(final TextView tv) {
            final String value;
            boolean is3rdTVSource = BannerView.mNavIntegration.is3rdTVSource();
            MtkLog.d(BannerView.TAG, "come in videoFormatSourceTimeChange is3rdTVSource=" + is3rdTVSource);
            if (!is3rdTVSource && BannerView.this.videoScramebled) {
                value = BannerView.this.mContext.getString(R.string.nav_channel_video_scrambled);
            } else if (!is3rdTVSource && BannerView.this.noVideo) {
                MtkLog.d(BannerView.TAG, "come in videoFormatSourceTimeChange == noVideo");
                value = BannerView.this.mContext.getString(R.string.nav_resolution_null);
            } else if (is3rdTVSource || BannerView.mNavIntegration.isCurrentSourceHasSignal()) {
                value = BannerView.mNavBannerImplement.getInputResolution();
            } else {
                MtkLog.d(BannerView.TAG, "come in videoFormatSourceTimeChange == isCurrentSourceHasSignal");
                value = BannerView.this.mContext.getString(R.string.nav_resolution_null);
            }
            tv.post(new Runnable() {
                public void run() {
                    if (value == null || (!value.equals(BannerView.this.mContext.getString(R.string.nav_channel_video_scrambled)) && !value.equals(BannerView.this.mContext.getString(R.string.nav_resolution_null)))) {
                        tv.setText(value);
                    } else {
                        tv.setText((CharSequence) null);
                    }
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public final ITimeChange videoFormatTimeChange = new ITimeChange() {
        public void refresh(final TextView tv) {
            final String value;
            boolean is3rdTVSource = BannerView.mNavIntegration.is3rdTVSource();
            MtkLog.d(BannerView.TAG, "come in videoFormatTimeChange is3rdTVSource=" + is3rdTVSource);
            if (!is3rdTVSource && BannerView.this.videoScramebled) {
                value = BannerView.this.mContext.getString(R.string.nav_channel_video_scrambled);
            } else if (is3rdTVSource || !BannerView.this.noVideo) {
                StateNormal.setisVideo(false);
                value = BannerView.mNavBannerImplement.getCurrentVideoInfo();
            } else {
                StateNormal.setisVideo(true);
                value = BannerView.this.mContext.getString(R.string.nav_resolution_null);
            }
            tv.post(new Runnable() {
                public void run() {
                    tv.setText(value);
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public boolean videoScramebled = false;

    interface ITimeChange {
        void refresh(TextView textView);
    }

    public void handlerHbbtvMessage(int type, int message) {
        MtkLog.d(TAG, "handlerHbbtvMessage, type=" + type + ", message=" + message);
        switch (type) {
            case 257:
                MtkLog.d(TAG, "handlerHbbtvMessage, HBBTV_MSG_DISABLE_BANNER");
                this.mDisableBanner = true;
                return;
            case 258:
                this.mDisableBanner = false;
                MtkLog.d(TAG, "handlerHbbtvMessage, HBBTV_MSG_ENABLE_BANNER");
                return;
            case 262:
                MtkLog.d(TAG, "handlerHbbtvMessage, HBBTV_STATUS_3RD_APP_RUNNING");
                this.mIs3rdRunning = true;
                return;
            case 263:
                MtkLog.d(TAG, "handlerHbbtvMessage, HBBTV_STATUS_3RD_APP_NOT_RUNNING");
                this.mIs3rdRunning = false;
                return;
            default:
                return;
        }
    }

    public void handlerMheg5Message(int type, int message) {
        MtkLog.d(TAG, "handlerMheg5Message, type=" + type + ", message=" + message);
        if (type == 258) {
            switch (message) {
                case 0:
                    MtkLog.d(TAG, "handlerMheg5Message, MHEG5_MSG_DISABLE_BANNER");
                    this.mDisableBanner = true;
                    return;
                case 1:
                    this.mDisableBanner = false;
                    MtkLog.d(TAG, "handlerMheg5Message, MHEG5_MSG_ENABLE_BANNER");
                    return;
                default:
                    return;
            }
        }
    }

    public void handleBlockStateFromSVCTX(boolean blockState) {
        MtkLog.d(TAG, "handleBlockStateFromSVCTX blockState=" + blockState + ", specialType=" + this.specialType);
        this.specialType = blockState ? this.specialType : -1;
        this.isBlockStateFromSVCTX = blockState;
    }

    public boolean isSpecialLockState() {
        MtkLog.d(TAG, "isSpecialLockState---->specialType=" + this.specialType);
        return (this.specialType == 2 || this.specialType == 4 || this.specialType == 3) && this.isBlockStateFromSVCTX;
    }

    public void tuneChannelAfterTuneTVSource() {
        if (this.mNeedChangeSource) {
            this.mNeedChangeSource = false;
            this.mTuneChannelId = -1;
        }
    }

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleBanner getSimpleBanner() {
        return this.mSimpleBanner;
    }

    public BasicBanner getBasicBanner() {
        return this.mBasicBanner;
    }

    public void updateBasicBarAudio() {
        if (this.mBasicBanner.getVisibility() == 0) {
            this.mBasicBanner.updateAudioLanguage();
        }
    }

    public void updateCaptionInfo() {
        MtkLog.d(TAG, "updateCaptionInfo");
        if (this.mBasicBanner.getVisibility() == 0) {
            this.mBasicBanner.setCaptionVisibility();
            this.mBasicBanner.setEyeOrEarHinderIconVisibility();
        }
    }

    public void updateSimpleCaptionInfo() {
        MtkLog.d(TAG, "updateSimpleCaptionInfo");
        if (!mNavIntegration.isCurrentSourceTv()) {
            this.mSimpleBanner.showCCView();
        }
    }

    public boolean isAudioScrambled() {
        return this.audioScramebled;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public boolean isCoExist(int componentID) {
        switch (componentID) {
            case 16777217:
            case NavBasic.NAV_COMP_ID_CH_LIST /*16777221*/:
            case 16777232:
                return false;
            case NavBasic.NAV_COMP_ID_PVR_TIMESHIFT /*16777241*/:
                return true;
            case NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG /*16777244*/:
                return true;
            default:
                return true;
        }
    }

    public boolean isKeyHandler(int keyCode) {
        String channelChangeTtsTexttemp;
        String notTVAnd3rdTVsource;
        this.PWD_SHOW_FLAG = 0;
        if (keyCode != 93) {
            if (keyCode == 165) {
                MtkLog.v(TAG, "come in isKeyHandler KEYCODE_MTKIR_INFO");
                if (disableShowBanner(true)) {
                    MtkLog.v(TAG, "come in isKeyHandler,noting to do!");
                    return false;
                } else if (!TextUtils.isEmpty(this.mSelectedChannelNumString)) {
                    KeyHandler(56, (KeyEvent) null, false);
                } else {
                    ((TwinkleView) ComponentsManager.getInstance().getComponentById(16777232)).setVisibility(8);
                    hideSundryTextView();
                    hideFavoriteChannelList();
                    this.isOnkeyInfo = true;
                    this.mSimpleBanner.showCCView();
                    show(true, -1, true, true);
                    if ((mNavBannerImplement != null && this.ttsUtil != null && mNavIntegration.isCurrentSourceTv()) || mNavIntegration.is3rdTVSource()) {
                        MtkLog.d(TAG, "channelChangeTtsTexttemp ture");
                        if (TextUtils.isEmpty(mNavBannerImplement.getCurrentChannelName())) {
                            channelChangeTtsTexttemp = "channel number is " + mNavBannerImplement.getCurrentChannelNum();
                        } else {
                            channelChangeTtsTexttemp = "channel number is " + mNavBannerImplement.getCurrentChannelNum() + " channel name is " + mNavBannerImplement.getCurrentChannelName();
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("channelChangeTtsTextForKey =");
                        sb.append(this.channelChangeTtsTextForKey);
                        sb.append(" ``` ");
                        sb.append(this.channelChangeTtsTextForKey.equals(""));
                        sb.append(" +++ ");
                        sb.append(!this.channelChangeTtsTextForKey.equals(channelChangeTtsTexttemp));
                        sb.append(" -- ");
                        sb.append(mNavIntegration.getCurrentChannelId() > 0);
                        MtkLog.d(TAG, sb.toString());
                        if ((TextUtils.equals(this.channelChangeTtsTextForKey, "") || !TextUtils.equals(this.channelChangeTtsTextForKey, channelChangeTtsTexttemp)) && mNavIntegration.getCurrentChannelId() > 0) {
                            MtkLog.d(TAG, "channelChangeTtsTextForKey ture");
                            if (mNavIntegration.isMenuInputTvBlock()) {
                                this.channelChangeTtsTextForKey = "Source name is TV";
                            } else {
                                this.channelChangeTtsTextForKey = channelChangeTtsTexttemp;
                            }
                            sendTTSSpeakMsg(this.channelChangeTtsTextForKey);
                        }
                    } else if (mNavBannerImplement != null && this.ttsUtil != null && !mNavIntegration.isCurrentSourceTv() && !mNavIntegration.is3rdTVSource()) {
                        String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName(mNavIntegration.getCurrentFocus());
                        if (!TextUtils.equals(sourceName, "")) {
                            notTVAnd3rdTVsource = "Source name is " + sourceName;
                        } else {
                            notTVAnd3rdTVsource = "Source name is null";
                        }
                        sendTTSSpeakMsg(notTVAnd3rdTVsource);
                    }
                    MtkLog.v(TAG, "come in isKeyHandler KEYCODE_MTKIR_INFO show(true, -1, true)");
                    this.isOnkeyInfo = false;
                    return true;
                }
            } else if (keyCode == 175) {
                MtkLog.d(TAG, "come in isKeyHandler KEYCODE_MTKIR_MTKIR_CC");
                if (mNavIntegration.is3rdTVSource()) {
                    mNavBannerImplement.changeNextCloseCaption();
                    return false;
                } else if (MarketRegionInfo.getCurrentMarketRegion() == 3 || MarketRegionInfo.getCurrentMarketRegion() == 0) {
                    return isKeyHandler(215);
                } else {
                    hideSundryTextView();
                    hideFavoriteChannelList();
                    if (mNavIntegration.isCurrentSourceTv() || mNavIntegration.is3rdTVSource()) {
                        this.mSimpleBanner.mCCView.setVisibility(8);
                        if (getVisibility() != 0) {
                            if (this.mStateManage.getState() >= 2 || this.isSpecialState) {
                                show(false, -1, false, true);
                            } else {
                                show(false, 2, false);
                                this.mStateManage.setState(2);
                            }
                        }
                        if (this.mNavSundryImplement.isFreeze()) {
                            this.mNavSundryImplement.setFreeze(false);
                        }
                        String curCCStr = this.mBasicBanner.mSubtitleOrCCIcon.getText().toString();
                        MtkLog.d(TAG, "cc Iskeyhandler~ curCCStr =" + curCCStr);
                        if (!(this.ttsUtil == null || curCCStr == null || curCCStr.length() <= 0)) {
                            sendTTSSpeakMsg(curCCStr);
                        }
                        return true;
                    }
                    String CCString = mNavBannerImplement.getBannerCaptionInfo();
                    if (CCString == null || "".equals(CCString)) {
                        this.mSimpleBanner.mCCView.setVisibility(8);
                    } else {
                        this.mSimpleBanner.mCCView.setVisibility(0);
                        this.mSimpleBanner.mCCView.setText(CCString);
                        if (this.ttsUtil != null) {
                            sendTTSSpeakMsg(CCString);
                        }
                    }
                    show(false, 1, false, true);
                    return true;
                }
            } else if (keyCode == 215 || keyCode == 218) {
                MtkLog.d(TAG, "come in isKeyHandler KEYCODE_MTKIR_SUBTITLE mIs3rdRunning: " + this.mIs3rdRunning);
                if (mNavIntegration.isCurrentSourceTv() && ((!disableShowBanner(true) || keyCode != 215) && (3 == MarketRegionInfo.getCurrentMarketRegion() || MarketRegionInfo.getCurrentMarketRegion() == 0))) {
                    if (getVisibility() != 0) {
                        if (this.mStateManage.getState() >= 2 || this.isSpecialState) {
                            show(false, -1, false, true);
                        } else {
                            show(false, 2, false, true);
                            this.mStateManage.setState(2);
                        }
                    }
                    return true;
                }
            } else {
                switch (keyCode) {
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        MtkLog.d(TAG, "come in isKeyHandler number key getVisibility" + getVisibility());
                        MtkLog.d(TAG, "come in isKeyHandler number key isCurrentSourceTv" + mNavIntegration.isCurrentSourceTv());
                        MtkLog.d(TAG, "come in isKeyHandler number key getChannelCountFromSVL" + mNavIntegration.getChannelCountFromSVL());
                        if (mNavIntegration.is3rdTVSource() || !mNavIntegration.isCurrentSourceTv()) {
                            MtkLog.d(TAG, "not handle digtal key.");
                            startTimeout(1000);
                            return true;
                        }
                        if (StateDvrFileList.getInstance() != null && StateDvrFileList.getInstance().isShowing()) {
                            StateDvrFileList.getInstance().dissmiss();
                        }
                        if (getVisibility() != 0) {
                            if (isSourceLockedOrEmptyChannel()) {
                                MtkLog.d(TAG, "come in isKeyHandler number key isCurrentSourceBlocked or no channel");
                                this.PWD_SHOW_FLAG = 1;
                                show(false, -1, false);
                                return true;
                            } else if (mNavIntegration.isCurrentSourceTv()) {
                                if (mNavIntegration.getChannelCountFromSVL() > 0) {
                                    sendTurnCHMsg(keyCode);
                                }
                            } else if (mNavIntegration.isPipOrPopState()) {
                                return true;
                            } else {
                                sendTurnCHMsg(keyCode);
                            }
                        }
                        return true;
                }
            }
            return false;
        }
        if (mNavIntegration.isCurrentSourceTv() && !mNavIntegration.isCurrentSourceBlocked() && mNavIntegration.getChannelCountFromSVL() > 0 && !mNavIntegration.is3rdTVSource()) {
            ComponentsManager.getInstance().hideAllComponents();
            FavChannelManager.getInstance(this.mContext).favAddOrErase();
        }
        changeChannelFavoriteMark();
        return true;
    }

    private boolean isHandled() {
        long curSec = SystemClock.uptimeMillis();
        MtkLog.d(TAG, "curSec: " + curSec);
        MtkLog.d(TAG, "lastSec: " + this.lastSec);
        MtkLog.d(TAG, "curSec - lastSec: " + (curSec - this.lastSec));
        if (curSec - this.lastSec < 1000) {
            return false;
        }
        this.lastSec = curSec;
        return true;
    }

    /* access modifiers changed from: private */
    public void sendTTSSpeakMsg(String ttsStr) {
        Message msg = new Message();
        msg.what = MSG_TTS_SPEAK_DELAY;
        msg.obj = ttsStr;
        this.navBannerHandler.sendMessageDelayed(msg, 1000);
    }

    private boolean isSourceLockedOrEmptyChannel() {
        boolean isSourceLocked = mNavIntegration.isCurrentSourceBlocked() && MtkTvPWDDialog.getInstance().PWDShow() == 0;
        boolean isEmptyChannel = mNavIntegration.isCurrentSourceTv() && mNavIntegration.getChannelCountFromSVL() <= 0;
        if (isSourceLocked || isEmptyChannel) {
            return true;
        }
        return false;
    }

    public void changeFavChannel() {
        if (mNavIntegration != null && mNavIntegration.isCurrentSourceTv() && mNavIntegration.getChannelAllNumByAPI() > 0) {
            mNavIntegration.iSetCurrentChannelFavorite();
            this.mBasicBanner.setFavoriteVisibility();
            show(false, -1, false);
        }
    }

    /* access modifiers changed from: private */
    public void turnCHByNumKey() {
        MtkTvChannelInfoBase curMtkTvChannelInfoBase;
        MtkLog.d(TAG, "turnCHByNumKey---->mSelectedChannelNumString=" + this.mSelectedChannelNumString);
        int channelID = mNavIntegration.getChannelIDByBannerNum(this.mSelectedChannelNumString);
        MtkLog.d(TAG, "channelID=" + channelID);
        MtkTvChannelInfoBase chanel = CommonIntegration.getInstance().getChannelById(channelID);
        if (isSupportTurnCHByNumKey(chanel)) {
            this.mNumputChangeChannel = false;
            this.mSelectedChannelNumString = "";
        } else if (!CommonIntegration.isEUPARegion() || !MarketRegionInfo.isFunctionSupport(13) || (curMtkTvChannelInfoBase = mNavIntegration.getCurChInfoByTIF()) == null || curMtkTvChannelInfoBase.getBrdcstType() == chanel.getBrdcstType()) {
            if (CommonIntegration.isEURegion() && !chanel.isAnalogService()) {
                boolean isNumberSelectable = chanel.isNumberSelectable();
                MtkLog.d(TAG, "isNumberSelectable=" + isNumberSelectable);
                if (!isNumberSelectable) {
                    this.mNumputChangeChannel = false;
                    this.mSelectedChannelNumString = "";
                    return;
                }
            }
            if (!(StateDvr.getInstance() == null || !StateDvr.getInstance().isRunning() || channelID == -1)) {
                MtkLog.d(TAG, "change channel stop dvr");
                if (mNavIntegration.getCurrentChannelId() != channelID || !TextUtils.equals(InputSourceManager.getInstance().getCurrentInputSourceName("main"), "TV")) {
                    String srctype = DvrManager.getInstance().getController().getSrcType();
                    if (TextUtils.equals(srctype, "TV") || ((InputSourceManager.getInstance(this.mContext) != null && InputSourceManager.getInstance(this.mContext).getConflictSourceList().contains(srctype)) || (chanel instanceof MtkTvAnalogChannelInfo))) {
                        if (InputSourceManager.getInstance().isCurrentTvSource("main")) {
                            DvrDialog dvrDialog = new DvrDialog((Activity) TurnkeyUiMainActivity.getInstance(), 1, (int) DvrDialog.TYPE_Change_ChannelNum, this.bannerView, 1);
                            dvrDialog.setChangeChannelNum(this.mSelectedChannelNumString);
                            dvrDialog.show();
                            return;
                        }
                        DvrDialog dvrDialog2 = new DvrDialog((Activity) TurnkeyUiMainActivity.getInstance(), 1, (int) DvrDialog.TYPE_Change_ChannelNum_SRC, this.bannerView, 1);
                        dvrDialog2.setChangeChannelNum(this.mSelectedChannelNumString);
                        dvrDialog2.show();
                        return;
                    }
                }
            }
            if (-1 == channelID) {
                show(false, -1, false);
            } else if (!mNavIntegration.isCurrentSourceTv()) {
                mNavIntegration.iSetSourcetoTv(channelID);
                this.mTuneChannelId = channelID;
                this.mNeedChangeSource = true;
            } else if (canTurnCH(chanel) || canTurnCHForFaker(chanel) || isHideCH(chanel)) {
                MtkLog.d(TAG, "start selectChannelByTIFInfo");
                TvSingletons.getSingletons().getChannelDataManager().selectChannelByTIFInfo(TvSingletons.getSingletons().getChannelDataManager().getHideChannelById(channelID));
            }
            this.mNumputChangeChannel = false;
            this.mSelectedChannelNumString = "";
        } else {
            MtkLog.d(TAG, "for PA,not tune other tv source channel.");
            this.mNumputChangeChannel = false;
            this.mSelectedChannelNumString = "";
        }
    }

    private boolean isSupportTurnCHByNumKey(MtkTvChannelInfoBase chanel) {
        if (chanel == null) {
            MtkLog.d(TAG, "chanel==null");
            return true;
        } else if (!chanel.isUserDelete()) {
            return false;
        } else {
            if (!CommonIntegration.isEUPARegion() && !CommonIntegration.isEURegion()) {
                return false;
            }
            MtkLog.d(TAG, "chanel.isUserDelete(): " + chanel.isUserDelete());
            return true;
        }
    }

    private boolean canTurnCH(MtkTvChannelInfoBase chanel) {
        int mask = SaveValue.getInstance(this.mContext).readValue(CommonIntegration.channelListfortypeMask, CommonIntegration.CH_LIST_MASK);
        int maskValue = SaveValue.getInstance(this.mContext).readValue(CommonIntegration.channelListfortypeMaskvalue, CommonIntegration.CH_LIST_VAL);
        MtkLog.d(TAG, "mask=" + mask + ",maskValue=" + maskValue);
        if ((CommonIntegration.isEUPARegion() || CommonIntegration.isCNRegion()) && mNavIntegration.isCurrentSourceATV()) {
            MtkLog.d(TAG, "PA Region can turn channel, change analog mask!");
            mask = CommonIntegration.CH_LIST_ANALOG_MASK;
            maskValue = CommonIntegration.CH_LIST_ANALOG_VAL;
        }
        boolean canTurnCH = mNavIntegration.checkChMask(chanel, mask, maskValue);
        MtkLog.d(TAG, "canTurnCH=" + canTurnCH);
        return canTurnCH;
    }

    private boolean isHideCH(MtkTvChannelInfoBase chanel) {
        boolean isHideCH = !mNavIntegration.checkChMask(chanel, CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
        MtkLog.d(TAG, "isHideCH=" + isHideCH);
        return isHideCH;
    }

    private boolean canTurnCHForFaker(MtkTvChannelInfoBase chanel) {
        CommonIntegration commonIntegration = mNavIntegration;
        if (!CommonIntegration.isUSRegion()) {
            CommonIntegration commonIntegration2 = mNavIntegration;
            if (!CommonIntegration.isSARegion()) {
                return false;
            }
        }
        boolean canTurnCHforFaker = mNavIntegration.checkChMask(chanel, CommonIntegration.CH_FAKE_MASK, CommonIntegration.CH_FAKE_VAL);
        MtkLog.d(TAG, "canTurnCHforFaker=" + canTurnCHforFaker);
        return canTurnCHforFaker;
    }

    private void sendTurnCHMsg(int keyCode) {
        MtkLog.d(TAG, "come in sendTurnCHMsg keyCode =" + keyCode);
        inputChannelNum(keyCode);
        if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            this.mSelectedChannelNumString = this.mSelectedChannelNumString.replace(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING, ".");
        } else {
            this.mSelectedChannelNumString = this.mSelectedChannelNumString.replace(".", MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
        }
        MtkLog.d(TAG, "come in sendTurnCHMsg mSelectedChannelNumString =" + this.mSelectedChannelNumString);
        this.mSimpleBanner.updateInputting(this.mSelectedChannelNumString);
        startTimeout(5000);
        this.navBannerHandler.removeMessages(MSG_NUMBER_KEY);
        this.navBannerHandler.sendEmptyMessageDelayed(MSG_NUMBER_KEY, 3000);
    }

    public boolean KeyHandler(int keyCode, KeyEvent event, boolean fromNative) {
        int navcompid = ComponentsManager.getActiveCompId();
        MtkLog.d(TAG, "navcompid=" + navcompid + ", keyCode=" + keyCode);
        if (navcompid == 33554433) {
            if (event != null) {
                ComponentsManager.nativeComponentReActive();
                return false;
            }
            ComponentsManager.updateActiveCompId(false, this.componentID);
        }
        boolean isHandler = true;
        if (keyCode != 4) {
            if (!(keyCode == 56 || keyCode == 76)) {
                if (keyCode == 93) {
                    MtkLog.d(TAG, "KEYCODE_MTKIR_EJECT");
                    if (mNavIntegration.isCurrentSourceTv() && !mNavIntegration.isCurrentSourceBlocked() && mNavIntegration.getChannelCountFromSVL() > 0 && !mNavIntegration.is3rdTVSource()) {
                        FavChannelManager.getInstance(this.mContext).favAddOrErase();
                    }
                    changeChannelFavoriteMark();
                } else if (keyCode != 165) {
                    if (keyCode != 175) {
                        if (keyCode != 215 && keyCode != 218) {
                            switch (keyCode) {
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                    break;
                                default:
                                    switch (keyCode) {
                                        case 19:
                                        case 20:
                                            this.mChannelListDialog = (ChannelListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
                                            this.mFavoriteChannelListView = (FavoriteListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
                                            if (!this.mChannelListDialog.isShowing() && !this.mFavoriteChannelListView.isShowing() && this.mDetailBanner.getDetailBannerVisible()) {
                                                if (19 == keyCode) {
                                                    this.mDetailBanner.pageUp();
                                                } else {
                                                    this.mDetailBanner.pageDown();
                                                }
                                                int curPagenum = this.mDetailBanner.detailTextReader.getCurPagenum();
                                                if (this.mDetailBanner.detailTextReader.getTotalPage() > 1) {
                                                    MtkLog.d(TAG, "num>1,deal it,so it will change channel,but to change page.");
                                                    isHandler = true;
                                                    break;
                                                } else {
                                                    MtkLog.d(TAG, "Detail page total num<=1,not to deal,pass to KEYCODE_DPAD_UP ");
                                                    isHandler = false;
                                                    startTimeout(5000);
                                                    break;
                                                }
                                            } else {
                                                isHandler = false;
                                                break;
                                            }
                                            break;
                                        case 21:
                                            MtkLog.d(TAG, "come in keyHandle KEYCODE_DPAD_LEFT mNextEvent: " + this.mNextEvent);
                                            this.mNextEvent = false;
                                            show(false, -1, true, true);
                                            break;
                                        case 22:
                                            MtkLog.d(TAG, "come in keyHandle KEYCODE_DPAD_RIGHT mNextEvent: " + this.mNextEvent);
                                            this.mNextEvent = true;
                                            show(false, -1, true, true);
                                            break;
                                        case 23:
                                            if (!this.mNumputChangeChannel) {
                                                isHandler = false;
                                                break;
                                            } else {
                                                this.navBannerHandler.removeMessages(MSG_NUMBER_KEY);
                                                this.navBannerHandler.sendEmptyMessage(MSG_NUMBER_KEY);
                                                break;
                                            }
                                        default:
                                            isHandler = false;
                                            break;
                                    }
                            }
                        } else {
                            MtkLog.d(TAG, "come in keyHandle KEYCODE_MTKIR_SUBTITLE mIs3rdRunning: " + this.mIs3rdRunning);
                            if (mNavIntegration.isCurrentSourceTv() && (!disableShowBanner(true) || keyCode != 215)) {
                                if (3 != MarketRegionInfo.getCurrentMarketRegion() && MarketRegionInfo.getCurrentMarketRegion() != 0) {
                                    isHandler = false;
                                } else if (mNavBannerImplement.isCurrentSourceATV() && event != null) {
                                    MtkLog.d(TAG, "close teletext");
                                    KeyDispatch.getInstance().passKeyToNative(keyCode, event);
                                } else if (this.mStateManage.getState() >= 2 || this.isSpecialState) {
                                    MtkLog.d(TAG, "subtitile keyevent~ change next close caption!");
                                    mNavBannerImplement.changeNextCloseCaption();
                                } else {
                                    show(false, 2, false, true);
                                    this.mStateManage.setState(2);
                                }
                            }
                        }
                    } else if (MarketRegionInfo.getCurrentMarketRegion() == 3 || MarketRegionInfo.getCurrentMarketRegion() == 0) {
                        return KeyHandler(215, (KeyEvent) null, false);
                    } else {
                        MtkLog.d(TAG, "come in KeyHandler KEYCODE_MTKIR_MTKIR_CC");
                        hideSundryTextView();
                        hideFavoriteChannelList();
                        this.cckeysend = true;
                        if (this.mStateManage.getState() >= 2 || this.isSpecialState) {
                            if (MarketRegionInfo.getCurrentMarketRegion() != 3) {
                                mNavBannerImplement.changeNextCloseCaption();
                            }
                            this.mSimpleBanner.showCCView();
                        } else {
                            show(false, 2, false, true);
                            this.mStateManage.setState(2);
                        }
                    }
                } else if (!TextUtils.isEmpty(this.mSelectedChannelNumString)) {
                    KeyHandler(56, (KeyEvent) null, false);
                } else {
                    hideSundryTextView();
                    hideFavoriteChannelList();
                    this.isOnkeyInfo = true;
                    show(true, -1, true, true);
                    this.isOnkeyInfo = false;
                    if (mNavIntegration.isPipOrPopState()) {
                        if (!MarketRegionInfo.isFunctionSupport(13)) {
                            ((FocusLabelControl) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_POP)).reShowFocus();
                        } else if (MarketRegionInfo.isFunctionSupport(26)) {
                            ((MultiViewControl) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_POP)).reShowFocus();
                        } else {
                            ((TVStateControl) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_POP)).reShowFocus();
                        }
                    }
                }
            }
            MtkLog.d(TAG, "KeyHandler Digtal key keyCode=" + keyCode);
            if (mNavIntegration.is3rdTVSource() || !mNavIntegration.isCurrentSourceTv()) {
                MtkLog.d(TAG, "not handle digtal key.");
                startTimeout(1000);
                return true;
            } else if (isSourceLockedOrEmptyChannel()) {
                MtkLog.d(TAG, "come in KeyHandler number key isCurrentSourceBlocked or no channel");
                startTimeout(5000);
                return true;
            } else if ((3 == MarketRegionInfo.getCurrentMarketRegion() || this.mSelectedChannelNumString.length() == 0) && keyCode == 56) {
                MtkLog.d(TAG, "KeyHandler return for period and number is null.");
                return true;
            } else if (mNavIntegration.isCurrentSourceTv()) {
                if (mNavIntegration.getChannelCountFromSVL() > 0) {
                    MtkLog.v(TAG, "come in input key number 1");
                    sendTurnCHMsg(keyCode);
                }
            } else if (mNavIntegration.isPipOrPopState()) {
                return true;
            } else {
                MtkLog.v(TAG, "come in input key number 2");
                sendTurnCHMsg(keyCode);
            }
        } else {
            isHandler = false;
            if (this.mNumputChangeChannel) {
                cancelNumChangeChannel();
            }
            if (getVisibility() != 8) {
                setVisibility(8);
            }
        }
        if (isHandler) {
            startTimeout(5000);
        }
        return isHandler;
    }

    public void setVisibility(int visibility) {
        if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
            hideAllBanner();
        } else if (((MenuOptionMain) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG)).isShowing()) {
            hideAllBanner();
        } else {
            MtkLog.d(TAG, "come in BannerView setVisibility ==" + visibility);
            if (visibility == 0) {
                UpdaterDialog dialog = (UpdaterDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_UPDATER);
                if (dialog != null && dialog.isShowing()) {
                    return;
                }
            } else {
                hideAllBanner();
                this.channelChangeTtsTextForKey = "";
                this.mSelectedChannelNumString = "";
            }
            super.setVisibility(visibility);
        }
    }

    private void init(Context context) {
        this.mCIState = CIStateChangedCallBack.getInstance(context);
        this.mCi = CIStateChangedCallBack.getInstance(this.mContext).getCIHandle();
        this.componentID = NavBasic.NAV_COMP_ID_BANNER;
        this.bannerView = this;
        this.ttsUtil = new TextToSpeechUtil(context);
        this.mStateManage = new StateManage();
        this.mStateManage.addState(0);
        this.mStateManage.addState(1);
        this.mStateManage.addState(2);
        this.mStateManage.addState(3);
        this.mStateManage.setState(2);
        inflate(this.mContext, R.layout.nav_banner_layout, this);
        mNavIntegration = CommonIntegration.getInstance();
        mNavBannerImplement = BannerImplement.getInstanceNavBannerImplement(this.mContext);
        this.mSimpleBanner = new SimpleBanner(this.mContext);
        this.mBasicBanner = new BasicBanner(this.mContext);
        this.mDetailBanner = new DetailBanner(this.mContext);
        this.bannerLayout = findViewById(R.id.banner_info_layout);
        this.topBanner = findViewById(R.id.banner_top_layout);
        this.topBanner.setLayoutParams(new LinearLayout.LayoutParams((int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.889d), (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.16d)));
        this.mBasicBanner.initView();
        this.mSimpleBanner.initView();
        this.mDetailBanner.initView();
        setAlpha(0.9f);
        this.mNavSundryImplement = SundryImplement.getInstanceNavSundryImplement(context);
        this.mComponentsManager = ComponentsManager.getInstance();
        this.mBannerTvCallbackHandler = TvCallbackHandler.getInstance();
        this.mBannerTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_CI_MSG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_SCAN_NOTIFY, this.navBannerHandler);
        this.mBannerTvCallbackHandler.addCallBackListener(TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO, this.navBannerHandler);
        ComponentStatusListener.getInstance().addListener(5, this);
        ComponentStatusListener.getInstance().addListener(2, this);
        ComponentStatusListener.getInstance().addListener(1, this);
        ComponentStatusListener.getInstance().addListener(4, this);
        ComponentStatusListener.getInstance().addListener(3, this);
        ComponentStatusListener.getInstance().addListener(10, this);
        ComponentStatusListener.getInstance().addListener(12, this);
        this.mMediaSession = new MediaSession(context, "com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity");
        this.mMetadataBuilder = new MediaMetadata.Builder();
        final InputSourceManager inputSourceManager = InputSourceManager.getInstance();
        inputSourceManager.addListener(new InputSourceManager.ISourceListListener() {
            public void onAvailabilityChanged(String inputId, int state) {
                MtkLog.d(BannerView.TAG, "come in callback onAvailabilityChanged start.");
                AbstractInput input = InputUtil.getInput(inputSourceManager.getCurrentInputSourceHardwareId());
                if (input != null && input.isHDMI() && inputId.equals(input.getTvInputInfo().getId()) && BannerView.this.bannerLayout.getVisibility() == 0) {
                    MtkLog.d(BannerView.TAG, "come in callback onAvailabilityChanged show banner.");
                    BannerView.this.showSimpleBanner();
                }
            }
        });
    }

    public boolean startComponent() {
        MtkLog.d(TAG, "come in startComponent");
        if (this.mFirstTimeToSystem) {
            MtkLog.d(TAG, "come in startComponent mFirstTimeToSystem == true");
            if (!mNavIntegration.isCurrentSourceTv() || mNavIntegration.getChannelCountFromSVL() <= 0) {
                MtkLog.d(TAG, "come in startComponent mFirstTimeToSystem == true and no channel");
                showSimpleBar();
            } else {
                showSimpleBar();
                MtkLog.d(TAG, "come in startComponent mFirstTimeToSystem == true and has channel");
                this.navBannerHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (!BannerView.this.isSpecialState && !BannerView.mNavIntegration.isCurrentSourceBlocked()) {
                            BannerView.this.showBasicBar();
                            BannerView.this.setBannerState(2);
                        }
                    }
                }, 1000);
            }
            this.mFirstTimeToSystem = false;
            setInterruptShowBanner(false);
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        } else if (DestroyApp.isCurActivityTkuiMainActivity()) {
            if (!this.mNumputChangeChannel) {
                if (!mNavIntegration.is3rdTVSource() && mNavIntegration.isCurrentSourceTv() && mNavIntegration.getChannelCountFromSVL() <= 0) {
                    this.isSpecialState = true;
                    this.specialType = 16;
                    MtkLog.d(TAG, "please scan channel!!!");
                }
                this.isShowEmptyVideoInfoFirst = true;
                show(false, -1, false);
                this.isShowEmptyVideoInfoFirst = false;
            }
            MtkLog.d(TAG, "come in startComponent mFirstTimeToSystem == false");
            setInterruptShowBanner(false);
            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        }
        return true;
    }

    public boolean deinitView() {
        this.mBannerTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_MSG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_CI_MSG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_CONFIG, this.navBannerHandler);
        this.mBannerTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_SCAN_NOTIFY, this.navBannerHandler);
        this.mBannerTvCallbackHandler.removeCallBackListener(TvCallbackConst.MSG_CB_BANNER_CHANNEL_LOGO, this.navBannerHandler);
        cancelTimerTask();
        return true;
    }

    class StateManage {
        private int curState = -1;
        private boolean orietation = true;
        private final List<Integer> stateArray = new ArrayList();

        StateManage() {
        }

        /* access modifiers changed from: private */
        public void addState(int state) {
            this.stateArray.add(Integer.valueOf(state));
            MtkLog.v(BannerView.TAG, "stateArray.add(state); ");
            this.curState = 0;
        }

        /* access modifiers changed from: private */
        public void setState(int state) {
            MtkLog.v(BannerView.TAG, "StateManage setState " + state);
            for (Integer intValue : this.stateArray) {
                int temp = intValue.intValue();
                if (temp == state) {
                    this.curState = this.stateArray.indexOf(Integer.valueOf(temp));
                    MtkLog.d(BannerView.TAG, "StateManage setState curState " + this.curState);
                }
            }
        }

        /* access modifiers changed from: private */
        public int getState() {
            MtkLog.v(BannerView.TAG, "StateManage getState " + this.curState);
            return this.stateArray.get(this.curState).intValue();
        }

        /* access modifiers changed from: private */
        public int getNextState() {
            int temp;
            MtkLog.v(BannerView.TAG, "curState " + this.curState);
            MtkLog.v(BannerView.TAG, "stateArray.size() " + this.stateArray.size());
            MtkLog.v(BannerView.TAG, "orietation " + this.orietation);
            if (this.orietation) {
                int temp2 = this.curState + 1;
                MtkLog.v(BannerView.TAG, "orietation " + this.orietation);
                if (temp2 < this.stateArray.size()) {
                    return this.stateArray.get(temp2).intValue();
                }
                this.orietation = false;
                temp = this.curState - 1;
            } else {
                int temp3 = this.curState - 1;
                if (temp3 >= 0) {
                    return this.stateArray.get(temp3).intValue();
                }
                this.orietation = true;
                temp = this.curState + 1;
            }
            return this.stateArray.get(temp).intValue();
        }

        /* access modifiers changed from: private */
        public void setOrietation(boolean flag) {
            this.orietation = flag;
        }
    }

    class SimpleBanner {
        public boolean isAnimation = false;
        public TextView mCCView;
        private TextView mChannelName;
        private final Context mContext;
        /* access modifiers changed from: private */
        public TextView mFirstLine;
        private ImageView mImgDoblyType;
        private ImageView mLockIcon;
        private TextView mReceiverType;
        private View mSecondLine;
        private View mSelfLayout;
        private View mThirdLine;
        /* access modifiers changed from: private */
        public TextView mThirdMiddle;
        private TextView mTime;
        private View mTypeTimeLayout;
        /* access modifiers changed from: private */
        public DelayTextTask timeDelayTextTask;

        public SimpleBanner(Context context) {
            this.mContext = context;
        }

        public SimpleBanner(Context context, AttributeSet attrs) {
            this.mContext = context;
        }

        /* access modifiers changed from: private */
        public void initView() {
            this.mSelfLayout = BannerView.this.findViewById(R.id.banner_simple_layout);
            this.mSelfLayout.setImportantForAccessibility(2);
            this.mSelfLayout.setVisibility(4);
            this.mImgDoblyType = (ImageView) BannerView.this.findViewById(R.id.banner_dobly_type);
            this.mFirstLine = (TextView) BannerView.this.findViewById(R.id.banner_simple_first_line);
            this.mSecondLine = BannerView.this.findViewById(R.id.banner_simple_second_line_layout);
            this.mChannelName = (TextView) BannerView.this.findViewById(R.id.banner_simple_channel_name);
            this.mChannelName.setFocusable(true);
            this.mChannelName.requestFocus();
            this.mLockIcon = (ImageView) BannerView.this.findViewById(R.id.banner_simple_lock_icon);
            this.mThirdLine = BannerView.this.findViewById(R.id.banner_simple_third_line_layout);
            this.mThirdMiddle = (TextView) BannerView.this.findViewById(R.id.banner_simple_third_middle);
            this.mCCView = (TextView) BannerView.this.findViewById(R.id.banner_simple_cc);
            this.mTypeTimeLayout = BannerView.this.findViewById(R.id.banner_simple_type_time_layout);
            this.mReceiverType = (TextView) BannerView.this.findViewById(R.id.banner_simple_receiver_type);
            this.mTime = (TextView) BannerView.this.findViewById(R.id.banner_simple_time);
            String strTime = BannerView.mNavBannerImplement.getCurrentTime();
            MtkLog.d(BannerView.TAG, "SimpleBanner strTime" + strTime);
            this.mTime.setText(strTime);
            this.timeDelayTextTask = new DelayTextTask(this.mTime, BannerView.this.timeTimeChange);
        }

        public int getVisibility() {
            return this.mSelfLayout.getVisibility();
        }

        public int getChannelNameVisibility() {
            return this.mChannelName.getVisibility();
        }

        public String getFirstLineStr() {
            return this.mFirstLine.getText().toString();
        }

        /* access modifiers changed from: private */
        public void show(boolean isBasicShow) {
            this.mImgDoblyType.setVisibility(8);
            boolean doShow = (getVisibility() == 0 && this.mTime.getVisibility() == 0) ? false : true;
            MtkLog.d(BannerView.TAG, "come in show SimpleBar");
            if (BannerView.mNavIntegration.isCurrentSourceTv()) {
                boolean isActiveChannel = CommonIntegration.getInstance().isActiveChannel();
                boolean isFakerChannel = CommonIntegration.getInstance().isFakerChannel(CommonIntegration.getInstance().getCurChInfo());
                MtkLog.d(BannerView.TAG, "isActiveChannel:" + isActiveChannel + " specialType: " + BannerView.this.specialType + ", isFakerChannel:" + isFakerChannel);
                if (BannerView.this.specialType == 5 && (TVContent.getInstance(this.mContext).isMYSCountry() || TVContent.getInstance(this.mContext).isIDNCountry())) {
                    MtkLog.d(BannerView.TAG, "SimpleBar show for MYS.");
                    this.mSelfLayout.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
                    this.mSelfLayout.setVisibility(0);
                    showSimpleChannel();
                    return;
                } else if ((!isActiveChannel || (BannerView.this.specialType == 5 && !TVContent.getInstance(this.mContext).isMYSCountry() && !TVContent.getInstance(this.mContext).isIDNCountry())) && !BannerView.mNavIntegration.is3rdTVSource() && !isFakerChannel) {
                    MtkLog.d(BannerView.TAG, "SimpleBar hidden");
                    hide();
                    return;
                } else {
                    MtkLog.d(BannerView.TAG, "SimpleBar shown");
                }
            }
            if (getVisibility() != 0) {
                if (!BannerView.this.isMenuOptionShow()) {
                    if (isBasicShow) {
                        MtkLog.d(BannerView.TAG, "come in show SimpleBar 2");
                        this.mSelfLayout.setBackgroundResource(R.drawable.translucent_background);
                    } else {
                        MtkLog.d(BannerView.TAG, "come in show SimpleBar 3");
                        this.mSelfLayout.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
                    }
                    this.mSelfLayout.setVisibility(0);
                } else {
                    return;
                }
            }
            if (BannerView.mNavIntegration.is3rdTVSource()) {
                boolean isBlockFor3rd = ((PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG)).isContentBlock(true);
                MtkLog.d(BannerView.TAG, "isBlockFor3rd=" + isBlockFor3rd);
                if (!BannerView.mNavIntegration.isCurrentSourceBlocked() || !isBlockFor3rd) {
                    MtkLog.d(BannerView.TAG, "come in test step1");
                    int channelSize3rd = BannerView.mNavIntegration.getAllChannelListByTIFFor3rdSource();
                    MtkLog.d(BannerView.TAG, "come in test step1 channelSize3rd=" + channelSize3rd);
                    if (channelSize3rd <= 0) {
                        MtkLog.d(BannerView.TAG, "come in test step3");
                        showSimpleTime();
                        return;
                    }
                    MtkLog.d(BannerView.TAG, "come in test step2");
                    showSimpleChannel();
                    showTime(doShow);
                    return;
                }
                showSimpleSource(isBlockFor3rd);
            } else if (!BannerView.mNavIntegration.isCurrentSourceTv()) {
                String curInputName = InputSourceManager.getInstance().getCurrentInputSourceName(BannerView.mNavIntegration.getCurrentFocus());
                MtkLog.d(BannerView.TAG, "curInputName: " + curInputName);
                if (curInputName == null || !curInputName.contains("TV") || BannerView.mNavIntegration.is3rdTVSource() || BannerView.mNavIntegration.isCurrentSourceHDMI()) {
                    showSimpleSource();
                } else {
                    MtkLog.d(BannerView.TAG, "not TV source but get a 'TV' inputname");
                }
            } else if (BannerView.mNavIntegration.isMenuInputTvBlock()) {
                showSimpleSource();
            } else if (TIFChannelManager.getInstance(this.mContext).getCurrChannelInfo() == null) {
                showSimpleTime();
            } else {
                showSimpleChannel();
                showTime(doShow);
            }
        }

        private void showTime(boolean doShow) {
            MtkLog.d(BannerView.TAG, "come in showTime doShow: " + doShow);
            if (doShow) {
                this.mTime.setVisibility(0);
                this.mTime.setText((CharSequence) null);
                this.timeDelayTextTask.start();
            }
        }

        /* access modifiers changed from: private */
        public void updateInputting(String num) {
            if (BannerView.this.bannerLayout.getVisibility() != 0) {
                BannerView.this.bannerLayout.setVisibility(0);
            }
            BannerView.this.hideChannelListDialog();
            BannerView.this.hideFavoriteChannelList();
            BannerView.this.mBasicBanner.hide();
            BannerView.this.mDetailBanner.hide();
            this.mSelfLayout.setVisibility(0);
            MtkLog.d(BannerView.TAG, "come in updateInputting set visible == " + this.mSelfLayout.getVisibility());
            MtkLog.d(BannerView.TAG, "come in updateInputting topBanner visible == " + BannerView.this.topBanner.getVisibility());
            MtkLog.d(BannerView.TAG, "come in updateInputting bannerLayout visible == " + BannerView.this.bannerLayout.getVisibility());
            this.mSelfLayout.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
            this.mFirstLine.setVisibility(0);
            this.mFirstLine.setText(num);
            this.mLockIcon.setVisibility(8);
            this.mChannelName.setVisibility(4);
            this.mTypeTimeLayout.setVisibility(4);
            this.mThirdMiddle.setVisibility(8);
            this.mReceiverType.setVisibility(4);
            this.mTime.setVisibility(4);
        }

        public void hide() {
            MtkLog.d(BannerView.TAG, "come in SimpleBanner hide()");
            if (this.mSelfLayout.getVisibility() == 0) {
                this.mSelfLayout.setVisibility(4);
                BannerView.this.cancelTimeUpdateTask();
            }
        }

        public void setSimpleBannerBg(boolean isShow) {
            if (isShow) {
                this.mSelfLayout.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
            } else {
                this.mSelfLayout.setBackgroundResource(R.drawable.translucent_background);
            }
        }

        /* access modifiers changed from: private */
        public void showCCView() {
            if (MarketRegionInfo.getCurrentMarketRegion() == 3) {
                return;
            }
            if (!BannerView.mNavIntegration.isCurrentSourceTv()) {
                MtkLog.d(BannerView.TAG, "showCCView----> other source");
                String CCString = BannerView.mNavBannerImplement.getBannerCaptionInfo();
                MtkLog.d(BannerView.TAG, "showCCView---->CCString: " + CCString);
                if (!TextUtils.isEmpty(CCString)) {
                    this.mCCView.setVisibility(0);
                    this.mCCView.setText(CCString);
                    MtkLog.d(BannerView.TAG, "showCCView---->mCCView normal...");
                    return;
                }
                MtkLog.d(BannerView.TAG, "showCCView---->mCCView gone1111...");
                this.mCCView.setVisibility(8);
                return;
            }
            MtkLog.d(BannerView.TAG, "showCCView---->mCCView gone2222...");
            this.mCCView.setVisibility(8);
        }

        private void showSimpleSource() {
            showSimpleSource(BannerView.mNavBannerImplement.isShowInputLockIcon() && !BannerView.this.isShowEmptyVideoInfoFirst);
        }

        /* access modifiers changed from: private */
        public void showDoblyIcon() {
            int doblyType = BannerView.mNavBannerImplement.getDoblyType();
            MtkLog.d(BannerView.TAG, "showDoblyIcon:doblyType=" + doblyType);
            if (doblyType == 1) {
                this.mImgDoblyType.setImageResource(R.drawable.icon_dobly_atoms);
                this.mImgDoblyType.setVisibility(0);
            } else if (doblyType == 2) {
                this.mImgDoblyType.setImageResource(R.drawable.icon_dobly_audio);
                this.mImgDoblyType.setVisibility(0);
            } else {
                this.mImgDoblyType.setImageResource(0);
                this.mImgDoblyType.setVisibility(8);
            }
        }

        private void showSimpleSource(boolean showLock) {
            String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName(BannerView.mNavIntegration.getCurrentFocus());
            MtkLog.d(BannerView.TAG, "getCurrentInputName: " + sourceName);
            String privSName = CommonIntegration.getInstance().getPrivSimBFirstLine();
            this.mImgDoblyType.setVisibility(8);
            if (BannerView.mNavIntegration.isCurrentSourceHDMI()) {
                showDoblyIcon();
            }
            if (!BannerView.mNavIntegration.is3rdTVSource() || showLock) {
                this.mFirstLine.setVisibility(0);
                this.mFirstLine.setText(sourceName);
                this.mChannelName.setVisibility(8);
                this.mTypeTimeLayout.setVisibility(8);
                if (sourceName != null && !sourceName.equals(privSName)) {
                    this.mThirdMiddle.setText("");
                    CommonIntegration.getInstance().setPrivSimBFirstLine(sourceName);
                }
                if (showLock) {
                    MtkLog.d(BannerView.TAG, "VISIBLE~ ");
                    this.mLockIcon.setVisibility(0);
                    this.mThirdMiddle.setVisibility(8);
                } else {
                    MtkLog.d(BannerView.TAG, "INVISIBLE~ ");
                    this.mLockIcon.setVisibility(4);
                    this.mThirdMiddle.setVisibility(0);
                    showCCView();
                }
                BannerView.this.navBannerHandler.removeMessages(BannerView.MSG_SHOW_SOURCE_BANNER_TIMING);
                BannerView.this.navBannerHandler.sendEmptyMessageDelayed(BannerView.MSG_SHOW_SOURCE_BANNER_TIMING, 500);
                return;
            }
            MtkLog.d(BannerView.TAG, "only show source name~");
            this.mLockIcon.setVisibility(4);
            this.mCCView.setVisibility(4);
            this.mThirdMiddle.setVisibility(4);
            this.mChannelName.setText(BannerView.mNavBannerImplement.getCurrentChannelName());
            this.mChannelName.setVisibility(0);
            this.mFirstLine.setVisibility(8);
        }

        /* access modifiers changed from: private */
        public void changeSourceBannerTiming() {
            new Thread(new Runnable() {
                public void run() {
                    BannerView.this.videoFormatSourceTimeChange.refresh(SimpleBanner.this.mThirdMiddle);
                }
            }).start();
        }

        private void showSimpleChannel() {
            MtkLog.d(BannerView.TAG, "showSimpleChannel~ ");
            this.mFirstLine.setVisibility(0);
            this.mFirstLine.setText(BannerView.mNavBannerImplement.getCurrentChannelNum());
            this.mChannelName.setVisibility(0);
            this.mChannelName.setText(BannerView.mNavBannerImplement.getCurrentChannelName());
            if (BannerView.this.specialType != 5 || (!TVContent.getInstance(this.mContext).isMYSCountry() && !TVContent.getInstance(this.mContext).isIDNCountry())) {
                if (CommonIntegration.getInstance().isCurCHAnalog()) {
                    MtkLog.d(BannerView.TAG, "ATV channel no need to show mReceiverType.");
                    this.mReceiverType.setVisibility(4);
                } else {
                    MtkLog.d(BannerView.TAG, "It will show for others.");
                    this.mReceiverType.setVisibility(0);
                    this.mReceiverType.setText(BannerView.mNavBannerImplement.getTVTurnerMode());
                }
                this.mLockIcon.setVisibility(8);
                this.mCCView.setVisibility(8);
                this.mTypeTimeLayout.setVisibility(0);
                this.mThirdMiddle.setVisibility(8);
                return;
            }
            MtkLog.d(BannerView.TAG, "MYS conntry and hidden channel.");
            this.mReceiverType.setVisibility(8);
            this.mLockIcon.setVisibility(8);
            this.mCCView.setVisibility(8);
            this.mTypeTimeLayout.setVisibility(8);
            this.mThirdMiddle.setVisibility(8);
        }

        private void showSimpleTime() {
            this.mFirstLine.setVisibility(4);
            this.mChannelName.setVisibility(4);
            this.mLockIcon.setVisibility(8);
            this.mTypeTimeLayout.setVisibility(0);
            this.mThirdMiddle.setVisibility(8);
            this.mReceiverType.setVisibility(4);
            this.mTime.setVisibility(0);
            this.mTime.setText((CharSequence) null);
            this.timeDelayTextTask.start();
        }
    }

    class BasicBanner {
        public boolean isAnimation = false;
        private String mAudioInfo;
        private TextView mAudioLanguage;
        private LinearLayout mCNBasicBannerLayout;
        /* access modifiers changed from: private */
        public final Context mContext;
        /* access modifiers changed from: private */
        public TextView mCurProgramDuration;
        /* access modifiers changed from: private */
        public TextView mCurProgramName;
        /* access modifiers changed from: private */
        public String mCurrentChannelLogoPath;
        /* access modifiers changed from: private */
        public TextView mCurrentProgramType;
        private int mDoblyType;
        private LinearLayout mEUBasicBannerLayout;
        private TextView mEyeOrEarIcon;
        private TextView mFavoriteIcon;
        private TextView mGingaTVIcon;
        private View mIconsLayout;
        private TextView mLockIcon;
        /* access modifiers changed from: private */
        public String mNextProgramCategory;
        /* access modifiers changed from: private */
        public TextView mNextProgramDuration;
        private View mNextProgramLayout;
        /* access modifiers changed from: private */
        public TextView mNextProgramName;
        /* access modifiers changed from: private */
        public String mNextProgramRating;
        /* access modifiers changed from: private */
        public String mNextProgramTime;
        /* access modifiers changed from: private */
        public String mNextProgramTitle;
        /* access modifiers changed from: private */
        public String mProgramCategory;
        /* access modifiers changed from: private */
        public String mProgramRating;
        /* access modifiers changed from: private */
        public String mProgramTime;
        /* access modifiers changed from: private */
        public String mProgramTitle;
        /* access modifiers changed from: private */
        public TextView mRateTextView;
        private LinearLayout mSABasicBannerLayout;
        private View mSecondLineLayout;
        /* access modifiers changed from: private */
        public View mSelfLayout;
        /* access modifiers changed from: private */
        public TextView mSpecialInfo;
        /* access modifiers changed from: private */
        public TextView mSubtitleOrCCIcon;
        private TextView mTTXIcon;
        private View mThirdLineLayout;
        private LinearLayout mUSBasicBannerLayout;
        /* access modifiers changed from: private */
        public TextView mVideoFormat;
        /* access modifiers changed from: private */
        public String mVideoInfo;
        String[] mtsAudioMode;
        /* access modifiers changed from: private */
        public DelayTextTask vfDelayTextTask;

        public BasicBanner(Context context) {
            this.mContext = context;
            this.mtsAudioMode = this.mContext.getResources().getStringArray(R.array.nav_mts_strings);
        }

        public BasicBanner(Context context, AttributeSet attrs) {
            this.mContext = context;
            this.mtsAudioMode = this.mContext.getResources().getStringArray(R.array.nav_mts_strings);
        }

        /* access modifiers changed from: private */
        public void initView() {
            this.mSelfLayout = BannerView.this.findViewById(R.id.banner_basic_layout);
            this.mCNBasicBannerLayout = (LinearLayout) BannerView.this.findViewById(R.id.banner_basic_layout_cn);
            this.mUSBasicBannerLayout = (LinearLayout) BannerView.this.findViewById(R.id.banner_basic_layout_us);
            this.mSABasicBannerLayout = (LinearLayout) BannerView.this.findViewById(R.id.banner_basic_layout_sa);
            this.mEUBasicBannerLayout = (LinearLayout) BannerView.this.findViewById(R.id.banner_basic_layout_eu);
            if (1 == MarketRegionInfo.getCurrentMarketRegion()) {
                this.mCNBasicBannerLayout.setVisibility(8);
                this.mSABasicBannerLayout.setVisibility(8);
                this.mEUBasicBannerLayout.setVisibility(8);
                this.mUSBasicBannerLayout.setVisibility(0);
                this.mCurProgramName = (TextView) BannerView.this.findViewById(R.id.banner_current_program_title_us);
                this.mSpecialInfo = (TextView) BannerView.this.findViewById(R.id.banner_special_info_us);
                this.mCurProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_current_program_duration_us);
                this.mAudioLanguage = (TextView) BannerView.this.findViewById(R.id.banner_audio_language_us);
                this.mEyeOrEarIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_eys_ear_icon_us);
                this.mLockIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_lock_icon_us);
                this.mFavoriteIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_favorite_icon_us);
                this.mSubtitleOrCCIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_cc_icon_us);
                this.mVideoFormat = (TextView) BannerView.this.findViewById(R.id.banner_video_format_us);
                this.mSecondLineLayout = BannerView.this.findViewById(R.id.banner_basic_second_line_us);
                this.mRateTextView = (TextView) BannerView.this.findViewById(R.id.banner_program_rating_us);
            } else if (2 == MarketRegionInfo.getCurrentMarketRegion()) {
                this.mCNBasicBannerLayout.setVisibility(8);
                this.mUSBasicBannerLayout.setVisibility(8);
                this.mEUBasicBannerLayout.setVisibility(8);
                this.mSABasicBannerLayout.setVisibility(0);
                this.mCurProgramName = (TextView) BannerView.this.findViewById(R.id.banner_current_program_title_sa);
                this.mSpecialInfo = (TextView) BannerView.this.findViewById(R.id.banner_special_info_sa);
                this.mCurProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_current_program_duration_sa);
                this.mAudioLanguage = (TextView) BannerView.this.findViewById(R.id.banner_audio_language_sa);
                this.mEyeOrEarIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_eys_ear_icon_sa);
                this.mLockIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_lock_icon_sa);
                this.mFavoriteIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_favorite_icon_sa);
                this.mSubtitleOrCCIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_cc_icon_sa);
                this.mVideoFormat = (TextView) BannerView.this.findViewById(R.id.banner_video_format_sa);
                this.mSecondLineLayout = BannerView.this.findViewById(R.id.banner_basic_second_line_sa);
                this.mRateTextView = (TextView) BannerView.this.findViewById(R.id.banner_program_rating_sa);
                this.mNextProgramName = (TextView) BannerView.this.findViewById(R.id.banner_next_program_title_sa);
                this.mNextProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_next_program_duration_sa);
                this.mGingaTVIcon = (TextView) BannerView.this.findViewById(R.id.banner_ginga_tv_icon_sa);
                this.mCurrentProgramType = (TextView) BannerView.this.findViewById(R.id.banner_current_program_type_sa);
            } else if (3 == MarketRegionInfo.getCurrentMarketRegion()) {
                this.mCNBasicBannerLayout.setVisibility(8);
                this.mUSBasicBannerLayout.setVisibility(8);
                this.mSABasicBannerLayout.setVisibility(8);
                this.mEUBasicBannerLayout.setVisibility(0);
                this.mCurProgramName = (TextView) BannerView.this.findViewById(R.id.banner_current_program_title_eu);
                this.mSpecialInfo = (TextView) BannerView.this.findViewById(R.id.banner_special_info_eu);
                this.mEyeOrEarIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_eys_ear_icon_eu);
                this.mCurProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_current_program_duration_eu);
                this.mAudioLanguage = (TextView) BannerView.this.findViewById(R.id.banner_audio_language_eu);
                this.mLockIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_lock_icon_eu);
                this.mFavoriteIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_favorite_icon_eu);
                this.mSubtitleOrCCIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_subtitle_icon_eu);
                this.mVideoFormat = (TextView) BannerView.this.findViewById(R.id.banner_video_format_eu);
                this.mSecondLineLayout = BannerView.this.findViewById(R.id.banner_basic_second_line_eu);
                this.mRateTextView = (TextView) BannerView.this.findViewById(R.id.banner_program_rating_eu);
                this.mNextProgramName = (TextView) BannerView.this.findViewById(R.id.banner_next_program_title_eu);
                this.mNextProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_next_program_duration_eu);
                this.mTTXIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_ttx_icon_eu);
                this.mCurrentProgramType = (TextView) BannerView.this.findViewById(R.id.banner_current_program_type_eu);
            } else if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                this.mUSBasicBannerLayout.setVisibility(8);
                this.mSABasicBannerLayout.setVisibility(8);
                this.mEUBasicBannerLayout.setVisibility(8);
                this.mCNBasicBannerLayout.setVisibility(0);
                this.mCurProgramName = (TextView) BannerView.this.findViewById(R.id.banner_current_program_title);
                this.mSecondLineLayout = BannerView.this.findViewById(R.id.banner_basic_second_line);
                this.mSpecialInfo = (TextView) BannerView.this.findViewById(R.id.banner_special_info);
                this.mSpecialInfo.setPadding((int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.334d), 0, 0, 0);
                this.mCurProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_current_program_duration);
                this.mAudioLanguage = (TextView) BannerView.this.findViewById(R.id.banner_audio_language);
                this.mThirdLineLayout = BannerView.this.findViewById(R.id.banner_basic_third_line);
                this.mNextProgramLayout = BannerView.this.findViewById(R.id.banner_next_program_layout);
                this.mNextProgramName = (TextView) BannerView.this.findViewById(R.id.banner_next_program_name);
                this.mNextProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_next_program_duration);
                this.mIconsLayout = BannerView.this.findViewById(R.id.banner_basic_icons_layout);
                this.mLockIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_lock_icon);
                this.mFavoriteIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_favorite_icon);
                this.mSubtitleOrCCIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_subtitle_icon);
                this.mTTXIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_ttx_icon);
                this.mVideoFormat = (TextView) BannerView.this.findViewById(R.id.banner_video_format);
            } else {
                this.mUSBasicBannerLayout.setVisibility(8);
                this.mSABasicBannerLayout.setVisibility(8);
                this.mEUBasicBannerLayout.setVisibility(8);
                this.mCNBasicBannerLayout.setVisibility(0);
                this.mCurProgramName = (TextView) BannerView.this.findViewById(R.id.banner_current_program_title);
                this.mSecondLineLayout = BannerView.this.findViewById(R.id.banner_basic_second_line);
                this.mSpecialInfo = (TextView) BannerView.this.findViewById(R.id.banner_special_info);
                this.mCurProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_current_program_duration);
                this.mAudioLanguage = (TextView) BannerView.this.findViewById(R.id.banner_audio_language);
                this.mNextProgramLayout = BannerView.this.findViewById(R.id.banner_next_program_layout);
                this.mNextProgramName = (TextView) BannerView.this.findViewById(R.id.banner_next_program_name);
                this.mNextProgramDuration = (TextView) BannerView.this.findViewById(R.id.banner_next_program_duration);
                this.mIconsLayout = BannerView.this.findViewById(R.id.banner_basic_icons_layout);
                this.mLockIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_lock_icon);
                this.mFavoriteIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_favorite_icon);
                this.mSubtitleOrCCIcon = (TextView) BannerView.this.findViewById(R.id.banner_channel_subtitle_icon);
                this.mVideoFormat = (TextView) BannerView.this.findViewById(R.id.banner_video_format);
            }
            this.mSelfLayout.setVisibility(4);
            this.mSpecialInfo.setPadding((int) (0.334d * ((double) ScreenConstant.SCREEN_WIDTH)), 0, 0, 0);
            this.vfDelayTextTask = new DelayTextTask(this.mVideoFormat, BannerView.this.videoFormatTimeChange);
        }

        /* access modifiers changed from: private */
        public int getVisibility() {
            return this.mSelfLayout.getVisibility();
        }

        private boolean isAllowSpecialInfoShow() {
            if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                MtkLog.d(BannerView.TAG, "showSpecialBar dvr is running!");
                return false;
            } else if (!BannerView.this.isMenuOptionShow()) {
                return true;
            } else {
                MtkLog.d(BannerView.TAG, "MenuOption is showing!");
                return false;
            }
        }

        /* access modifiers changed from: private */
        public void showSpecialInfo(int state) {
            if (isAllowSpecialInfoShow()) {
                String text = "";
                if (BannerView.mNavIntegration.isCurrentSourceBlocked() && !BannerView.mNavIntegration.isCurrentSourceTv() && this.mSelfLayout.getVisibility() == 0) {
                    this.mSelfLayout.setVisibility(4);
                    MtkLog.d(BannerView.TAG, "showSpecialInfo Basic hide");
                    BannerView.this.topBanner.setBackgroundResource(R.drawable.translucent_background);
                } else if (state != 1 || !TIFChannelManager.getInstance(this.mContext).isCiVirtualChannel()) {
                    if (this.mSelfLayout.getVisibility() != 0) {
                        this.mSelfLayout.setVisibility(0);
                        BannerView.this.mSimpleBanner.setSimpleBannerBg(false);
                        MtkLog.v(BannerView.TAG, "come in showSpecialInfo else");
                        BannerView.this.topBanner.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
                    }
                    setSpecialVisibility(true);
                    switch (state) {
                        case 1:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(0);
                            }
                            setIconsVisibility();
                            if ((1 == MarketRegionInfo.getCurrentMarketRegion() || 2 == MarketRegionInfo.getCurrentMarketRegion()) && !TIFChannelManager.getInstance(this.mContext).hasActiveChannel()) {
                                BannerView.this.mSimpleBanner.mFirstLine.setVisibility(4);
                            }
                            text = this.mContext.getString(R.string.nav_no_signal);
                            break;
                        case 2:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(0);
                            }
                            BannerView.this.hideFavoriteChannelList();
                            setIconsVisibility();
                            text = this.mContext.getString(R.string.nav_channel_has_locked);
                            break;
                        case 3:
                            setIconsVisibility();
                            BannerView.this.hideFavoriteChannelList();
                            text = this.mContext.getString(R.string.nav_program_has_locked);
                            break;
                        case 4:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(4);
                            } else {
                                setIconsVisibility();
                            }
                            BannerView.this.mBasicBanner.mSubtitleOrCCIcon.setVisibility(4);
                            text = this.mContext.getString(R.string.nav_input_has_locked);
                            break;
                        case 16:
                        case 17:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(4);
                            } else {
                                setIconsVisibility();
                            }
                            BannerView.this.mBasicBanner.mSubtitleOrCCIcon.setVisibility(4);
                            text = this.mContext.getString(R.string.nav_please_scan_channels);
                            BannerView.this.mSimpleBanner.mFirstLine.setVisibility(4);
                            break;
                        case 19:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(4);
                            } else {
                                setIconsVisibility();
                            }
                            text = this.mContext.getString(R.string.nav_channel_retrieving);
                            break;
                        case 20:
                            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                                this.mIconsLayout.setVisibility(0);
                            }
                            setIconsVisibility();
                            text = this.mContext.getString(R.string.nav_no_support);
                            break;
                    }
                    if (!(MarketRegionInfo.getCurrentMarketRegion() == 0 || this.mRateTextView.getVisibility() == 4)) {
                        this.mRateTextView.setVisibility(4);
                    }
                    MtkLog.d(BannerView.TAG, "setNextEvent region=" + MarketRegionInfo.getCurrentMarketRegion());
                    if (3 == MarketRegionInfo.getCurrentMarketRegion() || 2 == MarketRegionInfo.getCurrentMarketRegion()) {
                        MtkLog.v(BannerView.TAG, "showSpecialInfo ProgramCategory:" + BannerView.mNavBannerImplement.getProgramCategory());
                        if ((BannerView.this.specialType < 1 || BannerView.this.specialType > 5) && (BannerView.this.specialType < 16 || BannerView.this.specialType > 20)) {
                            this.mNextProgramName.setText(BannerView.mNavBannerImplement.getNextProgramTitle());
                            this.mNextProgramDuration.setText(BannerView.mNavBannerImplement.getNextProgramTime());
                            this.mCurrentProgramType.setText(BannerView.mNavBannerImplement.getProgramCategory());
                        } else {
                            this.mCurrentProgramType.setText("");
                            this.mNextProgramName.setText("");
                            this.mNextProgramDuration.setText("");
                        }
                    }
                    MtkLog.v(BannerView.TAG, "showSpecialInfo state:" + state + ",visibility:" + this.mSpecialInfo.getVisibility() + ",text:" + text + " specialType : " + BannerView.this.specialType);
                    this.mSpecialInfo.setText(text);
                    StringBuilder sb = new StringBuilder();
                    sb.append(" mSpecialInfo.setText(): ");
                    sb.append(text);
                    MtkLog.d(BannerView.TAG, sb.toString());
                } else {
                    MtkLog.d(BannerView.TAG, "the current channel is CI virtual channel!!!");
                    BannerView.this.mSimpleBanner.setSimpleBannerBg(true);
                }
            }
        }

        /* access modifiers changed from: private */
        public void show() {
            MtkLog.v(BannerView.TAG, "come in BaiscBar show()");
            getBannerInfoWithThead();
        }

        /* access modifiers changed from: private */
        public void resetBasicData() {
            this.mProgramTime = null;
            this.mProgramTitle = null;
            this.mCurrentChannelLogoPath = null;
            this.mProgramCategory = null;
            this.mNextProgramTitle = null;
            this.mNextProgramTime = null;
            this.mProgramRating = null;
            this.mVideoInfo = null;
        }

        private void getBannerInfoWithThead() {
            new Thread(new Runnable() {
                public void run() {
                    String str;
                    MtkTvChannelInfoBase currentChannelInfo;
                    BasicBanner.this.resetBasicData();
                    String unused = BasicBanner.this.mProgramTime = BannerView.mNavBannerImplement.getProgramTime();
                    String unused2 = BasicBanner.this.mProgramTitle = BannerView.mNavBannerImplement.getProgramTitle();
                    MtkLog.d(BannerView.TAG, "mProgramTime=" + BasicBanner.this.mProgramTime + ",mProgramTitle=" + BasicBanner.this.mProgramTitle);
                    if (CommonIntegration.isSARegion() && (currentChannelInfo = BannerView.mNavIntegration.getChannelById(BannerView.mNavIntegration.getCurrentChannelId())) != null && (currentChannelInfo instanceof MtkTvISDBChannelInfo)) {
                        String unused3 = BasicBanner.this.mCurrentChannelLogoPath = BannerView.mNavIntegration.getISDBChannelLogo((MtkTvISDBChannelInfo) currentChannelInfo);
                        MtkLog.d(BannerView.TAG, "come in basic banner show channel logo, mCurrentChannelLogoPath = " + BasicBanner.this.mCurrentChannelLogoPath);
                    }
                    MtkLog.d(BannerView.TAG, "current region=" + MarketRegionInfo.getCurrentMarketRegion());
                    if (CommonIntegration.isEURegion() || CommonIntegration.isSARegion()) {
                        String unused4 = BasicBanner.this.mProgramCategory = BannerView.mNavBannerImplement.getProgramCategory();
                        String unused5 = BasicBanner.this.mNextProgramCategory = BannerView.mNavBannerImplement.getNextProgramCategory();
                        MtkLog.d(BannerView.TAG, "mProgramCategory=" + BasicBanner.this.mProgramCategory + ", mNextProgramCategory=" + BasicBanner.this.mNextProgramCategory);
                    }
                    BasicBanner.this.getAudioLanguage();
                    if (!CommonIntegration.isUSRegion()) {
                        String unused6 = BasicBanner.this.mNextProgramTitle = BannerView.mNavBannerImplement.getNextProgramTitle();
                        String unused7 = BasicBanner.this.mNextProgramTime = BannerView.mNavBannerImplement.getNextProgramTime();
                        MtkLog.d(BannerView.TAG, "mNextProgramTitle=" + BasicBanner.this.mNextProgramTitle + ",mNextProgramTime=" + BasicBanner.this.mNextProgramTime);
                    }
                    if (!CommonIntegration.isCNRegion() && !BannerView.this.isSpecialState) {
                        String unused8 = BasicBanner.this.mProgramRating = BannerView.mNavBannerImplement.getProgramRating();
                        String unused9 = BasicBanner.this.mNextProgramRating = BannerView.mNavBannerImplement.getNextProgramRating();
                        MtkLog.d(BannerView.TAG, "mProgramRating=" + BasicBanner.this.mProgramRating + ", mNextProgramRating=" + BasicBanner.this.mNextProgramRating);
                    }
                    if (!BannerView.this.mIs3rdTVSource && BannerView.this.videoScramebled) {
                        String unused10 = BasicBanner.this.mVideoInfo = BasicBanner.this.mContext.getString(R.string.nav_channel_video_scrambled);
                    } else if (BannerView.this.mIs3rdTVSource || !BannerView.this.noVideo) {
                        BasicBanner basicBanner = BasicBanner.this;
                        if (BannerView.this.isShowEmptyVideoInfoFirst) {
                            str = "";
                        } else {
                            str = BannerView.mNavBannerImplement.getCurrentVideoInfo();
                        }
                        String unused11 = basicBanner.mVideoInfo = str;
                        MtkLog.d(BannerView.TAG, "mVideoInfo=" + BasicBanner.this.mVideoInfo);
                    } else {
                        String unused12 = BasicBanner.this.mVideoInfo = BasicBanner.this.mContext.getString(R.string.nav_resolution_null);
                    }
                    BannerView.this.bannerLayout.post(new Runnable() {
                        public void run() {
                            BasicBanner.this.mCurProgramName.setText(BasicBanner.this.mProgramTitle);
                            BasicBanner.this.showChannelLogo(BasicBanner.this.mCurrentChannelLogoPath);
                            BasicBanner.this.mCurProgramDuration.setText(BasicBanner.this.mProgramTime);
                            BasicBanner.this.changeAudioUI();
                            if (!CommonIntegration.isUSRegion()) {
                                BasicBanner.this.mNextProgramName.setText(BasicBanner.this.mNextProgramTitle);
                                BasicBanner.this.mNextProgramDuration.setText(BasicBanner.this.mNextProgramTime);
                            }
                            setNextEvent();
                            BasicBanner.this.setIconsVisibility();
                            BasicBanner.this.mVideoFormat.setText(BasicBanner.this.mVideoInfo);
                            BasicBanner.this.vfDelayTextTask.start();
                            if (!BannerView.this.isMenuOptionShow() && BasicBanner.this.mSelfLayout.getVisibility() != 0) {
                                MtkLog.v(BannerView.TAG, "come in BaiscBar show()~~");
                                BasicBanner.this.mSelfLayout.setVisibility(0);
                                BannerView.this.mSimpleBanner.setSimpleBannerBg(false);
                                BannerView.this.topBanner.setBackgroundResource(R.drawable.nav_infobar_basic_bg);
                            }
                            BasicBanner.this.setSpecialVisibility(false);
                        }

                        private void setNextEvent() {
                            int colorYellow = BannerView.this.getResources().getColor(R.color.dark_yellow);
                            int colorWhite = BannerView.this.getResources().getColor(R.color.white);
                            BasicBanner.this.mCurProgramName.setTextColor(!BannerView.this.mNextEvent ? colorYellow : colorWhite);
                            BasicBanner.this.mCurProgramDuration.setTextColor(!BannerView.this.mNextEvent ? colorYellow : colorWhite);
                            if (!CommonIntegration.isUSRegion()) {
                                BasicBanner.this.mNextProgramName.setTextColor(BannerView.this.mNextEvent ? colorYellow : colorWhite);
                                BasicBanner.this.mNextProgramDuration.setTextColor(BannerView.this.mNextEvent ? colorYellow : colorWhite);
                            }
                            MtkLog.d(BannerView.TAG, "setNextEvent region=" + MarketRegionInfo.getCurrentMarketRegion());
                            if (CommonIntegration.isEURegion() || CommonIntegration.isSARegion()) {
                                BasicBanner.this.mCurrentProgramType.setText(!BannerView.this.mNextEvent ? BasicBanner.this.mProgramCategory : BasicBanner.this.mNextProgramCategory);
                            }
                            if (CommonIntegration.isCNRegion() || BannerView.this.isSpecialState) {
                                BasicBanner.this.mRateTextView.setVisibility(4);
                                return;
                            }
                            BasicBanner.this.mRateTextView.setVisibility(0);
                            BasicBanner.this.mRateTextView.setText(!BannerView.this.mNextEvent ? BasicBanner.this.mProgramRating : BasicBanner.this.mNextProgramRating);
                        }
                    });
                }
            }).start();
        }

        /* access modifiers changed from: private */
        public void getAudioLanguage() {
            String str;
            boolean unused = BannerView.this.mIs3rdTVSource = BannerView.mNavIntegration.is3rdTVSource();
            MtkLog.d(BannerView.TAG, "mIs3rdTVSource=" + BannerView.this.mIs3rdTVSource);
            if (BannerView.this.mIs3rdTVSource) {
                str = BannerView.mNavBannerImplement.getBannerAudioInfoFor3rd();
            } else {
                str = BannerView.mNavBannerImplement.getBannerAudioInfo(BannerView.this.audioScramebled);
            }
            this.mAudioInfo = str;
            this.mDoblyType = BannerView.mNavBannerImplement.getDoblyType();
            MtkLog.d(BannerView.TAG, "mAudioInfo=" + this.mAudioInfo);
        }

        /* access modifiers changed from: private */
        public void updateAudioLanguage() {
            new Thread(new Runnable() {
                public void run() {
                    BasicBanner.this.getAudioLanguage();
                    BannerView.this.bannerLayout.post(new Runnable() {
                        public void run() {
                            BasicBanner.this.changeAudioUI();
                        }
                    });
                }
            }).start();
        }

        /* access modifiers changed from: private */
        public void changeAudioUI() {
            if (this.mSecondLineLayout.getVisibility() != 0) {
                return;
            }
            if (BannerView.this.mIs3rdTVSource) {
                this.mAudioLanguage.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.mAudioLanguage.setText(this.mAudioInfo);
                return;
            }
            this.mAudioLanguage.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            if (BannerView.this.audioScramebled) {
                if (!TextUtils.isEmpty(this.mAudioInfo)) {
                    this.mAudioLanguage.setText(this.mAudioInfo);
                } else {
                    this.mAudioLanguage.setText(R.string.nav_channel_audio_scrambled);
                }
                MtkLog.d(BannerView.TAG, "dismiss dolby icon,audioScramebled is true");
                this.mAudioLanguage.setText(R.string.nav_channel_audio_scrambled);
            } else if (BannerView.this.noAudio) {
                MtkLog.d(BannerView.TAG, "dismiss dolby icon,noAudio is true");
                StateNormal.setisAudio(true);
                this.mAudioLanguage.setText(R.string.nav_channel_no_audio);
            } else {
                StateNormal.setisAudio(false);
                String audioLanguage = this.mAudioLanguage.getText().toString();
                MtkLog.d(BannerView.TAG, "audioLanguage=" + audioLanguage);
                this.mAudioLanguage.setText(this.mAudioInfo);
                Drawable drawable = null;
                if (this.mDoblyType == 1) {
                    drawable = BannerView.this.getResources().getDrawable(R.drawable.icon_dobly_atoms);
                    drawable.setBounds(0, 0, BannerView.this.getResources().getDimensionPixelOffset(R.dimen.nav_banner_dobly_width), BannerView.this.getResources().getDimensionPixelOffset(R.dimen.nav_banner_dobly_height));
                } else if (this.mDoblyType == 2) {
                    drawable = BannerView.this.getResources().getDrawable(R.drawable.icon_dobly_audio);
                    drawable.setBounds(0, 0, BannerView.this.getResources().getDimensionPixelOffset(R.dimen.nav_banner_dobly_width), BannerView.this.getResources().getDimensionPixelOffset(R.dimen.nav_banner_dobly_height));
                }
                this.mAudioLanguage.setCompoundDrawables(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
                this.mAudioLanguage.setCompoundDrawablePadding(5);
            }
        }

        /* access modifiers changed from: private */
        public void hide() {
            MtkLog.v(BannerView.TAG, "come in BasicBar hide()");
            if (this.mSelfLayout.getVisibility() == 0) {
                MtkLog.v(BannerView.TAG, "come in BasicBar hide() 2");
                BannerView.this.mSimpleBanner.setSimpleBannerBg(true);
                this.mSelfLayout.setVisibility(4);
                BannerView.this.cancelVideoFormatUpdateTask();
                MtkLog.v(BannerView.TAG, "come in BasicBar hide() 2 end");
            }
            BannerView.this.topBanner.setBackgroundResource(R.drawable.translucent_background);
        }

        /* access modifiers changed from: private */
        public void setSpecialVisibility(boolean flag) {
            if (flag) {
                this.mCurProgramName.setVisibility(4);
                this.mSecondLineLayout.setVisibility(8);
                if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                    this.mNextProgramLayout.setVisibility(4);
                } else {
                    this.mRateTextView.setVisibility(4);
                }
                this.mSpecialInfo.setVisibility(0);
                this.mVideoFormat.setVisibility(4);
                return;
            }
            this.mCurProgramName.setVisibility(0);
            this.mSecondLineLayout.setVisibility(0);
            if (MarketRegionInfo.getCurrentMarketRegion() == 0) {
                this.mNextProgramLayout.setVisibility(0);
                this.mIconsLayout.setVisibility(0);
            }
            this.mSpecialInfo.setVisibility(8);
            this.mVideoFormat.setVisibility(0);
        }

        private boolean isShowTVLockIcon() {
            MtkLog.d(BannerView.TAG, "isShowTVLockIcon---->specialType=" + BannerView.this.specialType);
            boolean isLocked = BannerView.this.specialType == 2 || BannerView.this.specialType == 4 || BannerView.this.specialType == 3;
            if (!isLocked) {
                return false;
            }
            int showFlagtemp = MtkTvPWDDialog.getInstance().PWDShow();
            MtkLog.v(BannerView.TAG, "isShowTVLockIcon---->showFlagtemp=" + showFlagtemp);
            if (!isLocked || showFlagtemp != 0) {
                return false;
            }
            return true;
        }

        private void setLockVisibility() {
            MtkLog.d(BannerView.TAG, "setLockVisibility");
            if (!isShowTVLockIcon() || BannerView.mNavIntegration.is3rdTVSource()) {
                this.mLockIcon.setVisibility(4);
                MtkLog.d(BannerView.TAG, "setLockVisibility INVISIBLE");
                return;
            }
            this.mLockIcon.setVisibility(0);
            MtkLog.d(BannerView.TAG, "setLockVisibility VISIBLE");
        }

        /* access modifiers changed from: private */
        public void setFavoriteVisibility() {
            boolean isNeedShowFavicon = false;
            if (BannerView.mNavIntegration.isCurrentSourceTv() && !BannerView.mNavIntegration.isCurrentSourceBlocked()) {
                isNeedShowFavicon = true;
            }
            MtkLog.d(BannerView.TAG, "setFavoriteVisibility isNeedShowFavicon==" + isNeedShowFavicon);
            MtkLog.d(BannerView.TAG, "setFavoriteVisibility isFavChannel==" + FavChannelManager.getInstance(this.mContext).isFavChannel());
            if (BannerView.mNavIntegration.is3rdTVSource() || !isNeedShowFavicon || !FavChannelManager.getInstance(this.mContext).isFavChannel()) {
                this.mFavoriteIcon.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            } else {
                this.mFavoriteIcon.setCompoundDrawablesWithIntrinsicBounds(BannerView.this.getResources().getDrawable(R.drawable.nav_infobar_favorite), (Drawable) null, (Drawable) null, (Drawable) null);
            }
        }

        /* access modifiers changed from: private */
        public void setCaptionVisibility() {
            int textPadingLeft;
            String CCString = BannerView.mNavBannerImplement.getBannerCaptionInfo();
            boolean showCCIcon = BannerView.mNavBannerImplement.isShowCaptionIcon();
            MtkLog.d(BannerView.TAG, "setCaptionVisibility showCC =" + showCCIcon + " getCC = " + CCString);
            BannerView.this.mSimpleBanner.mCCView.setVisibility(8);
            this.mSubtitleOrCCIcon.setVisibility(4);
            if (showCCIcon && ((TextUtils.isEmpty(CCString) && !BannerView.mNavIntegration.is3rdTVSource()) || !TextUtils.isEmpty(CCString))) {
                MtkLog.d(BannerView.TAG, "setCaptionVisibility~ CCString 1");
                if (2 == MarketRegionInfo.getCurrentMarketRegion() || 1 == MarketRegionInfo.getCurrentMarketRegion()) {
                    this.mSubtitleOrCCIcon.setCompoundDrawablesWithIntrinsicBounds(BannerView.this.getResources().getDrawable(R.drawable.nav_cc_icon), (Drawable) null, (Drawable) null, (Drawable) null);
                } else {
                    this.mSubtitleOrCCIcon.setCompoundDrawablesWithIntrinsicBounds(BannerView.this.getResources().getDrawable(R.drawable.nav_banner_icon_sttl), (Drawable) null, (Drawable) null, (Drawable) null);
                }
                this.mSubtitleOrCCIcon.setPadding(0, 0, 0, 0);
                this.mSubtitleOrCCIcon.setVisibility(0);
                this.mSubtitleOrCCIcon.setText(CCString);
            } else if (!showCCIcon && !TextUtils.isEmpty(CCString) && (BannerView.mNavIntegration.isCurrentSourceTv() || BannerView.mNavIntegration.is3rdTVSource())) {
                MtkLog.d(BannerView.TAG, "setCaptionVisibility~ CCString 2");
                MtkLog.d(BannerView.TAG, "setCaptionVisibility showCC 2");
                this.mSubtitleOrCCIcon.setCompoundDrawables((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                if (2 == MarketRegionInfo.getCurrentMarketRegion() || 1 == MarketRegionInfo.getCurrentMarketRegion()) {
                    textPadingLeft = BannerView.this.getResources().getDrawable(R.drawable.nav_cc_icon).getIntrinsicWidth() + this.mSubtitleOrCCIcon.getCompoundDrawablePadding();
                } else {
                    textPadingLeft = this.mSubtitleOrCCIcon.getCompoundDrawablePadding() + BannerView.this.getResources().getDrawable(R.drawable.nav_banner_icon_sttl).getIntrinsicWidth();
                }
                this.mSubtitleOrCCIcon.setPadding(textPadingLeft, 0, 0, 0);
                this.mSubtitleOrCCIcon.setVisibility(0);
                this.mSubtitleOrCCIcon.setText(CCString);
            } else if (BannerView.mNavIntegration.isCurrentSourceTv()) {
                MtkLog.d(BannerView.TAG, "setCaptionVisibility showCC 3");
                this.mSubtitleOrCCIcon.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.mSubtitleOrCCIcon.setText(CCString);
            } else if (!BannerView.mNavIntegration.isCurrentSourceTv()) {
                MtkLog.d(BannerView.TAG, "setCaptionVisibility showCC 4");
                BannerView.this.mSimpleBanner.showCCView();
            }
            MtkLog.d(BannerView.TAG, "cc setCaptionVisibility~ CCString =" + CCString);
            if (BannerView.this.ttsUtil != null && BannerView.this.cckeysend && !TextUtils.isEmpty(CCString)) {
                boolean unused = BannerView.this.cckeysend = false;
                BannerView.this.sendTTSSpeakMsg(CCString);
            }
        }

        private void setTtxIconVisibility() {
            if (3 != MarketRegionInfo.getCurrentMarketRegion()) {
                return;
            }
            if (BannerView.mNavBannerImplement.isShowTtxIcon()) {
                this.mTTXIcon.setVisibility(0);
            } else {
                this.mTTXIcon.setVisibility(4);
            }
        }

        private void setGingaTVIconVisibility() {
            if (2 != MarketRegionInfo.getCurrentMarketRegion()) {
                return;
            }
            if (BannerView.mNavBannerImplement.isShowGingaIcon()) {
                this.mGingaTVIcon.setVisibility(0);
            } else {
                this.mGingaTVIcon.setVisibility(4);
            }
        }

        /* access modifiers changed from: private */
        public void setEyeOrEarHinderIconVisibility() {
            if (2 != MarketRegionInfo.getCurrentMarketRegion() && 3 != MarketRegionInfo.getCurrentMarketRegion() && 1 != MarketRegionInfo.getCurrentMarketRegion()) {
                return;
            }
            if (BannerView.mNavBannerImplement.isShowADEIcon()) {
                this.mEyeOrEarIcon.setCompoundDrawablesWithIntrinsicBounds(BannerView.this.getResources().getDrawable(R.drawable.nav_banner_eye_icon), (Drawable) null, (Drawable) null, (Drawable) null);
                this.mEyeOrEarIcon.setVisibility(0);
            } else if (BannerView.mNavBannerImplement.isShowEARIcon()) {
                this.mEyeOrEarIcon.setCompoundDrawablesWithIntrinsicBounds(BannerView.this.getResources().getDrawable(R.drawable.nav_banner_ear_icon), (Drawable) null, (Drawable) null, (Drawable) null);
                this.mEyeOrEarIcon.setVisibility(0);
            } else {
                this.mEyeOrEarIcon.setVisibility(4);
            }
        }

        /* access modifiers changed from: private */
        public void showChannelLogo(String logoPath) {
            if (logoPath != null) {
                MtkLog.d(BannerView.TAG, "null != logoPath , come in showChannelLogo");
                this.mCurProgramName.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, Drawable.createFromPath(logoPath), (Drawable) null);
                return;
            }
            this.mCurProgramName.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        }

        /* access modifiers changed from: private */
        public void setIconsVisibility() {
            setLockVisibility();
            setFavoriteVisibility();
            setCaptionVisibility();
            setTtxIconVisibility();
            setGingaTVIconVisibility();
            setEyeOrEarHinderIconVisibility();
        }
    }

    class DetailBanner {
        private final DetailTextReader.TextReaderPageChangeListener detailPageChangeListener = new DetailTextReader.TextReaderPageChangeListener() {
            public void onPageChanged(int page) {
                MtkLog.d(BannerView.TAG, "page =" + page);
                if (DetailBanner.this.detailTextReader.getTotalPage() <= 1) {
                    DetailBanner.this.mDetailIconLayout.setVisibility(4);
                    return;
                }
                DetailBanner.this.mDetailIconLayout.setVisibility(0);
                if (page <= 1) {
                    DetailBanner.this.mDetailUpArrow.setVisibility(4);
                    DetailBanner.this.mDetailDownArrow.setVisibility(0);
                } else if (page >= DetailBanner.this.detailTextReader.getTotalPage()) {
                    DetailBanner.this.mDetailUpArrow.setVisibility(0);
                    DetailBanner.this.mDetailDownArrow.setVisibility(4);
                } else {
                    DetailBanner.this.mDetailUpArrow.setVisibility(0);
                    DetailBanner.this.mDetailDownArrow.setVisibility(0);
                }
                String tpage = "" + page + "/" + DetailBanner.this.detailTextReader.getTotalPage();
                DetailBanner.this.mPageNum.setText(tpage);
                MtkLog.d(BannerView.TAG, "page =" + tpage);
            }
        };
        /* access modifiers changed from: private */
        public final DetailTextReader detailTextReader;
        public boolean isAnimation;
        private final Context mContext;
        /* access modifiers changed from: private */
        public ImageView mDetailDownArrow;
        /* access modifiers changed from: private */
        public View mDetailIconLayout;
        private TextView mDetailInfo;
        /* access modifiers changed from: private */
        public ImageView mDetailUpArrow;
        /* access modifiers changed from: private */
        public TextView mPageNum;
        private View mSelfLayout;

        public DetailBanner(Context context) {
            this.mContext = context;
            this.detailTextReader = DetailTextReader.getInstance();
            this.detailTextReader.registerPageChangeListener(this.detailPageChangeListener);
            initView();
        }

        public DetailBanner(Context context, AttributeSet attrs) {
            this.mContext = context;
            this.detailTextReader = DetailTextReader.getInstance();
            this.detailTextReader.registerPageChangeListener(this.detailPageChangeListener);
            initView();
        }

        /* access modifiers changed from: private */
        public void initView() {
            this.mSelfLayout = BannerView.this.findViewById(R.id.banner_detail_layout);
            this.mSelfLayout.setLayoutParams(new LinearLayout.LayoutParams((int) (((double) ScreenConstant.SCREEN_WIDTH) * 0.889d), (int) (((double) ScreenConstant.SCREEN_HEIGHT) * 0.15d)));
            this.mSelfLayout.setVisibility(4);
            this.mDetailInfo = (TextView) BannerView.this.findViewById(R.id.banner_detail_info);
            this.mDetailIconLayout = BannerView.this.findViewById(R.id.banner_detail_info_right_layout);
            this.mDetailUpArrow = (ImageView) BannerView.this.findViewById(R.id.banner_detail_info_uparrow);
            this.mDetailDownArrow = (ImageView) BannerView.this.findViewById(R.id.banner_detail_info_downarrow);
            this.mPageNum = (TextView) BannerView.this.findViewById(R.id.banner_detail_info_pagenum);
            this.detailTextReader.setTextView(this.mDetailInfo);
        }

        /* access modifiers changed from: private */
        public void show() {
            if (StateDvrPlayback.getInstance() != null && StateDvrPlayback.getInstance().isRunning()) {
                MtkLog.d(BannerView.TAG, "dvr is running!");
            } else if (BannerView.this.isMenuOptionShow()) {
                MtkLog.d(BannerView.TAG, "mDetailBanner.show()---isMenuOptionShow(): " + BannerView.this.isMenuOptionShow());
            } else if (this.mSelfLayout.getVisibility() != 0 && !this.isAnimation) {
                this.mSelfLayout.setVisibility(0);
                String mEventDetail = !BannerView.this.mNextEvent ? BannerView.mNavBannerImplement.getProgramDetails() : BannerView.mNavBannerImplement.getNextProgramDetails();
                if (mEventDetail == null || mEventDetail.length() == 0) {
                    this.mDetailInfo.setText("");
                } else {
                    this.mDetailInfo.setText(mEventDetail);
                }
                MtkLog.d(BannerView.TAG, "come in detail banner show");
                this.detailTextReader.resetCurPagenum();
                this.detailTextReader.setTextView(this.mDetailInfo);
                MtkLog.d(BannerView.TAG, "come in detailTextReader.setTextView(mDetailInfo)");
                this.mSelfLayout.setVisibility(0);
            }
        }

        /* access modifiers changed from: private */
        public void hide() {
            MtkLog.d(BannerView.TAG, "come in DetailBanner hide");
            if (this.mSelfLayout.getVisibility() == 0) {
                MtkLog.d(BannerView.TAG, "come in DetailBanner hide~~");
                this.mSelfLayout.setVisibility(4);
            }
        }

        /* access modifiers changed from: private */
        public boolean getDetailBannerVisible() {
            if (this.mSelfLayout.getVisibility() == 0) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void pageDown() {
            if (this.mSelfLayout.getVisibility() == 0) {
                this.detailTextReader.pageDown();
            }
        }

        /* access modifiers changed from: private */
        public void pageUp() {
            if (this.mSelfLayout.getVisibility() == 0) {
                this.detailTextReader.pageUp();
            }
        }
    }

    class DelayTextTask {
        ITimeChange iTimeChange;
        Timer mTimer = new Timer();
        TextView tv;

        DelayTextTask(TextView tv2, ITimeChange iTimeChange2) {
            this.tv = tv2;
            this.iTimeChange = iTimeChange2;
        }

        public void start() {
            try {
                MtkLog.d(BannerView.TAG, "come in showTime start().");
                MtkLog.d(BannerView.TAG, "start() mTimer=" + this.mTimer);
                this.mTimer = new Timer();
                this.mTimer.schedule(new TimerTask() {
                    public void run() {
                        MtkLog.d(BannerView.TAG, "come in showTime run().");
                        if (!Thread.currentThread().isInterrupted()) {
                            MtkLog.d(BannerView.TAG, "come in showTime refresh().");
                            DelayTextTask.this.iTimeChange.refresh(DelayTextTask.this.tv);
                        }
                    }
                }, 0, 1000);
            } catch (Exception ex) {
                MtkLog.d(BannerView.TAG, "Exception ex=" + ex.getMessage());
                MtkLog.printStackTrace();
            }
        }

        public void cancel() {
            MtkLog.d(BannerView.TAG, "cancel() mTimer=" + this.mTimer);
            if (this.mTimer != null) {
                this.mTimer.cancel();
                this.mTimer = null;
            }
        }
    }

    public void reset() {
        MtkLog.v(TAG, "Banner reset start.");
        this.isSpecialState = false;
        this.specialType = -1;
        this.audioScramebled = false;
        this.videoScramebled = false;
        this.noAudio = false;
        this.noVideo = false;
    }

    public void show(boolean isForward, int state, boolean manualClose) {
        show(isForward, state, manualClose, false);
    }

    public void show(boolean isForward, int state, boolean manualClose, boolean isHandleKey) {
        boolean z = isForward;
        int i = state;
        boolean z2 = manualClose;
        boolean z3 = isHandleKey;
        MtkLog.d(TAG, "show() isHandleKey=" + z3 + ",isForward:" + z + ",state:" + i + ",state:" + z2 + ",mBasicBanner.isAnimation =" + this.mBasicBanner.isAnimation + ",mSimpleBanner.isAnimation=" + this.mSimpleBanner.isAnimation + ",mDetailBanner.isAnimation =" + this.mDetailBanner.isAnimation + ",DestroyApp.getRunningActivity()=" + DestroyApp.isCurActivityTkuiMainActivity() + ",isHostQuietTuneStatus: " + this.isHostQuietTuneStatus);
        if (!disableShowBanner(z3)) {
            if (this.mBasicBanner.isAnimation || this.mSimpleBanner.isAnimation || this.mDetailBanner.isAnimation || !DestroyApp.isCurActivityTkuiMainActivity()) {
                MtkLog.d(TAG, "come in show(boolean isForward, int state, boolean manualClose) (mBasicBanner.isAnimation || mSimpleBanner.isAnimation");
            } else if (!this.isHostQuietTuneStatus) {
                boolean flag = false;
                if (this.bannerLayout.getVisibility() == 0) {
                    flag = true;
                }
                if (i >= 0) {
                    showByState(i);
                    this.totalState = -1;
                    return;
                }
                if (mNavIntegration.isCurrentSourceTv()) {
                    boolean isActiveChannel = CommonIntegration.getInstance().isActiveChannel();
                    boolean isFakerChannel = CommonIntegration.getInstance().isFakerChannel(CommonIntegration.getInstance().getCurChInfo());
                    MtkLog.d(TAG, "isActiveChannel:" + isActiveChannel + " specialType: " + this.specialType + ", isFakerChannel:" + isFakerChannel);
                    if (this.specialType == 5 && (TVContent.getInstance(this.mContext).isMYSCountry() || TVContent.getInstance(this.mContext).isIDNCountry())) {
                        MtkLog.d(TAG, "ShowSimpleBar show for MYS.");
                        showSimpleBar();
                        return;
                    } else if ((!isActiveChannel || this.specialType == 5) && this.specialType != 16 && !mNavIntegration.is3rdTVSource() && !isFakerChannel) {
                        MtkLog.d(TAG, "getCIHandle().getHostTuneStatus() 1");
                        return;
                    } else {
                        MtkLog.d(TAG, "getCIHandle().getHostTuneStatus() 0");
                    }
                }
                boolean condition1 = mNavIntegration.isCurrentSourceBlocked();
                MtkLog.d(TAG, "isSourceBlocked: " + condition1 + ", specialType: " + this.specialType);
                if (this.specialType == 4 && !condition1) {
                    this.isSpecialState = false;
                    MtkLog.d(TAG, "tvapi should send a message to set isSpecailState = false in time!!");
                }
                boolean is3rdTVSource = mNavIntegration.is3rdTVSource();
                boolean isBlockFor3rd = ((PwdDialog) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_PWD_DLG)).isContentBlock(true);
                MtkLog.d(TAG, "isBlockFor3rd=" + isBlockFor3rd);
                if (!condition1 || !is3rdTVSource || !isBlockFor3rd) {
                    MtkLog.d(TAG, "show(boolean isForward, int state, boolean manualClose) isSpecialState =" + this.isSpecialState);
                    if (this.isSpecialState || mNavIntegration.isCurrentSourceTv() || is3rdTVSource) {
                        if (!this.isSpecialState || mNavIntegration.is3rdTVSource()) {
                            this.totalState = -1;
                            MtkLog.v(TAG, "come in show isForward && flag is " + z + " " + flag);
                            if (z && flag) {
                                int curState = this.mStateManage.getNextState();
                                MtkLog.v(TAG, "come in show next state is " + curState);
                                setBannerState(curState);
                            }
                            int curState2 = this.mStateManage.getState();
                            MtkLog.v(TAG, "get will show state is " + curState2);
                            switch (curState2) {
                                case 0:
                                    showNoneBar(flag);
                                    MtkLog.v(TAG, "come in show showNoneBar(flag) + flag == " + flag);
                                    return;
                                case 1:
                                    showSimpleBar();
                                    MtkLog.v(TAG, "come in show showSimpleBar()");
                                    return;
                                case 2:
                                    showBasicBar();
                                    MtkLog.v(TAG, "come in show showBasicBar()");
                                    return;
                                case 3:
                                    showDetailBar();
                                    MtkLog.v(TAG, "come in show showDetailBar()");
                                    return;
                                default:
                                    return;
                            }
                        } else {
                            MtkLog.d(TAG, "come in show(,,) isSpecialState");
                            if (!flag || !z2) {
                                MtkLog.d(TAG, "come in show(,,) isSpecialState falg = false");
                                if (this.specialType != 16 || !mNavIntegration.hasActiveChannel()) {
                                    showSpecialBar(this.specialType);
                                    if (getVisibility() != 0) {
                                        setVisibility(0);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            MtkLog.d(TAG, "come in show(,,) isSpecialState falg = true");
                            if (getVisibility() != 8) {
                                setVisibility(8);
                            }
                        }
                    } else if (!flag || !z2) {
                        showSimpleBar();
                        this.totalState = -1;
                    } else if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                } else {
                    this.isSpecialState = true;
                    showSimpleBar();
                }
            }
        }
    }

    public void showDetailBar() {
        this.bannerLayout.setVisibility(0);
        if (this.mBasicBanner != null) {
            showBasicBar();
        }
        this.mDetailBanner.show();
        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        startTimeout(5000);
    }

    public void showSpecialBar(int state) {
        this.bannerLayout.setVisibility(0);
        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        boolean condition = !mNavIntegration.isCurrentSourceTv();
        boolean condition1 = mNavIntegration.isCurrentSourceBlocked();
        if (state == 4 || state == 1 || state == 3) {
        }
        if (condition) {
            MtkLog.d(TAG, "come in showSpecialBar :source not tv... ");
            this.mBasicBanner.hide();
            this.mDetailBanner.hide();
            this.mSimpleBanner.show(false);
        } else {
            if (condition1) {
                state = 4;
            }
            if (state == 4) {
                this.mBasicBanner.hide();
                this.mDetailBanner.hide();
                this.mSimpleBanner.show(false);
                MtkLog.d(TAG, "come in showSpecialBar for input block state == " + state + ",condition1==" + condition1);
            } else {
                this.mSimpleBanner.show(true);
                MtkLog.d(TAG, "come in showSpecialBar state == " + state + ",condition1==" + condition1);
                this.mBasicBanner.showSpecialInfo(state);
            }
            this.mDetailBanner.hide();
        }
        startTimeout(5000);
    }

    public void showByState(int state) {
        switch (state) {
            case 0:
                showNoneBar(true);
                return;
            case 1:
                showSimpleBar();
                return;
            case 2:
                showBasicBar();
                return;
            case 3:
                showDetailBar();
                return;
            default:
                return;
        }
    }

    public void showBasicBar() {
        this.bannerLayout.setVisibility(0);
        if (mNavIntegration.isCurrentSourceTv() && mNavIntegration.getChannelCountFromSVL() <= 0) {
        }
        if (mNavIntegration.is3rdTVSource() && mNavIntegration.getAllChannelListByTIFFor3rdSource() <= 0) {
        }
        int showFlagtemp = MtkTvPWDDialog.getInstance().PWDShow();
        MtkLog.v(TAG, "showFlagtemp=" + showFlagtemp);
        if (showFlagtemp == 0) {
            this.mDetailBanner.hide();
            return;
        }
        this.mBasicBanner.show();
        this.mSimpleBanner.show(true);
        this.mDetailBanner.hide();
        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        startTimeout(5000);
    }

    public void showNoneBar(boolean isVisible) {
        if (isVisible) {
            this.mBasicBanner.hide();
            this.mSimpleBanner.hide();
            this.mDetailBanner.hide();
            this.mStateManage.setOrietation(true);
            if (getVisibility() != 4) {
                setVisibility(4);
                return;
            }
            return;
        }
        showSimpleBar();
        setBannerState(1);
    }

    public void showSimpleBanner() {
        if (!disableShowBanner(false)) {
            showSimpleBar();
            ttsCurrentChannel();
        }
    }

    private void ttsCurrentChannel() {
        String channelChangeTtsTexttemp;
        if (mNavBannerImplement == null || this.ttsUtil == null || (!mNavIntegration.isCurrentSourceTv() && !mNavIntegration.is3rdTVSource())) {
            MtkLog.d(TAG, "itv test  mNavBannerImplement------.....==null");
            return;
        }
        if (TextUtils.isEmpty(mNavBannerImplement.getCurrentChannelName())) {
            channelChangeTtsTexttemp = "Channel number is " + mNavBannerImplement.getCurrentChannelNum();
        } else {
            channelChangeTtsTexttemp = "Channel number is " + mNavBannerImplement.getCurrentChannelNum() + ", channel name is " + mNavBannerImplement.getCurrentChannelName();
        }
        MtkLog.d(TAG, "ttsUtil  channelChangeTtsText - " + channelChangeTtsTexttemp);
        if (TextUtils.isEmpty(this.channelChangeTtsText) || !TextUtils.equals(this.channelChangeTtsText, channelChangeTtsTexttemp)) {
            MtkLog.d(TAG, "ttsUtil  start TTS speak!");
            this.channelChangeTtsText = channelChangeTtsTexttemp;
            sendTTSSpeakMsg(this.channelChangeTtsText);
        }
    }

    public void updateRatingInfo() {
        if (this.mBasicBanner.getVisibility() == 0 && !CommonIntegration.isCNRegion() && !this.isSpecialState) {
            new Thread(new Runnable() {
                public void run() {
                    String unused = BannerView.this.mBasicBanner.mProgramRating = BannerView.mNavBannerImplement.getProgramRating();
                    String unused2 = BannerView.this.mBasicBanner.mNextProgramRating = BannerView.mNavBannerImplement.getNextProgramRating();
                    MtkLog.d(BannerView.TAG, "updateRatingInfo---->mProgramRating=" + BannerView.this.mBasicBanner.mProgramRating + ", mNextProgramRating=" + BannerView.this.mBasicBanner.mNextProgramRating);
                    BannerView.this.bannerLayout.post(new Runnable() {
                        public void run() {
                            BannerView.this.mBasicBanner.mRateTextView.setVisibility(0);
                            BannerView.this.mBasicBanner.mRateTextView.setText(!BannerView.this.mNextEvent ? BannerView.this.mBasicBanner.mProgramRating : BannerView.this.mBasicBanner.mNextProgramRating);
                        }
                    });
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void showSimpleBar() {
        this.bannerLayout.setVisibility(0);
        this.mBasicBanner.hide();
        this.mDetailBanner.hide();
        this.mSimpleBanner.show(false);
        ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
        startTimeout(5000);
    }

    private boolean isMHeg5Showing() {
        int getActiveCompId = ComponentsManager.getActiveCompId();
        MtkLog.d(TAG, "getActiveCompId=" + getActiveCompId);
        return getActiveCompId == 33554433;
    }

    private boolean isNatvieCompShowing() {
        int getActiveCompId = ComponentsManager.getActiveCompId();
        MtkLog.d(TAG, "isNatvieCompShowing----->getActiveCompId=" + getActiveCompId);
        return getActiveCompId == 33554438 || getActiveCompId == 33554433;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0075  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean disableShowBanner(boolean r7) {
        /*
            r6 = this;
            java.lang.String r0 = "BannerView"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "isHandleKey="
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = ",mDisableBanner="
            r1.append(r2)
            boolean r2 = r6.mDisableBanner
            r1.append(r2)
            java.lang.String r2 = ",mIs3rdRunning="
            r1.append(r2)
            boolean r2 = r6.mIs3rdRunning
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 0
            if (r7 == 0) goto L_0x003b
            boolean r1 = r6.isMHeg5Showing()
            if (r1 == 0) goto L_0x003b
            java.lang.String r1 = "BannerView"
            java.lang.String r2 = "NativeComp is showing,do not show bannerview!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            return r0
        L_0x003b:
            boolean r1 = r6.mDisableBanner
            r2 = 1
            if (r1 != 0) goto L_0x0094
            boolean r1 = r6.mIs3rdRunning
            if (r1 != 0) goto L_0x0094
            boolean r1 = r6.isNatvieCompShowing()
            if (r1 == 0) goto L_0x004b
            goto L_0x0094
        L_0x004b:
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList r1 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList.getInstance()
            if (r1 == 0) goto L_0x0063
            com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList r1 = com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrFileList.getInstance()
            boolean r1 = r1.isShowing()
            if (r1 == 0) goto L_0x0063
            java.lang.String r0 = "BannerView"
            java.lang.String r1 = "DVR recordlist show,do not show bannerview!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            return r2
        L_0x0063:
            com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager r1 = com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager.getInstance()
            java.util.List r1 = r1.getCurrentActiveComps()
            java.util.Iterator r3 = r1.iterator()
        L_0x006f:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0093
            java.lang.Object r4 = r3.next()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            r5 = 16777221(0x1000005, float:2.35099E-38)
            if (r4 == r5) goto L_0x008b
            r5 = 16777226(0x100000a, float:2.3509915E-38)
            if (r4 != r5) goto L_0x008a
            goto L_0x008b
        L_0x008a:
            goto L_0x006f
        L_0x008b:
            java.lang.String r0 = "BannerView"
            java.lang.String r3 = "Can't show banner when ch-list and fav-list showing."
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r3)
            return r2
        L_0x0093:
            return r0
        L_0x0094:
            java.lang.String r0 = "BannerView"
            java.lang.String r1 = "NativeComp is showing,do not show bannerview!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.nav.view.BannerView.disableShowBanner(boolean):boolean");
    }

    public void setBannerState(int state) {
        this.mStateManage.setState(state);
    }

    public void hideAllBanner() {
        this.mBasicBanner.hide();
        this.mSimpleBanner.hide();
        this.mDetailBanner.hide();
        if (this.bannerLayout.getVisibility() == 0) {
            this.bannerLayout.setVisibility(4);
        }
    }

    public void channelInputChangeShowBanner() {
        postDelayed(new Runnable() {
            public void run() {
                MtkLog.d(BannerView.TAG, "come in channelInputChangeShowBanner mNumputChangeChannel == " + BannerView.this.mNumputChangeChannel + ",interruptShowBannerWithDelay ==" + BannerView.this.interruptShowBannerWithDelay);
                if (BannerView.this.isGingaRunning()) {
                    MtkLog.d(BannerView.TAG, "channelInputChangeShowBanner mtkTvGingaAppInfoBase is running.");
                } else if (!BannerView.this.mNumputChangeChannel && !BannerView.this.interruptShowBannerWithDelay && ComponentsManager.getNativeActiveCompId() != 33554434) {
                    if (BannerView.mNavIntegration.isCurrentSourceTv()) {
                        BannerView.this.show(false, -1, false);
                    } else {
                        BannerView.this.showSimpleBar();
                    }
                }
            }
        }, 700);
    }

    public void pipFocusOnSourceShowBanner() {
        postDelayed(new Runnable() {
            public void run() {
                MtkLog.d(BannerView.TAG, "come in pipFocusOnSourceShowBanner mNumputChangeChannel == " + BannerView.this.mNumputChangeChannel + ",interruptShowBannerWithDelay ==" + BannerView.this.interruptShowBannerWithDelay);
                if (!BannerView.this.mNumputChangeChannel && !BannerView.this.interruptShowBannerWithDelay) {
                    if (BannerView.mNavIntegration.isCurrentSourceTv()) {
                        MtkLog.d(BannerView.TAG, "come in pipFocusOnSourceShowBanner..source TV");
                        BannerView.this.hideAllBanner();
                        BannerView.this.show(false, -1, false);
                        return;
                    }
                    MtkLog.d(BannerView.TAG, "come in pipFocusOnSourceShowBanner..source Other");
                    BannerView.this.mBasicBanner.hide();
                    BannerView.this.mDetailBanner.hide();
                    if (BannerView.this.bannerLayout.getVisibility() == 0) {
                        BannerView.this.bannerLayout.setVisibility(4);
                    }
                    BannerView.this.showSimpleBar();
                }
            }
        }, 600);
    }

    private void inputChannelNum(int keycode) {
        this.mNumputChangeChannel = true;
        MtkLog.d(TAG, "come in inputChannelNum keyCode =" + keycode);
        MtkLog.d(TAG, "come in inputChannelNum 111mSelectedChannelNumString=" + this.mSelectedChannelNumString);
        if (keycode >= 7 && keycode <= 16) {
            MtkLog.d(TAG, "come in inputChannelNum number keyCode11");
            if (this.mSelectedChannelNumString.indexOf(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING) == -1) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.mSelectedChannelNumString);
                sb.append(keycode - 7);
                this.mSelectedChannelNumString = sb.toString();
                if (3 == MarketRegionInfo.getCurrentMarketRegion()) {
                    if (this.mSelectedChannelNumString.length() > 4) {
                        this.mSelectedChannelNumString = this.mSelectedChannelNumString.substring(1, 5);
                    }
                } else if (this.mSelectedChannelNumString.length() > 5) {
                    this.mSelectedChannelNumString = this.mSelectedChannelNumString.substring(1, 6);
                }
            } else if (this.mSelectedChannelNumString.indexOf(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING) != -1) {
                MtkLog.d(TAG, "come in inputChannelNum number mSelectedChannelNumString=" + this.mSelectedChannelNumString);
                this.mSelectedChannelNumString += (keycode + -7);
                String[] channelNum = this.mSelectedChannelNumString.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING);
                if (this.mSelectedChannelNumString.length() <= 9) {
                    this.mSelectedChannelNumString = channelNum[0] + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + Integer.valueOf(channelNum[1]);
                } else {
                    this.mSelectedChannelNumString = channelNum[0] + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + Integer.valueOf(this.mSelectedChannelNumString.substring(channelNum[0].length() + 1 + 1, 10));
                }
            }
        } else if (56 == keycode && this.mSelectedChannelNumString.indexOf(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING) == -1) {
            MtkLog.d(TAG, "come in inputChannelNum number keyCode22");
            this.mSelectedChannelNumString += MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING;
        } else if (56 == keycode && this.mSelectedChannelNumString.indexOf(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING) != -1) {
            MtkLog.d(TAG, "come in inputChannelNum number keyCode33");
            this.mSelectedChannelNumString = this.mSelectedChannelNumString.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING)[0];
        } else if (76 == keycode) {
            MtkLog.d(TAG, "come in inputChannelNum number keyCode44");
            this.mSelectedChannelNumString += ".";
        }
        MtkLog.d(TAG, "come in inputChannelNum number mSelectedChannelNumString+=" + this.mSelectedChannelNumString);
    }

    public void cancelNumChangeChannel() {
        this.mNumputChangeChannel = false;
        hideAllBanner();
        this.navBannerHandler.removeMessages(MSG_NUMBER_KEY);
        this.mSelectedChannelNumString = "";
    }

    public void updateComponentStatus(int statusID, int value) {
        MtkLog.d(TAG, "updateComponentStatus statusID =" + statusID + ">>value=" + value);
        switch (statusID) {
            case 1:
                boolean coexitComp = true;
                Iterator<Integer> it = ComponentsManager.getInstance().getCurrentActiveComps().iterator();
                while (true) {
                    if (it.hasNext()) {
                        Integer ID = it.next();
                        if (ID.intValue() != 16777241 && ID.intValue() != 16777235 && ID.intValue() != 16777219) {
                            coexitComp = false;
                        }
                    }
                }
                boolean isComponentsShow = ComponentsManager.getInstance().isComponentsShow();
                boolean isCurTKMain = DestroyApp.isCurActivityTkuiMainActivity();
                MtkLog.d(TAG, "NAV_COMPONENT_HIDE isComponentsShow =" + isComponentsShow + "isCurTKMain =" + isCurTKMain);
                if (mNavBannerImplement != null && coexitComp && isCurTKMain && value == 16777218) {
                    MtkLog.d(TAG, "ONLYFORCC updateComponentStatus setCCVisiable(true) ");
                    mNavBannerImplement.setCCVisiable(true);
                    return;
                }
                return;
            case 2:
                Iterator<Integer> it2 = ComponentsManager.getInstance().getCurrentActiveComps().iterator();
                while (true) {
                    if (it2.hasNext()) {
                        Integer ID2 = it2.next();
                        if (ID2.intValue() == 16777241 || ID2.intValue() == 16777235 || ID2.intValue() == 16777219) {
                        }
                    }
                }
                int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
                MtkLog.d(TAG, "showFlag: " + showFlag);
                MtkLog.d(TAG, "isSpecialState: " + this.isSpecialState);
                if ((showFlag != 0 || this.isSpecialState) && value == 16777234 && DestroyApp.isCurActivityTkuiMainActivity()) {
                    show(false, -1, false);
                    return;
                }
                return;
            case 4:
                if (mNavBannerImplement != null) {
                    mNavBannerImplement.setCCVisiable(false);
                    return;
                }
                return;
            case 5:
                if (value == 82) {
                    setInterruptShowBanner(true);
                    return;
                } else if (value != 93) {
                    if (value != 171) {
                        if (value == 178) {
                            this.navBannerHandler.removeMessages(MSG_NUMBER_KEY);
                            this.mSelectedChannelNumString = "";
                            postDelayed(new Runnable() {
                                public void run() {
                                    MtkLog.d(BannerView.TAG, "multi click source key,change source");
                                    BannerView.this.showSimpleBar();
                                }
                            }, 800);
                            ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
                            this.audioScramebled = false;
                            this.videoScramebled = false;
                            this.noAudio = false;
                            this.noVideo = false;
                            this.isSpecialState = false;
                            this.specialType = -1;
                            return;
                        } else if (value != 10062) {
                            switch (value) {
                                case 21:
                                case 22:
                                    break;
                                default:
                                    return;
                            }
                        }
                    }
                    pipFocusOnSourceShowBanner();
                    return;
                } else {
                    changeChannelFavoriteMark();
                    return;
                }
            case 10:
                DetailTextReader.getInstance().resetCurPagenum();
                if (value == 0) {
                    if (!DestroyApp.isCurActivityTkuiMainActivity() || mNavIntegration.getChannelAllNumByAPI() <= 0 || EPGEuActivity.mIsEpgChannelChange || CommonIntegration.getInstance().isCHChanging()) {
                        EPGEuActivity.mIsEpgChannelChange = false;
                        return;
                    }
                    MtkLog.d(TAG, "itv test  hide banner------.....");
                    showSimpleBanner();
                    ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
                    if (!CommonIntegration.getInstance().isPipOrPopState()) {
                        this.audioScramebled = false;
                        this.videoScramebled = false;
                        this.noAudio = false;
                        this.noVideo = false;
                    }
                } else if (!DestroyApp.isCurActivityTkuiMainActivity() || mNavIntegration.getChannelAllNumByAPI() <= 0 || EPGEuActivity.mIsEpgChannelChange) {
                    EPGEuActivity.mIsEpgChannelChange = false;
                    return;
                } else {
                    showSimpleBanner();
                    this.mBasicBanner.mSpecialInfo.setVisibility(8);
                    ComponentsManager.getInstance().showNavComponent(NavBasic.NAV_COMP_ID_BANNER);
                }
                MtkLog.d(TAG, "TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode() is " + TurnkeyUiMainActivity.getInstance().isInPictureInPictureMode());
                this.mMetadataBuilder.putString("android.media.metadata.DISPLAY_TITLE", mNavBannerImplement.getTVLauncherInfoForLiveTv());
                this.mMediaSession.setMetadata(this.mMetadataBuilder.build());
                this.mMediaSession.setActive(true);
                return;
            case 12:
                MtkLog.d(TAG, "content allowed:specialType==" + this.specialType + ",isSpecialState==" + this.isSpecialState);
                MtkLog.d(TAG, "Banner_NAV_CONTENT_ALLOWED content allowed");
                if (MtkTvPWDDialog.getInstance().PWDShow() != 0) {
                    if (isGingaRunning()) {
                        MtkLog.d(TAG, "NAV_CONTENT_ALLOWED mtkTvGingaAppInfoBase is running.");
                        return;
                    }
                    if ((this.specialType < 1 || this.specialType > 5) && (this.specialType < 16 || this.specialType > 20)) {
                        this.isSpecialState = false;
                    } else {
                        MtkLog.d(TAG, "content allowed:focus==" + mNavIntegration.getCurrentFocus() + ",isTV==" + mNavIntegration.isCurrentSourceTv());
                        if (mNavIntegration.getCurrentFocus().equalsIgnoreCase("sub") && mNavIntegration.isPipOrPopState() && mNavIntegration.isCurrentSourceTv()) {
                            this.isSpecialState = false;
                        }
                    }
                    MtkLog.d(TAG, "isSpecialState -----------  = " + this.isSpecialState);
                    if (mNavBannerImplement.isShowBannerBar()) {
                        this.bSourceOnTune = true;
                    } else if (getBasicBanner().getVisibility() == 0 || !DestroyApp.isCurActivityTkuiMainActivity() || mNavIntegration.is3rdTVSource()) {
                        this.bSourceOnTune = false;
                    } else {
                        MtkLog.d(TAG, "content allowed :now banner is not show in ap");
                        this.bSourceOnTune = true;
                    }
                    MtkLog.d(TAG, "content allowed:bSourceOnTune==" + this.bSourceOnTune);
                    if (DestroyApp.isCurActivityTkuiMainActivity()) {
                        show(false, -1, false);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public boolean isGingaRunning() {
        for (MtkTvGingaAppInfoBase mtkTvGingaAppInfoBase : MtkTvGinga.getInstance().getApplicationInfoList()) {
            if (mtkTvGingaAppInfoBase.isRunning()) {
                MtkLog.d(TAG, "mtkTvGingaAppInfoBase is running.");
                return true;
            }
        }
        return false;
    }

    public void pvrChangeNum(String value) {
        startTimeout(5000);
        this.mSelectedChannelNumString = value;
        this.navBannerHandler.removeMessages(MSG_NUMBER_KEY);
        this.navBannerHandler.sendEmptyMessageDelayed(MSG_NUMBER_KEY, 1000);
    }

    public void setInterruptShowBanner(boolean interrupt) {
        this.interruptShowBannerWithDelay = interrupt;
    }

    private void hideSundryTextView() {
        this.mSundryTextView = (SundryShowTextView) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_SUNDRY);
        if (this.mSundryTextView != null && this.mSundryTextView.isVisible()) {
            this.mSundryTextView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void hideChannelListDialog() {
        this.mChannelListDialog = (ChannelListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_CH_LIST);
        if (this.mChannelListDialog != null && this.mChannelListDialog.isShowing()) {
            this.mChannelListDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void hideFavoriteChannelList() {
        this.mFavoriteChannelListView = (FavoriteListDialog) this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_FAV_LIST);
        if (this.mFavoriteChannelListView != null && this.mFavoriteChannelListView.isShowing()) {
            this.mFavoriteChannelListView.dismiss();
        }
    }

    public boolean isChangingChannelWithNum() {
        return this.mNumputChangeChannel;
    }

    public void changeChannelFavoriteMark() {
        if (getVisibility() != 0 || 2 != this.mStateManage.getState()) {
            MtkLog.d(TAG, "come in changeChannelFavoriteMark 1");
            if (this.isSpecialState || !mNavIntegration.isCurrentSourceTv()) {
                MtkLog.d(TAG, "come in changeChannelFavoriteMark show state == -1");
                show(false, -1, false);
                return;
            }
            show(false, 2, false);
            this.mStateManage.setState(2);
            this.mStateManage.setOrietation(true);
        } else if (!mNavIntegration.isCurrentSourceTv() || mNavIntegration.isCurrentSourceBlocked()) {
            MtkLog.d(TAG, "come in changeChannelFavoriteMark ,source not tv not set favorite!!");
        } else {
            MtkLog.d(TAG, "come in changeChannelFavoriteMark 2");
            this.mBasicBanner.setFavoriteVisibility();
            startTimeout(5000);
        }
    }

    public void unScramebled() {
        this.audioScramebled = false;
        this.videoScramebled = false;
    }

    public void cancelTimerTask() {
        cancelTimeUpdateTask();
        cancelVideoFormatUpdateTask();
    }

    /* access modifiers changed from: private */
    public void cancelVideoFormatUpdateTask() {
        if (getBasicBanner() != null && getBasicBanner().vfDelayTextTask != null) {
            MtkLog.d(TAG, "cancelVideoFormatUpdateTask");
            getBasicBanner().vfDelayTextTask.cancel();
        }
    }

    /* access modifiers changed from: private */
    public void cancelTimeUpdateTask() {
        if (getSimpleBanner() != null && getSimpleBanner().timeDelayTextTask != null) {
            MtkLog.d(TAG, "cancelTimeUpdateTask");
            getSimpleBanner().timeDelayTextTask.cancel();
        }
    }

    public boolean bVideoScramebled() {
        MtkLog.d(TAG, "bVideoScramebled:" + this.videoScramebled);
        return this.videoScramebled;
    }

    public void setOnTuningSource(boolean bSourceOnTune2) {
        MtkLog.d(TAG, "setOnTuningSource:" + bSourceOnTune2);
        this.bSourceOnTune = bSourceOnTune2;
    }

    /* access modifiers changed from: private */
    public boolean isMenuOptionShow() {
        if (this.mComponentsManager == null || this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG) == null) {
            return false;
        }
        boolean ret = this.mComponentsManager.getComponentById(NavBasic.NAV_COMP_ID_MENU_OPTION_DIALOG).isVisible();
        MtkLog.d(TAG, "isMenuOptionShow:" + ret);
        return ret;
    }
}
