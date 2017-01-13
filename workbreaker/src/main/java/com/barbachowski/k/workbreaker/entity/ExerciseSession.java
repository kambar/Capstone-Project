package com.barbachowski.k.workbreaker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by K on 11/01/2017.
 */

public class ExerciseSession implements Parcelable {
    private Date date;
    private int skipped; //1-true, 0-false

    public ExerciseSession(int skipped){
        this.date= new Date();
        this.skipped = skipped;
    }

    public int getSkipped(){
        return skipped;
    }

    public Date getDate(){
        return date;
    }

    protected ExerciseSession(Parcel in) {
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        skipped = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeInt(skipped);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExerciseSession> CREATOR = new Parcelable.Creator<ExerciseSession>() {
        @Override
        public ExerciseSession createFromParcel(Parcel in) {
            return new ExerciseSession(in);
        }

        @Override
        public ExerciseSession[] newArray(int size) {
            return new ExerciseSession[size];
        }
    };
}
