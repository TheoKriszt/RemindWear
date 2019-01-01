package fr.kriszt.theo.remindwear.workers;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

/**
 * Created by T.Kriszt on 04/10/2018.
 * Service de planification : peut être appelé via un Intent
 * Reporte une tâche à plus tard (10 minutes)
 * S'utilise avec l'action "Plus Tard" de la notification (équivalent du 'Snooze' pour un réveil)
 */
public class SchedulerService extends Service {

    public static final String TAG = "SHEDULER_SERVICE";

    public static final int POSTPONE_TIME_MINUTES = 10;

    public static final String TASK_TAG = "TASK_ID";
    private Tasker tasker;

    @Override
    public void onCreate() {
//        Log.w(TAG, "onCreate: ");
        super.onCreate();
        tasker = Tasker.getInstance(getApplicationContext());
    }

    @Override
    /**
     * @param intent dont l'extra contient la tâche sérialisée à planifier
     */
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null){

            Bundle extras = intent.getExtras();

            if (extras != null){
                for (String k : extras.keySet()){
                    Log.w(TAG, "found key " + k + " = " + extras.get(k)); // TODO : pareil pour Later ou Track
                }

                Task task = (Task) extras.getSerializable(TASK_TAG);
                postponeTask(task);




            }else {
                Log.w(TAG, "onStartCommand: Les extras sont NULL");
            }




//            Log.w(TAG, "onStartCommand: SER : "  + task);
//            String test = intent.getStringExtra("TEST");
//            String taskId = extras.get(TASK_TAG).toString();
//
//            Log.w(TAG, "onStartCommand: WTF");
//            Log.w(TAG, "onStartCommand: test : " + test);
//            Log.w(TAG, "onStartCommand: taskId: " + taskId);
//




//            Task task = (Task) intent.getExtras().get("TASK");
//            Log.w(TAG, "onStartCommand: Message recu : " + intent.getExtras().get("TEST"));
//
////            Object value = intent.getExtras().get(TASK_TAG);
//
////            Log.w(TAG, "onStartCommand: VALUE :: " + value.toString());
//            int taskId = intent.getIntExtra(TASK_TAG, -1);
//
//            Log.w(TAG, "onStartCommand: TaskId : " + taskId);
//
//            if (task != null){
//                Log.w(TAG, "onStartCommand: Tâche récupéree : " + task.toString());
//                postponeTask(task);
//
//            }else {
//                Log.w(TAG, "Error : cannot find Task with ID " + taskId);
//            }
        }



        return super.onStartCommand(intent, flags, startId);
    }

    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Reporte une tâche de POSTPONE_TIME_MINUTES minutes
     * @param task une copie (potentiellement par serialisation) de la tâche a reporter
     * Gère la persistance côté Tasker
     */
    private void postponeTask(Task task) {

        Log.i(TAG, "postponeTask: La tâche " + task.getName() + " est reportée à dans "+ POSTPONE_TIME_MINUTES +" minutes");

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(task.getID());

        Task alteredTask = tasker.getTaskByID(task.getID()); // get actual tasker's Task, not just a copy

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MINUTE, POSTPONE_TIME_MINUTES);
        alteredTask.setTimeHour(calendar.get(Calendar.HOUR_OF_DAY));
        alteredTask.setTimeMinutes(calendar.get(Calendar.MINUTE));
        ReminderWorker.scheduleWorker(alteredTask);
        tasker.serializeLists(); // save the changes

    }
}
