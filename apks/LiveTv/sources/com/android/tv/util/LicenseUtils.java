package com.android.tv.util;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class LicenseUtils {
    public static final String LICENSE_FILE = "file:///android_asset/licenses.html";
    public static final String RATING_SOURCE_FILE = "file:///android_asset/rating_sources.html";
    private static final File licenseFile = new File(LICENSE_FILE);

    public static boolean hasLicenses(AssetManager am) {
        try {
            InputStream is = am.open("licenses.html");
            if (is != null) {
                $closeResource((Throwable) null, is);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    public static boolean hasRatingAttribution(AssetManager am) {
        try {
            InputStream is = am.open("rating_sources.html");
            if (is != null) {
                $closeResource((Throwable) null, is);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getTextFromResource(Context context, int resourcesIdentifier, long offset, int length) {
        return getTextFromInputStream(context.getApplicationContext().getResources().openRawResource(resourcesIdentifier), offset, length);
    }

    private static String getTextFromInputStream(InputStream stream, long offset, int length) {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream textArray = new ByteArrayOutputStream();
        try {
            if (stream.skip(offset) != -1) {
                int bytesRemaining = length > 0 ? length : Integer.MAX_VALUE;
                while (bytesRemaining > 0) {
                    int read = stream.read(buffer, 0, Math.min(bytesRemaining, buffer.length));
                    int bytes = read;
                    if (read == -1) {
                        break;
                    }
                    textArray.write(buffer, 0, bytes);
                    bytesRemaining -= bytes;
                }
                stream.close();
                try {
                    return textArray.toString("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Unsupported encoding UTF8. This should always be supported.", e);
                }
            } else {
                throw new RuntimeException("InputStream skip error, return -1.");
            }
        } catch (IOException e2) {
            throw new RuntimeException("Failed to read license or metadata text.", e2);
        }
    }

    private LicenseUtils() {
    }
}
