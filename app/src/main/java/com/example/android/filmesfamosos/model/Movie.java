package com.example.android.filmesfamosos.model;

/**
 * Created by Gui on 2/15/2018.
 */

public class Movie {
    long mID;
    Title mTitle;
    String mPosterPath;
    String mOverview;
    Votes mVotes;

    public Movie(long id, Title title, String posterPath, String overview, Votes votes) {
        mID = id;
        mTitle = title;
        mPosterPath = posterPath;
        mOverview = overview;
        mVotes = votes;
    }

    public long getId(){return mID;}
    public Title getTitle(){return mTitle;}
    public String getPosterPath(){return mPosterPath;}
    public String getOverview(){return mOverview;}
    public Votes getVotes(){return mVotes;}

    public static class MovieBuilder{
        long nID;
        Title nTitle;
        String nPosterPath;
        String nOverview;
        Votes nVotes;

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

        public Movie build(){
            return new Movie(
                    nID, nTitle, nPosterPath, nOverview, nVotes
            );
        }
    }

}
