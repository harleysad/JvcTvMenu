package com.mediatek.wwtv.tvcenter.util.tif;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.media.tv.TvContractCompat;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.MutableInt;
import com.android.tv.util.AsyncDbTask;
import com.android.tv.util.TvInputManagerHelper;
import com.android.tv.util.Utils;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo;
import com.mediatek.wwtv.setting.base.scan.ui.ScanViewActivity;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.nav.util.InputSourceManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.DestroyApp;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.WeakHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

public class TIFChannelManager {
    private static final int MSG_UPDATE_CHANNELS = 1000;
    private static final String ORDERBY = "substr(cast(internal_provider_data as varchar),19,10)";
    private static final String SELECTION = "";
    private static final String SELECTION_WITH_SVLID = "substr(cast(internal_provider_data as varchar),7,5) = ?";
    private static final String SELECTION_WITH_SVLID_CHANNELID = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(cast(internal_provider_data as varchar),19,10) = ?";
    private static final String SELECTION_WITH_SVLID_INPUTID = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?";
    private static final String SPNAME = "CHMODE";
    private static final String TAG = "TIFChannelManager";
    private static TIFChannelManager mTIFChannelManagerInstance;
    private int CURRENT_CHANNEL_SORT = 0;
    /* access modifiers changed from: private */
    public boolean afterGetAllChanelsGoonsort = false;
    /* access modifiers changed from: private */
    public boolean channelListUpdateState = false;
    TIFChannelInfo.CustomerComparator customerComparator;
    /* access modifiers changed from: private */
    public boolean getAllChannelsrunning = false;
    private final Set<Long> mBrowsableUpdateChannelIds = new HashSet();
    CommonIntegration mCI;
    /* access modifiers changed from: private */
    public TIFChannelInfo.DefaultComparator mChannelComparator;
    private final Map<String, MutableInt> mChannelCountMap = new HashMap();
    private ContentObserver mChannelObserver;
    /* access modifiers changed from: private */
    public final Map<Long, ChannelWrapper> mChannelWrapperMap = new HashMap();
    /* access modifiers changed from: private */
    public final List<TIFChannelInfo> mChannels = new CopyOnWriteArrayList();
    private volatile List<TIFChannelInfo> mChannelsFor3RDSource = new CopyOnWriteArrayList();
    private volatile List<TIFChannelInfo> mChannelsForCurrentSVL = new CopyOnWriteArrayList();
    private volatile List<TIFChannelInfo> mChannelsForCurrentSVLBase = new CopyOnWriteArrayList();
    /* access modifiers changed from: private */
    public QueryAllChannelsTask mChannelsUpdateTask;
    /* access modifiers changed from: private */
    public TvSurfaceView.BlockChecker mChecker;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final Executor mDbExecutor = TvSingletons.getSingletons().getDbExecutor();
    /* access modifiers changed from: private */
    public boolean mDbLoadFinished;
    private volatile List<TIFChannelInfo> mFindResultForChannels = new ArrayList();
    private final boolean mGetApiChannelFromSvlRecd = true;
    /* access modifiers changed from: private */
    public Handler mHandler;
    /* access modifiers changed from: private */
    public final TvInputManagerHelper mInputManager = TvSingletons.getSingletons().getTvInputManagerHelper();
    private final Set<Listener> mListeners = new CopyOnWriteArraySet();
    private final Set<Long> mLockedUpdateChannelIds = new HashSet();
    /* access modifiers changed from: private */
    public final List<Runnable> mPostRunnablesAfterChannelUpdate = new ArrayList();
    private SharedPreferences mSharedPreferences;
    private boolean mStarted;
    private final TvInputManager.TvInputCallback mTvInputCallback = new TvInputManager.TvInputCallback() {
        public void onInputAdded(String inputId) {
        }

        public void onInputRemoved(String inputId) {
        }
    };
    Map<Integer, MtkTvChannelInfoBase> maps = new HashMap();
    /* access modifiers changed from: private */
    public boolean upDateAllChannelsrunning = false;

    public interface ChannelListener {
        void onChannelRemoved(TIFChannelInfo tIFChannelInfo);

        void onChannelUpdated(TIFChannelInfo tIFChannelInfo);
    }

    public interface Listener {
        void onChannelBrowsableChanged();

        void onChannelListUpdated();

        void onLoadFinished();
    }

    private TIFChannelManager(Context context) {
        this.mContext = context;
        this.mContentResolver = this.mContext.getContentResolver();
        this.mCI = TvSingletons.getSingletons().getCommonIntegration();
        init();
        MtkLog.d(TAG, "TIFChannelManager() mChannels.size() = " + getChannelCount());
    }

    public static synchronized TIFChannelManager getInstance(Context context) {
        TIFChannelManager tIFChannelManager;
        synchronized (TIFChannelManager.class) {
            if (mTIFChannelManagerInstance == null) {
                mTIFChannelManagerInstance = new TIFChannelManager(context);
            }
            tIFChannelManager = mTIFChannelManagerInstance;
        }
        return tIFChannelManager;
    }

    public ContentResolver getContentResolver() {
        if (this.mContentResolver == null) {
            return this.mContext.getContentResolver();
        }
        return this.mContentResolver;
    }

    public TIFChannelInfo getPreChannelInfo() {
        return getTIFChannelInfoById(this.mCI.getLastChannelId());
    }

    public TIFChannelInfo getCurrChannelInfo() {
        return getTIFChannelInfoById(this.mCI.getCurrentChannelId());
    }

    public TIFChannelInfo getChannelInfoByUri() {
        if (TurnkeyUiMainActivity.getInstance() == null || TurnkeyUiMainActivity.getInstance().getTvView() == null) {
            return null;
        }
        long channelId = TurnkeyUiMainActivity.getInstance().getTvView().getCurrentChannelId();
        MtkLog.d(TAG, "getChannelInfoByUri channelId = " + channelId);
        return getChannel(Long.valueOf(channelId));
    }

