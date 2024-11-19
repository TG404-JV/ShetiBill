package com.example.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class create_account extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Declare UI elements
    private EditText etName, etMobileNumber, etEmail, etPassword;
    private Button btnCreateAccount;
    private TextView tvAlreadyHaveAccount, tvSkipCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keeps it hidden until user swipes
        );

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etMobileNumber = findViewById(R.id.etMobileNumberCreate);
        etEmail = findViewById(R.id.etEmailCreate);
        etPassword = findViewById(R.id.etPasswordCreate);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
        tvSkipCreate = findViewById(R.id.tvSkipCreate);

        // Handle "Create Account" button click
        btnCreateAccount.setOnClickListener(v -> createAccount());

        // Handle "Already have an account? Log in" click
        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent loginIntent = new Intent(create_account.this, MainActivity.class);
            startActivity(loginIntent);
        });

        // Handle "Skip" button click
        tvSkipCreate.setOnClickListener(v -> {
            Intent skipIntent = new Intent(create_account.this, MainActivity.class); // Replace with the target activity
            startActivity(skipIntent);
        });
    }

    // Method to handle account creation logic
    private void createAccount() {
        String name = etName.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Full name is required");
            return;
        }

        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length() != 10) {
            etMobileNumber.setError("Valid 10-digit mobile number is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters");
            return;
        }

        // Firebase Authentication for creating the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Account creation successful, save user data to Firebase Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User userProfile = new User(name, mobileNumber);
                            mDatabase.child(user.getUid()).setValue(userProfile)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Success, redirect to another activity
                                            Toast.makeText(create_account.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(create_account.this, FarmerDetails.class);
                                            startActivity(intent);
                                            finish(); // Close current activity
                                        } else {
                                            // Failed to save user data
                                            Toast.makeText(create_account.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If sign-in fails
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                        Toast.makeText(create_account.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User class to store user data in Firebase
    public static class User {
        public String name;
        public String mobileNumber;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String name, String mobileNumber) {
            this.name = name;
            this.mobileNumber = mobileNumber;
        }
    }
}
