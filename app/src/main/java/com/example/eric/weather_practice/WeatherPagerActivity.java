package com.example.eric.weather_practice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/2.
 */
public class WeatherPagerActivity extends AppCompatActivity{
    private ViewPager mViewPager;
    private ArrayList<Weather> mWeathers;
    private Weather w;

    public static WeatherPagerActivity weatherPagerActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewpager_weather);
        weatherPagerActivity = this;

        if(MainActivity.mainActivity != null)
            MainActivity.mainActivity.finish();

        mViewPager = (ViewPager) findViewById(R.id.weather_pager);
        w = (Weather) getIntent().getSerializableExtra("weather");

        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                mWeathers = WeatherArray.getInstance(WeatherPagerActivity.this).getArray();
                Log.d("WeatherPagerActivity","mWeather size is "+ mWeathers.size());
                Weather weather = mWeathers.get(position);
                return WeatherFragment.newInstance(weather);
            }

            @Override
            public int getCount() {
                mWeathers = WeatherArray.getInstance(WeatherPagerActivity.this).getArray();
                return mWeathers.size();
            }
        });
        mWeathers = WeatherArray.getInstance(this).getArray();
        for(int i = 0; i < mWeathers.size(); i++) {
            if(mWeathers.get(i).getCity_code().equals(w.getCity_code())) {
                mViewPager.setCurrentItem(i);
                break;
            }
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