    public List<TIFChannelInfo> queryChanelListAll(int attentionMask, int expectValue) {
        MtkLog.d(TAG, "start queryChanelListAll~");
        List<TIFChannelInfo> tIFChannelInfoList = new ArrayList<>();
        List<TIFChannelInfo> mChannelList = getCurrentSVLChannelListBase();
        MtkLog.d(TAG, "queryChanelListAll   mChannelList.size() " + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if (CommonIntegration.isUSRegion() || temTIFChannel.mIsBrowsable) {
                MtkLog.d(TAG, "start queryChanelListAll~ temTIFChannel : " + temTIFChannel);
                MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByChannelId(temTIFChannel.mInternalProviderFlag3);
                if (TIFFunctionUtil.checkChMask(tempApiChannel, attentionMask, expectValue)) {
                    if (CommonIntegration.getInstance().isCurrentSourceATVforEuPA()) {
                        tempApiChannel.setChannelNumber(CommonIntegration.getInstance().getAnalogChannelDisplayNumInt(tempApiChannel.getChannelNumber()));
                    }
                    temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                    tIFChannelInfoList.add(temTIFChannel);
                }
            }
        }
        MtkLog.d(TAG, "end queryChanelListAll  tIFChannelInfoList>>>" + tIFChannelInfoList.size());
        return tIFChannelInfoList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0020, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> queryRegionChanelListAll(int r10, int r11) {
        /*
            r9 = this;
            java.lang.String r0 = "TIFChannelManager"
            java.lang.String r1 = "start queryChanelListAll~"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r2 = r9.getSvlIdSelectionArgs()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r3 != 0) goto L_0x0022
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r3 == 0) goto L_0x0028
        L_0x0022:
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?"
            java.lang.String[] r2 = r9.getSvlIdAndInputIdSelectionArgs()
        L_0x0028:
            android.content.ContentResolver r3 = r9.mContentResolver
            android.net.Uri r4 = android.media.tv.TvContract.Channels.CONTENT_URI
            r5 = 0
            java.lang.String r8 = "substr(cast(internal_provider_data as varchar),19,10)"
            r6 = r1
            r7 = r2
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)
            if (r3 != 0) goto L_0x0038
            return r0
        L_0x0038:
            java.lang.String r4 = "TIFChannelManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "queryChanelListAll   c>>>"
            r5.append(r6)
            int r6 = r3.getCount()
            r5.append(r6)
            java.lang.String r6 = "   ORDERBY>>"
            r5.append(r6)
            java.lang.String r6 = "substr(cast(internal_provider_data as varchar),19,10)"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            r4 = 0
        L_0x005d:
            boolean r5 = r3.moveToNext()
            if (r5 == 0) goto L_0x0097
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r4 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r3)
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r5 != 0) goto L_0x0072
            boolean r5 = r4.mIsBrowsable
            if (r5 != 0) goto L_0x0072
            goto L_0x005d
        L_0x0072:
            long[] r5 = r4.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = r9.getAPIChannelInfoByBlobData(r5)
            boolean r6 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r5, (int) r10, (int) r11)
            if (r6 == 0) goto L_0x0096
            r4.mMtkTvChannelInfo = r5
            boolean r6 = r5 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r6 == 0) goto L_0x0096
            r6 = r5
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r6 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r6
            java.lang.String r7 = r6.getSvcProName()
            java.lang.String r8 = "ORF [Region"
            boolean r8 = r7.startsWith(r8)
            if (r8 == 0) goto L_0x0096
            r0.add(r4)
        L_0x0096:
            goto L_0x005d
        L_0x0097:
            r3.close()
            java.lang.String r5 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "end queryChanelListAll  tIFChannelInfoList>>>"
            r6.append(r7)
            int r7 = r0.size()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.queryRegionChanelListAll(int, int):java.util.ArrayList");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x001b, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x001d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo getFirstChannelForScan() {
        /*
            r8 = this;
            java.lang.String r0 = "TIFChannelManager"
            java.lang.String r1 = "start getFirstChannelForScan~"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            java.lang.String r0 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r1 = r8.getSvlIdSelectionArgs()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r8.mCI
            boolean r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r2 != 0) goto L_0x001d
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r8.mCI
            boolean r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r2 == 0) goto L_0x0023
        L_0x001d:
            java.lang.String r0 = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?"
            java.lang.String[] r1 = r8.getSvlIdAndInputIdSelectionArgs()
        L_0x0023:
            android.content.ContentResolver r2 = r8.mContentResolver
            android.net.Uri r3 = android.media.tv.TvContract.Channels.CONTENT_URI
            r4 = 0
            java.lang.String r7 = "substr(cast(internal_provider_data as varchar),19,10)"
            r5 = r0
            r6 = r1
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)
            r3 = 0
            if (r2 != 0) goto L_0x0034
            return r3
        L_0x0034:
            java.lang.String r4 = "TIFChannelManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getFirstChannelForScan   c>>>"
            r5.append(r6)
            int r6 = r2.getCount()
            r5.append(r6)
            java.lang.String r6 = "   ORDERBY>>"
            r5.append(r6)
            java.lang.String r6 = "substr(cast(internal_provider_data as varchar),19,10)"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            r4 = r3
        L_0x0059:
            boolean r5 = r2.moveToNext()
            if (r5 == 0) goto L_0x009b
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r4 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r2)
            boolean r5 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r5 != 0) goto L_0x006e
            boolean r5 = r4.mIsBrowsable
            if (r5 != 0) goto L_0x006e
            goto L_0x0059
        L_0x006e:
            long[] r5 = r4.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = r8.getAPIChannelInfoByBlobData(r5)
            int r6 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_LIST_MASK
            int r7 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_LIST_VAL
            boolean r6 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r5, (int) r6, (int) r7)
            if (r6 == 0) goto L_0x009a
            r4.mMtkTvChannelInfo = r5
            java.lang.String r3 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "end getFirstChannelForScan  temTIFChannel>>>"
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r3, r6)
            r2.close()
            return r4
        L_0x009a:
            goto L_0x0059
        L_0x009b:
            r2.close()
            java.lang.String r5 = "TIFChannelManager"
            java.lang.String r6 = "end getFirstChannelForScan null>>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getFirstChannelForScan():com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo");
    }

    public ArrayList<TIFChannelInfo> queryRegionChannelForHDAustria() {
        ArrayList<TIFChannelInfo> result = new ArrayList<>();
        List<MtkTvChannelInfoBase> channelBaseList = CommonIntegration.getInstance().getChannelListByMaskFilter(0, 0, CommonIntegration.getInstance().getChannelAllNumByAPI(), CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
        List<TIFChannelInfo> tifChannelInfos = TIFFunctionUtil.getTIFChannelList(channelBaseList);
        MtkLog.d(TAG, "size:" + channelBaseList.size());
        for (TIFChannelInfo tifChannelInfo : tifChannelInfos) {
            if (tifChannelInfo.mMtkTvChannelInfo instanceof MtkTvDvbChannelInfo) {
                String svcProName = ((MtkTvDvbChannelInfo) tifChannelInfo.mMtkTvChannelInfo).getSvcProName();
                Log.d("===", "svcProName=" + svcProName);
                if (svcProName.startsWith("ORF [Region")) {
                    result.add(tifChannelInfo);
                }
            }
        }
        return result;
    }

    public Map<String, List<TIFChannelInfo>> queryRegionChannelForDiveo() {
        Map<String, List<TIFChannelInfo>> result = new LinkedHashMap<>();
        result.put("BR Fernsehen", new ArrayList());
        result.put("MDR", new ArrayList());
        result.put("NDR", new ArrayList());
        result.put("RBB", new ArrayList());
        result.put("SWR", new ArrayList());
        result.put("WDR", new ArrayList());
        List<MtkTvChannelInfoBase> channelBaseList = CommonIntegration.getInstance().getChannelListByMaskFilter(0, 0, CommonIntegration.getInstance().getChannelAllNumByAPI(), CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
        List<TIFChannelInfo> tifChannelInfos = TIFFunctionUtil.getTIFChannelList(channelBaseList);
        MtkLog.d(TAG, "size:" + channelBaseList.size());
        for (TIFChannelInfo tifChannelInfo : tifChannelInfos) {
            if (tifChannelInfo.mMtkTvChannelInfo instanceof MtkTvDvbChannelInfo) {
                String svcProName = ((MtkTvDvbChannelInfo) tifChannelInfo.mMtkTvChannelInfo).getSvcProName();
                if (svcProName.startsWith("BR [Region")) {
                    result.get("BR Fernsehen").add(tifChannelInfo);
                } else if (svcProName.startsWith("MDR [Region")) {
                    result.get("MDR").add(tifChannelInfo);
                } else if (svcProName.startsWith("NDR [Region")) {
                    result.get("NDR").add(tifChannelInfo);
                } else if (svcProName.startsWith("RBB [Region")) {
                    result.get("RBB").add(tifChannelInfo);
                } else if (svcProName.startsWith("SWR [Region")) {
                    result.get("SWR").add(tifChannelInfo);
                } else if (svcProName.startsWith("WDR [Region")) {
                    result.get("WDR").add(tifChannelInfo);
                }
            }
        }
        Iterator<Map.Entry<String, List<TIFChannelInfo>>> iterator = result.entrySet().iterator();
        while (iterator.hasNext()) {
            if (((List) iterator.next().getValue()).size() == 0) {
                iterator.remove();
            }
        }
        return result;
    }

    public TIFChannelInfo queryChannelById(int id) {
        TIFChannelInfo tifChannelInfo = null;
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, "_id=" + id + " or " + "internal_provider_flag3" + "=" + id, (String[]) null, (String) null);
        if (c == null) {
            return null;
        }
        while (c.moveToNext()) {
            tifChannelInfo = TIFChannelInfo.parse(c);
        }
        c.close();
        MtkLog.d(TAG, "queryChannelById:" + tifChannelInfo);
        return tifChannelInfo;
    }

    public TIFChannelInfo getHideChannelById(int channelId) {
        TIFChannelInfo tifChannelInfo = null;
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID_CHANNELID, getSvlIdAndChannelIdSelectionArgs(channelId), ORDERBY);
        if (c == null) {
            return null;
        }
        while (c.moveToNext()) {
            tifChannelInfo = TIFChannelInfo.parse(c);
            tifChannelInfo.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(tifChannelInfo.mDataValue);
        }
        c.close();
        MtkLog.d(TAG, "getHideChannelById:" + tifChannelInfo);
        return tifChannelInfo;
    }

    public List<TIFChannelInfo> queryChanelListWithMaskCount(int count, int attentionMask, int expectValue) {
        return queryChanelListWithMaskCount(count, attentionMask, expectValue, false);
    }

    public Map<Integer, MtkTvChannelInfoBase> getAllChannels() {
        MtkLog.d(TAG, "getAllChannels:");
        this.getAllChannelsrunning = true;
        this.upDateAllChannelsrunning = false;
        List<MtkTvChannelInfoBase> mtkInfoList = this.mCI.getChannelListForMap();
        MtkLog.d(TAG, "getAllChannels mtkInfoList.size: " + mtkInfoList.size());
        Map<Integer, MtkTvChannelInfoBase> mapstemp = new HashMap<>();
        if (mtkInfoList != null && mtkInfoList.size() > 0) {
            for (MtkTvChannelInfoBase info : mtkInfoList) {
                mapstemp.put(Integer.valueOf(info.getChannelId()), info);
            }
        }
        this.maps.putAll(mapstemp);
        MtkLog.d(TAG, "getAllChannels maps.size: " + this.maps.size());
        this.getAllChannelsrunning = false;
        if (this.afterGetAllChanelsGoonsort) {
            afterGetAllChanelsGoonsort();
        }
        if (this.upDateAllChannelsrunning) {
            getAllChannels();
        }
        return this.maps;
    }

    public List<TIFChannelInfo> queryChanelListWithMaskCount(int count, int attentionMask, int expectValue, boolean showSkip) {
        List<TIFChannelInfo> mChannelList;
        MtkLog.d(TAG, "start queryChanelListWithMaskCount~" + count + " attentionMask: " + attentionMask + " expectValue: " + expectValue);
        List<TIFChannelInfo> tIFChannelInfoList = new ArrayList<>();
        if (count <= 0 || (mChannelList = getChannelListForFindOrNomal()) == null || mChannelList.size() == 0) {
            return tIFChannelInfoList;
        }
        MtkLog.d(TAG, "queryChanelListWithMask  mChannelList.size " + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if (CommonIntegration.isUSRegion() || temTIFChannel.mIsBrowsable) {
                if (attentionMask == -1 && expectValue == -1) {
                    if ((temTIFChannel.mDataValue == null || temTIFChannel.mDataValue.length != 9) && temTIFChannel.mIsBrowsable) {
                        MtkLog.d(TAG, "queryChanelListWithMask  3rd channel temTIFChannel: " + temTIFChannel);
                        tIFChannelInfoList.add(temTIFChannel);
                        if (tIFChannelInfoList.size() == count) {
                            break;
                        }
                    }
                } else if (!this.mCI.isDisableColorKey() || ((temTIFChannel.mDataValue != null && temTIFChannel.mDataValue.length == 9) || !temTIFChannel.mIsBrowsable)) {
                    MtkLog.d(TAG, "queryChanelListWithMask  not 3rd channel temTIFChannel: " + temTIFChannel);
                    temTIFChannel.mMtkTvChannelInfo = getAPIChannelInfoByChannelId(temTIFChannel.mInternalProviderFlag3);
                    if (temTIFChannel.mMtkTvChannelInfo != null && (showSkip || !temTIFChannel.mMtkTvChannelInfo.isSkip() || temTIFChannel.mMtkTvChannelInfo.getChannelId() == this.mCI.getCurrentChannelId())) {
                        if (TIFFunctionUtil.checkChMask(temTIFChannel.mMtkTvChannelInfo, attentionMask, expectValue)) {
                            tIFChannelInfoList.add(temTIFChannel);
                            if (tIFChannelInfoList.size() == count) {
                                break;
                            }
                        } else if (TIFFunctionUtil.checkChMask(temTIFChannel.mMtkTvChannelInfo, TIFFunctionUtil.CH_FAKE_MASK, TIFFunctionUtil.CH_FAKE_VAL)) {
                            tIFChannelInfoList.add(temTIFChannel);
                            if (tIFChannelInfoList.size() == count) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    MtkLog.d(TAG, "queryChanelListWithMask isDisableColorKey 3rd channel temTIFChannel: " + temTIFChannel);
                    tIFChannelInfoList.add(temTIFChannel);
                    if (tIFChannelInfoList.size() == count) {
                        break;
                    }
                }
            }
        }
        MtkLog.d(TAG, "end queryChanelListWithMaskCount  tIFChannelInfoList>>>" + tIFChannelInfoList.size());
        return tIFChannelInfoList;
    }

    public List<TIFChannelInfo> getTIFChannelListByWhereCondition(String[] projection, String selection, String[] selectionArgs, String order, int attentionMask, int expectValue) {
        MtkLog.d(TAG, "start getTIFChannelListByWhereCondition~" + selection);
        List<TIFChannelInfo> tIFChannelInfoList = new ArrayList<>();
        int svlId = this.mCI.getSvl();
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, projection, selection, selectionArgs, order);
        if (c == null) {
            return tIFChannelInfoList;
        }
        MtkLog.d(TAG, "getTIFChannelListByWhereCondition c>>>>" + c.getCount());
        while (c.moveToNext()) {
            TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
            if ((CommonIntegration.isUSRegion() || temTIFChannel.mIsBrowsable) && ((long) svlId) == temTIFChannel.mDataValue[0]) {
                MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
                if (TIFFunctionUtil.checkChMask(tempApiChannel, attentionMask, expectValue)) {
                    temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                    tIFChannelInfoList.add(temTIFChannel);
                }
            }
        }
        c.close();
        MtkLog.d(TAG, "end getTIFChannelListByWhereCondition  tIFChannelInfoList>>>" + tIFChannelInfoList.size());
        return tIFChannelInfoList;
    }

    public List<TIFChannelInfo> getAllDTVTIFChannels() {
        MtkTvChannelInfoBase temvpApiChannel;
        MtkLog.d(TAG, "start getAllDTVTIFChannels~");
        List<TIFChannelInfo> tIFChannelInfoList = new ArrayList<>();
        List<TIFChannelInfo> mChannelList = getChannelListForFindOrNomal();
        MtkLog.d(TAG, "getAllDTVTIFChannels mChannelList.size()>>>> " + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if ((CommonIntegration.isUSRegion() || temTIFChannel.mIsBrowsable) && (temvpApiChannel = getAPIChannelInfoByChannelId(temTIFChannel.mInternalProviderFlag3)) != null && temvpApiChannel.getBrdcstType() != 1 && TIFFunctionUtil.checkChMask(temvpApiChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                temTIFChannel.mMtkTvChannelInfo = temvpApiChannel;
                tIFChannelInfoList.add(temTIFChannel);
            }
        }
        MtkLog.d(TAG, "end getAllDTVTIFChannels  tIFChannelInfoList>>>" + tIFChannelInfoList.size());
        return tIFChannelInfoList;
    }

    public List<TIFChannelInfo> getAllATVTIFChannels() {
        MtkTvChannelInfoBase temvpApiChannel;
        MtkLog.d(TAG, "start getAllATVTIFChannels~");
        List<TIFChannelInfo> tIFChannelInfoList = new ArrayList<>();
        List<TIFChannelInfo> mChannelList = getCurrentSVLChannelListBase();
        MtkLog.d(TAG, "getAllATVTIFChannels mChannelList.size()>>>> " + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if ((CommonIntegration.isUSRegion() || temTIFChannel.mIsBrowsable) && (temvpApiChannel = getAPIChannelInfoByChannelId(temTIFChannel.mInternalProviderFlag3)) != null && temvpApiChannel.getBrdcstType() == 1 && TIFFunctionUtil.checkChMask(temvpApiChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                temTIFChannel.mMtkTvChannelInfo = temvpApiChannel;
                tIFChannelInfoList.add(temTIFChannel);
            }
        }
        MtkLog.d(TAG, "end getAllATVTIFChannels  tIFChannelInfoList>>>" + tIFChannelInfoList.size());
        return tIFChannelInfoList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:116:0x031e, code lost:
        r19 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x03ad, code lost:
        r21 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0584, code lost:
        r25 = r8;
        r24 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0692, code lost:
        r25 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00f9, code lost:
        r19 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x07f5, code lost:
        r19 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0885, code lost:
        if (r8.mDataValue.length != 9) goto L_0x088a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0244, code lost:
        r20 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> getTIFPreOrNextChannelList(int r29, boolean r30, boolean r31, int r32, int r33, int r34) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r31
            r4 = r32
            r5 = r33
            r6 = r34
            java.lang.String r7 = "TIFChannelManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "start getTIFPreOrNextChannelList>"
            r8.append(r9)
            r8.append(r1)
            java.lang.String r9 = ">>"
            r8.append(r9)
            r8.append(r2)
            java.lang.String r9 = ">>"
            r8.append(r9)
            r8.append(r3)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r7, r8)
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            if (r4 > 0) goto L_0x003c
            return r7
        L_0x003c:
            java.lang.String r8 = "TIFChannelManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "start getTIFPreOrNextChannelList  attentionMask: "
            r9.append(r10)
            r9.append(r5)
            java.lang.String r10 = " expectValue: "
            r9.append(r10)
            r9.append(r6)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            long r8 = (long) r1
            r10 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r8 = r8 & r10
            int r8 = (int) r8
            long r8 = (long) r8
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = -1
            if (r1 != r13) goto L_0x006a
            r12 = 1
        L_0x006a:
            java.util.List r14 = r28.getChannelListForFindOrNomal()
            java.lang.String r15 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r16 = r10
            java.lang.String r10 = "getTIFPreOrNextChannelList isPrePage>>>"
            r13.append(r10)
            r13.append(r2)
            java.lang.String r10 = "  mChannelList.size() = "
            r13.append(r10)
            int r10 = r14.size()
            r13.append(r10)
            java.lang.String r10 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r10)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r10 = r0.mCI
            int r10 = r10.getCurrentChannelId()
            if (r2 == 0) goto L_0x0522
            java.lang.String r15 = "TIFChannelManager"
            java.lang.String r13 = "getTIFPreOrNextChannelList temTIFChannel isPrePage"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r13)
            r13 = 0
            int r15 = r14.size()
            int r15 = r15 + -1
        L_0x00a8:
            if (r15 < 0) goto L_0x033a
            java.lang.Object r17 = r14.get(r15)
            r13 = r17
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r13 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r13
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r18 = r14
            java.lang.String r14 = "1 getTIFPreOrNextChannelList temTIFChannel = "
            r1.append(r14)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r1)
            if (r12 == 0) goto L_0x0203
            r1 = -1
            if (r5 != r1) goto L_0x00fd
            if (r6 != r1) goto L_0x00fd
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r2 = "end 1 getTIFPreOrNextChannelList> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x00e3
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r2 = 9
            if (r1 == r2) goto L_0x00eb
        L_0x00e3:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x00eb
            r1 = 0
            r7.add(r1, r13)
        L_0x00eb:
            int r1 = r7.size()
            if (r1 != r4) goto L_0x0108
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r2 = "end 1 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
        L_0x00f9:
            r19 = r12
            goto L_0x033e
        L_0x00fd:
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r1 != 0) goto L_0x010c
            boolean r1 = r13.mIsBrowsable
            if (r1 != 0) goto L_0x010c
        L_0x0108:
            r19 = r12
            goto L_0x01ff
        L_0x010c:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r0.mCI
            boolean r1 = r1.isDisableColorKey()
            if (r1 == 0) goto L_0x0144
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x011f
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r2 = 9
            if (r1 == r2) goto L_0x0144
        L_0x011f:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x0144
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "getTIFPreOrNextChannelList isDisableColorKey 3rd channel temTIFChannel: "
            r2.append(r14)
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            r1 = 0
            r7.add(r1, r13)
            int r1 = r7.size()
            if (r1 != r4) goto L_0x0108
            goto L_0x00f9
        L_0x0144:
            int r1 = r13.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByChannelId(r1)
            if (r1 == 0) goto L_0x01fd
            boolean r2 = r1.isSkip()
            if (r2 == 0) goto L_0x0159
            int r2 = r1.getChannelId()
            if (r2 == r10) goto L_0x0159
            goto L_0x0108
        L_0x0159:
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r19 = r12
            java.lang.String r12 = "tempApiChannel>>>"
            r14.append(r12)
            int r12 = r1.getChannelId()
            r14.append(r12)
            java.lang.String r12 = "   >"
            r14.append(r12)
            int r12 = r1.getChannelNumber()
            r14.append(r12)
            java.lang.String r12 = "  >"
            r14.append(r12)
            java.lang.String r12 = r1.getServiceName()
            r14.append(r12)
            java.lang.String r12 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r5, (int) r6)
            if (r2 == 0) goto L_0x01dd
            int r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r1, r2, r12)
            if (r2 == 0) goto L_0x01fc
            r13.mMtkTvChannelInfo = r1
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r2 = r2.isCurrentSourceATVforEuPA()
            if (r2 == 0) goto L_0x01ca
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r12 = ""
            r2.append(r12)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r14 = r13.mDisplayNumber
            int r12 = r12.getAnalogChannelDisplayNumInt((java.lang.String) r14)
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            r13.mDisplayNumber = r2
        L_0x01ca:
            r2 = 0
            r7.add(r2, r13)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x01fc
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r12 = "end 1 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            goto L_0x033e
        L_0x01dd:
            int r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r2, (int) r12)
            if (r2 == 0) goto L_0x01fc
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r12 = "end 1 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            r13.mMtkTvChannelInfo = r1
            r2 = 0
            r7.add(r2, r13)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x01fc
            goto L_0x033e
        L_0x01fc:
            goto L_0x01ff
        L_0x01fd:
            r19 = r12
        L_0x01ff:
            r12 = r19
            goto L_0x0330
        L_0x0203:
            r19 = r12
            if (r13 == 0) goto L_0x032c
            int r1 = r13.mInternalProviderFlag3
            long r1 = (long) r1
            int r1 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x0214
            long r1 = r13.mId
            int r1 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x032c
        L_0x0214:
            r12 = 1
            if (r3 == 0) goto L_0x0327
            r1 = -1
            if (r5 != r1) goto L_0x024c
            if (r6 != r1) goto L_0x024c
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r2 = "end 2 getTIFPreOrNextChannelList> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x022e
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r2 = 9
            if (r1 == r2) goto L_0x0236
        L_0x022e:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x0236
            r1 = 0
            r7.add(r1, r13)
        L_0x0236:
            int r1 = r7.size()
            if (r1 != r4) goto L_0x0248
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r2 = "end 2 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
        L_0x0244:
            r20 = r12
            goto L_0x031e
        L_0x0248:
            r20 = r12
            goto L_0x0324
        L_0x024c:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r0.mCI
            boolean r1 = r1.isDisableColorKey()
            if (r1 == 0) goto L_0x0284
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x025f
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r2 = 9
            if (r1 == r2) goto L_0x0284
        L_0x025f:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x0284
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "getTIFPreOrNextChannelList isDisableColorKey 3rd channel temTIFChannel: "
            r2.append(r14)
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            r1 = 0
            r7.add(r1, r13)
            int r1 = r7.size()
            if (r1 != r4) goto L_0x0248
            goto L_0x0244
        L_0x0284:
            int r1 = r13.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByChannelId(r1)
            if (r1 == 0) goto L_0x0322
            boolean r2 = r1.isSkip()
            if (r2 == 0) goto L_0x0299
            int r2 = r1.getChannelId()
            if (r2 == r10) goto L_0x0299
            goto L_0x0248
        L_0x0299:
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r20 = r12
            java.lang.String r12 = "tempApiChannel>>>"
            r14.append(r12)
            r14.append(r1)
            java.lang.String r12 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r5, (int) r6)
            if (r2 == 0) goto L_0x0300
            int r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r1, r2, r12)
            if (r2 == 0) goto L_0x0321
            r13.mMtkTvChannelInfo = r1
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r2 = r2.isCurrentSourceATVforEuPA()
            if (r2 == 0) goto L_0x02ee
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r12 = ""
            r2.append(r12)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r14 = r13.mDisplayNumber
            int r12 = r12.getAnalogChannelDisplayNumInt((java.lang.String) r14)
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            r13.mDisplayNumber = r2
        L_0x02ee:
            r2 = 0
            r7.add(r2, r13)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x0321
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r12 = "end 2 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            goto L_0x031e
        L_0x0300:
            int r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r2, (int) r12)
            if (r2 == 0) goto L_0x0321
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r12 = "end 2 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r12)
            r13.mMtkTvChannelInfo = r1
            r2 = 0
            r7.add(r2, r13)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x0321
        L_0x031e:
            r19 = r20
            goto L_0x033e
        L_0x0321:
            goto L_0x0324
        L_0x0322:
            r20 = r12
        L_0x0324:
            r12 = r20
            goto L_0x0330
        L_0x0327:
            r20 = r12
            int r11 = r11 + 1
            goto L_0x0330
        L_0x032c:
            int r11 = r11 + 1
            r12 = r19
        L_0x0330:
            int r15 = r15 + -1
            r14 = r18
            r1 = r29
            r2 = r30
            goto L_0x00a8
        L_0x033a:
            r19 = r12
            r18 = r14
        L_0x033e:
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r12 = "pretIFChannelInfoList>>>"
            r2.append(r12)
            int r12 = r7.size()
            r2.append(r12)
            java.lang.String r12 = "   afterStartIdCount>>"
            r2.append(r12)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            int r1 = r7.size()
            if (r1 >= r4) goto L_0x0516
            if (r11 <= 0) goto L_0x0516
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r2 = "getTIFPreOrNextChannelList first not get page channel, this is second"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r2)
            r1 = r18
            int r2 = r1.size()
            int r2 = r2 + -1
        L_0x0377:
            if (r2 < 0) goto L_0x0513
            java.lang.Object r12 = r1.get(r2)
            r13 = r12
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r13 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r13
            r12 = -1
            if (r5 != r12) goto L_0x03b1
            if (r6 != r12) goto L_0x03b1
            java.lang.String r12 = "TIFChannelManager"
            java.lang.String r14 = "end 3 getTIFPreOrNextChannelList>3rd 3"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r14)
            long[] r12 = r13.mDataValue
            if (r12 == 0) goto L_0x0397
            long[] r12 = r13.mDataValue
            int r12 = r12.length
            r14 = 9
            if (r12 == r14) goto L_0x039f
        L_0x0397:
            boolean r12 = r13.mIsBrowsable
            if (r12 == 0) goto L_0x039f
            r12 = 0
            r7.add(r12, r13)
        L_0x039f:
            int r12 = r7.size()
            if (r12 != r4) goto L_0x03bc
            java.lang.String r12 = "TIFChannelManager"
            java.lang.String r14 = "end 3 getTIFPreOrNextChannelList>3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r14)
        L_0x03ad:
            r21 = r11
            goto L_0x051a
        L_0x03b1:
            boolean r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r12 != 0) goto L_0x03c1
            boolean r12 = r13.mIsBrowsable
            if (r12 != 0) goto L_0x03c1
        L_0x03bc:
            r21 = r11
        L_0x03be:
            r11 = 0
            goto L_0x050d
        L_0x03c1:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = r0.mCI
            boolean r12 = r12.isDisableColorKey()
            if (r12 == 0) goto L_0x03f9
            long[] r12 = r13.mDataValue
            if (r12 == 0) goto L_0x03d4
            long[] r12 = r13.mDataValue
            int r12 = r12.length
            r14 = 9
            if (r12 == r14) goto L_0x03f9
        L_0x03d4:
            boolean r12 = r13.mIsBrowsable
            if (r12 == 0) goto L_0x03f9
            java.lang.String r12 = "TIFChannelManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "getTIFPreOrNextChannelList isDisableColorKey 3rd channel temTIFChannel: "
            r14.append(r15)
            r14.append(r13)
            java.lang.String r14 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r14)
            r12 = 0
            r7.add(r12, r13)
            int r12 = r7.size()
            if (r12 != r4) goto L_0x03bc
            goto L_0x03ad
        L_0x03f9:
            int r12 = r13.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r12 = r0.getAPIChannelInfoByChannelId(r12)
            if (r12 == 0) goto L_0x050a
            boolean r14 = r12.isSkip()
            if (r14 == 0) goto L_0x040e
            int r14 = r12.getChannelId()
            if (r14 == r10) goto L_0x040e
            goto L_0x03bc
        L_0x040e:
            java.lang.String r14 = "TIFChannelManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r21 = r11
            java.lang.String r11 = "tempApiChannel>>>"
            r15.append(r11)
            int r11 = r12.getChannelId()
            r15.append(r11)
            java.lang.String r11 = "   >"
            r15.append(r11)
            int r11 = r12.getChannelNumber()
            r15.append(r11)
            java.lang.String r11 = "  >"
            r15.append(r11)
            java.lang.String r11 = r12.getServiceName()
            r15.append(r11)
            java.lang.String r11 = r15.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r14, r11)
            int r11 = r12.getChannelId()
            r14 = r29
            if (r11 == r14) goto L_0x04bf
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r12, (int) r5, (int) r6)
            if (r11 == 0) goto L_0x049a
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r15 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r12, r11, r15)
            if (r11 == 0) goto L_0x03be
            r13.mMtkTvChannelInfo = r12
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r11 = r11.isCurrentSourceATVforEuPA()
            if (r11 == 0) goto L_0x0487
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r15 = ""
            r11.append(r15)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r15 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r14 = r13.mDisplayNumber
            int r14 = r15.getAnalogChannelDisplayNumInt((java.lang.String) r14)
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            r13.mDisplayNumber = r11
        L_0x0487:
            r11 = 0
            r7.add(r11, r13)
            int r11 = r7.size()
            if (r11 != r4) goto L_0x03be
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r14 = "end 3 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r14)
            goto L_0x051a
        L_0x049a:
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r14 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r12, (int) r11, (int) r14)
            if (r11 == 0) goto L_0x03be
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r14 = "end 3 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r14)
            r13.mMtkTvChannelInfo = r12
            r11 = 0
            r7.add(r11, r13)
            int r11 = r7.size()
            if (r11 != r4) goto L_0x03be
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r14 = "end 3 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r14)
            goto L_0x051a
        L_0x04bf:
            if (r3 != 0) goto L_0x051a
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r12, (int) r5, (int) r6)
            if (r11 == 0) goto L_0x051a
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r14 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r12, r11, r14)
            if (r11 == 0) goto L_0x051a
            r13.mMtkTvChannelInfo = r12
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r11 = r11.isCurrentSourceATVforEuPA()
            if (r11 == 0) goto L_0x04fe
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r14 = ""
            r11.append(r14)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r14 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r15 = r13.mDisplayNumber
            int r14 = r14.getAnalogChannelDisplayNumInt((java.lang.String) r15)
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            r13.mDisplayNumber = r11
        L_0x04fe:
            r11 = 0
            r7.add(r11, r13)
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r14 = "end 4 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r14)
            goto L_0x051a
        L_0x050a:
            r21 = r11
            r11 = 0
        L_0x050d:
            int r2 = r2 + -1
            r11 = r21
            goto L_0x0377
        L_0x0513:
            r21 = r11
            goto L_0x051a
        L_0x0516:
            r21 = r11
            r1 = r18
        L_0x051a:
            r25 = r8
            r13 = r16
            r14 = r29
            goto L_0x09c4
        L_0x0522:
            r1 = r14
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r13 = "temTIFChannel nextpage"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r13)
            java.util.Iterator r2 = r1.iterator()
            r13 = r16
        L_0x0530:
            boolean r14 = r2.hasNext()
            if (r14 == 0) goto L_0x07ef
            java.lang.Object r14 = r2.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r14 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r14
            java.lang.String r15 = "TIFChannelManager"
            r22 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r23 = r11
            java.lang.String r11 = "getTIFPreOrNextChannelList  nextpage temTIFChannel is "
            r2.append(r11)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r15, r2)
            if (r12 == 0) goto L_0x06a7
            r2 = -1
            if (r5 != r2) goto L_0x058e
            if (r6 != r2) goto L_0x058e
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "end 5 getTIFPreOrNextChannelList>3rd 5"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            long[] r2 = r14.mDataValue
            if (r2 == 0) goto L_0x056f
            long[] r2 = r14.mDataValue
            int r2 = r2.length
            r11 = 9
            if (r2 == r11) goto L_0x0576
        L_0x056f:
            boolean r2 = r14.mIsBrowsable
            if (r2 == 0) goto L_0x0576
            r7.add(r14)
        L_0x0576:
            int r2 = r7.size()
            if (r2 != r4) goto L_0x058a
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "end 5 getTIFPreOrNextChannelList>3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
        L_0x0584:
            r25 = r8
            r24 = r12
            goto L_0x07f5
        L_0x058a:
            r24 = r12
            goto L_0x0697
        L_0x058e:
            if (r14 != 0) goto L_0x059c
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "temTIFChannel is null!"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
        L_0x0598:
            r24 = r12
            goto L_0x069f
        L_0x059c:
            boolean r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r2 != 0) goto L_0x05a7
            boolean r2 = r14.mIsBrowsable
            if (r2 != 0) goto L_0x05a7
            goto L_0x0598
        L_0x05a7:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r0.mCI
            boolean r2 = r2.isDisableColorKey()
            if (r2 == 0) goto L_0x05de
            long[] r2 = r14.mDataValue
            if (r2 == 0) goto L_0x05ba
            long[] r2 = r14.mDataValue
            int r2 = r2.length
            r11 = 9
            if (r2 == r11) goto L_0x05de
        L_0x05ba:
            boolean r2 = r14.mIsBrowsable
            if (r2 == 0) goto L_0x05de
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r15 = "getTIFPreOrNextChannelList isDisableColorKey nextpage 3rd channel temTIFChannel: "
            r11.append(r15)
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            r7.add(r14)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x0598
            goto L_0x0584
        L_0x05de:
            int r2 = r14.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r2 = r0.getAPIChannelInfoByChannelId(r2)
            if (r2 == 0) goto L_0x069d
            boolean r11 = r2.isSkip()
            if (r11 == 0) goto L_0x05f3
            int r11 = r2.getChannelId()
            if (r11 == r10) goto L_0x05f3
            goto L_0x0598
        L_0x05f3:
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r24 = r12
            java.lang.String r12 = "tempApiChannel>>>"
            r15.append(r12)
            int r12 = r2.getChannelId()
            r15.append(r12)
            java.lang.String r12 = "   >"
            r15.append(r12)
            int r12 = r2.getChannelNumber()
            r15.append(r12)
            java.lang.String r12 = "  >"
            r15.append(r12)
            java.lang.String r12 = r2.getServiceName()
            r15.append(r12)
            java.lang.String r12 = r15.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2, (int) r5, (int) r6)
            if (r11 == 0) goto L_0x0675
            r14.mMtkTvChannelInfo = r2
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r2, r11, r12)
            if (r11 == 0) goto L_0x0696
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r11 = r11.isCurrentSourceATVforEuPA()
            if (r11 == 0) goto L_0x0664
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = ""
            r11.append(r12)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r12 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r15 = r14.mDisplayNumber
            int r12 = r12.getAnalogChannelDisplayNumInt((java.lang.String) r15)
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r14.mDisplayNumber = r11
        L_0x0664:
            r7.add(r14)
            int r11 = r7.size()
            if (r11 != r4) goto L_0x0696
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 5 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            goto L_0x0692
        L_0x0675:
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2, (int) r11, (int) r12)
            if (r11 == 0) goto L_0x0696
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 5 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            r14.mMtkTvChannelInfo = r2
            r7.add(r14)
            int r11 = r7.size()
            if (r11 != r4) goto L_0x0696
        L_0x0692:
            r25 = r8
            goto L_0x07f5
        L_0x0696:
        L_0x0697:
            r25 = r8
            r12 = r24
            goto L_0x07e7
        L_0x069d:
            r24 = r12
        L_0x069f:
            r2 = r22
            r11 = r23
            r12 = r24
            goto L_0x0530
        L_0x06a7:
            r24 = r12
            if (r14 == 0) goto L_0x07e1
            int r2 = r14.mInternalProviderFlag3
            long r11 = (long) r2
            int r2 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x06bd
            long r11 = r14.mId
            int r2 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x06b9
            goto L_0x06bd
        L_0x06b9:
            r25 = r8
            goto L_0x07e3
        L_0x06bd:
            r12 = 1
            if (r3 == 0) goto L_0x07dc
            r2 = -1
            if (r5 != r2) goto L_0x06f4
            if (r6 != r2) goto L_0x06f4
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "end 6 getTIFPreOrNextChannelList>3rd 6"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            long[] r2 = r14.mDataValue
            if (r2 == 0) goto L_0x06d7
            long[] r2 = r14.mDataValue
            int r2 = r2.length
            r11 = 9
            if (r2 == r11) goto L_0x06de
        L_0x06d7:
            boolean r2 = r14.mIsBrowsable
            if (r2 == 0) goto L_0x06de
            r7.add(r14)
        L_0x06de:
            int r2 = r7.size()
            if (r2 != r4) goto L_0x06f0
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "end 6 getTIFPreOrNextChannelList>3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
        L_0x06ec:
            r25 = r8
            goto L_0x07d5
        L_0x06f0:
            r25 = r8
            goto L_0x07e7
        L_0x06f4:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = r0.mCI
            boolean r2 = r2.isDisableColorKey()
            if (r2 == 0) goto L_0x072f
            long[] r2 = r14.mDataValue
            if (r2 == 0) goto L_0x0707
            long[] r2 = r14.mDataValue
            int r2 = r2.length
            r11 = 9
            if (r2 == r11) goto L_0x072f
        L_0x0707:
            boolean r2 = r14.mIsBrowsable
            if (r2 == 0) goto L_0x072f
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r15 = "getTIFPreOrNextChannelList isDisableColorKey nextpage 3rd channel temTIFChannel: "
            r11.append(r15)
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            r7.add(r14)
            int r2 = r7.size()
            if (r2 != r4) goto L_0x072b
            goto L_0x06ec
        L_0x072b:
            r25 = r8
            goto L_0x07e7
        L_0x072f:
            int r2 = r14.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r2 = r0.getAPIChannelInfoByChannelId(r2)
            if (r2 == 0) goto L_0x07d9
            boolean r11 = r2.isSkip()
            if (r11 == 0) goto L_0x0744
            int r11 = r2.getChannelId()
            if (r11 == r10) goto L_0x0744
            goto L_0x072b
        L_0x0744:
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r25 = r8
            java.lang.String r8 = "tempApiChannel>>>"
            r15.append(r8)
            int r8 = r2.getChannelId()
            r15.append(r8)
            java.lang.String r8 = "   >"
            r15.append(r8)
            int r8 = r2.getChannelNumber()
            r15.append(r8)
            java.lang.String r8 = "  >"
            r15.append(r8)
            java.lang.String r8 = r2.getServiceName()
            r15.append(r8)
            java.lang.String r8 = r15.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r8)
            boolean r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2, (int) r5, (int) r6)
            if (r8 == 0) goto L_0x07b8
            r14.mMtkTvChannelInfo = r2
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r8 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r8 = r8.isCurrentSourceATVforEuPA()
            if (r8 == 0) goto L_0x07a7
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = ""
            r8.append(r9)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r11 = r14.mDisplayNumber
            int r9 = r9.getAnalogChannelDisplayNumInt((java.lang.String) r11)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r14.mDisplayNumber = r8
        L_0x07a7:
            r7.add(r14)
            int r8 = r7.size()
            if (r8 != r4) goto L_0x07d8
            java.lang.String r8 = "TIFChannelManager"
            java.lang.String r9 = "end 6 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            goto L_0x07d5
        L_0x07b8:
            int r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r2, (int) r8, (int) r9)
            if (r8 == 0) goto L_0x07d8
            java.lang.String r8 = "TIFChannelManager"
            java.lang.String r9 = "end 6 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            r14.mMtkTvChannelInfo = r2
            r7.add(r14)
            int r8 = r7.size()
            if (r8 != r4) goto L_0x07d8
        L_0x07d5:
            r19 = r12
            goto L_0x07f7
        L_0x07d8:
            goto L_0x07e7
        L_0x07d9:
            r25 = r8
            goto L_0x07e7
        L_0x07dc:
            r25 = r8
            int r13 = r13 + 1
            goto L_0x07e7
        L_0x07e1:
            r25 = r8
        L_0x07e3:
            int r13 = r13 + 1
            r12 = r24
        L_0x07e7:
            r2 = r22
            r11 = r23
            r8 = r25
            goto L_0x0530
        L_0x07ef:
            r25 = r8
            r23 = r11
            r24 = r12
        L_0x07f5:
            r19 = r24
        L_0x07f7:
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "nexttIFChannelInfoList>>>"
            r8.append(r9)
            int r9 = r7.size()
            r8.append(r9)
            java.lang.String r9 = "   beforeStartIdCount>>"
            r8.append(r9)
            r8.append(r13)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r8)
            int r2 = r7.size()
            if (r2 >= r4) goto L_0x09c0
            if (r13 <= 0) goto L_0x09c0
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r8 = "getTIFPreOrNextChannelList 2first not get page channel, this is second"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r8)
            java.util.Iterator r2 = r1.iterator()
        L_0x082c:
            boolean r8 = r2.hasNext()
            if (r8 == 0) goto L_0x09c0
            java.lang.Object r8 = r2.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r8 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r8
            r9 = -1
            if (r5 != r9) goto L_0x0869
            if (r6 != r9) goto L_0x0869
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 6 getTIFPreOrNextChannelList>3rd 6"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            long[] r11 = r8.mDataValue
            if (r11 == 0) goto L_0x084f
            long[] r11 = r8.mDataValue
            int r11 = r11.length
            r12 = 9
            if (r11 == r12) goto L_0x0856
        L_0x084f:
            boolean r11 = r8.mIsBrowsable
            if (r11 == 0) goto L_0x0856
            r7.add(r8)
        L_0x0856:
            int r11 = r7.size()
            if (r11 != r4) goto L_0x0865
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r9 = "end 6 getTIFPreOrNextChannelList>3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r9)
            goto L_0x09c0
        L_0x0865:
            r14 = r29
            goto L_0x0974
        L_0x0869:
            boolean r11 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r11 != 0) goto L_0x0874
            boolean r11 = r8.mIsBrowsable
            if (r11 != 0) goto L_0x0874
            goto L_0x082c
        L_0x0874:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r11 = r0.mCI
            boolean r11 = r11.isDisableColorKey()
            if (r11 == 0) goto L_0x08af
            long[] r11 = r8.mDataValue
            if (r11 == 0) goto L_0x0888
            long[] r11 = r8.mDataValue
            int r11 = r11.length
            r12 = 9
            if (r11 == r12) goto L_0x08b1
            goto L_0x088a
        L_0x0888:
            r12 = 9
        L_0x088a:
            boolean r11 = r8.mIsBrowsable
            if (r11 == 0) goto L_0x08b1
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "getTIFPreOrNextChannelList isDisableColorKey nextpage 3rd channel temTIFChannel: "
            r14.append(r15)
            r14.append(r8)
            java.lang.String r14 = r14.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r14)
            r7.add(r8)
            int r11 = r7.size()
            if (r11 != r4) goto L_0x082c
            goto L_0x09c0
        L_0x08af:
            r12 = 9
        L_0x08b1:
            int r11 = r8.mInternalProviderFlag3
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r0.getAPIChannelInfoByChannelId(r11)
            if (r11 == 0) goto L_0x082c
            boolean r14 = r11.isSkip()
            if (r14 == 0) goto L_0x08c7
            int r14 = r11.getChannelId()
            if (r14 == r10) goto L_0x08c7
            goto L_0x082c
        L_0x08c7:
            java.lang.String r14 = "TIFChannelManager"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r9 = "tempApiChannel>>>"
            r15.append(r9)
            int r9 = r11.getChannelId()
            r15.append(r9)
            java.lang.String r9 = "   >"
            r15.append(r9)
            int r9 = r11.getChannelNumber()
            r15.append(r9)
            java.lang.String r9 = "  >"
            r15.append(r9)
            java.lang.String r9 = r11.getServiceName()
            r15.append(r9)
            java.lang.String r9 = r15.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r14, r9)
            int r9 = r11.getChannelId()
            r14 = r29
            if (r9 == r14) goto L_0x0976
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r5, (int) r6)
            if (r9 == 0) goto L_0x0950
            r8.mMtkTvChannelInfo = r11
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r15 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r11, r9, r15)
            if (r9 == 0) goto L_0x0974
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r9 = r9.isCurrentSourceATVforEuPA()
            if (r9 == 0) goto L_0x093e
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r15 = ""
            r9.append(r15)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r15 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r12 = r8.mDisplayNumber
            int r12 = r15.getAnalogChannelDisplayNumInt((java.lang.String) r12)
            r9.append(r12)
            java.lang.String r9 = r9.toString()
            r8.mDisplayNumber = r9
        L_0x093e:
            r7.add(r8)
            int r9 = r7.size()
            if (r9 != r4) goto L_0x0974
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r9 = "end 7 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r9)
            goto L_0x09c2
        L_0x0950:
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_MASK
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.CH_FAKE_VAL
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r9, (int) r12)
            if (r9 == 0) goto L_0x0974
            java.lang.String r9 = "TIFChannelManager"
            java.lang.String r12 = "end 7 getTIFPreOrNextChannelList> add current fake channel"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r12)
            r8.mMtkTvChannelInfo = r11
            r7.add(r8)
            int r9 = r7.size()
            if (r9 != r4) goto L_0x0974
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r9 = "end 7 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r9)
            goto L_0x09c2
        L_0x0974:
            goto L_0x082c
        L_0x0976:
            if (r3 != 0) goto L_0x09c2
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r5, (int) r6)
            if (r2 == 0) goto L_0x09c2
            int r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r11, r2, r9)
            if (r2 == 0) goto L_0x09c2
            r8.mMtkTvChannelInfo = r11
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r2 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            boolean r2 = r2.isCurrentSourceATVforEuPA()
            if (r2 == 0) goto L_0x09b5
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r9 = ""
            r2.append(r9)
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getInstance()
            java.lang.String r12 = r8.mDisplayNumber
            int r9 = r9.getAnalogChannelDisplayNumInt((java.lang.String) r12)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r8.mDisplayNumber = r2
        L_0x09b5:
            r7.add(r8)
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r9 = "end 8 getTIFPreOrNextChannelList>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r9)
            goto L_0x09c2
        L_0x09c0:
            r14 = r29
        L_0x09c2:
            r21 = r23
        L_0x09c4:
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "end getTIFPreOrNextChannelList  tIFChannelInfoList>>>"
            r8.append(r9)
            int r9 = r7.size()
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r8)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getTIFPreOrNextChannelList(int, boolean, boolean, int, int, int):java.util.List");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:159:0x0359, code lost:
        if (r10.size() == r5) goto L_0x0366;
     */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x033c A[LOOP:3: B:133:0x02ec->B:152:0x033c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x01f0 A[EDGE_INSN: B:168:0x01f0->B:84:0x01f0 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0366 A[EDGE_INSN: B:188:0x0366->B:162:0x0366 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01cd A[LOOP:1: B:55:0x0178->B:75:0x01cd, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> getTIFPreOrNextChannelListBySateRecId(int r33, boolean r34, boolean r35, int r36, int r37, int r38, int r39) {
        /*
            r32 = this;
            r0 = r32
            r1 = r33
            r2 = r34
            r3 = r35
            r4 = r36
            r5 = r37
            r6 = r38
            r7 = r39
            java.lang.String r8 = "TIFChannelManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "start getTIFPreOrNextChannelListBySateRecId>"
            r9.append(r10)
            r9.append(r1)
            java.lang.String r10 = ">>"
            r9.append(r10)
            r9.append(r2)
            java.lang.String r10 = ">>"
            r9.append(r10)
            r9.append(r3)
            java.lang.String r10 = ">>"
            r9.append(r10)
            r9.append(r4)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r9)
            if (r5 > 0) goto L_0x0042
            r8 = 0
            return r8
        L_0x0042:
            long r8 = (long) r1
            r10 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r8 = r8 & r10
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = -1
            if (r1 != r15) goto L_0x0056
            r14 = 1
        L_0x0056:
            android.content.ContentResolver r15 = r0.mContentResolver
            android.net.Uri r16 = android.media.tv.TvContract.Channels.CONTENT_URI
            r17 = 0
            java.lang.String r18 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r19 = r32.getSvlIdSelectionArgs()
            java.lang.String r20 = "substr(cast(internal_provider_data as varchar),19,10)"
            android.database.Cursor r15 = r15.query(r16, r17, r18, r19, r20)
            if (r15 != 0) goto L_0x006b
            return r10
        L_0x006b:
            r21 = r11
            java.lang.String r11 = "TIFChannelManager"
            r22 = r12
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r23 = r13
            java.lang.String r13 = "getTIFPreOrNextChannelListBySateRecId isPrePage>>>"
            r12.append(r13)
            r12.append(r2)
            java.lang.String r13 = "   c>>"
            r12.append(r13)
            int r13 = r15.getCount()
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            if (r2 == 0) goto L_0x01fb
            boolean r12 = r15.moveToLast()
            if (r12 == 0) goto L_0x012f
        L_0x009b:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r15)
            if (r12 != 0) goto L_0x00aa
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r13 = "end 1 getTIFPreOrNextChannelListBySateRecId> temTIFChannel is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r13)
            goto L_0x0122
        L_0x00aa:
            if (r14 == 0) goto L_0x00e0
            long[] r11 = r12.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r0.getAPIChannelInfoByBlobData(r11)
            if (r11 != 0) goto L_0x00b6
            goto L_0x0122
        L_0x00b6:
            boolean r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r6, (int) r7)
            if (r13 == 0) goto L_0x00df
            boolean r13 = r11 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r13 == 0) goto L_0x00df
            r13 = r11
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r13 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r13
            int r2 = r13.getSatRecId()
            if (r2 != r4) goto L_0x00df
            r12.mMtkTvChannelInfo = r11
            r2 = 0
            r10.add(r2, r12)
            int r2 = r10.size()
            if (r2 != r5) goto L_0x00df
            java.lang.String r2 = "TIFChannelManager"
            r24 = r11
            java.lang.String r11 = "end 1 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            goto L_0x0131
        L_0x00df:
            goto L_0x0122
        L_0x00e0:
            long[] r2 = r12.mDataValue
            if (r2 == 0) goto L_0x0120
            long[] r2 = r12.mDataValue
            r11 = 2
            r16 = r2[r11]
            int r2 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x0120
            r14 = 1
            if (r3 == 0) goto L_0x011d
            long[] r2 = r12.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r2 = r0.getAPIChannelInfoByBlobData(r2)
            if (r2 != 0) goto L_0x00f9
            goto L_0x0122
        L_0x00f9:
            boolean r11 = r2 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r11 == 0) goto L_0x011c
            r11 = r2
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r11 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r11
            int r13 = r11.getSatRecId()
            if (r13 != r4) goto L_0x011c
            r12.mMtkTvChannelInfo = r2
            r13 = 0
            r10.add(r13, r12)
            int r13 = r10.size()
            if (r13 != r5) goto L_0x011c
            java.lang.String r13 = "TIFChannelManager"
            r25 = r2
            java.lang.String r2 = "end 2 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r2)
            goto L_0x0131
        L_0x011c:
            goto L_0x0122
        L_0x011d:
            int r23 = r23 + 1
            goto L_0x0122
        L_0x0120:
            int r23 = r23 + 1
        L_0x0122:
            boolean r2 = r15.moveToPrevious()
            if (r2 != 0) goto L_0x0129
            goto L_0x0131
        L_0x0129:
            r21 = r12
            r2 = r34
            goto L_0x009b
        L_0x012f:
            r12 = r21
        L_0x0131:
            r13 = r23
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r26 = r12
            java.lang.String r12 = "pretIFChannelInfoList>>>"
            r11.append(r12)
            int r12 = r10.size()
            r11.append(r12)
            java.lang.String r12 = "   afterStartIdCount>>"
            r11.append(r12)
            r11.append(r13)
            java.lang.String r12 = "    c.moveToLast()>>"
            r11.append(r12)
            boolean r12 = r15.moveToLast()
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
            int r2 = r10.size()
            if (r2 >= r5) goto L_0x01f2
            if (r13 <= 0) goto L_0x01f2
            boolean r2 = r15.moveToLast()
            if (r2 == 0) goto L_0x01f2
            java.lang.String r2 = "TIFChannelManager"
            java.lang.String r11 = "getTIFPreOrNextChannelListBySateRecId first not get page channel, this is second"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r11)
        L_0x0178:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r15)
            if (r2 != 0) goto L_0x0189
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 3 getTIFPreOrNextChannelListBySateRecId> temTIFChannel is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
        L_0x0186:
            r27 = r13
            goto L_0x01c6
        L_0x0189:
            long[] r11 = r2.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r0.getAPIChannelInfoByBlobData(r11)
            if (r11 != 0) goto L_0x0192
            goto L_0x0186
        L_0x0192:
            int r12 = r11.getChannelId()
            if (r12 == r1) goto L_0x01d2
            boolean r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r6, (int) r7)
            if (r12 == 0) goto L_0x01c4
            boolean r12 = r11 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r12 == 0) goto L_0x01c4
            r12 = r11
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r12
            r27 = r13
            int r13 = r12.getSatRecId()
            if (r13 != r4) goto L_0x01c3
            r2.mMtkTvChannelInfo = r11
            r13 = 0
            r10.add(r13, r2)
            int r13 = r10.size()
            if (r13 != r5) goto L_0x01c3
            java.lang.String r13 = "TIFChannelManager"
            r28 = r12
            java.lang.String r12 = "end 3 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r12)
            goto L_0x01f0
        L_0x01c3:
            goto L_0x01c6
        L_0x01c4:
            r27 = r13
        L_0x01c6:
            boolean r11 = r15.moveToPrevious()
            if (r11 != 0) goto L_0x01cd
            goto L_0x01f0
        L_0x01cd:
            r26 = r2
            r13 = r27
            goto L_0x0178
        L_0x01d2:
            r27 = r13
            if (r3 != 0) goto L_0x01f0
            boolean r12 = r11 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r12 == 0) goto L_0x01e9
            r12 = r11
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r12 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r12
            int r13 = r12.getSatRecId()
            if (r13 != r4) goto L_0x01e9
            r2.mMtkTvChannelInfo = r11
            r13 = 0
            r10.add(r13, r2)
        L_0x01e9:
            java.lang.String r12 = "TIFChannelManager"
            java.lang.String r13 = "end 4 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)
        L_0x01f0:
            r13 = r1
            goto L_0x01f7
        L_0x01f2:
            r27 = r13
            r13 = r1
            r2 = r26
        L_0x01f7:
            r23 = r27
            goto L_0x0368
        L_0x01fb:
            r12 = r22
        L_0x01fd:
            boolean r2 = r15.moveToNext()
            if (r2 == 0) goto L_0x02a7
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r15)
            if (r2 != 0) goto L_0x0211
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r13 = "end 5 getTIFPreOrNextChannelListBySateRecId> temTIFChannel is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r13)
            goto L_0x025b
        L_0x0211:
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r1 = "end 5 getTIFPreOrNextChannelListBySateRecId temTIFChannel"
            r13.append(r1)
            r13.append(r2)
            java.lang.String r1 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r1)
            if (r14 == 0) goto L_0x0260
            long[] r1 = r2.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByBlobData(r1)
            if (r1 != 0) goto L_0x0232
            goto L_0x025b
        L_0x0232:
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r6, (int) r7)
            if (r11 == 0) goto L_0x025a
            boolean r11 = r1 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r11 == 0) goto L_0x025a
            r11 = r1
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r11 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r11
            int r13 = r11.getSatRecId()
            if (r13 != r4) goto L_0x025a
            r2.mMtkTvChannelInfo = r1
            r10.add(r2)
            int r13 = r10.size()
            if (r13 != r5) goto L_0x025a
            java.lang.String r13 = "TIFChannelManager"
            r29 = r1
            java.lang.String r1 = "end 5 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r1)
            goto L_0x02a9
        L_0x025a:
        L_0x025b:
            r21 = r2
            r1 = r33
            goto L_0x01fd
        L_0x0260:
            long[] r1 = r2.mDataValue
            if (r1 == 0) goto L_0x02a4
            long[] r1 = r2.mDataValue
            int r1 = r1.length
            r11 = 2
            if (r1 <= r11) goto L_0x02a4
            long[] r1 = r2.mDataValue
            r16 = r1[r11]
            int r1 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x02a4
            r14 = 1
            if (r3 == 0) goto L_0x02a1
            long[] r1 = r2.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByBlobData(r1)
            if (r1 != 0) goto L_0x027e
            goto L_0x025b
        L_0x027e:
            boolean r13 = r1 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r13 == 0) goto L_0x02a0
            r13 = r1
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r13 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r13
            int r11 = r13.getSatRecId()
            if (r11 != r4) goto L_0x02a0
            r2.mMtkTvChannelInfo = r1
            r10.add(r2)
            int r11 = r10.size()
            if (r11 != r5) goto L_0x02a0
            java.lang.String r11 = "TIFChannelManager"
            r30 = r1
            java.lang.String r1 = "end 6 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r1)
            goto L_0x02a9
        L_0x02a0:
            goto L_0x025b
        L_0x02a1:
            int r12 = r12 + 1
            goto L_0x025b
        L_0x02a4:
            int r12 = r12 + 1
            goto L_0x025b
        L_0x02a7:
            r2 = r21
        L_0x02a9:
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "nexttIFChannelInfoList>>>"
            r11.append(r13)
            int r13 = r10.size()
            r11.append(r13)
            java.lang.String r13 = "   beforeStartIdCount>>"
            r11.append(r13)
            r11.append(r12)
            java.lang.String r13 = "  c.moveToFirst()>>>"
            r11.append(r13)
            boolean r13 = r15.moveToFirst()
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r11)
            int r1 = r10.size()
            if (r1 >= r5) goto L_0x0364
            if (r12 <= 0) goto L_0x0364
            boolean r1 = r15.moveToFirst()
            if (r1 == 0) goto L_0x0364
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r11 = "2first not get page channel, this is second"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r11)
        L_0x02ec:
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r2 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r15)
            if (r2 != 0) goto L_0x02fd
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r11 = "end 7 getTIFPreOrNextChannelListBySateRecId> temTIFChannel is null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r11)
        L_0x02fa:
            r13 = r33
            goto L_0x0335
        L_0x02fd:
            long[] r1 = r2.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByBlobData(r1)
            if (r1 != 0) goto L_0x0306
            goto L_0x02fa
        L_0x0306:
            int r11 = r1.getChannelId()
            r13 = r33
            if (r11 == r13) goto L_0x0341
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r6, (int) r7)
            if (r11 == 0) goto L_0x0335
            boolean r11 = r1 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r11 == 0) goto L_0x0335
            r11 = r1
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r11 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r11
            int r0 = r11.getSatRecId()
            if (r0 != r4) goto L_0x0334
            r2.mMtkTvChannelInfo = r1
            r10.add(r2)
            int r0 = r10.size()
            if (r0 != r5) goto L_0x0334
            java.lang.String r0 = "TIFChannelManager"
            java.lang.String r6 = "end 7 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r6)
            goto L_0x0366
        L_0x0334:
        L_0x0335:
            boolean r0 = r15.moveToNext()
            if (r0 != 0) goto L_0x033c
            goto L_0x0366
        L_0x033c:
            r0 = r32
            r6 = r38
            goto L_0x02ec
        L_0x0341:
            if (r3 != 0) goto L_0x0366
            boolean r0 = r1 instanceof com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo
            if (r0 == 0) goto L_0x035c
            r0 = r1
            com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo r0 = (com.mediatek.twoworlds.tv.model.MtkTvDvbChannelInfo) r0
            int r6 = r0.getSatRecId()
            if (r6 != r4) goto L_0x035c
            r2.mMtkTvChannelInfo = r1
            r10.add(r2)
            int r6 = r10.size()
            if (r6 != r5) goto L_0x035c
            goto L_0x0366
        L_0x035c:
            java.lang.String r0 = "TIFChannelManager"
            java.lang.String r6 = "end 8 getTIFPreOrNextChannelListBySateRecId>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r6)
            goto L_0x0366
        L_0x0364:
            r13 = r33
        L_0x0366:
            r22 = r12
        L_0x0368:
            r15.close()
            java.lang.String r0 = "TIFChannelManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "end getTIFPreOrNextChannelListBySateRecId  tIFChannelInfoList>>>"
            r1.append(r6)
            int r6 = r10.size()
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getTIFPreOrNextChannelListBySateRecId(int, boolean, boolean, int, int, int, int):java.util.List");
    }

    public TIFChannelInfo getTIFUpOrDownChannelBySateRecId(boolean isPrePage, int sateRecId, int attentionMask, int expectValue) {
        MtkLog.d(TAG, "start getTIFUpOrDownChannelBySateRecId>>" + isPrePage);
        int currentChannelId = TIFFunctionUtil.getCurrentChannelId();
        long newId = ((long) currentChannelId) & 4294967295L;
        boolean canAddData = false;
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID, getSvlIdSelectionArgs(), ORDERBY);
        if (c == null) {
            return null;
        }
        MtkLog.d(TAG, "getTIFUpOrDownChannelBySateRecId isPrePage>>>" + isPrePage + "   c>>" + c.getCount());
        if (isPrePage) {
            if (c.moveToLast()) {
                do {
                    TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
                    if (temTIFChannel == null) {
                        MtkLog.d(TAG, "end 1 getTIFUpOrDownChannelBySateRecId> temTIFChannel is null");
                    } else if (canAddData) {
                        MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
                        if (tempApiChannel != null && TIFFunctionUtil.checkChMask(tempApiChannel, attentionMask, expectValue) && (tempApiChannel instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel).getSatRecId() == sateRecId) {
                            temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                            c.close();
                            MtkLog.d(TAG, "end 1 getTIFUpOrDownChannelBySateRecId>>");
                            return temTIFChannel;
                        }
                    } else if (temTIFChannel.mDataValue != null && temTIFChannel.mDataValue[2] == newId) {
                        canAddData = true;
                    }
                } while (c.moveToPrevious());
            }
            if (c.moveToLast()) {
                MtkLog.d(TAG, "getTIFUpOrDownChannelBySateRecId first not get page channel, this is second");
                do {
                    TIFChannelInfo temTIFChannel2 = TIFChannelInfo.parse(c);
                    if (temTIFChannel2 == null) {
                        MtkLog.d(TAG, "end 2 getTIFUpOrDownChannelBySateRecId> temTIFChannel is null");
                    } else {
                        MtkTvChannelInfoBase tempApiChannel2 = getAPIChannelInfoByBlobData(temTIFChannel2.mDataValue);
                        if (tempApiChannel2 != null) {
                            if (tempApiChannel2.getChannelId() == currentChannelId) {
                                break;
                            } else if (TIFFunctionUtil.checkChMask(tempApiChannel2, attentionMask, expectValue) && (tempApiChannel2 instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel2).getSatRecId() == sateRecId) {
                                temTIFChannel2.mMtkTvChannelInfo = tempApiChannel2;
                                c.close();
                                MtkLog.d(TAG, "end 2 getTIFUpOrDownChannelBySateRecId>>");
                                return temTIFChannel2;
                            }
                        }
                    }
                } while (c.moveToPrevious());
            }
        } else {
            while (c.moveToNext()) {
                TIFChannelInfo temTIFChannel3 = TIFChannelInfo.parse(c);
                if (temTIFChannel3 == null) {
                    MtkLog.d(TAG, "end 3 getTIFUpOrDownChannelBySateRecId> temTIFChannel is null");
                } else {
                    MtkLog.d(TAG, "end 3 getTIFUpOrDownChannelBySateRecId> temTIFChannel>>" + temTIFChannel3);
                    if (canAddData) {
                        MtkTvChannelInfoBase tempApiChannel3 = getAPIChannelInfoByBlobData(temTIFChannel3.mDataValue);
                        if (tempApiChannel3 != null && TIFFunctionUtil.checkChMask(tempApiChannel3, attentionMask, expectValue) && (tempApiChannel3 instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel3).getSatRecId() == sateRecId) {
                            temTIFChannel3.mMtkTvChannelInfo = tempApiChannel3;
                            c.close();
                            MtkLog.d(TAG, "end 3 getTIFUpOrDownChannelBySateRecId>>");
                            return temTIFChannel3;
                        }
                    } else if (temTIFChannel3.mDataValue != null && temTIFChannel3.mDataValue.length > 2 && temTIFChannel3.mDataValue[2] == newId) {
                        canAddData = true;
                    }
                }
            }
            if (c.moveToFirst()) {
                MtkLog.d(TAG, "getTIFUpOrDownChannelBySateRecId 2first not get page channel, this is second");
                do {
                    TIFChannelInfo temTIFChannel4 = TIFChannelInfo.parse(c);
                    if (temTIFChannel4 == null) {
                        MtkLog.d(TAG, "end 4 getTIFUpOrDownChannelBySateRecId> temTIFChannel is null");
                    } else {
                        MtkTvChannelInfoBase tempApiChannel4 = getAPIChannelInfoByBlobData(temTIFChannel4.mDataValue);
                        if (tempApiChannel4 != null) {
                            if (tempApiChannel4.getChannelId() == currentChannelId) {
                                break;
                            } else if (TIFFunctionUtil.checkChMask(tempApiChannel4, attentionMask, expectValue) && (tempApiChannel4 instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel4).getSatRecId() == sateRecId) {
                                temTIFChannel4.mMtkTvChannelInfo = tempApiChannel4;
                                c.close();
                                MtkLog.d(TAG, "end 4 getTIFUpOrDownChannelBySateRecId>>");
                                return temTIFChannel4;
                            }
                        }
                    }
                } while (c.moveToNext());
            }
        }
        c.close();
        MtkLog.d(TAG, "end getTIFUpOrDownChannelBySateRecId  >>>null");
        return null;
    }

    public TIFChannelInfo getTIFUpOrDownChannelfor3rdsource(boolean isup) {
        TIFChannelInfo selchannel = getTIFUpOrDownChannel(isup, -1, -1);
        MtkLog.d(TAG, "getTIFUpOrDownChannelfor3rdsource step 0 .isup =" + isup + ",selchannel: " + selchannel);
        return selchannel;
    }

    public TIFChannelInfo getTIFUpOrDownChannel(boolean isPreChannel, int attentionMask, int expectValue) {
        return getTIFUpOrDownChannel(isPreChannel, attentionMask, expectValue, getChannelListForFindOrNomal());
    }

    public TIFChannelInfo getTIFUpOrDownChannelForUSEPG(boolean isPreChannel, int attentionMask, int expectValue) {
        return getTIFUpOrDownChannelForUSEPG(isPreChannel, attentionMask, expectValue, getChannelListForFindOrNomal(), false);
    }

    private TIFChannelInfo getTIFUpOrDownChannel(boolean isPreChannel, int attentionMask, int expectValue, List<TIFChannelInfo> mChannelList) {
        return getTIFUpOrDownChannel(isPreChannel, attentionMask, expectValue, mChannelList, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03bf, code lost:
        if (r9.mDataValue.length != 9) goto L_0x03c4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo getTIFUpOrDownChannel(boolean r21, int r22, int r23, java.util.List<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> r24, boolean r25) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            java.lang.String r5 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "start getTIFUpOrDownChannel>>"
            r6.append(r7)
            r6.append(r1)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            int r5 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getCurrentChannelId()
            long r6 = (long) r5
            r8 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r6 = r6 & r8
            int r6 = (int) r6
            long r6 = (long) r6
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r8 = r0.getTIFChannelInfoById(r5)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r9 = r20.getChannelInfoByUri()
            r10 = 9
            if (r9 == 0) goto L_0x0047
            long[] r11 = r9.mDataValue
            if (r11 == 0) goto L_0x0042
            long[] r11 = r9.mDataValue
            int r11 = r11.length
            if (r11 == r10) goto L_0x0047
        L_0x0042:
            long r11 = r9.mId
            int r5 = (int) r11
            long r6 = r9.mId
        L_0x0047:
            r11 = 0
            r12 = 0
            boolean r13 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            r14 = 1
            if (r13 == 0) goto L_0x005b
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r13 = r0.mCI
            java.lang.String r13 = "ww.sa.skip"
            int r13 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.getProperty(r13)
            if (r13 != r14) goto L_0x005b
            r12 = 1
        L_0x005b:
            java.lang.String r13 = "TIFChannelManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r14 = "getTIFUpOrDownChannel isPrePage>>>"
            r10.append(r14)
            r10.append(r1)
            java.lang.String r14 = "  mChannelList.size() = "
            r10.append(r14)
            int r14 = r24.size()
            r10.append(r14)
            java.lang.String r10 = r10.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r10)
            if (r1 == 0) goto L_0x0274
            r13 = 0
            int r14 = r24.size()
            r16 = 1
            int r14 = r14 + -1
        L_0x0088:
            if (r14 < 0) goto L_0x0199
            java.lang.Object r17 = r4.get(r14)
            r13 = r17
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r13 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r13
            java.lang.String r10 = "TIFChannelManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r18 = r9
            java.lang.String r9 = "getTIFUpOrDownChannel temTIFChannel>>> "
            r1.append(r9)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r1)
            if (r11 == 0) goto L_0x0179
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "getTIFUpOrDownChannel isPreChannel canGetData>>> "
            r9.append(r10)
            r9.append(r11)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r9)
            r1 = -1
            if (r2 != r1) goto L_0x00de
            if (r3 != r1) goto L_0x00de
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r9 = "end 1 getTIFUpOrDownChannel> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r9)
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x00d9
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r9 = 9
            if (r1 == r9) goto L_0x00de
        L_0x00d9:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x00de
            return r13
        L_0x00de:
            boolean r1 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r1 != 0) goto L_0x00ea
            boolean r1 = r13.mIsBrowsable
            if (r1 != 0) goto L_0x00ea
            goto L_0x0191
        L_0x00ea:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r1 = r0.mCI
            boolean r1 = r1.isDisableColorKey()
            if (r1 == 0) goto L_0x0118
            long[] r1 = r13.mDataValue
            if (r1 == 0) goto L_0x00fd
            long[] r1 = r13.mDataValue
            int r1 = r1.length
            r9 = 9
            if (r1 == r9) goto L_0x0118
        L_0x00fd:
            boolean r1 = r13.mIsBrowsable
            if (r1 == 0) goto L_0x0118
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "end 1 getTIFUpOrDownChannel isDisableColorKey isPreChannel 3rd channel temTIFChannel: "
            r9.append(r10)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r9)
            return r13
        L_0x0118:
            long[] r1 = r13.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByBlobData(r1)
            if (r1 == 0) goto L_0x0191
            if (r25 != 0) goto L_0x0129
            boolean r9 = r1.isSkip()
            if (r9 == 0) goto L_0x0129
            goto L_0x0191
        L_0x0129:
            boolean r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            if (r9 == 0) goto L_0x015c
            if (r12 == 0) goto L_0x015c
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r2, (int) r3)
            if (r9 == 0) goto L_0x0178
            boolean r9 = r1 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r9 == 0) goto L_0x0178
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r9 = r8.mMtkTvChannelInfo
            boolean r9 = r9 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r9 == 0) goto L_0x0178
            r9 = r1
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r9 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r9
            int r9 = r9.getMajorNum()
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r10 = r8.mMtkTvChannelInfo
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r10 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r10
            int r10 = r10.getMajorNum()
            if (r9 == r10) goto L_0x0178
            java.lang.String r9 = "TIFChannelManager"
            java.lang.String r10 = "end sa 1 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            r13.mMtkTvChannelInfo = r1
            return r13
        L_0x015c:
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r2, (int) r3)
            if (r9 == 0) goto L_0x0178
            int r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r9 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r1, r9, r10)
            if (r9 == 0) goto L_0x0178
            java.lang.String r9 = "TIFChannelManager"
            java.lang.String r10 = "end 1 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            return r13
        L_0x0178:
            goto L_0x0191
        L_0x0179:
            if (r13 == 0) goto L_0x0191
            int r1 = r13.mInternalProviderFlag3
            long r9 = (long) r1
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0188
            long r9 = r13.mId
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0191
        L_0x0188:
            r1 = 1
            java.lang.String r9 = "TIFChannelManager"
            java.lang.String r10 = "end 2 getTIFUpOrDownChannel> PreChannel canGetData = true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            r11 = r1
        L_0x0191:
            int r14 = r14 + -1
            r9 = r18
            r1 = r21
            goto L_0x0088
        L_0x0199:
            r18 = r9
            int r1 = r24.size()
            r9 = 1
            int r1 = r1 - r9
        L_0x01a1:
            if (r1 < 0) goto L_0x0272
            java.lang.Object r9 = r4.get(r1)
            r13 = r9
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r13 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r13
            r9 = -1
            if (r2 != r9) goto L_0x01cc
            if (r3 != r9) goto L_0x01cc
            java.lang.String r9 = "TIFChannelManager"
            java.lang.String r10 = "end 2 getTIFUpOrDownChannel> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            long[] r9 = r13.mDataValue
            if (r9 == 0) goto L_0x01c1
            long[] r9 = r13.mDataValue
            int r9 = r9.length
            r10 = 9
            if (r9 == r10) goto L_0x026e
        L_0x01c1:
            long r9 = r13.mId
            int r9 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x026e
            boolean r9 = r13.mIsBrowsable
            if (r9 == 0) goto L_0x026e
            return r13
        L_0x01cc:
            boolean r9 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r9 != 0) goto L_0x01d8
            boolean r9 = r13.mIsBrowsable
            if (r9 != 0) goto L_0x01d8
            goto L_0x026e
        L_0x01d8:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r9 = r0.mCI
            boolean r9 = r9.isDisableColorKey()
            if (r9 == 0) goto L_0x0206
            long[] r9 = r13.mDataValue
            if (r9 == 0) goto L_0x01eb
            long[] r9 = r13.mDataValue
            int r9 = r9.length
            r10 = 9
            if (r9 == r10) goto L_0x0206
        L_0x01eb:
            boolean r9 = r13.mIsBrowsable
            if (r9 == 0) goto L_0x0206
            java.lang.String r9 = "TIFChannelManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r14 = "end 2 getTIFUpOrDownChannel isDisableColorKey isPreChannel 3rd channel temTIFChannel: "
            r10.append(r14)
            r10.append(r13)
            java.lang.String r10 = r10.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r9, r10)
            return r13
        L_0x0206:
            long[] r9 = r13.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r9 = r0.getAPIChannelInfoByBlobData(r9)
            if (r9 == 0) goto L_0x026e
            if (r25 != 0) goto L_0x0217
            boolean r10 = r9.isSkip()
            if (r10 == 0) goto L_0x0217
            goto L_0x026e
        L_0x0217:
            int r10 = r9.getChannelId()
            if (r10 == r5) goto L_0x0272
            boolean r10 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            if (r10 == 0) goto L_0x0250
            if (r12 == 0) goto L_0x0250
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r9, (int) r2, (int) r3)
            if (r10 == 0) goto L_0x026e
            boolean r10 = r9 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r10 == 0) goto L_0x026e
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r10 = r8.mMtkTvChannelInfo
            boolean r10 = r10 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r10 == 0) goto L_0x026e
            r10 = r9
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r10 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r10
            int r10 = r10.getMajorNum()
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r14 = r8.mMtkTvChannelInfo
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r14 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r14
            int r14 = r14.getMajorNum()
            if (r10 == r14) goto L_0x026e
            r13.mMtkTvChannelInfo = r9
            java.lang.String r10 = "TIFChannelManager"
            java.lang.String r14 = "end sa 2 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r14)
            return r13
        L_0x0250:
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r9, (int) r2, (int) r3)
            if (r10 == 0) goto L_0x026e
            int r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r14 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r9, r10, r14)
            if (r10 == 0) goto L_0x026e
            r13.mMtkTvChannelInfo = r9
            java.lang.String r10 = "TIFChannelManager"
            java.lang.String r14 = "end 2 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r14)
            return r13
        L_0x026e:
            int r1 = r1 + -1
            goto L_0x01a1
        L_0x0272:
            goto L_0x044d
        L_0x0274:
            r18 = r9
            java.util.Iterator r1 = r24.iterator()
        L_0x027a:
            boolean r9 = r1.hasNext()
            if (r9 == 0) goto L_0x0371
            java.lang.Object r9 = r1.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r9 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r9
            java.lang.String r10 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "end 3 getTIFUpOrDownChannel> nextChannel temTIFChannel = "
            r13.append(r14)
            r13.append(r9)
            java.lang.String r13 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r13)
            if (r11 == 0) goto L_0x0357
            r10 = -1
            if (r2 != r10) goto L_0x02ba
            if (r3 != r10) goto L_0x02ba
            java.lang.String r10 = "TIFChannelManager"
            java.lang.String r13 = "end 3 getTIFUpOrDownChannel> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r10, r13)
            long[] r10 = r9.mDataValue
            if (r10 == 0) goto L_0x02b5
            long[] r10 = r9.mDataValue
            int r10 = r10.length
            r13 = 9
            if (r10 == r13) goto L_0x02ba
        L_0x02b5:
            boolean r10 = r9.mIsBrowsable
            if (r10 == 0) goto L_0x02ba
            return r9
        L_0x02ba:
            boolean r10 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r10 != 0) goto L_0x02c5
            boolean r10 = r9.mIsBrowsable
            if (r10 != 0) goto L_0x02c5
            goto L_0x027a
        L_0x02c5:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r10 = r0.mCI
            boolean r10 = r10.isDisableColorKey()
            if (r10 == 0) goto L_0x02f3
            long[] r10 = r9.mDataValue
            if (r10 == 0) goto L_0x02d8
            long[] r10 = r9.mDataValue
            int r10 = r10.length
            r13 = 9
            if (r10 == r13) goto L_0x02f3
        L_0x02d8:
            boolean r10 = r9.mIsBrowsable
            if (r10 == 0) goto L_0x02f3
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r13 = "end 3 getTIFUpOrDownChannel isDisableColorKey nextChannel 3rd channel temTIFChannel: "
            r10.append(r13)
            r10.append(r9)
            java.lang.String r10 = r10.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r10)
            return r9
        L_0x02f3:
            long[] r10 = r9.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r10 = r0.getAPIChannelInfoByBlobData(r10)
            if (r10 == 0) goto L_0x027a
            if (r25 != 0) goto L_0x0305
            boolean r13 = r10.isSkip()
            if (r13 == 0) goto L_0x0305
            goto L_0x027a
        L_0x0305:
            boolean r13 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            if (r13 == 0) goto L_0x0338
            if (r12 == 0) goto L_0x0338
            boolean r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r10, (int) r2, (int) r3)
            if (r13 == 0) goto L_0x0356
            boolean r13 = r10 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r13 == 0) goto L_0x0356
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r13 = r8.mMtkTvChannelInfo
            boolean r13 = r13 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r13 == 0) goto L_0x0356
            r13 = r10
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r13 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r13
            int r13 = r13.getMajorNum()
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r14 = r8.mMtkTvChannelInfo
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r14 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r14
            int r14 = r14.getMajorNum()
            if (r13 == r14) goto L_0x0356
            r9.mMtkTvChannelInfo = r10
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r13 = "end sa 3 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r13)
            return r9
        L_0x0338:
            boolean r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r10, (int) r2, (int) r3)
            if (r13 == 0) goto L_0x0356
            int r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r14 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r10, r13, r14)
            if (r13 == 0) goto L_0x0356
            r9.mMtkTvChannelInfo = r10
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r13 = "end 3 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r13)
            return r9
        L_0x0356:
            goto L_0x036f
        L_0x0357:
            if (r9 == 0) goto L_0x036f
            int r10 = r9.mInternalProviderFlag3
            long r13 = (long) r10
            int r10 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x0366
            long r13 = r9.mId
            int r10 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r10 != 0) goto L_0x036f
        L_0x0366:
            r10 = 1
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r13 = "end 3 getTIFUpOrDownChannel> nextChannel canGetData = true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r13)
            r11 = r10
        L_0x036f:
            goto L_0x027a
        L_0x0371:
            java.util.Iterator r1 = r24.iterator()
        L_0x0375:
            boolean r9 = r1.hasNext()
            if (r9 == 0) goto L_0x044d
            java.lang.Object r9 = r1.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r9 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r9
            r10 = -1
            if (r2 != r10) goto L_0x03a3
            if (r3 != r10) goto L_0x03a3
            java.lang.String r13 = "TIFChannelManager"
            java.lang.String r14 = "end 4 getTIFUpOrDownChannel> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r13, r14)
            long[] r13 = r9.mDataValue
            if (r13 == 0) goto L_0x0398
            long[] r13 = r9.mDataValue
            int r13 = r13.length
            r14 = 9
            if (r13 == r14) goto L_0x03a3
        L_0x0398:
            long r13 = r9.mId
            int r13 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x03a3
            boolean r13 = r9.mIsBrowsable
            if (r13 == 0) goto L_0x03a3
            return r9
        L_0x03a3:
            boolean r13 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isUSRegion()
            if (r13 != 0) goto L_0x03ae
            boolean r13 = r9.mIsBrowsable
            if (r13 != 0) goto L_0x03ae
            goto L_0x0375
        L_0x03ae:
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r13 = r0.mCI
            boolean r13 = r13.isDisableColorKey()
            if (r13 == 0) goto L_0x03df
            long[] r13 = r9.mDataValue
            if (r13 == 0) goto L_0x03c2
            long[] r13 = r9.mDataValue
            int r13 = r13.length
            r14 = 9
            if (r13 == r14) goto L_0x03e1
            goto L_0x03c4
        L_0x03c2:
            r14 = 9
        L_0x03c4:
            boolean r13 = r9.mIsBrowsable
            if (r13 == 0) goto L_0x03e1
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r13 = "end 4 getTIFUpOrDownChannel isDisableColorKey nextChannel 3rd channel temTIFChannel: "
            r10.append(r13)
            r10.append(r9)
            java.lang.String r10 = r10.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r10)
            return r9
        L_0x03df:
            r14 = 9
        L_0x03e1:
            long[] r13 = r9.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r13 = r0.getAPIChannelInfoByBlobData(r13)
            if (r13 == 0) goto L_0x0375
            if (r25 != 0) goto L_0x03f2
            boolean r15 = r13.isSkip()
            if (r15 == 0) goto L_0x03f2
            goto L_0x0375
        L_0x03f2:
            int r10 = r13.getChannelId()
            if (r10 == r5) goto L_0x044d
            boolean r10 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isSARegion()
            if (r10 == 0) goto L_0x042d
            if (r12 == 0) goto L_0x042d
            if (r13 == 0) goto L_0x044b
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r13, (int) r2, (int) r3)
            if (r10 == 0) goto L_0x044b
            boolean r10 = r13 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r10 == 0) goto L_0x044b
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r10 = r8.mMtkTvChannelInfo
            boolean r10 = r10 instanceof com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo
            if (r10 == 0) goto L_0x044b
            r10 = r13
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r10 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r10
            int r10 = r10.getMajorNum()
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r14 = r8.mMtkTvChannelInfo
            com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo r14 = (com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo) r14
            int r14 = r14.getMajorNum()
            if (r10 == r14) goto L_0x044b
            r9.mMtkTvChannelInfo = r13
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r10 = "end sa 4 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r10)
            return r9
        L_0x042d:
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r13, (int) r2, (int) r3)
            if (r10 == 0) goto L_0x044b
            int r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r14 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r10 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r13, r10, r14)
            if (r10 == 0) goto L_0x044b
            r9.mMtkTvChannelInfo = r13
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r10 = "end 4 getTIFUpOrDownChannel>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r10)
            return r9
        L_0x044b:
            goto L_0x0375
        L_0x044d:
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r9 = "end getTIFUpOrDownChannel>> null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r9)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getTIFUpOrDownChannel(boolean, int, int, java.util.List, boolean):com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:124:0x029d, code lost:
        if (r8.mDataValue.length != 9) goto L_0x02a3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x030c A[EDGE_INSN: B:187:0x030c->B:148:0x030c ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo getTIFUpOrDownChannelForUSEPG(boolean r20, int r21, int r22, java.util.List<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> r23, boolean r24) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            java.lang.String r5 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "start getTIFUpOrDownChannelForUSEPG>>"
            r6.append(r7)
            r6.append(r1)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            int r5 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getCurrentChannelId()
            long r6 = (long) r5
            r8 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r6 = r6 & r8
            int r6 = (int) r6
            long r6 = (long) r6
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r8 = r0.getTIFChannelInfoById(r5)
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r9 = r19.getChannelInfoByUri()
            if (r9 == 0) goto L_0x0040
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r10 = r9.mMtkTvChannelInfo
            if (r10 != 0) goto L_0x0040
            long r10 = r9.mId
            int r5 = (int) r10
            long r6 = r9.mId
        L_0x0040:
            r10 = 0
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "getTIFUpOrDownChannel isPrePage>>>"
            r12.append(r13)
            r12.append(r1)
            java.lang.String r13 = "  mChannelList.size() = "
            r12.append(r13)
            int r13 = r23.size()
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            if (r1 == 0) goto L_0x01d7
            r14 = 0
            int r15 = r23.size()
            int r15 = r15 + -1
        L_0x006c:
            r16 = r15
            r11 = r16
            if (r11 < 0) goto L_0x013f
            java.lang.Object r15 = r4.get(r11)
            r14 = r15
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r14 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r14
            java.lang.String r12 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r1 = "getTIFUpOrDownChannelForUSEPG temTIFChannel>>> "
            r13.append(r1)
            r13.append(r14)
            java.lang.String r1 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r1)
            if (r10 == 0) goto L_0x011d
            java.lang.String r1 = "TIFChannelManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "getTIFUpOrDownChannelForUSEPG isPreChannel canGetData>>> "
            r12.append(r13)
            r12.append(r10)
            java.lang.String r12 = r12.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r12)
            r1 = -1
            if (r2 != r1) goto L_0x00c9
            if (r3 != r1) goto L_0x00c9
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r12 = "end 1 getTIFUpOrDownChannelForUSEPG > 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r12)
            long[] r1 = r14.mDataValue
            if (r1 == 0) goto L_0x00c4
            long[] r1 = r14.mDataValue
            int r1 = r1.length
            r12 = 6
            if (r1 == r12) goto L_0x00c9
            long[] r1 = r14.mDataValue
            int r1 = r1.length
            r12 = 9
            if (r1 == r12) goto L_0x00c9
        L_0x00c4:
            boolean r1 = r14.mIsBrowsable
            if (r1 == 0) goto L_0x00c9
            return r14
        L_0x00c9:
            long[] r1 = r14.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r0.getAPIChannelInfoByBlobData(r1)
            java.lang.String r12 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r17 = r8
            java.lang.String r8 = "00preChannel getTIFUpOrDownChannelForUSEPG  temTIFChannel:"
            r13.append(r8)
            r13.append(r14)
            java.lang.String r8 = ",tempApiChannel:"
            r13.append(r8)
            r13.append(r1)
            java.lang.String r8 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r8)
            if (r1 == 0) goto L_0x0137
            if (r24 != 0) goto L_0x00f9
            boolean r8 = r1.isSkip()
            if (r8 != 0) goto L_0x0137
        L_0x00f9:
            boolean r8 = r1.isAnalogService()
            if (r8 == 0) goto L_0x0100
            goto L_0x0137
        L_0x0100:
            boolean r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r1, (int) r2, (int) r3)
            if (r8 == 0) goto L_0x011c
            int r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r8 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r1, r8, r12)
            if (r8 == 0) goto L_0x011c
            java.lang.String r8 = "TIFChannelManager"
            java.lang.String r12 = "end 1 getTIFUpOrDownChannelForUSEPG>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r12)
            return r14
        L_0x011c:
            goto L_0x0137
        L_0x011d:
            r17 = r8
            if (r14 == 0) goto L_0x0137
            int r1 = r14.mInternalProviderFlag3
            long r12 = (long) r1
            int r1 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x012e
            long r12 = r14.mId
            int r1 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0137
        L_0x012e:
            r1 = 1
            java.lang.String r8 = "TIFChannelManager"
            java.lang.String r10 = "end 2 getTIFUpOrDownChannelForUSEPG> PreChannel canGetData = true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r10)
            r10 = r1
        L_0x0137:
            int r15 = r11 + -1
            r8 = r17
            r1 = r20
            goto L_0x006c
        L_0x013f:
            r17 = r8
            int r1 = r23.size()
            int r1 = r1 + -1
        L_0x0147:
            if (r1 < 0) goto L_0x01d5
            java.lang.Object r8 = r4.get(r1)
            r14 = r8
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r14 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r14
            r8 = -1
            if (r2 != r8) goto L_0x0178
            if (r3 != r8) goto L_0x0178
            java.lang.String r8 = "TIFChannelManager"
            java.lang.String r11 = "end 2 getTIFUpOrDownChannelForUSEPG> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r8, r11)
            long[] r8 = r14.mDataValue
            if (r8 == 0) goto L_0x016d
            long[] r8 = r14.mDataValue
            int r8 = r8.length
            r11 = 6
            if (r8 == r11) goto L_0x01d1
            long[] r8 = r14.mDataValue
            int r8 = r8.length
            r11 = 9
            if (r8 == r11) goto L_0x01d1
        L_0x016d:
            long r11 = r14.mId
            int r8 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x01d1
            boolean r8 = r14.mIsBrowsable
            if (r8 == 0) goto L_0x01d1
            return r14
        L_0x0178:
            long[] r8 = r14.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r8 = r0.getAPIChannelInfoByBlobData(r8)
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "preChannel getTIFUpOrDownChannelForUSEPG  temTIFChannel:"
            r12.append(r13)
            r12.append(r14)
            java.lang.String r13 = ",tempApiChannel:"
            r12.append(r13)
            r12.append(r8)
            java.lang.String r12 = r12.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            if (r8 == 0) goto L_0x01d1
            if (r24 != 0) goto L_0x01a6
            boolean r11 = r8.isSkip()
            if (r11 != 0) goto L_0x01d1
        L_0x01a6:
            boolean r11 = r8.isAnalogService()
            if (r11 == 0) goto L_0x01ad
            goto L_0x01d1
        L_0x01ad:
            int r11 = r8.getChannelId()
            if (r11 == r5) goto L_0x01d5
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r8, (int) r2, (int) r3)
            if (r11 == 0) goto L_0x01d1
            int r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r11 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r8, r11, r12)
            if (r11 == 0) goto L_0x01d1
            r14.mMtkTvChannelInfo = r8
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 2 getTIFUpOrDownChannelForUSEPG>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            return r14
        L_0x01d1:
            int r1 = r1 + -1
            goto L_0x0147
        L_0x01d5:
            goto L_0x030c
        L_0x01d7:
            r17 = r8
            java.util.Iterator r1 = r23.iterator()
        L_0x01dd:
            boolean r8 = r1.hasNext()
            if (r8 == 0) goto L_0x0272
            java.lang.Object r8 = r1.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r8 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r8
            java.lang.String r11 = "TIFChannelManager"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "end 3 getTIFUpOrDownChannelForUSEPG> nextChannel temTIFChannel = "
            r12.append(r13)
            r12.append(r8)
            java.lang.String r12 = r12.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            if (r10 == 0) goto L_0x0259
            r11 = -1
            if (r2 != r11) goto L_0x0223
            if (r3 != r11) goto L_0x0223
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 3 getTIFUpOrDownChannelForUSEPG> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
            long[] r11 = r8.mDataValue
            if (r11 == 0) goto L_0x021e
            long[] r11 = r8.mDataValue
            int r11 = r11.length
            r12 = 6
            if (r11 == r12) goto L_0x0223
            long[] r11 = r8.mDataValue
            int r11 = r11.length
            r12 = 9
            if (r11 == r12) goto L_0x0223
        L_0x021e:
            boolean r11 = r8.mIsBrowsable
            if (r11 == 0) goto L_0x0223
            return r8
        L_0x0223:
            long[] r11 = r8.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r0.getAPIChannelInfoByBlobData(r11)
            if (r11 == 0) goto L_0x01dd
            if (r24 != 0) goto L_0x0233
            boolean r12 = r11.isSkip()
            if (r12 != 0) goto L_0x01dd
        L_0x0233:
            boolean r12 = r11.isAnalogService()
            if (r12 == 0) goto L_0x023a
            goto L_0x01dd
        L_0x023a:
            boolean r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r2, (int) r3)
            if (r12 == 0) goto L_0x0258
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r11, r12, r13)
            if (r12 == 0) goto L_0x0258
            r8.mMtkTvChannelInfo = r11
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r12 = "end 3 getTIFUpOrDownChannelForUSEPG>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r12)
            return r8
        L_0x0258:
            goto L_0x0270
        L_0x0259:
            if (r8 == 0) goto L_0x0270
            int r11 = r8.mInternalProviderFlag3
            long r11 = (long) r11
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x0268
            long r11 = r8.mId
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 != 0) goto L_0x0270
        L_0x0268:
            r10 = 1
            java.lang.String r11 = "TIFChannelManager"
            java.lang.String r12 = "end 3 getTIFUpOrDownChannelForUSEPG> nextChannel canGetData = true"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r11, r12)
        L_0x0270:
            goto L_0x01dd
        L_0x0272:
            java.util.Iterator r1 = r23.iterator()
        L_0x0276:
            boolean r8 = r1.hasNext()
            if (r8 == 0) goto L_0x030c
            java.lang.Object r8 = r1.next()
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r8 = (com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo) r8
            r11 = -1
            if (r2 != r11) goto L_0x02ae
            if (r3 != r11) goto L_0x02ae
            java.lang.String r12 = "TIFChannelManager"
            java.lang.String r13 = "end 4 getTIFUpOrDownChannelForUSEPG> 3rd"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)
            long[] r12 = r8.mDataValue
            if (r12 == 0) goto L_0x02a0
            long[] r12 = r8.mDataValue
            int r12 = r12.length
            r13 = 6
            if (r12 == r13) goto L_0x02af
            long[] r12 = r8.mDataValue
            int r12 = r12.length
            r14 = 9
            if (r12 == r14) goto L_0x02b1
            goto L_0x02a3
        L_0x02a0:
            r13 = 6
            r14 = 9
        L_0x02a3:
            long r11 = r8.mId
            int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x02b1
            boolean r11 = r8.mIsBrowsable
            if (r11 == 0) goto L_0x02b1
            return r8
        L_0x02ae:
            r13 = 6
        L_0x02af:
            r14 = 9
        L_0x02b1:
            long[] r11 = r8.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r11 = r0.getAPIChannelInfoByBlobData(r11)
            java.lang.String r12 = "TIFChannelManager"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "11getTIFUpOrDownChannelForUSEPG  temTIFChannel:"
            r13.append(r14)
            r13.append(r8)
            java.lang.String r14 = ",tempApiChannel:"
            r13.append(r14)
            r13.append(r11)
            java.lang.String r13 = r13.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r12, r13)
            if (r11 == 0) goto L_0x0276
            if (r24 != 0) goto L_0x02df
            boolean r12 = r11.isSkip()
            if (r12 != 0) goto L_0x0276
        L_0x02df:
            boolean r12 = r11.isAnalogService()
            if (r12 == 0) goto L_0x02e6
            goto L_0x0276
        L_0x02e6:
            int r12 = r11.getChannelId()
            if (r12 == r5) goto L_0x030c
            boolean r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r11, (int) r2, (int) r3)
            if (r12 == 0) goto L_0x030a
            int r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            int r13 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.getmCurCategories()
            boolean r12 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChCategoryMask(r11, r12, r13)
            if (r12 == 0) goto L_0x030a
            r8.mMtkTvChannelInfo = r11
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r12 = "end 4 getTIFUpOrDownChannelForUSEPG>>done"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r12)
            return r8
        L_0x030a:
            goto L_0x0276
        L_0x030c:
            java.lang.String r1 = "TIFChannelManager"
            java.lang.String r8 = "end getTIFUpOrDownChannelForUSEPG for us epg>> null"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r1, r8)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getTIFUpOrDownChannelForUSEPG(boolean, int, int, java.util.List, boolean):com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo");
    }

    public TIFChannelInfo getTIFChannelInfoById(int channelId) {
        MtkLog.d(TAG, "start getTIFChannelInfoById>>" + channelId);
        List<TIFChannelInfo> mChannelList = new ArrayList<>();
        mChannelList.addAll(getChannelListForFindOrNomal());
        MtkLog.d(TAG, "getTIFChannelInfoById mChannelList.size>>" + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if (temTIFChannel != null && (temTIFChannel.mInternalProviderFlag3 == channelId || temTIFChannel.mId == ((long) channelId))) {
                temTIFChannel.mMtkTvChannelInfo = getAPIChannelInfoByChannelId(temTIFChannel.mInternalProviderFlag3);
                MtkLog.d(TAG, "end getTIFChannelInfoById>>" + temTIFChannel.mDisplayName);
                return temTIFChannel;
            }
        }
        MtkLog.d(TAG, "end getTIFChannelInfoById>>null");
        return null;
    }

    public void selectTIFChannelInfoByChannelIdForTuneMode() {
        MtkLog.d(TAG, "start selectTIFChannelInfoByChannelIdForTuneMode>>");
        List<TIFChannelInfo> mChannelList = new ArrayList<>();
        mChannelList.addAll(getChannelListForFindOrNomal());
        int channelId = CommonIntegration.getInstance().getChannelIdByConfigId();
        MtkLog.d(TAG, "selectTIFChannelInfoByChannelIdForTuneMode mChannelList.size>>" + mChannelList.size());
        TIFChannelInfo tiFChannel = null;
        Iterator<TIFChannelInfo> it = mChannelList.iterator();
        while (true) {
            if (it.hasNext()) {
                TIFChannelInfo temTIFChannel = it.next();
                if (temTIFChannel != null && temTIFChannel.mInternalProviderFlag3 == channelId) {
                    tiFChannel = temTIFChannel;
                    break;
                }
            } else {
                break;
            }
        }
        MtkLog.d(TAG, "end selectTIFChannelInfoByChannelIdForTuneMode>>" + tiFChannel);
        selectChannelByTIFInfo(tiFChannel);
    }

    public TIFChannelInfo getTIFChannelInfoForSourceById(int channelId) {
        MtkLog.d(TAG, "start getTIFChannelInfoById>>" + channelId);
        List<TIFChannelInfo> mChannelList = new ArrayList<>();
        mChannelList.addAll(getChannelList());
        int svlId = CommonIntegration.getInstance().getSvl();
        MtkLog.d(TAG, "getTIFChannelInfoById mChannelList.size>>" + mChannelList.size());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            CommonIntegration commonIntegration = this.mCI;
            if (!CommonIntegration.isCNRegion()) {
                CommonIntegration commonIntegration2 = this.mCI;
                if (!CommonIntegration.isEUPARegion()) {
                    if (temTIFChannel != null && ((temTIFChannel.mInternalProviderFlag3 == channelId || temTIFChannel.mId == ((long) channelId)) && temTIFChannel.mInternalProviderFlag1 == svlId)) {
                        MtkLog.d(TAG, "end getTIFChannelInfoById>>" + temTIFChannel.mDisplayName);
                        return temTIFChannel;
                    }
                }
            }
            if (temTIFChannel != null && (temTIFChannel.mInternalProviderFlag3 == channelId || temTIFChannel.mId == ((long) channelId))) {
                temTIFChannel.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
                if (temTIFChannel == null) {
                    continue;
                } else if (this.mCI.isCurrentSourceATV()) {
                    if (!TIFFunctionUtil.checkChMask(temTIFChannel, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_DIGITAL_VAL) && temTIFChannel.mInternalProviderFlag1 == svlId) {
                        return temTIFChannel;
                    }
                } else if (!this.mCI.isCurrentSourceDTV()) {
                    int brdcstType = this.mCI.getBrdcstType();
                    CommonIntegration commonIntegration3 = this.mCI;
                    if (brdcstType != 0) {
                        int brdcstType2 = this.mCI.getBrdcstType();
                        CommonIntegration commonIntegration4 = this.mCI;
                        if (brdcstType2 == 1 && TIFFunctionUtil.checkChMask(temTIFChannel, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_DIGITAL_VAL)) {
                            return temTIFChannel;
                        }
                    } else if (!TIFFunctionUtil.checkChMask(temTIFChannel, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_DIGITAL_VAL) && temTIFChannel.mInternalProviderFlag1 == svlId) {
                        return temTIFChannel;
                    }
                } else if (TIFFunctionUtil.checkChMask(temTIFChannel, TIFFunctionUtil.CH_LIST_ANALOG_MASK, TIFFunctionUtil.CH_LIST_DIGITAL_VAL)) {
                    return temTIFChannel;
                }
            }
        }
        MtkLog.d(TAG, "end getTIFChannelInfoById>>null");
        return null;
    }

    public boolean isCiVirtualChannel() {
        long[] datas;
        TIFChannelInfo tifInfo = getTIFChannelInfoById(this.mCI.getCurrentChannelId());
        if (tifInfo == null || (datas = tifInfo.mDataValue) == null || datas.length <= 5 || datas[5] != 17) {
            return false;
        }
        return true;
    }

    public TIFChannelInfo getTIFChannelInfoByProviderId(long providerChannelId) {
        MtkLog.d(TAG, "start getTIFChannelInfoByProviderId>>" + providerChannelId);
        Cursor c = this.mContentResolver.query(buildChannelUri(providerChannelId), (String[]) null, (String) null, (String[]) null, ORDERBY);
        if (c == null) {
            return null;
        }
        while (c.moveToNext()) {
            TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
            MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
            if (tempApiChannel != null) {
                temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                c.close();
                MtkLog.d(TAG, "end getTIFChannelInfoByProviderId>>" + temTIFChannel.mDisplayName);
                return temTIFChannel;
            }
        }
        c.close();
        MtkLog.d(TAG, "end getTIFChannelInfoByProviderId>>null");
        return null;
    }

    public List<TIFChannelInfo> getTIFChannelInfofro3rd() {
        MtkLog.d(TAG, "getTIFChannelInfofro3rd:");
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
        if (c == null) {
            MtkLog.d(TAG, "getTIFChannelInfoBySource, c");
            return null;
        }
        List<TIFChannelInfo> list = new ArrayList<>();
        while (c.moveToNext()) {
            TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
            MtkLog.d(TAG, "getTIFChannelInfofro3rd, temTIFChannel s" + temTIFChannel);
            list.add(temTIFChannel);
        }
        c.close();
        MtkLog.d(TAG, "getTIFChannelInfofro3rd, " + list.size());
        return list;
    }

    public List<TIFChannelInfo> getTIFChannelInfoBySource(String sourceId) {
        MtkLog.d(TAG, "getTIFChannelInfoBySource:" + sourceId);
        List<TIFChannelInfo> mChannelList = getChannelListForFindOrNomal();
        List<TIFChannelInfo> list = new ArrayList<>();
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if (temTIFChannel.mInputServiceName.equals(sourceId)) {
                MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
                if (tempApiChannel != null && TIFFunctionUtil.checkChMask(tempApiChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL)) {
                    temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                }
                list.add(temTIFChannel);
            }
        }
        MtkLog.d(TAG, "getTIFChannelInfoBySource, " + list.size());
        return list;
    }

    public TIFChannelInfo getTIFChannelInfoPLusByProviderId(long providerChannelId) {
        MtkLog.d(TAG, "start getTIFChannelInfoByProviderId>>" + providerChannelId);
        Cursor c = this.mContentResolver.query(buildChannelUri(providerChannelId), (String[]) null, (String) null, (String[]) null, ORDERBY);
        if (c == null) {
            MtkLog.d(TAG, "end getTIFChannelInfoByProviderId>>" + c);
            return null;
        } else if (c.moveToNext()) {
            TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
            temTIFChannel.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
            c.close();
            return temTIFChannel;
        } else {
            c.close();
            MtkLog.d(TAG, "end getTIFChannelInfoByProviderId>>null");
            return null;
        }
    }

    public MtkTvChannelInfoBase getAPIChannelInfoById(int channelId) {
        MtkLog.d(TAG, "start getAPIChannelInfoById>>" + channelId);
        List<TIFChannelInfo> mChannelList = new ArrayList<>();
        mChannelList.addAll(getCurrentSVLChannelList());
        for (TIFChannelInfo temTIFChannel : mChannelList) {
            if (temTIFChannel != null && temTIFChannel.mInternalProviderFlag3 == channelId) {
                MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
                temTIFChannel.mMtkTvChannelInfo = tempApiChannel;
                MtkLog.d(TAG, "end getAPIChannelInfoById>>" + temTIFChannel.mDisplayName);
                return tempApiChannel;
            }
        }
        MtkLog.d(TAG, "end getAPIChannelInfoById>>null");
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x001c, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x001e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCurrentSvlChannelListLength(int r10, int r11) {
        /*
            r9 = this;
            java.lang.String r0 = "TIFChannelManager"
            java.lang.String r1 = "start getCurrentSvlChannelListLength  >>>"
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            r0 = 0
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r2 = r9.getSvlIdSelectionArgs()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r3 != 0) goto L_0x001e
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r3 == 0) goto L_0x0024
        L_0x001e:
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?"
            java.lang.String[] r2 = r9.getSvlIdAndInputIdSelectionArgs()
        L_0x0024:
            android.content.ContentResolver r3 = r9.mContentResolver
            android.net.Uri r4 = android.media.tv.TvContract.Channels.CONTENT_URI
            r5 = 0
            java.lang.String r8 = "substr(cast(internal_provider_data as varchar),19,10)"
            r6 = r1
            r7 = r2
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)
            if (r3 != 0) goto L_0x0034
            return r0
        L_0x0034:
            r4 = 0
            r5 = 0
            java.lang.String r6 = "TIFChannelManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "getCurrentSvlChannelListLength c.moveToNext()>>> c  "
            r7.append(r8)
            int r8 = r3.getCount()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
        L_0x0050:
            boolean r6 = r3.moveToNext()
            if (r6 == 0) goto L_0x006e
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r4 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r3)
            boolean r6 = r4.mIsBrowsable
            if (r6 != 0) goto L_0x005f
            goto L_0x0050
        L_0x005f:
            long[] r6 = r4.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = r9.getAPIChannelInfoByBlobData(r6)
            boolean r6 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r5, (int) r10, (int) r11)
            if (r6 == 0) goto L_0x0050
            int r0 = r0 + 1
            goto L_0x0050
        L_0x006e:
            r3.close()
            java.lang.String r6 = "TIFChannelManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "end getCurrentSvlChannelListLength  >>>"
            r7.append(r8)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r6, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getCurrentSvlChannelListLength(int, int):int");
    }

    public int getChannelListConfirmLength(int count, int attentionMask, int expectValue) {
        int length = queryChanelListWithMaskCount(count, attentionMask, expectValue).size();
        if (!CommonIntegration.isUSRegion() || !TIFFunctionUtil.checkChMask(getAPIChannelInfoByChannelId(TIFFunctionUtil.getCurrentChannelId()), TIFFunctionUtil.CH_LIST_MASK, 0)) {
            return length;
        }
        return length + 1;
    }

    public long[] parserTIFChannelData(TIFChannelInfo tIFChannelInfo, String data) {
        String str = data;
        long[] v = new long[6];
        MtkLog.d(TAG, "data:" + str);
        if (str == null) {
            return v;
        }
        String[] value = str.split(",");
        if (value.length < 5) {
            MtkLog.d(TAG, "parserTIFChannelData data.length <6");
            return v;
        }
        long mSvlId = Long.parseLong(value[1]);
        long mSvlRecId = Long.parseLong(value[2]);
        long channelId = Long.parseLong(value[3]);
        long j = (mSvlId << 16) + mSvlRecId;
        v[0] = mSvlId;
        v[1] = mSvlRecId;
        v[2] = channelId;
        v[4] = (mSvlId << 16) + mSvlRecId;
        if (value.length == 6) {
            v[5] = Long.parseLong(value[5]);
        }
        tIFChannelInfo.mDataValue = v;
        return v;
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase getAPIChannelInfoByBlobData(long[] r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            if (r6 == 0) goto L_0x003a
            int r1 = r6.length     // Catch:{ all -> 0x0037 }
            r2 = 3
            if (r1 <= r2) goto L_0x003a
            com.mediatek.twoworlds.tv.MtkTvChannelList r1 = com.mediatek.twoworlds.tv.MtkTvChannelList.getInstance()     // Catch:{ Exception -> 0x001a }
            r2 = 0
            r2 = r6[r2]     // Catch:{ Exception -> 0x001a }
            int r2 = (int) r2     // Catch:{ Exception -> 0x001a }
            r3 = 1
            r3 = r6[r3]     // Catch:{ Exception -> 0x001a }
            int r3 = (int) r3     // Catch:{ Exception -> 0x001a }
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r1 = r1.getChannelInfoBySvlRecId(r2, r3)     // Catch:{ Exception -> 0x001a }
            monitor-exit(r5)
            return r1
        L_0x001a:
            r1 = move-exception
            java.lang.String r2 = "TIFChannelManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0037 }
            r3.<init>()     // Catch:{ all -> 0x0037 }
            java.lang.String r4 = "getChannelById "
            r3.append(r4)     // Catch:{ all -> 0x0037 }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0037 }
            r3.append(r4)     // Catch:{ all -> 0x0037 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0037 }
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r2, r3)     // Catch:{ all -> 0x0037 }
            monitor-exit(r5)
            return r0
        L_0x0037:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        L_0x003a:
            monitor-exit(r5)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getAPIChannelInfoByBlobData(long[]):com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase");
    }

    public MtkTvChannelInfoBase getAPIChannelInfoByChannelId(int channelId) {
        if (this.maps != null && this.maps.size() > 0) {
            MtkLog.d(TAG, "getAPIChannelInfoByChannelI maps.size()" + this.maps.size());
            return this.maps.get(Integer.valueOf(channelId));
        } else if (!this.getAllChannelsrunning) {
            MtkLog.d(TAG, "getAPIChannelInfoByChannelI maps.size() null");
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    TIFChannelManager.this.maps = new HashMap();
                    TIFChannelManager.this.getAllChannels();
                }
            });
            return null;
        } else {
            MtkLog.d(TAG, "getAllchannels is running,go on updata");
            this.upDateAllChannelsrunning = true;
            return null;
        }
    }

    public void updateMapsChannelInfoByList(List<MtkTvChannelInfoBase> list) {
        if (this.maps != null) {
            for (MtkTvChannelInfoBase channelInfo : list) {
                this.maps.put(Integer.valueOf(channelInfo.getChannelId()), channelInfo);
            }
        }
    }

    private String[] getSvlIdSelectionArgs() {
        int svlId = this.mCI.getSvl();
        String path = this.mCI.getCurrentFocus();
        String inputId = InputSourceManager.getInstance().getTvInputId(path);
        MtkLog.d(TAG, "getSvlIdSelectionArgs>>>" + inputId + ">>" + InputSourceManager.getInstance().isCurrentTvSource(path));
        return new String[]{String.format("%05d", new Object[]{Integer.valueOf(svlId)})};
    }

    private String[] getSvlIdAndInputIdSelectionArgs() {
        int svlId = this.mCI.getSvl();
        String[] selectionArgs = {String.format("%05d", new Object[]{Integer.valueOf(svlId)})};
        CommonIntegration commonIntegration = this.mCI;
        if (!CommonIntegration.isCNRegion()) {
            CommonIntegration commonIntegration2 = this.mCI;
            if (!CommonIntegration.isEUPARegion()) {
                return selectionArgs;
            }
        }
        if (this.mCI.isCurrentSourceATV()) {
            return new String[]{String.format("%05d", new Object[]{Integer.valueOf(svlId)}), "HW1"};
        } else if (!this.mCI.isCurrentSourceDTV()) {
            return selectionArgs;
        } else {
            return new String[]{String.format("%05d", new Object[]{Integer.valueOf(svlId)}), "HW0"};
        }
    }

    private String[] getSvlIdAndChannelIdSelectionArgs(int channelId) {
        long newId = ((long) channelId) & 4294967295L;
        MtkLog.d(TAG, "channelId>>>" + channelId + ">>" + newId);
        int svlId = this.mCI.getSvl();
        String path = this.mCI.getCurrentFocus();
        String inputId = InputSourceManager.getInstance().getTvInputId(path);
        MtkLog.d(TAG, "getSvlIdAndChannelIdSelectionArgs>>>" + inputId + ">>" + InputSourceManager.getInstance().isCurrentTvSource(path));
        return new String[]{String.format("%05d", new Object[]{Integer.valueOf(svlId)}), String.format("%010d", new Object[]{Long.valueOf(newId)})};
    }

    public boolean channelPre() {
        if (this.mCI.isCurrentSourceTv() && this.mCI.isMenuInputTvBlock()) {
            return false;
        }
        if (this.mCI.isPipOrPopState()) {
            ArrayList<String> sourceList = InputSourceManager.getInstance().getConflictSourceList();
            String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName(this.mCI.getCurrentFocus());
            if (!this.mCI.isCurrentSourceTv() || (sourceList != null && sourceList.contains(sourceName))) {
                MtkLog.d(TAG, "input source in conflict");
                return false;
            }
        }
        if (this.mCI.isCurrentSourceTv()) {
            TIFChannelInfo tIFChannelInfo = getTIFChannelInfoById(this.mCI.getLastChannelId());
            int lastId = this.mCI.getLastChannelId();
            int currentId = this.mCI.getCurrentChannelId();
            if (tIFChannelInfo == null || lastId == currentId) {
                return false;
            }
            return selectChannelByTIFInfo(tIFChannelInfo);
        }
        this.mCI.iSetSourcetoTv();
        return false;
    }

    public TIFChannelInfo getTifChannelInfoByUri(Uri channelUri) {
        Cursor c;
        MtkLog.d(TAG, "start getTifChannelInfoByUri  channelUri>>>" + channelUri);
        if (channelUri == null || (c = this.mContentResolver.query(channelUri, (String[]) null, (String) null, (String[]) null, ORDERBY)) == null) {
            return null;
        }
        MtkLog.d(TAG, "getTifChannelInfoByUri c.moveToNext()>>> c  " + c.getCount());
        if (c.moveToNext()) {
            TIFChannelInfo temTIFChannel = TIFChannelInfo.parse(c);
            MtkLog.d(TAG, " getTifChannelInfoByUri temTIFChannel >>>" + temTIFChannel);
            temTIFChannel.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(temTIFChannel.mDataValue);
            c.close();
            return temTIFChannel;
        }
        c.close();
        MtkLog.d(TAG, "end getTifChannelInfoByUri  >>>" + null);
        return null;
    }

    public boolean selectChannelByTIFInfo(TIFChannelInfo tifChannel) {
        return doSelectChannelByTIFInfo(tifChannel);
    }

    private boolean doSelectChannelByTIFInfo(TIFChannelInfo tifChannel) {
        Log.d(TAG, "selectChannelByTIFInfo chInfo = " + tifChannel);
        if (tifChannel == null) {
            ScanViewActivity.isSelectedChannel = true;
            Log.d(TAG, Log.getStackTraceString(new Throwable()));
            return false;
        }
        if (tifChannel.mMtkTvChannelInfo != null && !CommonIntegration.isCNRegion() && CommonIntegration.isEUPARegion() && tifChannel.mMtkTvChannelInfo != null) {
            TIFChannelInfo currentInfo = getTIFChannelInfoById(TIFFunctionUtil.getCurrentChannelId());
            if (tifChannel.mMtkTvChannelInfo.getBrdcstType() == 1) {
                if (!(currentInfo == null || currentInfo.mMtkTvChannelInfo == null || currentInfo.mMtkTvChannelInfo.getBrdcstType() == 1)) {
                    MtkLog.d(TAG, "D-A selectChannelByTIFInfo setChgSource = true");
                }
            } else if (!(currentInfo == null || currentInfo.mMtkTvChannelInfo == null || currentInfo.mMtkTvChannelInfo.getBrdcstType() != 1)) {
                MtkLog.d(TAG, "A-D selectChannelByTIFInfo setChgSource = true");
            }
        }
        return selectChannelByTIFId(tifChannel.mId, tifChannel.mInputServiceName);
    }

    public boolean selectChannelByTIFId(long tifChannelId) {
        MtkLog.d(TAG, "selectChannelByTIFId channelId =" + tifChannelId);
        String path = this.mCI.getCurrentFocus();
        String channelUri = buildChannelUri(tifChannelId).toString();
        MtkLog.d(TAG, "path>>>" + path + "  uri:" + channelUri);
        Intent intent = new Intent("com.mediatek.tv.selectchannel");
        intent.putExtra("path", path);
        intent.putExtra("channelUriStr", channelUri);
        this.mContext.sendBroadcast(intent);
        return true;
    }

    private boolean selectChannelByTIFId(long tifChannelId, String inputId) {
        Log.d(TAG, "selectChannelByTIFId channelId =" + tifChannelId);
        final Bundle params = new Bundle();
        String path = this.mCI.getCurrentFocus();
        if (TextUtils.isEmpty(inputId)) {
            String inputServiceName = getTIFChannelInfoPLusByProviderId(tifChannelId).mInputServiceName;
            if (TextUtils.isEmpty(inputServiceName)) {
                Log.i(TAG, "inputId again null");
                return false;
            }
            inputId = inputServiceName;
        }
        final String input_Id = inputId;
        if (path != null && TurnkeyUiMainActivity.getInstance() != null && path.equalsIgnoreCase("main") && TurnkeyUiMainActivity.getInstance().getTvView() != null) {
            final long j = tifChannelId;
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    if (TurnkeyUiMainActivity.getInstance().getTvView() != null) {
                        params.putInt("Path_Logo", 0);
                        TurnkeyUiMainActivity.getInstance().getTvView().tune(input_Id, TIFChannelManager.this.buildChannelUri(j), params);
                        ScanViewActivity.isSelectedChannel = true;
                    }
                }
            });
        } else if (path == null || TurnkeyUiMainActivity.getInstance() == null || !path.equalsIgnoreCase("sub") || TurnkeyUiMainActivity.getInstance().getPipView() == null) {
            MtkLog.d(TAG, "selectChannelByTIFId return false");
            ScanViewActivity.isSelectedChannel = true;
            return false;
        } else {
            final Bundle bundle = params;
            final String str = input_Id;
            final long j2 = tifChannelId;
            TurnkeyUiMainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    if (TurnkeyUiMainActivity.getInstance().getPipView() != null) {
                        Log.d(TIFChannelManager.TAG, "setNewTvMode:" + TIFChannelManager.this.mCI.getCurrentTVState());
                        MtkTvMultiView.getInstance().setNewTvMode(TIFChannelManager.this.mCI.getCurrentTVState());
                        bundle.putInt("Path_Logo", 1);
                        TurnkeyUiMainActivity.getInstance().getPipView().tune(str, TIFChannelManager.this.buildChannelUri(j2), bundle);
                        ScanViewActivity.isSelectedChannel = true;
                    }
                }
            });
        }
        return true;
    }

    public Uri buildChannelUri(long channelId) {
        return ContentUris.withAppendedId(TvContract.Channels.CONTENT_URI, channelId);
    }

    public boolean channelUpDownByMask(boolean isUp, int mask, int val) {
        TIFChannelInfo tifChannel;
        MtkLog.d(TAG, "channelUpDownByMask");
        if (this.mCI.isPipOrPopState()) {
            ArrayList<String> sourceList = InputSourceManager.getInstance().getConflictSourceList();
            String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName(this.mCI.getCurrentFocus());
            if (!this.mCI.isCurrentSourceTv() || (sourceList != null && sourceList.contains(sourceName))) {
                MtkLog.d(TAG, "input source in conflict:" + sourceName);
                return false;
            }
        }
        if (!this.mCI.isCurrentSourceTv()) {
            if (this.mCI.is3rdTVSource()) {
                TIFChannelInfo tifChannel2 = getTIFUpOrDownChannelfor3rdsource(!isUp);
                if (tifChannel2 != null) {
                    return selectChannelByTIFInfo(tifChannel2);
                }
            } else {
                this.mCI.iSetSourcetoTv();
            }
            return false;
        } else if (!this.mCI.isMenuInputTvBlock() && (tifChannel = getTIFUpOrDownChannel(!isUp, mask, val)) != null) {
            return selectChannelByTIFInfo(tifChannel);
        } else {
            return false;
        }
    }

    public TIFChannelInfo getUpAndDownChannel(boolean isUp) {
        return getTIFUpOrDownChannel(isUp, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
    }

    public boolean channelUpDownByMaskAndSat(int mask, int val, int satRecId, boolean isUp) {
        TIFChannelInfo tifChannelInfo;
        MtkLog.d(TAG, "channelUpDownByMaskAndSat");
        if (this.mCI.isPipOrPopState()) {
            ArrayList<String> sourceList = InputSourceManager.getInstance().getConflictSourceList();
            String sourceName = InputSourceManager.getInstance().getCurrentInputSourceName(this.mCI.getCurrentFocus());
            if (!this.mCI.isCurrentSourceTv() || (sourceList != null && sourceList.contains(sourceName))) {
                MtkLog.d(TAG, "input source in conflict:" + sourceName);
                return false;
            }
        }
        if (!this.mCI.isCurrentSourceTv()) {
            this.mCI.iSetSourcetoTv();
            return false;
        } else if (!this.mCI.isMenuInputTvBlock() && (tifChannelInfo = getTIFUpOrDownChannelBySateRecId(!isUp, satRecId, mask, val)) != null) {
            return selectChannelByTIFInfo(tifChannelInfo);
        } else {
            return false;
        }
    }

    public boolean hasDTVChannels() {
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID, getSvlIdSelectionArgs(), ORDERBY);
        if (c == null) {
            return false;
        }
        MtkLog.d(TAG, "hasDTVChannelsWithSvlid c.moveToNext()>>> c  " + c.getCount());
        while (c.moveToNext()) {
            MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(TIFChannelInfo.parse(c).mDataValue);
            if (TIFFunctionUtil.checkChMask(tempApiChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL) && !(tempApiChannel instanceof MtkTvAnalogChannelInfo)) {
                c.close();
                return true;
            }
        }
        c.close();
        return false;
    }

    public boolean hasATVChannels() {
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID, getSvlIdSelectionArgs(), ORDERBY);
        if (c == null) {
            return false;
        }
        MtkLog.d(TAG, "hasDTVChannelsWithSvlid c.moveToNext()>>> c  " + c.getCount());
        while (c.moveToNext()) {
            MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(TIFChannelInfo.parse(c).mDataValue);
            if (TIFFunctionUtil.checkChMask(tempApiChannel, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL) && (tempApiChannel instanceof MtkTvAnalogChannelInfo)) {
                c.close();
                return true;
            }
        }
        c.close();
        return false;
    }

    public boolean hasActiveChannel() {
        return hasActiveChannel(false);
    }

    public boolean hasActiveChannel(boolean showSkip) {
        List<TIFChannelInfo> chList = queryChanelListWithMaskCount(1, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL, showSkip);
        if (chList != null && chList.size() > 0) {
            MtkLog.d(TAG, "hasActiveChannel true~ ");
            return true;
        } else if (CommonIntegration.isUSRegion() && TIFFunctionUtil.checkChMask(getAPIChannelInfoById(TIFFunctionUtil.getCurrentChannelId()), TIFFunctionUtil.CH_LIST_MASK, 0)) {
            return true;
        } else {
            MtkLog.d(TAG, "hasActiveChannel false ");
            return false;
        }
    }

    public boolean hasOneChannel() {
        List<TIFChannelInfo> chList = queryChanelListWithMaskCount(2, TIFFunctionUtil.CH_LIST_MASK, TIFFunctionUtil.CH_LIST_VAL);
        if (chList == null || chList.size() > 1) {
            return false;
        }
        MtkLog.d(TAG, "has only one channel true~ ");
        return true;
    }

    public int getSatelliteChannelCount(int sateRecordId, int mask, int val) {
        int count = 0;
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID, getSvlIdSelectionArgs(), ORDERBY);
        if (c == null) {
            return 0;
        }
        while (c.moveToNext()) {
            MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(TIFChannelInfo.parse(c).mDataValue);
            if (TIFFunctionUtil.checkChMask(tempApiChannel, mask, val) && (tempApiChannel instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel).getSatRecId() == sateRecordId) {
                count++;
            }
        }
        c.close();
        return count;
    }

    public int getSatelliteChannelConfirmCount(int sateRecordId, int attentionCount, int mask, int val) {
        int count = 0;
        Cursor c = this.mContentResolver.query(TvContract.Channels.CONTENT_URI, (String[]) null, SELECTION_WITH_SVLID, getSvlIdSelectionArgs(), ORDERBY);
        if (c == null) {
            return 0;
        }
        while (c.moveToNext()) {
            MtkTvChannelInfoBase tempApiChannel = getAPIChannelInfoByBlobData(TIFChannelInfo.parse(c).mDataValue);
            if (TIFFunctionUtil.checkChMask(tempApiChannel, mask, val) && (tempApiChannel instanceof MtkTvDvbChannelInfo) && ((MtkTvDvbChannelInfo) tempApiChannel).getSatRecId() == sateRecordId && (count = count + 1) > attentionCount) {
                break;
            }
        }
        c.close();
        return count;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0016, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isChannelsExist() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            java.lang.String r2 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r3 = r10.getSvlIdSelectionArgs()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r10.mCI
            boolean r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r4 != 0) goto L_0x0018
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r4 = r10.mCI
            boolean r4 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r4 == 0) goto L_0x001e
        L_0x0018:
            java.lang.String r2 = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?"
            java.lang.String[] r3 = r10.getSvlIdAndInputIdSelectionArgs()
        L_0x001e:
            android.content.ContentResolver r4 = r10.mContentResolver
            android.net.Uri r5 = android.media.tv.TvContract.Channels.CONTENT_URI
            r6 = 0
            java.lang.String r9 = "substr(cast(internal_provider_data as varchar),19,10)"
            r7 = r2
            r8 = r3
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)
            if (r4 != 0) goto L_0x002f
            r5 = 0
            return r5
        L_0x002f:
            boolean r5 = r4.moveToNext()
            if (r5 == 0) goto L_0x003a
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r1 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r4)
            r0 = 1
        L_0x003a:
            r4.close()
            java.lang.String r5 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "isChannelsExist:"
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.isChannelsExist():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x002f, code lost:
        if (com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion() != false) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo> getAttentionMaskChannels(int r10, int r11, int r12) {
        /*
            r9 = this;
            java.lang.String r0 = "TIFChannelManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "start getAttentionMaskChannels~"
            r1.append(r2)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r0, r1)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ?"
            java.lang.String[] r2 = r9.getSvlIdSelectionArgs()
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isCNRegion()
            if (r3 != 0) goto L_0x0031
            com.mediatek.wwtv.tvcenter.util.CommonIntegration r3 = r9.mCI
            boolean r3 = com.mediatek.wwtv.tvcenter.util.CommonIntegration.isEUPARegion()
            if (r3 == 0) goto L_0x0037
        L_0x0031:
            java.lang.String r1 = "substr(cast(internal_provider_data as varchar),7,5) = ? and substr(input_id ,length(input_id)-2,3) = ?"
            java.lang.String[] r2 = r9.getSvlIdAndInputIdSelectionArgs()
        L_0x0037:
            android.content.ContentResolver r3 = r9.mContentResolver
            android.net.Uri r4 = android.media.tv.TvContract.Channels.CONTENT_URI
            r5 = 0
            java.lang.String r8 = "substr(cast(internal_provider_data as varchar),19,10)"
            r6 = r1
            r7 = r2
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)
            if (r3 != 0) goto L_0x0047
            return r0
        L_0x0047:
            java.lang.String r4 = "TIFChannelManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getAttentionMaskChannels    c>>>"
            r5.append(r6)
            int r6 = r3.getCount()
            r5.append(r6)
            java.lang.String r6 = "   ORDERBY>>"
            r5.append(r6)
            java.lang.String r6 = "substr(cast(internal_provider_data as varchar),19,10)"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r4, r5)
            r4 = 0
        L_0x006c:
            boolean r5 = r3.moveToNext()
            if (r5 == 0) goto L_0x0092
            com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo r4 = com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo.parse(r3)
            long[] r5 = r4.mDataValue
            com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase r5 = r9.getAPIChannelInfoByBlobData(r5)
            boolean r6 = com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil.checkChMask((com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase) r5, (int) r10, (int) r11)
            if (r6 == 0) goto L_0x0091
            r4.mMtkTvChannelInfo = r5
            r0.add(r4)
            r6 = -1
            if (r12 == r6) goto L_0x0091
            int r6 = r0.size()
            if (r6 < r12) goto L_0x0091
            goto L_0x0092
        L_0x0091:
            goto L_0x006c
        L_0x0092:
            r3.close()
            java.lang.String r5 = "TIFChannelManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "end getAttentionMaskChannels  tIFChannelInfoList>>>"
            r6.append(r7)
            int r7 = r0.size()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            com.mediatek.wwtv.tvcenter.util.MtkLog.d(r5, r6)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager.getAttentionMaskChannels(int, int, int):java.util.List");
    }

    public void selectCurrentChannelWithTIF() {
        this.mSharedPreferences = this.mContext.getSharedPreferences(SPNAME, 0);
        int channel3rdMId = this.mSharedPreferences.getInt(TIFFunctionUtil.current3rdMId, -1);
        MtkLog.d(TAG, "selectCurrentChannelWithTIF, channel3rdMId :" + channel3rdMId);
        if (channel3rdMId > 0) {
            TIFChannelInfo mTIFChannelInfo = getTifChannelInfoByUri(buildChannelUri((long) channel3rdMId));
            MtkLog.d(TAG, "selectCurrentChannelWithTIF, mTIFChannelInfo : " + mTIFChannelInfo);
            if (mTIFChannelInfo != null) {
                MtkLog.d(TAG, "selectCurrentChannelWithTIF, selectChannelByTIFInfo 3rd");
                selectChannelByTIFInfo(mTIFChannelInfo);
                return;
            }
            MtkLog.d(TAG, "selectCurrentChannelWithTIF,currentChannelInfo 3rd is null");
            return;
        }
        int currentChannelId = TIFFunctionUtil.getCurrentChannelId();
        MtkLog.d(TAG, "selectCurrentChannelWithTIF, newId =" + (((long) currentChannelId) & 4294967295L));
        TIFChannelInfo currentChannelInfo = getTIFChannelInfoById(currentChannelId);
        if (currentChannelInfo != null) {
            MtkLog.d(TAG, "selectCurrentChannelWithTIF, selectChannelByTIFInfo");
            selectChannelByTIFInfo(currentChannelInfo);
            return;
        }
        MtkLog.d(TAG, "selectCurrentChannelWithTIF,currentChannelInfo is null");
    }

    public static void enableAllChannels(Context context) {
        ContentValues values = new ContentValues();
        values.put("browsable", 1);
        context.getContentResolver().update(TvContract.Channels.CONTENT_URI, values, (String) null, (String[]) null);
    }

    public List<TIFChannelInfo> getTIFChannelList(List<MtkTvChannelInfoBase> apiChannelList) {
        List<TIFChannelInfo> chlist = new ArrayList<>();
        if (apiChannelList == null || apiChannelList.size() == 0) {
            return chlist;
        }
        for (MtkTvChannelInfoBase tempApiChannel : apiChannelList) {
            new TIFChannelInfo();
            TIFChannelInfo tempTIFChInfo = getTIFChannelInfoById(tempApiChannel.getChannelId());
            MtkLog.d(TAG, "getTIFChannelInfoById tempTIFChInfo " + tempTIFChInfo);
            chlist.add(tempTIFChInfo);
        }
        return chlist;
    }

    private void init() {
        Log.d(TAG, "init()");
        this.mHandler = new ChannelDataManagerHandler(this);
        this.mChannelComparator = new TIFChannelInfo.DefaultComparator(this.mContext, this.mInputManager);
        this.mChannelComparator.setDetectDuplicatesEnabled(true);
        this.mChannelObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                if (!TIFChannelManager.this.mHandler.hasMessages(1000)) {
                    TIFChannelManager.this.mHandler.sendEmptyMessage(1000);
                }
            }
        };
        this.customerComparator = new TIFChannelInfo.CustomerComparator(this.mContext, this.CURRENT_CHANNEL_SORT);
        getAllChannels();
    }

    /* access modifiers changed from: package-private */
    public ContentObserver getContentObserver() {
        return this.mChannelObserver;
    }

    public void start() {
        Log.d(TAG, "start() mStarted =" + this.mStarted);
        if (!this.mStarted) {
            this.mStarted = true;
            handleUpdateChannels();
            this.mContentResolver.registerContentObserver(TvContract.Channels.CONTENT_URI, true, this.mChannelObserver);
            this.mInputManager.addCallback(this.mTvInputCallback);
            MtkLog.d(TAG, "start() mChannels.size() = " + getChannelCount());
        }
    }

    public void stop() {
        Log.d(TAG, "stop()");
        if (this.mStarted) {
            this.mStarted = false;
            this.mDbLoadFinished = false;
            this.mInputManager.removeCallback(this.mTvInputCallback);
            this.mContentResolver.unregisterContentObserver(this.mChannelObserver);
            this.mHandler.removeCallbacksAndMessages((Object) null);
            this.mChannelWrapperMap.clear();
            clearChannels();
            this.mPostRunnablesAfterChannelUpdate.clear();
            if (this.mChannelsUpdateTask != null) {
                this.mChannelsUpdateTask.cancel(true);
                this.mChannelsUpdateTask = null;
            }
            applyUpdatedValuesToDb();
        }
    }

    public void addListener(Listener listener) {
        Log.d(TAG, "addListener " + listener);
        if (listener != null) {
            this.mListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        Log.d(TAG, "removeListener " + listener);
        if (listener != null) {
            this.mListeners.remove(listener);
        }
    }

    public void addChannelListener(Long channelId, ChannelListener listener) {
        ChannelWrapper channelWrapper = this.mChannelWrapperMap.get(channelId);
        if (channelWrapper != null) {
            channelWrapper.addListener(listener);
        }
    }

    public void removeChannelListener(Long channelId, ChannelListener listener) {
        ChannelWrapper channelWrapper = this.mChannelWrapperMap.get(channelId);
        if (channelWrapper != null) {
            channelWrapper.removeListener(listener);
        }
    }

    public boolean isDbLoadFinished() {
        return this.mDbLoadFinished;
    }

    public int getChannelCount() {
        return this.mChannels.size();
    }

    public List<TIFChannelInfo> getChannelList() {
        return Collections.unmodifiableList(this.mChannels);
    }

    public List<TIFChannelInfo> getBrowsableChannelList() {
        List<TIFChannelInfo> channels = new ArrayList<>();
        for (TIFChannelInfo channel : this.mChannels) {
            if (channel.mIsBrowsable) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public int getChannelCountForInput(String inputId) {
        MutableInt count = this.mChannelCountMap.get(inputId);
        if (count == null) {
            return 0;
        }
        return count.value;
    }

    public boolean doesChannelExistInDb(long channelId) {
        return this.mChannelWrapperMap.get(Long.valueOf(channelId)) != null;
    }

    public boolean areAllChannelsHidden() {
        if (this.mChannels.isEmpty()) {
            return false;
        }
        for (TIFChannelInfo channel : this.mChannels) {
            if (channel.mIsBrowsable) {
                return false;
            }
        }
        return true;
    }

    public TIFChannelInfo getChannel(Long channelId) {
        ChannelWrapper channelWrapper = this.mChannelWrapperMap.get(channelId);
        if (channelWrapper == null || channelWrapper.mInputRemoved) {
            return null;
        }
        return channelWrapper.mChannel;
    }

    public void updateBrowsable(Long channelId, boolean browsable) {
        updateBrowsable(channelId, browsable, false);
    }

    public void updateBrowsable(Long channelId, boolean browsable, boolean skipNotifyChannelBrowsableChanged) {
    }

    public void notifyChannelBrowsableChanged() {
        for (Listener l : this.mListeners) {
            l.onChannelBrowsableChanged();
        }
    }

    /* access modifiers changed from: private */
    public void notifyChannelListUpdated() {
        for (Listener l : this.mListeners) {
            l.onChannelListUpdated();
        }
    }

    /* access modifiers changed from: private */
    public void notifyLoadFinished() {
        for (Listener l : this.mListeners) {
            l.onLoadFinished();
        }
    }

    public void updateChannels(Runnable postRunnable) {
        MtkLog.d(TAG, "updateChannels() DestroyApp.isCurTaskTKUI() :" + DestroyApp.isCurTaskTKUI());
        if (DestroyApp.isCurTaskTKUI()) {
            if (this.mChannelsUpdateTask != null) {
                this.mChannelsUpdateTask.cancel(true);
                this.mChannelsUpdateTask = null;
            }
            this.mPostRunnablesAfterChannelUpdate.add(postRunnable);
            if (!this.mHandler.hasMessages(1000)) {
                MtkLog.d(TAG, "updateChannels() MSG_UPDATE_CHANNELS");
                this.mHandler.sendEmptyMessage(1000);
            }
        }
    }

    public void updateLocked(Long channelId, boolean locked) {
        ChannelWrapper channelWrapper = this.mChannelWrapperMap.get(channelId);
        if (channelWrapper != null && channelWrapper.mChannel.mLocked != locked) {
            channelWrapper.mChannel.mLocked = locked;
            if (locked == channelWrapper.mLockedInDb) {
                this.mLockedUpdateChannelIds.remove(Long.valueOf(channelWrapper.mChannel.mId));
            } else {
                this.mLockedUpdateChannelIds.add(Long.valueOf(channelWrapper.mChannel.mId));
            }
            channelWrapper.notifyChannelUpdated();
        }
    }

    public void applyUpdatedValuesToDb() {
        ArrayList<Long> browsableIds = new ArrayList<>();
        ArrayList<Long> unbrowsableIds = new ArrayList<>();
        for (Long id : this.mBrowsableUpdateChannelIds) {
            ChannelWrapper channelWrapper = this.mChannelWrapperMap.get(id);
            if (channelWrapper != null) {
                if (channelWrapper.mChannel.mIsBrowsable) {
                    browsableIds.add(id);
                } else {
                    unbrowsableIds.add(id);
                }
                channelWrapper.mBrowsableInDb = channelWrapper.mChannel.mIsBrowsable;
            }
        }
        if (browsableIds.size() != 0) {
            updateOneColumnValue("browsable", 1, browsableIds);
        }
        if (unbrowsableIds.size() != 0) {
            updateOneColumnValue("browsable", 0, unbrowsableIds);
        }
        this.mBrowsableUpdateChannelIds.clear();
        ArrayList<Long> lockedIds = new ArrayList<>();
        ArrayList<Long> unlockedIds = new ArrayList<>();
        for (Long id2 : this.mLockedUpdateChannelIds) {
            ChannelWrapper channelWrapper2 = this.mChannelWrapperMap.get(id2);
            if (channelWrapper2 != null) {
                if (channelWrapper2.mChannel.mLocked) {
                    lockedIds.add(id2);
                } else {
                    unlockedIds.add(id2);
                }
                channelWrapper2.mLockedInDb = channelWrapper2.mChannel.mLocked;
            }
        }
        if (lockedIds.size() != 0) {
            updateOneColumnValue(TvContractCompat.Channels.COLUMN_LOCKED, 1, lockedIds);
        }
        if (unlockedIds.size() != 0) {
            updateOneColumnValue(TvContractCompat.Channels.COLUMN_LOCKED, 0, unlockedIds);
        }
        this.mLockedUpdateChannelIds.clear();
        Log.d(TAG, "applyUpdatedValuesToDb\n browsableIds size:" + browsableIds.size() + "\n unbrowsableIds size:" + unbrowsableIds.size() + "\n lockedIds size:" + lockedIds.size() + "\n unlockedIds size:" + unlockedIds.size());
    }

    /* access modifiers changed from: private */
    public void addChannel(TIFChannelInfo channel) {
        this.mChannels.add(channel);
        String inputId = channel.mInputServiceName;
        MutableInt count = this.mChannelCountMap.get(inputId);
        if (count == null) {
            this.mChannelCountMap.put(inputId, new MutableInt(1));
        } else {
            count.value++;
        }
    }

    /* access modifiers changed from: private */
    public void clearChannels() {
        this.mChannels.clear();
        this.mChannelCountMap.clear();
    }

    public void handleUpdateChannels() {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                Log.d(TIFChannelManager.TAG, "handleUpdateChannels() ");
                if (TIFChannelManager.this.mChannelsUpdateTask != null) {
                    boolean unused = TIFChannelManager.this.channelListUpdateState = true;
                    return;
                }
                boolean unused2 = TIFChannelManager.this.channelListUpdateState = false;
                QueryAllChannelsTask unused3 = TIFChannelManager.this.mChannelsUpdateTask = new QueryAllChannelsTask(TIFChannelManager.this.mContentResolver);
                TIFChannelManager.this.mChannelsUpdateTask.executeOnDbThread(new Void[0]);
                MtkLog.d(TIFChannelManager.TAG, "handleUpdateChannels() mChannels.size() = " + TIFChannelManager.this.getChannelCount());
            }
        });
    }

    public void reload() {
        Log.d(TAG, "reload() ");
        if (this.mDbLoadFinished && !this.mHandler.hasMessages(1000)) {
            Log.d(TAG, "reload() MSG_UPDATE_CHANNELS");
            this.mHandler.sendEmptyMessage(1000);
        }
    }

    private class ChannelWrapper {
        boolean mBrowsableInDb;
        final TIFChannelInfo mChannel;
        final Set<ChannelListener> mChannelListeners = new ArraySet();
        boolean mInputRemoved;
        boolean mLockedInDb;

        ChannelWrapper(TIFChannelInfo channel) {
            this.mChannel = channel;
            this.mBrowsableInDb = channel.mIsBrowsable;
            this.mLockedInDb = channel.mLocked;
            this.mInputRemoved = !TIFChannelManager.this.mInputManager.hasTvInputInfo(channel.mInputServiceName);
        }

        /* access modifiers changed from: package-private */
        public void addListener(ChannelListener listener) {
            this.mChannelListeners.add(listener);
        }

        /* access modifiers changed from: package-private */
        public void removeListener(ChannelListener listener) {
            this.mChannelListeners.remove(listener);
        }

        /* access modifiers changed from: package-private */
        public void notifyChannelUpdated() {
            for (ChannelListener l : this.mChannelListeners) {
                l.onChannelUpdated(this.mChannel);
            }
        }

        /* access modifiers changed from: package-private */
        public void notifyChannelRemoved() {
            for (ChannelListener l : this.mChannelListeners) {
                l.onChannelRemoved(this.mChannel);
            }
        }
    }

    private final class QueryAllChannelsTask extends AsyncDbTask.AsyncQueryListTask<TIFChannelInfo> {
        QueryAllChannelsTask(ContentResolver contentResolver) {
            super(TIFChannelManager.this.mDbExecutor, TIFChannelManager.this.mContext, TvContract.Channels.CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
        }

        /* access modifiers changed from: protected */
        public final TIFChannelInfo fromCursor(Cursor c) {
            return TIFChannelInfo.parse(c);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(List<TIFChannelInfo> channels) {
            Iterator<TIFChannelInfo> it;
            boolean channelRemoved;
            QueryAllChannelsTask unused = TIFChannelManager.this.mChannelsUpdateTask = null;
            Log.d(TIFChannelManager.TAG, "onPostExecute");
            if (channels != null) {
                Set<Long> removedChannelIds = new HashSet<>(TIFChannelManager.this.mChannelWrapperMap.keySet());
                List<ChannelWrapper> removedChannelWrappers = new ArrayList<>();
                List<ChannelWrapper> updatedChannelWrappers = new ArrayList<>();
                boolean channelAdded = false;
                boolean channelUpdated = false;
                boolean channelRemoved2 = false;
                Log.d(TIFChannelManager.TAG, "onPostExecute channels.size = " + channels.size());
                Iterator<TIFChannelInfo> it2 = channels.iterator();
                while (it2.hasNext()) {
                    TIFChannelInfo channel = it2.next();
                    long channelId = channel.mId;
                    boolean newlyAdded = !removedChannelIds.remove(Long.valueOf(channelId));
                    if (!channel.mType.equalsIgnoreCase(TIFFunctionUtil.channelType)) {
                        if (newlyAdded) {
                            MtkLog.d(TIFChannelManager.TAG, "onPostExecute newlyAdded = " + newlyAdded);
                            ChannelWrapper channelWrapper = new ChannelWrapper(channel);
                            channelRemoved = channelRemoved2;
                            it = it2;
                            TIFChannelManager.this.mChannelWrapperMap.put(Long.valueOf(channel.mId), channelWrapper);
                            if (!channelWrapper.mInputRemoved) {
                                channelAdded = true;
                            }
                        } else {
                            channelRemoved = channelRemoved2;
                            it = it2;
                            MtkLog.d(TIFChannelManager.TAG, "onPostExecute newlyAdded = " + newlyAdded);
                            ChannelWrapper channelWrapper2 = (ChannelWrapper) TIFChannelManager.this.mChannelWrapperMap.get(Long.valueOf(channelId));
                            channelWrapper2.mInputRemoved = TIFChannelManager.this.mInputManager.hasTvInputInfo(channel.mInputServiceName) ^ true;
                            if (!channelWrapper2.mChannel.hasSameReadOnlyInfo(channel)) {
                                TIFChannelInfo oldChannel = channelWrapper2.mChannel;
                                MtkLog.d(TIFChannelManager.TAG, "onPostExecute oldChannel.mIsBrowsable = " + oldChannel.mIsBrowsable);
                                channel.mLocked = oldChannel.mLocked;
                                channelWrapper2.mChannel.copyFrom(channel);
                                if (!channelWrapper2.mInputRemoved) {
                                    channelUpdated = true;
                                    updatedChannelWrappers.add(channelWrapper2);
                                }
                            } else {
                                channelWrapper2.mChannel.copyFrom(channel);
                                if (!channelWrapper2.mInputRemoved) {
                                    channelUpdated = true;
                                    updatedChannelWrappers.add(channelWrapper2);
                                }
                            }
                        }
                        channelRemoved2 = channelRemoved;
                        it2 = it;
                    } else if (!newlyAdded) {
                        TIFChannelManager.this.mChannelWrapperMap.remove(Long.valueOf(channel.mId));
                    }
                }
                boolean channelRemoved3 = channelRemoved2;
                MtkLog.d(TIFChannelManager.TAG, "onPostExecute removedChannelIds.size() = " + removedChannelIds.size());
                for (Long longValue : removedChannelIds) {
                    ChannelWrapper channelWrapper3 = (ChannelWrapper) TIFChannelManager.this.mChannelWrapperMap.remove(Long.valueOf(longValue.longValue()));
                    if (!channelWrapper3.mInputRemoved) {
                        removedChannelWrappers.add(channelWrapper3);
                        channelRemoved3 = true;
                    }
                }
                TIFChannelManager.this.clearChannels();
                Log.d(TIFChannelManager.TAG, "onPostExecute mChannelWrapperMap.size() " + TIFChannelManager.this.mChannelWrapperMap.size());
                for (ChannelWrapper channelWrapper4 : TIFChannelManager.this.mChannelWrapperMap.values()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("onPostExecute !channelWrapper ");
                    sb.append(!channelWrapper4.mInputRemoved);
                    MtkLog.d(TIFChannelManager.TAG, sb.toString());
                    if (!channelWrapper4.mInputRemoved) {
                        TIFChannelManager.this.addChannel(channelWrapper4.mChannel);
                    }
                }
                Collections.sort(TIFChannelManager.this.mChannels, TIFChannelManager.this.mChannelComparator);
                if (!TIFChannelManager.this.mDbLoadFinished) {
                    boolean unused2 = TIFChannelManager.this.mDbLoadFinished = true;
                    TIFChannelManager.this.notifyLoadFinished();
                } else if (channelAdded || channelUpdated || channelRemoved3) {
                    TIFChannelManager.this.notifyChannelListUpdated();
                }
                for (ChannelWrapper channelWrapper5 : removedChannelWrappers) {
                    channelWrapper5.notifyChannelRemoved();
                }
                for (ChannelWrapper channelWrapper6 : updatedChannelWrappers) {
                    channelWrapper6.notifyChannelUpdated();
                }
                for (Runnable r : TIFChannelManager.this.mPostRunnablesAfterChannelUpdate) {
                    r.run();
                }
                TIFChannelManager.this.mPostRunnablesAfterChannelUpdate.clear();
                TIFChannelManager.this.getCurrentSVLChannellist();
                if (TIFChannelManager.this.mChecker != null) {
                    TIFChannelManager.this.mChecker.check();
                }
                CommonIntegration.getInstance().getChannelAllandActionNum();
            }
        }
    }

    private void updateOneColumnValue(final String columnName, final int columnValue, final List<Long> ids) {
        AsyncDbTask.execute(new Runnable() {
            public void run() {
                String selection = Utils.buildSelectionForIds("_id", ids);
                ContentValues values = new ContentValues();
                values.put(columnName, Integer.valueOf(columnValue));
                TIFChannelManager.this.mContentResolver.update(TvContract.Channels.CONTENT_URI, values, selection, (String[]) null);
            }
        });
    }

    private String getBrowsableKey(TIFChannelInfo channel) {
        return channel.mInputServiceName + "|" + channel.mId;
    }

    private static class ChannelDataManagerHandler extends WeakHandler<TIFChannelManager> {
        public ChannelDataManagerHandler(TIFChannelManager channelDataManager) {
            super(Looper.getMainLooper(), channelDataManager);
        }

        public void handleMessage(Message msg, TIFChannelManager channelDataManager) {
            if (msg.what == 1000) {
                MtkLog.d(TIFChannelManager.TAG, "handleMessage() DestroyApp.isCurTaskTKUI() :" + DestroyApp.isCurTaskTKUI());
                if (DestroyApp.isCurTaskTKUI()) {
                    Log.d(TIFChannelManager.TAG, "ChannelDataManagerHandler handleMessage MSG_UPDATE_CHANNELS ");
                    channelDataManager.handleUpdateChannels();
                }
            }
        }
    }

    private List<TIFChannelInfo> getCurrentSvlChannellistNotCheckMask() {
        List<TIFChannelInfo> result = new ArrayList<>();
        int svlId = CommonIntegration.getInstance().getSvl();
        for (TIFChannelInfo chinfo : this.mChannels) {
            if (chinfo.mDataValue != null && chinfo.mDataValue.length == 6 && chinfo.mInternalProviderFlag1 == svlId) {
                chinfo.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(chinfo.mDataValue);
                result.add(chinfo);
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public void getCurrentSVLChannellist() {
        MtkLog.d(TAG, "getCurrentSVLChannellist start");
        MtkLog.d(TAG, "getCurrentSVLChannellist getChannelCount() is " + getChannelCount());
        int svlId = CommonIntegration.getInstance().getSvl();
        List<TIFChannelInfo> mChannelsForCurrentSVLBaseTemp = new ArrayList<>();
        List<TIFChannelInfo> mChannelsFor3RDSourceTemp = new ArrayList<>();
        for (TIFChannelInfo chinfo : this.mChannels) {
            if (chinfo.mDataValue != null && chinfo.mDataValue.length == 9) {
                MtkLog.d(TAG, "getCurrentSVLChannellist is livetv channel");
                CommonIntegration commonIntegration = this.mCI;
                if (!CommonIntegration.isCNRegion()) {
                    CommonIntegration commonIntegration2 = this.mCI;
                    if (!CommonIntegration.isEUPARegion()) {
                        if (chinfo.mInternalProviderFlag1 == svlId) {
                            mChannelsForCurrentSVLBaseTemp.add(chinfo);
                        }
                    }
                }
                if (this.mCI.isCurrentSourceATV() && chinfo.mInternalProviderFlag1 == 2) {
                    mChannelsForCurrentSVLBaseTemp.add(chinfo);
                } else if (this.mCI.isCurrentSourceDTV() && chinfo.mInternalProviderFlag1 == svlId) {
                    mChannelsForCurrentSVLBaseTemp.add(chinfo);
                }
            } else if (chinfo.mDataValue == null || chinfo.mDataValue.length != 9) {
                MtkLog.d(TAG, "getCurrentSVLChannellist is 3rd channel chinfo.mIsBrowsable" + chinfo.mIsBrowsable);
                if (chinfo.mIsBrowsable) {
                    mChannelsFor3RDSourceTemp.add(chinfo);
                }
            }
        }
        this.mChannelsForCurrentSVLBase.clear();
        this.mChannelsForCurrentSVLBase.addAll(mChannelsForCurrentSVLBaseTemp);
        MtkLog.d(TAG, "getCurrentSVLChannellist end mChannelsForCurrentSVLBase.size() " + this.mChannelsForCurrentSVLBase.size());
        MtkLog.d(TAG, "getCurrentSVLChannellist end mChannelsFor3RDSource.size() " + this.mChannelsFor3RDSource.size());
        this.mChannelsFor3RDSource.clear();
        this.mChannelsFor3RDSource.addAll(mChannelsFor3RDSourceTemp);
        this.mChannelsForCurrentSVL.clear();
        this.mChannelsForCurrentSVL.addAll(this.mChannelsForCurrentSVLBase);
        this.mChannelsForCurrentSVL.addAll(this.mChannelsFor3RDSource);
        MtkLog.d(TAG, "getCurrentSVLChannellist end mChannelsForCurrentSVL.size() " + this.mChannelsForCurrentSVL.size());
        if (this.CURRENT_CHANNEL_SORT == 3 || this.CURRENT_CHANNEL_SORT == 4) {
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    boolean unused = TIFChannelManager.this.afterGetAllChanelsGoonsort = true;
                    if (!TIFChannelManager.this.getAllChannelsrunning) {
                        TIFChannelManager.this.getAllChannels();
                    } else {
                        boolean unused2 = TIFChannelManager.this.upDateAllChannelsrunning = true;
                    }
                }
            });
        } else {
            updataChannelListSort();
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    if (!TIFChannelManager.this.getAllChannelsrunning) {
                        TIFChannelManager.this.getAllChannels();
                    } else {
                        boolean unused = TIFChannelManager.this.upDateAllChannelsrunning = true;
                    }
                }
            });
        }
        if (CommonIntegration.getInstance().isCurrentSourceTv() && getChannelInfoByUri() == null && SaveValue.getInstance(this.mContext).readValue(CommonIntegration.channelListfortypeMask, 0) == -1) {
            MtkLog.d(TAG, "getCurrentSVLChannellist end current chanenl is network,and this chanenl removed from the tv.db");
            if (this.mChannelsFor3RDSource.size() > 0) {
                MtkLog.d(TAG, "selectchannel  netowrk for first channel ");
                selectChannelByTIFInfo(this.mChannelsFor3RDSource.get(0));
            } else if (this.mChannelsForCurrentSVLBase.size() > 0) {
                MtkLog.d(TAG, "selectchannel  broadcast for first channel ");
                selectChannelByTIFInfo(this.mChannelsForCurrentSVLBase.get(0));
            }
        }
        if (this.channelListUpdateState) {
            handleUpdateChannels();
        }
    }

    public List<TIFChannelInfo> getCurrentSVLChannelList() {
        return Collections.unmodifiableList(this.mChannelsForCurrentSVL);
    }

    public List<TIFChannelInfo> get3RDChannelList() {
        Collections.sort(this.mChannelsFor3RDSource, new TIFChannelInfo.CustomerComparator(this.mContext, 0));
        return Collections.unmodifiableList(this.mChannelsFor3RDSource);
    }

    public List<TIFChannelInfo> getCurrentSVLChannelListBase() {
        Collections.sort(this.mChannelsForCurrentSVLBase, new TIFChannelInfo.CustomerComparator(this.mContext, 0));
        return this.mChannelsForCurrentSVLBase;
    }

    public void setCurrentChannelSort(int sort) {
        MtkLog.d(TAG, "setCurrentChannelSort " + sort);
        this.CURRENT_CHANNEL_SORT = sort;
        this.customerComparator.setSortType(sort);
        updataChannelListSort();
    }

    public void updataChannelListSort() {
        MtkLog.d(TAG, "updataChannelListSort ");
        Collections.sort(this.mChannelsForCurrentSVL, this.customerComparator);
        Collections.sort(this.mChannelsForCurrentSVLBase, new TIFChannelInfo.CustomerComparator(this.mContext, 0));
    }

    public void afterGetAllChanelsGoonsort() {
        this.afterGetAllChanelsGoonsort = false;
        for (TIFChannelInfo tempinfo : this.mChannelsForCurrentSVL) {
            tempinfo.mMtkTvChannelInfo = getAPIChannelInfoByChannelId(tempinfo.mInternalProviderFlag3);
        }
        updataChannelListSort();
    }

    public void mChannelListSort(List<TIFChannelInfo> mChlist) {
        MtkLog.d(TAG, "mChannelListSort");
        Collections.sort(mChlist, this.customerComparator);
    }

    public void findChanelsForlist(String findStr, int mCurMask, int mCurVal) {
        MtkLog.d(TAG, "findChanelsForlist findStr = " + findStr);
        if (findStr == SELECTION) {
            this.mFindResultForChannels.clear();
            return;
        }
        this.mFindResultForChannels.clear();
        MtkLog.d(TAG, "findChanelsForlist mChannelsForCurrentSVLBase.size() " + this.mChannelsForCurrentSVLBase.size());
        this.mFindResultForChannels.addAll(findChanelsForlist(queryChanelListWithMaskCount(10000, mCurMask, mCurVal, false), findStr));
        MtkLog.d(TAG, "findChanelsForlist mFindResultForChannels.size() " + this.mFindResultForChannels.size());
    }

    public List<TIFChannelInfo> getChannelListForFindOrNomal() {
        MtkLog.d(TAG, "getChannelListForFindOrNomal ");
        if (this.mFindResultForChannels == null || this.mFindResultForChannels.size() == 0) {
            CommonIntegration commonIntegration = this.mCI;
            int svlId = CommonIntegration.getInstance().getSvl();
            MtkLog.d(TAG, "getChannelListForFindOrNomal svlId = " + svlId);
            List<TIFChannelInfo> tempChannels = new ArrayList<>();
            tempChannels.addAll(this.mChannelsForCurrentSVLBase);
            if (this.mChannelsForCurrentSVLBase == null || tempChannels.size() <= 0) {
                this.mChannelsForCurrentSVLBase = new CopyOnWriteArrayList();
                getCurrentSVLChannellist();
            } else if (this.mCI.isCurrentSourceDTVforEuPA() && tempChannels.get(0).mInternalProviderFlag1 != svlId) {
                getCurrentSVLChannellist();
            } else if (this.mCI.isCurrentSourceATVforEuPA() && tempChannels.get(0).mInternalProviderFlag1 != 2) {
                getCurrentSVLChannellist();
            } else if (tempChannels.get(0).mInternalProviderFlag1 != svlId) {
                getCurrentSVLChannellist();
            }
            Log.d(TAG, "getChannelListForFindOrNomal mChannelsForCurrentSVL.size() " + this.mChannelsForCurrentSVL.size());
            return this.mChannelsForCurrentSVL;
        }
        MtkLog.d(TAG, "getChannelListForFindOrNomal mFindResultForChannels.size() " + this.mFindResultForChannels.size());
        return this.mFindResultForChannels;
    }

    private List<TIFChannelInfo> findChanelsForlist(List<TIFChannelInfo> findList, String findStr) {
        List<TIFChannelInfo> tempResultForChannels = new ArrayList<>();
        new ArrayList();
        new ArrayList();
        MtkLog.d(TAG, "findChanelsForlist findStr is " + findStr);
        for (TIFChannelInfo chinfo : findList) {
            if (chinfo.mDisplayNumber.contains(findStr) || chinfo.mDisplayName.toLowerCase().contains(findStr.toLowerCase())) {
                tempResultForChannels.add(chinfo);
            }
        }
        Collections.sort(tempResultForChannels, new TIFChannelInfo.CustomerComparator(this.mContext, 0));
        MtkLog.d(TAG, "findChanelsForlist tempResultForChannels.size " + tempResultForChannels.size());
        return tempResultForChannels;
    }

    public TIFChannelInfo getChannelByNumOrName(String chNumOrName, boolean isName) {
        MtkLog.d(TAG, "getChanelByNumOrName() ");
        for (TIFChannelInfo chinfo : this.mChannelsForCurrentSVLBase) {
            if (isName) {
                if (chinfo.mDisplayName.equalsIgnoreCase(chNumOrName)) {
                    chinfo.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(chinfo.mDataValue);
                    MtkLog.d(TAG, "getChanelByNumOrName() isName chinfo is " + chinfo);
                    return chinfo;
                }
            } else if (chinfo.mDisplayNumber.equals(chNumOrName)) {
                chinfo.mMtkTvChannelInfo = getAPIChannelInfoByBlobData(chinfo.mDataValue);
                MtkLog.d(TAG, "getChanelByNumOrName() not isName chinfo is " + chinfo);
                return chinfo;
            }
        }
        MtkLog.d(TAG, "getChanelByNumOrName() not isName chinfo is null");
        return null;
    }

    public int getATVTIFChannelsForSourceSetup() {
        MtkLog.d(TAG, "start getATVTIFChannelsForSourceSetup~");
        int count = MtkTvChannelList.getInstance().getChannelCountByMask(this.mCI.getSvl(), MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE | MtkTvChCommonBase.SB_VNET_FAKE, MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE);
        MtkLog.d(TAG, "end getATVTIFChannelsForSourceSetup  count>>>" + count);
        return count;
    }

    public int getDTVTIFChannelsForSourceSetup() {
        MtkLog.d(TAG, "start getDTVTIFChannelsForSourceSetup~");
        int count = MtkTvChannelList.getInstance().getChannelCountByMask(this.mCI.getSvl(), MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_ANALOG_SERVICE, MtkTvChCommonBase.SB_VNET_ACTIVE);
        MtkLog.d(TAG, "end getDTVTIFChannelsForSourceSetup  count>>>" + count);
        return count;
    }

    public void addBlockCheck(TvSurfaceView.BlockChecker checker) {
        this.mChecker = checker;
    }
}
