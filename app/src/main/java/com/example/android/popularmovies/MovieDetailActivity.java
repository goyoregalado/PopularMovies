package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDBJsonUtils;
import com.example.android.popularmovies.utilities.TheMovieDBUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity
        implements TheMovieDBTrailerAdapter.TheMovieDBTrailerAdapterOnClickHandler,
        TheMovieDBReviewAdapter.TheMovieDBReviewAdapterOnClickHandler {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private String mMovieId;
    private TextView mOriginalTitle;
    private ImageView mMoviePoster;
    private TextView mMovieReleaseDate;
    private TextView mMovieUserRating;
    private TextView mSynopsis;

    private RecyclerView mTrailerRecyclerView;
    private TheMovieDBTrailerAdapter mTrailerAdapter;
    private TextView mTrailerError;
    private ProgressBar mTrailerProgressBar;

    private RecyclerView mReviewRecyclerView;
    private TheMovieDBReviewAdapter mReviewAdapter;
    private TextView mReviewError;
    private ProgressBar mReviewProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailerRecyclerView.setLayoutManager(trailersLayoutManager);

        mTrailerRecyclerView.setHasFixedSize(true);

        mTrailerError = (TextView) findViewById(R.id.tv_trailer_error_message);
        mTrailerProgressBar = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);

        mTrailerAdapter = new TheMovieDBTrailerAdapter(this);

        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        mReviewError = (TextView) findViewById(R.id.tv_review_error_message);
        mReviewProgressBar = (ProgressBar) findViewById(R.id.pb_review_loading_indicator);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mReviewRecyclerView.setLayoutManager(reviewsLayoutManager);

        mReviewRecyclerView.setHasFixedSize(true);

        mReviewAdapter = new TheMovieDBReviewAdapter(this);

        mReviewRecyclerView.setAdapter(mReviewAdapter);



        Intent intent = getIntent();

        if (intent.getExtras().getParcelable("movie") != null) {
            Movie movie = intent.getExtras().getParcelable("movie");

            mMovieId = movie.id;

            mOriginalTitle.setText(movie.originalTitle);

            Date date = TheMovieDBUtils.parseDate(movie.releaseDate);

            Calendar c = Calendar.getInstance();

            c.setTime(date);

            String dateStr = getString(R.string.release_date) + " " + String.valueOf(c.get(Calendar.YEAR));

            String ratingStr = getString(R.string.user_rating) + " " + String.valueOf(movie.userRating) + "/10";

            mMovieReleaseDate.setText(dateStr);
            mMovieUserRating.setText(ratingStr);
            mSynopsis.setText(movie.plotSynopsis);
            Picasso.with(this).load(movie.posterURL).into(mMoviePoster);
            if (savedInstanceState == null || !savedInstanceState.containsKey("trailerList") || !savedInstanceState.containsKey("reviewList")) {
                Log.i(TAG, "Obtaining trailers and reviews from TMDB");
                new FetchTrailers().execute(mMovieId);
                new FetchReviews().execute(mMovieId);
            }
            else {
                Log.i(TAG, "Restoring trailer and review data");
                mTrailerAdapter.setTrailerData((Trailer[]) savedInstanceState.getParcelableArray("trailerList"));
                mReviewAdapter.setReviewData((Review []) savedInstanceState.getParcelableArray("reviewList"));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving trailerData and reviewData");
        outState.putParcelableArray("trailerList", mTrailerAdapter.getTrailerData());
        outState.putParcelableArray("reviewList", mReviewAdapter.getReviewData());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "I'm restoring trailer and review data");
        mTrailerAdapter.setTrailerData((Trailer[]) savedInstanceState.getParcelableArray("trailerList"));
        mReviewAdapter.setReviewData((Review[]) savedInstanceState.getParcelableArray("reviewList"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showTrailers() {
        mTrailerError.setVisibility(View.INVISIBLE);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showTrailersErrorMessage() {
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        mTrailerError.setVisibility(View.VISIBLE);
    }

    private void showReviews() {
        mReviewError.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showReviewsErrorMessage() {
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
        mReviewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        //TODO: Launch Youtube Intent.
        Log.d(TAG, "Trailer clicked: " + trailer.title);
    }

    @Override
    public void onReviewClick(Review review) {
        Log.d(TAG, "Review clicked: " + review.author);

    }


    class FetchTrailers extends AsyncTask<String, Void, Trailer[]> {

        @Override
        protected void onPreExecute() {
            mTrailerProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Trailer[] doInBackground(String... params) {
            URL url = TheMovieDBUtils.buildTrailerURL(params[0]);
            try {
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Trailer[] parsedResponse = TheMovieDBJsonUtils.getTrailerArrayFromJSON(response);
                return parsedResponse;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException in FetchTrailer");
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON exception in FetchTrailer");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Trailer[] trailers) {
            mTrailerProgressBar.setVisibility(View.INVISIBLE);
            if (trailers != null) {
                Log.i(TAG, "Showing trailers");
                showTrailers();
                mTrailerAdapter.setTrailerData(trailers);
            }
            else {
                showTrailersErrorMessage();
            }
        }
    }

    class FetchReviews extends AsyncTask<String, Void, Review[]> {


        @Override
        protected void onPreExecute() {
            mReviewProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Review[] doInBackground(String... params) {
            URL url = TheMovieDBUtils.buildReviewsURL(params[0]);
            try {
                String response = TheMovieDBUtils.getResponseFromHttpsUrl(url);
                Review[] parsedResponse = TheMovieDBJsonUtils.getReviewArrayFromJSON(response);
                return parsedResponse;

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException in FetchReviews");
                return null;

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSONException in FetchReviews");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            mReviewProgressBar.setVisibility(View.INVISIBLE);
            if (reviews != null) {
                Log.i(TAG, "Showing reviews");
                showReviews();
                mReviewAdapter.setReviewData(reviews);
            }
            else {
                showReviewsErrorMessage();
            }
        }
    }
}
