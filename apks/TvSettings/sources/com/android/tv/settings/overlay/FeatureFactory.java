package com.android.tv.settings.overlay;

import android.content.Context;
import android.support.annotation.Keep;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsFragmentProvider;

@Keep
public abstract class FeatureFactory {
    private static final boolean DEBUG = false;
    protected static final String EXTRA_FRAGMENT_CLASS_NAME = "fragmentClassName";
    protected static final String TAG = "FeatureFactory";
    protected static FeatureFactory sFactory;

    public abstract SettingsFragmentProvider getSettingsFragmentProvider();

    public abstract boolean isTwoPanelLayout();

    public static FeatureFactory getFactory(Context context) {
        if (sFactory != null) {
            return sFactory;
        }
        String clsName = context.getString(R.string.config_featureFactory);
        if (!TextUtils.isEmpty(clsName)) {
            try {
                sFactory = (FeatureFactory) context.getClassLoader().loadClass(clsName).newInstance();
                return sFactory;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new FactoryNotFoundException(e);
            }
        } else {
            throw new UnsupportedOperationException("No feature factory configured");
        }
    }

    public static final class FactoryNotFoundException extends RuntimeException {
        public FactoryNotFoundException(Throwable throwable) {
            super("Unable to create factory. Did you misconfigure Proguard?", throwable);
        }
    }
}
