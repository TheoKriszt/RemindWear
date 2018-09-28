package fr.kriszt.theo.remindwear.workers;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import androidx.work.Worker;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.RemindNotification;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class ReminderWorker extends Worker {
    public static final String TAG = "REMINDER_WORKER";
    private static final String CATEGORY_NONE_TAG = "AUCUNE";
    private static String workTag = "REMINDER_WORK";
    private static final String TASK_ID_KEY = "UUID";

    @NonNull
    @Override
    public Result doWork() {
        Log.w(TAG, "doWork: ");


        int taskID = this.getInputData().getInt(TASK_ID_KEY, -1);

        Task task = Tasker.getInstance(getApplicationContext()).getTaskByID(taskID);

        if (task != null) {
            new RemindNotification(task, getApplicationContext()).show(null);

            if (false /* task.isRecurrent() */){
                // TODO : vérifier si replanification nécessaire (tâche récurrente)
            }

        }

        return Result.SUCCESS;
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    /**
     * @deprecated
     * @param millis
     * @param timeUnit
     */
    public static void scheduleWorker(long millis, @Nullable TimeUnit timeUnit){

        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS;
        }

        Log.w(TAG, "scheduleWorker: started with " + millis + " millis");
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(millis, timeUnit)
                //                .setInputData(inputData)
//                .addTag(workTag + "_" + )
                .build();

        WorkManager.getInstance().enqueue(work);

//alternatively, we can use this form to determine what happens to the existing stack
//WorkManager.getInstance().beginUniqueWork(workTag, ExistingWorkPolicy.REPLACE, notificationWork);

//ExistingWorkPolicy.REPLACE - Cancel the existing sequence and replace it with the new one
//ExistingWorkPolicy.KEEP - Keep the existing sequence and ignore your new request
//ExistingWorkPolicy.APPEND - Append your new sequence to the existing one,
//running the new sequence's first task after the existing sequence's last task finishes

    }

    /**
     * (Re)Planifie la notification d'une tâche
     * @param task
     */
    public static void scheduleWorker(Task task) {
        Log.w(TAG, "scheduleWorker: Scheduling task " + task.getName());

//        UUID taskId = task.getID();

        // TODO : check if needed to schedule


        if (task.getWorkID() != null){ // La tâche est déjà planifiée, annuler le job pour le reprogrammer derrière
            Log.w(TAG, "Task is already scheduled :  rescheduling");
            WorkManager.getInstance().cancelWorkById(task.getWorkID());
        }

        // TODO : a remplacer par genre task.getMinutesBeforeNextExecution()
        int before = task.getWarningBefore();
        int hour = task.getTimeHour();
        int minute = task.getTimeMinutes();

        String categoryTag = task.getCategory() == null ? CATEGORY_NONE_TAG : task.getCategory().getName();


        Data inputData = new Data.Builder().putInt(TASK_ID_KEY, task.getID()).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(minute, TimeUnit.MINUTES)  // TODO : en attendant de connaitre la prochaine execution, simuler le comportement avec les minutes
                                                            // TODO : ex : si la tâche commence à 13h03, planifier dans 3 minutes
                .addTag(workTag + "_" + categoryTag) // catégoriser le work par Category (de la tâche)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance().enqueue(work);
        task.setWorkID(work.getId());
    }

    public static LiveData<WorkStatus> getWorkStatus(Task task){
        UUID workId = task.getWorkID();
        LiveData<WorkStatus> workStatusLiveData = WorkManager.getInstance().getStatusById( workId );
        return workStatusLiveData;
    }


}
