package com.urbanxd.mobilalk_kotprog.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.urbanxd.mobilalk_kotprog.R;
import com.urbanxd.mobilalk_kotprog.data.repository.UserRepository;
import com.urbanxd.mobilalk_kotprog.utils.Utils;

public class RegisterActivity extends AppCompatActivity {
    EditText emailInput, firstnameInput, lastnameInput, passwordInput, repeatPasswordInput;
    TextView emailError, firstnameError, lastnameError, passwordError, repeatPasswordError;

    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private boolean firstRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Utils.backButtonToolbarOnCreate(this);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, 0);
        firstRegister = sharedPreferences.getBoolean(Utils.SHARED_PREFERENCE_FIRST_REGISTER, true);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Utils.handlePermissionResult(this, requestCode, permissions, grantResults);
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
        }

        if (hasError) return;

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = task.getResult().getUser();
                if(firebaseUser == null) return;

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Utils.SHARED_PREFERENCE_USER_ID, firebaseUser.getUid());
                    editor.apply();
                } catch (Exception ignored) { }

                new UserRepository().createUser(firebaseUser, firstname, lastname);

                Utils.openActivity(this, HomeActivity.class, true);
                Utils.openToast(getApplicationContext(), getString(R.string.success_register));

                if (!firstRegister) return; //ha mar volt regisztralva akk stop

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.SHARED_PREFERENCE_FIRST_REGISTER, false);
                editor.putBoolean(Utils.SHARED_PREFERENCE_ASK_FOR_NOTIFICATION_PERMISSION, true);
                editor.apply();

                return;
            }

            Exception exception = task.getException();
            String message = getString(R.string.unknown_firebase_error, "A regisztráció");

            if (exception instanceof FirebaseAuthUserCollisionException) {
                message = getString(R.string.email_already_registered_error);
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}