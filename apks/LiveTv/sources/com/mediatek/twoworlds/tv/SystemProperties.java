package com.mediatek.twoworlds.tv;

import android.text.TextUtils;
import android.util.Log;

public final class SystemProperties {
    private static final String TAG = "SystemProperties";

    public static String get(String key) {
        return get(key, "");
    }

    public static String get(String key, String def) {
        try {
            String value = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{key});
            TvDebugLog.d(TAG, "get " + key + ", " + def);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
            return def;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e);
        }
    }

    public static int getInt(String key, int def) {
        try {
            int ret = ((Integer) Class.forName("android.os.SystemProperties").getDeclaredMethod("getInt", new Class[]{String.class, Integer.TYPE}).invoke((Object) null, new Object[]{key, Integer.valueOf(def)})).intValue();
            TvDebugLog.d(TAG, "getInt " + key + ", " + ret);
            return ret;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e);
            return def;
        }
    }

    public static long getLong(String key, long def) {
        try {
            long ret = ((Long) Class.forName("android.os.SystemProperties").getDeclaredMethod("getLong", new Class[]{String.class, Long.TYPE}).invoke((Object) null, new Object[]{key, Long.valueOf(def)})).longValue();
            TvDebugLog.d(TAG, "getLong " + key + ", " + ret);
            return ret;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e);
            return def;
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        try {
            boolean ret = ((Boolean) Class.forName("android.os.SystemProperties").getDeclaredMethod("getBoolean", new Class[]{String.class, Boolean.TYPE}).invoke((Object) null, new Object[]{key, Boolean.valueOf(def)})).booleanValue();
            TvDebugLog.d(TAG, "getLong " + key + ", " + ret);
            return ret;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e);
            return def;
        }
    }

    public static void set(String key, String val) {
        TvDebugLog.d(TAG, "set " + key + ", " + val);
        MtkTvUtilBase.setSystemProperties(key, val);
    }
}
