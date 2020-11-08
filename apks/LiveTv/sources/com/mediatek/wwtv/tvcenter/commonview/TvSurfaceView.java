package com.mediatek.wwtv.tvcenter.commonview;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.PlaybackParams;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.wwtv.tvcenter.TvSingletons;
import com.mediatek.wwtv.tvcenter.dvr.controller.StateDvrPlayback;
import com.mediatek.wwtv.tvcenter.nav.input.AbstractInput;
import com.mediatek.wwtv.tvcenter.nav.input.ISource;
import com.mediatek.wwtv.tvcenter.nav.input.InputUtil;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentStatusListener;
import com.mediatek.wwtv.tvcenter.nav.util.ComponentsManager;
import com.mediatek.wwtv.tvcenter.nav.util.OnLoadingListener;
import com.mediatek.wwtv.tvcenter.nav.view.BannerView;
import com.mediatek.wwtv.tvcenter.nav.view.common.NavBasic;
import com.mediatek.wwtv.tvcenter.util.AudioFocusManager;
import com.mediatek.wwtv.tvcenter.util.CommonIntegration;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import com.mediatek.wwtv.tvcenter.util.TVAsyncExecutor;
import com.mediatek.wwtv.tvcenter.util.tif.TIFChannelManager;
import com.mediatek.wwtv.tvcenter.util.tif.TIFFunctionUtil;
import com.mediatek.wwtv.tvcenter.util.tif.TvInputCallbackMgr;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;

public class TvSurfaceView extends TvView implements TvInputCallbackMgr.TimeshiftCallback {
    public static final int BLOCK_STATE_LOCK = 0;
    public static final int BLOCK_STATE_NONE = -1;
    public static final int BLOCK_STATE_UNLOCK = 1;
    private static final int CAPTION_DEFAULT = 0;
    private static final int CAPTION_DISABLED = 2;
    private static final int CAPTION_ENABLED = 1;
    private static final boolean DEBUG = true;
    private static final long INVALID_TIME = -1;
    private static final String SPNAME = "CHMODE";
    private static final String TAG = "TvSurfaceView";
    public static final int TIME_SHIFT_STATE_FAST_FORWARD = 4;
    public static final int TIME_SHIFT_STATE_NONE = 0;
    public static final int TIME_SHIFT_STATE_PAUSE = 2;
    public static final int TIME_SHIFT_STATE_PLAY = 1;
    public static final int TIME_SHIFT_STATE_REWIND = 3;
    private boolean isStart;
    private int mBlockContent;
    private TvBlockView mBlockView;
    private int mCaptionEnabled;
    private final Context mContext;
    private boolean mIsHandleEvent;
    /* access modifiers changed from: private */
    public String mLocalInputId;
    private OnLoadingListener mOnLoadingListener;
    private SaveValue mSaveValue;
    private TIFChannelManager mTIFChannelManager;
    private boolean mTimeShiftAvailable;
    /* access modifiers changed from: private */
    public long mTimeShiftCurrentPositionMs;
    /* access modifiers changed from: private */
    public TimeShiftListener mTimeShiftListener;
    private int mTimeShiftState;
    private Uri mUrl;

    @FunctionalInterface
    public interface BlockChecker {
        void check();
    }

    public static abstract class OnScreenBlockingChangedListener {
        public abstract void onScreenBlockingChanged(boolean z);
    }

    public static abstract class TimeShiftListener {
        public abstract void onAvailabilityChanged();

        public abstract void onChannelChanged(String str, Uri uri);

        public abstract void onPlayStatusChanged(int i);

        public abstract void onRecordStartTimeChanged(long j);

        public abstract void onSpeechChange(float f);

