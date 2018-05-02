package com.example.android.filmesfamosos.loaders;

import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.MoviesContract;

public class MovieCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private AsyncTaskDelegate<Cursor> mDelegate;
//    Args Keys
    public static final String KEY_BUNDLE_MOVIE_ID = "movie_id";

    public MovieCursorLoader(Context context, AsyncTaskDelegate<Cursor> delegate){
        mContext = context;
        mDelegate = delegate;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri contentUri;
        if(args != null){
            contentUri = MoviesContract.MovieEntry.buildMovieUriWithId(args.getString(KEY_BUNDLE_MOVIE_ID));
        }else{
            contentUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                    .appendPath(MoviesContract.PATH_MOVIE)
                    .build();
        }

        return new CursorLoader(mContext,
                contentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mDelegate.processFinish(data,loader);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mDelegate.processFinish(null,loader);
    }
}
