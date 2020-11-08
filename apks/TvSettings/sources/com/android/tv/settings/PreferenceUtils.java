package com.android.tv.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.util.Log;
import com.android.tv.settings.partnercustomizer.utils.ProgressPreference;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public final class PreferenceUtils {
    public static final int FLAG_SET_TITLE = 1;
    private static final String TAG = "PreferenceUtils";
    public static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void resolveSystemActivityOrRemove(Context context, PreferenceGroup parent, Preference preference, int flags) {
        if (preference != null) {
            mContext = context;
            Intent intent = preference.getIntent();
            if (intent != null) {
                PackageManager pm = context.getPackageManager();
                for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
                    if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                        preference.setIntent(new Intent().setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                        if ((flags & 1) != 0) {
                            preference.setTitle(resolveInfo.loadLabel(pm));
                            return;
                        }
                        return;
                    }
                }
            }
            parent.removePreference(preference);
        }
    }

    public static boolean isPictureSettingEnabled(Context context) {
        return SystemProperties.get("ro.config.tv_advanced_settings").equals("1");
    }

    public static void setupSeekBarPreference(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        ProgressPreference pref = (ProgressPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            pref.setCurrentValue(getSettingIntValue(fragment.getContext().getContentResolver(), settingKey, pref.getCurrentValue()));
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
                return;
            }
            return;
        }
        Log.e(TAG, "Cannot find preference for " + preferenceKey);
    }

    public static ProgressPreference setupSeekBarPreferences(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        ProgressPreference pref = (ProgressPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            pref.setCurrentValue(getSettingIntValue(fragment.getContext().getContentResolver(), settingKey, pref.getCurrentValue()));
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
            }
        } else {
            Log.e(TAG, "Cannot find preference for " + preferenceKey);
        }
        return pref;
    }

    public static ProgressPreference setupSeekBarPreferences(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey, int MaxVal, int MinVal, int stepIncrement) {
        ProgressPreference pref = (ProgressPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            int value = getSettingIntValue(fragment.getContext().getContentResolver(), settingKey, pref.getCurrentValue());
            pref.setMaxValue(MaxVal);
            pref.setMinValue(MinVal);
            pref.setCurrentValue(value);
            pref.setmStep(stepIncrement);
            pref.setPositionView(false);
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
            }
        } else {
            Log.e(TAG, "Cannot find preference for " + preferenceKey);
        }
        return pref;
    }

    public static ListPreference setupListPreference(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        ListPreference pref = (ListPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            pref.setPersistent(false);
            String setting = getSettingStringValue(fragment.getContext().getContentResolver(), settingKey);
            Log.d(TAG, "setupListPreference Settings.Global.getString: " + setting);
            if (setting != null) {
                int index = pref.findIndexOfValue(setting);
                if (index >= 0) {
                    Log.d(TAG, "setupListPreference findIndexOfValue index =" + index);
                    pref.setValueIndex(index);
                }
            } else {
                pref.setValueIndex(0);
                pref.setValue((String) pref.getEntryValues()[0]);
                putSettingValueString(fragment.getContext().getContentResolver(), preferenceKey, (String) pref.getEntryValues()[0]);
            }
            Log.d(TAG, "setting:" + setting);
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
            }
        } else {
            Log.e(TAG, "Cannot find preference for " + preferenceKey);
        }
        return pref;
    }

    public static ListPreference setupListPreferences(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        ListPreference pref = (ListPreference) fragment.findPreference(preferenceKey);
        String setting = getSettingStringValue(fragment.getContext().getContentResolver(), settingKey);
        if (pref != null) {
            MtkLog.d(TAG, "void: setupListPreferences = setting : " + setting);
            if (setting != null) {
                pref.setValue(setting.toLowerCase());
            }
            if (fragment instanceof Preference.OnPreferenceChangeListener) {
                pref.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) fragment);
            } else {
                MtkLog.e(TAG, "Cannot find preference for " + preferenceKey);
            }
        }
        return pref;
    }

    public static void setupSwitchPreference(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        SwitchPreference pref = (SwitchPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            pref.setChecked(getSettingIntValue(fragment.getContext().getContentResolver(), settingKey, pref.isChecked() ? 1 : 0) != 0);
            return;
        }
        Log.e(TAG, "Cannot find preference for " + preferenceKey);
    }

    public static SwitchPreference setupSwitchPreferences(LeanbackPreferenceFragment fragment, String preferenceKey, String settingKey) {
        SwitchPreference pref = (SwitchPreference) fragment.findPreference(preferenceKey);
        if (pref != null) {
            pref.setChecked(getSettingIntValue(fragment.getContext().getContentResolver(), settingKey, pref.isChecked() ? 1 : 0) != 0);
        } else {
            Log.e(TAG, "Cannot find preference for " + preferenceKey);
        }
        return pref;
    }

    public static int getSettingIntValue(ContentResolver mResolver, String settingKey, int defValue) {
        int value = Settings.Global.getInt(mResolver, settingKey, defValue);
        MtkLog.d(TAG, "getSettingIntValue : settingKey = " + settingKey + "  value = " + value);
        return value;
    }

    public static String getSettingStringValue(ContentResolver mResolver, String settingKey) {
        String value = Settings.Global.getString(mResolver, settingKey);
        MtkLog.d(TAG, "getSettingStringValue : settingKey = " + settingKey + "  value = " + value);
        return value;
    }

    public static void putSettingValueInt(ContentResolver mResolver, String settingKey, int settingValue) {
        MtkLog.d(TAG, "putSettingValueInt : settingKey = " + settingKey + " settingValue = " + settingValue);
        MtkLog.printStackTrace();
        Settings.Global.putInt(mResolver, settingKey, settingValue);
    }

    public static void putSettingValueString(ContentResolver mResolver, String settingKey, String settingValue) {
        MtkLog.d(TAG, "putSettingValueString : settingKey " + settingKey + " settingValue : " + settingValue);
        MtkLog.printStackTrace();
        Settings.Global.putString(mResolver, settingKey, settingValue);
    }

    public static void putSettingValue(ContentResolver mResolver, String settingKey, Object settingValue) {
        if (settingValue instanceof String) {
            putSettingValueString(mResolver, settingKey, (String) settingValue);
        } else if (settingValue instanceof Integer) {
            putSettingValueInt(mResolver, settingKey, ((Integer) settingValue).intValue());
        } else {
            MtkLog.d(TAG, "PutSettingValue : setting Vaule none of int or String !");
        }
    }
}
