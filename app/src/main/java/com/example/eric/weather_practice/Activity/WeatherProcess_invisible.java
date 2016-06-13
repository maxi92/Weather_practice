package com.example.eric.weather_practice.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.eric.weather_practice.Data.Weather;
import com.example.eric.weather_practice.Data.WeatherArray;
import com.example.eric.weather_practice.Data.WeatherDB;
import com.example.eric.weather_practice.Tools.HttpCallBackListener;
import com.example.eric.weather_practice.R;
import com.example.eric.weather_practice.Tools.HttpUtil;
import com.example.eric.weather_practice.Tools.Utility;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/6.
 * 这是一个不显示的Activity，主要是作为一个中介的作用
 * 将上一个Activity传过来的citycode或者cityname用于查询天气
 * 将结果传递给下一个Activity，并且将结果存入数据库
 */
public class WeatherProcess_invisible extends AppCompatActivity {

    private ProgressDialog progressDialog;
    Weather w;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherprocess_invisible);
        int state = getIntent().getIntExtra("state",1);
        if(state == 1) {
            String citycode = getIntent().getStringExtra("citycode");
            QueryWeatherById(citycode, 1);
        }
        else {
            String cityname = getIntent().getStringExtra("cityname");
            QueryWeatherByname(cityname);
        }
    }

    private void QueryWeatherById(String citycode, final int state) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/recentweathers";
        String httpArg = "cityid=" + citycode;
        if(state == 1)
            showProgressDialog();
        HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                w = Utility.handleWeatherResponse(WeatherProcess_invisible.this, response);
                WeatherDB.getInstance(WeatherProcess_invisible.this).saveWeather(w);
                ArrayList<Weather> mWeathers = WeatherArray.getInstance(WeatherProcess_invisible.this).getArray();
                for(Weather w : mWeathers) {
                    WeatherDB.getInstance(WeatherProcess_invisible.this).saveWeather(w);
                }
                mWeathers.clear();
                mWeathers.addAll(WeatherDB.getInstance(WeatherProcess_invisible.this).loadWeather());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Intent intent = new Intent(WeatherProcess_invisible.this,
                                WeatherPagerActivity.class);
                        intent.putExtra("weather", w);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherProcess_invisible.this, "天气获取失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void QueryWeatherByname(String cityname) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
        String httpArg = "cityname=" + cityname;
        showProgressDialog();
        HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                w = Utility.handleWeatherResponse(WeatherProcess_invisible.this, response);
                WeatherDB.getInstance(WeatherProcess_invisible.this).saveWeather(w);
                ArrayList<Weather> mWeathers = WeatherArray.getInstance(WeatherProcess_invisible.this).getArray();
                for(Weather w:mWeathers) {
                    WeatherDB.getInstance(WeatherProcess_invisible.this).saveWeather(w);
                }
                mWeathers.clear();
                mWeathers.addAll(WeatherDB.getInstance(WeatherProcess_invisible.this).loadWeather());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Intent intent = new Intent(WeatherProcess_invisible.this,
                                WeatherPagerActivity.class);
                        intent.putExtra("weather", w);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherProcess_invisible.this, "天气获取失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        WeatherDB weatherDB = WeatherDB.getInstance(this);
        ArrayList<Weather> mWeathers = WeatherArray.getInstance(this).getArray();
        for(Weather w:mWeathers) {
            weatherDB.saveWeather(w);
        }
        super.onDestroy();
    }
}
