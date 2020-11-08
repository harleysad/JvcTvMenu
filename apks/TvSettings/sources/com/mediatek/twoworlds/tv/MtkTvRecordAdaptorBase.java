package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import java.util.ArrayList;
import java.util.List;

public class MtkTvRecordAdaptorBase {
    private static final String TAG = "MtkTvRecordAdaptorBase ";

    public MtkTvRecordAdaptorBase() {
        Log.d(TAG, "Enter MtkTvRecordAdaptorBase struct Here.");
    }

    public void updateBooking() {
        TVNativeWrapper.updateBooking_native();
    }

    public List<MtkTvBookingBase> getBookingList() {
        List<MtkTvBookingBase> recordList;
        Log.d(TAG, "Enter getRecordList Here.");
        synchronized (this) {
            recordList = new ArrayList<>();
            int count = TVNativeWrapper.getBookingCount_native();
            for (int j = 0; j < count; j++) {
                recordList.add(getBookingByIndex(j));
            }
            Log.d(TAG, "List  size = " + recordList.size());
            for (int i = 0; i < recordList.size(); i++) {
                Log.d(TAG, recordList.get(i).toString());
            }
        }
        return recordList;
    }

    /* access modifiers changed from: protected */
    public MtkTvBookingBase getBookingByIndex(int index) {
        MtkTvBookingBase item = new MtkTvBookingBase();
        item.setBookingId(index);
        item.setChannelId(TVNativeWrapper.getBookingChannelId(index));
        item.setDeviceIndex(TVNativeWrapper.getBookingDeviceIndex(index));
        item.setEventTitle(TVNativeWrapper.getBookingEventTitle(index));
        item.setGenre(TVNativeWrapper.getBookingGenre(index));
        item.setInfoData(TVNativeWrapper.getBookingInfoData(index));
        item.setRecordDelay(TVNativeWrapper.getBookingRecordDelay(index));
        item.setRecordDuration(TVNativeWrapper.getBookingRecordDuration(index));
        item.setRecordMode(TVNativeWrapper.getBookingRecordMode(index));
        item.setRecordStartTime(TVNativeWrapper.getBookingStartTime(index));
        item.setRepeatMode(TVNativeWrapper.getBookingRepeatMode(index));
        item.setSourceType(TVNativeWrapper.getBookingSourceType(index));
        item.setTunerType(TVNativeWrapper.getBookingTunerType(index));
        return item;
    }

    /* access modifiers changed from: protected */
    public void setBookingByIndex(int index, MtkTvBookingBase item) {
        TVNativeWrapper.setBookingChannelId(index, item.getChannelId());
        TVNativeWrapper.setBookingDeviceIndex(index, item.getDeviceIndex());
        TVNativeWrapper.setBookingEventTitle(index, item.getEventTitle());
        TVNativeWrapper.setBookingGenre(index, item.getGenre());
        TVNativeWrapper.setBookingInfoData(index, item.getInfoData());
        TVNativeWrapper.setBookingRecordDelay(index, item.getRecordDelay());
        TVNativeWrapper.setBookingRecordDuration(index, item.getRecordDuration());
        TVNativeWrapper.setBookingRecordMode(index, item.getRecordMode());
        TVNativeWrapper.setBookingStartTime(index, item.getRecordStartTime());
        TVNativeWrapper.setBookingRepeatMode(index, item.getRepeatMode());
        TVNativeWrapper.setBookingSourceType(index, item.getSourceType());
        TVNativeWrapper.setBookingTunerType(index, item.getTunerType());
    }

    public int addBooking(int channelId, int eventId) {
        Log.d(TAG, "Enter addRecord Here.");
        if (channelId < 0 || eventId < 0) {
            return -1;
        }
        return TVNativeWrapper.addBooking_native(channelId, eventId) ? 0 : -2;
    }

    public int addBooking(int channelId, long startTime, int duration, String firstEventTitle) {
        Log.d(TAG, "Enter addRecord Here.channelId:" + channelId + ",startTime:" + startTime + ",duration:" + duration + ", firstEventTitle:" + firstEventTitle);
        if (startTime < 0 || duration < 0 || channelId < 0) {
            return -1;
        }
        return TVNativeWrapper.addBooking_native(channelId, startTime, duration, firstEventTitle) ? 0 : -2;
    }

    public int addBooking(MtkTvBookingBase item) {
        setBookingByIndex(TVNativeWrapper.getBookingCount_native(), item);
        return 0;
    }

    public int replaceBooking(int index, MtkTvBookingBase item) {
        setBookingByIndex(index, item);
        return 0;
    }

    public int delBooking(int index) {
        return TVNativeWrapper.delBooking_native(index);
    }

    public String getRecordingFileName() {
        return TVNativeWrapper.getRecordingFile_native();
    }

    public String getRecordingFileNameByHandle(int handle) {
        return TVNativeWrapper.getRecordingFileByHandle_native(handle);
    }

    public boolean setRegisterInformation(String fileFullName) {
        return TVNativeWrapper.setRecordRegisterFile_native(fileFullName);
    }

    public boolean getRegisterInformation(String fileFullName) {
        return TVNativeWrapper.checkRecordRegisterFile_native(fileFullName);
    }
}
