package com.mediatek.twoworlds.tv;

import android.util.Log;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import java.util.List;

public class MtkTvRecordBase {
    public static final int NAV_REC_PVR_SRC_CHECK_RES_AV_ATV = 3;
    public static final int NAV_REC_PVR_SRC_CHECK_RES_AV_UNBLOCK = 5;
    public static final int NAV_REC_PVR_SRC_CHECK_RES_CI_NOTALLOW = 4;
    public static final int NAV_REC_PVR_SRC_CHECK_RES_CONFLICT = 2;
    public static final int NAV_REC_PVR_SRC_CHECK_RES_FAIL = 1;
    public static final int NAV_REC_PVR_SRC_CHECK_RES_OK = 0;
    private static final String TAG = "MtkTvRecordBase ";
    private MtkTvRecordAdaptorBase mRecordAdaptor = new MtkTvRecordAdaptorBase();

    public enum APP_CFG_RMDR_MODE_T {
        APP_CFG_RMDR_MODE_NONE,
        APP_CFG_RMDR_MODE_RMDR,
        APP_CFG_RMDR_MODE_RECD,
        APP_CFG_RMDR_MODE_CI
    }

    public enum RecordBaseErrorID {
        RECORD_PVR_ERR_ID_NONE,
        RECORD_PVR_ERR_ID_RECORDING,
        RECORD_PVR_ERR_ID_UNKNOWN_SRC,
        RECORD_PVR_ERR_ID_FEATURE_NOT_SUPPORTED,
        RECORD_PVR_ERR_ID_INSUFFICIENT_RESOURCE,
        RECORD_PVR_ERR_ID_AV_STREAM_NOT_AVAILABLE,
        RECORD_PVR_ERR_ID_STREAM_NOT_AUTHORIZED,
        RECORD_PVR_ERR_ID_INPUT_LOCKED,
        RECORD_PVR_ERR_ID_DISK_NOT_READY,
        RECORD_PVR_ERR_ID_DISK_TOO_SMALL,
        RECORD_PVR_ERR_ID_DISK_FULL,
        RECORD_PVR_ERR_ID_VIDEO_RESOLUTION_ERROR,
        RECORD_PVR_ERR_ID_INTERNAL_ERROR,
        RECORD_PVR_ERR_ID_ATV_IS_OFF,
        RECORD_PVR_ERR_ID_PVR_DIABLE_BY_PIP_POP,
        RECORD_PVR_ERR_ID_PVR_DIABLE_BY_WRONG_VIDEO_INFO,
        RECORD_PVR_ERR_ID_PVR_DIABLE_BY_NO_AUDIO,
        RECORD_PVR_ERR_ID_PVR_DIABLE_BY_SCRAMBLE_AV,
        RECORD_PVR_ERR_ID_PVR_MODE_OFF,
        RECORD_PVR_ERR_ID_START_FAILED_EAS,
        RECORD_PVR_ERR_ID_START_FAILED_UPDATER,
        RECORD_PVR_ERR_ID_START_FAILED_OAD,
        RECORD_PVR_ERR_ID_START_FAILED_TV_NET_SVC,
        RECORD_PVR_ERR_ID_START_FAILED_MMP,
        RECORD_PVR_ERR_ID_CAM_PIN_CODE_INVALID,
        RECORD_PVR_ERR_ID_REACH_UPPER_LIMIT,
        RECORD_PVR_ERR_ID_PVR_DISABLE_BY_ECI_PROTECT,
        RECORD_PVR_ERR_ID_LAST
    }

    public enum RecordDeviceStatus {
        NAV_REC_DEV_STATUS_NO_DEVICE,
        NAV_REC_DEV_STATUS_INSETTED,
        NAV_REC_DEV_STATUS_REMOVED,
        NAV_REC_DEV_STATUS_FORMATED,
        NAV_REC_DEV_STATUS_SELECTED,
        NAV_REC_DEV_STATUS_DEVICE_AVAIL,
        NAV_REC_DEV_STATUS_CHG_ANOTHER_AVAIL,
        NAV_REC_DEV_STATUS_READY,
        NAV_REC_DEV_STATUS_DEVICE_FULL,
        NAV_REC_DEV_STATUS_DEVICE_TOO_SMALL,
        NAV_REC_DEV_STATUS_DEVICE_READ_ONLY
    }

