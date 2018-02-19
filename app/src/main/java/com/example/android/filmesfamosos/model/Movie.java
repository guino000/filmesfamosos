package com.example.android.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gui on 2/15/2018.
 */

public class Movie implements Parcelable{
    long mID;
    Title mTitle;
    String mPosterPath;
    String mOverview;
    String mReleaseDate;
    Votes mVotes;

    public Movie(long id, Title title, String posterPath, String overview, Votes votes, String releaseDate){
        mID = id;
        mTitle = title;
        mPosterPath = posterPath;
        mOverview = overview;
        mVotes = votes;
        mReleaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        mID = in.readLong();
        mTitle = in.readParcelable(Title.class.getClassLoader());
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVotes = in.readParcelable(Votes.class.getClassLoader());
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
        dest.writeParcelable(mTitle, flags);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeParcelable(mVotes, flags);
        dest.writeString(mReleaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + mID + "\'" +
                ",title='" + mTitle.getTitle() + "\'" +
                ",descr='" + mOverview + "\'" +
                "}";
    }

    public long getId(){return mID;}
    public Title getTitle(){return mTitle;}
    public String getPosterPath(){return mPosterPath;}
    public String getOverview(){return mOverview;}
    public String getReleaseDate(){return mReleaseDate;}
    public Votes getVotes(){return mVotes;}

    public static class MovieBuilder{
        long nID;
        Title nTitle;
        String nPosterPath;
        String nOverview;
        Votes nVotes;
        String nReleaseDate;

        public MovieBuilder(long id, Title title, String posterPath){
            nID = id;
            nTitle = title;
            nPosterPath = posterPath;
        }

        public MovieBuilder setOverview(String newOverview){
            this.nOverview = newOverview;
            return this;
        }

        public MovieBuilder setVotes(Votes newVotes){
            this.nVotes = newVotes;
            return this;
        }

        public MovieBuilder setReleaseDate(String releaseDate){
            this.nReleaseDate = releaseDate;
            return this;
        }

        public Movie build(){
            return new Movie(
                    nID, nTitle, nPosterPath, nOverview, nVotes, nReleaseDate
            );
        }
    }

}
