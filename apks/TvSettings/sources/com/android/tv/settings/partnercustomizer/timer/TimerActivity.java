package com.android.tv.settings.partnercustomizer.timer;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.Toast;
import com.android.tv.settings.PreferenceUtils;
import com.android.tv.settings.R;
import com.android.tv.settings.dialog.old.ContentFragment;
import com.android.tv.settings.dialog.old.DialogActivity;
import com.android.tv.settings.partnercustomizer.timer.picker.Picker;
import com.android.tv.settings.partnercustomizer.timer.picker.PickerConstants;
import com.android.tv.settings.partnercustomizer.timer.picker.TimePicker;
import com.android.tv.settings.partnercustomizer.tvsettingservice.TVSettingConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.List;

public class TimerActivity extends DialogActivity {
    private static final String EXTRA_PICKER_TYPE = "SetDateTimeActivity.pickerType";
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final String TYPE_DATE = "date";
    private static final String TYPE_TIMER_OFF = "timr_off";
    private static final String TYPE_TIMER_ON = "timr_on";
    private String pickerType;

    private class DatePickerListener implements Picker.ResultListener {
        private DatePickerListener() {
        }

        public void onCommitResult(List<String> result) {
            String formatOrder = new String(DateFormat.getDateFormatOrder(TimerActivity.this)).toUpperCase();
            int yIndex = formatOrder.indexOf(89);
            int mIndex = formatOrder.indexOf(77);
            int dIndex = formatOrder.indexOf(68);
            if (yIndex < 0 || mIndex < 0 || dIndex < 0 || yIndex > 2 || mIndex > 2 || dIndex > 2) {
                mIndex = 0;
                dIndex = 1;
                yIndex = 2;
            }
            String month = result.get(mIndex);
            int parseInt = Integer.parseInt(result.get(dIndex));
            int parseInt2 = Integer.parseInt(result.get(yIndex));
            String[] months = PickerConstants.getDateInstance(TimerActivity.this.getResources()).months;
            int totalMonths = months.length;
            for (int i = 0; i < totalMonths; i++) {
                if (months[i].equals(month)) {
                    int monthInt = i;
                }
            }
            TimerActivity.this.finish();
        }
    }

    private class TimePickerListener implements Picker.ResultListener {
        private TimePickerListener() {
        }

        public void onCommitResult(List<String> result) {
            boolean is24hFormat = TimerActivity.isTimeFormat24h(TimerActivity.this);
            TimePicker.ColumnOrder columnOrder = new TimePicker.ColumnOrder(is24hFormat);
            int hour = Integer.parseInt(result.get(columnOrder.hours));
            int minute = Integer.parseInt(result.get(columnOrder.minutes));
            if (!is24hFormat) {
                if (result.get(columnOrder.amPm).equals(TimerActivity.this.getResources().getStringArray(R.array.ampm)[1])) {
                    hour = (hour % 12) + 12;
                } else {
                    hour %= 12;
                }
            }
            if (TimerActivity.this.setTime(TimerActivity.this, hour, minute)) {
                Toast.makeText(TimerActivity.this, "Power On Timer can not equals to Power Off Timer", 0).show();
            } else {
                TimerActivity.this.finish();
            }
        }
    }

