package fr.kriszt.theo.remindwear.data;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.WorkerThread;
import android.util.Log;

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

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataPoint;
import fr.kriszt.theo.shared.data.SportDataSet;

/**
 * Created by T.Kriszt on 08/10/2018.
 * Service Interface de la Data Layer côté téléphone mobile
 * Gère les échanges de messages avec la montre connectée
 */
public class PhoneDataService extends Service implements DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    private static final String TAG = "PhoneDataService";

//    private static final String START_ACTIVITY_PATH = "/start-activity";

    private int taskId;
    private SportType sportType;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null){
            Log.e(TAG, "onStartCommand: intent is null, WTF ??");
            return super.onStartCommand(null, flags, startId);
        }
//        Log.w(TAG, "PhoneDataService :: onStartCommand: " + intent.getAction());

        if (intent.getAction().equals(Constants.ACTION_LAUNCH_WEAR_APP)){

            if (intent.getExtras().get(Constants.KEY_SPORT_TYPE) != null) {
                sportType = (SportType) intent.getExtras().get(Constants.KEY_SPORT_TYPE);
            }

            int refererId = intent.getExtras().getInt(Constants.KEY_TASK_ID); // ID de la tâche d'origine
            Tasker tasker = Tasker.getInstance(getApplicationContext());



            tasker.unserializeLists();
            fr.kriszt.theo.remindwear.tasker.Task referer = tasker.getTaskByID(refererId);

            tasker.addSportTask(new SportTask(referer));

            tasker.serializeLists();

            this.taskId = refererId;

            new StartWearableActivityTask().execute();

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancel(refererId);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind: ");

        if (intent.getExtras() != null){
            for (String k : intent.getExtras().keySet()){
                Log.w(TAG, "onBind: intent key : " + k + " ==> " + intent.getExtras().get(k));
            }
        }

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
    }

    /**
     * Appelé quand la montre renvoie des données après un tracking sportif
     * @param messageEvent, dont la payload contient le JSON qui decrit le dataSet du tracking sportif
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        byte[] payload = messageEvent.getData();

        String message = new String(payload);
        SportDataSet sportDataSet = SportDataSet.fromJson(message);

        Log.w(TAG, "onMessageReceived: Recu " + sportDataSet);
        Log.w(TAG, "onMessageReceived: DataPoints : ");
//        for (SportDataPoint dp : sportDataSet.getPoints().values()){
////            Log.w(TAG, "onMessageReceived: DataPoint " + dp);
////            if (dp.coords == null){
////                Log.w(TAG, "onMessageReceived: Coords are null, WTF ??");
////
////            }else {
////                Log.w(TAG, dp.coords.toString());
////            }
//        }

        Tasker tasker = Tasker.getInstance(getApplicationContext());


        SportTask sportTask  = null;
        if (sportDataSet.getTaskId() != null) {
            sportTask = tasker.getSportTaskByID(sportDataSet.getTaskId());
        }

        if (sportTask == null){
            // TODO
            Log.w(TAG, "onMessageReceived: TODO : créer une sportTask a la volee");
            Category sport = tasker.getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
            Calendar cal = new GregorianCalendar();
            fr.kriszt.theo.remindwear.tasker.Task nullReferer = new fr.kriszt.theo.remindwear.tasker.Task("Sport libre : " + sportDataSet.getSportType().getName(), "", sport, cal, 0, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            sportTask = new SportTask(nullReferer);
            tasker.addSportTask(sportTask);
            sportTask.setDataset(sportDataSet);
            tasker.serializeLists();
        }else {
            sportTask.setDataset(sportDataSet);
        }
        tasker.serializeLists();

    }

    @Override
    public void onCapabilityChanged(final CapabilityInfo capabilityInfo) {
//        LOGD("onCapabilityChanged: " + capabilityInfo);

    }


    @WorkerThread
    private void sendStartActivityMessage(String node) {

        Map<String, String> params = new HashMap<>();
        params.put(Constants.KEY_TASK_ID, String.valueOf(taskId));
        if (sportType != null) {
            params.put(Constants.KEY_SPORT_TYPE, sportType.getName());
        }

        String payload = new Gson().toJson(params);


        Log.w(TAG, "sendStartActivityMessage: PAYLOAD : " + payload);
//        Log.w(TAG, "sendStartActivityMessage: Task Id is " + taskId);

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this)
                        .sendMessage(node, Constants.START_ACTIVITY_PATH, payload.getBytes());

        try {
            Integer result = Tasks.await(sendMessageTask);
            Log.d(TAG, "sendStartActivityMessage: Message sent : " + result);

        } catch (ExecutionException | InterruptedException exception) {
            Log.e(TAG, "Task failed: " + exception);
        }
    }

    @WorkerThread
    private Collection<String> getNodes(){
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

//    public void sendStopTrackMessage(String node){
//
//        Wearable.getMessageClient(this).sendMessage(node, Constants.START_ACTIVITY_PATH, new byte[0]);
//    }


    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();

            if (nodes.size() > 0) {
                for (String node : nodes) {
                    sendStartActivityMessage(node);
                }
            }
            return null;
        }
    }
}
