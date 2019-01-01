package fr.kriszt.theo.remindwear.ui.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataPoint;
import fr.kriszt.theo.shared.data.SportDataSet;


public class SportTaskListFragment extends Fragment {

    private static final String TAG = SportTaskListFragment.class.getSimpleName();
    View rootView;
    private String userSearch = "";
    private Boolean growing = true;

    private List<SportTask> tasksSportList;
    private RecyclerView sportList;
    private SportTaskListAdapterFragment tasksSportAdapter;

    private EditText searchBar;
    private ImageView upper;
    private ImageView lower;
    private ImageView search;
    private ImageView close;
    private Tasker tasker;

    public SportTaskListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_sport_list, container, false);
        tasker  = Tasker.getInstance(getContext());
        tasker.addObserver(this);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tasker myTasker = Tasker.getInstance(getContext());

        tasksSportList = new ArrayList<>();
        myTasker.unserializeLists();
        myTasker.garbageCollectOld();
        myTasker.serializeLists();


        tasksSportList = myTasker.getListSportTasks();

        //TODO REMOVE
        myTasker.unserializeLists();
//        if(myTasker.getListSportTasks().isEmpty()){
//            Category sport = myTasker.getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
//            Calendar calendar = new GregorianCalendar();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//            Task referer = new Task("Tâche de test", "Une petite description", sport, calendar, 0, hour, minute);
//
//            SportDataSet sportDataSet = new SportDataSet(SportType.SPORT_BIKE, true, true, true);
//
//            sportDataSet.addPoint(new SportDataPoint(new Coordinates(42, 1.83, 100, 20), 5, 75, 0.22f));
//            sportDataSet.addPoint(new SportDataPoint(new Coordinates(42.1, 1.83, 110, 30), 10, 60, 0.44f));
//            sportDataSet.addPoint(new SportDataPoint(new Coordinates(42, 1.84, 120, 20), 15, 63, 0.66f));
//            sportDataSet.addPoint(new SportDataPoint(new Coordinates(42.1, 1.83, 110, 10), 25, 115, 1.24f));
//
//            myTasker.addTask(referer);
//
//            SportTask s = new SportTask(referer);
//            s.setDataset(sportDataSet);
//
//            myTasker.addSportTask(s);
//            Log.w(TAG, "onViewCreated: CREATED Sport Task : " + s);
//
//            tasksSportList.add(s);
//        }

        myTasker.sportSort(false);
        myTasker.serializeLists();

//        Log.w(TAG, "onViewCreated: on arrete de deconner");
//        Log.w(TAG, "onViewCreated: nb Taches normales " + myTasker.getListTasks().size());
//        Log.w(TAG, "onViewCreated: nb Taches Sport    " + myTasker.getListSportTasks().size());

        sportList = rootView.findViewById(R.id.sportList);
        tasksSportAdapter = new SportTaskListAdapterFragment(getContext(), tasksSportList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        sportList.setLayoutManager(mLayoutManager);
        sportList.setItemAnimator(new DefaultItemAnimator());
        sportList.setAdapter(tasksSportAdapter);

        searchBar = rootView.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userSearch = s.toString();
                tasksSportList = Tasker.getInstance(getContext()).sportFilter(userSearch, growing);
                updateRecyclerView();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        upper = rootView.findViewById(R.id.upper);
        upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upper.setVisibility(View.GONE);
                lower.setVisibility(View.VISIBLE);
                growing = false;
                tasker.sportSort(growing);
                tasksSportList = Tasker.getInstance(getContext()).getListSportTasks();
                updateRecyclerView();
            }
        });

        lower = rootView.findViewById(R.id.lower);
        lower = rootView.findViewById(R.id.lower);
        lower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lower.setVisibility(View.GONE);
                upper.setVisibility(View.VISIBLE);
                growing = true;
                tasker.sportSort(growing);
                tasksSportList = Tasker.getInstance(getContext()).getListSportTasks();
                updateRecyclerView();
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
                tasker.sportSort(growing);
                tasksSportList = Tasker.getInstance(getContext()).getListSportTasks();
                updateRecyclerView();
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
                tasker.sportSort(growing);
                tasksSportList = Tasker.getInstance(getContext()).getListSportTasks();
                updateRecyclerView();
            }
        });


    }

    public void updateRecyclerView(){
        Log.w(TAG, "updateRecyclerView: ");
        sportList = rootView.findViewById(R.id.sportList);
        tasksSportAdapter = new SportTaskListAdapterFragment(getContext(), tasksSportList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        sportList.setLayoutManager(mLayoutManager);
        sportList.setItemAnimator(new DefaultItemAnimator());
        sportList.setAdapter(tasksSportAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
