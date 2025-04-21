package com.urbanxd.mobilalk_kotprog.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView emailError, passwordError;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Utils.backButtonToolbarOnCreate(this);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("emailError", emailError.getText().toString());
        outState.putInt("emailErrorVisibility", emailError.getVisibility());

        outState.putString("passwordError", passwordError.getText().toString());
        outState.putInt("passwordErrorVisibility", passwordError.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        emailError.setText(savedInstanceState.getString("emailError", ""));
        emailError.setVisibility(savedInstanceState.getInt("emailErrorVisibility", View.GONE));

        passwordError.setText(savedInstanceState.getString("passwordError", ""));
        passwordError.setVisibility(savedInstanceState.getInt("passwordErrorVisibility", View.GONE));
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

        if (Utils.checkConnectionIsUnavailable(this)) {
            Utils.openToast(this, "Internet kapcsolat szükséges!");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                try {
                    Utils.openActivity(this, HomeActivity.class, true);
                    Utils.openToast(this, getString(R.string.success_login));

                    SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    boolean firstAuth = sharedPreferences.getBoolean(Utils.SHARED_PREFERENCE_FIRST_AUTH, true);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Utils.SHARED_PREFERENCE_USER_ID, Objects.requireNonNull(task.getResult().getUser()).getUid());
                    if (firstAuth) {
                        editor.putBoolean(Utils.SHARED_PREFERENCE_ASK_FOR_NOTIFICATION_PERMISSION, true);
                        editor.putBoolean(Utils.SHARED_PREFERENCE_FIRST_AUTH, false);
                    }
                    editor.apply();
                } catch (Exception ignored) { }

                return;
            }

            Exception exception = task.getException();
            String message = getString(R.string.unknown_firebase_error, "A bejelentkezés");

            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                message = getString(R.string.invalid_credentials_error);
            }

            Utils.openToast(this, message, Toast.LENGTH_LONG);
        });
    }

    public void openRegisterActivity(View view) {
        Utils.openActivity(this, RegisterActivity.class);
    }
}