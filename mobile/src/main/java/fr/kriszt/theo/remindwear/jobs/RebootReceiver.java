package fr.kriszt.theo.remindwear.jobs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import fr.kriszt.theo.remindwear.NotificationTestingActivity;
import fr.kriszt.theo.remindwear.TasksActivity;
import fr.kriszt.theo.remindwear.utils.SchedulerJobService;

/**
 * Created by T.Kriszt on 21/09/2018.
 * Replanifie les tâche au redémarrage du téléphone
 */
public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            showNotification(context);
        }

        Intent myIntent = new Intent(context, SchedulerJobService.class);
        context.startService(myIntent);

        Toast.makeText(context, "RemindWear : Démarrage détecté, replanification des alarmes", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "/!\\ Rescheduling Not implemented yet", Toast.LENGTH_SHORT).show();
        // Here you can schedule your Alarm again or start your service again.
//        SchedulerJobService.scheduleJob(context);

//        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        // Vibrate for 500 milliseconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(5000,VibrationEffect.DEFAULT_AMPLITUDE));
//        }else{
//            //deprecated in API 26
//            v.vibrate(5000);
//        }




    }
    private void showNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, TasksActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(0)
                        .setContentTitle("My notification de demarrage")
                        .setContentText("Hello World! du demarrage");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}