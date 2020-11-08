package com.android.tv.settings.partnercustomizer.timer.picker;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import com.android.tv.settings.partnercustomizer.timer.picker.PickerConstants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TimePicker extends Picker {
    private static final String EXTRA_24H_FORMAT = "24h_format";
    private static final String EXTRA_DEFAULT_TO_CURRENT = "delault_to_current";
    private static final int HOURS_IN_HALF_DAY = 12;
    private ColumnOrder mColumnOrder;
    private PickerConstants.Time mConstant;
    private int mInitHour;
    private boolean mInitIsPm;
    private int mInitMinute;
    private boolean mIs24hFormat = false;
    private boolean mPendingTime = false;

    public static class ColumnOrder {
        public int amPm = 2;
        public int hours = 0;
        public int minutes = 1;

        public ColumnOrder(boolean is24hFormat) {
            Locale locale = Locale.getDefault();
            String hmaPattern = DateFormat.getBestDateTimePattern(locale, "hma");
            boolean isAmPmAtEnd = hmaPattern.indexOf("a") > hmaPattern.indexOf("m");
            if (TextUtils.getLayoutDirectionFromLocale(locale) == 1) {
                this.hours = 1;
                this.minutes = 0;
            }
            if (!isAmPmAtEnd && !is24hFormat) {
                this.amPm = 0;
                this.hours++;
                this.minutes++;
            }
        }
    }

    public static TimePicker newInstance() {
        return newInstance(true, true);
    }

    public static TimePicker newInstance(boolean is24hFormat, boolean defaultToCurrentTime) {
        TimePicker picker = new TimePicker();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_24H_FORMAT, is24hFormat);
        args.putBoolean(EXTRA_DEFAULT_TO_CURRENT, defaultToCurrentTime);
        picker.setArguments(args);
        return picker;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.mIs24hFormat = getArguments().getBoolean(EXTRA_24H_FORMAT, false);
        boolean useCurrent = getArguments().getBoolean(EXTRA_DEFAULT_TO_CURRENT, false);
        this.mColumnOrder = new ColumnOrder(this.mIs24hFormat);
        super.onCreate(savedInstanceState);
        this.mConstant = PickerConstants.getTimeInstance(getResources());
        if (useCurrent) {
            this.mPendingTime = true;
            Calendar cal = Calendar.getInstance();
            this.mInitHour = cal.get(11);
            if (!this.mIs24hFormat) {
                if (this.mInitHour >= 12) {
                    this.mInitIsPm = true;
                    if (this.mInitHour > 12) {
                        this.mInitHour -= 12;
                    }
                } else {
                    this.mInitIsPm = false;
                    if (this.mInitHour == 0) {
                        this.mInitHour = 12;
                    }
                }
            }
            this.mInitMinute = cal.get(12);
        }
    }

    public void onResume() {
        if (this.mPendingTime) {
            this.mPendingTime = false;
            setTime(this.mInitHour, this.mInitMinute, this.mInitIsPm);
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public boolean setTime(int hour, int minute, boolean isPm) {
        if (minute < 0 || minute > 59) {
            return false;
        }
        if (this.mIs24hFormat) {
            if (hour < 0 || hour > 23) {
                return false;
            }
        } else if (hour < 1 || hour > 12) {
            return false;
        }
        updateSelection(this.mColumnOrder.hours, this.mIs24hFormat ? hour : hour - 1);
        updateSelection(this.mColumnOrder.minutes, minute);
        if (!this.mIs24hFormat) {
            updateSelection(this.mColumnOrder.amPm, isPm);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public ArrayList<PickerColumn> getColumns() {
        ArrayList<PickerColumn> ret = new ArrayList<>();
        int capacity = this.mIs24hFormat ? 2 : 3;
        for (int i = 0; i < capacity; i++) {
            ret.add((Object) null);
        }
        PickerColumn hours = new PickerColumn(this.mIs24hFormat ? this.mConstant.hours24 : this.mConstant.hours12);
        PickerColumn minutes = new PickerColumn(this.mConstant.minutes);
        ret.set(this.mColumnOrder.hours, hours);
        ret.set(this.mColumnOrder.minutes, minutes);
        if (!this.mIs24hFormat) {
            ret.set(this.mColumnOrder.amPm, new PickerColumn(this.mConstant.ampm));
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public String getSeparator() {
        return this.mConstant.timeSeparator;
    }
}
