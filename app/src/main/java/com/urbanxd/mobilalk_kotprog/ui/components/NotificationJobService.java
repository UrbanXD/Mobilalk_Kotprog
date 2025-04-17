package com.urbanxd.mobilalk_kotprog.ui.components;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterRepository;
import com.urbanxd.mobilalk_kotprog.data.repository.WaterMeterStateRepository;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
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

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}