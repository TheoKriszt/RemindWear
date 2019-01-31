package fr.kriszt.theo.remindwear.ui.activity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.sensors.SensorUtils;
import fr.kriszt.theo.remindwear.ui.RecyclerViewAdapter;
import fr.kriszt.theo.remindwear.ui.SportTypeItem;
import fr.kriszt.theo.shared.Constants;

public class ChooseSportActivity extends WearableActivity {

    private RecyclerViewAdapter mAdapter;
    private List<SportTypeItem> myDataSet = new ArrayList<>();
    private SensorManager sensorManager;
    private String sportType;

    @BindView(R.id.recycler_launcher_view)
    public WearableRecyclerView mWearableRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer taskId = null;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get(Constants.KEY_PARAMS) != null) {
                HashMap<String, String> params = (HashMap<String, String>) getIntent().getExtras().get(Constants.KEY_PARAMS);

                assert params != null;
                if (params.containsKey(Constants.KEY_TASK_ID)) {
                    taskId = Integer.parseInt(Objects.requireNonNull(params.get(Constants.KEY_TASK_ID)));
                }

                if (params.containsKey(Constants.KEY_SPORT_TYPE)) {
                    sportType = params.get(Constants.KEY_SPORT_TYPE);
                }
            }

        }

        setContentView(R.layout.activity_choose_sport);
        ButterKnife.bind(this);

        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));

        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        boolean hasGPS = SensorUtils.isGPSAvailable(this);
        boolean hasPodo = SensorUtils.isPodometerAvailable(sensorManager);
        boolean hasCardio = SensorUtils.isHeartRateAvailable(this);

        myDataSet.addAll(SportTypeItem.getAvailableSportTypes(this, hasGPS, hasPodo, hasCardio));

        if (sportType != null) {

            boolean canStart = false;

            for (SportTypeItem sti : myDataSet) {
                if (sti.name.equals(sportType)) {
                    canStart = true;
                    break;
                }
            }

            if (canStart) {
                Intent startIntent = new Intent(this, WearActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startIntent.putExtra(Constants.KEY_SPORT_TYPE, sportType);
                startIntent.putExtra(Constants.KEY_TASK_ID, taskId);

                startActivity(startIntent);
            }

        }


        mAdapter = new RecyclerViewAdapter(myDataSet, taskId);
        mWearableRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
