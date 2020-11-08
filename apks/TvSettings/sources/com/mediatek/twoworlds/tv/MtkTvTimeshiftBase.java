package com.mediatek.twoworlds.tv;

import android.util.Log;

public class MtkTvTimeshiftBase {
    private static final String TAG = "MtkTvTimeshiftBase ";
    private int mRegisterDeviceSchedule = 0;

    public enum TimeshiftDeviceStatus {
        TIMESHIFT_DEV_STATUS_NO_DEVICE,
        TIMESHIFT_DEV_STATUS_INSETTED,
        TIMESHIFT_DEV_STATUS_REMOVED,
        TIMESHIFT_DEV_STATUS_FORMATED,
        TIMESHIFT_DEV_STATUS_SELECTED,
        TIMESHIFT_DEV_STATUS_DEVICE_AVAIL,
        TIMESHIFT_DEV_STATUS_CHG_ANOTHER_AVAIL,
        TIMESHIFT_DEV_STATUS_READY,
        TIMESHIFT_DEV_STATUS_DEVICE_FULL,
        TIMESHIFT_DEV_STATUS_DEVICE_TOO_SMALL,
        TIMESHIFT_DEV_STATUS_DEVICE_READ_ONLY
    }

    public enum TimeshiftErrorID {
        TIMESHIFT_ERR_ID_NONE,
        TIMESHIFT_ERR_ID_TIMESHIFTING,
        TIMESHIFT_ERR_ID_UNKNOWN_SRC,
        TIMESHIFT_ERR_ID_FEATURE_NOT_SUPPORTED,
        TIMESHIFT_ERR_ID_INSUFFICIENT_RESOURCE,
        TIMESHIFT_ERR_ID_AV_STREAM_NOT_AVAILABLE,
        TIMESHIFT_ERR_ID_INPUT_LOCKED,
        TIMESHIFT_ERR_ID_NO_DISK_FILE,
        TIMESHIFT_ERR_ID_VIDEO_RESOLUTION_ERROR,
        TIMESHIFT_ERR_ID_INTERNAL_ERROR,
        TIMESHIFT_ERR_ID_DISK_NOT_READY,
        TIMESHIFT_ERR_ID_LAST
    }

    public enum TimeshiftNotifyCode {
        TIMESHIFT_NTFY_STATUS_CHANGE,
        TIMESHIFT_NTFY_STRG_STATUS_CHANGE,
        TIMESHIFT_NTFY_ANALYSIS_BEGIN,
        TIMESHIFT_NTFY_ANALYSIS_PROGRESS,
        TIMESHIFT_NTFY_ANALYSIS_OK,
        TIMESHIFT_NTFY_ANALYSIS_FAIL,
        TIMESHIFT_NTFY_CREATE_FILE_BEGIN,
        TIMESHIFT_NTFY_CREATE_FILE_PROGRESS,
        TIMESHIFT_NTFY_CREATE_FILE_OK,
        TIMESHIFT_NTFY_CREATE_FILE_FAIL,
        TIMESHIFT_NTFY_SPEED_TEST_BEGIN,
        TIMESHIFT_NTFY_SPEED_TEST_PROGRESS,
        TIMESHIFT_NTFY_SPEED_TEST_LOW,
        TIMESHIFT_NTFY_SPEED_TEST_OK,
        TIMESHIFT_NTFY_SPEED_TEST_FAIL,
        TIMESHIFT_NTFY_REG_FINISHED,
        TIMESHIFT_NTFY_SPEED_CHANGE,
        TIMESHIFT_NTFY_NO_DISK_FILE,
        TIMESHIFT_NTFY_RECORD_STATUS,
        TIMESHIFT_NTFY_PLAYBACK_STATUS,
        TIMESHIFT_NTFY_LAST_ENTRY
    }

    public enum TimeshiftStatus {
        TIMESHIFT_STATUS_UNKNOWN,
        TIMESHIFT_STATUS_STARTED,
        TIMESHIFT_STATUS_STOPPED,
        TIMESHIFT_STATUS_LAST
    }

    public enum TimeshiftRecordStatus {
        TIMESHIFT_RECORD_UNKNOWN(0),
        TIMESHIFT_RECORD_STARTED(1),
        TIMESHIFT_RECORD_STOPPED(2),
        TIMESHIFT_RECORD_XX_1(3),
        TIMESHIFT_RECORD_XX_2(4);
        
        private int Value;

        private TimeshiftRecordStatus(int value) {
            this.Value = value;
        }

        public int Value() {
            return this.Value;
        }
    }

    public enum TimeshiftStopFlag {
        TIMESHIFT_STOP_RECORD(0),
        TIMESHIFT_STOP_MMP(1),
        TIMESHIFT_STOP_MMP_AND_SELECT_TV(2),
        TIMESHIFT_STOP_XX_1(3),
        TIMESHIFT_STOP_XX_2(4);
        
        private int Value;

        private TimeshiftStopFlag(int value) {
            this.Value = value;
        }

        public int Value() {
            return this.Value;
        }
    }

