package com.example.android.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guilherme Canalli on 2/15/2018.
 */

public class Votes implements Parcelable{
    private long mVoteCount;
    private double mVoteAverage;
    private double mPopularity;

    public Votes(long voteCount, double voteAverage, double popularity){
        mVoteCount = voteCount;
        mVoteAverage = voteAverage;
        mPopularity = popularity;
    }

    protected Votes(Parcel in) {
        mVoteCount = in.readLong();
        mVoteAverage = in.readDouble();
        mPopularity = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mVoteCount);
        dest.writeDouble(mVoteAverage);
        dest.writeDouble(mPopularity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Votes> CREATOR = new Creator<Votes>() {
        @Override
        public Votes createFromParcel(Parcel in) {
            return new Votes(in);
        }

        @Override
        public Votes[] newArray(int size) {
            return new Votes[size];
        }
    };

    public long getVoteCount(){return mVoteCount;}
    public double getVoteAverage(){return mVoteAverage;}
    public double getPopularity(){return mPopularity;}

}
