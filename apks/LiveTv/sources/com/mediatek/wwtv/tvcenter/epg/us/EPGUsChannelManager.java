package com.mediatek.wwtv.tvcenter.epg.us;

import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvEventATSC;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfo;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import java.util.ArrayList;
import java.util.List;

public class EPGUsChannelManager {
    private static String TAG = "EPGUsChannelManager";
    private static EPGUsChannelManager epgUsChannelManager;
    private boolean blocked;
    private MtkTvChannelInfoBase curChannelInfo;
    private MtkTvEventInfo curEventInfo;
    private final CommonIntegration integration;
    private List<MtkTvChannelInfoBase> mChannelList;
    private String mChannelName;
    private int mChannelNum;
    private String mChannelTime;
    private String mChannelprogram;
    private String mChannnelDay;
    private final Context mContext;
    private TIFChannelInfo mCurrentChannelTifInfo;
    private TIFChannelInfo mNextChannelTifInfo;
    private TIFChannelInfo mPreChannelTifInfo;
    private final TIFChannelManager mTIFChannelManager = TIFChannelManager.getInstance(this.mContext);
    private MtkTvChannelInfoBase nextChannel;
    private MtkTvChannelInfoBase preChannelInfo;

    public static synchronized EPGUsChannelManager getInstance(Context context) {
        EPGUsChannelManager ePGUsChannelManager;
        synchronized (EPGUsChannelManager.class) {
            if (epgUsChannelManager == null) {
                epgUsChannelManager = new EPGUsChannelManager(context);
            }
            ePGUsChannelManager = epgUsChannelManager;
        }
        return ePGUsChannelManager;
    }

    public EPGUsChannelManager(Context context) {
        this.mContext = context;
        this.integration = CommonIntegration.getInstanceWithContext(context.getApplicationContext());
    }

    public boolean preChannel() {
        if (CommonIntegration.supportTIFFunction()) {
            Log.d(TAG, "preChannel()...");
            if (this.mCurrentChannelTifInfo == null || this.mPreChannelTifInfo == null || this.mCurrentChannelTifInfo.mId == this.mPreChannelTifInfo.mId) {
                return false;
            }
            Log.d(TAG, "preChannel()go...");
            this.mTIFChannelManager.selectChannelByTIFInfo(this.mPreChannelTifInfo);
            return true;
        }
        this.integration.selectChannelByInfo(this.preChannelInfo);
        initChannelData();
        return true;
    }

    public boolean nextChannel() {
        if (CommonIntegration.supportTIFFunction()) {
            Log.d(TAG, "nextChannel()...");
            if (this.mCurrentChannelTifInfo == null || this.mNextChannelTifInfo == null || this.mCurrentChannelTifInfo.mId == this.mNextChannelTifInfo.mId) {
                return false;
            }
            Log.d(TAG, "nextChannel()go...");
            this.mTIFChannelManager.selectChannelByTIFInfo(this.mNextChannelTifInfo);
            return true;
        }
        this.integration.selectChannelByInfo(this.nextChannel);
        initChannelData();
        return true;
    }

    public boolean isNextChannelDig() {
        if (!CommonIntegration.supportTIFFunction() || this.mNextChannelTifInfo == null || this.mNextChannelTifInfo.mMtkTvChannelInfo == null || this.mNextChannelTifInfo.mMtkTvChannelInfo.getBrdcstType() == 1) {
            return false;
        }
        return true;
    }

    public boolean isPreChannelDig() {
        if (!CommonIntegration.supportTIFFunction() || this.mPreChannelTifInfo == null || this.mPreChannelTifInfo.mMtkTvChannelInfo == null || this.mPreChannelTifInfo.mMtkTvChannelInfo.getBrdcstType() == 1) {
            return false;
        }
        return true;
    }

    public boolean isCurrentChannelDig() {
        if (!CommonIntegration.supportTIFFunction() || this.mCurrentChannelTifInfo == null || this.mCurrentChannelTifInfo.mMtkTvChannelInfo == null || this.mCurrentChannelTifInfo.mMtkTvChannelInfo.getBrdcstType() == 1) {
            return false;
        }
        return true;
    }

    public void initChannelData() {
        long j;
        if (CommonIntegration.supportTIFFunction()) {
            if (!this.integration.is3rdTVSource() || this.integration.isDisableColorKey()) {
                this.mCurrentChannelTifInfo = this.mTIFChannelManager.getTIFChannelInfoById(this.integration.getCurrentChannelId());
                this.mPreChannelTifInfo = getTifChannelPrevious();
                this.mNextChannelTifInfo = getTifChannelNext();
            } else {
                long currntChId = TurnkeyUiMainActivity.getInstance().getTvView().getCurrentChannelId();
                TIFChannelManager tIFChannelManager = this.mTIFChannelManager;
                if (currntChId == -1) {
                    j = 0;
                } else {
                    j = currntChId;
                }
                this.mCurrentChannelTifInfo = tIFChannelManager.getTIFChannelInfoPLusByProviderId(j);
                this.mPreChannelTifInfo = this.mTIFChannelManager.getTIFUpOrDownChannelfor3rdsource(true);
                this.mNextChannelTifInfo = this.mTIFChannelManager.getTIFUpOrDownChannelfor3rdsource(false);
            }
            String str = TAG;
            MtkLog.d(str, "mCurrentChannelTifInfo>>>" + this.mCurrentChannelTifInfo + " >>" + this.mPreChannelTifInfo + " >>" + this.mNextChannelTifInfo);
            return;
        }
        this.curChannelInfo = this.integration.getCurChInfo();
        this.preChannelInfo = getChannelPrevious();
        this.nextChannel = getChannelNext();
        if (this.preChannelInfo != null && this.nextChannel != null && this.curChannelInfo != null) {
            String str2 = TAG;
            MtkLog.e(str2, "curChannelInfo>>>" + this.preChannelInfo.getChannelNumber() + "  " + this.preChannelInfo.getChannelId() + "  " + this.preChannelInfo.getServiceName() + "   " + this.curChannelInfo.getChannelNumber() + "  " + this.curChannelInfo.getChannelId() + "  " + this.curChannelInfo.getServiceName() + "   " + this.nextChannel.getChannelNumber() + "  " + this.nextChannel.getChannelId() + "  " + this.nextChannel.getServiceName());
        }
    }