    public void updatePowerOn(String mDate) {
        int value = PreferenceUtils.getSettingIntValue(getContentResolver(), "tv_timer_power_on_time_type_entry_values", 0);
        MtkLog.d("DateTimeView", "mDate:" + mDate + "value:" + value);
        TVSettingConfig mTvContent = TVSettingConfig.getInstance(this);
        if (value == 2) {
            mTvContent.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 1, mDate);
            mTvContent.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, mDate);
            return;
        }
        mTvContent.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON, 1, mDate);
        mTvContent.updatePowerOn(MtkTvConfigTypeBase.CFG_TIMER_TIMER_ON_ONCE, 0, mDate);
    }

    public void updatePowerOff(String mDate) {
        int value = PreferenceUtils.getSettingIntValue(getContentResolver(), "tv_timer_power_off_time_type_entry_values", 0);
        TVSettingConfig mTvContent = TVSettingConfig.getInstance(this);
        MtkLog.d("DateTimeView", "mDate:" + mDate + "value:" + value);
        if (value == 2) {
            mTvContent.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 1, mDate);
            mTvContent.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, mDate);
            return;
        }
        mTvContent.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF, 1, mDate);
        mTvContent.updatePowerOff(MtkTvConfigTypeBase.CFG_TIMER_TIMER_OFF_ONCE, 0, mDate);
    }

    public static Intent getSetDateIntent(Context context) {
        Intent intent = new Intent(context, TimerActivity.class);
        intent.putExtra(EXTRA_PICKER_TYPE, TYPE_DATE);
        return intent;
    }

    public static Intent getSetTimerOnIntent(Context context) {
        Intent intent = new Intent(context, TimerActivity.class);
        intent.putExtra(EXTRA_PICKER_TYPE, TYPE_TIMER_ON);
        return intent;
    }

    public static Intent getSetTimerOffIntent(Context context) {
        Intent intent = new Intent(context, TimerActivity.class);
        intent.putExtra(EXTRA_PICKER_TYPE, TYPE_TIMER_OFF);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Fragment actionFragment;
        Fragment contentFragment;
        super.onCreate(savedInstanceState);
        this.pickerType = getIntent().getStringExtra(EXTRA_PICKER_TYPE);
        Picker.ResultListener resultListener = new TimePickerListener();
        if (savedInstanceState == null) {
            if (TYPE_TIMER_ON.equals(this.pickerType)) {
                contentFragment = ContentFragment.newInstance(getString(R.string.system_timer), getString(R.string.set_timer_power_on_timer), (String) null, (int) R.drawable.partner_ic_access_time_132dp, getColor(R.color.icon_background));
                actionFragment = TimePicker.newInstance(isTimeFormat24h(this), true);
            } else {
                contentFragment = ContentFragment.newInstance(getString(R.string.system_timer), getString(R.string.set_timer_power_off_timer), (String) null, (int) R.drawable.partner_ic_access_time_132dp, getColor(R.color.icon_background));
                actionFragment = TimePicker.newInstance(isTimeFormat24h(this), true);
            }
            setContentAndActionFragments(contentFragment, actionFragment);
        }
        getFragmentManager().executePendingTransactions();
        ((Picker) getActionFragment()).setResultListener(resultListener);
    }

    public boolean setTime(Context context, int hour, int minute) {
        String strMin;
        String strMin2;
        ContentResolver contentResolver = context.getContentResolver();
        if (hour < 10) {
            String strHour = "0" + hour;
            if (minute < 10) {
                strMin2 = "0" + minute;
            } else {
                strMin2 = "" + minute;
            }
            if (TYPE_TIMER_ON.equals(this.pickerType)) {
                if (isOnEqualToOff(context, contentResolver, strHour + ":" + strMin2)) {
                    return true;
                }
                Settings.Global.putString(contentResolver, "tv_timer_power_on_timer_values", strHour + ":" + strMin2);
                updatePowerOn(strHour + ":" + strMin2);
                return false;
            }
            if (isOffEqualToOn(context, contentResolver, strHour + ":" + strMin2)) {
                return true;
            }
            Settings.Global.putString(contentResolver, "tv_timer_power_off_timer_values", strHour + ":" + strMin2);
            updatePowerOff(strHour + ":" + strMin2);
            return false;
        }
        if (minute < 10) {
            strMin = "0" + minute;
        } else {
            strMin = "" + minute;
        }
        if (TYPE_TIMER_ON.equals(this.pickerType)) {
            if (isOnEqualToOff(context, contentResolver, hour + ":" + strMin)) {
                return true;
            }
            Settings.Global.putString(contentResolver, "tv_timer_power_on_timer_values", hour + ":" + strMin);
            updatePowerOn(hour + ":" + strMin);
            return false;
        }
        if (isOffEqualToOn(context, contentResolver, hour + ":" + strMin)) {
            return true;
        }
        Settings.Global.putString(contentResolver, "tv_timer_power_off_timer_values", hour + ":" + strMin);
        updatePowerOff(hour + ":" + strMin);
        return false;
    }

    /* access modifiers changed from: private */
    public static boolean isTimeFormat24h(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    private static boolean isOnEqualToOff(Context context, ContentResolver contentResolver, String on) {
        if (on.equals(Settings.Global.getString(contentResolver, "tv_timer_power_off_timer_values"))) {
            return true;
        }
        return false;
    }

    private static boolean isOffEqualToOn(Context context, ContentResolver contentResolver, String off) {
        if (off.equals(Settings.Global.getString(contentResolver, "tv_timer_power_on_timer_values"))) {
            return true;
        }
        return false;
    }

    public int getMetricsCategory() {
        return 746;
    }
}
