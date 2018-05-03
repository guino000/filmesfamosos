package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.filmesfamosos.adapters.MovieAdapter;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.services.DatabaseService;
import com.example.android.filmesfamosos.loaders.MovieCursorLoader;
import com.example.android.filmesfamosos.loaders.MovieAsyncLoader;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, AsyncTaskDelegate {
//    member Variables
    private MovieAsyncLoader mMovieAsyncLoaderLoaderCallbacks;
    private MovieCursorLoader mFavoriteServiceLoaderCallbacks;
    private RecyclerView mRvMiniaturesGrid;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingBar;
    private TextView mErrorMessageTextView;
    private String mCurrentSortingMethod;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerViewScrollListener mScrollListener;

//    Request code to get movie favorite status from detail activity
    private static final int REQUEST_CODE_MOVIE_DETAIL = 1;
//    Loader ID
    private final static int ID_MOVIE_LOADER = 11;
    private final static int ID_FAVORITES_LOADER = 12;
//    Saved instance Keys
    private static final String SS_KEY_SORTING_METHOD = "sorting_method";
    private static final String SS_KEY_CURRENT_MOVIES = "current_movies";
    private static final String SS_KEY_SCROLL_LISTENER = "scroll_listener";
    private static final String SS_KEY_SCROLL_POSITION = "scroll_position";
    private static final String SS_KEY_DATASET_TYPE = "dataset_type";
//    Sorting method keys
    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_FAVORITES = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set layout components
        mLoadingBar = findViewById(R.id.pb_loading);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mRvMiniaturesGrid = findViewById(R.id.rvMovies);

        //Set up Layout Manager
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRvMiniaturesGrid.setLayoutManager(new GridLayoutManager(this, 2));
        }else{
            mRvMiniaturesGrid.setLayoutManager(new GridLayoutManager(this, 3));
        }
        mRvMiniaturesGrid.setHasFixedSize(true);

        //Set up Adapter to Recycler View
        mMovieAdapter = new MovieAdapter(this, MovieAdapter.DATASET_LIST);
        mRvMiniaturesGrid.setAdapter(mMovieAdapter);
        mRvMiniaturesGrid.setVisibility(View.VISIBLE);

        //Set up swipe refresh
        mSwipeRefreshLayout = findViewById(R.id.sr_swipeRefreshMovies);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.secondaryColor);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshMovieData();
                    }
                }
        );

        //Set Up Endless Scroll Listener
        mScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mRvMiniaturesGrid.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(!mCurrentSortingMethod.equals(SORT_BY_FAVORITES))
                    loadMovieData(mCurrentSortingMethod, String.valueOf(page) + 1);
            }
        };
        mRvMiniaturesGrid.addOnScrollListener(mScrollListener);

//        Recover app state
        if(savedInstanceState != null){
            mCurrentSortingMethod = savedInstanceState.getString(SS_KEY_SORTING_METHOD);
            mMovieAdapter.setMovieData(savedInstanceState.<Movie>getParcelableArrayList(SS_KEY_CURRENT_MOVIES));
            mScrollListener.setState(savedInstanceState.getBundle(SS_KEY_SCROLL_LISTENER));
            mRvMiniaturesGrid.scrollToPosition(savedInstanceState.getInt(SS_KEY_SCROLL_POSITION));
            mMovieAdapter.setCurrentDatasetType(savedInstanceState.getInt(SS_KEY_DATASET_TYPE));
        }else {
            mCurrentSortingMethod = SORT_BY_POPULARITY;
            mScrollListener.resetState();
        }

