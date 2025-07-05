package com.example.farmer.graph

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.farmer.R
import com.example.farmer.fertilizer.FertilizerExpenditure
import com.example.farmer.fertilizer.FertilizerExpenditure.Companion.fromJson
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.json.JSONArray
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragmentExpendGraph : Fragment() {
    private var fertilizerPieChart: PieChart? = null
    private var emptyFertilizerStateTextView: TextView? = null
    private var timeIntervalGroup: RadioGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expend_graph, container, false)

        fertilizerPieChart = view.findViewById<PieChart>(R.id.fertilizerPieChart)
        emptyFertilizerStateTextView =
            view.findViewById<TextView>(R.id.emptyFertilizerStateTextView)
        timeIntervalGroup = view.findViewById<RadioGroup>(R.id.timeIntervalGroup)

        timeIntervalGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group: RadioGroup?, checkedId: Int -> updateGraphData() })

        // Initially load the data with a default time interval (e.g., Monthly)
        updateGraphData()

        return view
    }

    private fun updateGraphData() {
        // Fetch the expenditure data
        val expenditureList = loadSavedData()

        // Filter and group the data based on the selected time interval
        val timeInterval = this.selectedTimeInterval
        val pieEntries = generatePieEntries(expenditureList, timeInterval)

        // Display the pie chart or show "No Data" message if no data
        if (pieEntries.isEmpty()) {
            fertilizerPieChart!!.setVisibility(View.GONE)
            emptyFertilizerStateTextView!!.setVisibility(View.VISIBLE)
            emptyFertilizerStateTextView!!.setText("No fertilizer expenditure data for this period.")
        } else {
            fertilizerPieChart!!.setVisibility(View.VISIBLE)
            emptyFertilizerStateTextView!!.setVisibility(View.GONE)

            // Create the PieDataSet and PieData for the chart
            val dataSet = PieDataSet(pieEntries, "Fertilizer Expenditure")
            dataSet.setColors(*this.chartColors)
            dataSet.setValueTextSize(16f)

            val pieData = PieData(dataSet)
            fertilizerPieChart!!.setData(pieData)
            fertilizerPieChart!!.invalidate() // Refresh the chart

            // Set chart properties (legend, description, etc.)
            val legend = fertilizerPieChart!!.getLegend()
            legend.setTextColor(Color.BLACK)
            legend.setTextSize(14f)
        }
    }

    private fun loadSavedData(): MutableList<FertilizerExpenditure> {
        val sharedPreferences =
            requireActivity().getSharedPreferences(EXPENDITURE_PREFS, Context.MODE_PRIVATE)
        val expenditureList: MutableList<FertilizerExpenditure> = ArrayList<FertilizerExpenditure>()

        val expenditureData: String = sharedPreferences.getString(EXPENDITURE_LIST_KEY, "[]")!!
        try {
            val jsonArray = JSONArray(expenditureData)
            for (i in 0..<jsonArray.length()) {
                val expenditure = fromJson(jsonArray.getJSONObject(i))
                expenditureList.add(expenditure!!)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return expenditureList
    }

    private fun generatePieEntries(
        expenditureList: MutableList<FertilizerExpenditure>,
        timeInterval: String
    ): MutableList<PieEntry?> {
        val pieEntries: MutableList<PieEntry?> = ArrayList<PieEntry?>()
        var totalAmount = 0.0

        // Initialize the date formatter (adjust the format to match your date string)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Process expenditures based on selected time interval
        val now = Calendar.getInstance()
        for (expenditure in expenditureList) {
            val amount = expenditure.amount!!.toDouble()

            // Parse the date string into a Date object
            val purchaseDateString = expenditure.purchaseDate // e.g., "13/11/2024"
            var expenditureDate: Date? = null
            try {
                expenditureDate = dateFormat.parse(purchaseDateString)
            } catch (e: ParseException) {
                e.printStackTrace() // Log the error if the date format is invalid
            }

            // Filter data by selected interval (Daily, Weekly, Monthly)
            if (expenditureDate != null && shouldIncludeExpenditure(
                    expenditureDate,
                    now,
                    timeInterval
                )
            ) {
                totalAmount += amount
            }
        }

        // Add entry for total expenditure (for simplicity, we're adding one entry)
        if (totalAmount > 0) {
            pieEntries.add(PieEntry(totalAmount.toFloat(), "Total Fertilizer"))
        }

        return pieEntries
    }

    private fun shouldIncludeExpenditure(
        expenditureDate: Date,
        currentDate: Calendar,
        timeInterval: String
    ): Boolean {
        val expenditureCalendar = Calendar.getInstance()
        expenditureCalendar.setTime(expenditureDate)

        when (timeInterval) {
            "Daily" -> return expenditureCalendar.get(Calendar.DAY_OF_YEAR) == currentDate.get(
                Calendar.DAY_OF_YEAR
            )

            "Weekly" -> return expenditureCalendar.get(Calendar.WEEK_OF_YEAR) == currentDate.get(
                Calendar.WEEK_OF_YEAR
            )

            "Monthly" -> return expenditureCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
            else -> return true // Default case, show all data
        }
    }

    private val selectedTimeInterval: String
        get() {
            val selectedId = timeIntervalGroup!!.getCheckedRadioButtonId()
            if (selectedId == R.id.radio_daily) {
                return "Daily"
            } else if (selectedId == R.id.radio_weekly) {
                return "Weekly"
            } else {
                return "Monthly"
            }
        }

    private val chartColors: IntArray
        get() = intArrayOf(
            ColorTemplate.COLORFUL_COLORS[0],  // Add more colors if needed
            ColorTemplate.COLORFUL_COLORS[1],
            ColorTemplate.COLORFUL_COLORS[2]
        )

    companion object {
        private const val EXPENDITURE_PREFS = "FertilizerPrefs"
        private const val EXPENDITURE_LIST_KEY = "expenditure_list"
    }
}
