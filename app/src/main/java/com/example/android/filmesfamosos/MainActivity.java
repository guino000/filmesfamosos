package com.example.android.filmesfamosos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvMiniaturesGrid;
    private MovieAdapter mMovieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String BY_POPULARITY = "popular";
        final String BY_RATING = "top_rated";

        mRvMiniaturesGrid = findViewById(R.id.rvMovies);
        RecyclerView.LayoutManager gridManager
                = new GridLayoutManager(this, 3);
        mRvMiniaturesGrid.setLayoutManager(gridManager);
        mRvMiniaturesGrid.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter();
        mRvMiniaturesGrid.setAdapter(mMovieAdapter);
        mRvMiniaturesGrid.setVisibility(View.VISIBLE);
        loadMovieData(BY_POPULARITY);
    }

    private void loadMovieData(String sortingMethod){
        new FetchMoviesTask().execute(sortingMethod);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>{
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            String sorting = params[0];
            URL moviesRequestUrl = NetworkUtils.buildURL(sorting, getString(R.string.themoviedb));

            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);
                ArrayList<Movie> parsedMovies = MovieJsonUtils.getMoviesFromJson(jsonMovieResponse);
                return parsedMovies;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies != null){
                mMovieAdapter.setMovieData(movies);
            }
        }
    }
}
