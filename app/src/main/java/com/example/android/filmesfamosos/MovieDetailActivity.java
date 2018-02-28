package com.example.android.filmesfamosos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.filmesfamosos.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mMoviePictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMoviePictureImageView = findViewById(R.id.iv_moviepicture);
        TextView mMovieTitleTextView = findViewById(R.id.tv_movietitle);
        TextView mMovieOriginalTitleTextView = findViewById(R.id.tv_original_title);
        TextView mMovieDescriptionTextView = findViewById(R.id.tv_moviesdescription);
        TextView mReleaseDateTextView = findViewById(R.id.tv_movie_releasedate);
        TextView mMovieRatingTextView = findViewById(R.id.tv_movierating);

        Intent intentFromMain = getIntent();
        if(intentFromMain.hasExtra(getString(R.string.intentres_name_movie))){
            Movie clickedMovie = intentFromMain.getParcelableExtra(getString(R.string.intentres_name_movie));
            String miniatureURL = clickedMovie.getPosterPath();
            Picasso.with(this).load(miniatureURL).into(mMoviePictureImageView);
            mMovieTitleTextView.setText(clickedMovie.getTitle().getTitle());
            String originalTitle = getString(R.string.moviedetail_menu_original_name) + clickedMovie.getTitle().getOriginalTitle();
            mMovieOriginalTitleTextView.setText(originalTitle);
            mMovieDescriptionTextView.setText(clickedMovie.getOverview());
            try {
                Date date = new SimpleDateFormat(getString(R.string.movieapi_date_format)).parse(clickedMovie.getReleaseDate());
                String formattedDate = new SimpleDateFormat(getString(R.string.date_short_date_format)).format(date);
                String releaseDateWithLabel = getString(R.string.moviedetail_menu_release_date) + formattedDate;
                mReleaseDateTextView.setText(releaseDateWithLabel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mMovieRatingTextView.setText(String.valueOf(
                    clickedMovie.getVotes().getVoteAverage()));
        }
    }
}