    public enum RecordNotifyCode {
        REC_PVR_NTFY_STATUS_CHANGE,
        REC_PVR_NTFY_STRG_STATUS_CHANGE,
        REC_PVR_NTFY_DEV_TEST_PROGRESS,
        REC_PVR_NTFY_DEV_TEST_COMPLETE,
        REC_PVR_NTFY_DEV_TEST_CANCELED,
        REC_PVR_NTFY_DEV_TEST_FAILED,
        REC_PVR_NTFY_LAST
    }

    public enum RecordNotifyMsgType {
        RECORD_PVR_NTFY_VIEW_HANDLE_KEY_EVENT,
        RECORD_PVR_NTFY_CONTROLLER_SET_MAX_DURAIOTN,
        RECORD_PVR_NTFY_VIEW_STATUS_RECORDING,
        RECORD_PVR_NTFY_VIEW_STATUS_STOPPED,
        RECORD_PVR_NTFY_VIEW_STATUS_SAVING,
        RECORD_PVR_NTFY_VIEW_STRG_STATUS_CHANGE,
        RECORD_PVR_NTFY_VIEW_DATA_SOURCE_UPDATE_CHANNEL_INFO,
        RECORD_PVR_NTFY_VIEW_DATA_SOURCE_UPDATE_RECORDING_STATUS,
        RECORD_PVR_NTFY_VIEW_INSERT_PVR_FILE,
        RECORD_PVR_NTFY_VIEW_ERROR_CODE,
        RECORD_PVR_NTFY_VIEW_ICL_MAIN_SVC,
        RECORD_PVR_NTFY_VIEW_ICL_SUB_SVC,
        RECORD_PVR_NTFY_VIEW_SOURCE_CHANGE,
        RECORD_PVR_NTFY_VIEW_SCHEDULE_START,
        RECORD_PVR_NTFY_VIEW_INTERNAL_ERROR,
        RECORD_PVR_NTFY_VIEW_UPDATE_EVERY_5_SECONDS,
        RECORD_PVR_NTFY_VIEW_ECP_NOT_CONTENT,
        RECORD_PVR_NTFY_STATUS_RECORDING,
        RECORD_PVR_NTFY_STATUS_STOPPED,
        RECORD_PVR_NTFY_STATUS_SAVING,
        RECORD_PVR_NTFY_CONTROLER_STOP_PVR,
        RECORD_PVR_NTFY_CONTROLLER_CHANNEL_READY,
        RECORD_PVR_NTFY_CONTROLLER_SOURCE_READY,
        RECORD_PVR_NTFY_CONTROLLER_DELAY_PVR_START_OK,
        RECORD_PVR_NTFY_CONTROLLER_SWITCH_CONTEXT,
        RECORD_PVR_NTFY_CONTROLLER_CAM_PIN_CHANGE,
        RECORD_PVR_NTFY_CONTROLLER_SET_CONFLICT_FEATURE,
        RECORD_PVR_NTFY_DELAY_PVR_START_OK,
        RECORD_PVR_NTFY_SWITCH_CONTEXT,
        RECORD_PVR_NTFY_CAM_PIN_CHANGE,
        RECORD_PVR_NTFY_INSERT_PVR_FILE,
        RECORD_PVR_NTFY_ICL_MAIN_SVC,
        RECORD_PVR_NTFY_ICL_SUB_SVC,
        RECORD_PVR_NTFY_CONTROLER_INTERNAL_ERROR,
        RECORD_PVR_NTFY_CONTROLER_DEVICE_ERROR,
        RECORD_PVR_NTFY_STRG_STATUS_CHANGE,
        RECORD_PVR_NTFY_ERROR_CODE,
        RECORD_PVR_NTFY_LAST_ENTRY
    }

    public enum RecordPvrStatus {
        RECORD_PVR_UNKNOWN,
        RECORD_PVR_RECORDING,
        RECORD_PVR_SAVING,
        RECORD_PVR_STOPPED,
        RECORD_PVR_SCHEDULING
    }

