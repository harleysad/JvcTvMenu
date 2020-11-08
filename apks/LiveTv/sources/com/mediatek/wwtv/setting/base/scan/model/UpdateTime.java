package com.mediatek.wwtv.setting.base.scan.model;

import android.content.Context;
import android.provider.Settings;
import android.text.format.Time;
import com.mediatek.wwtv.tvcenter.util.SaveValue;
import java.util.Calendar;

public class UpdateTime implements Runnable {
    static boolean isRun = true;
    private int hour = 0;
    private Context mContext;
    private UpdateListener mListener;
    private int minute = 0;
    private int month = 0;
    private int monthDay = 0;
    private int second = 0;
    private Time time = new Time();
    private Time timeModify;
    private String type;
    private int year = 0;

    public interface UpdateListener {
        void update(String str);
    }

    public UpdateTime() {
    }

    public UpdateTime(String type2) {
        this.type = type2;
    }

    public synchronized Time getTime() {
        this.time.setToNow();
        return this.time;
    }

    public synchronized void getDetailTime() {
        if (SaveValue.getInstance(this.mContext).readValue("SETUP_auto_syn") == 1) {
            Calendar mCalendar = Calendar.getInstance();
            this.year = mCalendar.get(1);
            if ("SETUP_time".equals(this.type)) {
                this.month = mCalendar.get(2);
            } else {
                this.month = mCalendar.get(2) + 1;
            }
            this.monthDay = mCalendar.get(5);
            this.hour = mCalendar.get(11);
            this.minute = mCalendar.get(12);
            this.second = mCalendar.get(13);
        } else {
            this.timeModify = getTime();
            this.year = this.timeModify.year;
            if ("SETUP_time".equals(this.type)) {
                this.month = this.timeModify.month;
            } else {
                this.month = this.timeModify.month + 1;
            }
            this.monthDay = this.timeModify.monthDay;
            this.hour = this.timeModify.hour;
            this.minute = this.timeModify.minute;
            this.second = this.timeModify.second;
        }
    }

    public void modifyTime() {
        this.time.set(this.second, this.minute, this.hour, this.monthDay, this.month, this.year);
        Long valueOf = Long.valueOf(this.time.toMillis(true));
        getTime();
    }

    public void run() {
        while (isRun) {
            getDetailTime();
            if ("SETUP_time".equals(this.type)) {
                String hourStr = this.hour + "";
                String minStr = this.minute + "";
                String secStr = this.second + "";
                if (this.hour < 10) {
                    hourStr = 0 + hourStr;
                }
                if (this.minute < 10) {
                    minStr = 0 + minStr;
                }
                if (this.second < 10) {
                    secStr = 0 + secStr;
                }
                this.mListener.update(hourStr + ":" + minStr + ":" + secStr);
            }
            if ("SETUP_date".equals(this.type)) {
                String monString = "" + this.month;
                String dayString = "" + this.monthDay;
                if (this.month < 10) {
                    monString = 0 + monString;
                }
                if (this.monthDay < 10) {
                    dayString = 0 + dayString;
                }
                this.mListener.update(this.year + "/" + monString + "/" + dayString);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startprocess(UpdateListener listener, Context context) {
        this.mListener = listener;
        isRun = true;
        new Thread(this).start();
        this.mContext = context;
    }

    public void shutDownThread() {
        isRun = false;
    }

    public void startThread() {
        isRun = true;
    }

    public void setAutoState(Context context, int value) {
        Settings.System.putInt(context.getContentResolver(), "auto_time", value);
    }

    public void onTimeModified(String time2) {
        if ("SETUP_time".equals(this.type)) {
            int hour2 = Integer.parseInt(time2.substring(0, 2));
            int minute2 = Integer.parseInt(time2.substring(3, 5));
            int second2 = 0;
            if (time2.length() > 5) {
                second2 = Integer.parseInt(time2.substring(6));
            }
            this.hour = hour2;
            this.minute = minute2;
            this.second = second2;
        }
        if ("SETUP_date".equals(this.type)) {
            int year2 = Integer.parseInt(time2.substring(0, 4));
            int month2 = Integer.parseInt(time2.substring(5, 7));
            int monthDay2 = Integer.parseInt(time2.substring(8));
            this.year = year2;
            this.month = month2 - 1;
            this.monthDay = monthDay2;
        }
        modifyTime();
    }
}
