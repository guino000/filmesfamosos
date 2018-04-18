package com.example.android.filmesfamosos.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.MoviesDbHelper;

public final class MovieUtils {
    public static boolean checkIsFavoriteMovie(Movie movie, Context context){
//        Query the database to see if the movie is there
        SQLiteOpenHelper openHelper = new MoviesDbHelper(context);
//        Select only the API_ID column to make the query faster
        String[] columns = {MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID};
        String selection = MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID + " = ?";
        String[] selectionArgs = {String.valueOf(movie.getId())};
        try (SQLiteDatabase db = openHelper.getReadableDatabase();
                Cursor retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null)) {
//            Return true if cursor has data or false otherwise
            return ((retCursor != null) && (retCursor.getCount() > 0));
        }
    }
}
