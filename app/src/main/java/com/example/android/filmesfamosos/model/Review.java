package com.example.android.filmesfamosos.model;

public class Review {
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

    public String getID(){return mID;}
    public String getAuthor(){return mAuthor;}
    public String getReviewContent(){return mContent;}
    public String getUrl(){return mUrl;}
}
