package com.example.hike;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "hike_manager.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_HIKES = "hikes";
    public static final String COL_HIKE_ID = "id";

    public static final String TABLE_OBS = "observations";
    public static final String COL_OBS_ID = "id";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHikes = "CREATE TABLE " + TABLE_HIKES + " ("
                + COL_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "location TEXT,"
                + "date TEXT,"
                + "parking TEXT,"
                + "difficulty TEXT,"
                + "description TEXT"
                + ");";

        String createObs = "CREATE TABLE " + TABLE_OBS + " ("
                + COL_OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "hike_id INTEGER NOT NULL,"
                + "description TEXT,"
                + "time TEXT,"
                + "FOREIGN KEY(hike_id) REFERENCES " + TABLE_HIKES + "(" + COL_HIKE_ID + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(createHikes);
        db.execSQL(createObs);

        // Enable foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }
}

