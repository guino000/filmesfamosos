package com.example.android.filmesfamosos.network;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.android.filmesfamosos.MovieDetailActivity;
import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.utilities.App;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by Guilherme Canalli on 4/8/2018.
 */

//This class is used to get Trailers data of a given movie from the API
public class TrailerService implements LoaderManager.LoaderCallbacks<List<Trailer>>{
    //    Keys
    private static final String KEY_MOVIE_ID = "movie_id";

//    Member variables
    private AsyncTaskDelegate delegate;
    private Context mContext;

    public TrailerService(AsyncTaskDelegate responder, Context context){
        this.delegate = responder;
        this.mContext = context;
    }

    private static URL buildURL(String movieID, String apiKey) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("videos")
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
    public Loader<List<Trailer>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<List<Trailer>>(mContext) {
            List<Trailer> mParsedTrailers;

            @Override
            protected void onStartLoading() {
//               Check if there are arguments
                if (args == null)
                    return;

//               Return a list of trailers if the list has data, otherwise force a load
                if (!mParsedTrailers.isEmpty()) {
                    deliverResult(mParsedTrailers);
                }else{
                    forceLoad();
                }
            }

            @Override
            public List<Trailer> loadInBackground() {
//                        Get the movie ID from the arguments
                String movieID = args.getString(KEY_MOVIE_ID);
                if(movieID == null)
                    return Collections.emptyList();

//              Get parameters and build an URL
                String apiKey = App.getContext().getString(R.string.themoviedb);
                URL trailersRequestURL = buildURL(movieID, apiKey);

//                Parse trailers from the API
                try{
                    String jsonTrailerResponse = NetworkService.getResponseFromHttpUrl(trailersRequestURL);
                    return MovieJsonUtils.getTrailersFromJson(jsonTrailerResponse);
                }catch (Exception e){
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }

            @Override
            public void deliverResult(List<Trailer> data) {
                mParsedTrailers = data;
                super.deliverResult(data);
            }
        };
    }

    @NonNull


    @Override
    public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> data) {
//        Delegate the data handling to the activity
        delegate.processFinish(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {

    }
}
