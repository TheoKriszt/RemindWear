package fr.kriszt.theo.remindwear.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.EditTaskActivity;

public class TaskListAdapterFragment extends RecyclerView.Adapter<TaskListAdapterFragment.MyViewHolder> {

    private List<Task> taskList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        CardView card_view;
        TextView date;
        TextView time;
        TextView repete;
        TextView description;
        public TextView category;
        Switch notified;
        public ImageView icon;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            card_view = view.findViewById(R.id.card_view);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            repete = view.findViewById(R.id.repete);
            description = view.findViewById(R.id.description);
            category = view.findViewById(R.id.category);
            notified = view.findViewById(R.id.notified);
            icon = view.findViewById(R.id.icon);
        }
    }

    TaskListAdapterFragment(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task p = taskList.get(position);
        final Task fTask = taskList.get(position);
        StringBuilder res;

        holder.icon.setImageResource(p.getCategory().getIcon());
        holder.name.setText(p.getName());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(view.getContext(), EditTaskActivity.class);
                myIntent.putExtra("idTask", fTask.getID());
                context.startActivity(myIntent);
            }
        });
        holder.card_view.setCardBackgroundColor(p.getCategory().getColor());
        res = new StringBuilder();
        if (p.getDateDeb() == null) {
            Calendar c = p.getNextDate();
            res.append(c.get(Calendar.DAY_OF_MONTH));
            res.append(" ");
            res.append(new SimpleDateFormat("MMM").format(c.getTime()));
            res.append(" ");
            res.append(c.get(Calendar.YEAR));
        } else {
            res.append(p.getDateDeb().get(Calendar.DAY_OF_MONTH));
            res.append(" ");
            res.append(new SimpleDateFormat("MMM").format(p.getDateDeb().getTime()));
            res.append(" ");
            res.append(p.getDateDeb().get(Calendar.YEAR));
        }
        holder.date.setText(res.toString());
        holder.description.setText(p.getDescription());
        res = new StringBuilder();
        res.append(p.getTimeHour());
        res.append(":");
        if (p.getTimeMinutes() < 10) {
            res.append("0");
        }
        res.append(p.getTimeMinutes());
        holder.time.setText(res.toString());

        if (p.getDateDeb() == null) {
            res = new StringBuilder();
            Boolean[] reps = p.getRepete();
            for (int i = 0; i < reps.length; i++) {
                if (reps[i]) {
                    switch (i) {
                        case 0:
                            res.append("lun. ");
                            break;
                        case 1:
                            res.append("mar. ");
                            break;
                        case 2:
                            res.append("mer. ");
                            break;
                        case 3:
                            res.append("jeu. ");
                            break;
                        case 4:
                            res.append("ven. ");
                            break;
                        case 5:
                            res.append("sam. ");
                            break;
                        case 6:
                            res.append("dim. ");
                            break;
                        default:
                            res.append("");
                            break;
                    }
                }
            }
            holder.repete.setText(res.toString());
        } else {
            holder.repete.setText(R.string.prompt_one_time);
        }
        holder.notified.setChecked(p.getIsActivatedNotification());
        holder.notified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Tasker.getInstance(context).changeWithSaveIsActivatedNotification(fTask);
            }
        });
        holder.category.setText(p.getCategory().getName());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
