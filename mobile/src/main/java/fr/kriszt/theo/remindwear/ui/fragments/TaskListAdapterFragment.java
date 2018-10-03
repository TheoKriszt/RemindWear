package fr.kriszt.theo.remindwear.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.EditTaskActivity;

public class TaskListAdapterFragment extends  RecyclerView.Adapter<TaskListAdapterFragment.MyViewHolder> {

    private List<Task> taskList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CardView card_view;
        public TextView date;
        public TextView time;
        public TextView repete;
        public TextView description;
        public TextView category;
        public Switch notified;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            card_view  = (CardView) view.findViewById(R.id.card_view);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            repete = (TextView) view.findViewById(R.id.repete);
            description = (TextView) view.findViewById(R.id.description);
            category = (TextView) view.findViewById(R.id.category);
            notified = (Switch) view.findViewById(R.id.notified);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public TaskListAdapterFragment(Context context, List<Task> taskList) {
        this.context =context;
        this.taskList = taskList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task p = taskList.get(position);
        final Task fTask = taskList.get(position);
        String res;

        holder.icon.setImageResource(p.getCategory().getIcon());
        holder.name.setText(p.getName());
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(view.getContext(), EditTaskActivity.class);
                //myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                myIntent.putExtra("idTask", fTask.getID());
                context.startActivity(myIntent);
            }
        });
        holder.card_view.setCardBackgroundColor(p.getCategory().getColor());
        res = "";
        if(p.getDateDeb() == null){
            int day=0;
            for(int i=0; i<p.getRepete().length; i++){
                if(p.getRepete()[i]){
                    day = i;
                    break;
                }
            }
            Calendar c = new GregorianCalendar();
            Calendar cTemp = new GregorianCalendar();
            c.add(Calendar.DAY_OF_MONTH, day+2 - cTemp.get(Calendar.DAY_OF_WEEK));


            res += c.get(Calendar.DAY_OF_MONTH);
            res += " ";
            res += new SimpleDateFormat("MMM").format(c.getTime());
            res += " ";
            res += c.get(Calendar.YEAR);
        }else {
            res += p.getDateDeb().get(Calendar.DAY_OF_MONTH);
            res += " ";
            res += new SimpleDateFormat("MMM").format(p.getDateDeb().getTime());
            res += " ";
            res += p.getDateDeb().get(Calendar.YEAR);
        }
        holder.date.setText(res);
        holder.description.setText(p.getDescription());
        res= "";
        res += p.getTimeHour();
        res += ":";
        if(p.getTimeMinutes() <10){
            res += "0";
        }
        res += p.getTimeMinutes();
        holder.time.setText(res);

        if(p.getDateDeb() == null){
            res = "";
            Boolean[] reps = p.getRepete();
            for(int i=0; i<reps.length; i++){
                if(reps[i]){
                    switch (i){
                        case 0:
                            res += "lun. ";
                            break;
                        case 1:
                            res += "mar. ";
                            break;
                        case 2:
                            res += "mer. ";
                            break;
                        case 3:
                            res += "jeu. ";
                            break;
                        case 4:
                            res += "ven. ";
                            break;
                        case 5:
                            res += "sam. ";
                            break;
                        case 6:
                            res += "dim. ";
                            break;
                        default: res += "";
                            break;
                    }
                }
            }
            holder.repete.setText(res);
        }else{
            holder.repete.setText(R.string.prompt_one_time);
        }
        holder.notified.setChecked(p.getIsActivatedNotification());
        holder.notified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Tasker.changeWithSaveIsActivatedNotification(fTask);
            }
        });
        holder.category.setText(p.getCategory().getName());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

}
