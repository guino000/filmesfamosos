package com.example.android.filmesfamosos.utilities;

import android.database.Cursor;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.model.Votes;

public final class ModelUtils {
    public static Movie getMovieFromCursor(Cursor cursor){
        return new Movie.MovieBuilder(
                cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry._ID)),
                new Title(
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE))
                ),
                cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)))
                .setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)))
                .setFavorite(true)
                .setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)))
                .setVotes(new Votes(
                        cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT)),
                        cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                        cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY))
                ))
                .setReviews(null)
                .setTrailers(null)
                .build();
    }

    public static Review getReviewFromCursor(Cursor cursor){
        return new Review(
                cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry._ID)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_URL))
        );
    }

    public static Trailer getTrailerFromCursor(Cursor cursor){
        return new Trailer(
                cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry._ID)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_TRAILER_KEY)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_SITE)),
                cursor.getInt(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_SIZE))
        );
    }
}
