package com.urbanxd.mobilalk_kotprog.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.urbanxd.mobilalk_kotprog.R;

import java.util.Objects;

public final class Utils {
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

    public static void backButtonToolbarOnCreate(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> activity.finish());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_back_arrow));

    }
}
