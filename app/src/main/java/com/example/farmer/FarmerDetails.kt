package com.example.farmer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class FarmerDetails : AppCompatActivity() {
    private var farmerNameLayout: LinearLayout? = null
    private var dobLayout: LinearLayout? = null
    private var mobileNumberLayout: LinearLayout? = null
    private var farmerOwnerLayout: LinearLayout? = null
    private var paymentTypeLayout: LinearLayout? = null
    private var paymentPerDayLayout: LinearLayout? = null
    private var paymentPerKgLayout: LinearLayout? = null
    private var dayOfPaymentLayout: LinearLayout? = null
    private var etFarmerName: TextInputEditText? = null
    private var etDob: TextInputEditText? = null
    private var etMobileNumber: TextInputEditText? = null
    private var etFarmerOwner: TextInputEditText? = null
    private var etPaymentPerDay: TextInputEditText? = null
    private var etPaymentPerKg: TextInputEditText? = null
    private var etDayOfPayment: TextInputEditText? = null
    private var radioGroupPaymentType: RadioGroup? = null
    private var rbWeekly: RadioButton? = null
    private var rbMonthly: RadioButton? = null
    private var farmerData: MutableMap<String?, String?>? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.farmer_details)

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) // Keeps it hidden until user swipes
        )

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FarmerDetails", MODE_PRIVATE)

        // Initialize layouts and fields
        initializeViews()

        // Initialize farmerData HashMap
        farmerData = HashMap<String?, String?>()
    }

    private fun initializeViews() {
        farmerNameLayout = findViewById<LinearLayout?>(R.id.farmerNameLayout)
        dobLayout = findViewById<LinearLayout?>(R.id.dobLayout)
        mobileNumberLayout = findViewById<LinearLayout?>(R.id.mobileNumberLayout)
        farmerOwnerLayout = findViewById<LinearLayout?>(R.id.farmerOwnerLayout)
        paymentTypeLayout = findViewById<LinearLayout?>(R.id.paymentTypeLayout)
        paymentPerDayLayout = findViewById<LinearLayout?>(R.id.paymentPerDayLayout)
        paymentPerKgLayout = findViewById<LinearLayout?>(R.id.paymentPerKgLayout)
        dayOfPaymentLayout = findViewById<LinearLayout?>(R.id.dayOfPaymentLayout)

        etFarmerName = findViewById<TextInputEditText>(R.id.etFarmerName)
        etDob = findViewById<TextInputEditText>(R.id.etDob)
        etMobileNumber = findViewById<TextInputEditText>(R.id.etMobileNumber)
        etFarmerOwner = findViewById<TextInputEditText>(R.id.etFarmerOwner)
        etPaymentPerDay = findViewById<TextInputEditText>(R.id.etPaymentPerDay)
        etPaymentPerKg = findViewById<TextInputEditText>(R.id.etPaymentPerKg)
        etDayOfPayment = findViewById<TextInputEditText>(R.id.etDayOfPayment)

        radioGroupPaymentType = findViewById<RadioGroup>(R.id.radioGroupPaymentType)
        rbWeekly = findViewById<RadioButton?>(R.id.rbWeekly)
        rbMonthly = findViewById<RadioButton?>(R.id.rbMonthly)
    }

    // Method to show the next screen with validation
    fun showNextScreen(view: View) {
        val id = view.getId()
        var nextField: String? = null
        var currentLayout: LinearLayout? = null
        var nextLayout: LinearLayout? = null

        if (id == R.id.btnNextFarmerName) {
            if (validateField(etFarmerName!!)) {
                nextField = "farmerName"
                currentLayout = farmerNameLayout
                nextLayout = dobLayout
            }
        } else if (id == R.id.btnNextDob) {
            if (validateField(etDob!!)) {
                nextField = "dob"
                currentLayout = dobLayout
                nextLayout = mobileNumberLayout
            }
        } else if (id == R.id.btnNextMobileNumber) {
            if (validateField(etMobileNumber!!)) {
                nextField = "mobileNumber"
                currentLayout = mobileNumberLayout
                nextLayout = farmerOwnerLayout
            }
        } else if (id == R.id.btnNextFarmerOwner) {
            if (validateField(etFarmerOwner!!)) {
                nextField = "farmerOwner"
                currentLayout = farmerOwnerLayout
                nextLayout = paymentTypeLayout
            }
        } else if (id == R.id.btnNextPaymentType) {
            // Ensure the payment type is selected
            val selectedId = radioGroupPaymentType!!.getCheckedRadioButtonId()
            if (selectedId != -1) {
                val paymentType = if (selectedId == R.id.rbWeekly) "Weekly" else "Monthly"
                farmerData!!.put("paymentType", paymentType) // Save the selected payment type
                currentLayout = paymentTypeLayout
                nextLayout = paymentPerDayLayout
                navigateToNextLayout(currentLayout!!, nextLayout!!) // Now move to next layout
                return  // Prevent further execution in this method
            } else {
                // Handle case where no payment type is selected
                showMessage("Please select a payment type.")
                return
            }
        } else if (id == R.id.btnNextPaymentPerDay) {
            if (validateField(etPaymentPerDay!!)) {
                nextField = "paymentPerDay"
                currentLayout = paymentPerDayLayout
                nextLayout = paymentPerKgLayout
            }
        } else if (id == R.id.btnNextPaymentPerKg) {
            if (validateField(etPaymentPerKg!!)) {
                nextField = "paymentPerKg"
                currentLayout = paymentPerKgLayout
                nextLayout = dayOfPaymentLayout
            }
        }

        if (nextField != null) {
            farmerData!!.put(nextField, getInputText(view))
            navigateToNextLayout(currentLayout!!, nextLayout!!)
        }
    }

    private fun navigateToNextLayout(currentLayout: LinearLayout, nextLayout: LinearLayout) {
        currentLayout.setVisibility(View.GONE)
        nextLayout.setVisibility(View.VISIBLE)
    }

    private fun getInputText(view: View): String? {
        var editText: TextInputEditText? = null

        if (view.getId() == R.id.btnNextFarmerName) {
            editText = etFarmerName
        } else if (view.getId() == R.id.btnNextDob) {
            editText = etDob
        } else if (view.getId() == R.id.btnNextMobileNumber) {
            editText = etMobileNumber
        } else if (view.getId() == R.id.btnNextFarmerOwner) {
            editText = etFarmerOwner
        } else if (view.getId() == R.id.btnNextPaymentPerDay) {
            editText = etPaymentPerDay
        } else if (view.getId() == R.id.btnNextPaymentPerKg) {
            editText = etPaymentPerKg
        }

        return if (editText != null) editText.getText().toString().trim { it <= ' ' } else null
    }

    // Method to save data locally in SharedPreferences and skip Firebase upload
    fun submitFarmerInfo(view: View?) {
        if (validateField(etDayOfPayment!!)) {
            val dayOfPayment = etDayOfPayment!!.getText().toString().trim { it <= ' ' }
            farmerData!!.put("dayOfPayment", dayOfPayment)

            // Save the complete farmer data locally in SharedPreferences
            saveFarmerDataLocally()

            // Show message to indicate success
            showMessage("Farmer data saved locally.")

            // Optionally navigate back to the main screen or another activity
            navigateToMainScreen()
        }
    }

    // Helper method to show a message
    private fun showMessage(message: String) {
        Snackbar.make(findViewById<View?>(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Validate input fields
    private fun validateField(editText: TextInputEditText): Boolean {
        if (editText.getText().toString().trim { it <= ' ' }.isEmpty()) {
            editText.setError("This field is required")
            return false
        }
        return true
    }

    // Save farmer data locally in SharedPreferences
    private fun saveFarmerDataLocally() {
        val editor = sharedPreferences!!.edit()
        for (entry in farmerData!!.entries) {
            editor.putString(entry.key, entry.value)
        }
        editor.apply() // Save changes
    }

    // Navigate to the main screen after successful submission
    private fun navigateToMainScreen() {
        // This can be modified to navigate to the appropriate screen in your app
        // Example:
        startActivity(Intent(this@FarmerDetails, MainActivity::class.java))
        finish()
    }
}
