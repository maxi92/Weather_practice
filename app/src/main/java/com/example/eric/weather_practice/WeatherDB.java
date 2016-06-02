package com.example.eric.weather_practice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/5/30.
 */
public class WeatherDB {

    public static final String DB_NAME = "weather";
    public static final int VERSION = 1;
    private static WeatherDB weatherDB;
    private SQLiteDatabase db;

    private WeatherDB(Context context) {
        WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context) {
        if(weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public void saveWeather(Weather weather) {
        if(weather != null) {
            if(!isEmpty(weather)) {
                updateWeather(weather);
                return;
            }
            ContentValues values = new ContentValues();
            values.put("city_name", weather.getCity_name());
            values.put("city_code",weather.getCity_code());
            values.put("city_weather",weather.getWeather());
            db.insert("Weather", null, values);
        }
    }

    public ArrayList<Weather> loadWeather() {
        ArrayList<Weather> list = new ArrayList<>();
        Cursor cursor = db.query("Weather", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Weather weather = new Weather();
                weather.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                weather.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                weather.setWeather(cursor.getString(cursor.getColumnIndex("city_weather")));
                list.add(weather);
            } while(cursor.moveToNext());
        }
        return list;
    }

    public void deleteWeather(Weather weather) {
        db.delete("Weather","city_code = ?",new String[] {weather.getCity_code()});
    }

    private boolean isEmpty(Weather weather) {
        Cursor cursor = db.query("Weather",null,"city_code = ?",new String[] {weather.getCity_code()}, null, null, null);
        if(cursor.getCount() == 0) return true;
        return false;
    }

    private void updateWeather(Weather weather) {
        ContentValues values = new ContentValues();
        values.put("city_weather", weather.getWeather());
        db.update("Weather", values, "city_code = ?",new String[] {weather.getCity_code()});
    }
}
