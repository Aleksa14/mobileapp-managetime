package com.example.olaor.taskmanager;

import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.Scheduler;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ShedulerUnitTest {



    @Test
    public void sheduleEmptyTaskList() throws Exception {
        List<Task> taskList = new LinkedList<Task>();
        Project project = new Project(System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7, 17280000, 3600000, 7200000, 1000 * 60 * 60 * 12);
        Scheduler scheduler = new Scheduler(project);
        scheduler.schedule(taskList);
        System.out.println("table size: " + taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println(new Date(taskList.get(i).getStartDate()));
            System.out.println(new Date(taskList.get(i).getEndDate()));
            System.out.println(taskList.get(i).getDuration());
            System.out.println("---------------------------");
        }
    }

    @Test
    public void scheduleNotEmptyLisy() throws Exception {
        List<Task> taskList = new LinkedList<Task>();
        long d1 = System.currentTimeMillis();
        long d2 = System.currentTimeMillis() + (long)hourToMilis(1);
        long d3 = System.currentTimeMillis() + (long)hourToMilis(2.5);
        long d4 = System.currentTimeMillis() + (long)hourToMilis(3.5);
        long d5 = System.currentTimeMillis() + (long)hourToMilis(6.5);
        long d6 = System.currentTimeMillis() + (long)hourToMilis(9.5);
        long d7 = System.currentTimeMillis() + (long)hourToMilis(15.5);
        long d8 = System.currentTimeMillis() + (long)hourToMilis(16.5);
        taskList.add(new Task(d1, d2, (long)hourToMilis(1.0)));
        taskList.add(new Task(d3, d4, (long)hourToMilis(1.0)));
        taskList.add(new Task(d5, d6, (long)hourToMilis(3.0)));
        taskList.add(new Task(d7, d8, (long)hourToMilis(1.0)));
        Project project = new Project(System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7, 17280000, (long) hourToMilis(1.0), (long) hourToMilis(2.0), (long) hourToMilis(3));
        Scheduler scheduler = new Scheduler(project);
        scheduler.schedule(taskList);
        System.out.println("table size: " + taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            System.out.println(new Date(taskList.get(i).getStartDate()));
            System.out.println(new Date(taskList.get(i).getEndDate()));
            System.out.println(taskList.get(i).getDuration());
            System.out.println("---------------------------");
        }
    }

    double hourToMilis(double hour) {
        return hour * 60 * 60 * 1000;
    }
}
