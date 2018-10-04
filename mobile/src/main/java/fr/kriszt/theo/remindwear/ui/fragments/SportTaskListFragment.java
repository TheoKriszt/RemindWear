package fr.kriszt.theo.remindwear.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;


public class SportTaskListFragment extends Fragment {

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
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksSportList = new ArrayList<>();
        tasker.unserializeLists();
        tasker.garbageCollectOld();
        tasker.serializeLists();
        tasker.sportSort(true);

        tasksSportList = Tasker.getInstance(getContext()).getListSportTasks();

        //TODO REMOVE
        tasker.unserializeLists();
        if(Tasker.getInstance(getContext()).getListSportTasks().size() <= 0){
            SportTask s = new SportTask("e","d", new Category("n", 0, 0),
                    new GregorianCalendar(), 30,23, 12, new Boolean[]{},
                    50, 60, 364, 788944321);
            Tasker.getInstance(getContext()).addSportTask(s);
            tasksSportList.add(s);
        }
        tasker.serializeLists();

        sportList = rootView.findViewById(R.id.sportList);
        tasksSportAdapter = new SportTaskListAdapterFragment(getContext(), tasksSportList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        sportList.setLayoutManager(mLayoutManager);
        sportList.setItemAnimator(new DefaultItemAnimator());
        sportList.setAdapter(tasksSportAdapter);

        searchBar = (EditText) rootView.findViewById(R.id.searchBar);
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

        upper = (ImageView) rootView.findViewById(R.id.upper);
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

        lower = (ImageView) rootView.findViewById(R.id.lower);
        lower = (ImageView) rootView.findViewById(R.id.lower);
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

        close = (ImageView) rootView.findViewById(R.id.close);
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
