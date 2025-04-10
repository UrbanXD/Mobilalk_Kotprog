package com.urbanxd.mobilalk_kotprog;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView emailError, passwordError;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_arrow));

        firebaseAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
    }

    public void login(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        emailError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);

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

        if (password.isEmpty()) {
            passwordError.setText(getString(R.string.required_input_field, "A jelszó"));
            passwordError.setVisibility(View.VISIBLE);
            hasError = true;
        }

        if (hasError) return;

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                openHomeActivity();
                return;
            }

            Exception exception = task.getException();
            String message = getString(R.string.unknown_firebase_error, "A bejelentkezés");

            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                message = getString(R.string.invalid_credentials_error);
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    public void openRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); /// uriti a stacket, hogy back buttonnal veletlen se lehessen pl ide visszakerulni
        startActivity(intent);
    }
}