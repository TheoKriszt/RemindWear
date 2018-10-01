package fr.kriszt.theo.remindwear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class WearActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
    }
}
