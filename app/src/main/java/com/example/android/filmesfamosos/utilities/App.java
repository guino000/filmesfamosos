package com.example.android.filmesfamosos.utilities;

import android.app.Application;
import android.content.Context;

/***************************************************************************************
 *    Title: App.java
 *    Author: Cristian
 *    Date: Dec 8, 2010
 *    Code version: 1.0
 *    Availability: https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context/4391811#4391811
 ***************************************************************************************/

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
