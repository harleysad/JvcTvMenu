package com.mediatek.twoworlds.tv;

import android.os.IBinder;
import java.lang.reflect.Method;

public class TVCommonUtil {
    private static final String TAG = "TVCommonUtil";

    private static IBinder getService(String svr) {
        Method getSvr = null;
        try {
            try {
                getSvr = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", new Class[]{String.class});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        if (getSvr == null) {
            return null;
        }
        try {
            return (IBinder) getSvr.invoke((Object) null, new Object[]{svr});
        } catch (Exception e3) {
            e3.printStackTrace();
            return null;
        }
    }
}
