package com.android.tv.settings.overlay;

import android.app.Fragment;
import android.os.Bundle;
import com.android.tv.settings.SettingsFragmentProvider;
import com.android.tv.settings.overlay.FeatureFactoryImpl;

/* renamed from: com.android.tv.settings.overlay.-$$Lambda$XmADKZBisPCEdw-5XEwNk2G-KZ0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$XmADKZBisPCEdw5XEwNk2GKZ0 implements SettingsFragmentProvider {
    public static final /* synthetic */ $$Lambda$XmADKZBisPCEdw5XEwNk2GKZ0 INSTANCE = new $$Lambda$XmADKZBisPCEdw5XEwNk2GKZ0();

    private /* synthetic */ $$Lambda$XmADKZBisPCEdw5XEwNk2GKZ0() {
    }

    public final Fragment newSettingsFragment(String str, Bundle bundle) {
        return FeatureFactoryImpl.SettingsFragment.newInstance(str, bundle);
    }
}
