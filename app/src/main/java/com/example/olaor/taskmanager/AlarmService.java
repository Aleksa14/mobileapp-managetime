package com.example.olaor.taskmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends Service{

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, TabbedTaskManager.class);
        pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{mIntent}, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Start task");
        builder.setContentText("U have to start your task!");
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
