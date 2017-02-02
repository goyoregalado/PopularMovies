package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDBJsonUtils;
import com.example.android.popularmovies.utilities.TheMovieDBUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLUMNS = 3;

    private RecyclerView mRecyclerView;
    private TheMovieDBAdapter mMovieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_posters);

        boolean shouldReverseLayout = false;

        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMNS, GridLayoutManager.VERTICAL, shouldReverseLayout);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mMovieAdapter = new TheMovieDBAdapter();

        mRecyclerView.setAdapter(mMovieAdapter);


        loadMoviePosters();
    }

    public void loadMoviePosters(){
        new FetchMovies().execute("popular");
    }


    class FetchMovies extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            URL url = TheMovieDBUtils.buildMovieURL(params[0]);
            try {
                Log.d(TAG, "Looking for a response");
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Log.d(TAG, "That's is: " + response);
                String[] parsedResponse = TheMovieDBJsonUtils.getMovieStringsFromJSON(response);
                Log.v(TAG, "Number of loaded movies: " + parsedResponse.length);
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
        protected void onPostExecute(String[] s) {
            if (s != null) {
                mMovieAdapter.setMovieData(s);
            }
        }
    }
}
