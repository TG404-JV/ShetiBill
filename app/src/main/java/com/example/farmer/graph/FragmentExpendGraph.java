package com.example.farmer.graph;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.farmer.R;
import com.example.farmer.fertilizer.FertilizerExpenditure;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentExpendGraph extends Fragment {

    private PieChart fertilizerPieChart;
    private TextView emptyFertilizerStateTextView;
    private RadioGroup timeIntervalGroup;

    private static final String EXPENDITURE_PREFS = "FertilizerPrefs";
    private static final String EXPENDITURE_LIST_KEY = "expenditure_list";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expend_graph, container, false);

        fertilizerPieChart = view.findViewById(R.id.fertilizerPieChart);
        emptyFertilizerStateTextView = view.findViewById(R.id.emptyFertilizerStateTextView);
        timeIntervalGroup = view.findViewById(R.id.timeIntervalGroup);

        timeIntervalGroup.setOnCheckedChangeListener((group, checkedId) -> updateGraphData());

        // Initially load the data with a default time interval (e.g., Monthly)
        updateGraphData();

        return view;
    }

    private void updateGraphData() {
        // Fetch the expenditure data
        List<FertilizerExpenditure> expenditureList = loadSavedData();

        // Filter and group the data based on the selected time interval
        String timeInterval = getSelectedTimeInterval();
        List<PieEntry> pieEntries = generatePieEntries(expenditureList, timeInterval);

        // Display the pie chart or show "No Data" message if no data
        if (pieEntries.isEmpty()) {
            fertilizerPieChart.setVisibility(View.GONE);
            emptyFertilizerStateTextView.setVisibility(View.VISIBLE);
            emptyFertilizerStateTextView.setText("No fertilizer expenditure data for this period.");
        } else {
            fertilizerPieChart.setVisibility(View.VISIBLE);
            emptyFertilizerStateTextView.setVisibility(View.GONE);

            // Create the PieDataSet and PieData for the chart
            PieDataSet dataSet = new PieDataSet(pieEntries, "Fertilizer Expenditure");
            dataSet.setColors(getChartColors());
            dataSet.setValueTextSize(16f);

            PieData pieData = new PieData(dataSet);
            fertilizerPieChart.setData(pieData);
            fertilizerPieChart.invalidate(); // Refresh the chart

            // Set chart properties (legend, description, etc.)
            Legend legend = fertilizerPieChart.getLegend();
            legend.setTextColor(Color.BLACK);
            legend.setTextSize(14f);
        }
    }

    private List<FertilizerExpenditure> loadSavedData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(EXPENDITURE_PREFS, Context.MODE_PRIVATE);
        List<FertilizerExpenditure> expenditureList = new ArrayList<>();

        String expenditureData = sharedPreferences.getString(EXPENDITURE_LIST_KEY, "[]");
        try {
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                FertilizerExpenditure expenditure = FertilizerExpenditure.fromJson(jsonArray.getJSONObject(i));
                expenditureList.add(expenditure);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return expenditureList;
    }

    private List<PieEntry> generatePieEntries(List<FertilizerExpenditure> expenditureList, String timeInterval) {
        List<PieEntry> pieEntries = new ArrayList<>();
        double totalAmount = 0;

        // Initialize the date formatter (adjust the format to match your date string)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Process expenditures based on selected time interval
        Calendar now = Calendar.getInstance();
        for (FertilizerExpenditure expenditure : expenditureList) {
            double amount = Double.parseDouble(expenditure.getAmount());

            // Parse the date string into a Date object
            String purchaseDateString = expenditure.getPurchaseDate();  // e.g., "13/11/2024"
            Date expenditureDate = null;
            try {
                expenditureDate = dateFormat.parse(purchaseDateString);
            } catch (ParseException e) {
                e.printStackTrace();  // Log the error if the date format is invalid
            }

            // Filter data by selected interval (Daily, Weekly, Monthly)
            if (expenditureDate != null && shouldIncludeExpenditure(expenditureDate, now, timeInterval)) {
                totalAmount += amount;
            }
        }

        // Add entry for total expenditure (for simplicity, we're adding one entry)
        if (totalAmount > 0) {
            pieEntries.add(new PieEntry((float) totalAmount, "Total Fertilizer"));
        }

        return pieEntries;
    }

    private boolean shouldIncludeExpenditure(Date expenditureDate, Calendar currentDate, String timeInterval) {
        Calendar expenditureCalendar = Calendar.getInstance();
        expenditureCalendar.setTime(expenditureDate);

        switch (timeInterval) {
            case "Daily":
                return expenditureCalendar.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
            case "Weekly":
                return expenditureCalendar.get(Calendar.WEEK_OF_YEAR) == currentDate.get(Calendar.WEEK_OF_YEAR);
            case "Monthly":
                return expenditureCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH);
            default:
                return true; // Default case, show all data
        }
    }

    private String getSelectedTimeInterval() {
        int selectedId = timeIntervalGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_daily) {
            return "Daily";
        } else if (selectedId == R.id.radio_weekly) {
            return "Weekly";
        } else {
            return "Monthly";
        }
    }

    private int[] getChartColors() {
        return new int[]{
                ColorTemplate.COLORFUL_COLORS[0], // Add more colors if needed
                ColorTemplate.COLORFUL_COLORS[1],
                ColorTemplate.COLORFUL_COLORS[2]
        };
    }
}
