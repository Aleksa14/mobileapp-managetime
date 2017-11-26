package com.example.olaor.taskmanager.TaskManager.Schedulers;

import android.content.Context;

import com.example.olaor.taskmanager.CalendarService;
import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;

import java.util.LinkedList;
import java.util.List;

public class TimeLine implements Runnable{

    private static List<Project> projectList;
    private static List<Task> taskList;
    private Project projectToSchedule;
    private final static Object locker = new Object();
    public static AppDatabase db;
    public Context context;

    public TimeLine(Project project, Context context){
        this.projectToSchedule = project;
        this.context = context;
    }

    public static void sheduleNewProject(Project project, Context context){
        new Thread(new TimeLine(project, context)).start();
    }

    @Override
    public void run() {
        synchronized (locker){
            projectList = db.projectDao().getAllProject();
            taskList = db.taskDao().getAllTask();
            List<Scheduler> schedulers = new LinkedList<>();
            for (int i = projectList.size() -1; i >= 0; i--){
                if (projectList.get(i).getEndDate() > projectToSchedule.getEndDate()){
                    projectList.get(i).getTasksFromDb(db);
                    Scheduler s = new Scheduler(projectList.get(i));
                    schedulers.add(s);
                    s.clearProject(taskList, context);
                }else {
                    break;
                }
            }
            schedulers.add(new Scheduler(projectToSchedule));
            for (int i = schedulers.size() - 1; i >= 0; i--){
                try {
                    schedulers.get(i).schedule(taskList, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            CalendarService.createCalendar(context);
//            for (Task t : taskList) {
//                CalendarService.insertTasksToCalendar(t, context);
//            }
        }
    }
}

