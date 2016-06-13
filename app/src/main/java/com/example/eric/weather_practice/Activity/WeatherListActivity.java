package com.example.eric.weather_practice.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.eric.weather_practice.Data.Weather;
import com.example.eric.weather_practice.Data.WeatherArray;
import com.example.eric.weather_practice.Data.WeatherDB;
import com.example.eric.weather_practice.R;

import java.util.ArrayList;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_list_toolbar);
        toolbar.setTitle("定制天气");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_list_toolbar,menu);
        return true;
    }

    @Override
    public void onDestroy() {
        WeatherDB weatherDB = WeatherDB.getInstance(this);
        ArrayList<Weather> mWeathers = WeatherArray.getInstance(this).getArray();
        for(Weather w:mWeathers) {
            weatherDB.saveWeather(w);
        }

        if(weatherDB.loadWeather().size() != 0) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isSelected",true);
            editor.apply();
        }
        else {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isSelected",false);
            editor.apply();
        }
        super.onDestroy();
    }


}
