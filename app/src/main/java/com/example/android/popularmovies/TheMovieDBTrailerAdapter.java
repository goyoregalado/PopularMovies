package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by goyo on 11/3/17.
 */

public class TheMovieDBTrailerAdapter
        extends RecyclerView.Adapter<TheMovieDBTrailerAdapter.TheMovieDBTrailerAdapterViewHolder>{

    interface TheMovieDBTrailerAdapterOnClickHandler {
        void onTrailerClick(Trailer trailer);
    }

    private static final String TAG = TheMovieDBTrailerAdapter.class.getSimpleName();

    final private TheMovieDBTrailerAdapterOnClickHandler mClickHandler;
    private Trailer[] mTrailer;


    public TheMovieDBTrailerAdapter(TheMovieDBTrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    @Override
    public TheMovieDBTrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_item, parent, false);

        TheMovieDBTrailerAdapterViewHolder viewHolder = new TheMovieDBTrailerAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TheMovieDBTrailerAdapterViewHolder holder, int position) {
        String trailerTitle = mTrailer[position].title;
        holder.mTrailerTextView.setText(trailerTitle);
        Log.d(TAG, "Loading trailer title: " + mTrailer[position].title);
    }

    @Override
    public int getItemCount() {
        if (mTrailer == null) {
            return 0;
        }
        return mTrailer.length;
    }

    public void setTrailerData(Trailer[] trailers) {
        mTrailer = trailers;
        notifyDataSetChanged();
    }

    public Trailer[] getTrailerData() {
        return mTrailer;
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
