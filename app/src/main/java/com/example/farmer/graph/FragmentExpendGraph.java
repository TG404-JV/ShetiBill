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

public class FragmentExpendGraph extends Fragment {

    private PieChart fertilizerPieChart;
    private PieChart laborPieChart;
    private TextView emptyFertilizerStateTextView;
    private TextView emptyLaborStateTextView;
    private RadioGroup timeIntervalGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expend_graph, container, false);

        fertilizerPieChart = view.findViewById(R.id.fertilizerPieChart);
        laborPieChart = view.findViewById(R.id.laborPieChart);
        emptyFertilizerStateTextView = view.findViewById(R.id.emptyFertilizerStateTextView);
        emptyLaborStateTextView = view.findViewById(R.id.emptyLaborStateTextView);
        timeIntervalGroup = view.findViewById(R.id.timeIntervalGroup);

        setupPieChart();
        setupLaborPieChart(); // Setup for labor pie chart

        // Set up the time interval selection
        timeIntervalGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDaily) {
                updateLaborPieChart("Daily");
            } else if (checkedId == R.id.rbWeekly) {
                updateLaborPieChart("Weekly");
            } else if (checkedId == R.id.rbMonthly) {
                updateLaborPieChart("Monthly");
            } else if (checkedId == R.id.rbQuarterly) {
                updateLaborPieChart("Quarterly");
            }
        });

        return view;
    }

    private void setupPieChart() {
        // Retrieve the expenditure data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");

        float cashTotal = 0f;
        float cardTotal = 0f;
        float onlineTotal = 0f;

        try {
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                float price = (float) jsonObject.optDouble("PurchaseAmount", 0.0);
                String paymentMode = jsonObject.optString("PaymentMode", "");

                switch (paymentMode) {
                    case "Cash":
                        cashTotal += price;
                        break;
                    case "Card":
                        cardTotal += price;
                        break;
                    case "Online":
                        onlineTotal += price;
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<PieEntry> entries = new ArrayList<>();
        if (cashTotal > 0) {
            entries.add(new PieEntry(cashTotal, "Cash"));
        }
        if (cardTotal > 0) {
            entries.add(new PieEntry(cardTotal, "Card"));
        }
        if (onlineTotal > 0) {
            entries.add(new PieEntry(onlineTotal, "Online"));
        }

        if (entries.isEmpty()) {
            emptyFertilizerStateTextView.setVisibility(View.VISIBLE);
            fertilizerPieChart.setVisibility(View.GONE);
            return;
        } else {
            emptyFertilizerStateTextView.setVisibility(View.GONE);
            fertilizerPieChart.setVisibility(View.VISIBLE);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenditures by Payment Mode");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        fertilizerPieChart.setData(pieData);

        fertilizerPieChart.getDescription().setEnabled(false);
        fertilizerPieChart.setDrawHoleEnabled(true);
        fertilizerPieChart.setHoleRadius(30f);
        fertilizerPieChart.setTransparentCircleRadius(35f);
        fertilizerPieChart.setRotationEnabled(true);
        fertilizerPieChart.animateY(1000);

        Legend legend = fertilizerPieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        fertilizerPieChart.invalidate();
    }

    private void setupLaborPieChart() {
        // Initial setup for labor pie chart
        updateLaborPieChart("Daily"); // Default view is daily
    }

    private void updateLaborPieChart(String timeInterval) {
        // Retrieve the labor data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LaborPrefs", Context.MODE_PRIVATE);
        String laborData = sharedPreferences.getString("labor_list", "[]");

        float harvestedWeight = 0f; // This will hold the total harvested weight based on time interval
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Assuming date format is yyyy-MM-dd

        try {
            JSONArray jsonArray = new JSONArray(laborData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                float weight = (float) jsonObject.optDouble("HarvestedWeight", 0.0);
                String harvestDate = jsonObject.optString("Date", ""); // Assuming the date is stored

                if (!harvestDate.isEmpty()) {
                    Date date = dateFormat.parse(harvestDate);

                    // Filter by the selected time interval
                    if (isDateInInterval(date, timeInterval, cal)) {
                        harvestedWeight += weight; // Aggregate weight for now
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        List<PieEntry> laborEntries = new ArrayList<>();
        if (harvestedWeight > 0) {
            laborEntries.add(new PieEntry(harvestedWeight, "Total Harvested Weight"));
        }

        if (laborEntries.isEmpty()) {
            emptyLaborStateTextView.setVisibility(View.VISIBLE);
            laborPieChart.setVisibility(View.GONE);
            return;
        } else {
            emptyLaborStateTextView.setVisibility(View.GONE);
            laborPieChart.setVisibility(View.VISIBLE);
        }

        PieDataSet laborDataSet = new PieDataSet(laborEntries, "Harvested Weight");
        laborDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        laborDataSet.setValueTextColor(Color.BLACK);
        laborDataSet.setValueTextSize(12f);

        PieData laborPieData = new PieData(laborDataSet);
        laborPieChart.setData(laborPieData);

        laborPieChart.getDescription().setEnabled(false);
        laborPieChart.setDrawHoleEnabled(true);
        laborPieChart.setHoleRadius(30f);
        laborPieChart.setTransparentCircleRadius(35f);
        laborPieChart.setRotationEnabled(true);
        laborPieChart.animateY(1000);

        Legend laborLegend = laborPieChart.getLegend();
        laborLegend.setEnabled(true);
        laborLegend.setTextColor(Color.BLACK);
        laborLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        laborPieChart.invalidate();
    }

    private boolean isDateInInterval(Date date, String interval, Calendar cal) {
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Get today's date for comparison
        Calendar today = Calendar.getInstance();

        switch (interval) {
            case "Daily":
                // For daily, compare with today's date
                return (year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH) + 1 && day == today.get(Calendar.DAY_OF_MONTH));
            case "Weekly":
                // For weekly, compare with the week of the year
                return (cal.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR) && year == today.get(Calendar.YEAR));
            case "Monthly":
                // For monthly, compare month and year
                return (month == today.get(Calendar.MONTH) + 1 && year == today.get(Calendar.YEAR));
            case "Quarterly":
                // For quarterly, check if the month is in the same quarter
                int currentQuarter = (today.get(Calendar.MONTH) / 3) + 1; // 1, 2, 3, or 4
                int harvestQuarter = (month / 3) + 1;
                return (currentQuarter == harvestQuarter && year == today.get(Calendar.YEAR));
            default:
                return false;
        }
    }
}
