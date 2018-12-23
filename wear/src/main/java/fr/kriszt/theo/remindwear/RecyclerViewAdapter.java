package fr.kriszt.theo.remindwear;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;


public class RecyclerViewAdapter
        extends WearableRecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private List<SportTypeItem> mListSportTypes;

    public RecyclerViewAdapter(List<SportTypeItem> mListSportTypes,  Context c) {
        this.mListSportTypes = mListSportTypes;

        this.context = c;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView icon;

        ViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.health_tip);
            icon =  view.findViewById(R.id.tip_details);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Log.w("Recycler type sport", "onClick: sur " + name.getText());

                    Intent startIntent = new Intent(view.getContext(), WearActivity.class);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startIntent.putExtra(Constants.KEY_SPORT_TYPE, name.getText().toString());

                    view.getContext().startActivity(startIntent);
                }
            });
        }


    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sport_type_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(mListSportTypes.get(position).name);
        holder.icon.setImageDrawable(mListSportTypes.get(position).icon);
    }

    @Override
    public int getItemCount() {
        return mListSportTypes.size();
    }


}
