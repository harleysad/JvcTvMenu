package com.mediatek.wwtv.setting.base.scan.model;

import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.Date;
import java.util.HashMap;

public class ScheduleItem implements Comparable<Object> {
    public static final String CHNNEL_NUM = "CHNNEL_NUM";
    public static final String END_TIME = "END_TIME";
    public static final int REPEAT_TYPE_DAILY = 2;
    public static final int REPEAT_TYPE_ONCE = 0;
    public static final int REPEAT_TYPE_WEEK = 1;
    public static final String START_TIME = "START_TIME";
    private final Boolean DEBUG = true;
    public final int SCHEDULE_TYPE_RECORD = 0;
    public final int SCHEDULE_TYPE_REMINDER = 1;
    private final String TAG = "ScheduleItem";
    private int channelID = 0;
    private String channelName = "";
    private int channelNum = 0;
    private Date date = new Date();
    private Long duration = 0L;
    private Date endTime = new Date();
    private boolean isEnble = true;
    private boolean isNewItem = false;
    private int repeatType = 0;
    private int scheduleType = 0;
    private String srcType = "DTV";
    private Date startTime = new Date();
    private int taskID = 0;
    private HashMap<String, Boolean> weekList = new HashMap<>();
    private int weeklyRepeat = 0;

    public HashMap<String, Boolean> getWeekList() {
        this.weekList.put("Monday", true);
        return this.weekList;
    }

    public void setWeekList(HashMap<String, Boolean> weekList2) {
        this.weekList = weekList2;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(String channelName2) {
        this.channelName = channelName2;
    }

    public int getChannelNum() {
        return this.channelNum;
    }

    public void setChannelNum(int channelNum2) {
        this.channelNum = channelNum2;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date2) {
        this.date = date2;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration2) {
        this.duration = duration2;
    }

    public int getRemindType() {
        return this.scheduleType;
    }

    public void setRemindType(int scheduleType2) {
        this.scheduleType = scheduleType2;
    }

    public int getRepeatType() {
        return this.repeatType;
    }

    public void setRepeatType(int repeatType2) {
        this.repeatType = repeatType2;
    }

    public int getWeeklyRepeat() {
        return this.weeklyRepeat;
    }

    public void setWeeklyRepeat(int weeklyRepeat2) {
        this.weeklyRepeat = weeklyRepeat2;
    }

    public String getSrcType() {
        return this.srcType;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime2) {
        this.startTime = startTime2;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime2) {
        this.endTime = endTime2;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int dbID) {
        this.taskID = dbID;
    }

    public void setSrcType(String srcType2) {
        this.srcType = srcType2;
    }

    public boolean isEnble() {
        return this.isEnble;
    }

    public void setEnble(boolean isEnble2) {
        this.isEnble = isEnble2;
    }

    public int getChannelID() {
        return this.channelID;
    }

    public void setChannelID(int channelID2) {
        this.channelID = channelID2;
    }

    public boolean isNewItem() {
        return this.isNewItem;
    }

    public void setNewItem(boolean isNewItem2) {
        this.isNewItem = isNewItem2;
    }

    public void showDebugInfo() {
        if (this.DEBUG.booleanValue()) {
            try {
                MtkLog.d("ScheduleItem", "srcType:" + this.srcType);
                MtkLog.d("ScheduleItem", "channelNum:" + this.channelNum);
                MtkLog.d("ScheduleItem", "date:" + this.date.toGMTString());
                MtkLog.d("ScheduleItem", "startTime:" + this.startTime.toGMTString());
                MtkLog.d("ScheduleItem", "endTime:" + this.endTime.toGMTString());
                MtkLog.d("ScheduleItem", "duration:" + this.duration);
                MtkLog.d("ScheduleItem", "srcType:" + this.srcType);
                MtkLog.d("ScheduleItem", "scheduleType:" + this.scheduleType);
                MtkLog.d("ScheduleItem", "repeatType:" + this.repeatType);
                MtkLog.d("ScheduleItem", "weeklyRepeat:" + this.weeklyRepeat);
                MtkLog.d("ScheduleItem", "isEnble:" + this.isEnble);
                MtkLog.d("ScheduleItem", "channelID:" + this.channelID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int compareTo(Object another) {
        long one = getStartTime().getTime();
        long two = ((ScheduleItem) another).getStartTime().getTime();
        MtkLog.e("compareTo", "sItem:" + one + "____:" + two);
        return (int) (one - two);
    }
}