        public abstract void onTimeshiftRecordStart(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeShiftState {
    }

    public void setStart(boolean flag) {
        this.isStart = flag;
        MtkLog.d(TAG, "setStart:" + this.isStart);
    }

    public boolean isStart() {
        MtkLog.d(TAG, "isStart:" + this.isStart);
        return this.isStart;
    }

    public TvSurfaceView(Context context) {
        super(context);
        this.mIsHandleEvent = false;
        this.mLocalInputId = null;
        this.mUrl = null;
        this.mTimeShiftState = 0;
        this.mTimeShiftCurrentPositionMs = -1;
        this.mBlockView = null;
        this.mBlockContent = -1;
        this.mContext = context;
    }

    public TvSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsHandleEvent = false;
        this.mLocalInputId = null;
        this.mUrl = null;
        this.mTimeShiftState = 0;
        this.mTimeShiftCurrentPositionMs = -1;
        this.mBlockView = null;
        this.mBlockContent = -1;
        this.mContext = context;
        this.mSaveValue = SaveValue.getInstance(context);
        this.mTIFChannelManager = TIFChannelManager.getInstance(this.mContext);
        if (context instanceof OnLoadingListener) {
            this.mOnLoadingListener = (OnLoadingListener) context;
        }
    }

    public void setBlockView(TvBlockView view) {
        this.mBlockView = view;
        this.mTIFChannelManager.addBlockCheck(new BlockChecker() {
            public final void check() {
                TvSurfaceView.this.CheckBlockFor3rd();
            }
        });
    }

    public void setHandleEvent(boolean isHandleEvent) {
        this.mIsHandleEvent = isHandleEvent;
    }

    public void tune(String inputId, Uri channelUri, Bundle params) {
        long mid = -1;
        if (TextUtils.isEmpty(inputId)) {
            Log.e(TAG, "inputId can not empty!!!");
            return;
        }
        if (this.mOnLoadingListener != null) {
            this.mOnLoadingListener.onHideLoading();
        }
        if (!SystemProperties.get("vendor.mtk.livetv.ready", "0").equals("1")) {
            MtkLog.d(TAG, "tune,SystemProperties.set(vendor.mtk.livetv.ready,1)");
            SystemProperties.set("vendor.mtk.livetv.ready", "1");
            MtkLog.i(TAG, "tune,SystemProperties.get(vendor.mtk.livetv.ready) = " + SystemProperties.get("vendor.mtk.livetv.ready"));
        }
        TvInputCallbackMgr.getInstance(this.mContext).setTimeshiftCallback(this);
        this.mTimeShiftCurrentPositionMs = -1;
        setTimeShiftPositionCallback((TvView.TimeShiftPositionCallback) null);
        boolean z = false;
        setTimeShiftAvailable(false);
        Log.d(TAG, "tune inputId : " + inputId);
        if (!inputId.contains("com.mediatek.tvinput/.tuner.TunerInputService")) {
            try {
                mid = ContentUris.parseId(channelUri);
                if (mid != -1) {
                    this.mSaveValue.saveValue(TIFFunctionUtil.current3rdMId, (int) mid);
                    MtkLog.d(TAG, "tune inputId mTIFChannelInfo.mId: " + mid);
                }
            } catch (Exception e) {
                MtkLog.d(TAG, "tune inputId tv e.printStackTrace();");
            }
        } else if (!CommonIntegration.getInstance().isCurrentSourceATVforEuPA()) {
            MtkLog.d(TAG, "tune inputId tv ");
            this.mSaveValue.saveValue(TIFFunctionUtil.current3rdMId, -1);
        }
        MtkLog.d(TAG, "set CHChanging is true.");
        CommonIntegration.getInstance().setCHChanging(true);
        BannerView bannerView = (BannerView) ComponentsManager.getInstance().getComponentById(NavBasic.NAV_COMP_ID_BANNER);
        if (bannerView != null) {
            MtkLog.d(TAG, "reset banner info before tune.");
            bannerView.reset();
        }
        if (mid == -1) {
            AudioFocusManager.getInstance(this.mContext).requestAudioFocus();
        }
        super.tune(inputId, channelUri, params);
        this.mLocalInputId = inputId;
        this.mUrl = channelUri;
        this.mBlockContent = -1;
        if (mid != -1) {
            z = true;
        }
        checkBlock(z);
        MtkLog.d(TAG, inputId + ", mid = " + mid);
        if (!isStart()) {
            TvSingletons.getSingletons().getInputSourceManager().saveWorldInputType();
        }
        Context context = this.mContext;
        Context context2 = this.mContext;
        setStart(((PowerManager) context.getSystemService("power")).isInteractive());
    }

