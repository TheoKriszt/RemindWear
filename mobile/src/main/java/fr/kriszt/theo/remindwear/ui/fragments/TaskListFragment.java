package fr.kriszt.theo.remindwear.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.AddTaskActivity;
import fr.kriszt.theo.remindwear.voice.VoiceUtils;

public class TaskListFragment extends Fragment {

    private View rootView;
    private String userSearch = "";
    private Boolean growing = true;

    private List<Task> tasksList;
    private RecyclerView tasks;
    private TaskListAdapterFragment tasksAdapter;

    private EditText searchBar;
    private ImageView upper;
    private ImageView lower;
    private ImageView search;
    private ImageView close;
    private Tasker tasker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        tasker = Tasker.getInstance(getContext());
        return rootView;

    }

    public TaskListFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tasksList = new ArrayList<>();
        tasker.unserializeLists();

        tasker.garbageCollectOld();
        tasker.serializeLists();
        tasker.sort(true);
        tasksList = tasker.getListTasks();

        tasks = rootView.findViewById(R.id.taskList);
        tasksAdapter = new TaskListAdapterFragment(getContext(), tasksList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        tasks.setLayoutManager(mLayoutManager);
        tasks.setItemAnimator(new DefaultItemAnimator());
        tasks.setAdapter(tasksAdapter);

        FloatingActionButton voiceButton = rootView.findViewById(R.id.voiceButton);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoiceUtils.startSpeechRecognizer(TaskListFragment.this);

            }
        });

        FloatingActionButton addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddTaskActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(myIntent);
            }
        });

        searchBar = rootView.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userSearch = s.toString();
                tasksList = Tasker.getInstance(getContext()).filter(userSearch, growing);
                setItemsAdapters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        upper = rootView.findViewById(R.id.upper);
        upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upper.setVisibility(View.GONE);
                lower.setVisibility(View.VISIBLE);
                growing = false;
                tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                setItemsAdapters();
            }
        });

        lower = rootView.findViewById(R.id.lower);
        lower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lower.setVisibility(View.GONE);
                upper.setVisibility(View.VISIBLE);
                growing = true;
                tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                setItemsAdapters();
            }
        });

        search = rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                upper.setVisibility(View.GONE);
                lower.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                growing = true;
                tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                setItemsAdapters();
            }
        });

        close = rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                upper.setVisibility(View.VISIBLE);
                growing = true;
                tasker.sort(growing);
                tasksList = Tasker.getInstance(getContext()).getListTasks();
                setItemsAdapters();
            }
        });

    }

    public void setItemsAdapters() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = VoiceUtils.getRecognizedSpeech(requestCode, resultCode, data, this.getContext());
        if (intent != null) {
            if (Objects.requireNonNull(intent.getComponent()).getClassName().toLowerCase().contains("service")) {
                Objects.requireNonNull(getContext()).startService(intent);
            } else {
                startActivity(intent);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}
