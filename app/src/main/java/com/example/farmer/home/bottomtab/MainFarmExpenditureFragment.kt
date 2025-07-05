package com.example.farmer.home.bottomtab

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.home.bottomtab.labour.Labour
import com.example.farmer.home.bottomtab.labour.LabourAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainFarmExpenditureFragment : Fragment() {

    private lateinit var addLabourFab: FloatingActionButton
    private lateinit var labourRecyclerView: RecyclerView
    private lateinit var labourAdapter: LabourAdapter
    private var labourList: MutableList<Labour> = mutableListOf()
    private var filteredLabourList: MutableList<Labour> = mutableListOf()
    private lateinit var searchBar: EditText
    private lateinit var sortSpinner: Spinner
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        private const val PREFERENCE_KEY = "LabourData"
        private const val LABOUR_LIST_KEY = "LabourList"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_farm_expenditure, container, false)

        // Initialize views
        addLabourFab = view.findViewById(R.id.addLabourBtn)
        labourRecyclerView = view.findViewById(R.id.AddWeightRecyclerView)
        searchBar = view.findViewById(R.id.searchBar)
        sortSpinner = view.findViewById(R.id.sortSpinner)

        // Load the labour list from SharedPreferences
        labourList = loadLabourData().toMutableList()
        filteredLabourList = labourList.toMutableList()

        // Initialize adapter with the filtered labour list
        labourAdapter = LabourAdapter(filteredLabourList, requireContext(),
            labourList
        ) {}
        labourRecyclerView.layoutManager = LinearLayoutManager(context)
        labourRecyclerView.adapter = labourAdapter

        // Set up search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterLabour(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up sort functionality
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sortLabourList(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Set FAB click listener to open dialog for adding labour
        addLabourFab.setOnClickListener { showAddLabourDialog() }

        // Add item decoration to reduce spacing between items
        labourRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 4 // Adjust this value to your desired spacing
            }
        })

        return view
    }

    // Filter labour list based on search query
    private fun filterLabour(query: String) {
        filteredLabourList.clear()
        if (query.isEmpty()) {
            filteredLabourList.addAll(labourList)
        } else {
            filteredLabourList.addAll(labourList.filter {
                it.name!!.contains(query, ignoreCase = true)
            })
        }
        labourAdapter.notifyDataSetChanged()
    }

    // Sort labour list based on selected sorting option
    private fun sortLabourList(sortOption: Int) {
        filteredLabourList.clear()
        val currentDate = Calendar.getInstance().time

        for (labour in labourList) {
            try {
                val labourDate = dateFormat.parse(labour.date)
                if (labourDate != null) {
                    when (sortOption) {
                        0 -> // All
                            filteredLabourList.add(labour)
                        1 -> // Daily
                            if (isSameDay(labourDate, currentDate)) {
                                filteredLabourList.add(labour)
                            }
                        2 -> // Weekly
                            if (isWithinLastDays(labourDate, currentDate, 7)) {
                                filteredLabourList.add(labour)
                            }
                        3 -> // Monthly
                            if (isWithinLastDays(labourDate, currentDate, 30)) {
                                filteredLabourList.add(labour)
                            }
                        4 -> // Quarterly
                            if (isWithinLastDays(labourDate, currentDate, 90)) {
                                filteredLabourList.add(labour)
                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        labourAdapter.notifyDataSetChanged()
    }

    // Helper methods to check date conditions
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isWithinLastDays(date: Date, currentDate: Date, days: Int): Boolean {
        val calendar = Calendar.getInstance().apply {
            time = currentDate
            add(Calendar.DAY_OF_YEAR, -days)
        }
        val dateLimit = calendar.time
        return date.after(dateLimit) && date.before(currentDate)
    }

    // Method to show dialog to add labour name and date using custom buttons
    private fun showAddLabourDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_labour, null)

        val labourNameEditText: TextInputEditText = dialogView.findViewById(R.id.labourNameEditText)
        val labourDateTextView: TextInputEditText = dialogView.findViewById(R.id.workDateEditText)
        val addButton: Button = dialogView.findViewById(R.id.saveButton)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        val cropNameEditText: TextInputEditText = dialogView.findViewById(R.id.cropNameEditText)
        val workingTypeRadioGroup: RadioGroup = dialogView.findViewById(R.id.workingTypeRadioGroup)

        labourDateTextView.setOnClickListener { showDatePickerDialog(labourDateTextView) }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        addButton.setOnClickListener {
            val labourName = labourNameEditText.text.toString()
            val labourDate = labourDateTextView.text.toString()
            val cropName = cropNameEditText.text.toString()

            // Get the selected RadioButton
            val selectedId = workingTypeRadioGroup.checkedRadioButtonId

            if (selectedId != -1) { // Ensure a RadioButton is selected
                val selectedBtn: RadioButton = dialogView.findViewById(selectedId)
                val workingType = selectedBtn.text.toString()

                if (labourName.isNotEmpty() && labourDate.isNotEmpty()) {
                    val newLabour = Labour(labourName, labourDate, cropName, workingType)
                    labourList.add(newLabour)
                    saveLabourData()
                    filterLabour(searchBar.text.toString())
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Please enter name and date", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please select a working type", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    // Date picker dialog for selecting the date
    private fun showDatePickerDialog(dateTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dateTextView.text = selectedDate
        }, year, month, day).show()
    }

    // Load labour data from SharedPreferences
    private fun loadLabourData(): List<Labour> {
        val sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(LABOUR_LIST_KEY, null)
        val type = object : TypeToken<List<Labour>>() {}.type
        return json?.let { Gson().fromJson(it, type) } ?: emptyList()
    }

    // Save labour data to SharedPreferences
    private fun saveLabourData() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(labourList)
        editor.putString(LABOUR_LIST_KEY, json)
        editor.apply()
    }
}