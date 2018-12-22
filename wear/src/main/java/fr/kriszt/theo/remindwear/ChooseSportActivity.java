package fr.kriszt.theo.remindwear;

import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import butterknife.ButterKnife;

public class ChooseSportActivity extends WearableActivity  {



    private static final String TAG = "sportChoose_activity";
    private WearableRecyclerView mWearableRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sport);
        ButterKnife.bind(this);

        // To align the edge children (first and last) with the center of the screen
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);


        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));


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
