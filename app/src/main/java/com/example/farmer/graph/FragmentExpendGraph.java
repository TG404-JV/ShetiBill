package com.example.farmer.graph;

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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

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
        // Sample data for the pie chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(19.7f, "Entertainment"));
        entries.add(new PieEntry(19.0f, "Food"));
        entries.add(new PieEntry(19.5f, "Healthcare"));
        entries.add(new PieEntry(16.2f, "Education"));
        entries.add(new PieEntry(16.8f, "Other"));
        entries.add(new PieEntry(8.8f, "Gas"));

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use Material colors for better visibility
        dataSet.setValueTextColor(Color.BLACK); // Color of the values on the graph
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        fertilizerPieChart.setData(pieData);

        // Customize the pie chart appearance
        fertilizerPieChart.getDescription().setEnabled(false); // Disable the description label
        fertilizerPieChart.setDrawHoleEnabled(true);
        fertilizerPieChart.setHoleRadius(30f);
        fertilizerPieChart.setTransparentCircleRadius(35f);

        // Set up the legend
        Legend legend = fertilizerPieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        fertilizerPieChart.invalidate(); // Refresh the chart
    }
}
