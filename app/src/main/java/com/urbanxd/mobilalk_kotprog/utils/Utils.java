package com.urbanxd.mobilalk_kotprog.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.ui.components.NotificationHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public final class Utils {
    public static final String SHARED_PREFERENCES_NAME = "appPreferences";
    public static final String SHARED_PREFERENCE_FIRST_REGISTER = "firstRegister";
    public static final String SHARED_PREFERENCE_ASK_FOR_NOTIFICATION_PERMISSION = "askForNotificationPermission";
    public static final String SHARED_PREFERENCE_USER_ID = "userId";

    public static final int CREATE_NEW_STATE_NOTIFICATION_LATENCY_JOB_ID = 99;
    public static final int CREATE_NEW_STATE_SEND_NOTIFICATION_JOB_ID = 100;

    private static String pendingNotificationMessage = null;

    private Utils() { }

    public static void openActivity(Context context, Class<?> cls) {
        openActivity(context, cls, false);
    }

    public static void openActivity(Context context, Class<?> cls, boolean stackClearFlag) {
        Intent intent = new Intent(context, cls);

        if (stackClearFlag) {
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        context.startActivity(intent);
    }

    public static void openToast(Context context, String message) {
        openToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void openToast(Context context, String message, int duration) {
        Toast.makeText(context.getApplicationContext(), message, duration).show();
    }

    public static void sendNotification(Activity activity, String message) {
        Context context = activity.getBaseContext();
        NotificationHandler notificationHandler = new NotificationHandler(context);

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            pendingNotificationMessage = message;
            ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                1001
            );
        }

        notificationHandler.sendNotification(message);
    }
    
    public static void handlePermissionResult(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        NotificationHandler notificationHandler = new NotificationHandler(context);

        if (requestCode == NotificationHandler.REQUEST_CODE) {
            if (
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if(pendingNotificationMessage == null) return;

                notificationHandler.sendNotification(pendingNotificationMessage);
                pendingNotificationMessage = null;
                return;
            }
            Utils.openToast(context, "Értesítési engedély megtagadva");
        }
    }

    public static void cancelNotification(Context context) {
        NotificationHandler notificationHandler = new NotificationHandler(context);

        notificationHandler.cancelNotification();
    }

    public static void backButtonToolbarOnCreate(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> activity.finish());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_back_arrow));
    }

    public static long getNumberTextInput(EditText input, long stepperValue, long minValue, long maxValue) {
        String text = input.getText().toString().trim();

        try {
            return getValueInsideBounds(Long.parseLong(text) + stepperValue, minValue, maxValue);
        } catch (NumberFormatException e) {
            return minValue;
        }
    }

    private static long getValueInsideBounds(long value, long minValue, long maxValue) {
        long result = Math.max(value, minValue + 1);
        if (maxValue > -1 && maxValue > minValue) result = Math.min(result, maxValue - 1);

        if (result < 0) result = 0;
        return result;
    }

    public static String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }
}
