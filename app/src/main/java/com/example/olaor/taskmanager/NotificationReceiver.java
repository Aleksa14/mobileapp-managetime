package com.example.olaor.taskmanager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver  extends BroadcastReceiver{

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmService.class);
        context.startService(service);
    }
}
