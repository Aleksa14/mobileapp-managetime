package com.example.olaor.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.util.ListIterator;

public class RescheduleActivity extends AppCompatActivity{

    EditText startTime;
    long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule);
        startTime = (EditText) findViewById(R.id.delay_time);
        Intent intent = getIntent();
        taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
        NotificationService.clearNotification(getApplicationContext());
    }

    public void applyDelay(View view) {
        new Thread() {
            @Override
            public void run() {
                actionAfterDismissed();
            }
        }.start();
    }


    private void actionAfterDismissed() {
        TimeLine.db = AppDatabase.getDatabase(getApplicationContext());
        Task task = TimeLine.db.taskDao().getTaskById(taskId);
        Project project = TimeLine.db.projectDao().getProjectById(task.getProjectId());
        removeTasks(project);
        final Thread scheduleThred;
        if (startTime.getText().toString().length() < 1){
            scheduleThred = TimeLine.sheduleNewProject(project, getApplicationContext(), project.getEndDate());
        }else {
            scheduleThred = TimeLine.sheduleNewProject(project, getApplicationContext(), System.currentTimeMillis() + (Long.parseLong(startTime.getText().toString()) * 60 * 60 * 1000));
        }
        final Project p = project;
        new Thread(){
            @Override
            public void run() {
                try {
                    scheduleThred.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //NotificationService.scheduleStartNotification(p.getTaskList().get(0), getApplicationContext());
            }
        }.start();
    }


    public void removeTasks(final Project project) {
        TimeLine.db =
                AppDatabase.getDatabase(getApplicationContext());

        project.getTasksFromDb(TimeLine.db);
        ListIterator<Task> iterator = project.getTaskList().listIterator();
        Task t;
        while (iterator.hasNext()) {
            t = iterator.next();
            iterator.remove();
            TimeLine.db.taskDao().removeIdById(t.id);
            int d = CalendarService.deleteTaskFromCalendar(t.getIdInCalendar(), getApplicationContext());
            Log.i("deleted id :", "" + d);
        }
    }
}

