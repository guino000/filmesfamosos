package com.example.android.filmesfamosos.services;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

abstract class NetworkService {
    static String getResponseFromHttpUrl(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if(response.body() != null) {
            return response.body().string();
        }else{
            return "";
        }
    }
}
