package com.example.olaor.taskmanager.TaskManager.Data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long addTask(Task task);

    @Query("SELECT * FROM task ORDER BY start_date")
    List<Task> getAllTask();

    @Query("SELECT * FROM task WHERE id = :taskId")
    Task getTaskById(long taskId);

    @Query("DELETE FROM task")
    void removeAllTask();

    @Query("DELETE FROM task WHERE id = :taskId")
    public void removeIdById(long taskId);

    @Query("SELECT * FROM task WHERE project_id = :id ORDER BY start_date")
    public List<Task> getTaskByProjectId(long id);

}
