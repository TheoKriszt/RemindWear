package fr.kriszt.theo.remindwear.ui.activity;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.sensors.SensorUtils;
import fr.kriszt.theo.remindwear.ui.RecyclerViewAdapter;
import fr.kriszt.theo.remindwear.ui.SportTypeItem;
import fr.kriszt.theo.shared.Constants;

public class ChooseSportActivity extends WearableActivity  {



    private static final String TAG = "sportChoose_activity";

    private RecyclerViewAdapter mAdapter;
    private List<SportTypeItem> myDataSet = new ArrayList<>();
    private SensorManager sensorManager;

    @BindView(R.id.recycler_launcher_view)
    public WearableRecyclerView mWearableRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer taskId = null;

        if (getIntent().getExtras() != null &&
                getIntent().getExtras().getString(Constants.KEY_TASK_ID) != null ){
            taskId = Integer.parseInt(getIntent().getExtras().getString(Constants.KEY_TASK_ID));
        }

        Log.w(TAG, "onCreate: TaskId = " + taskId);

        setContentView(R.layout.activity_choose_sport);
        ButterKnife.bind(this);

        // To align the edge children (first and last) with the center of the screen
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);


        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));

        sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));

        boolean hasGPS = SensorUtils.isGPSAvailable(this);
        boolean hasPodo = SensorUtils.isPodometerAvailable(sensorManager);
        boolean hasCardio = SensorUtils.isHeartRateAvailable(this);

        myDataSet.addAll(SportTypeItem.getAvailableSportTypes(this, hasGPS, hasPodo, hasCardio));

        mAdapter = new RecyclerViewAdapter(myDataSet, this, taskId);
        mWearableRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