    public void reset() {
        super.reset();
        setStart(false);
        MtkLog.d(TAG, "reset()");
    }

    public void timeShiftPlay(String inputId, Uri recordedProgramUri) {
        MtkLog.e(TAG, "timeShiftPlay, setTimeShiftPositionCallback");
        setTimeShiftPositionCallback(new TvView.TimeShiftPositionCallback() {
            public void onTimeShiftStartPositionChanged(String inputId, long timeMs) {
            }

            public void onTimeShiftCurrentPositionChanged(String inputId, long timeMs) {
                MtkLog.e(TvSurfaceView.TAG, "onTimeShiftCurrentPositionChanged, " + timeMs);
                long unused = TvSurfaceView.this.mTimeShiftCurrentPositionMs = timeMs;
            }
        });
        super.timeShiftPlay(inputId, recordedProgramUri);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mIsHandleEvent) {
            return super.onTouchEvent(event);
        }
        MtkLog.e(TAG, "onTouchEvent, " + event);
        switch (event.getAction()) {
            case 0:
                if (this.mLocalInputId != null) {
                    try {
                        for (TvInputInfo input : ((TvInputManager) this.mContext.getSystemService("tv_input")).getTvInputList()) {
                            if (!input.getId().equals(this.mLocalInputId) || input.createSettingsIntent() == null) {
                                MtkLog.e(TAG, "dispatchGenericMotionEvent, input.getId(): " + input.getId() + ", mLocalInputId: " + this.mLocalInputId);
                            } else {
                                Intent intent = input.createSettingsIntent();
                                MtkLog.e(TAG, "dispatchGenericMotionEvent, intent" + intent);
                                this.mContext.startActivity(intent);
                            }
                        }
                        break;
                    } catch (Exception e) {
                        break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setCaptionEnabled(boolean enabled) {
        MtkLog.e(TAG, "setCaptionEnabled:" + enabled);
        this.mCaptionEnabled = enabled ? 1 : 2;
        super.setCaptionEnabled(enabled);
    }

    public boolean isCaptionEnable() {
        return 2 != this.mCaptionEnabled;
    }

    public void setTimeShiftListener(TimeShiftListener listener) {
        this.mTimeShiftListener = listener;
    }

    private void setTimeShiftAvailable(boolean isTimeShiftAvailable) {
        MtkLog.i(TAG, "setTimeShiftAvailable mTimeShiftAvailable: " + this.mTimeShiftAvailable + ", isTimeShiftAvailable: " + isTimeShiftAvailable);
        if (this.mTimeShiftAvailable != isTimeShiftAvailable) {
            this.mTimeShiftAvailable = isTimeShiftAvailable;
            if (isTimeShiftAvailable) {
                MtkLog.i(TAG, "setTimeShiftAvailable isTimeShiftAvailable setTimeShiftPositionCallback()");
                setTimeShiftPositionCallback(new TvView.TimeShiftPositionCallback() {
                    public void onTimeShiftStartPositionChanged(String inputId, long timeMs) {
                        if (TvSurfaceView.this.mTimeShiftListener != null && TvSurfaceView.this.mLocalInputId.equals(inputId)) {
                            MtkLog.d(TvSurfaceView.TAG, "onTimeShiftStartPositionChanged timeMs: " + timeMs);
                            TvSurfaceView.this.mTimeShiftListener.onRecordStartTimeChanged(timeMs);
                        }
                    }

                    public void onTimeShiftCurrentPositionChanged(String inputId, long timeMs) {
                        MtkLog.d(TvSurfaceView.TAG, "onTimeShiftCurrentPositionChanged timeMs: " + timeMs);
                        long unused = TvSurfaceView.this.mTimeShiftCurrentPositionMs = timeMs;
                    }
                });
            } else {
                StateDvrPlayback stateDvrPlayback = StateDvrPlayback.getInstance();
                if (stateDvrPlayback == null || (stateDvrPlayback != null && !stateDvrPlayback.isRunning())) {
                    setTimeShiftPositionCallback((TvView.TimeShiftPositionCallback) null);
                    MtkLog.i(TAG, "setTimeShiftAvailable setTimeShiftPositionCallback(null)== timeshift available");
                }
            }
            if (this.mTimeShiftListener != null) {
                this.mTimeShiftListener.onAvailabilityChanged();
            }
        }
    }

    public boolean isTimeShiftAvailable() {
        return this.mTimeShiftAvailable;
    }

    public int getTimeShiftState() {
        return this.mTimeShiftState;
    }

    public void timeshiftPlayEx() {
        if (isTimeShiftAvailable()) {
            MtkLog.d(TAG, "timeshiftPlayEx mTimeShiftState=" + this.mTimeShiftState);
            if (this.mTimeShiftState != 1) {
                MtkLog.d(TAG, "timeShiftResume");
                timeShiftResume();
                return;
            }
            return;
        }
        throw new IllegalStateException("Time-shift is not supported for the current channel");
    }

    public void timeshiftPauseEx() {
        if (isTimeShiftAvailable()) {
            MtkLog.d(TAG, "timeshiftPauseEx mTimeShiftState=" + this.mTimeShiftState);
            if (this.mTimeShiftState != 2) {
                MtkLog.d(TAG, "timeShiftPause");
                timeShiftPause();
                return;
            }
            return;
        }
        throw new IllegalStateException("Time-shift is not supported for the current channel");
    }

    public void timeshiftRewind(int speed) {
        if (!isTimeShiftAvailable()) {
            throw new IllegalStateException("Time-shift is not supported for the current channel");
        } else if (speed > 0) {
            MtkLog.d(TAG, "timeshiftRewind");
            this.mTimeShiftState = 3;
            PlaybackParams params = new PlaybackParams();
            params.setSpeed((float) (speed * -1));
            timeShiftSetPlaybackParams(params);
        } else {
            throw new IllegalArgumentException("The speed should be a positive integer.");
        }
    }

    public void timeshiftFastForward(int speed) {
        if (!isTimeShiftAvailable()) {
            throw new IllegalStateException("Time-shift is not supported for the current channel");
        } else if (speed > 0) {
            MtkLog.d(TAG, "timeshiftFastForward");
            this.mTimeShiftState = 4;
            PlaybackParams params = new PlaybackParams();
            params.setSpeed((float) speed);
            timeShiftSetPlaybackParams(params);
        } else {
            throw new IllegalArgumentException("The speed should be a positive integer.");
        }
    }

    public void timeshiftSeekTo(long timeMs) {
        if (isTimeShiftAvailable()) {
            timeShiftSeekTo(timeMs);
            return;
        }
        throw new IllegalStateException("Time-shift is not supported for the current channel");
    }

    public long timeshiftGetCurrentPositionMs() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        MtkLog.d(TAG, "timeshiftGetCurrentPositionMs: current position =" + format.format(Long.valueOf(this.mTimeShiftCurrentPositionMs)));
        return this.mTimeShiftCurrentPositionMs;
    }

    public void onChannelChanged(String inputId, Uri channelUri) {
        MtkLog.w(TAG, "onChannelChanged, [" + this.mUrl + "][" + channelUri + "]");
        if (!(channelUri == null || this.mUrl == null || this.mTimeShiftAvailable || channelUri.compareTo(this.mUrl) == 0)) {
            setTimeShiftAvailable(false);
        }
        if (this.mTimeShiftListener != null) {
            this.mTimeShiftListener.onChannelChanged(inputId, channelUri);
        }
    }

    public void onTimeShiftStatusChanged(String inputId, int status) {
        StringBuilder sb = new StringBuilder();
        sb.append("onTimeShiftStatusChanged timeshiftAvailable: ");
        boolean z = false;
        sb.append(status == 3);
        MtkLog.w(TAG, sb.toString());
        if (status == 3) {
            z = true;
        }
        setTimeShiftAvailable(z);
    }

    public void onEvent(String inputId, String eventType, Bundle eventArgs) {
        if (this.mTimeShiftListener == null) {
            MtkLog.d(TAG, "onEvent--> mTimeShiftListener is null.");
        } else if (eventType.equals("session_event_timeshift_speedupdate")) {
            float speed = eventArgs.getFloat("SpeedUpdate");
            if (this.mTimeShiftListener != null) {
                this.mTimeShiftListener.onSpeechChange(speed);
            }
        } else if (eventType.equals("session_event_timeshift_playbackstatusupdate")) {
            int status = eventArgs.getInt("playbackstatusupdate");
            MtkLog.d(TAG, "onEvent--> timeshift playback status: " + status);
            this.mTimeShiftListener.onPlayStatusChanged(status);
        } else if (eventType.equals(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_TIMESHIFT_RECORD_STARTED)) {
            this.mTimeShiftListener.onTimeshiftRecordStart(true);
        } else if (eventType.equals(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_TIMESHIFT_RECORD_NOT_STARTED)) {
            this.mTimeShiftListener.onTimeshiftRecordStart(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Rect rect = new Rect();
        super.onLayout(changed, left, top, right, bottom);
        getGlobalVisibleRect(rect);
        MtkLog.d(TAG, "onLayout," + left + "," + top + "," + right + "," + bottom + "," + changed + ",getGlobalVisibleRect " + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
        TvInputCallbackMgr.getInstance(this.mContext).handleLayoutChanged(rect.right - rect.left, rect.bottom - rect.top);
    }

    public void unblockContent(TvContentRating unblockedRating) {
        if (unblockedRating != null) {
            super.unblockContent(unblockedRating);
        }
        MtkLog.d(TAG, "unblockContent = " + unblockedRating);
        synchronized (TvSurfaceView.class) {
            this.mBlockContent = 1;
            if (this.mBlockView != null) {
                this.mBlockView.setVisibility(8, 2);
                if (!this.mBlockView.isBlock()) {
                    setStreamVolume(1.0f);
                }
            }
        }
    }

    public void blockContent() {
        synchronized (TvSurfaceView.class) {
            this.mBlockContent = 0;
            if (this.mBlockView != null) {
                this.mBlockView.setVisibility(0, 2);
                setStreamVolume(0.0f);
            }
        }
        MtkLog.d(TAG, "blockContent = " + this.mBlockContent + this.mBlockView);
    }

    public boolean isContentBlock(boolean is3rdChannel) {
        if (is3rdChannel) {
            AbstractInput input = InputUtil.getInputByType(0);
            if (input == null && (input = InputUtil.getInputByType(ISource.TYPE_DTV)) == null) {
                Log.d(TAG, "input list wrong!");
                return false;
            } else if (this.mBlockContent == 1 || !input.isBlock()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean checkBlock(boolean is3rdChannel) {
        MtkLog.d(TAG, "checkBlock = " + is3rdChannel + this.mBlockContent);
        if (is3rdChannel) {
            TVAsyncExecutor.getInstance().execute(new Runnable() {
                public void run() {
                    if (TvSurfaceView.this.isContentBlock(true)) {
                        ComponentStatusListener.getInstance().updateStatus(13, 0);
                    }
                }
            });
        } else {
            synchronized (TvSurfaceView.class) {
                this.mBlockContent = 1;
                if (this.mBlockView != null) {
                    this.mBlockView.setVisibility(8, 2);
                }
            }
        }
        if (this.mBlockContent == 0) {
            return true;
        }
        return false;
    }

    public Uri getCurrentChannelUrl() {
        return this.mUrl;
    }

    public long getCurrentChannelId() {
        MtkLog.d(TAG, "getCurrentChannelId,mUrl " + this.mUrl);
        if (this.mUrl != null) {
            return ContentUris.parseId(this.mUrl);
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public void CheckBlockFor3rd() {
        if (this.mBlockView != null) {
            this.mBlockView.post(new Runnable() {
                public final void run() {
                    TvSurfaceView.this.checkBlock(CommonIntegration.getInstance().is3rdTVSource());
                }
            });
        }
    }
}
