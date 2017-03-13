package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.Review;
import com.example.android.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

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

    // Constants for Trailers
    final static String TMDB_TRAILER_ID = "id";

    final static String TMDB_TRAILER_KEY = "key";

    final static String TMDB_TRAILER_NAME = "name";


    //Constants for Reviews
    final static String TMDB_REVIEW_ID = "id";
    final static String TMDB_REVIEW_AUTHOR = "author";
    final static String TMDB_REVIEW_CONTENT = "content";




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

            String id = movie.getString(TMDB_ID);
            String title = movie.getString(TMDB_ORIGINAL_TITLE);
            String posterURL = TheMovieDBUtils.buildPictureURL(movie.getString(TMDB_POSTER)).toString();
            String synopsis = movie.getString(TMDB_OVERVIEW);
            double rating = movie.getDouble(TMDB_VOTE_AVERAGE);
            String releaseDate = movie.getString(TMDB_RELEASE_DATE);
            
            moviesParsedArray[i] = new Movie(id, title, posterURL, synopsis, rating, releaseDate);
        }
        return moviesParsedArray;
    }


    public static Trailer[] getTrailerArrayFromJSON(String trailersJSONStr) throws JSONException {
        Trailer[] trailersParsedArray;

        JSONObject trailersJSON = new JSONObject(trailersJSONStr);

        if (!trailersJSON.has(TMDB_RESULTS)) {
            return null;
        }
        JSONArray trailersArray = trailersJSON.getJSONArray(TMDB_RESULTS);

        trailersParsedArray = new Trailer[trailersArray.length()];

        for (int i = 0; i < trailersArray.length(); i++) {
            JSONObject trailer = trailersArray.getJSONObject(i);
            String id = trailer.getString(TMDB_TRAILER_ID);
            String key = trailer.getString(TMDB_TRAILER_KEY);
            String title = trailer.getString(TMDB_TRAILER_NAME);
            URL youtubeUrl = TheMovieDBUtils.buildVideoURL(key);
            String url = youtubeUrl.toString();
            trailersParsedArray[i] = new Trailer(id, title, url);
        }
        return trailersParsedArray;
    }


    public static Review[] getReviewArrayFromJSON(String reviewJSONStr) throws JSONException {
        Review[] reviewsParsedArray;
        JSONObject reviewsJSON = new JSONObject(reviewJSONStr);

        if (!reviewsJSON.has(TMDB_RESULTS)) {
            return null;
        }

        JSONArray reviewsArray = reviewsJSON.getJSONArray(TMDB_RESULTS);

        reviewsParsedArray = new Review[reviewsArray.length()];

        for(int i = 0; i < reviewsArray.length(); i++) {
            JSONObject review = reviewsArray.getJSONObject(i);
            String id = review.getString(TMDB_REVIEW_ID);
            String author = review.getString(TMDB_REVIEW_AUTHOR);
            String content = review.getString(TMDB_REVIEW_CONTENT);

            reviewsParsedArray[i] = new Review(id, author, content);
        }

        return reviewsParsedArray;
    }
}
