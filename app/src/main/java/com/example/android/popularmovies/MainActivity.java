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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
        implements TheMovieDBAdapter.TheMovieDBAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLUMNS = 2;

    private static final String MOVIE_LIST_KEY = "movieList";
    private static final String SORTING_CRITERIA_KEY = "sortingCriteria";

    private static final int OFFLINE_MOVIE_LOADER = 8;


    private RecyclerView mRecyclerView;
    private TheMovieDBAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private TextView mFavoriteErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;


    private String mSortingCriteria;

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
        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
            mSortingCriteria = PopularMoviesSettings.RATING_CRITERIA;
            loadMoviePosters(mSortingCriteria);
        }
        else {
            Log.i(TAG, "Restoring movie data");
            mSortingCriteria = savedInstanceState.getString(SORTING_CRITERIA_KEY);
            if (mSortingCriteria == PopularMoviesSettings.FAVORITE_CRITERIA) {
                loadFavoriteMoviesAsyncLoader();
            }
            else {
                mMovieAdapter.setMovieData((Movie[]) savedInstanceState.getParcelableArray(MOVIE_LIST_KEY));
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving movie data.");
        outState.putParcelableArray(MOVIE_LIST_KEY, mMovieAdapter.getMovieData());
        outState.putString(SORTING_CRITERIA_KEY, mSortingCriteria);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "I'm restoring the data.");
        mMovieAdapter.setMovieData((Movie[]) savedInstanceState.getParcelableArray(MOVIE_LIST_KEY));
        mSortingCriteria = savedInstanceState.getString(SORTING_CRITERIA_KEY);
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
                mSortingCriteria = PopularMoviesSettings.POPULAR_CRITERIA;
                loadMoviePosters(mSortingCriteria);
                return true;
            case R.id.action_top_rated:
                mSortingCriteria = PopularMoviesSettings.RATING_CRITERIA;
                loadMoviePosters(mSortingCriteria);
                return true;
            case R.id.action_favorites:
                mSortingCriteria = PopularMoviesSettings.FAVORITE_CRITERIA;
                loadFavoriteMoviesAsyncLoader();
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
        new FetchMovies().execute(sorting_criteria);
    }


    /**
     * This method uses a Task Loader to retrieve the movies from the content provider.
     */
    public void loadFavoriteMoviesAsyncLoader() {
       getSupportLoaderManager().initLoader(OFFLINE_MOVIE_LOADER, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mFavouriteData = null;


            @Override
            protected void onStartLoading() {
                if (mFavouriteData != null) {
                    deliverResult(mFavouriteData);
                }
                else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(
                            FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load favorite movies data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mFavouriteData = data;
                super.deliverResult(data);
            }
        };
    }


    /**
     * When the task loader finishes, we convert the data contained in the cursor to an Array of
     * Movie objects so we can reuse the same layout of the Popular or Rated movie list.
     * @param loader The task Loader
     * @param data A cursor with the movies data retrieved from the ContentProvider.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Movie[] favoriteMovies = TheMovieDBUtils.loadMovieArrayFromCursor(data);
        if (favoriteMovies != null) {
            if (favoriteMovies.length == 0) {
                showFavoriteErrorMessage();
            }
            else {
                showMoviePosters();
                mMovieAdapter.setMovieData(favoriteMovies);
            }
        }
        else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
