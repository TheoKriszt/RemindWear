package fr.kriszt.theo.remindwear;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.remindwear.jobs.SetReminderJob;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.utils.SchedulerJobService;
import fr.kriszt.theo.remindwear.workers.ReminderWorker;

;

public class NotificationTestingActivity extends AppCompatActivity {

    @BindView(R.id.genNotifButton) Button button;

    private static final String TAG = "NotifActivity";
    public static final String CHANNEL_ID = "remindwear_notification";

    private Tasker tasker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_testing);
        ButterKnife.bind(this);
        createNotificationChannel();
        // créer le job SetReminderJob
        tasker = Tasker.getInstance(this);
    }





    @OnClick(R.id.genNotifButton)
    void onButtonClicked(){

        Category none = tasker.getCategoryByName(Tasker.CATEGORY_NONE_TAG);
        Category sport = tasker.getCategoryByName(Tasker.CATEGORY_SPORT_TAG);
        Task task = new Task("Titre de ma tâche", "Description de ma tâche", sport, new GregorianCalendar(2018, 30, 9), 0, 23, 15);
        new RemindNotification(task, this).show(null);


    }

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library
     */
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RemindWear notification channel";
//            String description = getString(R.string."");
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("set_reminder_job", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // TODO :
    // Reschedule everything when device reboots
//    <receiver android:name=".AutoStartUp" android:enabled="true" android:exported="false" android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
//     <intent-filter>
//        <action android:name="android.intent.action.BOOT_COMPLETED" />
//    </intent-filter>
//    </receiver>

    @OnClick(R.id.scheduleButton)
    public void onScheduleButtonClicked(){
//        SchedulerJobService.scheduleJob(getApplicationContext());
//        SetReminderJob.scheduleJob(this);

        ArrayList<Task> tasks = tasker.getListTasks();
        ArrayList<Task> scheduledTasks = new ArrayList<>();
        for (Task t : tasks){
            if (t.getIsActivatedNotification()) { // Todo : check le timeBefore etc.
                scheduledTasks.add(t);
            }
        }

//        scheduledTasks.add(new Task("Tâche 1", "Description 1", null, new GregorianCalendar(2018, 9, 29), 0, 0, 1));
//        scheduledTasks.add(new Task("Tâche 2", "Description 2", null, new GregorianCalendar(2018, 9, 29), 0, 0, 2));
//        scheduledTasks.add(new Task("Tâche 3", "Description 3", null, new GregorianCalendar(2018, 9, 29), 0, 0, 3));
//        scheduledTasks.add(new Task("Tâche 4", "Description 4", null, new GregorianCalendar(2018, 9, 29), 0, 0, 4));

        Log.w(TAG, "onScheduleButtonClicked: " + scheduledTasks.size() + " tâches a planifier");
        Toast.makeText(this, scheduledTasks.size() + " tâches a planifier", Toast.LENGTH_SHORT).show();

        for (Task st : scheduledTasks){
            ReminderWorker.scheduleWorker(st);
//            Tasker.removeTask(st);
//            Tasker.serializeLists();
        }


//        Toast.makeText(context, "Starting worker", Toast.LENGTH_SHORT).show();
//        ReminderWorker.scheduleWorker(3 * 60 * 1000);

    }

    @OnClick(R.id.startServiceButton)
    public void startService(){
//        Intent myIntent = new Intent(this, SchedulerJobService.class);
//        this.startService(myIntent);
//        Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
//        SetReminderJob.scheduleJob();
    }



}
