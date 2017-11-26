package com.example.olaor.taskmanager.TaskManager.Data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProjectDao {
    @Insert
    long addProject(Project project);

    @Query("SELECT * FROM project ORDER BY endDate")
    List<Project> getAllProject();

    @Query("DELETE FROM project")
    void removeAllProject();

    @Query("DELETE FROM project WHERE id = :projectId")
    void removeProjectById(long projectId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProject(Project project);
}
