package com.example.olaor.taskmanager;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_ARRAY_KEY = "MainActivity.TaskArray";
    public static final int REQUEST_WRITE_CALENDAR_ID = 10;
    public static final int REQUEST_READ_CALENDAR_ID = 11;
    public static final String DB_NAME = "task-manager-db";
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        db = AppDatabase.getDatabase(this);
        //db = Room.databaseBuilder(context, AppDatabase.class, MainActivity.DB_NAME).build();
        new Thread() {
            @Override
            public void run() {

                for (Project p : db.projectDao().getAllProject()){
                    Log.i("info", p.getName());
                }

            }
        }.start();
//        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
//        builder.appendPath("time");
//        ContentUris.appendId(builder, System.currentTimeMillis());
//        Intent intent = new Intent(Intent.ACTION_VIEW)
//                .setData(builder.build());
//        startActivity(intent);
        //startIntent();

    }

    private void startIntent() {
        if (checkPermissions()) {
            Intent intent = new Intent(this, ShowCalendarActivity.class);
            try {
//                intent.putExtra(TASK_ARRAY_KEY, new Parcelable[]{new Task(System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60, 1000 * 60 * 60)});
                startActivity(intent);
            } catch (Exception e) {
                Log.wtf("Lol", e);
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_WRITE_CALENDAR_ID);
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR_ID);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_CALENDAR_ID:
            case REQUEST_READ_CALENDAR_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("info", "Yey, we have permissions.");
                    startIntent();
                } else {
                    Log.i("wtf", "Well, you are an idiot.");
                    startIntent();
                }
            }
        }
    }
}
