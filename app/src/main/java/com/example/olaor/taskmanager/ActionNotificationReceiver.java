package com.example.olaor.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

public class ActionNotificationReceiver extends BroadcastReceiver {

    private long taskId;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.i("receiver2", "I'm in the receiver");

        switch (intent.getAction()) {
            case "START_ACTION":
                Log.i("notification:", "start task");
                new Thread() {
                    @Override
                    public void run() {
                        taskId = intent.getLongExtra(NotificationReceiver.TASK_ID, 0);
                        TimeLine.db = AppDatabase.getDatabase(context);
                        Task task = TimeLine.db.taskDao().getTaskById(taskId);
                        NotificationService.clearNotification(context);
                        NotificationService.scheduleEndNotification(task, context);
                    }
                }.start();
                break;
        }
    }

}
