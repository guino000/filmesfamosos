package com.example.android.filmesfamosos.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable{
    private String mID;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public Review(String id, String author, String content, String url){
        mID = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    protected Review(Parcel in) {
        mID = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getID(){return mID;}
    public String getAuthor(){return mAuthor;}
    public String getReviewContent(){return mContent;}
    public String getUrl(){return mUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }
}
