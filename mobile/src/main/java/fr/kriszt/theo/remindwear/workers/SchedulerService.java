package fr.kriszt.theo.remindwear.workers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;
import androidx.work.WorkManager;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

/**
 * Created by T.Kriszt on 04/10/2018.
 */
public class SchedulerService extends Service {

    public static final String TAG = "SHEDULER_SERVICE";

    public static final int POSTPONE_TIME_MINUTES = 10;

    public static final String TASK_ID_TAG = "TASK_ID";

    @Override
    public void onCreate() {
//        Log.w(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand: ");

        int taskId = (int) intent.getExtras().get(TASK_ID_TAG);

        Task task = Tasker.getTaskByID(taskId);



        if (task != null){
            postponeTask(task);

        }else {
            Log.w(TAG, "Error : cannot find Task with ID " + taskId);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void postponeTask(Task task) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MINUTE, 10);
        task.setTimeHour(calendar.get(Calendar.HOUR_OF_DAY));
        task.setTimeMinutes(calendar.get(Calendar.MINUTE));
        ReminderWorker.scheduleWorker(task);

        Log.w(TAG, "postponeTask: Task " + task.getName() + " rescheduled in " + POSTPONE_TIME_MINUTES + " minutes");
    }
}
