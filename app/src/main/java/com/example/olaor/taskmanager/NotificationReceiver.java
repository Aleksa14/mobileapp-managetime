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

import com.example.olaor.taskmanager.TaskManager.Data.Task;

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, TabbedTaskManager.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("New task to do");
        builder.setContentText("You have to start your task soon");
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentIntent(pendingIntent);

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
