package com.mediatek.wwtv.tvcenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtil {
    private static final String TAG = "DateFormatUtil";

    public static Date getDate(String string) {
        Date tempDate;
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            String dateStr = SimpleDateFormat.getDateInstance(2, Locale.CHINA).format(new Date(System.currentTimeMillis()));
            MtkLog.d(TAG, "getDate()--------dateStr---powoffTimeStr--------->" + dateStr + "  " + string);
            StringBuilder sb = new StringBuilder();
            sb.append(dateStr);
            sb.append("  ");
            sb.append(string);
            tempDate = simpleDateFormat.parse(sb.toString());
            SimpleDateFormat.getDateTimeInstance(2, 1, Locale.CHINA);
            MtkLog.d(TAG, "getDate()-----tempDate--1111111111-----" + simpleDateFormat.format(tempDate));
            Date currentSystem = new Date(System.currentTimeMillis());
            Long poweroff = Long.valueOf(tempDate.getTime());
            Long currentTime = Long.valueOf(currentSystem.getTime());
            if (poweroff.longValue() < currentTime.longValue()) {
                SimpleDateFormat.getDateTimeInstance(2, 1, Locale.CHINA);
                MtkLog.d(TAG, "poweroff---->" + simpleDateFormat.format(new Date(poweroff.longValue())));
                MtkLog.d(TAG, "currentTime---->" + simpleDateFormat.format(new Date(currentTime.longValue())));
                String dateStr2 = SimpleDateFormat.getDateInstance(2, Locale.CHINA).format(new Date(System.currentTimeMillis() + 86400000));
                tempDate = simpleDateFormat.parse(dateStr2 + "  " + string);
            }
        } catch (ParseException e) {
            MtkLog.d(TAG, "time is invalida");
            tempDate = new Date(System.currentTimeMillis() + 86400000);
            e.printStackTrace();
        }
        SimpleDateFormat.getDateTimeInstance(2, 1, Locale.CHINA);
        MtkLog.d(TAG, "getDate()-----tempDate---222222222----" + simpleDateFormat.format(tempDate));
        return tempDate;
    }

    public static boolean checkPowOffTimerInvalid(String string) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            String dateStr = SimpleDateFormat.getDateInstance(2, Locale.CHINA).format(new Date(System.currentTimeMillis()));
            Date tempDate = simpleDateFormat.parse(dateStr + "  " + string);
            Date currentSystem = new Date(System.currentTimeMillis());
            MtkLog.d(TAG, "poweroff---->" + simpleDateFormat.format(tempDate));
            MtkLog.d(TAG, "currentTime---->" + simpleDateFormat.format(currentSystem));
            if (Long.valueOf(tempDate.getTime()).longValue() <= Long.valueOf(currentSystem.getTime()).longValue()) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean checkPowOnTimerInvalid(String string) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            String dateStr = SimpleDateFormat.getDateInstance(2, Locale.CHINA).format(new Date(System.currentTimeMillis()));
            Date tempDate = simpleDateFormat.parse(dateStr + "  " + string);
            Date currentSystem = new Date(System.currentTimeMillis());
            Long poweron = Long.valueOf(tempDate.getTime());
            Long currentTime = Long.valueOf(currentSystem.getTime());
            MtkLog.d(TAG, "poweron---->" + simpleDateFormat.format(tempDate));
            MtkLog.d(TAG, "currentTime---->" + simpleDateFormat.format(currentSystem));
            if (poweron.longValue() < currentTime.longValue() - MessageType.delayMillis3 || poweron.longValue() > currentTime.longValue()) {
                return false;
            }
            MtkLog.d(TAG, "----power--on--start--system------");
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern("yyyy/MM/dd");
        String currentTime = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        MtkLog.d(TAG, "currentTime=" + currentTime);
        return currentTime;
    }
}
