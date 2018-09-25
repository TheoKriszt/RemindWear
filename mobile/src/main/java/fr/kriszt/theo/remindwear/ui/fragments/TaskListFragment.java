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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.AddTaskActivity;

public class TaskListFragment extends Fragment {

    View rootView;
    FloatingActionButton addButton;

    private List<Task> tasksList;
    private RecyclerView tasks;
    private TaskListAdapterFragment tasksAdapter;

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
        Tasker tasker = new Tasker(getContext());
        tasker.unserializeLists();
        tasksList = tasker.getListTasks();

        Log.e("aaaaaaaaaaaaaaaaaaaaa", tasksList.toString());

        tasks = rootView.findViewById(R.id.taskList);
        tasksAdapter = new TaskListAdapterFragment(getContext(), tasksList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        tasks.setLayoutManager(mLayoutManager);
        tasks.setItemAnimator(new DefaultItemAnimator());
        tasks.setAdapter(tasksAdapter);

        /*
        RecyclerView.LayoutManager mNewLayoutManager = new GridLayoutManager(getActivity(), 2);
        tasks.setLayoutManager(mNewLayoutManager);
        tasks.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        tasks.setItemAnimator(new DefaultItemAnimator());
        tasks.setAdapter(tasksAdapter);
        */

        addButton = (FloatingActionButton) rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddTaskActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
