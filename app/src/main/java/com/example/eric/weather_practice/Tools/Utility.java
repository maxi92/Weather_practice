package com.example.eric.weather_practice.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.eric.weather_practice.Data.City;
import com.example.eric.weather_practice.Data.FutureWeather;
import com.example.eric.weather_practice.R;
import com.example.eric.weather_practice.Data.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eric on 2016/5/25.
 */
public class Utility {

    /**
     * 处理城市列表的Json返回值
     * @param response
     * @param datalist
     */
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

    /**
     * 处理天气请求的Json返回值
     * @param context
     * @param response
     * @return
     */
    //Process the response of weather
    public static Weather handleWeatherResponse(Context context, String response) {
        response = Utility.convert(response);
        Log.d("Utility",response);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.optJSONObject("retData");
            String citycode = weatherinfo.optString("cityid");
            String name = weatherinfo.optString("city");
            JSONObject today = weatherinfo.optJSONObject("today");
            String weather = today.optString("type");
            String l_tmp = today.optString("lowtemp");
            String h_tmp = today.optString("hightemp");

            JSONArray forecast = weatherinfo.optJSONArray("forecast");
            ArrayList<FutureWeather> mFutureWeathers = new ArrayList<>();
            for(int i = 0; i < forecast.length(); i++) {
                JSONObject jo = (JSONObject) forecast.get(i);
                String w = jo.optString("type");
                String we = jo.optString("week");
                String lowtemp = jo.optString("lowtemp");
                String hightemp = jo.optString("hightemp");
                FutureWeather fw = new FutureWeather(we, lowtemp, hightemp, w);
                mFutureWeathers.add(fw);
            }

            return new Weather(name, weather, citycode, sdf.format(new Date()), l_tmp, h_tmp, mFutureWeathers);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 处理反向地理编码的Json结果
     * @param response
     * @return
     */
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

    /**
     * 将天气数据存储到SharedPreference里面
     * @param context
     * @param weather
     */
    //save all the Weather information responded to SharedPreferences
    public static void saveWeatherInfo(Context context, Weather weather) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("name",weather.getCity_name());
        editor.putString("citycode",weather.getCity_code());
        editor.putString("weather",weather.getWeather());
        editor.putString("l_tmp",weather.getL_tmp());
        editor.putString("h_tmp",weather.getH_tmp());
        editor.putString("date", sdf.format(new Date()));
        editor.putString("future1",weather.getMfutureweathers().get(0).toString());
        editor.putString("future2",weather.getMfutureweathers().get(1).toString());
        editor.putString("future3",weather.getMfutureweathers().get(2).toString());
        editor.putString("future4",weather.getMfutureweathers().get(3).toString());
        editor.putBoolean("isSelected",true);
        editor.apply();
    }

    /**
     * 将unicode转为utf-8，否则无法显示中文
     * @param utfString
     * @return
     */
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

    /**
     * 这是为了防止我用的API返回值中，直辖市的province和city甚至district相同
     * 导致最后显示成"北京北京北京"这种问题
     * @param province
     * @param district
     * @param name
     * @return
     */
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

    /**
     * 根据天气返回对应的图标资源ID
     * @param weather
     * @return
     */
    public static int getImgByWeather(String weather) {

        String imgName = "00";
        if ("晴".equals(weather)) {
            imgName = "00";
        } else if ("多云".equals(weather)) {
            imgName = "01";
        } else if ("阴".equals(weather)) {
            imgName = "02";
        } else if ("阵雨".equals(weather)) {
            imgName = "03";
        } else if ("雷阵雨".equals(weather)) {
            imgName = "04";
        } else if ("雷阵雨伴有冰雹".equals(weather)) {
            imgName = "05";
        } else if ("雨夹雪".equals(weather)) {
            imgName = "06";
        } else if ("小雨".equals(weather)) {
            imgName = "07";
        } else if ("中雨".equals(weather)) {
            imgName = "08";
        } else if ("大雨".equals(weather)) {
            imgName = "09";
        } else if ("暴雨".equals(weather)) {
            imgName = "10";
        } else if ("大暴雨".equals(weather)) {
            imgName = "11";
        } else if ("特大暴雨".equals(weather)) {
            imgName = "12";
        } else if ("阵雪".equals(weather)) {
            imgName = "13";
        } else if ("小雪".equals(weather)) {
            imgName = "14";
        } else if ("中雪".equals(weather)) {
            imgName = "15";
        } else if ("大雪".equals(weather)) {
            imgName = "16";
        } else if ("暴雪".equals(weather)) {
            imgName = "17";
        } else if ("雾".equals(weather)) {
            imgName = "18";
        } else if ("冻雨".equals(weather)) {
            imgName = "19";
        } else if ("沙尘暴".equals(weather)) {
            imgName = "20";
        } else if ("小到中雨".equals(weather)) {
            imgName = "21";
        } else if ("中到大雨".equals(weather)) {
            imgName = "22";
        } else if ("大到暴雨".equals(weather)) {
            imgName = "23";
        } else if ("暴雨到大暴雨".equals(weather)) {
            imgName = "24";
        } else if ("大暴雨到特大暴雨".equals(weather)) {
            imgName = "25";
        } else if ("小到中雪".equals(weather)) {
            imgName = "26";
        } else if ("中到大雪".equals(weather)) {
            imgName = "27";
        } else if ("大到暴雪".equals(weather)) {
            imgName = "28";
        } else if ("浮尘".equals(weather)) {
            imgName = "29";
        } else if ("扬沙".equals(weather)) {
            imgName = "30";
        } else if ("强沙尘暴".equals(weather)) {
            imgName = "31";
        } else if ("霾".equals(weather)) {
            imgName = "53";
        }

        try{
            Field field = R.drawable.class.getField("weather_icon_"+imgName);
            return field.getInt(new R.drawable());
        } catch (Exception e) {
            return R.drawable.weather_icon_undefined;
        }

    }

}
