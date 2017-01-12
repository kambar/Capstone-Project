package com.barbachowski.k.workbreaker;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
        Call<ExerciseSession> call = api.addExercise(Utils.UID(this), new ExerciseSession(new Date(),false));
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

        Call<ExerciseStatistics> call2 = api.updateStatistics(Utils.UID(this), new ExerciseStatistics(2, 3));
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
