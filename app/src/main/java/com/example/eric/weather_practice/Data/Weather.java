package com.example.eric.weather_practice.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Eric on 2016/5/30.
 */
public class Weather implements Serializable{

    private String city_name;
    private String weather;
    private String city_code;
    private String date;
    private String l_tmp;
    private String h_tmp;

    private ArrayList<FutureWeather> mfutureweathers;

    public Weather(String city_name, String weather, String city_code, String date,
                   String l_tmp, String h_tmp, ArrayList<FutureWeather> mfutureweathers) {
        this.city_name = city_name;
        this.weather = weather;
        this.city_code = city_code;
        this.date = date;
        this.l_tmp = l_tmp;
        this.h_tmp = h_tmp;
        this.mfutureweathers = new ArrayList<>(mfutureweathers);
    }

    public Weather() {

    }


    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getL_tmp() {
        return l_tmp;
    }

    public void setL_tmp(String l_tmp) {
        this.l_tmp = l_tmp;
    }

    public String getH_tmp() {
        return h_tmp;
    }

    public void setH_tmp(String h_tmp) {
        this.h_tmp = h_tmp;
    }

    public ArrayList<FutureWeather> getMfutureweathers() {
        return mfutureweathers;
    }

    public void setMfutureweathers(ArrayList<FutureWeather> mfutureweathers) {
        this.mfutureweathers = mfutureweathers;
    }


    @Override
    public String toString() {
        return city_name+"     "+weather;
    }
}
