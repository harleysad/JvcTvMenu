package com.android.tv.settings.users;

import android.app.ActivityManager;
import android.app.Service;
import android.app.UserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.tv.settings.partnercustomizer.retailmode.RetailModeService;
import com.android.tv.settings.partnercustomizer.switchofftimer.SwitchOffTimerService;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVMenuSettingsService;

public class UserSwitchListenerService extends Service {
    private static final boolean DEBUG = false;
    private static final String ON_BOOT_USER_ID_PREFERENCE = "UserSwitchOnBootBroadcastReceiver.userId";
    private static final String RESTRICTED_PROFILE_LAUNCHER_ENTRY_ACTIVITY = "com.android.tv.settings.users.RestrictedProfileActivityLauncherEntry";
    private static final String SHARED_PREFERENCES_NAME = "RestrictedProfileSharedPreferences";
    private static final String TAG = "RestrictedProfile";

    public static class BootReceiver extends BroadcastReceiver {
        public void onReceive(final Context context, Intent intent) {
            new Handler().post(new Runnable() {
                public void run() {
                    boolean isSystemUser = UserManager.get(context).isSystemUser();
                    boolean isFbeEnabled = LockPatternUtils.isFileEncryptionEnabled();
                    if (isSystemUser && !isFbeEnabled) {
                        context.startService(new Intent(context, UserSwitchListenerService.class));
                        int bootUserId = UserSwitchListenerService.getBootUser(context);
                        if (UserHandle.myUserId() != bootUserId) {
                            UserSwitchListenerService.switchUserNow(bootUserId);
                        }
                    }
                    context.startService(new Intent(context, TVMenuSettingsService.class));
                    SwitchOffTimerService.bootup(context);
                    RetailModeService.bootup(context);
                    UserSwitchListenerService.updateLaunchPoint(context, new RestrictedProfileModel(context).getUser() != null);
                }
            });
            Log.v(UserSwitchListenerService.TAG, "onReceived end");
        }
    }

    public static void updateLaunchPoint(Context context, boolean enableLaunchPoint) {
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, RESTRICTED_PROFILE_LAUNCHER_ENTRY_ACTIVITY), enableLaunchPoint ? 1 : 2, 1);
    }

    static void setBootUser(Context context, int userId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).edit();
        editor.putInt(ON_BOOT_USER_ID_PREFERENCE, userId);
        editor.apply();
    }

    static int getBootUser(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0).getInt(ON_BOOT_USER_ID_PREFERENCE, 0);
    }

    /* access modifiers changed from: private */
    public static void switchUserNow(int userId) {
        try {
            ActivityManager.getService().switchUser(userId);
        } catch (RemoteException re) {
            Log.e(TAG, "Caught exception while switching user! ", re);
        }
    }

    public void onCreate() {
        super.onCreate();
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver() {
                public void onUserSwitchComplete(int newUserId) throws RemoteException {
                    UserSwitchListenerService.setBootUser(UserSwitchListenerService.this, newUserId);
                }
            }, UserSwitchListenerService.class.getName());
        } catch (RemoteException e) {
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
