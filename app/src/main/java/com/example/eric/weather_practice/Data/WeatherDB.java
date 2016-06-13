package com.example.eric.weather_practice.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/5/30.
 * 数据库的操作类
 * 因为city_code是唯一的，我都是用city_code作为判断两行是否相同的标准
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
            values.put("date",weather.getDate());
            values.put("l_tmp",weather.getL_tmp());
            values.put("h_tmp",weather.getH_tmp());
            values.put("future1",weather.getMfutureweathers().get(0).toString());
            values.put("future2",weather.getMfutureweathers().get(1).toString());
            values.put("future3",weather.getMfutureweathers().get(2).toString());
            values.put("future4",weather.getMfutureweathers().get(3).toString());
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
                weather.setDate(cursor.getString(cursor.getColumnIndex("date")));
                weather.setL_tmp(cursor.getString(cursor.getColumnIndex("l_tmp")));
                weather.setH_tmp(cursor.getString(cursor.getColumnIndex("h_tmp")));
                ArrayList<FutureWeather> mFutureWeathers = new ArrayList<>();
                mFutureWeathers.add(0,new FutureWeather(cursor.getString(cursor.getColumnIndex("future1"))));
                mFutureWeathers.add(1,new FutureWeather(cursor.getString(cursor.getColumnIndex("future2"))));
                mFutureWeathers.add(2,new FutureWeather(cursor.getString(cursor.getColumnIndex("future3"))));
                mFutureWeathers.add(3,new FutureWeather(cursor.getString(cursor.getColumnIndex("future4"))));
                weather.setMfutureweathers(mFutureWeathers);
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
        values.put("city_name", weather.getCity_name());
        values.put("date",weather.getDate());
        values.put("l_tmp",weather.getL_tmp());
        values.put("h_tmp",weather.getH_tmp());
        values.put("future1",weather.getMfutureweathers().get(0).toString());
        values.put("future2",weather.getMfutureweathers().get(1).toString());
        values.put("future3",weather.getMfutureweathers().get(2).toString());
        values.put("future4",weather.getMfutureweathers().get(3).toString());
        db.update("Weather", values, "city_code = ?",new String[] {weather.getCity_code()});
    }
}
