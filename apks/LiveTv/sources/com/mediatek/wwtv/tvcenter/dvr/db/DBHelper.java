package com.mediatek.wwtv.tvcenter.dvr.db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.tv.TvContract;
import android.net.Uri;
import android.support.media.tv.TvContractCompat;
import android.support.v4.app.NotificationCompat;
import com.mediatek.wwtv.tvcenter.util.MtkLog;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBHelper extends SQLiteOpenHelper {
    private static final Uri CONTENT_URI = TvContract.RecordedPrograms.CONTENT_URI;
    private static final String ORDERBY = "channel_id,start_time_utc_millis";
    public static final String SCHEDULE_ALARM_ACTION = "com.mediatek.wwtv.tvcenter.schedule.dvr";
    public static final String SCHEDULE_CONTACTS_TABLE = "scheduledvr";
    private static final String SCHEDULE_DATABASE_CREATE = "CREATE TABLE scheduledvr (_id INTEGER PRIMARY KEY AUTOINCREMENT,taskId TEXT,inputSrc TEXT NOT NULL,channelNum TEXT NOT NULL,channelName TEXT,startTime INTEGER,endTime INTEGER,scheduleType INTEGER,repeatType INTEGER,dayofweek INTEGER,isEnable INTEGER,channelID TEXT NOT NULL);";
    public static final String SCHEDULE_DATABASE_NAME = "alarm.db";
    public static final int SCHEDULE_DATABASE_VERSION = 1;
    private static final String TAG = "DBHelper[dvr]";
    private static DBHelper mDBHelper = null;
    /* access modifiers changed from: private */
    public final AlarmManager mAlarm;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public PendingIntent mPendingIntent = null;
    private final Runnable mUpdateAlarm = new Runnable() {
        public void run() {
            synchronized (DBHelper.class) {
                if (DBHelper.this.mPendingIntent != null) {
                    DBHelper.this.mAlarm.cancel(DBHelper.this.mPendingIntent);
                }
                Cursor c = DBHelper.this.mContentResolver.query(ScheduleDVRProvider.CONTENT_URI, (String[]) null, (String) null, (String[]) null, "startTime ASC");
                if (c == null) {
                    MtkLog.d(DBHelper.TAG, "updateAlarm, c null");
                    return;
                }
                if (c.moveToNext()) {
                    String channelId = c.getString(c.getColumnIndex("channelID"));
                    long startTime = c.getLong(c.getColumnIndex("startTime"));
                    long endTime = c.getLong(c.getColumnIndex("endTime"));
                    Intent intent = new Intent(DBHelper.SCHEDULE_ALARM_ACTION);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    intent.putExtra("channelID", channelId);
                    PendingIntent unused = DBHelper.this.mPendingIntent = PendingIntent.getBroadcast(DBHelper.this.mContext, 0, intent, 0);
                    DBHelper.this.mAlarm.set(1, startTime, DBHelper.this.mPendingIntent);
                    MtkLog.d(DBHelper.TAG, "updateAlarm, mAlarm.set, " + channelId + "," + startTime + "," + endTime);
                }
                c.close();
            }
        }
    };

    private DBHelper(Context context) {
        super(context, SCHEDULE_DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mAlarm = (AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
    }

    public static DBHelper getInstance(Context context) {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(context);
        }
        return mDBHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCHEDULE_DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS scheduledvr");
        onCreate(db);
    }

    public void updateAlarm() {
        new Thread(this.mUpdateAlarm).start();
    }

    public static List<RecordedProgramInfo> getRecordedList(Context content) {
        List<RecordedProgramInfo> programList = new ArrayList<>();
        Cursor c = content.getContentResolver().query(CONTENT_URI, (String[]) null, (String) null, (String[]) null, ORDERBY);
        if (c == null) {
            return programList;
        }
        while (c.moveToNext()) {
            RecordedProgramInfo info = new RecordedProgramInfo();
            parserRecordedProgramInfo(info, c);
            programList.add(info);
            MtkLog.d(TAG, "getRecordedList, " + info);
        }
        c.close();
        MtkLog.d(TAG, "getRecordedList, " + programList.size());
        return programList;
    }

    public static int deleteRecordById(Context content, long id) {
        return content.getContentResolver().delete(CONTENT_URI, "_id = " + id, (String[]) null);
    }

    public static RecordedProgramInfo getRecordedInfoById(Context content, long id) {
        RecordedProgramInfo program = new RecordedProgramInfo();
        Cursor c = content.getContentResolver().query(CONTENT_URI, (String[]) null, "_id=" + id, (String[]) null, (String) null);
        if (c == null) {
            return null;
        }
        if (c.moveToNext()) {
            parserRecordedProgramInfo(program, c);
        }
        c.close();
        MtkLog.d(TAG, "getRecordedInfoById, " + program);
        return program;
    }

    private static void parserRecordedProgramInfo(RecordedProgramInfo info, Cursor c) {
        info.mId = c.getLong(c.getColumnIndex("_id"));
        info.mInputId = c.getString(c.getColumnIndex("input_id"));
        info.mChannelId = c.getLong(c.getColumnIndex("channel_id"));
        info.mTitle = c.getString(c.getColumnIndex("title"));
        info.mSeasonDisplayNumber = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_SEASON_DISPLAY_NUMBER));
        info.mSeasonTitle = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_SEASON_TITLE));
        info.mEpisodeDisplayNumber = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_EPISODE_DISPLAY_NUMBER));
        info.mEpisodeTitle = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_EPISODE_TITLE));
        info.mStartTimeUtcMills = c.getLong(c.getColumnIndex("start_time_utc_millis"));
        info.mEndTimeUtcMills = c.getLong(c.getColumnIndex("end_time_utc_millis"));
        info.mBroadcastGenre = c.getString(c.getColumnIndex("broadcast_genre"));
        info.mCanonicalGenre = c.getString(c.getColumnIndex("canonical_genre"));
        info.mShortDescription = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_SHORT_DESCRIPTION));
        info.mLongDescription = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_LONG_DESCRIPTION));
        info.mVideoWidth = c.getInt(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_VIDEO_WIDTH));
        info.mVideoHeight = c.getInt(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_VIDEO_HEIGHT));
        info.mAudioLanguage = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_AUDIO_LANGUAGE));
        info.mContentRating = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_CONTENT_RATING));
        info.mPosterArtUri = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_POSTER_ART_URI));
        info.mThumbnallUri = c.getString(c.getColumnIndex(TvContractCompat.ProgramColumns.COLUMN_THUMBNAIL_URI));
        boolean z = true;
        if (c.getInt(c.getColumnIndex("searchable")) != 1) {
            z = false;
        }
        info.mSearchable = z;
        info.mRecordingDataUri = c.getString(c.getColumnIndex(TvContractCompat.RecordedPrograms.COLUMN_RECORDING_DATA_URI));
        Matcher matcher = Pattern.compile("\\d{4}_\\d{6}.pvr").matcher(info.mRecordingDataUri);
        while (matcher.find()) {
            info.mRecordingDataUri = matcher.group();
        }
        info.mRecordingDataBytes = c.getLong(c.getColumnIndex(TvContractCompat.RecordedPrograms.COLUMN_RECORDING_DATA_BYTES));
        info.mRecordingDurationMills = c.getInt(c.getColumnIndex(TvContractCompat.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS));
        info.mRecordingExpireTimeUtcMills = c.getLong(c.getColumnIndex(TvContractCompat.RecordedPrograms.COLUMN_RECORDING_EXPIRE_TIME_UTC_MILLIS));
        info.mInternalData = c.getBlob(c.getColumnIndex("internal_provider_data"));
        info.mInternalFlag1 = c.getInt(c.getColumnIndex("internal_provider_flag1"));
        info.mInternalFlag2 = c.getInt(c.getColumnIndex("internal_provider_flag2"));
        info.mInternalFlag3 = c.getInt(c.getColumnIndex("internal_provider_flag3"));
        info.mInternalFlag4 = c.getInt(c.getColumnIndex("internal_provider_flag4"));
        info.mVersionNumber = c.getInt(c.getColumnIndex("version_number"));
    }
}
