package com.example.olaor.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.util.List;

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
                TimeLine.db = AppDatabase.getDatabase(getApplicationContext());
                task = TimeLine.db.taskDao().getTaskById(taskId);
                project = TimeLine.db.projectDao().getProjectById(task.getProjectId());
                Intent intent = getIntent();
                taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
                timeSpent = (EditText) findViewById(R.id.time_spent);
                estimateTime = (EditText) findViewById(R.id.estimate_time_change);
                estimateTime.setText((project.getEstimatedTime() - task.getDuration())+"");
                NotificationService.clearNotification(getApplicationContext());
            }
        }.start();
    }

    public void saveChanges(View view) {
        final long loggedTime = Long.parseLong(timeSpent.getText().toString());
        final long estimate = Long.parseLong(estimateTime.getText().toString());

        new Thread(){
            @Override
            public void run(){
                TimeLine.db.taskDao().removeIdById(taskId);
                project.getTaskList().remove(0);
                if ((project.getEstimatedTime() - task.getDuration()) == estimate){
                    project.setEstimatedTime(estimate - loggedTime);
                    if (loggedTime != task.getDuration()){
                        TimeLine.db.projectDao().updateProject(project);
                        TimeLine.sheduleNewProject(project, getApplicationContext(), System.currentTimeMillis() + project.getMinTimeBetweenTask());
                    }else {
                        TimeLine.db.projectDao().updateProject(project);
                        List<Task> taskList = TimeLine.db.taskDao().getAllTask();
                        NotificationService.scheduleStartNotification(taskList.get(0), getApplicationContext());
                    }
                }else {
                    project.setEstimatedTime(estimate);
                    TimeLine.db.projectDao().updateProject(project);
                    TimeLine.sheduleNewProject(project, getApplicationContext(), System.currentTimeMillis() + project.getMinTimeBetweenTask());
                }
                
            }
        }.start();
    }
}
