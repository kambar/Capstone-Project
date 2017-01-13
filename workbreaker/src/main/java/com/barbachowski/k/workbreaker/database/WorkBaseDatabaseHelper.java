package com.barbachowski.k.workbreaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by K on 12/01/2017.
 */

public class WorkBaseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "workbreaker.db";
    private static final int DATABASE_VERSION = 1;

    public WorkBaseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ExerciseSessionTable.onCreate(sqLiteDatabase);
        ExerciseStatisticsTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        ExerciseSessionTable.onUpgrade(sqLiteDatabase,i,i);
        ExerciseStatisticsTable.onUpgrade(sqLiteDatabase,i,i);
    }
}
