package com.example.android.filmesfamosos.model;

import android.net.Uri;

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

    public String getId(){return mId;}
    public String getKey(){return mKey;}
    public String getName(){return mName;}
    public String getSite(){return mSite;}
    public int getSize(){return mSize;}
}
