package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavouriteMoviesContract;
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

    //private String mMovieId;
    private Movie mMovie;


    private TextView mOriginalTitle;
    private ImageView mMoviePoster;
    private TextView mMovieReleaseDate;
    private TextView mMovieUserRating;
    private TextView mSynopsis;
    private TextView mFavorite;

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

        Log.d(TAG, "On create");

        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        mFavorite = (TextView) findViewById(R.id.tv_favorite);

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

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            Log.d(TAG, "There's a movie in the bundle");
            mMovie = (Movie) savedInstanceState.getParcelable("movie");
        }
        else if (intent.getExtras().getParcelable("movie") != null) {
            Log.d(TAG, "There isn't a movie in the bundle obtaining it from intent");
            mMovie = (Movie) intent.getExtras().getParcelable("movie");
        }

        if (mMovie != null) {
            Log.d(TAG, "There's a movie: " + mMovie);

            mOriginalTitle.setText(mMovie.originalTitle);

            showFavorite(mMovie.favorite);

            Date date = TheMovieDBUtils.parseDate(mMovie.releaseDate);

            Calendar c = Calendar.getInstance();

            c.setTime(date);

            String dateStr = getString(R.string.release_date) + " " + String.valueOf(c.get(Calendar.YEAR));

            String ratingStr = getString(R.string.user_rating) + " " + String.valueOf(mMovie.userRating) + "/10";

            mMovieReleaseDate.setText(dateStr);
            mMovieUserRating.setText(ratingStr);
            mSynopsis.setText(mMovie.plotSynopsis);

            Picasso.with(this).load(mMovie.posterURL).into(mMoviePoster);

            if (savedInstanceState != null && savedInstanceState.containsKey("trailerList") && !savedInstanceState.containsKey("reviewList")) {
                Log.i(TAG, "Restoring trailer and review data");
                mTrailerAdapter.setTrailerData((Trailer[]) savedInstanceState.getParcelableArray("trailerList"));
                mReviewAdapter.setReviewData((Review []) savedInstanceState.getParcelableArray("reviewList"));
            }
            else {
                Log.i(TAG, "Obtaining trailers and reviews from TMDB");
                new FetchTrailers().execute(mMovie.id);
                new FetchReviews().execute(mMovie.id);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Saving trailerData and reviewData");
        outState.putParcelable("movie", mMovie);
        outState.putParcelableArray("trailerList", mTrailerAdapter.getTrailerData());
        outState.putParcelableArray("reviewList", mReviewAdapter.getReviewData());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "I'm restoring trailer and review data");
        mMovie = (Movie) savedInstanceState.getParcelable("movie");
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
        Log.d(TAG, "Trailer clicked: " + trailer.title);
        Uri uri = Uri.parse(trailer.trailerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onReviewClick(Review review) {
        Log.d(TAG, "Review clicked: " + review.author);

    }

    public void showFavorite(boolean favorite) {

        if (favorite == true) {
            mFavorite.setText(getString(R.string.delete_from_favorites));
            mFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow_star, 0, 0, 0);
        }
        else {
            mFavorite.setText(getString(R.string.mark_as_favorite));
            mFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.black_star, 0, 0, 0);
        }
    }

    public void onClickAddFavouriteMovie(View view) {
        if (mMovie.favorite) {
            // TODO: The Uri is not built properly.
            Uri uri = FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(mMovie.id).build();
            Log.d(TAG, "El Id de delete: " + mMovie.id);

            Log.d(TAG, "Delete URI: " + uri);

            int moviesDeleted = getContentResolver().delete(uri, null, null);
            if (moviesDeleted > 0) {
                showFavorite(false);
                mMovie.favorite = false;
                Toast.makeText(view.getContext(), getString(R.string.favorite_deleted), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(view.getContext(), getString(R.string.favorite_delete_error), Toast.LENGTH_LONG).show();
            }
        }
        else {

            // It isn't a favorite so we must save it to the database.
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_ID, mMovie.id);
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TITLE, mMovie.originalTitle);
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_POSTER_URL, mMovie.posterURL);
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_SYNOPSIS, mMovie.plotSynopsis);
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_USER_RATING, mMovie.userRating);
            contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_RELEASE_DATE, mMovie.releaseDate);

            Uri uri = getContentResolver().insert(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                showFavorite(true);
                mMovie.favorite = true;
                Toast.makeText(view.getContext(), getString(R.string.added_new_favorite), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(view.getContext(), getString(R.string.favorite_insertion_error), Toast.LENGTH_LONG).show();
            }
        }

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
