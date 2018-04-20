package com.example.android.filmesfamosos.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract.*;
import com.example.android.filmesfamosos.model.MoviesDbHelper;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.model.Votes;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;

import java.util.ArrayList;

public final class DatabaseService {
    public static Movie[] queryMovies(Context context){
//        Query all movie data from the database
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        try (SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
             Cursor cursor = db.query(MovieEntry.TABLE_NAME,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null);){
//              Loop the cursor and create a movies array
                cursor.moveToFirst();
                Movie[] queryMovies = new Movie[cursor.getCount()];
                for(int i = 0; i < cursor.getCount(); i++){
//                    Query reviews and trailers related to the current movie
                    long movieID = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
                    Review[] movieReviews = queryReviews(context, String.valueOf(movieID));
                    Trailer[] movieTrailers = queryTrailers(context, String.valueOf(movieID));
//                    Build a movie object based on cursor data and add it to the array
                    Movie movie = new Movie.MovieBuilder(
                            cursor.getLong(cursor.getColumnIndex(MovieEntry._ID)),
                            new Title(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)),
                                    cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE))),
                            cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)))
                            .setFavorite(true)
                            .setOverview(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)))
                            .setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)))
                            .setVotes(new Votes(
                                    cursor.getLong(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT)),
                                    cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)),
                                    cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY))))
                            .setReviews(movieReviews)
                            .setTrailers(movieTrailers)
                            .build();
                    queryMovies[i] = movie;
                    cursor.moveToNext();
                }

                return queryMovies;
        }catch (Exception e){
            e.printStackTrace();
            return new Movie[0];
        }
    }

    public static Review[] queryReviews(Context context, String movieID){
//        Query database and get reviews related to a movie
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        String selection = ReviewEntry.COLUMN_FK_MOVIE_ID + " = ?";
        String[] selectionArgs = {movieID};
        try(SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
            Cursor cursor = db.query(ReviewEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)){
//            loop cursor and build review objects based on cursor data
                Review[] queryReviews = new Review[cursor.getCount()];
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++){
                    Review review = new Review(
                            cursor.getString(cursor.getColumnIndex(ReviewEntry._ID)),
                            cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR)),
                            cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT)),
                            cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_URL)));
                    queryReviews[i] = review;
                    cursor.moveToNext();
                }

                return queryReviews;
        }catch (Exception e){
            e.printStackTrace();
            return new Review[0];
        }
    }

    public static Trailer[] queryTrailers(Context context, String movieID){
//        Query database and get trailers related to a movie
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        String selection = TrailerEntry.COLUMN_FK_MOVIE_ID + " = ?";
        String[] selectionArgs = {movieID};
        try(SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
            Cursor cursor = db.query(TrailerEntry.TABLE_NAME,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)){
//            Loop cursor and create trailer objects based on cursor data
            Trailer[] queryTrailers = new Trailer[cursor.getCount()];
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                Trailer trailer = new Trailer(
                        cursor.getString(cursor.getColumnIndex(TrailerEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_TRAILER_KEY)),
                        cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_SITE)),
                        cursor.getInt(cursor.getColumnIndex(TrailerEntry.COLUMN_SITE)));
                queryTrailers[i] = trailer;
                cursor.moveToNext();
            }

            return queryTrailers;
        }catch (Exception e){
            e.printStackTrace();
            return new Trailer[0];
        }
    }

    public static long insertMovie(Movie movie, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        try(SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase()) {
//        Create movie content values
            ContentValues movieValues = MovieJsonUtils.getMovieContentValues(movie);
//        Insert movie into database
            return db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        }
    }

    public static boolean bulkInsertReviews(Review[] reviews, String movieID, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
//        Create reviews content values array
        ContentValues[] reviewValues = MovieJsonUtils.getReviewsContentValues(reviews,movieID);
//        Bulk insert reviews into database
        db.beginTransaction();
        try{
            for(ContentValues contentValues : reviewValues){
                db.insert(ReviewEntry.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public static boolean bulkInsertTrailers(Trailer[] trailers, String movieID, Context context){
//        Get database from helper
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
//        Create trailers content values array
        ContentValues[] trailerValues = MovieJsonUtils.getTrailersContentValues(trailers, movieID);
//        Bulk insert trailers into database
        db.beginTransaction();
        try{
            for(ContentValues contentValues : trailerValues){
                db.insert(TrailerEntry.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public static int deleteMovie(Movie movie, Context context){
        SQLiteOpenHelper sqLiteOpenHelper = new MoviesDbHelper(context);
        try(SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();) {
            db.setForeignKeyConstraintsEnabled(true);
            return db.delete(MovieEntry.TABLE_NAME,
                    MovieEntry._ID + " = ?",
                    new String[]{String.valueOf(movie.getId())});
        }
    }

    public static boolean checkIsFavoriteMovie(Movie movie, Context context){
//        Query the database to see if the movie is there
        SQLiteOpenHelper openHelper = new MoviesDbHelper(context);
//        Select only the API_ID column to make the query faster
        String[] columns = {MovieEntry._ID};
        String selection = MovieEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(movie.getId())};
        try (SQLiteDatabase db = openHelper.getReadableDatabase();
             Cursor retCursor = db.query(MovieEntry.TABLE_NAME,
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
