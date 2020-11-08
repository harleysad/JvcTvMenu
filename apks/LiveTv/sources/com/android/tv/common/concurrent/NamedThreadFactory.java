package com.android.tv.common.concurrent;

import android.support.annotation.NonNull;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger mCount = new AtomicInteger(0);
    private final ThreadFactory mDefaultThreadFactory = Executors.defaultThreadFactory();
    private final String mPrefix;

    public NamedThreadFactory(String prefix) {
        this.mPrefix = prefix + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING;
    }

    public Thread newThread(@NonNull Runnable runnable) {
        Thread thread = this.mDefaultThreadFactory.newThread(runnable);
        thread.setName(this.mPrefix + this.mCount.getAndIncrement());
        return thread;
    }

    public boolean namedWithPrefix(Thread thread) {
        return thread.getName().startsWith(this.mPrefix);
    }
}
