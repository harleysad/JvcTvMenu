package com.android.tv.settings.core.instrumentation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;

public abstract class InstrumentedActivity extends FragmentActivity implements Instrumentable {
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    protected VisibilityLoggerMixin mVisibilityLoggerMixin;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMetricsFeatureProvider = new MetricsFeatureProvider();
        this.mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), this.mMetricsFeatureProvider);
        getLifecycle().addObserver(this.mVisibilityLoggerMixin);
    }
}
