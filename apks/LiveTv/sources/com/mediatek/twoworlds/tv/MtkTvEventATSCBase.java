package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;

public class MtkTvEventATSCBase {
    public static final String TAG = "TV_MtkTvEventATSCBase";

    public int loadEvents(int channelId, long startTime, int count, int[] requests) {
        return -1;
    }

    public int[] loadEvents(int channelId, long startTime, int count) {
        Log.d(TAG, "loadEvents\n");
        return TVNativeWrapper.loadEvents_native(channelId, startTime, count);
    }

    public int loadEventByEIT(int channelId, int eitIndex) {
        Log.d(TAG, "loadEvents " + channelId + " " + eitIndex + " \n");
        if (eitIndex < 0 || eitIndex > 127) {
            Log.d(TAG, "loadEvents " + eitIndex + " wrong,only EIT0-EIT127 is support\n");
        }
        return TVNativeWrapper.loadEventByEIT_native(channelId, eitIndex);
    }

    public int getEventNumberInEIT(int requestId) {
        Log.d(TAG, "getEventNumberInEIT " + requestId + " \n");
        return TVNativeWrapper.getEventNumberInEIT_native(requestId);
    }

    public MtkTvEventInfoBase getEventByIndex(int requestId, int indexInEIT) {
        Log.d(TAG, "getEventByIndex\n");
        MtkTvEventInfoBase eventInfo = new MtkTvEventInfoBase();
        TVNativeWrapper.getEventByIndex_native(requestId, indexInEIT, eventInfo);
        Log.d(TAG, "get event string" + eventInfo.toString());
        return eventInfo;
    }

    public String getEventDetailByIndex(int requestId, int eventId) {
        String eventDetail = TVNativeWrapper.getEventDetailByIndex_native(requestId, eventId);
        Log.d(TAG, "event detail" + eventDetail);
        return eventDetail;
    }

    public int getEventRatingMapByIndex(int requestId, int indexInEIT, MtkTvRatingConvert2Goo ratingMapped) {
        return TVNativeWrapper.getEventRatingMapByIndex_native(requestId, indexInEIT, ratingMapped);
    }

    public int[] checkEITStauts(int requestId, int group_number) {
        return TVNativeWrapper.checkEITStauts_native(requestId, group_number);
    }

    public MtkTvEventInfoBase getEvent(int requestId) {
        Log.d(TAG, "getEvent\n");
        MtkTvEventInfoBase eventInfo = new MtkTvEventInfoBase();
        TVNativeWrapper.getEvent_native(requestId, eventInfo);
        Log.d(TAG, eventInfo.toString());
        return eventInfo;
    }

    public int freeEvent(int requestId) {
        Log.d(TAG, "freeEvent\n");
        return TVNativeWrapper.freeEvent_native(requestId);
    }

    public boolean checkEventBlock(int requestId) {
        Log.d(TAG, "checkEventBlock\n");
        return false;
    }

    public boolean checkEventBlock(int requestId, int channelId) {
        Log.d(TAG, "checkEventBlockby channel id\n");
        return TVNativeWrapper.checkAtscEventBlock_native(requestId, channelId);
    }
}
