package com.labyrinth.team01.labyrinth.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Андрей on 19.04.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{
    private static final String DATABASE_NAME = "labirinth.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REPLAYS = "replays";
    public static final String COLUMN_REPLAYS_SEED = "seed";
    public static final String COLUMN_REPLAYS_WIDTH = "width";
    public static final String COLUMN_REPLAYS_HEIGHT = "height";
    public static final String COLUMN_REPLAYS_PATH = "path";
    public static final String COLUMN_REPLAYS_LENGTH = "length";
    public static final String COLUMN_REPLAYS_DATE = "date";

    private static final String DATABASE_CREATE_SCRIPT = "create table " + TABLE_REPLAYS + " (" +
            BaseColumns._ID + " integer primary key autoincrement, " +
            COLUMN_REPLAYS_SEED + " integer not null, " +
            COLUMN_REPLAYS_HEIGHT + " integer not null, " +
            COLUMN_REPLAYS_WIDTH + " integer not null, " +
            COLUMN_REPLAYS_DATE + " text not null, " +
            COLUMN_REPLAYS_LENGTH + " integer not null, " +
            COLUMN_REPLAYS_PATH + " text not null);";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
