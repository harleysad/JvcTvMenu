package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import java.util.ArrayList;
import java.util.List;

public class MtkTvEventBase {
    private static final String TAG = "MtkTvEvent ";
    private int mMaxEventNumPerChannel = 32;

    public MtkTvEventBase() {
        Log.d(TAG, "Enter MtkTvEvent Here.");
        this.mMaxEventNumPerChannel = 32;
    }

    public List<MtkTvEventInfoBase> getEventListByChannelId(int channelId, long startTime, long duration) throws MtkTvExceptionBase {
        ArrayList arrayList;
        int i = channelId;
        Log.d(TAG, "Enter getEventListByChannelId Here. channelID =" + i);
        if (startTime <= 0 || duration <= 0) {
            Log.d(TAG, "getEventListByChannelId  the query time is not correct");
            return null;
        }
        try {
            synchronized (this) {
                arrayList = new ArrayList();
                int ret = TVNativeWrapper.getEventListByChannelId_native(i, startTime, duration, this.mMaxEventNumPerChannel, arrayList);
                Log.d(TAG, "TVNativeWrapper : getEventList_native  ret = " + ret + "  by ChannelID " + i);
                if (ret != 0) {
                    throw new MtkTvExceptionBase(ret, "TVNativeWrapper getEventList_native fail");
                }
            }
            return arrayList;
        } catch (MtkTvExceptionBase e) {
            e.printStackTrace();
            return null;
        }
    }

    public MtkTvEventInfoBase getEventInfoByEventId(int eventId, int channelId) {
        Log.d(TAG, "Enter getEventInfoByEventId Here.");
        if (eventId < 0) {
            return null;
        }
        MtkTvEventInfoBase eventInfo = new MtkTvEventInfoBase();
        TVNativeWrapper.getEventInfoByEventId_native(eventId, channelId, eventInfo);
        return eventInfo;
    }

    public int setCurrentActiveWindows(List<MtkTvChannelInfoBase> channels, long startTime) {
        Log.d(TAG, "Enter setCurrentActiveWindows Here.");
        if (channels == null || startTime <= 0) {
            return -1;
        }
        TVNativeWrapper.setCurrentActiveWindowsInfo_native(channels, startTime);
        return 0;
    }

    public int setCurrentActiveWindows(List<MtkTvChannelInfoBase> channels, long startTime, long durationTime) {
        Log.d(TAG, "Enter setCurrentActiveWindowsEx Here.");
        if (channels == null || startTime <= 0 || durationTime <= 0) {
            return -1;
        }
        TVNativeWrapper.setCurrentActiveWindowsInfoEx_native(channels, startTime, durationTime);
        return 0;
    }

    public int clearActiveWindows() {
        Log.d(TAG, "Enter clearActiveWindows Here.");
        return TVNativeWrapper.clearActiveWindow_native();
    }

    public int setMaxEventNum(int num) {
        Log.d(TAG, "Enter setMaxEventNum Here. num = " + num);
        if (num < 0 || num > 1023) {
            return -1;
        }
        this.mMaxEventNumPerChannel = num;
        return 0;
    }

    public boolean checkEventBlock(int channelId, int eventId) {
        Log.d(TAG, "Enter getEventListByChannelId Here. channelID =" + channelId + "eventId =" + eventId);
        return TVNativeWrapper.checkEventBlock_native(channelId, eventId);
    }

    public int[] getCurrentActiveWinChannelList() {
        Log.d(TAG, "get active window channel list");
        return TVNativeWrapper.getCurrentActiveWinChannelList_native();
    }

    public long getCurrentActiveWinStartTime() {
        Log.d(TAG, "get active window start time");
        return TVNativeWrapper.getCurrentActiveWinStartTime_native();
    }

    public int getEventRatingMapById(int channelId, int eventId, MtkTvRatingConvert2Goo ratingMapped) {
        return TVNativeWrapper.getEventRatingMapById_native(channelId, eventId, ratingMapped);
    }

    public long getCurrentActiveWinEndTime() {
        Log.d(TAG, "get active window end time");
        return TVNativeWrapper.getCurrentActiveWinEndTime_native();
    }

    public MtkTvEventInfoBase getPFEventInfoByChannel(int channelId, boolean isPEvent) {
        Log.d(TAG, "Enter getEventInfoByEventId Here.");
        MtkTvEventInfoBase eventInfo = new MtkTvEventInfoBase();
        if (TVNativeWrapper.getPFEventInfoByChannel_native(channelId, isPEvent, eventInfo) == 0) {
            return eventInfo;
        }
        return null;
    }
}
