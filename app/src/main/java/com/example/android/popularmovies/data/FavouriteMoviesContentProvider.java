package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.R;

/**
 * Created by goyo on 15/3/17.
 */

public class FavouriteMoviesContentProvider extends ContentProvider {

    public static final int FAV_MOVIES = 100;
    public static final int FAV_MOVIES_WITH_ID = 101;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavouriteMoviesDbHelper mFavMoviesHelper;


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAV_MOVIES, FAV_MOVIES);
        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAV_MOVIES + "/#",
                FAV_MOVIES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavMoviesHelper = new FavouriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mFavMoviesHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor = null;

        switch (match) {
            case FAV_MOVIES:
                retCursor = db.query(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAV_MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                retCursor = db.query(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        null
                        );
                break;

            default:
                String error_msg = getContext().getString(R.string.unsupported_operation_error_msg);
                throw new UnsupportedOperationException(error_msg + " " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAV_MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + FavouriteMoviesContract.AUTHORITY + "/"
                        + FavouriteMoviesContract.PATH_FAV_MOVIES;
            case FAV_MOVIES_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + FavouriteMoviesContract.AUTHORITY + "/"
                        + FavouriteMoviesContract.PATH_FAV_MOVIES;
            default:
                String error_msg = getContext().getString(R.string.unknown_uri_error_msg);
                throw new UnsupportedOperationException(error_msg + ": " +uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavMoviesHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case FAV_MOVIES:
                long id = db.insert(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, id);
                }
                else {
                    String error_msg = getContext().getString(R.string.insert_failed_error_msg);
                    throw new android.database.sqlite.SQLiteException(error_msg + ": " + uri);
                }

                break;
            default:
                String error_msg = getContext().getString(R.string.unsupported_operation_error_msg);
                throw new UnsupportedOperationException(error_msg + ": " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavMoviesHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int moviesDeleted = 0;

        switch(match) {
            case FAV_MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                moviesDeleted = db.delete(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        "id=?",
                        new String[]{id});

                break;
            default:
                String error_msg = getContext().getString(R.string.unsupported_operation_error_msg);
                throw new UnsupportedOperationException(error_msg + ": " + uri);
        }

        if (moviesDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
