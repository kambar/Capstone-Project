package com.barbachowski.k.workbreaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.barbachowski.k.workbreaker.service.CountDownService;
import com.barbachowski.k.workbreaker.settings.SettingsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private static boolean timerRunning = false;
    private TextView mRemainingTimeTextView;
    private CountDownServiceBroadcastReceiver receiver;
    private long remainingMilliseconds;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(savedInstanceState!=null){
            //get remainingMilliseconds
            remainingMilliseconds = savedInstanceState.getLong(Const.millisKey, Const.defaultRemainingTime);
        }

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
                            .setAction(R.string.click_me, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openExerciseView();
                                }
                            }).show();
                }

            }
        });

        //setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Explode fade = new Explode();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Explode slide = new Explode();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Const.millisKey, remainingMilliseconds);
    }

    @Override
    protected void onStart() {
        super.onStart();
        restartCountDownService();
        updateFABIcon();
        mTracker.setScreenName("Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
            startCountDownService();
        }
        else{
            stopCountDownService();
        }
    }

    private void restartCountDownService(){
        stopCountDownService();

        if(remainingMilliseconds==0){
            setRemainingTime();
        }

        startCountDownService();
    }

    private void setRemainingTime(){
        remainingMilliseconds = getSharedPreferences(Const.sharedPref, MODE_PRIVATE).getLong(Const.millisKey,Const.defaultRemainingTime);
    }

    private void startCountDownService() {
        Utils.startCountDownService(this, remainingMilliseconds);
        timerRunning=true;
    }

    private void stopCountDownService() {
        Utils.stopCountDownService(this);
        timerRunning=false;
    }

    private void openExerciseView(){
        stopCountDownService();
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
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    private class CountDownServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                timerRunning = true;
                remainingMilliseconds = intent.getLongExtra(CountDownService.key, 0);
                if(remainingMilliseconds!=0){
                    mRemainingTimeTextView.setText(Utils.formatTimeFromMilliseconds(remainingMilliseconds));
                }
                else
                {
                    setRemainingTime();
                    openExerciseView();
                    mRemainingTimeTextView.setText(Utils.formatTimeFromMilliseconds(remainingMilliseconds));
                }
            }

        }
    }
}
