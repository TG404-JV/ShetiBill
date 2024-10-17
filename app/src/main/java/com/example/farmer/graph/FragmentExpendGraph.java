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

import java.util.ArrayList;
import java.util.List;

public class FragmentExpendGraph extends Fragment {

    private PieChart fertilizerPieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expend_graph, container, false);

        fertilizerPieChart = view.findViewById(R.id.fertilizerPieChart);
        setupPieChart();

        return view;
    }

    private void setupPieChart() {
        // Retrieve the expenditure data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");

        // Variables to store the sum of expenditures for each payment mode
        float cashTotal = 0f;
        float cardTotal = 0f;
        float onlineTotal = 0f;

        try {
            // Parse the JSON array of expenditures
            JSONArray jsonArray = new JSONArray(expenditureData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Get the price and payment mode
                float price = Float.parseFloat(jsonObject.getString("PurchaseAmount"));
                String paymentMode = jsonObject.getString("PaymentMode");

                // Add the price to the corresponding payment mode total
                if (paymentMode.equals("Cash")) {
                    cashTotal += price;
                } else if (paymentMode.equals("Card")) {
                    cardTotal += price;
                } else if (paymentMode.equals("Online")) {
                    onlineTotal += price;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a list of PieEntry objects based on the payment mode totals
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

        // If no data is available, add a placeholder entry
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No data available"));
        }

        // Create a PieDataSet with the entries
        PieDataSet dataSet = new PieDataSet(entries, "Expenditures by Payment Mode");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use Material colors for visibility
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // Set the data to the PieChart
        PieData pieData = new PieData(dataSet);
        fertilizerPieChart.setData(pieData);

        // Customize PieChart appearance
        fertilizerPieChart.getDescription().setEnabled(false); // Disable description label
        fertilizerPieChart.setDrawHoleEnabled(true);
        fertilizerPieChart.setHoleRadius(30f);
        fertilizerPieChart.setTransparentCircleRadius(35f);

        // Set up the legend
        Legend legend = fertilizerPieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        // Refresh the chart
        fertilizerPieChart.invalidate();
    }
}
