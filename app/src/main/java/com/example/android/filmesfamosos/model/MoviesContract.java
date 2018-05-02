package com.example.android.filmesfamosos.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.filmesfamosos";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_API_ID = "api_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";

        public static Uri buildMovieUriWithId(String id){
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(id)
                    .build();
        }
    }

    public static final class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_FK_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUriWithId(Uri reviewURI,String id){
            return Uri.withAppendedPath(reviewURI,id);
        }
    }

    public static final class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_FK_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_KEY = "trailer_key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";

        public static Uri buildTrailerUriWithId(Uri trailerURI, String id){
            return Uri.withAppendedPath(trailerURI,id);
        }
    }
}
