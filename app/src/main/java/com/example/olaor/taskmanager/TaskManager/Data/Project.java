package com.example.olaor.taskmanager.TaskManager.Data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.olaor.taskmanager.TaskManager.Exception.ProjectException;

import java.util.LinkedList;
import java.util.List;
@Entity
public class Project {
    @PrimaryKey(autoGenerate = true)
    public long id;
    private String name;
    private String description;
    private long startDate;
    private long endDate;
    private long estimatedTime;
    private long minTaskDuration;
    private long maxTaskDuration;
    private long minTimeBetweenTask;
    private long notificationTime;
    @Ignore
    private List<Task> taskList;

    public Project(
            String name, String description, long startDate, long endDate,
            long estimatedTime, long minTaskDuration,
            long maxTaskDuration, long minTimeBetweenTask, long notificationTime) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.estimatedTime = estimatedTime;
        this.minTaskDuration = minTaskDuration;
        this.maxTaskDuration = maxTaskDuration;
        this.minTimeBetweenTask = minTimeBetweenTask;
        this.notificationTime = notificationTime;
        this.taskList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public long getMinTaskDuration() {
        return minTaskDuration;
    }

    public long getMaxTaskDuration() {
        return maxTaskDuration;
    }

    public long getMinTimeBetweenTask() {
        return minTimeBetweenTask;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void add(Task task){
        taskList.add(task);
    }

    public void getTasksFromDb (AppDatabase db) {
        this.taskList = new LinkedList<>(db.taskDao().getTaskByProjectId(this.id));
    }
}
