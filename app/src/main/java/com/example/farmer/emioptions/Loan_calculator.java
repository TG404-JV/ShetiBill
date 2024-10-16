package com.example.farmer.emioptions;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.DecimalFormat;

public class Loan_calculator extends Fragment {

    private Spinner loanTypeSpinner;
    private EditText loanAmountInput, loanTenureInput, interestRateInput;
    private Button calculateEmiButton;
    private TextView emiResultText, amortizationDetails;
    private CircularProgressIndicator circularProgress;

    public Loan_calculator() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        loanTypeSpinner = view.findViewById(R.id.loanTypeSpinner);
        loanAmountInput = view.findViewById(R.id.loanAmountInput);
        loanTenureInput = view.findViewById(R.id.loanTenureInput);
        interestRateInput = view.findViewById(R.id.interestRateInput);
        calculateEmiButton = view.findViewById(R.id.calculateEmiButton);
        emiResultText = view.findViewById(R.id.emiResultText);
        amortizationDetails = view.findViewById(R.id.amortizationTitle);
        circularProgress = view.findViewById(R.id.circularProgress);

        // Handle Calculate EMI button click
        calculateEmiButton.setOnClickListener(v -> calculateEMI());
    }

    private void calculateEMI() {
        // Get user inputs
        String loanAmountStr = loanAmountInput.getText().toString().trim();
        String loanTenureStr = loanTenureInput.getText().toString().trim();
        String interestRateStr = interestRateInput.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(loanAmountStr) || TextUtils.isEmpty(loanTenureStr) || TextUtils.isEmpty(interestRateStr)) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double loanAmount = Double.parseDouble(loanAmountStr);
        int loanTenure = Integer.parseInt(loanTenureStr);
        double interestRate = Double.parseDouble(interestRateStr);

        // Calculate EMI using formula
        double monthlyInterestRate = interestRate / 12 / 100;
        int loanTenureMonths = loanTenure * 12;
        double emi = (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTenureMonths)) /
                (Math.pow(1 + monthlyInterestRate, loanTenureMonths) - 1);

        // Format EMI result
        DecimalFormat formatter = new DecimalFormat("#,###");
        String emiResultFormatted = formatter.format(emi);

        // Update EMI result in the UI
        emiResultText.setText("₹" + emiResultFormatted);
        emiResultText.setVisibility(View.VISIBLE); // Make the EMI result text visible

        // Update progress indicator (this is just a mock-up, you can adjust it based on your needs)
        circularProgress.setProgress(75, true); // Set progress with animation

        // Update amortization details
        updateAmortizationDetails(emi, loanTenureMonths);
    }

    private void updateAmortizationDetails(double emi, int loanTenureMonths) {
        // This is a mock-up, you can create a more complex logic for amortization schedules
        amortizationDetails.setText("Payments starting from " + getCurrentYear() + "\nEMI per month: ₹" + new DecimalFormat("#,###").format(emi) +
                "\nTotal tenure: " + loanTenureMonths + " months");
    }

    private String getCurrentYear() {
        return String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
    }
}

