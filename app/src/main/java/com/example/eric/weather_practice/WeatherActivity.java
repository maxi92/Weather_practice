package com.example.eric.weather_practice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;


/**
 * Created by Eric on 2016/5/26.
 */
public class WeatherActivity extends AppCompatActivity {

    public static AppCompatActivity weatherActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        weatherActivity = this;
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.weatherfragmentContainer);

        if(fragment == null) {
            fragment = new WeatherFragment();
            manager.beginTransaction()
                    .add(R.id.weatherfragmentContainer, fragment)
                    .commit();
        }
    }
}

