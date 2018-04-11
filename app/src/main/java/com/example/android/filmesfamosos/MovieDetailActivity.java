package com.example.android.filmesfamosos;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.filmesfamosos.adapters.TrailerAdapter;
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

public class MovieDetailActivity extends AppCompatActivity implements
        AsyncTaskDelegate<ArrayList<Trailer>>, TrailerAdapter.TrailerAdapterClickHandler{
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
    private TextView mTrailerErrorMsg;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private TrailerService mTrailerService;
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
        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        mTrailerErrorMsg = findViewById(R.id.tv_trailer_error_msg);

//        Set recyclerView layout manager
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerRecyclerView.setHasFixedSize(true);

//        Set recyclerView adapter
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);

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

//            Initialize trailer loader
            mTrailerService = new TrailerService(this, this);
            loadTrailerData(String.valueOf(clickedMovie.getId()));
        }
//        TODO: Create reviews loader
//        TODO: Create reviews adapter
//        TODO: Initialize the reviews Loader
//        TODO: Implement asynctaskdelegate listener to reviews
    }

    //    Control visibility of the trailers progress bar
    private void setTrailerProgressbarVisibility(boolean visible){
        if(visible)
            mTrailerProgressBar.setVisibility(View.VISIBLE);
        else
            mTrailerProgressBar.setVisibility(View.INVISIBLE);
    }

//    Control visibility of trailer error message
    private void setTrailerErrorMsgVisibility(boolean visible){
        if(visible)
            mTrailerErrorMsg.setVisibility(View.VISIBLE);
        else
            mTrailerErrorMsg.setVisibility(View.INVISIBLE);
    }

    private void loadTrailerData(String movieID){
        setTrailerErrorMsgVisibility(false);
        setTrailerProgressbarVisibility(true);

//        Create bundle for loader
        Bundle trailerBundle = new Bundle();
        trailerBundle.putString(TrailerService.KEY_MOVIE_ID, movieID);

//        Initialize or restart loader
        try{
            if(getSupportLoaderManager().getLoader(TRAILER_LOADER_ID).isStarted())
                getSupportLoaderManager().restartLoader(TRAILER_LOADER_ID, trailerBundle, mTrailerService);
            else
                getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, trailerBundle, mTrailerService);
        }catch (NullPointerException e){
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, trailerBundle, mTrailerService);
        }
    }

    @Override
    public void processFinish(ArrayList<Trailer> newTrailers) {
        setTrailerProgressbarVisibility(false);
        if(!newTrailers.isEmpty()){
            mTrailerAdapter.setTrailerData(newTrailers);
        }else{
            setTrailerErrorMsgVisibility(true);
        }
    }

    @Override
    public void onClick(Trailer clickedTrailer) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setData(clickedTrailer.getYoutubeURL(this));
        if(videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(videoIntent);
        }
    }
}
