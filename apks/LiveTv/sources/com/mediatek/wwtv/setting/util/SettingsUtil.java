package com.mediatek.wwtv.setting.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Calendar;

public class SettingsUtil {
    public static final String ACTION_PREPARE_SHUTDOWN = "android.intent.action.ACTION_PREPARE_SHUTDOWN";
    public static final int LOAD_STATUS_FINISH = 22;
    public static final int LOAD_STATUS_START = 21;
    public static final String MAIN_SOURCE_NAME = "multi_view_main_source_name";
    public static final String NAV_COMPONENT_SHOW_FLAG = "NavComponentShow";
    public static final int NAV_COMP_ID_BASIC = 16777216;
    public static final int NAV_COMP_ID_EAS = 16777217;
    public static String OPTIONSPLITER = "#";
    public static int SCREEN_HEIGHT = 0;
    public static int SCREEN_WIDTH = 0;
    private static final String SHUTDOWN_INTENT_EXTRA = "shutdown";
    public static String SPECIAL_SAT_DETAIL_INFO_ITEM_POL = "SAT_DETAIL_INFO_ITEM_POLAZation";
    public static final String SUB_SOURCE_NAME = "multi_view_sub_source_name";
    public static final int SVCTX_NTFY_CODE_AUDIO_ONLY_SVC = 20;
    public static final int SVCTX_NTFY_CODE_AUDIO_VIDEO_SVC = 22;
    public static final int SVCTX_NTFY_CODE_NO_AUDIO_VIDEO_SVC = 19;
    public static final int SVCTX_NTFY_CODE_SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC = 24;
    public static final int SVCTX_NTFY_CODE_SCRAMBLED_AUDIO_NO_VIDEO_SVC = 25;
    public static final int SVCTX_NTFY_CODE_SCRAMBLED_AUDIO_VIDEO_SVC = 23;
    public static final int SVCTX_NTFY_CODE_SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC = 26;
    public static final int SVCTX_NTFY_CODE_SCRAMBLED_VIDEO_NO_AUDIO_SVC = 27;
    public static final int SVCTX_NTFY_CODE_SERVICE_BLOCKED = 9;
    public static final int SVCTX_NTFY_CODE_SERVICE_UNBLOCKED = 12;
    public static final int SVCTX_NTFY_CODE_SIGNAL_LOCKED = 4;
    public static final int SVCTX_NTFY_CODE_SIGNAL_LOSS = 5;
    public static final int SVCTX_NTFY_CODE_VIDEO_FMT_UPDATE = 37;
    public static final int SVCTX_NTFY_CODE_VIDEO_ONLY_SVC = 21;
    public static final String TAG = "SettingsUtil";
    public static int loadStatus = -1;

    public static String[] getRealIdAndValue(String newId) {
        String[] idValue = newId.split("#");
        if (idValue != null && idValue.length == 2) {
            return idValue;
        }
        Log.e(TAG, "something error,please check your newId:" + newId);
        return null;
    }

    public static void setDate(Context context, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month);
        c.set(5, day);
        long when = c.getTimeInMillis();
        Log.e(TAG, "time miss==" + when);
        if (when / 1000 < 2147483647L) {
            ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).setTime(when);
        } else {
            Log.e(TAG, "this is too long...");
        }
    }

    public static void setTime(Context context, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(11, hourOfDay);
        c.set(12, minute);
        c.set(13, 0);
        c.set(14, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < 2147483647L) {
            ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).setTime(when);
            MtkLog.i(TAG, "set Time== " + c);
        }
    }

    public static void setTime(Context context, int hourOfDay, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(11, hourOfDay);
        c.set(12, minute);
        c.set(13, second);
        c.set(14, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < 2147483647L) {
            ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).setTime(when);
            MtkLog.i(TAG, "set Time== " + c);
        }
    }

    public static void sendResetBroadcast(Context context) {
        if (!ActivityManager.isUserAMonkey()) {
            Log.e(TAG, "factory reset......");
            Intent resetIntent = new Intent("android.intent.action.FACTORY_RESET");
            resetIntent.setPackage("android");
            resetIntent.setFlags(268435456);
            resetIntent.putExtra("android.intent.extra.REASON", "ResetConfirmFragment");
            resetIntent.putExtra(SHUTDOWN_INTENT_EXTRA, false);
            context.sendBroadcast(resetIntent);
        }
    }
}
