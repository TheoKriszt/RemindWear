package fr.kriszt.theo.remindwear.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.ui.activities.SportDetailsActivity;


public class SportTaskListAdapterFragment extends RecyclerView.Adapter<SportTaskListAdapterFragment.MyViewHolder> {

    private List<SportTask> taskSportList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView card_view;
        public TextView name;
        public TextView date;
        public TextView time;
        public TextView description;
        public TextView category;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            card_view  = (CardView) view.findViewById(R.id.card_view);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            description = (TextView) view.findViewById(R.id.description);
            category = (TextView) view.findViewById(R.id.category);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public SportTaskListAdapterFragment(Context context, List<SportTask> taskSportList) {
        this.context =context;
        this.taskSportList = taskSportList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sport_task_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SportTask p = taskSportList.get(position);
        final SportTask fTask = taskSportList.get(position);
        String res;

        holder.icon.setImageResource(p.getCategory().getIcon());

        holder.name.setText(p.getName());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(view.getContext(), SportDetailsActivity.class);
                //myIntent.setFlags(myIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                myIntent.putExtra("idSportTask", fTask.getID());
                context.startActivity(myIntent);
            }
        });
        holder.card_view.setCardBackgroundColor(p.getCategory().getColor());

        res = "";
        if(p.getDateDeb() == null){
            Calendar c = p.getNextDate();
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

        holder.category.setText(p.getCategory().getName());


    }

    @Override
    public int getItemCount() {
        return taskSportList.size();
    }




}
