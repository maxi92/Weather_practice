package com.example.eric.weather_practice.Activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
 * Created by Eric on 2016/6/2.
 */
public class WeatherFragment extends Fragment {

    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private LinearLayout future_weather;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_weather, parent, false);

        publishText = (TextView) v.findViewById(R.id.publish_text);
        weatherDespText = (TextView) v.findViewById(R.id.weather_desp);
        temp1Text = (TextView) v.findViewById(R.id.temp1);
        temp2Text = (TextView) v.findViewById(R.id.temp2);
        currentDateText = (TextView) v.findViewById(R.id.current_date);
        future_weather = (LinearLayout) v.findViewById(R.id.future_weather);

        w = (Weather) getArguments().getSerializable("weather");
        Utility.saveWeatherInfo(getActivity(),w);
        showWeather();

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.switch_city: {
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                prefs.putBoolean("isSelected",false);
                prefs.apply();
                Intent intent  = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.refresh_weather: {
                publishText.setText("同步中...");
                refresh(w.getCity_code());
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    /**
     * 显示天气的方法
     * 从SharedPreference中读取天气信息
     * 改变UI
     * 改变通知栏内容
     * 设置对应背景
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        temp1Text.setText(prefs.getString("l_tmp", ""));
        temp2Text.setText(prefs.getString("h_tmp", ""));
        publishText.setText("");
        weatherDespText.setText(prefs.getString("weather", ""));
        currentDateText.setText(prefs.getString("date", ""));
        String weather = prefs.getString("weather","");
        RelativeLayout relativeLayout2 = (RelativeLayout) v.findViewById(R.id.relative_layout2);
        LinearLayout future_weather = (LinearLayout) relativeLayout2
                .findViewById(R.id.future_weather);

        for(int i = 1; i <=4; i++) {
            String group = prefs.getString("future"+i,"");
            String[] mGoups = group.split(" ");
            ((TextView) ((RelativeLayout) future_weather.getChildAt(i)).getChildAt(0)).setText(mGoups[0]);
            ((TextView) ((RelativeLayout) future_weather.getChildAt(i)).getChildAt(1)).setText(mGoups[1]+"~"+mGoups[2] );
            ((TextView) ((RelativeLayout) future_weather.getChildAt(i)).getChildAt(2)).setText(mGoups[3]);
        }

        if(WeatherArray.getInstance(getActivity()).getArray().size() <= 1) {
            NotificationManager mNotificationManager=
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(getActivity())
                    .setContentTitle(prefs.getString("name","undefined"))
                    .setContentText(weather+"  "+prefs.getString("h_tmp",""))
                    .setSmallIcon(Utility.getImgByWeather(weather));

            mNotificationManager.notify(1,builder.build());
        }

        Log.d("WeatherFragment","weather is "+prefs.getString("weather", ""));
        if(weather.contains("晴")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_sunny);
        }
        else if(weather.contains("雷")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_thunderstorm);
        }
        else if(weather.contains("雨")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_rainy);
        }
        else if(weather.contains("多云")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_cloudy);
        }
        else if(weather.contains("阴")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_overcast);
        }
        else if(weather.contains("雪")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_snowy);
        }
        else if(weather.contains("雾") || weather.contains("霾")) {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_foggy);
        }
        else {
            relativeLayout2.setBackgroundResource(R.drawable.background_weather_sunny);
        }

    }

    /**
     * 刷新天气的方法
     * 因为要改变Fragment对应的layout，在Activity中我不知道怎么操作，所以只能在Fragment中获取天气并重新加载layout
     * @param citycode
     */
    public void refresh(String citycode) {
        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/recentweathers";
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
