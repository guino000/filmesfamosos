package com.example.android.filmesfamosos.loaders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.services.NetworkService;
import com.example.android.filmesfamosos.utilities.App;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by Guilherme Canalli on 2/28/2018.
 */

public class MovieAsyncLoader implements LoaderManager.LoaderCallbacks<List<Movie>>{
//    keys
    public static final String KEY_SORTING_METHOD = "sorting_method";
    public static final String KEY_PAGE = "page";
    private static final String KEY_APIKEY = "api_key";

    private AsyncTaskDelegate<List<Movie>> delegate;
    private Context mContext;
    private long mCurrentPage;

    public MovieAsyncLoader(AsyncTaskDelegate<List<Movie>> responder, Context context){
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
                .appendQueryParameter(KEY_PAGE, page)
                .appendQueryParameter(KEY_APIKEY,apiKey);
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

                try{
                    String apiKey = App.getContext().getString(R.string.themoviedb);
                    URL moviesRequestUrl = buildURL(sorting, page, apiKey);
                    String jsonMovieResponse = NetworkService.getResponseFromHttpUrl(moviesRequestUrl);
                    return MovieJsonUtils.getMoviesFromJson(jsonMovieResponse,mContext);
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
        delegate.processFinish(data, loader);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
