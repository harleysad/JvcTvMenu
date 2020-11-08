package com.mediatek.wwtv.setting.util;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.Method;

public class ActivityTransitionBitmapHelper {
    private static final String EXTRA_BINDER = "com.mediatek.wwtv.setting.util.extra_binder";
    private static final String TAG = "ActivityTransitionBitmapHelper";
    private static Method sGetBinder;
    private static Method sPutBinder;

    static {
        try {
            sPutBinder = Bundle.class.getDeclaredMethod("putBinder", new Class[]{String.class, IBinder.class});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        try {
            sGetBinder = Bundle.class.getDeclaredMethod("getBinder", new Class[]{String.class});
        } catch (Exception e2) {
            Log.e(TAG, e2.getMessage());
        }
    }

    public static Bitmap getBitmapFromBinderBundle(Bundle bundle) {
        return null;
    }

    public static Bundle bitmapAsBinderBundle(Bitmap bitmap) {
        return new Bundle();
    }
}
