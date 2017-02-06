package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDBUtils;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mOriginalTitle;
    private ImageView mMoviePoster;
    private TextView mMovieReleaseDate;
    private TextView mMovieUserRating;
    private TextView mSynopsis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        Intent intent = getIntent();

        if (intent.getExtras().getParcelable("movie") != null) {
            Movie movie = intent.getExtras().getParcelable("movie");
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
        }
    }

}
