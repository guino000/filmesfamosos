package com.example.android.filmesfamosos.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract;
import com.example.android.filmesfamosos.model.MoviesDbHelper;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Trailer;

import java.util.ArrayList;

public final class MovieDatabaseUtils {
    public static long insertMovie(Movie movie, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
//        Create movie content values
        ContentValues movieValues = MovieJsonUtils.getMovieContentValues(movie);
//        Insert movie into database
        long itemID = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, movieValues);
//        If movie insertion was successful, insert reviews and trailers of that movie
        if(itemID > 0){
            bulkInsertReviews(movie.getReviews(), String.valueOf(itemID),context);
            bulkInsertTrailers(movie.getTrailers(), String.valueOf(itemID), context);
            return itemID;
        }else{
            return -1;
        }
    }

    private static void bulkInsertReviews(Review[] reviews, String movieID, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
//        Create reviews content values array
        ContentValues[] reviewValues = MovieJsonUtils.getReviewsContentValues(reviews,movieID);
//        Bulk insert reviews into database
        db.beginTransaction();
        try{
            for(ContentValues contentValues : reviewValues){
                db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private static void bulkInsertTrailers(Trailer[] trailers, String movieID, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
//        Create trailers content values array
        ContentValues[] trailerValues = MovieJsonUtils.getTrailersContentValues(trailers, movieID);
//        Bulk insert trailers into database
        db.beginTransaction();
        try{
            for(ContentValues contentValues : trailerValues){
                db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

        private static void RemoveMovie(Movie movie, Context context){
            SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            db.setForeignKeyConstraintsEnabled(true);
            db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID,
                    new String[]{String.valueOf(movie.getId())});
    }
}
