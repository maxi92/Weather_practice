package com.example.eric.weather_practice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eric on 2016/5/30.
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_WEATHER = "create table Weather (" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "city_weather text," +
            "time, " +
            "date," +
            "l_tmp," +
            "h_tmp" +
            ")";

    public WeatherOpenHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
