package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvPvrBrowserItemBase;

public class MtkTvPvrBrowserBase {
    private static final String TAG = "MtkTvPvrBrowser ";

    public MtkTvPvrBrowserBase() {
        Log.d(TAG, "Enter MtkTvPvrBrowser struct Here.");
    }

    public MtkTvPvrBrowserItemBase getPvrBrowserItemByPath(String path) {
        Log.d(TAG, "Enter getPvrBrowserItemByPath Here.");
        MtkTvPvrBrowserItemBase item = new MtkTvPvrBrowserItemBase();
        item.mPath = path;
        item.mChannelId = TVNativeWrapper.getPvrBrowserItemChannelId_native(path);
        item.mChannelName = TVNativeWrapper.getPvrBrowserItemChannelName_native(path);
        item.mDuration = TVNativeWrapper.getPvrBrowserItemDuration_native(path);
        item.mProgramName = TVNativeWrapper.getPvrBrowserItemProgramName_native(path);
        item.mStartTime = TVNativeWrapper.getPvrBrowserItemStartTime_native(path);
        item.mEndTime = item.mStartTime + item.mDuration;
        item.mMajorChannelNum = TVNativeWrapper.getPvrBrowserItemMajorChannelNum_native(path);
        item.mMinorChannelNum = TVNativeWrapper.getPvrBrowserItemMinorChannelNum_native(path);
        item.mData = TVNativeWrapper.getPvrBrowserItemDate_native(path);
        item.mWeek = TVNativeWrapper.getPvrBrowserItemWeek_native(path);
        item.mTime = TVNativeWrapper.getPvrBrowserItemTime_native(path);
        item.mRetention = TVNativeWrapper.getPvrBrowserItemRentention_native(path);
        item.mFirstRatingRange = TVNativeWrapper.getPvrBrowserItemFirstRatingRange_native(path);
        return item;
    }

    public int getPvrBrowserItemCount() {
        return TVNativeWrapper.getPvrBrowserItemCount_native();
    }

    public MtkTvPvrBrowserItemBase getPvrBrowserItemByIndex(int index) {
        Log.d(TAG, "Enter getPvrBrowserItemByIndex Here.");
        MtkTvPvrBrowserItemBase item = new MtkTvPvrBrowserItemBase();
        item.mPath = TVNativeWrapper.getPvrBrowserItemPath_native(index);
        item.mChannelId = TVNativeWrapper.getPvrBrowserItemChannelId_native(index);
        item.mMajorChannelNum = TVNativeWrapper.getPvrBrowserItemMajorChannelNum_native(index);
        item.mMinorChannelNum = TVNativeWrapper.getPvrBrowserItemMinorChannelNum_native(index);
        item.mChannelName = TVNativeWrapper.getPvrBrowserItemChannelName_native(index);
        item.mDuration = TVNativeWrapper.getPvrBrowserItemDuration_native(index);
        item.mProgramName = TVNativeWrapper.getPvrBrowserItemProgramName_native(index);
        item.mData = TVNativeWrapper.getPvrBrowserItemDate_native(index);
        item.mWeek = TVNativeWrapper.getPvrBrowserItemWeek_native(index);
        item.mTime = TVNativeWrapper.getPvrBrowserItemTime_native(index);
        item.mStartTime = TVNativeWrapper.getPvrBrowserItemStartTime_native(index);
        item.mEndTime = item.mStartTime + item.mDuration;
        item.mRetention = TVNativeWrapper.getPvrBrowserItemRentention_native(index);
        item.mFirstRatingRange = TVNativeWrapper.getPvrBrowserItemFirstRatingRange_native(index);
        return item;
    }

    public int getPvrBrowserRecordingFileCount() {
        return TVNativeWrapper.getPvrBrowserRecordingFileCount_native();
    }

    public String getPvrBrowserRecordingFileName(int index) {
        return TVNativeWrapper.getPvrBrowserRecordingFileName_native(index);
    }

    public int deletePvrBrowserFileByIndex(int index) {
        return TVNativeWrapper.deletePvrBrowserFileByIndex_native(index);
    }
}
