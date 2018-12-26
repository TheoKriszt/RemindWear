package fr.kriszt.theo.remindwear.wear;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.data.DataSet;

/**
 * Created by T.Kriszt on 08/10/2018.
 * Service Interface de la Data Layer côté téléphone mobile
 * Gère les échanges de messages avec la montre connectée
 */
public class PhoneDataService extends Service implements DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    private static final String TAG = "PhoneDataService";

    private static final String START_ACTIVITY_PATH = "/start-activity";

    private int taskId;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null){
            Log.e(TAG, "onStartCommand: intent is null, WTF ??");
            return super.onStartCommand(intent, flags, startId);
        }
        Log.w(TAG, "PhoneDataService :: onStartCommand: " + intent.getAction());

        if (intent.getAction().equals(Constants.ACTION_LAUNCH_WEAR_APP)){
            int taskId = intent.getExtras().getInt(Constants.KEY_TASK_ID);
            this.taskId = taskId;

            Log.w(TAG, "onStartCommand: action " + Constants.ACTION_LAUNCH_WEAR_APP);
//            onStartWearableActivityClick(null);
            new StartWearableActivityTask().execute();
//
//            Set<String> extraKeys = intent.getExtras().keySet();
//
//            Log.w(TAG, "intent extras : ");
//
//            for (String k : extraKeys){
//                Log.w(TAG, "key : " + k + ", value : " + intent.getExtras().get(k));
//            }



            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancel(taskId);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind: ");

//        if (intent.getExtras() != null){
//            for (String k : intent.getExtras().keySet()){
//                Log.w(TAG, "Extra: " + k +  " --> " + intent.getExtras().get(k));
//            }
//        }else Log.w(TAG, "onBind: intent extra are null");


        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);

        return super.onUnbind(intent);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD("onDataChanged: " + dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                mDataItemListAdapter.add(
//                        new Event("DataItem Changed", event.getDataItem().toString()));
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
//                mDataItemListAdapter.add(
//                        new Event("DataItem Deleted", event.getDataItem().toString()));
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.w(TAG, "onMessageReceived: " + messageEvent);
        Log.w(TAG, "onMessageReceived: " + new String(messageEvent.getData()) );

        byte[] payload = messageEvent.getData();

        String message = new String(payload);
        DataSet dataSet = DataSet.fromJson(message);

        Log.w(TAG, "onMessageReceived: Recu" + dataSet);

        LOGD(
                "onMessageReceived() A message from watch was received:"
                        + messageEvent.getRequestId()
                        + " "
                        + messageEvent.getPath());

//        mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));

    }

    @Override
    public void onCapabilityChanged(final CapabilityInfo capabilityInfo) {
        LOGD("onCapabilityChanged: " + capabilityInfo);

//        mDataItemListAdapter.add(new Event("onCapabilityChanged", capabilityInfo.toString()));
    }

//    /** Sends an RPC to start a fullscreen Activity on the wearable. */
//    public void onStartWearableActivityClick(View view) {
//        LOGD(TAG, "Generating RPC");
//        Log.w(TAG, "onStartWearableActivityClick: ");
//
//        // Trigger an AsyncTask that will query for a list of connected nodes and send a
//        // "start-activity" message to each connected node.
//        new StartWearableActivityTask().execute();
//    }

    @WorkerThread
    private void sendStartActivityMessage(String node) {


//        byte[] taskIdPayload = ByteBuffer.allocate(4).putInt(taskId).array(); // taskId, en byte[]
        byte[] taskIdPayload = BigInteger.valueOf(taskId).toByteArray();

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this)
                        .sendMessage(node, START_ACTIVITY_PATH, taskIdPayload);

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            Integer result = Tasks.await(sendMessageTask);
            LOGD("Message sent: " + result);

        } catch (ExecutionException | InterruptedException exception) {
            Log.e(TAG, "Task failed: " + exception);
        }
    }

    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());
            }

        } catch (ExecutionException | InterruptedException exception) {
            Log.e(TAG, "Task failed: " + exception);

        }

        Log.w(TAG, "getNodes: " + results.size() + " nodes found");

        return results;
    }

    public void sendStopTrackMessage(String node){

        Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, new byte[0]);
    }

    /** As simple wrapper around Log.d */
    private static void LOGD(String message) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, message);
        }
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();

            if (nodes.size() > 0) {
                for (String node : nodes) {
                    sendStartActivityMessage(node);
                }
            } else {
            }
            Toast.makeText(PhoneDataService.this, nodes.size() + " nodes found", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private class StopTrackingDispatcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            for (String node : getNodes()) {
                sendStopTrackMessage(node);
            }
            return null;
        }
    }





}
