package com.android.tv.license;

import android.content.Context;
import android.support.annotation.RawRes;
import com.mediatek.wwtv.tvcenter.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public final class Licenses {
    public static final String TAG = "Licenses";

    public static boolean hasLicenses(Context context) {
        return !getTextFromResource(context.getApplicationContext(), R.raw.third_party_license_metadata, 0, -1).isEmpty();
    }

    public static ArrayList<License> getLicenses(Context context) {
        return getLicenseListFromMetadata(getTextFromResource(context.getApplicationContext(), R.raw.third_party_license_metadata, 0, -1), "");
    }

    private static ArrayList<License> getLicenseListFromMetadata(String metadata, String filePath) {
        String[] entries = metadata.split("\n");
        ArrayList<License> licenses = new ArrayList<>(entries.length);
        for (String entry : entries) {
            int delimiter = entry.indexOf(32);
            String[] licenseLocation = entry.substring(0, delimiter).split(":");
            licenses.add(License.create(entry.substring(delimiter + 1), Long.parseLong(licenseLocation[0]), Integer.parseInt(licenseLocation[1]), filePath));
        }
        Collections.sort(licenses);
        return licenses;
    }

    public static String getLicenseText(Context context, License license) {
        return getTextFromResource(context, R.raw.third_party_licenses, license.getLicenseOffset(), license.getLicenseLength());
    }

    private static String getTextFromResource(Context context, @RawRes int resourcesIdentifier, long offset, int length) {
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
}
