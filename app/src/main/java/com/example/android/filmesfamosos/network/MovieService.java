package com.example.android.filmesfamosos.network;

import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.utilities.App;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Guilherme Canalli on 2/28/2018.
 */

public class MovieService extends AsyncTask<String, Void, List<Movie>>{

    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";

    private AsyncTaskDelegate delegate;

    public MovieService(AsyncTaskDelegate responder){
        this.delegate = responder;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if(params.length == 0){
            return Collections.emptyList();
        }

        String sorting = params[0];
        String page = params[1];
        String apiKey = App.getContext().getString(R.string.themoviedb);
        URL moviesRequestUrl = buildURL(sorting, page, apiKey);

        try{
            String jsonMovieResponse = NetworkService.getResponseFromHttpUrl(moviesRequestUrl);
            return MovieJsonUtils.getMoviesFromJson(jsonMovieResponse);
        }catch (Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);

        if(delegate != null){
            delegate.processFinish(movies);
        }
    }

    private static URL buildURL(String sorting, String page, String apiKey) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sorting)
                .appendQueryParameter("page", page)
                .appendQueryParameter("api_key",apiKey);
        try {
            return new URL(builder.build().toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return  null;
        }
    }
}
