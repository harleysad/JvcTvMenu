package com.android.tv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.tv.TvTrackInfo;
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
    public static final int CONTENT_RATING_LEVEL_UNKNOWN = -1;
    private static final String PREF_CONTENT_RATING_LEVEL = "pref.content_rating_level";
    private static final String PREF_CONTENT_RATING_SYSTEMS = "pref.content_rating_systems";
    private static final String PREF_DISABLE_PIN_UNTIL = "pref.disable_pin_until";
    public static final String PREF_DISPLAY_MODE = "display_mode";
    private static final String PREF_DVR_MULTI_AUDIO_CHANNEL_COUNT = "pref.dvr_multi_audio_channel_count";
    private static final String PREF_DVR_MULTI_AUDIO_ID = "pref.dvr_multi_audio_id";
    private static final String PREF_DVR_MULTI_AUDIO_LANGUAGE = "pref.dvr_multi_audio_language";
    private static final String PREF_DVR_SUBTITLE_ID = "pref.dvr_subtitle_id";
    private static final String PREF_DVR_SUBTITLE_LANGUAGE = "pref.dvr_subtitle_language";
    private static final String PREF_MULTI_AUDIO_CHANNEL_COUNT = "pref.multi_audio_channel_count";
    private static final String PREF_MULTI_AUDIO_ID = "pref.multi_audio_id";
    private static final String PREF_MULTI_AUDIO_LANGUAGE = "pref.multi_audio_language";
    public static final String PREF_PIN = "pin";
    private static long disablePinUtil = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentRatingLevel {
    }

    private TvSettings() {
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

    public static void setDvrPlaybackTrackSettings(Context context, int trackType, TvTrackInfo info) {
        if (trackType == 0) {
            if (info == null) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(PREF_DVR_MULTI_AUDIO_ID).apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_DVR_MULTI_AUDIO_LANGUAGE, info.getLanguage()).putInt(PREF_DVR_MULTI_AUDIO_CHANNEL_COUNT, info.getAudioChannelCount()).putString(PREF_DVR_MULTI_AUDIO_ID, info.getId()).apply();
            }
        } else if (trackType != 2) {
        } else {
            if (info == null) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().remove(PREF_DVR_SUBTITLE_ID).apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_DVR_SUBTITLE_LANGUAGE, info.getLanguage()).putString(PREF_DVR_SUBTITLE_ID, info.getId()).apply();
            }
        }
    }

    public static TvTrackInfo getDvrPlaybackTrackSettings(Context context, int trackType) {
        String trackId;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (trackType == 0) {
            String trackId2 = pref.getString(PREF_DVR_MULTI_AUDIO_ID, (String) null);
            if (trackId2 == null) {
                return null;
            }
            String language = pref.getString(PREF_DVR_MULTI_AUDIO_LANGUAGE, (String) null);
            return new TvTrackInfo.Builder(trackType, trackId2).setLanguage(language).setAudioChannelCount(pref.getInt(PREF_DVR_MULTI_AUDIO_CHANNEL_COUNT, 0)).build();
        } else if (trackType != 2 || (trackId = pref.getString(PREF_DVR_SUBTITLE_ID, (String) null)) == null) {
            return null;
        } else {
            return new TvTrackInfo.Builder(trackType, trackId).setLanguage(pref.getString(PREF_DVR_SUBTITLE_LANGUAGE, (String) null)).build();
        }
    }

    public static void addContentRatingSystem(Context context, String id) {
        Set<String> contentRatingSystemSet = getContentRatingSystemSet(context);
        if (contentRatingSystemSet.add(id)) {
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

    public static boolean hasContentRatingSystem(Context context) {
        return !getContentRatingSystemSet(context).isEmpty();
    }

    public static boolean isContentRatingSystemSet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_CONTENT_RATING_SYSTEMS, (Set) null) != null;
    }

    private static Set<String> getContentRatingSystemSet(Context context) {
        return new HashSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_CONTENT_RATING_SYSTEMS, Collections.emptySet()));
    }

    public static int getContentRatingLevel(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_CONTENT_RATING_LEVEL, -1);
    }

    public static void setContentRatingLevel(Context context, int level) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_CONTENT_RATING_LEVEL, level).apply();
    }

    public static long getDisablePinUntil(Context context) {
        return disablePinUtil;
    }

    public static void setDisablePinUntil(Context context, long timeMillis) {
        disablePinUtil = timeMillis;
    }
}
