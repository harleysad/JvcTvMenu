package com.mediatek.wwtv.tvcenter.epg.sa.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.mediatek.wwtv.tvcenter.epg.EPGUtil;
import java.util.ArrayList;
import java.util.List;

public class DBMgrProgramList {
    private static DBMgrProgramList instance;
    private int mCount = 0;
    private DBHelperProgramList mDBHelperProgramList;
    private SQLiteDatabase mProgramDb;

    public DBMgrProgramList(Context context) {
        this.mDBHelperProgramList = new DBHelperProgramList(context);
    }

    public static synchronized DBMgrProgramList getInstance(Context context) {
        DBMgrProgramList dBMgrProgramList;
        synchronized (DBMgrProgramList.class) {
            if (instance == null) {
                instance = new DBMgrProgramList(context);
            }
            dBMgrProgramList = instance;
        }
        return dBMgrProgramList;
    }

    public synchronized void getReadableDB() {
        Log.d("guanglei", "DBMgrProgramList getReadableDB mCount: " + this.mCount);
        this.mProgramDb = this.mDBHelperProgramList.getReadableDatabase();
        this.mCount = this.mCount + 1;
    }

    public synchronized void getWriteableDB() {
        Log.d("guanglei", "DBMgrProgramList getWriteableDB mCount: " + this.mCount);
        this.mProgramDb = this.mDBHelperProgramList.getWritableDatabase();
        this.mCount = this.mCount + 1;
    }

    public void addProgram(EPGBookListViewDataItem bookedProgram) {
        for (EPGBookListViewDataItem tempItem : getProgramListWithDelete()) {
            if (tempItem.mProgramStartTime == bookedProgram.mProgramStartTime) {
                deleteProgram(tempItem);
            }
        }
        ContentValues cv = new ContentValues();
        cv.put(ProgramColumn.CHANNEL_ID, Integer.valueOf(bookedProgram.mChannelId));
        cv.put(ProgramColumn.PROGRAM_ID, Integer.valueOf(bookedProgram.mProgramId));
        cv.put(ProgramColumn.CHANNEL_NO_NAME, bookedProgram.mChannelNoName);
        cv.put(ProgramColumn.PROGRAM_NAME, bookedProgram.mProgramName);
        cv.put(ProgramColumn.PROGRAM_START_TIME, Long.valueOf(bookedProgram.mProgramStartTime));
        this.mProgramDb.insert(DBHelperProgramList.PROGRAM_LIST_TABLE, (String) null, cv);
    }

    public void deleteProgram(EPGBookListViewDataItem bookedProgram) {
        SQLiteDatabase sQLiteDatabase = this.mProgramDb;
        sQLiteDatabase.delete(DBHelperProgramList.PROGRAM_LIST_TABLE, "programStartTime=? and channelId=? and programId=?", new String[]{bookedProgram.mProgramStartTime + "", bookedProgram.mChannelId + "", bookedProgram.mProgramId + ""});
    }

    public void deleteAllPrograms() {
        this.mProgramDb.delete(DBHelperProgramList.PROGRAM_LIST_TABLE, (String) null, (String[]) null);
    }

    public List<EPGBookListViewDataItem> getProgramList() {
        Cursor c = this.mProgramDb.rawQuery("select * from programlist", (String[]) null);
        List<EPGBookListViewDataItem> mBookedList = new ArrayList<>();
        while (c.moveToNext()) {
            EPGBookListViewDataItem tempInfo = new EPGBookListViewDataItem();
            tempInfo.mChannelId = c.getInt(c.getColumnIndex(ProgramColumn.CHANNEL_ID));
            tempInfo.mProgramId = c.getInt(c.getColumnIndex(ProgramColumn.PROGRAM_ID));
            tempInfo.mChannelNoName = c.getString(c.getColumnIndex(ProgramColumn.CHANNEL_NO_NAME));
            tempInfo.mProgramName = c.getString(c.getColumnIndex(ProgramColumn.PROGRAM_NAME));
            tempInfo.mProgramStartTime = c.getLong(c.getColumnIndex(ProgramColumn.PROGRAM_START_TIME));
            tempInfo.marked = true;
            mBookedList.add(tempInfo);
        }
        c.close();
        return mBookedList;
    }

    public List<EPGBookListViewDataItem> getProgramListWithDelete() {
        Cursor c = this.mProgramDb.rawQuery("select * from programlist", (String[]) null);
        List<EPGBookListViewDataItem> mBookedList = new ArrayList<>();
        List<EPGBookListViewDataItem> mDeleteList = new ArrayList<>();
        while (c.moveToNext()) {
            EPGBookListViewDataItem tempInfo = new EPGBookListViewDataItem();
            tempInfo.mChannelId = c.getInt(c.getColumnIndex(ProgramColumn.CHANNEL_ID));
            tempInfo.mProgramId = c.getInt(c.getColumnIndex(ProgramColumn.PROGRAM_ID));
            tempInfo.mChannelNoName = c.getString(c.getColumnIndex(ProgramColumn.CHANNEL_NO_NAME));
            tempInfo.mProgramName = c.getString(c.getColumnIndex(ProgramColumn.PROGRAM_NAME));
            tempInfo.mProgramStartTime = c.getLong(c.getColumnIndex(ProgramColumn.PROGRAM_START_TIME));
            tempInfo.marked = true;
            if (tempInfo.mProgramStartTime < EPGUtil.getCurrentTime()) {
                mDeleteList.add(tempInfo);
            } else {
                mBookedList.add(tempInfo);
            }
        }
        for (EPGBookListViewDataItem tpInfo : mDeleteList) {
            deleteProgram(tpInfo);
        }
        c.close();
        return mBookedList;
    }

    public void deleteDB() {
        this.mProgramDb.execSQL("DROP TABLE IF EXISTS programlist");
    }

    public void deleteTable() {
        this.mProgramDb.execSQL("DROP TABLE IF EXISTS programlist");
    }

    public synchronized void closeDB() {
        this.mCount--;
        Log.d("guanglei", "DBMgrProgramList closeDB mCount:" + this.mCount);
        if (this.mCount <= 0) {
            this.mProgramDb.close();
        }
    }
}
