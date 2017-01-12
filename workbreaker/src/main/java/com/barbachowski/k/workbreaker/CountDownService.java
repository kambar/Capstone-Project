package com.barbachowski.k.workbreaker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class CountDownService extends Service {
    public static final String CountDownServiceRemainingTime = "RemainingTime";
    public static final String key = "remainingMilliseconds";
    private String TAG = CountDownService.class.getCanonicalName();

    private CountDownTimer mTimer;
    public CountDownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            long milliseconds = intent.getLongExtra(key, 0);
            Log.v(TAG, "Service started. Millis:"+milliseconds);
            mTimer = new CountDownTimer(milliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    //Log.v(TAG, "Service onTick. Millis until finished:"+millisUntilFinished);
                    Intent intent = new Intent(CountDownServiceRemainingTime);
                    intent.putExtra(key, millisUntilFinished);
                    LocalBroadcastManager.getInstance(CountDownService.this).sendBroadcast(intent);
                    updateWidget(millisUntilFinished);
                }

                public void onFinish() {
                    Log.v(TAG, "Service finished.");
                    Intent intent = new Intent(CountDownServiceRemainingTime);
                    intent.putExtra(key, 0L);
                    LocalBroadcastManager.getInstance(CountDownService.this).sendBroadcast(intent);
                    sendNotification();
                    updateWidget(0L);
                }
            }.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification(){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(CountDownService.this);
        builder.setContentTitle(getString(R.string.notification_title));
        builder.setContentText(getString(R.string.notification_text));
        builder.setSmallIcon(android.R.drawable.ic_media_pause);
        builder.setSound(soundUri);
        Intent intent = new Intent(this, ExercisesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);


        notificationManager.notify(0,builder.build());
    }

    private void updateWidget(long millisUntilFinished){
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.workbreaker_appwidget);
        ComponentName thisWidget = new ComponentName(context, WorkBreakerAppWidgetProvider.class);

        if(millisUntilFinished>0){
            remoteViews.setViewVisibility(R.id.widget_caption, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_remaining_time, Utils.formatTimeFromMilliseconds(millisUntilFinished));
        }
        else{
            remoteViews.setViewVisibility(R.id.widget_caption, View.INVISIBLE);
            remoteViews.setTextViewText(R.id.widget_remaining_time, getString(R.string.widget_break_now_info));
        }

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }
}
