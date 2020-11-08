package com.mediatek.wwtv.setting.parental;

import android.content.Context;
import android.preference.PreferenceManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TvSettings {
    public static final int CONTENT_RATING_LEVEL_CUSTOM = 4;
    public static final int CONTENT_RATING_LEVEL_HIGH = 1;
    public static final int CONTENT_RATING_LEVEL_LOW = 3;
    public static final int CONTENT_RATING_LEVEL_MEDIUM = 2;
    public static final int CONTENT_RATING_LEVEL_NONE = 0;
    public static final int PIP_LAYOUT_BOTTOM_LEFT = 3;
    public static final int PIP_LAYOUT_BOTTOM_RIGHT = 0;
    public static final int PIP_LAYOUT_LAST = 4;
    public static final int PIP_LAYOUT_SIDE_BY_SIDE = 4;
    public static final int PIP_LAYOUT_TOP_LEFT = 2;
    public static final int PIP_LAYOUT_TOP_RIGHT = 1;
    public static final int PIP_SIZE_BIG = 1;
    public static final int PIP_SIZE_LAST = 1;
    public static final int PIP_SIZE_SMALL = 0;
    public static final int PIP_SOUND_LAST = 1;
    public static final int PIP_SOUND_MAIN = 0;
    public static final int PIP_SOUND_PIP_WINDOW = 1;
    public static final String PREFS_FILE = "settings";
    public static final String PREF_CLOSED_CAPTION_ENABLED = "is_cc_enabled";
    private static final String PREF_CONTENT_RATING_LEVEL = "pref.content_rating_level";
    private static final String PREF_CONTENT_RATING_SYSTEMS = "pref.content_rating_systems";
    private static final String PREF_DISABLE_PIN_UNTIL = "pref.disable_pin_until";
    public static final String PREF_DISPLAY_MODE = "display_mode";
    private static final String PREF_MULTI_AUDIO_CHANNEL_COUNT = "pref.multi_audio_channel_count";
    private static final String PREF_MULTI_AUDIO_ID = "pref.multi_audio_id";
    private static final String PREF_MULTI_AUDIO_LANGUAGE = "pref.multi_audio_language";
    public static final String PREF_PIN = "pin";
    public static final String PREF_PIP_LAYOUT = "pip_layout";
    public static final String PREF_PIP_SIZE = "pip_size";
    public static final String PREF_TV_WATCH_LOGGING_ENABLED = "tv_watch_logging_enabled";

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentRatingLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PipLayout {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PipSize {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PipSound {
    }

    private TvSettings() {
    }

    public static int getPipLayout(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_PIP_LAYOUT, 0);
    }

    public static void setPipLayout(Context context, int pipLayout) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_PIP_LAYOUT, pipLayout).apply();
    }

    public static int getPipSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_PIP_SIZE, 0);
    }

    public static void setPipSize(Context context, int pipSize) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_PIP_SIZE, pipSize).apply();
    }

    public static String getMultiAudioId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_MULTI_AUDIO_ID, (String) null);
    }

    public static void setMultiAudioId(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_MULTI_AUDIO_ID, language).apply();
    }

    public static String getMultiAudioLanguage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_MULTI_AUDIO_LANGUAGE, (String) null);
    }

    public static void setMultiAudioLanguage(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_MULTI_AUDIO_LANGUAGE, language).apply();
    }

    public static int getMultiAudioChannelCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_MULTI_AUDIO_CHANNEL_COUNT, 0);
    }

    public static void setMultiAudioChannelCount(Context context, int channelCount) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_MULTI_AUDIO_CHANNEL_COUNT, channelCount).apply();
    }

    public static void addContentRatingSystems(Context context, Set<String> ids) {
        Set<String> contentRatingSystemSet = getContentRatingSystemSet(context);
        if (contentRatingSystemSet.addAll(ids)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(PREF_CONTENT_RATING_SYSTEMS, contentRatingSystemSet).apply();
        }
    }

    public static void addContentRatingSystem(Context context, String id) {
        Set<String> contentRatingSystemSet = getContentRatingSystemSet(context);
        if (contentRatingSystemSet.add(id)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(PREF_CONTENT_RATING_SYSTEMS, contentRatingSystemSet).apply();
        }
    }

    public static void removeContentRatingSystems(Context context, Set<String> ids) {
        Set<String> contentRatingSystemSet = getContentRatingSystemSet(context);
        if (contentRatingSystemSet.removeAll(ids)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(PREF_CONTENT_RATING_SYSTEMS, contentRatingSystemSet).apply();
        }
    }

    public static void removeContentRatingSystem(Context context, String id) {
        Set<String> contentRatingSystemSet = getContentRatingSystemSet(context);
        if (contentRatingSystemSet.remove(id)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(PREF_CONTENT_RATING_SYSTEMS, contentRatingSystemSet).apply();
        }
    }

    public static boolean hasContentRatingSystem(Context context, String id) {
        return getContentRatingSystemSet(context).contains(id);
    }

    public static boolean isContentRatingSystemSet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_CONTENT_RATING_SYSTEMS, (Set) null) != null;
    }

    private static Set<String> getContentRatingSystemSet(Context context) {
        return new HashSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_CONTENT_RATING_SYSTEMS, Collections.emptySet()));
    }

    public static int getContentRatingLevel(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_CONTENT_RATING_LEVEL, 0);
    }

    public static void setContentRatingLevel(Context context, int level) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_CONTENT_RATING_LEVEL, level).apply();
    }

    public static long getDisablePinUntil(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PREF_DISABLE_PIN_UNTIL, 0);
    }

    public static void setDisablePinUntil(Context context, long timeMillis) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PREF_DISABLE_PIN_UNTIL, timeMillis).apply();
    }
}
