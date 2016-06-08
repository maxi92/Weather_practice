package com.example.eric.weather_practice;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/2.
 */
public class WeatherFragment extends Fragment {

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private Button weatherList;
    private View v;
    private Weather w;

    public static WeatherFragment newInstance(Weather w) {
        Bundle args = new Bundle();
        args.putSerializable("weather", w);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_weather, parent, false);

        cityNameText = (TextView) v.findViewById(R.id.city_name);
        publishText = (TextView) v.findViewById(R.id.publish_text);
        weatherDespText = (TextView) v.findViewById(R.id.weather_desp);
        temp1Text = (TextView) v.findViewById(R.id.temp1);
        temp2Text = (TextView) v.findViewById(R.id.temp2);
        currentDateText = (TextView) v.findViewById(R.id.current_date);

        switchCity = (Button) v.findViewById(R.id.switch_city);
        refreshWeather = (Button) v.findViewById(R.id.refresh_weather);
        weatherList = (Button) v.findViewById(R.id.weather_list);

        w = (Weather) getArguments().getSerializable("weather");
        Utility.saveWeatherInfo(getActivity(),w);
        showWeather();

        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                prefs.putBoolean("isSelected",false);
                prefs.apply();
                Intent intent  = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中...");
                refresh(w.getCity_code());

            }
        });

        weatherList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeatherListActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }


    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cityNameText.setText(prefs.getString("name", ""));
        temp1Text.setText(prefs.getString("l_tmp", ""));
        temp2Text.setText(prefs.getString("h_tmp", ""));
        weatherDespText.setText(prefs.getString("weather", ""));
        publishText.setText("今天" + prefs.getString("time", "") + "发布");
        currentDateText.setText(prefs.getString("date", ""));
        cityNameText.setVisibility(View.VISIBLE);
        String weather = prefs.getString("weather","");
        RelativeLayout relativeLayout2 = (RelativeLayout) v.findViewById(R.id.relative_layout2);



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

    }

    public void refresh(String citycode) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityid";
        String httpArg = "cityid=" + citycode;
        HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                w = Utility.handleWeatherResponse(getActivity(), response);
                WeatherDB.getInstance(getActivity()).saveWeather(w);
                ArrayList<Weather> mWeathers = WeatherArray.getInstance(getActivity()).getArray();
                for(Weather w:mWeathers) {
                    WeatherDB.getInstance(getActivity()).saveWeather(w);
                }
                mWeathers.clear();
                mWeathers.addAll(WeatherDB.getInstance(getActivity()).loadWeather());

                Utility.saveWeatherInfo(getActivity(),w);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "同步失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
