package com.example.android.filmesfamosos.utilities;

import android.content.ContentValues;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.MoviesContract.*;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.model.Trailer;
import com.example.android.filmesfamosos.model.Votes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Guilherme Canalli on 2/15/2018.
 */

public final class MovieJsonUtils {
    public static ArrayList<Review> getReviewsFromJson(String jsonString) throws JSONException{
        final String REVIEW_ID = "id";
        final String AUTHOR_ID = "author";
        final String CONTENT_ID = "content";
        final String URL_ID = "url";
        final String RESULTS = "results";
        final String ERROR_CODE = "status_code";

        ArrayList<Review> parsedReviews = new ArrayList<>();
        JSONObject jsonReviews = new JSONObject(jsonString);

//        Check if has errors
        if(jsonReviews.has(ERROR_CODE)) {
            int errorCode = jsonReviews.getInt(ERROR_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

//       Loop json and return review objects
        JSONArray jsonArray = jsonReviews.getJSONArray(RESULTS);
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject reviewJsonObj = jsonArray.getJSONObject(i);
            Review parsedReview = new Review(
                    reviewJsonObj.optString(REVIEW_ID),
                    reviewJsonObj.optString(AUTHOR_ID),
                    reviewJsonObj.optString(CONTENT_ID),
                    reviewJsonObj.optString(URL_ID)
            );
            parsedReviews.add(parsedReview);
        }

        return parsedReviews;
    }

    public static ContentValues[] getReviewsContentValues(Review[] reviews, String movieID){
        ContentValues[] reviewValues = new ContentValues[reviews.length];
        for (int i = 0; i < reviews.length; i++) {
            ContentValues contentValues = getReviewContentValues(reviews[i], movieID);
            reviewValues[i] = contentValues;
        }

        return reviewValues;
    }

    public static ContentValues getReviewContentValues(Review review, String movieID){
        ContentValues reviewEntryValues = new ContentValues();
        reviewEntryValues.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
        reviewEntryValues.put(ReviewEntry.COLUMN_CONTENT, review.getReviewContent());
        reviewEntryValues.put(ReviewEntry.COLUMN_URL, review.getUrl());
        reviewEntryValues.put(ReviewEntry.COLUMN_FK_MOVIE_ID, movieID);
        return reviewEntryValues;
    }

    public static ArrayList<Trailer> getTrailersFromJson(String jsonString) throws JSONException{
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";
        final String RESULTS = "results";
        final String ERROR_CODE = "status_code";

        ArrayList<Trailer> parsedTrailers = new ArrayList<>();
        JSONObject jsonTrailers = new JSONObject(jsonString);

//        Check if json has errors
        if(jsonTrailers.has(ERROR_CODE)){
            int errorCode = jsonTrailers.getInt(ERROR_CODE);

            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonArray = jsonTrailers.getJSONArray(RESULTS);
        for(int i = 0; i < jsonArray.length() ; i++){
            JSONObject trailerJsonObj = jsonArray.getJSONObject(i);
//            Add parsed trailer object in the array if it's a trailer
            if(trailerJsonObj.optString(TRAILER_TYPE).equals("Trailer")) {
                parsedTrailers.add(new Trailer(
                        trailerJsonObj.optString(TRAILER_ID),
                        trailerJsonObj.optString(TRAILER_KEY),
                        trailerJsonObj.optString(TRAILER_NAME),
                        trailerJsonObj.optString(TRAILER_SITE),
                        trailerJsonObj.optInt(TRAILER_SIZE)
                ));
            }
        }

        return parsedTrailers;

    }

    public static ContentValues[] getTrailersContentValues(Trailer[] trailers, String movieID){
        ContentValues[] trailerValues = new ContentValues[trailers.length];
        for (int i = 0; i < trailers.length; i++) {
            ContentValues contentValues = getTrailerContentValues(trailers[i], movieID);
            trailerValues[i] = contentValues;
        }

        return trailerValues;
    }

    public static ContentValues getTrailerContentValues(Trailer trailer, String movieID){
        ContentValues trailerEntryValues = new ContentValues();
        trailerEntryValues.put(TrailerEntry.COLUMN_NAME, trailer.getName());
        trailerEntryValues.put(TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
        trailerEntryValues.put(TrailerEntry.COLUMN_SITE, trailer.getSite());
        trailerEntryValues.put(TrailerEntry.COLUMN_SIZE, trailer.getSize());
        trailerEntryValues.put(TrailerEntry.COLUMN_FK_MOVIE_ID, movieID);
        return trailerEntryValues;
    }

    public static ArrayList<Movie> getMoviesFromJson(String jsonString) throws JSONException {
        final String RESULTS = "results";
        final String ERROR_CODE = "status_code";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_POPULARITY = "popularity";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String POSTER_PATH_ROOT = "http://image.tmdb.org/t/p/w185/";

        ArrayList<Movie> parsedMovies = new ArrayList<>();
        JSONObject moviesJson = new JSONObject(jsonString);

        if(moviesJson.has(ERROR_CODE)){
            int errorCode = moviesJson.getInt(ERROR_CODE);

            switch (errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        for(int i = 0; i < moviesArray.length(); i++){
            JSONObject movieJsonObj = moviesArray.getJSONObject(i);
            Movie movie = new Movie.MovieBuilder(
                    movieJsonObj.getLong(MOVIE_ID),
                    new Title(
                            movieJsonObj.optString(MOVIE_TITLE),
                            movieJsonObj.optString(MOVIE_ORIGINAL_TITLE)
                    ),
                    POSTER_PATH_ROOT + movieJsonObj.optString(MOVIE_POSTER_PATH)
                    )
                    .setOverview(movieJsonObj.optString(MOVIE_OVERVIEW))
                    .setVotes(new Votes(
                            movieJsonObj.getLong(MOVIE_VOTE_AVERAGE),
                            movieJsonObj.getDouble(MOVIE_VOTE_AVERAGE),
                            movieJsonObj.getDouble(MOVIE_POPULARITY)
                    ))
                    .setReleaseDate(movieJsonObj.optString(MOVIE_RELEASE_DATE))
                    .setFavorite(false)
                    .build();
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }

    public static ContentValues[] getMoviesContentValues(Movie[] movies){
        ContentValues[] movieValues = new ContentValues[movies.length];
        for (int i = 0; i < movies.length; i++) {
            ContentValues contentValues = getMovieContentValues(movies[i]);
            movieValues[i] = contentValues;
        }

        return movieValues;
    }

    public static ContentValues getMovieContentValues(Movie movie){
        ContentValues movieEntryValues = new ContentValues();
        movieEntryValues.put(MovieEntry._ID, movie.getId());
        movieEntryValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getTitle().getOriginalTitle());
        movieEntryValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle().getTitle());
        movieEntryValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieEntryValues.put(MovieEntry.COLUMN_POPULARITY, movie.getVotes().getPopularity());
        movieEntryValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVotes().getVoteAverage());
        movieEntryValues.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVotes().getVoteCount());
        movieEntryValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieEntryValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        return movieEntryValues;
    }
}