    public enum TimeshiftPlaybackSpeed {
        TIMESHIFT_PLAY_SPEED_FORWARD_1X(1),
        TIMESHIFT_PLAY_SPEED_FORWARD_2X(2),
        TIMESHIFT_PLAY_SPEED_FORWARD_4X(4),
        TIMESHIFT_PLAY_SPEED_FORWARD_8X(8),
        TIMESHIFT_PLAY_SPEED_FORWARD_16X(16),
        TIMESHIFT_PLAY_SPEED_FORWARD_32X(32),
        TIMESHIFT_PLAY_SPEED_FORWARD_64X(64),
        TIMESHIFT_PLAY_SPEED_FORWARD_128X(128),
        TIMESHIFT_PLAY_SPEED_REWIND_1X(-1),
        TIMESHIFT_PLAY_SPEED_REWIND_2X(-2),
        TIMESHIFT_PLAY_SPEED_REWIND_4X(-4),
        TIMESHIFT_PLAY_SPEED_REWIND_8X(-8),
        TIMESHIFT_PLAY_SPEED_REWIND_16X(-16),
        TIMESHIFT_PLAY_SPEED_REWIND_32X(-32),
        TIMESHIFT_PLAY_SPEED_REWIND_64X(-64),
        TIMESHIFT_PLAY_SPEED_REWIND_128X(-128);
        
        private int Value;

        private TimeshiftPlaybackSpeed(int value) {
            this.Value = value;
        }

        public int Value() {
            return this.Value;
        }
    }

    public enum TimeshiftPlaybackStatus {
        TIMESHIFT_PLAYBACK_STARTED(0),
        TIMESHIFT_PLAYBACK_STOPED(1),
        TIMESHIFT_PLAYBACK_STOPPING(2),
        TIMESHIFT_PLAYBACK_STARTING(3),
        TIMESHIFT_PLAYBACK_AUTO_PLAY_FOR_REACH_THRESHOLD(4);
        
        private int Value;

        private TimeshiftPlaybackStatus(int value) {
            this.Value = value;
        }

        public int Value() {
            return this.Value;
        }
    }

    public MtkTvTimeshiftBase() {
        Log.d(TAG, "Enter MtkTvTimeshiftBase struct Here.");
    }

    public TimeshiftRecordStatus getRecordStatus() {
        return TVNativeWrapper.getRecordStatus_native();
    }

    public int stop(TimeshiftStopFlag flag) {
        return TVNativeWrapper.stopTimeshift_native(flag);
    }

    public int setAutoRecord(boolean flag) {
        return TVNativeWrapper.setAutoRecord_native(flag);
    }

    public int setPlaybackSpeed(TimeshiftPlaybackSpeed speed) {
        return TVNativeWrapper.setPlaybackSpeed_native(speed);
    }

    public int setPlaybackPause() {
        return TVNativeWrapper.setPlaybackPause_native();
    }

    public int setPlaybackResume() {
        return TVNativeWrapper.setPlaybackResume_native();
    }

    public int seekTo(long timeMs) {
        return TVNativeWrapper.seekTo_native(timeMs);
    }

    public long getStartPosition() {
        return TVNativeWrapper.getStartPosition_native();
    }

    public long getCurrentPosition() {
        return TVNativeWrapper.getCurrentPosition_native();
    }

    public long getDuration() {
        return TVNativeWrapper.getTimeshiftDuration_native();
    }

    public long getPosition() {
        return TVNativeWrapper.getTimeshiftPosition_native();
    }

    public int getDeviceStatus() {
        Log.d(TAG, "Enter getDeviceStatus Here.");
        return TVNativeWrapper.getTimeshiftDeviceStatus_native();
    }

    public int registerDevice(String path, long size) {
        return TVNativeWrapper.registerTimeshiftDevice_native(path, size);
    }

    public int getRegisterDeviceSchedule() {
        this.mRegisterDeviceSchedule = TVNativeWrapper.getTimeshiftRegisterDeviceSchedule_native();
        return this.mRegisterDeviceSchedule;
    }

    public int start() {
        Log.d(TAG, "Enter start Here.");
        return TVNativeWrapper.startTimeshift_native();
    }

    public int stop() {
        Log.d(TAG, "Enter stop Here.");
        return TVNativeWrapper.stopTimeshift_native();
    }

    public boolean play() {
        Log.d(TAG, "Enter play Here.");
        return TVNativeWrapper.playTimeshift_native();
    }

    public boolean pause() {
        Log.d(TAG, "Enter pause Here.");
        return TVNativeWrapper.pauseTimeshift_native();
    }

    public int seek(long time) {
        Log.d(TAG, "Enter seek Here.");
        return TVNativeWrapper.seekTimeshift_native(time);
    }

    public int trickPlay(double speed) {
        Log.d(TAG, "Enter seek Here.");
        return TVNativeWrapper.trickPlayTimeshift_native(speed);
    }

    public int getErrorID() {
        Log.d(TAG, "Enter getError Here.");
        return TVNativeWrapper.getTimeshiftErrorID_native();
    }
}
