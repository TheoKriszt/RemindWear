package fr.kriszt.theo.remindwear.ui.activity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.sensors.SensorUtils;
import fr.kriszt.theo.remindwear.ui.RecyclerViewAdapter;
import fr.kriszt.theo.remindwear.ui.SportTypeItem;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;

public class ChooseSportActivity extends WearableActivity  {



    private static final String TAG = "sportChoose_activity";

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
            if (getIntent().getExtras().get(Constants.KEY_PARAMS) != null){
                HashMap<String, String> params = (HashMap<String, String>) getIntent().getExtras().get(Constants.KEY_PARAMS);
                Log.w(TAG, "onCreate: params : " + params);
//                HashMap<String, String> map = new Gson().fromJson(params, HashMap.class);
//                Log.w(TAG, "onCreate: PARAMS " + map.keySet());
                if (params.containsKey(Constants.KEY_TASK_ID)) {
                    taskId = Integer.parseInt(params.get(Constants.KEY_TASK_ID));
                }

                if (params.containsKey(Constants.KEY_SPORT_TYPE)) {
//                    Log.w(TAG, "onCreate: Sport Type :: " + map.get(Constants.KEY_SPORT_TYPE));
                    sportType = params.get(Constants.KEY_SPORT_TYPE);
                    Log.w(TAG, "onCreate: Sport Type :: " + sportType);
                }else {
                    Log.w(TAG, "onCreate: No SportType provided");
                }
            }

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

        if (sportType != null){

            boolean canStart = false;

            for (SportTypeItem sti : myDataSet){
                if (sti.name.equals(sportType)){
                    canStart = true;
                    break;
                }
            }

            if ( canStart ) {
                Intent startIntent = new Intent(this, WearActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startIntent.putExtra(Constants.KEY_SPORT_TYPE, sportType);
                startIntent.putExtra(Constants.KEY_TASK_ID, taskId);

                startActivity(startIntent);
            }

        }



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
