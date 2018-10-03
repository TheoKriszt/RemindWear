package fr.kriszt.theo.remindwear.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.ui.activities.SportDetailsActivity;


public class TaskSportListAdapterFragment extends RecyclerView.Adapter<TaskSportListAdapterFragment.MyViewHolder> {

    private List<SportTask> taskSportList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView card_view;
        /*public TextView name;
        public TextView date;
        public TextView time;
        public TextView repete;
        public TextView description;
        public TextView category;
        public Switch notified;
        public ImageView icon;*/

        public MyViewHolder(View view) {
            super(view);
            card_view  = (CardView) view.findViewById(R.id.card_view);
            /*name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            repete = (TextView) view.findViewById(R.id.repete);
            description = (TextView) view.findViewById(R.id.description);
            category = (TextView) view.findViewById(R.id.category);
            notified = (Switch) view.findViewById(R.id.notified);
            icon = (ImageView) view.findViewById(R.id.icon);*/
        }
    }

    public TaskSportListAdapterFragment(Context context, List<SportTask> taskSportList) {
        this.context =context;
        this.taskSportList = taskSportList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO modifier xml associé car copiage de l'autre liste
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task_sport_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task p = taskSportList.get(position);
        final Task fTask = taskSportList.get(position);
        String res;

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(view.getContext(), SportDetailsActivity.class);
                //myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                //myIntent.putExtra("idTask", fTask.getID());
                context.startActivity(myIntent);
            }
        });
        holder.card_view.setCardBackgroundColor(p.getCategory().getColor());

    }

    @Override
    public int getItemCount() {
        return taskSportList.size();
    }




}
