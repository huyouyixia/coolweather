package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainActivity extends BaseActivity {

    private final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedIntanceState);
        Log.d(TAG, "onCreate: 启动onCreate");
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("weather",null)!= null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }

    }

}
