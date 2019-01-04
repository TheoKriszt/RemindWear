package fr.kriszt.theo.remindwear.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.AddTaskActivity;
import fr.kriszt.theo.remindwear.voice.VoiceUtils;
import fr.kriszt.theo.shared.SportType;

import static android.app.Activity.RESULT_OK;

public class TaskListFragment extends Fragment {

    private static final String TAG = TaskListFragment.class.getSimpleName();
    private View rootView;
    private String userSearch = "";
    private Boolean growing = true;

    private FloatingActionButton addButton;
    private FloatingActionButton voiceButton;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_task_list, container, false);

        tasker = Tasker.getInstance(getContext());
        return rootView;

    }

    public TaskListFragment(){}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tasksList = new ArrayList<>();
        tasker.unserializeLists();



//        if (tasker.getListTasks().isEmpty()){
//            Log.w(TAG, "onViewCreated: Tâche de test");
//            GregorianCalendar cal = new GregorianCalendar();
//            cal.add(Calendar.MINUTE, 1);
//            int timeHour = cal.get(Calendar.HOUR_OF_DAY);
//            int timeMinutes= cal.get(Calendar.MINUTE);
//            Category sport = tasker.getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
//            Task testTask = new Task("Tâche de test", "Créée pour l'exemple", sport, cal, 0, timeHour, timeMinutes);
//            tasker.addTask(testTask);
//
//            Toast.makeText(getContext(), "La tâche d'exemple commencera dans " + -testTask.getRemainingTime(TimeUnit.SECONDS) + " secondes", Toast.LENGTH_SHORT).show();
//            tasker.serializeLists();
//        }
//        else {
//            Log.w(TAG, "onViewCreated: Tâches présentes:  " + tasker.getListTasks().size());
//        }

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

        voiceButton = rootView.findViewById(R.id.voiceButton);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(TAG, "onClick: Clic sur bouton voix");

                VoiceUtils.startSpeechRecognizer(TaskListFragment.this);

            }
        });

        addButton = rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddTaskActivity.class);
                //myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
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
                tasker.sort(growing);
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
                tasker.sort(growing);
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
                tasker.sort(growing);
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
                tasker.sort(growing);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = VoiceUtils.getRecognizedSpeech(requestCode, resultCode, data, this.getContext());


        super.onActivityResult(requestCode, resultCode, data);
    }


}
