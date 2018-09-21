package fr.kriszt.theo.remindwear.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

public class SetReminderJob extends Job {
    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return null;
    }

    public static void scheduleJob() {

    }
}
