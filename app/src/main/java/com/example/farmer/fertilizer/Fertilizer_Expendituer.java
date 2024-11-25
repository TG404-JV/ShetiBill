package com.example.farmer.fertilizer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
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
    private MaterialRadioButton rbCash, rbCard, rbOnline;
    private MaterialButton btnSave, btnUploadReceipt;
    private ImageView ivReceiptPreview;


    private String receiptFilePath = ""; // To store the URI of the receipt
    private static final String PREF_NAME = "FertilizerPrefs";

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
        btnUploadReceipt = view.findViewById(R.id.btnUploadReceipt);
        etPrice = view.findViewById(R.id.etFertilizerPrice);
        ivReceiptPreview = view.findViewById(R.id.ivReceiptPreview); // Initialize the ImageView

        // Set listeners
        etPurchaseDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveData());
        btnUploadReceipt.setOnClickListener(v -> uploadReceipt());




        return view;
    }

    // Show DatePicker dialog
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etPurchaseDate.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Launch file picker for receipt upload
    private void uploadReceipt() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Receipt"), 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                receiptFilePath = selectedImageUri.toString();
                ivReceiptPreview.setImageURI(selectedImageUri); // Set the image in ImageView
                Toast.makeText(getActivity(), "Receipt uploaded successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save all data into SharedPreferences
    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get user input
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
            jsonObject.put("ReceiptPath", receiptFilePath); // Save receipt URI
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

        // Notify the user
        Toast.makeText(getActivity(), "Expenditure saved successfully!", Toast.LENGTH_SHORT).show();

        // Clear fields
        clearFields();
    }

    // Clear all input fields
    private void clearFields() {
        etItemName.setText("");
        etPurchaseDate.setText("");
        etPrice.setText("");
        rgPaymentMode.clearCheck();
        receiptFilePath = "";
        ivReceiptPreview.setImageResource(0); // Clear ImageView
    }

    // Load saved data into a list for RecyclerView
    public static List<FertilizerExpenditure> loadSavedData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<FertilizerExpenditure> expenditureList = new ArrayList<>();

        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");
        try {
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FertilizerExpenditure expenditure = FertilizerExpenditure.fromJson(jsonObject);
                expenditureList.add(expenditure);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expenditureList;
    }
}
