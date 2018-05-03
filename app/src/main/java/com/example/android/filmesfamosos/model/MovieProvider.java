package com.example.android.filmesfamosos.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID = 101;
    public static final int CODE_MOVIE_TRAILERS = 102;
    public static final int CODE_MOVIE_REVIEWS = 103;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mMovieHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,MoviesContract.PATH_MOVIE,CODE_MOVIES);
        matcher.addURI(authority,MoviesContract.PATH_MOVIE + "/#", CODE_MOVIES_WITH_ID);
        matcher.addURI(authority,MoviesContract.PATH_MOVIE + "/#/" + MoviesContract.PATH_TRAILER, CODE_MOVIE_TRAILERS);
        matcher.addURI(authority,MoviesContract.PATH_MOVIE + "/#/" + MoviesContract.PATH_REVIEW, CODE_MOVIE_REVIEWS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieHelper = new MoviesDbHelper((getContext()));
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        List<String> pathSegments;
        String movieID;
        SQLiteDatabase db = mMovieHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIES_WITH_ID:
                movieID = uri.getLastPathSegment();
                cursor = db.query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{movieID},
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_TRAILERS:
                pathSegments = uri.getPathSegments();
                movieID = pathSegments.get(pathSegments.size() - 2);
                cursor = db.query(
                        MoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MoviesContract.TrailerEntry.COLUMN_FK_MOVIE_ID + " = ?",
                        new String[]{movieID},
                        null,
                        null,
                        null);
                break;
            case CODE_MOVIE_REVIEWS:
                pathSegments = uri.getPathSegments();
                movieID = pathSegments.get(pathSegments.size() - 2);
                cursor = db.query(
                        MoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MoviesContract.ReviewEntry.COLUMN_FK_MOVIE_ID + " = ?",
                        new String[]{movieID},
                        null,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                id = db.insert(MoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        values);
                if(id != -1)
                    getContext().getContentResolver().notifyChange(uri,null);

                return MoviesContract.MovieEntry.buildMovieUriWithId(String.valueOf(id));
            case CODE_MOVIE_REVIEWS:
                id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME,
                        null,
                        values);
                if(id != -1)
                    getContext().getContentResolver().notifyChange(uri,null);

                return MoviesContract.ReviewEntry.buildReviewUriWithId(uri, String.valueOf(id));
            case CODE_MOVIE_TRAILERS:
                id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME,
                        null,
                        values);
                if(id != -1)
                    getContext().getContentResolver().notifyChange(uri,null);

                return MoviesContract.TrailerEntry.buildTrailerUriWithId(uri, String.valueOf(id));
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedCount;
        SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                deletedCount = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_MOVIES_WITH_ID:
                String movieID = uri.getLastPathSegment();
                deletedCount = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry._ID + " = ?",
                        new String[]{movieID});
                break;
            default:
                return 0;
        }

        if(deletedCount > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
