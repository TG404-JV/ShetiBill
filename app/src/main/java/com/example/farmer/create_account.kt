package com.example.farmer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class create_account : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    // Declare UI elements
    private var etName: TextInputEditText? = null
    private var etMobileNumber: TextInputEditText? = null
    private var etEmail: TextInputEditText? = null
    private var etPassword: TextInputEditText? = null
    private var btnCreateAccount: MaterialButton? = null
    private var tvAlreadyHaveAccount: TextView? = null
    private var tvSkipCreate: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Keeps it hidden until user swipes
        )

        // Initialize UI elements
        etName = findViewById<TextInputEditText>(R.id.etName)
        etMobileNumber = findViewById<TextInputEditText>(R.id.etMobileNumberCreate)
        etEmail = findViewById<TextInputEditText>(R.id.etEmailCreate)
        etPassword = findViewById<TextInputEditText>(R.id.etPasswordCreate)
        btnCreateAccount = findViewById<MaterialButton>(R.id.btnCreateAccount)
        tvAlreadyHaveAccount = findViewById<TextView>(R.id.tvAlreadyHaveAccount)
        tvSkipCreate = findViewById<TextView>(R.id.tvSkipCreate)

        // Handle "Create Account" button click
        btnCreateAccount!!.setOnClickListener(View.OnClickListener { v: View? -> createAccount() })

        // Handle "Already have an account? Log in" click
        tvAlreadyHaveAccount!!.setOnClickListener(View.OnClickListener { v: View? ->
            val loginIntent = Intent(this@create_account, MainActivity::class.java)
            startActivity(loginIntent)
        })

        // Handle "Skip" button click
        tvSkipCreate!!.setOnClickListener(View.OnClickListener { v: View? ->
            val skipIntent = Intent(
                this@create_account,
                MainActivity::class.java
            ) // Replace with the target activity
            startActivity(skipIntent)
        })
    }

    // Method to handle account creation logic
    private fun createAccount() {
        val name = etName!!.getText().toString().trim { it <= ' ' }
        val mobileNumber = etMobileNumber!!.getText().toString().trim { it <= ' ' }
        val email = etEmail!!.getText().toString().trim { it <= ' ' }
        val password = etPassword!!.getText().toString().trim { it <= ' ' }

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName!!.setError("Full name is required")
            return
        }

        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length != 10) {
            etMobileNumber!!.setError("Valid 10-digit mobile number is required")
            return
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail!!.setError("Valid email is required")
            return
        }

        if (TextUtils.isEmpty(password) || password.length < 6) {
            etPassword!!.setError("Password should be at least 6 characters")
            return
        }

        // Firebase Authentication for creating the user account
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener { task: Task<AuthResult?>? ->
                if (task!!.isSuccessful()) {
                    // Account creation successful, save user data to Firebase Database
                    val user = mAuth!!.getCurrentUser()
                    if (user != null) {
                        val userProfile = User(name, mobileNumber)
                        mDatabase!!.child(user.getUid()).setValue(userProfile)
                            .addOnCompleteListener(OnCompleteListener { task1: Task<Void?>? ->
                                if (task1!!.isSuccessful()) {
                                    // Success, redirect to another activity
                                    Toast.makeText(
                                        this@create_account,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@create_account, FarmerDetails::class.java)
                                    startActivity(intent)
                                    finish() // Close current activity
                                } else {
                                    // Failed to save user data
                                    Toast.makeText(
                                        this@create_account,
                                        "Failed to save user data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                } else {
                    // If sign-in fails
                    val errorMessage =
                        if (task.getException() != null) task.getException()!!.message else "Authentication failed."
                    Toast.makeText(this@create_account, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // User class to store user data in Firebase
    class User {
        var name: String? = null
        var mobileNumber: String? = null

        constructor()

        constructor(name: String?, mobileNumber: String?) {
            this.name = name
            this.mobileNumber = mobileNumber
        }
    }
}