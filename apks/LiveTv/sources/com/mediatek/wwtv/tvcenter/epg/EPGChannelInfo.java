package com.mediatek.wwtv.tvcenter.epg;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.model.MtkTvATSCChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBChannelInfo;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import java.util.List;

public class EPGChannelInfo {
    private static final String TAG = "EPGChannelInfo";
    private String mChannelName;
    private int mChannelNum;
    private String mDisplayNumber;
    public long mId;
    private boolean mIsCiVirturalCh = false;
    private Drawable mIsdbIcon;
    private int mSubChannelNum = 0;
    private MtkTvChannelInfoBase mTVChannel;
    private List<EPGProgramInfo> mTVProgramInfoList;

    public MtkTvChannelInfoBase getTVChannel() {
        return this.mTVChannel;
    }

    public void setTVChannel(MtkTvChannelInfo mTVChannel2) {
        this.mTVChannel = mTVChannel2;
    }

    public EPGChannelInfo(Context context) {
    }

    public EPGChannelInfo(MtkTvChannelInfoBase mCurrentChannel) {
        MtkLog.e("MtkTvChannelInfoBase", "----EPGChannelInfo:" + mCurrentChannel);
        if (mCurrentChannel != null) {
            if (mCurrentChannel instanceof MtkTvATSCChannelInfo) {
                MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) mCurrentChannel;
                MtkLog.e("saepg", "getChannelNumCur1===>" + (tmpAtsc.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpAtsc.getMinorNum()));
                this.mChannelNum = tmpAtsc.getMajorNum();
                this.mSubChannelNum = tmpAtsc.getMinorNum();
            } else if (mCurrentChannel instanceof MtkTvISDBChannelInfo) {
                MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) mCurrentChannel;
                MtkLog.e("sapg", "getChannelNumCur2===>" + (tmpIsdb.getMajorNum() + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + tmpIsdb.getMinorNum()));
                this.mChannelNum = tmpIsdb.getMajorNum();
                this.mSubChannelNum = tmpIsdb.getMinorNum();
            } else {
                this.mChannelNum = mCurrentChannel.getChannelNumber();
                MtkLog.e("sapg", "getChannelNumCur3===>" + (" " + mCurrentChannel.getChannelNumber()));
            }
            this.mChannelName = mCurrentChannel.getServiceName();
            if (this.mChannelName == null) {
                this.mChannelName = "";
            }
            this.mTVChannel = mCurrentChannel;
        }
    }

    public EPGChannelInfo(TIFChannelInfo tempChannelInfo) {
        boolean z = false;
        if (tempChannelInfo != null) {
            if (tempChannelInfo.mMtkTvChannelInfo == null) {
                this.mChannelNum = tempChannelInfo.mServiceId;
            } else if (tempChannelInfo.mMtkTvChannelInfo != null && (tempChannelInfo.mMtkTvChannelInfo instanceof MtkTvATSCChannelInfo)) {
                MtkTvATSCChannelInfo tmpAtsc = (MtkTvATSCChannelInfo) tempChannelInfo.mMtkTvChannelInfo;
                this.mChannelNum = tmpAtsc.getMajorNum();
                this.mSubChannelNum = tmpAtsc.getMinorNum();
            } else if (tempChannelInfo.mMtkTvChannelInfo == null || !(tempChannelInfo.mMtkTvChannelInfo instanceof MtkTvISDBChannelInfo)) {
                this.mChannelNum = tempChannelInfo.mMtkTvChannelInfo.getChannelNumber();
            } else {
                MtkTvISDBChannelInfo tmpIsdb = (MtkTvISDBChannelInfo) tempChannelInfo.mMtkTvChannelInfo;
                this.mChannelNum = tmpIsdb.getMajorNum();
                this.mSubChannelNum = tmpIsdb.getMinorNum();
                this.mIsdbIcon = Drawable.createFromPath(CommonIntegration.getInstance().getISDBChannelLogo(tmpIsdb));
            }
            this.mId = tempChannelInfo.mId;
            this.mDisplayNumber = tempChannelInfo.mDisplayNumber;
            this.mChannelName = tempChannelInfo.mDisplayName;
            this.mTVChannel = tempChannelInfo.mMtkTvChannelInfo;
            long[] datas = tempChannelInfo.mDataValue;
            if (datas == null || datas.length <= 5) {
                this.mIsCiVirturalCh = false;
            } else {
                this.mIsCiVirturalCh = datas[5] == 17 ? true : z;
            }
        }
    }

    public EPGChannelInfo(String name, short mChannelNum2, boolean mType) {
        this.mChannelName = name;
        this.mChannelNum = mChannelNum2;
    }

    public int getPlayingTVProgramPositon() {
        if (this.mTVProgramInfoList == null) {
            return 0;
        }
        Long time = Long.valueOf(EPGUtil.getCurrentTime());
        int i = 0;
        while (i < this.mTVProgramInfoList.size() && (time.longValue() < this.mTVProgramInfoList.get(i).getmStartTime().longValue() || time.longValue() > this.mTVProgramInfoList.get(i).getmEndTime().longValue())) {
            i++;
        }
        if (i >= this.mTVProgramInfoList.size()) {
            return 0;
        }
        return i;
    }

    public int getNextPosition(EPGProgramInfo mTVProgramInfo) {
        EPGProgramInfo local = mTVProgramInfo;
        if (local == null || local.isDrawLeftIcon() || this.mTVProgramInfoList == null || this.mTVProgramInfoList.size() == 0) {
            return 0;
        }
        Long time = local.getmStartTime();
        int i = this.mTVProgramInfoList.size() - 1;
        MtkLog.d(TAG, "Current program start time: " + time);
        EPGProgramInfo child = this.mTVProgramInfoList.get(i);
        while (child != null && child.getmStartTime().longValue() > time.longValue()) {
            if (i <= 0) {
                return 0;
            }
            i--;
            child = this.mTVProgramInfoList.get(i);
            MtkLog.d(TAG, "Next program start time: " + child.getmStartTime());
        }
        return i;
    }

    public boolean isCiVirturalCh() {
        return this.mIsCiVirturalCh;
    }

    public Drawable getIsdbIcon() {
        return this.mIsdbIcon;
    }

    public String getName() {
        return this.mChannelName;
    }

    public void setName(String name) {
        this.mChannelName = name;
    }

    public String getDisplayNumber() {
        return this.mDisplayNumber;
    }

    public int getmChanelNum() {
        return this.mChannelNum;
    }

    public String getmChanelNumString() {
        if (this.mChannelNum < 10) {
            return "0" + this.mChannelNum;
        }
        return this.mChannelNum + "";
    }

    public String getmSubNum() {
        if (this.mSubChannelNum == 0) {
            return "";
        }
        if (this.mSubChannelNum < 10) {
            return "0" + this.mSubChannelNum;
        }
        return this.mSubChannelNum + "";
    }

    public void setmChanelNum(short mChanelNum) {
        this.mChannelNum = mChanelNum;
    }

    public List<EPGProgramInfo> getmTVProgramInfoList() {
        return this.mTVProgramInfoList;
    }

    public void setmTVProgramInfoList(List<EPGProgramInfo> mTVProgramInfoList2) {
        this.mTVProgramInfoList = mTVProgramInfoList2;
    }

    public List<EPGProgramInfo> getmGroup() {
        return this.mTVProgramInfoList;
    }
}
