package com.android.tv.util;

import android.content.ComponentName;
import android.content.Context;
import android.media.tv.TvInputInfo;
import android.text.TextUtils;
import android.text.format.DateUtils;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Utils {
    private static final String PREF_KEY_LAST_WATCHED_CHANNEL_ID = "last_watched_channel_id";
    private static final String PREF_KEY_LAST_WATCHED_CHANNEL_ID_FOR_INPUT = "last_watched_channel_id_for_input_";
    private static final String PREF_KEY_LAST_WATCHED_CHANNEL_PARENT_ID = "last_watched_channel_parent_id";
    private static final String PREF_KEY_LAST_WATCHED_CHANNEL_URI = "last_watched_channel_uri";
    private static final String PREF_KEY_LAST_WATCHED_TUNER_INPUT_ID = "last_watched_tuner_input_id";
    private static final String PREF_KEY_RECORDING_FAILED_REASONS = "recording_failed_reasons";
    private static final String TAG = "Utils";

    public static boolean isIndexValid(Collection<?> collection, int index) {
        return collection != null && index >= 0 && index < collection.size();
    }

    public static String intern(String string) {
        if (string == null) {
            return null;
        }
        return string.intern();
    }

    public static String loadLabel(Context context, TvInputInfo input) {
        String label = null;
        if (input == null) {
            return null;
        }
        CharSequence customLabel = input.loadCustomLabel(context);
        if (customLabel != null) {
            label = customLabel.toString();
        }
        if (TextUtils.isEmpty(label)) {
            return input.loadLabel(context).toString();
        }
        return label;
    }

    public static String toTimeString(long timeMillis, boolean fullFormat) {
        if (fullFormat) {
            return new Date(timeMillis).toString();
        }
        long currentTimeMillis = System.currentTimeMillis();
        return (String) DateUtils.formatSameDayTime(timeMillis, System.currentTimeMillis(), 3, 3);
    }

    public static String toTimeString(long timeMillis) {
        return toTimeString(timeMillis, true);
    }

    public static String buildSelectionForIds(String idName, List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(idName);
        sb.append(" in (");
        sb.append(ids.get(0));
        for (int i = 1; i < ids.size(); i++) {
            sb.append(",");
            sb.append(ids.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    public static boolean isInternalTvInput(Context context, String inputId) {
        ComponentName unflattenInputId = ComponentName.unflattenFromString(inputId);
        if (unflattenInputId == null) {
            return false;
        }
        return context.getPackageName().equals(unflattenInputId.getPackageName());
    }
}
