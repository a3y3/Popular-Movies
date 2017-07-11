package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Soham on 11-Jul-17.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "favouritemovies.db";
    private static int DATABASE_VERSION = 3;
    public MovieDBHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+MovieContract.MovieEntry.TABLE_NAME+"(" +
                MovieContract.MovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS+" TEXT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_URL+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
