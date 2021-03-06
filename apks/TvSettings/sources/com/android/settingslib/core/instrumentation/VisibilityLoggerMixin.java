package com.android.settingslib.core.instrumentation;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class VisibilityLoggerMixin implements LifecycleObserver {
    public static final String EXTRA_SOURCE_METRICS_CATEGORY = ":settings:source_metrics";
    private static final String TAG = "VisibilityLoggerMixin";
    private final int mMetricsCategory;
    private MetricsFeatureProvider mMetricsFeature;
    private int mSourceMetricsCategory;
    private long mVisibleTimestamp;

    private VisibilityLoggerMixin() {
        this.mSourceMetricsCategory = 0;
        this.mMetricsCategory = 0;
    }

    public VisibilityLoggerMixin(int metricsCategory, MetricsFeatureProvider metricsFeature) {
        this.mSourceMetricsCategory = 0;
        this.mMetricsCategory = metricsCategory;
        this.mMetricsFeature = metricsFeature;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mVisibleTimestamp = SystemClock.elapsedRealtime();
        if (this.mMetricsFeature != null && this.mMetricsCategory != 0) {
            this.mMetricsFeature.visible((Context) null, this.mSourceMetricsCategory, this.mMetricsCategory);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mVisibleTimestamp = 0;
        if (this.mMetricsFeature != null && this.mMetricsCategory != 0) {
            this.mMetricsFeature.hidden((Context) null, this.mMetricsCategory);
        }
    }

    public void setSourceMetricsCategory(Activity activity) {
        Intent intent;
        if (this.mSourceMetricsCategory == 0 && activity != null && (intent = activity.getIntent()) != null) {
            this.mSourceMetricsCategory = intent.getIntExtra(EXTRA_SOURCE_METRICS_CATEGORY, 0);
        }
    }

    public long elapsedTimeSinceVisible() {
        if (this.mVisibleTimestamp == 0) {
            return 0;
        }
        return SystemClock.elapsedRealtime() - this.mVisibleTimestamp;
    }
}
