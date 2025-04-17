package com.urbanxd.mobilalk_kotprog.ui.components;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.ui.activity.HomeActivity;

public class NotificationHandler {
    private static final String CHANNEL_ID = "water_meter_notification_channel";
    private final int NOTIFICATION_ID = 0;
    public static final int REQUEST_CODE = 1001;

    private final NotificationManager notificationManager;
    private final Context context;

    public NotificationHandler(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Vízóra Értesítő",
            NotificationManager.IMPORTANCE_HIGH
        );
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Értesítések a Vízóra appból.");

        this.notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String content) {
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Vízóra App")
                .setContentText(content)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        this.notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotification() {
        this.notificationManager.cancel(NOTIFICATION_ID);
    }
}
