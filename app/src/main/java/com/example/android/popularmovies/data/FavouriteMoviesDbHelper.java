package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by goyo on 14/3/17.
 */

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper {


    static final String TAG = FavouriteMoviesDbHelper.class.getSimpleName();

    static final String DATABASE_NAME = "favouritemovies.db";

    static final int DATABASE_VERSION = 1;

    public FavouriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " +
                FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME + " (" +
                FavouriteMoviesContract.FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_ID + " INTEGER NOT NULL," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_USER_RATING + " REAL NOT NULL," +
                FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_RELEASE_DATE + " TIMESTAMP);";

        Log.d(TAG, SQL_CREATE_WAITLIST_TABLE);
        db.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME + ";");
        onCreate(db);
    }
}
