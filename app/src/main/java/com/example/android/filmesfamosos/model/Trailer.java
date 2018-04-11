package com.example.android.filmesfamosos.model;

import android.content.Context;
import android.net.Uri;

import com.example.android.filmesfamosos.R;

import java.net.MalformedURLException;
import java.net.URL;

public class Trailer {
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
}
