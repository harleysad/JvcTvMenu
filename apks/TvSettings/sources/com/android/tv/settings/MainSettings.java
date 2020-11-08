package com.android.tv.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import com.android.tv.settings.accessibility.AccessibilityFragment;
import com.android.tv.settings.device.sound.SoundFragment;
import com.android.tv.settings.partnercustomizer.audiosubtitle.AudioSubtitleFragment;
import com.android.tv.settings.partnercustomizer.captions.CaptionFusionFragment;
import com.android.tv.settings.partnercustomizer.device.Device3RdPrefFragment;
import com.android.tv.settings.partnercustomizer.picture.PictureFragment;
import com.android.tv.settings.partnercustomizer.picture.PictureModeFragment;
import com.android.tv.settings.partnercustomizer.power.PowerFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVMenuSettingsService;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.wwtv.tvcenter.util.Constants;
import com.mediatek.wwtv.tvcenter.util.EventHelper;
import com.mediatek.wwtv.tvcenter.util.MtkLog;

public class MainSettings extends TvSettingsActivity {
    private static final String TAG = "MainSettings";
    public static final EventHelper mEventHelper = new EventHelper();
    private boolean isFirstStarted = false;
    public final BroadcastReceiver mExitReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MtkLog.d(MainSettings.TAG, "mExitReceiver,intentAction()==" + intent.getAction());
            if (Constants.MTK_ACTION_EXIT_TVSETTINGSPLUS.equals(intent.getAction())) {
                MtkLog.d(MainSettings.TAG, "mExitReceiver,finish()");
                MainSettings.this.finish();
            }
        }
    };

    /* access modifiers changed from: protected */
    public Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, TVMenuSettingsService.class));
        mEventHelper.updateIntent(getIntent());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MTK_ACTION_EXIT_TVSETTINGSPLUS);
        registerReceiver(this.mExitReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        TVSettingConfig instance = TVSettingConfig.getInstance(getApplicationContext());
        if (Settings.Global.getInt(getApplicationContext().getContentResolver(), PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1) == 0) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(), PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1);
        }
        unregisterReceiver(this.mExitReceiver);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        MtkLog.d(TAG, "dispatchKeyEvent" + event);
        TVSettingConfig instance = TVSettingConfig.getInstance(getApplicationContext());
        if (Settings.Global.getInt(getApplicationContext().getContentResolver(), PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1) == 0 && event.getAction() == 1) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(), PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1);
            MtkLog.d(TAG, "KEY_POWER_PICTURE_OFF is 0, return");
            return true;
        }
        boolean done = super.dispatchKeyEvent(event);
        if (event.getAction() == 1 && (event.getKeyCode() == 4 || event.getKeyCode() == 66 || event.getKeyCode() == 23)) {
            if (mEventHelper.isEvent(1024) || mEventHelper.isEvent(128) || mEventHelper.isEvent(16384) || mEventHelper.isEvent(2048) || mEventHelper.isEvent(524288) || mEventHelper.isEvent(1048576)) {
                mEventHelper.updateIntent(new Intent());
                finish();
                MtkLog.d(TAG, "finish by hot key.");
            } else if (mEventHelper.isEvent(65536)) {
                MtkLog.d(TAG, "sleep key received");
                finish();
            }
        }
        return done;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mEventHelper.updateIntent(intent);
    }

    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            MtkLog.d(MainSettings.TAG, "mExitReceiver==" + MainSettings.mEventHelper.isEvent(524288));
            if (MainSettings.mEventHelper.isEvent(2)) {
                startPreferenceFragment(Device3RdPrefFragment.newInstance());
            } else if (MainSettings.mEventHelper.isEvent(4096)) {
                startPreferenceFragment(PictureFragment.newInstance());
            } else if (MainSettings.mEventHelper.isEvent(8192)) {
                startPreferenceFragment(SoundFragment.newInstance());
            } else if (MainSettings.mEventHelper.isEvent(2048)) {
                startPreferenceFragment(SoundFragment.newInstance(PreferenceConfigUtils.KEY_SOUND_SPEAKERS));
            } else if (MainSettings.mEventHelper.isEvent(16384)) {
                startPreferenceFragment(SoundFragment.newInstance(PreferenceConfigUtils.KEY_SOUND_STYLE));
            } else if (MainSettings.mEventHelper.isEvent(1024)) {
                startPreferenceFragment(PictureModeFragment.newInstance());
            } else if (MainSettings.mEventHelper.isEvent(64)) {
                startPreferenceFragment(CaptionFusionFragment.newInstance());
            } else if (MainSettings.mEventHelper.isEvent(128)) {
                startPreferenceFragment(PictureFragment.newInstance(PreferenceConfigUtils.KEY_PICTURE_FORMAT));
            } else if (MainSettings.mEventHelper.isEvent(512)) {
                startPreferenceFragment(PowerFragment.newInstance());
            } else if (!MainSettings.mEventHelper.isEvent(256)) {
                if (MainSettings.mEventHelper.isEvent(65536)) {
                    startPreferenceFragment(PowerFragment.newInstance(PreferenceConfigUtils.KEY_POWER_SLEEP_TIMER));
                } else if (MainSettings.mEventHelper.isEvent(131072)) {
                    startPreferenceFragment(AccessibilityFragment.newInstance());
                } else if (MainSettings.mEventHelper.isEvent(524288)) {
                    startPreferenceFragment(new AudioSubtitleFragment(PreferenceConfigUtils.KEY_AUDIO));
                } else if (MainSettings.mEventHelper.isEvent(1048576)) {
                    startPreferenceFragment(new AudioSubtitleFragment(PreferenceConfigUtils.KEY_SUBTITLE));
                } else if (MainSettings.mEventHelper.isEvent(1)) {
                    MainFragment fragment = MainFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isFromLiveTV", MainSettings.mEventHelper.isEvent(8));
                    fragment.setArguments(bundle);
                    startPreferenceFragment(fragment);
                }
            }
        }
    }
}
