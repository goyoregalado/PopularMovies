package com.example.android.popularmovies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by goyo on 1/2/17.
 */

public class TheMovieDBJsonUtils  {


    public static String[] getMovieStringsFromJSON(String moviesJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";

        final String TMDB_POSTER = "poster_path";

        final String TMDB_ADULT = "adult";

        final String TMDB_OVERVIEW = "overview";

        final String TMDB_RELEASE_DATE = "release_date";

        final String TMDB_GENRE_IDS = "genre_ids";

        final String TMDB_ID = "id";

        final String TMDB_ORIGINAL_TITLE = "original_title";

        final String TMDB_ORIGINAL_LANGUAGE = "original_language";

        final String TMDB_TITLE = "title";

        final String TMDB_BACKDROP = "backdrop_path";

        final String TMDB_POPULARITY = "popularity";

        final String TMDB_VOTES = "vote_count";

        final String TMDB_VIDEO = "video";

        final String TMDB_VOTE_AVERAGE = "vote_average";

        String[] mParsedPosterPaths= null;

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);

        if (!moviesJSON.has(TMDB_RESULTS)) {
            return null;
        }

        JSONArray moviesArray = moviesJSON.getJSONArray(TMDB_RESULTS);

        mParsedPosterPaths = new String[moviesArray.length()];

        for (int i = 0;i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            mParsedPosterPaths[i] = movie.getString(TMDB_POSTER);
        }
        return mParsedPosterPaths;
    }
}
