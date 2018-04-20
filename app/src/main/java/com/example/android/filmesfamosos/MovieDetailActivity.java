package com.example.android.filmesfamosos;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.filmesfamosos.adapters.ReviewAdapter;
import com.example.android.filmesfamosos.adapters.TrailerAdapter;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.services.ReviewService;
import com.example.android.filmesfamosos.services.TrailerService;
import com.example.android.filmesfamosos.utilities.FileSystemUtils;
import com.example.android.filmesfamosos.services.DatabaseService;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity implements
        AsyncTaskDelegate<ArrayList>,
        TrailerAdapter.TrailerAdapterClickHandler{
//    Loader ID's
    private static final int TRAILER_LOADER_ID = 21;
    private static final int REVIEW_LOADER_ID = 22;

//   Member variables for overall activity
    private ImageView mMoviePictureImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieOriginalTitleTextView;
    private TextView mMovieDescriptionTextView;
    private TextView mReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private Movie mCurrentMovie;
//    Member variables for trailer section
    private TextView mTrailerErrorMsg;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private TrailerService mTrailerService;
    private ProgressBar mTrailerProgressBar;
//    Member variables for reviews section
    private TextView mReviewErrorMsg;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private ReviewService mReviewService;
    private ProgressBar mReviewProgressBar;
    private EndlessRecyclerViewScrollListener mReviewScrollListener;

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
        mReviewProgressBar = findViewById(R.id.pb_reviews_loading);
        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        mReviewErrorMsg = findViewById(R.id.tv_reviews_error_msg);

//        Set trailer recyclerView layout manager
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerRecyclerView.setHasFixedSize(true);
//        Set review rv layout manager
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewRecyclerView.setHasFixedSize(true);

//        Set trailer recyclerView adapter
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
//        Set review rv adapter
        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setNestedScrollingEnabled(false);

//        Process income intent with movie data
        Intent intentFromMain = getIntent();
        if(intentFromMain.hasExtra(getString(R.string.intentres_name_movie))){
            mCurrentMovie = intentFromMain.getParcelableExtra(getString(R.string.intentres_name_movie));
            String miniatureURL = mCurrentMovie.getPosterPath();
            Picasso.with(this).load(miniatureURL).into(mMoviePictureImageView);
            mMovieTitleTextView.setText(mCurrentMovie.getTitle().getTitle());
            String originalTitle = mCurrentMovie.getTitle().getOriginalTitle();
            mMovieOriginalTitleTextView.setText(originalTitle);
            mMovieDescriptionTextView.setText(mCurrentMovie.getOverview());
            try {
                Date date = new SimpleDateFormat(getString(R.string.movieapi_date_format)).parse(mCurrentMovie.getReleaseDate());
                String formattedDate = new SimpleDateFormat(getString(R.string.date_short_date_format)).format(date);
                String releaseDateWithLabel = formattedDate;
                mReleaseDateTextView.setText(releaseDateWithLabel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mMovieRatingTextView.setText(String.valueOf(mCurrentMovie.getVotes().getVoteAverage()));

            //        Set review endless scroll listener
            mReviewScrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mReviewRecyclerView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadReviewData(String.valueOf(mCurrentMovie.getId()), String.valueOf(page + 1));
                }
            };

//            Create back button on actionbar
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

//            Initialize trailer loader
            mTrailerService = new TrailerService(this, this);
            loadTrailerData(String.valueOf(mCurrentMovie.getId()));

//            Initialize review loader
            mReviewService = new ReviewService(this,this);
            loadReviewData(String.valueOf(mCurrentMovie.getId()), "1");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Inflate menu layout
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_detail_menu, menu);
//        Set favorite icon style according to current movie favorite status
        MenuItem item = menu.findItem(R.id.action_movie_details_add_favorites);
        setFavoriteIconStyle(item, mCurrentMovie.getIsFavorite());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemID = item.getItemId();

        switch (selectedItemID){
            case R.id.action_movie_details_add_favorites:
                toggleFavorite();
                setFavoriteIconStyle(item,mCurrentMovie.getIsFavorite());
                break;
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent retIntent = new Intent();
        retIntent.putExtra(getString(R.string.intentres_name_movie), mCurrentMovie);
        setResult(RESULT_OK, retIntent);
        super.onBackPressed();
    }

    private void toggleFavorite(){
        if(!mCurrentMovie.getIsFavorite()) {
//            Save movie poster to internal storage
            boolean successfulFileSave = FileSystemUtils.saveBitmapToInternalStorage(
                    ((BitmapDrawable) mMoviePictureImageView.getDrawable()).getBitmap(),
                    this,
                    String.valueOf(mCurrentMovie.getId()) + ".jpg");
//            Insert movie data into the database
            long insertedMovieID = DatabaseService.insertMovie(mCurrentMovie, this);
            boolean successfulInsertReviews =  DatabaseService.bulkInsertReviews(mReviewAdapter.getReviewData().toArray(
                    new Review[mReviewAdapter.getReviewData().size()]),
                    String.valueOf(mCurrentMovie.getId()),
                    this);
            boolean successfulInsertTrailers = DatabaseService.bulkInsertTrailers(mTrailerAdapter.getTrailerData().toArray(
                    new Trailer[mTrailerAdapter.getTrailerData().size()]),
                    String.valueOf(mCurrentMovie.getId()),
                    this);
//            If everything worked, set movie as favorite
            if(insertedMovieID > 0 && successfulFileSave && successfulInsertReviews && successfulInsertTrailers)
                mCurrentMovie.setFavorite(true);
            else{
//                Undo operations case anything goes wrong
                FileSystemUtils.deleteFileFromInternalStorage(FileSystemUtils.IMAGE_DIR,
                        String.valueOf(mCurrentMovie.getId()) + ".jpg");
                DatabaseService.deleteMovie(mCurrentMovie,this);
            }
        }else{
//            Remove movie data and set favorite to false
            FileSystemUtils.deleteFileFromInternalStorage(FileSystemUtils.IMAGE_DIR,
                    String.valueOf(mCurrentMovie.getId()) + ".jpg");
            DatabaseService.deleteMovie(mCurrentMovie,this);
            mCurrentMovie.setFavorite(false);
        }
    }

    private void setFavoriteIconStyle(MenuItem item, boolean isFavorite){
//        Change the favorite action menu icon style based on current favorite status
        if(isFavorite)
            item.setIcon(R.drawable.ic_favorite_white_24dp);
        else
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
    }

    private void loadReviewData(String movieID, String page){
//        TODO: Load local reviews when they exist
//        TODO: Check if movie parcel contains trailers and reviews
        if(mCurrentMovie.getReviews() != null){
            if(mCurrentMovie.getReviews().length > 0){
                ArrayList<Review> localReviews = new ArrayList<>();
                Collections.addAll(localReviews,mCurrentMovie.getReviews());
                mReviewAdapter.setReviewData(localReviews);
            }
        }

        setReviewErrorMsgVisibility(false);
        setReviewProgressbarVisibility(true);

//        Create bundle for loader
        Bundle reviewBundle = new Bundle();
        reviewBundle.putString(ReviewService.KEY_BUNDLE_MOVIE_ID, movieID);
        reviewBundle.putString(ReviewService.KEY_BUNDLE_PAGE, page);

//        Initialize or restart loader
        try{
            if(getSupportLoaderManager().getLoader(REVIEW_LOADER_ID).isStarted())
                getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID, reviewBundle, mReviewService);
            else
                getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, reviewBundle, mReviewService);
        }catch (NullPointerException e){
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, reviewBundle, mReviewService);
        }
    }

    //    Control visibility of the trailers progress bar
    private void setReviewProgressbarVisibility(boolean visible){
        if(visible)
            mReviewProgressBar.setVisibility(View.VISIBLE);
        else
            mReviewProgressBar.setVisibility(View.INVISIBLE);
    }

    //    Control visibility of trailer error message
    private void setReviewErrorMsgVisibility(boolean visible){
        if(visible) {
            mReviewErrorMsg.setVisibility(View.VISIBLE);
            mReviewRecyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            mReviewErrorMsg.setVisibility(View.INVISIBLE);
            mReviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadTrailerData(String movieID){
//        TODO: Load local trailers when they exist
        if(mCurrentMovie.getTrailers() != null){
            if(mCurrentMovie.getTrailers().length > 0){
                ArrayList<Trailer> localTrailers = new ArrayList<>();
                Collections.addAll(localTrailers,mCurrentMovie.getTrailers());
                mTrailerAdapter.setTrailerData(localTrailers);
            }
        }

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

    //    Control visibility of the trailers progress bar
    private void setTrailerProgressbarVisibility(boolean visible){
        if(visible)
            mTrailerProgressBar.setVisibility(View.VISIBLE);
        else
            mTrailerProgressBar.setVisibility(View.INVISIBLE);
    }

    //    Control visibility of trailer error message
    private void setTrailerErrorMsgVisibility(boolean visible){
        if(visible) {
            mTrailerErrorMsg.setVisibility(View.VISIBLE);
            mTrailerRecyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            mTrailerErrorMsg.setVisibility(View.INVISIBLE);
            mTrailerRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void processFinish(ArrayList newData, android.support.v4.content.Loader callerLoader) {
        switch (callerLoader.getId()){
            case TRAILER_LOADER_ID:
                setTrailerProgressbarVisibility(false);
                if(!newData.isEmpty())
                    mTrailerAdapter.setTrailerData(newData);
                else
                    setTrailerErrorMsgVisibility(true);
                break;
            case REVIEW_LOADER_ID:
                setReviewProgressbarVisibility(false);
                if(!newData.isEmpty())
                    mReviewAdapter.setReviewData(newData);
                else
                    setReviewErrorMsgVisibility(true);
                break;
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
