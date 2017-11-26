package com.example.olaor.taskmanager;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.Scheduler;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProjectDetails extends Activity implements View.OnClickListener{

    private int index;
    private Project project;
    private AwesomeTextView remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        Intent intent = getIntent();
        index = intent.getIntExtra(ListingFragment.INDEX, 0);
        project = ListingFragment.adapter.getItem(index);
        remove = (AwesomeTextView) findViewById(R.id.remove_project);
        remove.setOnClickListener(this);
        setElementContent();
    }

    private void setElementContent() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        TextView name = (TextView) findViewById(R.id.name_project);
        TextView desc = (TextView) findViewById(R.id.desc_project);
        TextView start = (TextView) findViewById(R.id.start_date_project);
        TextView end = (TextView) findViewById(R.id.end_date_project);
        TextView duration = (TextView) findViewById(R.id.duration_project);

        name.setText(project.getName());
        desc.setText(project.getDescription());
        start.setText(dateFormat.format(new Date(project.getStartDate())));
        end.setText(dateFormat.format(new Date(project.getEndDate())));
        duration.setText((project.getEstimatedTime() / 1000  / 60 / 60) + " hours");
    }

    @Override
    public void onClick(View v) {
        ListingFragment.adapter.remove(index);
        finish();
    }
}
