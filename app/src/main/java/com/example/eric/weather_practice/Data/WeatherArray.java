package com.example.eric.weather_practice.Data;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/7.
 * 设置该类的原因是，我发现如果一直都是从数据库中读取数据会影响性能
 * 同时向Viewpager的getcount这种每秒会被调用几十次的方法，如果一直从数据库中读取还可能会出错
 * 所以就设置这样一个单例类
 * 从创建的时候从数据库中读取内容，之后就不再读取，程序运行过程中所有操作都只针对该ArrayList
 * 在onDestroy方法中再将其存回数据库
 * 这样就能在保证性能的情况下也保证数据的更新
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
