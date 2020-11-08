package com.mediatek.wwtv.setting.widget.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvTimeFormat;
import com.mediatek.wwtv.setting.preferences.PreferenceUtil;
import com.mediatek.wwtv.setting.scan.EditChannel;
import com.mediatek.wwtv.setting.util.MenuConfigManager;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimePicker extends Picker {
    private static final String EXTRA_24H_FORMAT = "24h_format";
    private static final String EXTRA_DEFAULT_TO_CURRENT = "delault_to_current";
    private static final int HOURS_IN_HALF_DAY = 12;
    private Context context;
    private ColumnOrder mColumnOrder = new ColumnOrder();
    private int mInitHour;
    private boolean mInitIsPm;
    private int mInitMinute;
    private boolean mIs24hFormat = false;
    private boolean mPendingTime = false;

    private static class ColumnOrder {
        int mAmPm;
        int mHours;
        int mMinutes;

        private ColumnOrder() {
            this.mHours = 0;
            this.mMinutes = 1;
            this.mAmPm = 2;
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
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.mItemId = bundle.getCharSequence(PreferenceUtil.PARENT_PREFERENCE_ID).toString();
        }
        MtkLog.d("Picker", "mItemId:" + this.mItemId);
        this.mIs24hFormat = getArguments().getBoolean(EXTRA_24H_FORMAT, false);
        boolean useCurrent = getArguments().getBoolean(EXTRA_DEFAULT_TO_CURRENT, false);
        if (!TextUtils.isEmpty(this.mItemId) && (this.mItemId.contains(MenuConfigManager.TIME_START_TIME) || this.mItemId.contains(MenuConfigManager.TIME_END_TIME))) {
            this.mIs24hFormat = true;
            useCurrent = true;
        }
        MtkLog.d("Picker", "useCurrent:" + useCurrent);
        if (!TextUtils.isEmpty(this.mItemId) && this.mItemId.length() > MenuConfigManager.TIME_END_TIME.length()) {
            if (this.mItemId.contains(MenuConfigManager.TIME_START_TIME)) {
                this.channelId = this.mItemId.substring(16);
            } else {
                this.channelId = this.mItemId.substring(14);
            }
        }
        MtkLog.d("guanglei", "channelId:" + this.channelId);
        if (useCurrent) {
            this.mPendingTime = true;
            String onTime = "00:00:00";
            if (this.mItemId.equals(MenuConfigManager.TIMER1)) {
                onTime = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIMER1);
            } else if (this.mItemId.equals(MenuConfigManager.TIMER2)) {
                onTime = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIMER2);
            } else if (this.mItemId.equals("SETUP_time")) {
                onTime = new SimpleDateFormat("HH:mm").format(new Date());
            } else if (this.mItemId.contains(MenuConfigManager.TIME_START_TIME) || this.mItemId.contains(MenuConfigManager.TIME_END_TIME)) {
                onTime = SaveValue.getInstance(this.context).readStrValue(this.mItemId);
            }
            if (onTime == null) {
                onTime = "00:00:00";
            }
            String[] strs = onTime.split(":");
            this.mInitHour = Integer.valueOf(strs[0]).intValue();
            if (!this.mIs24hFormat) {
                if (this.mInitHour >= 12) {
                    this.mInitIsPm = true;
                    if (this.mInitHour > 12) {
                        this.mInitHour = (this.mInitHour - 12) - 1;
                    }
                } else {
                    this.mInitIsPm = false;
                    if (this.mInitHour == 0) {
                        this.mInitHour = 0;
                    }
                }
            }
            this.mInitMinute = Integer.valueOf(strs[1]).intValue();
        }
        Locale locale = Locale.getDefault();
        String hmaPattern = DateFormat.getBestDateTimePattern(locale, "hma");
        boolean isAmPmAtEnd = hmaPattern.indexOf("a") > hmaPattern.indexOf("m");
        if (TextUtils.getLayoutDirectionFromLocale(locale) == 1) {
            this.mColumnOrder.mHours = 1;
            this.mColumnOrder.mMinutes = 0;
        }
        if (!isAmPmAtEnd && !this.mIs24hFormat) {
            this.mColumnOrder.mAmPm = 0;
            this.mColumnOrder.mHours++;
            this.mColumnOrder.mMinutes++;
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
        updateSelection(this.mColumnOrder.mHours, this.mIs24hFormat ? hour : hour - 1);
        updateSelection(this.mColumnOrder.mMinutes, minute);
        if (!this.mIs24hFormat) {
            updateSelection(this.mColumnOrder.mAmPm, isPm);
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
        ret.set(this.mColumnOrder.mHours, hours);
        ret.set(this.mColumnOrder.mMinutes, minutes);
        if (!this.mIs24hFormat) {
            ret.set(this.mColumnOrder.mAmPm, new PickerColumn(this.mConstant.ampm));
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public String getSeparator() {
        return this.mConstant.timeSeparator;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context2) {
        this.context = context2;
    }

    /* access modifiers changed from: protected */
    public void recordResult(List<String> mResult) {
        String hours1;
        String minutes1;
        super.recordResult(mResult);
        if (!TextUtils.isEmpty(this.mItemId) && (this.mItemId.contains(MenuConfigManager.TIME_START_TIME) || this.mItemId.contains(MenuConfigManager.TIME_END_TIME))) {
            int hour = Integer.parseInt(mResult.get(0));
            int minute = Integer.parseInt(mResult.get(1));
            if (hour < 10) {
                hours1 = "0" + hour;
            } else {
                hours1 = "" + hour;
            }
            if (minute < 10) {
                minutes1 = "0" + minute;
            } else {
                minutes1 = "" + minute;
            }
            String tempTime = hours1 + ":" + minutes1;
            SaveValue.getInstance(this.context).saveStrValue(this.mItemId, tempTime);
            saveShceduleDateTime(tempTime);
        }
        this.mBackStack.accept(this);
    }

    private void saveShceduleDateTime(String tempTime) {
        String[][] twoDateTimeArrays = compareDateTime();
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

    private String[][] compareDateTime() {
        String dateStart = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIME_START_DATE + this.channelId);
        String dateEnd = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIME_END_DATE + this.channelId);
        String timeStart = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIME_START_TIME + this.channelId);
        String timeEnd = SaveValue.getInstance(this.context).readStrValue(MenuConfigManager.TIME_END_TIME + this.channelId);
        Log.d("Picker", "compareDateTime:dstart:" + dateStart + ",dend:" + dateEnd + ",tstart:" + timeStart + ",tend:" + timeEnd);
        StringBuilder sb = new StringBuilder();
        sb.append("mItemId=");
        sb.append(this.mItemId);
        Log.d("Picker", sb.toString());
        if (this.mItemId.startsWith(MenuConfigManager.TIME_END_TIME)) {
            if (dateStart.compareTo(dateEnd) == 0 && timeStart.compareTo(timeEnd) > 0) {
                String[] tarray = timeStart.split(":");
                int nHour = Integer.parseInt(tarray[0]);
                if (nHour == 23) {
                    timeEnd = timeStart;
                } else if (nHour <= 0 || nHour >= 9) {
                    timeEnd = (nHour + 1) + ":" + tarray[1];
                } else {
                    timeEnd = "0" + (nHour + 1) + ":" + tarray[1];
                }
            }
            SaveValue.getInstance(this.context).saveStrValue(MenuConfigManager.TIME_END_TIME + this.channelId, timeEnd);
            Log.d("Picker", "mResultListener=" + this.mResultListener);
            if (this.mResultListener != null) {
                this.mResultListener.onCommitResult(timeEnd);
            }
        } else if (this.mItemId.startsWith(MenuConfigManager.TIME_START_TIME)) {
            if (dateStart.compareTo(dateEnd) == 0 && timeStart.compareTo(timeEnd) > 0) {
                String[] tarray2 = timeEnd.split(":");
                int nHour2 = Integer.parseInt(tarray2[0]);
                if (nHour2 == 0) {
                    timeStart = timeEnd;
                } else if (nHour2 <= 0 || nHour2 > 10) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(nHour2 - 1);
                    sb2.append(":");
                    sb2.append(tarray2[1]);
                    timeStart = sb2.toString();
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("0");
                    sb3.append(nHour2 - 1);
                    sb3.append(":");
                    sb3.append(tarray2[1]);
                    timeStart = sb3.toString();
                }
            }
            SaveValue.getInstance(this.context).saveStrValue(MenuConfigManager.TIME_START_TIME + this.channelId, timeStart);
            Log.d("Picker", "mResultListener=" + this.mResultListener);
            if (this.mResultListener != null) {
                this.mResultListener.onCommitResult(timeStart);
            }
        }
        Log.d("Picker", "compareDateTime222:dstart:" + dateStart + ",dend:" + dateEnd + ",tstart:" + timeStart + ",tend:" + timeEnd);
        return new String[][]{new String[]{dateStart, timeStart}, new String[]{dateEnd, timeEnd}};
    }
}
