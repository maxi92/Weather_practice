package com.example.eric.weather_practice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Eric on 2016/5/26.
 */
public class WeatherActivity extends AppCompatActivity {

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private Button weatherList;
    private ProgressDialog progressDialog;
    private String citycode;
    private String cityname;

    public static AppCompatActivity weatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_layout);
        weatherActivity = this;

        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.pulish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);

        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        weatherList = (Button) findViewById(R.id.weather_list);

        int state = getIntent().getIntExtra("state",1);
        Log.d("MainActivity","state is "+state+"");
        if(state == 1) {
            citycode = getIntent().getStringExtra("citycode");
            Log.d("MainActivity","citycode2 is "+citycode);
            QueryWeatherById(citycode, 1);
        }
        else {
            cityname = getIntent().getStringExtra("cityname");
            QueryWeatherByname(cityname);
        }

        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                prefs.putBoolean("isSelected",false);
                prefs.apply();
                Intent intent  = new Intent(WeatherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中...");
                QueryWeatherById(citycode, 2);
            }
        });

        weatherList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, WeatherListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void QueryWeatherById(String citycode, final int state) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
        String httpArg = "cityid=" + citycode;
        if(state == 1)
            showProgressDialog();
        HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        showWeather();
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
                        Toast.makeText(WeatherActivity.this, "天气获取失败", Toast.LENGTH_SHORT).show();
                        if(state == 2) {
                            showWeather();
                        }
                    }
                });
            }
        });
    }

    private void QueryWeatherByname(String name) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
        String httpArg = "cityname=" + cityname;
        showProgressDialog();
        HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this, response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        showWeather();
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
                        Toast.makeText(WeatherActivity.this, "天气获取失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("name", ""));
        temp1Text.setText(prefs.getString("l_tmp", ""));
        temp2Text.setText(prefs.getString("h_tmp", ""));
        weatherDespText.setText(prefs.getString("weather", ""));
        publishText.setText("今天" + prefs.getString("time", "") + "发布");
        currentDateText.setText(prefs.getString("date", ""));
        cityNameText.setVisibility(View.VISIBLE);
        String weather = prefs.getString("weather","");
        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.relative_layout2);

        if(weather.contains("晴")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_sunny);
        }
        else if(weather.contains("雨")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_rainy);
        }
        else if(weather.contains("多云") || weather.contains("阴")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_cloudy);
        }
        else if(weather.contains("雪")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_snowy);
        }

        WeatherDB.getInstance(this).saveWeather(new Weather(
                prefs.getString("name",""),
                prefs.getString("weather",""),
                prefs.getString("citycode","")));

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
