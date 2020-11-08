package com.mediatek.twoworlds.tv;

import android.util.Log;
import java.io.File;

public final class TvDebugLog {
    private static final String TAG = "TvDebugLog:";
    private static boolean logOnFlag;

    static {
        logOnFlag = false;
        if (new File("/data/mtk_tvapi_debug").exists()) {
            logOnFlag = true;
        } else {
            Log.i(TAG, "not print log");
        }
    }

    protected static void v(String msg) {
        if (logOnFlag) {
            Log.v(TAG, msg);
        }
    }

    protected static void v(String tag, String msg) {
        if (logOnFlag) {
            Log.v(tag, TAG + msg);
        }
    }

    protected static void v(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.v(tag, TAG + msg, tr);
        }
    }

    protected static void d(String msg) {
        if (logOnFlag) {
            Log.d(TAG, msg);
        }
    }

    protected static void d(String tag, String msg) {
        if (logOnFlag) {
            Log.d(tag, TAG + msg);
        }
    }

    protected static void d(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.d(tag, TAG + msg, tr);
        }
    }

    protected static void i(String msg) {
        if (logOnFlag) {
            Log.i(TAG, msg);
        }
    }

    protected static void i(String tag, String msg) {
        if (logOnFlag) {
            Log.i(tag, TAG + msg);
        }
    }

    protected static void i(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.i(tag, TAG + msg, tr);
        }
    }

    protected static void w(String msg) {
        if (logOnFlag) {
            Log.w(TAG, msg);
        }
    }

    protected static void w(String tag, String msg) {
        if (logOnFlag) {
            Log.w(tag, TAG + msg);
        }
    }

    protected static void w(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.w(tag, TAG + msg, tr);
        }
    }

    protected static void e(String msg) {
        if (logOnFlag) {
            Log.e(TAG, msg);
        }
    }

    protected static void e(String tag, String msg) {
        if (logOnFlag) {
            Log.e(tag, TAG + msg);
        }
    }

    protected static void e(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.e(tag, TAG + msg, tr);
        }
    }

    public static void printStackTrace() {
        Throwable tr = new Throwable();
        Log.getStackTraceString(tr);
        tr.printStackTrace();
    }
}
