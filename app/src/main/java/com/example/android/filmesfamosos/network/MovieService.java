package com.example.android.filmesfamosos.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

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

public class MovieService implements LoaderManager.LoaderCallbacks<List<Movie>>{
//    keys
    public static final String KEY_SORTING_METHOD = "sorting_method";
    public static final String KEY_PAGE = "page";
    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";

    private AsyncTaskDelegate<List<Movie>> delegate;
    private Context mContext;

    public MovieService(AsyncTaskDelegate responder, Context context){
        delegate = responder;
        mContext = context;
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

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(mContext) {
            List<Movie> mParsedMovies = Collections.emptyList();

            @Override
            protected void onStartLoading() {
//                Check if there are arguments
                if(args == null)
                    return;

//                Deliver results if there are results, otherwise force a load
                if(!mParsedMovies.isEmpty())
                    deliverResult(mParsedMovies);
                else
                    forceLoad();
            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                String sorting = args.getString(KEY_SORTING_METHOD);
                String page = args.getString(KEY_PAGE);
                if(sorting == null || page == null)
                    return Collections.emptyList();

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
            public void deliverResult(@Nullable List<Movie> data) {
                mParsedMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
//        Delegate data handling to the activity
        delegate.processFinish(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
