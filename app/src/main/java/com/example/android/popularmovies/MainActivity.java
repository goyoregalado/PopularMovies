package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.PersistableBundle;
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

import com.example.android.popularmovies.utilities.TheMovieDBJsonUtils;
import com.example.android.popularmovies.utilities.TheMovieDBUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements TheMovieDBAdapter.TheMovieDBAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLUMNS = 2;
    private static final String POPULAR_CRITERIA = "popular";
    private static final String RATING_CRITERIA = "topRated";


    private String defaultCriteria = POPULAR_CRITERIA;

    private RecyclerView mRecyclerView;
    private TheMovieDBAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_posters);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);



        boolean shouldReverseLayout = false;

        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMNS, GridLayoutManager.VERTICAL, shouldReverseLayout);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mMovieAdapter = new TheMovieDBAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movieList")) {
            Log.v(TAG, "There isn't any movie data so, look for that in Internet");
            loadMoviePosters(defaultCriteria);
            Log.v(TAG, "We should be looking at a lot of posters");

        }
        else {
            Log.v(TAG, "Restoring movie data");
            mMovieAdapter.setMovieData((Movie[]) savedInstanceState.getParcelableArray("movieList"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "I'm saving the data.");
        outState.putParcelableArray("movieList", mMovieAdapter.getMovieData());
        super.onSaveInstanceState(outState);
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
                loadMoviePosters(POPULAR_CRITERIA);
                return true;
            case R.id.action_top_rated:
                loadMoviePosters(RATING_CRITERIA);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMoviePosters(String sorting_criteria){

        new FetchMovies().execute(sorting_criteria);
    }

    void showMoviePosters() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieClick(Movie movie) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, movie.originalTitle, Toast.LENGTH_LONG);
        mToast.show();

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
                Log.d(TAG, "Looking for a response");
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Log.d(TAG, "That's is: " + response);
                //String[] parsedResponse = TheMovieDBJsonUtils.getMovieStringsFromJSON(response);
                //Log.v(TAG, "Number of loaded movies: " + parsedResponse.length);
                Movie[] parsedResponse = TheMovieDBJsonUtils.getMovieArrayFromJSON(response);
                return parsedResponse;
            }
            catch (IOException e){
                e.printStackTrace();
                return null;
            }
            catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMoviePosters();
                mMovieAdapter.setMovieData(movies);
            }
            else {
                showErrorMessage();
            }
        }
    }
}
