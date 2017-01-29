package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by goyo on 29/1/17.
 */

public class TheMovieDBUtils {

    private static final String TAG = TheMovieDBUtils.class.getSimpleName();

    private static final String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String API_KEY_QUERY_PARAM = "api_key";

    /**
     * This method will build the URL used to query TheMovieDB service.
     *
     * @String typeOfQuery  If contains "popular" then we should query the popular movies.
     *                      If contains "topRated" then we should query the top rated movies.
     * @return The URL to use to query the MovieDB server.
    */
    public static URL buildURL(String typeOfQuery) {
        Uri builtUri=null;

        switch (typeOfQuery) {
            case "popular":
                builtUri = Uri.parse(POPULAR_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY)
                        .build();
                break;

            case "topRated":
                builtUri = Uri.parse(TOP_RATED_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_QUERY_PARAM, CredentialUtils.APIKEY)
                        .build();
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL: " + url);
        return url;
    }

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
}
