package com.mediatek.wwtv.setting.widget.view;

import android.content.res.Resources;
import com.mediatek.wwtv.tvcenter.R;
import java.text.DateFormatSymbols;

public class PickerConstant {
    private static PickerConstant sInst;
    private static Object sInstLock = new Object();
    public final String[] ampm;
    public final String dateSeparator;
    public final String[] days28 = createStringIntArrays(28, false, 2);
    public final String[] days29 = createStringIntArrays(29, false, 2);
    public final String[] days30 = createStringIntArrays(30, false, 2);
    public final String[] days31 = createStringIntArrays(31, false, 2);
    public final String[] hours12 = createStringIntArrays(11, true, 2);
    public final String[] hours24 = createStringIntArrays(23, true, 2);
    public final String[] minutes = createStringIntArrays(59, true, 2);
    public final String[] months = new DateFormatSymbols().getShortMonths();
    public final String[] seconds = createStringIntArrays(59, true, 2);
    public final String timeSeparator;

    private PickerConstant(Resources resources) {
        this.ampm = resources.getStringArray(R.array.ampm);
        this.dateSeparator = resources.getString(R.string.date_separator);
        this.timeSeparator = resources.getString(R.string.time_separator);
    }

    private String[] createStringIntArrays(int lastNumber, boolean startAtZero, int minLen) {
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

    public static PickerConstant getInstance(Resources resources) {
        if (sInst == null) {
            sInst = new PickerConstant(resources);
        }
        return sInst;
    }
}
