package com.example.farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class FarmerDetails extends AppCompatActivity {

    private LinearLayout farmerNameLayout, dobLayout, mobileNumberLayout, farmerOwnerLayout, paymentTypeLayout, paymentPerDayLayout, paymentPerKgLayout, dayOfPaymentLayout;
    private TextInputEditText etFarmerName, etDob, etMobileNumber, etFarmerOwner, etPaymentPerDay, etPaymentPerKg, etDayOfPayment;
    private RadioGroup radioGroupPaymentType;
    private RadioButton rbWeekly, rbMonthly;
    private Map<String, String> farmerData;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_details);

        // Hide the status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hides the navigation bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Hides the status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keeps it hidden until user swipes
        );

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FarmerDetails", Context.MODE_PRIVATE);

        // Initialize layouts and fields
        initializeViews();

        // Initialize farmerData HashMap
        farmerData = new HashMap<>();
    }

    private void initializeViews() {
        farmerNameLayout = findViewById(R.id.farmerNameLayout);
        dobLayout = findViewById(R.id.dobLayout);
        mobileNumberLayout = findViewById(R.id.mobileNumberLayout);
        farmerOwnerLayout = findViewById(R.id.farmerOwnerLayout);
        paymentTypeLayout = findViewById(R.id.paymentTypeLayout);
        paymentPerDayLayout = findViewById(R.id.paymentPerDayLayout);
        paymentPerKgLayout = findViewById(R.id.paymentPerKgLayout);
        dayOfPaymentLayout = findViewById(R.id.dayOfPaymentLayout);

        etFarmerName = findViewById(R.id.etFarmerName);
        etDob = findViewById(R.id.etDob);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etFarmerOwner = findViewById(R.id.etFarmerOwner);
        etPaymentPerDay = findViewById(R.id.etPaymentPerDay);
        etPaymentPerKg = findViewById(R.id.etPaymentPerKg);
        etDayOfPayment = findViewById(R.id.etDayOfPayment);

        radioGroupPaymentType = findViewById(R.id.radioGroupPaymentType);
        rbWeekly = findViewById(R.id.rbWeekly);
        rbMonthly = findViewById(R.id.rbMonthly);
    }

    // Method to show the next screen with validation
    public void showNextScreen(View view) {
        int id = view.getId();
        String nextField = null;
        LinearLayout currentLayout = null, nextLayout = null;

        if (id == R.id.btnNextFarmerName) {
            if (validateField(etFarmerName)) {
                nextField = "farmerName";
                currentLayout = farmerNameLayout;
                nextLayout = dobLayout;
            }
        } else if (id == R.id.btnNextDob) {
            if (validateField(etDob)) {
                nextField = "dob";
                currentLayout = dobLayout;
                nextLayout = mobileNumberLayout;
            }
        } else if (id == R.id.btnNextMobileNumber) {
            if (validateField(etMobileNumber)) {
                nextField = "mobileNumber";
                currentLayout = mobileNumberLayout;
                nextLayout = farmerOwnerLayout;
            }
        } else if (id == R.id.btnNextFarmerOwner) {
            if (validateField(etFarmerOwner)) {
                nextField = "farmerOwner";
                currentLayout = farmerOwnerLayout;
                nextLayout = paymentTypeLayout;
            }
        } else if (id == R.id.btnNextPaymentType) {
            // Ensure the payment type is selected
            int selectedId = radioGroupPaymentType.getCheckedRadioButtonId();
            if (selectedId != -1) {
                String paymentType = (selectedId == R.id.rbWeekly) ? "Weekly" : "Monthly";
                farmerData.put("paymentType", paymentType); // Save the selected payment type
                currentLayout = paymentTypeLayout;
                nextLayout = paymentPerDayLayout;
                navigateToNextLayout(currentLayout, nextLayout);  // Now move to next layout
                return; // Prevent further execution in this method
            } else {
                // Handle case where no payment type is selected
                showMessage("Please select a payment type.");
                return;
            }
        } else if (id == R.id.btnNextPaymentPerDay) {
            if (validateField(etPaymentPerDay)) {
                nextField = "paymentPerDay";
                currentLayout = paymentPerDayLayout;
                nextLayout = paymentPerKgLayout;
            }
        } else if (id == R.id.btnNextPaymentPerKg) {
            if (validateField(etPaymentPerKg)) {
                nextField = "paymentPerKg";
                currentLayout = paymentPerKgLayout;
                nextLayout = dayOfPaymentLayout;
            }
        }

        if (nextField != null) {
            farmerData.put(nextField, getInputText(view));
            navigateToNextLayout(currentLayout, nextLayout);
        }
    }

    private void navigateToNextLayout(LinearLayout currentLayout, LinearLayout nextLayout) {
        currentLayout.setVisibility(View.GONE);
        nextLayout.setVisibility(View.VISIBLE);
    }

    private String getInputText(View view) {
        TextInputEditText editText = null;

        if (view.getId() == R.id.btnNextFarmerName) {
            editText = etFarmerName;
        } else if (view.getId() == R.id.btnNextDob) {
            editText = etDob;
        } else if (view.getId() == R.id.btnNextMobileNumber) {
            editText = etMobileNumber;
        } else if (view.getId() == R.id.btnNextFarmerOwner) {
            editText = etFarmerOwner;
        } else if (view.getId() == R.id.btnNextPaymentPerDay) {
            editText = etPaymentPerDay;
        } else if (view.getId() == R.id.btnNextPaymentPerKg) {
            editText = etPaymentPerKg;
        }

        return editText != null ? editText.getText().toString().trim() : null;
    }

    // Method to save data locally in SharedPreferences and skip Firebase upload
    public void submitFarmerInfo(View view) {
        if (validateField(etDayOfPayment)) {
            String dayOfPayment = etDayOfPayment.getText().toString().trim();
            farmerData.put("dayOfPayment", dayOfPayment);

            // Save the complete farmer data locally in SharedPreferences
            saveFarmerDataLocally();

            // Show message to indicate success
            showMessage("Farmer data saved locally.");

            // Optionally navigate back to the main screen or another activity
            navigateToMainScreen();
        }
    }

    // Helper method to show a message
    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    // Validate input fields
    private boolean validateField(TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("This field is required");
            return false;
        }
        return true;
    }

    // Save farmer data locally in SharedPreferences
    private void saveFarmerDataLocally() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, String> entry : farmerData.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply(); // Save changes
    }

    // Navigate to the main screen after successful submission
    private void navigateToMainScreen() {
        // This can be modified to navigate to the appropriate screen in your app
        // Example:
         startActivity(new Intent(FarmerDetails.this, MainActivity.class));
         finish();
    }
}
