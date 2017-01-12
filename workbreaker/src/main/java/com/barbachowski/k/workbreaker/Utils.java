package com.barbachowski.k.workbreaker;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import java.util.concurrent.TimeUnit;

/**
 * Created by K on 09/01/2017.
 */

public class Utils {

    /*return unique device id*/
    public static String UID(Context c){
        String deviceId = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return deviceId;
    }

    public static String formatTimeFromMilliseconds(long milliseconds){
        if(TimeUnit.MILLISECONDS.toHours(milliseconds)==0)
        {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        }
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public static String formatHoursMinutes(int hours, int minutes){
        return String.format("%02d:%02d", hours, minutes);
    }

    public static long getMillisecondsFromString(String o) {
        int hour = getHour(o);
        int minutes = getMinute(o);
        long millis = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minutes);
        return millis;
    }

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public static void startCountDownService(Context context, long millis){
        Intent intent = new Intent(context, CountDownService.class);
        intent.putExtra(CountDownService.key, millis);
        context.startService(intent);
    }

    public static void stopCountDownService(Context context) {
        Intent intent = new Intent(context, CountDownService.class);
        context.stopService(intent);
    }

    public static void restartCountDownService(Context context, long millis){
        stopCountDownService(context);
        startCountDownService(context, millis);
    }
}
