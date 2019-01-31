package fr.kriszt.theo.remindwear;

import android.annotation.SuppressLint;
import android.app.Notification;
//import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.activities.TasksActivity;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;
import fr.kriszt.theo.remindwear.data.PhoneDataService;
import fr.kriszt.theo.remindwear.workers.SchedulerService;
import fr.kriszt.theo.shared.Constants;

public class RemindNotification {
    public static final String TAG = "REMINDER_NOTIFICATION";
    private final static String notification_channel = "Wear Event Reminder";

    private String title, content;
    private Context context;
    private NotificationCompat.BigTextStyle bigText;
    private int taskId;
    private Notification notification;

    @SuppressLint("DefaultLocale")
    public RemindNotification(Task task, Context c){
        title = task.getName();
        content = String.format("Commence à %02dh%02d", task.getTimeHour(), task.getTimeMinutes());
        taskId = task.getID();
        context = c;

        String longText = "Commence à " + String.format("%02d", task.getTimeHour()) + "h" + String.format("%02d", task.getTimeMinutes())
                + "<p>" + task.getDescription() + "</p>";
        bigText = new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(longText));

        NotificationCompat.Builder builder = build(task);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("REMINDWEAR_CHANNEL", "RemindWear", importance);
            channel.setDescription("RemindWear Global Notifications");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            builder.setChannelId(channel.getId());
        }

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();

        addActions(task, builder);

        builder.extend(extender);
        builder.setAutoCancel(true);

        notification = builder.build();



    }

    private void addActions(Task task, NotificationCompat.Builder builder) {

        Intent trackingIntent = new Intent(context, TasksActivity.class);
        trackingIntent.putExtra(TasksActivity.FRAGMENT_TO_LAUNCH, SportTaskListFragment.class.getName());
        trackingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent sendMessageIntent = new Intent(context, PhoneDataService.class);
        sendMessageIntent.setAction(Constants.ACTION_LAUNCH_WEAR_APP);
        sendMessageIntent.putExtra(Constants.KEY_TASK_ID, task.getID());
        PendingIntent sendMessagePendingIntent = PendingIntent.getService(context, taskId, sendMessageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent postponeIntent = new Intent(context, SchedulerService.class);
        Bundle postponeBundle = new Bundle();
        postponeBundle.putSerializable(SchedulerService.TASK_TAG, task);
        postponeIntent.putExtras(postponeBundle);

        PendingIntent pendingPostponeIntent = PendingIntent.getService(context, taskId, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (task.getCategory().getName().equals(Tasker.CATEGORY_SPORT_TAG)) {
            builder.addAction(new NotificationCompat.Action(
                    R.drawable.baseline_directions_run_24, "Track", sendMessagePendingIntent
            ));
        }

        builder.addAction(new NotificationCompat.Action(
                R.drawable.ic_snooze, "+"+ Constants.POSTPONE_DELAY + " min", pendingPostponeIntent
        ));

    }

    private NotificationCompat.Builder build(Task task){
        int categoryIcon = task.getCategory().getIcon();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        return new NotificationCompat.Builder(context, notification_channel)
                .setSmallIcon(categoryIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)  // flag so the notification is automatically dismissed when a user clicks on it
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(task.getCategory().getColor())
                .setStyle( bigText )
                .setVibrate(new long[] { 200, 200, 200, 200, 1000 })
                .setSound(alarmSound)
                ;
    }

    public void show(){
        //trigger the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(taskId, notification);

    }

}