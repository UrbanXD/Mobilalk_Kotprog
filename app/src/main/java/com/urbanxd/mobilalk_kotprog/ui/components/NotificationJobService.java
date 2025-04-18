package com.urbanxd.mobilalk_kotprog.ui.components;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterRepository;
import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterStateRepository;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

import java.util.concurrent.TimeUnit;

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        int jobId = jobParameters.getJobId();

        if (jobId == Utils.CREATE_NEW_STATE_NOTIFICATION_LATENCY_JOB_ID) {
            ComponentName componentName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
            JobInfo.Builder builder = new JobInfo
                .Builder(Utils.CREATE_NEW_STATE_SEND_NOTIFICATION_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15));

            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        }

        if (jobId == Utils.CREATE_NEW_STATE_SEND_NOTIFICATION_JOB_ID) {
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString(Utils.SHARED_PREFERENCE_USER_ID, null);

            if (userId == null) return false;

            WaterMeterRepository waterMeterRepository = new WaterMeterRepository();
            WaterMeterStateRepository waterMeterStateRepository = new WaterMeterStateRepository();

            waterMeterRepository.getWaterMeter(userId, result -> {
                if (result.data == null) return;

                waterMeterStateRepository.isNewStateCreatedInTheLast10Minutes(result.data.getId(), result2 -> {
                    if (!result2.data) new NotificationHandler(getApplicationContext()).sendNotification("BIGG, pls add new state PLS");
                });
            });
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}