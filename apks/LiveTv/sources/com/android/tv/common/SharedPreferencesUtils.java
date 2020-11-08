package com.android.tv.common;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public final class SharedPreferencesUtils {
    public static final String SHARED_PREF_AUDIO_CAPABILITIES = "com.android.tv.audio_capabilities";
    public static final String SHARED_PREF_BROWSABLE = "browsable_shared_preference";
    public static final String SHARED_PREF_DVR_WATCHED_POSITION = "dvr_watched_position_shared_preference";
    public static final String SHARED_PREF_EPG = "epg_preferences";
    public static final String SHARED_PREF_FEATURES = "sharePreferencesFeatures";
    public static final String SHARED_PREF_RECURRING_RUNNER = "sharedPreferencesRecurringRunner";
    public static final String SHARED_PREF_SERIES_RECORDINGS = "seriesRecordings";
    public static final String SHARED_PREF_WATCHED_HISTORY = "watched_history_shared_preference";
    private static boolean sInitializeCalled;

    public static synchronized void initialize(final Context context, final Runnable postTask) {
        synchronized (SharedPreferencesUtils.class) {
            if (!sInitializeCalled) {
                sInitializeCalled = true;
                new AsyncTask<Void, Void, Void>() {
                    /* access modifiers changed from: protected */
                    public Void doInBackground(Void... params) {
                        PreferenceManager.getDefaultSharedPreferences(context);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_FEATURES, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_BROWSABLE, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_WATCHED_HISTORY, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_DVR_WATCHED_POSITION, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_AUDIO_CAPABILITIES, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_RECURRING_RUNNER, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_EPG, 0);
                        context.getSharedPreferences(SharedPreferencesUtils.SHARED_PREF_SERIES_RECORDINGS, 0);
                        return null;
                    }

                    /* access modifiers changed from: protected */
                    public void onPostExecute(Void result) {
                        postTask.run();
                    }
                }.execute(new Void[0]);
            }
        }
    }

    private SharedPreferencesUtils() {
    }
}
