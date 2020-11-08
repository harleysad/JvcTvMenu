package com.android.tv.settings.name;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceNameSuggestionStatus {
    private static final String IS_SUGGESTION_FINISHED = "IsSuggestionFinished";
    private static final String SUGGESTION_STATUS_STORAGE_FILE_NAME = "suggestionStatusStorage";
    private static DeviceNameSuggestionStatus sInstance;
    private SharedPreferences mSharedPreferences;

    public static DeviceNameSuggestionStatus getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DeviceNameSuggestionStatus(context);
        }
        return sInstance;
    }

    private DeviceNameSuggestionStatus(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(SUGGESTION_STATUS_STORAGE_FILE_NAME, 0);
    }

    public void setFinished() {
        if (!isFinished()) {
            this.mSharedPreferences.edit().putBoolean(IS_SUGGESTION_FINISHED, true).apply();
        }
    }

    public boolean isFinished() {
        return this.mSharedPreferences.getBoolean(IS_SUGGESTION_FINISHED, false);
    }
}
