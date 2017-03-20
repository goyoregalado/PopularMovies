package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavouriteMoviesContract;
import com.example.android.popularmovies.utilities.PopularMoviesSettings;
import com.example.android.popularmovies.utilities.TheMovieDBJsonUtils;
import com.example.android.popularmovies.utilities.TheMovieDBUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements TheMovieDBAdapter.TheMovieDBAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLUMNS = 2;

    private static final int ONLINE_MOVIE_LOADER = 7;
    private static final int OFFLINE_MOVIE_LOADER = 8;


    private RecyclerView mRecyclerView;
    private TheMovieDBAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private TextView mFavoriteErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_posters);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

        mFavoriteErrorMessageDisplay = (TextView) findViewById(R.id.tv_favorite_error_message);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);



        boolean shouldReverseLayout = false;

        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMNS, GridLayoutManager.VERTICAL, shouldReverseLayout);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mMovieAdapter = new TheMovieDBAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        // If we haven't nothing saved, then we must load the posters from the network.
        if (savedInstanceState == null || !savedInstanceState.containsKey("movieList")) {
            loadMoviePosters(PopularMoviesSettings.RATING_CRITERIA);
        }
        else {
            Log.i(TAG, "Restoring movie data");
            Movie movie = savedInstanceState.getParcelable("movieList");
            Log.d(TAG, movie.originalTitle);
            mMovieAdapter.setMovieData((Movie[]) savedInstanceState.getParcelableArray("movieList"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving movie data.");
        outState.putParcelableArray("movieList", mMovieAdapter.getMovieData());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "I'm restoring the data.");
        mMovieAdapter.setMovieData((Movie[]) savedInstanceState.getParcelableArray("movieList"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();

        switch (itemSelected) {
            case R.id.action_most_popular:
                loadMoviePosters(PopularMoviesSettings.POPULAR_CRITERIA);
                return true;
            case R.id.action_top_rated:
                loadMoviePosters(PopularMoviesSettings.RATING_CRITERIA);
                return true;
            case R.id.action_favorites:
                loadFavoriteMovies();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method launchs and AsyncTask to retrieve the movie data from
     * the network
     * @param sorting_criteria: It should be one of two strings stored at PopularMoviesSettings.
     *                        POPULAR_CRITERIA or RATING_CRITERIA.
     */
    public void loadMoviePosters(String sorting_criteria){
        // TODO: Implement an Async Task Loader.
        new FetchMovies().execute(sorting_criteria);
    }

    /**
     * This method uses the content provider to obtain the favorite movies from an Sqlite database.
     */
    public void loadFavoriteMovies(){
        // TODO: Implement a Cursor Async Loader.
        Uri uri = FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        Log.d(TAG, "Retrieved num records: " + cursor.getCount());
        Movie[] movies = TheMovieDBUtils.loadMovieArrayFromCursor(cursor);
        if (movies != null) {
            if (movies.length == 0) {
                showFavoriteErrorMessage();
            }
            else {
                showMoviePosters();
                mMovieAdapter.setMovieData(movies);
            }
        }
        else {
            showErrorMessage();
        }
    }

    /**
     * Hides any error message view and shows the poster's grid.
     */
    void showMoviePosters() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mFavoriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the poster's grid and shows error messages views.
     */
    void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mFavoriteErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the poster's grid and conventional error messages views and shows
     * an error message related to the inexistence of favorite movies.
     */
    void showFavoriteErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mFavoriteErrorMessageDisplay.setVisibility(View.VISIBLE);

    }




    /**
     * Launchs MovieDetailActivity Intent with the data related to a single movie
     * @param movie The data related to the movie that the user selected.
     */
    @Override
    public void onMovieClick(Movie movie) {
        Context context = this;
        Class detailActivityClass = MovieDetailActivity.class;
        Intent intent = new Intent(context, detailActivityClass);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    class FetchMovies extends AsyncTask<String, Void, Movie[]> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            URL url = TheMovieDBUtils.buildMovieURL(params[0]);
            try {
                Log.i(TAG, "Retrieving movie data from the network");
                Log.i(TAG, url.toString());
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Movie[] parsedResponse = TheMovieDBJsonUtils.getMovieArrayFromJSON(response);
                return parsedResponse;
            }
            catch (IOException e){
                e.printStackTrace();
                Log.e(TAG, "IOException in AsyncTask");
                return null;
            }
            catch (JSONException e2) {
                e2.printStackTrace();
                Log.e(TAG, "JSONException in AsyncTask");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                Log.i(TAG, "Showing movie posters");
                showMoviePosters();
                mMovieAdapter.setMovieData(movies);
            }
            else {
                showErrorMessage();
            }
        }
    }
}
