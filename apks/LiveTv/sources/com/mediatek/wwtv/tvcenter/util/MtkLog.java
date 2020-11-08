package com.mediatek.wwtv.tvcenter.util;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.LogPrinter;
import com.mediatek.twoworlds.tv.SystemProperties;
import java.io.File;

public final class MtkLog {
    private static final String INTERNAL_LOG = "[MtkLog][COMMON]";
    private static final String LOG = "[MtkLog]";
    private static boolean dumpStarted = false;
    public static boolean logOnFlag = false;
    static int loglevel = -1;

    static {
        init();
    }

    private MtkLog() {
    }

    private static void init() {
        if (new File("/data/tkui.print").exists()) {
            logOnFlag = true;
            Log.i(MtkLog.class.getSimpleName(), "[MtkLog]print log");
            return;
        }
        Log.i(MtkLog.class.getSimpleName(), "[MtkLog]not print log");
    }

    public static void v(String msg) {
        if (logOnFlag) {
            Log.v(INTERNAL_LOG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (logOnFlag) {
            Log.v(tag, LOG + msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.v(tag, LOG + msg, tr);
        }
    }

    public static void d(String msg) {
        if (logOnFlag) {
            Log.d(INTERNAL_LOG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (logOnFlag) {
            Log.d(tag, LOG + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.d(tag, LOG + msg, tr);
        }
    }

    public static void i(String msg) {
        if (logOnFlag) {
            Log.i(INTERNAL_LOG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (logOnFlag) {
            Log.i(tag, LOG + msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.i(tag, LOG + msg, tr);
        }
    }

    public static void w(String msg) {
        if (logOnFlag) {
            Log.w(INTERNAL_LOG, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (logOnFlag) {
            Log.w(tag, LOG + msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.w(tag, LOG + msg, tr);
        }
    }

    public static void e(String msg) {
        if (logOnFlag) {
            Log.e(INTERNAL_LOG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (logOnFlag) {
            Log.e(tag, LOG + msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (logOnFlag) {
            Log.e(tag, LOG + msg, tr);
        }
    }

    public static void printStackTrace() {
        if (logOnFlag) {
            printStackTraceEx();
        }
    }

    public static void printStackTraceEx() {
        Throwable tr = new Throwable();
        Log.getStackTraceString(tr);
        tr.printStackTrace();
    }

    public static void printf(String info) {
        Throwable tr = new Throwable();
        StackTraceElement[] elems = tr.getStackTrace();
        if (loglevel == -1) {
            if (new File("/data/printAll").exists()) {
                loglevel = 9;
            } else if (new File("/data/printMiddle").exists()) {
                loglevel = 5;
            } else {
                loglevel = 0;
            }
        }
        if (loglevel == 0) {
            if (elems == null || elems.length <= 1) {
                Log.getStackTraceString(tr);
                tr.printStackTrace();
                return;
            }
            String fileName = elems[1].getFileName();
            Log.e(fileName, elems[1].getMethodName() + "," + elems[1].getLineNumber() + ". " + info);
        } else if (5 != loglevel) {
            Log.getStackTraceString(tr);
            tr.printStackTrace();
        } else if (elems == null || elems.length <= 2) {
            Log.getStackTraceString(tr);
            tr.printStackTrace();
        } else {
            String fileName2 = elems[1].getFileName();
            Log.e(fileName2, elems[1].getMethodName() + "," + elems[1].getLineNumber() + ". " + info);
            String fileName3 = elems[2].getFileName();
            Log.e(fileName3, elems[2].getMethodName() + "," + elems[2].getLineNumber() + ". " + info);
        }
    }

    public static void dumpMessageQueueOnce(Handler handler) {
        handler.dump(new LogPrinter(3, "[TV]"), "[LP]");
    }

    public static synchronized void dumpMessageQueue(Handler handler, long delayMillis) {
        synchronized (MtkLog.class) {
            final Handler hd = handler;
            final long millis = delayMillis;
            if (!dumpStarted) {
                dumpStarted = true;
                final long j = delayMillis;
                TVAsyncExecutor.getInstance().execute(new Runnable() {
                    public void run() {
                        long seconds = 0;
                        int count = 0;
                        while (count < 100) {
                            seconds = (long) SystemProperties.getInt("vendor.mtk.livetv.msgq.debug", 0);
                            if (seconds > 0) {
                                break;
                            }
                            try {
                                Thread.sleep(millis);
                            } catch (Exception e) {
                            }
                            count++;
                        }
                        if (count < 100) {
                            if (1000 * seconds <= j) {
                                seconds = j;
                            }
                            long realMillis = seconds;
                            while (true) {
                                MtkLog.dumpMessageQueueOnce(hd);
                                Log.d("[HT]", "UTTIME:" + SystemClock.uptimeMillis());
                                try {
                                    Thread.sleep(realMillis);
                                } catch (Exception e2) {
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
