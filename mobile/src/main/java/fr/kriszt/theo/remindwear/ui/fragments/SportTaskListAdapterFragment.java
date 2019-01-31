package fr.kriszt.theo.remindwear.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.Date;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.SportDetailsActivity;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;


public class SportTaskListAdapterFragment extends RecyclerView.Adapter<SportTaskListAdapterFragment.SportTaskHolder> {

    private List<SportTask> taskSportList;
    private Context context;

    public class SportTaskHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        public TextView name;
        TextView date;
        TextView time;
        TextView description;
        public TextView category;
        public ImageView icon;

        SportTaskHolder(View view) {
            super(view);
            card_view = view.findViewById(R.id.card_view);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            description = view.findViewById(R.id.description);
            category = view.findViewById(R.id.category);
            icon = view.findViewById(R.id.icon);
        }
    }

    SportTaskListAdapterFragment(Context context, List<SportTask> taskSportList) {
        this.context = context;
        this.taskSportList = taskSportList;
        SportTask.bindTasks(taskSportList, context);
    }

    @NonNull
    @Override
    public SportTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sport_task_list_adapter, parent, false);

        return new SportTaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SportTaskHolder holder, int position) {
        SportTask p = taskSportList.get(position);
        final SportTask fTask = taskSportList.get(position);

        Category sport = Tasker.getInstance(context).getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
        Category cat = p.getCategory();
        int icon = cat == null ? sport.getIcon() : cat.getIcon();
        if (p.getDataset() != null) {
            SportType sportType = p.getDataset().getSportType();
            icon = sportType.getIcon();
        }
        holder.icon.setImageResource(icon);
        holder.icon.setColorFilter(Color.BLACK);

        holder.name.setText(p.getName());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent myIntent = new Intent(view.getContext(), SportDetailsActivity.class);
                myIntent.putExtra(Constants.KEY_TASK_ID, fTask.getID());
                context.startActivity(myIntent);
            }
        });
        int color = cat == null ? sport.getColor() : cat.getColor();
        holder.card_view.setCardBackgroundColor(color);

        setStartDate(p, holder);

        holder.description.setText(p.getDescription());

        setStartTime(p, holder);


        String catName = cat == null ? sport.getName() : cat.getName();
        holder.category.setText(catName);
    }

    @SuppressLint("SimpleDateFormat")
    private void setStartDate(@NonNull SportTask p, SportTaskHolder holder) {
        String res = "";
        Calendar c = p.getFirstDate();

        if (c != null) {
            res += c.get(Calendar.DAY_OF_MONTH);
            res += " ";
            res += new SimpleDateFormat("MMM").format(c.getTime());
            res += " ";
            res += c.get(Calendar.YEAR);
        }
        holder.date.setText(res);
    }

    private void setStartTime(SportTask p, SportTaskHolder holder) {
        Calendar c = p.getFirstDate();
        if (c != null) {
            Date d = c.getTime();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String dateString = formatter.format(d);
            holder.time.setText(dateString);
        }
    }

    @Override
    public int getItemCount() {
        return taskSportList.size();
    }


}
