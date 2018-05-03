package com.example.android.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guilherme Canalli on 2/15/2018.
 */

public class Movie implements Parcelable{
    private long mID;
    private Title mTitle;
    private String mPosterPath;
    private String mOverview;
    private String mReleaseDate;
    private Votes mVotes;
    private Review[] mReviews;
    private Trailer[] mTrailers;
    private boolean mIsFavorite;

    public Movie(long id, Title title, String posterPath, String overview, Votes votes,
                 String releaseDate, boolean isFavorite, Trailer[] trailers, Review[] reviews){
        mID = id;
        mTitle = title;
        mPosterPath = posterPath;
        mOverview = overview;
        mVotes = votes;
        mReleaseDate = releaseDate;
        mIsFavorite = isFavorite;
        mTrailers = trailers;
        mReviews = reviews;
    }

    protected Movie(Parcel in) {
        mID = in.readLong();
        mTitle = in.readParcelable(Title.class.getClassLoader());
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVotes = in.readParcelable(Votes.class.getClassLoader());
        mReleaseDate = in.readString();
        mIsFavorite = in.readByte() != 0;
        mReviews = in.createTypedArray(Review.CREATOR);
        mTrailers = in.createTypedArray(Trailer.CREATOR);
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
        dest.writeByte((byte) (mIsFavorite ? 1 : 0));
        dest.writeTypedArray(mReviews,flags);
        dest.writeTypedArray(mTrailers,flags);
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
    public void setFavorite(boolean isFavorite){mIsFavorite = isFavorite;}
    public boolean getIsFavorite(){return mIsFavorite;}
    public void setTrailers(Trailer[] trailers){
        mTrailers = trailers;
    }
    public Trailer[] getTrailers(){
        return mTrailers;
    }
    public void setReviews(Review[] reviews){
        mReviews = reviews;
    }
    public Review[] getReviews(){
        return mReviews;
    }

    public static class MovieBuilder{
        long nID;
        Title nTitle;
        String nPosterPath;
        String nOverview;
        Votes nVotes;
        String nReleaseDate;
        Review[] nReviews;
        Trailer[] nTrailers;
        boolean nIsFavorite;

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

        public MovieBuilder setFavorite(boolean isFavorite){
            this.nIsFavorite = isFavorite;
            return this;
        }

        public MovieBuilder setReviews(Review[] reviews){
            this.nReviews = reviews;
            return this;
        }

        public MovieBuilder setTrailers(Trailer[] trailers){
            this.nTrailers = trailers;
            return this;
        }

        public Movie build(){
            return new Movie(
                    nID, nTitle, nPosterPath, nOverview, nVotes, nReleaseDate,
                    nIsFavorite, nTrailers, nReviews
            );
        }
    }

}
