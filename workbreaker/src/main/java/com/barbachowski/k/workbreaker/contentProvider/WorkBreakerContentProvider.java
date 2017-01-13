package com.barbachowski.k.workbreaker.contentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.barbachowski.k.workbreaker.database.ExerciseSessionTable;
import com.barbachowski.k.workbreaker.database.ExerciseStatisticsTable;
import com.barbachowski.k.workbreaker.database.WorkBaseDatabaseHelper;

/**
 * Created by K on 12/01/2017.
 */

public class WorkBreakerContentProvider extends ContentProvider {

    private WorkBaseDatabaseHelper database;

    // used for the UriMacher
    private static final int EXERCISES = 10;
    private static final int EXERCISE_ID = 20;
    private static final int STATISTICS = 30;
    private static final int STATISTICS_ID = 40;

    private static final String AUTHORITY = "com.barbachowski.k.workbreaker.contentprovider";

    private static final String EXERCISE_SESSIONS_PATH = "exercise_sessions";
    private static final String EXERCISE_STATISTICS_PATH = "exercise_statistics";
    public static final Uri EXERCISE_SESSIONS__URI = Uri.parse("content://" + AUTHORITY + "/" + EXERCISE_SESSIONS_PATH);
    public static final Uri EXERCISE_STATISTICS__URI = Uri.parse("content://" + AUTHORITY + "/" + EXERCISE_STATISTICS_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/exercises";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/exercise";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, EXERCISE_SESSIONS_PATH, EXERCISES);
        sURIMatcher.addURI(AUTHORITY, EXERCISE_SESSIONS_PATH + "/#", EXERCISE_ID);
        sURIMatcher.addURI(AUTHORITY, EXERCISE_STATISTICS_PATH, STATISTICS);
    }


    @Override
    public boolean onCreate() {
        database = new WorkBaseDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);

        switch (uriType){
            case EXERCISES:
                // Set the table
                queryBuilder.setTables(ExerciseSessionTable.TABLE_EXERCISE_SESSION);
                break;
            case STATISTICS:
                // Set the table
                queryBuilder.setTables(ExerciseStatisticsTable.TABLE_EXERCISE_STATISTICS);
                break;
            case EXERCISE_ID:
                // Set the table
                queryBuilder.setTables(ExerciseSessionTable.TABLE_EXERCISE_SESSION);
                queryBuilder.appendWhere(ExerciseSessionTable.COLUMN_ID + "="
                    + uri.getLastPathSegment());
                break;
            case STATISTICS_ID:
                // Set the table
                queryBuilder.setTables(ExerciseStatisticsTable.TABLE_EXERCISE_STATISTICS);
                queryBuilder.appendWhere(ExerciseStatisticsTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        String path = null;
        long id = 0;
        switch (uriType){
            case EXERCISES:
            case STATISTICS:
                break;
            case EXERCISE_ID:
                id = db.insert(ExerciseSessionTable.TABLE_EXERCISE_SESSION, null, contentValues);
                path = EXERCISE_SESSIONS_PATH;
                break;
            case STATISTICS_ID:
                id = db.insert(ExerciseStatisticsTable.TABLE_EXERCISE_STATISTICS, null, contentValues);
                path = EXERCISE_STATISTICS_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Delete is not supported.");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        String path = null;
        int rowsUpdated = 0;
        switch (uriType){
            case EXERCISES:
            case EXERCISE_ID:
                break;

            case STATISTICS:
            case STATISTICS_ID:
                rowsUpdated = db.update(ExerciseStatisticsTable.TABLE_EXERCISE_STATISTICS, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
