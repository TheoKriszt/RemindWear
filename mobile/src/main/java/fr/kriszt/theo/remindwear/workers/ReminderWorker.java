package fr.kriszt.theo.remindwear.workers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import fr.kriszt.theo.remindwear.RemindNotification;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

/**
 * Planificateur de tâches (comme CRON)
 * Gère la persistance après la fermeture de l'appli voire le redémarrage du tél.
 * Appeler scheduleWorker(Task) pour chaque nouvelle tâche créée ou reprogrammée
 * La tâche interne planifiée fera apparaitre une notification de RemindWear à l'heure de rappel de la tâche entrée par l'utilisateur depuis l'UI
 */
public class ReminderWorker extends Worker {
    public static final String TAG = "REMINDER_WORKER";
    private static final String CATEGORY_NONE_TAG = "AUCUNE";
    private static final String TASK_ID_KEY = "UUID";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int taskID = this.getInputData().getInt(TASK_ID_KEY, -1);

        Task task = Tasker.getInstance(getApplicationContext()).getTaskByID(taskID);

        if (task != null) {
            new RemindNotification(task, getApplicationContext()).show();

            if (task.getDateDeb() == null) { // tâche récurrente
                scheduleWorker(task); // replanifier pour la prochaine occurence
            }
            return Result.success();

        } else {
            Log.w(TAG, "doWork: identifiant de tâche inconnu (sans doute supprimé entre temps) : " + taskID);
        }

        return Result.failure();
    }

    /**
     * (Re)Planifie la notification d'une tâche
     *
     * @param task La tâche à (re)plannifier
     */
    public static void scheduleWorker(Task task) {

        if (task.getWorkID() != null) { // La tâche est déjà planifiée, annuler le job pour le reprogrammer derrière
            Log.w(TAG, "Task is already scheduled :  rescheduling");
            WorkManager.getInstance().cancelWorkById(task.getWorkID());
        }

        if (!task.getIsActivatedNotification()) return; // ne pas dérange

        long remainingSeconds = task.getRemainingTime(TimeUnit.SECONDS) * -1; // dateDiff donne une valeur négative si dans le futur
        long remainingWithWarningBefore = remainingSeconds - task.getWarningBeforeSeconds();
        Log.w(TAG, "scheduleWorker: la tâche " + task.getName() + " commencera dans " + remainingSeconds + " secondes");

        if (remainingWithWarningBefore > 0) {
            remainingSeconds = remainingWithWarningBefore;
        }

        Data inputData = new Data.Builder().putInt(TASK_ID_KEY, task.getID()).build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(remainingSeconds, TimeUnit.SECONDS)
                .addTag(getCategoryTag(task.getCategory())) // catégoriser le work par Category (de la tâche)
                .setInputData(inputData)
                .build();


        WorkManager.getInstance().enqueue(work);
        task.setWorkID(work.getId());
    }

    private static String getCategoryTag(Category category) {

        String categoryTag = category == null ? CATEGORY_NONE_TAG : category.getName();
        String workTag = "REMINDER_WORK";
        return workTag + "_" + categoryTag;
    }

}
