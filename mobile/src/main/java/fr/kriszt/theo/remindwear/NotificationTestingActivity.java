package fr.kriszt.theo.remindwear;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.remindwear.utils.SchedulerJobService;

public class NotificationTestingActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "REMINDWEAR_NOTIFICATION_TESTING";
    private static int notificationId = 0;

    @BindView(R.id.genNotifButton) Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_testing);
        ButterKnife.bind(this);
        createNotificationChannel();
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
        notificationManager.notify(notificationId, nBuilder.build());
        notificationId++;


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
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
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
        scheduleTask(getApplicationContext());
    }

    public static void scheduleTask(Context context){

        ComponentName serviceComponent = new ComponentName(context, SchedulerJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 5000); // wait at least
        builder.setOverrideDeadline(3 * 5000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());

        Toast.makeText(context, "Scheduling new task...", Toast.LENGTH_SHORT).show();

    }

}
