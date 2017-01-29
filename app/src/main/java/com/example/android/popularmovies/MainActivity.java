package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDBUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView upperLeft;
    private TextView upperRight;
    private TextView lowerLeft;
    private TextView lowerRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upperLeft = (TextView) findViewById(R.id.tv_upperLeft);
        upperRight = (TextView) findViewById(R.id.tv_upperRight);
        lowerLeft = (TextView) findViewById(R.id.tv_lowerLeft);
        lowerRight = (TextView) findViewById(R.id.tv_lowerRight);
        loadMoviePosters();
    }

    public void loadMoviePosters(){
        new FetchMovies().execute("popular");
    }


    class FetchMovies extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = TheMovieDBUtils.buildURL(params[0]);
            try {
                Log.d(TAG, "Looking for a response");
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Log.d(TAG, "That's is: " + response);
                return response;
            }
            catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                upperLeft.setText(s);
            }
        }
    }
}
