package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by goyo on 1/2/17.
 * Utility class to work with the JSON format of The Movie DB API.
 */

public class TheMovieDBJsonUtils  {
    final static String TMDB_RESULTS = "results";

    final static String TMDB_POSTER = "poster_path";

    final static String TMDB_ADULT = "adult";

    final static String TMDB_OVERVIEW = "overview";

    final static String TMDB_RELEASE_DATE = "release_date";

    final static String TMDB_GENRE_IDS = "genre_ids";

    final static String TMDB_ID = "id";

    final static String TMDB_ORIGINAL_TITLE = "original_title";

    final static String TMDB_ORIGINAL_LANGUAGE = "original_language";

    final static String TMDB_TITLE = "title";

    final static String TMDB_BACKDROP = "backdrop_path";

    final static String TMDB_POPULARITY = "popularity";

    final static String TMDB_VOTES = "vote_count";

    final static String TMDB_VIDEO = "video";

    final static String TMDB_VOTE_AVERAGE = "vote_average";


    /**
     * This class obtains a list of movie poster paths from the raw response retrieved from the API.
     * @param moviesJsonStr A String with the raw response from the API.
     * @return An String array in which positions is stored just one movie poster path.
     * @throws JSONException
     */
    public static String[] getMovieStringsFromJSON(String moviesJsonStr) throws JSONException {

        String[] mParsedPosterPaths;

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

    /**
     * Returns a array of parcelable Movie objects
     * @param moviesJSONStr A String with the raw response from the API.
     * @return An array of parcelable Movie objects.
     * @throws JSONException
     */

    public static Movie[] getMovieArrayFromJSON(String moviesJSONStr) throws JSONException {

        Movie[] moviesParsedArray;

        JSONObject moviesJSON = new JSONObject(moviesJSONStr);

        if (!moviesJSON.has(TMDB_RESULTS)) {
            return null;
        }

        JSONArray moviesArray = moviesJSON.getJSONArray(TMDB_RESULTS);

        moviesParsedArray = new Movie[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);

            String title = movie.getString(TMDB_ORIGINAL_TITLE);
            String posterURL = TheMovieDBUtils.buildPictureURL(movie.getString(TMDB_POSTER)).toString();
            String synopsis = movie.getString(TMDB_OVERVIEW);
            double rating = movie.getDouble(TMDB_VOTE_AVERAGE);
            String releaseDate = movie.getString(TMDB_RELEASE_DATE);
            
            moviesParsedArray[i] = new Movie(title, posterURL, synopsis, rating, releaseDate);
        }
        return moviesParsedArray;
    }
}
