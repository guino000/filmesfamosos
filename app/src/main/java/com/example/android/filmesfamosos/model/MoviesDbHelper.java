package com.example.android.filmesfamosos.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.filmesfamosos.model.MoviesContract.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_API_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                " UNIQUE (" + MovieEntry.COLUMN_TITLE + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEWS_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_URL + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_FK_MOVIE_ID + " INTEGER NOT NULL," +
                " UNIQUE (" + ReviewEntry.COLUMN_URL + ") ON CONFLICT REPLACE, " +
                "CONSTRAINT " + ReviewEntry.COLUMN_FK_MOVIE_ID +
                " FOREIGN KEY (" + ReviewEntry.COLUMN_FK_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")" +
                " ON DELETE CASCADE);";

        final String SQL_CREATE_TRAILERS_TABLE =
                "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_SITE + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_FK_MOVIE_ID + " INTEGER NOT NULL," +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_KEY + ") ON CONFLICT REPLACE," +
                "CONSTRAINT " + TrailerEntry.COLUMN_FK_MOVIE_ID +
                " FOREIGN KEY (" + TrailerEntry.COLUMN_FK_MOVIE_ID + ")" +
                " REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ")" +
                " ON DELETE CASCADE);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
