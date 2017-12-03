package com.coolweather.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.litepal.LitePal;

/**
 * Created by wentaodeng on 2017/12/3.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "onCreate:context is empty " + (context==null));
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
