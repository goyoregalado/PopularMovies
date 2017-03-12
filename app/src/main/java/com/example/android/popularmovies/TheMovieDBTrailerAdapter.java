package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by goyo on 11/3/17.
 */

public class TheMovieDBTrailerAdapter
        extends RecyclerView.Adapter<TheMovieDBTrailerAdapter.TheMovieDBTrailerAdapterViewHolder>{

    final private TheMovieDBTrailerAdapterOnClickHandler mClickHandler;
    private Trailer[] mTrailer;

    interface TheMovieDBTrailerAdapterOnClickHandler {
        void onTrailerClick(Trailer trailer);
    }

    public TheMovieDBTrailerAdapter(TheMovieDBTrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    @Override
    public TheMovieDBTrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TheMovieDBTrailerAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (mTrailer == null) {
            return 0;
        }
        return mTrailer.length;
    }

    public class TheMovieDBTrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView mTrailerTextView;

        public TheMovieDBTrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerTextView = (TextView) view.findViewById(R.id.tv_trailer_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onTrailerClick(mTrailer[position]);
        }
    }
}
