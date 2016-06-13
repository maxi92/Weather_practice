package com.example.eric.weather_practice.Tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eric on 2016/5/24.
 */
public class HttpUtil {

    public static void sendHttpRequest(final String httpUrl,
                                       final String httpArg,
                                       final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.d("HttpUtil","run");
                    String http = httpUrl+"?"+httpArg;
                    URL url = new URL(http);
                    connection  = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("apikey", "63a719d09b209b04eb02dd329536cdcc");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    Log.d("HttpUtil","run1");
                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("HttpUtil",response.toString());
                    listener.onFinish(response.toString());
                } catch (Exception e) {
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
                finally {
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    public static void sendPositionRequest(final String longitude,
                                           final String latitude,
                                           final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String httpBaseUri = "http://api.map.baidu.com/geocoder/v2/?"
                            + "ak=jGRNRWYudgMDaX4pBEBZvzgygWbxmOns"
                            + "&mcode=82:52:4D:8E:38:ED:52:20:C5:C3:44:B4:92:B8:63:5D:56:9E:A1:EF;com.example.eric.weather_practice"
                            + "&callback=renderReverse"
                            + "&location="+latitude+","+longitude
                            + "&output=json"
                            + "&pois=0";
                    URL url = new URL(httpBaseUri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("Location",response.toString());
                    listener.onFinish(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
                finally {
                    if(connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }
}
