package com.barbachowski.k.workbreaker.settings;

/**
 * Created by K on 11/01/2017.
 * Code source http://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.barbachowski.k.workbreaker.R;
import com.barbachowski.k.workbreaker.Utils;

public class TimePreference extends DialogPreference {
    private int hours =0;
    private int minutes =0;
    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText(R.string.time_preference_set);
        setNegativeButtonText(R.string.time_preference_cancel);
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());
        picker.setIs24HourView(true);
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(hours);
        picker.setCurrentMinute(minutes);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            hours =picker.getCurrentHour();
            minutes =picker.getCurrentMinute();

            String time= Utils.formatHoursMinutes(hours, minutes);

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString(getContext().getString(R.string.default_interval));
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        hours =getHour(time);
        minutes =getMinute(time);
    }
}
