package com.barbachowski.k.workbreaker.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by K on 12/01/2017.
 */

public class ExerciseStatistics implements Parcelable {
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

    public int increaseSkipped(){
        this.numberOfSkipped+=1;
        return this.numberOfSkipped;
    }

    public int increaseDone(){
        this.numberOfDone+=1;
        return this.numberOfDone;
    }

    protected ExerciseStatistics(Parcel in) {
        numberOfSkipped = in.readInt();
        numberOfDone = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numberOfSkipped);
        dest.writeInt(numberOfDone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExerciseStatistics> CREATOR = new Parcelable.Creator<ExerciseStatistics>() {
        @Override
        public ExerciseStatistics createFromParcel(Parcel in) {
            return new ExerciseStatistics(in);
        }

        @Override
        public ExerciseStatistics[] newArray(int size) {
            return new ExerciseStatistics[size];
        }
    };
}
