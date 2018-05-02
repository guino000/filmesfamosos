package com.example.android.filmesfamosos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import com.example.android.filmesfamosos.loaders.ReviewCursorLoader;
import com.example.android.filmesfamosos.loaders.TrailerCursorLoader;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.loaders.ReviewAsyncLoader;
import com.example.android.filmesfamosos.loaders.TrailerAsyncLoader;
import com.example.android.filmesfamosos.utilities.FileSystemUtils;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//TODO: Adapt this activity to use the new dual dataset adapter
public class MovieDetailActivity extends AppCompatActivity implements
        AsyncTaskDelegate,
        TrailerAdapter.TrailerAdapterClickHandler{
//    Loader ID's
    private static final int TRAILER_LOADER_ID = 21;
    private static final int TRAILER_CURSOR_LOADER_ID = 22;
    private static final int REVIEW_LOADER_ID = 23;
    private static final int REVIEW_CURSOR_LOADER_ID = 24;

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
    private TrailerAsyncLoader mTrailerAsyncLoader;
    private TrailerCursorLoader mTrailerCursorLoader;
    private ProgressBar mTrailerProgressBar;
//    Member variables for reviews section
    private TextView mReviewErrorMsg;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private ReviewAsyncLoader mReviewAsyncLoader;
    private ReviewCursorLoader mReviewCursorLoader;
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
        mTrailerAdapter = new TrailerAdapter(this, TrailerAdapter.DATASET_LIST);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
//        Set review rv adapter
        mReviewAdapter = new ReviewAdapter(ReviewAdapter.DATASET_LIST);
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

//            Set adapter dataset type depending if movie is favorite or not
            if(mCurrentMovie.getIsFavorite()) {
                mTrailerAdapter.setCurrentDatasetType(TrailerAdapter.DATASET_CURSOR);
                mReviewAdapter.setCurrentDatasetType(ReviewAdapter.DATASET_CURSOR);
            }else{
                mTrailerAdapter.setCurrentDatasetType(TrailerAdapter.DATASET_LIST);
                mReviewAdapter.setCurrentDatasetType(ReviewAdapter.DATASET_LIST);
            }

//            Create back button on actionbar
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

//            Initialize trailer loader
            mTrailerAsyncLoader = new TrailerAsyncLoader(this, this);
            mTrailerCursorLoader = new TrailerCursorLoader(this,this);
            loadTrailerData(String.valueOf(mCurrentMovie.getId()));

//            Initialize review loader
            mReviewAsyncLoader = new ReviewAsyncLoader(this,this);
            mReviewCursorLoader = new ReviewCursorLoader(this,this);
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
            boolean posterSaveOK = FileSystemUtils.saveBitmapToInternalStorage(
                    ((BitmapDrawable) mMoviePictureImageView.getDrawable()).getBitmap(),
                    this,
                    String.valueOf(mCurrentMovie.getId()) + ".jpg");

//            Insert movie data into database
            ContentValues movieValues = MovieJsonUtils.getMovieContentValues(mCurrentMovie);
            Uri insertedMovieURI = getContentResolver().insert(
                    Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,MoviesContract.PATH_MOVIE),
                    movieValues);
            boolean insertMovieOK = !insertedMovieURI.getLastPathSegment().equals("-1");

//            Insert review data into database
            boolean insertReviewOK = true;
            if(mReviewAdapter.getReviewData() != null) {
                for (Review review : mReviewAdapter.getReviewData()) {
                    ContentValues reviewValues = MovieJsonUtils.getReviewContentValues(review, String.valueOf(mCurrentMovie.getId()));
                    Uri insertedReviewURI = getContentResolver().insert(
                            Uri.withAppendedPath(insertedMovieURI, MoviesContract.PATH_REVIEW),
                            reviewValues);
                    if (insertedReviewURI.getLastPathSegment().equals("-1") && insertReviewOK)
                        insertReviewOK = false;
                }
            }

//            Insert trailer data into database
            boolean insertTrailerOK = true;
            if(mTrailerAdapter.getTrailerData() != null) {
                for (Trailer trailer : mTrailerAdapter.getTrailerData()) {
                    ContentValues trailerValues = MovieJsonUtils.getTrailerContentValues(trailer, String.valueOf(mCurrentMovie.getId()));
                    Uri insertedTrailerURI = getContentResolver().insert(
                            Uri.withAppendedPath(insertedMovieURI, MoviesContract.PATH_TRAILER),
                            trailerValues);
                    if (insertedTrailerURI.getLastPathSegment().equals("-1") && insertTrailerOK)
                        insertTrailerOK = false;
                }
            }

