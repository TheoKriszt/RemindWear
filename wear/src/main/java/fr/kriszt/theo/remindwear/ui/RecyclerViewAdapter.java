package fr.kriszt.theo.remindwear.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.ui.activity.WearActivity;
import fr.kriszt.theo.shared.Constants;


public class RecyclerViewAdapter
        extends WearableRecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Integer taskId;
    private List<SportTypeItem> mListSportTypes;

    public RecyclerViewAdapter(List<SportTypeItem> mListSportTypes, Integer taskId) {
        this.mListSportTypes = mListSportTypes;

        this.taskId = taskId;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView icon;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.health_tip);
            icon = view.findViewById(R.id.tip_details);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent startIntent = new Intent(view.getContext(), WearActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startIntent.putExtra(Constants.KEY_SPORT_TYPE, name.getText().toString());
                    startIntent.putExtra(Constants.KEY_TASK_ID, RecyclerViewAdapter.this.taskId);

                    view.getContext().startActivity(startIntent);
                }
            });
        }


    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sport_type_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mListSportTypes.get(position).name);
        holder.icon.setImageDrawable(mListSportTypes.get(position).icon);
    }

    @Override
    public int getItemCount() {
        return mListSportTypes.size();
    }


}
