package com.urbanxd.mobilalk_kotprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, firstnameInput, lastnameInput, passwordInput, repeatPasswordInput;
    TextView emailError, firstnameError, lastnameError, passwordError, repeatPasswordError;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore database;
    private CollectionReference userCollection;
    private CollectionReference watermeterCollection;
    private CollectionReference watermeterStateCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_arrow));

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

        emailError = findViewById(R.id.emailError);
        firstnameError = findViewById(R.id.firstNameError);
        lastnameError = findViewById(R.id.lastnameError);
        passwordError = findViewById(R.id.passwordError);
        repeatPasswordError = findViewById(R.id.repeatPasswordError);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("emailError", emailError.getText().toString());
        outState.putInt("emailErrorVisibility", emailError.getVisibility());

        outState.putString("firstnameError", firstnameError.getText().toString());
        outState.putInt("firstnameErrorVisibility", firstnameError.getVisibility());

        outState.putString("lastnameError", lastnameError.getText().toString());
        outState.putInt("lastnameErrorVisibility", lastnameError.getVisibility());

        outState.putString("passwordError", passwordError.getText().toString());
        outState.putInt("passwordErrorVisibility", passwordError.getVisibility());

        outState.putString("repeatPasswordError", repeatPasswordError.getText().toString());
        outState.putInt("repeatPasswordErrorVisibility", repeatPasswordError.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        emailError.setText(savedInstanceState.getString("emailError", ""));
        emailError.setVisibility(savedInstanceState.getInt("emailErrorVisibility", View.GONE));

        firstnameError.setText(savedInstanceState.getString("firstnameError", ""));
        firstnameError.setVisibility(savedInstanceState.getInt("firstnameErrorVisibility", View.GONE));

        lastnameError.setText(savedInstanceState.getString("lastnameError", ""));
        lastnameError.setVisibility(savedInstanceState.getInt("lastnameErrorVisibility", View.GONE));

        passwordError.setText(savedInstanceState.getString("passwordError", ""));
        passwordError.setVisibility(savedInstanceState.getInt("passwordErrorVisibility", View.GONE));

        repeatPasswordError.setText(savedInstanceState.getString("repeatPasswordError", ""));
        repeatPasswordError.setVisibility(savedInstanceState.getInt("repeatPasswordErrorVisibility", View.GONE));
    }

    public void register(View view) {
        String email = emailInput.getText().toString();
        String lastname = lastnameInput.getText().toString();
        String firstname = firstnameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String repeatPassword = repeatPasswordInput.getText().toString();

        emailError.setVisibility(View.GONE);
        lastnameError.setVisibility(View.GONE);
        firstnameError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
        repeatPasswordError.setVisibility(View.GONE);

        boolean hasError = false;

        if (email.isEmpty()) {
            emailError.setText(getString(R.string.required_input_field, "Az email cím"));
            emailError.setVisibility(View.VISIBLE);
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setText(getString(R.string.invalid_email_format));
            emailError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (lastname.isEmpty()) {
            lastnameError.setText(getString(R.string.required_input_field, "A vezetéknév"));
            lastnameError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (firstname.isEmpty()) {
            firstnameError.setText(getString(R.string.required_input_field, "A keresztnév"));
            firstnameError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (password.isEmpty()) {
            passwordError.setText(getString(R.string.required_input_field, "A jelszó"));
            passwordError.setVisibility(View.VISIBLE);
            hasError = true;
        } else if (password.length() < 6) {
            passwordError.setText(getString(R.string.invalid_password));
            passwordError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordError.setText(getString(R.string.invalid_repeat_password));
            repeatPasswordError.setVisibility(View.VISIBLE);
            hasError = true;
        } else if (repeatPassword.isEmpty()) {
            repeatPasswordError.setText(getString(R.string.required_input_field, "A megerősítő jelszó"));
            repeatPasswordError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (hasError) return;

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String userUID = Objects.requireNonNull(task.getResult().getUser()).getUid();
                createUserProfile(userUID, firstname, lastname);
                openHomeActivity();
                Toast.makeText(getApplicationContext(), getString(R.string.success_register), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getApplicationContext(), getString(R.string.unknown_firebase_error, "A regisztráció"), Toast.LENGTH_LONG).show();
        });
    }

    protected void createUserProfile (String userUID, String firstname, String lastname) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("firstname", firstname);
        userProfile.put("lastname", lastname);

        userCollection
                .document(userUID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> createWatermeter(userUID));
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
        watermeterStateCollection.add(watermeterState);
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