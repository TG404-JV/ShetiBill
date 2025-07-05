package com.example.farmer.emioptions

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.farmer.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.pow


class Loan_calculator : Fragment() {
    private var loanTypeSpinner: Spinner? = null
    private var loanAmountInput: EditText? = null
    private var loanTenureInput: EditText? = null
    private var interestRateInput: EditText? = null
    private var calculateEmiButton: Button? = null
    private var emiResultText: TextView? = null
    private var amortizationDetails: TextView? = null
    private var circularProgress: CircularProgressIndicator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        loanTypeSpinner = view.findViewById<Spinner?>(R.id.loanTypeSpinner)
        loanAmountInput = view.findViewById<EditText>(R.id.loanAmountInput)
        loanTenureInput = view.findViewById<EditText>(R.id.loanTenureInput)
        interestRateInput = view.findViewById<EditText>(R.id.interestRateInput)
        calculateEmiButton = view.findViewById<Button>(R.id.calculateEmiButton)
        emiResultText = view.findViewById<TextView>(R.id.emiResultText)
        amortizationDetails = view.findViewById<TextView>(R.id.amortizationTitle)
        circularProgress = view.findViewById<CircularProgressIndicator>(R.id.circularProgress)

        // Handle Calculate EMI button click
        calculateEmiButton!!.setOnClickListener(View.OnClickListener { v: View? -> calculateEMI() })
    }

    private fun calculateEMI() {
        // Get user inputs
        val loanAmountStr = loanAmountInput!!.getText().toString().trim { it <= ' ' }
        val loanTenureStr = loanTenureInput!!.getText().toString().trim { it <= ' ' }
        val interestRateStr = interestRateInput!!.getText().toString().trim { it <= ' ' }

        // Validate input fields
        if (TextUtils.isEmpty(loanAmountStr) || TextUtils.isEmpty(loanTenureStr) || TextUtils.isEmpty(
                interestRateStr
            )
        ) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val loanAmount = loanAmountStr.toDouble()
        val loanTenure = loanTenureStr.toInt()
        val interestRate = interestRateStr.toDouble()

        // Calculate EMI using formula
        val monthlyInterestRate = interestRate / 12 / 100
        val loanTenureMonths = loanTenure * 12
        val emi =
            (loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(loanTenureMonths.toDouble())) /
                    ((1 + monthlyInterestRate).pow(loanTenureMonths.toDouble()) - 1)

        // Format EMI result
        val formatter = DecimalFormat("#,###")
        val emiResultFormatted = formatter.format(emi)

        // Update EMI result in the UI
        emiResultText!!.setText("₹" + emiResultFormatted)
        emiResultText!!.setVisibility(View.VISIBLE) // Make the EMI result text visible

        // Update progress indicator (this is just a mock-up, you can adjust it based on your needs)
        circularProgress!!.setProgress(75, true) // Set progress with animation

        // Update amortization details
        updateAmortizationDetails(emi, loanTenureMonths)
    }

    private fun updateAmortizationDetails(emi: Double, loanTenureMonths: Int) {
        // This is a mock-up, you can create a more complex logic for amortization schedules
        amortizationDetails!!.setText(
            "Payments starting from " + this.currentYear + "\nEMI per month: ₹" + DecimalFormat("#,###").format(
                emi
            ) +
                    "\nTotal tenure: " + loanTenureMonths + " months"
        )
    }

    private val currentYear: String
        get() = Calendar.getInstance().get(Calendar.YEAR).toString()
}

