package fr.kriszt.theo.remindwear.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;


/**
 * @deprecated
 */
public class ReminderJobCreator implements JobCreator {

    public static final String TAG = "ReminderJobCreator";



    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SetReminderJob.TAG:
                return new SetReminderJob();
            default:
                return null;
        }
    }
}
