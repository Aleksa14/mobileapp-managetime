package com.example.olaor.taskmanager;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.util.Calendar;
import java.util.List;

public class Settings extends Fragment implements View.OnClickListener {

    public static final String SETTINGS_PREF = "SETTINGS_PREF";
    public static final String SETTINGS_START = "SETTINGS_START";
    public static final String SETTINGS_END = "SETTINGS_END";
    private EditText startTime, endTime;
    private Button applyButton;
    private int mHour, mMinute;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);
        startTime = (EditText)rootView.findViewById(R.id.start_day_time);
        endTime = (EditText)rootView.findViewById(R.id.end_day_time);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        applyButton = (Button) rootView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        final View fView = view;
        if (view == startTime || view == endTime) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            EditText txtTime = fView == startTime ? startTime : endTime;
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (view == applyButton){
            long start = Long.parseLong(startTime.getText().toString()) * 60 * 60 * 1000;
            long end = Long.parseLong(endTime.getText().toString()) * 60 * 60 * 1000;
            Context context = getActivity();
            sharedpreferences = context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(SETTINGS_START, start);
            editor.putLong(SETTINGS_END, end);
            editor.commit();
            new Thread(){
                @Override
                public void run(){
                    List<Project> projectList = TimeLine.db.projectDao().getAllProject();
                    for (Project p : projectList){
                        TimeLine.sheduleNewProject(p, getContext(), System.currentTimeMillis());
                    }
                }
            }.start();
        }
    }



}
