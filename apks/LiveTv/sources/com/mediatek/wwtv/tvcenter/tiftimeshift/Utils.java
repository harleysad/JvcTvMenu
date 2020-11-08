package com.mediatek.wwtv.tvcenter.tiftimeshift;

import android.net.Uri;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {
    private static final boolean DEBUG = false;
    public static final String EXTRA_ACTION_SHOW_TV_INPUT = "show_tv_input";
    public static final String EXTRA_KEY_ACTION = "action";
    public static final String EXTRA_KEY_FROM_LAUNCHER = "from_launcher";
    public static final String EXTRA_KEY_KEYCODE = "keycode";
    private static final SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String TAG = "Utils";

    private Utils() {
    }

    private static boolean isTwoSegmentUriStartingWith(Uri uri, String pathSegment) {
        List<String> pathSegments = uri.getPathSegments();
        return pathSegments.size() == 2 && pathSegment.equals(pathSegments.get(0));
    }

    public static String toTimeString(long timeMillis) {
        return new Date(timeMillis).toString();
    }

    public static String toIsoDateTimeString(long timeMillis) {
        return ISO_8601.format(new Date(timeMillis));
    }

    public static String toRectString(View view) {
        return "{l=" + view.getLeft() + ",r=" + view.getRight() + ",t=" + view.getTop() + ",b=" + view.getBottom() + ",w=" + view.getWidth() + ",h=" + view.getHeight() + "}";
    }

    public static long floorTime(long timeMs, long timeUnit) {
        return timeMs - (timeMs % timeUnit);
    }

    public static long ceilTime(long timeMs, long timeUnit) {
        return (timeMs + timeUnit) - (timeMs % timeUnit);
    }
}
