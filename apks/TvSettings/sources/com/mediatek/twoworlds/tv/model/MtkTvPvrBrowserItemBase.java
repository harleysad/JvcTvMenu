package com.mediatek.twoworlds.tv.model;

import android.util.Log;

public class MtkTvPvrBrowserItemBase {
    public static final String TAG = "MtkTvPvrBrowserItemBase";
    public long mChannelId;
    public String mChannelName;
    public String mData;
    public long mDuration;
    public long mEndTime;
    public long mFirstRatingRange;
    public int mMajorChannelNum;
    public int mMinorChannelNum;
    public String mPath;
    public String mProgramName;
    public long mRetention;
    public long mStartTime;
    public String mTime;
    public String mWeek;

    public MtkTvPvrBrowserItemBase() {
        Log.d(TAG, "Enter MtkTvPvrBrowserItemBase struct Here.");
    }

    public String toString() {
        return "MtkTvPvrBrowserItemBase   [mPath=" + this.mPath + " , mChannelId=" + this.mChannelId + " , mDuration=" + this.mDuration + " , mChannelName=" + this.mChannelName + " , mStartTime=" + this.mStartTime + " , mEndTime=" + this.mEndTime + "]\n";
    }
}
