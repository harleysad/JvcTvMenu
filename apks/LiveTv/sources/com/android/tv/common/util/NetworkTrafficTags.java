package com.android.tv.common.util;

import android.net.TrafficStats;
import android.support.annotation.NonNull;
import com.android.tv.common.util.NetworkTrafficTags;
import java.util.concurrent.Executor;

public final class NetworkTrafficTags {
    public static final int DEFAULT_LIVE_CHANNELS = 1;
    public static final int EPG_FETCH = 4;
    public static final int HDHOMERUN = 3;
    public static final int LOGO_FETCHER = 2;

    public static class TrafficStatsTaggingExecutor implements Executor {
        private final Executor delegateExecutor;
        private final int tag;

        public TrafficStatsTaggingExecutor(Executor delegateExecutor2, int tag2) {
            this.delegateExecutor = delegateExecutor2;
            this.tag = tag2;
        }

        public void execute(@NonNull Runnable command) {
            this.delegateExecutor.execute(new Runnable(command) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NetworkTrafficTags.TrafficStatsTaggingExecutor.lambda$execute$0(NetworkTrafficTags.TrafficStatsTaggingExecutor.this, this.f$1);
                }
            });
        }

        public static /* synthetic */ void lambda$execute$0(TrafficStatsTaggingExecutor trafficStatsTaggingExecutor, Runnable command) {
            TrafficStats.setThreadStatsTag(trafficStatsTaggingExecutor.tag);
            try {
                command.run();
            } finally {
                TrafficStats.clearThreadStatsTag();
            }
        }
    }

    private NetworkTrafficTags() {
    }
}
