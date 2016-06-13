package com.example.eric.weather_practice.Tools;

/**
 * Created by Eric on 2016/5/24.
 */
public interface HttpCallBackListener {
    void onFinish(String response) ;
    void onError(Exception e);
}