//            If everything worked, set movie as favorite
            if(posterSaveOK && insertMovieOK && insertReviewOK && insertTrailerOK)
                mCurrentMovie.setFavorite(true);
            else{
//                Undo operations case anything goes wrong
                FileSystemUtils.deleteFileFromInternalStorage(FileSystemUtils.IMAGE_DIR,
                        String.valueOf(mCurrentMovie.getId()) + ".jpg");
                getContentResolver().delete(insertedMovieURI,null,null);
            }
        }else{
//            Remove movie data and set favorite to false
            FileSystemUtils.deleteFileFromInternalStorage(FileSystemUtils.IMAGE_DIR,
                    String.valueOf(mCurrentMovie.getId()) + ".jpg");
            getContentResolver().delete(
                    MoviesContract.MovieEntry.buildMovieUriWithId(String.valueOf(mCurrentMovie.getId())),
                    null,
                    null);
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
        setReviewErrorMsgVisibility(false);
        setReviewProgressbarVisibility(true);

        Bundle reviewBundle = new Bundle();

        switch (mReviewAdapter.getCurrentDatasetType()){
            case ReviewAdapter.DATASET_LIST:
                reviewBundle.putString(ReviewAsyncLoader.KEY_BUNDLE_MOVIE_ID, movieID);
                reviewBundle.putString(ReviewAsyncLoader.KEY_BUNDLE_PAGE, page);
                initializeOrRestartLoader(REVIEW_LOADER_ID, reviewBundle);
                break;
            case ReviewAdapter.DATASET_CURSOR:
                reviewBundle.putString(ReviewCursorLoader.KEY_BUNDLE_MOVIE_ID, movieID);
                initializeOrRestartLoader(REVIEW_CURSOR_LOADER_ID, reviewBundle);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private void loadTrailerData(String movieID){
        setTrailerErrorMsgVisibility(false);
        setTrailerProgressbarVisibility(true);

        Bundle trailerBundle = new Bundle();

        switch (mTrailerAdapter.getCurrentDatasetType()){
            case TrailerAdapter.DATASET_LIST:
                trailerBundle.putString(TrailerAsyncLoader.KEY_MOVIE_ID, movieID);
                initializeOrRestartLoader(TRAILER_LOADER_ID, trailerBundle);
                break;
            case TrailerAdapter.DATASET_CURSOR:
                trailerBundle.putString(TrailerCursorLoader.KEY_BUNDLE_MOVIE_ID, movieID);
                initializeOrRestartLoader(TRAILER_CURSOR_LOADER_ID, trailerBundle);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private void initializeOrRestartLoader(int loaderID, Bundle args){
        android.support.v4.app.LoaderManager.LoaderCallbacks callbacks;
        switch (loaderID){
            case REVIEW_LOADER_ID:
                callbacks = mReviewAsyncLoader;
                getSupportLoaderManager().destroyLoader(REVIEW_CURSOR_LOADER_ID);
                break;
            case REVIEW_CURSOR_LOADER_ID:
                callbacks = mReviewCursorLoader;
                getSupportLoaderManager().destroyLoader(REVIEW_LOADER_ID);
                break;
            case TRAILER_LOADER_ID:
                callbacks = mTrailerAsyncLoader;
                getSupportLoaderManager().destroyLoader(TRAILER_CURSOR_LOADER_ID);
                break;
            case TRAILER_CURSOR_LOADER_ID:
                callbacks = mTrailerCursorLoader;
                getSupportLoaderManager().destroyLoader(TRAILER_LOADER_ID);
                break;
            default:
                throw new UnsupportedOperationException("Load does not exist!");
        }

        try{
            if(getSupportLoaderManager().getLoader(loaderID).isStarted())
                getSupportLoaderManager().restartLoader(loaderID, args, callbacks);
            else
                getSupportLoaderManager().initLoader(loaderID, args, callbacks);
        }catch (NullPointerException e){
            getSupportLoaderManager().initLoader(loaderID, args, callbacks);
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
    public void processFinish(Object newData, android.support.v4.content.Loader callerLoader) {
        switch (callerLoader.getId()){
            case TRAILER_LOADER_ID:
                ArrayList<Trailer> newTrailerList = (ArrayList<Trailer>) newData;
                setTrailerProgressbarVisibility(false);
                if (!newTrailerList.isEmpty())
                    mTrailerAdapter.setTrailerData(newTrailerList);
                else
                    setTrailerErrorMsgVisibility(true);
                break;
            case REVIEW_LOADER_ID:
                ArrayList<Review> newReviewData = (ArrayList<Review>) newData;
                setReviewProgressbarVisibility(false);
                if(!newReviewData.isEmpty())
                    mReviewAdapter.setReviewData(newReviewData);
                else
                    setReviewErrorMsgVisibility(true);
                break;
            case TRAILER_CURSOR_LOADER_ID:
                Cursor newTrailerCursor = (Cursor) newData;
                setTrailerProgressbarVisibility(false);
                if(newTrailerCursor.getCount() > 0)
                    mTrailerAdapter.swapCursor(newTrailerCursor);
                else
                    setTrailerErrorMsgVisibility(true);
                break;
            case REVIEW_CURSOR_LOADER_ID:
                Cursor newReviewCursor = (Cursor) newData;
                setReviewProgressbarVisibility(false);
                if(newReviewCursor.getCount() > 0)
                    mReviewAdapter.swapCursor(newReviewCursor);
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
