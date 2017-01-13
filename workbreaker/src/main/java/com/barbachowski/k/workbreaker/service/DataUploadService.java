package com.barbachowski.k.workbreaker.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.barbachowski.k.workbreaker.Const;
import com.barbachowski.k.workbreaker.entity.ExerciseSession;
import com.barbachowski.k.workbreaker.entity.ExerciseStatistics;
import com.barbachowski.k.workbreaker.Utils;
import com.barbachowski.k.workbreaker.interfaces.WorkBreakerApi;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by K on 11/01/2017.
 */

public class DataUploadService extends IntentService {
    private String TAG = DataUploadService.class.getCanonicalName();
    public static final String statisticsKey = "statistics";
    public static final String exercisesKey = "exercise";

    public DataUploadService()
    {
        super(DataUploadService.class.getName());
    }

    public DataUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.firebaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WorkBreakerApi api = retrofit.create(WorkBreakerApi.class);
        ExerciseSession session = intent.getParcelableExtra(exercisesKey);

        Call<ExerciseSession> call = api.addExercise(Utils.UID(this), session);
        call.enqueue(new Callback<ExerciseSession>() {
            @Override
            public void onResponse(Call<ExerciseSession> call, Response<ExerciseSession> response) {
                Log.v(TAG, "onResponse");
            }

            @Override
            public void onFailure(Call<ExerciseSession> call, Throwable t) {
                Log.v(TAG, "onFailure");
            }
        });

        ExerciseStatistics statistics = intent.getParcelableExtra(statisticsKey);

        Call<ExerciseStatistics> call2 = api.updateStatistics(Utils.UID(this), statistics);
        call2.enqueue(new Callback<ExerciseStatistics>() {
            @Override
            public void onResponse(Call<ExerciseStatistics> call, Response<ExerciseStatistics> response) {

            }

            @Override
            public void onFailure(Call<ExerciseStatistics> call, Throwable t) {

            }
        });
    }
}
