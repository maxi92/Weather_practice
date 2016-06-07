package com.example.eric.weather_practice;

import java.io.Serializable;

/**
 * Created by Eric on 2016/5/30.
 */
public class Weather implements Serializable{

    private String city_name;
    private String weather;
    private String city_code;
    private String time;
    private String date;



    private String l_tmp;
    private String h_tmp;

    public Weather(String city_name, String weather, String city_code, String time, String date,
                   String l_tmp, String h_tmp) {
        this.city_name = city_name;
        this.weather = weather;
        this.city_code = city_code;
        this.date = date;
        this.time = time;
        this.l_tmp = l_tmp;
        this.h_tmp = h_tmp;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    @Override
    public String toString() {
        return city_name+"     "+weather;
    }
}
