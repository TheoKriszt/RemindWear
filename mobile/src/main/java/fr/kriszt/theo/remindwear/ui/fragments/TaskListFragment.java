package fr.kriszt.theo.remindwear.ui.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.AddTaskActivity;

public class TaskListFragment extends Fragment {

    private View rootView;
    private String userSearch = "";
    private Boolean growing = true;

    private FloatingActionButton addButton;

    private List<Task> tasksList;
    private RecyclerView tasks;
    private TaskListAdapterFragment tasksAdapter;

    private Spinner spinnerSort;
    private Spinner spinnerFilter;
    private EditText searchBar;
    private ImageView upper;
    private ImageView lower;
    private ImageView search;
    private ImageView close;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_task_list, container, false);
        return rootView;

    }

    public TaskListFragment(){}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tasksList = new ArrayList<>();
        Tasker.getInstance(getContext());
        Tasker.unserializeLists();
        Tasker.garbageCollectOld();
        Tasker.serializeLists();
        tasksList = Tasker.getInstance(getContext()).getListTasks();

        tasks = rootView.findViewById(R.id.taskList);
        tasksAdapter = new TaskListAdapterFragment(getContext(), tasksList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        tasks.setLayoutManager(mLayoutManager);
        tasks.setItemAnimator(new DefaultItemAnimator());
        tasks.setAdapter(tasksAdapter);

        addButton = (FloatingActionButton) rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddTaskActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        searchBar = (EditText) rootView.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userSearch = s.toString();
                tasksList = Tasker.getInstance(getContext()).filter(userSearch, growing);
                strangeMethode();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        upper = (ImageView) rootView.findViewById(R.id.upper);
        upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upper.setVisibility(View.GONE);
                lower.setVisibility(View.VISIBLE);
                growing = false;
                Tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                strangeMethode();
            }
        });

        lower = (ImageView) rootView.findViewById(R.id.lower);
        lower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lower.setVisibility(View.GONE);
                upper.setVisibility(View.VISIBLE);
                growing = true;
                Tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                strangeMethode();
            }
        });

        search = (ImageView) rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                upper.setVisibility(View.GONE);
                lower.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                growing = true;
                Tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                strangeMethode();
            }
        });

        close = (ImageView) rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                upper.setVisibility(View.VISIBLE);
                growing = true;
                Tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                strangeMethode();
            }
        });

    }

    public void strangeMethode(){
        tasks = rootView.findViewById(R.id.taskList);
        tasksAdapter = new TaskListAdapterFragment(getContext(), tasksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        tasks.setLayoutManager(mLayoutManager);
        tasks.setItemAnimator(new DefaultItemAnimator());
        tasks.setAdapter(tasksAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
