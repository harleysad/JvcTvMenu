package com.mediatek.wwtv.tvcenter.util.tif;

import android.support.media.tv.TvContractCompat;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvEventComom;
import com.mediatek.twoworlds.tv.model.MtkTvEventGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.wwtv.tvcenter.epg.DataReader;
import com.mediatek.wwtv.tvcenter.epg.EPGChannelInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo;
import com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TIFFunctionUtil {
    public static final int CH_CONFIRM_REMOVE_MASK = MtkTvChCommonBase.SB_VNET_REMOVAL_TO_CONFIRM;
    public static final int CH_CONFIRM_REMOVE_VAL = MtkTvChCommonBase.SB_VNET_REMOVAL_TO_CONFIRM;
    public static final int CH_FAKE_MASK = MtkTvChCommonBase.SB_VNET_FAKE;
    public static final int CH_FAKE_VAL = MtkTvChCommonBase.SB_VNET_FAKE;
    public static final int CH_LIST_ANALOG_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_ANALOG_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_DIGITAL_MASK = (((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE) | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
    public static final int CH_LIST_DIGITAL_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_EPG_MASK = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_LIST_EPG_US_MASK = (MtkTvChCommonBase.SB_VNET_EPG | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_LIST_EPG_US_VAL = MtkTvChCommonBase.SB_VNET_EPG;
    public static final int CH_LIST_EPG_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_LIST_MASK = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_LIST_RADIO_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE) | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE);
    public static final int CH_LIST_RADIO_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_RADIO_SERVICE);
    public static final int CH_LIST_VAL = MtkTvChCommonBase.SB_VNET_ACTIVE;
    public static final int CH_MASK_ALL = MtkTvChCommonBase.SB_VNET_ALL;
    public static final int CH_SCRAMBLED_MASK = MtkTvChCommonBase.SB_VNET_SCRAMBLED;
    public static final int CH_SCRAMBLED_VAL = MtkTvChCommonBase.SB_VNET_SCRAMBLED;
    public static final int CH_UP_DOWN_MASK = ((MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE) | MtkTvChCommonBase.SB_VNET_FAKE);
    public static final int CH_UP_DOWN_VAL = (MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_VISIBLE);
    private static final String CUR_CHANNEL_ID = "g_nav__air_crnt_ch";
    public static final String EXTRA_ACTIVITY_AFTER_COMPLETION = "com.android.tv.intent.extra.ACTIVITY_AFTER_COMPLETION";
    public static final String EXTRA_APP_LINK_CHANNEL_URI = "app_link_channel_uri";
    public static final String EXTRA_INPUT_ID = "com.android.tv.intent.extra.INPUT_ID";
    public static final String INTENT_ACTION_INPUT_SETUP = "com.android.tv.intent.action.INPUT_SETUP";
    private static final String TAG = "TIFFunctionUtil";
    public static final int channelDataValuelength = 9;
    public static String channelType = TvContractCompat.Channels.TYPE_PREVIEW;
    public static String current3rdMId = "current_3rd_channel_mId";
    private static int mCurCategories = -1;
    public static final int sourceATVSvlId = 2;

    public static int getCurrentChannelId() {
        int chId = MtkTvConfig.getInstance().getConfigValue("g_nav__air_crnt_ch");
        MtkLog.d(TAG, "getCurrentChannelId chId = " + chId);
        return chId;
    }

    public static boolean checkChMask(MtkTvChannelInfoBase chinfo, int attentionMask, int expectValue) {
        CommonIntegration.getInstance();
        if (!CommonIntegration.isEUPARegion()) {
            CommonIntegration.getInstance();
            if (!CommonIntegration.isCNRegion()) {
                if (chinfo != null && (!chinfo.isUserDelete() || (chinfo.isUserDelete() && CommonIntegration.isSARegion()))) {
                    MtkLog.d(TAG, "checkChMask chinfo.getNwMask() = " + chinfo.getNwMask() + "atentionMask=" + attentionMask + " expectValue=" + expectValue);
                    if ((chinfo.getNwMask() & attentionMask) == expectValue) {
                        MtkLog.d(TAG, "checkChMask true");
                        return true;
                    }
                }
                MtkLog.d(TAG, "checkChMask false");
                return false;
            }
        }
        if (CommonIntegration.getInstance().isCurrentSourceDTVforEuPA()) {
            if (chinfo == null || chinfo.isUserDelete() || chinfo.isAnalogService()) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask chinfo.getNwMask() = " + chinfo.getNwMask() + "atentionMask=" + attentionMask + " expectValue=" + expectValue);
            if ((chinfo.getNwMask() & attentionMask) != expectValue) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask true");
            return true;
        } else if (!CommonIntegration.getInstance().isCurrentSourceATVforEuPA() || chinfo == null || chinfo.isUserDelete() || !chinfo.isAnalogService()) {
            return false;
        } else {
            MtkLog.d(TAG, "checkChMask chinfo.getNwMask() = " + chinfo.getNwMask() + "atentionMask=" + attentionMask + " expectValue=" + expectValue);
            if ((chinfo.getNwMask() & attentionMask) != expectValue) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask true");
            return true;
        }
    }

    public static boolean checkChCategoryMask(MtkTvChannelInfoBase info, int CategoryMask, int CategoryValue) {
        if (!(info instanceof MtkTvDvbChannelInfo)) {
            return true;
        }
        MtkTvDvbChannelInfo chinfo = (MtkTvDvbChannelInfo) info;
        if (chinfo == null) {
            return false;
        }
        if (chinfo.isUserDelete() && (!chinfo.isUserDelete() || !CommonIntegration.isSARegion())) {
            return false;
        }
        if (chinfo == null || CategoryMask <= 0) {
            if (chinfo != null && CategoryMask == 0) {
                MtkLog.d(TAG, "checkChCategoryMask chinfo.getChannelNumber() = " + chinfo.getChannelNumber() + " CategoryMask = " + CategoryMask + " CategoryValue = " + CategoryValue);
                if (chinfo.getChannelNumber() >= 2000) {
                    MtkLog.d(TAG, "checkChMask true");
                    return true;
                }
            } else if (chinfo != null && CategoryMask == -1) {
                MtkLog.d(TAG, "checkChCategoryMask true");
                return true;
            }
        } else if ((chinfo.getCategoryMask() & CategoryMask) == CategoryValue) {
            MtkLog.d(TAG, "checkChMask true");
            return true;
        }
        MtkLog.d(TAG, "checkChCategoryMask false");
        return false;
    }

    public static boolean checkChMask(TIFChannelInfo chinfo, int attentionMask, int expectValue) {
        if (chinfo == null || chinfo.mMtkTvChannelInfo == null || (chinfo.mMtkTvChannelInfo.getNwMask() & attentionMask) != expectValue) {
            MtkLog.d(TAG, "checkChMask false");
            return false;
        }
        MtkLog.d(TAG, "checkChMask true");
        return true;
    }

    public static boolean checkChMaskformDataValue(TIFChannelInfo chinfo, int attentionMask, int expectValue) {
        CommonIntegration.getInstance();
        if (!CommonIntegration.isEUPARegion()) {
            CommonIntegration.getInstance();
            if (!CommonIntegration.isCNRegion()) {
                if (chinfo != null && (!chinfo.isUserDelete() || (chinfo.isUserDelete() && CommonIntegration.isSARegion()))) {
                    MtkLog.d(TAG, "checkChMask chinfo.mData" + chinfo.mData);
                    if (chinfo.mDataValue != null && chinfo.mDataValue.length == 9 && (chinfo.mDataValue[6] & ((long) attentionMask)) == ((long) expectValue)) {
                        MtkLog.d(TAG, "checkChMask true");
                        return true;
                    }
                }
                MtkLog.d(TAG, "checkChMask false");
                return false;
            }
        }
        if (CommonIntegration.getInstance().isCurrentSourceDTVforEuPA()) {
            if (chinfo == null || chinfo.isUserDelete() || chinfo.isAnalogService()) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask chinfo.mData" + chinfo.mData);
            if (chinfo.mDataValue == null || chinfo.mDataValue.length != 9 || (chinfo.mDataValue[6] & ((long) attentionMask)) != ((long) expectValue)) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask true");
            return true;
        } else if (!CommonIntegration.getInstance().isCurrentSourceATVforEuPA() || chinfo == null || chinfo.isUserDelete() || !chinfo.isAnalogService()) {
            return false;
        } else {
            MtkLog.d(TAG, "checkChMask chinfo.mData" + chinfo.mData);
            if (chinfo.mDataValue == null || chinfo.mDataValue.length != 9 || (chinfo.mDataValue[6] & ((long) attentionMask)) != ((long) expectValue)) {
                return false;
            }
            MtkLog.d(TAG, "checkChMask true");
            return true;
        }
    }

    public static List<EPGChannelInfo> getEpgChannelProgramsGroup(List<EPGChannelInfo> channelList, Map<Long, List<TIFProgramInfo>> programMapList, long startTime) {
        long endTime;
        Iterator<EPGChannelInfo> it;
        long endTime2;
        Iterator<EPGChannelInfo> it2;
        Iterator<TIFProgramInfo> it3;
        long endTime3;
        List<TIFProgramInfo> tempProgramList;
        Iterator<EPGChannelInfo> it4;
        EPGProgramInfo mainEpgProgramInfo;
        long j = startTime;
        long duration = 7200;
        long endTime4 = j + 7200;
        MtkLog.d(TAG, "startTime>>" + j + "   endTime>>" + endTime4);
        Map<Integer, EPGProgramInfo> mapEPGChannelInfo = new HashMap<>();
        Iterator<EPGChannelInfo> it5 = channelList.iterator();
        while (it5.hasNext()) {
            EPGChannelInfo tempChannelInfo = it5.next();
            List<TIFProgramInfo> tempProgramList2 = programMapList.get(Long.valueOf(tempChannelInfo.mId));
            MtkLog.d(TAG, "***************************tempChannelInfo.name=" + tempChannelInfo.getName() + "*****************************");
            StringBuilder sb = new StringBuilder();
            sb.append("tempProgramList>>");
            long duration2 = duration;
            sb.append(tempChannelInfo.mId);
            sb.append("  ");
            sb.append(tempProgramList2 == null ? tempProgramList2 : Integer.valueOf(tempProgramList2.size()));
            MtkLog.d(TAG, sb.toString());
            ArrayList arrayList = new ArrayList();
            if (tempProgramList2 == null) {
                tempChannelInfo.setmTVProgramInfoList(arrayList);
                endTime2 = endTime;
                it = it5;
            } else {
                Iterator<TIFProgramInfo> it6 = tempProgramList2.iterator();
                while (it6.hasNext()) {
                    TIFProgramInfo tempProgramInfo = it6.next();
                    MtkLog.d(TAG, "tempProgramInfo.mStartTimeUtcSec>>" + tempProgramInfo.mStartTimeUtcSec + "   " + tempProgramInfo.mEndTimeUtcSec);
                    if (tempProgramInfo.mEndTimeUtcSec <= j || tempProgramInfo.mStartTimeUtcSec >= endTime || containsEvent(arrayList, tempProgramInfo)) {
                        tempProgramList = tempProgramList2;
                        it3 = it6;
                        endTime3 = endTime;
                        it2 = it5;
                    } else {
                        MtkLog.d(TAG, "start creat epg evevt info>>>" + tempChannelInfo.getTVChannel().getChannelId() + "   " + tempProgramInfo.mEventId);
                        it3 = it6;
                        endTime3 = endTime;
                        EPGProgramInfo tempEPGinfo = new EPGProgramInfo(tempChannelInfo.getTVChannel().getChannelId(), tempProgramInfo.mEventId, tempProgramInfo.mStartTimeUtcSec, tempProgramInfo.mEndTimeUtcSec, CommonIntegration.getInstance().getAvailableString(tempProgramInfo.mTitle), tempProgramInfo.mRating);
                        MtkLog.d(TAG, "mEventId=" + tempProgramInfo.mEventId + ",ChannelId=" + tempChannelInfo.getTVChannel().getChannelId());
                        MtkTvEventInfoBase apiEPGInfo = MtkTvEvent.getInstance().getEventInfoByEventId(tempProgramInfo.mEventId, tempChannelInfo.getTVChannel().getChannelId());
                        if (apiEPGInfo == null) {
                            endTime = endTime3;
                            it6 = it3;
                        } else if (apiEPGInfo.getStartTime() == 0) {
                            endTime = endTime3;
                            it6 = it3;
                        } else {
                            MtkLog.d(TAG, "apiEPGInfo>>>" + apiEPGInfo.getEventRating() + "  event.getStartTime()>>>" + apiEPGInfo.getStartTime() + "   " + apiEPGInfo.getDuration());
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("----------------tempProgramInfo.mTitle=");
                            sb2.append(tempProgramInfo.mTitle);
                            sb2.append("------------------------------------------------");
                            MtkLog.d(TAG, sb2.toString());
                            MtkTvEventGroupBase[] mtkTvEventGroups = apiEPGInfo.getEventGroup();
                            if (mtkTvEventGroups != null && mtkTvEventGroups.length > 0) {
                                int length = mtkTvEventGroups.length;
                                int i = 0;
                                while (i < length) {
                                    MtkTvEventGroupBase mtkTvEventGroup = mtkTvEventGroups[i];
                                    int eventType = mtkTvEventGroup.geteventType();
                                    int i2 = length;
                                    List<TIFProgramInfo> tempProgramList3 = tempProgramList2;
                                    StringBuilder sb3 = new StringBuilder();
                                    MtkTvEventGroupBase[] mtkTvEventGroups2 = mtkTvEventGroups;
                                    sb3.append("eventType=");
                                    sb3.append(eventType);
                                    MtkLog.d(TAG, sb3.toString());
                                    if (eventType != 0) {
                                        if (eventType == 1) {
                                            mapEPGChannelInfo.put(Integer.valueOf(tempProgramInfo.mEventId), tempEPGinfo);
                                        }
                                        MtkTvEventComom[] eventComoms = mtkTvEventGroup.geteventCommom();
                                        if (eventComoms != null && eventComoms.length != 0) {
                                            int length2 = eventComoms.length;
                                            int i3 = 0;
                                            while (true) {
                                                if (i3 >= length2) {
                                                    break;
                                                }
                                                MtkTvEventGroupBase mtkTvEventGroup2 = mtkTvEventGroup;
                                                MtkTvEventComom eventComom = eventComoms[i3];
                                                MtkTvEventComom[] eventComoms2 = eventComoms;
                                                int i4 = length2;
                                                StringBuilder sb4 = new StringBuilder();
                                                it4 = it5;
                                                sb4.append("eventCommom.channelId=");
                                                sb4.append(eventComom.getChannelId());
                                                sb4.append(",eventCommom.eventId=");
                                                sb4.append(eventComom.getEventId());
                                                MtkLog.d(TAG, sb4.toString());
                                                if (tempProgramInfo.mEventId == eventComom.getEventId() && eventType == 2 && (mainEpgProgramInfo = mapEPGChannelInfo.get(Integer.valueOf(eventComom.getEventId()))) != null) {
                                                    tempEPGinfo.setmTitle(mainEpgProgramInfo.getmTitle());
                                                    break;
                                                }
                                                i3++;
                                                mtkTvEventGroup = mtkTvEventGroup2;
                                                eventComoms = eventComoms2;
                                                length2 = i4;
                                                it5 = it4;
                                            }
                                            i++;
                                            length = i2;
                                            tempProgramList2 = tempProgramList3;
                                            mtkTvEventGroups = mtkTvEventGroups2;
                                            it5 = it4;
                                        }
                                    }
                                    it4 = it5;
                                    i++;
                                    length = i2;
                                    tempProgramList2 = tempProgramList3;
                                    mtkTvEventGroups = mtkTvEventGroups2;
                                    it5 = it4;
                                }
                            }
                            MtkTvEventGroupBase[] mtkTvEventGroupBaseArr = mtkTvEventGroups;
                            it2 = it5;
                            MtkLog.d(TAG, "-----------------------------------------------------------------");
                            tempEPGinfo.setProgramBlock(MtkTvEvent.getInstance().checkEventBlock(tempChannelInfo.getTVChannel().getChannelId(), apiEPGInfo.getEventId()));
                            MtkLog.d(TAG, "tvEvent.checkEventBlock:" + tempEPGinfo.isProgramBlock());
                            tempEPGinfo.setCategoryType(apiEPGInfo.getEventCategory());
                            tempEPGinfo.setHasSubTitle(apiEPGInfo.isCaption());
                            DataReader.getInstance().setMStype(tempEPGinfo, apiEPGInfo.getEventCategory());
                            tempEPGinfo.setRatingType(apiEPGInfo.getEventRating());
                            tempEPGinfo.setRatingValue(apiEPGInfo.getEventRatingType());
                            tempEPGinfo.setDescribe(CommonIntegration.getInstance().getAvailableString(DataReader.getInstance().getResultDetail(apiEPGInfo.getGuidanceText(), tempProgramInfo.mDescription, tempProgramInfo.mLongDescription)));
                            TIFProgramInfo tIFProgramInfo = tempProgramInfo;
                            tempProgramList = tempProgramList2;
                            tempEPGinfo.setmScale(DataReader.getInstance().getProWidth(tempEPGinfo, j, duration2));
                            if (arrayList.size() == 0) {
                                float mLeftMargin = EPGTimeConvert.countShowWidth(tempEPGinfo.getmStartTime().longValue(), j);
                                MtkLog.d(TAG, "mLeftMargin?>i == 0>>" + tempEPGinfo.getmStartTime() + "   " + j + "   " + mLeftMargin);
                                if (mLeftMargin > 0.0f) {
                                    tempEPGinfo.setLeftMargin(mLeftMargin);
                                } else {
                                    tempEPGinfo.setLeftMargin(0.0f);
                                }
                            } else {
                                tempEPGinfo.setLeftMargin(DataReader.getInstance().getProLeftMargin(tempEPGinfo, (EPGProgramInfo) arrayList.get(arrayList.size() - 1), tempEPGinfo));
                            }
                            arrayList.add(tempEPGinfo);
                        }
                    }
                    tempProgramList2 = tempProgramList;
                    endTime = endTime3;
                    it6 = it3;
                    it5 = it2;
                }
                endTime2 = endTime;
                it = it5;
                tempChannelInfo.setmTVProgramInfoList(arrayList);
                MtkLog.d(TAG, "***********************************************************************");
            }
            ArrayList arrayList2 = arrayList;
            duration = duration2;
            endTime4 = endTime2;
            it5 = it;
        }
        Map<Long, List<TIFProgramInfo>> map = programMapList;
        long j2 = duration;
        long j3 = endTime;
        return channelList;
    }

    public static List<EPGChannelInfo> getEpgChannelProgramsGroupEx(List<EPGChannelInfo> channelList, Map<Long, List<TIFProgramInfo>> programMapList, long startTime) {
        long endTime;
        List<EPGProgramInfo> ePGProgramList;
        long endTime2;
        Iterator<TIFProgramInfo> it;
        List<EPGProgramInfo> ePGProgramList2;
        long j = startTime;
        long endTime3 = j + 7200;
        MtkLog.d(TAG, "startTime>>" + j + "   endTime>>" + endTime3);
        for (EPGChannelInfo tempChannelInfo : channelList) {
            List<TIFProgramInfo> tempProgramList = programMapList.get(Long.valueOf(tempChannelInfo.mId));
            StringBuilder sb = new StringBuilder();
            sb.append("getEpgChannelProgramsGroupEx,");
            sb.append(tempChannelInfo.mId);
            sb.append("  ");
            sb.append(tempProgramList == null ? tempProgramList : Integer.valueOf(tempProgramList.size()));
            MtkLog.d(TAG, sb.toString());
            List<EPGProgramInfo> ePGProgramList3 = new ArrayList<>();
            if (tempProgramList == null) {
                tempChannelInfo.setmTVProgramInfoList(ePGProgramList3);
                ePGProgramList = ePGProgramList3;
                endTime = endTime3;
            } else {
                Iterator<TIFProgramInfo> it2 = tempProgramList.iterator();
                while (it2.hasNext()) {
                    TIFProgramInfo tempProgramInfo = it2.next();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("getEpgChannelProgramsGroupEx, StartTime:");
                    List<TIFProgramInfo> tempProgramList2 = tempProgramList;
                    sb2.append(tempProgramInfo.mStartTimeUtcSec);
                    sb2.append(",");
                    sb2.append(tempProgramInfo.mEndTimeUtcSec);
                    MtkLog.d(TAG, sb2.toString());
                    if (tempProgramInfo.mEndTimeUtcSec < j || tempProgramInfo.mStartTimeUtcSec > endTime3 || containsEvent(ePGProgramList3, tempProgramInfo)) {
                        ePGProgramList2 = ePGProgramList3;
                        it = it2;
                        endTime2 = endTime3;
                    } else {
                        endTime2 = endTime3;
                        EPGProgramInfo tempEPGinfo = new EPGProgramInfo((int) tempChannelInfo.mId, tempProgramInfo.mEventId, tempProgramInfo.mStartTimeUtcSec, tempProgramInfo.mEndTimeUtcSec, tempProgramInfo.mTitle, tempProgramInfo.mRating);
                        tempEPGinfo.setRatingType(tempProgramInfo.getmRating());
                        tempEPGinfo.setDescribe(tempProgramInfo.getmDescription());
                        TIFProgramInfo tIFProgramInfo = tempProgramInfo;
                        ePGProgramList2 = ePGProgramList3;
                        it = it2;
                        tempEPGinfo.setmScale(DataReader.getInstance().getProWidth(tempEPGinfo, j, 7200));
                        if (ePGProgramList2.size() == 0) {
                            float mLeftMargin = EPGTimeConvert.countShowWidth(tempEPGinfo.getmStartTime().longValue(), j);
                            MtkLog.d(TAG, "mLeftMargin?>i == 0>>" + tempEPGinfo.getmStartTime() + "," + j + "," + mLeftMargin);
                            if (mLeftMargin > 0.0f) {
                                tempEPGinfo.setLeftMargin(mLeftMargin);
                            } else {
                                tempEPGinfo.setLeftMargin(0.0f);
                            }
                        } else {
                            tempEPGinfo.setLeftMargin(DataReader.getInstance().getProLeftMargin(tempEPGinfo, ePGProgramList2.get(ePGProgramList2.size() - 1), tempEPGinfo));
                        }
                        ePGProgramList2.add(tempEPGinfo);
                    }
                    ePGProgramList3 = ePGProgramList2;
                    it2 = it;
                    tempProgramList = tempProgramList2;
                    endTime3 = endTime2;
                    Map<Long, List<TIFProgramInfo>> map = programMapList;
                }
                ePGProgramList = ePGProgramList3;
                endTime = endTime3;
                List<TIFProgramInfo> list = tempProgramList;
                tempChannelInfo.setmTVProgramInfoList(ePGProgramList);
            }
            List<EPGProgramInfo> list2 = ePGProgramList;
            endTime3 = endTime;
        }
        return channelList;
    }

    public static boolean containsEvent(List<EPGProgramInfo> ePGProgramList, TIFProgramInfo tempEPGinfo) {
        if (ePGProgramList == null) {
            return false;
        }
        for (EPGProgramInfo info : ePGProgramList) {
            if (info.getmStartTime().longValue() == tempEPGinfo.mStartTimeUtcSec && info.getmEndTime().longValue() == tempEPGinfo.mEndTimeUtcSec) {
                return true;
            }
        }
        return false;
    }

    public static List<MtkTvChannelInfoBase> getApiChannelList(List<TIFChannelInfo> tifChannelList) {
        List<MtkTvChannelInfoBase> chlist = new ArrayList<>();
        if (tifChannelList == null || tifChannelList.size() == 0) {
            return chlist;
        }
        for (TIFChannelInfo tempTifChannel : tifChannelList) {
            chlist.add(tempTifChannel.mMtkTvChannelInfo);
        }
        return chlist;
    }

    public static List<TIFChannelInfo> getTIFChannelList(List<MtkTvChannelInfoBase> apiChannelList) {
        List<TIFChannelInfo> chlist = new ArrayList<>();
        if (apiChannelList == null || apiChannelList.size() == 0) {
            return chlist;
        }
        for (MtkTvChannelInfoBase tempApiChannel : apiChannelList) {
            TIFChannelInfo tempTIFChInfo = new TIFChannelInfo();
            tempTIFChInfo.mMtkTvChannelInfo = tempApiChannel;
            chlist.add(tempTIFChInfo);
        }
        return chlist;
    }

    public static List<MtkTvChannelInfoBase> getApiChannelListFromEpgChannel(List<EPGChannelInfo> epgChannelList) {
        List<MtkTvChannelInfoBase> chlist = new ArrayList<>();
        for (EPGChannelInfo tempEpgChannel : epgChannelList) {
            chlist.add(tempEpgChannel.getTVChannel());
        }
        return chlist;
    }

    public static void setActivityWindow(List<MtkTvChannelInfoBase> channels, long startTime) {
        MtkTvEvent.getInstance().setCurrentActiveWindows(channels, startTime);
    }

    public static void setActivityWindow(List<MtkTvChannelInfoBase> channels, long startTime, long durtion) {
        MtkTvEvent.getInstance().setCurrentActiveWindows(channels, startTime, durtion);
    }

    public static List<MtkTvEventInfoBase> getEpgEventListByChannelId(int channelId, long startTime, long duratuon) throws Exception {
        return MtkTvEvent.getInstance().getEventListByChannelId(channelId, startTime, duratuon);
    }

    public static boolean isEUPARegion() {
        CommonIntegration.getInstance();
        return CommonIntegration.isEUPARegion();
    }

    public static void setmCurCategories(int curCategories) {
        mCurCategories = curCategories;
        MtkLog.d(TAG, "setmCurCategories," + mCurCategories);
    }

    public static int getmCurCategories() {
        MtkLog.d(TAG, "getmCurCategories," + mCurCategories);
        return mCurCategories;
    }
}
