package com.android.tv.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class Partner {
    private static final String ACTION_PARTNER_CUSTOMIZATION = "com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION";
    private static final String INPUTS_ORDER = "home_screen_inputs_ordering";
    public static final String INPUT_TYPE_BUNDLED_TUNER = "input_type_combined_tuners";
    public static final String INPUT_TYPE_CEC_LOGICAL = "input_type_cec_logical";
    public static final String INPUT_TYPE_CEC_PLAYBACK = "input_type_cec_playback";
    public static final String INPUT_TYPE_CEC_RECORDER = "input_type_cec_recorder";
    public static final String INPUT_TYPE_COMPONENT = "input_type_component";
    public static final String INPUT_TYPE_COMPOSITE = "input_type_composite";
    public static final String INPUT_TYPE_DISPLAY_PORT = "input_type_displayport";
    public static final String INPUT_TYPE_DVI = "input_type_dvi";
    public static final String INPUT_TYPE_HDMI = "input_type_hdmi";
    private static final Map<String, Integer> INPUT_TYPE_MAP = new HashMap();
    public static final String INPUT_TYPE_MHL_MOBILE = "input_type_mhl_mobile";
    public static final String INPUT_TYPE_OTHER = "input_type_other";
    public static final String INPUT_TYPE_SCART = "input_type_scart";
    public static final String INPUT_TYPE_SVIDEO = "input_type_svideo";
    public static final String INPUT_TYPE_TUNER = "input_type_tuner";
    public static final String INPUT_TYPE_VGA = "input_type_vga";
    private static final String TAG = "Partner";
    private static final String TYPE_ARRAY = "array";
    private static final Object sLock = new Object();
    private static Partner sPartner;
    private final String mPackageName;
    private final String mReceiverName;
    private final Resources mResources;

    static {
        INPUT_TYPE_MAP.put(INPUT_TYPE_BUNDLED_TUNER, -3);
        INPUT_TYPE_MAP.put(INPUT_TYPE_TUNER, 0);
        INPUT_TYPE_MAP.put(INPUT_TYPE_CEC_LOGICAL, -2);
        INPUT_TYPE_MAP.put(INPUT_TYPE_CEC_RECORDER, -4);
        INPUT_TYPE_MAP.put(INPUT_TYPE_CEC_PLAYBACK, -5);
        INPUT_TYPE_MAP.put(INPUT_TYPE_MHL_MOBILE, -6);
        INPUT_TYPE_MAP.put(INPUT_TYPE_HDMI, 1007);
        INPUT_TYPE_MAP.put(INPUT_TYPE_DVI, 1006);
        INPUT_TYPE_MAP.put(INPUT_TYPE_COMPONENT, 1004);
        INPUT_TYPE_MAP.put(INPUT_TYPE_SVIDEO, 1002);
        INPUT_TYPE_MAP.put(INPUT_TYPE_COMPOSITE, 1001);
        INPUT_TYPE_MAP.put(INPUT_TYPE_DISPLAY_PORT, 1008);
        INPUT_TYPE_MAP.put(INPUT_TYPE_VGA, 1005);
        INPUT_TYPE_MAP.put(INPUT_TYPE_SCART, 1003);
        INPUT_TYPE_MAP.put(INPUT_TYPE_OTHER, 1000);
    }

    private Partner(String packageName, String receiverName, Resources res) {
        this.mPackageName = packageName;
        this.mReceiverName = receiverName;
        this.mResources = res;
    }

    public static Partner getInstance(Context context) {
        PackageManager pm = context.getPackageManager();
        synchronized (sLock) {
            ResolveInfo info = getPartnerResolveInfo(pm);
            if (info != null) {
                String packageName = info.activityInfo.packageName;
                try {
                    sPartner = new Partner(packageName, info.activityInfo.name, pm.getResourcesForApplication(packageName));
                    sPartner.sendInitBroadcast(context);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(TAG, "Failed to find resources for " + packageName);
                }
            }
            if (sPartner == null) {
                sPartner = new Partner((String) null, (String) null, (Resources) null);
            }
        }
        return sPartner;
    }

    public static void reset(Context context, String packageName) {
        synchronized (sLock) {
            if (sPartner != null && !TextUtils.isEmpty(packageName) && packageName.equals(sPartner.mPackageName)) {
                sPartner = null;
                getInstance(context);
            }
        }
    }

    private void sendInitBroadcast(Context context) {
        if (!TextUtils.isEmpty(this.mPackageName) && !TextUtils.isEmpty(this.mReceiverName)) {
            Intent intent = new Intent(ACTION_PARTNER_CUSTOMIZATION);
            intent.setComponent(new ComponentName(this.mPackageName, this.mReceiverName));
            intent.setFlags(268435456);
            context.sendBroadcast(intent);
        }
    }

    public Map<Integer, Integer> getInputsOrderMap() {
        HashMap<Integer, Integer> map = new HashMap<>();
        if (this.mResources != null && !TextUtils.isEmpty(this.mPackageName)) {
            String[] inputsArray = null;
            int resId = this.mResources.getIdentifier(INPUTS_ORDER, TYPE_ARRAY, this.mPackageName);
            if (resId != 0) {
                inputsArray = this.mResources.getStringArray(resId);
            }
            if (inputsArray != null) {
                int priority = 0;
                for (String input : inputsArray) {
                    Integer type = INPUT_TYPE_MAP.get(input);
                    if (type != null) {
                        map.put(type, Integer.valueOf(priority));
                        priority++;
                    }
                }
            }
        }
        return map;
    }

    private static ResolveInfo getPartnerResolveInfo(PackageManager pm) {
        for (ResolveInfo info : pm.queryBroadcastReceivers(new Intent(ACTION_PARTNER_CUSTOMIZATION), 0)) {
            if (isSystemApp(info)) {
                return info;
            }
        }
        return null;
    }

    protected static boolean isSystemApp(ResolveInfo info) {
        return (info.activityInfo == null || info.activityInfo.applicationInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) ? false : true;
    }
}
