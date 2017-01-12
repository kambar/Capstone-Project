package com.barbachowski.k.workbreaker;

/**
 * Created by K on 12/01/2017.
 */

public class ExerciseStatistics {
    int numberOfSkipped;
    int numberOfDone;

    public ExerciseStatistics(int numberOfDone, int numberOfSkipped){
        this.numberOfDone=numberOfDone;
        this.numberOfSkipped = numberOfSkipped;
    }

    public int getNumberOfSkipped(){
        return numberOfSkipped;
    }

    public int getNumberOfDone(){
        return numberOfDone;
    }

}
