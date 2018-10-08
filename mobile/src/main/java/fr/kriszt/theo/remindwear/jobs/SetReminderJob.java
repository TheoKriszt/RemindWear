package fr.kriszt.theo.remindwear.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.TasksActivity;

/**
 * @deprecated
 */
public class SetReminderJob extends Job {
    public static final String TAG = "set_reminder_job";
    private static int executions = 0;
    private static final int MAX_EXECUTIONS = 5;
    private static Context mContext = null;


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        // run your job here
        Log.w(TAG, "onRunJob: SUCCESS, execution no " + executions);
        sendNotification();
        executions++;
        if (executions < MAX_EXECUTIONS){
            scheduleJob(mContext);
        }else {
            executions = 0;
        }
        return Result.SUCCESS;
    }

    private void  sendNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("My Notification Title")
                        .setContentText("Job sent a notification");
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(getContext(), TasksActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void scheduleJob(@Nullable Context context) {
        mContext = context;
//        if (mContext != null) {
//            Toast.makeText(context, "scheduleJob()", Toast.LENGTH_SHORT).show();
//        }
        Log.w(TAG, "scheduleJob: SCHEDULED");

//        new JobRequest.Builder(SetReminderJob.TAG)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
//                .setUpdateCurrent(true)
////                .setPersisted(true)
//                .show()
//                .schedule();

        new JobRequest.Builder(SetReminderJob.TAG)
                .setExecutionWindow(1_000L, 3_000L)
                .build()
                .schedule();
    }
}
