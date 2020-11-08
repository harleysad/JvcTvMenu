package com.android.tv.license;

import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;

public final class LicenseUtils {
    public static final String RATING_SOURCE_FILE = "file:///android_asset/rating_sources.html";

    public static boolean hasRatingAttribution(AssetManager am) {
        try {
            InputStream is = am.open("rating_sources.html");
            if (is != null) {
                is.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private LicenseUtils() {
    }
}
