package com.example.android.filmesfamosos.utilities;

import com.example.android.filmesfamosos.model.Movie;
import com.example.android.filmesfamosos.model.Title;
import com.example.android.filmesfamosos.model.Votes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Gui on 2/15/2018.
 */

public final class MovieJsonUtils {
    public static ArrayList<Movie> getMoviesFromJson(String jsonString) throws JSONException {
        final String PAGE = "page";
        final String TOTAL_PAGES = "total_pages";
        final String RESULTS = "results";
        final String ERROR_CODE = "cod";
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

        long totalPages = moviesJson.getLong(TOTAL_PAGES);
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
                    )
                    .setOverview(movieJsonObj.getString(MOVIE_OVERVIEW))
                    .setVotes(new Votes(
                            movieJsonObj.getLong(MOVIE_VOTE_AVERAGE),
                            movieJsonObj.getDouble(MOVIE_VOTE_AVERAGE),
                            movieJsonObj.getDouble(MOVIE_POPULARITY)
                    ))
                    .setReleaseDate(movieJsonObj.getString(MOVIE_RELEASE_DATE))
                    .build();
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }
}
