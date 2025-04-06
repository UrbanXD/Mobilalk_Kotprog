package com.urbanxd.mobilalk_kotprog;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput;
    EditText firstnameInput;
    EditText lastnameInput;
    EditText passwordInput;
    EditText repeatPasswordInput;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;
    private CollectionReference userCollection;
    private CollectionReference watermeterCollection;
    private CollectionReference watermeterStateCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_arrow));

        sharedPreferences = getSharedPreferences(Objects.requireNonNull(RegisterActivity.class.getPackage()).toString(), MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        userCollection = database.collection("users");
        watermeterCollection = database.collection("watermeters");
        watermeterStateCollection = database.collection("watermeterStates");

        emailInput = findViewById(R.id.emailInput);
        firstnameInput = findViewById(R.id.firstNameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        passwordInput = findViewById(R.id.passwordInput);
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String email = emailInput.getText().toString();
        String firstname = firstnameInput.getText().toString();
        String lastname = lastnameInput.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.apply();
    }

    public void register(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String repeatPassword = repeatPasswordInput.getText().toString();

        if (!password.equals(repeatPassword)) return;

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userUID = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    createUserProfile(userUID);
                    openHomeActivity();
                    return;
                }


                Log.d(RegisterActivity.class.getName(), "HIBA " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
        Log.i(RegisterActivity.class.getName(), email + " " + password);
    }

    protected void createUserProfile (String userUID) {
        String firstname = firstnameInput.getText().toString();
        String lastname = lastnameInput.getText().toString();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("firstname", firstname);
        userProfile.put("lastname", lastname);

        userCollection
                .document(userUID)
                .set(userProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "User profile saved.");
                        createWatermeter(userUID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error saving user", e);
                    }
                });
    }

    protected void createWatermeter (String userUID) {
        String watermeterUID = UUID.randomUUID().toString();

        Map<String, Object> watermeter = new HashMap<>();
        watermeter.put("uuid", userUID);

        watermeterCollection.document(watermeterUID).set(watermeter);
        database
            .collection("watermeters")
            .document(watermeterUID).set(watermeter);

        Map<String, Object> watermeterState = new HashMap<>();
        watermeterState.put("watermeterID", watermeterUID);
        watermeterState.put("date", Timestamp.now());
        watermeterState.put("state", 0);
        database.collection("watermeterStates").add(watermeterState);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); /// uriti a stacket, hogy back buttonnal veletlen se lehessen pl ide visszakerulni
        startActivity(intent);
    }
}