package com.example.farmer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private var etMobileNumber: TextInputEditText? = null
    private var etPassword: TextInputEditText? = null
    private var mobileNumberLayout: TextInputLayout? = null
    private var passwordLayout: TextInputLayout? = null
    private var btnSignIn: MaterialButton? = null
    private var btnGoogleSignIn: MaterialButton? = null
    private var tvForgotPassword: TextView? = null
    private var tvCreateAccount: TextView? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        )

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if the user is already logged in
        val currentUser = firebaseAuth!!.getCurrentUser()
        if (currentUser != null) {
            val iHome = Intent(this@LoginActivity, HomePageActivity::class.java)
            startActivity(iHome)
            finish()
            return
        }

        // Initialize views
        etMobileNumber = findViewById<TextInputEditText>(R.id.etEmail)
        etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        mobileNumberLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        btnSignIn = findViewById<MaterialButton>(R.id.btnSignIn)
        btnGoogleSignIn = findViewById<MaterialButton>(R.id.btnGoogleSignIn)
        tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        // Sign In button click event
        btnSignIn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                loginUser()
            }
        })

        // Google Sign In button click event
        btnGoogleSignIn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                signInWithGoogle()
            }
        })

        // Forgot Password click event
        tvForgotPassword!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Show a dialog to get the user's email address
                showForgotPasswordDialog()
            }
        })


        // Create Account click event
        tvCreateAccount!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val iRegister = Intent(this@LoginActivity, create_account::class.java)
                startActivity(iRegister)
            }
        })
    }

    private fun loginUser() {
        val mobileNumber = etMobileNumber!!.getText().toString().trim { it <= ' ' }
        val password = etPassword!!.getText().toString().trim { it <= ' ' }

        if (TextUtils.isEmpty(mobileNumber)) {
            mobileNumberLayout!!.setError("Mobile number is required")
            return
        } else {
            mobileNumberLayout!!.setError(null)
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout!!.setError("Password is required")
            return
        } else {
            passwordLayout!!.setError(null)
        }

        firebaseAuth!!.signInWithEmailAndPassword(mobileNumber, password)
            .addOnCompleteListener(this, OnCompleteListener { task: Task<AuthResult?>? ->
                if (task!!.isSuccessful()) {
                    val user = firebaseAuth!!.getCurrentUser()
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                        .show()
                    val iHome = Intent(this@LoginActivity, HomePageActivity::class.java)
                    startActivity(iHome)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: " + task.getException()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Google Sign-In methods
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult<ApiException?>(ApiException::class.java)
                firebaseAuthWithGoogle(account.getIdToken())
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful()) {
                        val user = firebaseAuth!!.getCurrentUser()
                        Toast.makeText(
                            this@LoginActivity,
                            "Google Sign-In successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val iHome = Intent(this@LoginActivity, HomePageActivity::class.java)
                        startActivity(iHome)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication failed: " + task.getException()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }


    // Method to show the Forgot Password dialog
    private fun showForgotPasswordDialog() {
        // Create a dialog for email input
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle("Reset Password")

        // Add an input field for the email
        val inputEmail = EditText(this@LoginActivity)
        inputEmail.setHint("Enter your email")
        builder.setView(inputEmail)

        // Add Reset and Cancel buttons
        builder.setPositiveButton("Reset", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val email = inputEmail.getText().toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this@LoginActivity, "Email is required", Toast.LENGTH_SHORT)
                        .show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter a valid email address",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sendPasswordResetEmail(email) // Call the method to reset the password
                }
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss() // Close the dialog
            }
        })

        // Show the dialog
        builder.create().show()
    }

    // Method to send password reset email
    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener(OnCompleteListener { task: Task<Void?>? ->
                if (task!!.isSuccessful()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Reset email sent successfully. Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val errorMessage =
                        if (task.getException() != null) task.getException()!!.message else "Failed to send reset email"
                    Toast.makeText(this@LoginActivity, "Error: " + errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}