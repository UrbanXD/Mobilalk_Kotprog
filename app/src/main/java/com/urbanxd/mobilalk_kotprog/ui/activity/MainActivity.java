package com.urbanxd.mobilalk_kotprog.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) Utils.openActivity(this, HomeActivity.class, true);

        LinearLayout logoAndTitle = findViewById(R.id.logoAndTitleContainer);
        logoAndTitle.post(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logoAndTitle.startAnimation(slideIn);
        });
    }

    public void openRegisterActivity(View view) {
        Utils.openActivity(this, RegisterActivity.class);
    }

    public void openLoginActivity(View view) {
        Utils.openActivity(this, LoginActivity.class);
    }
}