    public int getCurrentChId() {
        return this.integration.getCurrentChannelId();
    }

    public MtkTvChannelInfoBase getChannelCurrent() {
        this.curChannelInfo = this.integration.getCurChInfo();
        return this.curChannelInfo;
    }

    public String getCurrentChannelNum() {
        String number = "";
        if (this.mCurrentChannelTifInfo != null) {
            number = this.mCurrentChannelTifInfo.mDisplayNumber;
        }
        String str = TAG;
        MtkLog.d(str, "getCurrentChannelNum--->number=" + number);
        return number;
    }

    public String getCurrentChannelName() {
        String name = "";
        if (this.mCurrentChannelTifInfo != null) {
            name = this.mCurrentChannelTifInfo.mDisplayName;
        }
        String str = TAG;
        MtkLog.d(str, "getCurrentChannelName--->name=" + name);
        return name;
    }

    public String getPreChannelNum() {
        String number = "";
        if (this.mPreChannelTifInfo != null) {
            number = this.mPreChannelTifInfo.mDisplayNumber;
        }
        String str = TAG;
        MtkLog.d(str, "getPreChannelNum:" + number);
        return number;
    }

    public String getNextChannelNum() {
        String number = "";
        if (this.mNextChannelTifInfo != null) {
            number = this.mNextChannelTifInfo.mDisplayNumber;
        }
        String str = TAG;
        MtkLog.d(str, "getNextChannelNum:" + number);
        return number;
    }

    public TIFChannelInfo getTifChannelPrevious() {
        TIFChannelInfo preChannel = this.mTIFChannelManager.getTIFUpOrDownChannelForUSEPG(true, CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
        if (preChannel == null) {
            TIFChannelInfo tIFChannelInfo = this.mCurrentChannelTifInfo;
        }
        return preChannel;
    }

    public MtkTvChannelInfoBase getChannelPrevious() {
        MtkTvChannelInfoBase preChannel = this.integration.getUpDownChInfoByFilter(true, this.integration.getChUpDownFilterEPG());
        if (preChannel == null) {
            MtkTvChannelInfoBase mtkTvChannelInfoBase = this.curChannelInfo;
        }
        return preChannel;
    }

    public TIFChannelInfo getTifChannelNext() {
        TIFChannelInfo nextChannel2 = this.mTIFChannelManager.getTIFUpOrDownChannelForUSEPG(false, CommonIntegration.CH_LIST_MASK, CommonIntegration.CH_LIST_VAL);
        if (nextChannel2 == null) {
            TIFChannelInfo tIFChannelInfo = this.mCurrentChannelTifInfo;
        }
        return nextChannel2;
    }

    public MtkTvChannelInfoBase getChannelNext() {
        MtkTvChannelInfoBase nextChannel2 = this.integration.getUpDownChInfoByFilter(false, this.integration.getChUpDownFilterEPG());
        if (nextChannel2 == null) {
            MtkTvChannelInfoBase mtkTvChannelInfoBase = this.curChannelInfo;
        }
        return nextChannel2;
    }

    public List<MtkTvChannelInfoBase> getChannelList() {
        return this.mChannelList;
    }

    private int getChannelPosition(MtkTvChannelInfoBase channelInfo) {
        MtkLog.e("mChannelList", "mChannelList:p:" + 0);
        if (this.mChannelList != null) {
            return this.mChannelList.indexOf(channelInfo);
        }
        return 0;
    }

    public void loadProgramEventDefault() {
        MtkTvEventATSC.getInstance().loadEvents(getChannelCurrent().getChannelId(), 0, 10);
        this.curEventInfo = (MtkTvEventInfo) MtkTvEventATSC.getInstance().getEvent(0);
    }

    public int[] loadProgramEvent(int channelId, long startTime, int count) {
        MtkLog.d("ChannelManager", "--- set time : [" + startTime + "," + count + "]");
        int[] value = MtkTvEventATSC.getInstance().loadEvents(channelId, startTime, count);
        StringBuilder sb = new StringBuilder();
        sb.append("returnvalue:");
        sb.append(value);
        MtkLog.d("ChannelManager", sb.toString());
        return value;
    }

    public List<MtkTvEventInfoBase> getProgramList(int value) {
        List<MtkTvEventInfoBase> mTVProgramInfoList = new ArrayList<>();
        mTVProgramInfoList.add(getProgramInfo(value));
        return mTVProgramInfoList;
    }

    public MtkTvEventInfoBase getProgramInfo(int requestId) {
        return MtkTvEventATSC.getInstance().getEvent(requestId);
    }

    public int getmChannelNum() {
        return this.mChannelNum;
    }

    public String getmChannelName() {
        return this.mChannelName;
    }

    public String getmChannnelDay() {
        return this.mChannnelDay;
    }

    public String getmChannelTime() {
        return this.mChannelTime;
    }

    public String getmChannelprogram() {
        return this.mChannelprogram;
    }

    public boolean isBlocked() {
        return this.blocked;
    }
}
