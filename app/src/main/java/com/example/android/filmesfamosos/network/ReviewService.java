package com.example.android.filmesfamosos.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.filmesfamosos.R;
import com.example.android.filmesfamosos.interfaces.AsyncTaskDelegate;
import com.example.android.filmesfamosos.model.Review;
import com.example.android.filmesfamosos.utilities.MovieJsonUtils;
import com.example.android.filmesfamosos.utilities.NetworkUtils;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewService implements LoaderManager.LoaderCallbacks<List<Review>>{
//    Keys for themoviedb api URL parameters
    private static final String KEY_APIKEY = "api_key";
    private static final String KEY_PAGE = "page";
//    Keys for the args bundle
    private static final String KEY_BUNDLE_MOVIE_ID = "movie_id";
    private static final String KEY_BUNDLE_PAGE = "review_page";
//    Member variables
    private AsyncTaskDelegate<List<Review>> mReviewDelegate;
    private Context mContext;

    public ReviewService(AsyncTaskDelegate<List<Review>> delegate, Context context){
        mReviewDelegate = delegate;
        mContext = context;
    }

    public URL buildURL(String movieID, String page){
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("reviews")
                .appendQueryParameter(KEY_APIKEY, mContext.getString(R.string.themoviedb))
                .appendQueryParameter(KEY_PAGE, page);

        try{
            return new URL(builder.build().toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<List<Review>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<List<Review>>(mContext) {
            private List<Review> mCachedReviews = new ArrayList<>();

            @Override
            protected void onStartLoading() {
//                Check if there are args
                if(args == null)
                    return;

//                Check if there is cached data
                if(!mCachedReviews.isEmpty()){
                    deliverResult(mCachedReviews);
                }else{
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Review> loadInBackground() {
//                Try to get the movie ID and reviews page from the args bundle
                String movieID = args.getString(KEY_BUNDLE_MOVIE_ID);
                String reviewsPage = args.getString(KEY_BUNDLE_PAGE);

                URL reviewRequestURL = buildURL(movieID, reviewsPage);
                try {
                    String jsonResponse = NetworkService.getResponseFromHttpUrl(reviewRequestURL);
                    return MovieJsonUtils.getReviewsFromJson(jsonResponse);
                }catch (Exception e){
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            public void deliverResult(@Nullable List<Review> data) {
//                TODO: Implement the deliverResult function
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
//    TODO: Implement onLoadFinished Function
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Review>> loader){
    }
}
