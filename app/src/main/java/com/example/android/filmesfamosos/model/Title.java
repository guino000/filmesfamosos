package com.example.android.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guilherme Canalli on 2/15/2018.
 */

public class Title implements Parcelable{
    private String mTitle;
    private String mOriginalTitle;

    public Title(String title, String originalTitle){
        mTitle = title;
        mOriginalTitle = originalTitle;
    }

    protected Title(Parcel in) {
        mTitle = in.readString();
        mOriginalTitle = in.readString();
    }

    public static final Creator<Title> CREATOR = new Creator<Title>() {
        @Override
        public Title createFromParcel(Parcel in) {
            return new Title(in);
        }

        @Override
        public Title[] newArray(int size) {
            return new Title[size];
        }
    };

    public String getTitle(){return mTitle;}
    public String getOriginalTitle(){return mOriginalTitle;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
    }
}
