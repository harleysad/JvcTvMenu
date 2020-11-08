package android.support.v4.os;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;

public final class ConfigurationCompat {
    private ConfigurationCompat() {
    }

    @NonNull
    public static LocaleListCompat getLocales(@NonNull Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 24) {
            return LocaleListCompat.wrap(configuration.getLocales());
        }
        return LocaleListCompat.create(configuration.locale);
    }
}
