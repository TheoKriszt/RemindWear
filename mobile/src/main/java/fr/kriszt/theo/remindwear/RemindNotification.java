package fr.kriszt.theo.remindwear;

import android.app.Notification;
//import android.app.PendingIntent;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;
import fr.kriszt.theo.remindwear.wear.PhoneDataService;
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

    public RemindNotification(Task task, Context c){
        title = task.getName();
        content = "Commence à " + task.getTimeHour() + "h" + task.getTimeMinutes();
        taskId = task.getID();
        context = c;
//        bigText = "Rappel : " + task.getName() + " à " + task.getTimeHour() + "h" + task.getTimeMinutes()
//                    + "\n\t" + content;


        String longText = "Commence à " + String.format("%02d", task.getTimeHour()) + "h" + String.format("%02d", task.getTimeMinutes())
                + "<p>" + task.getDescription() + "</p>";
        bigText = new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(longText));
//        bigText = new NotificationCompat.BigTextStyle().bigText(task.getName() + " commence à " + task.getTimeHour() + "h" + task.getTimeMinutes());



        NotificationCompat.Builder builder = build(task);
        NotificationCompat.WearableExtender extender = buildExtender(task);

        addActions(task, builder, extender);

        builder.extend(extender);
        builder.setAutoCancel(true);

        notification = builder.build();



    }

    private void addActions(Task task, NotificationCompat.Builder builder, NotificationCompat.WearableExtender extender) {
        // Ajouter l'action START TRACKING pour la catégorie sport

        Intent trackingIntent = new Intent(context, TasksActivity.class);
        trackingIntent.putExtra(TasksActivity.FRAGMENT_TO_LAUNCH, SportTaskListFragment.class.getName());
        trackingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingTrackingIntent = PendingIntent.getActivity(context, 0, trackingIntent, 0);

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
                R.drawable.ic_snooze, "Later", pendingPostponeIntent
        ));

    }

    private NotificationCompat.Builder build(Task task){
        int categoryIcon = task.getCategory().getIcon();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, notification_channel)
                        .setSmallIcon(categoryIcon)
                        .setContentTitle(title)
                        .setContentText(content)
//                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)  // flag so the notification is automatically dismissed when a user clicks on it
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setColor(task.getCategory().getColor())
//                        .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setStyle( bigText )
                        .setVibrate(new long[] { 200, 200, 200, 200, 200 });
//                        .setStyle( new NotificationCompat.BigTextStyle().bigText(bigText));



        return builder;
    }

    private NotificationCompat.WearableExtender buildExtender(Task task) {
        return new NotificationCompat.WearableExtender();

    }

    public void show(@Nullable PendingIntent pendingIntent){
        //trigger the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        //we give each notification the ID of the event it's describing,
        //to ensure they all show up and there are no duplicates
        notificationManager.notify(taskId, notification);
    }

}
