package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.media.tv.TvTrackInfo;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAnalogCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEventBase;
import com.mediatek.twoworlds.tv.MtkTvISDBCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvSubtitle;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BannerImplement {
    public static final int DECREASE_NUM = 2000;
    public static final int DOBLY_TYPE_ATOMS = 1;
    public static final int DOBLY_TYPE_AUDIO = 2;
    public static final int DOBLY_TYPE_NONE = 0;
    public static final String STR_TIME_SPAN_TAG = "(24:00)";
    private static final String TAG = "BannerImplement";
    private static BannerImplement mNavBannerImplement = null;
    String[] audioChannel;
    private Context mContext;
    private String mCurCaptionInfo = null;
    private InputSourceManager mInputSourceManager;
    private CommonIntegration mIntegration;
    private LanguageUtil mLanguageUtil;
    private MtkTvATSCCloseCaption mMtkTvATSCCloseCaption;
    private MtkTvAnalogCloseCaption mMtkTvAnalogCloseCaption;
    private MtkTvBanner mMtkTvBanner;
    private MtkTvEventBase mMtkTvEventBase;
    private MtkTvTime mMtkTvTime;
    private TVContent mTV;
    String[] mtsAudioMode;
    private TIFProgramManager tIFProgramManager;

    public BannerImplement(Context context) {
        this.mContext = context;
        this.mLanguageUtil = new LanguageUtil(this.mContext);
        this.mIntegration = CommonIntegration.getInstance();
        this.mInputSourceManager = InputSourceManager.getInstance(context);
        this.mMtkTvBanner = MtkTvBanner.getInstance();
        this.mMtkTvAnalogCloseCaption = MtkTvAnalogCloseCaption.getInstance();
        this.mMtkTvATSCCloseCaption = MtkTvATSCCloseCaption.getInstance();
        this.mMtkTvTime = MtkTvTime.getInstance();
        this.audioChannel = context.getResources().getStringArray(R.array.audio_channels_strings);
        this.mtsAudioMode = this.mContext.getResources().getStringArray(R.array.nav_mts_strings);
        this.mTV = TVContent.getInstance(this.mContext);
        this.tIFProgramManager = TIFProgramManager.getInstance(context);
        this.mMtkTvEventBase = new MtkTvEventBase();
    }

    public static BannerImplement getInstanceNavBannerImplement(Context context) {
        if (mNavBannerImplement == null) {
            mNavBannerImplement = new BannerImplement(context);
        }
        return mNavBannerImplement;
    }

    public static void reset() {
        mNavBannerImplement = null;
    }

    public String getBannerAudioInfoFor3rd() {
        String audioInfo = "";
        TIFChannelInfo channelInfo = TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri();
        MtkLog.d(TAG, "channelInfo = " + channelInfo);
        if (channelInfo == null) {
            return audioInfo;
        }
        List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) channelInfo.mId);
        if (tIFProInfoList != null && tIFProInfoList.size() > 0) {
            audioInfo = tIFProInfoList.get(0).mAudioLanguage;
            MtkLog.d(TAG, " getBannerAudioInfoFor3rd audioInfo == " + audioInfo);
        }
        return audioInfo == null ? "" : audioInfo;
    }

    public String getBannerAudioInfo(boolean isScrambled) {
        List<TvTrackInfo> audioTracks;
        if (MarketRegionInfo.isFunctionSupport(20)) {
            String currentAudioLang = "";
            String curTrackId = "";
            if ("main" == this.mIntegration.getCurrentFocus()) {
                audioTracks = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(0);
            } else {
                audioTracks = TurnkeyUiMainActivity.getInstance().getPipView().getTracks(0);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("audioTrack size:");
            sb.append(audioTracks == null ? null : Integer.valueOf(audioTracks.size()));
            MtkLog.d(TAG, sb.toString());
            if (audioTracks == null) {
                return currentAudioLang;
            }
            if (audioTracks.size() != 0) {
                List<TvTrackInfo> audioTracks2 = filterAudioTracks(audioTracks);
                if ("main" == this.mIntegration.getCurrentFocus()) {
                    curTrackId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(0);
                } else {
                    curTrackId = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(0);
                }
                MtkLog.d(TAG, "curTrackId:" + curTrackId);
                if (curTrackId == null || (curTrackId != null && curTrackId.matches("[^0-9]"))) {
                    curTrackId = "0";
                }
                MtkLog.d(TAG, "curTrackId2:" + curTrackId);
                currentAudioLang = getAudioChannelByTrackId(curTrackId, audioTracks2);
                MtkLog.d(TAG, "currentAudioChannel:" + currentAudioLang);
            } else if (this.mIntegration.isCurrentSourceTv() && this.mInputSourceManager.isCurrentAnalogSource(this.mIntegration.getCurrentFocus())) {
                MtkLog.d(TAG, "ATV Source");
                String audioInfo = this.mMtkTvBanner.getAudioInfo();
                MtkLog.d(TAG, "come in getBannerAudioInfo, value == " + audioInfo);
                return audioInfo;
            } else if (this.mIntegration.isCurrentSourceTv() || this.mIntegration.isCurrentSourceHDMI()) {
                MtkLog.d(TAG, "DTV or HDMI Source");
                TvProviderAudioTrackBase m_current_audio = MtkTvAVMode.getInstance().getCurrentAudio();
                if (m_current_audio == null) {
                    return "No Audio";
                }
                String currlang = m_current_audio.getAudioLanguage();
                if (TextUtils.isEmpty(currlang)) {
                    currlang = "Unknown";
                }
                int audioChannelIdx = m_current_audio.getAudioChannelCount();
                MtkLog.d(TAG, "check audio from other api:" + audioChannelIdx);
                return currlang + " " + this.audioChannel[audioChannelIdx];
            }
            if (!this.mIntegration.isCurrentSourceTv() || !this.mInputSourceManager.isCurrentAnalogSource(this.mIntegration.getCurrentFocus())) {
                MtkLog.d(TAG, "DTV source");
                if (isScrambled) {
                    return SundryImplement.getInstanceNavSundryImplement(this.mContext).getCurrentAudioLang();
                }
                return currentAudioLang + " " + SundryImplement.getInstanceNavSundryImplement(this.mContext).getCurrentAudioLang();
            }
            MtkLog.d(TAG, "ATV Source");
            return Integer.valueOf(curTrackId).intValue() < 12 ? this.mtsAudioMode[Integer.parseInt(curTrackId)] : this.mtsAudioMode[0];
        }
        String audioInfo2 = this.mMtkTvBanner.getAudioInfo();
        MtkLog.d(TAG, "come in getBannerAudioInfo, value == " + audioInfo2);
        return audioInfo2;
    }

    public int getDecType() {
        String trackId;
        List<TvTrackInfo> tracks;
        int i = 0;
        if ("main" == this.mIntegration.getCurrentFocus()) {
            tracks = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(0);
            trackId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(0);
        } else {
            tracks = TurnkeyUiMainActivity.getInstance().getPipView().getTracks(0);
            trackId = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(0);
        }
        int iDecType = 0;
        int trackIdx = -1;
        if (tracks == null || tracks.size() == 0) {
            MtkLog.w(TAG, "getDecType tracks==null or tracks is empty!");
        } else {
            while (true) {
                if (i >= tracks.size()) {
                    break;
                } else if (tracks.get(i).getId().equals(trackId)) {
                    trackIdx = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        MtkLog.d(TAG, "decType,trackIdx: " + trackIdx);
        if (trackIdx == -1) {
            TvProviderAudioTrackBase m_current_audio = MtkTvAVMode.getInstance().getInputSourceCurrentAudio();
            if (m_current_audio != null) {
                iDecType = m_current_audio.getAudioDecodeType();
                MtkLog.d(TAG, "getDecType,iDecType: " + iDecType);
            }
        } else {
            String decType = null;
            if (tracks.get(trackIdx).getExtra() != null) {
                decType = tracks.get(trackIdx).getExtra().getString("key_AudioDecodeType");
            }
            int audioChannelIdx = tracks.get(trackIdx).getAudioChannelCount();
            if (!TextUtils.isEmpty(decType)) {
                iDecType = Integer.parseInt(decType);
            }
        }
        MtkLog.d(TAG, "getDecType,iDecType: " + iDecType);
        return iDecType;
    }

    private String getAudioChannelByTrackId(String trackId, List<TvTrackInfo> tracks) {
        MtkLog.d(TAG, "getAudioChannelByTrackId,trackId:" + trackId + "tracks size:" + tracks.size());
        int trackIdx = -1;
        int i = 0;
        while (true) {
            if (i >= tracks.size()) {
                break;
            } else if (tracks.get(i).getId().equals(trackId)) {
                trackIdx = i;
                break;
            } else {
                i++;
            }
        }
        int iDecType = -1;
        int audioChannelIdx = 0;
        MtkLog.d(TAG, "getAudioChannelByTrackId,trackIdx: " + trackIdx);
        if (tracks.size() > 0) {
            if (trackIdx == -1) {
                TvProviderAudioTrackBase m_current_audio = MtkTvAVMode.getInstance().getCurrentAudio();
                if (m_current_audio != null) {
                    MtkLog.d(TAG, "audio----000mAudioMode.getCurrentAudio all info: " + m_current_audio.toString());
                    iDecType = m_current_audio.getAudioDecodeType();
                    MtkLog.d(TAG, "audio----000000000000000000type:" + iDecType);
                    audioChannelIdx = m_current_audio.getAudioChannelCount();
                    MtkLog.d(TAG, "audio----000000000000000000audioChannelIdx:" + audioChannelIdx);
                }
            } else {
                String decType = null;
                if (tracks.get(trackIdx).getExtra() != null) {
                    decType = tracks.get(trackIdx).getExtra().getString("key_AudioDecodeType");
                }
                audioChannelIdx = tracks.get(trackIdx).getAudioChannelCount();
                MtkLog.d(TAG, "audio----getAudioChannelByTrackId audioChannelIdx:" + audioChannelIdx);
                if (!TextUtils.isEmpty(decType)) {
                    iDecType = Integer.parseInt(decType);
                    MtkLog.d(TAG, "audio----decType==" + iDecType);
                }
            }
            String decAudioStr = "";
            if (iDecType == 4) {
                decAudioStr = this.mContext.getString(R.string.audio_type_aac);
            } else if (iDecType == 5) {
                decAudioStr = this.mContext.getString(R.string.audio_type_he_aac);
            } else if (iDecType == 6) {
                decAudioStr = this.mContext.getString(R.string.audio_type_he_aacv2);
            } else if (iDecType == 10) {
                decAudioStr = this.mContext.getString(R.string.audio_type_mpeg1l2);
            } else if (iDecType == 15) {
                decAudioStr = this.mContext.getString(R.string.audio_type_dts);
            } else if (iDecType == 16) {
                decAudioStr = this.mContext.getString(R.string.audio_type_dts_express);
            } else if (iDecType == 17) {
                decAudioStr = this.mContext.getString(R.string.audio_type_dts_hd);
            } else {
                MtkLog.d(TAG, "audio----No need to display this audio dec type");
            }
            int doblyType = getDoblyType();
            if (doblyType == 2 || doblyType == 1) {
                MtkLog.d(TAG, "No need append audioChannel!");
                return decAudioStr;
            }
            return decAudioStr + " " + this.audioChannel[audioChannelIdx];
        }
        MtkLog.d(TAG, "audio----getAudioChannelByTrackId audioChannelIdx:null");
        return "UnknownX";
    }

    public int getDoblyType() {
        int doblyType = 0;
        MtkTvChannelInfoBase curChannel = CommonIntegration.getInstance().getCurChInfo();
        MtkLog.d(TAG, "getDoblyType----->curChannel=" + curChannel);
        if ((this.mIntegration.isCurrentSourceTv() && curChannel != null && !curChannel.isAnalogService()) || this.mIntegration.isCurrentSourceHDMI()) {
            boolean audioAtoms = this.mMtkTvBanner.isDisplayADAtmos();
            MtkLog.d(TAG, "getDoblyType----->audioAtoms=" + audioAtoms);
            int i = 2;
            if (audioAtoms) {
                int doblyVersion = this.mIntegration.getDoblyVersion();
                MtkLog.d(TAG, "getDoblyType----->doblyVersion=" + doblyVersion);
                if (doblyVersion == 5) {
                    i = 1;
                }
                doblyType = i;
            } else {
                int decType = getDecType();
                MtkLog.d(TAG, "getDoblyType----->decType=" + decType);
                if (decType == 1 || decType == 2 || decType == 18) {
                    doblyType = 2;
                }
            }
            MtkLog.d(TAG, "getDoblyType----->doblyType=" + doblyType);
        }
        return doblyType;
    }

    private List<TvTrackInfo> filterAudioTracks(List<TvTrackInfo> tracks) {
        List<TvTrackInfo> filterTracks = new ArrayList<>();
        List<TvTrackInfo> filterVITracks = new ArrayList<>();
        List<String> audioLangs = new ArrayList<>();
        for (TvTrackInfo tk : tracks) {
            filterTracks.add(tk);
            String type = null;
            String mixtype = null;
            String eClass = null;
            if (tk.getExtra() != null) {
                type = tk.getExtra().getString("key_AudioType");
                mixtype = tk.getExtra().getString("key_AudioMixType");
                eClass = tk.getExtra().getString("key_AudioEditorialClass");
                MtkLog.d(TAG, "filterAudioTracks:audiotype==" + type + ",mixtype=" + mixtype + ",eclass=" + eClass);
            }
            if (mixtype == null || !mixtype.equals("1")) {
                if (mixtype != null && !mixtype.equals("2") && type != null && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3)) {
                    filterVITracks.add(tk);
                }
            } else if (eClass != null && eClass.equals("2")) {
                filterVITracks.add(tk);
            } else if (eClass != null && eClass.equals("0") && type != null && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3)) {
                filterVITracks.add(tk);
            }
        }
        filterTracks.removeAll(filterVITracks);
        for (TvTrackInfo tk2 : filterTracks) {
            audioLangs.add(tk2.getLanguage());
        }
        filterTracks.clear();
        for (TvTrackInfo tk3 : filterVITracks) {
            String tkLang = tk3.getLanguage();
            for (String lang : audioLangs) {
                if (tkLang.equals(lang)) {
                    filterTracks.add(tk3);
                }
            }
        }
        tracks.removeAll(filterTracks);
        for (TvTrackInfo tk4 : tracks) {
            MtkLog.d(TAG, "filterAudioTracks:list tk.getLanguage ==" + tk4.getLanguage());
        }
        return tracks;
    }

    public String getBannerCaptionInfo() {
        if (this.mIntegration.is3rdTVSource()) {
            this.mCurCaptionInfo = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(2);
            MtkLog.d(TAG, "come in is3rdTVSource getBannerCaptionInfo, value == " + this.mCurCaptionInfo);
        } else {
            this.mCurCaptionInfo = this.mMtkTvBanner.getCaption();
            MtkLog.d(TAG, "come in getBannerCaptionInfo, value == " + this.mCurCaptionInfo);
        }
        if (3 == MarketRegionInfo.getCurrentMarketRegion()) {
            this.mCurCaptionInfo = this.mLanguageUtil.getSubitleNameByValue(this.mCurCaptionInfo);
            MtkLog.d(TAG, "come in getBannerCaptionInfo, getSubitleNameByValue:mCurCaptionInfo == " + this.mCurCaptionInfo);
        }
        return this.mCurCaptionInfo;
    }

    public String getCurrentChannelName() {
        if (this.mIntegration.is3rdTVSource()) {
            String channelName = getCurrentTifchannel().mDisplayName;
            MtkLog.d(TAG, "come in getCurrentChannelName is3rdTVSource, channelName == " + channelName);
            return channelName;
        }
        String channelName2 = this.mIntegration.getAvailableString(this.mMtkTvBanner.getChannelName());
        MtkLog.d(TAG, "come in getCurrentChannelName TVSource, channelName == " + channelName2);
        return channelName2;
    }

    public String getCurrentChannelNum() {
        if (this.mIntegration.is3rdTVSource()) {
            String channelNumber = getCurrentTifchannel().mDisplayNumber;
            MtkLog.d(TAG, "come in getCurrentChannelNum is3rdTVSource, channelNumber == " + channelNumber);
            return channelNumber;
        }
        String channelNumber2 = this.mMtkTvBanner.getChannelNumber();
        MtkLog.d(TAG, "come in getCurrentChannelNum, TVSource channelNumber == " + channelNumber2);
        try {
            int num = Integer.parseInt(channelNumber2);
            if (!this.mIntegration.isCurrentSourceATVforEuPA() || num <= 2000) {
                return channelNumber2;
            }
            String channelNumber3 = Integer.toString(num - 2000);
            MtkLog.d(TAG, "come in getCurrentChannelNum, PA ATVSource channelNumber == " + channelNumber3);
            return channelNumber3;
        } catch (NumberFormatException e) {
            MtkLog.e(TAG, "come in getCurrentChannelNum Parse channelNumber exception!");
            e.printStackTrace();
            return channelNumber2;
        }
    }

    public String getCurrentInputName() {
        if (this.mIntegration.is3rdTVSource()) {
            return getCurrentTifchannel().mInputServiceName;
        }
        String inputName = this.mMtkTvBanner.getIptsName();
        MtkLog.d(TAG, "come in getCurrentInputName, value == " + inputName);
        return inputName;
    }

    public String getInputCaptionInfo() {
        String inputCaptionInfo = this.mMtkTvBanner.getIptsCC();
        MtkLog.d(TAG, "come in getInputCaptionInfo, value == " + inputCaptionInfo);
        return inputCaptionInfo;
    }

    public String getInputRating() {
        String inputRating = this.mMtkTvBanner.getIptsRating();
        MtkLog.d(TAG, "come in getInputRating, value == " + inputRating);
        return inputRating;
    }

    public String getInputResolution() {
        if (this.mIntegration.is3rdTVSource()) {
            return this.mMtkTvBanner.getIptsRslt();
        }
        String inputResolution = this.mMtkTvBanner.getIptsRslt();
        MtkLog.d(TAG, "come in getInputResolution, value == " + inputResolution);
        return inputResolution;
    }

    public String getSpecialMessage() {
        String specialMessage = this.mMtkTvBanner.getMsg();
        MtkLog.d(TAG, "come in getSpecialMessage, value == " + specialMessage);
        return specialMessage;
    }

    public String getNextProgramTime() {
        int timeType = EPGUtil.judgeFormatTime12_24(this.mContext);
        if (this.mIntegration.is3rdTVSource()) {
            SimpleDateFormat sdf = new SimpleDateFormat(timeType == 1 ? "MM-dd HH:mm" : "MM-dd hh:mm,a", Locale.ENGLISH);
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri().mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 1) {
                MtkLog.d(TAG, " is3rdTVSourcecome in getNextProgramTime, value == ");
                return "";
            }
            String programTime = sdf.format(new Date(tIFProInfoList.get(1).mStartTimeUtcSec * 1000)) + " - " + sdf.format(new Date(tIFProInfoList.get(1).mEndTimeUtcSec * 1000));
            MtkLog.d(TAG, " is3rdTVSourcecome in getNextProgramTime, mStartTimeUtcSec " + tIFProInfoList.get(1).mStartTimeUtcSec + " mEndTimeUtcSec " + tIFProInfoList.get(1).mEndTimeUtcSec);
            StringBuilder sb = new StringBuilder();
            sb.append(" is3rdTVSourcecome in getNextProgramTime, value == ");
            sb.append(programTime);
            MtkLog.d(TAG, sb.toString());
            return programTime;
        }
        String nextProgramTime = this.mMtkTvBanner.getNextProgTime();
        MtkLog.d(TAG, "before come in getNextProgramTime, value == " + nextProgramTime);
        String nextProgramTime2 = convertFormatTimeByType(nextProgramTime, timeType);
        MtkLog.d(TAG, "after come in getNextProgramTime, value == " + nextProgramTime2);
        return nextProgramTime2;
    }

    public String getNextProgramTitle() {
        if (this.mIntegration.is3rdTVSource()) {
            TIFChannelInfo channelInfo = TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri();
            if (channelInfo == null) {
                MtkLog.e(TAG, " channelInfo==null");
                return "";
            }
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) channelInfo.mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 1) {
                MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTime, value == ");
                return "";
            }
            String programTitle = tIFProInfoList.get(1).mTitle;
            MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTitle, value == " + programTitle);
            return programTitle;
        }
        String nextProgramTitle = this.mMtkTvBanner.getNextProgTitle();
        MtkLog.d(TAG, "come in getNextProgramTitle, value == " + nextProgramTitle);
        if (!TextUtils.isEmpty(nextProgramTitle)) {
            return nextProgramTitle;
        }
        return this.mIntegration.isCurCHAnalog() ? "" : this.mContext.getString(R.string.banner_no_program_title);
    }

    public String getNextProgramCategory() {
        if (this.mIntegration.is3rdTVSource()) {
            MtkLog.d(TAG, " is3rdTVSourcecome in programCategory, value == ");
            return "";
        }
        int categoryIndex = -1;
        try {
            String strProgCategoryIdx = this.mMtkTvBanner.getNextProgCategoryIdx();
            MtkLog.d(TAG, "come in getNextProgramCategory, strProgCategoryIdx == " + strProgCategoryIdx);
            categoryIndex = Integer.parseInt(strProgCategoryIdx);
            MtkLog.d(TAG, "come in getNextProgramCategory, categoryIndex == " + categoryIndex);
        } catch (NumberFormatException e) {
            MtkLog.e(TAG, "come in getNextProgramCategory Parse ProgCategoryIdx exception!");
            e.printStackTrace();
        }
        String[] categoryArr = null;
        if (CommonIntegration.isEURegion()) {
            categoryArr = this.mContext.getResources().getStringArray(R.array.nav_banner_category_array);
        } else if (CommonIntegration.isSARegion()) {
            categoryArr = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_sa_type);
        }
        if (categoryArr == null) {
            MtkLog.e(TAG, "come in getNextProgramCategory categoryArr is null!");
            return "";
        } else if (categoryIndex >= categoryArr.length || categoryIndex < 0) {
            MtkLog.e(TAG, "come in getNextProgramCategory Invalid categoryIndex!");
            return "";
        } else {
            String programCategory = categoryArr[categoryIndex];
            MtkLog.d(TAG, "come in getNextProgramCategory, value == " + programCategory);
            return programCategory;
        }
    }

    public String getProgramCategory() {
        if (this.mIntegration.is3rdTVSource()) {
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri().mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 0) {
                MtkLog.d(TAG, " is3rdTVSourcecome in programCategory, value == ");
                return "";
            }
            String programCategory = tIFProInfoList.get(0).mCanonicalGenre;
            MtkLog.d(TAG, " is3rdTVSourcecome in programCategory, value == " + programCategory);
            return programCategory;
        }
        int categoryIndex = -1;
        try {
            String strProgCategoryIdx = this.mMtkTvBanner.getProgCategoryIdx();
            MtkLog.d(TAG, "come in getProgramCategory, strProgCategoryIdx == " + strProgCategoryIdx);
            categoryIndex = Integer.parseInt(strProgCategoryIdx);
            MtkLog.d(TAG, "come in getProgramCategory, categoryIndex == " + categoryIndex);
        } catch (NumberFormatException e) {
            MtkLog.e(TAG, "come in getProgramCategory Parse ProgCategoryIdx exception!");
            e.printStackTrace();
        }
        String[] categoryArr = null;
        if (CommonIntegration.isEURegion()) {
            categoryArr = this.mContext.getResources().getStringArray(R.array.nav_banner_category_array);
        } else if (CommonIntegration.isSARegion()) {
            categoryArr = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_sa_type);
        }
        if (categoryArr == null) {
            MtkLog.e(TAG, "come in getProgramCategory categoryArr is null!");
            return "";
        } else if (categoryIndex >= categoryArr.length || categoryIndex < 0) {
            MtkLog.e(TAG, "come in getProgramCategory Invalid categoryIndex!");
            return "";
        } else {
            String programCategory2 = categoryArr[categoryIndex];
            MtkLog.d(TAG, "come in getProgramCategory, value == " + programCategory2);
            return programCategory2;
        }
    }

    public String getProgramDetails() {
        if (this.mIntegration.is3rdTVSource()) {
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri().mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 0) {
                MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTitle, value == ");
                return "";
            }
            String programDetails = tIFProInfoList.get(0).mLongDescription;
            MtkLog.d(TAG, " is3rdTVSourcecome in programDetails, value == " + programDetails);
            return programDetails;
        }
        String programDetails2 = this.mMtkTvBanner.getProgDetail();
        MtkLog.d(TAG, "come in getProgramDetails, value == " + programDetails2);
        if (TextUtils.isEmpty(programDetails2)) {
            programDetails2 = this.mIntegration.isCurCHAnalog() ? "" : this.mContext.getString(R.string.nav_No_program_details);
        }
        return this.mIntegration.getAvailableString(programDetails2);
    }

    public String getNextProgramDetails() {
        if (this.mIntegration.is3rdTVSource()) {
            MtkLog.d(TAG, "come in getNextProgramDetails, 3rd.");
            return "";
        }
        String programDetails = this.mMtkTvBanner.getNextProgDetail();
        MtkLog.d(TAG, "come in getNextProgramDetails, value == " + programDetails);
        if (TextUtils.isEmpty(programDetails)) {
            programDetails = this.mIntegration.isCurCHAnalog() ? "" : this.mContext.getString(R.string.nav_No_program_details);
        }
        return this.mIntegration.getAvailableString(programDetails);
    }

    public String getProgramPageIndex() {
        String programPageIndex = this.mMtkTvBanner.getProgDetailPageIdx();
        MtkLog.d(TAG, "come in getProgramPageIndex, value == " + programPageIndex);
        return programPageIndex;
    }

    public String getProgramTime() {
        int timeType = EPGUtil.judgeFormatTime12_24(this.mContext);
        MtkLog.d(TAG, "come in getProgramTime, timeType == " + timeType);
        if (this.mIntegration.is3rdTVSource()) {
            SimpleDateFormat sdf = new SimpleDateFormat(timeType == 1 ? "MM-dd HH:mm" : "MM-dd hh:mm,a", Locale.ENGLISH);
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri().mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 0) {
                MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTime, value == ");
                return "";
            }
            String programTime = sdf.format(new Date(tIFProInfoList.get(0).mStartTimeUtcSec * 1000)) + " - " + sdf.format(new Date(tIFProInfoList.get(0).mEndTimeUtcSec * 1000));
            MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTime, mStartTimeUtcSec " + (tIFProInfoList.get(0).mStartTimeUtcSec * 1000) + " mEndTimeUtcSec " + (tIFProInfoList.get(0).mEndTimeUtcSec * 1000));
            StringBuilder sb = new StringBuilder();
            sb.append(" is3rdTVSourcecome in getProgramTime, value == ");
            sb.append(programTime);
            MtkLog.d(TAG, sb.toString());
            return programTime;
        }
        String programTime2 = this.mMtkTvBanner.getProgTime();
        MtkLog.d(TAG, "before come in getProgramTime, programTime == " + programTime2);
        String programTime3 = convertFormatTimeByType(programTime2, timeType);
        MtkLog.d(TAG, "after come in getProgramTime, value == " + programTime3);
        return programTime3;
    }

    private String convertFormatTimeByType(String time, int timeType) {
        String[] timeArr;
        String convertTime;
        String str;
        MtkLog.d(TAG, "convertFormatTimeByType, time =" + time + ",timeType=" + timeType);
        if (time == null) {
            MtkLog.d(TAG, "convertFormatTimeByType, time == null");
            return "";
        }
        boolean needFormat = false;
        if (time.contains("AM") || time.contains("PM")) {
            if (timeType == 1) {
                needFormat = true;
            }
        } else if (timeType == 0) {
            needFormat = true;
        }
        MtkLog.d(TAG, "convertFormatTimeByType, needFormat =" + needFormat);
        String convertTime2 = time;
        if (needFormat && (timeArr = time.split(MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING)) != null && timeArr.length == 2) {
            String tempStr = timeArr[1];
            boolean flag = false;
            if (tempStr.contains(STR_TIME_SPAN_TAG)) {
                tempStr = tempStr.substring(0, tempStr.indexOf(STR_TIME_SPAN_TAG));
                flag = true;
                MtkLog.d(TAG, "convertFormatTimeByType, tempStr =" + tempStr);
            }
            if (timeType == 1) {
                convertTime = formatTime12_24(timeArr[0]) + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + formatTime12_24(tempStr);
            } else {
                convertTime = formatTime24_12(timeArr[0]) + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + formatTime24_12(tempStr);
            }
            MtkLog.d(TAG, "convertFormatTimeByType, flag =" + flag);
            if (flag) {
                str = convertTime + STR_TIME_SPAN_TAG;
            } else {
                str = convertTime;
            }
            convertTime2 = str;
        }
        MtkLog.d(TAG, "convertFormatTimeByType-----> convertTime=" + convertTime2);
        return convertTime2;
    }

    private String formatTime24_12(String strTime) {
        String formatTime = "";
        try {
            formatTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new SimpleDateFormat("HH:mm").parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MtkLog.d(TAG, "formatTime24_12-----> formatTime=" + formatTime);
        return formatTime;
    }

    private String formatTime12_24(String strTime) {
        String formatTime = "";
        try {
            formatTime = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MtkLog.d(TAG, "formatTime12_24-----> formatTime=" + formatTime);
        return formatTime;
    }

    public String getProgramTitle() {
        if (this.mIntegration.is3rdTVSource()) {
            TIFChannelInfo channelInfo = TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri();
            if (channelInfo == null) {
                MtkLog.e(TAG, " channelInfo==null");
                return "";
            }
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) channelInfo.mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 0) {
                MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTitle, value == ");
                return "";
            }
            MtkLog.d(TAG, " tIFProInfoList  " + tIFProInfoList.get(0).toString());
            String programTitle = tIFProInfoList.get(0).mTitle;
            MtkLog.d(TAG, " is3rdTVSourcecome in getProgramTitle, value == " + programTitle);
            return programTitle;
        }
        String programTitle2 = this.mMtkTvBanner.getProgTitle();
        MtkLog.d(TAG, "come in getProgramTitle, value == " + programTitle2);
        if (TextUtils.isEmpty(programTitle2)) {
            programTitle2 = this.mIntegration.isCurCHAnalog() ? "" : this.mContext.getString(R.string.banner_no_program_title);
        }
        return this.mIntegration.getAvailableString(programTitle2);
    }

    public String getProgramRating() {
        if (this.mIntegration.is3rdTVSource()) {
            List<TIFProgramInfo> tIFProInfoList = this.tIFProgramManager.queryProgramListWithGroupFor3rd((int) TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri().mId);
            if (tIFProInfoList == null || tIFProInfoList.size() <= 0) {
                MtkLog.d(TAG, " is3rdTVSourcecome in programRateing, value == ");
                return "";
            }
            String programRateing = tIFProInfoList.get(0).mRating;
            MtkLog.d(TAG, " is3rdTVSourcecome in programRateing, value == " + programRateing);
            return programRateing;
        } else if (CommonIntegration.isEURegion()) {
            MtkTvEventInfoBase mtkTvEventInfoBase = this.mMtkTvEventBase.getPFEventInfoByChannel(CommonIntegration.getInstance().getCurrentChannelId(), true);
            if (mtkTvEventInfoBase == null) {
                MtkLog.e(TAG, "mtkTvEventInfoBase==null");
                return "";
            }
            int ratingValue = mtkTvEventInfoBase.getEventRatingType();
            String strRating = mtkTvEventInfoBase.getEventRating();
            MtkLog.d(TAG, "come in getProgramRating------->ratingValue=" + ratingValue + ",strRating=" + strRating);
            String programRating = this.mIntegration.mapRating2CustomerStr(ratingValue, strRating);
            StringBuilder sb = new StringBuilder();
            sb.append("come in getProgramRating------->programRating=");
            sb.append(programRating);
            MtkLog.d(TAG, sb.toString());
            return programRating;
        } else {
            String programRating2 = this.mMtkTvBanner.getRating();
            MtkLog.d(TAG, "come in getProgramRating, value == " + programRating2);
            return programRating2;
        }
    }

    public String getNextProgramRating() {
        if (this.mIntegration.is3rdTVSource()) {
            MtkLog.d(TAG, " is3rdTVSourcecome in programRateing, value == ");
            return "";
        } else if (CommonIntegration.isEURegion()) {
            MtkTvEventInfoBase mtkTvEventInfoBase = this.mMtkTvEventBase.getPFEventInfoByChannel(CommonIntegration.getInstance().getCurrentChannelId(), false);
            if (mtkTvEventInfoBase == null) {
                MtkLog.e(TAG, "mtkTvEventInfoBase==null");
                return "";
            }
            int ratingValue = mtkTvEventInfoBase.getEventRatingType();
            String strRating = mtkTvEventInfoBase.getEventRating();
            MtkLog.d(TAG, "come in getNextProgrameRating------->ratingValue=" + ratingValue + ",strRating=" + strRating);
            String programRating = this.mIntegration.mapRating2CustomerStr(ratingValue, strRating);
            StringBuilder sb = new StringBuilder();
            sb.append("come in getNextProgrameRating------->programRating=");
            sb.append(programRating);
            MtkLog.d(TAG, sb.toString());
            return programRating;
        } else {
            String programRating2 = this.mMtkTvBanner.getNextRating();
            MtkLog.d(TAG, "come in getNextProgrameRating, value == " + programRating2);
            return programRating2;
        }
    }

    public String getCurrentTime() {
        MtkTvTimeFormatBase time = this.mMtkTvTime.getBroadcastLocalTime();
        MtkLog.d(TAG, "getCurrentTime,hour=" + time.hour + ",minute=" + time.minute);
        String timeStr = EPGUtil.formatTime(time.hour, time.minute, this.mContext);
        if (!TVContent.getInstance(this.mContext).isMYSCountry()) {
            return timeStr;
        }
        String dateStr = EPGUtil.getYMDLocalTime();
        String weekStr = EPGUtil.getWeek(time.weekDay);
        MtkLog.d(TAG, "getCurrentTime,timeStr=" + timeStr + ",dateStr=" + dateStr + ", weekStr=" + weekStr);
        return timeStr + " " + dateStr + weekStr;
    }

    public String getTVTurnerMode() {
        if (this.mIntegration.is3rdTVSource()) {
            return "";
        }
        int iTurnerMode = MtkTvConfig.getInstance().getConfigValue("g_bs__bs_src");
        MtkLog.d(TAG, "come in getTVTurnerMode, iTurnerMode == " + iTurnerMode);
        String[] tnuerArr = this.mContext.getResources().getStringArray(R.array.menu_tv_tuner_mode_array_full_eu_sat_only);
        if (iTurnerMode >= tnuerArr.length || iTurnerMode < 0) {
            MtkLog.e(TAG, "Invalid turnerMode!");
            return "";
        }
        String turnerMode = tnuerArr[iTurnerMode];
        MtkLog.d(TAG, "come in getTVTurnerMode, turnerMode == " + turnerMode);
        if (MarketRegionInfo.getCurrentMarketRegion() != 0 || !CommonIntegration.getInstance().isCurrentSourceATV()) {
            return turnerMode;
        }
        MtkLog.d(TAG, "getTVTurnerMode---->Current source is atv.");
        return "Cable";
    }

    public String getCurrentVideoInfo() {
        if (this.mIntegration.is3rdTVSource()) {
            return getCurrentTifchannel().mVideoFormat;
        }
        String videoInfo = this.mMtkTvBanner.getVideoInfo();
        MtkLog.d(TAG, "come in getCurrentVideoInfo, value == " + videoInfo);
        return videoInfo;
    }

    public boolean isShowADEIcon() {
        boolean showADEIcon = this.mMtkTvBanner.isDisplayADEyeIcon();
        MtkLog.d(TAG, "come in isShowADEIcon, value == " + showADEIcon);
        return showADEIcon;
    }

    public TvTrackInfo getSelectedSubTitle() {
        String currentTvSubTitleId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(2);
        List<TvTrackInfo> subtitleListTv = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(2);
        MtkLog.d(TAG, "getSelectedSubTitle---->currentTvSubTitleId=" + currentTvSubTitleId + ",subtitleListTv=" + subtitleListTv);
        if (subtitleListTv == null || currentTvSubTitleId == null) {
            return null;
        }
        int size = subtitleListTv.size();
        MtkLog.d(TAG, "getSelectedSubTitle---->currentTvSubTitleId=" + currentTvSubTitleId);
        for (TvTrackInfo trackInfo : subtitleListTv) {
            if (currentTvSubTitleId.equals(trackInfo.getId())) {
                return trackInfo;
            }
        }
        return null;
    }

    public boolean isShowEARIcon() {
        boolean showEarIcon = this.mMtkTvBanner.isDisplayADEarIcon();
        MtkLog.d(TAG, "come in isShowEARIcon, value == " + showEarIcon);
        return showEarIcon || isShowHOH();
    }

    private boolean isShowHOH() {
        TvTrackInfo selectedTrackInfo = getSelectedSubTitle();
        if (selectedTrackInfo == null) {
            MtkLog.d(TAG, "come in isShowHOH, selectedTrackInfo ==null ");
            return false;
        }
        MtkLog.d(TAG, "come in isShowHOH, selectedTrackInfo = " + selectedTrackInfo);
        if (selectedTrackInfo == null || selectedTrackInfo.getExtra() == null) {
            return false;
        }
        String hearingImpaired = selectedTrackInfo.getExtra().getString("key_HearingImpaired");
        MtkLog.d(TAG, "come in isShowHOH, hearingImpaired = " + hearingImpaired);
        return TextUtils.equals(hearingImpaired, "true");
    }

    public boolean isShowCaptionIcon() {
        boolean showCaptionIcon = this.mMtkTvBanner.isDisplayCaptionIcon();
        MtkLog.d(TAG, "come in isShowCaptionIcon, value == " + showCaptionIcon);
        return showCaptionIcon;
    }

    public boolean isShowFavoriteIcon() {
        boolean showFavoriteIcon = this.mMtkTvBanner.isDisplayFavIcon();
        MtkLog.d(TAG, "come in isShowFavoriteIcon, value == " + showFavoriteIcon);
        return showFavoriteIcon;
    }

    public boolean isShowFrameCH() {
        boolean showFrameCh = this.mMtkTvBanner.isDisplayFrmCH();
        MtkLog.d(TAG, "come in isShowFrameCH, value == " + showFrameCh);
        return showFrameCh;
    }

    public boolean isShowFrameDetails() {
        boolean showFrameDetails = this.mMtkTvBanner.isDisplayFrmDetail();
        MtkLog.d(TAG, "come in isShowFrameDetails, value == " + showFrameDetails);
        return showFrameDetails;
    }

    public boolean isShowFrameInfo() {
        boolean showFrameInfo = this.mMtkTvBanner.isDisplayFrmInfo();
        MtkLog.d(TAG, "come in isShowFrameInfo, value == " + showFrameInfo);
        return showFrameInfo;
    }

    public boolean isShowGingaIcon() {
        boolean showGingaIcon = this.mMtkTvBanner.isDisplayGingaIcon();
        MtkLog.d(TAG, "come in isShowGingaIcon, value == " + showGingaIcon);
        return showGingaIcon;
    }

    public boolean isShowInputLockIcon() {
        boolean showInputLockIcon = this.mMtkTvBanner.isDisplayIptsLockIcon();
        MtkLog.d(TAG, "come in isShowInputLockIcon, value == " + showInputLockIcon);
        return showInputLockIcon;
    }

    public boolean isShowChannelLogoIcon() {
        boolean showChannelLogoIcon = this.mMtkTvBanner.isDisplayLogoIcon();
        MtkLog.d(TAG, "come in isShowChannelLogoIcon, value == " + showChannelLogoIcon);
        return showChannelLogoIcon;
    }

    public boolean isShowDetailsDownIcon() {
        boolean showDetailsDownIcon = this.mMtkTvBanner.isDisplayProgDetailDownIcon();
        MtkLog.d(TAG, "come in isShowDetailsDownIcon, value == " + showDetailsDownIcon);
        return showDetailsDownIcon;
    }

    public boolean isShowDetailsUpIcon() {
        boolean showDetailsUpIcon = this.mMtkTvBanner.isDisplayProgDetailUpIcon();
        MtkLog.d(TAG, "come in isShowDetailsUpIcon, value == " + showDetailsUpIcon);
        return showDetailsUpIcon;
    }

    public boolean isShowTtxIcon() {
        boolean showTtxIcon = this.mMtkTvBanner.isDisplayTtxIcon();
        MtkLog.d(TAG, "come in isShowTtxIcon, value == " + showTtxIcon);
        return showTtxIcon;
    }

    public boolean isShowTVLockIcon() {
        boolean showTVLockIcon = this.mMtkTvBanner.isDisplayTVLockIcon();
        MtkLog.d(TAG, "come in isShowTVLockIcon, value == " + showTVLockIcon);
        return showTVLockIcon;
    }

    public boolean isShowBannerBar() {
        boolean showTVLockIcon = this.mMtkTvBanner.isDisplayBanner();
        MtkLog.d(TAG, "come in isShowBannerBar, value == " + showTVLockIcon);
        return showTVLockIcon;
    }

    public int changeNextCloseCaption() {
        int result = -1;
        MtkLog.d(TAG, "isCurrentSourceATV>>>" + this.mIntegration.isCurrentSourceATV() + "   " + this.mIntegration.isCurrentSourceDTV());
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 3:
                break;
            case 1:
                result = this.mMtkTvAnalogCloseCaption.analogCCNextStream();
                break;
            case 2:
                result = MtkTvISDBCloseCaption.getInstance().ISDBCCNextStream();
                break;
        }
        if (this.mIntegration.isCurrentSourceTv()) {
            if (MarketRegionInfo.isFunctionSupport(22)) {
                changeToNextStreamByTif();
            } else {
                result = MtkTvSubtitle.getInstance().nextStream();
            }
        }
        MtkLog.d(TAG, "changeNextCloseCaption result:" + result);
        return result;
    }

    private void changeToNextStreamByTif() {
        String focusWin = CommonIntegration.getInstance().getCurrentFocus();
        if (focusWin.equals("main")) {
            String currentTvSubTitleId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(2);
            List<TvTrackInfo> subtitleListTv = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(2);
            if (subtitleListTv != null) {
                int curSubtitleSize = subtitleListTv.size();
                MtkLog.d(TAG, "changeToNextStreamByTif---->currentTvSubTitleId=" + currentTvSubTitleId);
                boolean hasFind = false;
                int i = 0;
                if (currentTvSubTitleId != null) {
                    for (int j = 0; j < curSubtitleSize; j++) {
                        MtkLog.d(TAG, "changeToNextStreamByTif id=" + subtitleListTv.get(j).getId() + ",language=" + subtitleListTv.get(j).getLanguage() + ",currentTvSubTitleId=" + currentTvSubTitleId);
                    }
                    while (true) {
                        if (i >= curSubtitleSize) {
                            break;
                        } else if (currentTvSubTitleId.equals(subtitleListTv.get(i).getId())) {
                            hasFind = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                MtkLog.d(TAG, "changeToNextStreamByTif---->hasFind=" + hasFind + ",index=" + i);
                if (!hasFind && curSubtitleSize > 0) {
                    TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(2, subtitleListTv.get(0).getId());
                } else if (i + 1 < curSubtitleSize) {
                    TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(2, subtitleListTv.get(i + 1).getId());
                } else {
                    MtkTvSubtitle.getInstance().nextStream();
                }
            }
        } else if (focusWin.equals("sub")) {
            String currentPipSubTitleId = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(2);
            List<TvTrackInfo> subtitleListPip = TurnkeyUiMainActivity.getInstance().getPipView().getTracks(2);
            if (subtitleListPip == null) {
                MtkLog.e(TAG, "subtitleListPip==null");
                return;
            }
            boolean hasFind2 = false;
            int i2 = 0;
            if (currentPipSubTitleId != null) {
                while (true) {
                    if (i2 >= subtitleListPip.size()) {
                        break;
                    } else if (currentPipSubTitleId.equals(subtitleListPip.get(i2).getId())) {
                        hasFind2 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            if (!hasFind2 && subtitleListPip.size() > 0) {
                TurnkeyUiMainActivity.getInstance().getPipView().selectTrack(2, subtitleListPip.get(0).getId());
            } else if (i2 + 1 < subtitleListPip.size()) {
                TurnkeyUiMainActivity.getInstance().getPipView().selectTrack(2, subtitleListPip.get(i2 + 1).getId());
            } else {
                int nextStream = MtkTvSubtitle.getInstance().nextStream();
                TurnkeyUiMainActivity.getInstance().getPipView().selectTrack(2, String.valueOf(255));
            }
        }
    }

    public int setCCVisiable(boolean visiable) {
        int result = -1;
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 0:
            case 3:
                if (!MarketRegionInfo.isFunctionSupport(15)) {
                    this.mIntegration.isCurrentSourceTv();
                    break;
                } else {
                    setCaptionEnabled(visiable);
                    break;
                }
            case 1:
                if (!MarketRegionInfo.isFunctionSupport(15)) {
                    MtkLog.d("Abstract", "Abstract set tvapi....");
                    if (!this.mIntegration.isCurrentSourceATV()) {
                        if (this.mIntegration.isCurrentSourceDTV()) {
                            result = this.mMtkTvATSCCloseCaption.atscCCSetCcVisible(visiable);
                            break;
                        }
                    } else {
                        result = this.mMtkTvAnalogCloseCaption.analogCCSetCcVisible(visiable);
                        break;
                    }
                } else {
                    MtkLog.d("Abstract", "Abstract set us caption....");
                    setCaptionEnabled(visiable);
                    break;
                }
                break;
            case 2:
                if (!MarketRegionInfo.isFunctionSupport(15)) {
                    MtkLog.d("Abstract", "set ISDBCCEnable....");
                    result = MtkTvISDBCloseCaption.getInstance().ISDBCCEnable(visiable);
                    break;
                } else {
                    MtkLog.d("Abstract", "Abstract set ISDB caption....");
                    setCaptionEnabled(visiable);
                    break;
                }
        }
        MtkLog.d(TAG, "setCCVisiable index =" + result);
        return result;
    }

    public boolean isCurrentSourceATV() {
        return this.mIntegration.isCurrentSourceATV();
    }

    public String getCurrentCCOrSubtitleValue() {
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 1:
                if (this.mIntegration.isCurrentSourceATV()) {
                    switch (this.mMtkTvAnalogCloseCaption.analogCCNextStream()) {
                        case 1:
                            return "CC1";
                        case 2:
                            return "CC2";
                        case 3:
                            return "CC3";
                        case 4:
                            return "CC4";
                        case 5:
                            return "TEXT1";
                        case 6:
                            return "TEXT2";
                        case 7:
                            return "TEXT13";
                        case 8:
                            return "TEXT4";
                        default:
                            return "";
                    }
                } else if (!this.mIntegration.isCurrentSourceDTV()) {
                    return "";
                } else {
                    return "Service" + this.mMtkTvATSCCloseCaption.atscCCNextStream();
                }
            case 2:
                if (!this.mIntegration.isCurrentSourceTv()) {
                    return "";
                }
                return "Language" + MtkTvISDBCloseCaption.getInstance().ISDBCCGetCCString();
            case 3:
                if (this.mIntegration.isCurrentSourceTv()) {
                    return "";
                }
                return "";
            default:
                return "";
        }
    }

    private void setCaptionEnabled(boolean enabled) {
        String focusWin = CommonIntegration.getInstance().getCurrentFocus();
        if (focusWin.equals("main")) {
            TurnkeyUiMainActivity.getInstance().getTvView().setCaptionEnabled(enabled);
        } else if (focusWin.equals("sub")) {
            TurnkeyUiMainActivity.getInstance().getPipView().setCaptionEnabled(enabled);
        }
    }

    public TIFChannelInfo getCurrentTifchannel() {
        TIFChannelInfo tifchannel;
        if (!this.mIntegration.is3rdTVSource() || (tifchannel = TIFChannelManager.getInstance(this.mContext).getChannelInfoByUri()) == null) {
            return new TIFChannelInfo();
        }
        return tifchannel;
    }

    public String getTVLauncherInfoForLiveTv() {
        String TVLauncherInfoForLiveTv;
        if (this.mIntegration.is3rdTVSource()) {
            MtkLog.d(TAG, "getTVLauncherInfoForLiveTv is3rdTVSource ");
            TVLauncherInfoForLiveTv = getCurrentInputName() + " - " + getCurrentTifchannel().mDisplayName;
        } else if (this.mIntegration.isCurrentSourceTv() || this.mIntegration.isCurrentSourceATV() || this.mIntegration.isCurrentSourceDTV()) {
            MtkLog.d(TAG, "getTVLauncherInfoForLiveTv isCurrentSourceTv ");
            String curCHName = getCurrentChannelName();
            MtkLog.d(TAG, "getTVLauncherInfoForLiveTv curCHName= " + curCHName);
            if (!TextUtils.isEmpty(curCHName)) {
                MtkLog.d(TAG, "getTVLauncherInfoForLiveTv isCurrentSourceTv getCurrentChannelName ");
                TVLauncherInfoForLiveTv = curCHName;
            } else {
                MtkLog.d(TAG, "getTVLauncherInfoForLiveTv isCurrentSourceTv getCurrentChannelNum ");
                TVLauncherInfoForLiveTv = getCurrentChannelNum();
            }
            String programTitle = getProgramTitle();
            MtkLog.d(TAG, "getTVLauncherInfoForLiveTv programTitle =" + programTitle);
            if (programTitle != null && !TextUtils.equals(programTitle, this.mContext.getString(R.string.banner_no_program_title))) {
                TVLauncherInfoForLiveTv = TVLauncherInfoForLiveTv + " - " + getProgramTitle();
            }
        } else {
            MtkLog.d(TAG, "getTVLauncherInfoForLiveTv not isCurrentSourceTv and 3rd ");
            TVLauncherInfoForLiveTv = getCurrentInputName();
        }
        MtkLog.d(TAG, "getTVLauncherInfoForLiveTv TVLauncherInfoForLiveTv= " + TVLauncherInfoForLiveTv);
        return TVLauncherInfoForLiveTv;
    }
}
