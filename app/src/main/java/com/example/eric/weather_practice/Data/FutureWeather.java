package com.example.eric.weather_practice.Data;

import java.io.Serializable;

/**
 * Created by Eric on 2016/6/8.
 */
public class FutureWeather implements Serializable {

    private String week;
    private String hightemp;
    private String lowtemp;
    private String weather;

    public FutureWeather(String week, String lowtemp, String hightemp, String weather) {
        this.week = week;
        this.hightemp = hightemp;
        this.lowtemp = lowtemp;
        this.weather = weather;
    }

    public FutureWeather(String group) {
        String[] mGroups = group.split(" ");
        this.week = mGroups[0];
        this.lowtemp = mGroups[1];
        this.hightemp = mGroups[2];
        this.weather = mGroups[3];
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getHightemp() {
        return hightemp;
    }

    public void setHightemp(String hightemp) {
        this.hightemp = hightemp;
    }

    public String getLowtemp() {
        return lowtemp;
    }

    public void setLowtemp(String lowtemp) {
        this.lowtemp = lowtemp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return week+" "+lowtemp+" "+hightemp+" "+weather;
    }

}
