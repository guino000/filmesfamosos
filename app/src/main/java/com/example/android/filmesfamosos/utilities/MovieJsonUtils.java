package com.example.android.filmesfamosos.utilities;

import com.example.android.filmesfamosos.model.Movie;
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
                    .build();
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }
}
