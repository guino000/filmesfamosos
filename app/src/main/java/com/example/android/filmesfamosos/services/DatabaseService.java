package com.example.android.filmesfamosos.services;

import android.content.Context;
import android.database.Cursor;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.MoviesContract.*;

public final class DatabaseService {
    public static boolean checkIsFavoriteMovie(Movie movie, Context context){
//        Select only the API_ID column to make the query faster
        String[] columns = {MovieEntry._ID};

        try(Cursor retCursor = context.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUriWithId(String.valueOf(movie.getId())),
                columns,
                null,
                null,
                null)) {
            return ((retCursor != null) && (retCursor.getCount() > 0));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
