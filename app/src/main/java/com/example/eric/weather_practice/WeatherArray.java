package com.example.eric.weather_practice;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/7.
 */
public class WeatherArray {

    private ArrayList<Weather> mWeathers;
    private static WeatherArray weatherArray;

    private WeatherArray(Context context) {
        mWeathers = WeatherDB.getInstance(context).loadWeather();
    }

    public synchronized static WeatherArray getInstance(Context context) {
        if(weatherArray == null) {
            weatherArray = new WeatherArray(context);
        }
        return weatherArray;
    }

    public ArrayList<Weather> getArray() {
        return mWeathers;
    }

}
