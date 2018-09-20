package fr.kriszt.theo.remindwear.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import fr.kriszt.theo.remindwear.NotificationTestingActivity;

/**
 * Created by T.Kriszt on 21/09/2018.
 */
public class SchedulerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationTestingActivity.scheduleTask(context);
        Toast.makeText(context, "SchedulerReceiver received an event", Toast.LENGTH_SHORT).show();
    }
}