//        Initialize Movies LoaderCallbacks
        mMovieAsyncLoaderLoaderCallbacks = new MovieAsyncLoader(this, this);
        mFavoriteServiceLoaderCallbacks  = new MovieCursorLoader(this,this, mMovieAdapter);
        loadMovieData(mCurrentSortingMethod, "1");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_MOVIE_DETAIL:
                if(resultCode==RESULT_OK){
//                    Get the movie returned from detail activity and update the favorite status in the adapter data
                    if(mMovieAdapter.getCurrentDatasetType() == MovieAdapter.DATASET_LIST) {
                        Movie retMovie = data.getParcelableExtra(getString(R.string.intentres_name_movie));
                        ArrayList<Movie> currentMovies = mMovieAdapter.getMovieData();
                        for (int i = 0; i < currentMovies.size(); i++) {
                            if (currentMovies.get(i).getId() == retMovie.getId())
                                currentMovies.set(i, retMovie);
                        }
                        mMovieAdapter.setMovieData(currentMovies);
                    }
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemID = item.getItemId();

        switch (selectedItemID){
            case R.id.action_sort_popularity:
                mMovieAdapter.setMovieData(new ArrayList<Movie>());
                mScrollListener.resetState();
                loadMovieData(SORT_BY_POPULARITY,"1");
                mCurrentSortingMethod = SORT_BY_POPULARITY;
                break;
            case R.id.action_sort_toprated :
                mMovieAdapter.setMovieData(new ArrayList<Movie>());
                mScrollListener.resetState();
                loadMovieData(SORT_BY_TOP_RATED,"1");
                mCurrentSortingMethod = SORT_BY_TOP_RATED;
                break;
            case R.id.action_see_favorites:
                mScrollListener.resetState();
                loadMovieData(SORT_BY_FAVORITES, "1");
                mCurrentSortingMethod = SORT_BY_FAVORITES;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SS_KEY_SORTING_METHOD, mCurrentSortingMethod);
        outState.putParcelableArrayList(SS_KEY_CURRENT_MOVIES, mMovieAdapter.getMovieData());
        outState.putBundle(SS_KEY_SCROLL_LISTENER,mScrollListener.getState());
        outState.putInt(SS_KEY_SCROLL_POSITION,
                ((GridLayoutManager) mRvMiniaturesGrid.getLayoutManager()).findLastVisibleItemPosition());
        outState.putInt(SS_KEY_DATASET_TYPE,mMovieAdapter.getCurrentDatasetType());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void processFinish(Object data, Loader callerLoader) {
        setLoadingBarVisibility(false);
        if(data instanceof ArrayList) {
            ArrayList<Movie> moviePage = (ArrayList<Movie>) data;
            if (!moviePage.isEmpty()) {
                setErrorMessageVisibility(false);
//            Check if the data is already loaded
                ArrayList<Movie> currentMovies = mMovieAdapter.getMovieData();
                Movie checkMovie = moviePage.get(0);
                boolean movieExists = false;
                for (int i = 0; i < currentMovies.size(); i++) {
                    if (currentMovies.get(i).getId() == checkMovie.getId()) {
                        movieExists = true;
                        break;
                    }
                }
//            Exit if the data is already loaded
                if (movieExists) return;
//            Check if movies are favorites
                for (int i = 0; i < moviePage.size(); i++)
                    moviePage.get(i).setFavorite(DatabaseService.checkIsFavoriteMovie(moviePage.get(i), this));
                currentMovies.addAll(moviePage);
//            Insert movies on adapter with the correct favorite status
                mMovieAdapter.setMovieData(currentMovies);
                mMovieAdapter.setCurrentDatasetType(MovieAdapter.DATASET_LIST);
            } else if (!mMovieAdapter.getMovieData().isEmpty()) {
                setErrorMessageVisibility(true);
            }
        }else if (data instanceof Cursor){
            Cursor movieCursor = (Cursor) data;
            if(movieCursor.getCount() > 0) {
                mMovieAdapter.swapCursor(movieCursor);
                mMovieAdapter.setCurrentDatasetType(MovieAdapter.DATASET_CURSOR);
            }
            else
                setErrorMessageVisibility(true);
        }
    }

    private void refreshMovieData(){
        switch (mMovieAdapter.getCurrentDatasetType()){
            case MovieAdapter.DATASET_LIST:
                mMovieAdapter.setMovieData(new ArrayList<Movie>());
                mScrollListener.resetState();
                if(mCurrentSortingMethod != null){
                    loadMovieData(mCurrentSortingMethod, "1");
                }else{
                    mCurrentSortingMethod = SORT_BY_POPULARITY;
                    loadMovieData(mCurrentSortingMethod, "1");
                }
                break;
            case MovieAdapter.DATASET_CURSOR:
                mMovieAdapter.swapCursor(null);
                mScrollListener.resetState();
                loadMovieData(SORT_BY_FAVORITES,"1");
                break;
            default:
                throw new IllegalArgumentException("Not supported yet.");
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadMovieData(String sortingMethod, String page){
        setErrorMessageVisibility(false);
        setLoadingBarVisibility(true);
        switch (sortingMethod){
            case SORT_BY_POPULARITY: case SORT_BY_TOP_RATED:
        //        Check internet connectivity
                if(NetworkUtils.isOnline(this) || sortingMethod.equals(SORT_BY_FAVORITES)) {
//                  Create bundle for loader
                    Bundle movieBundle = new Bundle();
                    movieBundle.putString(MovieAsyncLoader.KEY_SORTING_METHOD, sortingMethod);
                    movieBundle.putString(MovieAsyncLoader.KEY_PAGE, page);
                    initializeOrRestartLoader(ID_MOVIE_LOADER,movieBundle);
                }else{
                    setLoadingBarVisibility(false);
                    setErrorMessageVisibility(true);
                }
                break;
            case SORT_BY_FAVORITES:
                initializeOrRestartLoader(ID_FAVORITES_LOADER,null);
                break;
            default:
                throw new UnsupportedOperationException("Sorting method not supported yet.");
        }
    }

    private void initializeOrRestartLoader(int loaderID, Bundle args){
        android.support.v4.app.LoaderManager.LoaderCallbacks callbacks;
        switch (loaderID){
            case ID_MOVIE_LOADER:
                callbacks = mMovieAsyncLoaderLoaderCallbacks;
                getSupportLoaderManager().destroyLoader(ID_FAVORITES_LOADER);
                break;
            case ID_FAVORITES_LOADER:
                callbacks = mFavoriteServiceLoaderCallbacks;
                getSupportLoaderManager().destroyLoader(ID_MOVIE_LOADER);
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

    private void setErrorMessageVisibility(boolean visible){
        if(visible){
            mRvMiniaturesGrid.setVisibility(View.INVISIBLE);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        }else{
            mRvMiniaturesGrid.setVisibility(View.VISIBLE);
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setLoadingBarVisibility(boolean visible){
        if(visible){
            mLoadingBar.setVisibility(View.VISIBLE);
        }else{
            mLoadingBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(Movie clickedMovie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToMovieDetail = new Intent(context, destinationClass);
        intentToMovieDetail.putExtra("movie", clickedMovie);
        startActivityForResult(intentToMovieDetail, REQUEST_CODE_MOVIE_DETAIL);
    }
}
