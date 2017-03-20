package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goyo on 2/2/17.
 * This class is a model of The Movie DB API records that we are taking into account for our app.
 * It implements Parcelable to make it easy to send this kind of objects between Intents.
 */

public class Movie implements Parcelable {
    String id;
    String originalTitle;
    String posterURL;
    String plotSynopsis;
    double userRating;
    String releaseDate;
    boolean favorite;

    public Movie(String id, String title, String poster, String synopsis, double rating,
                 String date, boolean favorite) {
        this.id = id;
        this.originalTitle = title;
        this.posterURL = poster;
        this.plotSynopsis = synopsis;
        this.userRating = rating;
        this.releaseDate = date;
        this.favorite = favorite;
    }

    private Movie(Parcel in) {
        this.id = in.readString();
        this.originalTitle = in.readString();
        this.posterURL = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
        int booleanValue = in.readInt();
        if (booleanValue != 0) {
            this.favorite = true;
        }
        else {
            this.favorite = false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.originalTitle);
        dest.writeString(this.posterURL);
        dest.writeString(this.plotSynopsis);
        dest.writeDouble(this.userRating);
        dest.writeString(this.releaseDate);
        if (this.favorite) {
            dest.writeInt(1);
        }
        else {
            dest.writeInt(0);
        }
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
