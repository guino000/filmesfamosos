package com.example.android.filmesfamosos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

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

        mLoadingBar = findViewById(R.id.pb_loading);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);

        mRvMiniaturesGrid = findViewById(R.id.rvMovies);
        RecyclerView.LayoutManager gridManager
                = new GridLayoutManager(this, 2);
        mRvMiniaturesGrid.setLayoutManager(gridManager);
        mRvMiniaturesGrid.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRvMiniaturesGrid.setAdapter(mMovieAdapter);
        mRvMiniaturesGrid.setVisibility(View.VISIBLE);

        mScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) gridManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMovieData(mCurrentSortingMethod, String.valueOf(page) + 1);
            }
        };
        mRvMiniaturesGrid.addOnScrollListener(mScrollListener);

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

        mMovieAdapter.eraseMovieData();
        mScrollListener.resetState();
        mCurrentSortingMethod = NetworkUtils.SORT_BY_POPULARITY;
        loadMovieData(mCurrentSortingMethod, "1");
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
                loadMovieData(NetworkUtils.SORT_BY_POPULARITY,"1");
                mCurrentSortingMethod = NetworkUtils.SORT_BY_POPULARITY;
                break;
            case R.id.action_sort_toprated :
                loadMovieData(NetworkUtils.SORT_BY_TOP_RATED,"1");
                mCurrentSortingMethod = NetworkUtils.SORT_BY_TOP_RATED;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshMovieData(){
        mMovieAdapter.eraseMovieData();
        mScrollListener.resetState();
        if(!mCurrentSortingMethod.isEmpty()){
            loadMovieData(mCurrentSortingMethod, "1");
        }else{
            mCurrentSortingMethod = NetworkUtils.SORT_BY_POPULARITY;
            loadMovieData(mCurrentSortingMethod, "1");
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadMovieData(String sortingMethod, String page){
        new FetchMoviesTask().execute(sortingMethod, page);
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

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setLoadingBarVisibility(true);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            String sorting = params[0];
            String page = params[1];
            URL moviesRequestUrl = NetworkUtils.buildURL(sorting, page, getString(R.string.themoviedb));

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);
                ArrayList<Movie> parsedMovies = MovieJsonUtils.getMoviesFromJson(jsonMovieResponse);
                return parsedMovies;
            }catch (Exception e){
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            setLoadingBarVisibility(false);
            if(movies.size() > 0){
                setErrorMessageVisibility(false);
                mMovieAdapter.addMovieData(movies);
            }else if (mMovieAdapter.getMovieData().size() == 0){
                setErrorMessageVisibility(true);
            }
        }
    }
}
