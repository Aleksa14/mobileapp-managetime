package com.example.olaor.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ActionNotificationReceiver extends BroadcastReceiver{

    private long taskId;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.i("receiver2", "I'm in the receiver");

        switch (intent.getAction()) {
            case "START_ACTION":
                Log.i("notification:", "start task");
                new Thread(){
                    @Override
                    public void run(){
                        taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
                        TimeLine.db = AppDatabase.getDatabase(context);
                        Task task = TimeLine.db.taskDao().getTaskById(taskId);
                        NotificationService.clearNotification(context);
                        NotificationService.scheduleEndNotification(task, context);
                    }
                }.start();

                break;
            case "DISMISSED_ACTION":
                Log.i("notification:", "task dismissed");
                taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
                TimeLine.db = AppDatabase.getDatabase(context);
                Task task = TimeLine.db.taskDao().getTaskById(taskId);
                Project project = TimeLine.db.projectDao().getProjectById(task.getProjectId());
                removeTasks(project, context);
                TimeLine.sheduleNewProject(project, context, task.getEndDate());
                NotificationService.clearNotification(context);
                NotificationService.scheduleStartNotification(task, context);
                break;
        }
    }

    public void removeTasks(final Project project, final Context context) {
        TimeLine.db =
                AppDatabase.getDatabase(context);
        new Thread() {
            @Override
            public void run() {
                project.getTasksFromDb(TimeLine.db);
                ListIterator<Task> iterator = project.getTaskList().listIterator();
                Task t;
                while (iterator.hasNext()) {
                    t = iterator.next();
                    iterator.remove();
                    TimeLine.db.taskDao().removeIdById(t.id);
                    int d = CalendarService.deleteTaskFromCalendar(t.getIdInCalendar(), context);
                    Log.i("deleted id :", "" + d);
                }
            }
        }.start();

    }


}
