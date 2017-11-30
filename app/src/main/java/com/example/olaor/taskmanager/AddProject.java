package com.example.olaor.taskmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class AddProject extends AppCompatActivity implements View.OnClickListener{

    AppDatabase db;
    private EditText startDate, startTime, endDate, endTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        startDate = (EditText) findViewById(R.id.start_project_date);
        startTime = (EditText) findViewById(R.id.start_project_time);
        endDate = (EditText) findViewById(R.id.end_project_date);
        endTime = (EditText) findViewById(R.id.end_project_time);
        startTime.setOnClickListener(this);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final View fView = view;
        if (view == startDate || view == endDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            EditText txtDate = fView == startDate ? startDate : endDate;

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == startTime || view == endTime){
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
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
    }


    LinkedList<String> allFields = new LinkedList<>();

    public void addProject(View view) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        EditText name = (EditText) findViewById(R.id.project_name);
        EditText desc = (EditText) findViewById(R.id.project_desc);
        EditText estimateTime = (EditText) findViewById(R.id.estimate_time);
        EditText minTaskDuration = (EditText) findViewById(R.id.min_task_duration);
        EditText maxTaskDuration = (EditText) findViewById(R.id.max_task_duration);
        EditText minTimeBetweenTask = (EditText) findViewById(R.id.minimum_time_beteen_tasts);
        EditText notification = (EditText) findViewById(R.id.notification_time);

        String pName = name.getText().toString();
        allFields.add(pName);
        String pDesc = desc.getText().toString();
        allFields.add(pDesc);
        String pStartDate = startDate.getText().toString();
        allFields.add(pStartDate);
        String pStartTime = startTime.getText().toString();
        allFields.add(pStartTime);
        String startString = pStartDate + " " + pStartTime;
        Date start = dateFormat.parse(startString);
        String pEndDate = endDate.getText().toString();
        allFields.add(pEndDate);
        String pEndTime = endTime.getText().toString();
        allFields.add(pEndDate);
        String endString = pEndDate + " " + pEndTime;
        Date end = dateFormat.parse(endString);
        Long pEstimateTime = Long.parseLong(estimateTime.getText().toString()) * 60 * 60 * 1000;
        allFields.add(estimateTime.getText().toString());
        Long pMinTaskDuration = Long.parseLong(minTaskDuration.getText().toString()) * 60 * 60 * 1000;
        allFields.add(minTaskDuration.getText().toString());
        Long pMaxTaskDuration = Long.parseLong(maxTaskDuration.getText().toString()) * 60 * 60 * 1000;
        allFields.add(maxTaskDuration.getText().toString());
        Long pMinTimeBetweenTask = Long.parseLong(minTimeBetweenTask.getText().toString()) * 60 * 60 * 1000;
        allFields.add(minTimeBetweenTask.getText().toString());
        Long pNotification = Long.parseLong(notification.getText().toString()) * 60 * 1000;
        allFields.add(notification.getText().toString());

        for (String s : allFields) {
            if (s.length() < 1) {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (start.getTime() > end.getTime()) {
            Toast.makeText(this, "Start day is after end day!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Project project = new Project(pName, pDesc, start.getTime(), end.getTime(), pEstimateTime, pMinTaskDuration, pMaxTaskDuration, pMinTimeBetweenTask, pNotification);
        Log.i("info", pName + " " + pDesc + " " + start.getTime() + " " + end.getTime() + " " + pEstimateTime + " " + pMinTaskDuration + " " + pMaxTaskDuration + " " + pMinTimeBetweenTask + " " + pNotification);
        db = AppDatabase.getDatabase(getApplicationContext());
        TimeLine.db = db;
        new Thread() {
            @Override
            public void run() {
                project.id = db.projectDao().addProject(project);
                Log.i("info", "Project with id " + project.id + " added to db");
            }
        }.start();
        TimeLine.sheduleNewProject(project, this, System.currentTimeMillis());
        Intent intent = new Intent(this, TabbedTaskManager.class);
        startActivity(intent);

    }
}
