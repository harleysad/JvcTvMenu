package com.mediatek.wwtv.tvcenter.epg.sa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperProgramList extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE = "CREATE TABLE programlist (_id integer primary key autoincrement,channelId INTEGER,programId INTEGER,channelNoName text,programName text,programStartTime INTEGER);";
    public static final String DATABASE_NAME = "bookedprogranlist.db";
    public static final int DATABASE_VERSION = 1;
    public static final String PROGRAM_LIST_TABLE = "programlist";

    public DBHelperProgramList(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS programlist");
        onCreate(db);
    }
}
