package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvEventATSC;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.R;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EPGUsManager {
    public static final int Message_CHANGE_CHANNEL = 1006;
    public static final int Message_LoadData = 1003;
    public static final int Message_ReFreshData = 1004;
    public static final int Message_Refresh_ListView = 1002;
    public static final int Message_Refresh_Time = 1001;
    public static final int Message_ShouldFinish = 1005;
    public static final int PER_PAGE_NUM = 4;
    private static final String TAG = "EPGUsManager";
    private static EPGUsManager epgUsManager;
    public static boolean requestComplete;
    private EPGUsChannelManager channelManager;
    private List<ListItemData> dataGroup;
    private int loadReturn = 1;
    private Context mContext;

    public static synchronized EPGUsManager getInstance(Context context) {
        EPGUsManager ePGUsManager;
        synchronized (EPGUsManager.class) {
            epgUsManager = new EPGUsManager(context);
            ePGUsManager = epgUsManager;
        }
        return ePGUsManager;
    }

    public EPGUsManager(Context context) {
        this.mContext = context;
        this.channelManager = EPGUsChannelManager.getInstance(this.mContext);
        this.channelManager.initChannelData();
        this.dataGroup = new ArrayList();
    }

    public String getTimeToShow() {
        return EPGUtil.formatCurrentTime(this.mContext);
    }

    public EPGProgressDialog loading(Context context, boolean load) {
        EPGProgressDialog pDialog = new EPGProgressDialog(context, R.style.dialog);
        EPGUtil.setPositon(-700, -400, pDialog);
        pDialog.show();
        return pDialog;
    }

    public boolean onLeftChannel() {
        return this.channelManager.preChannel();
    }

    public boolean onRightChannel() {
        return this.channelManager.nextChannel();
    }

    public void initChannels() {
        this.channelManager.initChannelData();
    }

    public boolean isCurChATV() {
        boolean isATV = CommonIntegration.getInstance().isCurrentSourceATV();
        MtkLog.e(TAG, "isAnalogService:" + isATV);
        return isATV;
    }

    private String getChannelNumber(MtkTvChannelInfoBase mCurrentChannel) {
        String value;
        if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
            MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
            value = tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum();
            MtkLog.e(TAG, "getChannelNumCur1===>" + value);
        } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
            MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
            value = tmpIsdb.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpIsdb.getMinorNum();
            MtkLog.e(TAG, "getChannelNumCur2===>" + value);
        } else {
            value = " " + mCurrentChannel.getChannelNumber();
            MtkLog.e(TAG, "getChannelNumCur3===>" + value);
        }
        return value + " ";
    }

    public int getChannelIdCur() {
        if (this.channelManager == null || this.channelManager.getChannelCurrent() == null) {
            return 1;
        }
        return this.channelManager.getChannelCurrent().getChannelId();
    }

    public String getChannelNumCur() {
        if (CommonIntegration.supportTIFFunction()) {
            if (this.channelManager != null) {
                return this.channelManager.getCurrentChannelNum();
            }
            return "";
        } else if (this.channelManager == null || this.channelManager.getChannelCurrent() == null) {
            return "";
        } else {
            return getChannelNumber(this.channelManager.getChannelCurrent());
        }
    }

    public String getChannelNameCur() {
        if (CommonIntegration.supportTIFFunction()) {
            if (this.channelManager != null) {
                return this.channelManager.getCurrentChannelName();
            }
            return "";
        } else if (this.channelManager == null || this.channelManager.getChannelCurrent() == null || this.channelManager.getChannelCurrent().getServiceName() == null) {
            return "";
        } else {
            return this.channelManager.getChannelCurrent().getServiceName();
        }
    }

    public String getChannelNumPre() {
        if (CommonIntegration.supportTIFFunction()) {
            if (this.channelManager != null) {
                return this.channelManager.getPreChannelNum();
            }
            return "";
        } else if (this.channelManager == null || this.channelManager.getChannelPrevious() == null) {
            return "";
        } else {
            return getChannelNumber(this.channelManager.getChannelPrevious());
        }
    }

    public String getChannelNumNext() {
        if (CommonIntegration.supportTIFFunction()) {
            if (this.channelManager != null) {
                return this.channelManager.getNextChannelNum();
            }
            return "";
        } else if (this.channelManager == null || this.channelManager.getChannelNext() == null) {
            return "";
        } else {
            return getChannelNumber(this.channelManager.getChannelNext());
        }
    }

    public List<Integer> getDataGroup(long startTime, int countNum) {
        List<Integer> requestList = new ArrayList<>();
        if (this.channelManager == null) {
            MtkLog.e(TAG, "channelManager==null");
            return requestList;
        }
        int[] requests = this.channelManager.loadProgramEvent(this.channelManager.getCurrentChId(), startTime, countNum);
        if (requests != null) {
            MtkLog.d(TAG, "getDataGroup:returnhandle:" + requests + "     " + requests.length);
            if (requests != null) {
                for (int i = 0; i < requests.length; i++) {
                    if (requests[i] != 0) {
                        requestList.add(Integer.valueOf(requests[i]));
                    }
                    MtkLog.d(TAG, "getDataGroup:returnhandle:" + requests[i]);
                }
            }
        }
        return requestList;
    }

    public List<ListItemData> getDataGroup() {
        List<ListItemData> tmp;
        if (CommonIntegration.getInstance().is3rdTVSource() && (tmp = loadEvents()) != null) {
            this.dataGroup = tmp;
        }
        return this.dataGroup;
    }

    public synchronized void addDataGroupItem(ListItemData itemData) {
        MtkLog.d(TAG, "addDataGroupItem>>" + itemData);
        if (this.dataGroup != null) {
            int size = this.dataGroup.size();
            int i = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (this.dataGroup.get(i2).getEventId() == itemData.getEventId()) {
                    this.dataGroup.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            int size2 = this.dataGroup.size();
            if (size2 != 0) {
                if (itemData.getMillsStartTime() <= this.dataGroup.get(size2 - 1).getMillsStartTime()) {
                    List<ListItemData> preList = new ArrayList<>();
                    List<ListItemData> nextList = new ArrayList<>();
                    while (true) {
                        if (i >= size2) {
                            break;
                        } else if (itemData.getMillsStartTime() < this.dataGroup.get(i).getMillsStartTime()) {
                            preList.add(itemData);
                            for (int j = i; j < size2; j++) {
                                nextList.add(this.dataGroup.get(j));
                            }
                        } else {
                            preList.add(this.dataGroup.get(i));
                            i++;
                        }
                    }
                    this.dataGroup.clear();
                    this.dataGroup.addAll(preList);
                    this.dataGroup.addAll(nextList);
                }
            }
            this.dataGroup.add(itemData);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bc, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void updateDataGroup(com.mediatek.wwtv.tvcenter.epg.us.ListItemData r10, int r11) {
        /*
            r9 = this;
            monitor-enter(r9)
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r0 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            if (r0 != 0) goto L_0x0007
            monitor-exit(r9)
            return
        L_0x0007:
            java.lang.String r0 = "EPGUsManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bd }
            r1.<init>()     // Catch:{ all -> 0x00bd }
            java.lang.String r2 = "updateDataGroup>>"
            r1.append(r2)     // Catch:{ all -> 0x00bd }
            r1.append(r10)     // Catch:{ all -> 0x00bd }
            java.lang.String r2 = "  "
            r1.append(r2)     // Catch:{ all -> 0x00bd }
            r1.append(r11)     // Catch:{ all -> 0x00bd }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00bd }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)     // Catch:{ all -> 0x00bd }
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r0 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            int r0 = r0.size()     // Catch:{ all -> 0x00bd }
            r1 = 0
            r2 = 0
            r3 = r2
        L_0x002e:
            if (r3 >= r0) goto L_0x0050
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r4 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x00bd }
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r4 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r4     // Catch:{ all -> 0x00bd }
            int r4 = r4.getEventId()     // Catch:{ all -> 0x00bd }
            if (r4 != r11) goto L_0x004d
            r1 = 1
            if (r10 == 0) goto L_0x0047
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r4 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r4.set(r3, r10)     // Catch:{ all -> 0x00bd }
            goto L_0x0050
        L_0x0047:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r4 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r4.remove(r3)     // Catch:{ all -> 0x00bd }
            goto L_0x0050
        L_0x004d:
            int r3 = r3 + 1
            goto L_0x002e
        L_0x0050:
            if (r1 != 0) goto L_0x00bb
            if (r10 == 0) goto L_0x00bb
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r3 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            int r3 = r3.size()     // Catch:{ all -> 0x00bd }
            if (r3 != 0) goto L_0x0062
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r2 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r2.add(r10)     // Catch:{ all -> 0x00bd }
            goto L_0x00bb
        L_0x0062:
            boolean r3 = r9.containsEvent(r10)     // Catch:{ all -> 0x00bd }
            if (r3 != 0) goto L_0x00bb
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x00bd }
            r3.<init>()     // Catch:{ all -> 0x00bd }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x00bd }
            r4.<init>()     // Catch:{ all -> 0x00bd }
        L_0x0073:
            if (r2 >= r0) goto L_0x00ac
            long r5 = r10.getMillsStartTime()     // Catch:{ all -> 0x00bd }
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r7 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            java.lang.Object r7 = r7.get(r2)     // Catch:{ all -> 0x00bd }
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r7 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r7     // Catch:{ all -> 0x00bd }
            long r7 = r7.getMillsStartTime()     // Catch:{ all -> 0x00bd }
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x009e
            r3.add(r10)     // Catch:{ all -> 0x00bd }
            r5 = r2
        L_0x008d:
            if (r5 >= r0) goto L_0x009d
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r6 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ all -> 0x00bd }
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r6 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r6     // Catch:{ all -> 0x00bd }
            r4.add(r6)     // Catch:{ all -> 0x00bd }
            int r5 = r5 + 1
            goto L_0x008d
        L_0x009d:
            goto L_0x00ac
        L_0x009e:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r5 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ all -> 0x00bd }
            com.mediatek.wwtv.tvcenter.epg.us.ListItemData r5 = (com.mediatek.wwtv.tvcenter.epg.us.ListItemData) r5     // Catch:{ all -> 0x00bd }
            r3.add(r5)     // Catch:{ all -> 0x00bd }
            int r2 = r2 + 1
            goto L_0x0073
        L_0x00ac:
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r2 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r2.clear()     // Catch:{ all -> 0x00bd }
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r2 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r2.addAll(r3)     // Catch:{ all -> 0x00bd }
            java.util.List<com.mediatek.wwtv.tvcenter.epg.us.ListItemData> r2 = r9.dataGroup     // Catch:{ all -> 0x00bd }
            r2.addAll(r4)     // Catch:{ all -> 0x00bd }
        L_0x00bb:
            monitor-exit(r9)
            return
        L_0x00bd:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.epg.us.EPGUsManager.updateDataGroup(com.mediatek.wwtv.tvcenter.epg.us.ListItemData, int):void");
    }

    private boolean containsEvent(ListItemData itemData) {
        if (this.dataGroup == null) {
            return false;
        }
        int size = this.dataGroup.size();
        for (int i = 0; i < size; i++) {
            if (this.dataGroup.get(i).getEventId() == itemData.getEventId()) {
                return true;
            }
        }
        return false;
    }

    public boolean clearEvent(int eventId) {
        if (this.dataGroup == null) {
            return false;
        }
        int size = this.dataGroup.size();
        for (int i = 0; i < size; i++) {
            if (this.dataGroup.get(i).getEventId() == eventId) {
                this.dataGroup.remove(i);
                return true;
            }
        }
        return false;
    }

    public void clearDataGroup() {
        if (this.dataGroup != null) {
            this.dataGroup.clear();
        }
    }

    public int groupSize() {
        if (this.dataGroup != null) {
            return this.dataGroup.size();
        }
        return 0;
    }

    public MtkTvEventInfoBase getEvent(int requestId) {
        if (this.channelManager != null) {
            return this.channelManager.getProgramInfo(requestId);
        }
        return null;
    }

    public ListItemData getEventItem(int requestId) {
        String str;
        String str2;
        MtkLog.d(TAG, "getEventItem>>" + requestId);
        ListItemData itemData = null;
        MtkTvEventInfoBase programInfo = getEvent(requestId);
        if (programInfo != null && programInfo.getStartTime() >= 0) {
            String eventRating = programInfo.getEventRating();
            String eventDetail = programInfo.getEventDetail();
            String eventTitle = programInfo.getEventTitle();
            itemData = new ListItemData();
            itemData.setEventId(requestId);
            itemData.setItemId(programInfo.getChannelId());
            itemData.setItemTime(EPGUtil.getWeekDayofTime(programInfo.getStartTime(), EPGUtil.getCurrentDayStartTime()));
            String title = (eventTitle == null || eventTitle.equals("")) ? this.mContext.getString(R.string.nav_epg_no_program_title) : eventTitle;
            if (eventRating == null || eventRating.equals("")) {
                str = this.mContext.getString(R.string.nav_epg_not_rated);
            } else {
                str = eventRating;
            }
            itemData.setItemProgramType(str);
            itemData.setMillsStartTime(programInfo.getStartTime());
            itemData.setMillsDurationTime(programInfo.getDuration());
            if (eventDetail == null || eventDetail.equals("")) {
                str2 = this.mContext.getString(R.string.nav_epg_no_program_detail);
            } else {
                str2 = eventDetail;
            }
            itemData.setItemProgramDetail(str2);
            itemData.setItemProgramName(title);
            itemData.setCC(programInfo.isCaption());
            itemData.setBlocked(MtkTvEventATSC.getInstance().checkEventBlock(requestId, this.channelManager.getCurrentChId()));
        }
        return itemData;
    }

    public ListItemData getEventItemD(int value) {
        ListItemData itemData = new ListItemData();
        itemData.setItemId(value);
        itemData.setItemTime(value + "");
        itemData.setItemProgramName("No program." + value);
        itemData.setCC(true);
        itemData.setBlocked(false);
        return itemData;
    }

    public ListItemData getNoProItem() {
        ListItemData itemData = new ListItemData();
        itemData.setItemProgramName(this.mContext.getString(R.string.nav_epg_no_program_title));
        itemData.setProgramStartTime("");
        if (MtkTvBanner.getInstance().isDisplayCaptionIcon()) {
            MtkLog.d(TAG, "setCC(true)!!!");
            itemData.setCC(true);
        } else {
            itemData.setCC(false);
        }
        itemData.setBlocked(false);
        itemData.setValid(false);
        return itemData;
    }

    public void clearData() {
        clearDataGroup();
    }

    private List<ListItemData> loadEvents() {
        if (!CommonIntegration.supportTIFFunction()) {
            MtkLog.d(TAG, "loadEvents, supportTIFFunction");
            return null;
        } else if (!CommonIntegration.getInstance().is3rdTVSource()) {
            MtkLog.d(TAG, "loadEvents, is3rdTVSource");
            return null;
        } else {
            long currntChId = TurnkeyUiMainActivity.getInstance().getTvView().getCurrentChannelId();
            if (currntChId == -1) {
                MtkLog.d(TAG, "loadEvents, currntChId");
                return null;
            }
            MtkTvTimeFormatBase tvTime = MtkTvTime.getInstance().getLocalTime();
            long startTime = ((tvTime.toMillis() - ((long) (tvTime.minute * 60))) - ((long) tvTime.second)) * 1000;
            long endTime = 86400000 + startTime;
            String SELECTION = "channel_id in (" + currntChId + ") and " + "end_time_utc_millis" + " > ? and " + "start_time_utc_millis" + " < ?";
            Map<Long, List<TIFProgramInfo>> maps = TIFProgramManager.getInstance(this.mContext).queryProgramListWithGroupCondition((String[]) null, SELECTION, new String[]{String.valueOf(startTime), String.valueOf(endTime)}, (String) null);
            if (maps == null) {
                MtkLog.d(TAG, "loadEvents, maps");
                Log.d(TAG, "loadEvents, maps");
                return null;
            }
            List<TIFProgramInfo> tempProgramList = maps.get(Long.valueOf(currntChId));
            if (tempProgramList == null) {
                MtkLog.d(TAG, "loadEvents, tempProgramList, " + SELECTION + "," + startTime + "," + endTime);
                return null;
            }
            List<ListItemData> itemData = new ArrayList<>();
            for (TIFProgramInfo info : tempProgramList) {
                MtkTvTimeFormatBase tvTime2 = tvTime;
                StringBuilder sb = new StringBuilder();
                sb.append("loadEvents, TIFProgramInfo ");
                sb.append(info.mTitle);
                Log.d(TAG, sb.toString());
                ListItemData data = new ListItemData();
                data.setEventId(info.mEventId);
                data.setItemProgramName(info.mTitle);
                data.setMillsStartTime(info.mStartTimeUtcSec);
                data.setMillsDurationTime(info.mEndTimeUtcSec - info.mStartTimeUtcSec);
                data.setItemProgramDetail(info.mDescription + info.mLongDescription);
                data.setCC(false);
                data.setBlocked(false);
                data.setValid(false);
                itemData.add(data);
                tvTime = tvTime2;
                currntChId = currntChId;
                startTime = startTime;
            }
            long j = currntChId;
            long j2 = startTime;
            return itemData;
        }
    }
}
