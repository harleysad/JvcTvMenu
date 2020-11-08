package com.mediatek.wwtv.tvcenter.util;

import android.app.Instrumentation;

public class InstrumentationHandler {
    private static InstrumentationHandler mInstrumentationHandler = null;
    /* access modifiers changed from: private */
    public Instrumentation mInstrumentation;

    private InstrumentationHandler() {
        this.mInstrumentation = null;
        this.mInstrumentation = new Instrumentation();
    }

    public static synchronized InstrumentationHandler getInstance() {
        InstrumentationHandler instrumentationHandler;
        synchronized (InstrumentationHandler.class) {
            if (mInstrumentationHandler == null) {
                mInstrumentationHandler = new InstrumentationHandler();
            }
            instrumentationHandler = mInstrumentationHandler;
        }
        return instrumentationHandler;
    }

    public void sendKeyDownUpSync(final int key) {
        TVAsyncExecutor.getInstance().execute(new Runnable() {
            public void run() {
                try {
                    InstrumentationHandler.this.mInstrumentation.sendKeyDownUpSync(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
