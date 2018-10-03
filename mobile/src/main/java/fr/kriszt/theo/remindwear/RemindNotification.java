package fr.kriszt.theo.remindwear;

import android.app.Notification;
//import android.app.PendingIntent;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;

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

        String longText = "" //+ "<big>" + task.getName() + "</big><br/>"
                + "Commence à " + task.getTimeHour() + "h" + task.getTimeMinutes()
                + "<p>" + task.getDescription() + "</p>";
        bigText = new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(longText));
//        bigText = new NotificationCompat.BigTextStyle().bigText(task.getName() + " commence à " + task.getTimeHour() + "h" + task.getTimeMinutes());



        NotificationCompat.Builder builder = build(task);
        NotificationCompat.WearableExtender extender = buildExtender(task);

        addActions(task, builder, extender);

        builder.extend(extender);

        notification = builder.build();



    }

    private void addActions(Task task, NotificationCompat.Builder builder, NotificationCompat.WearableExtender extender) {
        // Ajouter l'action START TRACKING pour la catégorie sport

        Intent intent = new Intent(context, TasksActivity.class);
        intent.putExtra(TasksActivity.FRAGMENT_TO_LAUNCH, SportTaskListFragment.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);



//        builder
//                .setSmallIcon(R.drawable.ic_base_0)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);

        builder.addAction(new NotificationCompat.Action(
                R.drawable.baseline_directions_run_24, "Start tracking", pendingIntent
        ));

//
//        if (task.getCategory() != null) {
//            if (task.getCategory().getName().equals(Tasker.CATEGORY_SPORT_TAG)) {
//                extender.addAction(new NotificationCompat.Action(
//                        R.drawable.baseline_directions_run_24,
//                        "Start tracking",
//                        pendingIntent)
//                );
//
//                builder.setContentIntent(pendingIntent);
//            }
//        }
//
//        // TODO : Ajouter l'action Snooze (me rappeler dans 10 minutes)
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
                        .setStyle( bigText );
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
