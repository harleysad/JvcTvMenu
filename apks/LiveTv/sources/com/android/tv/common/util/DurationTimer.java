package com.android.tv.common.util;

import android.os.SystemClock;
import android.util.Log;

public final class DurationTimer {
    private static final String TAG = "DurationTimer";
    public static final long TIME_NOT_SET = -1;
    private boolean mLogEngOnly;
    private long mStartTimeMs = -1;
    private String mTag = TAG;

    public DurationTimer() {
    }

    public DurationTimer(String tag, boolean logEngOnly) {
        this.mTag = tag;
        this.mLogEngOnly = logEngOnly;
    }

    public boolean isRunning() {
        return this.mStartTimeMs != -1;
    }

    public void start() {
        this.mStartTimeMs = SystemClock.elapsedRealtime();
    }

    public boolean isStarted() {
        return this.mStartTimeMs != -1;
    }

    public long getDuration() {
        if (isRunning()) {
            return SystemClock.elapsedRealtime() - this.mStartTimeMs;
        }
        return -1;
    }

    public long reset() {
        long duration = getDuration();
        this.mStartTimeMs = -1;
        return duration;
    }

    public void log(String message) {
        if (isRunning() && !this.mLogEngOnly) {
            String str = this.mTag;
            Log.i(str, message + " : " + getDuration() + "ms");
        }
    }
}
