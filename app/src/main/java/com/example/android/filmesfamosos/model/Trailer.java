package com.example.android.filmesfamosos.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.filmesfamosos.R;

public class Trailer implements Parcelable{
    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;

    public Trailer(String id, String key, String name, String site, int size){
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
    }

    protected Trailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    //    Build video Uri for youtube
    public Uri getYoutubeURL(Context context){
        return Uri.parse(context.getString(R.string.youtube_url))
                .buildUpon()
                .appendPath(mKey)
                .build();
    }

    public String getId(){return mId;}
    public String getKey(){return mKey;}
    public String getName(){return mName;}
    public String getSite(){return mSite;}
    public int getSize(){return mSize;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeInt(mSize);
    }
}
