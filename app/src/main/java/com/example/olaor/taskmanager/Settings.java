package com.example.olaor.taskmanager;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class Settings extends Fragment implements View.OnClickListener {

    public static final String SETTINGS_PREF = "SETTINGS_PREF";
    public static final String SETTINGS_START = "SETTINGS_START";
    public static final String SETTINGS_END = "SETTINGS_END";
    private EditText startTime, endTime;
    private long startTimeDay, endTimeDay;
    private Button applyButton;
    private int mHour, mMinute;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);
        getSettingsFromSharedPref();
        startTime = (EditText)rootView.findViewById(R.id.start_day_time);
        startTime.setText(startTimeDay+"");
        endTime = (EditText)rootView.findViewById(R.id.end_day_time);
        endTime.setText(endTimeDay+"");
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        applyButton = (Button) rootView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(this);
        return rootView;
    }

    private void getSettingsFromSharedPref(){
        SharedPreferences sp = getActivity().getSharedPreferences(Settings.SETTINGS_PREF, 0);
        startTimeDay = sp.getLong(Settings.SETTINGS_START, 0);
        endTimeDay = sp.getLong(Settings.SETTINGS_END, 0);
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
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Context context = getActivity();
            sharedpreferences = context.getSharedPreferences(SETTINGS_PREF, 0);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            try {
                Date dstart = dateFormat.parse(startTime.getText().toString());
                long start = dstart.getTime();
                Date dend = dateFormat.parse(endTime.getText().toString());
                long end = dend.getTime();
                editor.putLong(SETTINGS_START, start);
                editor.putLong(SETTINGS_END, end);
                editor.commit();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new Thread(){
                @Override
                public void run(){
                    TimeLine.db = AppDatabase.getDatabase(getContext());
                    List<Project> projectList = TimeLine.db.projectDao().getAllProject();
                    for (Project p : projectList){
                        removeTasks(p);
                        TimeLine.sheduleNewProject(p, getContext(), System.currentTimeMillis());
                    }
                }
            }.start();
        }
    }


    public void removeTasks(final Project project) {
        project.getTasksFromDb(TimeLine.db);
        ListIterator<Task> iterator = project.getTaskList().listIterator();
        Task t;
        while (iterator.hasNext()) {
            t = iterator.next();
            iterator.remove();
            TimeLine.db.taskDao().removeIdById(t.id);
            int d = CalendarService.deleteTaskFromCalendar(t.getIdInCalendar(), getContext());
            Log.i("deleted id :", "" + d);
        }
    }

}
