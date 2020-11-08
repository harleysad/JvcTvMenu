package com.mediatek.wwtv.tvcenter.util;

import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TVAsyncExecutor implements Executor {
    private static final int CORE_POOL_SIZE = 10;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static TVAsyncExecutor EXECUTOR = null;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = 20;
    static boolean debug = false;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "TVAsyncExecutor #" + this.mCount.getAndIncrement());
        }
    };
    private final ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(10, 20, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(), sThreadFactory);

    public static synchronized TVAsyncExecutor getInstance() {
        TVAsyncExecutor tVAsyncExecutor;
        synchronized (TVAsyncExecutor.class) {
            if (EXECUTOR == null) {
                EXECUTOR = new TVAsyncExecutor();
            }
            tVAsyncExecutor = EXECUTOR;
        }
        return tVAsyncExecutor;
    }

    private TVAsyncExecutor() {
        this.mExecutor.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable runnable) {
        if (debug) {
            Log.d("TVAsyncExecutor", this.mExecutor.toString());
        }
        this.mExecutor.execute(runnable);
    }

    public static void setDebug(boolean dg) {
        debug = dg;
    }
}
