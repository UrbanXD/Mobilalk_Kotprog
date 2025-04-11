package com.urbanxd.mobilalk_kotprog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            finish();
            return;
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadUser(firebaseUser);

        TextView welcomeText = findViewById(R.id.welcomeText);

        userViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                String welcomeMessage = "Üdv nálunk " + user.getLastname() + " " + user.getFirstname() + "!";
                welcomeText.setText(welcomeMessage);
            }
        });

        LinearLayout logoAndTitle = findViewById(R.id.logoAndTitleContainer);
        logoAndTitle.post(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logoAndTitle.startAnimation(slideIn);
        });
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        openMainActivity();
        Toast.makeText(getApplicationContext(), getString(R.string.success_logout), Toast.LENGTH_SHORT).show();
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); /// uriti a stacket, hogy back buttonnal veletlen se lehessen pl ide visszakerulni
        startActivity(intent);
    }
}