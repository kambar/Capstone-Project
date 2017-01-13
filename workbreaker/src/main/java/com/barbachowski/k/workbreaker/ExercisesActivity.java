package com.barbachowski.k.workbreaker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.barbachowski.k.workbreaker.contentProvider.WorkBreakerContentProvider;
import com.barbachowski.k.workbreaker.database.ExerciseSessionTable;
import com.barbachowski.k.workbreaker.database.ExerciseStatisticsTable;
import com.barbachowski.k.workbreaker.entity.ExerciseSession;
import com.barbachowski.k.workbreaker.entity.ExerciseStatistics;
import com.barbachowski.k.workbreaker.service.DataUploadService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ExercisesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private InterstitialAd mInterstitialAd;
    private Tracker mTracker;
    private ExerciseStatistics mStatistics;
    private TextView mSkippedBadgeTV;
    private TextView mDoneBadgeTV;
    private String TAG = ExercisesActivity.class.getCanonicalName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercises);
        setupWindowAnimations();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the shared Tracker instance.
        MyAnalyticsApplication application = (MyAnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        AppCompatButton skipButton = (AppCompatButton)findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseSession exercise = new ExerciseSession(1);

                if(mStatistics!=null){
                    mStatistics.increaseSkipped();
                }

                handleButtonClick(exercise, mStatistics, "Skip");
            }
        });

        AppCompatButton doneButton = (AppCompatButton)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseSession exercise = new ExerciseSession(0);

                if(mStatistics!=null){
                    mStatistics.increaseDone();
                }

                handleButtonClick(exercise, mStatistics, "Done");
            }
        });

        mSkippedBadgeTV = (TextView)findViewById(R.id.skip_badge);
        mDoneBadgeTV = (TextView)findViewById(R.id.done_badge);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);


    }

    private void setupWindowAnimations() {
        Explode fade = new Explode();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Explode slide = new Explode();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);

    }

    private void handleButtonClick(ExerciseSession exercise, ExerciseStatistics statistics, String actionTracking){
        updateData(exercise,statistics);
        Intent intent = new Intent(ExercisesActivity.this, DataUploadService.class);
        intent.putExtra(DataUploadService.statisticsKey, statistics);
        intent.putExtra(DataUploadService.exercisesKey, exercise);
        startService(intent);
        sendActionTracking(actionTracking);
        showInterstitial();
    }

    private void updateData(ExerciseSession exercise, ExerciseStatistics statistics){
        ContentValues v = new ContentValues();
        v.put(ExerciseSessionTable.COLUMN_SKIPPED, exercise.getSkipped());
        v.put(ExerciseSessionTable.COLUMN_DATE, exercise.getDate().toString());
        getContentResolver().insert(WorkBreakerContentProvider.EXERCISE_SESSIONS__URI, v);


        ContentValues v2 = new ContentValues();
        v2.put(ExerciseStatisticsTable.COLUMN_COUNT_DONE, statistics.getNumberOfDone());
        v2.put(ExerciseStatisticsTable.COLUMN_COUNT_SKIPPED, statistics.getNumberOfSkipped());
        getContentResolver().update(WorkBreakerContentProvider.EXERCISE_STATISTICS__URI, v2, null, null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mTracker.setScreenName(getString(R.string.Exercise_Activity_Screen_Name));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        sendActionTracking("Back");
        showInterstitial();
    }

    private void sendActionTracking(String action){
        mTracker.setScreenName(ExercisesActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ExerciseAction").setAction(action).build());
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                goToTheMainScreen();
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                goToTheMainScreen();
            }
        });
        return interstitialAd;
    }

    private void goToTheMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showInterstitial() {

        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, R.string.add_did_not_load_text, Toast.LENGTH_SHORT).show();
            goToTheMainScreen();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = { ExerciseStatisticsTable.COLUMN_COUNT_SKIPPED, ExerciseStatisticsTable.COLUMN_COUNT_DONE };
        return new CursorLoader(this,
                WorkBreakerContentProvider.EXERCISE_STATISTICS__URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            int skippedCount = cursor.getInt(cursor.getColumnIndex(ExerciseStatisticsTable.COLUMN_COUNT_SKIPPED));
            int doneCount = cursor.getInt(cursor.getColumnIndex(ExerciseStatisticsTable.COLUMN_COUNT_DONE));
            mStatistics = new ExerciseStatistics(doneCount, skippedCount);
            Log.v(TAG, "Skipped:"+skippedCount+", Done:"+doneCount);
            //update buttons
            String skipped;
            String done;
            if(skippedCount < 100){
                skipped = " "+Integer.toString(skippedCount)+" ";
            }
            else{
                skipped = Integer.toString(skippedCount);
            }

            if(skippedCount < 100){
                done = " "+Integer.toString(doneCount)+" ";
            }
            else{
                done = Integer.toString(doneCount);
            }

            mSkippedBadgeTV.setText(skipped);
            mDoneBadgeTV.setText(done);

            //cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
