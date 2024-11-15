package com.example.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmer.MainActivity;
import com.example.farmer.R;
import com.example.farmer.create_account;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etMobileNumber, etPassword;
    private TextInputLayout mobileNumberLayout, passwordLayout;
    private Button btnSignIn;
    private TextView tvForgotPassword, tvCreateAccount;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to MainActivity
            Intent iHome = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(iHome);
            finish();
            return; // Exit the activity to prevent further execution
        }

        // Initialize views
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etPassword = findViewById(R.id.etPassword);
        mobileNumberLayout = findViewById(R.id.mobileNumberLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);



        // Sign In button click event
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Forgot Password click event
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password logic
                Toast.makeText(LoginActivity.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
                // Here, you can redirect to the ForgotPasswordActivity or open a dialog to enter email for password reset.
            }
        });

        // Create Account click event
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the CreateAccount activity
                Intent iRegister = new Intent(LoginActivity.this, create_account.class);
                startActivity(iRegister);
            }
        });
    }

    private void loginUser() {
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(mobileNumber)) {
            mobileNumberLayout.setError("Mobile number is required");
            return;
        } else {
            mobileNumberLayout.setError(null); // Clear the error
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return;
        } else {
            passwordLayout.setError(null); // Clear the error
        }

        // Authenticate user with Firebase using email and password
        // (You will likely need to adjust the logic if logging in by mobile number)
        firebaseAuth.signInWithEmailAndPassword(mobileNumber, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Redirect to main activity or dashboard
                        Intent iHome = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(iHome);
                        finish();
                    } else {
                        // Sign-in failed
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}

