package com.android.tv.settings.partnercustomizer.power;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.partnercustomizer.switchofftimer.SwitchOffTimerService;
import com.android.tv.settings.partnercustomizer.timer.TimerFragment;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.android.tv.settings.partnercustomizer.tvsettingservice.util.PreferenceConfigUtils;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.wwtv.tvcenter.util.InstrumentationHandler;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.PartnerSettingsConfig;
import com.mediatek.wwtv.tvcenter.util.TextToSpeechUtil;
import java.util.ArrayList;

public class PowerFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String SOURCEACTION = "mtk.intent.input.source";
    private static final String SWITCH_OFF_TIMER_SERVICE_NAME = "com.android.tv.settings.partnercustomizer.switchofftimer.SwitchOffTimerService";
    private static final String TAG = "PowerFragment";
    private String[] entries;
    private final IntentFilter ifilter;
    /* access modifiers changed from: private */
    public ContentResolver mContentResolver;
    private Context mContext;
    private PreferenceConfigUtils mPreferenceConfigUtils;
    private final BroadcastReceiver mReceiver;
    private ListPreference mSleepTimerPref;
    private String mStartKey;
    /* access modifiers changed from: private */
    public TVSettingConfig mTVSettingConfig;
    boolean registed;

    public static PowerFragment newInstance() {
        return new PowerFragment();
    }

    public static PowerFragment newInstance(String key) {
        return new PowerFragment(key);
    }

    public PowerFragment() {
        this.entries = new String[]{"Off", "10 Minutes", "20 Minutes", "30 Minutes", "40 Minutes", "50 Minutes", "60 Minutes", "90 Minutes", "120 Minutes"};
        this.mStartKey = null;
        this.registed = false;
        this.ifilter = new IntentFilter();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(PowerFragment.TAG, "onReceiveintent.getAction():" + intent.getAction());
                if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) || PowerFragment.SOURCEACTION.equals(intent.getAction())) {
                    Log.i(PowerFragment.TAG, "received ACTION_CLOSE_SYSTEM_DIALOGS || SOURCEACTION");
                    if (PowerFragment.this.mTVSettingConfig.isPictureOff()) {
                        Settings.Global.putInt(PowerFragment.this.mContentResolver, PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1);
                    }
                }
            }
        };
        this.mStartKey = null;
    }

    public PowerFragment(String key) {
        this.entries = new String[]{"Off", "10 Minutes", "20 Minutes", "30 Minutes", "40 Minutes", "50 Minutes", "60 Minutes", "90 Minutes", "120 Minutes"};
        this.mStartKey = null;
        this.registed = false;
        this.ifilter = new IntentFilter();
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i(PowerFragment.TAG, "onReceiveintent.getAction():" + intent.getAction());
                if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) || PowerFragment.SOURCEACTION.equals(intent.getAction())) {
                    Log.i(PowerFragment.TAG, "received ACTION_CLOSE_SYSTEM_DIALOGS || SOURCEACTION");
                    if (PowerFragment.this.mTVSettingConfig.isPictureOff()) {
                        Settings.Global.putInt(PowerFragment.this.mContentResolver, PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1);
                    }
                }
            }
        };
        this.mStartKey = key;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MtkLog.d(TAG, "onCreate");
        register();
    }

    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        if (this.mTVSettingConfig.isPictureOff()) {
            Settings.Global.putInt(this.mContentResolver, PreferenceConfigUtils.KEY_POWER_PICTURE_OFF, 1);
        }
        super.onResume();
        updatePrefEnabled();
        showPreferenceFromStartkey();
    }

    private void showPreferenceFromStartkey() {
        MtkLog.d(TAG, "showPreferenceFromStartkey,mStartKey==" + this.mStartKey);
        if (this.mStartKey != null) {
            String str = this.mStartKey;
            char c = 65535;
            if (str.hashCode() == 1162699683 && str.equals(PreferenceConfigUtils.KEY_POWER_SLEEP_TIMER)) {
                c = 0;
            }
            if (c == 0) {
                ListPreference startPreference = (ListPreference) findPreference(this.mStartKey);
                startPreference.getPreferenceManager().showDialog(startPreference);
            }
        }
    }

    private void updatePrefEnabled() {
        int remaintime = this.mTVSettingConfig.getSleepTimerRemaining();
        Long timeLeft = 0L;
        if (remaintime != 0) {
            timeLeft = Long.valueOf((((long) this.mTVSettingConfig.getSleepTimerRemaining()) / 60) + 1);
        }
        Log.d(TAG, "remaintime:" + remaintime + ",timeLeft:" + timeLeft);
        if (timeLeft.longValue() > 0) {
            int mInitValue = this.mTVSettingConfig.getIndexByLeftTime(timeLeft);
            if (mInitValue == 0) {
                mInitValue = 1;
            }
            this.mSleepTimerPref.setValueIndex(mInitValue);
            return;
        }
        this.mSleepTimerPref.setValueIndex(0);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregister();
        super.onDestroy();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        MtkLog.d(TAG, "onCreatePreferences");
        setPreferencesFromResource(R.xml.partner_power, (String) null);
        this.mContext = getContext();
        this.mContentResolver = this.mContext.getContentResolver();
        this.mTVSettingConfig = TVSettingConfig.getInstance(this.mContext);
        this.mPreferenceConfigUtils = PreferenceConfigUtils.getInstance(getPreferenceManager().getContext());
        setupPreferences();
    }

    private void setupPreferences() {
        ArrayList<String> configList = PartnerSettingsConfig.getSettingsList(PreferenceConfigUtils.KEY_POWER_EFFECTS);
        this.mSleepTimerPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_POWER_SLEEP_TIMER);
        String[] mSleepTimerArray = this.mContext.getResources().getStringArray(R.array.timer_sleep_timer_entries);
        for (int i = 0; i < mSleepTimerArray.length; i++) {
            if ("0".equalsIgnoreCase(mSleepTimerArray[i])) {
                mSleepTimerArray[i] = this.mContext.getResources().getString(R.string.pic_advance_video_entries_off);
            } else {
                mSleepTimerArray[i] = this.mContext.getResources().getString(R.string.timer_sleep_timer_entrie, new Object[]{Integer.valueOf(Integer.parseInt(mSleepTimerArray[i]))});
            }
            MtkLog.d(TAG, "setupPreferences, mSleepTimerArray[i] = " + mSleepTimerArray[i]);
        }
        this.mSleepTimerPref.setEntries((CharSequence[]) mSleepTimerArray);
        if (configList == null || configList.contains(PreferenceConfigUtils.KEY_POWER_SLEEP_TIMER)) {
            this.mSleepTimerPref.setOnPreferenceChangeListener(this);
            int remaintime = this.mTVSettingConfig.getSleepTimerRemaining();
            Long timeLeft = 0L;
            if (remaintime != 0) {
                timeLeft = Long.valueOf((((long) this.mTVSettingConfig.getSleepTimerRemaining()) / 60) + 1);
            }
            Log.d(TAG, "remaintime:" + remaintime + ",timeLeft:" + timeLeft);
            if (timeLeft.longValue() > 0) {
                int mInitValue = this.mTVSettingConfig.getIndexByLeftTime(timeLeft);
                if (mInitValue == 0) {
                    mInitValue = 1;
                }
                this.mSleepTimerPref.setValueIndex(mInitValue);
            } else {
                this.mSleepTimerPref.setValueIndex(0);
            }
        } else {
            getPreferenceScreen().removePreference(this.mSleepTimerPref);
        }
        ListPreference switchOffTimerPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER);
        String[] mSwitchOffTimerArray = this.mContext.getResources().getStringArray(R.array.power_switch_off_timer_entries_latest);
        for (int i2 = 0; i2 < mSwitchOffTimerArray.length; i2++) {
            if ("0".equalsIgnoreCase(mSwitchOffTimerArray[i2])) {
                mSwitchOffTimerArray[i2] = this.mContext.getResources().getString(R.string.pic_advance_video_entries_off);
            } else {
                mSwitchOffTimerArray[i2] = this.mContext.getResources().getString(R.string.timer_sleep_timer_entrie, new Object[]{Integer.valueOf(Integer.parseInt(mSwitchOffTimerArray[i2]))});
            }
            MtkLog.d(TAG, "setupPreferences, mSwitchOffTimerArray[i] = " + mSwitchOffTimerArray[i2]);
        }
        switchOffTimerPref.setEntries((CharSequence[]) mSwitchOffTimerArray);
        if (configList == null || configList.contains(PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER)) {
            String switchOffTimerValue = Settings.Global.getString(this.mContentResolver, PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER);
            if (switchOffTimerValue == null) {
                switchOffTimerValue = "0";
            }
            switchOffTimerPref.setValue(switchOffTimerValue);
            switchOffTimerPref.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(switchOffTimerPref);
        }
        ListPreference mNoSiganlAutoPowerOffPref = (ListPreference) findPreference(PreferenceConfigUtils.KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF);
        String[] mNoSignalAutoPowerArray = this.mContext.getResources().getStringArray(R.array.no_signal_auto_power_off_entries);
        for (int i3 = 0; i3 < mNoSignalAutoPowerArray.length; i3++) {
            if ("0".equalsIgnoreCase(mNoSignalAutoPowerArray[i3])) {
                mNoSignalAutoPowerArray[i3] = this.mContext.getResources().getString(R.string.pic_advance_video_entries_off);
            } else {
                mNoSignalAutoPowerArray[i3] = this.mContext.getResources().getString(R.string.timer_sleep_timer_entrie, new Object[]{Integer.valueOf(Integer.parseInt(mNoSignalAutoPowerArray[i3]))});
            }
            MtkLog.d(TAG, "setupPreferences, mNoSignalAutoPowerArray[i] = " + mNoSignalAutoPowerArray[i3]);
        }
        mNoSiganlAutoPowerOffPref.setEntries((CharSequence[]) mNoSignalAutoPowerArray);
        if (configList == null || configList.contains(PreferenceConfigUtils.KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF)) {
            String mNoSiganlAutoPowerOffValue = Settings.Global.getString(this.mContentResolver, PreferenceConfigUtils.KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF);
            if (mNoSiganlAutoPowerOffValue == null) {
                mNoSiganlAutoPowerOffValue = MtkTvRatingConvert2Goo.RATING_STR_3;
            }
            mNoSiganlAutoPowerOffPref.setValue(mNoSiganlAutoPowerOffValue);
            mNoSiganlAutoPowerOffPref.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(mNoSiganlAutoPowerOffPref);
        }
        MtkLog.d(TAG, "setupPreferences, picture off~ ");
        Preference mPictureOffPref = findPreference(PreferenceConfigUtils.KEY_POWER_PICTURE_OFF);
        if ((configList != null && !configList.contains(PreferenceConfigUtils.KEY_POWER_PICTURE_OFF)) || TextToSpeechUtil.isTTSEnabled(this.mContext)) {
            getPreferenceScreen().removePreference(mPictureOffPref);
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        String preferenceKey = preference.getKey();
        Log.d(TAG, "onPreferenceTreeClick : preferenceKey = " + preferenceKey);
        if (!TextUtils.equals(preferenceKey, PreferenceConfigUtils.KEY_POWER_PICTURE_OFF)) {
            return super.onPreferenceTreeClick(preference);
        }
        if (!TVSettingConfig.getInstance(this.mContext).isPictureOff()) {
            Log.d(TAG, "onPreferenceTreeClick isPictureOff false");
            Settings.Global.putInt(this.mContentResolver, preferenceKey, 0);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        Log.d(TAG, "preferenceKey:" + preferenceKey + ",newValue:" + newValue);
        if (this.mStartKey != null) {
            Log.d(TAG, "send KEYCODE_BACK");
            InstrumentationHandler.getInstance().sendKeyDownUpSync(4);
        }
        if (TextUtils.equals(preferenceKey, PreferenceConfigUtils.KEY_POWER_NO_SIGNAL_AUTO_POWER_OFF)) {
            Settings.Global.putInt(this.mContentResolver, preferenceKey, new Integer((String) newValue).intValue());
        }
        if (TextUtils.equals(preferenceKey, PreferenceConfigUtils.KEY_POWER_SLEEP_TIMER)) {
            Settings.Global.putInt(this.mContentResolver, "tv_timer_sleep_timer_entry_values", new Integer((String) newValue).intValue());
            Log.d(TAG, "sleep>>>>>" + newValue);
            setSleepTimerType();
            return true;
        } else if (!TextUtils.equals(preferenceKey, PreferenceConfigUtils.KEY_POWER_SWITCH_OFF_TIMER)) {
            return true;
        } else {
            String selection = (String) newValue;
            Log.v(TAG, "power_switch_off_timer:selection= " + selection);
            int SwitchOffTimerValue = Integer.parseInt(selection);
            boolean putInt = Settings.Global.putInt(this.mContentResolver, preferenceKey, SwitchOffTimerValue);
            if (SwitchOffTimerValue <= 0) {
                Log.d(TAG, "STOP SwitchOffTimerService...");
                getActivity().stopService(new Intent(getActivity(), SwitchOffTimerService.class));
                return true;
            } else if (!isServiceRunningCheck(SWITCH_OFF_TIMER_SERVICE_NAME)) {
                Log.d(TAG, "START SwitchOffTimerService...");
                Intent intentSvc = new Intent(getActivity(), SwitchOffTimerService.class);
                intentSvc.putExtra("SwitchOffTimerValue", SwitchOffTimerValue);
                getActivity().startService(intentSvc);
                return true;
            } else {
                Log.d(TAG, "RE-start SwitchOffTimerService...");
                getActivity().stopService(new Intent(getActivity(), SwitchOffTimerService.class));
                Intent intentSvc2 = new Intent(getActivity(), SwitchOffTimerService.class);
                intentSvc2.putExtra("SwitchOffTimerValue", SwitchOffTimerValue);
                getActivity().startService(intentSvc2);
                return true;
            }
        }
    }

    private void setSleepTimerType() {
        int lastValue;
        long mills;
        int value = Settings.Global.getInt(this.mContentResolver, "tv_timer_sleep_timer_entry_values", 0);
        int remaintime = this.mTVSettingConfig.getSleepTimerRemaining();
        Long timeLeft = 0L;
        if (remaintime != 0) {
            timeLeft = Long.valueOf((((long) this.mTVSettingConfig.getSleepTimerRemaining()) / 60) + 1);
        }
        if (timeLeft.longValue() > 0) {
            lastValue = getIndexByLeftTime(timeLeft);
        } else {
            lastValue = 0;
        }
        if (remaintime != 0) {
            Long timeLeft2 = Long.valueOf((((long) this.mTVSettingConfig.getSleepTimerRemaining()) / 60) + 1);
        }
        Log.d(TAG, "handleSleepTimerChange value:" + value + "  lastValue:" + lastValue);
        if (value > lastValue) {
            if (lastValue == 0 && value == this.entries.length - 1) {
                this.mTVSettingConfig.setSleepTimer(false);
            } else {
                int times = value - lastValue;
                while (times > 0) {
                    times--;
                    this.mTVSettingConfig.setSleepTimer(true);
                }
            }
        } else if (value == 0 && lastValue == this.entries.length - 1) {
            this.mTVSettingConfig.setSleepTimer(true);
        } else {
            int times2 = lastValue - value;
            while (times2 > 0) {
                times2--;
                this.mTVSettingConfig.setSleepTimer(false);
            }
        }
        String text = this.entries[value].toString();
        if (text.contains(" ")) {
            try {
                mills = ((long) (Integer.parseInt(text.substring(0, text.indexOf(" "))) * 60)) * 1000;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mills = 300000;
            } catch (Exception e2) {
                mills = 300000;
            }
            Intent intent = new Intent(TimerFragment.SLEEP_TIMER_ACTION);
            intent.putExtra("itemId", "SETUP_sleep_timer");
            intent.putExtra("mills", mills);
            this.mContext.sendBroadcast(intent);
            Log.d(TAG, "sleep timer sendbroadcast item:" + text + ",mills:" + mills);
            return;
        }
        Intent intent2 = new Intent(TimerFragment.SLEEP_TIMER_ACTION);
        intent2.putExtra("itemId", "SETUP_sleep_timer");
        intent2.putExtra("mills", 0);
        this.mContext.sendBroadcast(intent2);
        Log.d(TAG, "sleep timer sendbroadcast :" + text);
    }

    public int getIndexByLeftTime(Long timeLeft) {
        if (timeLeft.longValue() < 10) {
            return 0;
        }
        if (timeLeft.longValue() < 20) {
            return 1;
        }
        if (timeLeft.longValue() < 30) {
            return 2;
        }
        if (timeLeft.longValue() < 40) {
            return 3;
        }
        if (timeLeft.longValue() < 50) {
            return 4;
        }
        if (timeLeft.longValue() < 60) {
            return 5;
        }
        if (timeLeft.longValue() < 90) {
            return 6;
        }
        if (timeLeft.longValue() < 120) {
            return 7;
        }
        return 8;
    }

    public int getMetricsCategory() {
        return 336;
    }

    public void register() {
        Log.i(TAG, "register:" + this.registed);
        if (!this.registed) {
            this.registed = true;
            this.ifilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            this.ifilter.addAction(SOURCEACTION);
            this.mContext.registerReceiver(this.mReceiver, this.ifilter);
        }
    }

    private void unregister() {
        Log.i(TAG, "unregister registed:" + this.registed);
        if (this.registed) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        this.registed = false;
    }

    public boolean isServiceRunningCheck(String TargetServiceName) {
        for (ActivityManager.RunningServiceInfo service : ((ActivityManager) getActivity().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (TargetServiceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
