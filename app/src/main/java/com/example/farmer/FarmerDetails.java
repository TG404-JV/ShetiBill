package com.example.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FarmerDetails extends AppCompatActivity {

    private LinearLayout farmerNameLayout, dobLayout, mobileNumberLayout, farmerOwnerLayout, paymentTypeLayout, paymentPerDayLayout, paymentPerKgLayout, dayOfPaymentLayout;
    private TextInputEditText etFarmerName, etDob, etMobileNumber, etFarmerOwner, etPaymentPerDay, etPaymentPerKg, etDayOfPayment;
    private RadioGroup radioGroupPaymentType;
    private RadioButton rbWeekly, rbMonthly;
    private DatabaseReference databaseReference;
    private Map<String, String> farmerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_details);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Farmers");

        // Initialize layouts
        farmerNameLayout = findViewById(R.id.farmerNameLayout);
        dobLayout = findViewById(R.id.dobLayout);
        mobileNumberLayout = findViewById(R.id.mobileNumberLayout);
        farmerOwnerLayout = findViewById(R.id.farmerOwnerLayout);
        paymentTypeLayout = findViewById(R.id.paymentTypeLayout);
        paymentPerDayLayout = findViewById(R.id.paymentPerDayLayout);
        paymentPerKgLayout = findViewById(R.id.paymentPerKgLayout);
        dayOfPaymentLayout = findViewById(R.id.dayOfPaymentLayout);

        // Initialize EditTexts
        etFarmerName = findViewById(R.id.etFarmerName);
        etDob = findViewById(R.id.etDob);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etFarmerOwner = findViewById(R.id.etFarmerOwner);
        etPaymentPerDay = findViewById(R.id.etPaymentPerDay);
        etPaymentPerKg = findViewById(R.id.etPaymentPerKg);
        etDayOfPayment = findViewById(R.id.etDayOfPayment);

        // Radio Group
        radioGroupPaymentType = findViewById(R.id.radioGroupPaymentType);
        rbWeekly = findViewById(R.id.rbWeekly);
        rbMonthly = findViewById(R.id.rbMonthly);

        // Initialize farmerData HashMap
        farmerData = new HashMap<>();
    }

    // Method to show the next screen
    public void showNextScreen(View view) {
        int id = view.getId();

        if (id == R.id.btnNextFarmerName) {
            if (validateField(etFarmerName)) {
                farmerData.put("farmerName", etFarmerName.getText().toString().trim());
                farmerNameLayout.setVisibility(View.GONE);
                dobLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btnNextDob) {
            if (validateField(etDob)) {
                farmerData.put("dob", etDob.getText().toString().trim());
                dobLayout.setVisibility(View.GONE);
                mobileNumberLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btnNextMobileNumber) {
            if (validateField(etMobileNumber)) {
                farmerData.put("mobileNumber", etMobileNumber.getText().toString().trim());
                mobileNumberLayout.setVisibility(View.GONE);
                farmerOwnerLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btnNextFarmerOwner) {
            if (validateField(etFarmerOwner)) {
                farmerData.put("farmerOwner", etFarmerOwner.getText().toString().trim());
                farmerOwnerLayout.setVisibility(View.GONE);
                paymentTypeLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btnNextPaymentType) {
            String paymentType = rbWeekly.isChecked() ? "Weekly" : "Monthly";
            farmerData.put("paymentType", paymentType);
            paymentTypeLayout.setVisibility(View.GONE);
            paymentPerDayLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.btnNextPaymentPerDay) {
            if (validateField(etPaymentPerDay)) {
                farmerData.put("paymentPerDay", etPaymentPerDay.getText().toString().trim());
                paymentPerDayLayout.setVisibility(View.GONE);
                paymentPerKgLayout.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btnNextPaymentPerKg) {
            if (validateField(etPaymentPerKg)) {
                farmerData.put("paymentPerKg", etPaymentPerKg.getText().toString().trim());
                paymentPerKgLayout.setVisibility(View.GONE);
                dayOfPaymentLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    // Method to submit data to Firebase
    public void submitFarmerInfo(View view) {
        if (validateField(etDayOfPayment)) {
            String dayOfPayment = etDayOfPayment.getText().toString().trim();
            farmerData.put("dayOfPayment", dayOfPayment);

            // Push the farmer data to Firebase Realtime Database under a unique ID
            String farmerId = databaseReference.push().getKey(); // Generate a unique key
            if (farmerId != null) {
                databaseReference.child(farmerId).setValue(farmerData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Data uploaded successfully
                        showMessage("Farmer data uploaded successfully.");
                        startActivity(new Intent(FarmerDetails.this, MainActivity.class));
                        finish(); // Optional: Close this activity to prevent going back
                    } else {
                        // Handle failure
                        showMessage("Error uploading farmer data: " + task.getException().getMessage());
                    }
                });
            } else {
                showMessage("Error generating farmer ID.");
            }
        }
    }

    // Helper method to show a message (you can use Toast or Snackbar)
    private void showMessage(String message) {
        Toast.makeText(FarmerDetails.this, message, Toast.LENGTH_SHORT).show();
    }

    // Validate input fields
    private boolean validateField(TextInputEditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError("This field is required");
            return false;
        }
        return true;
    }
}
