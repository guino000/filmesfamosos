package com.example.android.filmesfamosos.utilities;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.Title;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gui on 2/15/2018.
 */

public final class MovieJsonUtils {
    final String SORT_BY_POPULARITY = "popular";
    final String SORT_BY_TOP_RATED = "top_rated";

    public static ArrayList<Movie> getMoviesFromJson(String jsonString) throws JSONException {
        final String RESULTS = "results";
        final String ERROR_CODE = "cod";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_POSTER_PATH = "poster_path";
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
                            movieJsonObj.getString(MOVIE_TITLE),
                            movieJsonObj.getString(MOVIE_ORIGINAL_TITLE)
                    ),
                    POSTER_PATH_ROOT + movieJsonObj.getString(MOVIE_POSTER_PATH)
            ).build();
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }
}
