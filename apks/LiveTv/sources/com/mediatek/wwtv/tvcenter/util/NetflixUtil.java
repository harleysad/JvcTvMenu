package com.mediatek.wwtv.tvcenter.util;

import android.app.Activity;
import android.util.Log;
import com.mediatek.twoworlds.tv.SystemProperties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class NetflixUtil {
    private static final String FILE_BOOT_REASON = "/sys/kernel/wakeup_reasons/mstar_resume_reason";
    private static final String TAG = "NetflixUtil";

    public static boolean isNetflixKeyResume() {
        BufferedReader localBufferedReader;
        boolean result = false;
        try {
            FileReader localFileReader = new FileReader(FILE_BOOT_REASON);
            try {
                localBufferedReader = new BufferedReader(localFileReader, 256);
                String oneline = localBufferedReader.readLine();
                if (oneline == null) {
                    Log.d(TAG, "oneline == null");
                    localBufferedReader.close();
                    localFileReader.close();
                    return false;
                }
                Log.d(TAG, "Boot reason line = " + oneline);
                StringTokenizer st = new StringTokenizer(oneline);
                while (true) {
                    if (st.hasMoreTokens()) {
                        if (st.nextToken().equals(new String("reason:")) && st.nextToken().equals(new String("NETFLIX,"))) {
                            result = true;
                            break;
                        }
                    }
                }
                localBufferedReader.close();
                localFileReader.close();
                return result;
            } catch (Throwable th) {
                localFileReader.close();
                throw th;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkNetflixKeyWhenForceLaunchonBoot(Activity activity) {
        if ("1".equals(SystemProperties.get("vendor.mtk.intercept.tv.for.netflix", "0"))) {
            SystemProperties.set("vendor.mtk.intercept.tv.for.netflix", "0");
            Log.d(TAG, "netflix cold boot, finish self.");
            activity.finish();
        }
    }
}
