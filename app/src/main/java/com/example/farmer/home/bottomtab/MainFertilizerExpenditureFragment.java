package com.example.farmer.home.bottomtab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmer.R;
import com.example.farmer.fertilizer.FertilizerExpenditure;
import com.example.farmer.fertilizer.FertilizerExpenditureAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class MainFertilizerExpenditureFragment extends Fragment {

    private RecyclerView recyclerViewExpenditures;
    private FertilizerExpenditureAdapter adapter;
    private List<FertilizerExpenditure> expenditureList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_fertilizer_expenditure, container, false);

        recyclerViewExpenditures = view.findViewById(R.id.recyclerViewExpenditures);
        recyclerViewExpenditures.setLayoutManager(new LinearLayoutManager(getActivity()));

        expenditureList = new ArrayList<>();
        adapter = new FertilizerExpenditureAdapter(expenditureList, getContext()); // Pass context to adapter
        recyclerViewExpenditures.setAdapter(adapter);

        reloadData(); // Load the data when the view is created

        return view;
    }

    private void reloadData() {
        expenditureList.clear();
        expenditureList.addAll(loadSavedData());
        adapter.notifyDataSetChanged();
    }

    private List<FertilizerExpenditure> loadSavedData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FertilizerPrefs", Context.MODE_PRIVATE);
        List<FertilizerExpenditure> expenditureList = new ArrayList<>();

        String expenditureData = sharedPreferences.getString("expenditure_list", "[]");
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

    @Override
    public void onResume() {
        super.onResume();
        reloadData(); // Reload data when the fragment is resumed
    }
}
