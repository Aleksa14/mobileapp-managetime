package com.example.olaor.taskmanager.TaskManager.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.olaor.taskmanager.TaskManager.Exception.TaskException;

import java.util.jar.Attributes;

@Entity
public class Task {


    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "start_date")
    private long mStartDate;
    @ColumnInfo(name = "end_date")
    private long mEndDate;
    @ColumnInfo(name = "project_id")
    private long mProjectId;

    public String getProjectName() {
        return mProjectName;
    }

    @ColumnInfo(name = "project_name")
    private String mProjectName;


    public Task(long startDate, long endDate, long projectId, String projectName) {
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mProjectId = projectId;
        this.mProjectName = projectName;
    }

    public void setIdInCalendar(long idInCalendar) {
        this.idInCalendar = idInCalendar;
    }

    public long getIdInCalendar() {
        return idInCalendar;
    }

    private long idInCalendar;


    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public long getDuration() {
        return mEndDate - mStartDate;
    }

    public long getProjectId() {
        return mProjectId;
    }
}
