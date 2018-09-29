package fr.kriszt.theo.remindwear;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import fr.kriszt.theo.remindwear.tasker.Task;

public class RemindNotification {
    public static final String TAG = "REMINDER_NOTIFICATION";
    private final static String notification_channel = "Wear Event Reminder";

    private String title, content;
    private Context context;
    private int taskId;

    public RemindNotification(Task task, Context c){
        Log.w(TAG, "RemindNotification: " + task + "(TASKID : " + task.getID() + ")");
        title = task.getCategory().getName() + " : " + task.getName();
        content = task.getDescription();
        taskId = task.getID();
        context = c;

    }

    public void show(@Nullable PendingIntent pendingIntent){
        //show the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, notification_channel)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(title)
                        .setContentText(content)
//                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)  // flag so the notification is automatically dismissed when a user clicks on it
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //trigger the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        //we give each notification the ID of the event it's describing,
        //to ensure they all show up and there are no duplicates
        notificationManager.notify(taskId, notificationBuilder.build());
        Log.w(TAG, "show: Showing notification");
    }







}
