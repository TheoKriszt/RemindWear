package fr.kriszt.theo.remindwear;

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
import fr.kriszt.theo.remindwear.sensingStrategies.SensorUtils;

public class ChooseSportActivity extends WearableActivity  {



    private static final String TAG = "sportChoose_activity";

    private RecyclerViewAdapter mAdapter;
    private List<SportTypeItem> myDataSet = new ArrayList<>();
    private SensorManager sensorManager;

    @BindView(R.id.recycler_launcher_view)
    WearableRecyclerView mWearableRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mAdapter = new RecyclerViewAdapter(myDataSet, this);
        mWearableRecyclerView.setAdapter(mAdapter);



    }

//    @Override
//    public void onLayoutFinished(View child, RecyclerView parent) {
//
//        // Figure out % progress from top to bottom
//        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
//        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;
//
//        // Normalize for center
//        mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
//        // Adjust to the maximum scale
//        mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS);
//
//        child.setScaleX(1 - mProgressToCenter);
//        child.setScaleY(1 - mProgressToCenter);
//    }




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
