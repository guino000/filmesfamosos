package com.example.android.filmesfamosos.model;

/**
 * Created by Gui on 2/15/2018.
 */

public class Votes {
    long mVoteCount;
    float mVoteAverage;
    float mPopularity;

    public Votes(long voteCount, float voteAverage, float popularity){
        mVoteCount = voteCount;
        mVoteAverage = voteAverage;
        mPopularity = popularity;
    }

    public long getVoteCount(){return mVoteCount;}
    public float getVoteAverage(){return mVoteAverage;}
    public float getPopularity(){return mPopularity;}

}
