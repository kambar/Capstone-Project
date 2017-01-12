package com.barbachowski.k.workbreaker;

import java.util.Date;

/**
 * Created by K on 11/01/2017.
 */

public class ExerciseSession {
    private Date date;
    private Boolean skipped;

    public ExerciseSession(Date date, Boolean skipped){
        this.date= date;
        this.skipped = skipped;
    }

    public Boolean getSkipped(){
        return skipped;
    }

    public void setSkipped(Boolean skipped){
        this.skipped = skipped;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }
}
