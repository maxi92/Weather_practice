package com.example.eric.weather_practice.Activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.eric.weather_practice.Tools.Utility;

/**
 * Created by Eric on 2016/6/13.
 */
public class ForegroundService extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate (){
        this.startForeground();
    }

    private void startForeground() {
        startForeground(1, getMyActivityNotification("undefined","undefined","晴"));
    }

    private Notification getMyActivityNotification(String title, String text, String weather){
        // The PendingIntent to launch our activity if the user selects
        // this notification
        Intent intent = new Intent(this, WeatherPagerActivity.class);
        intent.putExtra("fromService",true);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent , PendingIntent.FLAG_CANCEL_CURRENT);

        return new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(Utility.getImgByWeather(weather))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent).build();
    }
}