package com.mediatek.wwtv.tvcenter.tiftimeshift;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.format.DateFormat;
import android.util.Range;
import com.mediatek.twoworlds.tv.MtkTvTimeBase;
import com.mediatek.wwtv.tvcenter.commonview.TvSurfaceView;
import com.mediatek.wwtv.tvcenter.dvr.manager.DvrManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.WeakHandler;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelInfo;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFProgramInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class TifTimeShiftManager {
    private static final long DISABLE_ACTION_THRESHOLD = (REQUEST_CURRENT_POSITION_INTERVAL * 3);
    private static final long ENABLE_ACTION_THRESHOLD = (DISABLE_ACTION_THRESHOLD + (REQUEST_CURRENT_POSITION_INTERVAL * 3));
    public static final int ERROR_DATE_TIME = 1;
    @VisibleForTesting
    static final long INVALID_TIME = -1;
    /* access modifiers changed from: private */
    public static final long INVALID_TIMESHIFT_DURATION = TimeUnit.HOURS.toMillis(24);
    private static final int[] LONG_PROGRAM_SPEED_FACTORS = {2, 4, 8, 16, 32};
    /* access modifiers changed from: private */
    public static final long MAX_DUMMY_PROGRAM_DURATION = TimeUnit.MINUTES.toMillis(30);
    /* access modifiers changed from: private */
    public static final long MIN_DUMMY_PROGRAM_DURATION = TimeUnit.MINUTES.toMillis(30);
    private static final int MSG_GET_CURRENT_POSITION = 1000;
    private static final int MSG_PREFETCH_PROGRAM = 1001;
    public static final int PLAY_DIRECTION_BACKWARD = 1;
    public static final int PLAY_DIRECTION_FORWARD = 0;
    public static final int PLAY_SPEED_1X = 1;
    public static final int PLAY_SPEED_2X = 2;
    public static final int PLAY_SPEED_3X = 3;
    public static final int PLAY_SPEED_4X = 4;
    public static final int PLAY_SPEED_5X = 5;
    public static final int PLAY_SPEED_6X = 6;
    public static final int PLAY_STATUS_PAUSED = 0;
    public static final int PLAY_STATUS_PLAYING = 1;
    /* access modifiers changed from: private */
    public static final long PREFETCH_DURATION_FOR_NEXT = TimeUnit.HOURS.toMillis(2);
    /* access modifiers changed from: private */
    public static final long PREFETCH_TIME_OFFSET_FROM_PROGRAM_END = TimeUnit.MINUTES.toMillis(1);
    private static final long PROGRAM_START_TIME_THRESHOLD = TimeUnit.SECONDS.toMillis(3);
    /* access modifiers changed from: private */
    public static final long RECORDING_BOUNDARY_THRESHOLD = (3 * REQUEST_CURRENT_POSITION_INTERVAL);
    /* access modifiers changed from: private */
    public static final long REQUEST_CURRENT_POSITION_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    @VisibleForTesting
    static final long REQUEST_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(3);
    private static final int[] SHORT_PROGRAM_SPEED_FACTORS = {2, 4, 8, 16, 32};
    private static final int SHORT_PROGRAM_THRESHOLD_MILLIS = 2760000;
    private static final String TAG = "TifTimeShiftManager";
    public static final int TIME_SHIFT_ACTION_ID_FAST_FORWARD = 8;
    public static final int TIME_SHIFT_ACTION_ID_JUMP_TO_NEXT = 32;
    public static final int TIME_SHIFT_ACTION_ID_JUMP_TO_PREVIOUS = 16;
    public static final int TIME_SHIFT_ACTION_ID_PAUSE = 2;
    public static final int TIME_SHIFT_ACTION_ID_PLAY = 1;
    public static final int TIME_SHIFT_ACTION_ID_REWIND = 4;
    private static TifTimeShiftManager mTifTimeShiftManager;
    public boolean isTimeshiftStarted = false;
    public boolean isTimeshiftStopped = false;
    /* access modifiers changed from: private */
    public boolean mAvailabilityChanged = false;
    /* access modifiers changed from: private */
    public Context mContext;
    @VisibleForTesting
    final CurrentPositionMediator mCurrentPositionMediator = new CurrentPositionMediator();
    private TIFProgramInfo mCurrentProgram;
    private int mEnabledActionIds = 63;
    /* access modifiers changed from: private */
    public final Handler mHandler = new TimeShiftHandler(this);
    private int mLastActionId = 0;
    private Listener mListener;
    private MtkTvTimeBase mMtkTvTimeBase;
    /* access modifiers changed from: private */
    public boolean mNotificationEnabled;
    /* access modifiers changed from: private */
    public PlayController mPlayController;
    /* access modifiers changed from: private */
    public final ProgramManager mProgramManager;

    public interface Listener {
        void onActionEnabledChanged(int i, boolean z);

        void onAvailabilityChanged();

        void onCurrentPositionChanged();

        void onError(int i);

        void onPlayStatusChanged(int i);

        void onProgramInfoChanged();

        void onRecordStartTimeChanged();

        void onSpeedChange(float f);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlaySpeed {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeShiftActionId {
    }

    public TifTimeShiftManager(Context context, TvSurfaceView tvView) {
        this.mContext = context;
        this.mPlayController = new PlayController(tvView);
        this.mProgramManager = new ProgramManager();
        this.mMtkTvTimeBase = new MtkTvTimeBase();
    }

    public static TifTimeShiftManager getInstance(Context context, TvSurfaceView tvView) {
        if (mTifTimeShiftManager == null) {
            mTifTimeShiftManager = new TifTimeShiftManager(context, tvView);
        } else {
            mTifTimeShiftManager.updatePlayController(context, tvView);
        }
        return mTifTimeShiftManager;
    }

    public static TifTimeShiftManager getInstance() {
        return mTifTimeShiftManager;
    }

    public void updatePlayController(Context context, TvSurfaceView tvView) {
        this.mContext = context;
        this.mPlayController = new PlayController(tvView);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public boolean isAvailable() {
        return this.mPlayController.isAvailable();
    }

    public long getCurrentPositionMs() {
        if (this.mCurrentPositionMediator.mCurrentPositionMs >= this.mPlayController.getRecordTimeMs() || this.mPlayController.getTvView().timeshiftGetCurrentPositionMs() <= this.mPlayController.getRecordTimeMs()) {
            return this.mCurrentPositionMediator.mCurrentPositionMs;
        }
        return this.mPlayController.getTvView().timeshiftGetCurrentPositionMs();
    }

    /* access modifiers changed from: package-private */
    public void setCurrentPositionMs(long currentTimeMs) {
        this.mCurrentPositionMediator.onCurrentPositionChanged(currentTimeMs);
    }

    public long getRecordStartTimeMs() {
        if (this.mProgramManager.getOldestProgramStartTime() == -1) {
            return -1;
        }
        return this.mPlayController.mRecordStartTimeMs;
    }

    public long getBroadcastTimeInUtcSeconds() {
        long milliSeconds = this.mMtkTvTimeBase.getCurrentTimeInUtcMilliSeconds();
        long seconds = this.mMtkTvTimeBase.getLocalTime().toSeconds();
        long millis = this.mMtkTvTimeBase.getLocalTime().toMillis();
        MtkLog.d(TAG, "Get time milliSeconds: " + milliSeconds);
        MtkLog.d(TAG, "Get time seconds: " + seconds);
        MtkLog.d(TAG, "Get time millis: " + millis);
        return milliSeconds;
    }

    public void play() {
        if (isActionEnabled(1)) {
            this.mLastActionId = 1;
            this.mPlayController.play();
            updateActions();
        }
    }

    public void speed(float speed) {
        if (this.mListener != null) {
            this.mListener.onSpeedChange(speed);
        }
    }

    public boolean isTimeshiftStarted() {
        return this.isTimeshiftStarted;
    }

    public void stop() {
        this.mCurrentPositionMediator.mCurrentPositionMs = 0;
        this.mAvailabilityChanged = false;
        this.mPlayController.stop();
    }

    public void stopAll() {
        this.mCurrentPositionMediator.mCurrentPositionMs = 0;
        this.mAvailabilityChanged = false;
        this.mPlayController.stopAll();
    }

    public boolean isForwarding() {
        return this.mPlayController.isForwarding();
    }

    public boolean isRewinding() {
        return this.mPlayController.isRewinding();
    }

    public void pause() {
        if (isActionEnabled(2)) {
            this.mLastActionId = 2;
            this.mPlayController.pause();
            updateActions();
        }
    }

    public void togglePlayPause() {
        this.mPlayController.togglePlayPause();
    }

    public void rewind() {
        if (!isActionEnabled(4)) {
            MtkLog.d(TAG, "timeshift rewind action disabled.");
            return;
        }
        this.mLastActionId = 4;
        this.mPlayController.rewind();
        updateActions();
    }

    public void fastForward() {
        if (!isActionEnabled(8)) {
            MtkLog.d(TAG, "timeshift fastforward action disabled.");
            return;
        }
        this.mLastActionId = 8;
        this.mPlayController.fastForward();
        updateActions();
    }

    public void jumpToPrevious() {
        TIFProgramInfo program;
        if (isActionEnabled(16) && (program = this.mProgramManager.getProgramAt(this.mCurrentPositionMediator.mCurrentPositionMs - PROGRAM_START_TIME_THRESHOLD)) != null) {
            long seekPosition = Math.max(program.getmStartTimeUtcSec(), this.mPlayController.mRecordStartTimeMs);
            this.mLastActionId = 16;
            this.mPlayController.seekTo(seekPosition);
            this.mCurrentPositionMediator.onSeekRequested(seekPosition);
            updateActions();
        }
    }

    public void jumpToNext() {
        TIFProgramInfo currentProgram;
        if (isActionEnabled(32) && (currentProgram = this.mProgramManager.getProgramAt(this.mCurrentPositionMediator.mCurrentPositionMs)) != null) {
            TIFProgramInfo nextProgram = this.mProgramManager.getProgramAt(currentProgram.getmEndTimeUtcSec());
            long currentTimeMs = getBroadcastTimeInUtcSeconds();
            this.mLastActionId = 32;
            if (nextProgram == null || nextProgram.getmStartTimeUtcSec() > currentTimeMs) {
                this.mPlayController.seekTo(currentTimeMs);
                if (this.mPlayController.isForwarding()) {
                    boolean unused = this.mPlayController.mIsPlayOffsetChanged = false;
                    this.mCurrentPositionMediator.initialize(currentTimeMs);
                } else {
                    this.mCurrentPositionMediator.onSeekRequested(currentTimeMs);
                }
            } else {
                this.mPlayController.seekTo(nextProgram.getmStartTimeUtcSec());
                this.mCurrentPositionMediator.onSeekRequested(nextProgram.getmStartTimeUtcSec());
            }
            updateActions();
        }
    }

    public int getPlayStatus() {
        return this.mPlayController.mPlayStatus;
    }

    public int getDisplayedPlaySpeed() {
        return this.mPlayController.mDisplayedPlaySpeed;
    }

    public int getmPlaybackSpeed() {
        return this.mPlayController.mPlaybackSpeed;
    }

    public int getPlayDirection() {
        return this.mPlayController.mPlayDirection;
    }

    public int getLastActionId() {
        return this.mLastActionId;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void enableAction(int actionId, boolean enable) {
        int oldEnabledActionIds = this.mEnabledActionIds;
        if (enable) {
            this.mEnabledActionIds |= actionId;
        } else {
            this.mEnabledActionIds &= ~actionId;
        }
        if (this.mNotificationEnabled && this.mListener != null && oldEnabledActionIds != this.mEnabledActionIds) {
            this.mListener.onActionEnabledChanged(actionId, enable);
        }
    }

    public boolean isActionEnabled(int actionId) {
        return (this.mEnabledActionIds & actionId) == actionId;
    }

    private void updateActions() {
        boolean enabled = false;
        if (isAvailable()) {
            enableAction(1, true);
            enableAction(2, true);
            MtkLog.d(TAG, "updateActions rewind threshold:" + (isActionEnabled(4) ? DISABLE_ACTION_THRESHOLD : ENABLE_ACTION_THRESHOLD));
            boolean enabled2 = this.mCurrentPositionMediator.mCurrentPositionMs - this.mPlayController.mRecordStartTimeMs > DISABLE_ACTION_THRESHOLD;
            enableAction(4, true);
            enableAction(16, enabled2);
            MtkLog.d(TAG, "updateActions fastforward threshold:" + (isActionEnabled(8) ? DISABLE_ACTION_THRESHOLD : ENABLE_ACTION_THRESHOLD));
            if (getBroadcastTimeInUtcSeconds() - this.mCurrentPositionMediator.mCurrentPositionMs > DISABLE_ACTION_THRESHOLD) {
                enabled = true;
            }
            enableAction(8, true);
            enableAction(32, enabled);
            return;
        }
        enableAction(1, false);
        enableAction(2, false);
        enableAction(4, false);
        enableAction(16, false);
        enableAction(8, false);
        enableAction(1, false);
    }

    private void updateCurrentProgram() {
        TIFProgramInfo currentProgram = getProgramAt(this.mCurrentPositionMediator.mCurrentPositionMs);
        if (!Objects.equals(this.mCurrentProgram, currentProgram)) {
            MtkLog.d(TAG, "Current program has been updated. " + currentProgram);
            this.mCurrentProgram = currentProgram;
            if (this.mNotificationEnabled && this.mPlayController.getCurrentChannel() != null) {
                this.mPlayController.onCurrentProgramChanged();
            }
        }
    }

    public boolean isNormalPlaying() {
        if (this.mPlayController.isAvailable() && this.mPlayController.mPlayStatus == 1 && this.mPlayController.mPlayDirection == 0 && this.mPlayController.mDisplayedPlaySpeed == 1) {
            return true;
        }
        return false;
    }

    public boolean isPaused() {
        return this.mPlayController.isAvailable() && this.mPlayController.mPlayStatus == 0;
    }

    public boolean getAvailabilityChanged() {
        return this.mAvailabilityChanged;
    }

    @NonNull
    public TIFProgramInfo getProgramAt(long timeMs) {
        MtkLog.d(TAG, "getProgramAt timeMs: " + getTimeString(timeMs));
        TIFProgramInfo program = this.mProgramManager.getProgramAt(timeMs);
        if (program != null) {
            return program;
        }
        this.mProgramManager.addDummyProgramsAt(timeMs);
        TIFProgramInfo program2 = this.mProgramManager.getProgramAt(timeMs);
        MtkLog.d(TAG, "initializeTimeline, " + program2);
        return program2;
    }

    /* access modifiers changed from: package-private */
    public void onAvailabilityChanged() {
        this.mProgramManager.onAvailabilityChanged(this.mPlayController.isAvailable(), this.mPlayController.getCurrentChannel(), this.mPlayController.mRecordStartTimeMs);
        updateActions();
        if (this.mListener != null) {
            this.mListener.onAvailabilityChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onRecordStartTimeChanged() {
        if (this.mPlayController.isAvailable()) {
            this.mProgramManager.onRecordStartTimeChanged(this.mPlayController.mRecordStartTimeMs);
        }
        updateActions();
        if (this.mNotificationEnabled && this.mListener != null) {
            this.mListener.onRecordStartTimeChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onCurrentPositionChanged() {
        updateActions();
        updateCurrentProgram();
        if (this.mNotificationEnabled && this.mListener != null) {
            this.mListener.onCurrentPositionChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onPlayStatusChanged(int status) {
        if (this.mNotificationEnabled && this.mListener != null) {
            this.mListener.onPlayStatusChanged(status);
        }
    }

    /* access modifiers changed from: package-private */
    public void onProgramInfoChanged() {
        updateCurrentProgram();
        if (this.mNotificationEnabled && this.mListener != null) {
            this.mListener.onProgramInfoChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onError(int errId) {
        if (this.mListener != null) {
            this.mListener.onError(errId);
        }
    }

    @Nullable
    public TIFProgramInfo getCurrentProgram() {
        if (isAvailable()) {
            return this.mCurrentProgram;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int getPlaybackSpeed() {
        int[] playbackSpeedList;
        if (getCurrentProgram() == null || getCurrentProgram().getmEndTimeUtcSec() - getCurrentProgram().getmStartTimeUtcSec() > 2760000) {
            playbackSpeedList = LONG_PROGRAM_SPEED_FACTORS;
        } else {
            playbackSpeedList = SHORT_PROGRAM_SPEED_FACTORS;
        }
        switch (this.mPlayController.mDisplayedPlaySpeed) {
            case 1:
                return 1;
            case 2:
                return playbackSpeedList[0];
            case 3:
                return playbackSpeedList[1];
            case 4:
                return playbackSpeedList[2];
            case 5:
                return playbackSpeedList[3];
            case 6:
                return playbackSpeedList[4];
            default:
                MtkLog.w(TAG, "Unknown displayed play speed is chosen : " + this.mPlayController.mDisplayedPlaySpeed);
                return 1;
        }
    }

    private class PlayController {
        /* access modifiers changed from: private */
        public int mDisplayedPlaySpeed = 1;
        /* access modifiers changed from: private */
        public boolean mIsPlayOffsetChanged;
        /* access modifiers changed from: private */
        public int mPlayDirection = 0;
        /* access modifiers changed from: private */
        public int mPlayStatus = 0;
        /* access modifiers changed from: private */
        public int mPlaybackSpeed;
        /* access modifiers changed from: private */
        public long mPossibleStartTimeMs;
        /* access modifiers changed from: private */
        public long mRecordStartTimeMs;
        /* access modifiers changed from: private */
        public long mSystemTimeMs = 0;
        private final TvSurfaceView mTvView;

        PlayController(TvSurfaceView tvView) {
            this.mTvView = tvView;
            this.mTvView.setTimeShiftListener(new TvSurfaceView.TimeShiftListener(TifTimeShiftManager.this) {
                public void onAvailabilityChanged() {
                    MtkLog.d(TifTimeShiftManager.TAG, "onAvailabilityChanged start.");
                    boolean unused = TifTimeShiftManager.this.mAvailabilityChanged = true;
                    boolean unused2 = TifTimeShiftManager.this.mNotificationEnabled = false;
                    int unused3 = PlayController.this.mDisplayedPlaySpeed = 1;
                    int unused4 = PlayController.this.mPlaybackSpeed = 1;
                    int unused5 = PlayController.this.mPlayDirection = 0;
                    boolean unused6 = PlayController.this.mIsPlayOffsetChanged = false;
                    long unused7 = PlayController.this.mPossibleStartTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
                    long unused8 = PlayController.this.mRecordStartTimeMs = PlayController.this.mPossibleStartTimeMs;
                    long unused9 = PlayController.this.mSystemTimeMs = PlayController.this.mPossibleStartTimeMs;
                    MtkLog.d(TifTimeShiftManager.TAG, "onAvailabilityChanged mPossibleStartTimeMs: " + TifTimeShiftManager.this.getTimeString(PlayController.this.mPossibleStartTimeMs));
                    TifTimeShiftManager.this.mCurrentPositionMediator.initialize(PlayController.this.mPossibleStartTimeMs);
                    TifTimeShiftManager.this.mHandler.removeMessages(1000);
                    if (PlayController.this.isAvailable()) {
                        TifTimeShiftManager.this.mPlayController.setPlayStatus(1);
                        TifTimeShiftManager.this.mHandler.sendEmptyMessageDelayed(1000, 3 * TifTimeShiftManager.REQUEST_CURRENT_POSITION_INTERVAL);
                    } else {
                        TifTimeShiftManager.this.mPlayController.setPlayStatus(0);
                    }
                    TifTimeShiftManager.this.onAvailabilityChanged();
                    boolean unused10 = TifTimeShiftManager.this.mNotificationEnabled = true;
                }

                public void onRecordStartTimeChanged(long recordStartTimeMs) {
                    MtkLog.d(TifTimeShiftManager.TAG, "onRecordStartTimeChanged recordStartTimeMs: " + TifTimeShiftManager.this.getTimeString(recordStartTimeMs));
                    if (PlayController.this.mRecordStartTimeMs != recordStartTimeMs) {
                        long unused = PlayController.this.mRecordStartTimeMs = recordStartTimeMs;
                        TifTimeShiftManager.this.onRecordStartTimeChanged();
                    }
                }

                public void onSpeechChange(float speech) {
                    TifTimeShiftManager.this.speed(speech);
                }

                public void onPlayStatusChanged(int status) {
                    TifTimeShiftManager.this.onPlayStatusChanged(status);
                }

                public void onChannelChanged(String inputId, Uri channelUri) {
                }

                public void onTimeshiftRecordStart(boolean isStarted) {
                    TifTimeShiftManager.this.isTimeshiftStarted = isStarted;
                }
            });
        }

        /* access modifiers changed from: package-private */
        public boolean isAvailable() {
            return this.mTvView.isTimeShiftAvailable();
        }

        /* access modifiers changed from: package-private */
        public void handleGetCurrentPosition() {
            long currentTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
            boolean isRecordStartTime = true;
            if (this.mSystemTimeMs == 0 || Math.abs(this.mSystemTimeMs - currentTimeMs) <= TifTimeShiftManager.INVALID_TIMESHIFT_DURATION) {
                this.mSystemTimeMs = currentTimeMs;
                MtkLog.d(TifTimeShiftManager.TAG, "handleGetCurrentPosition, mIsPlayOffsetChanged: " + this.mIsPlayOffsetChanged);
                if (this.mIsPlayOffsetChanged) {
                    long currentPositionMs = Math.max(this.mTvView.timeshiftGetCurrentPositionMs(), this.mRecordStartTimeMs);
                    boolean isCurrentTime = currentTimeMs - currentPositionMs < TifTimeShiftManager.RECORDING_BOUNDARY_THRESHOLD;
                    MtkLog.d(TifTimeShiftManager.TAG, "handleGetCurrentPosition, currentPositionMs: " + TifTimeShiftManager.this.getTimeString(currentPositionMs) + ", isCurrentTime:" + isCurrentTime);
                    if (!isCurrentTime || !isForwarding()) {
                        long newCurrentPositionMs = currentPositionMs;
                        if (currentPositionMs - this.mRecordStartTimeMs >= TifTimeShiftManager.RECORDING_BOUNDARY_THRESHOLD) {
                            isRecordStartTime = false;
                        }
                        if (isRecordStartTime && isRewinding()) {
                            TifTimeShiftManager.this.play();
                        }
                    } else {
                        long j = currentTimeMs;
                        this.mIsPlayOffsetChanged = false;
                        if (this.mDisplayedPlaySpeed > 1) {
                            TifTimeShiftManager.this.play();
                        }
                    }
                    TifTimeShiftManager.this.setCurrentPositionMs(this.mTvView.timeshiftGetCurrentPositionMs());
                } else {
                    TifTimeShiftManager.this.setCurrentPositionMs(TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds());
                }
                TifTimeShiftManager.this.onCurrentPositionChanged();
                TifTimeShiftManager.this.mHandler.sendEmptyMessageDelayed(1000, TifTimeShiftManager.REQUEST_CURRENT_POSITION_INTERVAL);
                return;
            }
            TifTimeShiftManager.this.onError(1);
            TifTimeShiftManager.this.isTimeshiftStopped = true;
            MtkLog.d(TifTimeShiftManager.TAG, "handleGetCurrentPosition, error");
        }

        /* access modifiers changed from: package-private */
        public void play() {
            MtkLog.d(TifTimeShiftManager.TAG, "play");
            this.mDisplayedPlaySpeed = 1;
            this.mPlaybackSpeed = 1;
            this.mPlayDirection = 0;
            try {
                this.mTvView.timeshiftPlayEx();
                setPlayStatus(1);
            } catch (Exception e) {
            }
        }

        /* access modifiers changed from: package-private */
        public void pause() {
            MtkLog.d(TifTimeShiftManager.TAG, "pause");
            DvrManager.getInstance().speakText("timeshift pause");
            this.mDisplayedPlaySpeed = 1;
            this.mPlaybackSpeed = 1;
            try {
                this.mTvView.timeshiftPauseEx();
                setPlayStatus(0);
                this.mIsPlayOffsetChanged = true;
            } catch (Exception e) {
            }
        }

        /* access modifiers changed from: package-private */
        public void stop() {
            this.mPossibleStartTimeMs = 0;
            this.mRecordStartTimeMs = 0;
            MtkLog.d(TifTimeShiftManager.TAG, "stop");
            this.mTvView.sendAppPrivateCommand("session_event_timeshift_stop_mmp_and_select_tv", new Bundle());
        }

        /* access modifiers changed from: package-private */
        public void stopAll() {
            this.mPossibleStartTimeMs = 0;
            this.mRecordStartTimeMs = 0;
            MtkLog.d(TifTimeShiftManager.TAG, "stopAll");
            this.mTvView.sendAppPrivateCommand("session_event_timeshift_stop_rec", new Bundle());
        }

        /* access modifiers changed from: package-private */
        public void togglePlayPause() {
            if (this.mPlayStatus == 0) {
                DvrManager.getInstance().speakText("timeshift play");
                play();
                return;
            }
            pause();
        }

        /* access modifiers changed from: package-private */
        public void rewind() {
            MtkLog.d(TifTimeShiftManager.TAG, "rewind");
            DvrManager.getInstance().speakText("timeshift fast rewind");
            if (this.mPlayDirection == 1) {
                increaseDisplayedPlaySpeed();
            } else {
                this.mDisplayedPlaySpeed = 2;
            }
            this.mPlayDirection = 1;
            this.mPlaybackSpeed = TifTimeShiftManager.this.getPlaybackSpeed();
            try {
                this.mTvView.timeshiftRewind(this.mPlaybackSpeed);
                setPlayStatus(1);
                this.mIsPlayOffsetChanged = true;
            } catch (Exception e) {
                MtkLog.e(TifTimeShiftManager.TAG, "rewind error " + e.getMessage());
            }
        }

        /* access modifiers changed from: package-private */
        public void fastForward() {
            MtkLog.d(TifTimeShiftManager.TAG, "fastForward");
            DvrManager.getInstance().speakText("timeshift fastForward");
            if (this.mPlayDirection == 0) {
                increaseDisplayedPlaySpeed();
            } else {
                this.mDisplayedPlaySpeed = 2;
            }
            this.mPlayDirection = 0;
            this.mPlaybackSpeed = TifTimeShiftManager.this.getPlaybackSpeed();
            try {
                this.mTvView.timeshiftFastForward(this.mPlaybackSpeed);
                setPlayStatus(1);
                this.mIsPlayOffsetChanged = true;
            } catch (Exception e) {
                MtkLog.e(TifTimeShiftManager.TAG, "fastForward error " + e.getMessage());
            }
        }

        /* access modifiers changed from: package-private */
        public void seekTo(long timeMs) {
            try {
                this.mTvView.timeshiftSeekTo(Math.min(TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds(), Math.max(this.mRecordStartTimeMs, timeMs)));
                this.mIsPlayOffsetChanged = true;
            } catch (Exception e) {
            }
        }

        /* access modifiers changed from: package-private */
        public void onCurrentProgramChanged() {
            int playbackSpeed;
            if (this.mDisplayedPlaySpeed != 1 && (playbackSpeed = TifTimeShiftManager.this.getPlaybackSpeed()) != this.mPlaybackSpeed) {
                this.mPlaybackSpeed = playbackSpeed;
                if (this.mPlayDirection == 0) {
                    try {
                        this.mTvView.timeshiftFastForward(this.mPlaybackSpeed);
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        this.mTvView.timeshiftRewind(this.mPlaybackSpeed);
                    } catch (Exception e2) {
                    }
                }
            }
        }

        private void increaseDisplayedPlaySpeed() {
            switch (this.mDisplayedPlaySpeed) {
                case 1:
                    this.mDisplayedPlaySpeed = 2;
                    return;
                case 2:
                    this.mDisplayedPlaySpeed = 3;
                    return;
                case 3:
                    this.mDisplayedPlaySpeed = 4;
                    return;
                case 4:
                    this.mDisplayedPlaySpeed = 5;
                    return;
                case 5:
                    this.mDisplayedPlaySpeed = 6;
                    return;
                case 6:
                    this.mDisplayedPlaySpeed = 2;
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: private */
        public void setPlayStatus(int status) {
            if (status != 4) {
                this.mPlayStatus = status;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isForwarding() {
            return this.mPlayStatus == 1 && this.mPlayDirection == 0;
        }

        /* access modifiers changed from: private */
        public boolean isRewinding() {
            return this.mPlayStatus == 1 && this.mPlayDirection == 1;
        }

        /* access modifiers changed from: package-private */
        public TIFChannelInfo getCurrentChannel() {
            return TIFChannelManager.getInstance(TifTimeShiftManager.this.mContext).getTIFChannelInfoById(CommonIntegration.getInstance().getCurrentChannelId());
        }

        public TvSurfaceView getTvView() {
            return this.mTvView;
        }

        public long getRecordTimeMs() {
            return this.mRecordStartTimeMs;
        }
    }

    private class ProgramManager {
        private TIFChannelInfo mChannel;
        private final Queue<Range<Long>> mProgramLoadQueue = new LinkedList();
        private final List<TIFProgramInfo> mPrograms = new ArrayList();

        ProgramManager() {
        }

        /* access modifiers changed from: package-private */
        public void onAvailabilityChanged(boolean available, TIFChannelInfo channel, long currentPositionMs) {
            this.mProgramLoadQueue.clear();
            TifTimeShiftManager.this.mHandler.removeMessages(1001);
            this.mPrograms.clear();
            this.mChannel = channel;
            if (channel != null && available) {
                long prefetchStartTimeMs = Utils.floorTime(currentPositionMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION);
                this.mPrograms.addAll(createDummyPrograms(prefetchStartTimeMs, TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT + currentPositionMs));
                MtkLog.d(TifTimeShiftManager.TAG, "onAvailabilityChanged, createDummyPrograms, " + prefetchStartTimeMs + "," + currentPositionMs);
                schedulePrefetchPrograms();
                TifTimeShiftManager.this.onProgramInfoChanged();
            }
        }

        /* access modifiers changed from: package-private */
        public void onRecordStartTimeChanged(long startTimeMs) {
            if (this.mChannel != null) {
                boolean addDummyPrograms = addDummyPrograms(Utils.floorTime(startTimeMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION), TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT + TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds());
            }
        }

        private void startTaskIfNeeded() {
            if (!this.mProgramLoadQueue.isEmpty()) {
            }
        }

        private void startNext() {
        }

        /* access modifiers changed from: package-private */
        public void addDummyProgramsAt(long timeMs) {
            addDummyPrograms(timeMs, TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT + timeMs);
        }

        private boolean addDummyPrograms(Range<Long> period) {
            return addDummyPrograms(period.getLower().longValue(), period.getUpper().longValue());
        }

        private boolean addDummyPrograms(long startTimeMs, long endTimeMs) {
            boolean added = false;
            removeDummyPrograms(startTimeMs, endTimeMs);
            if (this.mPrograms.isEmpty()) {
                this.mPrograms.addAll(createDummyPrograms(startTimeMs, endTimeMs));
                return true;
            }
            TIFProgramInfo firstProgram = this.mPrograms.get(0);
            if (startTimeMs < firstProgram.getmStartTimeUtcSec()) {
                this.mPrograms.addAll(0, createDummyPrograms(startTimeMs, firstProgram.getmStartTimeUtcSec()));
                added = true;
            }
            TIFProgramInfo lastProgram = this.mPrograms.get(this.mPrograms.size() - 1);
            if (endTimeMs - lastProgram.getmEndTimeUtcSec() >= TifTimeShiftManager.MIN_DUMMY_PROGRAM_DURATION) {
                this.mPrograms.addAll(createDummyPrograms(lastProgram.getmEndTimeUtcSec(), endTimeMs));
                added = true;
            }
            boolean added2 = added;
            int i = 1;
            while (i < this.mPrograms.size()) {
                long endOfPrevious = this.mPrograms.get(i - 1).getmEndTimeUtcSec();
                long startOfCurrent = this.mPrograms.get(i).getmStartTimeUtcSec();
                if (startOfCurrent > endOfPrevious) {
                    List<TIFProgramInfo> dummyPrograms = createDummyPrograms(endOfPrevious, startOfCurrent);
                    this.mPrograms.addAll(i, dummyPrograms);
                    i += dummyPrograms.size();
                    added2 = true;
                }
                i++;
            }
            if (MtkLog.logOnFlag != 0) {
                dumpPrograms();
            }
            return added2;
        }

        private void removeDummyPrograms() {
            for (int i = 0; i < this.mPrograms.size(); i++) {
                TIFProgramInfo tIFProgramInfo = this.mPrograms.get(i);
            }
        }

        private void removeDummyPrograms(long startTimeMs, long endTimeMs) {
            int i;
            int i2 = 0;
            while (i2 < this.mPrograms.size()) {
                TIFProgramInfo program = this.mPrograms.get(i2);
                if (program.getmStartTimeUtcSec() > TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT + endTimeMs) {
                    i = i2 - 1;
                    this.mPrograms.remove(i2);
                } else if (program.getmEndTimeUtcSec() < TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT + startTimeMs) {
                    i = i2 - 1;
                    this.mPrograms.remove(i2);
                } else {
                    i = i2;
                }
                i2 = i + 1;
            }
        }

        public boolean isProgramExist(long startTimeMs, long endTimeMs) {
            if (this.mPrograms == null || this.mPrograms.isEmpty()) {
                return false;
            }
            for (int i = 0; i < this.mPrograms.size(); i++) {
                TIFProgramInfo program = this.mPrograms.get(i);
                if (program.getmStartTimeUtcSec() == startTimeMs && program.getmEndTimeUtcSec() == endTimeMs) {
                    return true;
                }
            }
            return false;
        }

        private void removeOverlappedPrograms(List<TIFProgramInfo> loadedPrograms) {
            if (this.mPrograms.size() != 0) {
                int j = 0;
                TIFProgramInfo program = this.mPrograms.get(0);
                int i = 0;
                while (i < this.mPrograms.size() && j < loadedPrograms.size()) {
                    TIFProgramInfo loadedProgram = loadedPrograms.get(j);
                    while (program.getmEndTimeUtcSec() < loadedProgram.getmStartTimeUtcSec()) {
                        i++;
                        if (i != this.mPrograms.size()) {
                            program = this.mPrograms.get(i);
                        } else {
                            return;
                        }
                    }
                    while (program.getmStartTimeUtcSec() < loadedProgram.getmEndTimeUtcSec() && program.getmEndTimeUtcSec() > loadedProgram.getmStartTimeUtcSec()) {
                        this.mPrograms.remove(i);
                        if (i >= this.mPrograms.size()) {
                            break;
                        }
                        program = this.mPrograms.get(i);
                    }
                    j++;
                }
            }
        }

        private List<TIFProgramInfo> createDummyPrograms(long startTimeMs, long endTimeMs) {
            MtkLog.d(TifTimeShiftManager.TAG, "createDummyPrograms. " + TifTimeShiftManager.this.getTimeString(startTimeMs) + "," + TifTimeShiftManager.this.getTimeString(endTimeMs));
            if (startTimeMs >= endTimeMs) {
                return Collections.emptyList();
            }
            List<TIFProgramInfo> programs = new ArrayList<>();
            long start = Utils.floorTime(startTimeMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION);
            long end = Utils.ceilTime(startTimeMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION);
            if (!isProgramExist(start, end) && end < endTimeMs) {
                programs.add(new TIFProgramInfo.Builder().setmStartTimeUtcSec(start).setmEndTimeUtcSec(end).build());
                end += TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION;
            }
            if (endTimeMs >= end) {
                long start2 = Utils.floorTime(endTimeMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION);
                long end2 = Utils.ceilTime(endTimeMs, TifTimeShiftManager.MAX_DUMMY_PROGRAM_DURATION);
                if (!isProgramExist(start2, end2)) {
                    programs.add(new TIFProgramInfo.Builder().setmStartTimeUtcSec(start2).setmEndTimeUtcSec(end2).build());
                }
            }
            return programs;
        }

        /* access modifiers changed from: package-private */
        public TIFProgramInfo getProgramAt(long timeMs) {
            return getProgramAt(timeMs, 0, this.mPrograms.size() - 1);
        }

        private TIFProgramInfo getProgramAt(long timeMs, int start, int end) {
            if (start > end) {
                return null;
            }
            int mid = (start + end) / 2;
            TIFProgramInfo program = this.mPrograms.get(mid);
            MtkLog.d(TifTimeShiftManager.TAG, "getProgramAt, " + TifTimeShiftManager.this.getTimeString(program.getmStartTimeUtcSec()) + "," + TifTimeShiftManager.this.getTimeString(timeMs) + "," + TifTimeShiftManager.this.getTimeString(program.getmEndTimeUtcSec()));
            if (program.getmStartTimeUtcSec() > timeMs) {
                return getProgramAt(timeMs, start, mid - 1);
            }
            if (program.getmEndTimeUtcSec() <= timeMs) {
                return getProgramAt(timeMs, mid + 1, end);
            }
            return program;
        }

        /* access modifiers changed from: private */
        public long getOldestProgramStartTime() {
            if (this.mPrograms.isEmpty()) {
                return -1;
            }
            return this.mPrograms.get(0).getmStartTimeUtcSec();
        }

        private TIFProgramInfo getLastValidProgram() {
            return this.mPrograms.get(this.mPrograms.size() - 1);
        }

        private void schedulePrefetchPrograms() {
            MtkLog.d(TifTimeShiftManager.TAG, "Scheduling prefetching programs.");
            if (!TifTimeShiftManager.this.mHandler.hasMessages(1001)) {
                TIFProgramInfo lastValidProgram = getLastValidProgram();
                MtkLog.d(TifTimeShiftManager.TAG, "Last valid program = " + lastValidProgram);
                if (lastValidProgram != null) {
                    long delay = (lastValidProgram.getmEndTimeUtcSec() - TifTimeShiftManager.PREFETCH_TIME_OFFSET_FROM_PROGRAM_END) - TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
                    TifTimeShiftManager.this.mHandler.sendEmptyMessageDelayed(1001, delay);
                    MtkLog.d(TifTimeShiftManager.TAG, "Scheduling with " + delay + "(ms) delays.");
                    return;
                }
                TifTimeShiftManager.this.mHandler.sendEmptyMessage(1001);
                MtkLog.d(TifTimeShiftManager.TAG, "Scheduling promptly.");
            }
        }

        /* access modifiers changed from: private */
        public void prefetchPrograms() {
            long startTimeMs;
            TIFProgramInfo lastValidProgram = getLastValidProgram();
            if (lastValidProgram == null) {
                startTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
            } else {
                startTimeMs = lastValidProgram.getmStartTimeUtcSec();
            }
            long endTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds() + TifTimeShiftManager.PREFETCH_DURATION_FOR_NEXT;
            MtkLog.d(TifTimeShiftManager.TAG, "Prefetch task starts: {startTime=" + Utils.toTimeString(startTimeMs) + ", endTime=" + Utils.toTimeString(endTimeMs) + "}");
            try {
                this.mProgramLoadQueue.add(Range.create(Long.valueOf(startTimeMs), Long.valueOf(endTimeMs)));
                startTaskIfNeeded();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void dumpPrograms() {
            for (int i = 0; i < this.mPrograms.size(); i++) {
                TIFProgramInfo program = this.mPrograms.get(i);
                long time1 = program.getmStartTimeUtcSec();
                long time2 = program.getmEndTimeUtcSec();
                MtkLog.d(TifTimeShiftManager.TAG, "" + time1 + "[" + DateFormat.getTimeFormat(TifTimeShiftManager.this.mContext).format(Long.valueOf(time1)) + "] - " + time2 + "[" + DateFormat.getTimeFormat(TifTimeShiftManager.this.mContext).format(Long.valueOf(time2)) + "]");
            }
        }
    }

    /* access modifiers changed from: private */
    public String getTimeString(long timeMs) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(timeMs));
    }

    @VisibleForTesting
    final class CurrentPositionMediator {
        long mCurrentPositionMs;
        long mSeekRequestTimeMs;

        CurrentPositionMediator() {
        }

        /* access modifiers changed from: package-private */
        public void initialize(long timeMs) {
            MtkLog.d(TifTimeShiftManager.TAG, "CurrentPositionMediator-->initialize: " + TifTimeShiftManager.this.getTimeString(timeMs));
            this.mSeekRequestTimeMs = -1;
            MtkLog.d(TifTimeShiftManager.TAG, "TifTimeShiftManager.CurrentPositionMediator initialize: " + TifTimeShiftManager.this.getTimeString(timeMs));
            if (timeMs >= TifTimeShiftManager.this.mPlayController.getRecordTimeMs() || TifTimeShiftManager.this.mPlayController.getTvView().timeshiftGetCurrentPositionMs() <= TifTimeShiftManager.this.mPlayController.getRecordTimeMs()) {
                this.mCurrentPositionMs = timeMs;
            } else {
                this.mCurrentPositionMs = TifTimeShiftManager.this.mPlayController.getTvView().timeshiftGetCurrentPositionMs();
            }
            TifTimeShiftManager.this.onCurrentPositionChanged();
        }

        /* access modifiers changed from: package-private */
        public void onSeekRequested(long seekTimeMs) {
            MtkLog.d(TifTimeShiftManager.TAG, "CurrentPositionMediator-->onSeekRequested: " + TifTimeShiftManager.this.getTimeString(seekTimeMs));
            this.mSeekRequestTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
            MtkLog.d(TifTimeShiftManager.TAG, "TifTimeShiftManager.CurrentPositionMediator onSeekRequested: " + TifTimeShiftManager.this.getTimeString(seekTimeMs));
            this.mCurrentPositionMs = seekTimeMs;
            TifTimeShiftManager.this.onCurrentPositionChanged();
        }

        /* access modifiers changed from: package-private */
        public void onCurrentPositionChanged(long currentPositionMs) {
            MtkLog.d(TifTimeShiftManager.TAG, "CurrentPositionMediator-->onCurrentPositionChanged: " + TifTimeShiftManager.this.getTimeString(currentPositionMs));
            if (this.mSeekRequestTimeMs == -1) {
                MtkLog.d(TifTimeShiftManager.TAG, "TifTimeShiftManager.CurrentPositionMediator onCurrentPositionChanged: " + TifTimeShiftManager.this.getTimeString(currentPositionMs));
                this.mCurrentPositionMs = currentPositionMs;
                TifTimeShiftManager.this.onCurrentPositionChanged();
                return;
            }
            long currentTimeMs = TifTimeShiftManager.this.getBroadcastTimeInUtcSeconds();
            boolean isTimeout = false;
            boolean isValid = Math.abs(currentPositionMs - this.mCurrentPositionMs) < TifTimeShiftManager.REQUEST_TIMEOUT_MS;
            if (currentTimeMs > this.mSeekRequestTimeMs + TifTimeShiftManager.REQUEST_TIMEOUT_MS) {
                isTimeout = true;
            }
            if (isValid || isTimeout) {
                initialize(currentPositionMs);
                return;
            }
            if (TifTimeShiftManager.this.getPlayStatus() == 1) {
                if (TifTimeShiftManager.this.getPlayDirection() == 0) {
                    this.mCurrentPositionMs += (currentTimeMs - this.mSeekRequestTimeMs) * ((long) TifTimeShiftManager.this.getPlaybackSpeed());
                } else {
                    this.mCurrentPositionMs -= (currentTimeMs - this.mSeekRequestTimeMs) * ((long) TifTimeShiftManager.this.getPlaybackSpeed());
                }
            }
            TifTimeShiftManager.this.onCurrentPositionChanged();
        }
    }

    private static class TimeShiftHandler extends WeakHandler<TifTimeShiftManager> {
        public TimeShiftHandler(TifTimeShiftManager ref) {
            super(ref);
        }

        public void handleMessage(Message msg, @NonNull TifTimeShiftManager timeShiftManager) {
            switch (msg.what) {
                case 1000:
                    timeShiftManager.mPlayController.handleGetCurrentPosition();
                    return;
                case 1001:
                    timeShiftManager.mProgramManager.prefetchPrograms();
                    return;
                default:
                    return;
            }
        }
    }
}
