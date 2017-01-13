package com.barbachowski.k.workbreaker.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by K on 12/01/2017.
 */

public class ExerciseStatisticsTable {
    private static String TAG = ExerciseStatisticsTable.class.getCanonicalName();

    // Database table
    public static final String TABLE_EXERCISE_STATISTICS = "exercise_statistics";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COUNT_SKIPPED = "count_skipped";
    public static final String COLUMN_COUNT_DONE = "count_done";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EXERCISE_STATISTICS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COUNT_SKIPPED + " integer not null, "
            + COLUMN_COUNT_DONE + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {

        database.execSQL(DATABASE_CREATE);

        //insert 0, 0 statistics
        ContentValues v = new ContentValues();
        v.put(COLUMN_COUNT_SKIPPED, 0);
        v.put(COLUMN_COUNT_DONE, 0);
        database.insert(ExerciseStatisticsTable.TABLE_EXERCISE_STATISTICS, null, v);
        Log.v(TAG, "Database initialized.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExerciseSessionTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_STATISTICS);
        onCreate(database);
    }
}
