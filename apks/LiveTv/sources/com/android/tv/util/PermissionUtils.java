package com.android.tv.util;

import android.content.Context;

public class PermissionUtils {
    public static final String PERMISSION_READ_TV_LISTINGS = "android.permission.READ_TV_LISTINGS";
    private static Boolean sHasAccessAllEpgPermission;
    private static Boolean sHasAccessWatchedHistoryPermission;
    private static Boolean sHasModifyParentalControlsPermission;

    public static boolean hasAccessAllEpg(Context context) {
        if (sHasAccessAllEpgPermission == null) {
            sHasAccessAllEpgPermission = Boolean.valueOf(context.checkSelfPermission("com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA") == 0);
        }
        return sHasAccessAllEpgPermission.booleanValue();
    }

    public static boolean hasAccessWatchedHistory(Context context) {
        if (sHasAccessWatchedHistoryPermission == null) {
            sHasAccessWatchedHistoryPermission = Boolean.valueOf(context.checkSelfPermission("com.android.providers.tv.permission.ACCESS_WATCHED_PROGRAMS") == 0);
        }
        return sHasAccessWatchedHistoryPermission.booleanValue();
    }

    public static boolean hasModifyParentalControls(Context context) {
        if (sHasModifyParentalControlsPermission == null) {
            sHasModifyParentalControlsPermission = Boolean.valueOf(context.checkSelfPermission("android.permission.MODIFY_PARENTAL_CONTROLS") == 0);
        }
        return sHasModifyParentalControlsPermission.booleanValue();
    }

    public static boolean hasReadTvListings(Context context) {
        return context.checkSelfPermission("android.permission.READ_TV_LISTINGS") == 0;
    }
}
