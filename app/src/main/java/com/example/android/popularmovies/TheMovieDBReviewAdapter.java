package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by goyo on 13/3/17.
 */

public class TheMovieDBReviewAdapter
        extends RecyclerView.Adapter<TheMovieDBReviewAdapter.TheMovieDBReviewAdapterViewHolder>{

    interface TheMovieDBReviewAdapterOnClickHandler {
        void onReviewClick(Review review);
    }

    private static final String TAG = TheMovieDBReviewAdapter.class.getSimpleName();
    final private TheMovieDBReviewAdapterOnClickHandler mClickHandler;
    private Review[] mReview;

    public TheMovieDBReviewAdapter(TheMovieDBReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    @Override
    public TheMovieDBReviewAdapter.TheMovieDBReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.review_item, parent, false);

        TheMovieDBReviewAdapterViewHolder viewHolder = new TheMovieDBReviewAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TheMovieDBReviewAdapter.TheMovieDBReviewAdapterViewHolder holder, int position) {
        String author = mReview[position].author;
        String content = mReview[position].content;
        Context context = holder.mReviewTextView.getContext();

        String text = context.getResources().getString(R.string.author) + ": " + author + "\n" + content;

        holder.mReviewTextView.setText(text);

        Log.d(TAG, "Loading review: " + text);
    }

    @Override
    public int getItemCount() {
        if (mReview == null) {
            return 0;
        }
        return mReview.length;
    }

    public void setReviewData(Review[] reviews) {
        mReview = reviews;
        notifyDataSetChanged();
    }

    public Review[] getReviewData() {
        return mReview;
    }

    public class TheMovieDBReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mReviewTextView;

        public TheMovieDBReviewAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_review);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onReviewClick(mReview[position]);
        }
    }
}
