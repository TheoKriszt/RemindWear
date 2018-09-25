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
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.remindwear.jobs.SetReminderJob;
import fr.kriszt.theo.remindwear.utils.SchedulerJobService;
import fr.kriszt.theo.remindwear.workers.ReminderWorker;

;

public class NotificationTestingActivity extends AppCompatActivity {

    @BindView(R.id.genNotifButton) Button button;

    private static final String TAG = "NotifActivity";
    public static final String CHANNEL_ID = "remindwear_notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_testing);
        ButterKnife.bind(this);
        createNotificationChannel();
        // créer le job SetReminderJob

    }



    @OnClick(R.id.genNotifButton)
    void onButtonClicked(){

        // Create an explicit intent for the notification tap action
        Intent intent = new Intent(this, TasksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Notification de l'appli")
                .setContentText("Lorem Ipsum blablabla")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // automatically closes the notif. when tapped
                ;

        // TODO : wearable notif extension
        //Wear OS requires a hint to display the reply action inline.
        NotificationCompat.Action.WearableExtender actionExtender =
                new NotificationCompat.Action.WearableExtender()
                        .setHintLaunchesActivity(true)
                        .setHintDisplayActionInline(true);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(12345, nBuilder.build());


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
        Context context = getApplicationContext();


//        SetReminderJob.scheduleJob(context);

        Toast.makeText(context, "Starting worker", Toast.LENGTH_SHORT).show();
        ReminderWorker.scheduleWorker(3 * 60 * 1000);

    }

    @OnClick(R.id.startServiceButton)
    public void startService(){
//        Intent myIntent = new Intent(this, SchedulerJobService.class);
//        this.startService(myIntent);
//        Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
//        SetReminderJob.scheduleJob();
    }



}
