package com.android.settingslib.core.instrumentation;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class SharedPreferencesLogger implements SharedPreferences {
    private static final String LOG_TAG = "SharedPreferencesLogger";
    /* access modifiers changed from: private */
    public final Context mContext;
    private final MetricsFeatureProvider mMetricsFeature;
    private final Set<String> mPreferenceKeySet = new ConcurrentSkipListSet();
    private final String mTag;

    public SharedPreferencesLogger(Context context, String tag, MetricsFeatureProvider metricsFeature) {
        this.mContext = context;
        this.mTag = tag;
        this.mMetricsFeature = metricsFeature;
    }

    public Map<String, ?> getAll() {
        return null;
    }

    public String getString(String key, String defValue) {
        return defValue;
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return defValues;
    }

    public int getInt(String key, int defValue) {
        return defValue;
    }

    public long getLong(String key, long defValue) {
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        return defValue;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return defValue;
    }

    public boolean contains(String key) {
        return false;
    }

    public SharedPreferences.Editor edit() {
        return new EditorLogger();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
    }

    /* access modifiers changed from: private */
    public void logValue(String key, Object value) {
        logValue(key, value, false);
    }

    /* access modifiers changed from: private */
    public void logValue(String key, Object value, boolean forceLog) {
        Pair<Integer, Object> valueData;
        int intVal;
        String prefKey = buildPrefKey(this.mTag, key);
        if (forceLog || this.mPreferenceKeySet.contains(prefKey)) {
            this.mMetricsFeature.count(this.mContext, buildCountName(prefKey, value), 1);
            if (value instanceof Long) {
                Long longVal = (Long) value;
                if (longVal.longValue() > 2147483647L) {
                    intVal = Integer.MAX_VALUE;
                } else if (longVal.longValue() < -2147483648L) {
                    intVal = Integer.MIN_VALUE;
                } else {
                    intVal = longVal.intValue();
                }
                valueData = Pair.create(1089, Integer.valueOf(intVal));
            } else if (value instanceof Integer) {
                valueData = Pair.create(1089, value);
            } else if (value instanceof Boolean) {
                valueData = Pair.create(1089, Integer.valueOf(((Boolean) value).booleanValue() ? 1 : 0));
            } else if (value instanceof Float) {
                valueData = Pair.create(995, value);
            } else if (value instanceof String) {
                Log.d(LOG_TAG, "Tried to log string preference " + prefKey + " = " + value);
                valueData = null;
            } else {
                Log.w(LOG_TAG, "Tried to log unloggable object" + value);
                valueData = null;
            }
            if (valueData != null) {
                this.mMetricsFeature.action(this.mContext, 853, (Pair<Integer, Object>[]) new Pair[]{Pair.create(854, prefKey), valueData});
                return;
            }
            return;
        }
        this.mPreferenceKeySet.add(prefKey);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void logPackageName(String key, String value) {
        this.mMetricsFeature.action(this.mContext, 853, value, Pair.create(854, this.mTag + "/" + key));
    }

    /* access modifiers changed from: private */
    public void safeLogValue(String key, String value) {
        new AsyncPackageCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{key, value});
    }

    public static String buildCountName(String prefKey, Object value) {
        return prefKey + "|" + value;
    }

    public static String buildPrefKey(String tag, String key) {
        return tag + "/" + key;
    }

    private class AsyncPackageCheck extends AsyncTask<String, Void, Void> {
        private AsyncPackageCheck() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(String... params) {
            String key = params[0];
            String value = params[1];
            PackageManager pm = SharedPreferencesLogger.this.mContext.getPackageManager();
            try {
                ComponentName name = ComponentName.unflattenFromString(value);
                if (value != null) {
                    value = name.getPackageName();
                }
            } catch (Exception e) {
            }
            try {
                pm.getPackageInfo(value, 4194304);
                SharedPreferencesLogger.this.logPackageName(key, value);
                return null;
            } catch (PackageManager.NameNotFoundException e2) {
                SharedPreferencesLogger.this.logValue(key, value, true);
                return null;
            }
        }
    }

    public class EditorLogger implements SharedPreferences.Editor {
        public EditorLogger() {
        }

        public SharedPreferences.Editor putString(String key, String value) {
            SharedPreferencesLogger.this.safeLogValue(key, value);
            return this;
        }

        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            SharedPreferencesLogger.this.safeLogValue(key, TextUtils.join(",", values));
            return this;
        }

        public SharedPreferences.Editor putInt(String key, int value) {
            SharedPreferencesLogger.this.logValue(key, Integer.valueOf(value));
            return this;
        }

        public SharedPreferences.Editor putLong(String key, long value) {
            SharedPreferencesLogger.this.logValue(key, Long.valueOf(value));
            return this;
        }

        public SharedPreferences.Editor putFloat(String key, float value) {
            SharedPreferencesLogger.this.logValue(key, Float.valueOf(value));
            return this;
        }

        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            SharedPreferencesLogger.this.logValue(key, Boolean.valueOf(value));
            return this;
        }

        public SharedPreferences.Editor remove(String key) {
            return this;
        }

        public SharedPreferences.Editor clear() {
            return this;
        }

        public boolean commit() {
            return true;
        }

        public void apply() {
        }
    }
}
