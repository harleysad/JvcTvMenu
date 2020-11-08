package com.mediatek.wwtv.tvcenter.epg;

import android.content.Context;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvPWDDialog;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MarketRegionInfo;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataReader {
    public static final int PER_PAGE_CHANNEL_NUMBER = 6;
    private static final String TAG = "DataReader";
    private static DataReader dtReader;
    private CommonIntegration integration;
    List<EPGChannelInfo> mChannelList = new ArrayList();
    private Context mContext;
    private String[] mMonthFull;
    private String[] mMonthSimple;
    private List<TIFChannelInfo> mTIFChannelInfoList;
    private TIFChannelManager mTIFChannelManager;
    private Map<Long, List<TIFProgramInfo>> mTIFProgramInfoMapList;
    private TIFProgramManager mTIFProgramManager;
    private String[] mType;
    private String[] mWeekFull;
    private String[] mWeekSimple;
    private String[][] sType;
    private EPGTimeConvert tmCvt;
    private List<MtkTvChannelInfoBase> tvChannelList;
    private MtkTvEvent tvEvent;

    private DataReader(Context context) {
        this.mContext = context;
        this.tmCvt = EPGTimeConvert.getInstance();
        this.tvEvent = MtkTvEvent.getInstance();
        this.integration = CommonIntegration.getInstanceWithContext(context.getApplicationContext());
        this.mTIFChannelManager = TIFChannelManager.getInstance(this.mContext);
        this.mTIFProgramManager = TIFProgramManager.getInstance(this.mContext);
        loadProgramType();
        loadMonthAndWeekRes();
    }

    public static synchronized DataReader getInstance(Context context) {
        DataReader dataReader;
        synchronized (DataReader.class) {
            if (dtReader == null) {
                dtReader = new DataReader(context);
            }
            dataReader = dtReader;
        }
        return dataReader;
    }

    public static DataReader getInstance() {
        if (dtReader == null) {
            dtReader = new DataReader(DestroyApp.appContext);
        }
        return dtReader;
    }

    public static void reset() {
        dtReader = null;
    }

    public String mapRating2CustomerStr(int ratingValue, String strRating) {
        return this.integration.mapRating2CustomerStr(ratingValue, strRating);
    }

    public List<EPGChannelInfo> setActiveWindow(List<EPGChannelInfo> channels, int dayNum, int startHour) {
        long startTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startHour);
        MtkLog.d(TAG, "setActiveWindow>>startHour>" + dayNum + "  startHour>" + startHour + "   startTime>" + startTime);
        for (EPGChannelInfo iiiii : channels) {
            MtkLog.d(TAG, "setActiveWindow>>" + iiiii.mId + "  " + iiiii.getTVChannel().getChannelId() + "  " + iiiii.getName());
        }
        if (CommonIntegration.isSARegion()) {
            TIFFunctionUtil.setActivityWindow(this.tvChannelList, startTime);
        } else {
            TIFFunctionUtil.setActivityWindow(TIFFunctionUtil.getApiChannelListFromEpgChannel(channels), startTime);
        }
        return channels;
    }

    public List<EPGChannelInfo> setActiveWindow(EPGChannelInfo channel, int dayNum, int startHour, int hourDurtion) {
        List<EPGChannelInfo> channels = new ArrayList<>();
        channels.add(channel);
        long startTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startHour);
        MtkLog.d(TAG, "setActiveWindow>>dayNum>" + dayNum + "  startHour>" + startHour + "   startTime>" + startTime + ",hourDurtion=" + hourDurtion);
        TIFFunctionUtil.setActivityWindow(TIFFunctionUtil.getApiChannelListFromEpgChannel(channels), startTime, (long) (hourDurtion * 60 * 60 * 1000));
        return channels;
    }

    public long getStartTime(int dayNum, int startHour) {
        return this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startHour);
    }

    public List<EPGProgramInfo> getProgramListByChId(EPGChannelInfo channelInfo, int dayNum, int startHour, int hourDurtion) {
        int i = startHour;
        int i2 = hourDurtion;
        StringBuilder sb = new StringBuilder();
        sb.append("getProgramListByChId----->dayNum=");
        int i3 = dayNum;
        sb.append(i3);
        sb.append(",startHour=");
        sb.append(i);
        sb.append(",hourDurtion=");
        sb.append(i2);
        MtkLog.d(TAG, sb.toString());
        long startTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), i3, (long) i) * 1000;
        long endTime = ((long) (i2 * 60 * 60 * 1000)) + startTime;
        int mtkChId = -1;
        long chId = channelInfo.mId;
        MtkLog.d(TAG, "mtkChId=" + -1 + ",chId=" + chId + ",startTime=" + startTime + ",endTime=" + endTime);
        if (channelInfo.getTVChannel() != null) {
            mtkChId = channelInfo.getTVChannel().getChannelId();
        }
        long j = chId;
        return this.mTIFProgramManager.queryProgramByChannelId(mtkChId, chId, startTime, endTime);
    }

    public List<EPGChannelInfo> readProgramInfoByTIF(List<EPGChannelInfo> channels, int dayNum, int startHour) {
        long startTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startHour);
        this.mTIFProgramInfoMapList = this.mTIFProgramManager.queryProgramListWithGroupByChannelId(channels, startTime);
        if (this.integration.is3rdTVSource()) {
            TIFFunctionUtil.getEpgChannelProgramsGroupEx(channels, this.mTIFProgramInfoMapList, startTime);
        } else {
            TIFFunctionUtil.getEpgChannelProgramsGroup(channels, this.mTIFProgramInfoMapList, startTime);
        }
        return channels;
    }

    public void readChannelProgramInfoByTime(List<EPGChannelInfo> chList, int dayNum, int startTime, int mTimeSpan) {
        long sTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startTime);
        int size = chList.size();
        MtkTvChannelInfoBase[] channels = new MtkTvChannelInfoBase[size];
        for (int i = 0; i < size; i++) {
            channels[i] = chList.get(i).getTVChannel();
        }
        List<EPGChannelInfo> list = chList;
        int chIdx = 0;
        Iterator<EPGChannelInfo> it = chList.iterator();
        while (it.hasNext()) {
            long duration = this.tmCvt.getHourtoMsec(mTimeSpan);
            StringBuilder sb = new StringBuilder();
            sb.append("set start time : [");
            Iterator<EPGChannelInfo> it2 = it;
            sb.append(EPGUtil.getCurrentTime());
            sb.append("]duration>>");
            sb.append(duration);
            MtkLog.d(TAG, sb.toString());
            MtkLog.d(TAG, "set start time : [dayNum:" + dayNum + "][startTime:" + sTime + "]");
            new ArrayList();
            long j = duration;
            it.next().setmTVProgramInfoList(readChannelProgramInfoByTime(channels[chIdx], sTime, duration));
            it = it2;
            chIdx++;
            List<EPGChannelInfo> list2 = chList;
        }
        int i2 = dayNum;
        int i3 = mTimeSpan;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0286  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo> readChannelProgramInfoByTime(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r34, long r35, long r37) {
        /*
            r33 = this;
            r1 = r33
            r8 = r35
            r10 = r37
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            java.lang.String r0 = "DataReader"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "--- set time : ["
            r2.append(r3)
            r2.append(r8)
            java.lang.String r3 = ","
            r2.append(r3)
            r2.append(r10)
            java.lang.String r3 = "]"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r2)
            r13 = r34
            r0 = 0
            r14 = r0
            com.mediatek.twoworlds.tv.MtkTvEvent r0 = r1.tvEvent     // Catch:{ Exception -> 0x0048 }
            r2 = 32
            r0.setMaxEventNum(r2)     // Catch:{ Exception -> 0x0048 }
            com.mediatek.twoworlds.tv.MtkTvEvent r2 = r1.tvEvent     // Catch:{ Exception -> 0x0048 }
            int r3 = r13.getChannelId()     // Catch:{ Exception -> 0x0048 }
            r4 = r8
            r6 = r10
            java.util.List r0 = r2.getEventListByChannelId(r3, r4, r6)     // Catch:{ Exception -> 0x0048 }
            r14 = r0
            goto L_0x0049
        L_0x0048:
            r0 = move-exception
        L_0x0049:
            if (r14 == 0) goto L_0x02c5
            java.lang.String r0 = "DataReader"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "---mTVEventList get :"
            r2.append(r3)
            int r3 = r14.size()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r2)
            r0 = 0
        L_0x0066:
            int r2 = r14.size()
            if (r0 >= r2) goto L_0x02bd
            java.lang.Object r2 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r2 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r2
            long r2 = r2.getStartTime()
            java.lang.Object r4 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r4 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r4
            long r4 = r4.getDuration()
            long r4 = r4 + r2
            java.lang.String r6 = "DataReader"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r15 = "+++++++ event guidance ++++++++++++"
            r7.append(r15)
            java.lang.Object r15 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r15 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r15
            int r15 = r15.getChannelId()
            r7.append(r15)
            java.lang.String r15 = "   "
            r7.append(r15)
            java.lang.Object r15 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r15 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r15
            int r15 = r15.getEventId()
            r7.append(r15)
            java.lang.String r15 = "   "
            r7.append(r15)
            java.lang.Object r15 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r15 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r15
            java.lang.String r15 = r15.getGuidanceText()
            r7.append(r15)
            java.lang.String r15 = "    "
            r7.append(r15)
            java.lang.Object r15 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r15 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r15
            long r10 = r15.getDuration()
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            java.lang.String r6 = "DataReader"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "+++++++ event name ++++++++++++"
            r7.append(r10)
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            java.lang.String r10 = r10.getEventTitle()
            r7.append(r10)
            java.lang.String r10 = "   "
            r7.append(r10)
            r7.append(r2)
            java.lang.String r10 = "   "
            r7.append(r10)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            java.lang.String r6 = "DataReader"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "+++++++ event detail ++++++++++++"
            r7.append(r10)
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            java.lang.String r10 = r10.getEventDetail()
            r7.append(r10)
            java.lang.String r10 = "     >>"
            r7.append(r10)
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            java.lang.String r10 = r10.getEventRating()
            r7.append(r10)
            java.lang.String r10 = "  extend detail:"
            r7.append(r10)
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            java.lang.String r10 = r10.getEventDetailExtend()
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            java.lang.Object r6 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r6 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r6
            int[] r6 = r6.getEventCategory()
            java.lang.Object r7 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r7 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r7
            java.lang.String r7 = r7.getEventTitle()
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            java.lang.String r10 = r10.getGuidanceText()
            java.lang.Object r11 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r11 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r11
            java.lang.String r11 = r11.getEventDetail()
            java.lang.Object r15 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r15 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r15
            java.lang.String r15 = r15.getEventDetailExtend()
            r18 = r12
            java.lang.String r12 = r1.getResultDetail(r10, r11, r15)
            r19 = r10
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r10 = r1.integration
            java.lang.String r7 = r10.getAvailableString(r7)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r10 = r1.integration
            java.lang.String r10 = r10.getAvailableString(r12)
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r12 = new com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo
            java.lang.Long r21 = java.lang.Long.valueOf(r2)
            java.lang.Long r22 = java.lang.Long.valueOf(r4)
            if (r7 == 0) goto L_0x01ab
            r28 = r4
            java.lang.String r4 = ""
            boolean r4 = r7.equals(r4)
            if (r4 == 0) goto L_0x01a8
            goto L_0x01ad
        L_0x01a8:
            r23 = r7
            goto L_0x01b8
        L_0x01ab:
            r28 = r4
        L_0x01ad:
            android.content.Context r4 = r1.mContext
            r5 = 2131692036(0x7f0f0a04, float:1.901316E38)
            java.lang.String r4 = r4.getString(r5)
            r23 = r4
        L_0x01b8:
            r25 = 0
            r26 = 1
            r27 = 1
            r20 = r12
            r24 = r10
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r4 = r12
            java.lang.Object r5 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r5 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r5
            boolean r5 = r5.isCaption()
            r4.setHasSubTitle(r5)
            r4.setCategoryType(r6)
            int r5 = r13.getChannelId()
            r4.setChannelId(r5)
            java.lang.Object r5 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r5 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r5
            int r5 = r5.getEventId()
            r4.setProgramId(r5)
            com.mediatek.twoworlds.tv.MtkTvEvent r5 = r1.tvEvent
            int r12 = r13.getChannelId()
            java.lang.Object r20 = r14.get(r0)
            r30 = r7
            r7 = r20
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r7 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r7
            int r7 = r7.getEventId()
            boolean r5 = r5.checkEventBlock(r12, r7)
            r4.setProgramBlock(r5)
            java.lang.String r5 = "DataReader"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r12 = "++++programblock++"
            r7.append(r12)
            com.mediatek.twoworlds.tv.MtkTvEvent r12 = r1.tvEvent
            r31 = r10
            int r10 = r13.getChannelId()
            java.lang.Object r20 = r14.get(r0)
            r32 = r11
            r11 = r20
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r11 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r11
            int r11 = r11.getEventId()
            boolean r10 = r12.checkEventBlock(r10, r11)
            r7.append(r10)
            java.lang.String r10 = "   "
            r7.append(r10)
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10
            boolean r10 = r10.isCaption()
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r7)
            r1.setMStype(r4, r6)
            java.lang.Object r5 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r5 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r5
            java.lang.String r5 = r5.getEventRating()
            r4.setRatingType(r5)
            java.lang.Object r5 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase r5 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r5
            int r5 = r5.getEventRatingType()
            r4.setRatingValue(r5)
            java.lang.Object r5 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfo r5 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfo) r5
            float r5 = r1.getProWidth((com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r4, (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r5, (long) r8)
            r4.setmScale(r5)
            if (r0 != 0) goto L_0x0286
            com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert r7 = r1.tmCvt
            float r7 = com.mediatek.wwtv.tvcenter.epg.EPGTimeConvert.countShowWidth(r2, r8)
            r10 = 0
            int r11 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r11 <= 0) goto L_0x0282
            r4.setLeftMargin(r7)
            goto L_0x0285
        L_0x0282:
            r4.setLeftMargin(r10)
        L_0x0285:
            goto L_0x02b1
        L_0x0286:
            int r7 = r0 + -1
            java.lang.Object r7 = r14.get(r7)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfo r7 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfo) r7
            java.lang.Object r10 = r14.get(r0)
            com.mediatek.twoworlds.tv.model.MtkTvEventInfo r10 = (com.mediatek.twoworlds.tv.model.MtkTvEventInfo) r10
            float r7 = r1.getProLeftMargin((com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo) r4, (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r7, (com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase) r10)
            r4.setLeftMargin(r7)
            java.lang.String r10 = "xinsheng"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "+++++++ mLeftMargin ++++++++++++"
            r11.append(r12)
            r11.append(r7)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r11)
        L_0x02b1:
            r7 = r18
            r7.add(r4)
            int r0 = r0 + 1
            r12 = r7
            r10 = r37
            goto L_0x0066
        L_0x02bd:
            r7 = r12
            int r0 = r14.size()
            r3 = r37
            goto L_0x02ec
        L_0x02c5:
            r7 = r12
            com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo r0 = new com.mediatek.wwtv.tvcenter.epg.EPGProgramInfo
            java.lang.Long r2 = java.lang.Long.valueOf(r35)
            r3 = r37
            long r5 = r8 + r3
            java.lang.Long r17 = java.lang.Long.valueOf(r5)
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 1
            r15 = r0
            r16 = r2
            r15.<init>(r16, r17, r18, r19, r20, r21, r22)
            r2 = 1065353216(0x3f800000, float:1.0)
            r0.setmScale(r2)
            r7.add(r0)
        L_0x02ec:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.DataReader.readChannelProgramInfoByTime(com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase, long, long):java.util.List");
    }

    public String getResultDetail(String guidence, String detail, String extendDetail) {
        MtkLog.d(TAG, "guidence>>" + guidence + ">>");
        MtkLog.d(TAG, "guidence222>>" + detail + ">>" + extendDetail + ">>>");
        String resultDetail = "";
        if (guidence == null) {
            guidence = "";
        }
        if (detail == null) {
            detail = "";
        }
        if (extendDetail == null) {
            extendDetail = "";
        }
        if (MarketRegionInfo.getCurrentMarketRegion() == 2) {
            if (!guidence.equals("")) {
                resultDetail = guidence;
            }
            if (resultDetail.equals("")) {
                return detail;
            }
            if (detail.equals("")) {
                return resultDetail;
            }
            return resultDetail + "\n" + detail;
        } else if (MarketRegionInfo.getCurrentMarketRegion() != 3) {
            return resultDetail;
        } else {
            if (!guidence.equals("")) {
                resultDetail = guidence;
            }
            if (resultDetail.equals("")) {
                resultDetail = detail;
            } else if (!detail.equals("")) {
                resultDetail = resultDetail + "\n" + detail;
            }
            if (resultDetail.equals("")) {
                return extendDetail;
            }
            if (extendDetail.equals("")) {
                return resultDetail;
            }
            return resultDetail + "\n" + extendDetail;
        }
    }

    public float getProWidth(EPGProgramInfo epgProInfo, MtkTvEventInfoBase event, long startTime) {
        float width;
        long proStartTime = event.getStartTime();
        long proEndTime = event.getDuration() + proStartTime;
        long duration = this.tmCvt.getHourtoMsec(2);
        if (proStartTime < startTime && proEndTime > startTime + duration) {
            epgProInfo.setDrawLeftIcon(true);
            epgProInfo.setDrawRightIcon(true);
            width = 1.0f;
            MtkLog.d(TAG, "setAdpter-----layoutParams.1---------------->");
        } else if (proStartTime < startTime) {
            EPGTimeConvert ePGTimeConvert = this.tmCvt;
            width = EPGTimeConvert.countShowWidth(proEndTime, startTime);
            epgProInfo.setDrawLeftIcon(true);
            MtkLog.d(TAG, "setAdpter-----layoutParams.2---------------->");
        } else if (proEndTime > startTime + duration) {
            EPGTimeConvert ePGTimeConvert2 = this.tmCvt;
            width = EPGTimeConvert.countShowWidth(startTime + duration, proStartTime);
            epgProInfo.setDrawRightIcon(true);
            MtkLog.d(TAG, "setAdpter-----layoutParams.3---------------->");
        } else {
            width = this.tmCvt.countShowWidth(event.getDuration());
            MtkLog.d(TAG, "setAdpter-----layoutParams.4---------------->" + event.getDuration());
        }
        MtkLog.d(TAG, " program width: proEndTime:" + proEndTime + "_proEndTime:" + proEndTime + "_startTime:" + startTime + "_width:" + width);
        return width;
    }

    public float getProWidth(EPGProgramInfo epgProInfo, long startTime, long duration) {
        float width;
        long proStartTime = epgProInfo.getmStartTime().longValue();
        long proEndTime = epgProInfo.getmEndTime().longValue();
        if (proStartTime < startTime && proEndTime > startTime + duration) {
            epgProInfo.setDrawLeftIcon(true);
            epgProInfo.setDrawRightIcon(true);
            width = 1.0f;
            MtkLog.d(TAG, "setAdpter-----layoutParams.1---------------->");
        } else if (proStartTime < startTime) {
            EPGTimeConvert ePGTimeConvert = this.tmCvt;
            width = EPGTimeConvert.countShowWidth(proEndTime, startTime);
            epgProInfo.setDrawLeftIcon(true);
            MtkLog.d(TAG, "setAdpter-----layoutParams.2---------------->");
        } else if (proEndTime > startTime + duration) {
            EPGTimeConvert ePGTimeConvert2 = this.tmCvt;
            width = EPGTimeConvert.countShowWidth(startTime + duration, proStartTime);
            epgProInfo.setDrawRightIcon(true);
            MtkLog.d(TAG, "setAdpter-----layoutParams.3---------------->");
        } else {
            width = this.tmCvt.countShowWidth(proEndTime - proStartTime);
            MtkLog.d(TAG, "setAdpter-----layoutParams.4---------------->");
        }
        MtkLog.d(TAG, " program width: proEndTime:" + proEndTime + "_proEndTime:" + proEndTime + "_startTime:" + startTime + "_width:" + width);
        return width;
    }

    public float getProLeftMargin(EPGProgramInfo mTVprogramInfo, MtkTvEventInfoBase preTvEvent, MtkTvEventInfoBase currentTvEvent) {
        long startTime = preTvEvent.getStartTime() + preTvEvent.getDuration();
        long endTime = currentTvEvent.getStartTime();
        EPGTimeConvert ePGTimeConvert = this.tmCvt;
        return EPGTimeConvert.countShowWidth(endTime, startTime);
    }

    public float getProLeftMargin(EPGProgramInfo mTVprogramInfo, EPGProgramInfo preTvEvent, EPGProgramInfo currentTvEvent) {
        long startTime = preTvEvent.getmEndTime().longValue();
        long endTime = currentTvEvent.getmStartTime().longValue();
        EPGTimeConvert ePGTimeConvert = this.tmCvt;
        return EPGTimeConvert.countShowWidth(endTime, startTime);
    }

    public List<EPGProgramInfo> getChannelProgramList(MtkTvChannelInfoBase channel, int dayNum, int startTime, int mTimeSpan) {
        if (channel != null) {
            long sTime = this.tmCvt.setDate(EPGUtil.getCurrentDateDayAsMills(), dayNum, (long) startTime);
            long duration = this.tmCvt.getHourtoMsec(mTimeSpan);
            new ArrayList();
            return readChannelProgramInfoByTime(channel, sTime, duration);
        }
        int i = startTime;
        int i2 = mTimeSpan;
        return null;
    }

    public List<EPGChannelInfo> getChannelList() {
        List<EPGChannelInfo> mChannelList2 = new ArrayList<>();
        if (CommonIntegration.supportTIFFunction()) {
            this.mTIFChannelInfoList = this.mTIFChannelManager.getTIFPreOrNextChannelList(TIFFunctionUtil.getCurrentChannelId(), false, true, 6, TIFFunctionUtil.CH_LIST_EPG_MASK, TIFFunctionUtil.CH_LIST_EPG_VAL);
            this.tvChannelList = TIFFunctionUtil.getApiChannelList(this.mTIFChannelInfoList);
            if (this.mTIFChannelInfoList != null) {
                for (TIFChannelInfo tempTifChannel : this.mTIFChannelInfoList) {
                    mChannelList2.add(new EPGChannelInfo(tempTifChannel));
                }
            }
        } else {
            int i = 0;
            this.tvChannelList = this.integration.getChList(getCurrentChId(), 0, 6);
            if (this.tvChannelList != null) {
                MtkLog.e(TAG, "setAdapter.list." + this.tvChannelList.size());
                while (true) {
                    int i2 = i;
                    if (i2 >= this.tvChannelList.size()) {
                        break;
                    }
                    mChannelList2.add(new EPGChannelInfo(this.tvChannelList.get(i2)));
                    i = i2 + 1;
                }
            }
        }
        return mChannelList2;
    }

    public void updateChannList(List<EPGChannelInfo> oldList, ArrayList<EPGChannelInfo> mNewList) {
        if (oldList != null && mNewList != null) {
            for (int i = 0; i < oldList.size(); i++) {
                EPGChannelInfo oldChannel = oldList.get(i);
                int j = 0;
                while (true) {
                    if (j >= mNewList.size()) {
                        break;
                    }
                    EPGChannelInfo newChannel = mNewList.get(j);
                    if (oldChannel.getTVChannel().getChannelId() == newChannel.getTVChannel().getChannelId()) {
                        newChannel.setmTVProgramInfoList(oldChannel.getmTVProgramInfoList());
                        oldList.set(i, newChannel);
                        mNewList.remove(j);
                        break;
                    }
                    j++;
                }
            }
        }
    }

    public List<EPGChannelInfo> getAllChannelList(boolean filterAnalog) {
        int i = 0;
        this.tvChannelList = this.integration.getChannelList(0, 0, this.integration.getAllEPGChannelLength(), this.integration.getChListFilterEPG());
        MtkLog.e(TAG, "epg get all channel list size:" + this.tvChannelList.size());
        if (this.tvChannelList != null) {
            while (true) {
                int i2 = i;
                if (i2 >= this.tvChannelList.size()) {
                    break;
                }
                MtkTvChannelInfoBase mtkTvChannelInfoBase = this.tvChannelList.get(i2);
                if (!filterAnalog || mtkTvChannelInfoBase == null || !mtkTvChannelInfoBase.isAnalogService()) {
                    this.mChannelList.add(new EPGChannelInfo(this.tvChannelList.get(i2)));
                }
                i = i2 + 1;
            }
        }
        return this.mChannelList;
    }

    public List<EPGChannelInfo> getAllChannelList() {
        return getAllChannelList(false);
    }

    public List<EPGChannelInfo> getAllChannelListByTIF(boolean filterAnalog) {
        List<EPGChannelInfo> mChannelList2 = new ArrayList<>();
        this.mTIFChannelInfoList = new ArrayList();
        boolean is3rdTVSource = this.integration.is3rdTVSource();
        MtkLog.d(TAG, "is3rdTVSource=" + is3rdTVSource);
        if (is3rdTVSource) {
            List<TIFChannelInfo> tIFChannelInfoListBySource = this.mTIFChannelManager.get3RDChannelList();
            if (tIFChannelInfoListBySource != null) {
                this.mTIFChannelInfoList.addAll(tIFChannelInfoListBySource);
            } else {
                MtkLog.e(TAG, "getAllChannelListByTIF, tvInputInfo");
            }
        } else {
            List<TIFChannelInfo> tIFChannelInfoListAll = this.mTIFChannelManager.queryChanelListAll(TIFFunctionUtil.CH_LIST_EPG_MASK, TIFFunctionUtil.CH_LIST_EPG_VAL);
            if (tIFChannelInfoListAll != null) {
                for (TIFChannelInfo tempTifChannel : tIFChannelInfoListAll) {
                    MtkTvChannelInfoBase mtkTvChannelInfoBase = tempTifChannel.mMtkTvChannelInfo;
                    if (mtkTvChannelInfoBase == null || ((!filterAnalog || !mtkTvChannelInfoBase.isAnalogService()) && !mtkTvChannelInfoBase.isSkip())) {
                        this.mTIFChannelInfoList.add(tempTifChannel);
                    } else {
                        MtkLog.e(TAG, "getAllChannelListByTIF, isAnalogService>>>" + mtkTvChannelInfoBase.isAnalogService() + ",isSkip>>>" + mtkTvChannelInfoBase.isSkip());
                    }
                }
            }
            if (this.mTIFChannelInfoList != null) {
                this.tvChannelList = TIFFunctionUtil.getApiChannelList(this.mTIFChannelInfoList);
            }
        }
        if (this.mTIFChannelInfoList != null) {
            for (TIFChannelInfo tempTifChannel2 : this.mTIFChannelInfoList) {
                mChannelList2.add(new EPGChannelInfo(tempTifChannel2));
            }
        }
        return mChannelList2;
    }

    public List<EPGChannelInfo> getAllChannelListByTIF() {
        return getAllChannelListByTIF(true);
    }

    public List<MtkTvChannelInfoBase> getChannelList(boolean nextPage) {
        new ArrayList();
        if (nextPage) {
            return this.integration.getChList(this.integration.getCurChInfo().getChannelId() + 1, 0, 6);
        }
        return this.integration.getChList(this.integration.getCurChInfo().getChannelId(), 6, 0);
    }

    public List<EPGChannelInfo> getChannelListByTIF(boolean nextPage) {
        List<EPGChannelInfo> mChannelList2 = new ArrayList<>();
        this.mTIFChannelInfoList = this.mTIFChannelManager.getTIFPreOrNextChannelList(TIFFunctionUtil.getCurrentChannelId(), !nextPage, false, 6, TIFFunctionUtil.CH_LIST_EPG_MASK, TIFFunctionUtil.CH_LIST_EPG_VAL);
        StringBuilder sb = new StringBuilder();
        sb.append("mTIFChannelInfoList?>>>");
        sb.append(this.mTIFChannelInfoList != null ? Integer.valueOf(this.mTIFChannelInfoList.size()) : this.mTIFChannelInfoList);
        MtkLog.d(TAG, sb.toString());
        this.tvChannelList = TIFFunctionUtil.getApiChannelList(this.mTIFChannelInfoList);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("tvChannelList?>>>");
        sb2.append(this.tvChannelList != null ? Integer.valueOf(this.tvChannelList.size()) : this.tvChannelList);
        MtkLog.d(TAG, sb2.toString());
        for (TIFChannelInfo tempTifChannel : this.mTIFChannelInfoList) {
            mChannelList2.add(new EPGChannelInfo(tempTifChannel));
        }
        MtkLog.d(TAG, "mChannelList?>>>" + mChannelList2.size());
        return mChannelList2;
    }

    public int getCurrentChId() {
        return this.integration.getCurrentChannelId();
    }

    public MtkTvChannelInfoBase getCurrentPlayChannel() {
        return this.integration.getCurChInfo();
    }

    public int getChannelPosition(MtkTvChannelInfoBase channel) {
        int position = 0;
        if (this.tvChannelList != null) {
            position = this.tvChannelList.indexOf(channel);
        }
        if (position < 0) {
            return 0;
        }
        return position;
    }

    public int get3rdCurrentPosition() {
        int curChannelId = this.integration.getCurrentChannelId();
        List<EPGChannelInfo> listEPGChannelInfo = getAllChannelListByTIF();
        int position = 0;
        int i = 0;
        while (true) {
            if (i >= listEPGChannelInfo.size()) {
                break;
            } else if (((long) curChannelId) == listEPGChannelInfo.get(i).mId) {
                position = i;
                MtkLog.d(TAG, "get3rdCurrentPositioncur>>>ChannelId=" + curChannelId);
                break;
            } else {
                i++;
            }
        }
        MtkLog.d(TAG, "get3rdCurrentPosition>>>position=" + position);
        return position;
    }

    public int getCurrentPlayChannelPosition() {
        if (this.integration.is3rdTVSource()) {
            MtkLog.d(TAG, "get3rdCurrentPosition is3rdTVSource");
            return get3rdCurrentPosition();
        }
        MtkLog.d(TAG, "get3rdCurrentPosition getCurrentPlayChannel");
        return getChannelPosition(getCurrentPlayChannel());
    }

    public boolean isChannelExit(int channelId) {
        if (this.mTIFChannelManager.getAPIChannelInfoById(channelId) != null) {
            return true;
        }
        return false;
    }

    public EPGChannelInfo getChannelByChannelNum(short channelNum) {
        for (EPGChannelInfo child : getChannelList()) {
            if (child.getmChanelNum() == channelNum) {
                return child;
            }
        }
        return null;
    }

    public void selectChannel(MtkTvChannelInfoBase chInfo) {
        this.integration.selectChannelByInfo(chInfo);
    }

    public void selectChannelByTIF(long id) {
        this.mTIFChannelManager.selectChannelByTIFInfo(this.mTIFChannelManager.getTIFChannelInfoPLusByProviderId(id));
    }

    public boolean isTvSourceLock() {
        int showFlag = MtkTvPWDDialog.getInstance().PWDShow();
        MtkLog.d(TAG, "showFlag>>>" + showFlag);
        return this.integration.isMenuInputTvBlock();
    }

    public boolean isChannelBlocked(MtkTvChannelInfoBase chInfo) {
        if (chInfo != null) {
            return chInfo.isBlock();
        }
        return false;
    }

    public String getCurrentSubtitleLang() {
        return null;
    }

    public void setMStype(EPGProgramInfo program, int[] categoryType) {
        int index = categoryType[0];
        MtkLog.d(TAG, "subIndex>>>" + index + "   " + program.getmTitle());
        if (MarketRegionInfo.getCurrentMarketRegion() != 3) {
            if (index == 0) {
                program.setMainType(-1);
                program.setSubType(-1);
                return;
            }
            int mainIndex = (index & 240) >> 4;
            if (mainIndex < 0 || mainIndex >= this.mType.length) {
                program.setMainType(-1);
                program.setSubType(-1);
                return;
            }
            program.setMainType(mainIndex);
            int subIndex = (index & 240) << 4;
            if (subIndex < 0 || subIndex >= this.sType[mainIndex].length) {
                program.setSubType(-1);
            } else {
                program.setSubType(subIndex);
            }
        } else if (index == 0) {
            program.setMainType(-1);
            program.setSubType(-1);
        } else {
            int mainIndex2 = (index & 240) >> 4;
            if (mainIndex2 > 0) {
                String country = MtkTvConfig.getInstance().getCountry();
                if (country != null && country.equals("GBR")) {
                    switch (mainIndex2) {
                        case 6:
                        case 9:
                        case 10:
                            mainIndex2 -= 3;
                            break;
                        case 7:
                        case 8:
                            mainIndex2 = 2;
                            break;
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                            mainIndex2 = 9;
                            break;
                        case 15:
                            mainIndex2 = 8;
                            break;
                    }
                    program.setMainType(mainIndex2);
                    program.setSubType(-1);
                } else if (country != null && country.equals("AUS")) {
                    MtkLog.d(TAG, "AUS subIndex>>>" + mainIndex2);
                    program.setMainType(mainIndex2);
                    program.setSubType(-1);
                } else if (mainIndex2 <= 0 || mainIndex2 >= this.mType.length) {
                    program.setMainType(-1);
                    program.setSubType(-1);
                } else {
                    program.setMainType(mainIndex2);
                    int subIndex2 = index & 15;
                    MtkLog.d(TAG, "subIndex>>>" + mainIndex2 + "   " + subIndex2);
                    if (this.sType[mainIndex2] == null || subIndex2 < 0 || subIndex2 >= this.sType[mainIndex2].length) {
                        program.setSubType(-1);
                    } else {
                        program.setSubType(subIndex2);
                    }
                }
            } else {
                program.setMainType(-1);
                program.setSubType(-1);
            }
        }
    }

    public String[] getMainType() {
        return this.mType;
    }

    public String[][] getSubType() {
        return this.sType;
    }

    public void loadProgramType() {
        switch (MarketRegionInfo.getCurrentMarketRegion()) {
            case 2:
                this.sType = new String[16][];
                this.mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_sa_type);
                this.sType[0] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_news);
                this.sType[1] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_sports);
                this.sType[2] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_education);
                this.sType[3] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_soap_opera);
                this.sType[4] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_mini_series);
                this.sType[5] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_series);
                this.sType[6] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_variety);
                this.sType[7] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_reality_show);
                this.sType[8] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_information);
                this.sType[9] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_comical);
                this.sType[10] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_children);
                this.sType[11] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_erotic);
                this.sType[12] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_movie);
                this.sType[13] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_RA_TE_SA_PR);
                this.sType[14] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_DEBATE_INTERVIEW);
                this.sType[15] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sa_other);
                return;
            case 3:
                this.sType = new String[11][];
                String country = MtkTvConfig.getInstance().getCountry();
                if (country != null && country.equals("GBR")) {
                    this.mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_type_UK);
                    return;
                } else if (country == null || !country.equals("AUS")) {
                    this.mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_type);
                    this.sType[0] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_movie);
                    this.sType[1] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_news);
                    this.sType[2] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_show);
                    this.sType[3] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sports);
                    this.sType[4] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_children);
                    this.sType[5] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_music);
                    this.sType[6] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_arts);
                    this.sType[7] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_social);
                    this.sType[8] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_education);
                    this.sType[9] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_leisure);
                    this.sType[10] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_special);
                    return;
                } else {
                    this.sType = new String[14][];
                    this.mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_type_AUS);
                    return;
                }
            default:
                this.sType = new String[11][];
                this.mType = this.mContext.getResources().getStringArray(R.array.nav_epg_filter_type_cn);
                this.sType[0] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_movie_cn);
                this.sType[1] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_news_cn);
                this.sType[2] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_show_cn);
                this.sType[3] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_sports_cn);
                this.sType[4] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_children_cn);
                this.sType[5] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_music_cn);
                this.sType[6] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_arts_cn);
                this.sType[7] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_social_cn);
                this.sType[8] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_education_cn);
                this.sType[9] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_leisure_cn);
                this.sType[10] = this.mContext.getResources().getStringArray(R.array.nav_epg_subtype_special_cn);
                return;
        }
    }

    public void cleanMStypeDB() {
        if (this.mType == null) {
            loadProgramType();
        }
        for (int i = 0; i < this.mType.length; i++) {
            if (SaveValue.getInstance(this.mContext).readBooleanValue(this.mType[i], false)) {
                SaveValue.getInstance(this.mContext).removekey(this.mType[i]);
            }
            if (this.sType[i] != null) {
                for (int j = 0; j < this.sType[i].length; j++) {
                    if (SaveValue.getInstance(this.mContext).readBooleanValue(this.sType[i][j], false)) {
                        SaveValue.getInstance(this.mContext).removekey(this.sType[i][j]);
                    }
                }
            }
        }
    }

    public void loadMonthAndWeekRes() {
        this.mMonthFull = new String[12];
        this.mMonthSimple = new String[12];
        this.mWeekFull = new String[7];
        this.mWeekSimple = new String[7];
        this.mMonthFull[0] = this.mContext.getString(R.string.nav_epg_January);
        this.mMonthFull[1] = this.mContext.getString(R.string.nav_epg_February);
        this.mMonthFull[2] = this.mContext.getString(R.string.nav_epg_March);
        this.mMonthFull[3] = this.mContext.getString(R.string.nav_epg_April);
        this.mMonthFull[4] = this.mContext.getString(R.string.nav_epg_May);
        this.mMonthFull[5] = this.mContext.getString(R.string.nav_epg_June);
        this.mMonthFull[6] = this.mContext.getString(R.string.nav_epg_July);
        this.mMonthFull[7] = this.mContext.getString(R.string.nav_epg_August);
        this.mMonthFull[8] = this.mContext.getString(R.string.nav_epg_September);
        this.mMonthFull[9] = this.mContext.getString(R.string.nav_epg_October);
        this.mMonthFull[10] = this.mContext.getString(R.string.nav_epg_November);
        this.mMonthFull[11] = this.mContext.getString(R.string.nav_epg_December);
        this.mMonthSimple[0] = this.mContext.getString(R.string.nav_epg_Jan);
        this.mMonthSimple[1] = this.mContext.getString(R.string.nav_epg_Feb);
        this.mMonthSimple[2] = this.mContext.getString(R.string.nav_epg_Mar);
        this.mMonthSimple[3] = this.mContext.getString(R.string.nav_epg_Apr);
        this.mMonthSimple[4] = this.mContext.getString(R.string.nav_epg_may);
        this.mMonthSimple[5] = this.mContext.getString(R.string.nav_epg_Jun);
        this.mMonthSimple[6] = this.mContext.getString(R.string.nav_epg_Jul);
        this.mMonthSimple[7] = this.mContext.getString(R.string.nav_epg_Aug);
        this.mMonthSimple[8] = this.mContext.getString(R.string.nav_epg_Sep);
        this.mMonthSimple[9] = this.mContext.getString(R.string.nav_epg_Oct);
        this.mMonthSimple[10] = this.mContext.getString(R.string.nav_epg_Nov);
        this.mMonthSimple[11] = this.mContext.getString(R.string.nav_epg_Dec);
        this.mWeekFull[0] = this.mContext.getString(R.string.nav_epg_Sunday);
        this.mWeekFull[1] = this.mContext.getString(R.string.nav_epg_Monday);
        this.mWeekFull[2] = this.mContext.getString(R.string.nav_epg_Tuesday);
        this.mWeekFull[3] = this.mContext.getString(R.string.nav_epg_Wednesday);
        this.mWeekFull[4] = this.mContext.getString(R.string.nav_epg_Thursday);
        this.mWeekFull[5] = this.mContext.getString(R.string.nav_epg_Friday);
        this.mWeekFull[6] = this.mContext.getString(R.string.nav_epg_Saturday);
        this.mWeekSimple[0] = this.mContext.getString(R.string.nav_epg_Sun);
        this.mWeekSimple[1] = this.mContext.getString(R.string.nav_epg_Mon);
        this.mWeekSimple[2] = this.mContext.getString(R.string.nav_epg_Tue);
        this.mWeekSimple[3] = this.mContext.getString(R.string.nav_epg_Wed);
        this.mWeekSimple[4] = this.mContext.getString(R.string.nav_epg_Thur);
        this.mWeekSimple[5] = this.mContext.getString(R.string.nav_epg_Fri);
        this.mWeekSimple[6] = this.mContext.getString(R.string.nav_epg_Sat);
    }

    public String[] getWeekFullArray() {
        return this.mWeekFull;
    }

    public String[] getWeekSimpleArray() {
        return this.mWeekSimple;
    }

    public String[] getMonthFullArray() {
        return this.mMonthFull;
    }

    public String[] getMonthSimpleArray() {
        return this.mMonthSimple;
    }
}
