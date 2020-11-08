package com.android.tv.settings.partnercustomizer.timer.picker;

import android.content.res.Resources;
import com.android.tv.settings.R;
import java.text.DateFormatSymbols;

public class PickerConstants {

    public static class Date {
        public final String dateSeparator;
        public final String[] days31;
        public final String[] months;

        private Date(Resources resources) {
            this.months = new DateFormatSymbols().getShortMonths();
            this.days31 = PickerConstants.createStringIntArrays(31, false, 2);
            this.dateSeparator = resources.getString(R.string.date_separator);
        }
    }

    public static class Time {
        public final String[] ampm;
        public final String[] hours12;
        public final String[] hours24;
        public final String[] minutes;
        public final String timeSeparator;

        private Time(Resources resources) {
            this.hours12 = PickerConstants.createStringIntArrays(12, false, 2);
            this.hours24 = PickerConstants.createStringIntArrays(23, true, 2);
            this.minutes = PickerConstants.createStringIntArrays(59, true, 2);
            this.ampm = resources.getStringArray(R.array.ampm);
            this.timeSeparator = resources.getString(R.string.time_separator);
        }
    }

    private PickerConstants() {
    }

    /* access modifiers changed from: private */
    public static String[] createStringIntArrays(int lastNumber, boolean startAtZero, int minLen) {
        int range = startAtZero ? lastNumber + 1 : lastNumber;
        String format = "%0" + minLen + "d";
        String[] array = new String[range];
        for (int i = 0; i < range; i++) {
            if (minLen > 0) {
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(startAtZero ? i : i + 1);
                array[i] = String.format(format, objArr);
            } else {
                array[i] = String.valueOf(startAtZero ? i : i + 1);
            }
        }
        return array;
    }

    public static Date getDateInstance(Resources resources) {
        return new Date(resources);
    }

    public static Time getTimeInstance(Resources resources) {
        return new Time(resources);
    }
}
