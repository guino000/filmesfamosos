package com.example.android.filmesfamosos.loaders;

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

public class ReviewCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private AsyncTaskDelegate<Cursor> mDelegate;
//    Args keys
    public static final String KEY_BUNDLE_MOVIE_ID = "movie_id";

    public ReviewCursorLoader(Context context, AsyncTaskDelegate<Cursor> delegate){
        mContext = context;
        mDelegate = delegate;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if(args != null) {
            Uri contentUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                    .appendPath(MoviesContract.PATH_MOVIE)
                    .appendPath(args.getString(KEY_BUNDLE_MOVIE_ID))
                    .appendPath(MoviesContract.PATH_REVIEW)
                    .build();

            return new CursorLoader(
                    mContext,
                    contentUri,
                    null,
                    null,
                    null,
                    null
            );
        }else{
            throw new IllegalArgumentException("Should have inserted movie ID in args Bundle.");
        }
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
