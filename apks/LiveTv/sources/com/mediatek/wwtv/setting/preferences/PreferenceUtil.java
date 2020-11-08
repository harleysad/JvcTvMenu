package com.mediatek.wwtv.setting.preferences;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeshift;
import com.mediatek.twoworlds.tv.SystemProperties;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.setting.fragments.BaseContentFragment;
import com.mediatek.wwtv.setting.util.LanguageUtil;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.setting.util.RegionConst;
import com.mediatek.wwtv.setting.util.TVContent;
import com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity;
import com.mediatek.wwtv.tvcenter.tiftimeshift.TifTimeShiftManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;

public class PreferenceUtil {
    public static final String CHILD_PREFERENCE_ID = "child";
    public static final int DELAY_MILLIS = 300;
    public static final int MESSAGE_RESET = 10001;
    public static final String PARENT_PREFERENCE_ID = "parent";
    private static final String TAG = "PreferenceUtil";
    private static PreferenceUtil mPreference = null;
    /* access modifiers changed from: private */
    public AsyncTimeTask att;
    Preference.OnPreferenceChangeListener mChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            int areaCode;
            int itemPosition;
            MtkLog.d(PreferenceUtil.TAG, "onPreferenceChange " + preference + "," + preference.getKey() + "," + newValue);
            if (!preference.getKey().startsWith(MenuConfigManager.PARENTAL_INPUT_BLOCK_SOURCE)) {
                if (preference.getKey().equals("SETUP_sleep_timer")) {
                    int value = Integer.parseInt((String) newValue);
                    PreferenceData.getInstance(PreferenceUtil.this.mThemedContext.getApplicationContext()).handleSleepTimerChange((ListPreference) preference, value);
                } else if (preference.getKey().equals("SETUP_auto_syn")) {
                    MtkLog.d(PreferenceUtil.TAG, "MenuConfigManager.AUTO_SYNCSETUP_auto_syn");
                    if (PreferenceUtil.this.att != null) {
                        if (PreferenceUtil.this.att.getStatus() == AsyncTask.Status.RUNNING) {
                            PreferenceUtil.this.att.cancel(true);
                            AsyncTimeTask unused = PreferenceUtil.this.att = null;
                        }
                        AsyncTimeTask unused2 = PreferenceUtil.this.att = new AsyncTimeTask(preference);
                        PreferenceUtil.this.att.execute(new String[]{(String) newValue});
                    } else {
                        AsyncTimeTask unused3 = PreferenceUtil.this.att = new AsyncTimeTask(preference);
                        PreferenceUtil.this.att.execute(new String[]{(String) newValue});
                    }
                } else if (preference.getKey().equals("g_record__rec_tshift_mode")) {
                    if (newValue instanceof Boolean) {
                        newValue = Integer.valueOf(((Boolean) newValue).booleanValue() ? 1 : 0);
                    }
                    PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), newValue);
                    if (newValue.equals(0)) {
                        TifTimeShiftManager.getInstance().stop();
                        TifTimeShiftManager.getInstance().stopAll();
                        MtkTvTimeshift.getInstance().setAutoRecord(false);
                    } else if (newValue.equals(1)) {
                        if (SystemProperties.get("vendor.mtk.tif.timeshift").equals("1")) {
                            MtkTvTimeshift.getInstance().setAutoRecord(true);
                            MtkLog.i(PreferenceUtil.TAG, "MtkTvTimeshift.getInstance().setAutoRecord(true)");
                        } else {
                            MtkLog.i(PreferenceUtil.TAG, "vendor.mtk.tif.timeshift != 1 ");
                        }
                    }
                } else if (preference.getKey().contains(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING)) {
                    int itemPosition2 = new Integer(preference.getKey().replace(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING, "")).intValue();
                    int value2 = Integer.parseInt((String) newValue);
                    SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveValue(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING, itemPosition2);
                    SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveValue(MenuConfigManager.SETUP_REGION_SETTING_SELECT, value2);
                    MtkLog.d(PreferenceUtil.TAG, "item positon:" + itemPosition2 + ",select position:" + value2);
                    TVContent.getInstance(PreferenceUtil.this.mThemedContext).setConfigValue(MtkTvConfigTypeBase.CFG_SET_AND_GET_AREA_CODE, RegionConst.getEcuadorAreaCodeArray(itemPosition2)[value2]);
                } else if (preference.getKey().contains(MenuConfigManager.SETUP_REGION_SETTING_LUZON) || preference.getKey().contains(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS) || preference.getKey().contains(MenuConfigManager.SETUP_REGION_SETTING_MINDANAO)) {
                    int value3 = Integer.parseInt((String) newValue);
                    if (preference.getKey().contains(MenuConfigManager.SETUP_REGION_SETTING_LUZON)) {
                        itemPosition = Integer.valueOf(preference.getKey().replace(MenuConfigManager.SETUP_REGION_SETTING_LUZON, "")).intValue();
                        SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveStrValue(MenuConfigManager.SETUP_REGION_SETTING, MenuConfigManager.SETUP_REGION_SETTING_LUZON);
                        areaCode = RegionConst.phiCityAreaCodeLuzong[itemPosition][value3];
                    } else if (preference.getKey().contains(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS)) {
                        itemPosition = Integer.valueOf(preference.getKey().replace(MenuConfigManager.SETUP_REGION_SETTING_VISAYAS, "")).intValue();
                        SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveStrValue(MenuConfigManager.SETUP_REGION_SETTING, MenuConfigManager.SETUP_REGION_SETTING_VISAYAS);
                        areaCode = RegionConst.phiCityAreaCodeVisayas[itemPosition][value3];
                    } else {
                        itemPosition = Integer.valueOf(preference.getKey().replace(MenuConfigManager.SETUP_REGION_SETTING_MINDANAO, "")).intValue();
                        SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveStrValue(MenuConfigManager.SETUP_REGION_SETTING, MenuConfigManager.SETUP_REGION_SETTING_MINDANAO);
                        areaCode = RegionConst.phiCityAreaCodeMindanao[itemPosition][value3];
                    }
                    SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveValue(MenuConfigManager.SETUP_REGION_PHILIPPINES_SETTING, itemPosition);
                    SaveValue.getInstance(PreferenceUtil.this.mThemedContext).saveValue(MenuConfigManager.SETUP_REGION_SETTING_SELECT, value3);
                    MtkLog.d(PreferenceUtil.TAG, "item positon:" + itemPosition + ",select position:" + value3);
                    TVContent.getInstance(PreferenceUtil.this.mThemedContext).setConfigValue(MtkTvConfigTypeBase.CFG_SET_AND_GET_AREA_CODE, areaCode);
                } else if (preference.getKey().equals("g_video__picture_mode")) {
                    Log.d(PreferenceUtil.TAG, "set picture mode" + preference.getKey() + " " + MenuConfigManager.PICTURE_MODE_dOVI);
                    ListPreference listPreference = null;
                    if (preference instanceof ListPreference) {
                        listPreference = (ListPreference) preference;
                    }
                    int value4 = Integer.valueOf((String) newValue).intValue();
                    Log.d(PreferenceUtil.TAG, "picture mode value is" + value4);
                    if (listPreference != null) {
                        CharSequence[] entry = listPreference.getEntries();
                        if (value4 >= 0 && value4 < entry.length) {
                            listPreference.setSummary(entry[value4]);
                        }
                    }
                    if (MenuConfigManager.PICTURE_MODE_dOVI) {
                        if (value4 == 0) {
                            value4 = 5;
                        } else if (value4 == 1) {
                            value4 = 6;
                        }
                        PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), value4);
                    } else {
                        if (newValue instanceof Boolean) {
                            MtkLog.d(PreferenceUtil.TAG, "instanceof Boolean");
                            newValue = Integer.valueOf(((Boolean) newValue).booleanValue() ? 1 : 0);
                        }
                        PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), newValue);
                    }
                } else if (preference.getKey().equals(MenuConfigManager.POWER_SETTING_CONFIG_VALUE)) {
                    Log.d(PreferenceUtil.TAG, "power setting mode" + preference.getKey() + " " + MenuConfigManager.POWER_SETTING_CONFIG_VALUE);
                    ListPreference listPreference2 = null;
                    if (preference instanceof ListPreference) {
                        listPreference2 = (ListPreference) preference;
                    }
                    int value5 = Integer.valueOf((String) newValue).intValue() + 1;
                    Log.d(PreferenceUtil.TAG, "power setting value is" + value5);
                    if (listPreference2 != null) {
                        CharSequence[] entry2 = listPreference2.getEntries();
                        if (value5 >= 0 && value5 < entry2.length) {
                            listPreference2.setSummary(entry2[value5 - 1]);
                        }
                    }
                    MenuConfigManager.getInstance(PreferenceUtil.this.mThemedContext).setAutoSleepValue(value5);
                    SaveValue.saveWorldValue(PreferenceUtil.this.mThemedContext, MenuConfigManager.POWER_SETTING_VALUE, value5 - 1, true);
                } else if (preference.getKey().contains(MenuConfigManager.SOUNDTRACKS_GET_STRING)) {
                    String value6 = preference.getKey().substring(MenuConfigManager.SOUNDTRACKS_GET_STRING.length() + 1);
                    try {
                        Log.d(PreferenceUtil.TAG, "SOUNDTRACKS_GET_STRING value" + value6);
                        TurnkeyUiMainActivity.getInstance().getTvView().selectTrack(0, value6);
                    } catch (Exception e) {
                        Log.d(PreferenceUtil.TAG, "SOUNDTRACKS_GET_STRING error");
                    }
                } else if (preference.getKey().equals("g_ginga__ginga_enable")) {
                    if (newValue instanceof Boolean) {
                        newValue = Integer.valueOf(((Boolean) newValue).booleanValue() ? 1 : 0);
                        PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), newValue);
                        int status = PreferenceUtil.this.mConfigManager.getDefault("g_cc__cc_caption");
                        MtkLog.d(PreferenceUtil.TAG, "GINGA_SETUP Boolean :" + newValue);
                        if (((Integer) newValue).intValue() == 1 && status != 1) {
                            PreferenceUtil.this.mConfigManager.setValue("g_cc__cc_caption", 1);
                        }
                    }
                } else if (preference.getKey().equals("g_cc__cc_caption")) {
                    MtkLog.d(PreferenceUtil.TAG, "SETUP_ENABLE_CAPTION int :" + newValue);
                    PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), newValue);
                    int status2 = PreferenceUtil.this.mConfigManager.getDefault("g_ginga__ginga_enable");
                    if (Integer.valueOf((String) newValue).intValue() != 1 && status2 == 1) {
                        PreferenceUtil.this.mConfigManager.setValue("g_ginga__ginga_enable", 0);
                    }
                } else {
                    if (newValue instanceof Boolean) {
                        MtkLog.d(PreferenceUtil.TAG, "instanceof Boolean");
                        newValue = Integer.valueOf(((Boolean) newValue).booleanValue() ? 1 : 0);
                    }
                    PreferenceUtil.this.mConfigManager.setValue(preference.getKey(), newValue);
                }
            }
            PreferenceData.getInstance(PreferenceUtil.this.mThemedContext.getApplicationContext()).invalidate(preference.getKey(), newValue);
            return true;
        }
    };
    Preference.OnPreferenceClickListener mClickListener = new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference preference) {
            MtkLog.d(PreferenceUtil.TAG, "onPreferenceClick " + preference);
            PreferenceUtil.this.mConfigManager.setValueDefault(preference.getKey());
            if (!"g_video__dovi_reset_pic_setting".equals(preference.getKey())) {
                return true;
            }
            Message msg = PreferenceUtil.this.mHandler.obtainMessage();
            msg.what = 10001;
            PreferenceUtil.this.mHandler.sendMessageDelayed(msg, 300);
            return true;
        }
    };
    public MenuConfigManager mConfigManager = null;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            MtkLog.i(PreferenceUtil.TAG, "msg.what: " + msg.what);
            if (msg.what == 10001) {
                PreferenceUtil.this.mHandler.removeMessages(10001);
            }
        }
    };
    public LanguageUtil mOsdLanguage;
    /* access modifiers changed from: private */
    public Context mThemedContext;

    private PreferenceUtil(Context themedContext) {
        this.mThemedContext = themedContext;
        this.mConfigManager = MenuConfigManager.getInstance(themedContext.getApplicationContext());
        this.mOsdLanguage = new LanguageUtil(this.mThemedContext.getApplicationContext());
    }

    public static PreferenceUtil getInstance(Context themedContext) {
        if (mPreference == null) {
            mPreference = new PreferenceUtil(themedContext);
        }
        mPreference.mThemedContext = themedContext;
        return mPreference;
    }

    public static String[] getCharSequence(int size) {
        String[] seq = new String[size];
        for (int i = 0; i < size; i++) {
            seq[i] = String.valueOf(i);
        }
        return seq;
    }

    public Preference createFragmentPreference(String key, int resTitle, boolean enabled, String className) {
        Preference preference = createPreferenceInternal(key);
        preference.setTitle(resTitle);
        preference.setFragment(className);
        preference.setEnabled(enabled);
        return preference;
    }

    private Preference createPreferenceInternal(String key) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.getExtras().putCharSequence(PARENT_PREFERENCE_ID, preference.getKey());
        return preference;
    }

    public Preference createPreference(String key, int resTitle) {
        Preference preference = createPreferenceInternal(key);
        preference.setTitle(resTitle);
        preference.setFragment(BaseContentFragment.class.getName());
        return preference;
    }

    public Preference createPreference(String key, String title) {
        Preference preference = createPreferenceInternal(key);
        preference.setTitle((CharSequence) title);
        preference.setFragment(BaseContentFragment.class.getName());
        return preference;
    }

    public Preference createPreference(String key, int resTitle, Intent intent) {
        Preference preference = createPreferenceInternal(key);
        preference.setTitle(resTitle);
        preference.setIntent(intent);
        return preference;
    }

    public Preference createPreference(String key, String title, Intent intent) {
        Preference preference = createPreferenceInternal(key);
        preference.setTitle((CharSequence) title);
        preference.setIntent(intent);
        return preference;
    }

    public Preference createPreference(String key, int resTitle, boolean status) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle(resTitle);
        preference.setEnabled(status);
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    public Preference createPreference(String key, String title, boolean status) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle((CharSequence) title);
        preference.setEnabled(status);
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    public Preference createProgressPreference(String key, int resTitle, boolean isPositionView, int minValue, int maxValue, int defValue) {
        ProgressPreference preference = new ProgressPreference(this.mThemedContext);
        preference.setPositionView(isPositionView);
        preference.setKey(key);
        preference.setTitle(resTitle);
        preference.setMinValue(minValue);
        preference.setMaxValue(maxValue);
        preference.setCurrentValue(defValue);
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    public Preference createProgressPreference(String key, int resTitle, boolean isPositionView) {
        return createProgressPreference(key, resTitle, isPositionView, this.mConfigManager.getMin(key), this.mConfigManager.getMax(key), this.mConfigManager.getDefault(key));
    }

    public Preference createSwitchPreference(String key, int resTitle, boolean checked) {
        SwitchPreference preference = new SwitchPreference(this.mThemedContext);
        preference.setPersistent(false);
        preference.setTitle(resTitle);
        preference.setKey(key);
        preference.setChecked(checked);
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    public Preference createSwitchPreference(String key, String title, boolean checked) {
        SwitchPreference preference = new SwitchPreference(this.mThemedContext);
        preference.setPersistent(false);
        preference.setTitle((CharSequence) title);
        preference.setKey(key);
        preference.setChecked(checked);
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    private ListPreference createListPreferenceInternal(String key, boolean status, String[] entries, String[] entryValues, String defValue) {
        ListPreference preference = new ListPreference(this.mThemedContext);
        preference.setKey(key);
        preference.setPersistent(false);
        preference.setEnabled(status);
        preference.setEntries((CharSequence[]) entries);
        preference.setEntryValues((CharSequence[]) entryValues);
        preference.setValue(defValue);
        for (int i = 0; i < entryValues.length; i++) {
            if (entryValues[i].equals(defValue)) {
                preference.setSummary(entries[i]);
            }
        }
        preference.setOnPreferenceChangeListener(this.mChangeListener);
        return preference;
    }

    public Preference createListPreference(String key, String title, boolean status, String[] entries, int defValue) {
        ListPreference preference = createListPreferenceInternal(key, status, entries, getCharSequence(entries.length), String.valueOf(defValue));
        preference.setTitle((CharSequence) title);
        preference.setDialogTitle((CharSequence) title);
        return preference;
    }

    public Preference createListPreference(String key, int resTitle, boolean status, String[] entries, int defValue) {
        ListPreference preference = createListPreferenceInternal(key, status, entries, getCharSequence(entries.length), String.valueOf(defValue));
        preference.setTitle(resTitle);
        preference.setDialogTitle(resTitle);
        return preference;
    }

    public Preference createListPreference(String key, String title, boolean status, String[] entries, String[] entryValues, String defValue) {
        ListPreference preference = createListPreferenceInternal(key, status, entries, entryValues, defValue);
        preference.setTitle((CharSequence) title);
        preference.setDialogTitle((CharSequence) title);
        return preference;
    }

    public Preference createListPreference(String key, int resTitle, boolean status, String[] entries, String[] entryValues, String defValue) {
        ListPreference preference = createListPreferenceInternal(key, status, entries, entryValues, defValue);
        preference.setTitle(resTitle);
        preference.setDialogTitle(resTitle);
        return preference;
    }

    public DialogPreference createDialogPreference(String key, int resTitle, Dialog dialog) {
        DialogPreference preference = new DialogPreference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle(resTitle);
        preference.setDialog(dialog);
        return preference;
    }

    public DialogPreference createDialogPreference(String key, String title, Dialog dialog) {
        DialogPreference preference = new DialogPreference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle((CharSequence) title);
        preference.setDialog(dialog);
        return preference;
    }

    public Preference createClickPreference(String key, int resTitle) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle(resTitle);
        preference.setOnPreferenceClickListener(this.mClickListener);
        return preference;
    }

    public Preference createClickPreference(String key, String title) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle((CharSequence) title);
        preference.setOnPreferenceClickListener(this.mClickListener);
        return preference;
    }

    public Preference createClickPreference(String key, int resTitle, Preference.OnPreferenceClickListener clickListener) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle(resTitle);
        preference.setOnPreferenceClickListener(clickListener);
        return preference;
    }

    public Preference createClickPreference(String key, String title, Preference.OnPreferenceClickListener clickListener) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle((CharSequence) title);
        preference.setOnPreferenceClickListener(clickListener);
        return preference;
    }

    public Preference createPreferenceWithSummary(String key, int title, String summary) {
        Preference preference = new Preference(this.mThemedContext);
        preference.setKey(key);
        preference.setTitle(title);
        preference.setSummary((CharSequence) summary);
        return preference;
    }

    class AsyncTimeTask extends AsyncTask<String, String, String> {
        Preference taskPreference;

        AsyncTimeTask(Preference taskPreference2) {
            this.taskPreference = taskPreference2;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            int i;
            int value = Integer.parseInt(arg0[0]);
            MtkLog.d("Adapter", "setTimeSyncSource :" + value);
            PreferenceUtil.this.mConfigManager.setValue(this.taskPreference.getKey(), value);
            MtkTvTime.getInstance().setTimeSyncSource(value);
            ContentResolver contentResolver = PreferenceUtil.this.mThemedContext.getApplicationContext().getContentResolver();
            if (value == 2) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(contentResolver, "auto_time", i);
            return arg0[0];
        }
    }
}
