package fr.kriszt.theo.remindwear.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import fr.kriszt.theo.remindwear.NotificationTestingActivity;

/**
 * Created by T.Kriszt on 21/09/2018.
 */
public class RebootRescheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "RemindWear : Démarrage détecté, replanification des alarmes", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "/!\\ Rescheduling Not implemented yet", Toast.LENGTH_SHORT).show();
        // Here you can schedule your Alarm again or start your service again.
        SchedulerJobService.scheduleJob(context);
    }
}

// TODO : remove si le JobScheduler survit aux redemarrages