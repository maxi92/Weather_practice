package com.example.eric.weather_practice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Eric on 2016/5/31.
 */
public class WeatherListActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.weather_list_container);

        if(fragment == null) {
            fragment = new WeatherListFragment();
            manager.beginTransaction()
                    .add(R.id.weather_list_container, fragment)
                    .commit();
        }
    }
}
