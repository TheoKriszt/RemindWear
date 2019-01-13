/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.kriszt.theo.remindwear;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import fr.kriszt.theo.remindwear.ui.activity.ChooseSportActivity;
import fr.kriszt.theo.remindwear.ui.activity.WearActivity;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.data.SportDataSet;

/** Listens to DataItems and Messages from the local node. */
public class   WearDataService extends Service implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private static final String TAG = "WearDataService";

////    private static final String START_ACTIVITY_PATH = "/start-activity";
//    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
//    public static final String COUNT_PATH = "/count";
//    private static WearActivity observer;
    private SportDataSet dataset;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind: ");


        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        return null;
    }

//    public static void setObserver(WearActivity observer) {
////        WearDataService.observer = observer;
//    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG, "onUnbind: ");

        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);

        return super.onUnbind(intent);

    }



    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.w(TAG, "onDataChanged: " + dataEvents);

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.w(TAG, "onMessageReceived: " + messageEvent);

        String source = new String(messageEvent.getData());
        HashMap params = new Gson().fromJson( source, HashMap.class );
//        Log.w(TAG, "onMessageReceived: PARAMS" + params.keySet());
//        for (Object k : params.keySet()){
//            Log.w(TAG, "onMessageReceived: key " + k + " :-> " + params.get(k));
//        }

        String taskId = (String) params.get(Constants.KEY_TASK_ID);
//        Log.w(TAG, "onMessageReceived: TaskID : " + taskId);
//        byte[] taskIdPayload = BigInteger.valueOf(taskId).toByteArray();


        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(Constants.START_ACTIVITY_PATH)) {
            Log.w(TAG, "onMessageReceived: params : " + params);
            Log.w(TAG, "onMessageReceived: taskId : " + taskId);
            Intent startIntent = new Intent(this, ChooseSportActivity.class);
            startIntent.putExtra(Constants.KEY_TASK_ID, taskId);
            startIntent.putExtra(Constants.KEY_PARAMS, params);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(startIntent);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand: intent = " + intent);
//        Log.w(TAG, "onStartCommand: " + intent.getAction());

        if (intent != null && intent.getAction() != null && intent.getAction().equals(Constants.ACTION_END_TRACK)){
//            Log.w(TAG, "onStartCommand: Fin du tracking");

            if (intent.getExtras() != null){
                String payload =  intent.getExtras().get(Constants.KEY_DATASET).toString();
                dataset = SportDataSet.fromJson(payload);



//                if (dataset != null) {
//                    Log.w(TAG, "onStartCommand: Dataset de taille " + dataset.size());
//                }else {
//                    Log.w(TAG, "onStartCommand: SportDataSet is null");
//                }

            } else {
//                Log.w(TAG, "onStartCommand: Pas d'extras");
            }


//            Log.w(TAG, "onStartCommand: Fin du tracking, " + DataLayerUtils.getNodes(getApplicationContext()).size() + " noeuds trouves");

            new StopDispatcher().execute();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void sendStopMessage(String nodeId){
        byte[] payload = dataset.toJson().getBytes();
        Log.w(TAG, "sendStopMessage: Sending payload : " + dataset);
        final String buttonText = WearActivity.lastInstance.getButtonText();
        final boolean firstTry = buttonText.startsWith("Stop");

        if (firstTry){
            WearActivity.lastInstance.setButton("Send", R.color.blue);
        }else {
            WearActivity.lastInstance.setButton("Sending ...", R.color.grey);
        }

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this)
                        .sendMessage(nodeId, Constants.PHONE_PATH, payload);

        sendMessageTask.addOnCompleteListener(
                new OnCompleteListener<Integer>() {
                    @Override
                    public void onComplete(Task<Integer> task) {
                        if (task.isSuccessful()) {

                            if (!firstTry){
                                WearActivity.lastInstance.setButton("Sent !", R.color.green);
                            }
                        } else {
                            WearActivity.lastInstance.setButton("Failed !", R.color.dark_red);
                        }
                    }
                });


    }


    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        Log.w(TAG, "onCapabilityChanged: " + capabilityInfo);
    }

    private class StopDispatcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStopMessage(node);
            }
            return null;
        }
    }

    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);
            Log.w(TAG, "getNodes: nodes size : " + nodes.size());

            for (Node node : nodes) {
                results.add(node.getId());
            }

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }
        return results;
    }

}
