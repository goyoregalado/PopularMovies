package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by goyo on 29/1/17.
 * This class is used to retrieve movie data from The Movie DB API.
 */

public class TheMovieDBUtils {

    private static final String TAG = TheMovieDBUtils.class.getSimpleName();

    private static final String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_DETAIL_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String TRAILER_PATH = "videos";
    private static final String REVIEW_PATH = "reviews";
    private static final String PICTURE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String API_KEY_QUERY_PARAM = "api_key";
    private static final String PICTURE_DEFAULT_SIZE = "w185";

    // Youtube base URL to play the trailers.
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String YOUTUBE_TRAILER_KEY_PARAM = "v";

    /**
     * This method will build the URL used to query TheMovieDB service.
     *
     * @String typeOfQuery It can be one of the values stored at PopularMoviesSettings class.
     * @return The URL to use to query the MovieDB server.
    */
    public static URL buildMovieURL(String typeOfQuery) {
        Uri builtUri=null;

        switch (typeOfQuery) {
            case PopularMoviesSettings.POPULAR_CRITERIA:
                builtUri = Uri.parse(POPULAR_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY)
                        .build();
                Log.i(TAG, "Fetching popular movies");
                break;

            case PopularMoviesSettings.RATING_CRITERIA:
                builtUri = Uri.parse(TOP_RATED_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY)
                        .build();
                Log.i(TAG, "Fetching top rated movies");
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
            Log.e(TAG, "Malformed URL");
        }

        Log.i(TAG, "Built URL: " + url);
        return url;
    }


    /**
     * This method will build a full trailer URL for the movie represented by movieId.
     * @param movieId The identification of a Movie at TMDB API.
     * @return A full trailer URL.
     */
    public static URL buildTrailerURL(String movieId){
        Uri builtUri = null;

        builtUri = Uri.parse(MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(TRAILER_PATH)
                .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY).build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Malformed URL");
        }

        Log.i(TAG, "Trailer URL: " + url);
        return url;
    }



    public static URL buildReviewsURL(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DETAIL_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Malformed URL");
        }

        Log.i(TAG, "Reviews URL: " + url);
        return url;
    }

    /**
     * This method will build a full picture URL from the path retrieved from the API.
     * @param picturePath The poster path obtained from the API.
     * @return A full picture URL.
     */
    public static URL buildPictureURL(String picturePath) {
        Uri builtUri = Uri.parse(PICTURE_BASE_URL).buildUpon()
                .appendEncodedPath(PICTURE_DEFAULT_SIZE)
                .appendEncodedPath(picturePath)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built image URL: " + url);
        return url;
    }


    /**
     * This method builds a valid YOUTUBE URL which allows to reproduce a single trailer.
     * This kind of URL are in this format: https://www.youtube.com/watch?v=<KEY>
     * @param key The youtube key for the trailer that we want to reproduce.
     * @return A youtube valid URL for the trailer.
     */
    public static URL buildVideoURL(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_TRAILER_KEY_PARAM, key).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built youtube URL: " + url);
        return url;

    }

    /**
     * Obtains the data from a Https url.
     * @param url The url from we are going to get the data.
     * @return An String with the response obtained from the server.
     * @throws IOException
     */
    public static String getResponseFromHttpsUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            }
            else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This just converts an String in the format provided by The Movie DB API into a Date object.
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
