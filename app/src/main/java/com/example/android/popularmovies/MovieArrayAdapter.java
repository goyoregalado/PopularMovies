package com.example.android.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.TheMovieDBUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by goyo on 2/2/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    public MovieArrayAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_grid_item, parent, false);
        }
        ImageView poster = (ImageView) convertView.findViewById(R.id.iv_poster);

        URL pictureUrl = TheMovieDBUtils.buildPictureURL(getItem(position).posterURL);

        Picasso.with(getContext())
                .load(pictureUrl.toString()).into(poster);

        return convertView;
    }

}
