package fr.kriszt.theo.remindwear.data;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataSet;

/**
 * Service Interface de la Data Layer côté téléphone mobile
 * Gère les échanges de messages avec la montre connectée
 *
 * Deux messages à gérer
 * Un premier message peut demander à lancer l'appli Wear à distance
 *
 * Un second message venant de la Wear peut revenir avec sa payload pour rendre les données d'un tracking sportif
 */
public class PhoneDataService extends Service implements
//        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private static final String TAG = "PhoneDataService";
    private int taskId;
    private SportType sportType = null;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }

        if (Objects.requireNonNull(intent.getAction()).equals(Constants.ACTION_LAUNCH_WEAR_APP)) {
            if (intent.getExtras() != null){
                launchWearApp(intent);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void launchWearApp(Intent intent) {

        // Si un type de sport a été spécifié
        if (Objects.requireNonNull(intent.getExtras()).get(Constants.KEY_SPORT_TYPE) != null) {
            sportType = (SportType) intent.getExtras().get(Constants.KEY_SPORT_TYPE);
        }

        // Sinon, il sera demandé à l'utilisateur quel type de sport lancer
        int refererId = intent.getExtras().getInt(Constants.KEY_TASK_ID); // ID de la tâche d'origine
        Tasker tasker = Tasker.getInstance(getApplicationContext());

        tasker.unserializeLists();
        fr.kriszt.theo.remindwear.tasker.Task referer = tasker.getTaskByID(refererId);

        tasker.addSportTask(new SportTask(referer));

        tasker.serializeLists();

        this.taskId = refererId;

        new StartWearableActivityTask().execute();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(refererId);
    }


    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);

        return super.onUnbind(intent);

    }

    /**
     * Appelé quand la montre renvoie des données après un tracking sportif
     *
     * @param messageEvent, dont la payload contient le JSON qui decrit le dataSet du tracking sportif
     */
    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        byte[] payload = messageEvent.getData();

        String message = new String(payload);
        SportDataSet sportDataSet = SportDataSet.fromJson(message);

        Tasker tasker = Tasker.getInstance(getApplicationContext());

        SportTask sportTask = null;
        if (sportDataSet.getTaskId() != null) {
            sportTask = tasker.getSportTaskByID(sportDataSet.getTaskId());
        }

        // Créer une tâche à la volée si pas de tâche correspondate
        // Signifie que le tracking a été initié depuis la montre
        if (sportTask == null) {
            Category sport = tasker.getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
            Calendar cal = new GregorianCalendar();
            fr.kriszt.theo.remindwear.tasker.Task nullReferer = new fr.kriszt.theo.remindwear.tasker.Task("Sport libre : " + sportDataSet.getSportType().getName(), "", sport, cal, 0, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            sportTask = new SportTask(nullReferer);
            sportTask.setDataset(sportDataSet);
            tasker.addSportTask(sportTask);
        }
        sportTask.setDataset(sportDataSet);
        tasker.serializeLists();

    }

    @Override
    public void onCapabilityChanged(@NonNull final CapabilityInfo capabilityInfo) {
    }


    @WorkerThread
    private void sendStartActivityMessage(String node) {

        Map<String, String> params = new HashMap<>();
        params.put(Constants.KEY_TASK_ID, String.valueOf(taskId));
        if (sportType != null) {
            params.put(Constants.KEY_SPORT_TYPE, sportType.getName());
        }

        String payload = new Gson().toJson(params);

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this)
                        .sendMessage(node, Constants.START_ACTIVITY_PATH, payload.getBytes());

        try {
            Tasks.await(sendMessageTask);
        } catch (ExecutionException | InterruptedException exception) {
            Log.e(TAG, "Task failed: " + exception);
        }
    }

    /**
     *
     * @return Les noeuds connectés au téléphone
     */
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


    /**
     * Tâche async qui demande à un noeud de lancer à distance l'activité principale sur la montre
     */
    @SuppressLint("StaticFieldLeak")
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
