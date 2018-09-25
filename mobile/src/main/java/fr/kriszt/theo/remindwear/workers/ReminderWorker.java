package fr.kriszt.theo.remindwear.workers;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.RemindNotification;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;

public class ReminderWorker extends Worker {
    public static final String TAG = "REMINDER_WORKER";
    private static String workTag = "REMINDER_WORK";

    @NonNull
    @Override
    public Result doWork() {
        Log.w(TAG, "doWork: ");

        //Toast.makeText(getApplicationContext(), "Worker is running", Toast.LENGTH_SHORT).show();
        Calendar calendar = new GregorianCalendar(2018, 8, 26, 13, 30);
//        Task test = new Task("Tâche apres redemarrage", "pliz work fine", new Category("Ma categorie"), calendar, 15);
        Task test = new Task("Tache de test", "description", new Category("categ", R.drawable.ic_notifications_black_24dp), new GregorianCalendar(2018, 10, 28, 12, 30), 15, 12, 30);
        new RemindNotification(test, getApplicationContext()).show(null);

        return Result.SUCCESS;
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    public static void scheduleWorker(long millis){
        Log.w(TAG, "scheduleWorker: started with " + millis + " millis");
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(millis, TimeUnit.MILLISECONDS)
                //                .setInputData(inputData)
                .addTag(workTag)
                .build();

        WorkManager.getInstance().enqueue(work);

//alternatively, we can use this form to determine what happens to the existing stack
//WorkManager.getInstance().beginUniqueWork(workTag, ExistingWorkPolicy.REPLACE, notificationWork);

//ExistingWorkPolicy.REPLACE - Cancel the existing sequence and replace it with the new one
//ExistingWorkPolicy.KEEP - Keep the existing sequence and ignore your new request
//ExistingWorkPolicy.APPEND - Append your new sequence to the existing one,
//running the new sequence's first task after the existing sequence's last task finishes

    }
}