    public enum RecordSrcType {
        RECORD_PVR_SRC_TYPE_UNKNOWN,
        RECORD_PVR_SRC_TYPE_ATV,
        RECORD_PVR_SRC_TYPE_DTV,
        RECORD_PVR_SRC_TYPE_COMPOSITE,
        RECORD_PVR_SRC_TYPE_S_VIDEO,
        RECORD_PVR_SRC_TYPE_SCART,
        RECORD_PVR_SRC_TYPE_MMP,
        RECORD_PVR_SRC_TYPE_COMPONENT,
        RECORD_PVR_SRC_TYPE_VGA,
        RECORD_PVR_SRC_TYPE_HDMI
    }

    public enum RecordStopReason {
        RECORD_PVR_STOP_REASON_DISK_NOT_READY,
        RECORD_PVR_STOP_REASON_INPUT_BLOCK,
        RECORD_PVR_STOP_REASON_SVC_CHANGE,
        RECORD_PVR_STOP_REASON_SVC_STOP,
        RECORD_PVR_STOP_REASON_SVL_UPDATE,
        RECORD_PVR_STOP_REASON_POWER_OFF,
        RECORD_PVR_STOP_REASON_NO_SIGNAL,
        RECORD_PVR_STOP_REASON_ACFG_CHG,
        RECORD_PVR_STOP_REASON_REACH_DUR,
        RECORD_PVR_STOP_REASON_BY_USER,
        RECORD_PVR_STOP_REASON_BY_OTHER_OPERATION,
        RECORD_PVR_STOP_REASON_EAS,
        RECORD_PVR_STOP_REASON_DISK_FULL,
        RECORD_PVR_STOP_REASON_SCRAMBLED_STREAM,
        RECORD_PVR_STOP_REASON_BY_START_EPG,
        RECORD_PVR_STOP_REASON_BY_START_PIPPOP,
        RECORD_PVR_STOP_REASON_BY_START_MMP,
        RECORD_PVR_STOP_REASON_BY_CHANGE_CHANNEL,
        RECORD_PVR_STOP_REASON_DO_CONFLICT_ACTION,
        RECORD_PVR_STOP_REASON_LAST_ENTRY
    }

    public MtkTvRecordBase() {
        Log.d(TAG, "Enter MtkTvRecordBase struct Here.");
    }

    public List<MtkTvBookingBase> getBookingList() {
        return this.mRecordAdaptor.getBookingList();
    }

    public int addBooking(int channelId, int eventId) {
        Log.d(TAG, "Enter addBooking Here.");
        if (channelId < 0 || eventId < 0) {
            return -1;
        }
        return this.mRecordAdaptor.addBooking(channelId, eventId);
    }

    public int addBooking(int channelId, long startTime, int duration, String firstEventTitle) {
        Log.d(TAG, "Enter addRecord Here.channelId:" + channelId + ",startTime:" + startTime + ",duration:" + duration + ", firstEventTitle:" + firstEventTitle);
        if (startTime < 0 || duration < 0 || channelId < 0) {
            return -1;
        }
        return this.mRecordAdaptor.addBooking(channelId, startTime, duration, firstEventTitle);
    }

    public int addBooking(MtkTvBookingBase item) {
        if (item == null) {
            return -1;
        }
        Log.d(TAG, "Enter addBooing Here." + item);
        this.mRecordAdaptor.addBooking(item);
        this.mRecordAdaptor.updateBooking();
        return 0;
    }

    public int replaceBooking(int index, MtkTvBookingBase item) {
        if (item == null) {
            return -1;
        }
        Log.d(TAG, "Enter replaceBooking Here." + item);
        this.mRecordAdaptor.replaceBooking(index, item);
        this.mRecordAdaptor.updateBooking();
        return 0;
    }

    public int deleteBooking(int index) {
        Log.d(TAG, "Enter deleteBooking Here." + index);
        return this.mRecordAdaptor.delBooking(index);
    }

