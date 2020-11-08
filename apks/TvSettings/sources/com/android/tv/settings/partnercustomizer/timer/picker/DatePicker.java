package com.android.tv.settings.partnercustomizer.timer.picker;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.tv.settings.partnercustomizer.timer.picker.PickerConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePicker extends Picker {
    private static final int DEFAULT_YEAR_RANGE = 67;
    private static final String EXTRA_DEFAULT_TO_CURRENT = "default_to_current";
    private static final String EXTRA_FORMAT = "date_format";
    private static final String EXTRA_START_YEAR = "start_year";
    private static final String EXTRA_YEAR_RANGE = "year_range";
    private static final int MAX_YEAR = 2037;
    private int mColDayIndex = 1;
    private int mColMonthIndex = 0;
    private int mColYearIndex = 2;
    private PickerConstants.Date mConstant;
    private String[] mDayStrings = null;
    private int mInitDay;
    private int mInitMonth;
    private int mInitYear;
    private boolean mPendingDate = false;
    private int mSelectedMonth;
    private int mSelectedYear;
    private int mStartYear;
    private int mYearRange;
    private String[] mYears;

    public static DatePicker newInstance(String format) {
        return newInstance(format, 1970, 67, true);
    }

    public static DatePicker newInstance(String format, int startYear, int yearRange, boolean startOnToday) {
        DatePicker datePicker = new DatePicker();
        if (startYear <= 0) {
            throw new IllegalArgumentException("The start year must be > 0. Got " + startYear);
        } else if (yearRange > 0) {
            Bundle args = new Bundle();
            args.putString(EXTRA_FORMAT, format);
            args.putInt(EXTRA_START_YEAR, startYear);
            args.putInt(EXTRA_YEAR_RANGE, yearRange);
            args.putBoolean(EXTRA_DEFAULT_TO_CURRENT, startOnToday);
            datePicker.setArguments(args);
            return datePicker;
        } else {
            throw new IllegalArgumentException("The year range must be > 0. Got " + yearRange);
        }
    }

    private void initYearsArray(int startYear, int yearRange) {
        this.mYears = new String[yearRange];
        for (int i = 0; i < yearRange; i++) {
            this.mYears[i] = String.format("%d", new Object[]{Integer.valueOf(startYear + i)});
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mConstant = PickerConstants.getDateInstance(getResources());
        this.mYearRange = getArguments().getInt(EXTRA_YEAR_RANGE, 67);
        this.mStartYear = getArguments().getInt(EXTRA_START_YEAR, 2037 - this.mYearRange);
        boolean startOnToday = getArguments().getBoolean(EXTRA_DEFAULT_TO_CURRENT, false);
        initYearsArray(this.mStartYear, this.mYearRange);
        this.mDayStrings = this.mConstant.days31;
        String format = getArguments().getString(EXTRA_FORMAT);
        if (format != null && !format.isEmpty()) {
            String format2 = format.toUpperCase();
            int yIndex = format2.indexOf(89);
            int mIndex = format2.indexOf(77);
            int dIndex = format2.indexOf(68);
            if (yIndex < 0 || mIndex < 0 || dIndex < 0 || yIndex > 2 || mIndex > 2 || dIndex > 2) {
                this.mColMonthIndex = 0;
                this.mColDayIndex = 1;
                this.mColYearIndex = 2;
            } else {
                this.mColMonthIndex = mIndex;
                this.mColDayIndex = dIndex;
                this.mColYearIndex = yIndex;
            }
        }
        if (startOnToday) {
            this.mPendingDate = true;
            Calendar cal = Calendar.getInstance();
            this.mInitYear = cal.get(1);
            this.mInitMonth = cal.get(2);
            this.mInitDay = cal.get(5);
        }
    }

    public void onResume() {
        if (this.mPendingDate) {
            this.mPendingDate = false;
            setDate(this.mInitYear, this.mInitMonth, this.mInitDay);
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public ArrayList<PickerColumn> getColumns() {
        ArrayList<PickerColumn> ret = new ArrayList<>();
        PickerColumn months = new PickerColumn(this.mConstant.months);
        PickerColumn days = new PickerColumn(this.mDayStrings);
        PickerColumn years = new PickerColumn(this.mYears);
        for (int i = 0; i < 3; i++) {
            if (i == this.mColYearIndex) {
                ret.add(years);
            } else if (i == this.mColMonthIndex) {
                ret.add(months);
            } else if (i == this.mColDayIndex) {
                ret.add(days);
            }
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public String getSeparator() {
        return this.mConstant.dateSeparator;
    }

    /* access modifiers changed from: protected */
    public boolean setDate(int year, int month, int day) {
        if (year < this.mStartYear || year > this.mStartYear + this.mYearRange) {
            return false;
        }
        try {
            GregorianCalendar cal = new GregorianCalendar(year, month, day);
            cal.setLenient(false);
            cal.getTime();
            this.mSelectedYear = year;
            this.mSelectedMonth = month;
            updateDayStrings(year, month);
            updateSelection(this.mColYearIndex, year - this.mStartYear);
            updateSelection(this.mColMonthIndex, month);
            updateSelection(this.mColDayIndex, day - 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void onScroll(int column, View v, int position) {
        if (column == this.mColMonthIndex) {
            this.mSelectedMonth = position;
        } else if (column == this.mColYearIndex) {
            this.mSelectedYear = Integer.parseInt(((TextView) v).getText().toString());
        } else {
            return;
        }
        updateDayStrings(this.mSelectedYear, this.mSelectedMonth);
    }

    private void updateDayStrings(int year, int month) {
        String[] dayStrings = (String[]) Arrays.copyOfRange(this.mConstant.days31, 0, new GregorianCalendar(year, month, 1).getActualMaximum(5));
        if (!Arrays.equals(this.mDayStrings, dayStrings)) {
            this.mDayStrings = dayStrings;
            updateAdapter(this.mColDayIndex, new PickerColumn(this.mDayStrings));
        }
    }
}
