package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goyo on 2/2/17.
 */

public class Movie implements Parcelable {
    String originalTitle;
    String posterURL;
    String plotSynopsis;
    double userRating;
    String releaseDate;

    public Movie(String title, String poster, String synopsis, double rating, String date) {
        this.originalTitle = title;
        this.posterURL = poster;
        this.plotSynopsis = synopsis;
        this.userRating = rating;
        this.releaseDate = date;
    }

    private Movie(Parcel in) {
        this.originalTitle = in.readString();
        this.posterURL = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalTitle);
        dest.writeString(this.posterURL);
        dest.writeString(this.plotSynopsis);
        dest.writeDouble(this.userRating);
        dest.writeString(this.releaseDate);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
