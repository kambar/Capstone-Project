package com.barbachowski.k.workbreaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private static boolean timerRunning = false;
    private TextView mRemainingTimeTextView;
    private CountDownServiceBroadcastReceiver receiver;
    private long remainingMilliseconds = 30000; // todo init value from settings
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Obtain the shared Tracker instance.
        MyAnalyticsApplication application = (MyAnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mRemainingTimeTextView = (TextView) findViewById(R.id.remaining_time) ;
        setSupportActionBar(toolbar);
        receiver = new CountDownServiceBroadcastReceiver();

        fab = (FloatingActionButton) findViewById(R.id.fab);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timerRunning = !timerRunning;

                updateFABIcon();

                triggerService();

                if(timerRunning){
                    Snackbar.make(view, R.string.make_exercises_now, Snackbar.LENGTH_LONG)
                            .setAction("Click me!", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openExerciseView();
                                }
                            }).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(CountDownService.CountDownServiceRemainingTime));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void triggerService(){
        if(timerRunning){
            startCountingInService();
        }
        else{
            pauseCountingInService();
        }
    }

    private void startCountingInService() {
        Intent intent = new Intent(this, CountDownService.class);
        intent.putExtra(CountDownService.key, remainingMilliseconds);
        startService(intent);
    }

    private void pauseCountingInService() {
        Intent intent = new Intent(this, CountDownService.class);
        stopService(intent);
    }

    private void openExerciseView(){
        Intent intent = new Intent(getApplicationContext(), ExercisesActivity.class);

        startActivity(intent);
    }

    private void updateFABIcon(){
        if(!timerRunning){
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_play));
        }
        else{
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_media_pause));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTracker.setScreenName("Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String formatTimeFromMilliseconds(long milliseconds){
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

    private class CountDownServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                remainingMilliseconds = intent.getLongExtra(CountDownService.key, 0);
                if(remainingMilliseconds!=0){
                    mRemainingTimeTextView.setText(formatTimeFromMilliseconds(remainingMilliseconds));
                }
                else
                {
                    remainingMilliseconds = 30000; // todo set from settings
                    openExerciseView();
                    mRemainingTimeTextView.setText(formatTimeFromMilliseconds(remainingMilliseconds));
                }
            }

        }
    }
}