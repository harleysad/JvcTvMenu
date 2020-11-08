package com.mediatek.wwtv.tvcenter.epg.sa.db;

import android.provider.BaseColumns;

public class ProgramColumn implements BaseColumns {
    public static final String CHANNEL_ID = "channelId";
    public static final String CHANNEL_NO_NAME = "channelNoName";
    public static final String PROGRAM_ID = "programId";
    public static final String PROGRAM_NAME = "programName";
    public static final String PROGRAM_START_TIME = "programStartTime";
    public static final String[] PROJECTION = {"_id", CHANNEL_ID, PROGRAM_ID, CHANNEL_NO_NAME, PROGRAM_NAME, PROGRAM_START_TIME};
}
