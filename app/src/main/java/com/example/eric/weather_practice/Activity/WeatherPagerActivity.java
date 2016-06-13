package com.example.eric.weather_practice.Activity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.weather_practice.Data.Weather;
import com.example.eric.weather_practice.Data.WeatherArray;
import com.example.eric.weather_practice.Data.WeatherDB;
import com.example.eric.weather_practice.R;
import com.example.eric.weather_practice.Tools.Utility;

import java.util.ArrayList;

/**
 * Created by Eric on 2016/6/2.
 */
public class WeatherPagerActivity extends AppCompatActivity{
    private ViewPager mViewPager;
    private ArrayList<Weather> mWeathers;
    private Weather w;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Button weatherList;

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

        /**
         * 此处启动Foreground Service
         * 首先判断是不是从Service自己跳转而来的（点击状态栏上的通知，即PendingIntent）
         * 再判断服务是不是已经被启动了
         * 根据上面判断的结果来决定是启动服务还是更新通知内容
         */
        if(getIntent().getBooleanExtra("fromService",false)) {
            w = WeatherArray.getInstance(this).getArray().get(0);
        }
        else if(!isMyServiceRunning(ForegroundService.class)){
            Intent startIntent = new Intent(WeatherPagerActivity.this, ForegroundService.class);
            startService(startIntent);
        }
        else {
            NotificationManager mNotificationManager=
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(WeatherPagerActivity.this)
                    .setContentTitle(w.getCity_name())
                    .setContentText(w.getWeather()+"  "+ w.getH_tmp())
                    .setSmallIcon(Utility.getImgByWeather(w.getWeather()));

            mNotificationManager.notify(1,builder.build());
        }

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

        /**
         * 三个作用：
         * 1.启动程序以后标题对应该城市名称
         * 2.设置显示对应页面的天气
         * 3.添加底部小圆点
         */
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        mWeathers = WeatherArray.getInstance(this).getArray();
        for(int i = 0; i < mWeathers.size(); i++) {
            if(mWeathers.get(i).getCity_code().equals(w.getCity_code())) {
                setTitle(mWeathers.get(i).getCity_name());
                mViewPager.setCurrentItem(i);
                addBottomDots(i);
                break;
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                Weather w = WeatherArray.getInstance(WeatherPagerActivity.this).getArray().get(position);
                setTitle(w.getCity_name());

                NotificationManager mNotificationManager=
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(WeatherPagerActivity.this)
                        .setContentTitle(w.getCity_name())
                        .setContentText(w.getWeather()+"  "+ w.getH_tmp())
                        .setSmallIcon(Utility.getImgByWeather(w.getWeather()));

                mNotificationManager.notify(1,builder.build());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         * 底部列表按钮的监听器设置
         */
        weatherList = (Button) findViewById(R.id.weather_list);
        weatherList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherPagerActivity.this, WeatherListActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Toolbar的设置
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_toolbar, menu);
        return true;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[WeatherArray.getInstance(this).getArray().size()];
        int colorsActive = 0x7ffafafa;
        int colorsInactive = 0x7f5e82a6;

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive);
        }

    }

    /**
     * 判断是否某个服务是否在运行
     * @param serviceClass
     * @return
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        WeatherDB weatherDB = WeatherDB.getInstance(this);
        ArrayList<Weather> mWeathers = WeatherArray.getInstance(this).getArray();
        for(Weather w:mWeathers) {
            weatherDB.saveWeather(w);
        }

        if(weatherDB.loadWeather().size() != 0) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isSelected",true);
            editor.apply();
        }
        else {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isSelected",false);
            editor.apply();
        }
        super.onDestroy();
    }
}
