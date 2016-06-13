package com.example.eric.weather_practice.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eric.weather_practice.Data.City;
import com.example.eric.weather_practice.Data.Weather;
import com.example.eric.weather_practice.Data.WeatherArray;
import com.example.eric.weather_practice.Data.WeatherDB;
import com.example.eric.weather_practice.Tools.HttpCallBackListener;
import com.example.eric.weather_practice.R;
import com.example.eric.weather_practice.Tools.HttpUtil;
import com.example.eric.weather_practice.Tools.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button search_button;
    private EditText search_text;
    private ListView listview;
    private ArrayAdapter<City> adapter;
    private List<City> datalist;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private LocationManager locationManager;
    private String provider;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        /**
         * 从SharedPreference中读取isSelected标记，如果为true就说明不是第一次启动，那么就直接进入天气显示
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSelected = prefs.getBoolean("isSelected",false);
        if(WeatherPagerActivity.weatherPagerActivity != null)
            WeatherPagerActivity.weatherPagerActivity.finish();
        if(isSelected) {
            ArrayList<Weather> mWeathers = WeatherArray.getInstance(this).getArray();
            if(!mWeathers.isEmpty()) {
                Intent intent = new Intent(this, WeatherPagerActivity.class);
                intent.putExtra("weather",mWeathers.get(0));
                startActivity(intent);
            }
        }

        search_button = (Button) findViewById(R.id.search_button);
        search_text = (EditText) findViewById(R.id.search_text);
        listview = (ListView) findViewById(R.id.list_view);
        datalist = new ArrayList<City>();
        adapter = new ArrayAdapter<City>(this, android.R.layout.simple_list_item_1, datalist);
        listview.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("天气预报");
        }
        setSupportActionBar(toolbar);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("search","search button");
                String inputText = search_text.getText().toString();
                queryCityList(inputText);
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = parent.getAdapter();
                City city = (City) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, WeatherProcess_invisible.class);
                intent.putExtra("state", 1);
                intent.putExtra("citycode", city.getCity_code());
                startActivity(intent);
            }
        });
    }

    /**
     * 根据城市名称来搜索相关的城市列表（传递的是API的cityname参数）
     * 利用回调的办法，在回调的方法中调用runOnUiThread方法回到主线程更新UI
     * @param cityname
     */
    private void queryCityList(String cityname) {
        String httpUrl;
        String httpArg;
        if(!TextUtils.isEmpty(cityname)) {
            httpUrl = "http://apis.baidu.com/apistore/weatherservice/citylist";
            httpArg = "cityname="+cityname;
            showProgressDialog();
            Log.d("MainActivity",httpArg);
            HttpUtil.sendHttpRequest(httpUrl, httpArg, new HttpCallBackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d("MainActivity","onFinish");
                    Utility.handle(response, datalist);
                    Log.d("MainActivity","onFinish1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MainActivity","onFinishrun");
                            closeProgressDialog();
                            Log.d("MainActivity","Size:"+datalist.size());
                            adapter.notifyDataSetChanged();
                            listview.setSelection(0);
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
                            Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    /**
     * Toolbar的初始化以及上面按钮的相关动作
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.gps: {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providerList =  locationManager.getProviders(true);
                if(providerList.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                }
                else if(providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                }
                else {
                    Toast.makeText(this, "无法定位，请打开您的GPS或移动网络", Toast.LENGTH_LONG).show();
                }
                Log.d("Locationprovider",provider);
                Log.d("Location","test");
                locationManager.requestLocationUpdates(provider,5000,1,locationListener);
                Location location = locationManager.getLastKnownLocation(provider);
                Log.d("Location","test2");
                if(location != null) {
                    processCoordinate(location);
                }
                else {
                    Log.d("Location","test4");
                    Toast.makeText(MainActivity.this, "暂时无法获得位置信息",Toast.LENGTH_SHORT);
                }
                break;
            }
            case R.id.about: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("关于");
                dialog.setMessage("应用名称：天气\n作者:马西\n版本：0.0.1");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                break;
            }
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 位置监听器保证位置的更新
     * processCoordinate方法将location中的经纬度取出，利用百度地图的API查询对应位置，并返回城市名
     * 然后启动下一步的Activity
     */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            processCoordinate(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void processCoordinate(Location location) {
        HttpUtil.sendPositionRequest(location.getLongitude() + "", location.getLatitude() + "", new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                String cityname = Utility.handleLocationResponse(response);
                Log.d("cityname", cityname);
                Intent intent = new Intent(MainActivity.this, WeatherPagerActivity.class);
                intent.putExtra("state",2);
                intent.putExtra("cityname",cityname );
                startActivity(intent);
                finish();
            }
            @Override
            public void onError(Exception e) {
                closeProgressDialog();
                Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示进度的转动圆圈
     */
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

    /**
     * onDestroy主要做两件事
     * 首先将Weather ArrayList存进数据库
     * 此外判断一下，如果数据库中已经没有天气内容了，那么就将isSelected置为false，否则就置为true
     * 这是为了防止在MainActivity中退出以后下一次打开程序不会自动进入天气显示页面，同时也防止天气被删完以后SharedPreference还残留有数据错误显示的问题
     */
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
