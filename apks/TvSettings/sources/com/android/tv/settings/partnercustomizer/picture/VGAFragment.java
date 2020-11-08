package com.android.tv.settings.partnercustomizer.picture;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import java.util.ArrayList;
import java.util.List;

public class VGAFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "VGAFragment";
    private final int MSG_AUTOADJUST = 1;
    /* access modifiers changed from: private */
    public MtkTvAppTVBase appTV;
    /* access modifiers changed from: private */
    public int autoTimeOut = 0;
    private ContentResolver contentResolver;
    /* access modifiers changed from: private */
    public boolean flag = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkLog.d(VGAFragment.TAG, "mHandler  " + msg.what);
            if (msg.what == 1) {
                removeMessages(1);
                boolean unused = VGAFragment.this.flag = VGAFragment.this.appTV.AutoColorCondSuccess("main");
                VGAFragment.access$208(VGAFragment.this);
                if (VGAFragment.this.flag || VGAFragment.this.autoTimeOut >= 5) {
                    int unused2 = VGAFragment.this.autoTimeOut = 0;
                } else {
                    sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private SharedPreferences mSharedPreferences;
    private TVSettingConfig mTVSettingConfig;

    static /* synthetic */ int access$208(VGAFragment x0) {
        int i = x0.autoTimeOut;
        x0.autoTimeOut = i + 1;
        return i;
    }

    public static VGAFragment newInstance(String key) {
        return new VGAFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.partner_black_pref, (String) null);
        MtkLog.d(TAG, "onCreatePreferences");
        this.contentResolver = getContext().getContentResolver();
        this.mTVSettingConfig = TVSettingConfig.getInstance(getContext());
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        createPreferences();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        this.appTV = new MtkTvAppTVBase();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String prefKey = preference.getKey();
        MtkLog.d(TAG, "onPreferenceTreeClick : prefKey = " + prefKey);
        if (TextUtils.equals(prefKey, PreferenceConfigUtils.KEY_PICTURE_VGA_AUTO)) {
            this.appTV.setAutoClockPhasePostion("main");
            this.mHandler.sendEmptyMessage(1);
        }
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        MtkLog.e(TAG, "onPreferenceChange preference == " + preferenceKey + "  " + ((String) newValue));
        this.mPreferenceConfigUtils.updatePreferenceChanged(this, preference, newValue);
        return true;
    }

    private void createPreferences() {
        PreferenceScreen mScreen = getPreferenceScreen();
        mScreen.setTitle((int) R.string.device_picture_vga);
        new ArrayList();
        List<String> prefKeys = PartnerSettingsConfig.getSettingsList("picture_vga");
        if (prefKeys != null) {
            MtkLog.e(TAG, "prefKeys " + prefKeys.size());
            for (int i = 0; i < prefKeys.size(); i++) {
                String prefKey = prefKeys.get(i);
                MtkLog.d(TAG, "prefKeys " + prefKey);
                if (prefKey == null) {
                    MtkLog.e(TAG, "prefKey is null");
                    return;
                }
                if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_VGA_AUTO)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createPreference((LeanbackPreferenceFragment) this, prefKey, (int) R.string.device_picture_vga_auto, (String) null));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_VGA_HP)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_vga_hp, "g_vga__vga_pos_h", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_VGA_VP)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_vga_vp, "g_vga__vga_pos_v", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_VGA_PHASE)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_vga_phase, "g_vga__vga_phase", 1));
                } else if (prefKey.equals(PreferenceConfigUtils.KEY_PICTURE_VGA_CLOCK)) {
                    mScreen.addPreference(this.mPreferenceConfigUtils.createProgressPreference(this, prefKey, R.string.device_picture_vga_clock, "g_vga__vga_clock", 1));
                }
            }
        }
    }

    public int getMetricsCategory() {
        return 336;
    }
}
