package com.example.olaor.taskmanager;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class TaskManagerApplication extends Application {


    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
