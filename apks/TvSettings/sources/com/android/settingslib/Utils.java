package com.android.settingslib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.settingslib.wrapper.LocationManagerWrapper;
import java.text.NumberFormat;

public class Utils {
    private static final String CURRENT_MODE_KEY = "CURRENT_MODE";
    private static final String NEW_MODE_KEY = "NEW_MODE";
    @VisibleForTesting
    static final String STORAGE_MANAGER_ENABLED_PROPERTY = "ro.storage_manager.enabled";
    static final int[] WIFI_PIE = {17302781, 17302782, 17302783, 17302784, 17302785};
    private static String sPermissionControllerPackageName;
    private static String sServicesSystemSharedLibPackageName;
    private static String sSharedSystemSharedLibPackageName;
    private static Signature[] sSystemSignature;

    public static void updateLocationEnabled(Context context, boolean enabled, int userId, int source) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "location_changer", source, userId);
        Intent intent = new Intent("com.android.settings.location.MODE_CHANGING");
        int newMode = 0;
        int oldMode = Settings.Secure.getIntForUser(context.getContentResolver(), "location_mode", 0, userId);
        if (enabled) {
            newMode = 3;
        }
        intent.putExtra(CURRENT_MODE_KEY, oldMode);
        intent.putExtra(NEW_MODE_KEY, newMode);
        context.sendBroadcastAsUser(intent, UserHandle.of(userId), "android.permission.WRITE_SECURE_SETTINGS");
        new LocationManagerWrapper((LocationManager) context.getSystemService("location")).setLocationEnabledForUser(enabled, UserHandle.of(userId));
    }

    public static boolean updateLocationMode(Context context, int oldMode, int newMode, int userId, int source) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "location_changer", source, userId);
        Intent intent = new Intent("com.android.settings.location.MODE_CHANGING");
        intent.putExtra(CURRENT_MODE_KEY, oldMode);
        intent.putExtra(NEW_MODE_KEY, newMode);
        context.sendBroadcastAsUser(intent, UserHandle.of(userId), "android.permission.WRITE_SECURE_SETTINGS");
        return Settings.Secure.putIntForUser(context.getContentResolver(), "location_mode", newMode, userId);
    }

    public static int getTetheringLabel(ConnectivityManager cm) {
        String[] usbRegexs = cm.getTetherableUsbRegexs();
        String[] wifiRegexs = cm.getTetherableWifiRegexs();
        String[] bluetoothRegexs = cm.getTetherableBluetoothRegexs();
        boolean bluetoothAvailable = false;
        boolean usbAvailable = usbRegexs.length != 0;
        boolean wifiAvailable = wifiRegexs.length != 0;
        if (bluetoothRegexs.length != 0) {
            bluetoothAvailable = true;
        }
        if (wifiAvailable && usbAvailable && bluetoothAvailable) {
            return R.string.tether_settings_title_all;
        }
        if (wifiAvailable && usbAvailable) {
            return R.string.tether_settings_title_all;
        }
        if (wifiAvailable && bluetoothAvailable) {
            return R.string.tether_settings_title_all;
        }
        if (wifiAvailable) {
            return R.string.tether_settings_title_wifi;
        }
        if (usbAvailable && bluetoothAvailable) {
            return R.string.tether_settings_title_usb_bluetooth;
        }
        if (usbAvailable) {
            return R.string.tether_settings_title_usb;
        }
        return R.string.tether_settings_title_bluetooth;
    }

    public static String getUserLabel(Context context, UserInfo info) {
        String name = info != null ? info.name : null;
        if (info.isManagedProfile()) {
            return context.getString(R.string.managed_user_title);
        }
        if (info.isGuest()) {
            name = context.getString(R.string.user_guest);
        }
        if (name == null && info != null) {
            name = Integer.toString(info.id);
        } else if (info == null) {
            name = context.getString(R.string.unknown);
        }
        return context.getResources().getString(R.string.running_process_item_user_label, new Object[]{name});
    }

    public static Drawable getUserIcon(Context context, UserManager um, UserInfo user) {
        Bitmap icon;
        int iconSize = UserIconDrawable.getSizeForList(context);
        if (user.isManagedProfile()) {
            Drawable drawable = UserIconDrawable.getManagedUserDrawable(context);
            drawable.setBounds(0, 0, iconSize, iconSize);
            return drawable;
        } else if (user.iconPath == null || (icon = um.getUserIcon(user.id)) == null) {
            return new UserIconDrawable(iconSize).setIconDrawable(UserIcons.getDefaultUserIcon(context.getResources(), user.id, false)).bake();
        } else {
            return new UserIconDrawable(iconSize).setIcon(icon).bake();
        }
    }

    public static String formatPercentage(double percentage, boolean round) {
        return formatPercentage(round ? Math.round((float) percentage) : (int) percentage);
    }

    public static String formatPercentage(long amount, long total) {
        return formatPercentage(((double) amount) / ((double) total));
    }

    public static String formatPercentage(int percentage) {
        return formatPercentage(((double) percentage) / 100.0d);
    }

    public static String formatPercentage(double percentage) {
        return NumberFormat.getPercentInstance().format(percentage);
    }

    public static int getBatteryLevel(Intent batteryChangedIntent) {
        return (batteryChangedIntent.getIntExtra("level", 0) * 100) / batteryChangedIntent.getIntExtra("scale", 100);
    }

    public static String getBatteryStatus(Resources res, Intent batteryChangedIntent) {
        int status = batteryChangedIntent.getIntExtra("status", 1);
        if (status == 2) {
            return res.getString(R.string.battery_info_status_charging);
        }
        if (status == 3) {
            return res.getString(R.string.battery_info_status_discharging);
        }
        if (status == 4) {
            return res.getString(R.string.battery_info_status_not_charging);
        }
        if (status == 5) {
            return res.getString(R.string.battery_info_status_full);
        }
        return res.getString(R.string.battery_info_status_unknown);
    }

    public static int getColorAccent(Context context) {
        return getColorAttr(context, 16843829);
    }

    public static int getColorError(Context context) {
        return getColorAttr(context, 16844099);
    }

    public static int getDefaultColor(Context context, int resId) {
        return context.getResources().getColorStateList(resId, context.getTheme()).getDefaultColor();
    }

    public static int getDisabled(Context context, int inputColor) {
        return applyAlphaAttr(context, 16842803, inputColor);
    }

    public static int applyAlphaAttr(Context context, int attr, int inputColor) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        float alpha = ta.getFloat(0, 0.0f);
        ta.recycle();
        return applyAlpha(alpha, inputColor);
    }

    public static int applyAlpha(float alpha, int inputColor) {
        return Color.argb((int) (alpha * ((float) Color.alpha(inputColor))), Color.red(inputColor), Color.green(inputColor), Color.blue(inputColor));
    }

    public static int getColorAttr(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int colorAccent = ta.getColor(0, 0);
        ta.recycle();
        return colorAccent;
    }

    public static int getThemeAttr(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int theme = ta.getResourceId(0, 0);
        ta.recycle();
        return theme;
    }

    public static Drawable getDrawable(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        Drawable drawable = ta.getDrawable(0);
        ta.recycle();
        return drawable;
    }

    public static boolean isSystemPackage(Resources resources, PackageManager pm, PackageInfo pkg) {
        if (sSystemSignature == null) {
            sSystemSignature = new Signature[]{getSystemSignature(pm)};
        }
        if (sPermissionControllerPackageName == null) {
            sPermissionControllerPackageName = pm.getPermissionControllerPackageName();
        }
        if (sServicesSystemSharedLibPackageName == null) {
            sServicesSystemSharedLibPackageName = pm.getServicesSystemSharedLibraryPackageName();
        }
        if (sSharedSystemSharedLibPackageName == null) {
            sSharedSystemSharedLibPackageName = pm.getSharedSystemSharedLibraryPackageName();
        }
        if ((sSystemSignature[0] == null || !sSystemSignature[0].equals(getFirstSignature(pkg))) && !pkg.packageName.equals(sPermissionControllerPackageName) && !pkg.packageName.equals(sServicesSystemSharedLibPackageName) && !pkg.packageName.equals(sSharedSystemSharedLibPackageName) && !pkg.packageName.equals("com.android.printspooler") && !isDeviceProvisioningPackage(resources, pkg.packageName)) {
            return false;
        }
        return true;
    }

    private static Signature getFirstSignature(PackageInfo pkg) {
        if (pkg == null || pkg.signatures == null || pkg.signatures.length <= 0) {
            return null;
        }
        return pkg.signatures[0];
    }

    private static Signature getSystemSignature(PackageManager pm) {
        try {
            return getFirstSignature(pm.getPackageInfo("android", 64));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static boolean isDeviceProvisioningPackage(Resources resources, String packageName) {
        String deviceProvisioningPackage = resources.getString(17039663);
        return deviceProvisioningPackage != null && deviceProvisioningPackage.equals(packageName);
    }

    public static int getWifiIconResource(int level) {
        if (level >= 0 && level < WIFI_PIE.length) {
            return WIFI_PIE[level];
        }
        throw new IllegalArgumentException("No Wifi icon found for level: " + level);
    }

    public static int getDefaultStorageManagerDaysToRetain(Resources resources) {
        try {
            return resources.getInteger(17694872);
        } catch (Resources.NotFoundException e) {
            return 90;
        }
    }

    public static boolean isWifiOnly(Context context) {
        return !((ConnectivityManager) context.getSystemService(ConnectivityManager.class)).isNetworkSupported(0);
    }

    public static boolean isStorageManagerEnabled(Context context) {
        boolean isDefaultOn;
        try {
            isDefaultOn = SystemProperties.getBoolean(STORAGE_MANAGER_ENABLED_PROPERTY, false);
        } catch (Resources.NotFoundException e) {
            isDefaultOn = false;
        }
        if (Settings.Secure.getInt(context.getContentResolver(), "automatic_storage_manager_enabled", isDefaultOn ? 1 : 0) != 0) {
            return true;
        }
        return false;
    }

    public static boolean isAudioModeOngoingCall(Context context) {
        int audioMode = ((AudioManager) context.getSystemService(AudioManager.class)).getMode();
        return audioMode == 1 || audioMode == 2 || audioMode == 3;
    }
}
