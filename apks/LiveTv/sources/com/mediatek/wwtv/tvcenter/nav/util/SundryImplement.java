package com.mediatek.wwtv.tvcenter.nav.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvGinga;
import com.mediatek.twoworlds.tv.MtkTvMHEG5;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SundryImplement {
    private static String TAG = "SundryImplement";
    private static SundryImplement instance;
    private static MtkTvAVMode navMtkTvAVMode;
    private static MtkTvGinga navMtkTvGinga;
    private static MtkTvMHEG5 navMtkTvMHEG5;
    private static MtkTvTime navMtkTvTime;
    Map<String, String> audioSpecialMap;
    private CommonIntegration mCommonIntegration;
    private Context mContext;
    private InputSourceManager mInputSourceManager;
    private List<TvTrackInfo> mMtsAudioTracks;
    private TvView mainTvView;
    private MtkTvAppTV mtkTvAppTv = MtkTvAppTV.getInstance();
    public String[] mtsAudioMode;
    private TvView subTvView;

    public List<TvTrackInfo> getmMtsAudioTracks() {
        MtkLog.d(TAG, "getmMtsAudioTracks");
        return this.mMtsAudioTracks;
    }

    public void setmMtsAudioTracks(List<TvTrackInfo> mMtsAudioTracks2) {
        this.mMtsAudioTracks = mMtsAudioTracks2;
    }

    private SundryImplement(Context context) {
        this.mContext = context;
        navMtkTvAVMode = MtkTvAVMode.getInstance();
        navMtkTvTime = MtkTvTime.getInstance();
        navMtkTvGinga = MtkTvGinga.getInstance();
        navMtkTvMHEG5 = MtkTvMHEG5.getInstance();
        if (TurnkeyUiMainActivity.getInstance() != null) {
            this.mainTvView = TurnkeyUiMainActivity.getInstance().getTvView();
            this.subTvView = TurnkeyUiMainActivity.getInstance().getPipView();
        }
        this.mCommonIntegration = CommonIntegration.getInstance();
        this.mInputSourceManager = InputSourceManager.getInstance(context);
        this.mtsAudioMode = this.mContext.getResources().getStringArray(R.array.nav_mts_strings);
        this.audioSpecialMap = new HashMap();
    }

    public static synchronized SundryImplement getInstanceNavSundryImplement(Context context) {
        SundryImplement sundryImplement;
        synchronized (SundryImplement.class) {
            if (instance == null) {
                instance = new SundryImplement(context);
            }
            sundryImplement = instance;
        }
        return sundryImplement;
    }

    public static synchronized void setInstanceNavSundryImplementNull() {
        synchronized (SundryImplement.class) {
            instance = null;
        }
    }

    public boolean isHeadphoneSetOn() {
        boolean result = ((AudioManager) this.mContext.getSystemService("audio")).isWiredHeadsetOn();
        String str = TAG;
        MtkLog.d(str, "isHeadphoneSetOn:" + result);
        return result;
    }

    public String getCurrentAudioLang() {
        List<TvTrackInfo> audioTracks;
        String curTrackId;
        MtkLog.d(TAG, "getCurrentAudioLang");
        String str = TAG;
        MtkLog.d(str, "navMtkTvAVMode.getAudioLang():" + navMtkTvAVMode.getAudioLang());
        String currentAudioLang = "";
        if (MarketRegionInfo.isFunctionSupport(20)) {
            if ("main" == this.mCommonIntegration.getCurrentFocus()) {
                audioTracks = TurnkeyUiMainActivity.getInstance().getTvView().getTracks(0);
            } else {
                audioTracks = TurnkeyUiMainActivity.getInstance().getPipView().getTracks(0);
            }
            if (audioTracks == null) {
                return currentAudioLang;
            }
            String str2 = TAG;
            MtkLog.d(str2, "audioTrack size:" + audioTracks.size());
            if (audioTracks.size() != 0) {
                List<TvTrackInfo> audioTracks2 = filterAudioTracks(audioTracks);
                if (!CommonIntegration.getInstance().is3rdTVSource() && this.mInputSourceManager.isCurrentTvSource(this.mCommonIntegration.getCurrentFocus()) && this.mInputSourceManager.isCurrentAnalogSource(this.mCommonIntegration.getCurrentFocus())) {
                    MtkLog.d(TAG, "ATV Source");
                    currentAudioLang = navMtkTvAVMode.getAudioLang();
                } else if ((this.mInputSourceManager.isCurrentTvSource(this.mCommonIntegration.getCurrentFocus()) && !this.mInputSourceManager.isCurrentAnalogSource(this.mCommonIntegration.getCurrentFocus())) || this.mCommonIntegration.is3rdTVSource()) {
                    MtkLog.d(TAG, "DTV source");
                    if ("main" == this.mCommonIntegration.getCurrentFocus()) {
                        curTrackId = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(0);
                    } else {
                        curTrackId = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(0);
                    }
                    String str3 = TAG;
                    MtkLog.d(str3, "curTrackId:" + curTrackId);
                    currentAudioLang = getAudioTrackLangById(curTrackId, audioTracks2);
                }
                String curTrackId2 = TAG;
                MtkLog.d(curTrackId2, "currentAudioLang:" + currentAudioLang);
                return currentAudioLang;
            } else if (!this.mInputSourceManager.isCurrentTvSource(this.mCommonIntegration.getCurrentFocus()) || !this.mInputSourceManager.isCurrentAnalogSource(this.mCommonIntegration.getCurrentFocus())) {
                return this.mContext.getResources().getString(R.string.nav_no_function);
            } else {
                MtkLog.d(TAG, "ATV Source");
                return navMtkTvAVMode.getAudioLang();
            }
        } else {
            String currentAudioLang2 = navMtkTvAVMode.getAudioLang();
            String str4 = TAG;
            MtkLog.d(str4, "come in getCurrentAudioLang,text = " + currentAudioLang2);
            return currentAudioLang2;
        }
    }

    public String[] getAllMtsModes() {
        return (String[]) getAllMtsModesList().toArray(new String[0]);
    }

    private List<String> getAllMtsModesList() {
        List<TvProviderAudioTrackBase> tracks = navMtkTvAVMode.getAudioAvailableRecord();
        List<String> modes = new ArrayList<>();
        for (TvProviderAudioTrackBase tvProviderAudioTrackBase : tracks) {
            int audioId = tvProviderAudioTrackBase.getAudioId();
            String str = TAG;
            MtkLog.d(str, "getAllMtsModes audioId=" + audioId);
            String currentAudioLang = null;
            if (audioId < this.mtsAudioMode.length && audioId > -1) {
                currentAudioLang = this.mtsAudioMode[audioId];
            }
            if (!modes.contains(currentAudioLang) && currentAudioLang != null) {
                modes.add(currentAudioLang);
                String str2 = TAG;
                MtkLog.d(str2, "getAllMtsModes addAudioLang=" + currentAudioLang);
            }
        }
        return modes;
    }

    public String getMtsModeString(int index) {
        if (index < 0 || index > this.mtsAudioMode.length) {
            return "";
        }
        return this.mtsAudioMode[index];
    }

    public int getMtsByModeString(String mode) {
        for (int i = 0; i < this.mtsAudioMode.length; i++) {
            if (mode.equalsIgnoreCase(this.mtsAudioMode[i])) {
                return i;
            }
        }
        return 0;
    }

    public String getMtsSummaryByAcfgValue(int configValue) {
        List<String> list = getAllMtsModesList();
        if (list.size() == 0) {
            return null;
        }
        String mode = getMtsModeString(configValue);
        String summary = "";
        if (list.contains(mode)) {
            summary = mode;
        } else if (list.size() > 0) {
            summary = list.get(0);
        }
        String str = TAG;
        MtkLog.d(str, "getMtsSummaryByAcfgValue summary=" + summary);
        return summary;
    }

    public String getMtsCurIndexByAcfgValue(int configValue) {
        List<String> list = getAllMtsModesList();
        String mode = getMtsModeString(configValue);
        if (list.contains(mode)) {
            return mode;
        }
        return null;
    }

    public List<TvProviderAudioTrackBase> filterAudioTracksForNav(List<TvProviderAudioTrackBase> tracks) {
        List<TvProviderAudioTrackBase> filterTracks = new ArrayList<>();
        List<TvProviderAudioTrackBase> filterVITracks = new ArrayList<>();
        List<TvProviderAudioTrackBase> fiListSplits = new ArrayList<>();
        List<String> audioLangs = new ArrayList<>();
        for (TvProviderAudioTrackBase tk : tracks) {
            filterTracks.add(tk);
            String type = String.valueOf(tk.getAudioType());
            String mixtype = String.valueOf(tk.getAudioMixType());
            String eClass = String.valueOf(tk.getAudioEditorialClass());
            String str = TAG;
            MtkLog.d(str, "filterAudioTracksForNav:" + tk.toString());
            if (mixtype == null || !mixtype.equals("1")) {
                if (mixtype != null && !mixtype.equals("2") && type != null && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3)) {
                    filterVITracks.add(tk);
                }
            } else if (eClass != null && eClass.equals("2")) {
                filterVITracks.add(tk);
            } else if (eClass != null && eClass.equals("0") && type != null && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3)) {
                filterVITracks.add(tk);
            }
            if (TextUtils.isEmpty(tk.getAudioLanguage()) && tk.getAudioFormat() == 2 && tk.getAudioIndex() == 2) {
                fiListSplits.add(tk);
            }
        }
        filterTracks.removeAll(filterVITracks);
        for (TvProviderAudioTrackBase tk2 : filterTracks) {
            audioLangs.add(tk2.getAudioLanguage());
        }
        filterTracks.clear();
        for (TvProviderAudioTrackBase tk3 : filterVITracks) {
            String tkLang = tk3.getAudioLanguage();
            for (String lang : audioLangs) {
                if (tkLang.equals(lang)) {
                    filterTracks.add(tk3);
                }
            }
        }
        tracks.removeAll(filterTracks);
        for (TvProviderAudioTrackBase tk4 : fiListSplits) {
            List<TvProviderAudioTrackBase> tempsInfos = new ArrayList<>();
            String leftLang = "";
            String rightLang = "";
            for (TvProviderAudioTrackBase tvProviderAudioTrackBase : tracks) {
                if (tk4.getAudioPid() == tvProviderAudioTrackBase.getAudioPid()) {
                    tempsInfos.add(tvProviderAudioTrackBase);
                }
            }
            for (TvProviderAudioTrackBase tvTrackInfo : tempsInfos) {
                if (tvTrackInfo.getAudioIndex() == 0) {
                    leftLang = tvTrackInfo.getAudioLanguage();
                } else if (tvTrackInfo.getAudioIndex() == 1) {
                    rightLang = tvTrackInfo.getAudioLanguage();
                }
            }
            tk4.setAudioLanguage(leftLang + " + " + rightLang);
        }
        for (TvProviderAudioTrackBase tk5 : tracks) {
            Iterator<TvProviderAudioTrackBase> iterator = fiListSplits.iterator();
            while (iterator.hasNext()) {
                TvProviderAudioTrackBase next = iterator.next();
                if (TextUtils.isEmpty(tk5.getAudioLanguage()) && tk5.getAudioId() == next.getAudioId()) {
                    tk5.setAudioLanguage(next.getAudioLanguage());
                    iterator.remove();
                }
            }
        }
        return tracks;
    }

    private boolean isSupportVI() {
        return MtkTvConfig.getInstance().getConfigValue("g_audio__aud_type") == 2;
    }

    public List<TvTrackInfo> filterAudioTracks(List<TvTrackInfo> tracks) {
        List<TvTrackInfo> filterTracks = new ArrayList<>();
        List<TvTrackInfo> filterVITracks = new ArrayList<>();
        List<TvTrackInfo> fiListSplits = new ArrayList<>();
        List<String> audioLangs = new ArrayList<>();
        for (TvTrackInfo tk : tracks) {
            filterTracks.add(tk);
            String type = null;
            String mixtype = null;
            String eClass = null;
            String audioFormt = null;
            String audioIndex = null;
            if (tk.getExtra() != null) {
                type = tk.getExtra().getString("key_AudioType");
                mixtype = tk.getExtra().getString("key_AudioMixType");
                eClass = tk.getExtra().getString("key_AudioEditorialClass");
                audioFormt = tk.getExtra().getString("key_AudioFormt");
                audioIndex = tk.getExtra().getString("key_AudioIndex");
            }
            if (!TextUtils.isEmpty(audioFormt) && audioFormt.equals("2") && !TextUtils.isEmpty(audioIndex) && audioIndex.equals("2") && TextUtils.isEmpty(tk.getLanguage())) {
                fiListSplits.add(tk);
            }
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("filterAudioTracks:lang:");
            sb.append(tk.getLanguage());
            sb.append(" id:");
            sb.append(tk.getId());
            sb.append(" Bundle:");
            sb.append(tk.getExtra() == null ? "null" : tk.getExtra().toString());
            MtkLog.d(str, sb.toString());
            if (!isSupportVI()) {
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
        List<TvTrackInfo> newInfos = new ArrayList<>();
        for (TvTrackInfo tk5 : fiListSplits) {
            List<TvTrackInfo> tempsInfos = new ArrayList<>();
            String leftLang = "";
            String rightLang = "";
            for (TvTrackInfo tvTrackInfo : tracks) {
                if (!(tk5.getExtra() == null || tvTrackInfo.getExtra() == null || !tk5.getExtra().getString("key_AudioPid").equals(tvTrackInfo.getExtra().getString("key_AudioPid")))) {
                    tempsInfos.add(tvTrackInfo);
                }
            }
            for (TvTrackInfo tvTrackInfo2 : tempsInfos) {
                if (tvTrackInfo2.getExtra() != null && tvTrackInfo2.getExtra().getString("key_AudioIndex").equals("0")) {
                    leftLang = tvTrackInfo2.getLanguage();
                } else if (tvTrackInfo2.getExtra() != null && tvTrackInfo2.getExtra().getString("key_AudioIndex").equals("1")) {
                    rightLang = tvTrackInfo2.getLanguage();
                }
            }
            newInfos.add(new TvTrackInfo.Builder(0, tk5.getId()).setLanguage(leftLang + " + " + rightLang).build());
        }
        List<TvTrackInfo> result = new ArrayList<>();
        for (TvTrackInfo tvTrackInfo3 : tracks) {
            int index = -1;
            int i = 0;
            while (true) {
                if (i >= newInfos.size()) {
                    break;
                } else if (tvTrackInfo3.getId().equals(newInfos.get(i).getId())) {
                    index = i;
                    break;
                } else {
                    i++;
                }
            }
            if (index >= 0) {
                result.add(newInfos.get(index));
                newInfos.remove(index);
            } else {
                result.add(tvTrackInfo3);
            }
        }
        return result;
    }

    private String getAudioTrackLangById(String trackId, List<TvTrackInfo> tracks) {
        String str = TAG;
        MtkLog.d(str, "getAudioTrackLangById,trackId:" + trackId);
        boolean isScrambled = false;
        BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
        if (bannerView != null) {
            isScrambled = bannerView.isAudioScrambled();
        }
        int trackIdx = 0;
        String viString = "";
        if (tracks.size() != 1) {
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
            if (isScrambled) {
                return (trackIdx + 1) + "/" + tracks.size() + " " + this.mContext.getString(R.string.nav_channel_audio_scrambled);
            }
            String lang = tracks.get(trackIdx).getLanguage();
            String str2 = TAG;
            MtkLog.d(str2, "lang:" + lang);
            if (isSupportVI() && a_aud_is_visual_impaired_audio(tracks.get(trackIdx))) {
                viString = "(VI)";
            }
            return buildAudioLangStrByLang((trackIdx + 1) + "/" + tracks.size(), lang) + viString;
        } else if (isScrambled) {
            return this.mContext.getString(R.string.nav_channel_audio_scrambled);
        } else {
            String lang2 = tracks.get(0).getLanguage();
            String str3 = TAG;
            MtkLog.d(str3, "lang:" + lang2);
            if (isSupportVI() && a_aud_is_visual_impaired_audio(tracks.get(0))) {
                viString = "(VI)";
            }
            return buildAudioLangStrByLang(lang2) + viString;
        }
    }

    private boolean a_aud_is_visual_impaired_audio(TvTrackInfo t_audio_mpeg) {
        return a_aud_is_independent_visual_impaired_audio(t_audio_mpeg) || a_aud_is_mixable_visual_impaired_audio(t_audio_mpeg);
    }

    private boolean a_aud_is_independent_visual_impaired_audio(TvTrackInfo t_audio_mpeg) {
        if (t_audio_mpeg.getExtra() == null) {
            return false;
        }
        String type = t_audio_mpeg.getExtra().getString("key_AudioType");
        String mixtype = t_audio_mpeg.getExtra().getString("key_AudioMixType");
        String eClass = t_audio_mpeg.getExtra().getString("key_AudioEditorialClass");
        String str = TAG;
        MtkLog.d(str, "a_aud_is_independent_visual_impaired_audio  mixtype:" + mixtype + " eClass:" + eClass + " type:" + type);
        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(mixtype) || TextUtils.isEmpty(eClass) || !mixtype.equals("2")) {
            return false;
        }
        if (eClass.equals("2") || eClass.equals(MtkTvRatingConvert2Goo.RATING_STR_4) || (eClass.equals("0") && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3))) {
            return true;
        }
        return false;
    }

    private boolean a_aud_is_mixable_visual_impaired_audio(TvTrackInfo t_audio_mpeg) {
        if (t_audio_mpeg.getExtra() == null) {
            return false;
        }
        String type = t_audio_mpeg.getExtra().getString("key_AudioType");
        String mixtype = t_audio_mpeg.getExtra().getString("key_AudioMixType");
        String eClass = t_audio_mpeg.getExtra().getString("key_AudioEditorialClass");
        String str = TAG;
        MtkLog.d(str, "a_aud_is_mixable_visual_impaired_audio  mixtype:" + mixtype + " eClass:" + eClass + " type:" + type);
        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(mixtype) || TextUtils.isEmpty(eClass)) {
            return false;
        }
        if (mixtype.equals("1")) {
            if (eClass.equals("2") || (eClass.equals("0") && type.equals(MtkTvRatingConvert2Goo.RATING_STR_3))) {
                return true;
            }
            return false;
        } else if (mixtype.equals("2") || !type.equals(MtkTvRatingConvert2Goo.RATING_STR_3)) {
            return false;
        } else {
            return true;
        }
    }

    public String getAudioTrackLangByIdNoNumbForNav(String trackId, List<TvProviderAudioTrackBase> tracks) {
        String str = TAG;
        MtkLog.d(str, "getAudioTrackLangById,trackId:" + trackId);
        int trackIdx = 0;
        if (tracks.size() != 1) {
            int i = 0;
            while (true) {
                if (i >= tracks.size()) {
                    break;
                } else if (String.valueOf(tracks.get(i).getAudioId()).equals(trackId)) {
                    trackIdx = i;
                    break;
                } else {
                    i++;
                }
            }
            String lang = tracks.get(trackIdx).getAudioLanguage();
            String str2 = TAG;
            MtkLog.d(str2, "lang:" + lang);
            return buildAudioLangStrByLang((trackIdx + 1) + "/" + tracks.size(), lang);
        }
        String lang2 = tracks.get(0).getAudioLanguage();
        String str3 = TAG;
        MtkLog.d(str3, "lang:" + lang2);
        return buildAudioLangStrByLang(lang2);
    }

    public String getAudioTrackLangByIdNoNumb(String trackId, List<TvTrackInfo> tracks) {
        String str = TAG;
        MtkLog.d(str, "getAudioTrackLangById,trackId:" + trackId);
        int trackIdx = 0;
        if (tracks.size() != 1) {
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
            String lang = tracks.get(trackIdx).getLanguage();
            String str2 = TAG;
            MtkLog.d(str2, "lang:" + lang);
            return buildAudioLangStrByLang((trackIdx + 1) + "/" + tracks.size(), lang);
        }
        String lang2 = tracks.get(0).getLanguage();
        String str3 = TAG;
        MtkLog.d(str3, "lang:" + lang2);
        return buildAudioLangStrByLang(lang2);
    }

    private String buildAudioLangStrByLang(String lang) {
        if (!TextUtils.isEmpty(lang)) {
            if (this.audioSpecialMap.containsKey(lang.toLowerCase())) {
                return this.audioSpecialMap.get(lang.toLowerCase());
            }
            LanguageUtil mLanguageUtil = new LanguageUtil(this.mContext);
            String str = TAG;
            MtkLog.d(str, "lang==" + new Locale(lang));
            return mLanguageUtil.getMtsNameByValue(lang);
        } else if (isCountryIndonesia()) {
            return this.mContext.getResources().getString(R.string.menu_string_undifined_audio);
        } else {
            return this.mContext.getResources().getString(R.string.menu_string_unknow_audio);
        }
    }

    private String buildAudioLangStrByLang(String pref, String lang) {
        if (!TextUtils.isEmpty(lang)) {
            if (this.audioSpecialMap.containsKey(lang.toLowerCase())) {
                if (MtkTvConfig.getInstance().getConfigValue("g_misc__ch_list_type") > 0) {
                    if (MtkTvConfigTypeBase.S639_CFG_LANG_QAA.equalsIgnoreCase(lang)) {
                        String str = TAG;
                        Log.d(str, "qaa: " + MtkTvCI.getInstance(Constants.slot_id).getProfileISO639LangCode());
                        this.audioSpecialMap.put(MtkTvConfigTypeBase.S639_CFG_LANG_QAA, MtkTvCI.getInstance(Constants.slot_id).getProfileISO639LangCode());
                    }
                    if (MtkTvConfigTypeBase.S639_CFG_LANG_UND.equalsIgnoreCase(lang)) {
                        String str2 = TAG;
                        Log.d(str2, "und: " + MtkTvCI.getInstance(Constants.slot_id).getProfileISO639LangCode());
                        this.audioSpecialMap.put(MtkTvConfigTypeBase.S639_CFG_LANG_UND, MtkTvCI.getInstance(Constants.slot_id).getProfileISO639LangCode());
                    }
                } else {
                    if (MtkTvConfigTypeBase.S639_CFG_LANG_QAA.equalsIgnoreCase(lang)) {
                        if (TVContent.getInstance(this.mContext).isFraCountry()) {
                            this.audioSpecialMap.put(MtkTvConfigTypeBase.S639_CFG_LANG_QAA, "V.O.");
                        } else {
                            this.audioSpecialMap.put(MtkTvConfigTypeBase.S639_CFG_LANG_QAA, "Original audio");
                        }
                    }
                    if (MtkTvConfigTypeBase.S639_CFG_LANG_UND.equalsIgnoreCase(lang)) {
                        this.audioSpecialMap.put(MtkTvConfigTypeBase.S639_CFG_LANG_UND, "Undefined audio");
                    }
                }
                return pref + " " + this.audioSpecialMap.get(lang.toLowerCase());
            }
            String[] split = lang.split(" \\+ ");
            LanguageUtil mLanguageUtil = new LanguageUtil(this.mContext);
            if (split.length == 2) {
                return pref + " " + mLanguageUtil.getMtsNameByValue(split[0].toLowerCase()) + " + " + mLanguageUtil.getMtsNameByValue(split[1].toLowerCase());
            }
            String str3 = TAG;
            MtkLog.d(str3, "lang===" + new Locale(lang));
            return pref + " " + mLanguageUtil.getMtsNameByValue(lang.toLowerCase());
        } else if (isCountryIndonesia()) {
            return pref + " " + this.mContext.getResources().getString(R.string.menu_string_undifined_audio);
        } else {
            return pref + " " + this.mContext.getResources().getString(R.string.menu_string_unknow_audio);
        }
    }

    public int setNextAudioLang() {
        MtkLog.d(TAG, "setNextAudioLang");
        if (!MarketRegionInfo.isFunctionSupport(20)) {
            return navMtkTvAVMode.setNextAudioLang();
        }
        if ("main" == this.mCommonIntegration.getCurrentFocus()) {
            List<TvTrackInfo> filterTracks = filterAudioTracks(TurnkeyUiMainActivity.getInstance().getTvView().getTracks(0));
            String currentID = TurnkeyUiMainActivity.getInstance().getTvView().getSelectedTrack(0);
            String nextSelectID = getNextTrackID(currentID, filterTracks);
            if (nextSelectID == null || nextSelectID.equalsIgnoreCase(currentID)) {
                return -1;
            }
            MtkLog.d(TAG, "come in setNextAudioLang with TIF,focus win main to set");
            TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(0, nextSelectID);
            return -1;
        }
        List<TvTrackInfo> filterTracks2 = filterAudioTracks(TurnkeyUiMainActivity.getInstance().getPipView().getTracks(0));
        String currentID2 = TurnkeyUiMainActivity.getInstance().getPipView().getSelectedTrack(0);
        String nextSelectID2 = getNextTrackID(currentID2, filterTracks2);
        if (nextSelectID2 == null || nextSelectID2.equalsIgnoreCase(currentID2)) {
            return -1;
        }
        MtkLog.d(TAG, "come in setNextAudioLang with TIF,focus win sub to set");
        TurnkeyUiMainActivity.getInstance().getPipView().selectTrack(0, nextSelectID2);
        return -1;
    }

    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateFormat.is24HourFormat(this.mContext) ? "HH:mm:ss" : "hh:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = TAG;
        MtkLog.d(str, "current system time :" + formatter.format(curDate));
        return formatter.format(curDate);
    }

    public boolean isCountryIndonesia() {
        return MtkTvConfig.getInstance().getCountry().equalsIgnoreCase(MtkTvConfigTypeBase.S3166_CFG_COUNT_IDN);
    }

    public int getCurrentScreenMode() {
        return navMtkTvAVMode.getScreenMode();
    }

    public int setCurrentScreenMode(int mode) {
        return navMtkTvAVMode.setScreenMode(mode);
    }

    public void setVideoMute(boolean isMute) {
        this.mtkTvAppTv.setVideoMute("main", isMute);
    }

    public int[] getSupportScreenModes() {
        return navMtkTvAVMode.getAllScreenMode();
    }

    public int getCurrentPictureMode() {
        return navMtkTvAVMode.getPictureMode();
    }

    public int setCurrentPictureMode(int mode) {
        return navMtkTvAVMode.setPictureMode(mode);
    }

    public int[] getSupportPictureModes() {
        return navMtkTvAVMode.getAllPictureMode();
    }

    public int getCurrentSoundEffect() {
        return navMtkTvAVMode.getSoundEffect();
    }

    public int setCurrentSoundEffect(int mode) {
        return navMtkTvAVMode.setSoundEffect(mode);
    }

    public int[] getSupportSoundEffects() {
        return navMtkTvAVMode.getAllSoundEffect();
    }

    public boolean isFreeze() {
        if (MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_TV_FOCUS_WIN) == 0) {
            return navMtkTvAVMode.isFreeze("main");
        }
        return navMtkTvAVMode.isFreeze("sub");
    }

    public int setFreeze(boolean isFreeze) {
        if (MtkTvConfig.getInstance().getConfigValue(MtkTvConfigTypeBase.CFG_PIP_POP_TV_FOCUS_WIN) == 0) {
            return navMtkTvAVMode.setFreeze("main", isFreeze);
        }
        return navMtkTvAVMode.setFreeze("sub", isFreeze);
    }

    public boolean isGingaWindowResize() {
        boolean result = navMtkTvGinga.isGingaWindowResize();
        String str = TAG;
        MtkLog.d(str, "isGingaWindowResize:" + result);
        return result;
    }

    public boolean getInternalScrnMode() {
        boolean result = navMtkTvMHEG5.getInternalScrnMode();
        String str = TAG;
        MtkLog.d(str, "getInternalScrnMode:" + result);
        return result;
    }

    private String getNextTrackID(String currentSelectID, List<TvTrackInfo> currentTrackList) {
        MtkLog.d(TAG, "come in getNextTrackID, the currentSelectID = " + currentSelectID);
        if (currentTrackList != null) {
            for (int i = 0; i < currentTrackList.size(); i++) {
                MtkLog.d(TAG, "come in getNextTrackID, the ID = " + currentTrackList.get(i).getId());
            }
        }
        if (!(currentSelectID == null || currentTrackList == null)) {
            for (int i2 = 0; i2 < currentTrackList.size(); i2++) {
                if (currentSelectID.equals(currentTrackList.get(i2).getId())) {
                    MtkLog.d(TAG, "come in getNextTrackID, the i = " + i2);
                    if (i2 + 1 < currentTrackList.size()) {
                        return currentTrackList.get(i2 + 1).getId();
                    }
                    return currentTrackList.get(0).getId();
                }
            }
        }
        MtkLog.d(TAG, "come in getNextTrackID, the nextID = " + null);
        return null;
    }
}
