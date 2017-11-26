package com.example.olaor.taskmanager;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.olaor.taskmanager.TaskManager.Data.Project;

import java.util.ArrayList;

public class ListingFragment extends ListFragment {

    public static ProjectElementAdapter adapter;
    public static final String INDEX = "com.example.olaor.taskmanager.INDEX";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        readList();
    }

    private void readList() {
        adapter = new ProjectElementAdapter(getActivity(), R.layout.list_element, new ArrayList<Project>());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id){
        showDetails(position);
    }

    private void showDetails(int position) {
        Intent intent = new Intent(getActivity(), ProjectDetails.class);
        intent.putExtra(INDEX, position);
        Log.i("index", "" + position);
        startActivity(intent);
    }

}
