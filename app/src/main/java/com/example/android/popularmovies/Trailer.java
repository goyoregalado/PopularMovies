package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goyo on 11/3/17.
 */

public class Trailer implements Parcelable {

    String id;
    String title;
    String trailerUrl;

    public Trailer(String id, String title, String trailerUrl) {
        this.id = id;
        this.title = title;
        this.trailerUrl = trailerUrl;
    }

    private Trailer(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.trailerUrl = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.trailerUrl);
    }

    public final static Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){

        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
