package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.example.android.filmesfamosos.services.MovieService;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, AsyncTaskDelegate<List<Movie>> {
//    member Variables
    private MovieService mMovieServiceLoaderCallbacks;
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
//    Saved instance Keys
    private static final String SS_KEY_SORTING_METHOD = "sorting_method";
    private static final String SS_KEY_CURRENT_MOVIES = "current_movies";
    private static final String SS_KEY_SCROLL_LISTENER = "scroll_listener";
    private static final String SS_KEY_SCROLL_POSITION = "scroll_position";

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
        mMovieAdapter = new MovieAdapter(this);
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
        }else {
            mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
            mScrollListener.resetState();
        }

//        Initialize Movies LoaderCallbacks
        mMovieServiceLoaderCallbacks = new MovieService(this, this);
        loadMovieData(mCurrentSortingMethod, "1");
    }

    @Override
    protected void onResume() {
//        Refresh favorite movies view in case user has deleted a favorite
        if(mCurrentSortingMethod.equals(MovieService.SORT_BY_FAVORITES))
            refreshMovieData();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_MOVIE_DETAIL:
                if(resultCode==RESULT_OK){
//                    Get the movie returned from detail activity and update the favorite status in the adapter data
                    Movie retMovie = data.getParcelableExtra(getString(R.string.intentres_name_movie));
                    ArrayList<Movie> currentMovies = mMovieAdapter.getMovieData();
                    for(int i = 0; i < currentMovies.size(); i++){
                        if(currentMovies.get(i).getId() == retMovie.getId())
                            currentMovies.set(i, retMovie);
                    }
                    mMovieAdapter.setMovieData(currentMovies);
                }
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
                loadMovieData(MovieService.SORT_BY_POPULARITY,"1");
                mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
                break;
            case R.id.action_sort_toprated :
                mMovieAdapter.setMovieData(new ArrayList<Movie>());
                mScrollListener.resetState();
                loadMovieData(MovieService.SORT_BY_TOP_RATED,"1");
                mCurrentSortingMethod = MovieService.SORT_BY_TOP_RATED;
                break;
            case R.id.action_see_favorites:
                mMovieAdapter.setMovieData(new ArrayList<Movie>());
                mScrollListener.resetState();
                loadMovieData(MovieService.SORT_BY_FAVORITES, "1");
                mCurrentSortingMethod = MovieService.SORT_BY_FAVORITES;
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
        super.onSaveInstanceState(outState);
    }

    @Override
    public void processFinish(List<Movie> moviePage, Loader callerLoader) {
        setLoadingBarVisibility(false);
        if(!moviePage.isEmpty()){
            setErrorMessageVisibility(false);
//            Check if the data is already loaded
            ArrayList<Movie> currentMovies = mMovieAdapter.getMovieData();
            Movie checkMovie = moviePage.get(0);
            boolean movieExists = false;
            for(int i = 0; i < currentMovies.size(); i++){
                if(currentMovies.get(i).getId() == checkMovie.getId()){
                    movieExists = true;
                    break;
                }
            }
//            Exit if the data is already loaded
            if(movieExists) return;
//            Check if movies are favorites
            for (int i = 0; i < moviePage.size(); i++)
                moviePage.get(i).setFavorite(DatabaseService.checkIsFavoriteMovie(moviePage.get(i), this));
            currentMovies.addAll(moviePage);
//            Insert movies on adapter with the correct favorite status
            mMovieAdapter.setMovieData(currentMovies);
        }else if (!mMovieAdapter.getMovieData().isEmpty()){
            setErrorMessageVisibility(true);
        }
    }

    private void refreshMovieData(){
        mMovieAdapter.setMovieData(new ArrayList<Movie>());
        mScrollListener.resetState();
        if(mCurrentSortingMethod != null){
            loadMovieData(mCurrentSortingMethod, "1");
        }else{
            mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
            loadMovieData(mCurrentSortingMethod, "1");
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadMovieData(String sortingMethod, String page){
//        Check internet connectivity
        if(NetworkUtils.isOnline(this) || mCurrentSortingMethod.equals(MovieService.SORT_BY_FAVORITES)) {
            setErrorMessageVisibility(false);
            setLoadingBarVisibility(true);

//            Create bundle for loader
            Bundle movieBundle = new Bundle();
            movieBundle.putString(MovieService.KEY_SORTING_METHOD, sortingMethod);
            movieBundle.putString(MovieService.KEY_PAGE, page);

//           Initialize or restart loader
            try{
                if(getSupportLoaderManager().getLoader(ID_MOVIE_LOADER).isStarted())
                    getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, movieBundle, mMovieServiceLoaderCallbacks);
                else
                    getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, movieBundle, mMovieServiceLoaderCallbacks);
            }catch (NullPointerException e){
                getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, movieBundle, mMovieServiceLoaderCallbacks);
            }
        }else{
            setErrorMessageVisibility(true);
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
}
