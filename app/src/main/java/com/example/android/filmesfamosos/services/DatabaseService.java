package com.example.android.filmesfamosos.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.MoviesContract.*;
import com.example.android.filmesfamosos.model.MoviesDbHelper;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.model.Votes;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;

import java.util.ArrayList;

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
