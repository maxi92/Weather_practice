package com.example.eric.weather_practice;

/**
 * Created by Eric on 2016/5/24.
 */
public interface HttpCallBackListener {
    void onFinish(String response) ;
    void onError(Exception e);
}
