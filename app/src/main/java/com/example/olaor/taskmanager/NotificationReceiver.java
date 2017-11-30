package com.example.olaor.taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver{

    public static final String ACTION_KEY = "ACTION_KEY";
    public static final String ACTION_START_TASK = "ACTION_START_TASK";
    public static final String ACTION_END_TASK = "ACTION_END_TASK";
    public static final String TASK_ID = "TASK_ID";
    private long taskId;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);

        switch (intent.getStringExtra(ACTION_KEY)){
            case ACTION_START_TASK:
                Intent intent1 = new Intent();
                taskId = intent.getLongExtra(TASK_ID, 0);
                intent1.putExtra(TASK_ID, taskId);
                intent1.setAction("START_ACTION");
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(0, "Start", pendingIntent1);

                Intent intent2 = new Intent(context, RescheduleActivity.class);
                taskId = intent.getLongExtra(TASK_ID, 0);
                intent2.putExtra(TASK_ID, taskId);
//                intent2.setAction("DISMISSED_ACTION");
                PendingIntent pendingIntent2 = PendingIntent.getActivities(context, 1, new Intent[]{intent2}, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(0, "Reschedule", pendingIntent2);

                builder.setContentTitle("New task to do");
                builder.setContentText("You have to start your task soon");

                break;
            case ACTION_END_TASK:
                Intent intent3 = new Intent(context, LoggingActivity.class);
                PendingIntent pendingIntent3 = PendingIntent.getActivities(context, 0, new Intent[]{intent3}, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(0, "Log the work", pendingIntent3);

                builder.setContentTitle("Task ended!");
                builder.setContentText("Log your work time.");

                break;
        }


        Notification n = builder.build();

        // create the notification
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(R.drawable.ic_notifications_black_24dp, n);

        // create a vibration
        try {
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        } catch (Exception e) {
        }
    }
}
