package com.barbachowski.k.workbreaker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

/**
 * Created by K on 12/01/2017.
 */

public class ExerciseSessionTable {
    // Database table
    public static final String TABLE_EXERCISE_SESSION = "exercise_session";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SKIPPED = "skipped";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EXERCISE_SESSION
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_SKIPPED + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ExerciseSessionTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_SESSION);
        onCreate(database);
    }
}
