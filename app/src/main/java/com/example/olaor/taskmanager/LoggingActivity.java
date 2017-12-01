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

import java.util.List;
import java.util.ListIterator;

public class LoggingActivity extends AppCompatActivity {

    EditText timeSpent;
    EditText estimateTime;
    long taskId;
    Task task;
    Project project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        new Thread(){
            @Override
            public void run(){
                Intent intent = getIntent();
                taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
                TimeLine.db = AppDatabase.getDatabase(getApplicationContext());
                task = TimeLine.db.taskDao().getTaskById(taskId);
                project = TimeLine.db.projectDao().getProjectById(task.getProjectId());
                timeSpent = (EditText) findViewById(R.id.time_spent);
                estimateTime = (EditText) findViewById(R.id.estimate_time_change);
                long setHours = (project.getEstimatedTime() - task.getDuration()) / 1000 / 60 / 60;
                estimateTime.setText(setHours + "");
                NotificationService.clearNotification(getApplicationContext());
            }
        }.start();
    }

    public void saveChanges(View view) {
        final long loggedTime = Long.parseLong(timeSpent.getText().toString()) * 60 * 60 * 1000;
        final long estimate = Long.parseLong(estimateTime.getText().toString()) * 60 * 60 * 1000;

        new Thread(){
            @Override
            public void run(){
                TimeLine.db.taskDao().removeIdById(taskId);
                if ((project.getEstimatedTime() - task.getDuration()) == estimate){
                    project.setEstimatedTime(estimate - loggedTime);
                    if (loggedTime != task.getDuration()){
                        removeTasks(project);
                        TimeLine.db.projectDao().updateProject(project);
                        TimeLine.sheduleNewProject(project, getApplicationContext(), System.currentTimeMillis() + project.getMinTimeBetweenTask());
                    }else {
                        TimeLine.db.projectDao().updateProject(project);
                        List<Task> taskList = TimeLine.db.taskDao().getAllTask();
                        NotificationService.scheduleStartNotification(taskList.get(0), getApplicationContext());
                    }
                }else {
                    project.setEstimatedTime(estimate);
                    removeTasks(project);
                    TimeLine.db.projectDao().updateProject(project);
                    TimeLine.sheduleNewProject(project, getApplicationContext(), System.currentTimeMillis() + project.getMinTimeBetweenTask());
                }
                
            }
        }.start();
    }

    public void removeTasks(final Project project) {
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
