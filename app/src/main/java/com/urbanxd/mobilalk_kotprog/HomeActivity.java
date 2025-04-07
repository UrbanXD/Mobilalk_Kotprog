package com.urbanxd.mobilalk_kotprog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String firstname;
    private String lastname;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;
    private CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
        userCollection = database.collection("users");
        if (user == null) finish();

        LinearLayout logoAndTitle = findViewById(R.id.logoAndTitleContainer);
        logoAndTitle.post(() -> {
            Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
            logoAndTitle.startAnimation(slideIn);
        });

        userCollection
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.exists()) {
                        Log.d("HomeActivity", "User document not found");
                        return;
                    }

                    String firstname = documentSnapshot.getString("firstname");
                    String lastname = documentSnapshot.getString("lastname");

                    String welcomeMessage = "Üdv nálunk " + lastname + " " + firstname + "!";
                    TextView welcomeText = findViewById(R.id.welcomeText);
                    welcomeText.setText(welcomeMessage);
                }).addOnFailureListener(e -> {
                    Log.e("HomeActivity", "Hiba a felhasználó lekérdezésénél", e);
                });

        Log.d(HomeActivity.class.getName(), "aefeff "+user.getEmail());
    }

    public void logout(View view) {
        firebaseAuth.signOut();
        openMainActivity();
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); /// uriti a stacket, hogy back buttonnal veletlen se lehessen pl ide visszakerulni
        startActivity(intent);
    }
}