package fr.kriszt.theo.remindwear.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

import fr.kriszt.theo.remindwear.NotificationTestingActivity;

/**
 * Created by T.Kriszt on 21/09/2018.
 */
public class SchedulerJobService extends JobService {
    public static int count = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "Scheduler service started", Toast.LENGTH_SHORT).show();
        int max = 5;
        if (count++ < max){

            NotificationTestingActivity.scheduleTask(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Rescheduling,  " + count + "/" + max, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "Scheduler service stopped", Toast.LENGTH_SHORT).show();
        return true;
    }
}
