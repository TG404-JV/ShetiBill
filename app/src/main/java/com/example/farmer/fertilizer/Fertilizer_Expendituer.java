package com.example.farmer.fertilizer;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fertilizer_Expendituer extends Fragment {

    private TextInputEditText etItemName, etPurchaseDate, etPrice;
    private RadioGroup rgPaymentMode;
    private RadioButton rbCash, rbCard, rbOnline;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fertilizer__expendituer, container, false);

        // Initialize views
        etItemName = view.findViewById(R.id.etItemName);
        etPurchaseDate = view.findViewById(R.id.etPurchaseDate);
        rgPaymentMode = view.findViewById(R.id.rgPaymentMode);
        rbCash = view.findViewById(R.id.rbCash);
        rbCard = view.findViewById(R.id.rbCard);
        rbOnline = view.findViewById(R.id.rbOnline);
        btnSave = view.findViewById(R.id.btnSave);
        etPrice = view.findViewById(R.id.etFertilizerPrice);

        // Load saved data if any
        loadSavedData();

        // Set OnClickListener to show DatePicker
        etPurchaseDate.setOnClickListener(v -> showDatePicker());

        // Save data when the save button is clicked
        btnSave.setOnClickListener(v -> saveData());

        return view;
    }

    // Method to show DatePicker
    private void showDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date to the TextInputEditText
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etPurchaseDate.setText(formattedDate);
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Method to save data into SharedPreferences
    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the user input
        String itemName = etItemName.getText().toString();
        String purchaseDate = etPurchaseDate.getText().toString();
        String price = etPrice.getText().toString();
        int selectedPaymentModeId = rgPaymentMode.getCheckedRadioButtonId();
        String paymentMode = "";

        if (selectedPaymentModeId == R.id.rbCash) {
            paymentMode = "Cash";
        } else if (selectedPaymentModeId == R.id.rbCard) {
            paymentMode = "Card";
        } else if (selectedPaymentModeId == R.id.rbOnline) {
            paymentMode = "Online";
        }

        // Create a JSON object for the current entry
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ItemName", itemName);
            jsonObject.put("PurchaseDate", purchaseDate);
            jsonObject.put("PurchaseAmount", price);
            jsonObject.put("PaymentMode", paymentMode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Retrieve existing data and add the new entry
        String existingData = sharedPreferences.getString("expenditure_list", "[]");
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(existingData);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
        }

        // Save the updated data
        editor.putString("expenditure_list", jsonArray.toString());
        editor.apply();

        // Show toast message after saving
        Toast.makeText(getActivity(), "Data saved successfully!", Toast.LENGTH_SHORT).show();
    }

    // Method to load saved data
    private List<FertilizerExpenditure> loadSavedData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        List<FertilizerExpenditure> expenditureList = new ArrayList<>();

        // Retrieve the JSON array of expenditures from SharedPreferences
        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");
        try {
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                // Convert each JSON object to a FertilizerExpenditure object
                FertilizerExpenditure expenditure = FertilizerExpenditure.fromJson(jsonArray.getJSONObject(i));
                expenditureList.add(expenditure);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expenditureList;
    }
}

