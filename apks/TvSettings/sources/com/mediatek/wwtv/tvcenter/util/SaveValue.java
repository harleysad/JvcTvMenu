package com.mediatek.wwtv.tvcenter.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class SaveValue {
    private static final String AUTHORITY = "com.mediatek.tv.internal.data";
    public static final String GLOBAL_PROVIDER_ID = "global_value";
    public static final Uri GLOBAL_PROVIDER_URI_URI = Uri.parse("content://com.mediatek.tv.internal.data/global_value");
    public static final String GLOBAL_VALUE_KEY = "key";
    public static final String GLOBAL_VALUE_STORED = "stored";
    public static final String GLOBAL_VALUE_VALUE = "value";
    public static final String NAME = "mediatek_pref";
    private static final String TAG = "SaveValue";
    private static Map<String, Boolean> mBooleanMap = new HashMap();
    /* access modifiers changed from: private */
    public static Context mContext = null;
    /* access modifiers changed from: private */
    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            Log.d(SaveValue.TAG, "handleMessage++++try=" + msg.arg1);
            if (msg.arg1 <= 3) {
                ContentValues values = (ContentValues) msg.obj;
                Log.d(SaveValue.TAG, "ContentValues=" + values.get(SaveValue.GLOBAL_VALUE_VALUE));
                int tryCount = msg.arg1;
                try {
                    SaveValue.mContext.getContentResolver().insert(SaveValue.GLOBAL_PROVIDER_URI_URI, values);
                } catch (Exception e) {
                    int tryCount2 = tryCount + 1;
                    Message msg1 = Message.obtain();
                    msg1.arg1 = tryCount2;
                    msg1.obj = values;
                    SaveValue.mHandler.sendMessageDelayed(msg1, 2000);
                    Log.d(SaveValue.TAG, "handle writeWorldStringValue+" + tryCount2);
                }
            }
        }
    };
    private static Map<String, Integer> mIntegerMap = new HashMap();
    public static SaveValue save_data;
    private SharedPreferences mSharedPreferences;

    private SaveValue(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(NAME, 0);
    }

    public static synchronized SaveValue getInstance(Context context) {
        SaveValue saveValue;
        synchronized (SaveValue.class) {
            if (save_data == null) {
                save_data = new SaveValue(context);
            }
            saveValue = save_data;
        }
        return saveValue;
    }

    public synchronized void saveValue(String name, int value) {
        this.mSharedPreferences.edit().putInt(name, value).commit();
    }

    public synchronized void saveValue(String name, float value) {
        this.mSharedPreferences.edit().putFloat(name, value).commit();
    }

    public synchronized void saveValue(String name, long value) {
        this.mSharedPreferences.edit().putLong(name, value).commit();
    }

    public synchronized void saveValue(String name, boolean value) {
        saveBooleanValue(name, value);
    }

    public synchronized void saveValue(String name, String value) {
        saveStrValue(name, value);
    }

    public synchronized void saveStrValue(String name, String value) {
        this.mSharedPreferences.edit().putString(name, value).commit();
    }

    public synchronized void saveBooleanValue(String name, boolean value) {
        this.mSharedPreferences.edit().putBoolean(name, value).commit();
    }

    public synchronized int readValue(String id) {
        return readValue(id, 0);
    }

    public synchronized int readValue(String id, int def) {
        return this.mSharedPreferences.getInt(id, def);
    }

    public synchronized float readFloatValue(String id) {
        return readFloatValue(id, 0.0f);
    }

    public synchronized float readFloatValue(String id, float def) {
        return this.mSharedPreferences.getFloat(id, def);
    }

    public synchronized String readStrValue(String id) {
        return readStrValue(id, "0");
    }

    public synchronized String readStrValue(String id, String def) {
        return this.mSharedPreferences.getString(id, def);
    }

    public synchronized boolean readBooleanValue(String id) {
        return readBooleanValue(id, false);
    }

    public synchronized boolean readBooleanValue(String id, boolean def) {
        return this.mSharedPreferences.getBoolean(id, def);
    }

    public synchronized long readLongValue(String id) {
        return readLongValue(id, 0);
    }

    public synchronized long readLongValue(String id, long def) {
        return this.mSharedPreferences.getLong(id, def);
    }

    public synchronized void removekey(String key) {
        this.mSharedPreferences.edit().remove(key).commit();
    }

    public static synchronized boolean writeWorldStringValue(Context context, String id, String value, boolean isStored) {
        synchronized (SaveValue.class) {
            ContentValues values = new ContentValues();
            values.put("key", id);
            values.put(GLOBAL_VALUE_VALUE, value);
            values.put(GLOBAL_VALUE_STORED, Boolean.valueOf(isStored));
            mContext = context;
            try {
                context.getContentResolver().insert(GLOBAL_PROVIDER_URI_URI, values);
            } catch (Exception e) {
                Log.d(TAG, "Exception++++");
                Message msg = Message.obtain();
                msg.arg1 = 1;
                msg.obj = values;
                mHandler.sendMessageDelayed(msg, 2000);
                return false;
            }
        }
        return true;
    }

    public static synchronized String readWorldStringValue(Context context, String id) {
        synchronized (SaveValue.class) {
            Cursor cursor = null;
            String value = "";
            try {
                cursor = context.getContentResolver().query(Uri.withAppendedPath(GLOBAL_PROVIDER_URI_URI, id), (String[]) null, (String) null, (String[]) null, (String) null);
                if (cursor == null) {
                    return value;
                }
                if (cursor.moveToNext()) {
                    value = cursor.getString(1);
                }
                try {
                    cursor.close();
                    return value;
                } catch (Exception e) {
                    return value;
                }
            } catch (Exception e2) {
            }
        }
    }

    public static synchronized boolean saveWorldBooleanValue(Context context, String id, boolean value, boolean isStored) {
        boolean writeWorldStringValue;
        synchronized (SaveValue.class) {
            writeWorldStringValue = writeWorldStringValue(context, id, String.valueOf(value), isStored);
        }
        return writeWorldStringValue;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0013, code lost:
        return false;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized boolean readWorldBooleanValue(android.content.Context r3, java.lang.String r4) {
        /*
            java.lang.Class<com.mediatek.wwtv.tvcenter.util.SaveValue> r0 = com.mediatek.wwtv.tvcenter.util.SaveValue.class
            monitor-enter(r0)
            java.lang.String r1 = readWorldStringValue(r3, r4)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            boolean r2 = java.lang.Boolean.parseBoolean(r1)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            monitor-exit(r0)
            return r2
        L_0x000d:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        L_0x0010:
            r1 = move-exception
            r1 = 0
            monitor-exit(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.SaveValue.readWorldBooleanValue(android.content.Context, java.lang.String):boolean");
    }

    public static synchronized boolean saveWorldValue(Context context, String id, int value, boolean isStored) {
        boolean writeWorldStringValue;
        synchronized (SaveValue.class) {
            writeWorldStringValue = writeWorldStringValue(context, id, String.valueOf(value), isStored);
        }
        return writeWorldStringValue;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0013, code lost:
        return 0;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized int readWorldIntValue(android.content.Context r3, java.lang.String r4) {
        /*
            java.lang.Class<com.mediatek.wwtv.tvcenter.util.SaveValue> r0 = com.mediatek.wwtv.tvcenter.util.SaveValue.class
            monitor-enter(r0)
            java.lang.String r1 = readWorldStringValue(r3, r4)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            int r2 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            monitor-exit(r0)
            return r2
        L_0x000d:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        L_0x0010:
            r1 = move-exception
            r1 = 0
            monitor-exit(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.SaveValue.readWorldIntValue(android.content.Context, java.lang.String):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0012, code lost:
        return r5;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized int readWorldIntValue(android.content.Context r3, java.lang.String r4, int r5) {
        /*
            java.lang.Class<com.mediatek.wwtv.tvcenter.util.SaveValue> r0 = com.mediatek.wwtv.tvcenter.util.SaveValue.class
            monitor-enter(r0)
            java.lang.String r1 = readWorldStringValue(r3, r4)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            int r2 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0010, all -> 0x000d }
            monitor-exit(r0)
            return r2
        L_0x000d:
            r3 = move-exception
            monitor-exit(r0)
            throw r3
        L_0x0010:
            r1 = move-exception
            monitor-exit(r0)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.wwtv.tvcenter.util.SaveValue.readWorldIntValue(android.content.Context, java.lang.String, int):int");
    }

    public static synchronized void setLocalMemoryValue(String id, int value) {
        synchronized (SaveValue.class) {
            mIntegerMap.put(id, Integer.valueOf(value));
        }
    }

    public static synchronized void setLocalMemoryValue(String id, boolean value) {
        synchronized (SaveValue.class) {
            mBooleanMap.put(id, Boolean.valueOf(value));
        }
    }

    public static synchronized int readLocalMemoryIntValue(String id) {
        int intValue;
        synchronized (SaveValue.class) {
            Integer value = mIntegerMap.get(id);
            intValue = value == null ? -1 : value.intValue();
        }
        return intValue;
    }

    public static synchronized boolean readLocalMemoryBooleanValue(String id) {
        boolean booleanValue;
        synchronized (SaveValue.class) {
            Boolean value = mBooleanMap.get(id);
            booleanValue = value == null ? false : value.booleanValue();
        }
        return booleanValue;
    }

    public static synchronized boolean writeWorldInputType(Context context, int inputType) {
        boolean saveWorldValue;
        synchronized (SaveValue.class) {
            saveWorldValue = saveWorldValue(context, "MAIN_INPUT_TYPE", inputType, true);
        }
        return saveWorldValue;
    }

    public static synchronized int readWorldInputType(Context context) {
        int readWorldIntValue;
        synchronized (SaveValue.class) {
            readWorldIntValue = readWorldIntValue(context, "MAIN_INPUT_TYPE", 10);
        }
        return readWorldIntValue;
    }
}
