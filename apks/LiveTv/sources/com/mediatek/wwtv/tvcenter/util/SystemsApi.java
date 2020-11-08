package com.mediatek.wwtv.tvcenter.util;

import android.content.Context;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;

public final class SystemsApi {
    private static String TAG = "SystemsApi";
    private static IDreamManager mDreamManager = null;

    public static boolean isUserSetupComplete(Context context) {
        boolean z = false;
        boolean isSetupComplete = false;
        try {
            if (Settings.Secure.getIntForUser(context.getContentResolver(), "user_setup_complete", 0) != 0) {
                z = true;
            }
            isSetupComplete = z;
        } catch (Exception e) {
        }
        MtkLog.v(TAG, "isUserSetupComplete - " + isSetupComplete);
        return isSetupComplete;
    }

    public static void dayDreamAwaken() {
        try {
            if (mDreamManager == null) {
                mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
            }
            if (mDreamManager != null && mDreamManager.isDreaming()) {
                mDreamManager.awaken();
            }
        } catch (Exception e) {
        }
    }
}
