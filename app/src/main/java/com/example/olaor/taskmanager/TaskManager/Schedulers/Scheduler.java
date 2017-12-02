package com.example.olaor.taskmanager.TaskManager.Schedulers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.olaor.taskmanager.CalendarService;
import com.example.olaor.taskmanager.Settings;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;


public class Scheduler {

    private Project project;
    private long startTimeDay;
    private long endTimeDay;

    public Scheduler(Project project) {
        this.project = project;
    }

    public void schedule(List<Task> taskList, Context context, long startFrom) throws Exception {
        getSettingsFromSharedPref(context);
        Log.i("schedule", "Start scheduling with startFrom = " + startFrom);
        long dayRemainder = 24 * 60 * 60 * 1000;
        ListIterator<Task> iterator = taskList.listIterator();
        long spentTime = 0L;
        Task previousTask = null;
        Task nextTask = null;
        Task projectTask = null;
        long startTime = Math.max(project.getStartDate(), startFrom);
        while (iterator.hasNext() && spentTime <= project.getEstimatedTime()) {
            previousTask = nextTask;
            nextTask = iterator.next();
            long startNewTaskTime = previousTask == null ? startTime : previousTask.getEndDate();
            long endNewTaskTime = nextTask.getStartDate();
            if (projectTask != null) {
                startNewTaskTime = Math.max(startNewTaskTime, projectTask.getEndDate() + project.getMinTimeBetweenTask());
            }
            //for settings
            Log.i("day", new Date(startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay).toString());
            if (startTimeDay > (startNewTaskTime % dayRemainder)){
                startNewTaskTime = startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay;
            }
            if (endTimeDay < (startNewTaskTime % dayRemainder)){
                startNewTaskTime = startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay + dayRemainder;
            }

            endNewTaskTime = Math.min(endNewTaskTime, startNewTaskTime + project.getMaxTaskDuration());
            endNewTaskTime = Math.min(endNewTaskTime, startNewTaskTime + (project.getEstimatedTime() - spentTime));
            //for settings
            endNewTaskTime = Math.min(endNewTaskTime, startNewTaskTime - (startNewTaskTime % dayRemainder) + endTimeDay);
            if (endNewTaskTime - startNewTaskTime < project.getMinTaskDuration() && endNewTaskTime - startNewTaskTime != project.getEstimatedTime() - spentTime) {
                continue;
            }
            Task newTask = new Task(startNewTaskTime, endNewTaskTime, project.id, project.getName());
            spentTime += endNewTaskTime - startNewTaskTime;
            iterator.previous();
            iterator.add(newTask);
            nextTask = newTask;
            project.add(newTask);
            addToDbCalendar(newTask, context);
            projectTask = newTask;
        }
        while (spentTime < project.getEstimatedTime()) {
            long startNewTaskTime = taskList.size() == 0 ? startTime : taskList.get(taskList.size() - 1).getEndDate();
            if (projectTask != null) {
                startNewTaskTime = Math.max(startNewTaskTime, projectTask.getEndDate() + project.getMinTimeBetweenTask());
            }
            //for settings
            Log.i("time", "sntt " + startNewTaskTime + " t " + (startNewTaskTime % dayRemainder));
            Log.i("day", new Date(startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay).toString());
            if (startTimeDay > (startNewTaskTime % dayRemainder)){
                startNewTaskTime = startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay;
            }
            if (endTimeDay < (startNewTaskTime % dayRemainder)){
                startNewTaskTime = startNewTaskTime - (startNewTaskTime % dayRemainder) + startTimeDay + dayRemainder;
            }
            long endNewTaskTime = Math.min(startNewTaskTime + project.getMaxTaskDuration(), startNewTaskTime + (project.getEstimatedTime() - spentTime));
            //for settings
            endNewTaskTime = Math.min(endNewTaskTime, startNewTaskTime - (startNewTaskTime % dayRemainder) + endTimeDay);
            Log.i("day end", new Date(endNewTaskTime).toString());
            Task newTask = new Task(startNewTaskTime, endNewTaskTime, project.id, project.getName());
            spentTime += endNewTaskTime - startNewTaskTime;
            iterator.add(newTask);
            project.add(newTask);
            addToDbCalendar(newTask, context);
            projectTask = newTask;
        }
    }

    private void addToDbCalendar(Task task, Context context) {
        CalendarService.createCalendar(context);
        CalendarService.insertTasksToCalendar(task, context);
        task.id = TimeLine.db.taskDao().addTask(task);
        Log.i("addToDbCalendar", "Added task with id: " + task.id +
                " to project with id: " + task.getProjectId());
    }

    public void clearProject(List<Task> timeLine, Context contex) {
        ListIterator<Task> iterator = project.getTaskList().listIterator();
        Task t;
        while (iterator.hasNext()) {
            t = iterator.next();
            timeLine.remove(t);
            iterator.remove();
            TimeLine.db.taskDao().removeIdById(t.id);
            int d = CalendarService.deleteTaskFromCalendar(t.getIdInCalendar(), contex);
            Log.i("deleted id :", "" + d);
        }
    }

    private void getSettingsFromSharedPref(Context context){
        SharedPreferences sp = context.getSharedPreferences(Settings.SETTINGS_PREF, 0);
        startTimeDay = sp.getLong(Settings.SETTINGS_START, 0);
        Log.i("SharedPref", "start: " + startTimeDay);
        endTimeDay = sp.getLong(Settings.SETTINGS_END, 0);
        Log.i("SharedPref", "end: " + endTimeDay);
    }
}
