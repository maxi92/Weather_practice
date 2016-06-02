package com.example.eric.weather_practice;

/**
 * Created by Eric on 2016/5/30.
 */
public class Weather {

    private String city_name;
    private String weather;
    private String city_code;

    public Weather() {
        this.city_name = null;
        this.city_code = null;
        this.weather = null;
    }

    public Weather(String city_cn, String weather, String city_code) {
        this.city_name = city_cn;
        this.weather = weather;
        this.city_code = city_code;
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

    @Override
    public String toString() {
        return city_name+"     "+weather;
    }
}
