package com.example.eric.weather_practice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eric on 2016/5/25.
 */
public class Utility {

    //Process the response of city's list
    public static void handle(String response, List<City> datalist) {

            response = Utility.convert(response);
            Log.d("Utility",response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.optJSONArray("retData");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = jsonArray.getJSONObject(i);
                    StringBuilder result = new StringBuilder();
                    String province = jsonItem.getString("province_cn");
                    String district = jsonItem.getString("district_cn");
                    String name = jsonItem.getString("name_cn");
                    String citycode = jsonItem.getString("area_id");
                    String res = Utility.process(province, district, name);
                    City city = new City(province, district, name, citycode, res);
                    datalist.add(city);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    //Process the response of weather
    public static Weather handleWeatherResponse(Context context,String response) {
        response = Utility.convert(response);
        Log.d("Utility",response);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.optJSONObject("retData");
            String citycode = weatherinfo.optString("citycode");
            String name = weatherinfo.optString("city");
            String time = weatherinfo.optString("time");
            String weather = weatherinfo.optString("weather");
            String l_tmp = weatherinfo.optString("l_tmp");
            String h_tmp = weatherinfo.optString("h_tmp");
            String WD = weatherinfo.optString("WD");
            String WS = weatherinfo.optString("WS");
            //saveWeatherInfo(context, name, citycode, l_tmp, h_tmp, weather, time);
            return new Weather(name, weather, citycode, time, sdf.format(new Date()), l_tmp, h_tmp);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String handleLocationResponse(String response) {
        response = response.substring(29, response.length()-1);
        String city = "北京";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject result = jsonObject.optJSONObject("result");
            JSONObject addressComponent = result.optJSONObject("addressComponent");
            city = addressComponent.optString("city");
            if(city.endsWith("市")) {
                city = city.substring(0, city.length()-1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return city;
        }
    }

    //save all the Weather information responded to SharedPreferences
    public static void saveWeatherInfo(Context context, Weather weather) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("name",weather.getCity_name());
        editor.putString("time",weather.getTime());
        editor.putString("citycode",weather.getCity_code());
        editor.putString("weather",weather.getWeather());
        editor.putString("l_tmp",weather.getL_tmp());
        editor.putString("h_tmp",weather.getH_tmp());
        editor.putString("date", sdf.format(new Date()));
        editor.putBoolean("isSelected",true);
        editor.commit();
    }

    //convert unicode to utf-8
    private static String convert(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }
        sb.append(utfString.substring(pos));
        return sb.toString();
    }

    private static String process(String province, String district, String name) {
        StringBuilder sb = new StringBuilder();
        if(province.equals(district)) {
            sb.append(district);
            if(!district.equals(name)) {
                sb.append(name);
            }
            return sb.toString();
        }
        if(district.equals(name)) {
            sb.append(province+name);
            return sb.toString();
        }
        sb.append(province+district+name);
        return sb.toString();
    }

}
