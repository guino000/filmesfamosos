package com.example.android.filmesfamosos;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.network.TrailerService;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements AsyncTaskDelegate{
//    Loader ID's
    private static final int TRAILER_LOADER_ID = 21;
    private static final int REVIEW_LOADER_ID = 22;

//   Member variables
    private ImageView mMoviePictureImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieOriginalTitleTextView;
    private TextView mMovieDescriptionTextView;
    private TextView mReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private ProgressBar mTrailerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

//        Member variables
        mMoviePictureImageView = findViewById(R.id.iv_moviepicture);
        mMovieTitleTextView = findViewById(R.id.tv_movietitle);
        mMovieOriginalTitleTextView = findViewById(R.id.tv_original_title);
        mMovieDescriptionTextView = findViewById(R.id.tv_moviesdescription);
        mReleaseDateTextView = findViewById(R.id.tv_movie_releasedate);
        mMovieRatingTextView = findViewById(R.id.tv_movierating);
        mTrailerProgressBar = findViewById(R.id.pb_trailer_loading);

//        Process income intent with movie data
        Intent intentFromMain = getIntent();
        if(intentFromMain.hasExtra(getString(R.string.intentres_name_movie))){
            Movie clickedMovie = intentFromMain.getParcelableExtra(getString(R.string.intentres_name_movie));
            String miniatureURL = clickedMovie.getPosterPath();
            Picasso.with(this).load(miniatureURL).into(mMoviePictureImageView);
            mMovieTitleTextView.setText(clickedMovie.getTitle().getTitle());
            String originalTitle = clickedMovie.getTitle().getOriginalTitle();
            mMovieOriginalTitleTextView.setText(originalTitle);
            mMovieDescriptionTextView.setText(clickedMovie.getOverview());
            try {
                Date date = new SimpleDateFormat(getString(R.string.movieapi_date_format)).parse(clickedMovie.getReleaseDate());
                String formattedDate = new SimpleDateFormat(getString(R.string.date_short_date_format)).format(date);
                String releaseDateWithLabel = formattedDate;
                mReleaseDateTextView.setText(releaseDateWithLabel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mMovieRatingTextView.setText(String.valueOf(
                    clickedMovie.getVotes().getVoteAverage()));
        }

//        TODO: Initialize the trailers Loader
//        TODO: Initialize the reviews Loader
    }

//    Control visibility of the trailers progress bar
    private void setTrailerProgressbarVisibility(boolean visible){
        if(visible)
            mTrailerProgressBar.setVisibility(View.VISIBLE);
        else
            mTrailerProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void processFinish(Object output) {
        ArrayList<Trailer> trailers = (ArrayList<Trailer>) output;

        setTrailerProgressbarVisibility(false);
        if(!trailers.isEmpty()){
// TODO: Add parsed trailers to the trailer adapter
        }else{
//TODO: Show an error message on the trailers layout if nothing was parsed
        }
    }
}
