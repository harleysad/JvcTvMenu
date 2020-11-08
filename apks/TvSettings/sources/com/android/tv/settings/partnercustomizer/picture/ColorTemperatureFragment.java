package com.android.tv.settings.partnercustomizer.picture;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;

public class ColorTemperatureFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "ColorTemperatureFragment";
    private static ColorTemperatureFragment ctf;
    private final int MSG_UPDATE_COLOR_TEMP_ITEM = 256;
    private ContentResolver contentResolver;
    private PreferenceConfigUtils mConfigUtils;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 256) {
                ColorTemperatureFragment.this.updateColorTempItemValue();
            }
        }
    };
    private SharedPreferences mSharedPreferences;

    public static ColorTemperatureFragment newInstance() {
        if (ctf == null) {
            ctf = new ColorTemperatureFragment();
        }
        return ctf;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.contentResolver = getContext().getContentResolver();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        this.mConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture_color_temperature);
        mScreen.addPreference(this.mConfigUtils.createListPreference((LeanbackPreferenceFragment) this, "picture_color_temperature", (int) R.string.device_picture_color_temperature, (int) R.array.picture_color_temperature_entries, (int) R.array.picture_color_temperature_entry_values, "g_video__clr_temp"));
        mScreen.addPreference(this.mConfigUtils.createProgressPreference(this, PreferenceConfigUtils.KEY_PICTURE_RED_GAIN, R.string.device_picture_red_gain, "g_video__clr_gain_r", 1));
        mScreen.addPreference(this.mConfigUtils.createProgressPreference(this, PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN, R.string.device_picture_green_gain, "g_video__clr_gain_g", 1));
        mScreen.addPreference(this.mConfigUtils.createProgressPreference(this, PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN, R.string.device_picture_blue_gain, "g_video__clr_gain_b", 1));
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(android.support.v7.preference.Preference r7, java.lang.Object r8) {
        /*
            r6 = this;
            java.lang.String r0 = r7.getKey()
            com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils r1 = r6.mConfigUtils
            r1.updatePreferenceChanged(r6, r7, r8)
            int r1 = r0.hashCode()
            r2 = -1362055908(0xffffffffaed0ad1c, float:-9.489495E-11)
            r3 = 1
            if (r1 == r2) goto L_0x0041
            r2 = -771843689(0xffffffffd1fe9997, float:-1.36687313E11)
            if (r1 == r2) goto L_0x0037
            r2 = 8753294(0x85908e, float:1.2265977E-38)
            if (r1 == r2) goto L_0x002d
            r2 = 807841347(0x3026ae43, float:6.063809E-10)
            if (r1 == r2) goto L_0x0023
            goto L_0x004b
        L_0x0023:
            java.lang.String r1 = "picture_blue_gain"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x004b
            r1 = 3
            goto L_0x004c
        L_0x002d:
            java.lang.String r1 = "picture_red_gain"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x004b
            r1 = r3
            goto L_0x004c
        L_0x0037:
            java.lang.String r1 = "picture_color_temperature"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x004b
            r1 = 0
            goto L_0x004c
        L_0x0041:
            java.lang.String r1 = "picture_green_gain"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x004b
            r1 = 2
            goto L_0x004c
        L_0x004b:
            r1 = -1
        L_0x004c:
            switch(r1) {
                case 0: goto L_0x006f;
                case 1: goto L_0x006b;
                case 2: goto L_0x006b;
                case 3: goto L_0x006b;
                default: goto L_0x004f;
            }
        L_0x004f:
            java.lang.String r1 = "ColorTemperatureFragment"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "onPreferenceChange preference "
            r2.append(r4)
            r2.append(r0)
            java.lang.String r4 = " not handled."
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Log.e(r1, r2)
            goto L_0x0079
        L_0x006b:
            r6.setColorTempToUser()
            goto L_0x0079
        L_0x006f:
            android.os.Handler r1 = r6.mHandler
            r2 = 256(0x100, float:3.59E-43)
            r4 = 1000(0x3e8, double:4.94E-321)
            r1.sendEmptyMessageDelayed(r2, r4)
        L_0x0079:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tv.settings.partnercustomizer.picture.ColorTemperatureFragment.onPreferenceChange(android.support.v7.preference.Preference, java.lang.Object):boolean");
    }

    /* access modifiers changed from: private */
    public void updateColorTempItemValue() {
        this.mConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_PICTURE_RED_GAIN), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_RED_GAIN)));
        this.mConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_GREEN_GAIN)));
        this.mConfigUtils.onPreferenceValueChange(findPreference(PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN), Integer.valueOf(PreferenceConfigUtils.getSettingValueInt(this.contentResolver, PreferenceConfigUtils.KEY_PICTURE_BLUE_GAIN)));
    }

    private void setColorTempToUser() {
        this.mConfigUtils.updatePreferenceChanged(this, findPreference("picture_color_temperature"), 0);
    }

    public int getMetricsCategory() {
        return 336;
    }
}
