package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.utilities.MovieService;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, AsyncTaskDelegate {

    private RecyclerView mRvMiniaturesGrid;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingBar;
    private TextView mErrorMessageTextView;
    private String mCurrentSortingMethod;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerViewScrollListener mScrollListener;

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

        //Set Up Endless Scroll Listener
        mScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mRvMiniaturesGrid.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMovieData(mCurrentSortingMethod, String.valueOf(page) + 1);
            }
        };
        mRvMiniaturesGrid.addOnScrollListener(mScrollListener);

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

        //Initialize data in case it's empty
        if(mMovieAdapter.getMovieData().isEmpty()) {
            mMovieAdapter.eraseMovieData();
            mScrollListener.resetState();
            mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
            loadMovieData(mCurrentSortingMethod, "1");
        }
    }

    @Override
    public void onClick(Movie clickedMovie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToMovieDetail = new Intent(context, destinationClass);
        intentToMovieDetail.putExtra("movie", clickedMovie);
        startActivity(intentToMovieDetail);
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

        mMovieAdapter.eraseMovieData();
        mScrollListener.resetState();

        switch (selectedItemID){
            case R.id.action_sort_popularity:
                loadMovieData(MovieService.SORT_BY_POPULARITY,"1");
                mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
                break;
            case R.id.action_sort_toprated :
                loadMovieData(MovieService.SORT_BY_TOP_RATED,"1");
                mCurrentSortingMethod = MovieService.SORT_BY_TOP_RATED;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void processFinish(Object output) {
        ArrayList<Movie> movies = (ArrayList<Movie>) output;

        setLoadingBarVisibility(false);
        if(!movies.isEmpty()){
            setErrorMessageVisibility(false);
            mMovieAdapter.addMovieData(movies);
        }else if (!mMovieAdapter.getMovieData().isEmpty()){
            setErrorMessageVisibility(true);
        }
    }

    private void refreshMovieData(){
        mMovieAdapter.eraseMovieData();
        mScrollListener.resetState();
        if(!mCurrentSortingMethod.isEmpty()){
            loadMovieData(mCurrentSortingMethod, "1");
        }else{
            mCurrentSortingMethod = MovieService.SORT_BY_POPULARITY;
            loadMovieData(mCurrentSortingMethod, "1");
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadMovieData(String sortingMethod, String page){
        if(NetworkUtils.isOnline(this)) {
            setErrorMessageVisibility(false);
            setLoadingBarVisibility(true);
            new MovieService(this).execute(sortingMethod, page);
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
