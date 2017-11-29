package com.example.olaor.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.olaor.taskmanager.TaskManager.Data.AppDatabase;
import com.example.olaor.taskmanager.TaskManager.Data.Project;
import com.example.olaor.taskmanager.TaskManager.Data.Task;
import com.example.olaor.taskmanager.TaskManager.Schedulers.Scheduler;
import com.example.olaor.taskmanager.TaskManager.Schedulers.TimeLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ProjectElementAdapter extends ArrayAdapter<Project> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Project> projects = null;
    private AppDatabase db;
    private final Handler handler;

    public ProjectElementAdapter(Context context, int layoutResourceId, ArrayList<Project> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        handler = new Handler(context.getMainLooper());
        db = AppDatabase.getDatabase(context);
        this.projects = data;
        new Thread() {
            @Override
            public void run() {
                projects.addAll(db.projectDao().getAllProject());
                for (Project p : projects) {
                    Log.i("projectInfo", p.getName());
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        callBack();
                    }
                });

            }
        }.start();
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public void callBack() {
        notifyDataSetChanged();
        Log.i("ProjectViewAdapter", "data size " + projects.size());
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("ProjectViewAdapter", "get view " + position);
        View row = view;
        ProjectHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProjectHolder();
            holder.name = (TextView) row.findViewById(R.id.element_title);
            row.setTag(holder);
        } else {
            holder = (ProjectHolder) row.getTag();
        }

        Project project = projects.get(position);
        holder.name.setText(project.getName());

        return row;
    }

    private class ProjectHolder {
        TextView name;
    }

    public void remove(final int i) {
        TimeLine.db =
                AppDatabase.getDatabase(context);
        new Thread() {
            @Override
            public void run() {
                projects.get(i).getTasksFromDb(TimeLine.db);
                Log.i("Project tasks: ", projects.get(i).getTaskList().size()+"");
                for (Task t : projects.get(i).getTaskList()){
                    Log.i("tasks: ", t.id+"");
                }
                List<Task> timeLine = new LinkedList<>(TimeLine.db.taskDao().getAllTask());
                ListIterator<Task> iterator = projects.get(i).getTaskList().listIterator();
                Task t;
                while (iterator.hasNext()) {
                    t = iterator.next();
                    Log.i("task id: ", t.id+"");
                    timeLine.remove(t);
                    iterator.remove();
                    TimeLine.db.taskDao().removeIdById(t.id);
                    int d = CalendarService.deleteTaskFromCalendar(t.getIdInCalendar(), context);
                    Log.i("deleted id :", "" + d);
                }
                TimeLine.db.projectDao().removeProjectById(projects.get(i).id);
                projects.remove(i);
                runOnUiThread(new Runnable() {
                    public void run() {
                        callBack();
                    }
                });
                for (Project p : projects){
                    if (p.getEndDate() > projects.get(i).getEndDate()){
                        TimeLine.sheduleNewProject(p, context);
                    }
                }
            }
        }.start();

    }

}
