package com.barbachowski.k.workbreaker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class CountDownService extends Service {
    public static final String CountDownServiceRemainingTime = "RemainingTime";
    public static final String key = "remainingMilliseconds";

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
            mTimer = new CountDownTimer(milliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    Intent intent = new Intent(CountDownServiceRemainingTime);
                    intent.putExtra(key, millisUntilFinished);
                    LocalBroadcastManager.getInstance(CountDownService.this).sendBroadcast(intent);
                }

                public void onFinish() {
                    Intent intent = new Intent(CountDownServiceRemainingTime);
                    intent.putExtra(key, 0L);
                    LocalBroadcastManager.getInstance(CountDownService.this).sendBroadcast(intent);
                    sendNotification();
                }
            }.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification(){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(CountDownService.this);
        builder.setContentTitle("Break time!");
        builder.setContentText("Make exercises to feel better now!");
        builder.setSmallIcon(android.R.drawable.ic_media_pause);
        builder.setSound(soundUri);
        Intent intent = new Intent(this, ExercisesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);


        notificationManager.notify(0,builder.build());
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }
}