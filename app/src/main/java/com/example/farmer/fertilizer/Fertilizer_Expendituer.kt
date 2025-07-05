package com.example.farmer.fertilizer

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.farmer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class Fertilizer_Expendituer : Fragment() {
    private var etItemName: TextInputEditText? = null
    private var etPurchaseDate: TextInputEditText? = null
    private var etPrice: TextInputEditText? = null
    private var rgPaymentMode: RadioGroup? = null
    private var rbCash: MaterialRadioButton? = null
    private var rbCard: MaterialRadioButton? = null
    private var rbOnline: MaterialRadioButton? = null
    private var btnSave: MaterialButton? = null
    private var btnUploadReceipt: MaterialButton? = null
    private var ivReceiptPreview: ImageView? = null


    private var receiptFilePath = "" // To store the URI of the receipt
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fertilizer__expendituer, container, false)

        // Initialize views
        etItemName = view.findViewById<TextInputEditText>(R.id.etItemName)
        etPurchaseDate = view.findViewById<TextInputEditText>(R.id.etPurchaseDate)
        rgPaymentMode = view.findViewById<RadioGroup>(R.id.rgPaymentMode)
        rbCash = view.findViewById<MaterialRadioButton?>(R.id.rbCash)
        rbCard = view.findViewById<MaterialRadioButton?>(R.id.rbCard)
        rbOnline = view.findViewById<MaterialRadioButton?>(R.id.rbOnline)
        btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        btnUploadReceipt = view.findViewById<MaterialButton>(R.id.btnUploadReceipt)
        etPrice = view.findViewById<TextInputEditText>(R.id.etFertilizerPrice)
        ivReceiptPreview =
            view.findViewById<ImageView>(R.id.ivReceiptPreview) // Initialize the ImageView

        // Set listeners
        etPurchaseDate!!.setOnClickListener(View.OnClickListener { v: View? -> showDatePicker() })
        btnSave!!.setOnClickListener(View.OnClickListener { v: View? -> saveData() })
        btnUploadReceipt!!.setOnClickListener(View.OnClickListener { v: View? -> uploadReceipt() })




        return view
    }

    // Show DatePicker dialog
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            OnDateSetListener { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedDate =
                    selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear
                etPurchaseDate!!.setText(formattedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    // Launch file picker for receipt upload
    private fun uploadReceipt() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, "Select Receipt"), 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.getData()
            if (selectedImageUri != null) {
                receiptFilePath = selectedImageUri.toString()
                ivReceiptPreview!!.setImageURI(selectedImageUri) // Set the image in ImageView
                Toast.makeText(getActivity(), "Receipt uploaded successfully!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Save all data into SharedPreferences
    private fun saveData() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get user input
        val itemName = etItemName!!.getText().toString()
        val purchaseDate = etPurchaseDate!!.getText().toString()
        val price = etPrice!!.getText().toString()

        val selectedPaymentModeId = rgPaymentMode!!.getCheckedRadioButtonId()
        var paymentMode = ""
        if (selectedPaymentModeId == R.id.rbCash) {
            paymentMode = "Cash"
        } else if (selectedPaymentModeId == R.id.rbCard) {
            paymentMode = "Card"
        } else if (selectedPaymentModeId == R.id.rbOnline) {
            paymentMode = "Online"
        }

        // Create a JSON object for the current entry
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ItemName", itemName)
            jsonObject.put("PurchaseDate", purchaseDate)
            jsonObject.put("PurchaseAmount", price)
            jsonObject.put("PaymentMode", paymentMode)
            jsonObject.put("ReceiptPath", receiptFilePath) // Save receipt URI
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Retrieve existing data and add the new entry
        val existingData: String = sharedPreferences.getString("expenditure_list", "[]")!!
        var jsonArray: JSONArray?
        try {
            jsonArray = JSONArray(existingData)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
            jsonArray = JSONArray()
            jsonArray.put(jsonObject)
        }

        // Save the updated data
        editor.putString("expenditure_list", jsonArray.toString())
        editor.apply()

        // Notify the user
        Toast.makeText(getActivity(), "Expenditure saved successfully!", Toast.LENGTH_SHORT).show()

        // Clear fields
        clearFields()
    }

    // Clear all input fields
    private fun clearFields() {
        etItemName!!.setText("")
        etPurchaseDate!!.setText("")
        etPrice!!.setText("")
        rgPaymentMode!!.clearCheck()
        receiptFilePath = ""
        ivReceiptPreview!!.setImageResource(0) // Clear ImageView
    }

    companion object {
        private const val PREF_NAME = "FertilizerPrefs"

        // Load saved data into a list for RecyclerView
        fun loadSavedData(context: Context): MutableList<FertilizerExpenditure?> {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val expenditureList: MutableList<FertilizerExpenditure?> =
                ArrayList<FertilizerExpenditure?>()

            val expenditureData: String = sharedPreferences.getString("expenditure_list", "[]")!!
            try {
                val jsonArray = JSONArray(expenditureData)
                for (i in 0..<jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val expenditure = FertilizerExpenditure.fromJson(jsonObject)
                    expenditureList.add(expenditure)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return expenditureList
        }
    }
}
