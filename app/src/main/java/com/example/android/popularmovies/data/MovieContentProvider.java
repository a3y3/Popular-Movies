package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Soham on 11-Jul-17.
 */

public class MovieContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    private MovieDBHelper mMovieDBHelper;
    private UriMatcher mUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH+"/#",MOVIES_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDBHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getWritableDatabase();
        Uri returnUri;
        int match = mUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                long id = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id < 0) {
                    throw new SQLiteException("Failed to insert into " + uri);
                } else {
                    returnUri = ContentUris.withAppendedId(MovieContract.BASE_CONTENT_URI, id);
                    return returnUri;
                }
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getReadableDatabase();
        switch (match){
            case MOVIES:
                return sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mMovieDBHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match){
            case MOVIES:
                return sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME,
                        s,
                        strings);
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
