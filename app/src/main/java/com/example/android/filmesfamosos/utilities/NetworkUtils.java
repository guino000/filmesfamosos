package com.example.android.filmesfamosos.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static URL buildURL(String sorting, String page, String apiKey) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sorting)
                .appendQueryParameter("page", page)
                .appendQueryParameter("api_key",apiKey);
        try {
            return new URL(builder.build().toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setConnectTimeout(5000);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
