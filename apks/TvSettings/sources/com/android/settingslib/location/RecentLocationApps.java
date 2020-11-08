package com.android.settingslib.location;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.VisibleForTesting;
import android.util.IconDrawableFactory;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentLocationApps {
    @VisibleForTesting
    static final String ANDROID_SYSTEM_PACKAGE_NAME = "android";
    @VisibleForTesting
    static final int[] LOCATION_OPS = {41, 42};
    private static final long RECENT_TIME_INTERVAL_MILLIS = 86400000;
    private static final String TAG = RecentLocationApps.class.getSimpleName();
    private final Context mContext;
    private final IconDrawableFactory mDrawableFactory;
    private final PackageManager mPackageManager;

    public RecentLocationApps(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mDrawableFactory = IconDrawableFactory.newInstance(context);
    }

    public List<Request> getAppList() {
        Request request;
        List<AppOpsManager.PackageOps> appOps = ((AppOpsManager) this.mContext.getSystemService("appops")).getPackagesForOps(LOCATION_OPS);
        int appOpsCount = appOps != null ? appOps.size() : 0;
        ArrayList<Request> requests = new ArrayList<>(appOpsCount);
        long now = System.currentTimeMillis();
        List<UserHandle> profiles = ((UserManager) this.mContext.getSystemService("user")).getUserProfiles();
        for (int i = 0; i < appOpsCount; i++) {
            AppOpsManager.PackageOps ops = appOps.get(i);
            String packageName = ops.getPackageName();
            int uid = ops.getUid();
            int userId = UserHandle.getUserId(uid);
            if (!(uid == 1000 && ANDROID_SYSTEM_PACKAGE_NAME.equals(packageName)) && profiles.contains(new UserHandle(userId)) && (request = getRequestFromOps(now, ops)) != null) {
                requests.add(request);
            }
        }
        return requests;
    }

    public List<Request> getAppListSorted() {
        List<Request> requests = getAppList();
        Collections.sort(requests, Collections.reverseOrder(new Comparator<Request>() {
            public int compare(Request request1, Request request2) {
                return Long.compare(request1.requestFinishTime, request2.requestFinishTime);
            }
        }));
        return requests;
    }

    private Request getRequestFromOps(long now, AppOpsManager.PackageOps ops) {
        int userId;
        String packageName = ops.getPackageName();
        List<AppOpsManager.OpEntry> entries = ops.getOps();
        long recentLocationCutoffTime = now - RECENT_TIME_INTERVAL_MILLIS;
        boolean highBattery = false;
        boolean normalBattery = false;
        long locationRequestFinishTime = 0;
        for (AppOpsManager.OpEntry entry : entries) {
            if (entry.isRunning() || entry.getTime() >= recentLocationCutoffTime) {
                locationRequestFinishTime = entry.getTime() + ((long) entry.getDuration());
                switch (entry.getOp()) {
                    case 41:
                        normalBattery = true;
                        break;
                    case 42:
                        highBattery = true;
                        break;
                }
            }
        }
        if (highBattery || normalBattery) {
            int uid = ops.getUid();
            int userId2 = UserHandle.getUserId(uid);
            try {
                ApplicationInfo appInfo = this.mPackageManager.getApplicationInfoAsUser(packageName, 128, userId2);
                if (appInfo == null) {
                    try {
                        Log.w(TAG, "Null application info retrieved for package " + packageName + ", userId " + userId2);
                        return null;
                    } catch (PackageManager.NameNotFoundException e) {
                        userId = userId2;
                        int i = uid;
                        Log.w(TAG, "package name not found for " + packageName + ", userId " + userId);
                        return null;
                    }
                } else {
                    UserHandle userHandle = new UserHandle(userId2);
                    Drawable icon = this.mDrawableFactory.getBadgedIcon(appInfo, userId2);
                    CharSequence appLabel = this.mPackageManager.getApplicationLabel(appInfo);
                    CharSequence badgedAppLabel = this.mPackageManager.getUserBadgedLabel(appLabel, userHandle);
                    if (appLabel.toString().contentEquals(badgedAppLabel)) {
                        badgedAppLabel = null;
                    }
                    CharSequence badgedAppLabel2 = badgedAppLabel;
                    CharSequence charSequence = appLabel;
                    userId = userId2;
                    ApplicationInfo applicationInfo = appInfo;
                    int i2 = uid;
                    try {
                        Request request = new Request(packageName, userHandle, icon, appLabel, highBattery, badgedAppLabel2, locationRequestFinishTime);
                        return request;
                    } catch (PackageManager.NameNotFoundException e2) {
                        Log.w(TAG, "package name not found for " + packageName + ", userId " + userId);
                        return null;
                    }
                }
            } catch (PackageManager.NameNotFoundException e3) {
                userId = userId2;
                int i3 = uid;
                Log.w(TAG, "package name not found for " + packageName + ", userId " + userId);
                return null;
            }
        } else {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, packageName + " hadn't used location within the time interval.");
            }
            return null;
        }
    }

    public static class Request {
        public final CharSequence contentDescription;
        public final Drawable icon;
        public final boolean isHighBattery;
        public final CharSequence label;
        public final String packageName;
        public final long requestFinishTime;
        public final UserHandle userHandle;

        private Request(String packageName2, UserHandle userHandle2, Drawable icon2, CharSequence label2, boolean isHighBattery2, CharSequence contentDescription2, long requestFinishTime2) {
            this.packageName = packageName2;
            this.userHandle = userHandle2;
            this.icon = icon2;
            this.label = label2;
            this.isHighBattery = isHighBattery2;
            this.contentDescription = contentDescription2;
            this.requestFinishTime = requestFinishTime2;
        }
    }
}
