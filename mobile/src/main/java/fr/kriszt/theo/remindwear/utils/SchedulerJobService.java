package fr.kriszt.theo.remindwear.utils;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import fr.kriszt.theo.remindwear.jobs.SetReminderJob;

/**
 * Created by T.Kriszt on 21/09/2018.
 */
public class SchedulerJobService extends JobService {
    public static int count = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        Handler handler = new Handler(Looper.getMainLooper());
        final Service service = this;
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(service, "Scheduler service started", Toast.LENGTH_SHORT).show();
            }
        });

        SetReminderJob.scheduleJob(this);
        int max = 5;
        if (count++ < max){

            scheduleJob(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Rescheduling,  " + count + "/" + max, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "Scheduler service stopped", Toast.LENGTH_SHORT).show();
        return true;
    }

    public static void scheduleJob(Context context){

        ComponentName serviceComponent = new ComponentName(context, SchedulerJobService.class);
        JobInfo.Builder schedulerJobServiceBuilder = new JobInfo.Builder(0, serviceComponent);
        schedulerJobServiceBuilder.setMinimumLatency(1 * 4000); // wait at least
        schedulerJobServiceBuilder.setOverrideDeadline(3 * 5000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(schedulerJobServiceBuilder.build());

        Toast.makeText(context, "Scheduling job in 4-5 seconds...", Toast.LENGTH_SHORT).show();

    }
}
