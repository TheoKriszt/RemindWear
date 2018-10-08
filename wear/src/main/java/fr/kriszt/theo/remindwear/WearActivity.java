package fr.kriszt.theo.remindwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.kriszt.theo.shared.Constants;

/**
 * Simple activité wearable
 * L'intent peut diriger vers certaines vues ou fragemnts
 *
 * Si ouvert depuis les applis mobiles :
 *     propose de commencer un exercice
 */
public class WearActivity extends WearableActivity {

    private static final String TAG = "wear_activity";
//    private SwipeDismissFrameLayout.Callback dismissCallback = new SwipeDismissFrameLayout.Callback() {
//        @Override
//        public void onDismissed(SwipeDismissFrameLayout layout) {
//           super.onDismissed(layout);
//           closeActivity();
//
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

//        SwipeDismissFrameLayout swipeLayout = new SwipeDismissFrameLayout(this);
//        SwipeDismissFrameLayout swipeLayout = findViewById(R.id.swipe_dismiss_root);
//        if (swipeLayout != null) {
//            swipeLayout.addCallback(dismissCallback);
//        }else {
//            Log.w(TAG, "onCreate: ERREUR : Missing layout");
//        }
    }


    public void stopTracking(View view){
        Log.w(TAG, "stopTracking: ");
        Intent stopIntent = new Intent(this, WearDataService.class);
        stopIntent.setAction(Constants.ACTION_END_TRACK);
        this.startService(stopIntent);


    }

    /** Find the connected nodes that provide at least one of the given capabilities. */
//    private void showNodes(final String... capabilityNames) {
//
//        Task<Map<String, CapabilityInfo>> capabilitiesTask =
//                Wearable.getCapabilityClient(this)
//                        .getAllCapabilities(CapabilityClient.FILTER_REACHABLE);
//
//        capabilitiesTask.addOnSuccessListener(
//                new OnSuccessListener<Map<String, CapabilityInfo>>() {
//                    @Override
//                    public void onSuccess(Map<String, CapabilityInfo> capabilityInfoMap) {
//                        Set<Node> nodes = new HashSet<>();
//
//                        if (capabilityInfoMap.isEmpty()) {
//                            Log.w(TAG, "onSuccess: " + nodes);
//                            return;
//                        }
//                        for (String capabilityName : capabilityNames) {
//                            CapabilityInfo capabilityInfo = capabilityInfoMap.get(capabilityName);
//                            if (capabilityInfo != null) {
//                                nodes.addAll(capabilityInfo.getNodes());
//                            }
//                        }
//                        Log.w(TAG, "onSuccess: " + nodes);
//                    }
//                });
//    }
}
