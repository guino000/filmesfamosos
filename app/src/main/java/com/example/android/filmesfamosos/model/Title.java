package com.example.android.filmesfamosos.model;

/**
 * Created by Gui on 2/15/2018.
 */

public class Title {
    String mTitle;
    String mOriginalTitle;

    public Title(String title, String originalTitle){
        mTitle = title;
        mOriginalTitle = originalTitle;
    }

    public String getTitle(){return mTitle;}
    public String getOriginalTitle(){return mOriginalTitle;}
}
