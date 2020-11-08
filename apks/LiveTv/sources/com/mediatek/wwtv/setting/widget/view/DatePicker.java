package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvTimeFormat;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DatePicker extends Picker {
    private static final int DEFAULT_START_YEAR = Calendar.getInstance().get(1);
    private static final int DEFAULT_YEAR_RANGE = 24;
    private static final String EXTRA_DEFAULT_TO_CURRENT = "default_to_current";
    private static final String EXTRA_FORMAT = "date_format";
    private static final String EXTRA_START_YEAR = "start_year";
    private static final String EXTRA_YEAR_RANGE = "year_range";
    Context context;
    private int mColDayIndex = 1;
    private int mColMonthIndex = 0;
    private int mColYearIndex = 2;
    private String[] mDayString = null;
    private int mInitDay;
    private int mInitMonth;
    private int mInitYear;
    private boolean mPendingDate = false;
    private String mSelectedMonth;
    private int mSelectedYear = DEFAULT_START_YEAR;
    private int mStartYear;
    private int mYearRange;
    private String[] mYears;

    public static DatePicker newInstance() {
        return newInstance("");
    }

    public static DatePicker newInstance(String format) {
        return newInstance(format, DEFAULT_START_YEAR);
    }

    public static DatePicker newInstance(String format, int startYear) {
        return newInstance(format, startYear, 24, true);
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
            this.mYears[i] = String.valueOf(startYear + i);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        String[] dates;
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.mItemId = bundle.getCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID).toString();
        }
        MtkLog.d("guanglei", "mItemId:" + this.mItemId);
        if (!TextUtils.isEmpty(this.mItemId) && this.mItemId.length() > MenuConfigManager.TIME_END_DATE.length()) {
            if (this.mItemId.contains(MenuConfigManager.TIME_START_DATE)) {
                this.channelId = this.mItemId.substring(16);
            } else {
                this.channelId = this.mItemId.substring(14);
            }
        }
        MtkLog.d("guanglei", "channelId:" + this.channelId);
        int mDefYear = Calendar.getInstance().get(1);
        if (mDefYear > 1985) {
            mDefYear -= 15;
        }
        if (this.mItemId.contains("date")) {
            newInstance(new String(DateFormat.getDateFormatOrder(this.context)), mDefYear, 36, false);
        }
        this.mStartYear = getArguments().getInt(EXTRA_START_YEAR, DEFAULT_START_YEAR);
        this.mYearRange = getArguments().getInt(EXTRA_YEAR_RANGE, 24);
        boolean startOnToday = getArguments().getBoolean(EXTRA_DEFAULT_TO_CURRENT, false);
        this.mSelectedMonth = this.mConstant.months[0];
        initYearsArray(this.mStartYear, this.mYearRange);
        this.mDayString = this.mConstant.days30;
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
            return;
        }
        this.mPendingDate = true;
        String selectDate = SaveValue.getInstance(this.context).readStrValue(this.mItemId);
        Log.d("DatePicker", "selectDate==" + selectDate);
        if (!TextUtils.isEmpty(selectDate) && (dates = selectDate.split("/")) != null && dates.length == 3) {
            this.mInitYear = Integer.parseInt(dates[0]);
            this.mInitMonth = Integer.parseInt(dates[1]) - 1;
            this.mInitDay = Integer.parseInt(dates[2]);
            Log.d("DatePicker", "mInitYear==" + this.mInitYear + ",mInitMonth==" + this.mInitMonth + ",mInitDay==" + this.mInitDay);
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
        PickerColumn days = new PickerColumn(this.mDayString);
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
        String[] dayString;
        boolean isLeapYear = false;
        if (year < this.mStartYear || year > this.mStartYear + this.mYearRange) {
            return false;
        }
        try {
            GregorianCalendar cal = new GregorianCalendar(year, month, day);
            cal.setLenient(false);
            cal.getTime();
            this.mSelectedYear = year;
            this.mSelectedMonth = this.mConstant.months[month];
            updateSelection(this.mColYearIndex, year - this.mStartYear);
            updateSelection(this.mColMonthIndex, month);
            if (year % 400 == 0) {
                isLeapYear = true;
            } else if (year % 100 == 0) {
                isLeapYear = false;
            } else if (year % 4 == 0) {
                isLeapYear = true;
            }
            if (month == 1) {
                if (isLeapYear) {
                    dayString = this.mConstant.days29;
                } else {
                    dayString = this.mConstant.days28;
                }
            } else if (month == 3 || month == 5 || month == 8 || month == 10) {
                dayString = this.mConstant.days30;
            } else {
                dayString = this.mConstant.days31;
            }
            if (this.mDayString != dayString) {
                this.mDayString = dayString;
                updateAdapter(this.mColDayIndex, new PickerColumn(this.mDayString));
            }
            updateSelection(this.mColDayIndex, day - 1);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void onScroll(View v) {
        String[] dayString;
        int column = ((Integer) v.getTag()).intValue();
        String text = ((TextView) v).getText().toString();
        if (column == this.mColMonthIndex) {
            this.mSelectedMonth = text;
        } else if (column == this.mColYearIndex) {
            this.mSelectedYear = Integer.parseInt(text);
        } else {
            return;
        }
        boolean isLeapYear = false;
        if (this.mSelectedYear % 400 == 0) {
            isLeapYear = true;
        } else if (this.mSelectedYear % 100 == 0) {
            isLeapYear = false;
        } else if (this.mSelectedYear % 4 == 0) {
            isLeapYear = true;
        }
        if (this.mSelectedMonth.equals(this.mConstant.months[1])) {
            if (isLeapYear) {
                dayString = this.mConstant.days29;
            } else {
                dayString = this.mConstant.days28;
            }
        } else if (this.mSelectedMonth.equals(this.mConstant.months[3]) || this.mSelectedMonth.equals(this.mConstant.months[5]) || this.mSelectedMonth.equals(this.mConstant.months[8]) || this.mSelectedMonth.equals(this.mConstant.months[10])) {
            dayString = this.mConstant.days30;
        } else {
            dayString = this.mConstant.days31;
        }
        if (!this.mDayString.equals(dayString)) {
            this.mDayString = dayString;
            updateAdapter(this.mColDayIndex, new PickerColumn(this.mDayString));
        }
    }

    /* access modifiers changed from: protected */
    public void recordResult(List<String> mResult) {
        List<String> list = mResult;
        super.recordResult(mResult);
        if (this.mItemId.contains("date")) {
            StringBuilder sb = new StringBuilder();
            sb.append("recordResult:");
            sb.append(list.get(0));
            sb.append(",");
            sb.append(list.get(1));
            Log.d("Picker", sb.toString());
            String formatOrder = new String(DateFormat.getDateFormatOrder(this.context)).toUpperCase();
            int yIndex = formatOrder.indexOf(89);
            int mIndex = formatOrder.indexOf(77);
            int dIndex = formatOrder.indexOf(68);
            if (yIndex < 0 || mIndex < 0 || dIndex < 0 || yIndex > 2 || mIndex > 2 || dIndex > 2) {
                mIndex = 0;
                dIndex = 1;
                yIndex = 2;
            }
            String month = list.get(mIndex);
            int day = Integer.parseInt(list.get(dIndex));
            int year = Integer.parseInt(list.get(yIndex));
            int monthInt = 0;
            String[] months = PickerConstant.getInstance(this.context.getResources()).months;
            int totalMonths = months.length;
            for (int i = 0; i < totalMonths; i++) {
                if (months[i].equals(month)) {
                    monthInt = i;
                }
            }
            String dayStr = "" + day;
            String monStr = "" + (monthInt + 1);
            if (monthInt < 9) {
                monStr = "0" + monStr;
            }
            if (day < 10) {
                dayStr = "0" + day;
            }
            SaveValue instance = SaveValue.getInstance(this.context);
            String str = this.mItemId;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(year);
            String str2 = formatOrder;
            sb2.append("/");
            sb2.append(monStr);
            sb2.append("/");
            sb2.append(dayStr);
            instance.saveStrValue(str, sb2.toString());
            String tempDate = year + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + monStr + MtkTvScanDvbsBase.TVAPI_DVBS_LINK_STRING + dayStr;
            saveShceduleDateTime(tempDate);
            MtkLog.d("Picker", "tempDate: " + tempDate);
        }
        this.mBackStack.accept(this);
    }

    private void saveShceduleDateTime(String tempDate) {
        MtkLog.d("Picker", "saveShceduleDateTime: " + tempDate);
        String[][] twoDateTimeArrays = compareDateTime();
        if (twoDateTimeArrays != null) {
            int year1 = Integer.parseInt(twoDateTimeArrays[0][0].substring(0, 4));
            int month1 = Integer.parseInt(twoDateTimeArrays[0][0].substring(5, 7));
            int day1 = Integer.parseInt(twoDateTimeArrays[0][0].substring(8));
            int hour1 = Integer.parseInt(twoDateTimeArrays[0][1].substring(0, 2));
            int minute1 = Integer.parseInt(twoDateTimeArrays[0][1].substring(3, 5));
            int year2 = Integer.parseInt(twoDateTimeArrays[1][0].substring(0, 4));
            int month2 = Integer.parseInt(twoDateTimeArrays[1][0].substring(5, 7));
            int day2 = Integer.parseInt(twoDateTimeArrays[1][0].substring(8));
            int hour2 = Integer.parseInt(twoDateTimeArrays[1][1].substring(0, 2));
            int minute2 = Integer.parseInt(twoDateTimeArrays[1][1].substring(3, 5));
            MtkTvTimeFormat.getInstance().set(0, minute1, hour1, day1, month1 - 1, year1);
            EditChannel.getInstance(this.context).setSchBlockFromUTCTime(Integer.valueOf(this.channelId).intValue(), MtkTvTimeFormat.getInstance().toMillis());
            MtkTvTimeFormat.getInstance().set(0, minute2, hour2, day2, month2 - 1, year2);
            EditChannel.getInstance(this.context).setSchBlockTOUTCTime(Integer.valueOf(this.channelId).intValue(), MtkTvTimeFormat.getInstance().toMillis());
        }
    }

    private String[][] compareDateTime() {
        SaveValue instance = SaveValue.getInstance(this.context);
        String dateStart = instance.readStrValue(MenuConfigManager.TIME_START_DATE + this.channelId);
        SaveValue instance2 = SaveValue.getInstance(this.context);
        String dateEnd = instance2.readStrValue(MenuConfigManager.TIME_END_DATE + this.channelId);
        SaveValue instance3 = SaveValue.getInstance(this.context);
        String timeStart = instance3.readStrValue(MenuConfigManager.TIME_START_TIME + this.channelId);
        SaveValue instance4 = SaveValue.getInstance(this.context);
        String timeEnd = instance4.readStrValue(MenuConfigManager.TIME_END_TIME + this.channelId);
        Log.d("Picker", "compareDateTime:dstart:" + dateStart + ",dend:" + dateEnd + ",tstart:" + timeStart + ",tend:" + timeEnd);
        StringBuilder sb = new StringBuilder();
        sb.append("mItemId:");
        sb.append(this.mItemId);
        MtkLog.d("Picker", sb.toString());
        if (this.mItemId.startsWith(MenuConfigManager.TIME_START_DATE)) {
            if (dateStart.compareTo(dateEnd) > 0) {
                return null;
            }
            if (dateStart.compareTo(dateEnd) == 0 && timeStart.compareTo(timeEnd) >= 0) {
                return null;
            }
            if (this.mResultListener != null) {
                this.mResultListener.onCommitResult(dateStart);
            }
            SaveValue instance5 = SaveValue.getInstance(this.context);
            instance5.saveStrValue(MenuConfigManager.TIME_END_DATE + this.channelId, dateEnd);
            SaveValue instance6 = SaveValue.getInstance(this.context);
            instance6.saveStrValue(MenuConfigManager.TIME_END_TIME + this.channelId, timeEnd);
        } else if (this.mItemId.startsWith(MenuConfigManager.TIME_END_DATE)) {
            if (dateStart.compareTo(dateEnd) > 0) {
                return null;
            }
            if (dateStart.compareTo(dateEnd) == 0 && timeStart.compareTo(timeEnd) >= 0) {
                return null;
            }
            Log.d("Picker", "mResultListener=" + this.mResultListener);
            if (this.mResultListener != null) {
                this.mResultListener.onCommitResult(dateEnd);
            }
            SaveValue instance7 = SaveValue.getInstance(this.context);
            instance7.saveStrValue(MenuConfigManager.TIME_START_DATE + this.channelId, dateStart);
            SaveValue instance8 = SaveValue.getInstance(this.context);
            instance8.saveStrValue(MenuConfigManager.TIME_START_TIME + this.channelId, timeStart);
        }
        Log.d("Picker", "compareDateTime222:dstart:" + dateStart + ",dend:" + dateEnd + ",tstart:" + timeStart + ",tend:" + timeEnd);
        return new String[][]{new String[]{dateStart, timeStart}, new String[]{dateEnd, timeEnd}};
    }
}