    public String getRecordingFileName() {
        Log.d(TAG, "Enter getRecordingFile Here.");
        String fileName = this.mRecordAdaptor.getRecordingFileName();
        if (fileName != null) {
            Log.d(TAG, "File: " + fileName);
        }
        return fileName;
    }

    public String getRecordingFileNameByHandle(int handle) {
        Log.d(TAG, "Enter getRecordingFile Here.");
        String fileName = this.mRecordAdaptor.getRecordingFileNameByHandle(handle);
        if (fileName != null) {
            Log.d(TAG, "File: " + fileName);
        }
        return fileName;
    }

    public boolean setRegisterInformation(String fileFullName) {
        Log.d(TAG, "Enter setGegisterInformation Here,  file = " + fileFullName);
        return this.mRecordAdaptor.setRegisterInformation(fileFullName);
    }

    public boolean getRegisterInformation(String fileFullName) {
        Log.d(TAG, "Enter getRegisterInformation Here");
        return this.mRecordAdaptor.getRegisterInformation(fileFullName);
    }

    public static long getStorageFreeSize() {
        return TVNativeWrapper.getStorageFreeSize_native();
    }

    public static long getStorageSize() {
        return TVNativeWrapper.getStorageSize_native();
    }

    public static int getRecordingPosition() {
        return TVNativeWrapper.getRecordingPosition();
    }

    public static int getRecordingPositionByHandle(int handle) {
        return TVNativeWrapper.getRecordingPositionByHandle(handle);
    }

    public static boolean setFileName(String path) {
        return true;
    }

    public static boolean setDisk(String mountpoint) {
        TVNativeWrapper.SetRecordDisk(mountpoint);
        return true;
    }

    public static String getDisk() {
        return TVNativeWrapper.getRecordDisk();
    }

    public static boolean setDiskByHandle(int handle, String mountpoint) {
        TVNativeWrapper.SetRecordDiskByHandle(handle, mountpoint);
        return true;
    }

    public static int open(int type) {
        Log.d(TAG, "Enter getRegisterInformation Here");
        return TVNativeWrapper.recordOpenPVR(type);
    }

    public static int start(int type) {
        Log.d(TAG, "Enter start Here");
        return TVNativeWrapper.recordStartPVR(type);
    }

    public static int startByHandle(int handle) {
        Log.d(TAG, "Enter getRegisterInformation Here");
        return TVNativeWrapper.recordStartPVRByHandle(handle);
    }

    public static int stop() {
        Log.d(TAG, "Enter stop Here");
        return TVNativeWrapper.recordStopPVR();
    }

    public static int close(int handle) {
        Log.d(TAG, "Enter getRegisterInformation Here");
        return TVNativeWrapper.recordClosePVR(handle);
    }

    public static int stopByHandle(int handle) {
        return TVNativeWrapper.recordStopPVRByHandle(handle);
    }

    public static boolean startSpeedTest(String mountPoint, int duration) {
        return TVNativeWrapper.recordStartSpeedTest(mountPoint, duration);
    }

    public static int getStatus() {
        return TVNativeWrapper.recordGetPVRStatus();
    }

    public static int getStatusByHandle(int handle) {
        return TVNativeWrapper.recordGetPVRStatusByHandle(handle);
    }

    public static int getSrcType() {
        return TVNativeWrapper.recordGetPVRSrcType();
    }

    public static int getSrcTypeByHandle(int handle) {
        return TVNativeWrapper.recordGetPVRSrcTypeByHandle(handle);
    }

    public static int getErrorID() {
        return TVNativeWrapper.recordgetErrorID();
    }

    public static int getErrorIDByHandle(int handle) {
        return TVNativeWrapper.recordgetErrorIDByHandle(handle);
    }

    public static long getRecordingFilesize() {
        return TVNativeWrapper.recordGetRecordingFilesize();
    }

    public static int open(int type, boolean bind) {
        Log.d(TAG, "Ex Open Enter getRegisterInformation Here");
        return TVNativeWrapper.recordOpenPVREx(type, bind);
    }

    public static int SelectSvc(int handle, int channelId, int svlId) {
        return TVNativeWrapper.recordPVRSelectSvc(handle, channelId, svlId);
    }
}
