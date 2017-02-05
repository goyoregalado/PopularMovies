package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.TheMovieDBUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by goyo on 31/1/17.
 */

public class TheMovieDBAdapter extends
        RecyclerView.Adapter<TheMovieDBAdapter.TheMovieDBAdapterViewHolder>{

    private Movie mMoviesData[];
    final private TheMovieDBAdapterOnClickHandler mClickHandler;

    interface TheMovieDBAdapterOnClickHandler {
        void onMovieClick(Movie movie);
    }

    public TheMovieDBAdapter(TheMovieDBAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TheMovieDBAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int LayoutIDPosterGridItem = R.layout.poster_grid_item;

        boolean shouldAttachToParentInmediatly = false;

        View view = inflater.inflate(LayoutIDPosterGridItem, parent, shouldAttachToParentInmediatly);

        TheMovieDBAdapterViewHolder viewHolder = new TheMovieDBAdapterViewHolder(view);

        return viewHolder;

    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        }
        else {
            return mMoviesData.length;
        }
    }

    @Override
    public void onBindViewHolder(TheMovieDBAdapterViewHolder holder, int position) {
        String pictureUrl = mMoviesData[position].posterURL;

        Picasso.with(holder.mPosterImageView.getContext())
                .load(pictureUrl).into(holder.mPosterImageView);
    }

    public void setMovieData(Movie[] movieData) {
        mMoviesData = null;
        mMoviesData = new Movie[movieData.length];
        mMoviesData = movieData;
        notifyDataSetChanged();
    }

    public Movie[] getMovieData() {
        return mMoviesData;
    }

    class TheMovieDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mPosterImageView;

        public TheMovieDBAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onMovieClick(mMoviesData[position]);
        }
    }
}
