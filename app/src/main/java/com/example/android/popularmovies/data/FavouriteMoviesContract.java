package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by goyo on 14/3/17.
 */

public class FavouriteMoviesContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAV_MOVIES = "favouritemovies";

    public static final class FavouriteMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAV_MOVIES).build();

        private FavouriteMoviesEntry() {};

        public static final String TABLE_NAME = "favouritemovies";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER_URL = "posterURL";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_USER_RATING = "userRating";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

    }
}
