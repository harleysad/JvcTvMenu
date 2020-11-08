package com.mediatek.wwtv.tvcenter.dvr.db;

import android.provider.BaseColumns;

public class AlarmColumn implements BaseColumns {
    public static final String CHANNELID = "channelID";
    public static final String CHANNELNAME = "channelName";
    public static final String CHANNELNUM = "channelNum";
    public static final String DAYOFWEEK = "dayofweek";
    public static final String ENDTIME = "endTime";
    public static final String INPUTSOURCE = "inputSrc";
    public static final String ISENABLE = "isEnable";
    public static final String[] PROJECTION = {"_id", "taskId", "inputSrc", "channelNum", "startTime", "endTime", "scheduleType", "repeatType", "dayofweek", "isEnable", "channelID"};
    public static final String REPEATTYPE = "repeatType";
    public static final String SCHEDULETYPE = "scheduleType";
    public static final String STARTTIME = "startTime";
    public static final String TASKID = "taskId";
}
