package com.barbachowski.k.workbreaker.interfaces;

import com.barbachowski.k.workbreaker.entity.ExerciseSession;
import com.barbachowski.k.workbreaker.entity.ExerciseStatistics;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by K on 11/01/2017.
 */

public interface WorkBreakerApi {
    //add new break
    @POST("/{new}/break.json")
    Call<ExerciseSession> addExercise(@Path("new") String s1, @Body ExerciseSession exerciseSession);

    //update statistics
    @PUT("/{new}/statistics.json")
    Call<ExerciseStatistics> updateStatistics(@Path("new") String s1, @Body ExerciseStatistics exerciseStatistics);

}
